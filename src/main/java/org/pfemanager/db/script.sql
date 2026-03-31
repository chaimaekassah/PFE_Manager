-- =========================================================
-- CREATION DE LA BASE
-- =========================================================
CREATE DATABASE pfemanager;

-- Se connecter à la base (psql)
-- \c pfemanager

-- =========================================================
-- SUPPRESSION DES TABLES
-- =========================================================
DROP TABLE IF EXISTS commentaires CASCADE;
DROP TABLE IF EXISTS documents CASCADE;
DROP TABLE IF EXISTS candidatures CASCADE;
DROP TABLE IF EXISTS projets CASCADE;
DROP TABLE IF EXISTS utilisateurs CASCADE;

CREATE TABLE utilisateurs (
                              id BIGSERIAL PRIMARY KEY,
                              nom VARCHAR(255) NOT NULL,
                              prenom VARCHAR(255),
                              email VARCHAR(255) UNIQUE NOT NULL,
                              mot_de_passe VARCHAR(255),
                              role VARCHAR(50) NOT NULL CHECK (role IN ('ETUDIANT', 'ENCADRANT', 'ADMINISTRATEUR')),
                              statut VARCHAR(50) NOT NULL CHECK (statut IN ('ACTIF', 'INACTIF', 'SUSPENDU')),
                              date_creation TIMESTAMP,
                              date_modification TIMESTAMP,
                              photo VARCHAR(255)
);

INSERT INTO utilisateurs
(nom, prenom, email, mot_de_passe, role, statut, date_creation, date_modification, photo)
VALUES
    ('Admin', 'System', 'admin@pfemanager.com', 'admin123', 'ADMINISTRATEUR', 'ACTIF', CURRENT_TIMESTAMP, NULL, NULL),
    ('Malki', 'Ikram', 'ikram@pfemanager.com', '123456', 'ETUDIANT', 'ACTIF', CURRENT_TIMESTAMP, NULL, NULL),
    ('Professeur', 'A', 'prof@pfemanager.com', 'prof123', 'ENCADRANT', 'ACTIF', CURRENT_TIMESTAMP, NULL, NULL);

-- =========================================================
-- TABLE : projets
-- =========================================================
CREATE TABLE projets (
                         id BIGSERIAL PRIMARY KEY,
                         sujet VARCHAR(255) NOT NULL,
                         description TEXT,
                         etudiant_id BIGINT,
                         encadrant_id BIGINT,
                         statut VARCHAR(50) DEFAULT 'EN_COURS',
                         date_debut TIMESTAMP,
                         date_fin TIMESTAMP,

                         FOREIGN KEY (etudiant_id) REFERENCES utilisateurs(id) ON DELETE SET NULL,
                         FOREIGN KEY (encadrant_id) REFERENCES utilisateurs(id) ON DELETE SET NULL
);

-- =========================================================
-- TABLE : candidatures
-- =========================================================
CREATE TABLE candidatures (
                              id BIGSERIAL PRIMARY KEY,
                              etudiant_id BIGINT NOT NULL,
                              encadrant_id BIGINT,
                              sujet VARCHAR(255) NOT NULL,
                              message_motivation TEXT,
                              statut VARCHAR(50) DEFAULT 'EN_ATTENTE',
                              remarque_encadrant TEXT,
                              date_candidature TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              date_reponse TIMESTAMP,

                              FOREIGN KEY (etudiant_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
                              FOREIGN KEY (encadrant_id) REFERENCES utilisateurs(id) ON DELETE SET NULL
);

-- =========================================================
-- TABLE : documents
-- =========================================================
CREATE TABLE documents (
                           id BIGSERIAL PRIMARY KEY,
                           nom_fichier VARCHAR(255),
                           type_fichier VARCHAR(100),
                           taille BIGINT,
                           chemin VARCHAR(500),
                           date_depot TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           depositaire_id BIGINT,
                           projet_id BIGINT,

                           FOREIGN KEY (depositaire_id) REFERENCES utilisateurs(id) ON DELETE SET NULL,
                           FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE
);

-- =========================================================
-- TABLE : commentaires
-- =========================================================
CREATE TABLE commentaires (
                              id BIGSERIAL PRIMARY KEY,
                              auteur_id BIGINT,
                              contenu TEXT NOT NULL,
                              date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              projet_id BIGINT,

                              FOREIGN KEY (auteur_id) REFERENCES utilisateurs(id) ON DELETE SET NULL,
                              FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE
);

-- =========================================================
-- DONNEES DE TEST
-- =========================================================

INSERT INTO utilisateurs (nom, email, mot_de_passe, role, photo)
VALUES
    ('Admin', 'admin@pfemanager.com', 'admin123', 'ADMINISTRATEUR', NULL),
    ('Ikram', 'ikram@pfemanager.com', '123456', 'ETUDIANT', NULL),
    ('Professeur A', 'prof@pfemanager.com', 'prof123', 'ENCADRANT', NULL);

INSERT INTO projets (sujet, description, etudiant_id, encadrant_id, statut, date_debut)
VALUES
    (
        'Plateforme de gestion des PFE',
        'Application web pour gérer les PFE',
        2,
        3,
        'EN_COURS',
        CURRENT_TIMESTAMP
    );

INSERT INTO candidatures (etudiant_id, encadrant_id, sujet, message_motivation)
VALUES
    (
        2,
        3,
        'Plateforme de gestion des PFE',
        'Je suis motivée pour travailler sur ce projet.'
    );

INSERT INTO documents (nom_fichier, type_fichier, taille, chemin, depositaire_id, projet_id)
VALUES
    (
        'cahier_des_charges.pdf',
        'application/pdf',
        245760,
        '/uploads/cahier.pdf',
        2,
        1
    );

INSERT INTO commentaires (auteur_id, contenu, projet_id)
VALUES
    (
        3,
        'Bon début de projet, continuez.',
        1
    );