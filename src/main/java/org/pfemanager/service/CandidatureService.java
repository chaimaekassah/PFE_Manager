package org.pfemanager.service;

import org.pfemanager.model.Candidature;
import org.pfemanager.model.User;
import org.pfemanager.enums.StatutCandidature;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service de gestion des candidatures avec données simulées
 */
@ApplicationScoped
public class CandidatureService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    private List<Candidature> candidatures;
    private Long nextId = 1L;

    @PostConstruct
    public void init() {
        candidatures = new ArrayList<>();

        // Récupération des utilisateurs pour les données simulées
        User etudiant1 = userService.findById(2L).orElse(null); // Amal Benali
        User etudiant2 = userService.findById(3L).orElse(null); // Leila Chakir
        User encadrant1 = userService.findById(5L).orElse(null); // Samira Tazi
        User encadrant2 = userService.findById(6L).orElse(null); // Nadia Idrissi
        User encadrant3 = userService.findById(7L).orElse(null); // Khadija Zahiri

        // Données simulées
        Candidature c1 = new Candidature(
                nextId++,
                etudiant1,
                encadrant1,
                "Application mobile de gestion des stocks",
                "Je suis très intéressée par ce sujet car j'ai une expérience en développement mobile."
        );
        c1.setStatut(StatutCandidature.EN_ATTENTE);
        c1.setDateCandidature(LocalDateTime.now().minusDays(3));

        Candidature c2 = new Candidature(
                nextId++,
                etudiant1,
                encadrant2,
                "Système de détection de fraude par Machine Learning",
                "Le Machine Learning me passionne et je souhaite approfondir mes connaissances."
        );
        c2.setStatut(StatutCandidature.ACCEPTEE);
        c2.setDateCandidature(LocalDateTime.now().minusDays(10));
        c2.setDateReponse(LocalDateTime.now().minusDays(7));
        c2.setRemarqueEncadrant("Excellent profil, motivée et compétente.");

        Candidature c3 = new Candidature(
                nextId++,
                etudiant1,
                encadrant3,
                "Plateforme e-commerce avec recommandations IA",
                "Je trouve ce projet très intéressant pour combiner développement web et IA."
        );
        c3.setStatut(StatutCandidature.REFUSEE);
        c3.setDateCandidature(LocalDateTime.now().minusDays(15));
        c3.setDateReponse(LocalDateTime.now().minusDays(12));
        c3.setRemarqueEncadrant("Profil intéressant mais projet déjà attribué.");

        Candidature c4 = new Candidature(
                nextId++,
                etudiant2,
                encadrant1,
                "Application mobile de gestion des stocks",
                "J'ai réalisé plusieurs projets mobiles et je maîtrise Flutter."
        );
        c4.setStatut(StatutCandidature.EN_ATTENTE);
        c4.setDateCandidature(LocalDateTime.now().minusDays(2));

        Candidature c5 = new Candidature(
                nextId++,
                etudiant2,
                encadrant2,
                "Chatbot intelligent pour service client",
                "Passionnée par le NLP et les chatbots."
        );
        c5.setStatut(StatutCandidature.EN_ATTENTE);
        c5.setDateCandidature(LocalDateTime.now().minusDays(1));

        candidatures.add(c1);
        candidatures.add(c2);
        candidatures.add(c3);
        candidatures.add(c4);
        candidatures.add(c5);
    }

    // Récupérer toutes les candidatures
    public List<Candidature> getAll() {
        return new ArrayList<>(candidatures);
    }

    // Trouver par ID
    public Optional<Candidature> findById(Long id) {
        return candidatures.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    // Candidatures d'un étudiant
    public List<Candidature> getCandidaturesByEtudiant(Long etudiantId) {
        return candidatures.stream()
                .filter(c -> c.getEtudiant().getId().equals(etudiantId))
                .collect(Collectors.toList());
    }

    // Candidatures reçues par un encadrant
    public List<Candidature> getCandidaturesByEncadrant(Long encadrantId) {
        return candidatures.stream()
                .filter(c -> c.getEncadrant().getId().equals(encadrantId))
                .collect(Collectors.toList());
    }

    // Statistiques étudiant
    public long countByEtudiantAndStatut(Long etudiantId, StatutCandidature statut) {
        return candidatures.stream()
                .filter(c -> c.getEtudiant().getId().equals(etudiantId))
                .filter(c -> c.getStatut() == statut)
                .count();
    }

    // Créer une candidature
    public Candidature create(Candidature candidature) {
        candidature.setId(nextId++);
        candidature.setDateCandidature(LocalDateTime.now());
        candidature.setStatut(StatutCandidature.EN_ATTENTE);
        candidatures.add(candidature);
        return candidature;
    }

    // Accepter une candidature
    public void accepter(Long id, String remarque) {
        findById(id).ifPresent(c -> {
            c.setStatut(StatutCandidature.ACCEPTEE);
            c.setDateReponse(LocalDateTime.now());
            c.setRemarqueEncadrant(remarque);
        });
    }

    // Refuser une candidature
    public void refuser(Long id, String remarque) {
        findById(id).ifPresent(c -> {
            c.setStatut(StatutCandidature.REFUSEE);
            c.setDateReponse(LocalDateTime.now());
            c.setRemarqueEncadrant(remarque);
        });
    }

    // Retirer une candidature (par l'étudiant)
    public void retirer(Long id) {
        findById(id).ifPresent(c -> {
            if (c.getStatut() == StatutCandidature.EN_ATTENTE) {
                c.setStatut(StatutCandidature.RETIREE);
            }
        });
    }

    // Supprimer une candidature
    public void delete(Long id) {
        candidatures.removeIf(c -> c.getId().equals(id));
    }
}