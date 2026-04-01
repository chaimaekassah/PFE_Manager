package org.pfemanager.service;

import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.Commentaire;
import org.pfemanager.model.Projet;
import org.pfemanager.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional // WildFly gère les transactions automatiquement
public class ProjetService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    public List<Projet> getAll() {
        return em.createQuery("SELECT p FROM Projet p ORDER BY p.id", Projet.class)
                .getResultList();
    }

    public Optional<Projet> findById(Long id) {
        return Optional.ofNullable(em.find(Projet.class, id));
    }

    public Optional<Projet> getProjetByEtudiant(Long etudiantId) {
        List<Projet> result = em.createQuery(
                        "SELECT p FROM Projet p WHERE p.etudiant.id = :id ORDER BY p.id", Projet.class)
                .setParameter("id", etudiantId)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<Projet> getProjetsByEncadrant(Long encadrantId) {
        return em.createQuery(
                        "SELECT p FROM Projet p WHERE p.encadrant.id = :id ORDER BY p.id", Projet.class)
                .setParameter("id", encadrantId)
                .getResultList();
    }

    public Projet create(Projet projet) {
        if (projet.getDateDebut() == null) projet.setDateDebut(LocalDateTime.now());
        if (projet.getStatut() == null) projet.setStatut(StatutProjet.EN_COURS);
        em.persist(projet);
        return projet;
    }

    public Projet update(Projet projet) {
        return em.merge(projet);
    }

    public void changerStatut(Long id, StatutProjet statut) {
        Projet projet = em.find(Projet.class, id);
        if (projet != null) {
            projet.setStatut(statut);
            if (statut == StatutProjet.TERMINE) {
                projet.setDateFin(LocalDateTime.now());
            }
        }
    }

    public void delete(Long id) {
        Projet projet = em.find(Projet.class, id);
        if (projet != null) em.remove(projet);
    }

    public void ajouterCommentaire(Long projetId, User auteur, String contenu) {
        Projet projet = em.find(Projet.class, projetId);
        if (projet != null && auteur != null && contenu != null && !contenu.trim().isEmpty()) {
            Commentaire commentaire = new Commentaire();
            commentaire.setAuteur(auteur);
            commentaire.setContenu(contenu);
            commentaire.setProjet(projet);
            commentaire.setDateCreation(LocalDateTime.now());
            em.persist(commentaire);
        }
    }

    public List<Commentaire> getCommentaires(Long projetId) {
        return em.createQuery(
                        "SELECT c FROM Commentaire c WHERE c.projet.id = :id ORDER BY c.dateCreation DESC",
                        Commentaire.class)
                .setParameter("id", projetId)
                .getResultList();
    }
}