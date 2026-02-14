-- Flyway V1: initial schema for TaskFlow
-- Uses pgcrypto for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- USERS
CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  username varchar(100) NOT NULL UNIQUE,
  email varchar(255) NOT NULL UNIQUE,
  password varchar(255) NOT NULL,
  enabled boolean NOT NULL DEFAULT true,
  admin_mode boolean NOT NULL DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz
);

-- ROLES
CREATE TABLE IF NOT EXISTS roles (
  name varchar(50) PRIMARY KEY
);

-- USER_ROLES (many-to-many)
CREATE TABLE IF NOT EXISTS user_roles (
  user_id uuid NOT NULL,
  role_name varchar(50) NOT NULL,
  PRIMARY KEY (user_id, role_name),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE CASCADE
);

-- PROJECTS
CREATE TABLE IF NOT EXISTS projects (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name varchar(200) NOT NULL,
  description text,
  visibility varchar(20) NOT NULL DEFAULT 'private',
  owner_id uuid REFERENCES users(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz
);

-- PROJECT_MEMBERS
CREATE TABLE IF NOT EXISTS project_members (
  project_id uuid NOT NULL,
  user_id uuid NOT NULL,
  project_role varchar(50) NOT NULL,
  PRIMARY KEY (project_id, user_id),
  CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
  CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- TAGS
CREATE TABLE IF NOT EXISTS tags (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name varchar(100) NOT NULL UNIQUE
);

-- TASKS
CREATE TABLE IF NOT EXISTS tasks (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id uuid NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
  title varchar(300) NOT NULL,
  description text,
  status varchar(50) NOT NULL DEFAULT 'TODO',
  priority varchar(50) DEFAULT 'MEDIUM',
  due_date timestamptz,
  created_by uuid REFERENCES users(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz
);

CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);

-- TASK_ASSIGNEES (many-to-many tasks <-> users)
CREATE TABLE IF NOT EXISTS task_assignees (
  task_id uuid NOT NULL,
  user_id uuid NOT NULL,
  PRIMARY KEY (task_id, user_id),
  CONSTRAINT fk_ta_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_ta_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_task_assignees_user_id ON task_assignees(user_id);

-- TASK_TAGS (many-to-many tasks <-> tags)
CREATE TABLE IF NOT EXISTS task_tags (
  task_id uuid NOT NULL,
  tag_id uuid NOT NULL,
  PRIMARY KEY (task_id, tag_id),
  CONSTRAINT fk_tt_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_tt_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- COMMENTS
CREATE TABLE IF NOT EXISTS comments (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id uuid NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  author_id uuid REFERENCES users(id),
  content text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

-- ATTACHMENTS
CREATE TABLE IF NOT EXISTS attachments (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id uuid NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  filename varchar(255) NOT NULL,
  url text NOT NULL,
  uploaded_by uuid REFERENCES users(id),
  created_at timestamptz NOT NULL DEFAULT now()
);

-- TASK_HISTORY (audit)
CREATE TABLE IF NOT EXISTS task_history (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  task_id uuid NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  changed_by uuid REFERENCES users(id),
  change_type varchar(100) NOT NULL,
  payload jsonb,
  created_at timestamptz NOT NULL DEFAULT now()
);

-- NOTIFICATIONS (in-app)
CREATE TABLE IF NOT EXISTS notifications (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type varchar(100),
  message text,
  link_url text,
  read boolean NOT NULL DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

-- Useful indexes
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
