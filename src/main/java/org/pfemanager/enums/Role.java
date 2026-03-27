package org.pfemanager.enums;

/**
 * Énumération des rôles utilisateur
 */
public enum Role {
    ETUDIANT("Étudiant(e)"),
    ENCADRANT("Encadrant(e)"),
    ADMIN("Administrateur/trice");

    private final String libelle;

    Role(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}