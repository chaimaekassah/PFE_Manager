package org.pfemanager.controller;

import org.pfemanager.model.Candidature;
import org.pfemanager.service.CandidatureService;
import org.pfemanager.enums.StatutCandidature;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Bean pour la gestion des candidatures côté encadrant
 */
@Named("candidatureEncadrantBean")
@ViewScoped
public class CandidatureEncadrantBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CandidatureService candidatureService;

    @Inject
    private TestModuleBean testModuleBean;

    private List<Candidature> candidatures;
    private Candidature selectedCandidature;
    private String remarque;

    @PostConstruct
    public void init() {
        loadCandidatures();
    }

    public void loadCandidatures() {
        if (testModuleBean.getCurrentUser() != null) {
            candidatures = candidatureService.getCandidaturesByEncadrant(
                    testModuleBean.getCurrentUser().getId()
            );
        }
    }

    // Voir les détails d'une candidature
    public void voirDetails(Candidature candidature) {
        this.selectedCandidature = candidature;
        this.remarque = candidature.getRemarqueEncadrant();
    }

    // Accepter une candidature
    public void accepter() {
        if (selectedCandidature != null) {
            candidatureService.accepter(selectedCandidature.getId(), remarque);
            addMessage(FacesMessage.SEVERITY_INFO, "Candidature acceptée");
            selectedCandidature = null;
            remarque = null;
            loadCandidatures();
        }
    }

    // Refuser une candidature
    public void refuser() {
        if (selectedCandidature != null) {
            candidatureService.refuser(selectedCandidature.getId(), remarque);
            addMessage(FacesMessage.SEVERITY_INFO, "Candidature refusée");
            selectedCandidature = null;
            remarque = null;
            loadCandidatures();
        }
    }

    // Fermer le dialogue
    public void annuler() {
        selectedCandidature = null;
        remarque = null;
    }

    // Statistiques
    public long getCandidaturesEnAttente() {
        if (candidatures == null) return 0;
        return candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.EN_ATTENTE)
                .count();
    }

    public long getCandidaturesTraitees() {
        if (candidatures == null) return 0;
        return candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.ACCEPTEE ||
                        c.getStatut() == StatutCandidature.REFUSEE)
                .count();
    }

    // Vérifier si une candidature peut être traitée
    public boolean canTraiter(Candidature candidature) {
        return candidature.getStatut() == StatutCandidature.EN_ATTENTE;
    }

    // Classe CSS selon le statut
    public String getStatutClass(StatutCandidature statut) {
        switch (statut) {
            case EN_ATTENTE:
                return "statut-en-attente";
            case ACCEPTEE:
                return "statut-acceptee";
            case REFUSEE:
                return "statut-refusee";
            case RETIREE:
                return "statut-retiree";
            default:
                return "";
        }
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, message, null));
    }

    // Getters et Setters
    public List<Candidature> getCandidatures() {
        return candidatures;
    }

    public void setCandidatures(List<Candidature> candidatures) {
        this.candidatures = candidatures;
    }

    public Candidature getSelectedCandidature() {
        return selectedCandidature;
    }

    public void setSelectedCandidature(Candidature selectedCandidature) {
        this.selectedCandidature = selectedCandidature;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }
    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

}