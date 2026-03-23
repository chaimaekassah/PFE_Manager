package org.pfemanager.dao;

import org.pfemanager.model.Utilisateur;
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
    public void save(Utilisateur utilisateur) {
        em.persist(utilisateur);
    }

    /**
     * Pour la connexion (Sign In)
     */
    public Optional<Utilisateur> findByEmail(String email) {
        return em.createQuery("SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    /**
     * Pour la modification du mot de passe
     */
    @Transactional
    public void update(Utilisateur utilisateur) {
        em.merge(utilisateur);
    }
}