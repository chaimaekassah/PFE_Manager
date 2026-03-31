package org.pfemanager.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_fichier")
    private String nomFichier;

    @Column(name = "type_fichier")
    private String typeFichier;

    private Long taille;

    private String chemin;

    @Column(name = "date_depot")
    private LocalDateTime dateDepot;

    @ManyToOne
    @JoinColumn(name = "depositaire_id")
    private User depositaire;

    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;

    public Document() {
        this.dateDepot = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (dateDepot == null) {
            dateDepot = LocalDateTime.now();
        }
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getTypeFichier() {
        return typeFichier;
    }

    public void setTypeFichier(String typeFichier) {
        this.typeFichier = typeFichier;
    }

    public Long getTaille() {
        return taille;
    }

    public void setTaille(Long taille) {
        this.taille = taille;
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    public LocalDateTime getDateDepot() {
        return dateDepot;
    }

    public void setDateDepot(LocalDateTime dateDepot) {
        this.dateDepot = dateDepot;
    }

    public User getDepositaire() {
        return depositaire;
    }

    public void setDepositaire(User depositaire) {
        this.depositaire = depositaire;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    // =========================
    // UTILE POUR AFFICHAGE
    // =========================

    public String getTailleFormatee() {
        if (taille == null) return "0 B";

        if (taille < 1024) return taille + " B";
        else if (taille < 1024 * 1024)
            return String.format("%.2f KB", taille / 1024.0);
        else if (taille < 1024 * 1024 * 1024)
            return String.format("%.2f MB", taille / (1024.0 * 1024.0));
        else
            return String.format("%.2f GB", taille / (1024.0 * 1024.0 * 1024.0));
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", nomFichier='" + nomFichier + '\'' +
                ", typeFichier='" + typeFichier + '\'' +
                ", depositaire=" + (depositaire != null ? depositaire.getNomComplet() : "null") +
                ", projet=" + (projet != null ? projet.getSujet() : "null") +
                ", dateDepot=" + dateDepot +
                '}';
    }
}