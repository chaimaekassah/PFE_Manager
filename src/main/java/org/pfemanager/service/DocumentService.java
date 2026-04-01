package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.pfemanager.model.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional // Indispensable pour que WildFly gère les transactions (create/delete)
public class DocumentService implements Serializable {

    private static final long serialVersionUID = 1L;

    // On laisse le serveur injecter l'EntityManager géré
    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    public List<Document> getAll() {
        return em.createQuery("SELECT d FROM Document d ORDER BY d.id", Document.class)
                .getResultList();
    }

    public Optional<Document> findById(Long id) {
        return Optional.ofNullable(em.find(Document.class, id));
    }

    public List<Document> getDocumentsByProjet(Long projetId) {
        return em.createQuery(
                "SELECT d FROM Document d WHERE d.projet.id = :id",
                Document.class
        ).setParameter("id", projetId).getResultList();
    }

    public Document create(Document document) {
        // Plus besoin de em.getTransaction().begin(), @Transactional s'en occupe
        if (document.getDateDepot() == null) {
            document.setDateDepot(LocalDateTime.now());
        }

        em.persist(document);
        return document;
    }

    public void delete(Long id) {
        Document doc = em.find(Document.class, id);
        if (doc != null) {
            em.remove(doc);
        }
        // Pas besoin de commit manuel, WildFly valide la transaction à la fin de la méthode
    }
}