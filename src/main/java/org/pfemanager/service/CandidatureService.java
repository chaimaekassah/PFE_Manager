package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.pfemanager.enums.StatutCandidature;
import org.pfemanager.model.Candidature;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CandidatureService implements Serializable {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("default");

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public List<Candidature> getAll() {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT c FROM Candidature c", Candidature.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Candidature> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Candidature.class, id));
        } finally {
            em.close();
        }
    }

    public List<Candidature> getCandidaturesByEtudiant(Long etudiantId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT c FROM Candidature c WHERE c.etudiant.id = :id",
                            Candidature.class
                    )
                    .setParameter("id", etudiantId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Candidature> getCandidaturesByEncadrant(Long encadrantId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT c FROM Candidature c WHERE c.encadrant.id = :id",
                            Candidature.class
                    )
                    .setParameter("id", encadrantId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Candidature create(Candidature c) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            c.setDateCandidature(LocalDateTime.now());
            c.setStatut(StatutCandidature.EN_ATTENTE);
            em.persist(c);
            em.getTransaction().commit();
            return c;
        } finally {
            em.close();
        }
    }

    public void accepter(Long id, String remarque) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Candidature c = em.find(Candidature.class, id);
            if (c != null) {
                c.setStatut(StatutCandidature.ACCEPTEE);
                c.setDateReponse(LocalDateTime.now());
                c.setRemarqueEncadrant(remarque);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void refuser(Long id, String remarque) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Candidature c = em.find(Candidature.class, id);
            if (c != null) {
                c.setStatut(StatutCandidature.REFUSEE);
                c.setDateReponse(LocalDateTime.now());
                c.setRemarqueEncadrant(remarque);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void retirer(Long id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Candidature c = em.find(Candidature.class, id);
            if (c != null && c.getStatut() == StatutCandidature.EN_ATTENTE) {
                c.setStatut(StatutCandidature.RETIREE);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}