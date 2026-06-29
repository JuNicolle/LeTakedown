# APEX 4.

## Workflow de Développement Structuré

## A

## Analyze Explorer et comprendre

## P

## Plan Concevoir avec validation

## E

## Execute Implémenter sans dérive

## X

## eXamine Vérifier et valider

###### Principe Description

n Correctness > Speed La qualité prime sur la vitesse

n Gate User Validation humaine obligatoire avant exécution

n File I/O as State L'état vit dans les fichiers, pas le transcript

n Re-anchoring Contexte frais à chaque phase

n Subagents Haiku Délégation économique pour exploration

```
Version 4.3 - Optimisé pour Claude Code
```

### Vue d'Ensemble du Workflow

```
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n RE-ANCHOR n
n (Lire fichiers .apex/, git status) n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n
t
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n A ANALYZE - Subagents explorent en parallèle n
n explore-codebase, explore-docs (Context7) n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n
t
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n P PLAN - Conception détaillée n
n n GATE USER - Validation obligatoire n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n
t (après "go" du user)
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n E EXECUTE - Implémentation stricte n
n NO scope creep, suivre patterns existants n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n
t
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n X EXAMINE - Vérification complète n
n lint, typecheck, tests → Si fail: retour PLAN n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
```

### Phase 1: ANALYZE

## A

#### ANALYZE

###### Objectif: Explorer et comprendre le contexte avant toute action.

###### Actions:

- Lancer les subagents en parallèle (Haiku pour économiser les tokens)
- explore-codebase: structure, patterns, conventions
- explore-docs: documentation existante, specs
- Context7: fetch documentation externe si nécessaire

###### Output attendu:

- Liste des fichiers pertinents identifiés
- Patterns et conventions détectés
- Dépendances et impacts potentiels
- Questions en suspens (si flou → AskUserQuestion)

##### Subagents d'Exploration

###### Subagent Modèle Rôle

```
explore-codebase Haiku Structure fichiers, patterns code
```
```
explore-docs Haiku README, CLAUDE.md, specs
```
```
explore-tests Haiku Tests existants, coverage
```
```
websearch Haiku Documentation externe, best practices
```

### Phase 2: PLAN

## P

#### PLAN

###### Objectif: Concevoir une solution détaillée et obtenir validation.

###### Actions:

- Rédiger un plan détaillé avec fichiers exacts à modifier/créer
- Identifier les risques et dépendances
- Proposer des alternatives si pertinent
- n STOP et demander validation au user

###### Output attendu:

- Plan structuré avec étapes numérotées
- Liste précise: fichiers à créer, modifier, supprimer
- Estimation de complexité
- Questions de clarification si nécessaire

###### n GATE USER - Point de Contrôle Obligatoire

###### Le workflow DOIT s'arrêter ici et attendre la validation explicite:

- Présenter le plan au user
- Dire: "Voici mon plan. Réponds 'go' pour continuer ou suggère des modifications."
- NE PAS continuer sans réponse explicite
- Si modifications demandées → réviser le plan et re-soumettre


### Phase 3: EXECUTE

## E

#### EXECUTE

###### Objectif: Implémenter exactement ce qui a été validé, sans dérive.

###### Règles strictes:

- STRICTLY IN SCOPE - uniquement ce qui est dans le plan
- NO comments sauf si absolument nécessaire
- Suivre les patterns existants du codebase
- Parallel execution où possible
- Si découverte d'un problème majeur → STOP et retour au user

###### Output attendu:

- Code implémenté selon le plan
- Fichiers créés/modifiés comme prévu
- Confirmation de chaque étape complétée

##### Règles de Scope

###### n Autorisé n Interdit

```
Implémenter le plan validé Ajouter des "améliorations" non demandées
```
```
Suivre les patterns existants Refactorer du code hors scope
```
```
Corriger un bug bloquant découvert Optimiser du code qui fonctionne
```
```
Demander clarification si bloqué Deviner et continuer
```

### Phase 4: EXAMINE

## X

#### EXAMINE

###### Objectif: Vérifier que tout fonctionne avant de déclarer terminé.

###### Checklist de vérification:

- Compilation/syntaxe: py_compile, tsc, etc.
- Linting: eslint, ruff, biome
- Type checking: mypy, tsc --noEmit
- Tests: uniquement sur ce qui a changé
- Format: prettier, black

###### Si échec:

- Analyser l'erreur
- Retour à PLAN si problème de conception
- Fix direct si erreur mineure

##### Matrice de Vérification

###### Vérification Commande Si Échec

```
Syntaxe Python python -m py_compile *.py Fix syntax error
```
```
Syntaxe TypeScript tsc --noEmit Fix type error
```
```
Lint Python ruff check. Auto-fix ou manuel
```
```
Lint JS/TS eslint. Auto-fix ou manuel
```
```
Tests pytest / vitest Debug ou retour PLAN
```
```
Format black / prettier Auto-fix
```

