package org.pfemanager.service;

import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.Commentaire;
import org.pfemanager.model.Projet;
import org.pfemanager.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des projets connecté à la base de données
 */
@ApplicationScoped
public class ProjetService implements Serializable {

    private static final long serialVersionUID = 1L;

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("default");

    private EntityManager em() {
        return emf.createEntityManager();
    }

    // Récupérer tous les projets
    public List<Projet> getAll() {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT p FROM Projet p ORDER BY p.id",
                    Projet.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver par ID
    public Optional<Projet> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Projet.class, id));
        } finally {
            em.close();
        }
    }

    // Projet d'un étudiant
    public Optional<Projet> getProjetByEtudiant(Long etudiantId) {
        EntityManager em = em();
        try {
            List<Projet> result = em.createQuery(
                            "SELECT p FROM Projet p WHERE p.etudiant.id = :id ORDER BY p.id",
                            Projet.class
                    )
                    .setParameter("id", etudiantId)
                    .getResultList();

            return result.stream().findFirst();
        } finally {
            em.close();
        }
    }

    // Projets encadrés par un encadrant
    public List<Projet> getProjetsByEncadrant(Long encadrantId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT p FROM Projet p WHERE p.encadrant.id = :id ORDER BY p.id",
                            Projet.class
                    )
                    .setParameter("id", encadrantId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Créer un projet
    public Projet create(Projet projet) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            if (projet.getDateDebut() == null) {
                projet.setDateDebut(LocalDateTime.now());
            }
            if (projet.getStatut() == null) {
                projet.setStatut(StatutProjet.EN_COURS);
            }

            em.persist(projet);
            em.getTransaction().commit();

            return projet;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Mettre à jour un projet
    public Projet update(Projet projet) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Projet updatedProjet = em.merge(projet);
            em.getTransaction().commit();
            return updatedProjet;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Changer le statut
    public void changerStatut(Long id, StatutProjet statut) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            Projet projet = em.find(Projet.class, id);
            if (projet != null) {
                projet.setStatut(statut);
                if (statut == StatutProjet.TERMINE) {
                    projet.setDateFin(LocalDateTime.now());
                }
                em.merge(projet);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Supprimer un projet
    public void delete(Long id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            Projet projet = em.find(Projet.class, id);
            if (projet != null) {
                em.remove(projet);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Ajouter un commentaire
    public void ajouterCommentaire(Long projetId, User auteur, String contenu) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            Projet projet = em.find(Projet.class, projetId);
            if (projet != null && auteur != null && contenu != null && !contenu.trim().isEmpty()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setAuteur(auteur);
                commentaire.setContenu(contenu);
                commentaire.setProjet(projet);
                commentaire.setDateCreation(LocalDateTime.now());

                em.persist(commentaire);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Récupérer les commentaires d'un projet
    public List<Commentaire> getCommentaires(Long projetId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT c FROM Commentaire c WHERE c.projet.id = :id ORDER BY c.dateCreation DESC",
                            Commentaire.class
                    )
                    .setParameter("id", projetId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}