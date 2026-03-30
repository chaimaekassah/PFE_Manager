package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.pfemanager.enums.Role;
import org.pfemanager.enums.StatutUser;
import org.pfemanager.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService implements Serializable {

    private static final long serialVersionUID = 1L;

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<User> getAllUsers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.statut <> :statutSupprime ORDER BY u.id",
                            User.class
                    )
                    .setParameter("statutSupprime", StatutUser.SUPPRIME)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<User> getAllUsersIncludingDeleted() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u ORDER BY u.id", User.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<User> findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(User.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<User> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            List<User> result = em.createQuery(
                            "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)",
                            User.class
                    )
                    .setParameter("email", email)
                    .getResultList();

            return result.stream().findFirst();
        } finally {
            em.close();
        }
    }

    public List<User> getEtudiants() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.role = :role AND u.statut <> :statutSupprime ORDER BY u.id",
                            User.class
                    )
                    .setParameter("role", Role.ETUDIANT)
                    .setParameter("statutSupprime", StatutUser.SUPPRIME)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<User> getEncadrants() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.role = :role AND u.statut <> :statutSupprime ORDER BY u.id",
                            User.class
                    )
                    .setParameter("role", Role.ENCADRANT)
                    .setParameter("statutSupprime", StatutUser.SUPPRIME)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public User create(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public User update(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User updatedUser = em.merge(user);
            em.getTransaction().commit();
            return updatedUser;
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
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                user.setStatut(StatutUser.SUPPRIME);
                em.merge(user);
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

    public void toggleStatut(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                if (user.getStatut() == StatutUser.ACTIF) {
                    user.setStatut(StatutUser.INACTIF);
                } else if (user.getStatut() == StatutUser.INACTIF) {
                    user.setStatut(StatutUser.ACTIF);
                }
                em.merge(user);
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

    public List<User> search(String keyword) {
        EntityManager em = getEntityManager();
        try {
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
                            User.class
                    )
                    .setParameter("statutSupprime", StatutUser.SUPPRIME)
                    .setParameter("kw", kw)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}