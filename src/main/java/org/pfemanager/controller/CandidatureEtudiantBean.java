package org.pfemanager.controller;

import org.pfemanager.model.Candidature;
import org.pfemanager.model.Sujet;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.CandidatureService;
import org.pfemanager.service.SujetService;
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

@Named("candidatureEtudiantBean")
@ViewScoped
public class CandidatureEtudiantBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient CandidatureService candidatureService;

    @Inject
    private transient SujetService sujetService;

    @Inject
    private AuthBean authBean; // ✅ On utilise AuthBean au lieu de TestModuleBean

    private List<Candidature> candidatures;
    private Candidature selectedCandidature;

    // ── Pour le modal nouvelle candidature ────────────────
    private boolean formulaireOuvert = false;
    private Long sujetIdSelectionne;
    private String messageMotivation;
    private List<Sujet> sujetsDisponibles;

    @PostConstruct
    public void init() {
        loadCandidatures();
        loadSujetsDisponibles();
    }

    public void loadCandidatures() {
        Utilisateur user = authBean.getUtilisateurConnecte();
        if (user != null) {
            candidatures = candidatureService.getCandidaturesByEtudiant(user.getId());
        }
    }

    public void loadSujetsDisponibles() {
        sujetsDisponibles = sujetService.getSujetsDisponibles();
    }

    // ── Modal nouvelle candidature ─────────────────────────
    public void ouvrirFormulaire() {
        formulaireOuvert = true;
        sujetIdSelectionne = null;
        messageMotivation = null;
        loadSujetsDisponibles();
    }

    public void fermerFormulaire() {
        formulaireOuvert = false;
        sujetIdSelectionne = null;
        messageMotivation = null;
    }
    // ── Preview sujet dans le modal ────────────────────────
    public String getSujetPreviewTitre() {
        if (sujetIdSelectionne == null || sujetIdSelectionne == 0) return "";
        Sujet s = sujetService.findById(sujetIdSelectionne);
        return s != null ? s.getTitre() : "";
    }

    public String getSujetPreviewEncadrant() {
        if (sujetIdSelectionne == null || sujetIdSelectionne == 0) return "";
        Sujet s = sujetService.findById(sujetIdSelectionne);
        return s != null ? s.getEncadrant().getNom() : "";
    }

    // ── Actions ────────────────────────────────────────────
    public void voirDetails(Candidature candidature) {
        this.selectedCandidature = candidature;
    }

    public void annuler() {
        this.selectedCandidature = null;
    }

    // ── Statistiques ───────────────────────────────────────
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

    public boolean canRetirer(Candidature candidature) {
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

    // ── Getters / Setters ──────────────────────────────────
    public List<Candidature> getCandidatures() { return candidatures; }
    public Candidature getSelectedCandidature() { return selectedCandidature; }
    public void setSelectedCandidature(Candidature c) { this.selectedCandidature = c; }
    public boolean isFormulaireOuvert() { return formulaireOuvert; }
    public void setFormulaireOuvert(boolean b) { this.formulaireOuvert = b; }
    public Long getSujetIdSelectionne() { return sujetIdSelectionne; }
    public void setSujetIdSelectionne(Long id) { this.sujetIdSelectionne = id; }
    public String getMessageMotivation() { return messageMotivation; }
    public void setMessageMotivation(String m) { this.messageMotivation = m; }
    public List<Sujet> getSujetsDisponibles() { return sujetsDisponibles; }
    // Ajoute ces deux champs
    private String messageSucces;
    private String messageErreur;

    // Modifie postulerDepuisModal() :
    public void postulerDepuisModal() {
        messageSucces = null;
        messageErreur = null;

        Utilisateur user = authBean.getUtilisateurConnecte();
        if (user == null) {
            messageErreur = "Session expirée.";
            return;
        }
        if (sujetIdSelectionne == null) {
            messageErreur = "Veuillez choisir un sujet.";
            return;
        }
        if (messageMotivation == null || messageMotivation.trim().isEmpty()) {
            messageErreur = "Veuillez saisir votre motivation.";
            return;
        }

        Sujet sujet = sujetService.findById(sujetIdSelectionne);
        if (sujet == null) {
            messageErreur = "Sujet introuvable.";
            return;
        }

        boolean dejaPostule = candidatureService.existeCandidature(
                user.getId(), sujetIdSelectionne);
        if (dejaPostule) {
            messageErreur = "Vous avez déjà postulé à ce sujet.";
            return;
        }

        Candidature c = new Candidature();
        c.setEtudiant(user);
        c.setEncadrant(sujet.getEncadrant());
        c.setSujet(sujet.getTitre());
        c.setMessageMotivation(messageMotivation.trim());
        candidatureService.create(c);

        messageSucces = "Candidature envoyée avec succès !";
        fermerFormulaire();
        loadCandidatures();
    }

    // Modifie retirer() :
    public void retirer(Long candidatureId) {
        messageSucces = null;
        messageErreur = null;
        candidatureService.retirer(candidatureId);
        messageSucces = "Candidature retirée avec succès.";
        loadCandidatures();
        selectedCandidature = null;
    }

    // Ajoute les getters :
    public String getMessageSucces() { return messageSucces; }
    public String getMessageErreur() { return messageErreur; }
}