# 🔒 Implementação de Sistema de Roles e Correção de Sincronização Offline

## 📋 Resumo Executivo

Este documento detalha as mudanças implementadas para resolver dois problemas críticos no sistema Finanza:

1. **Falta de distinção entre usuários e administradores**
2. **Erros de sincronização quando o usuário está offline**

**Status**: ✅ **CONCLUÍDO**

**Data**: Outubro 2025

**Versão**: 2.0.0

---

## 🎯 Objetivos

### Problema 1: Separação de Roles
**Antes:**
- Não havia distinção entre usuários regulares e administradores
- Admin podia acessar o app mobile (não deveria)
- Usuários regulares poderiam tentar acessar o painel admin (com credenciais)

**Depois:**
- Sistema de roles implementado: `user` e `admin`
- Administradores APENAS podem acessar o painel desktop
- Usuários regulares APENAS podem acessar o app mobile
- Validação em ambos os lados (cliente e servidor)

### Problema 2: Erros de Sincronização Offline
**Antes:**
- Usuário logado offline não conseguia sincronizar
- Mensagem "não está logado" mesmo estando autenticado
- Botão de sincronização desabilitado quando offline

**Depois:**
- Sincronização funciona mesmo offline (salva localmente)
- Botão sempre habilitado quando usuário está logado
- Sync automático quando conexão é restaurada
- Mensagens claras sobre o estado (online/offline)

---

## 🔧 Mudanças Técnicas Implementadas

### 1. Schema do Banco de Dados

#### SQLite (Mobile - `database/finanza.sql`)
```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    senha TEXT NOT NULL,
    data_criacao INTEGER NOT NULL,
    role TEXT NOT NULL DEFAULT 'user' CHECK (role IN ('user', 'admin'))
);
```

#### MySQL (Desktop - `DESKTOP VERSION/banco/script_inicial.sql`)
```sql
CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') NOT NULL DEFAULT 'user',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Modelos de Dados

#### Mobile (`app/.../model/Usuario.java`)
```java
/** Role do usuário: 'user' (padrão) ou 'admin' */
@ColumnInfo(defaultValue = "'user'")
public String role;

public boolean isAdmin() {
    return "admin".equals(role);
}

public boolean isUser() {
    return "user".equals(role);
}
```

#### Server (`DESKTOP VERSION/.../model/Usuario.java`)
```java
private String role; // 'user' or 'admin'

public boolean isAdmin() { 
    return "admin".equals(role); 
}

public boolean isUser() { 
    return "user".equals(role); 
}
```

### 3. Migração do Banco Mobile

**Versão do Banco:** 6 → 7

```java
static final Migration MIGRATION_6_7 = new Migration(6, 7) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE Usuario ADD COLUMN role TEXT NOT NULL DEFAULT 'user'");
    }
};
```

### 4. Autenticação com Role

#### Mobile - Login (`AuthManager.java`)
```java
public void login(String email, String senha, AuthCallback callback) {
    Usuario usuarioLocal = buscarUsuarioLocal(email, senha);
    
    // IMPORTANT: Block admin users from logging into mobile app
    if (usuarioLocal != null && usuarioLocal.isAdmin()) {
        callback.onError("Acesso negado. Administradores devem usar o painel desktop.");
        return;
    }
    
    // ... resto do código de login
}
```

Também valida role vindo do servidor:
```java
String role = userData.length >= 4 ? userData[3] : "user";

if ("admin".equals(role)) {
    callback.onError("Acesso negado. Administradores devem usar o painel desktop.");
    return;
}
```

#### Desktop - Login (`AuthController.java`)
```java
String role = dadosUsuario.length >= 4 ? dadosUsuario[3] : "user";

usuarioLogado = new Usuario(userId, nome, emailUsuario, "", role);

