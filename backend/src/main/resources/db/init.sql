-- Burnout Bar — Script d'initialisation de la base de données

CREATE TABLE utilisateur (
    id            BIGSERIAL PRIMARY KEY,
    nom           VARCHAR(100) NOT NULL,
    prenom        VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL CHECK (role IN ('CLIENT', 'BARMAKER'))
);

CREATE TABLE categorie (
    id   BIGSERIAL PRIMARY KEY,
    nom  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE cocktail (
    id           BIGSERIAL PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    description  TEXT,
    image_url    VARCHAR(500),
    categorie_id BIGINT  NOT NULL REFERENCES categorie(id) ON DELETE RESTRICT,
    disponible   BOOLEAN NOT NULL DEFAULT TRUE,
    ordre        INT     NOT NULL DEFAULT 999
);

CREATE TABLE ingredient (
    id   BIGSERIAL PRIMARY KEY,
    nom  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE cocktail_ingredient (
    cocktail_id   BIGINT NOT NULL REFERENCES cocktail(id)   ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredient(id) ON DELETE RESTRICT,
    PRIMARY KEY (cocktail_id, ingredient_id)
);

CREATE TABLE cocktail_prix (
    id          BIGSERIAL PRIMARY KEY,
    cocktail_id BIGINT         NOT NULL REFERENCES cocktail(id) ON DELETE CASCADE,
    taille      VARCHAR(1)     NOT NULL CHECK (taille IN ('S', 'M', 'L')),
    prix        DECIMAL(10, 2) NOT NULL CHECK (prix > 0),
    UNIQUE (cocktail_id, taille)
);

CREATE TABLE commande (
    id               BIGSERIAL PRIMARY KEY,
    utilisateur_id   BIGINT      NOT NULL REFERENCES utilisateur(id) ON DELETE RESTRICT,
    statut           VARCHAR(20) NOT NULL CHECK (statut IN ('PANIER', 'COMMANDEE', 'EN_COURS', 'TERMINEE')),
    date_creation    TIMESTAMP   NOT NULL DEFAULT NOW(),
    date_mise_a_jour TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE ligne_commande (
    id            BIGSERIAL PRIMARY KEY,
    commande_id   BIGINT         NOT NULL REFERENCES commande(id)  ON DELETE CASCADE,
    cocktail_id   BIGINT         NOT NULL REFERENCES cocktail(id)  ON DELETE RESTRICT,
    taille        VARCHAR(1)     NOT NULL CHECK (taille IN ('S', 'M', 'L')),
    prix_unitaire DECIMAL(10,2)  NOT NULL CHECK (prix_unitaire > 0),
    statut        VARCHAR(30)    NOT NULL DEFAULT 'PREPARATION_INGREDIENTS'
                                          CHECK (statut IN ('PREPARATION_INGREDIENTS', 'ASSEMBLAGE', 'DRESSAGE', 'TERMINEE'))
);

-- Données de test
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
    ('Dupont', 'Alice', 'alice@bar.com', 'password', 'CLIENT'),
    ('Martin', 'Bob',   'bob@bar.com',   'password', 'BARMAKER');

INSERT INTO categorie (nom) VALUES ('Classiques'), ('Tropicaux'), ('Sans alcool');

INSERT INTO cocktail (nom, description, categorie_id, disponible, ordre) VALUES
    ('Mojito',      'Rhum, menthe, citron vert, sucre de canne, eau gazeuse', 1, TRUE, 1),
    ('Margarita',   'Tequila, triple sec, jus de citron vert',                1, TRUE, 2),
    ('Pina Colada', 'Rhum blanc, lait de coco, jus d ananas',                 2, TRUE, 3);

INSERT INTO ingredient (nom) VALUES
    ('Rhum'), ('Menthe'), ('Citron vert'), ('Sucre de canne'), ('Eau gazeuse'),
    ('Tequila'), ('Triple sec'), ('Lait de coco'), ('Jus d ananas');

INSERT INTO cocktail_ingredient (cocktail_id, ingredient_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 6), (2, 7), (2, 3),
    (3, 1), (3, 8), (3, 9);

INSERT INTO cocktail_prix (cocktail_id, taille, prix) VALUES
    (1, 'S', 7.50),  (1, 'M', 9.00),  (1, 'L', 11.00),
    (2, 'S', 8.00),  (2, 'M', 10.00), (2, 'L', 12.00),
    (3, 'S', 7.00),  (3, 'M', 9.50),  (3, 'L', 11.50);
