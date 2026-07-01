# API Documentation - My Museum Backend

## Modelos de Resposta

### BookResponse
```json
{
  "id": "string",
  "title": "string",
  "authors": ["string"],
  "description": "string",
  "thumbnail": "string",
  "infoLink": "string",
  "previewLink": "string",
  "language": "string",
  "pageCount": 0,
  "averageRating": 0.0,
  "ratingsCount": 0,
  "userCollectionInfo": {
    "inCollection": true,
    "status": "COMPLETED",
    "rating": 5,
    "finishedAt": "2024-01-15",
    "currentSeason": null,
    "currentEpisode": null
  }
}
```

### GameResponse
```json
{
  "id": 0,
  "name": "string",
  "slug": "string",
  "thumbnail": "string",
  "releaseDate": "2024-01-01",
  "metacritic": 0,
  "developer": "string",
  "publisher": "string",
  "genres": ["string"],
  "platforms": ["PC", "PlayStation 4", "Xbox One", "Nintendo Switch"],
  "stores": [
    {
      "name": "Steam",
      "url": "https://store.steampowered.com/app/..."
    },
    {
      "name": "PlayStation Store",
      "url": "https://store.playstation.com/..."
    }
  ],
  "userCollectionInfo": {
    "inCollection": true,
    "status": "COMPLETED",
    "rating": 5,
    "finishedAt": "2024-01-15",
    "currentSeason": null,
    "currentEpisode": null
  }
}
```

### MovieResponse
```json
{
  "id": 0,
  "title": "string",
  "originalTitle": "string",
  "overview": "string",
  "thumbnail": "string",
  "backdropUrl": "string",
  "releaseDate": "2024-01-01",
  "voteAverage": 0.0,
  "voteCount": 0,
  "popularity": 0.0,
  "originalLanguage": "string",
  "adult": false,
  "video": false,
  "userCollectionInfo": {
    "inCollection": true,
    "status": "COMPLETED",
    "rating": 5,
    "finishedAt": "2024-01-15",
    "currentSeason": null,
    "currentEpisode": null
  }
}
```

### SeriesResponse
```json
{
  "id": 0,
  "name": "string",
  "originalName": "string",
  "overview": "string",
  "thumbnail": "string",
  "backdropUrl": "string",
  "firstAirDate": "2024-01-01",
  "voteAverage": 0.0,
  "voteCount": 0,
  "popularity": 0.0,
  "originalLanguage": "string",
  "originCountry": ["string"],
  "userCollectionInfo": {
    "inCollection": true,
    "status": "IN_PROGRESS",
    "rating": 5,
    "finishedAt": null,
    "currentSeason": 3,
    "currentEpisode": 7
  }
}
```

### SeriesDetailResponse
```json
{
  "id": 0,
  "name": "string",
  "originalName": "string",
  "overview": "string",
  "posterPath": "string",
  "backdropPath": "string",
  "firstAirDate": "2024-01-01",
  "voteAverage": 0.0,
  "voteCount": 0,
  "popularity": 0.0,
  "originalLanguage": "string",
  "genreIds": [1, 2, 3],
  "originCountry": ["string"],
  "numberOfSeasons": 5,
  "numberOfEpisodes": 50,
  "seasons": [
    {
      "seasonNumber": 1,
      "name": "Season 1",
      "episodeCount": 10,
      "posterPath": "string"
    }
  ]
}
```

### SeasonDetailResponse
```json
{
  "seasonNumber": 1,
  "name": "Season 1",
  "episodeCount": 10,
  "posterPath": "string",
  "overview": "string",
  "airDate": "2024-01-01",
  "episodes": [
    {
      "episodeNumber": 1,
      "name": "Episode 1",
      "overview": "string",
      "airDate": "2024-01-01",
      "stillPath": "string",
      "voteAverage": 8.5,
      "voteCount": 100,
      "runtime": 45
    }
  ]
}
```

### ReadingNowResponse
```json
{
  "id": 0,
  "title": "string",
  "thumbnail": "string",
  "pageCount": 0
}
```

### FavoriteAuthorResponse
```json
{
  "author": "string",
  "bookCount": 0
}
```

### WatchingNowResponse
```json
{
  "id": 0,
  "title": "string",
  "thumbnail": "string",
  "pageCount": 0,
  "currentSeason": 1,
  "currentEpisode": 5
}
```

