
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

