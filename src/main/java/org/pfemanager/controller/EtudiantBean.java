package org.pfemanager.controller;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.pfemanager.model.Sujet;

@Named
@RequestScoped
public class EtudiantBean {

    @Inject
    private AuthBean authBean;

    // Pour l'instant pas d'affectation implémentée (c'est le binôme)
    // On expose juste les propriétés nécessaires au dashboard
    private boolean affecte = false;
    private Sujet sujetAffecte = null;

    @PostConstruct
    public void init() {
        // Le binôme implémentera la logique d'affectation
        // Pour l'instant : pas affecté par défaut
        affecte = false;
        sujetAffecte = null;
    }

    public boolean isAffecte() { return affecte; }
    public Sujet getSujetAffecte() { return sujetAffecte; }
}