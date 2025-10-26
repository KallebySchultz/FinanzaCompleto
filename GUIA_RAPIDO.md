# 🚀 GUIA RÁPIDO - Sistema Finanza

## 📖 Visão Geral Simples

O **Finanza** é um sistema de controle financeiro com **3 componentes principais**:

```
┌─────────────────┐
│  App Mobile     │ ←────┐
│  (Android)      │      │
└─────────────────┘      │
                         │ Comunicação
┌─────────────────┐      │ via Socket
│  Desktop Admin  │ ←────┤ TCP/IP
│  (Java Swing)   │      │ Porta 12345
└─────────────────┘      │
                         │
┌─────────────────┐      │
│  Servidor       │ ←────┘
│  (Java + MySQL) │
└─────────────────┘
```

---

## 🎯 O Que Cada Componente Faz

### 📱 Mobile (Android)
**Para:** Usuários comuns  
**Função:** Gerenciar suas finanças pessoais

**Pode fazer:**
- ✅ Criar conta (email + senha)
- ✅ Fazer login
- ✅ Adicionar contas bancárias (corrente, poupança, etc)
- ✅ Adicionar categorias (alimentação, transporte, etc)
- ✅ Lançar receitas e despesas
- ✅ Ver dashboard com resumo financeiro
- ✅ Sincronizar dados com servidor
- ✅ Funcionar offline (sincroniza depois)

### 💻 Desktop Admin (Java Swing)
**Para:** Administradores  
**Função:** Gerenciar usuários do sistema

**Pode fazer:**
- ✅ Login como admin
- ✅ Ver lista de todos os usuários
- ✅ Editar dados dos usuários
- ✅ Alterar senhas
- ✅ Gerenciar seu próprio perfil

### 🖥️ Servidor (Java + MySQL)
**Para:** Sistema (roda automaticamente)  
**Função:** Armazenar e processar dados

**Responsável por:**
- ✅ Receber conexões dos clientes (mobile/desktop)
- ✅ Validar logins
- ✅ Salvar dados no banco MySQL
- ✅ Sincronizar dados entre dispositivos
- ✅ Resolver conflitos de sincronização

---

## 📂 Estrutura de Pastas Simplificada

```
FinanzaCompleto/
│
├── app/                              # 📱 Aplicativo Android
│   └── src/main/java/com/example/finanza/
│       ├── ui/                       # Telas do app (8 Activities)
│       │   ├── LoginActivity.java        → Tela de login
│       │   ├── RegisterActivity.java     → Tela de cadastro
│       │   ├── MainActivity.java         → Tela principal/dashboard
│       │   ├── AccountsActivity.java     → Gerenciar contas
│       │   ├── CategoriaActivity.java    → Gerenciar categorias
│       │   ├── MovementsActivity.java    → Lançamentos financeiros
│       │   ├── ProfileActivity.java      → Perfil do usuário
│       │   └── SettingsActivity.java     → Configurações
│       │
│       ├── model/                    # Modelos de dados (4 classes)
│       │   ├── Usuario.java             → Dados do usuário
│       │   ├── Conta.java               → Dados da conta bancária
│       │   ├── Categoria.java           → Dados da categoria
│       │   └── Lancamento.java          → Dados do lançamento/transação
│       │
│       ├── db/                       # Banco de dados local (SQLite/Room)
│       │   ├── AppDatabase.java         → Configuração do banco
│       │   ├── UsuarioDao.java          → Acesso aos dados de usuário
│       │   ├── ContaDao.java            → Acesso aos dados de conta
│       │   ├── CategoriaDao.java        → Acesso aos dados de categoria
│       │   └── LancamentoDao.java       → Acesso aos dados de lançamento
│       │
│       ├── network/                  # Comunicação com servidor
│       │   ├── ServerClient.java        → Cliente de conexão TCP
│       │   ├── Protocol.java            → Protocolo de comunicação
│       │   ├── AuthManager.java         → Gerenciador de autenticação
│       │   ├── SyncService.java         → Serviço de sincronização
│       │   ├── EnhancedSyncService.java → Sincronização avançada
│       │   └── ConflictResolutionManager.java → Resolver conflitos
│       │
│       └── util/                     # Utilitários
│           └── DataIntegrityValidator.java → Validar dados
│
├── DESKTOP VERSION/
│   │
│   ├── ServidorFinanza/              # 🖥️ Servidor Java
│   │   └── src/
│   │       ├── server/               # Servidor TCP
│   │       │   ├── FinanzaServer.java    → Servidor principal
│   │       │   ├── ClientHandler.java    → Manipulador de clientes
│   │       │   └── Protocol.java         → Protocolo de comandos
│   │       │
│   │       ├── dao/                  # Acesso ao banco MySQL
│   │       │   ├── UsuarioDAO.java
│   │       │   ├── ContaDAO.java
│   │       │   ├── CategoriaDAO.java
│   │       │   └── MovimentacaoDAO.java
│   │       │
│   │       ├── model/                # Modelos de dados
│   │       │   ├── Usuario.java
│   │       │   ├── Conta.java
│   │       │   ├── Categoria.java
│   │       │   └── Movimentacao.java
│   │       │
│   │       └── util/                 # Utilitários
│   │           ├── DatabaseUtil.java     → Conexão MySQL
│   │           └── SecurityUtil.java     → Criptografia
│   │
│   └── ClienteFinanza/               # 💻 Cliente Desktop Admin
│       └── src/
│           ├── view/                 # Interface gráfica (Swing)
│           │   ├── LoginView.java        → Tela de login admin
│           │   ├── AdminDashboardView.java → Dashboard admin
│           │   └── EditarUsuarioDialog.java → Editar usuário
│           │
│           ├── controller/           # Controladores
│           │   ├── AuthController.java   → Controle de autenticação
│           │   └── FinanceController.java → Controle financeiro
│           │
│           ├── model/                # Modelos de dados
│           │   ├── Usuario.java
│           │   ├── Conta.java
│           │   ├── Categoria.java
│           │   └── Movimentacao.java
│           │
│           └── util/                 # Utilitários
│               └── NetworkClient.java    → Cliente de rede
│
└── database/                         # Scripts SQL
    └── script_inicial.sql                → Criar banco MySQL
```

