package org.pfemanager.controller;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.service.PasswordResetService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordBeanTest {

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @InjectMocks
    private ResetPasswordBean resetPasswordBean;

    private MockedStatic<FacesContext> mockedFacesContext;
    private Map<String, String> requestParams;

    @BeforeEach
    void setUp() {
        mockedFacesContext = mockStatic(FacesContext.class);
        mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);

        // Préparer une map de paramètres vide par défaut
        requestParams = new HashMap<>();
        lenient().when(externalContext.getRequestParameterMap()).thenReturn(requestParams);
    }

    @AfterEach
    void tearDown() {
        mockedFacesContext.close();
    }

    @Test
    void testInit_WithValidToken() {
        // Given
        String validToken = "abc-123";
        requestParams.put("token", validToken);
        when(passwordResetService.tokenValide(validToken)).thenReturn(true);

        // When
        resetPasswordBean.init(); // Appel manuel du PostConstruct

        // Then
        assertTrue(resetPasswordBean.isTokenValide());
        assertEquals(validToken, resetPasswordBean.getToken());
        assertNull(resetPasswordBean.getMessageErreur());
    }

    @Test
    void testInit_WithInvalidToken() {
        // Given
        String expiredToken = "expired-456";
        requestParams.put("token", expiredToken);
        when(passwordResetService.tokenValide(expiredToken)).thenReturn(false);

        // When
        resetPasswordBean.init();

        // Then
        assertFalse(resetPasswordBean.isTokenValide());
        assertEquals("Ce lien est expiré ou déjà utilisé.", resetPasswordBean.getMessageErreur());
    }

    @Test
    void testReinitialiser_Success() throws IOException {
        // Given
        String token = "valid-token";
        resetPasswordBean.setToken(token);
        resetPasswordBean.setNouveauMotDePasse("newPassword123");
        resetPasswordBean.setConfirmerMotDePasse("newPassword123");

        when(passwordResetService.reinitialiserMotDePasse(token, "newPassword123")).thenReturn(true);
        when(externalContext.getRequestContextPath()).thenReturn("/PFE");

        // When
        resetPasswordBean.reinitialiser();

        // Then
        assertNull(resetPasswordBean.getMessageErreur());
        verify(externalContext).redirect("/PFE/login.xhtml?reset=success");
    }

    @Test
    void testReinitialiser_PasswordsDoNotMatch() throws IOException {
        // Given
        resetPasswordBean.setNouveauMotDePasse("password123");
        resetPasswordBean.setConfirmerMotDePasse("different123");

        // When
        resetPasswordBean.reinitialiser();

        // Then
        assertEquals("Les mots de passe ne correspondent pas.", resetPasswordBean.getMessageErreur());
        verify(passwordResetService, never()).reinitialiserMotDePasse(anyString(), anyString());
    }

    @Test
    void testReinitialiser_PasswordTooShort() throws IOException {
        // Given
        resetPasswordBean.setNouveauMotDePasse("123");
        resetPasswordBean.setConfirmerMotDePasse("123");

        // When
        resetPasswordBean.reinitialiser();

        // Then
        assertEquals("Le mot de passe doit contenir au moins 8 caractères.", resetPasswordBean.getMessageErreur());
    }
}