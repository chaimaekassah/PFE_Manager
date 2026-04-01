package org.pfemanager.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.pfemanager.service.PasswordResetService;

import java.io.IOException;

@Named
@RequestScoped
public class ForgotPasswordBean {

    @Inject
    private PasswordResetService passwordResetService;

    private String email;
    private String messageSucces;
    private String messageErreur;

    public void envoyerLien() throws IOException {
        messageErreur = null;
        messageSucces = null;

        if (email == null || !email.contains("@")) {
            messageErreur = "Veuillez entrer une adresse email valide.";
            return;
        }

        // Construire l'URL de base
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String baseUrl = ec.getRequestScheme() + "://"
                + ec.getRequestServerName() + ":"
                + ec.getRequestServerPort()
                + ec.getRequestContextPath();

        passwordResetService.demanderReinitialisation(email.trim(), baseUrl);

        // Toujours afficher succès (sécurité)
        messageSucces = "Si cet email existe, un lien de réinitialisation vous a été envoyé.";
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMessageSucces() { return messageSucces; }
    public String getMessageErreur() { return messageErreur; }
}