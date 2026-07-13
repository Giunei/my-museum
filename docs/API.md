# API — My Museum

Base: `http://localhost:8080` (front usa `/api` via proxy).

Erros seguem `{ "timestamp", "status", "message", "path" }`.

---

## Auth

| Método | Path | Auth |
|--------|------|------|
| POST | `/auth/register` | — |
| POST | `/auth/login` | — |
| POST | `/auth/refresh` | — |
| POST | `/auth/forgot-password` | — |
| POST | `/auth/reset-password` | — |
| GET | `/auth/verify-email?token=` | — |
| POST | `/auth/resend-verification` | `{ "email" }` |

Login/register retornam `accessToken`, `refreshToken`, `expiresInSeconds`.

---

## Perfil

| Método | Path | Auth |
|--------|------|------|
| GET | `/profile/me` | sim | inclui `account` (email, emailVerified, nextUsernameChangeAvailableAt) |
| PATCH | `/profile` | sim | body pode incluir `username` e `email` (opcionais) |
| PATCH | `/profile/theme` | sim |
| PATCH | `/profile/privacy` | sim |
| GET | `/users/username/{username}/profile` | opcional |

`ProfileResponse` inclui `visibility` (`privateProfile`, `canViewFullProfile`, `followStatus`).

| Campo | Significado |
|-------|-------------|
| `totalItems` | Quantidade de itens na biblioteca (qualquer status) |
| `ratingsCount` | Quantidade de **notas dadas** (itens concluídos com `rating` preenchido) |

Ambos são `null` quando o visitante não tem acesso ao museu.

---

## Follow

| Método | Path |
|--------|------|
| POST | `/follow/{username}` → `{ "status": "ACCEPTED" \| "PENDING" }` |
| DELETE | `/follow/{username}` |
| GET | `/follow/{username}/status` |
| GET | `/follow/requests` |
| POST | `/follow/requests/{username}/accept` |
| POST | `/follow/requests/{username}/reject` |
| GET | `/users/{username}/followers?page&size` |
| GET | `/users/{username}/following?page&size` |

`GET /users/{username}/followers` e `.../following`: dono sempre; outros só se o perfil **não** for privado (`403` caso contrário). Pública (sem auth ok). Resposta: `Page` de `{ userId, username, profileImageUrl }`.

Perfil privado: follow vira `PENDING` até o dono aceitar.

---

## Perfil público por username

Prefixo: `/users/{username}/`

Requer permissão (dono, seguidor aceito ou perfil público). Caso contrário: **403**.

- `followers`, `following` — exceção: só dono **ou** perfil público (seguidor aceito de privado **não** vê)
- `achievements`, `achievements/count`
- `activities/recent`
- `books/highlighted`, `books/summary`, `books/favorite-authors`, `books/reading-now`
- `movies/highlighted`, `movies/summary`
- `series/highlighted`, `series/summary`, `series/watching-now`
- `games/highlighted`, `games/summary`, `games/all`, `games/most-played`
- `goals`, `preferences`, `collections`, `media`, `media/wishlist`
- `collections/{id}/media`
- `lol/rank`, `lol/connection-status`, `steam/summary`

Comentários no perfil (`/profile/{username}/comments`):

| Método | Path | Auth |
|--------|------|------|
| GET | `/profile/{username}/comments?page&size` | opcional* |
| POST | `/profile/{username}/comments` | sim |
| PUT | `/profile/{username}/comments/{commentId}` | sim |
| DELETE | `/profile/{username}/comments/{commentId}` | sim |

Body criar/editar: `{ "content": "..." }`. POST → `201`; DELETE → `204`.

\* Listagem exige permissão de ver o museu (perfil público, dono ou seguidor aceito).

Rotas legadas `/profile-comments/*` **removidas**.

---

## Coleção (JWT = usuário logado)

| Área | Principais rotas |
|------|------------------|
| Mídia | `GET/POST/PATCH/DELETE /media` |
| Livros | `/books/search`, `/books/summary`, recomendações em `/books/recommendations` |

Recomendações (`for-you` e `maybe-you-like`) em livros, filmes, séries e jogos aceitam `limitPerBucket` (default `10`, min `1`, max `20`). Controla o **total** de itens na lista plana da resposta (misturando trending/bestseller/classic). Sem preferências de onboarding, `for-you` retorna `[]`.
| Filmes / Séries | `/movies/*`, `/series/*` + streams em `/reactive/*` |
| Jogos | `/games/*`, Steam `/steam/*` |
| Metas | `/goals` |
| Preferências | `/preference/me` |
| Coleções | `/collections` |

---

## Home

| GET | `/home/statistics` |
| GET | `/home/popular-profiles` |

---

## WebSocket

- Endpoint SockJS: `/ws`
- `CONNECT` STOMP exige header `Authorization: Bearer <accessToken>`
- Sem token válido → conexão rejeitada

---

## Infra (não usar no front)

| Recurso | Comportamento |
|---------|----------------|
| `GET /actuator/health` | Público (health check) |
| Outros `/actuator/*` | Bloqueados |
| `DELETE /cache/clear` | Só com Spring profile `dev` ativo |

---
