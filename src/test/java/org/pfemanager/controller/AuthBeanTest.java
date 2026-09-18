package org.pfemanager.controller;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.model.Role;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.AuthService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class AuthBeanTest {

    @Mock
    private AuthService authService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthBean authBean;

    private MockedStatic<FacesContext> mockedFacesContext;

    @BeforeEach
    void setUp() {
        mockedFacesContext = mockStatic(FacesContext.class);

        // ✅ On ajoute lenient() ici pour que Mockito ne râle pas
        // si un test n'utilise pas FacesContext
        mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

        // ✅ On fait pareil pour l'externalContext
        lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
    }

    @AfterEach
    void tearDown() {
        // Très important : libérer le mock statique après chaque test
        mockedFacesContext.close();
    }

    // ===================== TESTS LOGIN =====================

    @Test
    void testLoginSuccess_Admin() throws IOException {
        // Given
        authBean.setEmail("admin@test.com");
        authBean.setMotDePasse("password123");

        Utilisateur user = new Utilisateur();
        user.setEmail("admin@test.com");
        user.setRole(Role.ADMINISTRATEUR);

        when(authService.authentifier("admin@test.com", "password123")).thenReturn(user);
        when(externalContext.getSession(true)).thenReturn(session);
        when(externalContext.getRequestContextPath()).thenReturn("/PFE");

        // When
        authBean.login();

        // Then
        assertNull(authBean.getMessageErreur());
        assertEquals(user, authBean.getUtilisateurConnecte());
        verify(externalContext).redirect("/PFE/admin/dashboard.xhtml");
        verify(session).setAttribute("authBean", authBean);
    }

    @Test
    void testLoginFailure_WrongCredentials() throws IOException {
        // Given
        authBean.setEmail("wrong@test.com");
        authBean.setMotDePasse("wrong");
        when(authService.authentifier("wrong@test.com", "wrong")).thenReturn(null);

        // When
        authBean.login();

        // Then
        assertEquals("Email ou mot de passe incorrect.", authBean.getMessageErreur());
        assertNull(authBean.getUtilisateurConnecte());
        verify(externalContext, never()).redirect(anyString());
    }

    // ===================== TESTS REGISTER =====================

    @Test
    void testRegisterSuccess() throws IOException {
        // Given
        authBean.setNom("Chaimae");
        authBean.setEmail("chaimae@etu.ma");
        authBean.setMotDePasse("password123"); // 11 caractères (>= 8)
        authBean.setRoleSelectionne("ETUDIANT");

        when(authService.inscrire(any(Utilisateur.class))).thenReturn(true);
        when(externalContext.getRequestContextPath()).thenReturn("/PFE");

        // When
        authBean.register();

        // Then
        assertNull(authBean.getMessageErreur());
        verify(externalContext).redirect("/PFE/login.xhtml");
    }

    @Test
    void testRegisterFailure_PasswordTooShort() throws IOException {
        // Given
        authBean.setNom("Test");
        authBean.setEmail("test@test.com");
        authBean.setMotDePasse("123"); // Trop court

        // When
        authBean.register();

        // Then
        assertEquals("Le mot de passe doit contenir au moins 8 caractères.", authBean.getMessageErreur());
        verify(authService, never()).inscrire(any());
    }

    @Test
    void testRegisterFailure_EmailExists() throws IOException {
        // Given
        authBean.setNom("User");
        authBean.setEmail("deja@pris.com");
        authBean.setMotDePasse("password123");
        authBean.setRoleSelectionne("ENCADRANT");

        when(authService.inscrire(any(Utilisateur.class))).thenReturn(false);

        // When
        authBean.register();

        // Then
        assertEquals("Cet email est déjà utilisé.", authBean.getMessageErreur());
    }
}