package org.pfemanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.controller.AuthBean;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private HttpSession session;

    @Mock
    private AuthBean authBean;

    @InjectMocks
    private AuthFilter authFilter;

    @Test
    void testDoFilter_NotLoggedIn_ShouldRedirect() throws IOException, ServletException {
        // Given : Aucune session active
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/PFE");

        // When
        authFilter.doFilter(request, response, chain);

        // Then : On doit rediriger vers le login et NE PAS continuer la chaîne
        verify(response).sendRedirect("/PFE/login.xhtml");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void testDoFilter_LoggedIn_ShouldContinue() throws IOException, ServletException {
        // Given : Utilisateur connecté dans l'AuthBean
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("authBean")).thenReturn(authBean);
        when(authBean.isConnecte()).thenReturn(true);

        // When
        authFilter.doFilter(request, response, chain);

        // Then : On laisse passer l'utilisateur
        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testDoFilter_SessionExistsButNotConnected_ShouldRedirect() throws IOException, ServletException {
        // Given : Session existe mais AuthBean est vide ou isConnecte() est faux
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("authBean")).thenReturn(authBean);
        when(authBean.isConnecte()).thenReturn(false);
        when(request.getContextPath()).thenReturn("/PFE");

        // When
        authFilter.doFilter(request, response, chain);

        // Then
        verify(response).sendRedirect("/PFE/login.xhtml");
        verify(chain, never()).doFilter(any(), any());
    }
}