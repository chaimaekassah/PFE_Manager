package org.pfemanager.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.pfemanager.dao.SujetDAO;
import org.pfemanager.model.Sujet;
import org.pfemanager.model.Utilisateur;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class SujetBean implements Serializable {

    @Inject private SujetDAO sujetDAO;
    @Inject private AuthBean authBean;

    private List<Sujet> sujets;
    private Sujet sujetForm = new Sujet();
    private Sujet sujetSelectionne;
    private String motCle;

    @PostConstruct
    public void init() {
        chargerSujets();
    }

    private void chargerSujets() {
        Utilisateur u = authBean.getUtilisateurConnecte();
        if (u == null) return;

        switch (u.getRole()) {
            case ENCADRANT:
                sujets = sujetDAO.findByEncadrant(u.getId());
                break;
            default:
                sujets = sujetDAO.findAll();
        }
    }

    // ── CRUD Encadrant ────────────────────────────────────

    public String creerSujet() {
        sujetForm.setEncadrant(authBean.getUtilisateurConnecte());
        sujetDAO.create(sujetForm);
        sujetForm = new Sujet();
        addSucces("Sujet créé avec succès !");
        return "/encadrant/sujets.xhtml?faces-redirect=true";
    }

    public void preparerModification(Long id) {
        this.sujetForm = sujetDAO.findById(id);
    }

    public String modifierSujet() {
        sujetDAO.update(sujetForm);
        addSucces("Sujet modifié avec succès !");
        return "/encadrant/sujets.xhtml?faces-redirect=true";
    }

    public void supprimerSujet(Long id) {
        sujetDAO.delete(id);
        addSucces("Sujet supprimé.");
        chargerSujets();
    }

    public void selectionnerSujet(String idStr) {
        if (idStr == null || idStr.isEmpty()) return;
        try {
            this.sujetSelectionne = sujetDAO.findById(Long.parseLong(idStr));
            this.sujetForm = this.sujetSelectionne;
        } catch (Exception e) {
            addErreur("Sujet introuvable.");
        }
    }

    // ── Recherche ─────────────────────────────────────────

    public void rechercher() {
        if (motCle == null || motCle.trim().isEmpty()) {
            chargerSujets();
        } else {
            sujets = sujetDAO.rechercher(motCle.trim());
        }
    }

    public void reinitialiserRecherche() {
        motCle = null;
        chargerSujets();
    }

    // ── Helpers ───────────────────────────────────────────

    private void addSucces(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void addErreur(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    // Getters / Setters
    public List<Sujet> getSujets() { return sujets; }
    public Sujet getSujetForm() { return sujetForm; }
    public void setSujetForm(Sujet s) { this.sujetForm = s; }
    public Sujet getSujetSelectionne() { return sujetSelectionne; }
    public void setSujetSelectionne(Sujet s) { this.sujetSelectionne = s; }
    public String getMotCle() { return motCle; }
    public void setMotCle(String m) { this.motCle = m; }

    public String sauvegarder() {
        if (sujetForm.getId() == null) {
            // Création
            sujetForm.setEncadrant(authBean.getUtilisateurConnecte());
            sujetDAO.create(sujetForm);
            addSucces("Sujet créé avec succès !");
        } else {
            // Modification
            sujetDAO.update(sujetForm);
            addSucces("Sujet modifié avec succès !");
        }
        sujetForm = new Sujet();
        return "/encadrant/sujets.xhtml?faces-redirect=true";
    }
}