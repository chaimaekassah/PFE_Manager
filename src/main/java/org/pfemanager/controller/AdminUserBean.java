package org.pfemanager.controller;

import org.pfemanager.model.User;
import org.pfemanager.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Bean pour la gestion des utilisateurs par l'administrateur
 */
@Named("adminUserBean")
@ViewScoped
public class AdminUserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    private List<User> users;
    private String searchKeyword;

    @PostConstruct
    public void init() {
        loadUsers();
    }

    public void loadUsers() {
        users = userService.getAllUsers();
    }

    // Rechercher
    public void search() {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            users = userService.search(searchKeyword);
        } else {
            loadUsers();
        }
    }

    // Réinitialiser la recherche
    public void resetSearch() {
        searchKeyword = null;
        loadUsers();
    }

    // Activer/Désactiver un utilisateur
    public void toggleStatut(Long userId) {
        userService.toggleStatut(userId);
        addMessage(FacesMessage.SEVERITY_INFO, "Statut modifié");
        loadUsers();
    }

    // Supprimer logiquement un utilisateur
    public void delete(Long userId) {
        userService.delete(userId);
        addMessage(FacesMessage.SEVERITY_INFO, "Utilisateur supprimé");
        loadUsers();
    }

    // Navigation vers la page d'ajout
    public String goToAdd() {
        return "ajouter-utilisateur?faces-redirect=true";
    }

    // Navigation vers la page de modification
    public String goToEdit(Long userId) {
        return "modifier-utilisateur?faces-redirect=true&userId=" + userId;
    }

    // Classe CSS selon le statut
    public String getStatutClass(User user) {
        switch (user.getStatut()) {
            case ACTIF:
                return "statut-actif";
            case INACTIF:
                return "statut-inactif";
            case SUPPRIME:
                return "statut-supprime";
            default:
                return "";
        }
    }

    // Classe CSS selon le rôle
    public String getRoleClass(User user) {
        switch (user.getRole()) {
            case ETUDIANT:
                return "role-etudiant";
            case ENCADRANT:
                return "role-encadrant";
            case ADMIN:
                return "role-admin";
            default:
                return "";
        }
    }

    // Statistiques
    public long getTotalUsers() {
        return users != null ? users.size() : 0;
    }

    public long getActifUsers() {
        if (users == null) return 0;
        return users.stream()
                .filter(u -> u.getStatut().name().equals("ACTIF"))
                .count();
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, message, null));
    }

    // Getters et Setters
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}