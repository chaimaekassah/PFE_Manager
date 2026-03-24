package org.pfemanager.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import org.pfemanager.model.Sujet;
import org.pfemanager.model.Utilisateur;

import java.util.List;

@Stateless
public class SujetDAO {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    public void create(Sujet s) {
        em.persist(s);
    }

    public Sujet findById(Long id) {
        return em.createQuery(
                        "SELECT s FROM Sujet s LEFT JOIN FETCH s.encadrant WHERE s.id = :id",
                        Sujet.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Sujet update(Sujet s) {
        return em.merge(s);
    }

    public void delete(Long id) {
        Sujet s = em.find(Sujet.class, id);
        if (s != null) em.remove(s);
    }

    // Tous les sujets (admin)
    public List<Sujet> findAll() {
        return em.createQuery(
                        "SELECT s FROM Sujet s LEFT JOIN FETCH s.encadrant " +
                                "ORDER BY s.dateCreation DESC", Sujet.class)
                .getResultList();
    }

    // Sujets d'un encadrant
    public List<Sujet> findByEncadrant(Long encadrantId) {
        return em.createQuery(
                        "SELECT s FROM Sujet s LEFT JOIN FETCH s.encadrant " +
                                "WHERE s.encadrant.id = :id ORDER BY s.dateCreation DESC",
                        Sujet.class)
                .setParameter("id", encadrantId)
                .getResultList();
    }

    // Recherche par titre ou technologie
    public List<Sujet> rechercher(String motCle) {
        return em.createQuery(
                        "SELECT s FROM Sujet s LEFT JOIN FETCH s.encadrant " +
                                "WHERE LOWER(s.titre) LIKE :mc OR LOWER(s.technologiesRequises) LIKE :mc " +
                                "ORDER BY s.dateCreation DESC", Sujet.class)
                .setParameter("mc", "%" + motCle.toLowerCase() + "%")
                .getResultList();
    }
}