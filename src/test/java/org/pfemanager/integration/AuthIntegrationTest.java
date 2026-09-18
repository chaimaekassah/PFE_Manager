package org.pfemanager.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.pfemanager.dao.UtilisateurDAO;
import org.pfemanager.model.Role;
import org.pfemanager.model.Utilisateur;
import org.pfemanager.service.AuthService;
import org.pfemanager.util.PasswordUtil;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private AuthService authService;
    private UtilisateurDAO utilisateurDAO;

    @BeforeAll
    void init() {
        // On initialise l'EntityManager avec l'unité de test
        emf = Persistence.createEntityManagerFactory("pfe_manager_pu_test");
    }

    @BeforeEach
    void setUp() throws Exception {
        em = emf.createEntityManager();
        utilisateurDAO = new UtilisateurDAO();
        authService = new AuthService();

        // Injection manuelle (car pas de serveur CDI ici)
        setInternalState(utilisateurDAO, "em", em);
        setInternalState(authService, "utilisateurDAO", utilisateurDAO);

        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); // On annule pour garder la base propre
        }
        em.close();
    }

    @AfterAll
    void close() {
        emf.close();
    }

    @Test
    @DisplayName("Scénario complet : Inscription puis Connexion")
    void testFullAuthFlow() {
        // 1. Inscription
        Utilisateur user = new Utilisateur();
        user.setNom("Test Integration");
        user.setEmail("integration@test.com");
        user.setMotDePasse("password123");
        user.setRole(Role.ETUDIANT);

        boolean inscrit = authService.inscrire(user);
        assertTrue(inscrit, "L'inscription devrait réussir");

        // On force l'écriture en base
        em.flush();

        // 2. Connexion (Succès)
        Utilisateur connecte = authService.authentifier("integration@test.com", "password123");
        assertNotNull(connecte, "L'authentification devrait réussir");
        assertEquals("Test Integration", connecte.getNom());

        // 3. Connexion (Échec mauvais mot de passe)
        Utilisateur echec = authService.authentifier("integration@test.com", "wrong_pass");
        assertNull(echec, "L'authentification devrait échouer avec un mauvais mot de passe");
    }

    @Test
    @DisplayName("Échec d'inscription si email déjà présent")
    void testRegisterDuplicateEmail() {
        // 1. On crée le premier utilisateur proprement
        Utilisateur u1 = new Utilisateur();
        u1.setNom("Utilisateur Test 1"); // <-- IL MANQUAIT ÇA
        u1.setEmail("double@test.com");
        u1.setMotDePasse("pass123");
        u1.setRole(org.pfemanager.model.Role.ETUDIANT); // <-- ET ÇA

        authService.inscrire(u1);
        em.flush();

        // 2. On tente d'en créer un deuxième avec le même email
        Utilisateur u2 = new Utilisateur();
        u2.setNom("Utilisateur Test 2");
        u2.setEmail("double@test.com");
        u2.setMotDePasse("autre123");
        u2.setRole(org.pfemanager.model.Role.ETUDIANT);

        boolean inscrit = authService.inscrire(u2);
        assertFalse(inscrit, "L'inscription devrait échouer car l'email existe déjà");
    }

    // Utilitaire pour injecter l'EM sans CDI
    private void setInternalState(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}