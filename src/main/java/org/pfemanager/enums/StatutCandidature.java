package org.pfemanager.enums;

/**
 * Énumération des statuts de candidature
 */
public enum StatutCandidature {
    EN_ATTENTE("En attente"),
    ACCEPTEE("Acceptée"),
    REFUSEE("Refusée"),
    RETIREE("Retirée");

    private final String libelle;

    StatutCandidature(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}