package org.pfemanager.service;

import org.pfemanager.model.User;
import org.pfemanager.enums.Role;
import org.pfemanager.enums.StatutUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service de gestion des utilisateurs avec données simulées en mémoire
 */
@ApplicationScoped
public class UserService implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<User> users;
    private Long nextId = 1L;

    @PostConstruct
    public void init() {
        users = new ArrayList<>();

        // Données simulées
        User admin = new User(nextId++, "Alami", "Fatima", "fatima.alami@uca.ma", Role.ADMIN);
        admin.setStatut(StatutUser.ACTIF);

        User etudiant1 = new User(nextId++, "Benali", "Amal", "amal.benali@etu.uca.ma", Role.ETUDIANT);
        etudiant1.setStatut(StatutUser.ACTIF);

        User etudiant2 = new User(nextId++, "Chakir", "Leila", "leila.chakir@etu.uca.ma", Role.ETUDIANT);
        etudiant2.setStatut(StatutUser.ACTIF);

        User etudiant3 = new User(nextId++, "Mansouri", "Safaa", "safaa.mansouri@etu.uca.ma", Role.ETUDIANT);
        etudiant3.setStatut(StatutUser.INACTIF);

        User encadrant1 = new User(nextId++, "Tazi", "Samira", "samira.tazi@uca.ma", Role.ENCADRANT);
        encadrant1.setStatut(StatutUser.ACTIF);

        User encadrant2 = new User(nextId++, "Idrissi", "Nadia", "nadia.idrissi@uca.ma", Role.ENCADRANT);
        encadrant2.setStatut(StatutUser.ACTIF);

        User encadrant3 = new User(nextId++, "Zahiri", "Khadija", "khadija.zahiri@uca.ma", Role.ENCADRANT);
        encadrant3.setStatut(StatutUser.ACTIF);

        users.add(admin);
        users.add(etudiant1);
        users.add(etudiant2);
        users.add(etudiant3);
        users.add(encadrant1);
        users.add(encadrant2);
        users.add(encadrant3);
    }

    // Récupérer tous les utilisateurs non supprimés
    public List<User> getAllUsers() {
        return users.stream()
                .filter(u -> u.getStatut() != StatutUser.SUPPRIME)
                .collect(Collectors.toList());
    }

    // Récupérer tous les utilisateurs (même supprimés)
    public List<User> getAllUsersIncludingDeleted() {
        return new ArrayList<>(users);
    }

    // Trouver par ID
    public Optional<User> findById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    // Trouver par email
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // Récupérer les étudiants
    public List<User> getEtudiants() {
        return users.stream()
                .filter(u -> u.getRole() == Role.ETUDIANT)
                .filter(u -> u.getStatut() != StatutUser.SUPPRIME)
                .collect(Collectors.toList());
    }

    // Récupérer les encadrants
    public List<User> getEncadrants() {
        return users.stream()
                .filter(u -> u.getRole() == Role.ENCADRANT)
                .filter(u -> u.getStatut() != StatutUser.SUPPRIME)
                .collect(Collectors.toList());
    }

    // Créer un utilisateur
    public User create(User user) {
        user.setId(nextId++);
        users.add(user);
        return user;
    }

    // Mettre à jour un utilisateur
    public User update(User user) {
        Optional<User> existing = findById(user.getId());
        if (existing.isPresent()) {
            User u = existing.get();
            u.setNom(user.getNom());
            u.setPrenom(user.getPrenom());
            u.setEmail(user.getEmail());
            u.setRole(user.getRole());
            u.setStatut(user.getStatut());
            return u;
        }
        return null;
    }

    // Supprimer logiquement
    public void delete(Long id) {
        findById(id).ifPresent(u -> u.setStatut(StatutUser.SUPPRIME));
    }

    // Activer/Désactiver
    public void toggleStatut(Long id) {
        findById(id).ifPresent(u -> {
            if (u.getStatut() == StatutUser.ACTIF) {
                u.setStatut(StatutUser.INACTIF);
            } else if (u.getStatut() == StatutUser.INACTIF) {
                u.setStatut(StatutUser.ACTIF);
            }
        });
    }

    // Rechercher
    public List<User> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }

        String kw = keyword.toLowerCase();
        return users.stream()
                .filter(u -> u.getStatut() != StatutUser.SUPPRIME)
                .filter(u ->
                        u.getNom().toLowerCase().contains(kw) ||
                                u.getPrenom().toLowerCase().contains(kw) ||
                                u.getEmail().toLowerCase().contains(kw)
                )
                .collect(Collectors.toList());
    }
}