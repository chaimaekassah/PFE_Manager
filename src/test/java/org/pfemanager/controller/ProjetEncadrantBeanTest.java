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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires – ProjetEncadrantBean")
class ProjetEncadrantBeanTest {

    // ------------------------------------------------------------------ mocks
    @Mock private ProjetService    projetService;
    @Mock private DocumentService  documentService;
    @Mock private AuthBean         authBean;
    @Mock private FacesContext     facesContext;

    @InjectMocks
    private ProjetEncadrantBean bean;

    // ------------------------------------------------------------------ fixtures
    private Utilisateur encadrant;
    private Projet      projet1;
    private Projet      projet2;

    // ------------------------------------------------------------------ setup
    @BeforeEach
    void setUp() {
        encadrant = new Utilisateur();
        encadrant.setId(1L);
        encadrant.setNom("Dupont");

        projet1 = new Projet();
        projet1.setId(10L);
        projet1.setStatut(StatutProjet.EN_COURS);

        projet2 = new Projet();
        projet2.setId(20L);
        projet2.setStatut(StatutProjet.VALIDE);

        // Inject static FacesContext (nécessaire pour addMessage)
        try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
            mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        }
    }

    // ================================================================== loadProjets
    @Nested
    @DisplayName("loadProjets()")
    class LoadProjets {

        @Test
        @DisplayName("charge les projets quand l'utilisateur est connecté")
        void loadProjets_utilisateurConnecte() {
            when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
            when(projetService.getProjetsByEncadrant(1L))
                    .thenReturn(Arrays.asList(projet1, projet2));

            bean.loadProjets();

            assertThat(bean.getProjets()).hasSize(2).contains(projet1, projet2);
            verify(projetService).getProjetsByEncadrant(1L);
        }

        @Test
        @DisplayName("ne charge rien si aucun utilisateur connecté")
        void loadProjets_aucunUtilisateur() {
            when(authBean.getUtilisateurConnecte()).thenReturn(null);

            bean.loadProjets();

            assertThat(bean.getProjets()).isNull();
            verifyNoInteractions(projetService);
        }
    }

    // ================================================================== selectionnerProjet
    @Nested
    @DisplayName("selectionnerProjet()")
    class SelectionnerProjet {

        @Test
        @DisplayName("affecte le projet sélectionné et charge ses détails")
        void selectionnerProjet_chargeDetails() {
            List<Commentaire> comms = Collections.singletonList(new Commentaire());
            List<Document>    docs  = Collections.singletonList(new Document());

            when(projetService.getCommentaires(10L)).thenReturn(comms);
            when(documentService.getDocumentsByProjet(10L)).thenReturn(docs);

            bean.selectionnerProjet(projet1);

            assertThat(bean.getSelectedProjet()).isEqualTo(projet1);
            assertThat(bean.getNouveauStatut()).isEqualTo(StatutProjet.EN_COURS);
            assertThat(bean.getCommentaires()).isEqualTo(comms);
            assertThat(bean.getDocuments()).isEqualTo(docs);
        }
    }

    // ================================================================== changerStatut
    @Nested
    @DisplayName("changerStatut()")
    class ChangerStatut {

        @Test
        @DisplayName("change le statut et recharge le projet")
        void changerStatut_succes() {
            bean.setSelectedProjet(projet1);
            bean.setNouveauStatut(StatutProjet.TERMINE);

            Projet projetMaj = new Projet();
            projetMaj.setId(10L);
            projetMaj.setStatut(StatutProjet.TERMINE);

            when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
            when(projetService.getProjetsByEncadrant(anyLong()))
                    .thenReturn(List.of(projetMaj));
            when(projetService.findById(10L)).thenReturn(Optional.of(projetMaj));
            when(projetService.getCommentaires(10L)).thenReturn(Collections.emptyList());
            when(documentService.getDocumentsByProjet(10L)).thenReturn(Collections.emptyList());

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

                bean.changerStatut();
            }

            verify(projetService).changerStatut(10L, StatutProjet.TERMINE);
            assertThat(bean.getSelectedProjet().getStatut()).isEqualTo(StatutProjet.TERMINE);
            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_INFO
            ));
        }

        @Test
        @DisplayName("ne fait rien si aucun projet sélectionné")
        void changerStatut_aucunProjet() {
            bean.setSelectedProjet(null);
            bean.setNouveauStatut(StatutProjet.TERMINE);

            bean.changerStatut();

            verifyNoInteractions(projetService);
        }

        @Test
        @DisplayName("ne fait rien si nouveauStatut est null")
        void changerStatut_statutNull() {
            bean.setSelectedProjet(projet1);
            bean.setNouveauStatut(null);

            bean.changerStatut();

            verify(projetService, never()).changerStatut(anyLong(), any());
        }
    }

    // ================================================================== ajouterCommentaire
    @Nested
    @DisplayName("ajouterCommentaire()")
    class AjouterCommentaire {

        @Test
        @DisplayName("ajoute un commentaire valide")
        void ajouterCommentaire_succes() {
            bean.setSelectedProjet(projet1);
            bean.setNouveauCommentaire("Super travail !");
            when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);
            when(projetService.getCommentaires(10L)).thenReturn(Collections.emptyList());
            when(documentService.getDocumentsByProjet(10L)).thenReturn(Collections.emptyList());

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

                bean.ajouterCommentaire();
            }

            verify(projetService).ajouterCommentaire(10L, encadrant, "Super travail !");
            assertThat(bean.getNouveauCommentaire()).isNull();
            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_INFO
            ));
        }

        @Test
        @DisplayName("affiche une erreur si commentaire vide")
        void ajouterCommentaire_commentaireVide() {
            bean.setSelectedProjet(projet1);
            bean.setNouveauCommentaire("   ");
            when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

                bean.ajouterCommentaire();
            }

            verify(projetService, never()).ajouterCommentaire(anyLong(), any(), any());
            verify(facesContext).addMessage(eq(null), argThat(
                    m -> m.getSeverity() == FacesMessage.SEVERITY_ERROR
            ));
        }

        @Test
        @DisplayName("affiche une erreur si commentaire null")
        void ajouterCommentaire_commentaireNull() {
            bean.setSelectedProjet(projet1);
            bean.setNouveauCommentaire(null);
            when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

                bean.ajouterCommentaire();
            }

            verify(projetService, never()).ajouterCommentaire(anyLong(), any(), any());
        }

        @Test
        @DisplayName("ne fait rien si utilisateur non connecté")
        void ajouterCommentaire_utilisateurNull() {
            bean.setSelectedProjet(projet1);
            bean.setNouveauCommentaire("Commentaire");
            when(authBean.getUtilisateurConnecte()).thenReturn(null);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

                bean.ajouterCommentaire();
            }

            verify(projetService, never()).ajouterCommentaire(anyLong(), any(), any());
        }

        @Test
        @DisplayName("ne fait rien si projet non sélectionné")
        void ajouterCommentaire_aucunProjet() {
            bean.setSelectedProjet(null);
            bean.setNouveauCommentaire("Commentaire");
            when(authBean.getUtilisateurConnecte()).thenReturn(encadrant);

            try (MockedStatic<FacesContext> mfc = mockStatic(FacesContext.class)) {
                mfc.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

                bean.ajouterCommentaire();
            }

            verify(projetService, never()).ajouterCommentaire(anyLong(), any(), any());
        }
    }

    // ================================================================== retourListe
    @Nested
    @DisplayName("retourListe()")
    class RetourListe {

        @Test
        @DisplayName("réinitialise toutes les données de sélection")
        void retourListe_reinitialise() {
            bean.setSelectedProjet(projet1);
            bean.setNouveauStatut(StatutProjet.EN_COURS);
            bean.setNouveauCommentaire("texte");
            bean.setCommentaires(List.of(new Commentaire()));
            bean.setDocuments(List.of(new Document()));

            bean.retourListe();

            assertThat(bean.getSelectedProjet()).isNull();
            assertThat(bean.getNouveauStatut()).isNull();
            assertThat(bean.getNouveauCommentaire()).isNull();
            assertThat(bean.getCommentaires()).isNull();
            assertThat(bean.getDocuments()).isNull();
        }
    }

    // ================================================================== getProjetsEnCours
    @Nested
    @DisplayName("getProjetsEnCours()")
    class GetProjetsEnCours {

        @Test
        @DisplayName("compte les projets EN_COURS correctement")
        void getProjetsEnCours_comptage() {
            Projet p3 = new Projet(); p3.setStatut(StatutProjet.EN_COURS);
            bean.setProjets(Arrays.asList(projet1, projet2, p3));

            assertThat(bean.getProjetsEnCours()).isEqualTo(2);
        }

        @Test
        @DisplayName("retourne 0 si la liste est null")
        void getProjetsEnCours_listeNull() {
            bean.setProjets(null);
            assertThat(bean.getProjetsEnCours()).isZero();
        }

        @Test
        @DisplayName("retourne 0 si aucun projet EN_COURS")
        void getProjetsEnCours_aucunEnCours() {
            bean.setProjets(List.of(projet2)); // VALIDE
            assertThat(bean.getProjetsEnCours()).isZero();
        }
    }

    // ================================================================== getStatutProjetClass
    @Nested
    @DisplayName("getStatutProjetClass()")
    class GetStatutProjetClass {

        @Test void enCours()   { assertThat(bean.getStatutProjetClass(StatutProjet.EN_COURS)).isEqualTo("statut-en-cours"); }
        @Test void valide()    { assertThat(bean.getStatutProjetClass(StatutProjet.VALIDE)).isEqualTo("statut-valide");     }
        @Test void termine()   { assertThat(bean.getStatutProjetClass(StatutProjet.TERMINE)).isEqualTo("statut-termine");  }
        @Test void statutNull(){ assertThat(bean.getStatutProjetClass(null)).isEmpty();                                     }
    }

    // ================================================================== formatDate
    @Nested
    @DisplayName("formatDate()")
    class FormatDate {

        @Test
        @DisplayName("formate correctement une date")
        void formatDate_valide() {
            LocalDateTime dt = LocalDateTime.of(2024, 6, 15, 9, 30);
            assertThat(bean.formatDate(dt)).isEqualTo("15/06/2024 09:30");
        }

        @Test
        @DisplayName("retourne une chaîne vide si date null")
        void formatDate_null() {
            assertThat(bean.formatDate(null)).isEmpty();
        }
    }

    // ================================================================== getAllStatuts
    @Nested
    @DisplayName("getAllStatuts()")
    class GetAllStatuts {

        @Test
        @DisplayName("retourne tous les statuts disponibles")
        void getAllStatuts_retourneValeurs() {
            StatutProjet[] statuts = bean.getAllStatuts();
            assertThat(statuts).containsExactlyInAnyOrder(StatutProjet.values());
        }
    }
}
