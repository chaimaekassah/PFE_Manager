package org.pfemanager.controller;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.pfemanager.model.Role;

@Named
@RequestScoped
public class AdminStatsBean {

    @PersistenceContext(unitName = "pfe_manager_pu")
    private EntityManager em;

    private long totalEtudiants;
    private long totalSujets;
    private long totalAffectations;

    @PostConstruct
    public void init() {
        totalEtudiants = em.createQuery(
                        "SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :r", Long.class)
                .setParameter("r", Role.ETUDIANT)
                .getSingleResult();

        totalSujets = em.createQuery(
                        "SELECT COUNT(s) FROM Sujet s", Long.class)
                .getSingleResult();

        // Sujets complets = affectés
        totalAffectations = em.createQuery(
                        "SELECT COUNT(s) FROM Sujet s WHERE s.statut = 'COMPLET'", Long.class)
                .getSingleResult();
    }

    public long getTotalEtudiants() { return totalEtudiants; }
    public long getTotalSujets() { return totalSujets; }
    public long getTotalAffectations() { return totalAffectations; }
}