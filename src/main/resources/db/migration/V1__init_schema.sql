-- Usar BIGSERIAL para compatibilidade com GenerationType.IDENTITY
-- O PostgreSQL criará sequências automaticamente

CREATE TABLE app_user
(
    id                      BIGSERIAL PRIMARY KEY,
    username                VARCHAR(255) UNIQUE NOT NULL,
    password                VARCHAR(255)        NOT NULL,
    email                   VARCHAR(255) UNIQUE NULL,
    email_verified          BOOLEAN DEFAULT FALSE,
    onboarding_completed    BOOLEAN DEFAULT FALSE,
    created_at              TIMESTAMP,
    updated_at              TIMESTAMP
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
    user_id    INTEGER UNIQUE,
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

CREATE TABLE book_catalog
(
    id                 BIGSERIAL PRIMARY KEY,
    title              VARCHAR(255) NOT NULL,
    author             VARCHAR(255) NOT NULL,
    language           VARCHAR(20),
    editorial_category  VARCHAR(20) NOT NULL,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP
);

CREATE TABLE book_catalog_genre
(
    book_catalog_id BIGINT NOT NULL,
    genre           VARCHAR(50) NOT NULL,
    CONSTRAINT fk_book_catalog_genre_book FOREIGN KEY (book_catalog_id) REFERENCES book_catalog (id)
);

CREATE TABLE media_collection
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    type       VARCHAR(50) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    icon       VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_media_collection_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE INDEX idx_media_collection_user_id ON media_collection(user_id);
CREATE INDEX idx_media_collection_type ON media_collection(type);

CREATE TABLE movie_catalog
(
    id                 BIGSERIAL PRIMARY KEY,
    tmdb_id            BIGINT UNIQUE NOT NULL,
    title              VARCHAR(255) NOT NULL,
    original_title     VARCHAR(255),
    overview           TEXT,
    poster_path        VARCHAR(255),
    backdrop_path      VARCHAR(255),
    release_date       DATE,
    vote_average       DOUBLE PRECISION,
    vote_count         INTEGER,
    popularity         DOUBLE PRECISION,
    original_language  VARCHAR(10),
    adult              BOOLEAN,
    video              BOOLEAN,
    director           VARCHAR(255),
    editorial_category VARCHAR(20),
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP
);

CREATE TABLE movie_progress
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    movie_id   BIGINT NOT NULL,
    watched    BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_movie_progress_user FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT fk_movie_progress_movie FOREIGN KEY (movie_id) REFERENCES movie_catalog (id),
    CONSTRAINT uk_movie_progress_user_movie UNIQUE (user_id, movie_id)
);

CREATE TABLE series_catalog
(
    id                 BIGSERIAL PRIMARY KEY,
    tmdb_id            BIGINT UNIQUE NOT NULL,
    name               VARCHAR(255) NOT NULL,
    original_name      VARCHAR(255),
    overview           TEXT,
    poster_path        VARCHAR(255),
    backdrop_path      VARCHAR(255),
    first_air_date     DATE,
    vote_average       DOUBLE PRECISION,
    vote_count         INTEGER,
    popularity         DOUBLE PRECISION,
    original_language  VARCHAR(10),
    origin_country     VARCHAR(10),
    creator            VARCHAR(255),
    editorial_category VARCHAR(20),
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP
);

CREATE TABLE series_progress
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    series_id    BIGINT NOT NULL,
    last_season  INTEGER,
    last_episode INTEGER,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    CONSTRAINT fk_series_progress_user FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT fk_series_progress_series FOREIGN KEY (series_id) REFERENCES series_catalog (id),
    CONSTRAINT uk_series_progress_user_series UNIQUE (user_id, series_id)
);

CREATE TABLE user_media
(
    id              BIGSERIAL PRIMARY KEY,
    external_id     VARCHAR(255) NOT NULL,
    type            VARCHAR(50) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    thumbnail       VARCHAR(255),
    completed       BOOLEAN DEFAULT FALSE,
    rating          INTEGER,
    display_order   INTEGER,
    page_count      INTEGER,
    finished_at     DATE,
    highlighted     BOOLEAN DEFAULT FALSE,
    author          VARCHAR(255),
    user_id         BIGINT NOT NULL,
    status          VARCHAR(20),
    current_season  INTEGER,
    current_episode INTEGER,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT fk_user_media_user FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT uk_user_media_external_id_user UNIQUE (external_id, user_id)
);

CREATE TABLE media_collection_user_media
(
    media_collection_id BIGINT NOT NULL,
    user_media_id       BIGINT NOT NULL,
    CONSTRAINT fk_media_collection_user_media_collection FOREIGN KEY (media_collection_id) REFERENCES media_collection (id),
    CONSTRAINT fk_media_collection_user_media_user_media FOREIGN KEY (user_media_id) REFERENCES user_media (id),
    CONSTRAINT uk_media_collection_user_media UNIQUE (media_collection_id, user_media_id)
);

CREATE INDEX idx_media_collection_user_media_collection ON media_collection_user_media(media_collection_id);
CREATE INDEX idx_media_collection_user_media_user_media ON media_collection_user_media(user_media_id);

CREATE TABLE highlight
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    time_spent      TIME,
    user_media_id   BIGINT,
    category_id     INTEGER,
    museum_id       INTEGER,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,

    CONSTRAINT fk_highlight_user_media FOREIGN KEY (user_media_id) REFERENCES user_media (id),
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

