package org.pfemanager.enums;

/**
 * Énumération des statuts utilisateur
 */
public enum StatutUser {
    ACTIF("Actif"),
    INACTIF("Inactif"),
    SUPPRIME("Supprimé");

    private final String libelle;

    StatutUser(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}