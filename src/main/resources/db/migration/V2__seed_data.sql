-- =========================
-- ADMIN USER
-- =========================

INSERT INTO app_user (id, username, email, password, created_at, updated_at)
VALUES (nextval('app_user_id_seq'),
        'admin',
        'pmjgiunei@gmail.com',
        '$2a$12$YMcFQbatT633bElelHMoz.oWTCu.Jnj4TaYz2f8X5Iy.byNJy1.NW', -- senha: admin
        NOW(),
        NOW());

-- =========================
-- PERSON
-- =========================

INSERT INTO person (id, name, user_id, created_at, updated_at)
VALUES (nextval('person_id_seq'),
        'Admin',
        currval('app_user_id_seq'),
        NOW(),
        NOW());

-- =========================
-- MUSEUM (1-1 com USER)
-- =========================

INSERT INTO museum (id, user_id, created_at, updated_at)
VALUES (nextval('museum_id_seq'),
        currval('app_user_id_seq'),
        NOW(),
        NOW());

-- =========================
-- CATEGORIES PADRÃO
-- =========================

INSERT INTO category (id, name, photo)
VALUES (nextval('category_id_seq'), 'Jogos Platinados', NULL),
       (nextval('category_id_seq'), 'Filmes Assistidos', NULL),
       (nextval('category_id_seq'), 'Séries Finalizadas', NULL),
       (nextval('category_id_seq'), 'Livros Lidos', NULL);

INSERT INTO profile (id, user_id, theme)
VALUES (nextval('profile_id_seq'), currval('app_user_id_seq'), 'DEFAULT');