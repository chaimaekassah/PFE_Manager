package org.pfemanager.model;

import jakarta.persistence.*;
import org.pfemanager.util.PasswordUtil;
import java.io.Serializable;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe")
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "photo")
    private String photo;

    public User() {
    }

    public User(String nom, String email, String motDePasse, Role role) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public User(Long id, String nom, String email, String motDePasse, Role role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhotoOuDefaut() {
        return (photo != null && !photo.isEmpty())
                ? photo
                : "resources/images/default-avatar.png";
    }

    public String getNomComplet() {
        return nom;
    }

    /**
     * Optionnel : si tu veux hasher avant sauvegarde.
     */
    public void setMotDePasseHash(String motDePasseClair) {
        this.motDePasse = PasswordUtil.hasher(motDePasseClair);
    }

    /**
     * Optionnel : pour vérifier au login.
     */
    public boolean verifierMotDePasse(String motDePasseClair) {
        if (motDePasse == null) {
            return false;
        }
        return PasswordUtil.verifier(motDePasseClair, motDePasse);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}