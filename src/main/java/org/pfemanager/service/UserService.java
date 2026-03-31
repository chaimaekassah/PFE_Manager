package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.pfemanager.enums.Role;
import org.pfemanager.enums.StatutUser;
import org.pfemanager.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService implements Serializable {

    private static final long serialVersionUID = 1L;

    // L'EntityManager est injecté par WildFly.
    // "unitName" doit correspondre exactement au nom dans ton persistence.xml
    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    /**
     * Récupère tous les utilisateurs non supprimés
     */
    public List<User> getAllUsers() {
        return em.createQuery(
                        "SELECT u FROM User u WHERE u.statut <> :statutSupprime ORDER BY u.id",
                        User.class)
                .setParameter("statutSupprime", StatutUser.SUPPRIME)
                .getResultList();
    }

    /**
     * Récupère tous les utilisateurs, y compris ceux marqués comme supprimés
     */
    public List<User> getAllUsersIncludingDeleted() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.id", User.class)
                .getResultList();
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();

        return em.createQuery(
                        "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)",
                        User.class)
                .setParameter("email", email.trim())
                .getResultStream()
                .findFirst();
    }

    public List<User> getEtudiants() {
        return em.createQuery(
                        "SELECT u FROM User u WHERE u.role = :role AND u.statut <> :statutSupprime ORDER BY u.id",
                        User.class)
                .setParameter("role", Role.ETUDIANT)
                .setParameter("statutSupprime", StatutUser.SUPPRIME)
                .getResultList();
    }

    public List<User> getEncadrants() {
        return em.createQuery(
                        "SELECT u FROM User u WHERE u.role = :role AND u.statut <> :statutSupprime ORDER BY u.id",
                        User.class)
                .setParameter("role", Role.ENCADRANT)
                .setParameter("statutSupprime", StatutUser.SUPPRIME)
                .getResultList();
    }

    @Transactional
    public User create(User user) {
        em.persist(user);
        return user;
    }

    @Transactional
    public User update(User user) {
        return em.merge(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            user.setStatut(StatutUser.SUPPRIME);
            em.merge(user);
        }
    }

    @Transactional
    public void toggleStatut(Long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            if (user.getStatut() == StatutUser.ACTIF) {
                user.setStatut(StatutUser.INACTIF);
            } else {
                user.setStatut(StatutUser.ACTIF);
            }
            em.merge(user);
        }
    }

    public List<User> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }

        String kw = "%" + keyword.toLowerCase().trim() + "%";

        return em.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.statut <> :statutSupprime " +
                                "AND (" +
                                "LOWER(u.nom) LIKE :kw OR " +
                                "LOWER(COALESCE(u.prenom, '')) LIKE :kw OR " +
                                "LOWER(u.email) LIKE :kw" +
                                ") ORDER BY u.id",
                        User.class)
                .setParameter("statutSupprime", StatutUser.SUPPRIME)
                .setParameter("kw", kw)
                .getResultList();
    }
}