package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.pfemanager.dao.ResetTokenDAO;
import org.pfemanager.dao.UtilisateurDAO;
import org.pfemanager.model.ResetToken;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PasswordResetService {

    @Inject private ResetTokenDAO resetTokenDAO;
    @Inject private UtilisateurDAO utilisateurDAO;
    @Inject private EmailService emailService;

    // Étape 1 : demande de réinitialisation
    public boolean demanderReinitialisation(String email, String baseUrl) {
        Optional<Utilisateur> opt = utilisateurDAO.findByEmail(email);
        if (opt.isEmpty()) {
            // On retourne true quand même pour ne pas révéler si l'email existe
            return true;
        }

        // Supprimer les anciens tokens
        resetTokenDAO.deleteByEmail(email);

        // Créer un nouveau token
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setDateExpiration(LocalDateTime.now().plusMinutes(30));
        resetToken.setUtilise(false);
        resetTokenDAO.save(resetToken);

        // Envoyer l'email
        String lien = baseUrl + "/reset-password.xhtml?token=" + token;
        emailService.envoyerLienReinitialisation(email, lien);

        return true;
    }

    // Étape 2 : vérifier le token
    public boolean tokenValide(String token) {
        Optional<ResetToken> opt = resetTokenDAO.findByToken(token);
        if (opt.isEmpty()) return false;
        ResetToken rt = opt.get();
        return !rt.isUtilise() && rt.getDateExpiration().isAfter(LocalDateTime.now());
    }

    // Étape 3 : réinitialiser le mot de passe
    public boolean reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        Optional<ResetToken> opt = resetTokenDAO.findByToken(token);
        if (opt.isEmpty()) return false;

        ResetToken rt = opt.get();
        if (rt.isUtilise() || rt.getDateExpiration().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Mettre à jour le mot de passe
        Optional<Utilisateur> userOpt = utilisateurDAO.findByEmail(rt.getEmail());
        if (userOpt.isEmpty()) return false;

        Utilisateur u = userOpt.get();
        u.setMotDePasse(PasswordUtil.hasher(nouveauMotDePasse));
        utilisateurDAO.update(u);

        // Marquer le token comme utilisé
        rt.setUtilise(true);
        resetTokenDAO.update(rt);

        return true;
    }
}