package org.pfemanager.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.model.Utilisateur;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Utilisateur> query;

    @InjectMocks
    private UtilisateurDAO utilisateurDAO;

    @Test
    void testSave() {
        // Given
        Utilisateur user = new Utilisateur();
        user.setEmail("test@test.com");

        // When
        utilisateurDAO.save(user);

        // Then
        verify(em).persist(user); // Vérifie que JPA a bien reçu l'ordre d'enregistrer
    }

    @Test
    void testFindByEmail_Found() {
        // Given
        String email = "chaimae@etu.ma";
        Utilisateur expectedUser = new Utilisateur();
        expectedUser.setEmail(email);

        // Simulation de la chaîne : em.createQuery -> setParameter -> getResultStream
        when(em.createQuery(anyString(), eq(Utilisateur.class))).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(expectedUser));

        // When
        Optional<Utilisateur> result = utilisateurDAO.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(em).createQuery(contains("SELECT u FROM Utilisateur u WHERE u.email = :email"), eq(Utilisateur.class));
    }

    @Test
    void testFindByEmail_NotFound() {
        // Given
        String email = "inconnu@test.com";
        when(em.createQuery(anyString(), eq(Utilisateur.class))).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        // When
        Optional<Utilisateur> result = utilisateurDAO.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdate() {
        // Given
        Utilisateur user = new Utilisateur();
        user.setId(1L);
        user.setNom("Modifié");

        // When
        utilisateurDAO.update(user);

        // Then
        verify(em).merge(user); // Vérifie que JPA a bien reçu l'ordre de mise à jour
    }
}