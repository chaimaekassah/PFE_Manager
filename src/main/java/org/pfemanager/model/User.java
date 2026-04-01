package org.pfemanager.model;

import jakarta.persistence.*;
import org.pfemanager.enums.Role;
import org.pfemanager.enums.StatutUser;
import org.pfemanager.util.PasswordUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutUser statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "photo")
    private String photo;

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

    public User(String nom, String prenom, String email, Role role) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * À utiliser quand tu reçois un mot de passe brut depuis un formulaire.
     */
    public void setPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }
        this.passwordHash = PasswordUtil.hashPassword(plainPassword);
    }

    /**
     * Vérifie si le mot de passe brut correspond au hash stocké.
     */
    public boolean checkPassword(String plainPassword) {
        if (plainPassword == null || passwordHash == null) {
            return false;
        }
        return PasswordUtil.checkPassword(plainPassword, passwordHash);
    }

    /**
     * Compatibilité temporaire si ton ancien code utilise encore getMotDePasse().
     * À supprimer plus tard après refactorisation complète.
     */
    @Transient
    public String getMotDePasse() {
        return passwordHash;
    }

    /**
     * Compatibilité temporaire si ton ancien code utilise encore setMotDePasse().
     * Ici on hash automatiquement la valeur reçue.
     */
    public void setMotDePasse(String motDePasse) {
        setPassword(motDePasse);
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNomComplet() {
        String p = prenom == null ? "" : prenom;
        String n = nom == null ? "" : nom;
        return (p + " " + n).trim();
    }

    public String getPhotoOuDefaut() {
        return (photo != null && !photo.isEmpty())
                ? photo
                : "resources/images/default-avatar.png";
    }

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutUser.ACTIF;
        }
    }

    @PreUpdate
    public void preUpdate() {
        dateModification = LocalDateTime.now();
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