---

## 🔄 Como os Componentes se Comunicam

### Exemplo 1: Usuário Faz Login no Mobile

```
┌──────────────────────────────────────────────────────────────┐
│ 1. USUÁRIO DIGITA EMAIL E SENHA                              │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 2. LoginActivity.java                                        │
│    - Valida campos não vazios                                │
│    - Chama AuthManager.login(email, senha)                   │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 3. AuthManager.java                                          │
│    - Criptografa senha (SHA-256)                             │
│    - Formata comando: "LOGIN|email|senhaHash"                │
│    - Chama ServerClient.sendCommand()                        │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 4. ServerClient.java                                         │
│    - Conecta ao servidor (porta 12345)                       │
│    - Envia comando via Socket TCP                            │
│    - Aguarda resposta                                        │
└──────────────────────────────────────────────────────────────┘
                           ↓
                    【 INTERNET 】
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 5. FinanzaServer.java (Servidor)                             │
│    - Recebe conexão                                          │
│    - Cria ClientHandler para processar                       │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 6. ClientHandler.java                                        │
│    - Recebe comando "LOGIN|email|senhaHash"                  │
│    - Extrai email e senha                                    │
│    - Chama UsuarioDAO.buscarPorEmail(email)                  │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 7. UsuarioDAO.java                                           │
│    - Executa query SQL no MySQL:                             │
│      SELECT * FROM usuario WHERE email = 'email@exemplo.com' │
│    - Retorna dados do usuário                                │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 8. ClientHandler.java                                        │
│    - Compara senhaHash recebida com senhaHash do banco       │
│    - Se iguais: retorna "OK|{dados_usuario}"                 │
│    - Se diferentes: retorna "ERROR|Credenciais inválidas"    │
└──────────────────────────────────────────────────────────────┘
                           ↓
                    【 INTERNET 】
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 9. ServerClient.java (Mobile)                                │
│    - Recebe resposta                                         │
│    - Retorna para AuthManager                                │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 10. AuthManager.java                                         │
│     - Parse da resposta                                      │
│     - Se OK: salva dados no banco local (Room/SQLite)        │
│     - Retorna sucesso para LoginActivity                     │
└──────────────────────────────────────────────────────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│ 11. LoginActivity.java                                       │
│     - Exibe mensagem "Login realizado com sucesso!"          │
│     - Abre MainActivity (tela principal)                     │
└──────────────────────────────────────────────────────────────┘
```

**Resumo:** 11 passos do dedo do usuário até a tela principal!

---

### Exemplo 2: Usuário Adiciona uma Despesa

