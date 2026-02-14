# TaskFlow API — FEATURES

Ce document décrit les fonctionnalités backend attendues pour l'API TaskFlow.

## Acteurs (utilisateurs impliqués)
- **Administrateur (app)** : gestion globale, activation du mode administrateur, consultation des audits.
- **Utilisateur (app)** : peut être membre ou chef dans différents projets.
- **Rôle Projet — Project Manager (PM)** : gestion d'un projet spécifique.
- **Rôle Projet — Member** : contributeur aux tâches d'un projet.
- **Rôle Projet — Viewer** : accès lecture seule.
- **Système (service interne)** : scheduler et moteur de notifications internes.
- **Auditeur** : lecture des audits.

## Fonctionnalités API

### Auth & Users
- Endpoints d'authentification (login, refresh token optionnel), gestion des sessions.
- Gestion des utilisateurs (CRUD, activation, rôle application).
- Réinitialisation mot de passe (flow backend, token expirant).

### Projets & permissions
- Endpoints CRUD projets, gestion des membres et rôles projet.
- Permissions contrôlées au niveau API (checks projet vs rôle app).

### Tâches
- Endpoints CRUD tâches et sous-tâches.
- Assignations multi‑utilisateurs.
- Gestion des étiquettes (tags) et relation many-to-many.
- Upload/download d'attachements (stockage local pour le moment).
- Historique des modifications (audit par tâche) et endpoint pour récupérer l'historique.

### Collaboration & notifications internes
- Endpoints pour commentaires sur tâches.
- Endpoints pour récupérer notifications in-app; marquer comme lues.

### Vues & agrégations
- Endpoints pour boards (Kanban), calendrier, timelines / Gantt simplifié.
- Endpoint `My Tasks` : récupération agrégée de toutes les tâches assignées à un utilisateur.

### Monitoring & qualité
- Logs structurés (SLF4J/Logback) et endpoints Actuator protégés en dev.
- Tests unitaires (JUnit + Mockito) pour services et controllers.

## Contraintes
- Aucune intégration externe (pas d'envoi e-mail ni webhooks externes).
- Secrets et credentials via variables d'environnement.

## Cross-link
- Frontend repo: https://github.com/KengniJohan/TaskFlow.git (voir README frontend pour setup et usage).

