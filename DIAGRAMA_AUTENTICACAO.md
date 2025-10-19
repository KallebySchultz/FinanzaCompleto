# Diagrama de Autenticação - Sistema Finanza

## 🔄 Fluxo de Autenticação

### Antes da Implementação ❌

```
┌─────────────────┐
│  Desktop Admin  │
└────────┬────────┘
         │ LOGIN
         │ (sem validação de tipo)
         ↓
    ┌────────────┐
    │  Servidor  │
    └─────┬──────┘
          │ OK (qualquer usuário)
          ↓
    ✅ Login aceito (PROBLEMA!)


┌──────────────┐
│  App Mobile  │
└──────┬───────┘
       │ LOGIN
       │ (sem validação de tipo)
       ↓
  ┌────────────┐
  │  Servidor  │
  └─────┬──────┘
        │ OK (qualquer usuário)
        ↓
  ✅ Login aceito (PROBLEMA!)
```

**Problema:** Admin podia acessar mobile e usuário podia acessar desktop!

---

### Depois da Implementação ✅

#### Cenário 1: Admin no Desktop (Sucesso)

```
┌─────────────────┐
│  Desktop Admin  │
│  (AuthController)│
└────────┬────────┘
         │
         │ LOGIN|admin@finanza.com|senha|admin
         │         ↑
         │         └─ tipo_cliente = "admin"
         │
         ↓
    ┌────────────┐
    │  Servidor  │────┐
    │(ClientHandler) │ │ 1. Autentica credenciais
    └────────────┘    │ 2. Busca usuário no banco
         ↑            │ 3. Verifica tipo_usuario
         │            └─ tipo_usuario = "admin" ✓
         │
         │ OK|1;Administrador;admin@finanza.com;admin
         │
         ↓
    ┌────────────┐
    │   Desktop  │
    │ Dashboard  │
    └────────────┘
    
    ✅ Acesso permitido
```

#### Cenário 2: Usuário Comum no Desktop (Bloqueado)

```
┌─────────────────┐
│  Desktop Admin  │
│  (AuthController)│
└────────┬────────┘
         │
         │ LOGIN|user@gmail.com|senha|admin
         │         ↑
         │         └─ tipo_cliente = "admin"
         │
         ↓
    ┌────────────┐
    │  Servidor  │────┐
    │(ClientHandler) │ │ 1. Autentica credenciais ✓
    └────────────┘    │ 2. Busca usuário no banco
         ↑            │ 3. Verifica tipo_usuario
         │            │    tipo_usuario = "usuario" ✗
         │            └─   tipo_cliente = "admin" ✗
         │                 INCOMPATÍVEL!
         │
         │ ACCESS_DENIED|Acesso negado. Apenas administradores...
         │
         ↓
    ┌────────────┐
    │ Mensagem   │
    │  de Erro   │
    └────────────┘
    
    ❌ Acesso bloqueado
```

#### Cenário 3: Usuário Comum no Mobile (Sucesso)

```
┌──────────────┐
│  App Mobile  │
│(ServerClient)│
└──────┬───────┘
       │
       │ LOGIN|user@gmail.com|senha|mobile
       │         ↑
       │         └─ tipo_cliente = "mobile"
       │
       ↓
  ┌────────────┐
  │  Servidor  │────┐
  │(ClientHandler) │ │ 1. Autentica credenciais
  └────────────┘    │ 2. Busca usuário no banco
       ↑            │ 3. Verifica tipo_usuario
       │            └─ tipo_usuario = "usuario" ✓
       │
       │ OK|2;Usuário;user@gmail.com;usuario
       │
       ↓
  ┌────────────┐
  │   Mobile   │
  │ Dashboard  │
  └────────────┘
  
  ✅ Acesso permitido
```

#### Cenário 4: Admin no Mobile (Bloqueado)

