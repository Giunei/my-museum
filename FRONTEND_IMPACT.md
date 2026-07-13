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

## 5. Mudanças anteriores (ainda válidas)

### `ProfileResponse`

- Campo `highlights` removido.
- `visibility`: `privateProfile`, `canViewFullProfile`, `followStatus`.
- `totalItems` pode ser `null` se visitante não vê o museu.

### Endpoints `/museums` removidos

Sem impacto se o front já não usava.

### Banco local

Migrations consolidadas em V1–V3. Reset do banco ao trocar schema base.

---

## 6. Checklist rápido para o front

- [ ] Migrar edit/delete de comentários para `/profile/{username}/comments/{commentId}`
- [ ] Remover `POST /profile-comments` e `GET /profile-comments/{userId}`
- [ ] WebSocket: enviar `Authorization: Bearer` no `CONNECT`
- [ ] Tratar `201` no POST de comentário e `204` no DELETE
- [ ] Não depender de `/cache/clear` nem `/actuator/*` na aplicação
