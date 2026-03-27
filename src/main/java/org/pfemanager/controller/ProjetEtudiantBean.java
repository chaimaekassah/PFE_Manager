package org.pfemanager.controller;

import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.Projet;
import org.pfemanager.model.Commentaire;
import org.pfemanager.model.Document;
import org.pfemanager.service.ProjetService;
import org.pfemanager.service.DocumentService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
/**
 * Bean pour le suivi du projet côté étudiant
 */
@Named("projetEtudiantBean")
@ViewScoped
public class ProjetEtudiantBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjetService projetService;

    @Inject
    private DocumentService documentService;

    @Inject
    private TestModuleBean testModuleBean;

    private Projet projet;
    private List<Commentaire> commentaires;
    private List<Document> documents;

    // Pour le dépôt de document
    private String nomDocument;
    private String typeDocument;

    @PostConstruct
    public void init() {
        loadProjet();
    }

    public void loadProjet() {
        if (testModuleBean.getCurrentUser() != null) {
            projet = projetService.getProjetByEtudiant(
                    testModuleBean.getCurrentUser().getId()
            ).orElse(null);

            if (projet != null) {
                commentaires = projetService.getCommentaires(projet.getId());
                documents = documentService.getDocumentsByProjet(projet.getId());
            }
        }
    }

    // Déposer un document (simulation)
    public void deposerDocument() {
        if (projet != null && nomDocument != null && !nomDocument.trim().isEmpty()) {
            Document doc = new Document();
            doc.setNomFichier(nomDocument);
            doc.setTypeFichier(typeDocument != null ? typeDocument : "pdf");
            doc.setDepositaire(testModuleBean.getCurrentUser());
            doc.setProjet(projet);
            doc.setTaille(1024L * 512); // Taille simulée : 512 KB

            documentService.create(doc);

            addMessage(FacesMessage.SEVERITY_INFO, "Document déposé avec succès");

            nomDocument = null;
            typeDocument = null;

            documents = documentService.getDocumentsByProjet(projet.getId());
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Veuillez saisir un nom de document");
        }
    }

    // Vérifier si l'étudiant a un projet
    public boolean hasProjet() {
        return projet != null;
    }

    // Classe CSS selon le statut du projet
    public String getStatutClass() {
        if (projet == null) return "";

        switch (projet.getStatut()) {
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

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, message, null));
    }

    // Getters et Setters
    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
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

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }
    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    public String getStatutProjetClass(StatutProjet statut) {
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
}