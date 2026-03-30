package org.pfemanager.controller;

import org.pfemanager.enums.Role;
import org.pfemanager.model.User;
import org.pfemanager.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

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
        // Par défaut : premier étudiant trouvé
        currentUser = findFirstByRole(Role.ETUDIANT);
        selectedUserId = currentUser != null ? currentUser.getId() : null;
    }

    public String login() {
        if (selectedUserId != null) {
            currentUser = userService.findById(selectedUserId).orElse(null);
        }
        return "test-module?faces-redirect=true";
    }

    public String loginAsEtudiant() {
        currentUser = findFirstByRole(Role.ETUDIANT);
        selectedUserId = currentUser != null ? currentUser.getId() : null;
        return "test-module?faces-redirect=true";
    }

    public String loginAsEncadrant() {
        currentUser = findFirstByRole(Role.ENCADRANT);
        selectedUserId = currentUser != null ? currentUser.getId() : null;
        return "test-module?faces-redirect=true";
    }

    public String loginAsAdmin() {
        currentUser = findFirstByRole(Role.ADMIN);
        selectedUserId = currentUser != null ? currentUser.getId() : null;
        return "test-module?faces-redirect=true";
    }

    public String logout() {
        currentUser = null;
        selectedUserId = null;
        return "test-module?faces-redirect=true";
    }

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

    private User findFirstByRole(Role role) {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .filter(u -> u.getRole() == role)
                .findFirst()
                .orElse(null);
    }

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