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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordBeanTest {

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @InjectMocks
    private ForgotPasswordBean forgotPasswordBean;

    private MockedStatic<FacesContext> mockedFacesContext;

    @BeforeEach
    void setUp() {
        mockedFacesContext = mockStatic(FacesContext.class);
        mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

        // Utilisation de lenient() car le test d'erreur email ne l'appellera pas
        lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
    }

    @AfterEach
    void tearDown() {
        mockedFacesContext.close();
    }

    @Test
    void testEnvoyerLien_Success() throws IOException {
        // Given
        String testEmail = "chaimae@etu.ma";
        forgotPasswordBean.setEmail(testEmail);

        // Simulation des infos de l'URL par ExternalContext
        when(externalContext.getRequestScheme()).thenReturn("http");
        when(externalContext.getRequestServerName()).thenReturn("localhost");
        when(externalContext.getRequestServerPort()).thenReturn(8080);
        when(externalContext.getRequestContextPath()).thenReturn("/PFE");

        String expectedBaseUrl = "http://localhost:8080/PFE";

        // When
        forgotPasswordBean.envoyerLien();

        // Then
        assertNull(forgotPasswordBean.getMessageErreur());
        assertEquals("Si cet email existe, un lien de réinitialisation vous a été envoyé.",
                forgotPasswordBean.getMessageSucces());

        // Vérifier que le service a été appelé avec les bons paramètres
        verify(passwordResetService).demanderReinitialisation(testEmail, expectedBaseUrl);
    }

    @Test
    void testEnvoyerLien_InvalidEmail() throws IOException {
        // Given
        forgotPasswordBean.setEmail("email_invalide");

        // When
        forgotPasswordBean.envoyerLien();

        // Then
        assertNotNull(forgotPasswordBean.getMessageErreur());
        assertEquals("Veuillez entrer une adresse email valide.", forgotPasswordBean.getMessageErreur());
        assertNull(forgotPasswordBean.getMessageSucces());

        // Vérifier que le service n'est JAMAIS appelé
        verify(passwordResetService, never()).demanderReinitialisation(anyString(), anyString());
    }

    @Test
    void testEnvoyerLien_EmptyEmail() throws IOException {
        // Given
        forgotPasswordBean.setEmail(null);

        // When
        forgotPasswordBean.envoyerLien();

        // Then
        assertEquals("Veuillez entrer une adresse email valide.", forgotPasswordBean.getMessageErreur());
        verify(passwordResetService, never()).demanderReinitialisation(anyString(), anyString());
    }
}