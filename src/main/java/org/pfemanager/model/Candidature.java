package org.pfemanager.model;

import org.pfemanager.enums.StatutCandidature;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant une candidature d'étudiant(e) à un projet
 */
public class Candidature implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private User etudiant;
    private User encadrant;
    private String sujet;
    private String messageMotivation;
    private StatutCandidature statut;
    private String remarqueEncadrant;
    private LocalDateTime dateCandidature;
    private LocalDateTime dateReponse;

    // Constructeurs
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

    // Getters et Setters
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