```
USUÁRIO: Gastei R$ 50,00 no almoço
    ↓
MovementsActivity.java → Exibe formulário
    ↓
USUÁRIO: Preenche dados e clica "Salvar"
    ↓
MovementsActivity → Valida dados
    ↓
DataIntegrityValidator → Verifica se dados estão corretos
    ↓
LancamentoDao → Salva no banco local (SQLite)
    ↓
ContaDao → Atualiza saldo da conta (subtrai R$ 50)
    ↓
EnhancedSyncService → Envia para o servidor
    ↓
【 Internet 】
    ↓
ClientHandler (Servidor) → Recebe dados
    ↓
MovimentacaoDAO → Salva no MySQL
    ↓
ContaDAO → Atualiza saldo no servidor
    ↓
Resposta: "OK|id_da_movimentacao"
    ↓
【 Internet 】
    ↓
MovementsActivity → Atualiza lista na tela
    ↓
USUÁRIO: Vê despesa de R$ 50 na lista!
```

---

## 🗂️ Banco de Dados

### Mobile (SQLite - local no celular)

**4 Tabelas principais:**

1. **usuario** - Dados do usuário logado
2. **conta** - Contas bancárias (Nubank, Caixa, etc)
3. **categoria** - Categorias (Alimentação, Transporte, etc)
4. **lancamento** - Transações/Movimentações financeiras

### Servidor (MySQL - banco central)

**4 Tabelas principais (espelhadas):**

1. **usuario** - Todos os usuários do sistema
2. **conta** - Todas as contas de todos os usuários
3. **categoria** - Todas as categorias
4. **movimentacao** - Todas as transações

---

## 📊 Arquivos Principais e Sua Função

### Mobile - Telas (Activities)

| Arquivo | O que faz | O que você vê |
|---------|-----------|---------------|
| **LoginActivity.java** | Tela de login | Campos de email e senha + botão Entrar |
| **RegisterActivity.java** | Tela de cadastro | Formulário com nome, email, senha |
| **MainActivity.java** | Tela principal | Dashboard com saldo total, receitas, despesas |
| **AccountsActivity.java** | Gerenciar contas | Lista de contas (Nubank, Caixa, etc) |
| **CategoriaActivity.java** | Gerenciar categorias | Lista de categorias (Alimentação, etc) |
| **MovementsActivity.java** | Lançamentos | Lista de transações (receitas e despesas) |
| **ProfileActivity.java** | Perfil | Dados do usuário + editar |
| **SettingsActivity.java** | Configurações | Preferências do app |

### Mobile - Banco de Dados (DAOs)

| Arquivo | O que faz |
|---------|-----------|
| **AppDatabase.java** | Configura o banco SQLite/Room |
| **UsuarioDao.java** | Busca, salva, atualiza usuários |
| **ContaDao.java** | Busca, salva, atualiza contas (30+ métodos) |
| **CategoriaDao.java** | Busca, salva, atualiza categorias |
| **LancamentoDao.java** | Busca, salva, atualiza lançamentos (40+ métodos) |

**Por que tantos métodos?**
- Buscar por ID
- Buscar por usuário
- Buscar por período (últimos 30 dias)
- Buscar por tipo (receita/despesa)
- Somar valores
- Calcular saldos
- Sincronizar com servidor
- Detectar duplicatas
- E muito mais!

### Mobile - Comunicação

| Arquivo | O que faz |
|---------|-----------|
| **ServerClient.java** | Conecta ao servidor via Socket TCP |
| **Protocol.java** | Define comandos (LOGIN, REGISTER, ADD_CONTA, etc) |
| **AuthManager.java** | Gerencia login e sessão do usuário |
| **SyncService.java** | Sincroniza dados com servidor |
| **EnhancedSyncService.java** | Sincronização avançada e inteligente |
| **ConflictResolutionManager.java** | Resolve conflitos de sincronização |

### Servidor - Núcleo

| Arquivo | O que faz |
|---------|-----------|
| **FinanzaServer.java** | Inicia servidor na porta 12345 |
| **ClientHandler.java** | Processa comandos dos clientes (LOGIN, etc) |
| **Protocol.java** | Define protocolo de comunicação |

### Servidor - Banco de Dados (DAOs)

| Arquivo | O que faz |
|---------|-----------|
| **UsuarioDAO.java** | Acessa tabela `usuario` do MySQL |
| **ContaDAO.java** | Acessa tabela `conta` do MySQL |
| **CategoriaDAO.java** | Acessa tabela `categoria` do MySQL |
| **MovimentacaoDAO.java** | Acessa tabela `movimentacao` do MySQL |

### Desktop Admin - Interface

