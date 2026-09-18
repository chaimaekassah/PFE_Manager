package org.pfemanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.enums.StatutProjet;
import org.pfemanager.model.*;
import org.pfemanager.service.DocumentService;
import org.pfemanager.service.ProjetService;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires – ProjetEtudiantBean")
class ProjetEtudiantBeanTest {

    // ------------------------------------------------------------------ mocks
    @Mock private ProjetService   projetService;
    @Mock private DocumentService documentService;
    @Mock private AuthBean        authBean;
    @Mock private FacesContext    facesContext;

    @InjectMocks
    private ProjetEtudiantBean bean;

    // ------------------------------------------------------------------ fixtures
    private Utilisateur etudiant;
    private Projet      projet;

    // ------------------------------------------------------------------ setup
    @BeforeEach
    void setUp() {
        etudiant = new Utilisateur();
        etudiant.setId(42L);
        etudiant.setNom("Martin");

        projet = new Projet();
        projet.setId(100L);
        projet.setStatut(StatutProjet.EN_COURS);
    }

    // ================================================================== loadProjet
    @Nested
    @DisplayName("loadProjet()")
    class LoadProjet {

        @Test
        @DisplayName("charge le projet et ses détails quand l'étudiant a un projet")
        void loadProjet_avecProjet() {
            List<Commentaire> comms = List.of(new Commentaire());
            List<Document>    docs  = List.of(new Document());

            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(projetService.getProjetByEtudiant(42L)).thenReturn(Optional.of(projet));
            when(projetService.getCommentaires(100L)).thenReturn(comms);
            when(documentService.getDocumentsByProjet(100L)).thenReturn(docs);

            bean.loadProjet();

            assertThat(bean.getProjet()).isEqualTo(projet);
            assertThat(bean.getCommentaires()).isEqualTo(comms);
            assertThat(bean.getDocuments()).isEqualTo(docs);
        }

        @Test
        @DisplayName("projet reste null quand l'étudiant n'a pas de projet")
        void loadProjet_sansProjet() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(projetService.getProjetByEtudiant(42L)).thenReturn(Optional.empty());

            bean.loadProjet();

            assertThat(bean.getProjet()).isNull();
            verifyNoInteractions(documentService);
            verify(projetService, never()).getCommentaires(anyLong());
        }

