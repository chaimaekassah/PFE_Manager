package org.pfemanager.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.pfemanager.service.PasswordResetService;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@Named
@ViewScoped
public class ResetPasswordBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient PasswordResetService passwordResetService;

    private String token;
    private String nouveauMotDePasse;
    private String confirmerMotDePasse;
    private String messageErreur;
    private boolean tokenValide = false;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        // Lire le token depuis l'URL
        Map<String, String> params = ec.getRequestParameterMap();
        token = params.get("token");

        System.out.println("=== POSTCONSTRUCT TOKEN : " + token + " ===");

        if (token == null || token.trim().isEmpty()) {
            messageErreur = "Lien invalide.";
            tokenValide = false;
            return;
        }

        tokenValide = passwordResetService.tokenValide(token);
        System.out.println("=== TOKEN VALIDE : " + tokenValide + " ===");

        if (!tokenValide) {
            messageErreur = "Ce lien est expiré ou déjà utilisé.";
        }
    }

    public void reinitialiser() throws IOException {
        messageErreur = null;

        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 8) {
            messageErreur = "Le mot de passe doit contenir au moins 8 caractères.";
            return;
        }
        if (!nouveauMotDePasse.equals(confirmerMotDePasse)) {
            messageErreur = "Les mots de passe ne correspondent pas.";
            return;
        }
        if (token == null || token.isEmpty()) {
            messageErreur = "Token manquant.";
            return;
        }

        boolean succes = passwordResetService.reinitialiserMotDePasse(token, nouveauMotDePasse);
        if (!succes) {
            messageErreur = "Lien invalide ou expiré.";
            return;
        }

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/login.xhtml?reset=success");
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNouveauMotDePasse() { return nouveauMotDePasse; }
    public void setNouveauMotDePasse(String p) { this.nouveauMotDePasse = p; }
    public String getConfirmerMotDePasse() { return confirmerMotDePasse; }
    public void setConfirmerMotDePasse(String p) { this.confirmerMotDePasse = p; }
    public String getMessageErreur() { return messageErreur; }
    public boolean isTokenValide() { return tokenValide; }
}