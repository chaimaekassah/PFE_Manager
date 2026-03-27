package org.pfemanager.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant un commentaire sur un projet
 */
public class Commentaire implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private User auteur;
    private String contenu;
    private LocalDateTime dateCreation;
    private Projet projet;

    // Constructeurs
    public Commentaire() {
        this.dateCreation = LocalDateTime.now();
    }

    public Commentaire(Long id, User auteur, String contenu, Projet projet) {
        this();
        this.id = id;
        this.auteur = auteur;
        this.contenu = contenu;
        this.projet = projet;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuteur() {
        return auteur;
    }

    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", auteur=" + (auteur != null ? auteur.getNomComplet() : "null") +
                ", contenu='" + contenu + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}