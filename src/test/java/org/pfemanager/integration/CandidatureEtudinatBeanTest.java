package org.pfemanager.integration;

import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.pfemanager.controller.AuthBean;
import org.pfemanager.controller.CandidatureEtudiantBean;
import org.pfemanager.enums.StatutCandidature;
import org.pfemanager.model.Candidature;
import org.pfemanager.model.Sujet;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.CandidatureService;
import org.pfemanager.service.SujetService;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CandidatureEtudiantBeanIT {

    private CandidatureEtudiantBean bean;

    private CandidatureService candidatureService;
    private SujetService sujetService;
    private AuthBean authBean;

    private MockedStatic<FacesContext> facesContextMock;

    @BeforeEach
    void setup() {
        bean = new CandidatureEtudiantBean();

        candidatureService = mock(CandidatureService.class);
        sujetService = mock(SujetService.class);
        authBean = mock(AuthBean.class);

        // 🔥 Injection via Reflection
        inject(bean, "candidatureService", candidatureService);
        inject(bean, "sujetService", sujetService);
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

    // ================= POSTULER SUCCESS =================

    @Test
    void postuler_success() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Utilisateur encadrant = new Utilisateur();
        encadrant.setNom("Dupont");

        Sujet sujet = new Sujet();
        sujet.setId(100L);
        sujet.setTitre("Sujet PFE");
        sujet.setEncadrant(encadrant);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(sujetService.findById(100L)).thenReturn(sujet);
        when(candidatureService.existeCandidature(1L, 100L)).thenReturn(false);

        bean.setSujetIdSelectionne(100L);
        bean.setMessageMotivation("Je suis motivé");

        bean.postulerDepuisModal();

        verify(candidatureService).create(any(Candidature.class));
        assertThat(bean.getMessageSucces()).contains("succès");
        assertThat(bean.getMessageErreur()).isNull();
    }

    // ================= ERREURS =================

    @Test
    void postuler_sansUtilisateur() {
        when(authBean.getUtilisateurConnecte()).thenReturn(null);

        // 🔥 IMPORTANT : satisfaire les autres conditions
        bean.setSujetIdSelectionne(100L);
        bean.setMessageMotivation("Motivation");

        bean.postulerDepuisModal();

        assertThat(bean.getMessageErreur()).contains("Session expirée");
    }

    @Test
    void postuler_sansSujet() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);

        bean.setMessageMotivation("Motivation");

        bean.postulerDepuisModal();

        assertThat(bean.getMessageErreur()).contains("choisir un sujet");
    }

    @Test
    void postuler_dejaExiste() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Sujet sujet = new Sujet();
        sujet.setId(100L);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(sujetService.findById(100L)).thenReturn(sujet); // 🔥 FIX
        when(candidatureService.existeCandidature(1L, 100L)).thenReturn(true);

        bean.setSujetIdSelectionne(100L);
        bean.setMessageMotivation("Motivation");

        bean.postulerDepuisModal();

        assertThat(bean.getMessageErreur()).contains("déjà postulé");
    }

    // ================= RETIRER =================

    @Test
    void retirer_success() {
        bean.retirer(5L);

        verify(candidatureService).retirer(5L);
        assertThat(bean.getMessageSucces()).contains("retirée");
    }

    // ================= STATISTIQUES =================

    @Test
    void statistiques() {
        Candidature c1 = build(StatutCandidature.EN_ATTENTE);
        Candidature c2 = build(StatutCandidature.ACCEPTEE);
        Candidature c3 = build(StatutCandidature.REFUSEE);

        inject(bean, "candidatures", List.of(c1, c2, c3));

        assertThat(bean.getTotalCandidatures()).isEqualTo(3);
        assertThat(bean.getCandidaturesEnAttente()).isEqualTo(1);
        assertThat(bean.getCandidaturesAcceptees()).isEqualTo(1);
        assertThat(bean.getCandidaturesRefusees()).isEqualTo(1);
    }

    // ================= HELPERS =================

    private void inject(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Candidature build(StatutCandidature statut) {
        Candidature c = new Candidature();
        c.setStatut(statut);
        return c;
    }
}