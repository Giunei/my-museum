# Impacto no frontend — My Museum API

Documento para o time Angular alinhar chamadas após mudanças no backend.

---

## 1. Comentários de perfil (breaking change)

### Antes (dois namespaces)

| Ação | Rota legada | Rota nova |
|------|-------------|-----------|
| Listar | — | `GET /profile/{username}/comments` |
| Criar | `POST /profile-comments` (body com `profileOwnerId`) | `POST /profile/{username}/comments` |
| Editar | `PUT /profile-comments/{id}` | — |
| Apagar | `DELETE /profile-comments/{id}` | — |
| Listar por userId | `GET /profile-comments/{userId}` | — |

### Agora (único namespace)

Tudo em `/profile/{username}/comments`:

| Método | Path | Auth | Body | Resposta |
|--------|------|------|------|----------|
| `GET` | `/profile/{username}/comments?page&size` | opcional* | — | `Page<ProfileCommentResponse>` |
| `POST` | `/profile/{username}/comments` | sim | `{ "content": "..." }` | `201` + comentário |
| `PUT` | `/profile/{username}/comments/{commentId}` | sim | `{ "content": "..." }` | comentário atualizado |
| `DELETE` | `/profile/{username}/comments/{commentId}` | sim | — | `204` sem body |

\* Visitante anônimo só lista se tiver permissão de ver o museu (perfil público ou seguidor aceito). Perfil privado → `403`.

### O que mudar no Angular

**Remover** chamadas a `/profile-comments/*`.

**`profile-comment.service.ts` (exemplo):**

```typescript
// Listar
GET `/profile/${username}/comments?page=${page}&size=${size}`

// Criar
POST `/profile/${username}/comments`  body: { content }

// Editar — username do dono do perfil onde o comentário está
PUT `/profile/${profileOwnerUsername}/comments/${commentId}`  body: { content }

// Apagar
DELETE `/profile/${profileOwnerUsername}/comments/${commentId}`
```

**DTO de criação:** só `{ content: string }`. Campos `profileOwnerId` / `userId` não existem mais.

**Status HTTP:** `POST` retorna `201`; `DELETE` retorna `204` (sem body).

---

## 2. WebSocket (comportamento mais restrito)

### Endpoint

- SockJS: `http://localhost:8080/ws` (no Angular, via proxy: `/api/ws`)
- Prefixo STOMP app: `/app`
- Broker: `/topic`, `/queue`
- Destinos por usuário: `/user/...`

### O que mudou

Antes, o `CONNECT` STOMP **aceitava conexão sem JWT** (ficava anônimo).  
Agora o `CONNECT` **exige** header:

```
Authorization: Bearer <accessToken>
```

Sem token ou com token inválido/expirado → conexão **rejeitada**.

### O que conferir no front

No cliente STOMP (ex.: `@stomp/ng2-stompjs` ou similar), ao conectar:

```typescript
client.connect(
  { Authorization: `Bearer ${accessToken}` },
  onConnect,
  onError
);
```

- Renovar/reconectar quando o access token for atualizado (refresh).
- Tratar falha de conexão após logout ou token expirado.

### CORS / origem

O backend usa `app.frontend.url` (dev: `http://localhost:4200`). Em produção, definir `FRONTEND_URL=https://mymuseum.giunei.dev` no servidor.

---

## 3. Actuator (sem impacto direto no Angular)

Endpoints de monitoramento Spring Boot:

| Path | Quem acessa |
|------|-------------|
| `GET /actuator/health` | Público (load balancer / health check) |
| Demais `/actuator/*` | **Bloqueados** (`403`) na API |

O front **não deve** chamar actuator. Só infra (Docker, K8s, painel do provedor).

---

## 4. `DELETE /cache/clear` — perfil `dev`, não “só localhost”

### O que `@Profile("dev")` significa

O controller **só existe** quando o Spring está com profile ativo **`dev`**.

- **Não** é amarrado a localhost.
- Se alguém subir o JAR na cloud com `SPRING_PROFILES_ACTIVE=dev`, o endpoint **existiria** lá também.
- Em produção use **`SPRING_PROFILES_ACTIVE=prod`**: o bean nem carrega → rota inexistente (`404`).

Hoje o `application.yml` default é `spring.profiles.active: dev` — por isso funciona no seu PC local.

### Contrato

- `DELETE /cache/clear` → `204 No Content`
- Limpa todos os caches Redis/Spring registrados.
- **Não chamar do front em produção** — é ferramenta de desenvolvimento (Postman, curl).

---

## 5. Upload de foto de perfil

`POST /users/upload-profile-image` (multipart `file`):

- **Aceitos:** JPG/JPEG, PNG, WebP (máx. 2MB)
- **Rejeitados:** GIF e qualquer outro tipo (PDF, SVG, etc.), inclusive GIF renomeado