```
┌──────────────┐
│  App Mobile  │
│(ServerClient)│
└──────┬───────┘
       │
       │ LOGIN|admin@finanza.com|senha|mobile
       │         ↑
       │         └─ tipo_cliente = "mobile"
       │
       ↓
  ┌────────────┐
  │  Servidor  │────┐
  │(ClientHandler) │ │ 1. Autentica credenciais ✓
  └────────────┘    │ 2. Busca usuário no banco
       ↑            │ 3. Verifica tipo_usuario
       │            │    tipo_usuario = "admin" ✗
       │            └─   tipo_cliente = "mobile" ✗
       │                 INCOMPATÍVEL!
       │
       │ ACCESS_DENIED|Acesso negado. Administradores não podem...
       │
       ↓
  ┌────────────┐
  │ Mensagem   │
  │  de Erro   │
  └────────────┘
  
  ❌ Acesso bloqueado
```

---

## 🗄️ Estrutura do Banco de Dados

### Tabela `usuario` - Antes

```sql
CREATE TABLE usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    senha_hash VARCHAR(255),
    data_criacao TIMESTAMP,
    data_atualizacao TIMESTAMP
);
```

### Tabela `usuario` - Depois

```sql
CREATE TABLE usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    senha_hash VARCHAR(255),
    tipo_usuario ENUM('admin', 'usuario') DEFAULT 'usuario',  ← NOVO!
    data_criacao TIMESTAMP,
    data_atualizacao TIMESTAMP
);
```

### Dados de Exemplo

```
┌────┬───────────────┬───────────────────┬──────────────┐
│ id │ nome          │ email             │ tipo_usuario │
├────┼───────────────┼───────────────────┼──────────────┤
│ 1  │ Administrador │ admin@finanza.com │ admin        │ ← Acessa Desktop
│ 2  │ Usuário Teste │ teste1@gmail.com  │ usuario      │ ← Acessa Mobile
│ 3  │ João Silva    │ joao@gmail.com    │ usuario      │ ← Acessa Mobile
│ 4  │ Maria Admin   │ maria@finanza.com │ admin        │ ← Acessa Desktop
└────┴───────────────┴───────────────────┴──────────────┘
```

---

## 🔐 Matriz de Decisão

```
┌──────────────────────┬──────────────┬───────────────────┐
│   Tipo de Usuário    │ Tipo Cliente │     Resultado     │
├──────────────────────┼──────────────┼───────────────────┤
│ tipo_usuario='admin' │ "admin"      │ ✅ PERMITIDO      │
│ tipo_usuario='admin' │ "mobile"     │ ❌ ACCESS_DENIED  │
│ tipo_usuario='usuario'│ "admin"     │ ❌ ACCESS_DENIED  │
│ tipo_usuario='usuario'│ "mobile"    │ ✅ PERMITIDO      │
└──────────────────────┴──────────────┴───────────────────┘
```

**Lógica de Validação:**
```
if (tipo_cliente == "admin" && tipo_usuario != "admin") {
    return ACCESS_DENIED;
}

if (tipo_cliente == "mobile" && tipo_usuario == "admin") {
    return ACCESS_DENIED;
}

return OK;
```

---

## 📡 Protocolo de Comunicação

### Comando LOGIN (Atualizado)

```
┌─────────────────────────────────────────────────────────┐
│ LOGIN|email|senha|tipo_cliente                          │
│        ↑     ↑           ↑                              │
│        │     │           └─ Novo parâmetro!            │
│        │     │              "admin" ou "mobile"         │
│        │     └───────────── Senha do usuário           │
│        └─────────────────── Email do usuário           │
└─────────────────────────────────────────────────────────┘
```

### Resposta de Sucesso (Atualizada)

```
┌─────────────────────────────────────────────────────────┐
│ OK|userId;nome;email;tipo_usuario                       │
│    ↑                        ↑                           │
│    │                        └─ Novo campo!              │
│    │                           "admin" ou "usuario"     │
│    └───────────────────────── Dados do usuário         │
└─────────────────────────────────────────────────────────┘
```

### Resposta de Bloqueio (Nova)