### Gestion du Contexte

##### Structure des Fichiers d'État

```
.apex/
nnn current-task.md # Tâche en cours
nnn analysis.md # Résultats de la phase ANALYZE
nnn plan-draft.md # Plan en attente de validation
nnn plan-approved.md # Plan validé par le user
nnn changes.md # Log des modifications
nnn examine-results.md # Résultats des vérifications
nnn blockers.md # Problèmes identifiés
```
##### Principe de Re-anchoring

###### Pourquoi: Éviter le context drift et la pollution par les erreurs passées.

###### Quand: Au début de chaque phase APEX.

###### Comment:

###### 1. Lire les fichiers .apex/*.md pertinents

###### 2. Lire git status et git diff

###### 3. NE PAS se fier au transcript de la conversation

###### 4. L'état de vérité = les fichiers, pas la mémoire


### Commande /apex

##### Utilisation

```
/apex Ajoute une fonctionnalité de dark mode
```
```
/apex Refactor le module auth pour séparer JWT et sessions
```
```
/apex Fix le bug #42 - l'utilisateur ne peut pas se déconnecter
```
##### Template de la Commande

```
---
description: APEX workflow - Analyze, Plan, Execute, eXamine
argument-hint: <task description>
---
```
```
# APEX Workflow
```
```
**ULTRA THINK before each action.**
**Priority: Correctness > Completeness > Speed**
```
```
## Contexte Dynamique
!git status
!git branch
```
```
## Phase 1: ANALYZE
```
- Lancer subagents (Haiku): explore-codebase, explore-docs
- Identifier fichiers pertinents, patterns, dépendances
- Output: # 1. ANALYZE

```
## Phase 2: PLAN
```
- Plan détaillé avec fichiers exacts
- n STOP si flou → AskUserQuestion
- n ATTENDRE approbation user ("go")
- Output: # 2. PLAN

```
## Phase 3: EXECUTE
```
- STRICTLY IN SCOPE
- NO scope creep
- Suivre patterns existants
- Output: # 3. EXECUTE

```
## Phase 4: EXAMINE
```
- Compile, lint, typecheck, tests
- Si fail → retour PLAN
- Output: # 4. EXAMINE


### Checklist Complète

###### 3 Phase Vérification

```
n RE-ANCHOR Fichiers .apex/ lus
```
```
n RE-ANCHOR git status vérifié
```
```
n ANALYZE Subagents lancés en parallèle
```
```
n ANALYZE Fichiers pertinents identifiés
```
```
n ANALYZE Patterns documentés
```
```
n PLAN Plan détaillé rédigé
```
```
n PLAN Fichiers listés précisément
```
```
n PLAN n GATE: User a validé
```
```
n EXECUTE Scope strict respecté
```
```
n EXECUTE Patterns existants suivis
```
```
n EXECUTE Pas de scope creep
```
```
n EXAMINE Compilation OK
```
```
n EXAMINE Lint OK
```
```
n EXAMINE Typecheck OK
```
```
n EXAMINE Tests passent
```
##### Quand Utiliser APEX

###### Situation Recommandation

```
Tâche simple < 30 min /oneshot (pas besoin d'APEX)
```
```
Feature moyenne avec tests /apex (workflow complet)
```
```
Refactoring complexe /apex (gate user critique)
```
```
Bug fix rapide /oneshot ou /apex selon complexité
```

Nouveau projet /init-project puis /apex


### Intégration avec l'Écosystème

##### MCP Servers Recommandés

###### MCP Server Usage dans APEX

```
context7 ANALYZE: fetch documentation externe
```
```
sequential-thinking PLAN: raisonnement complexe
```
```
github EXAMINE: créer PR, issues
```
```
supabase EXECUTE: opérations DB si pertinent
```
##### Commandes Complémentaires

###### Commande Description

```
/init-project Initialiser un projet avec CLAUDE.md
```
```
/challenge Challenger une décision ou approche
```
```
/prd Générer un Product Requirements Document
```
```
/archi Documenter l'architecture
```
```
/create-tasks Découper en tâches depuis PRD
```
```
/workflow-start Démarrer le workflow complet
```
##### Mode Autonome (APEX Auto)

###### Pour une exécution sans gate user (fire and forget):

- Définir des critères de succès binaires (tests pass, build OK)
- Configurer un max d'itérations (éviter boucles infinies)
- Auto-stop après 3 échecs consécutifs
- État dans fichiers .apex/ pour reprise

###### Note: Le mode autonome est déconseillé pour les tâches complexes où le jugement

###### humain est nécessaire.


nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn

```
APEX 4.3 - Workflow de Développement Structuré
Julian Verhelle - Mars 2026
Optimisé pour Claude Code + MCP Ecosystem
```

