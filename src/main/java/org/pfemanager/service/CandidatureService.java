package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional; // Import important pour les écritures
import org.pfemanager.enums.StatutCandidature;
import org.pfemanager.model.Candidature;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional // Cette annotation permet à WildFly de gérer begin/commit automatiquement
public class CandidatureService implements Serializable {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

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

    public void accepter(Long id, String remarque) {
        Candidature c = em.find(Candidature.class, id);
        if (c != null) {
            c.setStatut(StatutCandidature.ACCEPTEE);
            c.setDateReponse(LocalDateTime.now());
            c.setRemarqueEncadrant(remarque);
            // Pas besoin de em.merge() ici, l'objet est "managed" par la transaction
        }
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
}