Resposta de erro (`400`):

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Arquivo não permitido. Envie uma imagem JPG, PNG ou WebP (GIF não é aceito).",
  "path": "/users/upload-profile-image"
}
```

Exibir o campo **`error`** no toast/alerta do usuário.

---

## 5b. Editar perfil — username e email (`PATCH /profile`)

Body (campos novos opcionais; omitir = não altera):

```json
{
  "name": "...",
  "nationality": "...",
  "gender": "...",
  "birthDate": "2000-01-01",
  "bio": "...",
  "username": "novo_user",
  "email": "novo@email.com"
}
```

### Regras

| Campo | Regra |
|-------|--------|
| `username` | Só a cada **15 dias**. Se ainda no cooldown → `400` com mensagem. |
| `email` (já tinha email) | Só se `emailVerified === true`. Senão → `403` *"Confirme o email atual antes de alterá-lo"*. Depois fica pendente de verificação de novo. |
| `email` (não tinha email) | Pode adicionar; envia e-mail de verificação (igual cadastro). |

### Resposta

```json
{
  "name": "...",
  "nationality": "...",
  "gender": "...",
  "username": "novo_user",
  "email": "novo@email.com",
  "emailVerified": false,
  "emailVerificationSent": true,
  "nextUsernameChangeAvailableAt": "2026-07-28T12:00:00",
  "accessToken": "...",
  "refreshToken": "...",
  "expiresIn": 3600
}
```

- Se **username** mudou: gravar `accessToken` / `refreshToken` (JWT antigo fica inválido).
- Se username **não** mudou: tokens vêm `null` — manter os atuais.
- Se `emailVerificationSent`: avisar o usuário para confirmar o e-mail.

### `GET /profile/me`

Novo campo `account` (só no próprio perfil; público continua `null`):

```json
{
  "account": {
    "email": "me@email.com",
    "emailVerified": true,
    "nextUsernameChangeAvailableAt": null
  }
}
```

`nextUsernameChangeAvailableAt: null` = pode alterar username agora.

---

## 5c. Rate limit (anti-bot)

| Endpoint | Limite | Escopo |
|----------|--------|--------|
| `POST /auth/login` | 5 / minuto | por IP |
| `POST /auth/register` | 3 / hora | por IP |

Resposta `429`:

```json
{
  "timestamp": "...",
  "status": 429,
  "error": "Muitas tentativas de login. Tente novamente em breve.",
  "path": "/auth/login"
}
```

Header `Retry-After` (segundos). Mostrar mensagem amigável no front.

---

## 5d. `MediaStatus.OWNED` (jogos Steam / biblioteca)

Novo valor de status:

| Status | Uso |
|--------|-----|
| `PENDING` | Wishlist (“quero jogar/ler/assistir”) |
| `OWNED` | Na coleção / biblioteca, sem progresso definido |
| `IN_PROGRESS` / `COMPLETED` / `ABANDONED` | Progresso do usuário |

- Import Steam grava jogos novos como **`OWNED`** (não mais `PENDING`).
- Wishlist (`/media/wishlist`, filtros “quero jogar”) continua só com `PENDING`.
- Filtrar biblioteca: `GET /games?status=OWNED` (ou listagem geral sem filtro).

---

## 5e. Cache de busca de livros (Google Books quota)

- `GET /books/search` agora usa Redis (`books:search`, TTL **12h**)
- Curated (`books:curated`) TTL **24h**
- Mesma busca (query/filtros/página) não consome cota de novo até expirar
- Resultado pode ficar até 12h “congelado” (ratings etc.) — trade-off aceitável vs cota diária

---

## 6. Mudanças anteriores (ainda válidas)

### `ProfileResponse`

- Campo `highlights` removido.
- `visibility`: `privateProfile`, `canViewFullProfile`, `followStatus`.
- `totalItems` pode ser `null` se visitante não vê o museu.

### Endpoints `/museums` removidos

Sem impacto se o front já não usava.

### Banco local

Migrations consolidadas em V1–V3. Reset do banco ao trocar schema base.

---

## 7. Checklist rápido para o front

- [ ] Migrar edit/delete de comentários para `/profile/{username}/comments/{commentId}`
- [ ] Remover `POST /profile-comments` e `GET /profile-comments/{userId}`
- [ ] WebSocket: enviar `Authorization: Bearer` no `CONNECT`
- [ ] Tratar `201` no POST de comentário e `204` no DELETE
- [ ] Não depender de `/cache/clear` nem `/actuator/*` na aplicação
- [ ] Upload de perfil: mostrar `error` do `400` (GIF / tipo inválido)
- [ ] `PATCH /profile`: username/email + trocar tokens se username mudar; usar `account` do `/profile/me`
- [ ] Login/cadastro: tratar `429` (rate limit) e header `Retry-After`
- [ ] `GET /users/{username}/followers` e `/following` — listas (perfil público ou dono)
- [ ] Jogos: tratar `OWNED` (biblioteca Steam); wishlist só `PENDING`
- [ ] Busca de livros: cache 12h — resultados podem estar um pouco defasados
