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

## Tests

```bash
# Backend (JUnit)
cd backend && mvn test

# Frontend (Vitest)
cd frontend && npm run test:unit
```
