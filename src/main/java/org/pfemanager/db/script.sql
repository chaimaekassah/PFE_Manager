-- =========================================================
-- BASE DE DONNÉES PFE_MANAGER
-- =========================================================

CREATE DATABASE pfemanager;

-- =========================================================
-- CLEAN
-- =========================================================
DROP TABLE IF EXISTS reset_tokens CASCADE;
DROP TABLE IF EXISTS commentaires CASCADE;
DROP TABLE IF EXISTS documents CASCADE;
DROP TABLE IF EXISTS candidatures CASCADE;
DROP TABLE IF EXISTS projets CASCADE;
DROP TABLE IF EXISTS sujets CASCADE;
DROP TABLE IF EXISTS utilisateurs CASCADE;

-- =========================================================
-- UTILISATEURS (COMPATIBLE AVEC User.java)
-- =========================================================
CREATE TABLE utilisateurs (
                              id BIGSERIAL PRIMARY KEY,
                              nom VARCHAR(255) NOT NULL,
                              email VARCHAR(255) UNIQUE NOT NULL,
                              mot_de_passe VARCHAR(255) NOT NULL,
                              role VARCHAR(50) NOT NULL CHECK (
                                  role IN ('ETUDIANT', 'ENCADRANT', 'ADMINISTRATEUR')
                                  ),
                              photo VARCHAR(255)
);

-- =========================================================
-- SUJETS
-- =========================================================
CREATE TABLE sujets (
                        id BIGSERIAL PRIMARY KEY,
                        titre VARCHAR(255) NOT NULL,
                        description TEXT,
                        technologies VARCHAR(255),
                        capacite_max INTEGER DEFAULT 1,
                        encadrant_id BIGINT,
                        FOREIGN KEY (encadrant_id) REFERENCES utilisateurs(id)
);

-- =========================================================
-- PROJETS
-- =========================================================
CREATE TABLE projets (
                         id BIGSERIAL PRIMARY KEY,
                         sujet VARCHAR(255) NOT NULL,
                         description TEXT,
                         etudiant_id BIGINT,
                         encadrant_id BIGINT,
                         statut VARCHAR(50) DEFAULT 'EN_COURS',
                         date_debut TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (etudiant_id) REFERENCES utilisateurs(id),
                         FOREIGN KEY (encadrant_id) REFERENCES utilisateurs(id)
);

-- =========================================================
-- CANDIDATURES
-- =========================================================
CREATE TABLE candidatures (
                              id BIGSERIAL PRIMARY KEY,
                              etudiant_id BIGINT,
                              encadrant_id BIGINT,
                              sujet VARCHAR(255),
                              message_motivation TEXT,
                              statut VARCHAR(50) DEFAULT 'EN_ATTENTE',
                              date_candidature TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (etudiant_id) REFERENCES utilisateurs(id),
                              FOREIGN KEY (encadrant_id) REFERENCES utilisateurs(id)
);

-- =========================================================
-- DOCUMENTS
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
                           FOREIGN KEY (depositaire_id) REFERENCES utilisateurs(id),
                           FOREIGN KEY (projet_id) REFERENCES projets(id)
);

-- =========================================================
-- COMMENTAIRES
-- =========================================================
CREATE TABLE commentaires (
                              id BIGSERIAL PRIMARY KEY,
                              auteur_id BIGINT,
                              contenu TEXT,
                              date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              projet_id BIGINT,
                              FOREIGN KEY (auteur_id) REFERENCES utilisateurs(id),
                              FOREIGN KEY (projet_id) REFERENCES projets(id)
);

-- =========================================================
-- RESET TOKEN
-- =========================================================
CREATE TABLE reset_tokens (
                              id BIGSERIAL PRIMARY KEY,
                              email VARCHAR(255),
                              token VARCHAR(255),
                              date_expiration TIMESTAMP,
                              utilise BOOLEAN DEFAULT FALSE
);

-- =========================================================
-- INSERT USERS
-- =========================================================
INSERT INTO utilisateurs (nom, email, mot_de_passe, role) VALUES
                                                              ('Admin', 'admin@pfemanager.com', '$2a$10$hash', 'ADMINISTRATEUR'),
                                                              ('Ikram', 'ikram@pfemanager.com', '$2a$10$hash', 'ETUDIANT'),
                                                              ('Prof', 'prof@pfemanager.com', '$2a$10$hash', 'ENCADRANT');

-- =========================================================
-- INSERT SUJETS
-- =========================================================
INSERT INTO sujets (titre, description, technologies, capacite_max, encadrant_id)
VALUES ('Plateforme PFE', 'Gestion PFE', 'Java, JSF', 2, 3);

-- =========================================================
-- INSERT PROJETS
-- =========================================================
INSERT INTO projets (sujet, description, etudiant_id, encadrant_id)
VALUES ('Plateforme PFE', 'Projet académique', 2, 3);

-- =========================================================
-- INSERT CANDIDATURES
-- =========================================================
INSERT INTO candidatures (etudiant_id, encadrant_id, sujet, message_motivation)
VALUES (2, 3, 'Plateforme PFE', 'Motivée');

-- =========================================================
-- INSERT DOCUMENTS
-- =========================================================
INSERT INTO documents (nom_fichier, type_fichier, taille, chemin, depositaire_id, projet_id)
VALUES ('rapport.pdf', 'pdf', 1000, '/files/rapport.pdf', 2, 1);

-- =========================================================
-- INSERT COMMENTAIRES
-- =========================================================
INSERT INTO commentaires (auteur_id, contenu, projet_id)
VALUES (3, 'Bon travail', 1);

-- =========================================================
-- INSERT TOKEN
-- =========================================================
INSERT INTO reset_tokens (email, token, date_expiration)
VALUES ('ikram@pfemanager.com', 'token123', CURRENT_TIMESTAMP + INTERVAL '1 day');