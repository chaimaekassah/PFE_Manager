package org.pfemanager.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.enums.StatutCandidature;
import org.pfemanager.model.Candidature;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.CandidatureService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidatureEncadrantBeanTest {

    @Mock private CandidatureService candidatureService;
    @Mock private AuthBean authBean;
    @Mock private FacesContext facesContext;

    @InjectMocks
    private CandidatureEncadrantBean bean;

    private MockedStatic<FacesContext> facesContextMock;

    private Utilisateur encadrant;
    private Utilisateur etudiant;
    private Candidature enAttente;
    private Candidature acceptee;
    private Candidature refusee;

    @BeforeEach
    void setUp() {
        // 🔥 FIX FacesContext
        facesContextMock = mockStatic(FacesContext.class);
        facesContextMock.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

        encadrant = user(1L, "Dupont");
        etudiant  = user(2L, "Martin");

        enAttente = candidature(10L, StatutCandidature.EN_ATTENTE, "Remarque");
        acceptee  = candidature(11L, StatutCandidature.ACCEPTEE, null);
        refusee   = candidature(12L, StatutCandidature.REFUSEE, "Refus");
    }

    @AfterEach
    void tearDown() {
        facesContextMock.close();
    }

    // ================= INIT =================

    @Test
    void init_chargeLesCandidatures() {
        when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
        when(candidatureService.getCandidaturesByEncadrant(1L))
                .thenReturn(List.of(enAttente));

        bean.init();

        assertThat(bean.getCandidatures()).containsExactly(enAttente);
    }

    @Test
    void onPreRender_recharge() {
        when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
        when(candidatureService.getCandidaturesByEncadrant(1L))
                .thenReturn(List.of(enAttente, acceptee));

        bean.onPreRender(mock(ComponentSystemEvent.class));

        verify(candidatureService).getCandidaturesByEncadrant(1L);
    }

    // ================= DETAILS =================

    @Test
    void voirDetails() {
        bean.voirDetails(enAttente);

        assertThat(bean.getSelectedCandidature()).isEqualTo(enAttente);
        assertThat(bean.getRemarque()).isEqualTo("Remarque");
    }

    @Test
    void annuler() {
        bean.voirDetails(enAttente);
        bean.annuler();

        assertThat(bean.getSelectedCandidature()).isNull();
        assertThat(bean.getRemarque()).isNull();
    }

    // ================= ACCEPTER =================

    @Test
    void accepter_ok() {
        when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
        when(candidatureService.getCandidaturesByEncadrant(1L))
                .thenReturn(Collections.emptyList());

        bean.voirDetails(enAttente);
        bean.setRemarque("OK");

        bean.accepter();

        verify(candidatureService).accepter(10L, "OK");
        verify(facesContext).addMessage(eq(null), any(FacesMessage.class));

        assertThat(bean.getSelectedCandidature()).isNull();
    }

    @Test
    void accepter_sansSelection() {
        bean.annuler();

        bean.accepter();

        verify(candidatureService, never()).accepter(anyLong(), any());
    }

    // ================= REFUSER =================

    @Test
    void refuser_ok() {
        when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
        when(candidatureService.getCandidaturesByEncadrant(1L))
                .thenReturn(Collections.emptyList());

        bean.voirDetails(enAttente);
        bean.setRemarque("Refus");

        bean.refuser();

        verify(candidatureService).refuser(10L, "Refus");
        verify(facesContext).addMessage(eq(null), any(FacesMessage.class));

        assertThat(bean.getSelectedCandidature()).isNull();
    }

    @Test
    void refuser_sansSelection() {
        bean.annuler();

        bean.refuser();

        verify(candidatureService, never()).refuser(anyLong(), any());
    }

    // ================= STATS =================

    @Test
    void stats() {
        when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
        when(candidatureService.getCandidaturesByEncadrant(1L))
                .thenReturn(Arrays.asList(enAttente, acceptee, refusee));

        bean.init();

        assertThat(bean.getCandidaturesEnAttente()).isEqualTo(1);
        assertThat(bean.getCandidaturesTraitees()).isEqualTo(2);
    }

    // ================= UTILS =================

    @Test
    void canTraiter() {
        assertThat(bean.canTraiter(enAttente)).isTrue();
        assertThat(bean.canTraiter(acceptee)).isFalse();
        assertThat(bean.canTraiter(null)).isFalse();
    }

    @Test
    void getStatutClass() {
        assertThat(bean.getStatutClass(StatutCandidature.EN_ATTENTE)).isEqualTo("en_attente");
        assertThat(bean.getStatutClass(null)).isEmpty();
    }

    @Test
    void formatDate() {
        LocalDateTime dt = LocalDateTime.of(2025, 3, 5, 14, 0);
        assertThat(bean.formatDate(dt)).isEqualTo("05/03/2025 14:00");
    }

    // ================= HELPERS =================

    private Utilisateur user(Long id, String nom) {
        Utilisateur u = new Utilisateur();
        u.setId(id);
        u.setNom(nom);
        return u;
    }

    private Candidature candidature(Long id, StatutCandidature statut, String remarque) {
        Candidature c = new Candidature();
        c.setId(id);
        c.setStatut(statut);
        c.setEtudiant(etudiant);
        c.setRemarqueEncadrant(remarque);
        return c;
    }
}