### PreferenceResponse
```json
{
  "bookGenres": ["FICÇÃO", "AVENTURA"],
  "movieGenres": ["AÇÃO", "COMÉDIA"],
  "seriesGenres": ["DRAMA", "FICÇÃO CIENTÍFICA"],
  "gameGenres": ["RPG", "AÇÃO"]
}
```

### UserGoalResponse
```json
{
  "id": 0,
  "type": "BOOK",
  "goalType": "MONTHLY",
  "target": 10,
  "progress": 5,
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "completed": false
}
```

### BookSummaryResponse
```json
{
  "totalBooks": 25,
  "booksRead": 18,
  "totalPagesRead": 4500
}
```

### MovieSummaryResponse
```json
{
  "totalMovies": 30,
  "moviesWatched": 22,
  "favoriteGenres": ["Ação", "Comédia", "Ficção Científica"]
}
```

### SeriesSummaryResponse
```json
{
  "totalSeries": 15,
  "seriesWatched": 10,
  "totalEpisodesWatched": null,
  "favoriteGenres": ["Drama", "Comédia", "Ação"]
}
```

### SteamSummaryResponse
```json
{
  "gamesInLibrary": 150,
  "completedGames": 45,
  "platinumedGames": 12,
  "totalHoursPlayed": 1250.5,
  "totalAchievements": 850,
  "favoriteGenres": ["RPG", "Ação", "Aventura"]
}
```

### SteamConnectionStatusResponse
```json
{
  "connected": true,
  "steamId64": "76561198000000000",
  "personaName": "NomeDoUsuario",
  "avatarUrl": "https://..."
}
```
- **connected**: Boolean indicando se a conta Steam está conectada
- **steamId64**: ID da conta Steam (null se não conectado)
- **personaName**: Nome do usuário na Steam (null se não conectado)
- **avatarUrl**: URL do avatar na Steam (null se não conectado)

### SteamSyncStatusResponse
```json
{
  "syncing": true,
  "current": 75,
  "total": 148,
  "message": "Sincronizando: Game Name"
}
```
- **syncing**: Boolean indicando se a sincronização está em andamento
- **current**: Número de jogos já sincronizados
- **total**: Total de jogos a serem sincronizados
- **message**: Mensagem de status atual

