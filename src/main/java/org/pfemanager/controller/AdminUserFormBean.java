package org.pfemanager.controller;

import org.pfemanager.model.User;
import org.pfemanager.model.Role;
import org.pfemanager.enums.StatutUser;
import org.pfemanager.service.UserService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named("adminUserFormBean")
@RequestScoped
public class AdminUserFormBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    private User user = new User();
    private Long userId;
    private boolean editMode = false;

    public void loadUser() {
        if (userId != null) {
            Optional<User> existing = userService.findById(userId);
            if (existing.isPresent()) {
                user = existing.get();
                editMode = true;
            }
        }
    }

    public String save() {
        try {
            if (editMode) {
                userService.update(user);
            } else {
                userService.create(user);
            }
            return "/admin/gestion-utilisateurs?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur lors de l'enregistrement : " + e.getMessage(),
                            null
                    )
            );
            return null;
        }
    }

    public String cancel() {
        return "/admin/gestion-utilisateurs?faces-redirect=true";
    }

    public List<Role> getAllRoles() {
        return Arrays.stream(Role.values())
                .filter(r -> r != Role.ADMINISTRATEUR)
                .collect(Collectors.toList());
    }

    public List<StatutUser> getAllStatuts() {
        return Arrays.asList(StatutUser.values());
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
}