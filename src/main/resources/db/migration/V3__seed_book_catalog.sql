-- Seed simples de livros de referência para recomendações locais
-- A ideia é manter um catálogo local com títulos clássicos, famosos e em alta.

INSERT INTO book_catalog (id, title, author, language, editorial_category, created_at, updated_at)
VALUES
    (nextval('book_catalog_id_seq'), 'Dom Casmurro', 'Machado de Assis', 'pt', 'CLASSIC', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 'pt', 'CLASSIC', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'Harry Potter e a Pedra Filosofal', 'J. K. Rowling', 'pt', 'BESTSELLER', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'A Biblioteca da Meia-Noite', 'Matt Haig', 'pt', 'BESTSELLER', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'A Empregada', 'Freida McFadden', 'pt', 'TRENDING', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'Verity', 'Colleen Hoover', 'pt', 'TRENDING', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'It: A Coisa', 'Stephen King', 'pt', 'BESTSELLER', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'Orgulho e Preconceito', 'Jane Austen', 'pt', 'CLASSIC', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'Hábitos Atômicos', 'James Clear', 'pt', 'BESTSELLER', NOW(), NOW()),
    (nextval('book_catalog_id_seq'), 'A Hora da Estrela', 'Clarice Lispector', 'pt', 'CLASSIC', NOW(), NOW());

INSERT INTO book_catalog_genre (book_catalog_id, genre)
VALUES
    (currval('book_catalog_id_seq') - 9, 'DRAMA'),
    (currval('book_catalog_id_seq') - 8, 'FANTASY'),
    (currval('book_catalog_id_seq') - 8, 'DRAMA'),
    (currval('book_catalog_id_seq') - 7, 'FANTASY'),
    (currval('book_catalog_id_seq') - 7, 'MYSTERY'),
    (currval('book_catalog_id_seq') - 6, 'DRAMA'),
    (currval('book_catalog_id_seq') - 6, 'ROMANCE'),
    (currval('book_catalog_id_seq') - 5, 'HORROR'),
    (currval('book_catalog_id_seq') - 5, 'MYSTERY'),
    (currval('book_catalog_id_seq') - 4, 'ROMANCE'),
    (currval('book_catalog_id_seq') - 4, 'DRAMA'),
    (currval('book_catalog_id_seq') - 3, 'HORROR'),
    (currval('book_catalog_id_seq') - 3, 'MYSTERY'),
    (currval('book_catalog_id_seq') - 2, 'ROMANCE'),
    (currval('book_catalog_id_seq') - 2, 'DRAMA'),
    (currval('book_catalog_id_seq') - 1, 'DRAMA'),
    (currval('book_catalog_id_seq') - 1, 'MYSTERY'),
    (currval('book_catalog_id_seq'), 'DRAMA');

