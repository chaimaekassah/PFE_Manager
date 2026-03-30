package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.pfemanager.model.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DocumentService implements Serializable {

    private static final long serialVersionUID = 1L;

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("default");

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public List<Document> getAll() {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT d FROM Document d ORDER BY d.id", Document.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Document> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Document.class, id));
        } finally {
            em.close();
        }
    }

    public List<Document> getDocumentsByProjet(Long projetId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT d FROM Document d WHERE d.projet.id = :id",
                    Document.class
            ).setParameter("id", projetId).getResultList();
        } finally {
            em.close();
        }
    }

    public Document create(Document document) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            if (document.getDateDepot() == null) {
                document.setDateDepot(LocalDateTime.now());
            }

            em.persist(document);
            em.getTransaction().commit();

            return document;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            Document doc = em.find(Document.class, id);
            if (doc != null) {
                em.remove(doc);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}