| Arquivo | O que faz | O que você vê |
|---------|-----------|---------------|
| **LoginView.java** | Tela de login admin | Formulário de login |
| **AdminDashboardView.java** | Dashboard admin | Tabela com lista de usuários |
| **EditarUsuarioDialog.java** | Editar usuário | Formulário para editar dados |

---

## 🔑 Conceitos Importantes

### 1. DAO (Data Access Object)
**O que é:** Classe que acessa o banco de dados  
**Por que usar:** Organiza código e separa lógica de banco de dados  
**Exemplo:** `UsuarioDao.buscarPorEmail("teste@gmail.com")`

### 2. Model (Modelo)
**O que é:** Classe que representa uma entidade (Usuário, Conta, etc)  
**Por que usar:** Organiza dados de forma estruturada  
**Exemplo:**
```java
Usuario usuario = new Usuario();
usuario.setNome("João");
usuario.setEmail("joao@gmail.com");
```

### 3. Activity (Android)
**O que é:** Uma tela do aplicativo Android  
**Por que usar:** Cada tela é uma Activity diferente  
**Exemplo:** LoginActivity = tela de login

### 4. Sincronização
**O que é:** Manter dados iguais no celular e servidor  
**Por que usar:** Funcionar offline e ter backup  
**Como funciona:**
1. Usuário cria despesa no celular → salva no SQLite
2. App envia para servidor → salva no MySQL
3. Se não tem internet, envia depois
4. Se mudou no celular E no servidor, resolve conflito

### 5. Socket TCP/IP
**O que é:** Comunicação direta entre celular e servidor  
**Por que usar:** Rápido e em tempo real  
**Como funciona:** Como um telefone, um liga e outro atende

### 6. Protocolo de Comunicação
**O que é:** "Linguagem" que celular e servidor usam  
**Formato:** `COMANDO|PARAMETRO1|PARAMETRO2|...`  
**Exemplos:**
- `LOGIN|joao@gmail.com|senha_hash`
- `ADD_CONTA|Nubank|corrente|1000.00|1`
- `LIST_MOVIMENTACOES|1|2024-01-01|2024-12-31`

---

## 🎓 Para Entender o Código

### Passo 1: Comece pelas Telas (Activities)
1. Abra `LoginActivity.java`
2. Leia os comentários no topo
3. Veja o método `onCreate()` - é o que roda quando abre a tela
4. Veja o método `realizarLogin()` - é o que roda quando clica no botão

### Passo 2: Entenda os Modelos (Models)
1. Abra `Usuario.java`
2. Veja os campos: `id`, `nome`, `email`, `senhaHash`
3. Veja os getters/setters: `getNome()`, `setNome()`

### Passo 3: Entenda o Banco Local (DAOs)
1. Abra `UsuarioDao.java`
2. Veja os métodos disponíveis
3. Entenda que cada método faz uma operação no banco

### Passo 4: Entenda a Comunicação
1. Abra `ServerClient.java`
2. Veja como conecta ao servidor
3. Veja como envia comandos

### Passo 5: Entenda o Servidor
1. Abra `FinanzaServer.java` - inicia servidor
2. Abra `ClientHandler.java` - processa comandos
3. Veja o switch/case com todos os comandos

---

## 📝 Fluxo de Dados Resumido

### Criar Conta Bancária

```
Usuário preenche formulário
    ↓
AccountsActivity valida dados
    ↓
Cria objeto Conta
    ↓
ContaDao.inserir(conta) → salva no SQLite
    ↓
EnhancedSyncService.syncConta(conta)
    ↓
ServerClient.sendCommand("ADD_CONTA_ENHANCED|...")
    ↓
【 Socket TCP 】
    ↓
ClientHandler recebe comando
    ↓
ContaDAO.criar(conta) → salva no MySQL
    ↓
Resposta: "OK|id_conta"
    ↓
【 Socket TCP 】
    ↓
ContaDao.marcarComoSincronizado()
    ↓
AccountsActivity atualiza lista
```

---

## 🚦 Estados de Sincronização

Cada registro (conta, categoria, lançamento) tem um `syncStatus`:

| Status | Valor | Significado |
|--------|-------|-------------|
| **LOCAL_ONLY** | 0 | Existe só no celular |
| **SYNCED** | 1 | Sincronizado com servidor ✅ |
| **NEEDS_SYNC** | 2 | Modificado, precisa sincronizar 🔄 |
| **CONFLICT** | 3 | Conflito detectado ⚠️ |
| **DELETED** | 4 | Deletado (não aparece mais) 🗑️ |

---

## 🎯 Comandos Principais do Protocolo

