package org.pfemanager.controller;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import org.pfemanager.dao.UtilisateurDAO;
import org.pfemanager.model.User;
import org.pfemanager.util.PasswordUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Named
@SessionScoped
public class ProfilBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AuthBean authBean;

    @Inject
    private transient UtilisateurDAO utilisateurDAO;

    private String ancienMotDePasse;
    private String nouveauMotDePasse;
    private String confirmerMotDePasse;
    private String messageSucces;
    private String messageErreur;
    private Part photoPart;

    @PostConstruct
    public void init() {
        messageSucces = null;
        messageErreur = null;
    }

    public void changerMotDePasse() {
        messageSucces = null;
        messageErreur = null;

        User u = authBean.getUtilisateurConnecte();
        if (u == null) {
            messageErreur = "Session expirée, veuillez vous reconnecter.";
            return;
        }

        if (ancienMotDePasse == null || ancienMotDePasse.isEmpty()) {
            messageErreur = "Veuillez saisir votre mot de passe actuel.";
            return;
        }

        if (!PasswordUtil.verifier(ancienMotDePasse, u.getMotDePasse())) {
            messageErreur = "Mot de passe actuel incorrect.";
            return;
        }

        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 8) {
            messageErreur = "Le nouveau mot de passe doit contenir au moins 8 caractères.";
            return;
        }

        if (!nouveauMotDePasse.equals(confirmerMotDePasse)) {
            messageErreur = "Les nouveaux mots de passe ne correspondent pas.";
            return;
        }

        u.setMotDePasse(PasswordUtil.hasher(nouveauMotDePasse));
        utilisateurDAO.update(u);

        ancienMotDePasse = null;
        nouveauMotDePasse = null;
        confirmerMotDePasse = null;
        messageSucces = "Mot de passe modifié avec succès !";
    }

    public void uploadPhoto() {
        messageSucces = null;
        messageErreur = null;

        if (photoPart == null || photoPart.getSize() == 0) {
            messageErreur = "Veuillez sélectionner une photo.";
            return;
        }

        String contentType = photoPart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            messageErreur = "Seules les images sont acceptées (JPG, PNG).";
            return;
        }

        if (photoPart.getSize() > 2 * 1024 * 1024) {
            messageErreur = "La photo ne doit pas dépasser 2 Mo.";
            return;
        }

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            User u = authBean.getUtilisateurConnecte();

            if (u == null) {
                messageErreur = "Session expirée, veuillez vous reconnecter.";
                return;
            }

            String uploadDir = ec.getRealPath("/resources/photos/");
            Files.createDirectories(Paths.get(uploadDir));

            String ext = contentType.contains("png") ? ".png" : ".jpg";
            String fileName = "user_" + u.getId() + ext;
            Path dest = Paths.get(uploadDir, fileName);

            try (InputStream is = photoPart.getInputStream()) {
                Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
            }

            u.setPhoto("resources/photos/" + fileName);
            utilisateurDAO.update(u);

            messageSucces = "Photo mise à jour avec succès !";
        } catch (IOException e) {
            messageErreur = "Erreur lors de l'upload : " + e.getMessage();
        }
    }

    public String getAncienMotDePasse() { return ancienMotDePasse; }
    public void setAncienMotDePasse(String s) { this.ancienMotDePasse = s; }

    public String getNouveauMotDePasse() { return nouveauMotDePasse; }
    public void setNouveauMotDePasse(String s) { this.nouveauMotDePasse = s; }

    public String getConfirmerMotDePasse() { return confirmerMotDePasse; }
    public void setConfirmerMotDePasse(String s) { this.confirmerMotDePasse = s; }

    public String getMessageSucces() { return messageSucces; }
    public String getMessageErreur() { return messageErreur; }

    public Part getPhotoPart() { return photoPart; }
    public void setPhotoPart(Part p) { this.photoPart = p; }
}