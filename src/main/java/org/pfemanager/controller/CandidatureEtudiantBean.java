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
 * Bean pour la gestion des candidatures côté étudiant
 */
@Named("candidatureEtudiantBean")
@ViewScoped
public class CandidatureEtudiantBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CandidatureService candidatureService;

    @Inject
    private TestModuleBean testModuleBean;

    private List<Candidature> candidatures;
    private Candidature selectedCandidature;

    @PostConstruct
    public void init() {
        loadCandidatures();
    }

    public void loadCandidatures() {
        if (testModuleBean.getCurrentUser() != null) {
            candidatures = candidatureService.getCandidaturesByEtudiant(
                    testModuleBean.getCurrentUser().getId()
            );
        }
    }

    // Voir les détails d'une candidature
    public void voirDetails(Candidature candidature) {
        this.selectedCandidature = candidature;
    }

    // Fermer le panneau de détails
    public void annuler() {
        this.selectedCandidature = null;
    }

    // Retirer une candidature
    public void retirer(Long candidatureId) {
        candidatureService.retirer(candidatureId);
        addMessage(FacesMessage.SEVERITY_INFO, "Candidature retirée avec succès");
        loadCandidatures();
        selectedCandidature = null;
    }

    // Statistiques
    public long getTotalCandidatures() {
        return candidatures != null ? candidatures.size() : 0;
    }

    public long getCandidaturesEnAttente() {
        if (candidatures == null) return 0;
        return candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.EN_ATTENTE)
                .count();
    }

    public long getCandidaturesAcceptees() {
        if (candidatures == null) return 0;
        return candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.ACCEPTEE)
                .count();
    }

    public long getCandidaturesRefusees() {
        if (candidatures == null) return 0;
        return candidatures.stream()
                .filter(c -> c.getStatut() == StatutCandidature.REFUSEE)
                .count();
    }

    // Vérifier si une candidature peut être retirée
    public boolean canRetirer(Candidature candidature) {
        return candidature != null && candidature.getStatut() == StatutCandidature.EN_ATTENTE;
    }

    // Classe CSS selon le statut
    public String getStatutClass(StatutCandidature statut) {
        if (statut == null) return "";

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
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severity, message, null)
        );
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
    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}