```
┌─────────────────────────────────────────────────────────┐
│ ACCESS_DENIED|Mensagem de erro descritiva               │
│      ↑                ↑                                  │
│      │                └─ Ex: "Apenas administradores..." │
│      └──────────────────── Novo status!                 │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 Fluxo de Registro

### Registro via Desktop

```
┌─────────────────┐
│  Desktop Admin  │
└────────┬────────┘
         │ REGISTER|nome|email|senha
         ↓
    ┌────────────┐
    │  Servidor  │────┐
    └────────────┘    │ Cria usuário com
         ↑            └─ tipo_usuario = "usuario"
         │               (SEMPRE, por segurança)
         │ OK|userId;nome;email;usuario
         ↓
    ┌────────────┐
    │  Diálogo   │ "Usuário criado como comum.
    │ Informativo│  Para criar admin, use o banco."
    └────────────┘
```

### Registro via Mobile

```
┌──────────────┐
│  App Mobile  │
└──────┬───────┘
       │ REGISTER|nome|email|senha
       ↓
  ┌────────────┐
  │  Servidor  │────┐
  └────────────┘    │ Cria usuário com
       ↑            └─ tipo_usuario = "usuario"
       │
       │ OK|userId;nome;email;usuario
       ↓
  ┌────────────┐
  │   Mobile   │
  │ Dashboard  │
  └────────────┘
```

### Criação de Admin (Apenas via Banco)

```
┌──────────────┐
│ Administrador│
│   do Banco   │
└──────┬───────┘
       │ SQL: INSERT INTO usuario ...
       │      tipo_usuario = 'admin'
       ↓
  ┌────────────┐
  │   MySQL    │
  │  Database  │
  └────────────┘
       ↓
  ✅ Admin criado com segurança
```

---

## 📊 Comparação Visual

### Antes (Inseguro)

```
      ┌─────────┐         ┌─────────┐
      │ Desktop │         │  Mobile │
      └────┬────┘         └────┬────┘
           │                   │
           └───────┬───────────┘
                   │
              ┌────▼────┐
              │ Servidor│
              └────┬────┘
                   │
           ┌───────┴───────┐
           │               │
      ┌────▼────┐     ┌────▼────┐
      │  Admin  │     │ Usuário │
      └─────────┘     └─────────┘
           │               │
           └───────┬───────┘
                   │
           Qualquer um acessa
              qualquer lugar
                 ❌ PROBLEMA
```

### Depois (Seguro)

```
      ┌─────────┐         ┌─────────┐
      │ Desktop │         │  Mobile │
      │ (admin) │         │ (mobile)│
      └────┬────┘         └────┬────┘
           │                   │
           │    ┌─────────┐    │
           └───►│ Servidor├────┘
                │(valida) │
                └────┬────┘
                     │
             ┌───────┴───────┐
             │               │
        ┌────▼────┐     ┌────▼────┐
        │  Admin  │     │ Usuário │
        │    ✓    │     │    ✓    │
        └────┬────┘     └────┬────┘
             │               │
             │               │
        Desktop          Mobile
         APENAS          APENAS
           ✅              ✅
```

---

## 🎯 Pontos-Chave

### ✅ Implementado
- Tipo de usuário no banco de dados
- Validação no servidor (server-side)
- Bloqueio de acessos cruzados
- Mensagens claras de erro
- Documentação completa

### 🔒 Segurança
- Admin só via banco de dados
- Validação não confia no cliente
- Sem escalação de privilégios
- Auditável e rastreável

### 📚 Documentação
- Guias passo a passo
- Diagramas e exemplos
- Scripts de migração
- Troubleshooting

### 🚀 Pronto para
- Merge na branch principal
- Deploy em produção
- Testes em ambiente real
- Uso por desenvolvedores

---

## 📖 Leia Mais

- `RESUMO_IMPLEMENTACAO.md` - Visão geral completa
- `ALTERACOES_USER_ADMIN.md` - Changelog técnico
- `GUIA_RAPIDO_ADMIN_USER.md` - Quick reference
- `README_MIGRACAO.md` - Guia de migração