-- Índices para performance na tabela user_media
CREATE INDEX idx_museum_user_id ON museum(user_id);
CREATE INDEX idx_user_media_user_id ON user_media(user_id);
CREATE INDEX idx_user_media_type ON user_media(type);
CREATE INDEX idx_user_media_external_id ON user_media(external_id);
CREATE INDEX idx_user_media_user_type_finished ON user_media(user_id, type, finished_at DESC);
CREATE INDEX idx_user_media_completed ON user_media(user_id, type, completed);
CREATE INDEX idx_book_catalog_editorial_category ON book_catalog(editorial_category);
CREATE INDEX idx_book_catalog_genre_name ON book_catalog_genre(genre);
CREATE INDEX idx_movie_catalog_editorial_category ON movie_catalog(editorial_category);
CREATE INDEX idx_series_catalog_editorial_category ON series_catalog(editorial_category);

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
    id          BIGSERIAL PRIMARY KEY,
    user_id     INTEGER NOT NULL,
    type        VARCHAR(50) NOT NULL,
    value       VARCHAR(255),
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT fk_preference_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- Tabelas do sistema de achievements
CREATE TABLE achievement
(
    code        VARCHAR(255) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(255),
    type        VARCHAR(20) NOT NULL
);

CREATE TABLE user_achievement
(
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    achievement_code    VARCHAR(255) NOT NULL,
    unlocked_at         TIMESTAMP NOT NULL,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP,
    CONSTRAINT fk_user_achievement_user FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT fk_user_achievement_achievement FOREIGN KEY (achievement_code) REFERENCES achievement (code)
);

CREATE TABLE user_goal
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    type        VARCHAR(50) NOT NULL,
    target      INTEGER NOT NULL,
    progress    INTEGER DEFAULT 0,
    goal_type   VARCHAR(50) NOT NULL,
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    completed   BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT fk_user_goal_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- Índices para performance nas tabelas de achievement
CREATE INDEX idx_user_achievement_user_id ON user_achievement(user_id);
CREATE INDEX idx_user_goal_user_id ON user_goal(user_id);
CREATE INDEX idx_user_goal_type ON user_goal(type);
CREATE INDEX idx_user_goal_completed ON user_goal(completed);

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

-- Tabela para tokens de atualização de sessão
CREATE TABLE refresh_token
(
    id         BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(255) UNIQUE NOT NULL,
    user_id    BIGINT              NOT NULL,
    expires_at TIMESTAMP           NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX idx_refresh_token_expires_at ON refresh_token(expires_at);

-- Tabelas do sistema de jogos
CREATE TABLE game_catalog
(
    id      BIGSERIAL PRIMARY KEY,
    rawg_id BIGINT UNIQUE NOT NULL,
    name    VARCHAR(255) NOT NULL
);

CREATE INDEX idx_game_catalog_rawg_id ON game_catalog(rawg_id);
CREATE INDEX idx_game_catalog_name ON game_catalog(name);

CREATE TABLE steam_account
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT UNIQUE NOT NULL,
    steam_id64     VARCHAR(255) NOT NULL,
    profile_url    VARCHAR(255),
    avatar_url     VARCHAR(255),
    persona_name   VARCHAR(255),
    last_sync_at   TIMESTAMP,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    CONSTRAINT fk_steam_account_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE INDEX idx_steam_account_user_id ON steam_account(user_id);
CREATE INDEX idx_steam_account_steam_id64 ON steam_account(steam_id64);

CREATE TABLE user_game
(
    id                    BIGSERIAL PRIMARY KEY,
    user_media_id         BIGINT UNIQUE NOT NULL,
    steam_app_id          VARCHAR(255),
    playtime_minutes      INTEGER,
    achievements_unlocked INTEGER,
    total_achievements    INTEGER,
    platinumed            BOOLEAN DEFAULT FALSE,
    status                VARCHAR(20),
    rawg_id               BIGINT,
    name                  VARCHAR(255),
    genres                VARCHAR(1000),
    platforms             VARCHAR(1000),
    stores                VARCHAR(2000),
    created_at            TIMESTAMP,
    updated_at            TIMESTAMP,
    CONSTRAINT fk_user_game_user_media FOREIGN KEY (user_media_id) REFERENCES user_media (id)
);

CREATE INDEX idx_user_game_user_media_id ON user_game(user_media_id);
CREATE INDEX idx_user_game_status ON user_game(status);
CREATE INDEX idx_user_game_platinumed ON user_game(platinumed);

-- Tabela para comentários de perfil
CREATE TABLE profile_comment
(
    id               BIGSERIAL PRIMARY KEY,
    author_id        BIGINT NOT NULL,
    profile_owner_id BIGINT NOT NULL,
    content          VARCHAR(1000) NOT NULL,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    CONSTRAINT fk_profile_comment_author FOREIGN KEY (author_id) REFERENCES app_user (id),
    CONSTRAINT fk_profile_comment_profile_owner FOREIGN KEY (profile_owner_id) REFERENCES app_user (id)
);

CREATE INDEX idx_profile_comment_author ON profile_comment(author_id);
CREATE INDEX idx_profile_comment_profile_owner ON profile_comment(profile_owner_id);

-- Adicionar coluna game_catalog_id na tabela user_media
ALTER TABLE user_media ADD COLUMN game_catalog_id BIGINT;
ALTER TABLE user_media ADD CONSTRAINT fk_user_media_game_catalog FOREIGN KEY (game_catalog_id) REFERENCES game_catalog (id);
CREATE INDEX idx_user_media_game_catalog_id ON user_media(game_catalog_id);

