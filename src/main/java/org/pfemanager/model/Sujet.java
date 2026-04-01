package org.pfemanager.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sujets")
public class Sujet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "technologies_requises")
    private String technologiesRequises;

    @Column(name = "nombre_max_etudiants", nullable = false)
    private int nombreMaxEtudiants = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSujet statut = StatutSujet.DISPONIBLE;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encadrant_id", nullable = false)
    private User encadrant;

    public enum StatutSujet {
        DISPONIBLE, COMPLET, FERME
    }

    public Sujet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTechnologiesRequises() { return technologiesRequises; }
    public void setTechnologiesRequises(String t) { this.technologiesRequises = t; }

    public int getNombreMaxEtudiants() { return nombreMaxEtudiants; }
    public void setNombreMaxEtudiants(int n) { this.nombreMaxEtudiants = n; }

    public StatutSujet getStatut() { return statut; }
    public void setStatut(StatutSujet statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }

    public User getEncadrant() { return encadrant; }
    public void setEncadrant(User encadrant) { this.encadrant = encadrant; }
}