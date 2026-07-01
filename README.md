# Burnout Bar

Application web de commande et suivi de cocktails en temps réel, sur le thème racing PS2 Burnout.

## Stack

- **Backend** : Java 21 · Spring Boot 3.5 · PostgreSQL 16
- **Frontend** : Vue 3 · TypeScript · Vite · Pinia

## Rôles utilisateurs

| Rôle | Accès |
|---|---|
| **Client** | Consulter la carte, composer un panier, lancer une commande, suivre la préparation en temps réel (PitTracker) |
| **Barmaker** | Gérer les commandes, avancer les étapes de préparation, administrer la carte (cocktails, catégories, ingrédients, disponibilité) |

---

## Lancer avec Docker (recommandé)

Prérequis : [Docker Desktop](https://www.docker.com/products/docker-desktop) installé et démarré.

```bash
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost |
| Backend API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |

La base de données est initialisée automatiquement depuis `backend/src/main/resources/db/init.sql`.

Pour tout stopper :
```bash
docker compose down
```

Pour stopper et repartir d'une base vide :
```bash
docker compose down -v
```

---

## Lancer en local (sans Docker)

### Prérequis

- Java 21+
- Maven 3.9+
- Node 20+
- PostgreSQL 16

### 1. Base de données

```bash
psql -U postgres -c "CREATE USER barapp WITH PASSWORD 'barapp';"
psql -U postgres -c "CREATE DATABASE barapp OWNER barapp;"
psql -U barapp -d barapp -f backend/src/main/resources/db/init.sql
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

API disponible sur `http://localhost:8080`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Interface disponible sur `http://localhost:5173`

---

## API Endpoints

Base URL : `http://localhost:8080`

### Authentification
| Méthode | URL | Description |
|---|---|---|
| POST | `/api/auth/register` | Créer un compte |
| POST | `/api/auth/login` | Se connecter |

### Catégories
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/categories` | Lister toutes les catégories |
| POST | `/api/categories` | Créer une catégorie |
| DELETE | `/api/categories/{id}` | Supprimer une catégorie |

### Cocktails
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/cocktails` | Lister tous les cocktails (triés par ordre) |
| GET | `/api/cocktails/{id}` | Détail d'un cocktail |
| POST | `/api/cocktails` | Créer un cocktail |
| PUT | `/api/cocktails/{id}` | Modifier un cocktail |
| PATCH | `/api/cocktails/{id}/disponibilite` | Basculer la disponibilité (pause/reprise) |
| DELETE | `/api/cocktails/{id}` | Supprimer un cocktail |

### Ingrédients
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/ingredients` | Lister tous les ingrédients |
| POST | `/api/ingredients` | Créer un ingrédient |
| DELETE | `/api/ingredients/{id}` | Supprimer un ingrédient |

### Commandes
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/commandes` | Lister toutes les commandes (barmaker) |
| GET | `/api/commandes/{id}` | Détail d'une commande |
| GET | `/api/commandes/panier/{utilisateurId}` | Récupérer le panier du client |
| POST | `/api/commandes/panier/{utilisateurId}/lignes` | Ajouter un cocktail au panier |
| DELETE | `/api/commandes/panier/{utilisateurId}/lignes/{ligneId}` | Retirer un cocktail du panier |
| POST | `/api/commandes/panier/{utilisateurId}/lancer` | Valider et lancer la commande |
| PATCH | `/api/commandes/{commandeId}/lignes/{ligneId}/avancer` | Avancer l'étape d'un cocktail (barmaker) |

---

## Tests

```bash
# Backend (JUnit)
cd backend && mvn test

# Lancer les tests sans le rapport JaCoCo (Java 25+)
cd backend && mvn test -Djacoco.skip=true
```

---

## Structure du projet

```
projetBar/
├── backend/          # Spring Boot API
│   ├── src/main/java/com/barapp/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── repository/
│   ├── src/main/resources/db/init.sql
│   └── Dockerfile
├── frontend/         # Vue 3 TypeScript
│   ├── src/
│   │   ├── views/
│   │   ├── components/
│   │   ├── stores/
│   │   ├── api/
│   │   └── types/
│   ├── nginx.conf
│   └── Dockerfile
└── docker-compose.yml
```
