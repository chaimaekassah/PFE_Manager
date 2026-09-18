package org.pfemanager.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.pfemanager.model.Sujet;

import java.util.List;

@Stateless
public class SujetService {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    public List<Sujet> findAll() {
        return em.createQuery("SELECT s FROM Sujet s", Sujet.class)
                .getResultList();
    }

    public Sujet findById(Long id) {
        return em.find(Sujet.class, id);
    }

    public void save(Sujet sujet) {
        if (sujet.getId() == null) em.persist(sujet);
        else em.merge(sujet);
    }

    public void delete(Long id) {
        Sujet s = em.find(Sujet.class, id);
        if (s != null) em.remove(s);
    }

    public List<Sujet> search(String query) {
        return em.createQuery(
                        "SELECT s FROM Sujet s WHERE " +
                                "LOWER(s.titre) LIKE LOWER(:q) OR " +
                                "LOWER(s.technologiesRequises) LIKE LOWER(:q)",
                        Sujet.class)
                .setParameter("q", "%" + query + "%")
                .getResultList();
    }

    // ✅ Sujets disponibles pour le modal candidature
    public List<Sujet> getSujetsDisponibles() {
        return em.createQuery(
                        "SELECT s FROM Sujet s WHERE s.statut = :statut",
                        Sujet.class)
                .setParameter("statut", Sujet.StatutSujet.DISPONIBLE)
                .getResultList();
    }
}