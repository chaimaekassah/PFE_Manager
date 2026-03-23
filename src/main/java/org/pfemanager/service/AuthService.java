package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.pfemanager.dao.UtilisateurDAO;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.util.PasswordUtil;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    private UtilisateurDAO utilisateurDAO;

    // LOGIN : vérifie email + mot de passe hashé
    public Utilisateur authentifier(String email, String motDePasse) {
        Optional<Utilisateur> optUtilisateur = utilisateurDAO.findByEmail(email);

        if (optUtilisateur.isEmpty()) {
            return null;
        }

        Utilisateur utilisateur = optUtilisateur.get();

        // Vérifie le mot de passe avec BCrypt
        if (!PasswordUtil.verifier(motDePasse, utilisateur.getMotDePasse())) {
            return null;
        }

        return utilisateur;
    }

    // REGISTER : crée un compte avec mot de passe hashé
    public boolean inscrire(Utilisateur utilisateur) {
        // Vérifier que l'email n'existe pas déjà
        Optional<Utilisateur> existant = utilisateurDAO.findByEmail(utilisateur.getEmail());
        if (existant.isPresent()) {
            return false;
        }

        // Hasher le mot de passe avant de sauvegarder
        String motDePasseHashe = PasswordUtil.hasher(utilisateur.getMotDePasse());
        utilisateur.setMotDePasse(motDePasseHashe);

        // save() au lieu de sauvegarder()
        utilisateurDAO.save(utilisateur);
        return true;
    }
}