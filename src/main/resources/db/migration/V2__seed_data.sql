-- =========================
-- ADMIN USER
-- =========================

INSERT INTO app_user (id, username, email, password, created_at, updated_at)
VALUES (nextval('app_user_id_seq'),
        'admin',
        'admin@example.com',
        '$2a$12$M/GuFg6F5.fQT7xGMELfte4uMLecwVB.0TpeaASAf/jE2hGsXVJKC',
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

-- =========================
-- ACHIEVEMENTS
-- =========================

INSERT INTO achievement (code, name, description, image_url, type) VALUES
('READ_FIRST_BOOK', 'Primeiro livro lido', 'Parabéns pelo primeiro livro lido', 'assets/achievements/livro-1.png', 'BOOK'),
('READ_5_BOOKS', '5 livros lidos', 'Leu 5 livros', 'assets/achievements/livro-5.png', 'BOOK'),
('READ_10_BOOKS', '10 livros lidos', 'Leu 10 livros', 'assets/achievements/livro-10.png', 'BOOK'),
('READ_20_BOOKS', '20 livros lidos', 'Leu 20 livros', 'assets/achievements/livro-20.png', 'BOOK'),
('WATCH_FIRST_MOVIE', 'Primeiro filme assistido', 'Parabéns pelo primeiro filme assistido', 'assets/achievements/filme-1.png', 'MOVIE'),
('WATCH_5_MOVIES', '5 filmes assistidos', 'Assistiu 5 filmes', 'assets/achievements/filme-5.png', 'MOVIE'),
('WATCH_10_MOVIES', '10 filmes assistidos', 'Assistiu 10 filmes', 'assets/achievements/filme-10.png', 'MOVIE'),
('WATCH_FIRST_SERIES', 'Primeira série assistida', 'Parabéns pela primeira série assistida', 'assets/achievements/serie-1.png', 'SERIES'),
('WATCH_5_SERIES', '5 séries assistidas', 'Assistiu 5 séries', 'assets/achievements/serie-5.png', 'SERIES'),
('WATCH_10_SERIES', '10 séries assistidas', 'Assistiu 10 séries', 'assets/achievements/serie-10.png', 'SERIES'),
('COMPLETE_FIRST_GAME', 'Primeiro jogo concluído', 'Parabéns pelo primeiro jogo concluído', 'assets/achievements/jogo-1.png', 'GAME'),
('COMPLETE_5_GAMES', '5 jogos concluídos', 'Concluiu 5 jogos', 'assets/achievements/jogo-5.png', 'GAME'),
('COMPLETE_10_GAMES', '10 jogos concluídos', 'Concluiu 10 jogos', 'assets/achievements/jogo-10.png', 'GAME'),
('COMPLETE_FIRST_GOAL', 'Primeira meta concluída', 'Você concluiu sua primeira meta', 'assets/achievements/meta-1.png', 'GENERAL'),
('COMPLETE_5_GOALS', '5 metas concluídas', 'Você concluiu 5 metas', 'assets/achievements/meta-5.png', 'GENERAL'),
('COMPLETE_10_GOALS', '10 metas concluídas', 'Você concluiu 10 metas', 'assets/achievements/meta-10.png', 'GENERAL'),
('RATE_5_BOOKS', '5 avaliações dadas', 'Você avaliou 5 livros lidos', 'assets/achievements/avaliacao-5.png', 'BOOK');