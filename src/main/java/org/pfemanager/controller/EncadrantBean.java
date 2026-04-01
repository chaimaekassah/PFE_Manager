package org.pfemanager.controller;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Named
@RequestScoped
public class EncadrantBean {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    @Inject
    private AuthBean authBean;

    private long mesSujetsCount;
    private long attenteCount;
    private long validesCount;

    @PostConstruct
    public void init() {
        Long encadrantId = authBean.getUtilisateurConnecte().getId();

        mesSujetsCount = em.createQuery(
                        "SELECT COUNT(s) FROM Sujet s WHERE s.encadrant.id = :id", Long.class)
                .setParameter("id", encadrantId)
                .getSingleResult();

        // Sujets complets de cet encadrant
        validesCount = em.createQuery(
                        "SELECT COUNT(s) FROM Sujet s WHERE s.encadrant.id = :id " +
                                "AND s.statut = 'COMPLET'", Long.class)
                .setParameter("id", encadrantId)
                .getSingleResult();

        // attenteCount sera géré par le binôme (candidatures)
        // Pour l'instant on met 0
        attenteCount = 0;
    }

    public long getMesSujetsCount() { return mesSujetsCount; }
    public long getAttenteCount() { return attenteCount; }
    public long getValidesCount() { return validesCount; }
}