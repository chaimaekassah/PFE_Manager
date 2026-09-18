package org.pfemanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.pfemanager.model.Sujet;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SujetServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private SujetService sujetService;

    private Sujet sujet;

    @BeforeEach
    void setUp() {
        sujet = new Sujet();
        sujet.setId(1L);
        sujet.setTitre("IA Project");
    }

    // 🔹 Test findAll()
    @Test
    void testFindAll() {
        TypedQuery<Sujet> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Sujet.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(sujet));

        List<Sujet> result = sujetService.findAll();

        assertEquals(1, result.size());
        verify(em).createQuery(anyString(), eq(Sujet.class));
    }

    // 🔹 Test findById()
    @Test
    void testFindById() {
        when(em.find(Sujet.class, 1L)).thenReturn(sujet);

        Sujet result = sujetService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // 🔹 Test save() -> persist
    @Test
    void testSavePersist() {
        Sujet newSujet = new Sujet(); // id = null

        sujetService.save(newSujet);

        verify(em).persist(newSujet);
        verify(em, never()).merge(any());
    }

    // 🔹 Test save() -> merge
    @Test
    void testSaveMerge() {
        sujetService.save(sujet);

        verify(em).merge(sujet);
        verify(em, never()).persist(any());
    }

    // 🔹 Test delete()
    @Test
    void testDelete() {
        when(em.find(Sujet.class, 1L)).thenReturn(sujet);

        sujetService.delete(1L);

        verify(em).remove(sujet);
    }

    // 🔹 Test delete() si null
    @Test
    void testDeleteNotFound() {
        when(em.find(Sujet.class, 1L)).thenReturn(null);

        sujetService.delete(1L);

        verify(em, never()).remove(any());
    }

    // 🔹 Test search()
    @Test
    void testSearch() {
        TypedQuery<Sujet> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Sujet.class))).thenReturn(query);
        when(query.setParameter(eq("q"), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(sujet));

        List<Sujet> result = sujetService.search("IA");

        assertEquals(1, result.size());
        verify(query).setParameter("q", "%IA%");
    }

    // 🔹 Test getSujetsDisponibles()
    @Test
    void testGetSujetsDisponibles() {
        TypedQuery<Sujet> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Sujet.class))).thenReturn(query);
        when(query.setParameter(eq("statut"), eq(Sujet.StatutSujet.DISPONIBLE)))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(sujet));

        List<Sujet> result = sujetService.getSujetsDisponibles();

        assertEquals(1, result.size());
        verify(query).setParameter("statut", Sujet.StatutSujet.DISPONIBLE);
    }
}