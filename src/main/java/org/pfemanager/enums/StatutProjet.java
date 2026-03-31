package org.pfemanager.enums;

/**
 * Énumération des statuts de projet
 */
public enum StatutProjet {
    EN_COURS("En cours"),
    VALIDE("Validé"),
    TERMINE("Terminé");

    private final String libelle;

    StatutProjet(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}