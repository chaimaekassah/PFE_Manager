package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.pfemanager.dao.UtilisateurDAO;
import org.pfemanager.model.User;
import org.pfemanager.util.PasswordUtil;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    private UtilisateurDAO utilisateurDAO;

    // LOGIN : vérifie email + mot de passe hashé
    public User authentifier(String email, String motDePasse) {
        Optional<User> optUtilisateur = utilisateurDAO.findByEmail(email);

        if (optUtilisateur.isEmpty()) {
            return null;
        }

        User utilisateur = optUtilisateur.get();

        if (!PasswordUtil.verifier(motDePasse, utilisateur.getMotDePasse())) {
            return null;
        }

        return utilisateur;
    }

    // REGISTER : crée un compte avec mot de passe hashé
    public boolean inscrire(User utilisateur) {
        Optional<User> existant = utilisateurDAO.findByEmail(utilisateur.getEmail());
        if (existant.isPresent()) {
            return false;
        }

        String motDePasseHashe = PasswordUtil.hasher(utilisateur.getMotDePasse());
        utilisateur.setMotDePasse(motDePasseHashe);

        utilisateurDAO.save(utilisateur);
        return true;
    }
}