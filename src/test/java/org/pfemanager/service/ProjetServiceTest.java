package org.pfemanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.Projet;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.model.Commentaire;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private ProjetService projetService;

    private Projet projet;

    @BeforeEach
    void setUp() {
        projet = new Projet();
        projet.setId(1L);
    }

    // 🔹 Test getAll()
    @Test
    void testGetAll() {
        TypedQuery<Projet> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Projet.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(projet));

        List<Projet> result = projetService.getAll();

        assertEquals(1, result.size());
        verify(em).createQuery(anyString(), eq(Projet.class));
    }

    // 🔹 Test findById()
    @Test
    void testFindById() {
        when(em.find(Projet.class, 1L)).thenReturn(projet);

        Optional<Projet> result = projetService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    // 🔹 Test create()
    @Test
    void testCreate() {
        Projet newProjet = new Projet();

        Projet result = projetService.create(newProjet);

        assertNotNull(result.getDateDebut());
        assertEquals(StatutProjet.EN_COURS, result.getStatut());
        verify(em).persist(newProjet);
    }

    // 🔹 Test update()
    @Test
    void testUpdate() {
        when(em.merge(projet)).thenReturn(projet);

        Projet result = projetService.update(projet);

        assertEquals(projet, result);
        verify(em).merge(projet);
    }

    // 🔹 Test changerStatut()
    @Test
    void testChangerStatut() {
        when(em.find(Projet.class, 1L)).thenReturn(projet);

        projetService.changerStatut(1L, StatutProjet.TERMINE);

        assertEquals(StatutProjet.TERMINE, projet.getStatut());
        assertNotNull(projet.getDateFin());
    }

    // 🔹 Test delete()
    @Test
    void testDelete() {
        when(em.find(Projet.class, 1L)).thenReturn(projet);

        projetService.delete(1L);

        verify(em).remove(projet);
    }

    // 🔹 Test ajouterCommentaire()
    @Test
    void testAjouterCommentaire() {
        Utilisateur user = new Utilisateur();
        user.setId(10L);

        when(em.find(Projet.class, 1L)).thenReturn(projet);

        projetService.ajouterCommentaire(1L, user, "Bon travail");

        verify(em).persist(any(Commentaire.class));
    }

    // 🔹 Test getCommentaires()
    @Test
    void testGetCommentaires() {
        TypedQuery<Commentaire> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Commentaire.class))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(1L))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Commentaire()));

        List<Commentaire> result = projetService.getCommentaires(1L);

        assertEquals(1, result.size());
        verify(query).setParameter("id", 1L);
    }
}