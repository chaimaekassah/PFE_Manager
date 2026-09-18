package org.pfemanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.dao.UtilisateurDAO;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.util.PasswordUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UtilisateurDAO utilisateurDAO;

    @InjectMocks
    private AuthService authService;

    private MockedStatic<PasswordUtil> mockedPasswordUtil;

    @BeforeEach
    void setUp() {
        // Comme PasswordUtil utilise des méthodes statiques (BCrypt),
        // on le mock de façon statique pour contrôler le résultat du hachage/vérification.
        mockedPasswordUtil = mockStatic(PasswordUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedPasswordUtil.close();
    }

    // ===================== TESTS AUTHENTIFIER (LOGIN) =====================

    @Test
    void testAuthentifier_Success() {
        // Given
        String email = "chaimae@etu.ma";
        String pass = "12345678";
        String hashedPass = "hashed_12345678";

        Utilisateur user = new Utilisateur();
        user.setEmail(email);
        user.setMotDePasse(hashedPass);

        when(utilisateurDAO.findByEmail(email)).thenReturn(Optional.of(user));
        // On simule que PasswordUtil dit "OUI" pour ce mot de passe
        mockedPasswordUtil.when(() -> PasswordUtil.verifier(pass, hashedPass)).thenReturn(true);

        // When
        Utilisateur result = authService.authentifier(email, pass);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testAuthentifier_WrongPassword() {
        // Given
        String email = "test@test.com";
        Utilisateur user = new Utilisateur();
        user.setMotDePasse("hash_secret");

        when(utilisateurDAO.findByEmail(email)).thenReturn(Optional.of(user));
        mockedPasswordUtil.when(() -> PasswordUtil.verifier(anyString(), anyString())).thenReturn(false);

        // When
        Utilisateur result = authService.authentifier(email, "wrong_pass");

        // Then
        assertNull(result);
    }

    // ===================== TESTS INSCRIRE (REGISTER) =====================

    @Test
    void testInscrire_Success() {
        // Given
        Utilisateur newUser = new Utilisateur();
        newUser.setEmail("nouveau@test.com");
        newUser.setMotDePasse("plainPassword");

        when(utilisateurDAO.findByEmail("nouveau@test.com")).thenReturn(Optional.empty());
        mockedPasswordUtil.when(() -> PasswordUtil.hasher("plainPassword")).thenReturn("hashedPassword");

        // When
        boolean result = authService.inscrire(newUser);

        // Then
        assertTrue(result);
        assertEquals("hashedPassword", newUser.getMotDePasse()); // Vérifie que le mot de passe a été haché
        verify(utilisateurDAO).save(newUser);
    }

    @Test
    void testInscrire_EmailAlreadyExists() {
        // Given
        Utilisateur existingUser = new Utilisateur();
        existingUser.setEmail("deja@pris.com");

        when(utilisateurDAO.findByEmail("deja@pris.com")).thenReturn(Optional.of(existingUser));

        // When
        boolean result = authService.inscrire(existingUser);

        // Then
        assertFalse(result);
        verify(utilisateurDAO, never()).save(any());
    }
}