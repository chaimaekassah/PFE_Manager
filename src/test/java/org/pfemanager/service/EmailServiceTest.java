package org.pfemanager.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    private MockedStatic<Transport> mockedTransport;

    @BeforeEach
    void setUp() {
        // On intercepte les appels statiques à Transport pour ne pas envoyer de vrai email
        mockedTransport = mockStatic(Transport.class);
    }

    @AfterEach
    void tearDown() {
        mockedTransport.close();
    }

    @Test
    void testEnvoyerLienReinitialisation_StructureSuccess() {
        // Given
        String emailDest = "test@student.ma";
        String lien = "http://localhost/reset?token=123";

        // When
        // On vérifie simplement que la méthode s'exécute sans lancer d'exception
        assertDoesNotThrow(() -> emailService.envoyerLienReinitialisation(emailDest, lien));

        // Then
        // On vérifie que la méthode statique send() a bien été appelée une fois
        mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
    }

    @Test
    void testEnvoyerLienReinitialisation_HandlesMessagingException() {
        // Given
        String emailDest = "error@student.ma";
        String lien = "http://localhost/reset";

        // On simule une erreur réseau ou SMTP
        mockedTransport.when(() -> Transport.send(any(Message.class)))
                .thenThrow(new MessagingException("SMTP Error"));

        // When & Then
        // Ton code transforme la MessagingException en RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.envoyerLienReinitialisation(emailDest, lien);
        });

        assertTrue(exception.getMessage().contains("Erreur envoi email"));
    }
}