# TaskFlow API — Architecture

Cette page décrit l'architecture recommandée pour le service backend `taskflow_api`.

## Résumé
- Style : Hexagonal / Clean Architecture (Domain-centric, testable, découplé).
- Langage / runtime : Java 17+ / Spring Boot 3.x.
- Build : Maven.

## Objectifs
- Isoler la logique métier (domain) des frameworks (Spring, JPA).
- Rendre le code testable (unit tests) et maintenable.
- Fournir des ports/adapters pour persistance, sécurité et websocket (notifications internes).

## Organisation des packages (exemple)
- `net.giagroup.taskflowapi.config` — configuration Spring (Security, WebSocket, beans).
- `net.giagroup.taskflowapi.api.v1.controller` — controllers REST (thin controllers).
- `net.giagroup.taskflowapi.api.v1.dto` — DTOs et mappers.
- `net.giagroup.taskflowapi.application` — cas d'utilisation / services applicatifs (orchestrators).
- `net.giagroup.taskflowapi.domain` — entités, value objects, exceptions, invariants.
- `net.giagroup.taskflowapi.ports.in` — interfaces d'entrée des use-cases.
- `net.giagroup.taskflowapi.ports.out` — interfaces de sortie (repositories, stockage fichiers).
- `net.giagroup.taskflowapi.adapters.persistence` — impl JPA, Spring Data repositories, mappers.
- `net.giagroup.taskflowapi.adapters.websocket` — WebSocket/STOMP notification adapter.
- `net.giagroup.taskflowapi.adapters.security` — UserDetails, token provider wrappers.

## Modèle de données (points clés)
- Entités principales : `User`, `Role` (app), `Project`, `ProjectRole` (member/pm/viewer), `Task`, `Tag`, `Comment`, `Attachment`, `TaskHistory` (audit).
- Relations importantes :
  - `Task` <-> `Tag` : many-to-many.
  - `Task` <-> `User` : many-to-many (assignments).
  - `Project` -> `Task` : one-to-many.
- Identifiants : UUID recommandés (sécurité/merge), Long acceptable si souhaité.

## Persistence & migrations
- JPA / Hibernate pour la persistance.
- Flyway pour migrations DB (préférable pour contrôler schémas).
- Tests de repository : H2 en mémoire pour tests unitaires/DAO.

## Sécurité
- Spring Security + JWT (stateless). Stocker secret via variables d'environnement.
- `CustomUserDetailsService` + method-security (`@PreAuthorize`) pour contrôles fins.
- Implémenter le concept de "mode administrateur" : flag utilisateur + endpoint d'activation; vérification explicite dans la couche d'autorisation.

## Notifications internes
- WebSocket (STOMP) pour notifications in-app / realtime.

## Historique & Audit
- `TaskHistory` persistée pour chaque modification importante (qui, quoi, avant/après, timestamp).
- Audit des actions sensibles (user CRUD, role changes) stocké et accessible aux Auditeurs.

## Mapping & DTOs
- Utiliser MapStruct ou mappers manuels pour séparer entités/DTOs.

## Tests
- JUnit5 + Mockito pour unit tests (services, controllers).
- Tests d'intégration légers avec Spring Boot Test + H2.

## Observabilité
- SLF4J + Logback, fichiers de logs et niveaux configurables.
- Actuator exposé en dev (health, metrics) — endpoints protégés.

## Conventions / recommandations
- Keep controllers thin, business logic in `application` layer.
- Toutes les interactions DB passent par interfaces dans `ports.out`.
- Valider inputs via DTOs `@Valid` et centraliser les erreurs via `@ControllerAdvice`.

## Début rapide
- Générer projet via Spring Initializr avec dépendances : Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Validation, Lombok (optionnel), Flyway.
- Configurer `application.properties` pour utiliser variables d'environnement pour DB/JWT.

