package org.pfemanager.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.pfemanager.model.ResetToken;

import java.util.Optional;

@ApplicationScoped
public class ResetTokenDAO {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    @Transactional
    public void save(ResetToken token) {
        em.persist(token);
    }

    public Optional<ResetToken> findByToken(String token) {
        return em.createQuery(
                        "SELECT t FROM ResetToken t WHERE t.token = :token", ResetToken.class)
                .setParameter("token", token)
                .getResultStream()
                .findFirst();
    }

    @Transactional
    public void update(ResetToken token) {
        em.merge(token);
    }

    // Supprime les anciens tokens de cet email
    @Transactional
    public void deleteByEmail(String email) {
        em.createQuery("DELETE FROM ResetToken t WHERE t.email = :email")
                .setParameter("email", email)
                .executeUpdate();
    }
}