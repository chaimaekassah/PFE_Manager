package org.pfemanager.model;

import org.pfemanager.enums.Role;
import org.pfemanager.enums.StatutUser;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant un utilisateur
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role;
    private StatutUser statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Constructeurs
    public User() {
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutUser.ACTIF;
    }

    public User(Long id, String nom, String prenom, String email, Role role) {
        this();
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public StatutUser getStatut() {
        return statut;
    }

    public void setStatut(StatutUser statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    // Méthode utilitaire
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", statut=" + statut +
                '}';
    }
}