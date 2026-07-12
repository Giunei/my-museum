# My Museum

<p align="center">
  <img src="docs/images/homeMM.png" alt="My Museum — Home" width="900"/>
</p>

<p align="center">
  <a href="https://my-museum-ui-zeta.vercel.app"><strong>Abrir a aplicação</strong></a>
  ·
  <a href="https://github.com/Giunei/my-museum/actions/workflows/backend.yml"><img src="https://github.com/Giunei/my-museum/actions/workflows/backend.yml/badge.svg" alt="Build"/></a>
</p>

<p align="center">
  <em>Seu museu pessoal de entretenimento — livros, filmes, séries e jogos.</em>
</p>

---

## Descrição

**My Museum** é uma plataforma social onde cada usuário constrói e exibe seu próprio museu de mídia. Cataloge o que leu, assistiu ou jogou, avalie obras, defina metas, desbloqueie conquistas, siga outros museus e receba recomendações personalizadas.

O projeto foi desenvolvido como portfólio full-stack, com API em **Spring Boot 4 / Java 25** e interface em **Angular**, deploy em Vercel + Railway + Neon + Upstash.

| | |
|---|---|
| **Frontend** | [my-museum-ui-zeta.vercel.app](https://my-museum-ui-zeta.vercel.app) |
| **API (prod)** | [my-museum-production.up.railway.app](https://my-museum-production.up.railway.app) |
| **Versão** | `1.0.0` |

---

## Funcionalidades

- **Autenticação** — registro, login JWT, refresh token, verificação de e-mail e recuperação de senha
- **Perfil** — público ou privado, tema, bio, foto (Cloudinary), comentários e sistema de seguidores
- **Biblioteca** — livros, filmes, séries e jogos com status (quero ler / em progresso / concluído), notas e destaques
- **Gamificação** — conquistas e metas por tipo de mídia
- **Recomendações** — *for you* e *maybe you like* com base em preferências e avaliações
- **Integrações** — Steam (OpenID + sync de biblioteca), Google Books, TMDB, RAWG, Riot/LoL
- **Tempo real** — notificação de conquista via WebSocket (STOMP)
- **Cache** — Redis (Upstash em produção) com fallback em memória
- **Busca** — descoberta de obras nas APIs externas e adição à biblioteca

---

## Screenshots

### Home

<p align="center">
  <img src="docs/images/homeMM.png" alt="Home" width="850"/>
</p>

### Biblioteca de jogos

<p align="center">
  <img src="docs/images/library_games.png" alt="Biblioteca de jogos" width="850"/>
</p>

### Perfil

<p align="center">
  <img src="docs/images/profile.png" alt="Perfil" width="850"/>
</p>

### Recomendações

<p align="center">
  <img src="docs/images/recomendation.png" alt="Recomendações" width="850"/>
</p>

### Busca

<p align="center">
  <img src="docs/images/search.png" alt="Busca" width="850"/>
</p>

---

## Tecnologias

### Backend
| Tecnologia | Uso |
|------------|-----|
| Java 25 | Runtime |
| Spring Boot 4 | API REST, Security, Data JPA, Mail, Actuator |
| Spring WebFlux / Reactor | Streams SSE (curated) |
| PostgreSQL + Flyway | Persistência e migrations |
| Redis / Lettuce | Cache |
| JWT (jjwt) | Auth stateless |
| STOMP + SockJS | WebSocket |
| Resend | E-mail transacional (prod) |
| Maven | Build |
| Docker | Imagem de deploy (Temurin 25) |

### Frontend
| Tecnologia | Uso |
|------------|-----|
| Angular | SPA |
| Vercel | Hosting |

### Infra
| Serviço | Papel |
|---------|--------|
| Railway | API |
| Neon | PostgreSQL |
| Upstash | Redis |
| GitHub Actions | CI (`mvn verify`) |

### APIs externas
Google Books · TMDB · RAWG · Steam Web API · Riot Games · Cloudinary

---

## Arquitetura

```
┌─────────────┐     HTTPS      ┌──────────────────┐
│  Angular UI │ ─────────────► │  Spring Boot API │
│   (Vercel)  │ ◄─── WS/STOMP ─│    (Railway)     │
└─────────────┘                └────────┬─────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
              ┌──────────┐        ┌──────────┐        ┌──────────┐
              │   Neon   │        │ Upstash  │        │ Resend / │
              │ Postgres │        │  Redis   │        │ externos │
              └──────────┘        └──────────┘        └──────────┘
```

A API é organizada por **módulos de domínio** (`auth`, `media`, `book`, `movie`, `series`, `game`, `recommendation`, `social`, `achievement`, etc.), cada um com `controller` → `service` → `repository` / `entity` / `dto`.

Convenções de código: [docs/CONVENTIONS.md](docs/CONVENTIONS.md).

---

## Como executar

### Pré-requisitos
- JDK **25**
- Maven 3.9+
- Docker (Postgres + Redis via `docker-compose`) **ou** instâncias locais equivalentes
- Conta/credenciais opcionais: Gmail ou Resend, TMDB, Google Books, RAWG, Steam, Cloudinary

### 1. Infra local

```bash
docker compose up -d
```

Sobe PostgreSQL (`localhost:5432`, database `my_museum`) e Redis (`localhost:6379`).

### 2. Variáveis (dev)

Defina no ambiente ou no run config (ver também `application-dev.yml`):

```bash
DB_PASSWORD=postgres
JWT_SECRET=<base64 com pelo menos 256 bits>

# E-mail local (SMTP Gmail + App Password)
MAIL_USERNAME=
MAIL_PASSWORD=

# Opcionais
GOOGLE_BOOKS_API_KEY=
TMDB_API_TOKEN=
RAWG_API_KEY=
STEAM_API_KEY=
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
```

### 3. API

```bash
mvn spring-boot:run
```

API em `http://localhost:8080`. Flyway aplica as migrations na subida.

### 4. Frontend

Suba o app Angular em `http://localhost:4200` com proxy `/api` → `http://localhost:8080`.

---

## Deploy

| Camada | Plataforma | Notas |
|--------|------------|--------|
| UI | **Vercel** | Angular |
| API | **Railway** | Dockerfile (JDK 25), profile `prod` |
| DB | **Neon** | `DATABASE_URL` / `USERNAME` / `PASSWORD` |
| Cache | **Upstash** | `REDIS_URL=rediss://...` |
| E-mail | **Resend** | `RESEND_API_KEY` (SMTP é bloqueado no Railway Hobby) |

Variáveis importantes em produção:

```text
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=...
DATABASE_URL=...
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
REDIS_URL=rediss://default:...@....upstash.io:6379
RESEND_API_KEY=re_...
FRONTEND_URL=https://my-museum-ui-zeta.vercel.app
STEAM_REALM=https://my-museum-production.up.railway.app
STEAM_API_KEY=...
```

---

## Estrutura do projeto

```text
my-museum/
├── docs/
│   ├── images/           screenshots do README
│   ├── API.md
│   └── CONVENTIONS.md
├── src/main/java/com/giunei/my_museum/
│   ├── common/           config, security, exception, websocket, storage
│   ├── auth/
│   ├── user/ profile/ social/ preference/
│   ├── media/
│   ├── book/ movie/ series/ game/
│   ├── recommendation/
│   ├── achievement/
│   ├── home/
│   └── integration/      LoL / Riot, etc.
├── src/main/resources/
│   ├── application*.yml
│   └── db/migration/     Flyway (V1 schema, V2 seed, V3 catalog, …)
├── src/test/
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## API

Documentação dos endpoints: **[docs/API.md](docs/API.md)**.

Autenticação:

```http
Authorization: Bearer <accessToken>
```

Refresh: `POST /auth/refresh` com o refresh token.

Health: `GET /actuator/health`  
WebSocket: SockJS `/ws` · destino de conquistas `/user/queue/achievements`

---

## Testes / CI

```bash
mvn test
# ou o pipeline completo:
mvn -B clean verify
```

**GitHub Actions** (`.github/workflows/backend.yml`): em push/PR para `main`/`master`, roda Java 25 + `mvn clean verify`.

![Build](https://github.com/Giunei/my-museum/actions/workflows/backend.yml/badge.svg)

---

## Roadmap

- [ ] **Reviews** — textos de opinião além da nota numérica
- [ ] **Comunidade** — feed de atividades, listas compartilhadas e mais interações sociais
- [ ] **UI/UX** — polimento de interfaces (biblioteca, recomendações, onboarding)
- [ ] **Notificações** — mais eventos em tempo real (seguidores, comentários, metas)
- [ ] **Domínio próprio + e-mail** — enviar verificação com domínio verificado no Resend (melhor entregabilidade)
- [ ] **Importações** — mais fontes além da Steam

---

## Autor

**Giunei Philippi Machado Júnior**

- LinkedIn: [linkedin.com/in/giunei](https://www.linkedin.com/in/giunei/)
- E-mail: [pmjgiunei@gmail.com](mailto:pmjgiunei@gmail.com)
- GitHub: [github.com/Giunei](https://github.com/Giunei)

---

<p align="center">
  <sub>My Museum · v1.0.0 · Java 25 · Spring Boot 4 · Angular</sub>
</p>
