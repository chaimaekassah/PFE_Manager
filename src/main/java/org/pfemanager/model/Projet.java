package org.pfemanager.model;

import jakarta.persistence.*;
import org.pfemanager.enums.StatutProjet;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un projet de fin d'études
 */
@Entity
@Table(name = "projets")
public class Projet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sujet;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id")
    private User etudiant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "encadrant_id")
    private User encadrant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProjet statut;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Transient
    private List<Commentaire> commentaires;

    @Transient
    private List<Document> documents;

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

    @PrePersist
    public void prePersist() {
        if (dateDebut == null) {
            dateDebut = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutProjet.EN_COURS;
        }
        if (commentaires == null) {
            commentaires = new ArrayList<>();
        }
        if (documents == null) {
            documents = new ArrayList<>();
        }
    }

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
        if (commentaires == null) {
            commentaires = new ArrayList<>();
        }
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public List<Document> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<>();
        }
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void ajouterCommentaire(Commentaire commentaire) {
        getCommentaires().add(commentaire);
    }

    public void ajouterDocument(Document document) {
        getDocuments().add(document);
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