### AuthResponse
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresInSeconds": 3600
}
```

## Autenticação

**IMPORTANTE**: A autenticação usa tokens JWT enviados no header `Authorization: Bearer <token>`.

### POST /auth/register
- Descrição: Registrar novo usuário
- Request Body: `RegisterRequest`
  ```json
  {
    "username": "usuario",
    "email": "usuario@email.com",
    "password": "senha123"
  }
  ```
- Response: `AuthResponse`
  ```json
  {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "expiresInSeconds": 3600
  }
  ```
- Autenticação: Não requerida

### POST /auth/login
- Descrição: Fazer login
- Request Body: `LoginRequest`
  ```json
  {
    "username": "usuario",
    "password": "senha123"
  }
  ```
- Response: `AuthResponse`
  ```json
  {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "expiresInSeconds": 3600
  }
  ```
- Autenticação: Não requerida

### POST /auth/refresh
- Descrição: Refresh token (renovar access token)
- Request Body: `RefreshTokenRequest`
  ```json
  {
    "refreshToken": "eyJhbGci..."
  }
  ```
- Response: `AuthResponse`
- Autenticação: Não requerida

### POST /auth/logout
- Descrição: Fazer logout
- Response: 204 No Content
- Autenticação: Requerida
- Nota: JWT tokens são stateless, logout é tratado no cliente descartando o token

### GET /auth/verify-email
- Descrição: Verificar email
- Parâmetros: `token` (query) - token de verificação
- Response: `String` (mensagem de sucesso)
- Autenticação: Não requerida

---

## Endpoints Novos para Frontend

### 1. Seguir/Desseguir Usuário

**POST /follow/{username}**
- Descrição: Seguir um usuário
- Parâmetros: `username` (path) - nome de usuário a seguir
- Resposta: 204 No Content
- Autenticação: Requerida

**DELETE /follow/{username}**
- Descrição: Deixar de seguir um usuário
- Parâmetros: `username` (path) - nome de usuário a deixar de seguir
- Resposta: 204 No Content
- Autenticação: Requerida

---

### 2. Conquistas (Achievements)

**GET /achievements?type=BOOK**
- Descrição: Listar todas as conquistas do usuário
- Parâmetros: `type` (query, opcional) - BOOK, MOVIE, SERIES, GAME, GENERAL (se não informado, retorna todos os tipos)
- Resposta: `AchievementResponse[]`
  ```json
  [
    {
      "code": "READ_FIRST_BOOK",
      "name": "Primeiro livro lido",
      "description": "Parabéns pelo primeiro livro lido",
      "imageUrl": "assets/achievements/livro-1.png",
      "unlockedAt": "2024-01-15T10:30:00"
    }
  ]
  ```
- Autenticação: Requerida

**GET /achievements/count**
- Descrição: Retornar quantidade de conquistas do usuário
- Resposta: `long` (número de conquistas)
- Autenticação: Requerida

---

### 3. Atividades Recentes

**GET /activities/recent?limit=10&type=BOOK**
- Descrição: Listar as últimas atividades do usuário (livros lidos, jogos concluídos, filmes/series assistidos)
- Parâmetros:
  - `limit` (query, default: 10) - quantidade de atividades
  - `type` (query, opcional) - BOOK, MOVIE, SERIES, GAME (se não informado, retorna todos os tipos)
- Resposta: `RecentActivityResponse[]`
  ```json
  [
    {
      "id": 1,
      "title": "O Senhor dos Anéis",
      "thumbnail": "https://...",
      "type": "BOOK",
      "finishedAt": "2024-01-15",
      "rating": 5,
      "timeAgo": "2 dias atrás"
    }
  ]
  ```
- Autenticação: Requerida

---

### 4. Destaques (Highlighted)

**GET /books/highlighted**
- Descrição: Listar livros em destaque (até 6)
- Resposta: `UserMediaResponse[]`
- Autenticação: Requerida

**GET /movies/highlighted**
- Descrição: Listar filmes em destaque (até 6)
- Resposta: `UserMediaResponse[]`
- Autenticação: Requerida

**GET /series/highlighted**
- Descrição: Listar séries em destaque (até 6)
- Resposta: `UserMediaResponse[]`
- Autenticação: Requerida

**GET /games/highlighted**
- Descrição: Listar jogos em destaque (até 6)
- Resposta: `UserGameResponse[]`
- Autenticação: Requerida

---

### 5. Integração com Steam

**GET /steam/auth-url**
- Descrição: Gerar URL de autenticação OpenID da Steam
- Resposta: `String` (URL de autenticação)
- Autenticação: Requerida
- Fluxo: O usuário é redirecionado para a página de login da Steam

**GET /steam/callback**
- Descrição: Callback do OpenID da Steam após login
- Parâmetros: Parâmetros de retorno do OpenID (openid.mode, openid.identity, etc.)
- Resposta: 302 Redirect para URL configurada
- Autenticação: Não requerida (callback público)
- Fluxo: Steam redireciona para este endpoint após login bem-sucedido

**POST /steam/connect**
- Descrição: Conectar conta Steam manualmente (sem OpenID)
- Request Body: `ConnectSteamRequest`
  ```json
  {
    "steamId64": "76561198000000000"
  }
  ```
- Resposta: 204 No Content
- Autenticação: Requerida

**POST /steam/sync**
- Descrição: Sincronizar dados da conta Steam (jogos, playtime, achievements)
- Resposta: 202 Accepted
- Autenticação: Requerida
- **Nota**: Este endpoint é assíncrono. Retorna 202 e inicia a sincronização em background. Consulte `/steam/sync/status` para acompanhar o progresso.

**GET /steam/sync/status**
- Descrição: Verificar status da sincronização Steam
- Resposta: `SteamSyncStatusResponse`
  ```json
  {
    "syncing": true,
    "current": 75,
    "total": 148,
    "message": "Sincronizando: Game Name"
  }
  ```
- Autenticação: Requerida
- **Nota**: O front deve consultar este endpoint a cada 2 segundos para mostrar o progresso ao usuário

**GET /steam/summary**
- Descrição: Retornar resumo geral da conta Steam
- Resposta: `SteamSummaryResponse`
  ```json
  {
    "gamesInLibrary": 150,
    "completedGames": 75,
    "platinumedGames": 20,
    "totalHoursPlayed": 1250.5,
    "totalAchievements": 450,
    "favoriteGenres": ["RPG", "Ação"]
  }
  ```
- Autenticação: Requerida

**GET /steam/connection-status**
- Descrição: Verificar status de conexão com conta Steam
- Resposta: `SteamConnectionStatusResponse`
  ```json
  {
    "connected": true,
    "steamId64": "76561198000000000",
    "personaName": "NomeDoUsuario",
    "avatarUrl": "https://..."
  }
  ```
- Autenticação: Requerida
- **Nota**: Se não conectado, retorna `connected: false` com os demais campos null

---

### 6. Resumo de Livros

**GET /books/summary**
- Descrição: Retornar resumo da biblioteca de livros
- Resposta: `BookSummaryResponse`
  ```json
  {
    "totalBooks": 25,
    "booksRead": 18,
    "totalPagesRead": 4500
  }
  ```
- Autenticação: Requerida

---

### 7. Autores Favoritos

**GET /books/favorite-authors**
- Descrição: Retornar autores favoritos (top 10)
- Resposta: `FavoriteAuthorResponse[]`
  ```json
  [
    {
      "author": "J.K. Rowling",
      "bookCount": 5
    },
    {
      "author": "George R.R. Martin",
      "bookCount": 3
    }
  ]
  ```
- Autenticação: Requerida

---

### 8. Lendo Agora

**GET /books/reading-now**
- Descrição: Retornar livros sendo lidos atualmente (até 6)
- Resposta: `ReadingNowResponse[]`
  ```json
  [
    {
      "id": 1,
      "title": "O Hobbit",
      "thumbnail": "https://...",
      "pageCount": 310
    }
  ]
  ```
- Autenticação: Requerida

---

### 9. Resumo de Séries

**GET /series/summary**
- Descrição: Retornar resumo da biblioteca de séries
- Resposta: `SeriesSummaryResponse`
  ```json
  {
    "totalSeries": 15,
    "seriesWatched": 10,
    "totalEpisodesWatched": null,
    "favoriteGenres": ["Drama", "Comédia", "Ação"]
  }
  ```
- Autenticação: Requerida

---

### 10. Assistindo Agora

**GET /series/watching-now**
- Descrição: Retornar séries sendo assistidas atualmente (até 6, com status IN_PROGRESS)
- Resposta: `WatchingNowResponse[]`

**GET /series/{id}/details**
- Descrição: Buscar detalhes completos de uma série (inclui temporadas e episódios)
- Parâmetros:
  - `id` (path) - ID da série no TMDB
- Resposta: `SeriesDetailResponse`
- Autenticação: Não requerida

**GET /series/{id}/season/{seasonNumber}**
- Descrição: Buscar detalhes de uma temporada específica (inclui todos os episódios)
- Parâmetros:
  - `id` (path) - ID da série no TMDB
  - `seasonNumber` (path) - Número da temporada
- Resposta: `SeasonDetailResponse`
- Autenticação: Não requerida
  ```json
  [
    {
      "id": 1,
      "title": "Breaking Bad",
      "thumbnail": "https://...",
      "pageCount": null
    }
  ]
  ```
- Autenticação: Requerida

---

### 11. Resumo de Filmes

**GET /movies/summary**
- Descrição: Retornar resumo da biblioteca de filmes
- Resposta: `MovieSummaryResponse`
  ```json
  {
    "totalMovies": 30,
    "moviesWatched": 22,
    "favoriteGenres": ["Ação", "Comédia", "Ficção Científica"]
  }
  ```
- Autenticação: Requerida

---

### 11. Perfil

**GET /profile/me**
- Descrição: Obter perfil do usuário autenticado
- Resposta: `ProfileResponse`
- Autenticação: Requerida

**PATCH /profile**
- Descrição: Completar perfil do usuário
- Request Body: `CompleteProfileRequest`
  ```json
  {
    "name": "Nome do Usuário",
    "nationality": "BRAZIL",
    "gender": "MALE",
    "birthDate": "1990-01-15",
    "bio": "Minha bio"
  }
  ```
- Resposta: `CompleteProfileResponse`
- Autenticação: Requerida

**PATCH /profile/theme**
- Descrição: Atualizar tema do perfil
- Request Body: `UpdateProfileThemeRequest`
  ```json
  {
    "theme": "DARK"
  }
  ```
- Resposta: 204 No Content
- Autenticação: Requerida
- **Nota**: Temas disponíveis: DEFAULT, BROWN, NEON, GREEN, PURPLE

---

### 12. Comentários de Perfil

**POST /profile-comments**
- Descrição: Criar comentário no perfil de um usuário
- Request Body: `ProfileCommentRequest`
  ```json
  {
    "userId": 5,
    "content": "Ótima coleção!"
  }
  ```
- Resposta: `ProfileCommentResponse`
- Autenticação: Requerida

**GET /profile-comments/{userId}?page=0&size=10**
- Descrição: Listar comentários do perfil de um usuário
- Parâmetros: `userId` (path), `page` (query, default: 0), `size` (query, default: 10)
- Resposta: `Page<ProfileCommentResponse>`
- Autenticação: Requerida

**DELETE /profile-comments/{id}**
- Descrição: Deletar comentário do perfil
- Parâmetros: `id` (path)
- Resposta: 204 No Content
- Autenticação: Requerida

---

### 12. Coleções Personalizadas

**GET /collections?type=BOOK**
- Descrição: Listar coleções do usuário por tipo de mídia
- Parâmetros:
  - `type` (query) - BOOK, MOVIE, SERIES, GAME
- Resposta: `MediaCollectionResponse[]`
- Autenticação: Requerida

**POST /collections**
- Descrição: Criar nova coleção personalizada
- Request Body: `MediaCollectionRequest`
  ```json
  {
    "type": "BOOK",
    "name": "Favoritos",
    "icon": "assets/icons/favorites.png"
  }
  ```
- Resposta: `MediaCollectionResponse`
- Autenticação: Requerida

**DELETE /collections/{id}**
- Descrição: Excluir coleção personalizada
- Parâmetros:
  - `id` (path) - ID da coleção
- Resposta: 204 No Content
- Autenticação: Requerida
- **Nota**: A coleção deve pertencer ao usuário autenticado. Ao excluir a coleção, os itens da biblioteca não são removidos, apenas a associação com a coleção.

---

### 13. Biblioteca (UserMedia)

**GET /media?page=0&size=20&type=BOOK&completed=true**
- Descrição: Listar mídia da biblioteca com filtros
- Parâmetros:
  - `page` (query, default: 0)
  - `size` (query, default: 20, max: 100)
  - `type` (query, opcional) - BOOK, MOVIE, SERIES, GAME
  - `completed` (query, opcional) - true/false
- Resposta: `Page<UserMediaResponse>`
- Autenticação: Requerida

**GET /media/collection/{collectionId}?page=0&size=20**
- Descrição: Listar mídia de uma coleção personalizada específica
- Parâmetros:
  - `collectionId` (path) - ID da coleção personalizada
  - `page` (query, default: 0)
  - `size` (query, default: 20, max: 100)
- Resposta: `Page<UserMediaResponse>`
- Autenticação: Requerida
- **Nota**: A coleção personalizada deve pertencer ao usuário autenticado

**POST /media/{id}/collection/{collectionId}**
- Descrição: Adicionar mídia a uma coleção personalizada
- Parâmetros:
  - `id` (path) - ID da mídia
  - `collectionId` (path) - ID da coleção personalizada
- Resposta: 204 No Content
- Autenticação: Requerida
- **Nota**: Um item pode estar em múltiplas coleções personalizadas

**DELETE /media/{id}/collection/{collectionId}**
- Descrição: Remover mídia de uma coleção personalizada específica
- Parâmetros:
  - `id` (path) - ID da mídia
  - `collectionId` (path) - ID da coleção personalizada
- Resposta: 204 No Content
- Autenticação: Requerida
- **Nota**: Um item pode estar em múltiplas coleções personalizadas; este endpoint remove apenas da coleção especificada

**GET /media/wishlist?type=BOOK**
- Descrição: Listar wishlist (itens com status PENDING) por tipo
- Parâmetros:
  - `type` (query) - BOOK, MOVIE, SERIES, GAME
- Resposta: `UserMediaResponse[]`
- Autenticação: Requerida

**PATCH /media/{id}**
- Descrição: Atualizar mídia da biblioteca
- Parâmetros:
  - `id` (path) - ID da mídia
- Body: `UpdateUserMediaRequest`
- Resposta: `UpdateMediaResult`
- Autenticação: Requerida
- **Nota**: Pode incluir `collectionIds` para adicionar/remover o item de coleções personalizadas

---

### 14. Search e Add na Biblioteca

**Livros:**
- **GET /books/search?query=harry&page=0**
  - Descrição: Buscar livros por query
  - Parâmetros: `query` (query), `page` (query, default: 0)
  - Resposta: `BookResponse[]`
  - Autenticação: Não requerida
- **GET /reactive/books/curated/stream**
  - Descrição: Listar livros curados (stream reativo)
  - Resposta: `Flux<BookResponse>` (Server-Sent Events)
  - Autenticação: Não requerida
  - **Nota**: Se o usuário estiver autenticado, cada item incluirá `userCollectionInfo` com informações da biblioteca do usuário (status, rating, finishedAt, etc.)
- **POST /media** - Adicionar livro à biblioteca

**Filmes:**
- **GET /movies/search?query=avatar&page=0**
  - Descrição: Buscar filmes por query
  - Parâmetros: `query` (query), `page` (query, default: 0)
  - Resposta: `MovieResponse[]`
  - Autenticação: Requerida
- **GET /reactive/movies/curated/stream**
  - Descrição: Listar filmes curados (stream reativo, limitado a 20 itens)
  - Resposta: `Flux<MovieResponse>` (Server-Sent Events)
  - Autenticação: Não requerida
  - **Nota**: Se o usuário estiver autenticado, cada item incluirá `userCollectionInfo` com informações da biblioteca do usuário (status, rating, finishedAt, etc.)
- **POST /media** - Adicionar filme à biblioteca

**Séries:**
- **GET /series/search?query=breaking&page=0**
  - Descrição: Buscar séries por query
  - Parâmetros: `query` (query), `page` (query, default: 0)
  - Resposta: `SeriesResponse[]`
  - Autenticação: Requerida
- **GET /reactive/series/curated/stream**
  - Descrição: Listar séries curadas (stream reativo, limitado a 20 itens)
  - Resposta: `Flux<SeriesResponse>` (Server-Sent Events)
  - Autenticação: Não requerida
  - **Nota**: Se o usuário estiver autenticado, cada item incluirá `userCollectionInfo` com informações da biblioteca do usuário (status, rating, finishedAt, currentSeason, currentEpisode)
- **POST /media** - Adicionar série à biblioteca

**Jogos:**
- **GET /games/search?query=elden&page=1**
  - Descrição: Buscar jogos por query
  - Parâmetros: `query` (query), `page` (query, default: 1)
  - Resposta: `GameResponse[]`
  - Autenticação: Requerida
- **GET /reactive/games/curated/stream**
  - Descrição: Listar jogos curados (stream reativo)
  - Resposta: `Flux<GameResponse>` (Server-Sent Events)
  - Autenticação: Não requerida
  - **Nota**: Se o usuário estiver autenticado, cada item incluirá `userCollectionInfo` com informações da biblioteca do usuário (status, rating, finishedAt, etc.)
- **GET /games/all?status=COMPLETED&sort=PLAYTIME**
  - Descrição: Listar jogos da biblioteca com filtros
  - Parâmetros:
    - `status` (query, opcional) - COMPLETED, PLAYING, BACKLOG, ABANDONED
    - `sort` (query, opcional) - PLAYTIME, RECENTLY_PLAYED, NAME
  - Resposta: `UserGameResponse[]`
  - Autenticação: Requerida
- **POST /games/add** - Adicionar jogo à biblioteca
- **PATCH /games/{id}** - Atualizar informações de um jogo
  - Descrição: Atualizar rating, data de conclusão, destaque, status e platinumed
  - Parâmetros:
    - `id` (path) - ID do jogo
    - `rating` (body, opcional) - Nota de 0 a 5
    - `finishedAt` (body, opcional) - Data de conclusão (ISO 8601)
    - `highlighted` (body, opcional) - Se está em destaque
    - `status` (body, opcional) - COMPLETED, PLAYING, BACKLOG, ABANDONED
    - `platinumed` (body, opcional) - Se foi platinumed
  - Resposta: 204 No Content
  - Autenticação: Requerida

---

### 15. Metas (Goals)

**GET /goals?type=BOOK**
- Descrição: Listar metas por tipo
- Parâmetros: `type` (query, opcional) - BOOK, MOVIE, SERIES, GAME
- Resposta: `UserGoalResponse[]`
- Autenticação: Requerida

---

### 16. Jogos Mais Jogados

**GET /games/most-played?limit=10**
- Descrição: Listar jogos mais jogados (por horas jogadas)
- Parâmetros: `limit` (query, default: 10)
- Resposta: `UserGameResponse[]`
- Autenticação: Requerida

---

### 17. Preferências do Usuário

**GET /preference/me**
- Descrição: Retornar gêneros favoritos do usuário (salvos no onboarding)
- Resposta: `PreferenceResponse`
  ```json
  {
    "bookGenres": ["FICÇÃO", "AVENTURA"],
    "movieGenres": ["AÇÃO", "COMÉDIA"],
    "seriesGenres": ["DRAMA", "FICÇÃO CIENTÍFICA"],
    "gameGenres": ["RPG", "AÇÃO"]
  }
  ```
- Autenticação: Requerida

**POST /preference**
- Descrição: Salvar preferências do usuário (onboarding)
- Request Body: `PreferenceRequest`
- Resposta: 204 No Content
- Autenticação: Requerida

**PUT /preference**
- Descrição: Atualizar preferências do usuário
- Request Body: `PreferenceRequest`
- Resposta: 204 No Content
- Autenticação: Requerida

**GET /preference/options**
- Descrição: Retornar opções de gêneros disponíveis para seleção
- Resposta: `PreferenceOptionsResponse`
  ```json
  {
    "bookGenres": [
      {"value": "FICÇÃO", "label": "Ficção"},
      {"value": "AVENTURA", "label": "Aventura"}
    ],
    "movieGenres": [...],
    "seriesGenres": [...],
    "gameGenres": [...]
  }
  ```
- Autenticação: Não requerida

---

### 18. Busca de Usuários

**GET /users/search?query=joao**
- Descrição: Buscar usuários por nome de usuário (case-insensitive, busca parcial)
- Parâmetros: `query` (query) - texto para buscar no username
- Resposta: `UserSearchResponse[]`
  ```json
  [
    {
      "id": 1,
      "username": "joao123"
    },
    {
      "id": 5,
      "username": "joaosilva"
    }
  ]
  ```
- Autenticação: Requerida
- Nota: Este endpoint é ideal para autocomplete reativo conforme o usuário digita

---

## DTOs Importantes

### CompleteProfileRequest
```json
{
  "name": "Nome do Usuário",
  "nationality": "BRAZIL",
  "gender": "MALE",
  "birthDate": "1990-01-15",
  "bio": "Minha bio"
}
```
- **name**: Nome completo do usuário
- **nationality**: Nacionalidade (BRAZIL, USA, UK, etc.)
- **gender**: Gênero (MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY)
- **birthDate**: Data de nascimento
- **bio**: Biografia do usuário

### CompleteProfileResponse
```json
{
  "name": "Nome do Usuário",
  "nationality": "BRAZIL",
  "gender": "MALE"
}
```

### UpdateProfileThemeRequest
```json
{
  "theme": "DARK"
}
```
- **theme**: Tema do perfil (DEFAULT, BROWN, NEON, GREEN, PURPLE)

### ProfileInfo
```json
{
  "profileImageUrl": "https://...",
  "bio": "Minha bio",
  "theme": "PURPLE"
}
```
- **profileImageUrl**: URL da imagem de perfil
- **bio**: Biografia do usuário
- **theme**: Tema do perfil (DEFAULT, BROWN, NEON, GREEN, PURPLE)

### PersonInfo
```json
{
  "name": "Nome do Usuário",
  "nationality": "BRAZIL",
  "gender": "MALE",
  "birthDate": "1990-01-15"
}
```
- **name**: Nome completo do usuário
- **nationality**: Nacionalidade (BRAZIL, USA, UK, etc.)
- **gender**: Gênero (MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY)
- **birthDate**: Data de nascimento

### UserCollectionInfo
```json
{
  "inCollection": true,
  "status": "COMPLETED",
  "rating": 5,
  "finishedAt": "2024-01-15",
  "currentSeason": 1,
  "currentEpisode": 5
}
```
- **inCollection**: Boolean indicando se o item está na coleção do usuário
- **status**: Status do item na coleção (PENDING, IN_PROGRESS, COMPLETED, ABANDONED)
- **rating**: Nota do usuário (1-5)
- **finishedAt**: Data de conclusão
- **currentSeason**: Temporada atual (apenas para séries)
- **currentEpisode**: Episódio atual (apenas para séries)
- **Nota**: Se o usuário não estiver autenticado ou o item não estiver na coleção, todos os campos serão null exceto inCollection=false

### UserMediaResponse
```json
{
  "id": 1,
  "externalId": "12345",
  "type": "BOOK",
  "title": "Título",
  "thumbnail": "https://...",
  "completed": true,
  "rating": 5,
  "finishedAt": "2024-01-15",
  "status": "COMPLETED",
  "currentSeason": 1,
  "currentEpisode": 5,
  "author": "Nome do Autor",
  "collectionIds": [1, 2, 3]
}
```

### UpdateUserMediaRequest
```json
{
  "rating": 5,
  "finishedAt": "2024-01-15",
  "highlighted": true,
  "currentSeason": 1,
  "currentEpisode": 5,
  "author": "Nome do Autor",
  "collectionIds": [1, 2, 3]
}
```

### UpdateMediaResult
```json
{
  "media": {
    "id": 1,
    "externalId": "12345",
    "type": "SERIES",
    "title": "Game of Thrones",
    "thumbnail": "https://...",
    "completed": false,
    "rating": 5,
    "finishedAt": null,
    "status": "IN_PROGRESS",
    "currentSeason": 1,
    "currentEpisode": 5
  },
  "newAchievements": ["WATCH_FIRST_SERIES"]
}
```

### UserGameResponse
```json
{
  "id": 1,
  "mediaId": 1,
  "name": "Nome do Jogo",
  "thumbnail": "https://cdn.cloudflare.steamstatic.com/steam/apps/620/header.jpg",
  "status": "COMPLETED",
  "platinumed": true,
  "playtimeMinutes": 1200,
  "achievementsUnlocked": 45,
  "totalAchievements": 50,
  "highlighted": true,
  "displayOrder": 1,
  "genres": ["RPG", "Ação"],
  "platforms": ["PC", "PlayStation 4"],
  "stores": [
    {
      "name": "Steam",
      "url": "https://store.steampowered.com/app/620/"
    },
    {
      "name": "PlayStation Store",
      "url": "https://store.playstation.com/..."
    }
  ]
}
```

### UserMediaRequest
```json
{
  "externalId": "12345",
  "type": "BOOK",
  "title": "Título",
  "thumbnail": "https://...",
  "completed": true,
  "rating": 5,
  "finishedAt": "2024-01-15",
  "pageCount": 300,
  "status": "COMPLETED",
  "currentSeason": 1,
  "currentEpisode": 5,
  "author": "Nome do Autor",
  "collectionIds": [1, 2, 3]
}
```

### MediaCollectionRequest
```json
{
  "type": "BOOK",
  "name": "Favoritos",
  "icon": "assets/icons/favorites.png"
}
```

### MediaCollectionResponse
```json
{
  "id": 1,
  "type": "BOOK",
  "name": "Favoritos",
  "icon": "assets/icons/favorites.png",
  "createdAt": "2024-01-15T10:30:00"
}
```

## Notas Importantes

- Todos os endpoints requerem autenticação via header `Authorization: Bearer <accessToken>` (exceto quando especificado)
- Formato de data: `YYYY-MM-DD`
- Formato de datetime: `YYYY-MM-DDTHH:mm:ss`
- Paginação padrão: page 0, size 20
- Destaques (highlighted): limitado a 6 itens por tipo
- JWT tokens são stateless - o cliente deve armazenar o accessToken e refreshToken
- Quando o accessToken expirar, use o endpoint `/auth/refresh` para obter um novo token
