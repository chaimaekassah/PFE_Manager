package org.pfemanager.service;

import org.pfemanager.model.Document;
import org.pfemanager.model.User;
import org.pfemanager.model.Projet;

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
 * Service de gestion des documents avec données simulées
 */
@ApplicationScoped
public class DocumentService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjetService projetService;

    @Inject
    private UserService userService;

    private List<Document> documents;
    private Long nextId = 1L;

    @PostConstruct
    public void init() {
        documents = new ArrayList<>();

        // Récupération des données pour simuler
        Projet projet1 = projetService.findById(1L).orElse(null);
        Projet projet2 = projetService.findById(2L).orElse(null);
        User etudiant1 = userService.findById(2L).orElse(null);
        User etudiant2 = userService.findById(3L).orElse(null);

        if (projet1 != null && etudiant1 != null) {
            // Documents pour le projet 1
            Document doc1 = new Document(
                    nextId++,
                    "Cahier_des_charges.pdf",
                    "pdf",
                    etudiant1,
                    projet1
            );
            doc1.setTaille(524288L); // 512 KB
            doc1.setDateDepot(LocalDateTime.now().minusDays(20));
            doc1.setChemin("/uploads/projet1/cahier_des_charges.pdf");

            Document doc2 = new Document(
                    nextId++,
                    "Dataset_transactions.csv",
                    "csv",
                    etudiant1,
                    projet1
            );
            doc2.setTaille(2097152L); // 2 MB
            doc2.setDateDepot(LocalDateTime.now().minusDays(15));
            doc2.setChemin("/uploads/projet1/dataset.csv");

            Document doc3 = new Document(
                    nextId++,
                    "Rapport_intermediaire.docx",
                    "docx",
                    etudiant1,
                    projet1
            );
            doc3.setTaille(1048576L); // 1 MB
            doc3.setDateDepot(LocalDateTime.now().minusDays(5));
            doc3.setChemin("/uploads/projet1/rapport_intermediaire.docx");

            projet1.ajouterDocument(doc1);
            projet1.ajouterDocument(doc2);
            projet1.ajouterDocument(doc3);

            documents.add(doc1);
            documents.add(doc2);
            documents.add(doc3);
        }

        if (projet2 != null && etudiant2 != null) {
            // Documents pour le projet 2
            Document doc4 = new Document(
                    nextId++,
                    "Maquettes_UI.pdf",
                    "pdf",
                    etudiant2,
                    projet2
            );
            doc4.setTaille(3145728L); // 3 MB
            doc4.setDateDepot(LocalDateTime.now().minusDays(10));
            doc4.setChemin("/uploads/projet2/maquettes.pdf");

            projet2.ajouterDocument(doc4);
            documents.add(doc4);
        }
    }

    // Récupérer tous les documents
    public List<Document> getAll() {
        return new ArrayList<>(documents);
    }

    // Trouver par ID
    public Optional<Document> findById(Long id) {
        return documents.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
    }

    // Documents d'un projet
    public List<Document> getDocumentsByProjet(Long projetId) {
        return documents.stream()
                .filter(d -> d.getProjet().getId().equals(projetId))
                .collect(Collectors.toList());
    }

    // Documents déposés par un utilisateur
    public List<Document> getDocumentsByDepositaire(Long userId) {
        return documents.stream()
                .filter(d -> d.getDepositaire().getId().equals(userId))
                .collect(Collectors.toList());
    }

    // Créer/Ajouter un document
    public Document create(Document document) {
        document.setId(nextId++);
        document.setDateDepot(LocalDateTime.now());
        documents.add(document);

        // Ajouter aussi au projet
        if (document.getProjet() != null) {
            projetService.findById(document.getProjet().getId())
                    .ifPresent(p -> p.ajouterDocument(document));
        }

        return document;
    }

    // Supprimer un document
    public void delete(Long id) {
        findById(id).ifPresent(doc -> {
            documents.remove(doc);
            // Retirer aussi du projet
            if (doc.getProjet() != null) {
                projetService.findById(doc.getProjet().getId())
                        .ifPresent(p -> p.getDocuments().remove(doc));
            }
        });
    }
}