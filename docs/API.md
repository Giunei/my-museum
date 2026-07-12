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
| GET | `/profile/me` | sim |
| PATCH | `/profile` | sim |
| PATCH | `/profile/theme` | sim |
| PATCH | `/profile/privacy` | sim |
| GET | `/users/username/{username}/profile` | opcional |

`ProfileResponse` inclui `visibility` (`privateProfile`, `canViewFullProfile`, `followStatus`).  
`totalItems` é `null` quando o visitante não tem acesso ao museu.

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

Perfil privado: follow vira `PENDING` até o dono aceitar.

---

## Perfil público por username

Prefixo: `/users/{username}/`

Requer permissão (dono, seguidor aceito ou perfil público). Caso contrário: **403**.

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

Recomendações (`for-you` e `maybe-you-like`) em livros, filmes, séries e jogos aceitam `limitPerBucket` (default `4`, min `1`, max `20`). Controla quantos itens por categoria editorial (trending, bestseller, classic) entram na resposta. Sem preferências de onboarding, `for-you` retorna `[]`.
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
