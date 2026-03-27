package org.pfemanager.service;

import org.pfemanager.model.Projet;
import org.pfemanager.model.User;
import org.pfemanager.model.Commentaire;
import org.pfemanager.enums.StatutProjet;

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
 * Service de gestion des projets avec données simulées
 */
@ApplicationScoped
public class ProjetService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    private List<Projet> projets;
    private Long nextId = 1L;
    private Long nextCommentaireId = 1L;

    @PostConstruct
    public void init() {
        projets = new ArrayList<>();

        // Récupération des utilisateurs
        User etudiant1 = userService.findById(2L).orElse(null); // Amal Benali
        User etudiant2 = userService.findById(3L).orElse(null); // Leila Chakir
        User encadrant1 = userService.findById(5L).orElse(null); // Samira Tazi
        User encadrant2 = userService.findById(6L).orElse(null); // Nadia Idrissi

        // Projet 1 : En cours
        Projet p1 = new Projet(
                nextId++,
                "Système de détection de fraude par Machine Learning",
                "Développer un système intelligent capable de détecter les transactions frauduleuses en temps réel.",
                etudiant1,
                encadrant2
        );
        p1.setStatut(StatutProjet.EN_COURS);
        p1.setDateDebut(LocalDateTime.now().minusMonths(2));

        // Ajout de commentaires
        Commentaire com1 = new Commentaire(
                nextCommentaireId++,
                encadrant2,
                "Bon début de projet. Pensez à bien documenter vos algorithmes.",
                p1
        );
        com1.setDateCreation(LocalDateTime.now().minusDays(15));

        Commentaire com2 = new Commentaire(
                nextCommentaireId++,
                etudiant1,
                "Merci ! J'ai terminé la phase de collecte des données.",
                p1
        );
        com2.setDateCreation(LocalDateTime.now().minusDays(10));

        Commentaire com3 = new Commentaire(
                nextCommentaireId++,
                encadrant2,
                "Excellent ! Passons maintenant à la phase de modélisation.",
                p1
        );
        com3.setDateCreation(LocalDateTime.now().minusDays(5));

        p1.ajouterCommentaire(com1);
        p1.ajouterCommentaire(com2);
        p1.ajouterCommentaire(com3);

        // Projet 2 : En cours
        Projet p2 = new Projet(
                nextId++,
                "Application mobile e-commerce",
                "Créer une application mobile complète pour un commerce en ligne.",
                etudiant2,
                encadrant1
        );
        p2.setStatut(StatutProjet.EN_COURS);
        p2.setDateDebut(LocalDateTime.now().minusMonths(1));

        Commentaire com4 = new Commentaire(
                nextCommentaireId++,
                encadrant1,
                "Le design de l'interface est très réussi. Continuez !",
                p2
        );
        com4.setDateCreation(LocalDateTime.now().minusDays(7));

        p2.ajouterCommentaire(com4);

        projets.add(p1);
        projets.add(p2);
    }

    // Récupérer tous les projets
    public List<Projet> getAll() {
        return new ArrayList<>(projets);
    }

    // Trouver par ID
    public Optional<Projet> findById(Long id) {
        return projets.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    // Projet d'un étudiant
    public Optional<Projet> getProjetByEtudiant(Long etudiantId) {
        return projets.stream()
                .filter(p -> p.getEtudiant().getId().equals(etudiantId))
                .findFirst();
    }

    // Projets encadrés par un encadrant
    public List<Projet> getProjetsByEncadrant(Long encadrantId) {
        return projets.stream()
                .filter(p -> p.getEncadrant().getId().equals(encadrantId))
                .collect(Collectors.toList());
    }

    // Créer un projet
    public Projet create(Projet projet) {
        projet.setId(nextId++);
        projet.setDateDebut(LocalDateTime.now());
        projet.setStatut(StatutProjet.EN_COURS);
        projets.add(projet);
        return projet;
    }

    // Changer le statut
    public void changerStatut(Long id, StatutProjet statut) {
        findById(id).ifPresent(p -> {
            p.setStatut(statut);
            if (statut == StatutProjet.TERMINE) {
                p.setDateFin(LocalDateTime.now());
            }
        });
    }

    // Ajouter un commentaire
    public void ajouterCommentaire(Long projetId, User auteur, String contenu) {
        findById(projetId).ifPresent(p -> {
            Commentaire commentaire = new Commentaire(
                    nextCommentaireId++,
                    auteur,
                    contenu,
                    p
            );
            p.ajouterCommentaire(commentaire);
        });
    }

    // Récupérer les commentaires d'un projet
    public List<Commentaire> getCommentaires(Long projetId) {
        return findById(projetId)
                .map(Projet::getCommentaires)
                .orElse(new ArrayList<>());
    }
}