        @Test
        @DisplayName("ne fait rien si aucun utilisateur connecté")
        void loadProjet_utilisateurNull() {
            when(authBean.getUtilisateurConnecte()).thenReturn(null);

            bean.loadProjet();

            assertThat(bean.getProjet()).isNull();
            verifyNoInteractions(projetService, documentService);
        }
    }

    // ================================================================== deposerDocument
    @Nested
    @DisplayName("deposerDocument()")
    class DeposerDocument {

        @BeforeEach
        void setProjet() {
            bean.setProjet(projet);
        }

        @Test
        @DisplayName("crée le document avec le type fourni et recharge la liste")
        void deposerDocument_succes_avecType() {
            bean.setNomDocument("rapport_final.pdf");
            bean.setTypeDocument("pdf");
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            List<Document> docsMAJ = List.of(new Document());
            when(documentService.getDocumentsByProjet(100L)).thenReturn(docsMAJ);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
                bean.deposerDocument();
            }

            ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
            verify(documentService).create(captor.capture());
            Document created = captor.getValue();
            assertThat(created.getNomFichier()).isEqualTo("rapport_final.pdf");
            assertThat(created.getTypeFichier()).isEqualTo("pdf");
            assertThat(created.getDepositaire()).isEqualTo(etudiant);
            assertThat(created.getProjet()).isEqualTo(projet);
            assertThat(created.getTaille()).isEqualTo(1024L * 512);

            assertThat(bean.getNomDocument()).isNull();
            assertThat(bean.getTypeDocument()).isNull();
            assertThat(bean.getDocuments()).isEqualTo(docsMAJ);

            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_INFO
            ));
        }

        @Test
        @DisplayName("utilise le type par défaut 'pdf' si typeDocument est null")
        void deposerDocument_typeParDefaut() {
            bean.setNomDocument("memoire.doc");
            bean.setTypeDocument(null);
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(documentService.getDocumentsByProjet(100L)).thenReturn(Collections.emptyList());

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
                bean.deposerDocument();
            }

            ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
            verify(documentService).create(captor.capture());
            assertThat(captor.getValue().getTypeFichier()).isEqualTo("pdf");
        }

        @Test
        @DisplayName("affiche une erreur si le nom est vide")
        void deposerDocument_nomVide() {
            bean.setNomDocument("   ");
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
                bean.deposerDocument();
            }

            verify(documentService, never()).create(any());
            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_ERROR
            ));
        }

        @Test
        @DisplayName("affiche une erreur si le nom est null")
        void deposerDocument_nomNull() {
            bean.setNomDocument(null);
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
                bean.deposerDocument();
            }

            verify(documentService, never()).create(any());
            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_ERROR
            ));
        }

        @Test
        @DisplayName("affiche une erreur si aucun utilisateur connecté")
        void deposerDocument_utilisateurNull() {
            bean.setNomDocument("rapport.pdf");
            when(authBean.getUtilisateurConnecte()).thenReturn(null);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
                bean.deposerDocument();
            }

            verify(documentService, never()).create(any());
            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_ERROR
            ));
        }

        @Test
        @DisplayName("affiche une erreur si aucun projet assigné")
        void deposerDocument_projetNull() {
            bean.setProjet(null);
            bean.setNomDocument("rapport.pdf");
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
                bean.deposerDocument();
            }

            verify(documentService, never()).create(any());
            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_ERROR
            ));
        }
    }

    // ================================================================== hasProjet
    @Nested
    @DisplayName("hasProjet()")
    class HasProjet {

        @Test
        @DisplayName("retourne true quand un projet est chargé")
        void hasProjet_true() {
            bean.setProjet(projet);
            assertThat(bean.hasProjet()).isTrue();
        }

        @Test
        @DisplayName("retourne false quand aucun projet")
        void hasProjet_false() {
            bean.setProjet(null);
            assertThat(bean.hasProjet()).isFalse();
        }
    }

    // ================================================================== getStatutClass
    @Nested
    @DisplayName("getStatutClass()")
    class GetStatutClass {

        @Test
        @DisplayName("retourne la classe CSS pour EN_COURS")
        void getStatutClass_enCours() {
            projet.setStatut(StatutProjet.EN_COURS);
            bean.setProjet(projet);
            assertThat(bean.getStatutClass()).isEqualTo("statut-en-cours");
        }

        @Test
        @DisplayName("retourne la classe CSS pour VALIDE")
        void getStatutClass_valide() {
            projet.setStatut(StatutProjet.VALIDE);
            bean.setProjet(projet);
            assertThat(bean.getStatutClass()).isEqualTo("statut-valide");
        }

        @Test
        @DisplayName("retourne la classe CSS pour TERMINE")
        void getStatutClass_termine() {
            projet.setStatut(StatutProjet.TERMINE);
            bean.setProjet(projet);
            assertThat(bean.getStatutClass()).isEqualTo("statut-termine");
        }

        @Test
        @DisplayName("retourne une chaîne vide si projet null")
        void getStatutClass_projetNull() {
            bean.setProjet(null);
            assertThat(bean.getStatutClass()).isEmpty();
        }
    }

    // ================================================================== getStatutProjetClass
    @Nested
    @DisplayName("getStatutProjetClass()")
    class GetStatutProjetClass {

        @Test void enCours()    { assertThat(bean.getStatutProjetClass(StatutProjet.EN_COURS)).isEqualTo("statut-en-cours"); }
        @Test void valide()     { assertThat(bean.getStatutProjetClass(StatutProjet.VALIDE)).isEqualTo("statut-valide");     }
        @Test void termine()    { assertThat(bean.getStatutProjetClass(StatutProjet.TERMINE)).isEqualTo("statut-termine");  }
        @Test void statutNull() { assertThat(bean.getStatutProjetClass(null)).isEmpty();                                     }
    }

    // ================================================================== formatDate
    @Nested
    @DisplayName("formatDate()")
    class FormatDate {

        @Test
        @DisplayName("formate correctement une date valide")
        void formatDate_valide() {
            LocalDateTime dt = LocalDateTime.of(2025, 1, 8, 14, 5);
            assertThat(bean.formatDate(dt)).isEqualTo("08/01/2025 14:05");
        }

        @Test
        @DisplayName("retourne une chaîne vide pour une date null")
        void formatDate_null() {
            assertThat(bean.formatDate(null)).isEmpty();
        }

        @Test
        @DisplayName("gère correctement minuit (00:00)")
        void formatDate_minuit() {
            LocalDateTime dt = LocalDateTime.of(2024, 12, 31, 0, 0);
            assertThat(bean.formatDate(dt)).isEqualTo("31/12/2024 00:00");
        }
    }
}