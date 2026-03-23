-- 1. Création de la base de données
CREATE DATABASE pfe_manager_db;

-- 2. Table parente : Utilisateurs (pour l'authentification)
CREATE TABLE utilisateurs (
                              id SERIAL PRIMARY KEY,
                              nom VARCHAR(100) NOT NULL,
                              email VARCHAR(150) UNIQUE NOT NULL,
                              mot_de_passe VARCHAR(255) NOT NULL,
                              role VARCHAR(20) NOT NULL -- Stockera 'ETUDIANTE', 'ENCADRANTE' ou 'ADMINISTRATRICE'
);

-- 3. Table fille : Etudiantes (Héritage JOINED)
CREATE TABLE etudiants (
                           id INTEGER PRIMARY KEY REFERENCES utilisateurs(id) ON DELETE CASCADE,
                           cne_apogee VARCHAR(20) UNIQUE, -- Identifiant spécifique [cite: 324]
                           filiere VARCHAR(100),
                           moyenne_academique FLOAT -- Pour faciliter la sélection [cite: 324]
);

-- 4. Table fille : Encadrantes (Héritage JOINED)
CREATE TABLE encadrants (
                            id INTEGER PRIMARY KEY REFERENCES utilisateurs(id) ON DELETE CASCADE,
                            specialite VARCHAR(100), -- Spécialité académique [cite: 323]
                            departement VARCHAR(100)
);

-- 5. Table : Sujets de PFE
CREATE TABLE sujets (
                        id SERIAL PRIMARY KEY,
                        titre VARCHAR(200) NOT NULL,
                        description TEXT,
                        technologies VARCHAR(255),
                        capacite_max INTEGER DEFAULT 1, -- Capacité d'étudiantes autorisées [cite: 325]
                        id_encadrant INTEGER REFERENCES encadrants(id)
);


INSERT INTO utilisateurs (nom, email, mot_de_passe, role)
VALUES ('Kassah Chaimae', 'admin@university.edu', 'admin123', 'ADMINISTRATRICE');