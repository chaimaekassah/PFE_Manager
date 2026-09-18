package org.pfemanager.model;

import jakarta.persistence.*;
import org.pfemanager.enums.StatutCandidature;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidatures")
public class Candidature implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Utilisateur etudiant;   // ✅ Utilisateur au lieu de User

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "encadrant_id")
    private Utilisateur encadrant; // ✅ Utilisateur au lieu de User

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

    public Candidature(Long id, Utilisateur etudiant, Utilisateur encadrant,
                       String sujet, String messageMotivation) {
        this();
        this.id = id;
        this.etudiant = etudiant;
        this.encadrant = encadrant;
        this.sujet = sujet;
        this.messageMotivation = messageMotivation;
    }

    @PrePersist
    public void prePersist() {
        if (dateCandidature == null) dateCandidature = LocalDateTime.now();
        if (statut == null) statut = StatutCandidature.EN_ATTENTE;
    }

    // ── Getters / Setters ──────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getEtudiant() { return etudiant; }
    public void setEtudiant(Utilisateur etudiant) { this.etudiant = etudiant; }

    public Utilisateur getEncadrant() { return encadrant; }
    public void setEncadrant(Utilisateur encadrant) { this.encadrant = encadrant; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getMessageMotivation() { return messageMotivation; }
    public void setMessageMotivation(String m) { this.messageMotivation = m; }

    public StatutCandidature getStatut() { return statut; }
    public void setStatut(StatutCandidature statut) { this.statut = statut; }

    public String getRemarqueEncadrant() { return remarqueEncadrant; }
    public void setRemarqueEncadrant(String r) { this.remarqueEncadrant = r; }

    public LocalDateTime getDateCandidature() { return dateCandidature; }
    public void setDateCandidature(LocalDateTime d) { this.dateCandidature = d; }

    public LocalDateTime getDateReponse() { return dateReponse; }
    public void setDateReponse(LocalDateTime d) { this.dateReponse = d; }

    @Override
    public String toString() {
        return "Candidature{id=" + id
                + ", etudiant=" + (etudiant != null ? etudiant.getNom() : "null")
                + ", sujet='" + sujet + "'"
                + ", statut=" + statut + "}";
    }
}