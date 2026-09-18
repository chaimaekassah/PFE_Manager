package org.pfemanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.dao.ResetTokenDAO;
import org.pfemanager.dao.UtilisateurDAO;
import org.pfemanager.model.ResetToken;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock private ResetTokenDAO resetTokenDAO;
    @Mock private UtilisateurDAO utilisateurDAO;
    @Mock private EmailService emailService;

    @InjectMocks private PasswordResetService passwordResetService;

    private MockedStatic<PasswordUtil> mockedPasswordUtil;

    @BeforeEach
    void setUp() {
        mockedPasswordUtil = mockStatic(PasswordUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedPasswordUtil.close();
    }

    // ===================== ÉTAPE 1 : DEMANDE =====================

    @Test
    void testDemanderReinitialisation_UserExists() {
        // Given
        String email = "chaimae@etu.ma";
        Utilisateur user = new Utilisateur();
        user.setEmail(email);

        when(utilisateurDAO.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        boolean result = passwordResetService.demanderReinitialisation(email, "http://localhost");

        // Then
        assertTrue(result);
        verify(resetTokenDAO).deleteByEmail(email); // Vérifie qu'on nettoie les anciens tokens
        verify(resetTokenDAO).save(any(ResetToken.class)); // Vérifie qu'on en crée un nouveau
        verify(emailService).envoyerLienReinitialisation(eq(email), contains("token=")); // Vérifie l'envoi d'email
    }

    @Test
    void testDemanderReinitialisation_UserNotFound() {
        // Given
        String email = "inconnu@test.com";
        when(utilisateurDAO.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = passwordResetService.demanderReinitialisation(email, "http://localhost");

        // Then
        assertTrue(result); // Doit rester true pour la sécurité
        verifyNoInteractions(resetTokenDAO);
        verifyNoInteractions(emailService);
    }

    // ===================== ÉTAPE 2 : VALIDATION =====================

    @Test
    void testTokenValide_Success() {
        // Given
        ResetToken token = new ResetToken();
        token.setUtilise(false);
        token.setDateExpiration(LocalDateTime.now().plusMinutes(10));
        when(resetTokenDAO.findByToken("token-ok")).thenReturn(Optional.of(token));

        // When & Then
        assertTrue(passwordResetService.tokenValide("token-ok"));
    }

    @Test
    void testTokenValide_Expired() {
        // Given
        ResetToken token = new ResetToken();
        token.setUtilise(false);
        token.setDateExpiration(LocalDateTime.now().minusMinutes(1)); // Expiré
        when(resetTokenDAO.findByToken("token-ko")).thenReturn(Optional.of(token));

        // When & Then
        assertFalse(passwordResetService.tokenValide("token-ko"));
    }

    // ===================== ÉTAPE 3 : FINALISATION =====================

    @Test
    void testReinitialiserMotDePasse_Success() {
        // Given
        String tokenStr = "valid-uuid";
        ResetToken token = new ResetToken();
        token.setEmail("chaimae@etu.ma");
        token.setUtilise(false);
        token.setDateExpiration(LocalDateTime.now().plusMinutes(10));

        Utilisateur user = new Utilisateur();
        user.setEmail("chaimae@etu.ma");

        when(resetTokenDAO.findByToken(tokenStr)).thenReturn(Optional.of(token));
        when(utilisateurDAO.findByEmail("chaimae@etu.ma")).thenReturn(Optional.of(user));
        mockedPasswordUtil.when(() -> PasswordUtil.hasher("newPass")).thenReturn("hash123");

        // When
        boolean success = passwordResetService.reinitialiserMotDePasse(tokenStr, "newPass");

        // Then
        assertTrue(success);
        assertTrue(token.isUtilise());
        assertEquals("hash123", user.getMotDePasse());
        verify(utilisateurDAO).update(user);
        verify(resetTokenDAO).update(token);
    }
}