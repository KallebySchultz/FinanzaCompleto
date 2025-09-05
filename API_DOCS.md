# 🔌 Finanza API Documentation

## 📋 Visão Geral

A API REST do Finanza fornece endpoints completos para gestão financeira pessoal.

**Base URL:** `http://localhost:8080/api`

**Autenticação:** JWT Bearer Token (exceto endpoints de auth)

**Content-Type:** `application/json`

## 🔐 Autenticação

### Registrar Usuário

```http
POST /auth/register
```

**Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com", 
  "senha": "123456"
}
```

**Response (201):**
```json
{
  "message": "Usuário criado com sucesso",
  "user": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "data_criacao": 1704067200000
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Login

```http
POST /auth/login
```

**Body:**
```json
{
  "email": "joao@email.com",
  "senha": "123456"
}
```

**Response (200):**
```json
{
  "message": "Login realizado com sucesso",
  "user": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 👤 Usuários

### Obter Perfil

```http
GET /users/profile
Authorization: Bearer {token}
```

**Response (200):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "data_criacao": 1704067200000
}
```

### Atualizar Perfil

```http
PUT /users/profile
Authorization: Bearer {token}
```

**Body:**
```json
{
  "nome": "João Santos",
  "email": "joao.santos@email.com",
  "senha_atual": "123456",
  "nova_senha": "novasenha123"
}
```

### Resumo Financeiro

```http
GET /users/financial-summary
Authorization: Bearer {token}
```

**Response (200):**
```json
{
  "saldo_total": 5250.00,
  "receitas_mes": 3500.00,
  "despesas_mes": 1250.00,
  "total_contas": 3
}
```

### Excluir Conta

```http
DELETE /users/profile
Authorization: Bearer {token}
```

**Body:**
```json
{
  "senha": "123456"
}
```

## 🏦 Contas

### Listar Contas

```http
GET /accounts
Authorization: Bearer {token}
```

**Response (200):**
```json
[
  {
    "id": 1,
    "nome": "Conta Corrente",
    "saldo_inicial": 1000.00,
    "saldo_atual": 850.00,
    "usuario_id": 1
  },
  {
    "id": 2,
    "nome": "Poupança",
    "saldo_inicial": 5000.00,
    "saldo_atual": 5200.00,
    "usuario_id": 1
  }
]
```

### Criar Conta

```http
POST /accounts
Authorization: Bearer {token}
```

**Body:**
```json
{
  "nome": "Cartão de Crédito",
  "saldo_inicial": 0.00
}
```

**Response (201):**
```json
{
  "message": "Conta criada com sucesso",
  "account": {
    "id": 3,
    "nome": "Cartão de Crédito",
    "saldo_inicial": 0.00,
    "usuario_id": 1
  }
}
```

### Atualizar Conta

```http
PUT /accounts/{id}
Authorization: Bearer {token}
```

**Body:**
```json
{
  "nome": "Conta Corrente Principal",
  "saldo_inicial": 1200.00
}
```

### Excluir Conta

```http
DELETE /accounts/{id}
Authorization: Bearer {token}
```

## 📝 Transações

### Listar Transações

```http
GET /transactions?limit=10&offset=0&tipo=despesa&start_date=1704067200000&end_date=1706745600000
Authorization: Bearer {token}
```

**Query Params:**
- `limit` (int): Limite de resultados (padrão: 50)
- `offset` (int): Offset para paginação (padrão: 0)
- `tipo` (string): Filtrar por tipo ('receita' ou 'despesa')
- `start_date` (timestamp): Data inicial em milissegundos
- `end_date` (timestamp): Data final em milissegundos

**Response (200):**
```json
{
  "transactions": [
    {
      "id": 1,
      "valor": -150.00,
      "data": 1704067200000,
      "descricao": "Supermercado",
      "tipo": "despesa",
      "conta_nome": "Conta Corrente",
      "categoria_nome": "Alimentação",
      "categoria_cor": "#FF6B6B"
    }
  ],
  "total": 25,
  "limit": 10,
  "offset": 0
}
```

### Criar Transação

```http
POST /transactions
Authorization: Bearer {token}
```

**Body:**
```json
{
  "valor": 150.00,
  "data": 1704067200000,
  "descricao": "Freelance desenvolvimento",
  "conta_id": 1,
  "categoria_id": 2,
  "tipo": "receita"
}
```

**Response (201):**
```json
{
  "message": "Transação criada com sucesso",
  "transaction": {
    "id": 2,
    "valor": 150.00,
    "data": 1704067200000,
    "descricao": "Freelance desenvolvimento",
    "tipo": "receita",
    "conta_nome": "Conta Corrente",
    "categoria_nome": "Freelance",
    "categoria_cor": "#3498DB"
  }
}
```

### Atualizar Transação

```http
PUT /transactions/{id}
Authorization: Bearer {token}
```

**Body:**
```json
{
  "valor": 175.00,
  "descricao": "Freelance desenvolvimento - projeto X",
  "categoria_id": 2
}
```

### Excluir Transação

```http
DELETE /transactions/{id}
Authorization: Bearer {token}
```

### Resumo por Período

```http
GET /transactions/summary/period?start_date=1704067200000&end_date=1706745600000
Authorization: Bearer {token}
```

**Response (200):**
```json
{
  "total_receitas": 3500.00,
  "total_despesas": 1250.00,
  "count_receitas": 5,
  "count_despesas": 15,
  "saldo_periodo": 2250.00
}
```

## 🏷️ Categorias

### Listar Categorias

```http
GET /categories?tipo=despesa
Authorization: Bearer {token}
```

**Query Params:**
- `tipo` (string): Filtrar por tipo ('receita' ou 'despesa')

**Response (200):**
```json
[
  {
    "id": 1,
    "nome": "Alimentação",
    "cor_hex": "#FF6B6B",
    "tipo": "despesa"
  },
  {
    "id": 2,
    "nome": "Transporte",
    "cor_hex": "#4ECDC4",
    "tipo": "despesa"
  }
]
```

### Criar Categoria

```http
POST /categories
Authorization: Bearer {token}
```

**Body:**
```json
{
  "nome": "Pets",
  "cor_hex": "#FF9F43",
  "tipo": "despesa"
}
```

### Atualizar Categoria

```http
PUT /categories/{id}
Authorization: Bearer {token}
```

**Body:**
```json
{
  "nome": "Alimentação e Bebidas",
  "cor_hex": "#FF6B6B"
}
```

### Excluir Categoria

```http
DELETE /categories/{id}
Authorization: Bearer {token}
```

## 👑 Administração

### Estatísticas do Sistema

```http
GET /admin/stats
Authorization: Bearer {admin_token}
```

**Response (200):**
```json
{
  "total_usuarios": 150,
  "total_transacoes": 1250,
  "total_contas": 450,
  "volume_total": 125000.00,
  "usuario_mais_ativo": {
    "nome": "João Silva",
    "total_transacoes": 45
  },
  "categoria_mais_usada": {
    "nome": "Alimentação",
    "total_uso": 234
  }
}
```

### Atividade Recente

```http
GET /admin/activity?limit=20
Authorization: Bearer {admin_token}
```

### Listar Usuários

```http
GET /admin/users?page=1&limit=10
Authorization: Bearer {admin_token}
```

### Criar Usuário (Admin)

```http
POST /admin/users
Authorization: Bearer {admin_token}
```

### Atualizar Usuário (Admin)

```http
PUT /admin/users/{id}
Authorization: Bearer {admin_token}
```

### Excluir Usuário (Admin)

```http
DELETE /admin/users/{id}
Authorization: Bearer {admin_token}
```

## 🩺 Health Check

### Status da API

```http
GET /health
```

**Response (200):**
```json
{
  "status": "OK",
  "timestamp": "2024-01-01T12:00:00.000Z",
  "uptime": 3600.123
}
```

## ❌ Códigos de Erro

| Código | Descrição |
|--------|-----------|
| 400 | Bad Request - Dados inválidos |
| 401 | Unauthorized - Token inválido/ausente |
| 403 | Forbidden - Sem permissão |
| 404 | Not Found - Recurso não encontrado |
| 409 | Conflict - Recurso já existe |
| 429 | Too Many Requests - Rate limit |
| 500 | Internal Server Error - Erro interno |

## 📝 Exemplos de Uso

### Fluxo Completo com cURL

```bash
# 1. Registrar usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"João","email":"joao@test.com","senha":"123456"}'

# 2. Fazer login e obter token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@test.com","senha":"123456"}' | jq -r '.token')

# 3. Criar uma conta
curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Minha Conta","saldo_inicial":1000.00}'

# 4. Criar uma transação
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"valor":-50.00,"data":1704067200000,"descricao":"Almoço","conta_id":1,"categoria_id":1,"tipo":"despesa"}'

# 5. Obter resumo financeiro
curl -X GET http://localhost:8080/api/users/financial-summary \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo JavaScript (Frontend)

```javascript
// Configuração base
const API_BASE = 'http://localhost:8080/api';
let authToken = localStorage.getItem('authToken');

// Função helper para requisições
async function apiRequest(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(authToken && { 'Authorization': `Bearer ${authToken}` })
    },
    ...options
  };
  
  const response = await fetch(url, config);
  
  if (!response.ok) {
    throw new Error(`API Error: ${response.status}`);
  }
  
  return response.json();
}

// Login
async function login(email, senha) {
  const response = await apiRequest('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, senha })
  });
  
  authToken = response.token;
  localStorage.setItem('authToken', authToken);
  return response;
}

// Obter transações
async function getTransactions(filters = {}) {
  const params = new URLSearchParams(filters);
  return apiRequest(`/transactions?${params}`);
}

// Criar transação
async function createTransaction(transaction) {
  return apiRequest('/transactions', {
    method: 'POST',
    body: JSON.stringify(transaction)
  });
}
```

---

**Finanza API** - Documentação completa para desenvolvimento 🚀