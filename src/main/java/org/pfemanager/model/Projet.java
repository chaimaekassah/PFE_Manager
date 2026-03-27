package org.pfemanager.model;

import org.pfemanager.enums.StatutProjet;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un projet de fin d'études
 */
public class Projet implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sujet;
    private String description;
    private User etudiant;
    private User encadrant;
    private StatutProjet statut;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private List<Commentaire> commentaires;
    private List<Document> documents;

    // Constructeurs
    public Projet() {
        this.dateDebut = LocalDateTime.now();
        this.statut = StatutProjet.EN_COURS;
        this.commentaires = new ArrayList<>();
        this.documents = new ArrayList<>();
    }

    public Projet(Long id, String sujet, String description, User etudiant, User encadrant) {
        this();
        this.id = id;
        this.sujet = sujet;
        this.description = description;
        this.etudiant = etudiant;
        this.encadrant = encadrant;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public User getEncadrant() {
        return encadrant;
    }

    public void setEncadrant(User encadrant) {
        this.encadrant = encadrant;
    }

    public StatutProjet getStatut() {
        return statut;
    }

    public void setStatut(StatutProjet statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
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

    // Méthodes utilitaires
    public void ajouterCommentaire(Commentaire commentaire) {
        this.commentaires.add(commentaire);
    }

    public void ajouterDocument(Document document) {
        this.documents.add(document);
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id=" + id +
                ", sujet='" + sujet + '\'' +
                ", etudiant=" + (etudiant != null ? etudiant.getNomComplet() : "null") +
                ", encadrant=" + (encadrant != null ? encadrant.getNomComplet() : "null") +
                ", statut=" + statut +
                '}';
    }

}