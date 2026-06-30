# Bar'app

Application web de commande et suivi de cocktails en temps réel.

## Stack

- **Backend** : Java 21 · Spring Boot 3.5 · PostgreSQL 16
- **Frontend** : VueJS 3 · TypeScript · Vite · Pinia

## Prérequis

- Java 21+
- Maven 3.9+
- Node 20+
- PostgreSQL 16

## Lancer le projet en local

### 1. Base de données

```bash
psql -U postgres -c "CREATE USER barapp WITH PASSWORD 'barapp';"
psql -U postgres -c "CREATE DATABASE barapp OWNER barapp;"
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

## Lancer avec Docker

```bash
docker-compose up
```

- Frontend : `http://localhost:80`
- Backend : `http://localhost:8080`
- PostgreSQL : `localhost:5432`

## API Endpoints

Base URL : `http://localhost:8080`

### Catégories
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/categories` | Lister toutes les catégories |
| GET | `/api/categories/{id}` | Détail d'une catégorie |
| POST | `/api/categories` | Créer une catégorie |
| PUT | `/api/categories/{id}` | Modifier une catégorie |
| DELETE | `/api/categories/{id}` | Supprimer une catégorie |

### Cocktails
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/cocktails` | Lister tous les cocktails (carte) |
| GET | `/api/cocktails/{id}` | Détail d'un cocktail |
| POST | `/api/cocktails` | Créer un cocktail |
| PUT | `/api/cocktails/{id}` | Modifier un cocktail |
| DELETE | `/api/cocktails/{id}` | Supprimer un cocktail |

### Ingrédients
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/ingredients` | Lister tous les ingrédients |
| GET | `/api/ingredients/{id}` | Détail d'un ingrédient |
| POST | `/api/ingredients` | Créer un ingrédient |
| PUT | `/api/ingredients/{id}` | Modifier un ingrédient |
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

## Tests

```bash
# Backend (JUnit)
cd backend && mvn test

# Frontend (Vitest)
cd frontend && npm run test:unit
```