// Validate that only admins can access desktop panel
if (!"admin".equals(role)) {
    return new LoginResult(false, 
        "Acesso negado. Apenas administradores podem acessar o painel desktop.", 
        null);
}
```

### 5. Resposta do Servidor

#### Server (`ClientHandler.java`)
```java
Usuario usuario = usuarioDAO.autenticar(email, senha);
if (usuario != null) {
    usuarioLogado = usuario;
    String role = usuario.getRole() != null ? usuario.getRole() : "user";
    
    // Response format: OK|userId;nome;email;role
    String userData = usuario.getId() + Protocol.FIELD_SEPARATOR + 
                     usuario.getNome() + Protocol.FIELD_SEPARATOR + 
                     usuario.getEmail() + Protocol.FIELD_SEPARATOR +
                     role;
    return Protocol.createSuccessResponse(userData);
}
```

### 6. Correção da Sincronização Offline

#### Settings Activity (`SettingsActivity.java`)

**Antes:**
```java
private void triggerManualSync() {
    if (!authManager.isLoggedIn()) {
        Toast.makeText(this, "Você precisa estar logado", ...).show();
        return;
    }
    
    if (!syncService.isOnline()) {  // ❌ PROBLEMA: Bloqueia sync offline
        Toast.makeText(this, "Conecte-se ao servidor primeiro", ...).show();
        return;
    }
    
    // sync code...
}
```

**Depois:**
```java
private void triggerManualSync() {
    if (!authManager.isLoggedIn()) {
        Toast.makeText(this, "Você precisa estar logado", ...).show();
        return;
    }
    
    // ✅ CORREÇÃO: Permite sync offline
    // Allow sync even if offline - it will save locally
    // No need to check if online - the sync service handles this gracefully
    
    int userId = authManager.getLoggedUserId();
    syncService.sincronizarTudo(userId, new SyncService.SyncCallback() {
        // callbacks...
    });
}
```

#### Botão de Sincronização

**Antes:**
```java
if (btnSync != null) {
    btnSync.setEnabled(syncService.isOnline()); // ❌ Desabilitado quando offline
}
```

**Depois:**
```java
if (btnSync != null) {
    // ✅ Sempre habilitado quando logado
    btnSync.setEnabled(true);
}
```

#### Status de Conexão

**Antes:**
```java
private void updateConnectionStatus() {
    if (syncService.isOnline()) {
        statusText.setText("🟢 Conectado");
        btnSync.setEnabled(true);
    } else {
        statusText.setText("🔴 Desconectado");
        btnSync.setEnabled(false);  // ❌ Desabilita botão
    }
}
```

**Depois:**
```java
private void updateConnectionStatus() {
    if (syncService.isOnline()) {
        statusText.setText("🟢 Conectado ao servidor");
    } else {
        statusText.setText("🔴 Modo offline");
    }
    
    // ✅ Botão sempre habilitado quando logado
    if (btnSync != null && authManager.isLoggedIn()) {
        btnSync.setEnabled(true);
    }
}
```

---

## 📁 Arquivos Modificados

### Mobile App
1. `app/src/main/java/com/example/finanza/model/Usuario.java`
   - Adicionado campo `role`
   - Adicionados métodos `isAdmin()` e `isUser()`

2. `app/src/main/java/com/example/finanza/db/AppDatabase.java`
   - Versão incrementada para 7
   - Adicionada `MIGRATION_6_7`

3. `app/src/main/java/com/example/finanza/network/AuthManager.java`
   - Validação de role no login local
   - Validação de role na resposta do servidor
   - Bloqueio de admins no app mobile

4. `app/src/main/java/com/example/finanza/ui/SettingsActivity.java`
   - Removida verificação de conexão antes de sync
   - Botão de sync sempre habilitado quando logado
   - Mensagens de status melhoradas

### Desktop Application
5. `DESKTOP VERSION/ServidorFinanza/src/model/Usuario.java`
   - Adicionado campo `role`
   - Construtor com role
   - Métodos `isAdmin()` e `isUser()`

6. `DESKTOP VERSION/ServidorFinanza/src/dao/UsuarioDAO.java`
   - Atualizado `inserir()` para incluir role
   - Atualizado `mapResultSetToUsuario()` para ler role

7. `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java`
   - Login response inclui role
   - Suporte a role no modo teste

8. `DESKTOP VERSION/ClienteFinanza/src/controller/AuthController.java`
   - Parse de role da resposta do servidor
   - Validação de admin-only access

### Database
9. `database/finanza.sql`
   - Adicionado campo role na tabela usuarios
   - Admin padrão com role 'admin'

10. `DESKTOP VERSION/banco/script_inicial.sql`
    - Adicionado campo role na tabela usuario
    - Criação de usuário admin

11. `DESKTOP VERSION/banco/migration_add_role.sql` (NOVO)
    - Script de migração para bancos existentes

---

## 🚀 Como Aplicar as Mudanças

### Para Bancos de Dados Existentes

#### MySQL (Desktop Server)
```bash
# 1. Faça backup do banco
mysqldump -u root -p finanza_db > backup_finanza.sql

