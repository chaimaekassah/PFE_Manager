package org.pfemanager.controller;

import org.pfemanager.enums.StatutUser;
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

    public void search() {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            users = userService.search(searchKeyword);
        } else {
            loadUsers();
        }
    }

    public void resetSearch() {
        searchKeyword = null;
        loadUsers();
    }

    public void toggleStatut(Long userId) {
        userService.toggleStatut(userId);
        addMessage(FacesMessage.SEVERITY_INFO, "Statut modifié");
        loadUsers();
    }

    public void delete(Long userId) {
        userService.delete(userId);
        addMessage(FacesMessage.SEVERITY_INFO, "Utilisateur supprimé");
        loadUsers();
    }

    public String goToAdd() {
        return "ajouter-utilisateur?faces-redirect=true";
    }

    public String goToEdit(Long userId) {
        return "modifier-utilisateur?faces-redirect=true&userId=" + userId;
    }

    public String getStatutClass(User user) {
        if (user == null || user.getStatut() == null) {
            return "";
        }

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

    public String getRoleClass(User user) {
        if (user == null || user.getRole() == null) {
            return "";
        }

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

    public long getTotalUsers() {
        return users != null ? users.size() : 0;
    }

    public long getActifUsers() {
        if (users == null) {
            return 0;
        }
        return users.stream()
                .filter(u -> u.getStatut() == StatutUser.ACTIF)
                .count();
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severity, message, null)
        );
    }

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