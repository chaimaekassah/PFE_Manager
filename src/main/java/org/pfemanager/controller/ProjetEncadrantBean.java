package org.pfemanager.controller;

import org.pfemanager.model.Projet;
import org.pfemanager.model.Commentaire;
import org.pfemanager.model.Document;
import org.pfemanager.service.ProjetService;
import org.pfemanager.service.DocumentService;
import org.pfemanager.enums.StatutProjet;

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

/**
 * Bean pour le suivi des projets côté encadrant
 */
@Named("projetEncadrantBean")
@ViewScoped
public class ProjetEncadrantBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjetService projetService;

    @Inject
    private DocumentService documentService;

    @Inject
    private TestModuleBean testModuleBean;

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
        if (testModuleBean.getCurrentUser() != null) {
            projets = projetService.getProjetsByEncadrant(
                    testModuleBean.getCurrentUser().getId()
            );
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
            addMessage(FacesMessage.SEVERITY_INFO, "Statut modifié avec succès");
            loadProjets();
            loadDetailsProjet();
        }
    }

    public void ajouterCommentaire() {
        if (selectedProjet != null && nouveauCommentaire != null && !nouveauCommentaire.trim().isEmpty()) {
            projetService.ajouterCommentaire(
                    selectedProjet.getId(),
                    testModuleBean.getCurrentUser(),
                    nouveauCommentaire
            );

            addMessage(FacesMessage.SEVERITY_INFO, "Commentaire ajouté");
            nouveauCommentaire = null;
            loadDetailsProjet();
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Veuillez saisir un commentaire");
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

    public String getStatutClass(StatutProjet statut) {
        if (statut == null) return "";

        switch (statut) {
            case EN_COURS:
                return "statut-en-cours";
            case VALIDE:
                return "statut-valide";
            case TERMINE:
                return "statut-termine";
            default:
                return "";
        }
    }

    public String getStatutProjetClass(StatutProjet statut) {
        return getStatutClass(statut);
    }

    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public StatutProjet[] getAllStatuts() {
        return StatutProjet.values();
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severity, message, null)
        );
    }

    public List<Projet> getProjets() {
        return projets;
    }

    public void setProjets(List<Projet> projets) {
        this.projets = projets;
    }

    public Projet getSelectedProjet() {
        return selectedProjet;
    }

    public void setSelectedProjet(Projet selectedProjet) {
        this.selectedProjet = selectedProjet;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public StatutProjet getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(StatutProjet nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public String getNouveauCommentaire() {
        return nouveauCommentaire;
    }

    public void setNouveauCommentaire(String nouveauCommentaire) {
        this.nouveauCommentaire = nouveauCommentaire;
    }
}