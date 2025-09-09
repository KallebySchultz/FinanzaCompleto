# 🔄 Fluxos de Funcionalidades - Sistema Finanza

## 📋 Índice

1. [Fluxo: Alterar Senha](#-fluxo-alterar-senha)
2. [Fluxo: Login e Autenticação](#-fluxo-login-e-autenticação)
3. [Fluxo: Adicionar Movimentação](#-fluxo-adicionar-movimentação)
4. [Fluxo: Sincronização de Dados](#-fluxo-sincronização-de-dados)
5. [Fluxo: Resolução de Conflitos](#-fluxo-resolução-de-conflitos)
6. [Mapeamento de Código](#-mapeamento-de-código)

---

## 🔐 Fluxo: Alterar Senha

### 📍 **Exemplo Prático: Quando o usuário clica em "Alterar Senha"**

Este é o fluxo completo que acontece quando você clica em "Alterar Senha" no sistema:

#### 🎯 **1. INTERFACE DO USUÁRIO (View)**

**📱 Desktop: PerfilView.java**
```java
Localização: /DESKTOP VERSION/ClienteFinanza/src/view/PerfilView.java

┌─────────────────────────────────────────────────────────┐
│ 👤 USUÁRIO CLICA EM "ALTERAR SENHA"                    │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Método: botaoAlterarSenha.addActionListener()         │
│  Linha: ~150                                           │
│                                                         │
│  📋 O que acontece:                                     │
│  1. Abre formulário com 3 campos:                      │
│     • Senha atual                                      │
│     • Nova senha                                       │
│     • Confirmar nova senha                             │
│                                                         │
│  2. Validações na interface:                           │
│     • Campos não podem estar vazios                    │
│     • Nova senha deve ter mínimo 6 caracteres          │
│     • Confirmação deve ser igual à nova senha          │
│                                                         │
│  📄 Método específico:                                  │
│  abrirFormularioAlterarSenha()                         │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**🔍 Detalhamento do Código:**
```java
// PASSO 1: Criação dos campos do formulário
private void abrirFormularioAlterarSenha() {
    JPasswordField senhaAtualField = new JPasswordField();
    JPasswordField novaSenhaField = new JPasswordField();
    JPasswordField confirmarSenhaField = new JPasswordField();
    
    // PASSO 2: Coleta dos dados quando usuário clica OK
    String senhaAtual = new String(senhaAtualField.getPassword());
    String novaSenha = new String(novaSenhaField.getPassword());
    String confirmarSenha = new String(confirmarSenhaField.getPassword());
    
    // PASSO 3: Validações locais
    if (senhaAtual.isEmpty() || novaSenha.isEmpty()) {
        // Mostra erro: "Todos os campos são obrigatórios"
        return;
    }
    
    if (!novaSenha.equals(confirmarSenha)) {
        // Mostra erro: "Nova senha e confirmação não coincidem"  
        return;
    }
    
    if (novaSenha.length() < 6) {
        // Mostra erro: "Nova senha deve ter pelo menos 6 caracteres"
        return;
    }
    
    // PASSO 4: Se passou nas validações, chama o método de alteração
    alterarSenha(senhaAtual, novaSenha);
}
```

#### 🎮 **2. CONTROLADOR (Controller)**

**📂 Desktop: FinanceController.java**
```java
Localização: /DESKTOP VERSION/ClienteFinanza/src/controller/FinanceController.java

┌─────────────────────────────────────────────────────────┐
│ 🎮 CONTROLLER PROCESSA A SOLICITAÇÃO                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Método: alterarSenha(senhaAtual, novaSenha)           │
│  Linha: ~580                                           │
│                                                         │
│  📋 O que acontece:                                     │
│  1. Verifica se está conectado ao servidor             │
│  2. Monta comando no protocolo do sistema              │
│  3. Envia via TCP socket                               │
│  4. Processa resposta do servidor                      │
│  5. Retorna resultado para a View                      │
│                                                         │
│  📡 Comando TCP enviado:                               │
│  "CHANGE_PASSWORD|senhaAtual|novaSenha"                │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**🔍 Detalhamento do Código:**
```java
public OperationResult<Void> alterarSenha(String senhaAtual, String novaSenha) {
    // PASSO 1: Verifica conexão
    if (!networkClient.isConnected()) {
        return new OperationResult<>(false, "Não conectado ao servidor", null);
    }
    
    // PASSO 2: Monta comando seguindo protocolo
    String comando = CMD_CHANGE_PASSWORD + SEPARATOR + senhaAtual + SEPARATOR + novaSenha;
    // Resultado: "CHANGE_PASSWORD|minhasenha123|novasenha456"
    
    // PASSO 3: Envia comando via TCP
    String resposta = networkClient.sendCommand(comando);
    
    // PASSO 4: Processa resposta do servidor
    String[] partes = resposta.split("\\|");
    
    if (partes.length >= 1 && "OK".equals(partes[0])) {
        return new OperationResult<>(true, "Senha alterada com sucesso", null);
    }
    
    // PASSO 5: Retorna erro se houver
    String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
    return new OperationResult<>(false, erro, null);
}
```

#### 🌐 **3. REDE (NetworkClient)**

**📂 Desktop: NetworkClient.java**
```java
Localização: /DESKTOP VERSION/ClienteFinanza/src/util/NetworkClient.java

┌─────────────────────────────────────────────────────────┐
│ 📡 ENVIO VIA TCP SOCKET                                 │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  📋 O que acontece:                                     │
│  1. Usa socket TCP já conectado                        │
│  2. Envia comando como string                          │
│  3. Aguarda resposta do servidor                       │
│  4. Retorna resposta para o Controller                 │
│                                                         │
│  🌐 Protocolo:                                         │
│  Porta: 8080 (padrão)                                 │
│  Formato: "COMANDO|PARAM1|PARAM2"                     │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

#### 🖥️ **4. SERVIDOR (Server)**

**📂 Servidor: ClientHandler.java**
```java
Localização: /DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java

┌─────────────────────────────────────────────────────────┐
│ 🖥️ SERVIDOR RECEBE E PROCESSA                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Método: processarAlterarSenha(String[] partes)        │
│  Linha: ~280                                           │
│                                                         │
│  📋 O que acontece:                                     │
│  1. Valida se usuário está logado                      │
│  2. Valida se parâmetros estão corretos               │
│  3. Verifica senha atual no banco                      │
│  4. Atualiza senha no banco via DAO                    │
│  5. Retorna resposta de sucesso/erro                   │
│                                                         │
│  🔐 Validações de segurança:                          │
│  • Usuário autenticado                                │
│  • Senha atual correta                                │
│  • Nova senha atende critérios                        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**🔍 Detalhamento do Código:**
```java
private String processarAlterarSenha(String[] partes) {
    // PASSO 1: Validações básicas
    if (usuarioLogado == null) {
        return Protocol.createErrorResponse("Usuário não está logado");
    }
    
    if (partes.length < 3) {
        return Protocol.createErrorResponse("Senha atual e nova senha são obrigatórias");
    }
    
    String senhaAtual = partes[1];
    String novaSenha = partes[2];
    
    // PASSO 2: Validação da nova senha
    if (!SecurityUtil.validarSenha(novaSenha)) {
        return Protocol.createResponse("INVALID_DATA", "Nova senha deve ter pelo menos 6 caracteres");
    }
    
    // PASSO 3: Verifica senha atual no banco
    Usuario usuario = usuarioDAO.autenticar(usuarioLogado.getEmail(), senhaAtual);
    if (usuario == null) {
        return Protocol.createResponse("INVALID_CREDENTIALS", "Senha atual incorreta");
    }
    
    // PASSO 4: Atualiza senha no banco
    if (usuarioDAO.atualizarSenha(usuarioLogado.getId(), novaSenha)) {
        return Protocol.createSuccessResponse("Senha alterada com sucesso");
    } else {
        return Protocol.createErrorResponse("Erro ao alterar senha");
    }
}
```

#### 💾 **5. ACESSO AOS DADOS (DAO)**

**📂 Servidor: UsuarioDAO.java**
```java
Localização: /DESKTOP VERSION/ServidorFinanza/src/dao/UsuarioDAO.java

┌─────────────────────────────────────────────────────────┐
│ 💾 ACESSO AO BANCO DE DADOS                            │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Método: atualizarSenha(int idUsuario, String novaSenha)│
│  Linha: ~150                                           │
│                                                         │
│  📋 O que acontece:                                     │
│  1. Gera hash da nova senha (SHA-256)                  │
│  2. Executa UPDATE no MySQL                            │
│  3. Retorna true/false para sucesso                    │
│                                                         │
│  🗄️ SQL executado:                                     │
│  UPDATE usuario SET senha_hash = ? WHERE id = ?        │
│                                                         │
│  🔐 Segurança:                                         │
│  • Senha nunca armazenada em texto puro               │
│  • Hash SHA-256 com salt                              │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**🔍 Detalhamento do Código:**
```java
public boolean atualizarSenha(int idUsuario, String novaSenha) {
    String sql = "UPDATE usuario SET senha_hash = ? WHERE id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // PASSO 1: Gera hash seguro da nova senha
        stmt.setString(1, SecurityUtil.hashSenha(novaSenha));
        stmt.setInt(2, idUsuario);
        
        // PASSO 2: Executa update no banco
        int rowsAffected = stmt.executeUpdate();
        
        // PASSO 3: Retorna true se alterou 1 linha
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("Erro ao atualizar senha: " + e.getMessage());
        return false;
    }
}
```

#### 🔒 **6. SEGURANÇA (SecurityUtil)**

**📂 Servidor: SecurityUtil.java**
```java
Localização: /DESKTOP VERSION/ServidorFinanza/src/util/SecurityUtil.java

┌─────────────────────────────────────────────────────────┐
│ 🔒 CRIPTOGRAFIA E VALIDAÇÃO                            │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Método: hashSenha(String senha)                       │
│                                                         │
│  📋 O que acontece:                                     │
│  1. Aplica algoritmo SHA-256                           │
│  2. Adiciona salt para segurança                       │
│  3. Retorna hash final                                 │
│                                                         │
│  🔐 Exemplo:                                           │
│  Entrada: "minhasenha123"                              │
│  Saída: "a1b2c3d4e5f6..." (64 caracteres)             │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 🔄 **FLUXO COMPLETO VISUAL**

```
┌─────────────┐  clica "Alterar  ┌─────────────┐  chama método   ┌─────────────┐
│             │    Senha"        │             │ alterarSenha()  │             │
│ PerfilView  │ ──────────────→  │ Controller  │ ──────────────→ │ NetworkClient│
│             │                  │             │                 │             │
│ (Interface) │                  │ (Lógica)    │                 │ (TCP)       │
└─────────────┘                  └─────────────┘                 └─────────────┘
       ↑                                                                │
       │                                                                │
       │ resultado                                                      │ TCP
       │                                                                ▼
┌─────────────┐                  ┌─────────────┐  usa DAO       ┌─────────────┐
│             │                  │             │ atualizarSenha()│             │
│ SwingWorker │                  │ Server      │ ──────────────→ │ UsuarioDAO  │
│             │                  │             │                 │             │
│ (Threading) │                  │ (Handler)   │                 │ (Database)  │
└─────────────┘                  └─────────────┘                 └─────────────┘
                                                                         │
                                                                         │
                                                                         ▼
                                                                ┌─────────────┐
                                                                │             │
                                                                │ MySQL       │
                                                                │             │
                                                                │ (Banco)     │
                                                                └─────────────┘
```

### 📋 **RESUMO DO CAMINHO:**

1. **👤 PerfilView.java** → Usuário clica no botão
2. **🎮 FinanceController.java** → Processa a solicitação  
3. **🌐 NetworkClient.java** → Envia via TCP
4. **🖥️ ClientHandler.java** → Recebe no servidor
5. **💾 UsuarioDAO.java** → Atualiza no banco
6. **🔐 SecurityUtil.java** → Aplica criptografia
7. **🗄️ MySQL** → Persiste dados
8. **↩️ Retorno** → Resposta volta o mesmo caminho

---

## 🔑 Fluxo: Login e Autenticação

### 📍 **Quando o usuário faz login no sistema**

#### 🎯 **1. TELA DE LOGIN**

**📱 Desktop: LoginView.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 🔑 USUÁRIO INSERE CREDENCIAIS                          │
├─────────────────────────────────────────────────────────┤
│  Campos:                                               │
│  • Email: usuario@exemplo.com                          │
│  • Senha: ************                                 │
│                                                         │
│  Botão: "Entrar" → loginButtonActionPerformed()        │
└─────────────────────────────────────────────────────────┘
```

#### 🎮 **2. CONTROLLER DE AUTENTICAÇÃO**

**📂 AuthController.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 🎮 PROCESSA LOGIN                                       │
├─────────────────────────────────────────────────────────┤
│  Método: login(email, senha)                           │
│                                                         │
│  1. Valida formato do email                            │
│  2. Verifica se campos não estão vazios                │
│  3. Conecta ao servidor se necessário                  │
│  4. Envia comando: "LOGIN|email|senha"                 │
│  5. Processa resposta                                  │
│                                                         │
│  Sucesso: Armazena dados do usuário logado             │
│  Erro: Retorna mensagem de erro                        │
└─────────────────────────────────────────────────────────┘
```

#### 🖥️ **3. SERVIDOR AUTENTICA**

**📂 ClientHandler.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 🖥️ VALIDAÇÃO NO SERVIDOR                               │
├─────────────────────────────────────────────────────────┤
│  Método: processarLogin(String[] partes)               │
│                                                         │
│  1. Extrai email e senha do comando                    │
│  2. Chama UsuarioDAO.autenticar()                      │
│  3. Verifica hash da senha                             │
│  4. Se válido: cria sessão do usuário                  │
│  5. Responde: "OK|id|nome|email"                       │
│                                                         │
│  Erro: "ERROR|Credenciais inválidas"                   │
└─────────────────────────────────────────────────────────┘
```

#### 💾 **4. VALIDAÇÃO NO BANCO**

**📂 UsuarioDAO.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 💾 CONSULTA NO MYSQL                                   │
├─────────────────────────────────────────────────────────┤
│  Método: autenticar(email, senha)                      │
│                                                         │
│  1. Busca usuário por email                            │
│  2. Compara hash da senha fornecida                    │
│  3. Retorna objeto Usuario se válido                   │
│  4. Retorna null se inválido                           │
│                                                         │
│  SQL: SELECT * FROM usuario WHERE email = ?            │
│  Validação: SecurityUtil.verificarSenha()              │
└─────────────────────────────────────────────────────────┘
```

---

## 💰 Fluxo: Adicionar Movimentação

### 📍 **Quando o usuário adiciona uma nova transação**

#### 🎯 **1. FORMULÁRIO DE MOVIMENTAÇÃO**

**📱 Desktop: MovimentacaoFormDialog.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 💰 USUÁRIO PREENCHE TRANSAÇÃO                          │
├─────────────────────────────────────────────────────────┤
│  Campos obrigatórios:                                  │
│  • Descrição: "Almoço restaurante"                     │
│  • Valor: R$ 45,90                                     │
│  • Data: 15/03/2024                                    │
│  • Tipo: DESPESA                                       │
│  • Conta: Conta Corrente                              │
│  • Categoria: Alimentação                              │
│                                                         │
│  Botão: "Salvar" → salvarMovimentacao()                │
└─────────────────────────────────────────────────────────┘
```

#### 🎮 **2. CONTROLLER FINANCEIRO**

**📂 FinanceController.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 🎮 PROCESSA NOVA MOVIMENTAÇÃO                          │
├─────────────────────────────────────────────────────────┤
│  Método: adicionarMovimentacao(movimentacao)           │
│                                                         │
│  1. Valida dados obrigatórios                          │
│  2. Verifica se valor > 0                              │
│  3. Serializa objeto para protocolo                    │
│  4. Envia: "ADD_MOVEMENT|desc|valor|data|tipo|..."      │
│  5. Aguarda confirmação do servidor                    │
│                                                         │
│  Sucesso: Atualiza interface                           │
│  Erro: Mostra mensagem de erro                         │
└─────────────────────────────────────────────────────────┘
```

#### 🖥️ **3. SERVIDOR PROCESSA**

**📂 ClientHandler.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 🖥️ INSERE MOVIMENTAÇÃO                                 │
├─────────────────────────────────────────────────────────┤
│  Método: processarAdicionarMovimentacao()              │
│                                                         │
│  1. Valida se usuário está logado                      │
│  2. Verifica se conta/categoria pertencem ao usuário   │
│  3. Cria objeto Movimentacao                           │
│  4. Chama MovimentacaoDAO.inserir()                    │
│  5. Atualiza saldo da conta                            │
│  6. Notifica outros clientes conectados                │
│                                                         │
│  Resposta: "OK|id_movimentacao_criada"                 │
└─────────────────────────────────────────────────────────┘
```

#### 💾 **4. PERSISTÊNCIA**

**📂 MovimentacaoDAO.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 💾 SALVA NO BANCO                                      │
├─────────────────────────────────────────────────────────┤
│  Método: inserir(movimentacao)                         │
│                                                         │
│  1. Inicia transação no MySQL                          │
│  2. INSERT na tabela movimentacao                      │
│  3. UPDATE no saldo da conta                           │
│  4. Commit da transação                                │
│  5. Retorna ID da movimentação criada                  │
│                                                         │
│  SQL: INSERT INTO movimentacao (...) VALUES (...)      │
│  SQL: UPDATE conta SET saldo = saldo + valor WHERE...  │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 Fluxo: Sincronização de Dados

### 📍 **Como funciona a sincronização automática**

#### 📱 **1. MOBILE DETECTA MUDANÇA**

**📂 SyncService.java (Android)**
```java
┌─────────────────────────────────────────────────────────┐
│ 📱 MOBILE IDENTIFICA DADOS MODIFICADOS                 │
├─────────────────────────────────────────────────────────┤
│  Triggers:                                             │
│  • Nova transação adicionada                           │
│  • Timer automático (30 segundos)                      │
│  • App volta do background                             │
│  • Conexão de rede restabelecida                       │
│                                                         │
│  Processo:                                             │
│  1. Verifica dados com flag "pending_sync"             │
│  2. Serializa dados para protocolo                     │
│  3. Envia em lote para servidor                        │
└─────────────────────────────────────────────────────────┘
```

#### 🌐 **2. PROTOCOLO DE SINCRONIZAÇÃO**

**📂 Protocol.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 🌐 COMANDOS DE SINCRONIZAÇÃO                           │
├─────────────────────────────────────────────────────────┤
│  Comandos disponíveis:                                 │
│  • SYNC_REQUEST|timestamp_ultima_sync                  │
│  • SYNC_DATA|tipo|operacao|dados                       │
│  • SYNC_RESPONSE|dados_servidor                        │
│  • CONFLICT|id|dados_cliente|dados_servidor            │
│                                                         │
│  Tipos de dados:                                       │
│  • USER, ACCOUNT, CATEGORY, MOVEMENT                   │
│                                                         │
│  Operações:                                            │
│  • CREATE, UPDATE, DELETE                              │
└─────────────────────────────────────────────────────────┘
```

#### 🖥️ **3. SERVIDOR COORDENA**

**📂 ClientHandler.java**
```java
┌─────────────────────────────────────────────────────────┐
│ 🖥️ SERVIDOR GERENCIA SINCRONIZAÇÃO                     │
├─────────────────────────────────────────────────────────┤
│  Processo:                                             │
│  1. Recebe dados do cliente                            │
│  2. Verifica conflitos por timestamp                   │
│  3. Aplica mudanças no MySQL                           │
│  4. Propaga para outros clientes conectados            │
│  5. Mantém log de sincronização                        │
│                                                         │
│  Resolução de conflitos:                               │
│  • Automática: último timestamp vence                  │
│  • Manual: envia dados para escolha do usuário         │
└─────────────────────────────────────────────────────────┘
```

---

## 🎯 Mapeamento de Código

### 📂 **Principais Arquivos e Responsabilidades**

| Arquivo | Responsabilidade | Métodos Principais |
|---------|-----------------|-------------------|
| **PerfilView.java** | Interface do perfil | `abrirFormularioAlterarSenha()` |
| **FinanceController.java** | Lógica de negócio | `alterarSenha()`, `adicionarMovimentacao()` |
| **NetworkClient.java** | Comunicação TCP | `sendCommand()`, `connect()` |
| **ClientHandler.java** | Servidor TCP | `processarAlterarSenha()`, `run()` |
| **UsuarioDAO.java** | Acesso a dados | `atualizarSenha()`, `autenticar()` |
| **SecurityUtil.java** | Segurança | `hashSenha()`, `verificarSenha()` |
| **Protocol.java** | Protocolo de comunicação | `createResponse()`, `parseCommand()` |

### 🔍 **Como Navegar no Código**

#### **Para entender uma funcionalidade:**

1. **Comece pela View** → Interface que o usuário vê
2. **Vá para o Controller** → Lógica de processamento  
3. **Siga para o Network** → Comunicação com servidor
4. **Examine o Server** → Processamento no servidor
5. **Termine no DAO** → Persistência dos dados

#### **Para adicionar nova funcionalidade:**

1. **Model** → Crie/modifique entidades
2. **DAO** → Implemente acesso aos dados
3. **Server** → Adicione processamento no servidor
4. **Protocol** → Defina comandos de comunicação
5. **Controller** → Implemente lógica no cliente
6. **View** → Crie interface do usuário

Este mapeamento mostra exatamente **onde cada coisa acontece** e **como navegar pelo código** quando você precisar entender ou modificar funcionalidades!