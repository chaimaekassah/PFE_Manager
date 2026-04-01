package org.pfemanager.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.pfemanager.model.Sujet;

import java.util.List;

@Stateless
    public class SujetService {
        @PersistenceContext
        private EntityManager em;

        public List<Sujet> findAll() {
            return em.createQuery("SELECT s FROM Sujet s", Sujet.class).getResultList();
        }

        public void save(Sujet sujet) {
            if (sujet.getId() == null) em.persist(sujet);
            else em.merge(sujet);
        }

        public void delete(Long id) {
            Sujet s = em.find(Sujet.class, id);
            if (s != null) em.remove(s);
        }

        // Pour la recherche simple
        public List<Sujet> search(String query) {
            return em.createQuery("SELECT s FROM Sujet s WHERE s.titre LIKE :q OR s.technologies LIKE :q", Sujet.class)
                    .setParameter("q", "%" + query + "%")
                    .getResultList();
        }
    }

