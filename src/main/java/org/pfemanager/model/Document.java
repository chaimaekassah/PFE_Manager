package org.pfemanager.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant un document déposé dans le cadre d'un projet
 */
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nomFichier;
    private String typeFichier;
    private Long taille;
    private String chemin;
    private LocalDateTime dateDepot;
    private User depositaire;
    private Projet projet;

    // Constructeurs
    public Document() {
        this.dateDepot = LocalDateTime.now();
    }

    public Document(Long id, String nomFichier, String typeFichier, User depositaire, Projet projet) {
        this();
        this.id = id;
        this.nomFichier = nomFichier;
        this.typeFichier = typeFichier;
        this.depositaire = depositaire;
        this.projet = projet;
    }

    // Getters et Setters
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

    /**
     * Retourne la taille formatée pour l'affichage
     */
    public String getTailleFormatee() {
        if (taille == null) {
            return "0 B";
        }

        if (taille < 1024) {
            return taille + " B";
        } else if (taille < 1024 * 1024) {
            return String.format("%.2f KB", taille / 1024.0);
        } else if (taille < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", taille / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", taille / (1024.0 * 1024.0 * 1024.0));
        }
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", nomFichier='" + nomFichier + '\'' +
                ", typeFichier='" + typeFichier + '\'' +
                ", depositaire=" + (depositaire != null ? depositaire.getNomComplet() : "null") +
                ", projet=" + (projet != null ? projet.getSujet(): "null") +
                ", dateDepot=" + dateDepot +
                '}';
    }
}