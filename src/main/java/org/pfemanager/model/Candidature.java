package org.pfemanager.model;

import jakarta.persistence.*;
import org.pfemanager.enums.StatutCandidature;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant une candidature d'étudiant(e) à un projet
 */
@Entity
@Table(name = "candidatures")
public class Candidature implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private User etudiant;

    @ManyToOne
    @JoinColumn(name = "encadrant_id")
    private User encadrant;

    @Column(nullable = false)
    private String sujet;

    @Column(name = "message_motivation")
    private String messageMotivation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut;

    @Column(name = "remarque_encadrant")
    private String remarqueEncadrant;

    @Column(name = "date_candidature")
    private LocalDateTime dateCandidature;

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    public Candidature() {
        this.dateCandidature = LocalDateTime.now();
        this.statut = StatutCandidature.EN_ATTENTE;
    }

    public Candidature(Long id, User etudiant, User encadrant, String sujet, String messageMotivation) {
        this();
        this.id = id;
        this.etudiant = etudiant;
        this.encadrant = encadrant;
        this.sujet = sujet;
        this.messageMotivation = messageMotivation;
    }

    @PrePersist
    public void prePersist() {
        if (dateCandidature == null) {
            dateCandidature = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutCandidature.EN_ATTENTE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getMessageMotivation() {
        return messageMotivation;
    }

    public void setMessageMotivation(String messageMotivation) {
        this.messageMotivation = messageMotivation;
    }

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }

    public String getRemarqueEncadrant() {
        return remarqueEncadrant;
    }

    public void setRemarqueEncadrant(String remarqueEncadrant) {
        this.remarqueEncadrant = remarqueEncadrant;
    }

    public LocalDateTime getDateCandidature() {
        return dateCandidature;
    }

    public void setDateCandidature(LocalDateTime dateCandidature) {
        this.dateCandidature = dateCandidature;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "id=" + id +
                ", etudiant=" + (etudiant != null ? etudiant.getNomComplet() : "null") +
                ", encadrant=" + (encadrant != null ? encadrant.getNomComplet() : "null") +
                ", sujet='" + sujet + '\'' +
                ", statut=" + statut +
                '}';
    }
}