# 2. Execute a migração
mysql -u root -p finanza_db < "DESKTOP VERSION/banco/migration_add_role.sql"

# 3. Verifique a migração
mysql -u root -p finanza_db -e "SELECT id, nome, email, role FROM usuario;"
```

#### SQLite (Mobile App)
A migração é automática quando o app é atualizado:
- Room detecta versão 6 do banco
- Executa automaticamente `MIGRATION_6_7`
- Adiciona coluna `role` com default 'user'

### Para Novos Usuários

#### Criar Usuário Admin (MySQL)
```sql
INSERT INTO usuario (nome, email, senha_hash, role) VALUES 
('Seu Nome Admin', 'admin@exemplo.com', 'hash_da_senha', 'admin');
```

#### Criar Usuário Regular (MySQL)
```sql
INSERT INTO usuario (nome, email, senha_hash, role) VALUES 
('Nome do Usuário', 'user@exemplo.com', 'hash_da_senha', 'user');
```

---

## 🧪 Como Testar

### Teste 1: Admin Não Pode Acessar Mobile
```
1. ✅ Crie/use usuário com role='admin'
2. ✅ Tente fazer login no app mobile
3. ✅ Esperado: "Acesso negado. Administradores devem usar o painel desktop."
```

### Teste 2: User Não Pode Acessar Desktop
```
1. ✅ Crie/use usuário com role='user'
2. ✅ Tente fazer login no painel desktop
3. ✅ Esperado: "Acesso negado. Apenas administradores podem acessar o painel desktop."
```

### Teste 3: Sincronização Offline
```
1. ✅ Faça login no app mobile (usuário regular)
2. ✅ Desconecte do servidor (modo avião ou desligar servidor)
3. ✅ Crie transações, contas, categorias
4. ✅ Vá em Configurações
5. ✅ Observe que botão "🔄 Sync" está habilitado
6. ✅ Clique em Sync
7. ✅ Esperado: "Modo offline - dados salvos localmente"
8. ✅ Reconecte ao servidor
9. ✅ Clique em Sync novamente
10. ✅ Esperado: Dados enviados ao servidor com sucesso
```

### Teste 4: Login e Sync Automático
```
1. ✅ Faça logout
2. ✅ Crie dados offline (transações, etc)
3. ✅ Conecte ao servidor
4. ✅ Faça login
5. ✅ Esperado: Sincronização automática após login
6. ✅ Verifique dados no painel desktop
```

---

## 📊 Protocolo de Comunicação Atualizado

### Resposta de Login (Server → Client)

**Formato Anterior:**
```
OK|userId;nome;email
```

**Formato Novo:**
```
OK|userId;nome;email;role
```

**Exemplo:**
```
OK|1;João Silva;joao@exemplo.com;user
OK|2;Admin System;admin@finanza.com;admin
```

### Backward Compatibility
O código é compatível com servidores antigos que não enviam o campo role:
```java
String role = userData.length >= 4 ? userData[3] : "user";
```

---

## 🔐 Considerações de Segurança

### Validação em Múltiplas Camadas

1. **Cliente Mobile:**
   - Verifica role localmente antes de permitir login
   - Verifica role da resposta do servidor
   - Dupla validação para segurança

2. **Cliente Desktop:**
   - Verifica role antes de permitir acesso ao painel
   - Valida no login

3. **Servidor:**
   - Envia role correta na resposta
   - Banco tem constraint para garantir valores válidos

### Usuário Admin Padrão

**Senha Padrão:** `admin`
**Email:** `admin@finanza.com`

⚠️ **IMPORTANTE:** Altere a senha do admin em produção!

```sql
-- Alterar senha do admin
UPDATE usuario 
SET senha_hash = 'novo_hash_seguro' 
WHERE email = 'admin@finanza.com';
```

---

## 🎨 Interface do Usuário

### Mobile - Tela de Login
**Antes:**
- Qualquer usuário podia logar

**Depois:**
- Admin tenta logar → ❌ "Acesso negado. Administradores devem usar o painel desktop."
- User tenta logar → ✅ Login bem-sucedido

### Desktop - Tela de Login
**Antes:**
- Qualquer usuário podia logar

**Depois:**
- User tenta logar → ❌ "Acesso negado. Apenas administradores podem acessar o painel desktop."
- Admin tenta logar → ✅ Login bem-sucedido

### Mobile - Configurações
**Antes:**
```
Status: 🔴 Desconectado
[🔗 Testar] [🔄 Sync (desabilitado)] [💾 Salvar]
```

**Depois:**
```
Status: 🔴 Modo offline
[🔗 Testar] [🔄 Sync] [💾 Salvar]
           ↑ Sempre habilitado quando logado
