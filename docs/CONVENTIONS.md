# Convenções do projeto

Padrões adotados para manter o código consistente em revisão de portfólio.

## Estrutura de pacotes

```
com.giunei.my_museum
├── MyMuseumApplication.java
├── common/              # infraestrutura compartilhada
│   ├── config/          # beans Spring (security filter chain, cache, cors, …)
│   ├── security/        # JWT filter, SecurityUtils, entry point
│   ├── websocket/       # interceptor STOMP
│   ├── storage/         # Cloudinary
│   ├── persistence/       # EntityAbstract, converters JPA
│   └── exception/       # erros da API + GlobalExceptionHandler
├── auth/                # login, registro, refresh, reset de senha
├── user/                # conta, busca, entidades base
├── profile/             # perfil próprio e visibilidade
├── social/              # follow + comentários no perfil
│   └── comment/
├── preference/
├── media/
├── book/ | movie/ | series/ | game/
├── recommendation/
├── achievement/
├── home/
└── integration/lol/     # integrações externas (Riot)
```

Cada módulo de domínio segue, quando aplicável:

`controller` · `service` · `repository` · `entity` · `dto`

Subpastas extras (`catalog`, `client`, `mapper`) só onde o domínio exige.

## API HTTP

| Situação | Padrão |
|----------|--------|
| **GET lista** | Sempre `200` com corpo `[]` se vazio. Nunca `404` por lista vazia. |
| **GET único** | `200` com DTO ou `404` se o recurso não existir. |
| **POST criação** | `201` + DTO criado (ou `200` + DTO quando já existia o padrão legado). |
| **PUT/PATCH** | `200` + DTO atualizado. |
| **DELETE / ações sem corpo** | `204 No Content` — preferir `@ResponseStatus(NO_CONTENT)` ou `ResponseEntity.noContent()`. |
| **Erros** | JSON `ErrorResponse` via `GlobalExceptionHandler`; `500` com mensagem genérica (sem stack). |

**Listas vazias vs body vazio:** retornar `[]` em coleções é correto e esperado. Body vazio em mutações (`DELETE` com `void` e `200`) é o que padronizamos para `204`.

## Nomenclatura

| Camada | Convenção | Exemplos |
|--------|-----------|----------|
| Controller | verbo HTTP implícito na rota; método descreve ação | `list`, `create`, `update`, `delete` |
| Service leitura | `find*` (opcional, um item) · `list*` / `search*` (coleções) | `findByUsername`, `listComments` |
| Service escrita | `create*`, `update*`, `delete*` | `createComment`, `updatePrivacy` |
| Repository | Spring Data: `find*`, `exists*`, `count*` | `findByUsername` |
| DTO request | sufixo `Request` | `CreateProfileCommentRequest` |
| DTO response | sufixo `Response` | `ProfileResponse` |

## Entidades e enums

- Entidades JPA em `entity/`, estendendo `EntityAbstract` quando há `id` + timestamps.
- Enums do domínio ficam no mesmo módulo (`social.entity.FollowStatus`, `media.enums.MediaType`).

## Exceções

- Todas em `common.exception`.
- `@ResponseStatus` na exceção quando o status é fixo; casos especiais no `GlobalExceptionHandler`.
- Não lançar `RuntimeException` genérica em regra de negócio.

## Migrations Flyway

- `V{n}__descricao_snake.sql`, numeradas em ordem.
- Schema consolidado em poucos arquivos em dev; reset de banco ao mudar V1.

## O que ainda pode divergir (dívida técnica conhecida)

Alguns controllers legados ainda usam `void` em `DELETE` (retorna `200` vazio). A meta é migrar para `204` sem quebrar o front. Há dois namespaces de comentários de perfil — ver `docs/API.md`.
