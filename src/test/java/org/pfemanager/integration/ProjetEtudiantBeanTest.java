package org.pfemanager.integration;

import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.pfemanager.controller.AuthBean;
import org.pfemanager.controller.ProjetEtudiantBean;
import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.*;
import org.pfemanager.service.DocumentService;
import org.pfemanager.service.ProjetService;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProjetEtudiantBeanIT {

    private ProjetEtudiantBean bean;

    private ProjetService projetService;
    private DocumentService documentService;
    private AuthBean authBean;

    private MockedStatic<FacesContext> facesContextMock;

    @BeforeEach
    void setup() {
        bean = new ProjetEtudiantBean();

        projetService = mock(ProjetService.class);
        documentService = mock(DocumentService.class);
        authBean = mock(AuthBean.class);

        inject(bean, "projetService", projetService);
        inject(bean, "documentService", documentService);
        inject(bean, "authBean", authBean);

        // Mock JSF
        facesContextMock = mockStatic(FacesContext.class);
        facesContextMock.when(FacesContext::getCurrentInstance)
                .thenReturn(mock(FacesContext.class));
    }

    @AfterEach
    void tearDown() {
        facesContextMock.close();
    }

    // ================= LOAD PROJET =================

    @Test
    void loadProjet_success() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Projet p = new Projet();
        p.setId(10L);
        p.setStatut(StatutProjet.EN_COURS);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(projetService.getProjetByEtudiant(1L)).thenReturn(Optional.of(p));
        when(projetService.getCommentaires(10L)).thenReturn(List.of(new Commentaire()));
        when(documentService.getDocumentsByProjet(10L)).thenReturn(List.of(new Document()));

        bean.loadProjet();

        assertThat(bean.getProjet()).isNotNull();
        assertThat(bean.getCommentaires()).hasSize(1);
        assertThat(bean.getDocuments()).hasSize(1);
    }

    @Test
    void loadProjet_aucunProjet() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(projetService.getProjetByEtudiant(1L)).thenReturn(Optional.empty());

        bean.loadProjet();

        assertThat(bean.getProjet()).isNull();
    }

    // ================= DEPOSER DOCUMENT =================

    @Test
    void deposerDocument_success() {
        Utilisateur user = new Utilisateur();
        user.setId(1L);

        Projet p = new Projet();
        p.setId(10L);

        when(authBean.getUtilisateurConnecte()).thenReturn(user);
        when(documentService.getDocumentsByProjet(10L)).thenReturn(List.of());

        bean.setProjet(p);
        bean.setNomDocument("rapport.pdf");
        bean.setTypeDocument("pdf");

        bean.deposerDocument();

        verify(documentService).create(any(Document.class));
        assertThat(bean.getNomDocument()).isNull();
    }

    @Test
    void deposerDocument_erreur() {
        bean.setNomDocument(""); // invalide

        bean.deposerDocument();

        verify(documentService, never()).create(any());
    }

    // ================= HAS PROJET =================

    @Test
    void hasProjet_true() {
        bean.setProjet(new Projet());
        assertThat(bean.hasProjet()).isTrue();
    }

    @Test
    void hasProjet_false() {
        bean.setProjet(null);
        assertThat(bean.hasProjet()).isFalse();
    }

    // ================= STATUT CLASS =================

    @Test
    void getStatutClass() {
        Projet p = new Projet();
        p.setStatut(StatutProjet.VALIDE);

        bean.setProjet(p);

        assertThat(bean.getStatutClass()).isEqualTo("statut-valide");
    }

    // ================= FORMAT DATE =================

    @Test
    void formatDate() {
        assertThat(bean.formatDate(null)).isEmpty();
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