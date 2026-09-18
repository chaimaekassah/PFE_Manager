package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional; // Import important pour les écritures
import org.pfemanager.enums.StatutCandidature;
import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.Candidature;
import org.pfemanager.model.Projet;
import org.pfemanager.model.Sujet;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional // Cette annotation permet à WildFly de gérer begin/commit automatiquement
public class CandidatureService implements Serializable {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    // Dans CandidatureService.java — modifie la méthode accepter()
    @Inject
    private ProjetService projetService; // ← ajoute cette injection

    public void accepter(Long id, String remarque) {
        Candidature c = em.find(Candidature.class, id);
        if (c != null) {
            c.setStatut(StatutCandidature.ACCEPTEE);
            c.setDateReponse(LocalDateTime.now());
            c.setRemarqueEncadrant(remarque);

            // ✅ Créer le projet automatiquement
            creerProjetDepuisCandidature(c);
        }
    }

    private void creerProjetDepuisCandidature(Candidature candidature) {
        // Vérifier qu'un projet n'existe pas déjà pour cet étudiant
        Long count = em.createQuery(
                        "SELECT COUNT(p) FROM Projet p WHERE p.etudiant.id = :id",
                        Long.class)
                .setParameter("id", candidature.getEtudiant().getId())
                .getSingleResult();

        if (count == 0) {
            Projet projet = new Projet();
            projet.setEtudiant(candidature.getEtudiant());
            projet.setEncadrant(candidature.getEncadrant());
            projet.setSujet(candidature.getSujet());
            projet.setDateDebut(LocalDateTime.now());
            projet.setStatut(StatutProjet.EN_COURS);
            em.persist(projet);
        }
    }
    public List<Candidature> getAll() {
        // Plus besoin de em(), on utilise directement "em"
        return em.createQuery("SELECT c FROM Candidature c", Candidature.class)
                .getResultList();
    }

    public Optional<Candidature> findById(Long id) {
        return Optional.ofNullable(em.find(Candidature.class, id));
    }

    public List<Candidature> getCandidaturesByEtudiant(Long etudiantId) {
        return em.createQuery(
                        "SELECT c FROM Candidature c WHERE c.etudiant.id = :id",
                        Candidature.class
                )
                .setParameter("id", etudiantId)
                .getResultList();
    }

    public List<Candidature> getCandidaturesByEncadrant(Long encadrantId) {
        return em.createQuery(
                        "SELECT c FROM Candidature c WHERE c.encadrant.id = :id",
                        Candidature.class
                )
                .setParameter("id", encadrantId)
                .getResultList();
    }

    public Candidature create(Candidature c) {
        // Plus de em.getTransaction().begin() ! @Transactional s'en occupe
        c.setDateCandidature(LocalDateTime.now());
        c.setStatut(StatutCandidature.EN_ATTENTE);
        em.persist(c);
        return c;
    }

    public void refuser(Long id, String remarque) {
        Candidature c = em.find(Candidature.class, id);
        if (c != null) {
            c.setStatut(StatutCandidature.REFUSEE);
            c.setDateReponse(LocalDateTime.now());
            c.setRemarqueEncadrant(remarque);
        }
    }

    public void retirer(Long id) {
        Candidature c = em.find(Candidature.class, id);
        if (c != null && c.getStatut() == StatutCandidature.EN_ATTENTE) {
            c.setStatut(StatutCandidature.RETIREE);
        }
    }
    // Vérifie si l'étudiant a déjà une candidature active pour ce sujet
    public boolean existeCandidature(Long etudiantId, Long sujetId) {
        // On cherche par titre du sujet car Candidature.sujet est un String
        Sujet sujet = em.find(Sujet.class, sujetId);
        if (sujet == null) return false;

        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM Candidature c " +
                                "WHERE c.etudiant.id = :etudiantId " +
                                "AND c.sujet = :titreSujet " +
                                "AND c.statut <> :statut",
                        Long.class)
                .setParameter("etudiantId", etudiantId)
                .setParameter("titreSujet", sujet.getTitre())
                .setParameter("statut", StatutCandidature.RETIREE)
                .getSingleResult();
        return count > 0;
    }
}