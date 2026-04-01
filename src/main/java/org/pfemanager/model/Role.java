package org.pfemanager.model;

public enum Role {
    ADMINISTRATEUR("Administrateur/trice"),
    ENCADRANT("Encadrant(e)"),
    ETUDIANT("Étudiant(e)");

    private final String libelle;

    Role(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}