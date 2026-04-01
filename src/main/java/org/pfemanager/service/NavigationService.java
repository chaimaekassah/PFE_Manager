package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.pfemanager.model.Role;

@ApplicationScoped // Existe pour toute l'application
public class NavigationService {

    public String getDashboardByRole(Role role) {
        if (role == null) return "/login?faces-redirect=true";

        switch (role) {
            case ADMINISTRATEUR: return "/admin/dashboard?faces-redirect=true";
            case ENCADRANT:      return "/encadrant/sujets?faces-redirect=true";
            case ETUDIANT:       return "/etudiant/pfe_market?faces-redirect=true";
            default:             return "/login?faces-redirect=true";
        }
    }
}