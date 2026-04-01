package org.pfemanager.dao;

import org.pfemanager.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UtilisateurDAO {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    /**
     * Pour l'inscription (Sign Up)
     */
    @Transactional
    public void save(User utilisateur) {
        em.persist(utilisateur);
    }

    /**
     * Pour la connexion (Sign In)
     */
    public Optional<User> findByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    /**
     * Pour la modification du mot de passe
     */
    @Transactional
    public void update(User utilisateur) {
        em.merge(utilisateur);
    }
}