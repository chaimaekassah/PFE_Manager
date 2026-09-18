package org.pfemanager.controller;

import org.pfemanager.model.Candidature;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.CandidatureService;
import org.pfemanager.enums.StatutCandidature;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@Named("candidatureEncadrantBean")
@ViewScoped
public class CandidatureEncadrantBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient CandidatureService candidatureService;

    @Inject
    private AuthBean authBean; // ✅ AuthBean au lieu de TestModuleBean

    private List<Candidature> candidatures;
    private Candidature selectedCandidature;
    private String remarque;

    @PostConstruct
    public void init() {
        loadCandidatures();
    }

    // Appelé par f:event preRenderView pour forcer le rechargement
    public void onPreRender(ComponentSystemEvent event) {
        loadCandidatures();
    }

    public void loadCandidatures() {
        Utilisateur user = authBean.getUtilisateurConnecte();
        if (user != null) {
            candidatures = candidatureService
                    .getCandidaturesByEncadrant(user.getId());
        }
    }

    public void voirDetails(Candidature candidature) {
        this.selectedCandidature = candidature;
        this.remarque = candidature.getRemarqueEncadrant();
    }

    public void accepter() {
        if (selectedCandidature != null) {
            candidatureService.accepter(selectedCandidature.getId(), remarque);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Candidature de "
                            + selectedCandidature.getEtudiant().getNom()
                            + " acceptée avec succès.");
            selectedCandidature = null;
            remarque = null;
            loadCandidatures();
        }
    }

    public void refuser() {
        if (selectedCandidature != null) {
            candidatureService.refuser(selectedCandidature.getId(), remarque);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Candidature de "
                            + selectedCandidature.getEtudiant().getNom()
                            + " refusée.");
            selectedCandidature = null;
            remarque = null;
            loadCandidatures();
        }
    }

    public void annuler() {
        selectedCandidature = null;
        remarque = null;
    }

    public long getCandidaturesEnAttente() {
        if (candidatures == null) return 0;
        return candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.EN_ATTENTE)
                .count();
    }

    public long getCandidaturesTraitees() {
        if (candidatures == null) return 0;
        return candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.ACCEPTEE
                        || c.getStatut() == StatutCandidature.REFUSEE)
                .count();
    }

    public boolean canTraiter(Candidature candidature) {
        return candidature != null
                && candidature.getStatut() == StatutCandidature.EN_ATTENTE;
    }

    public String getStatutClass(StatutCandidature statut) {
        if (statut == null) return "";
        switch (statut) {
            case EN_ATTENTE: return "en_attente";
            case ACCEPTEE:   return "acceptee";
            case REFUSEE:    return "refusee";
            case RETIREE:    return "retiree";
            default:         return "";
        }
    }

    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void addMessage(FacesMessage.Severity severity, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, msg, null));
    }

    // Getters / Setters
    public List<Candidature> getCandidatures() { return candidatures; }
    public Candidature getSelectedCandidature() { return selectedCandidature; }
    public void setSelectedCandidature(Candidature c) { this.selectedCandidature = c; }
    public String getRemarque() { return remarque; }
    public void setRemarque(String r) { this.remarque = r; }
}