package org.pfemanager.controller;

import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.*;
import org.pfemanager.service.DocumentService;
import org.pfemanager.service.ProjetService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Named("projetEncadrantBean")
@ViewScoped
public class ProjetEncadrantBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient ProjetService projetService;

    @Inject
    private transient DocumentService documentService;

    @Inject
    private AuthBean authBean; // ✅ AuthBean au lieu de TestModuleBean

    private List<Projet> projets;
    private Projet selectedProjet;
    private List<Commentaire> commentaires;
    private List<Document> documents;
    private StatutProjet nouveauStatut;
    private String nouveauCommentaire;

    @PostConstruct
    public void init() {
        loadProjets();
    }

    public void loadProjets() {
        Utilisateur user = authBean.getUtilisateurConnecte();
        if (user != null) {
            projets = projetService.getProjetsByEncadrant(user.getId());
        }
    }

    public void selectionnerProjet(Projet projet) {
        this.selectedProjet = projet;
        this.nouveauStatut = projet.getStatut();
        loadDetailsProjet();
    }

    private void loadDetailsProjet() {
        if (selectedProjet != null) {
            commentaires = projetService.getCommentaires(selectedProjet.getId());
            documents = documentService.getDocumentsByProjet(selectedProjet.getId());
        }
    }

    public void changerStatut() {
        if (selectedProjet != null && nouveauStatut != null) {
            projetService.changerStatut(selectedProjet.getId(), nouveauStatut);
            addMessage(FacesMessage.SEVERITY_INFO, "Statut modifié avec succès.");
            loadProjets();
            selectedProjet = projetService.findById(selectedProjet.getId())
                    .orElse(selectedProjet);
            loadDetailsProjet();
        }
    }

    public void ajouterCommentaire() {
        Utilisateur user = authBean.getUtilisateurConnecte();
        if (selectedProjet != null
                && nouveauCommentaire != null
                && !nouveauCommentaire.trim().isEmpty()
                && user != null) {
            projetService.ajouterCommentaire(
                    selectedProjet.getId(), user, nouveauCommentaire);
            addMessage(FacesMessage.SEVERITY_INFO, "Commentaire ajouté.");
            nouveauCommentaire = null;
            loadDetailsProjet();
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Veuillez saisir un commentaire.");
        }
    }

    public void retourListe() {
        selectedProjet = null;
        nouveauStatut = null;
        nouveauCommentaire = null;
        commentaires = null;
        documents = null;
    }

    public long getProjetsEnCours() {
        if (projets == null) return 0;
        return projets.stream()
                .filter(p -> p.getStatut() == StatutProjet.EN_COURS)
                .count();
    }

    public String getStatutProjetClass(StatutProjet statut) {
        if (statut == null) return "";
        switch (statut) {
            case EN_COURS: return "statut-en-cours";
            case VALIDE:   return "statut-valide";
            case TERMINE:  return "statut-termine";
            default:       return "";
        }
    }

    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public StatutProjet[] getAllStatuts() { return StatutProjet.values(); }

    private void addMessage(FacesMessage.Severity severity, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, msg, null));
    }

    // Getters / Setters
    public List<Projet> getProjets() { return projets; }
    public void setProjets(List<Projet> p) { this.projets = p; }
    public Projet getSelectedProjet() { return selectedProjet; }
    public void setSelectedProjet(Projet p) { this.selectedProjet = p; }
    public List<Commentaire> getCommentaires() { return commentaires; }
    public void setCommentaires(List<Commentaire> c) { this.commentaires = c; }
    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> d) { this.documents = d; }
    public StatutProjet getNouveauStatut() { return nouveauStatut; }
    public void setNouveauStatut(StatutProjet s) { this.nouveauStatut = s; }
    public String getNouveauCommentaire() { return nouveauCommentaire; }
    public void setNouveauCommentaire(String c) { this.nouveauCommentaire = c; }
}