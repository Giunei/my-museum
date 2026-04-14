-- Usar BIGSERIAL para compatibilidade com GenerationType.IDENTITY
-- O PostgreSQL criará sequências automaticamente

CREATE TABLE app_user
(
    id                  BIGSERIAL PRIMARY KEY,
    username            VARCHAR(255) UNIQUE NOT NULL,
    password            VARCHAR(255)        NOT NULL,
    email               VARCHAR(255) UNIQUE NOT NULL,
    email_verified      BOOLEAN DEFAULT FALSE,
    onboarding_completed BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

CREATE TABLE person
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    gender          VARCHAR(20),
    nationality     VARCHAR(255),
    birth_date      DATE,
    user_id         INTEGER UNIQUE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT fk_person_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE TABLE profile
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            INTEGER UNIQUE NOT NULL,
    profile_image_url  VARCHAR(255),
    theme              VARCHAR(50),
    bio                TEXT,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE TABLE museum
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_museum_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE TABLE category
(
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    photo TEXT
);

CREATE TABLE highlight
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    time_spent  TIME,
    finished    BOOLEAN,
    platinumed  BOOLEAN,
    review      VARCHAR(50),
    category_id INTEGER,
    museum_id   INTEGER,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,

    CONSTRAINT fk_highlight_category FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_highlight_museum FOREIGN KEY (museum_id) REFERENCES museum (id)
);

CREATE TABLE follow
(
    id           BIGSERIAL PRIMARY KEY,
    follower_id  INTEGER NOT NULL,
    following_id INTEGER NOT NULL,
    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id) REFERENCES app_user (id),
    CONSTRAINT fk_follow_following FOREIGN KEY (following_id) REFERENCES app_user (id)
);

CREATE INDEX idx_following ON follow(following_id);
CREATE INDEX idx_follower ON follow(follower_id);

CREATE TABLE friendship
(
    id              BIGSERIAL PRIMARY KEY,
    user_request_id INTEGER NOT NULL,
    addressee_id    INTEGER NOT NULL,
    status          VARCHAR(50),
    CONSTRAINT fk_friendship_requester FOREIGN KEY (user_request_id) REFERENCES app_user (id),
    CONSTRAINT fk_friendship_addressee FOREIGN KEY (addressee_id) REFERENCES app_user (id)
);

-- Tabela para preferências do usuário
CREATE TABLE preference
(
    id     BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    type   VARCHAR(50) NOT NULL,
    value  VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_preference_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- Tabela para tokens de verificação de email
CREATE TABLE email_verification_token
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP        NOT NULL,
    user_id    INTEGER          NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_email_verification_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- Tabela para tokens de redefinição de senha
CREATE TABLE password_reset_token
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP        NOT NULL,
    user_id    INTEGER          NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- Índices para busca rápida por tokens
CREATE INDEX idx_email_verification_token ON email_verification_token(token);
CREATE INDEX idx_password_reset_token ON password_reset_token(token);

-- Usar BIGINT em vez de BIGSERIAL para compatibilidade com a sequência
-- A sequência genérica será usada pela EntityAbstract
