package org.pfemanager.integration;

import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.pfemanager.controller.AuthBean;
import org.pfemanager.controller.ProjetEncadrantBean;
import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.*;
import org.pfemanager.service.DocumentService;
import org.pfemanager.service.ProjetService;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProjetEncadrantBeanIT {

    private ProjetEncadrantBean bean;

    private ProjetService projetService;
    private DocumentService documentService;
    private AuthBean authBean;

    private MockedStatic<FacesContext> facesContextMock;

    @BeforeEach
    void setup() {
        bean = new ProjetEncadrantBean();

        projetService = mock(ProjetService.class);
        documentService = mock(DocumentService.class);
        authBean = mock(AuthBean.class);

        inject(bean, "projetService", projetService);
        inject(bean, "documentService", documentService);
        inject(bean, "authBean", authBean);

        facesContextMock = mockStatic(FacesContext.class);
        facesContextMock.when(FacesContext::getCurrentInstance)
                .thenReturn(mock(FacesContext.class));
    }

    @AfterEach
    void tearDown() {
        facesContextMock.close();
    }

    // ================= LOAD PROJETS =================

    @Test
    void loadProjets_success() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Projet p = new Projet();
        p.setStatut(StatutProjet.EN_COURS);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(projetService.getProjetsByEncadrant(1L)).thenReturn(List.of(p));

        bean.loadProjets();

        assertThat(bean.getProjets()).hasSize(1);
    }

    // ================= SELECTION PROJET =================

    @Test
    void selectionnerProjet_chargeDetails() {
        Projet p = new Projet();
        p.setId(10L);
        p.setStatut(StatutProjet.EN_COURS);

        when(projetService.getCommentaires(10L)).thenReturn(List.of(new Commentaire()));
        when(documentService.getDocumentsByProjet(10L)).thenReturn(List.of(new Document()));

        bean.selectionnerProjet(p);

        assertThat(bean.getSelectedProjet()).isEqualTo(p);
        assertThat(bean.getCommentaires()).hasSize(1);
        assertThat(bean.getDocuments()).hasSize(1);
    }

    // ================= CHANGER STATUT =================

    @Test
    void changerStatut_success() {
        Projet p = new Projet();
        p.setId(10L);
        p.setStatut(StatutProjet.EN_COURS);

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(projetService.getProjetsByEncadrant(1L)).thenReturn(List.of(p));
        when(projetService.findById(10L)).thenReturn(Optional.of(p));
        when(projetService.getCommentaires(10L)).thenReturn(List.of());
        when(documentService.getDocumentsByProjet(10L)).thenReturn(List.of());

        bean.setSelectedProjet(p);
        bean.setNouveauStatut(StatutProjet.VALIDE);

        bean.changerStatut();

        verify(projetService).changerStatut(10L, StatutProjet.VALIDE);
        assertThat(bean.getSelectedProjet()).isNotNull();
    }

    // ================= AJOUT COMMENTAIRE =================

    @Test
    void ajouterCommentaire_success() {
        Projet p = new Projet();
        p.setId(10L);

        Utilisateur user = new Utilisateur();
        user.setId(1L);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(projetService.getCommentaires(10L)).thenReturn(List.of());

        bean.setSelectedProjet(p);
        bean.setNouveauCommentaire("Bon travail");

        bean.ajouterCommentaire();

        verify(projetService).ajouterCommentaire(10L, user, "Bon travail");
        assertThat(bean.getNouveauCommentaire()).isNull();
    }

    @Test
    void ajouterCommentaire_erreur() {
        bean.setNouveauCommentaire(""); // vide

        bean.ajouterCommentaire();

        // Pas d'appel service
        verify(projetService, never()).ajouterCommentaire(anyLong(), any(), any());
    }

    // ================= RETOUR LISTE =================

    @Test
    void retourListe_reset() {
        bean.setSelectedProjet(new Projet());
        bean.setNouveauCommentaire("test");
        bean.setNouveauStatut(StatutProjet.EN_COURS);

        bean.retourListe();

        assertThat(bean.getSelectedProjet()).isNull();
        assertThat(bean.getNouveauCommentaire()).isNull();
        assertThat(bean.getNouveauStatut()).isNull();
    }

    // ================= STAT =================

    @Test
    void getProjetsEnCours() {
        Projet p1 = new Projet();
        p1.setStatut(StatutProjet.EN_COURS);

        Projet p2 = new Projet();
        p2.setStatut(StatutProjet.VALIDE);

        inject(bean, "projets", List.of(p1, p2));

        assertThat(bean.getProjetsEnCours()).isEqualTo(1);
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
}