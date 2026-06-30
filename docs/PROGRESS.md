# Journal de progression — Bar'app

## Jour 1 — Conception & Initialisation (29/06/2026)

### 1. Analyse des consignes
- Lecture des consignes de l'examen (`instructions/consignes.md`)
- Lecture et adoption de la méthode APEX 4.3 (`instructions/APEXmethod.md`)
- Identification des deux profils : **Client** et **Barmaker**
- Identification des flux métier : carte → panier → commande → suivi de préparation

### 2. Initialisation du projet
- Création du backend **Spring Boot 3.5** (Java 21) via Spring Initializr
  - Dépendances : Spring Web, Spring Data JPA, PostgreSQL, Validation, Lombok, DevTools
- Création du frontend **VueJS 3 + TypeScript** via create-vue
  - Stack : Vite, Vue Router, Pinia, Vitest, ESLint, Prettier
- Création de la structure Docker (`docker-compose.yml`, `Dockerfile` backend et frontend)
- Configuration Nginx pour le frontend (`nginx.conf`)
- Proxy Vite configuré (`/api` → `localhost:8080`)
- `.gitignore` racine (Java + Node + IDE)
- `README.md` avec instructions de lancement
- Dossier `.apex/` avec les 7 fichiers d'état (méthode APEX)

### 3. MCD — Modèle Conceptuel de Données
- Conception des 8 entités :

| Entité | Rôle |
|---|---|
| `utilisateur` | Client ou Barmaker (role enum) |
| `categorie` | Regroupement de cocktails |
| `cocktail` | Cocktail de la carte |
| `ingredient` | Ingrédient utilisé dans les cocktails |
| `cocktail_ingredient` | Table de jointure M:N |
| `cocktail_prix` | Prix par taille (S / M / L) |
| `commande` | Panier ou commande lancée |
| `ligne_commande` | Cocktail commandé avec étapes de préparation |

- Choix de modéliser le **panier comme une commande** avec statut `PANIER`
- Snapshot du `prix_unitaire` dans `ligne_commande` (protection contre les changements de prix)
- Création du MCD
- Script SQL d'initialisation (`backend/src/main/resources/db/init.sql`)

---

## Jour 2 — Backend Java Spring Boot (29/06/2026)

### 4. Structure du projet backend

```
com.barapp/
├── enums/        Role, Taille, StatutCommande, StatutLigneCommande
├── entity/       Utilisateur, Categorie, Cocktail, Ingredient,
│                 CocktailPrix, Commande, LigneCommande
├── repository/   1 interface JPA par entité
├── dto/
│   ├── request/  CategorieRequest, IngredientRequest, CocktailRequest,
│   │             CocktailPrixRequest, LigneCommandeRequest
│   └── response/ CategorieResponse, IngredientResponse, CocktailResponse,
│                 CocktailPrixResponse, CommandeResponse, LigneCommandeResponse
├── service/      CategorieService, IngredientService, CocktailService,
│                 CommandeService
├── controller/   CategorieController, IngredientController,
│                 CocktailController, CommandeController
├── exception/    ResourceNotFoundException, GlobalExceptionHandler
└── config/       CorsConfig
```

### 5. Logique métier implémentée
- CRUD complet pour Catégories, Ingrédients, Cocktails
- Gestion du panier : ajout, retrait, consultation
- Lancement de commande (PANIER → COMMANDEE)
- Avancement des étapes barmaker (PREPARATION_INGREDIENTS → ASSEMBLAGE → DRESSAGE → TERMINEE)
- **Règle automatique** : quand toutes les lignes sont TERMINEE → commande passe à TERMINEE
- Validation des données entrantes (`@Valid`, `@NotBlank`, `@NotNull`, etc.)
- Gestion centralisée des erreurs (`GlobalExceptionHandler`)
- CORS configuré pour `localhost:5173` (dev) et `localhost:80` (Docker)

### 6. Endpoints REST (13 routes)

| Méthode | URL | Usage |
|---|---|---|
| GET/POST | `/api/categories` | Liste / créer |
| GET/PUT/DELETE | `/api/categories/{id}` | Lire / modifier / supprimer |
| GET/POST | `/api/cocktails` | Carte / créer |
| GET/PUT/DELETE | `/api/cocktails/{id}` | Détail / modifier / supprimer |
| GET/POST | `/api/ingredients` | Liste / créer |
| GET/PUT/DELETE | `/api/ingredients/{id}` | Modifier / supprimer |
| GET | `/api/commandes` | Toutes les commandes (barmaker) |
| GET | `/api/commandes/{id}` | Détail commande |
| GET | `/api/commandes/panier/{userId}` | Panier du client |
| POST | `/api/commandes/panier/{userId}/lignes` | Ajouter au panier |
| DELETE | `/api/commandes/panier/{userId}/lignes/{ligneId}` | Retirer du panier |
| POST | `/api/commandes/panier/{userId}/lancer` | Valider la commande |
| PATCH | `/api/commandes/{commandeId}/lignes/{ligneId}/avancer` | Avancer étape (barmaker) |

### 7. Tests (JUnit + Mockito + JaCoCo)
- **36 tests** — 0 échec
- Couverture :
  - `CategorieService` : 100%
  - `IngredientService` : 100%
  - `CommandeService` : 89%
  - `CategorieController` : 100%
  - `GlobalExceptionHandler` : 83%
- Rapport JaCoCo disponible dans `backend/target/site/jacoco/`
- Base H2 in-memory pour les tests (pas de dépendance PostgreSQL en test)

### 8. Base de données & données de test
- Création de la BDD PostgreSQL locale (`barapp` / `barapp`)
- Insertion de la carte complète via l'API REST :
  - **5 catégories** : Signature, Classiques revisités, Sans alcool, Spritz & Fraîcheur, Shots
  - **33 ingrédients**
  - **13 cocktails** avec ingrédients et prix par taille

---

## Reste à faire

### Jour 3-4 — Frontend VueJS + TypeScript
- [ ] Vues client : carte, détail cocktail, panier, suivi commande
- [ ] Vues barmaker : gestion carte, liste commandes, détail commande + avancement
- [ ] Store Pinia (cocktails, commande, utilisateur courant)
- [ ] Appels API (service Axios ou fetch)
- [ ] Tests Vitest

### Jour 5 — Présentation
- [ ] Slides de présentation
- [ ] Scénario de démonstration (client + barmaker en simultané)
- [ ] Zip du projet + README finalisé
- [ ] Envoi du livrable avant le 02/07/2026 à 17h
