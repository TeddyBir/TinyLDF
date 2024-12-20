# TinyLDF
Linked Data Fragment server able to process any Quad PAttern Query as Wikidata LDF.

**Auteurs:**

*   RAMAHEFARINAIVO Stéphane
*   REPPERT William
*   POULALION Maxime

**Objectif du projet:**

Ce projet vise à développer une application web permettant d'interroger et d'ajouter des données dans un entrepôt de données RDF (Resource Description Framework), en s'inspirant de l'interface de [Wikidata Query Service](https://query.wikidata.org/bigdata/ldf). L'application permet aux utilisateurs de rechercher des quads (sujet, prédicat, objet, graph) en utilisant des patterns de recherche et d'ajouter de nouveaux quads (sujet, prédicat, objet, graphe) à la base de données.

## Fonctionnalités implémentées

*   **Authentification:**
    *   Intégration de l'authentification Google Sign-In pour identifier les utilisateurs.
*   **Interface utilisateur:**
    *   Création d'une interface utilisateur web avec [Mithril.js](https://mithril.js.org/) pour une expérience utilisateur réactive.
    *   Utilisation de la librairie CSS [Bulma](https://bulma.io/) pour un style visuel cohérent et agréable.
    *   Deux formulaires principaux:
        *   **Formulaire de requête:** Permet de rechercher des triplets en spécifiant le sujet, le prédicat, l'objet et/ou le graphe.
        *   **Formulaire d'ajout de Quad:** Permet d'ajouter de nouveaux quads à la base de données.
    *   Affichage des résultats sous forme de liste non ordonnée ( `<ul>` et `<li>`).
*   **Interaction avec le backend:**
    *   Communication avec une API backend via des requêtes `fetch`.
    *   Gestion des requêtes GET pour la recherche de triplets et POST pour l'ajout de quads.
    *   Parsing des réponses JSON du backend.
*   **Gestion des erreurs:**
    *   Affichage de messages d'erreur en cas d'échec des requêtes ou d'absence de résultats.

## Fonctionnalités non implémentées

*   **Authentification avancée:**
    *   Gestion Authentification.
    *   Fonctionnalité de déconnexion.
*   **Interface utilisateur avancée:**
    *   Édition et suppression de quads.
    *   Pagination des résultats.
    *   Saisie semi-automatique des termes (sujet, prédicat, objet) lors de la saisie dans les formulaires.
    *   Visualisation graphique des données.
    *   Possibilité de choisir le format de sortie (JSON, CSV, TSV, etc.).
    *   Historique des requêtes.
*   **Backend avancé:**
    *   Validation des données lors de l'ajout de quads.
    *   Gestion d'un véritable QUAD Set.
*   **Documentation:**
    *   Documentation plus détaillée du code, notamment du backend.
    *   Instructions d'installation et de déploiement plus complètes.


