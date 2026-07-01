-- ============================================================
--  BURNOUT BAR — Requêtes de vérification PostgreSQL
-- ============================================================

  -- Commandes utiles à retenir :

  -- # Se connecter (base par défaut)
  -- psql postgres

  -- # Se connecter à une base spécifique
  -- psql barapp

  -- # Démarrer / arrêter / redémarrer
  -- brew services start postgresql@18
  -- brew services stop postgresql@18
  -- brew services restart postgresql@18

  -- Dans le shell psql :
  -- \l          -- lister les bases
  -- \c barapp   -- changer de base
  -- \dt         -- lister les tables
  -- \q          -- quitter


-- ------------------------------------------------------------
--  UTILISATEURS
-- ------------------------------------------------------------

-- Tous les utilisateurs (mot de passe masqué)
SELECT id, prenom, nom, email, role,
       LEFT(mot_de_passe, 7) || '...' AS mot_de_passe_hash
FROM utilisateur
ORDER BY role, id;

-- Compter par rôle
SELECT role, COUNT(*) AS total
FROM utilisateur
GROUP BY role;

-- Vérifier que tous les barmakers ont un mot de passe hashé (BCrypt commence par $2a$)
SELECT id, prenom, email,
       CASE WHEN mot_de_passe LIKE '$2a$%' THEN 'OK (BCrypt)'
            WHEN mot_de_passe = ''         THEN 'vide (client anonyme)'
            ELSE 'PROBLEME : clair !'
       END AS statut_hash
FROM utilisateur
ORDER BY role;

-- Clients créés aujourd'hui
SELECT id, prenom, email
FROM utilisateur
WHERE role = 'CLIENT'
  AND email LIKE 'client_%@burnoutbar.fr'
ORDER BY id DESC;


-- ------------------------------------------------------------
--  CATALOGUE
-- ------------------------------------------------------------

-- Toutes les catégories
SELECT id, nom FROM categorie ORDER BY id;

-- Tous les cocktails avec leur catégorie, disponibilité et ordre d'affichage
SELECT c.id, c.nom, cat.nom AS categorie, c.disponible, c.ordre
FROM cocktail c
JOIN categorie cat ON cat.id = c.categorie_id
ORDER BY c.ordre ASC, c.id ASC;

-- Cocktails en pause
SELECT c.id, c.nom, cat.nom AS categorie
FROM cocktail c
JOIN categorie cat ON cat.id = c.categorie_id
WHERE c.disponible = FALSE;

-- Cocktails sans prix définis
SELECT c.id, c.nom
FROM cocktail c
LEFT JOIN cocktail_prix cp ON cp.cocktail_id = c.id
WHERE cp.id IS NULL;

-- Prix de chaque cocktail
SELECT c.nom, cp.taille, cp.prix
FROM cocktail_prix cp
JOIN cocktail c ON c.id = cp.cocktail_id
ORDER BY c.nom, cp.taille;

-- Ingrédients de chaque cocktail
SELECT c.nom AS cocktail, STRING_AGG(i.nom, ', ' ORDER BY i.nom) AS ingredients
FROM cocktail c
JOIN cocktail_ingredient ci ON ci.cocktail_id = c.id
JOIN ingredient i ON i.id = ci.ingredient_id
GROUP BY c.id, c.nom
ORDER BY c.nom;

-- Tous les ingrédients
SELECT id, nom FROM ingredient ORDER BY nom;

-- Ingrédients non utilisés dans aucun cocktail
SELECT i.id, i.nom
FROM ingredient i
LEFT JOIN cocktail_ingredient ci ON ci.ingredient_id = i.id
WHERE ci.cocktail_id IS NULL;


-- ------------------------------------------------------------
--  COMMANDES
-- ------------------------------------------------------------

-- Toutes les commandes (hors paniers) avec le prénom du client
SELECT co.id, u.prenom, co.statut, co.date_creation,
       COUNT(lc.id) AS nb_lignes,
       COALESCE(SUM(lc.prix_unitaire), 0) AS total
FROM commande co
JOIN utilisateur u ON u.id = co.utilisateur_id
LEFT JOIN ligne_commande lc ON lc.commande_id = co.id
WHERE co.statut <> 'PANIER'
GROUP BY co.id, u.prenom, co.statut, co.date_creation
ORDER BY co.date_creation DESC;

-- Paniers en cours (non encore commandés)
SELECT co.id, u.prenom, COUNT(lc.id) AS nb_articles
FROM commande co
JOIN utilisateur u ON u.id = co.utilisateur_id
LEFT JOIN ligne_commande lc ON lc.commande_id = co.id
WHERE co.statut = 'PANIER'
GROUP BY co.id, u.prenom
ORDER BY co.id DESC;

-- Détail d'une commande spécifique (remplacer 1 par l'id voulu)
SELECT lc.id, cock.nom AS cocktail, lc.taille, lc.prix_unitaire, lc.statut
FROM ligne_commande lc
JOIN cocktail cock ON cock.id = lc.cocktail_id
WHERE lc.commande_id = 1
ORDER BY lc.id;

-- Commandes en cours de préparation
SELECT co.id, u.prenom, co.statut, co.date_creation
FROM commande co
JOIN utilisateur u ON u.id = co.utilisateur_id
WHERE co.statut IN ('COMMANDEE', 'EN_COURS')
ORDER BY co.date_creation ASC;

-- Statistiques : nombre de commandes par statut
SELECT statut, COUNT(*) AS total
FROM commande
GROUP BY statut
ORDER BY total DESC;

-- Cocktails les plus commandés (toutes commandes hors paniers)
SELECT cock.nom, COUNT(*) AS fois_commande
FROM ligne_commande lc
JOIN cocktail cock ON cock.id = lc.cocktail_id
JOIN commande co ON co.id = lc.commande_id
WHERE co.statut <> 'PANIER'
GROUP BY cock.id, cock.nom
ORDER BY fois_commande DESC;

-- Chiffre d'affaires total (commandes terminées uniquement)
SELECT COALESCE(SUM(lc.prix_unitaire), 0) AS chiffre_affaires
FROM ligne_commande lc
JOIN commande co ON co.id = lc.commande_id
WHERE co.statut = 'TERMINEE';
