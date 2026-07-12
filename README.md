# My Museum

Build Status

![Build](https://github.com/Giunei/my-museum/actions/workflows/backend.yml/badge.svg)

API para gerenciar e exibir uma coleção pessoal de livros, filmes, séries e jogos — com perfil social, metas, conquistas e recomendações.

**Stack:** Java 25 · Spring Boot 4 · PostgreSQL · Redis · JWT

## Rodando local

```bash
# Variáveis (application-dev.yml)
DB_PASSWORD=postgres
JWT_SECRET=<base64 com pelo menos 256 bits>
MAIL_USERNAME=
MAIL_PASSWORD=

# Opcionais: TMDB, Google Books, RAWG, Steam, Riot, Cloudinary

mvn spring-boot:run
```

Front Angular em `http://localhost:4200` com proxy `/api` → `http://localhost:8080`.

## Banco

Flyway aplica as migrations na subida:

| Arquivo | Conteúdo |
|---------|----------|
| `V1__init_schema.sql` | Schema completo |
| `V2__seed_data.sql` | Usuário admin + conquistas |
| `V3__seed_book_catalog.sql` | Catálogo editorial de livros |

**Reset do banco (dev):** dropar e recriar `my_museum`, depois subir a aplicação.

## Estrutura

```
src/main/java/com/giunei/my_museum/
├── common/         config, security, exception, persistence, websocket, storage
├── auth/
├── user/
├── profile/
├── social/         follow + profile comments
├── preference/
├── media/
├── book/ movie/ series/ game/
├── recommendation/
├── achievement/
├── home/
└── integration/    APIs externas (ex.: LoL/Riot)
```

Cada módulo segue `controller` → `service` → `repository` / `entity` / `dto` quando aplicável.

Convenções detalhadas: [docs/CONVENTIONS.md](docs/CONVENTIONS.md).

## API

Ver [docs/API.md](docs/API.md).

Autenticação: `Authorization: Bearer <accessToken>`. Refresh em `POST /auth/refresh`.

## Testes

```bash
mvn test
```
