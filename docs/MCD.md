# MCD — Bar'app

```mermaid
erDiagram
    UTILISATEUR {
        bigint id PK
        varchar nom
        varchar prenom
        varchar email
        varchar mot_de_passe
        varchar role "CLIENT | BARMAKER"
    }

    CATEGORIE {
        bigint id PK
        varchar nom
    }

    COCKTAIL {
        bigint id PK
        varchar nom
        text description
        varchar image_url
        bigint categorie_id FK
    }

    INGREDIENT {
        bigint id PK
        varchar nom
    }

    COCKTAIL_INGREDIENT {
        bigint cocktail_id FK
        bigint ingredient_id FK
    }

    COCKTAIL_PRIX {
        bigint id PK
        bigint cocktail_id FK
        varchar taille "S | M | L"
        decimal prix
    }

    COMMANDE {
        bigint id PK
        bigint utilisateur_id FK
        varchar statut "PANIER | COMMANDEE | EN_COURS | TERMINEE"
        timestamp date_creation
        timestamp date_mise_a_jour
    }

    LIGNE_COMMANDE {
        bigint id PK
        bigint commande_id FK
        bigint cocktail_id FK
        varchar taille "S | M | L"
        decimal prix_unitaire
        varchar statut "PREPARATION_INGREDIENTS | ASSEMBLAGE | DRESSAGE | TERMINEE"
    }

    UTILISATEUR ||--o{ COMMANDE : "passe"
    CATEGORIE ||--o{ COCKTAIL : "contient"
    COCKTAIL }o--o{ INGREDIENT : "composé de"
    COCKTAIL_INGREDIENT }|--|| COCKTAIL : ""
    COCKTAIL_INGREDIENT }|--|| INGREDIENT : ""
    COCKTAIL ||--o{ COCKTAIL_PRIX : "a pour prix"
    COMMANDE ||--o{ LIGNE_COMMANDE : "contient"
    COCKTAIL ||--o{ LIGNE_COMMANDE : "est commandé via"
```

## Statuts

### Commande
| Statut | Description |
|---|---|
| `PANIER` | En cours de constitution par le client |
| `COMMANDEE` | Lancée par le client, en attente de traitement |
| `EN_COURS` | Au moins un cocktail en préparation |
| `TERMINEE` | Tous les cocktails terminés |

### Ligne de commande
| Statut | Description |
|---|---|
| `PREPARATION_INGREDIENTS` | Étape initiale |
| `ASSEMBLAGE` | Ingrédients prêts, assemblage en cours |
| `DRESSAGE` | Assemblage terminé, présentation |
| `TERMINEE` | Cocktail prêt |

## Règle métier clé
Quand **toutes** les `ligne_commande` d'une `commande` passent à `TERMINEE`, la `commande` passe automatiquement à `TERMINEE`.
