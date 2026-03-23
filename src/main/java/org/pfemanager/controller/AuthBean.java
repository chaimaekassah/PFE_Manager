package org.pfemanager.controller;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.model.Role;
import org.pfemanager.service.AuthService;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serializable;

@Named
@SessionScoped
public class AuthBean implements Serializable {

    @Inject
    private AuthService authService;

    private String email;
    private String motDePasse;
    private String nom;
    private String roleSelectionne;
    private String messageErreur;
    private Utilisateur utilisateurConnecte;

    // ===================== LOGIN =====================
    public void login() throws IOException {
        messageErreur = null;

        if (email == null || email.trim().isEmpty()) {
            messageErreur = "L'email est obligatoire.";
            return;
        }
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            messageErreur = "Le mot de passe est obligatoire.";
            return;
        }

        Utilisateur u = authService.authentifier(email.trim(), motDePasse);
        if (u == null) {
            messageErreur = "Email ou mot de passe incorrect.";
            return;
        }

        this.utilisateurConnecte = u;

        // Stocker dans la session HTTP pour le filtre
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) ec.getSession(true);
        session.setAttribute("authBean", this);

        // Redirection selon le rôle
        String contextPath = ec.getRequestContextPath();
        switch (u.getRole()) {
            case ADMINISTRATEUR:
                ec.redirect(contextPath + "/admin/dashboard.xhtml");
                break;
            case ENCADRANT:
                ec.redirect(contextPath + "/encadrant/dashboard.xhtml");
                break;
            case ETUDIANT:
                ec.redirect(contextPath + "/etudiant/dashboard.xhtml");
                break;
            default:
                messageErreur = "Rôle inconnu.";
        }
    }

    // ===================== REGISTER =====================
    public void register() throws IOException {
        messageErreur = null;

        if (nom == null || nom.trim().isEmpty()) {
            messageErreur = "Le nom est obligatoire.";
            return;
        }
        if (email == null || !email.contains("@")) {
            messageErreur = "Email invalide.";
            return;
        }
        if (motDePasse == null || motDePasse.length() < 8) {
            messageErreur = "Le mot de passe doit contenir au moins 8 caractères.";
            return;
        }

        Utilisateur u = new Utilisateur();
        u.setNom(nom.trim());
        u.setEmail(email.trim());
        u.setMotDePasse(motDePasse);
        u.setRole(Role.valueOf(roleSelectionne));

        boolean succes = authService.inscrire(u);
        if (!succes) {
            messageErreur = "Cet email est déjà utilisé.";
            return;
        }

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/login.xhtml");
    }

    // ===================== LOGOUT =====================
    public void logout() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) ec.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        ec.redirect(ec.getRequestContextPath() + "/login.xhtml");
    }

    public boolean isConnecte() {
        return utilisateurConnecte != null;
    }

    // Getters et Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getRoleSelectionne() { return roleSelectionne; }
    public void setRoleSelectionne(String r) { this.roleSelectionne = r; }
    public String getMessageErreur() { return messageErreur; }
    public Utilisateur getUtilisateurConnecte() { return utilisateurConnecte; }
}