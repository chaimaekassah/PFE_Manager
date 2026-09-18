package org.pfemanager.integration;

import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.pfemanager.controller.AuthBean;
import org.pfemanager.controller.CandidatureEncadrantBean;
import org.pfemanager.enums.StatutCandidature;
import org.pfemanager.model.Candidature;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.CandidatureService;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CandidatureEncadrantBeanIT {

    private CandidatureEncadrantBean bean;
    private CandidatureService service;
    private AuthBean authBean;

    private MockedStatic<FacesContext> facesContextMock;

    @BeforeEach
    void setup() throws Exception {
        bean = new CandidatureEncadrantBean();

        service = mock(CandidatureService.class);
        authBean = mock(AuthBean.class);

        // 🔥 Injection via Reflection
        inject(bean, "candidatureService", service);
        inject(bean, "authBean", authBean);

        // Mock FacesContext
        facesContextMock = mockStatic(FacesContext.class);
        facesContextMock.when(FacesContext::getCurrentInstance)
                .thenReturn(mock(FacesContext.class));
    }

    @AfterEach
    void tearDown() {
        facesContextMock.close();
    }

    @Test
    void integration_accepter_fluxComplet() {
        // GIVEN
        Utilisateur encadrant = new Utilisateur();
        encadrant.setId(1L);

        Utilisateur etudiant = new Utilisateur();
        etudiant.setNom("Martin");

        Candidature c = new Candidature();
        c.setId(10L);
        c.setStatut(StatutCandidature.EN_ATTENTE);
        c.setEtudiant(etudiant);

        when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
        when(service.getCandidaturesByEncadrant(1L))
                .thenReturn(List.of(c));

        // WHEN
        bean.init();
        bean.voirDetails(c);
        bean.setRemarque("Bon profil");
        bean.accepter();

        // THEN
        verify(service).accepter(10L, "Bon profil");
        assertThat(bean.getSelectedCandidature()).isNull();
    }

    @Test
    void integration_refuser_fluxComplet() {
        Utilisateur encadrant = new Utilisateur();
        encadrant.setId(1L);

        Utilisateur etudiant = new Utilisateur();
        etudiant.setNom("Martin");

        Candidature c = new Candidature();
        c.setId(20L);
        c.setStatut(StatutCandidature.EN_ATTENTE);
        c.setEtudiant(etudiant);

        when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
        when(service.getCandidaturesByEncadrant(1L))
                .thenReturn(List.of(c));

        bean.init();
        bean.voirDetails(c);
        bean.setRemarque("Refus");

        bean.refuser();

        verify(service).refuser(20L, "Refus");
        assertThat(bean.getSelectedCandidature()).isNull();
    }

    // 🔧 Méthode générique d’injection
    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}