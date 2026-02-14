# ERD — TaskFlow API

Le diagramme suivant décrit les entités principales et leurs relations (Mermaid ERD).

```mermaid
erDiagram
    USERS {
      UUID id PK
      varchar username
      varchar email
      boolean enabled
    }
    ROLES {
      varchar name PK
    }
    USER_ROLES {
      UUID user_id FK
      varchar role_name FK
    }
    PROJECTS {
      UUID id PK
      varchar name
      varchar visibility
      UUID owner_id FK
    }
    PROJECT_MEMBERS {
      UUID project_id FK
      UUID user_id FK
      varchar project_role
    }
    TASKS {
      UUID id PK
      UUID project_id FK
      varchar title
      varchar status
      timestamptz due_date
    }
    TASK_ASSIGNEES {
      UUID task_id FK
      UUID user_id FK
    }
    TAGS {
      UUID id PK
      varchar name
    }
    TASK_TAGS {
      UUID task_id FK
      UUID tag_id FK
    }
    COMMENTS {
      UUID id PK
      UUID task_id FK
      UUID author_id FK
      text content
    }
    ATTACHMENTS {
      UUID id PK
      UUID task_id FK
      varchar filename
      varchar url
    }
    TASK_HISTORY {
      UUID id PK
      UUID task_id FK
      UUID changed_by FK
      varchar change_type
      jsonb payload
    }
    NOTIFICATIONS {
      UUID id PK
      UUID user_id FK
      varchar type
      text message
      boolean read
    }

    USERS ||--o{ USER_ROLES : has
    ROLES ||--o{ USER_ROLES : assigned
    PROJECTS ||--o{ PROJECT_MEMBERS : has
    USERS ||--o{ PROJECT_MEMBERS : participates
    PROJECTS ||--o{ TASKS : contains
    TASKS ||--o{ COMMENTS : has
    TASKS ||--o{ ATTACHMENTS : has
    TASKS ||--o{ TASK_HISTORY : has
    TASKS ||--o{ TASK_ASSIGNEES : assigned
    USERS ||--o{ TASK_ASSIGNEES : assigned_to
    TAGS ||--o{ TASK_TAGS : attached
    TASKS ||--o{ TASK_TAGS : tagged
    USERS ||--o{ NOTIFICATIONS : receives

```

Notes:
- Les identifiants sont conçus comme `UUID` pour faciliter la fusion et la distribution.
- Les historiques (`TASK_HISTORY`) et notifications sont persistés pour audit et affichage in-app.