### Autenticação
- `LOGIN|email|senha_hash` - Fazer login
- `REGISTER|nome|email|senha|uuid|timestamp` - Criar conta
- `LOGOUT` - Sair

### Contas
- `ADD_CONTA_ENHANCED|uuid|nome|tipo|saldo|...` - Adicionar conta
- `UPDATE_CONTA_ENHANCED|uuid|nome|tipo|saldo|...` - Atualizar conta
- `DELETE_CONTA|id` - Deletar conta
- `LIST_CONTAS|usuario_id` - Listar contas

### Lançamentos
- `ADD_MOVIMENTACAO_ENHANCED|uuid|valor|data|desc|...` - Adicionar
- `UPDATE_MOVIMENTACAO_ENHANCED|...` - Atualizar
- `DELETE_MOVIMENTACAO|id` - Deletar
- `LIST_MOVIMENTACOES|usuario_id` - Listar

### Sincronização
- `LIST_CHANGES_SINCE|timestamp` - Listar mudanças desde data X
- `BULK_UPLOAD|dados...` - Enviar vários registros de uma vez

---

## 💡 Dicas para Navegar no Código

### 1. Use a Busca do Editor
- Ctrl+F (ou Cmd+F no Mac) para buscar texto
- Ctrl+Shift+F para buscar em todos os arquivos

### 2. Siga as Chamadas
- Viu `authManager.login()`? → Abra `AuthManager.java` e procure o método `login()`
- Viu `contaDao.inserir()`? → Abra `ContaDao.java` e procure o método `inserir()`

### 3. Leia os Comentários
- Todo arquivo tem comentário no topo explicando o que faz
- Todo método importante tem comentário explicando

### 4. Veja os Exemplos no MAPEAMENTO_COMPLETO.md
- Exemplos completos de fluxo
- Código real de cada etapa

---

## 🎨 Arquitetura Visual

```
┌─────────────────────────────────────────────────────────────┐
│                    MOBILE APP (Android)                     │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │ Activities  │  │   Models    │  │     DAOs    │        │
│  │ (Telas UI)  │→ │   (Dados)   │→ │  (Banco)    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│         ↓                                   ↓               │
│  ┌──────────────────────────────────────────────────┐      │
│  │          Network Layer (Comunicação)             │      │
│  │  ServerClient → Protocol → SyncService           │      │
│  └──────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
                           ↓
                    【 SOCKET TCP 】
                    【 Porta 12345 】
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                 SERVIDOR JAVA (Backend)                     │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────┐      │
│  │         FinanzaServer (Escuta conexões)          │      │
│  └──────────────────────────────────────────────────┘      │
│         ↓                                                   │
│  ┌──────────────────────────────────────────────────┐      │
│  │  ClientHandler (Processa cada cliente)           │      │
│  └──────────────────────────────────────────────────┘      │
│         ↓                                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │    DAOs     │→ │   Models    │→ │    MySQL    │        │
│  │  (Acesso)   │  │   (Dados)   │  │  (Banco)    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                           ↑
                    【 SOCKET TCP 】
                           ↑
┌─────────────────────────────────────────────────────────────┐
│              DESKTOP ADMIN (Java Swing)                     │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐       │
│  │    Views    │→ │ Controllers  │→ │ NetworkClient│       │
│  │  (Telas)    │  │   (Lógica)   │  │ (Comunica)  │       │
│  └─────────────┘  └──────────────┘  └─────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

---

## 📚 Recursos Adicionais

- **MAPEAMENTO_COMPLETO.md** - Documentação técnica completa
- **README.md** - Informações gerais do projeto
- **CHANGES_SUMMARY.md** - Histórico de mudanças
- **IMPROVEMENTS_SUMMARY.md** - Melhorias implementadas

---

## ✨ Conclusão

O sistema Finanza é um **sistema cliente-servidor completo** que demonstra:
- ✅ Desenvolvimento mobile (Android)
- ✅ Desenvolvimento desktop (Java Swing)
- ✅ Desenvolvimento de servidor (Java + MySQL)
- ✅ Comunicação via rede (Sockets TCP/IP)
- ✅ Banco de dados local (SQLite/Room) e remoto (MySQL)
- ✅ Sincronização bidirecional
- ✅ Resolução de conflitos
- ✅ Segurança (criptografia de senha)

**Ideal para aprender:** POO, Arquitetura de Software, Redes, Banco de Dados!

---

Desenvolvido como Trabalho Interdisciplinar - IFSUL Campus Venâncio Aires  
**Autor:** Kalleby Schultz
