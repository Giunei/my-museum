# Mudanças na Autenticação - Cookies HttpOnly

## Resumo das Mudanças

O backend foi atualizado para usar cookies HttpOnly para JWT em vez de retornar tokens no corpo da resposta JSON. Isso melhora a segurança, pois os tokens não ficam acessíveis via JavaScript.

## Mudanças nos Endpoints de Autenticação

### Antes (JSON Response)

**Request:**
```json
POST /auth/login
{
  "username": "admin",
  "password": "password"
}
```

**Response:**
```json
{
  "token": "eyJhbGci...",
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```

### Depois (Cookies HttpOnly)

**Request:**
```json
POST /auth/login
{
  "username": "admin",
  "password": "password"
}
```

**Response:**
```
204 No Content
Set-Cookie: jwt=eyJhbGci...; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=3600
Set-Cookie: refresh_token=eyJhbGci...; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=604800
```

## Endpoints Afetados

### POST /auth/register
- **Antes**: Retornava `AuthResponse` com tokens no JSON
- **Depois**: Retorna `204 No Content` com cookies HttpOnly
- **Cookies setados**:
  - `jwt`: Access token (HttpOnly, Secure, SameSite=Strict, Max-Age=3600s)
  - `refresh_token`: Refresh token (HttpOnly, Secure, SameSite=Strict, Max-Age=7 dias)

### POST /auth/login
- **Antes**: Retornava `AuthResponse` com tokens no JSON
- **Depois**: Retorna `204 No Content` com cookies HttpOnly
- **Cookies setados**: Mesmo que `/auth/register`

### POST /auth/refresh
- **Antes**: Retornava `AuthResponse` com tokens no JSON
- **Depois**: Retorna `204 No Content` com cookies HttpOnly
- **Request Body**: Ainda precisa enviar `{ "refreshToken": "..." }` no corpo
- **Cookies setados**: Mesmo que `/auth/register`

### POST /auth/logout (NOVO)
- **Descrição**: Limpa os cookies de autenticação
- **Response**: `204 No Content`
- **Cookies limpos**: `jwt` e `refresh_token` com Max-Age=0

### GET /auth/verify-email
- **Sem mudanças**: Continua retornando String

## CSRF Protection

Foi implementada proteção CSRF usando `CookieCsrfTokenRepository`.

### Detalhes:
- **Cookie CSRF**: `XSRF-TOKEN` (HttpOnly=false, para que o JS possa ler)
- **Header esperado**: `X-XSRF-TOKEN`
- **Endpoints ignorados**: `/auth/**`, `/ws/**`, `/api/ws/**`

### Como funciona no Frontend:
1. O backend envia um cookie `XSRF-TOKEN` nas respostas
2. O frontend deve ler esse cookie e enviar o valor no header `X-XSRF-TOKEN` em todas as requisições POST/PUT/DELETE/PATCH
3. O backend valida o token CSRF antes de processar a requisição

## Compatibilidade com Authorization Header

O `JwtAuthenticationFilter` foi atualizado para suportar ambos:
1. **Cookie**: Lê o token do cookie `jwt` (prioridade)
2. **Header**: Fallback para `Authorization: Bearer <token>` (para compatibilidade)

Isso permite uma migração gradual - o frontend pode continuar usando Authorization header enquanto se adapta aos cookies.

## Configuração dos Cookies

### Access Token (jwt)
- **Nome**: `jwt`
- **HttpOnly**: `true` (não acessível via JavaScript)
- **Secure**: `true` (apenas HTTPS em produção)
- **SameSite**: `Strict` (proteção contra CSRF)
- **Path**: `/`
- **Max-Age**: Igual ao tempo de expiração do token (padrão: 3600s = 1 hora)

### Refresh Token (refresh_token)
- **Nome**: `refresh_token`
- **HttpOnly**: `true`
- **Secure**: `true`
- **SameSite**: `Strict`
- **Path**: `/`
- **Max-Age**: 7 dias (604800 segundos)

## Mudanças Necessárias no Frontend

### 1. Remover armazenamento de tokens
- Não armazenar mais tokens em localStorage ou sessionStorage
- Não enviar mais tokens no header `Authorization`

### 2. Implementar leitura de CSRF token
```javascript
// Ler o cookie CSRF
function getCsrfToken() {
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    const [name, value] = cookie.trim().split('=');
    if (name === 'XSRF-TOKEN') {
      return decodeURIComponent(value);
    }
  }
  return null;
}

// Enviar CSRF token nas requisições
fetch('/api/endpoint', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-XSRF-TOKEN': getCsrfToken()
  },
  body: JSON.stringify(data)
});
```

### 3. Atualizar chamadas de autenticação
```javascript
// Login
await fetch('/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ username, password }),
  credentials: 'include' // Importante para enviar/receber cookies
});

// Logout
await fetch('/auth/logout', {
  method: 'POST',
  credentials: 'include'
});
```

### 4. Configurar fetch/axios para incluir cookies
```javascript
// Com fetch
fetch('/api/endpoint', {
  credentials: 'include'
});

// Com axios
axios.defaults.withCredentials = true;
```

### 5. Verificar autenticação
Como os tokens não estão mais acessíveis via JavaScript, o frontend pode:
- Fazer uma requisição para um endpoint protegido para verificar se está autenticado
- Usar o status code da resposta para determinar se precisa fazer login

```javascript
async function isAuthenticated() {
  try {
    const response = await fetch('/api/user/profile', {
      credentials: 'include'
    });
    return response.ok;
  } catch {
    return false;
  }
}
```

## Notas Importantes

1. **CORS**: Certifique-se de que o CORS está configurado para permitir `credentials: 'include'`
2. **HTTPS**: Em produção, os cookies `Secure` só funcionam com HTTPS
3. **SameSite**: `SameSite=Strict` pode causar problemas em alguns cenários de navegação cruzada. Se necessário, pode ser alterado para `Lax`
4. **Compatibilidade**: O backend ainda suporta Authorization header para facilitar a migração
5. **CSRF**: O CSRF token é necessário apenas para métodos que modificam dados (POST, PUT, DELETE, PATCH)

## Testando

### Testar cookies funcionando:
1. Faça login
2. Verifique no navegador que os cookies `jwt` e `refresh_token` foram setados
3. Faça uma requisição para um endpoint protegido
4. Verifique que funciona sem enviar Authorization header

### Testar CSRF:
1. Faça uma requisição POST sem o header `X-XSRF-TOKEN`
2. Deve receber erro 403 Forbidden
3. Adicione o header com o valor do cookie `XSRF-TOKEN`
4. Deve funcionar
