```
Durée de l’épreuve
Interne : 5 jours en présentiel (
jours de travail, +
présentation)Ressources à
exploiter : Illimitées.
```
## Le concept

Il est porté à votre attention que cet examen sera utilisé dans la rédaction du votre livret
d'évaluation pour le titre professionnel.

## Contexte

Vous allez réaliser une application web fonctionnelle.

Vous devrez livrer les éléments suivants :

- Les maquettes, au format image ou pdf
- Le code source au format zip incluant les sources, un readme pour lancer
    l’application
- Le liens vers le repository github que vous aurez utilisé

Le tout est à indiquer dans un mail à destination de
laury.bossaert@foreach-academy.fr avant le 02 / 07 /202 6 à 1 7 h.

Les technologies utilisées le sont celles que vous avez étudiées à savoir Java, VueJs
et PostgreSQL.

## L’application : Le Bar’app

Cette application vise à améliorer l’expérience des clients de bars à cocktail et de
ses employés.

L’application permettra, à partir d’une carte, de commander des boissons tout en
suivant leur préparation.

Il faut distinguer deux types d’utilisateurs : les clients et les Barmakers.


```
Durée de l’épreuve
: Interne : 5 jours en présentiel
Ressources à exploiter :
Illimitées.
```
Un client peut consulter la carte, remplir son panier avec des cocktails, consulter son
panier, lancer la commande et voir l’avancée de la préparation de commande
(Commandée, en cours de préparation, Terminée).

Un barmaker doit pouvoir paramétrer l’application en créant sa carte : des catégories
et des cocktails. Un cocktail est représenté par des ingrédients, une taille (S, M,L) et un
prix par taille.

Le barmaker peut consulter la liste des commandes à traiter.

Lorsqu'il sélectionne une commande à traiter, il voit la liste des cocktails commandés.
Il peut ainsi en sélectionner un et le faire avancer d’étapes en étapes : Préparation des
Ingrédients - Assemblage - Dressage - Terminée.

Lorsque tous les cocktails sont à l’étape Terminer, la commande globale passe au
statut Terminée.

**Phase 1 : Conception - Jour 1**

Vous allez créer plusieurs entités SQL reprenant les descriptions précédentes.

**Vous réaliserez le MCD de votre choix représentant cette Application.**

Avant de vous lancer, vous réaliserez toutes les maquettes nécessaires au déroulé
d’une commande sur Figma.

**Phase 2 : Métier - API - Les Créations et modifications - Jour 2**

Vous créerez une application Java Springboot permettant la création, modification des
différentes entités de votre MCD.

Ces API devront être testées (Junit, avec une couverture de plus de 85%) et contrôlées
(vérification des données entrantes).


```
Durée de l’épreuve
: Interne : 5 jours en présentiel
Ressources à exploiter :
Illimitées.
```
**Vous livrerez un package dockerfile utilisant votre application ainsi qu’un script
d'initialisation de votre base de données.**

**Phase 3 : L’application Web - Jour 3 et 4**

Vous créerez une application VueJS, en utilisant typescript afin de créer les différentes
vues de votre application.

Les aspects graphiques sont laissés à votre appréciation. L'utilisation d’images, de filtre
(pour le choix des cocktails), de picto pour l’ergonomie seront grandement appréciés.

Ce code Vue JS devra être testés

**Vous livrerez un package dockerfile utilisant votre application.**

**Phase 4 : La démonstration - Jour 5**

**Vous présenterez votre application devant vos évaluateurs. La démonstration inclura une
présentation type Powerpoint afin de démontrer l'intérêt de votre réalisation puis une
démonstration de votre application, en live.**

**La démonstration doit être scénarisée et réalisée en 15 minutes. S’en suivra 5 minutes
d’échange.**


```
Durée de l’épreuve
: Interne : 5 jours en présentiel
Ressources à exploiter :
Illimitées.
```
## L’évaluation

Vous serez évalué sur :

- La conception
- L’application Java : les api et l'utilisation du langage
- L'ergonomie de l’application
- L’application VueJs : le type script et l’utilisation du langage.

Votre application web doit tenir compte de la taille de l’écran, des fonctionnalités
selon l'utilisateur et de l’ergonomie

L’utilisation de tests, de clean code et de pattern de conception sera grandement
appréciée.

Pour vérifier votre application, nous la déploierons et l'utiliserons avec deux devices
Web afin de simuler un client et un barmaker.

L’évaluation sera répartie selon la grille suivantes :

```
Conception
Graphique et
Ergonomie
Maquette
```
```
Pertinence du
MCD
```
```
exécution et
développeme
nt Java
```
```
exécution et
développeme
nt VueJS
```
```
Déploiement
et maîtrise
ops
```
```
Démonstration
et
Présentation
```
### 10% 10% 30% 30% 5% 15%