```

---

## 📝 Logs e Debugging

### Mobile
```java
// AuthManager
Log.d(TAG, "Tentativa de login - role: " + usuario.role);

// SyncService
Log.d(TAG, "Não conectado ao servidor - operação offline");
Log.d(TAG, "Sincronização de dados pendentes iniciada");
```

### Desktop
```java
// ClientHandler
System.out.println("Login successful - Role: " + usuario.getRole());

// AuthController
System.out.println("Admin validation: " + usuario.isAdmin());
```

---

## 🐛 Troubleshooting

### Problema: "Column role does not exist"

**Causa:** Banco não foi migrado

**Solução:**
```bash
# MySQL
mysql -u root -p finanza_db < migration_add_role.sql

# SQLite (Mobile)
# Desinstale e reinstale o app, ou incremente versão do banco
```

### Problema: Admin consegue logar no mobile

**Causa:** Role não está sendo validado corretamente

**Verificar:**
1. Versão do app está atualizada?
2. Role no banco está correto? (`SELECT role FROM usuario WHERE email = '...'`)
3. Logs do AuthManager mostram validação?

### Problema: Sync não funciona offline

**Causa:** Versão antiga do código

**Verificar:**
1. SettingsActivity está atualizado?
2. Botão Sync está habilitado mesmo offline?
3. Mensagem "Conecte-se ao servidor primeiro" aparece? (não deveria)

---

## 📈 Melhorias Futuras

### Curto Prazo
- [ ] Adicionar mais roles (moderador, visualizador, etc)
- [ ] Permitir que admin gerencie roles de usuários
- [ ] Adicionar auditoria de mudanças de role

### Médio Prazo
- [ ] Implementar permissões granulares por feature
- [ ] Role-based access control (RBAC) completo
- [ ] Multi-tenancy com isolation de dados

### Longo Prazo
- [ ] SSO (Single Sign-On) integration
- [ ] OAuth2/OpenID Connect
- [ ] API REST com JWT e role claims

---

## 📚 Referências

### Padrões Utilizados
- **Enum Pattern**: Para roles no banco de dados
- **Strategy Pattern**: Validação diferente por tipo de cliente
- **Repository Pattern**: DAO com suporte a roles

### Documentação Relacionada
- [SYNC_FIX_DOCUMENTATION.md](../SYNC_FIX_DOCUMENTATION.md) - Correção anterior de sync
- [USER_MANUAL.md](../USER_MANUAL.md) - Manual do usuário
- [README.md](../README.md) - Documentação geral

---

## ✅ Checklist de Implementação

### Desenvolvimento
- [x] Schema do banco atualizado (MySQL e SQLite)
- [x] Modelos com campo role
- [x] Migração do banco mobile (6 → 7)
- [x] Validação no login mobile
- [x] Validação no login desktop
- [x] Resposta do servidor com role
- [x] Correção do sync offline
- [x] Botão sync sempre habilitado

### Testes
- [x] Admin bloqueado no mobile
- [x] User bloqueado no desktop
- [x] Sync funciona offline
- [x] Dados criados offline sincronizam
- [x] Backward compatibility mantida

### Documentação
- [x] Documentação técnica completa
- [x] Script de migração criado
- [x] Comentários no código
- [x] Guia de troubleshooting

---

## 👥 Autores

**Finanza Development Team**
- Implementação: GitHub Copilot Agent
- Revisão: KallebySchultz
- Documentação: Comprehensive team effort

---

## 📄 Licença

Este projeto faz parte do Trabalho Interdisciplinar 2025 - IFSUL Campus Venâncio Aires

---

## 🆘 Suporte

Para problemas relacionados a roles ou sincronização:
1. Verifique os logs no Android Studio / Console
2. Confirme migração do banco foi executada
3. Teste a conexão com o servidor
4. Verifique role do usuário no banco de dados

**Última Atualização**: Outubro 2025
