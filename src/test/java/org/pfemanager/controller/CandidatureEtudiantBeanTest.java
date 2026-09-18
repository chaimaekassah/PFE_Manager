package org.pfemanager.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.enums.StatutCandidature;
import org.pfemanager.model.Candidature;
import org.pfemanager.model.Sujet;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.CandidatureService;
import org.pfemanager.service.SujetService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link CandidatureEtudiantBean}.
 *
 * Dépendances : JUnit 5, Mockito, AssertJ
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CandidatureEtudiantBean – Tests unitaires")
class CandidatureEtudiantBeanTest {

    // ── Mocks ──────────────────────────────────────────────
    @Mock private CandidatureService candidatureService;
    @Mock private SujetService       sujetService;
    @Mock private AuthBean           authBean;

    // ── Objet testé ────────────────────────────────────────
    @InjectMocks
    private CandidatureEtudiantBean bean;

    // ── Fixtures ───────────────────────────────────────────
    private Utilisateur etudiant;
    private Utilisateur encadrant;
    private Sujet       sujet;
    private Candidature candidatureEnAttente;
    private Candidature candidatureAcceptee;
    private Candidature candidatureRefusee;

    @BeforeEach
    void setUp() throws Exception {
        etudiant  = buildUtilisateur(1L, "Dupont");
        encadrant = buildUtilisateur(2L, "Martin");

        sujet = new Sujet();
        sujet.setId(10L);
        sujet.setTitre("Titre du sujet");
        sujet.setEncadrant(encadrant);

        candidatureEnAttente = buildCandidature(1L, StatutCandidature.EN_ATTENTE);
        candidatureAcceptee  = buildCandidature(2L, StatutCandidature.ACCEPTEE);
        candidatureRefusee   = buildCandidature(3L, StatutCandidature.REFUSEE);
    }

    // ══════════════════════════════════════════════════════
    // init / loadCandidatures / loadSujetsDisponibles
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Chargement initial")
    class ChargementInitial {

        @Test
        @DisplayName("init() charge les candidatures et les sujets disponibles")
        void init_chargeLesDonnees() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(candidatureService.getCandidaturesByEtudiant(1L))
                    .thenReturn(List.of(candidatureEnAttente));
            when(sujetService.getSujetsDisponibles()).thenReturn(List.of(sujet));

            bean.init();

            assertThat(bean.getCandidatures()).containsExactly(candidatureEnAttente);
            assertThat(bean.getSujetsDisponibles()).containsExactly(sujet);
        }

        @Test
        @DisplayName("loadCandidatures() ne plante pas si l'utilisateur est null")
        void loadCandidatures_utilisateurNull() {
            when(authBean.getUtilisateurConnecte()).thenReturn(null);

            bean.loadCandidatures();

            assertThat(bean.getCandidatures()).isNull();
            verify(candidatureService, never()).getCandidaturesByEtudiant(anyLong());
        }

