package org.pfemanager.controller;

import org.pfemanager.model.User;
import org.pfemanager.service.UserService;
import org.pfemanager.enums.Role;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Bean de test pour simuler une session utilisateur
 * Permet de naviguer entre les différentes pages sans authentification
 */
@Named("testModuleBean")
@SessionScoped
public class TestModuleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    private User currentUser;
    private Long selectedUserId;

    @PostConstruct
    public void init() {
        // Par défaut, se connecter en tant qu'étudiante Amal Benali
        currentUser = userService.findById(2L).orElse(null);
        selectedUserId = 2L;
    }

    // Simuler la connexion avec un utilisateur spécifique
    public String login() {
        if (selectedUserId != null) {
            currentUser = userService.findById(selectedUserId).orElse(null);
        }
        return "test-module?faces-redirect=true";
    }

    // Connexion rapide en tant qu'étudiant
    public String loginAsEtudiant() {
        currentUser = userService.findById(2L).orElse(null); // Amal Benali
        selectedUserId = 2L;
        return "test-module?faces-redirect=true";
    }

    // Connexion rapide en tant qu'encadrant
    public String loginAsEncadrant() {
        currentUser = userService.findById(5L).orElse(null); // Samira Tazi
        selectedUserId = 5L;
        return "test-module?faces-redirect=true";
    }

    // Connexion rapide en tant qu'admin
    public String loginAsAdmin() {
        currentUser = userService.findById(1L).orElse(null); // Fatima Alami
        selectedUserId = 1L;
        return "test-module?faces-redirect=true";
    }

    // Déconnexion
    public String logout() {
        currentUser = null;
        selectedUserId = null;
        return "test-module?faces-redirect=true";
    }

    // Vérifications de rôle
    public boolean isEtudiant() {
        return currentUser != null && currentUser.getRole() == Role.ETUDIANT;
    }

    public boolean isEncadrant() {
        return currentUser != null && currentUser.getRole() == Role.ENCADRANT;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // Getters et Setters
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Long getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(Long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }
}