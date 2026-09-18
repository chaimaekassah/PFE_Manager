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

        // Vérifie le mot de passe avec BCrypt ou en clair
        boolean isMatch = false;
        try {
            isMatch = PasswordUtil.verifier(motDePasse, utilisateur.getMotDePasse());
        } catch (IllegalArgumentException e) {
            // Hash invalide (peut-être en texte clair)
            if (motDePasse.equals(utilisateur.getMotDePasse())) {
                isMatch = true;
                // Mettre à jour avec le hash
                utilisateur.setMotDePasse(PasswordUtil.hasher(motDePasse));
                utilisateurDAO.update(utilisateur);
            }
        }

        if (!isMatch) {
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

        // S'assurer que le statut est défini (sécurité, au cas où l'appelant ne l'a pas fait)
        if (utilisateur.getStatut() == null || utilisateur.getStatut().isEmpty()) {
            utilisateur.setStatut("ACTIF");
        }

        // save() au lieu de sauvegarder()
        utilisateurDAO.save(utilisateur);
        return true;
    }
}