        @Test
        @DisplayName("loadSujetsDisponibles() alimente la liste des sujets")
        void loadSujetsDisponibles_alimenteLaListe() {
            when(sujetService.getSujetsDisponibles()).thenReturn(List.of(sujet));

            bean.loadSujetsDisponibles();

            assertThat(bean.getSujetsDisponibles()).containsExactly(sujet);
        }
    }

    // ══════════════════════════════════════════════════════
    // Modal formulaire
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Gestion du formulaire (modal)")
    class GestionFormulaire {

        @Test
        @DisplayName("ouvrirFormulaire() ouvre le formulaire et réinitialise les champs")
        void ouvrirFormulaire_reinitialise() {
            when(sujetService.getSujetsDisponibles()).thenReturn(List.of(sujet));
            bean.setSujetIdSelectionne(99L);
            bean.setMessageMotivation("ancien texte");

            bean.ouvrirFormulaire();

            assertThat(bean.isFormulaireOuvert()).isTrue();
            assertThat(bean.getSujetIdSelectionne()).isNull();
            assertThat(bean.getMessageMotivation()).isNull();
        }

        @Test
        @DisplayName("fermerFormulaire() ferme le formulaire et réinitialise les champs")
        void fermerFormulaire_reinitialise() {
            bean.setFormulaireOuvert(true);
            bean.setSujetIdSelectionne(10L);
            bean.setMessageMotivation("motivation");

            bean.fermerFormulaire();

            assertThat(bean.isFormulaireOuvert()).isFalse();
            assertThat(bean.getSujetIdSelectionne()).isNull();
            assertThat(bean.getMessageMotivation()).isNull();
        }
    }

    // ══════════════════════════════════════════════════════
    // Preview sujet
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Aperçu du sujet")
    class ApercuSujet {

        @Test
        @DisplayName("getSujetPreviewTitre() retourne le titre quand le sujet existe")
        void getSujetPreviewTitre_retourneTitre() {
            bean.setSujetIdSelectionne(10L);
            when(sujetService.findById(10L)).thenReturn(sujet);

            assertThat(bean.getSujetPreviewTitre()).isEqualTo("Titre du sujet");
        }

        @Test
        @DisplayName("getSujetPreviewTitre() retourne '' si sujetId est null")
        void getSujetPreviewTitre_sujetIdNull() {
            bean.setSujetIdSelectionne(null);
            assertThat(bean.getSujetPreviewTitre()).isEmpty();
        }

        @Test
        @DisplayName("getSujetPreviewTitre() retourne '' si le sujet est introuvable")
        void getSujetPreviewTitre_sujetIntrouvable() {
            bean.setSujetIdSelectionne(99L);
            when(sujetService.findById(99L)).thenReturn(null);

            assertThat(bean.getSujetPreviewTitre()).isEmpty();
        }

        @Test
        @DisplayName("getSujetPreviewEncadrant() retourne le nom de l'encadrant")
        void getSujetPreviewEncadrant_retourneNom() {
            bean.setSujetIdSelectionne(10L);
            when(sujetService.findById(10L)).thenReturn(sujet);

            assertThat(bean.getSujetPreviewEncadrant()).isEqualTo("Martin");
        }

        @Test
        @DisplayName("getSujetPreviewEncadrant() retourne '' si sujetId est null")
        void getSujetPreviewEncadrant_sujetIdNull() {
            bean.setSujetIdSelectionne(null);
            assertThat(bean.getSujetPreviewEncadrant()).isEmpty();
        }
    }

    // ══════════════════════════════════════════════════════
    // Actions : voirDetails / annuler
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Actions détail / annuler")
    class ActionsDetail {

        @Test
        @DisplayName("voirDetails() affecte la candidature sélectionnée")
        void voirDetails_affecteSelectedCandidature() {
            bean.voirDetails(candidatureEnAttente);
            assertThat(bean.getSelectedCandidature()).isEqualTo(candidatureEnAttente);
        }

        @Test
        @DisplayName("annuler() réinitialise la candidature sélectionnée")
        void annuler_reinitialiseSelectedCandidature() {
            bean.voirDetails(candidatureEnAttente);
            bean.annuler();
            assertThat(bean.getSelectedCandidature()).isNull();
        }
    }

    // ══════════════════════════════════════════════════════
    // Statistiques
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Statistiques")
    class Statistiques {

        @BeforeEach
        void chargerCandidatures() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(candidatureService.getCandidaturesByEtudiant(1L))
                    .thenReturn(Arrays.asList(
                            candidatureEnAttente,
                            candidatureAcceptee,
                            candidatureRefusee));
            when(sujetService.getSujetsDisponibles()).thenReturn(Collections.emptyList());
            bean.init();
        }

        @Test void getTotalCandidatures_retourne3()       { assertThat(bean.getTotalCandidatures()).isEqualTo(3); }
        @Test void getCandidaturesEnAttente_retourne1()   { assertThat(bean.getCandidaturesEnAttente()).isEqualTo(1); }
        @Test void getCandidaturesAcceptees_retourne1()   { assertThat(bean.getCandidaturesAcceptees()).isEqualTo(1); }
        @Test void getCandidaturesRefusees_retourne1()    { assertThat(bean.getCandidaturesRefusees()).isEqualTo(1); }

        @Test
        @DisplayName("Statistiques retournent 0 si la liste est null")
        void statistiques_listeNull() {
            // Bean sans init → liste null
            CandidatureEtudiantBean beanVide = new CandidatureEtudiantBean();
            assertThat(beanVide.getTotalCandidatures()).isZero();
            assertThat(beanVide.getCandidaturesEnAttente()).isZero();
            assertThat(beanVide.getCandidaturesAcceptees()).isZero();
            assertThat(beanVide.getCandidaturesRefusees()).isZero();
        }
    }

    // ══════════════════════════════════════════════════════
    // canRetirer
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("canRetirer")
    class CanRetirer {

        @Test void canRetirer_enAttente_retourneTrue()  { assertThat(bean.canRetirer(candidatureEnAttente)).isTrue(); }
        @Test void canRetirer_acceptee_retourneFalse()  { assertThat(bean.canRetirer(candidatureAcceptee)).isFalse(); }
        @Test void canRetirer_refusee_retourneFalse()   { assertThat(bean.canRetirer(candidatureRefusee)).isFalse(); }
        @Test void canRetirer_null_retourneFalse()      { assertThat(bean.canRetirer(null)).isFalse(); }
    }

    // ══════════════════════════════════════════════════════
    // getStatutClass
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("getStatutClass")
    class GetStatutClass {

        @Test void enAttente_retourneEnAttente() { assertThat(bean.getStatutClass(StatutCandidature.EN_ATTENTE)).isEqualTo("en_attente"); }
        @Test void acceptee_retourneAcceptee()   { assertThat(bean.getStatutClass(StatutCandidature.ACCEPTEE)).isEqualTo("acceptee"); }
        @Test void refusee_retourneRefusee()     { assertThat(bean.getStatutClass(StatutCandidature.REFUSEE)).isEqualTo("refusee"); }
        @Test void retiree_retourneRetiree()     { assertThat(bean.getStatutClass(StatutCandidature.RETIREE)).isEqualTo("retiree"); }
        @Test void null_retourneVide()           { assertThat(bean.getStatutClass(null)).isEmpty(); }
    }

    // ══════════════════════════════════════════════════════
    // formatDate
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("formatDate")
    class FormatDate {

        @Test
        @DisplayName("formatDate() formate correctement une date")
        void formatDate_formateCorrectement() {
            LocalDateTime dt = LocalDateTime.of(2025, 6, 15, 9, 30);
            assertThat(bean.formatDate(dt)).isEqualTo("15/06/2025 09:30");
        }

        @Test
        @DisplayName("formatDate() retourne '' si la date est null")
        void formatDate_null() {
            assertThat(bean.formatDate(null)).isEmpty();
        }
    }

    // ══════════════════════════════════════════════════════
    // postulerDepuisModal
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("postulerDepuisModal")
    class PostulerDepuisModal {

        @BeforeEach
        void preParer() {
            bean.setSujetIdSelectionne(10L);
            bean.setMessageMotivation("Je suis très motivé.");
        }

        @Test
        @DisplayName("Postule avec succès si toutes les conditions sont réunies")
        void postule_avecSucces() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(sujetService.findById(10L)).thenReturn(sujet);
            when(candidatureService.existeCandidature(1L, 10L)).thenReturn(false);
            when(candidatureService.getCandidaturesByEtudiant(1L))
                    .thenReturn(Collections.emptyList());

            bean.postulerDepuisModal();

            verify(candidatureService).create(any(Candidature.class));
            assertThat(bean.getMessageSucces()).contains("succès");
            assertThat(bean.getMessageErreur()).isNull();
            assertThat(bean.isFormulaireOuvert()).isFalse();
        }

        @Test
        @DisplayName("Erreur si l'utilisateur est null (session expirée)")
        void postule_utilisateurNull() {
            when(authBean.getUtilisateurConnecte()).thenReturn(null);

            bean.postulerDepuisModal();

            verify(candidatureService, never()).create(any());
            assertThat(bean.getMessageErreur()).contains("Session");
        }

        @Test
        @DisplayName("Erreur si aucun sujet n'est sélectionné")
        void postule_sujetNonSelectionne() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            bean.setSujetIdSelectionne(null);

            bean.postulerDepuisModal();

            verify(candidatureService, never()).create(any());
            assertThat(bean.getMessageErreur()).contains("sujet");
        }

        @Test
        @DisplayName("Erreur si la motivation est vide")
        void postule_motivationVide() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            bean.setMessageMotivation("   ");

            bean.postulerDepuisModal();

            verify(candidatureService, never()).create(any());
            assertThat(bean.getMessageErreur()).contains("motivation");
        }

        @Test
        @DisplayName("Erreur si la motivation est null")
        void postule_motivationNull() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            bean.setMessageMotivation(null);

            bean.postulerDepuisModal();

            verify(candidatureService, never()).create(any());
            assertThat(bean.getMessageErreur()).contains("motivation");
        }

        @Test
        @DisplayName("Erreur si le sujet est introuvable en base")
        void postule_sujetIntrouvable() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(sujetService.findById(10L)).thenReturn(null);

            bean.postulerDepuisModal();

            verify(candidatureService, never()).create(any());
            assertThat(bean.getMessageErreur()).contains("introuvable");
        }

        @Test
        @DisplayName("Erreur si l'étudiant a déjà postulé à ce sujet")
        void postule_dejaPostule() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(sujetService.findById(10L)).thenReturn(sujet);
            when(candidatureService.existeCandidature(1L, 10L)).thenReturn(true);

            bean.postulerDepuisModal();

            verify(candidatureService, never()).create(any());
            assertThat(bean.getMessageErreur()).contains("déjà postulé");
        }

        @Test
        @DisplayName("La candidature créée contient les bonnes données")
        void postule_verifieDonneesCreees() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(sujetService.findById(10L)).thenReturn(sujet);
            when(candidatureService.existeCandidature(1L, 10L)).thenReturn(false);
            when(candidatureService.getCandidaturesByEtudiant(1L))
                    .thenReturn(Collections.emptyList());

            bean.postulerDepuisModal();

            ArgumentCaptor<Candidature> captor = ArgumentCaptor.forClass(Candidature.class);
            verify(candidatureService).create(captor.capture());

            Candidature created = captor.getValue();
            assertThat(created.getEtudiant()).isEqualTo(etudiant);
            assertThat(created.getEncadrant()).isEqualTo(encadrant);
            assertThat(created.getSujet()).isEqualTo("Titre du sujet");
            assertThat(created.getMessageMotivation()).isEqualTo("Je suis très motivé.");
        }
    }

    // ══════════════════════════════════════════════════════
    // retirer
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("retirer")
    class Retirer {

        @Test
        @DisplayName("retirer() appelle le service et recharge les candidatures")
        void retirer_appelleServiceEtRecharge() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(candidatureService.getCandidaturesByEtudiant(1L))
                    .thenReturn(Collections.emptyList());
            bean.voirDetails(candidatureEnAttente);

            bean.retirer(1L);

            verify(candidatureService).retirer(1L);
            assertThat(bean.getMessageSucces()).contains("retirée");
            assertThat(bean.getSelectedCandidature()).isNull();
        }

        @Test
        @DisplayName("retirer() efface tout message d'erreur précédent")
        void retirer_effaceMessageErreur() {
            when(authBean.getUtilisateurConnecte()).thenReturn(etudiant);
            when(candidatureService.getCandidaturesByEtudiant(1L))
                    .thenReturn(Collections.emptyList());

            bean.retirer(1L);

            assertThat(bean.getMessageErreur()).isNull();
        }
    }

    // ══════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════

    private Utilisateur buildUtilisateur(Long id, String nom) {
        Utilisateur u = new Utilisateur();
        u.setId(id);
        u.setNom(nom);
        return u;
    }

    private Candidature buildCandidature(Long id, StatutCandidature statut) {
        Candidature c = new Candidature();
        c.setId(id);
        c.setStatut(statut);
        c.setEtudiant(etudiant);
        return c;
    }
}
