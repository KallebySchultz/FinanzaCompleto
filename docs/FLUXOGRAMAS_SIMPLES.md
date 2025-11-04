# 🔄 Fluxogramas Simples - Sistema Finanza

## 📖 Sobre este Documento

Este documento contém **fluxogramas simples e diretos** explicando como cada parte do sistema funciona. Todos os diagramas usam a sintaxe **Mermaid** que pode ser visualizada no GitHub.

---

## 🗂️ Índice

1. [Visão Geral do Sistema](#visão-geral-do-sistema)
2. [Fluxos de Autenticação](#fluxos-de-autenticação)
3. [Fluxos do Desktop Admin](#fluxos-do-desktop-admin)
4. [Fluxos do Mobile](#fluxos-do-mobile)
5. [Fluxos do Servidor](#fluxos-do-servidor)
6. [Sincronização](#sincronização)

---

## 🎯 Visão Geral do Sistema

### Arquitetura Completa

```mermaid
graph TB
    subgraph Mobile["📱 MOBILE ANDROID"]
        M1[Activity - Telas]
        M2[Model - Dados]
        M3[SQLite Local]
        M4[Network - Comunicação]
        M1 --> M2
        M2 --> M3
        M1 --> M4
    end
    
    subgraph Desktop["💻 DESKTOP ADMIN"]
        D1[View - Telas Swing]
        D2[Controller - Lógica]
        D3[Model - Dados]
        D4[NetworkClient]
        D1 --> D2
        D2 --> D3
        D2 --> D4
    end
    
    subgraph Servidor["🖥️ SERVIDOR JAVA"]
        S1[FinanzaServer]
        S2[ClientHandler]
        S3[Protocol]
        S4[DAO]
        S5[Model]
        S1 --> S2
        S2 --> S3
        S3 --> S4
        S4 --> S5
    end
    
    subgraph Banco["🗄️ BANCO DE DADOS"]
        B1[(MySQL)]
    end
    
    M4 -.Internet.-> S1
    D4 -.Rede Local.-> S1
    S4 --> B1
```

### Componentes Principais

```mermaid
graph LR
    A[👤 Usuário] --> B{Usa qual?}
    B -->|Celular| C[📱 App Android]
    B -->|Computador| D[💻 Desktop Admin]
    C --> E[🖥️ Servidor]
    D --> E
    E --> F[🗄️ MySQL]
```

---

## 🔐 Fluxos de Autenticação

### 1. Login - Desktop Admin

```mermaid
sequenceDiagram
    participant U as 👤 Admin
    participant V as LoginView
    participant C as AuthController
    participant N as NetworkClient
    participant S as Servidor
    participant DB as MySQL
    
    U->>V: Digita email e senha
    U->>V: Clica "Entrar"
    V->>V: Valida campos preenchidos
    V->>C: login(email, senha)
    C->>N: sendCommand("LOGIN...")
    N->>S: Envia via Socket
    S->>DB: SELECT * FROM usuario WHERE email=?
    DB->>S: Retorna dados do usuário
    S->>S: Valida senha (hash)
    
    alt Senha correta
        S->>N: "OK|dados_usuario"
        N->>C: Retorna resposta OK
        C->>V: LoginResult(sucesso=true)
        V->>V: Fecha LoginView
        V->>V: Abre AdminDashboardView
    else Senha incorreta
        S->>N: "ERROR|Credenciais inválidas"
        N->>C: Retorna erro
        C->>V: LoginResult(sucesso=false)
        V->>U: Mostra mensagem de erro
    end
```

### 2. Login - Mobile Android

```mermaid
flowchart TD
    A[MainActivity inicia] --> B{Usuário<br/>está logado?}
    B -->|Sim| C[Abre MenuActivity]
    B -->|Não| D[Abre LoginActivity]
    D --> E[Usuário digita<br/>email e senha]
    E --> F[Clica em Entrar]
    F --> G[AuthManager.login]
    G --> H{Está online?}
    H -->|Sim| I[Envia para servidor]
    H -->|Não| J[Valida no SQLite local]
    I --> K{Servidor<br/>validou?}
    K -->|Sim| L[Salva sessão]
    K -->|Não| M[Mostra erro]
    J --> N{Existe<br/>localmente?}
    N -->|Sim| L
    N -->|Não| M
    L --> O[Vai para MenuActivity]
    M --> E
```

### 3. Registro de Novo Usuário - Mobile

```mermaid
flowchart TD
    A[RegisterActivity] --> B[Usuário preenche:<br/>Nome, Email, Senha]
    B --> C[Clica em Registrar]
    C --> D{Campos<br/>válidos?}
    D -->|Não| E[Mostra erro<br/>de validação]
    D -->|Sim| F{Email<br/>válido?}
    E --> B
    F -->|Não| G[Mostra erro:<br/>Email inválido]
    F -->|Sim| H{Senhas<br/>coincidem?}
    G --> B
    H -->|Não| I[Mostra erro:<br/>Senhas diferentes]
    H -->|Sim| J[AuthManager.register]
    I --> B
    J --> K[Envia REGISTER<br/>para servidor]
    K --> L{Servidor<br/>registrou?}
    L -->|Sim| M[Mostra sucesso]
    L -->|Não| N[Mostra erro:<br/>Email já existe]
    M --> O[Volta para LoginActivity]
    N --> B
```

---

## 💻 Fluxos do Desktop Admin

### 1. Listar Todos os Usuários

```mermaid
sequenceDiagram
    participant A as AdminDashboardView
    participant C as AuthController
    participant N as NetworkClient
    participant S as Servidor
    participant P as Protocol
    participant D as UsuarioDAO
    participant DB as MySQL
    
    A->>C: listarUsuarios()
    C->>N: sendCommand("LIST_USERS")
    N->>S: Envia comando
    S->>P: processCommand("LIST_USERS")
    P->>D: listarTodos()
    D->>DB: SELECT * FROM usuario
    DB->>D: Retorna todos os usuários
    D->>P: List<Usuario>
    P->>S: Formata resposta
    S->>N: "OK|user1;user2;user3..."
    N->>C: Retorna lista
    C->>A: List<Usuario>
    A->>A: Exibe na tabela
```

### 2. Editar Usuário

```mermaid
flowchart TD
    A[Admin vê lista<br/>de usuários] --> B[Seleciona um usuário]
    B --> C[Clica em Editar]
    C --> D[EditarUsuarioDialog abre]
    D --> E[Mostra dados atuais<br/>nome e email]
    E --> F[Admin altera dados]
    F --> G[Clica em Salvar]
    G --> H{Campos<br/>válidos?}
    H -->|Não| I[Mostra erro]
    I --> F
    H -->|Sim| J[FinanceController.<br/>atualizarUsuario]
    J --> K[Envia UPDATE_USER<br/>para servidor]
    K --> L[Servidor atualiza<br/>no MySQL]
    L --> M{Atualizado?}
    M -->|Sim| N[Mostra sucesso]
    M -->|Não| O[Mostra erro]
    N --> P[Fecha diálogo]
    O --> F
    P --> Q[Atualiza lista<br/>de usuários]
```

### 3. Alterar Senha de Usuário

```mermaid
flowchart TD
    A[Admin seleciona usuário] --> B[Clica em Alterar Senha]
    B --> C[Diálogo pede nova senha]
    C --> D[Admin digita nova senha]
    D --> E[Clica em Confirmar]
    E --> F{Senha<br/>válida?}
    F -->|Não| G[Mostra erro:<br/>Senha muito curta]
    F -->|Sim| H[AuthController.<br/>atualizarSenhaUsuario]
    G --> D
    H --> I[Gera hash da senha]
    I --> J[Envia UPDATE_USER_PASSWORD<br/>para servidor]
    J --> K[Servidor atualiza<br/>senha_hash no MySQL]
    K --> L{Sucesso?}
    L -->|Sim| M[Mostra mensagem:<br/>Senha alterada]
    L -->|Não| N[Mostra erro]
    M --> O[Fecha diálogo]
    N --> D
```

### 4. Excluir Usuário

```mermaid
flowchart TD
    A[Admin seleciona usuário] --> B[Clica em Excluir]
    B --> C{Confirma<br/>exclusão?}
    C -->|Não| D[Cancela operação]
    C -->|Sim| E[AuthController.deletarUsuario]
    E --> F[Envia DELETE_USER<br/>para servidor]
    F --> G[Servidor verifica<br/>se pode deletar]
    G --> H{Usuário tem<br/>dependências?}
    H -->|Sim| I[Deleta em CASCADE<br/>contas, movimentações]
    H -->|Não| J[Deleta diretamente]
    I --> K[DELETE FROM usuario]
    J --> K
    K --> L{Deletado?}
    L -->|Sim| M[Remove da tabela<br/>da interface]
    L -->|Não| N[Mostra erro]
    M --> O[Mostra mensagem:<br/>Usuário removido]
    N --> P[Mantém na lista]
```

---

## 📱 Fluxos do Mobile

### 1. Visualizar Dashboard / Menu Principal

```mermaid
flowchart TD
    A[MenuActivity inicia] --> B[Busca resumo financeiro]
    B --> C[Query no SQLite local]
    C --> D[Calcula saldo total]
    D --> E[Calcula receitas do mês]
    E --> F[Calcula despesas do mês]
    F --> G[Exibe nos CardViews]
    G --> H{Usuário<br/>clica em?}
    H -->|Contas| I[Abre AccountsActivity]
    H -->|Categorias| J[Abre CategoriaActivity]
    H -->|Movimentações| K[Abre MovementsActivity]
    H -->|Perfil| L[Abre ProfileActivity]
    H -->|Sincronizar| M[Inicia SyncService]
    H -->|Sair| N[AuthManager.logout]
    N --> O[Limpa sessão]
    O --> P[Volta para LoginActivity]
```

### 2. Adicionar Conta Bancária

```mermaid
sequenceDiagram
    participant U as 👤 Usuário
    participant A as AccountsActivity
    participant D as Dialog
    participant L as ContaDao (SQLite)
    participant N as ServerClient
    participant S as Servidor
    participant DB as MySQL
    
    U->>A: Clica no botão "+"
    A->>D: Abre diálogo
    U->>D: Preenche nome e saldo inicial
    U->>D: Clica em Salvar
    D->>D: Valida campos
    
    alt Campos válidos
        D->>L: insert(conta)
        L->>L: Salva no SQLite
        L->>D: ID gerado
        
        alt Está online
            D->>N: sendCommand("ADD_CONTA...")
            N->>S: Envia via Socket
            S->>DB: INSERT INTO conta
            DB->>S: ID da conta no servidor
            S->>N: "OK|id"
            N->>D: Resposta OK
            D->>L: Atualiza ID do servidor
            D->>L: Marca como sincronizado
        end
        
        D->>A: Notifica sucesso
        A->>A: Atualiza lista de contas
        A->>U: Mostra mensagem "Conta adicionada"
    else Campos inválidos
        D->>U: Mostra erro
    end
```

### 3. Adicionar Movimentação (Receita ou Despesa)

```mermaid
flowchart TD
    A[MovementsActivity] --> B[Usuário clica no +]
    B --> C[Diálogo abre]
    C --> D[Usuário preenche:<br/>Valor, Tipo, Categoria, Conta, Data]
    D --> E[Clica em Salvar]
    E --> F{Validação}
    F -->|Valor <= 0| G[Erro: Valor inválido]
    F -->|Sem categoria| H[Erro: Escolha categoria]
    F -->|Sem conta| I[Erro: Escolha conta]
    F -->|OK| J[Cria objeto Lancamento]
    G --> D
    H --> D
    I --> D
    J --> K[LancamentoDao.insert]
    K --> L[Salva no SQLite local]
    L --> M{Está online?}
    M -->|Sim| N[Envia ADD_MOVIMENTACAO<br/>para servidor]
    M -->|Não| O[Marca como<br/>não sincronizado]
    N --> P[Servidor salva<br/>no MySQL]
    P --> Q[Retorna ID]
    Q --> R[Atualiza ID local<br/>Marca como sincronizado]
    O --> S[Será sincronizado depois]
    R --> T[Atualiza RecyclerView]
    S --> T
    T --> U[Mostra mensagem:<br/>Movimentação adicionada]
```

### 4. Filtrar Movimentações

```mermaid
flowchart TD
    A[MovementsActivity] --> B[Usuário abre Filtros]
    B --> C{Qual filtro?}
    C -->|Por Período| D[DatePicker seleciona<br/>data início e fim]
    C -->|Por Conta| E[Spinner seleciona conta]
    C -->|Por Categoria| F[Spinner seleciona categoria]
    C -->|Todos| G[Remove todos os filtros]
    D --> H[LancamentoDao.<br/>buscarPorPeriodo]
    E --> I[LancamentoDao.<br/>buscarPorConta]
    F --> J[LancamentoDao.<br/>buscarPorCategoria]
    G --> K[LancamentoDao.<br/>buscarTodos]
    H --> L[Query com WHERE data BETWEEN]
    I --> M[Query com WHERE conta_id = ?]
    J --> N[Query com WHERE categoria_id = ?]
    K --> O[Query SELECT * FROM lancamento]
    L --> P[Retorna lista filtrada]
    M --> P
    N --> P
    O --> P
    P --> Q[Atualiza RecyclerView<br/>com resultados]
```

### 5. Editar Perfil

```mermaid
flowchart TD
    A[ProfileActivity inicia] --> B[Busca dados do<br/>usuário logado]
    B --> C[UsuarioDao.buscarPorId]
    C --> D[Exibe nome e email<br/>nos campos]
    D --> E{Usuário<br/>faz o que?}
    E -->|Edita nome/email| F[Altera campos]
    E -->|Clica Alterar Senha| G[Abre diálogo de senha]
    E -->|Clica Salvar| H[Valida campos]
    F --> H
    G --> I[Usuário digita:<br/>Senha atual, Nova, Confirmar]
    I --> J{Senhas<br/>válidas?}
    J -->|Não| K[Mostra erro]
    K --> I
    J -->|Sim| L[Envia CHANGE_PASSWORD<br/>para servidor]
    H --> M{Campos<br/>válidos?}
    M -->|Não| N[Mostra erro de validação]
    N --> F
    M -->|Sim| O[Atualiza no SQLite local]
    O --> P{Online?}
    P -->|Sim| Q[Envia UPDATE_PERFIL<br/>para servidor]
    P -->|Não| R[Marca para sincronizar]
    Q --> S[Servidor atualiza MySQL]
    L --> S
    S --> T[Mostra mensagem:<br/>Perfil atualizado]
    R --> T
```

---

## 🖥️ Fluxos do Servidor

### 1. Inicialização do Servidor

```mermaid
flowchart TD
    A[MainServidor.main] --> B[Cria FinanzaServer]
    B --> C[finanzaServer.start]
    C --> D[DatabaseUtil.testConnection]
    D --> E{Conectou<br/>no MySQL?}
    E -->|Não| F[Mostra erro:<br/>MySQL não disponível]
    F --> G[Servidor não inicia]
    E -->|Sim| H[DatabaseUtil.initDatabase]
    H --> I[Cria tabelas se não existirem:<br/>usuario, conta, categoria, lancamento]
    I --> J[ServerSocket na porta 8080]
    J --> K[Mostra mensagem:<br/>Servidor rodando na porta 8080]
    K --> L[Loop infinito:<br/>aguardando conexões]
    L --> M[Cliente conecta]
    M --> N[socket = serverSocket.accept]
    N --> O[Cria nova Thread com ClientHandler]
    O --> P[Thread inicia]
    P --> L
```

### 2. Processamento de Comando

```mermaid
sequenceDiagram
    participant C as Cliente
    participant H as ClientHandler
    participant P as Protocol
    participant D as DAO
    participant DB as MySQL
    
    C->>H: Envia comando via Socket
    H->>H: Lê linha de texto
    H->>P: processCommand(comando)
    P->>P: Split por "|"
    P->>P: Identifica tipo de comando
    
    alt LOGIN
        P->>D: UsuarioDAO.buscarPorEmail()
        D->>DB: SELECT * FROM usuario
        DB->>D: Retorna usuário
        D->>P: Usuario ou null
        P->>P: Valida senha com SecurityUtil
        P->>H: Formata resposta "OK|dados"
    else ADD_MOVIMENTACAO
        P->>P: Extrai parâmetros
        P->>D: MovimentacaoDAO.inserir()
        D->>DB: INSERT INTO lancamento
        DB->>D: ID gerado
        D->>P: true/false
        P->>H: "OK|id" ou "ERROR|msg"
    else LIST_USERS
        P->>D: UsuarioDAO.listarTodos()
        D->>DB: SELECT * FROM usuario
        DB->>D: List<Usuario>
        D->>P: Lista
        P->>P: Formata lista com ";"
        P->>H: "OK|user1;user2;..."
    end
    
    H->>C: Envia resposta
```

### 3. Gerenciamento de Conexões Múltiplas

```mermaid
flowchart TD
    A[FinanzaServer rodando] --> B{Cliente 1<br/>conecta}
    B --> C[Cria Thread 1:<br/>ClientHandler 1]
    C --> D[Thread 1 processa<br/>comandos do Cliente 1]
    D --> E{Cliente 2<br/>conecta}
    E --> F[Cria Thread 2:<br/>ClientHandler 2]
    F --> G[Thread 2 processa<br/>comandos do Cliente 2]
    G --> H{Cliente 3<br/>conecta}
    H --> I[Cria Thread 3:<br/>ClientHandler 3]
    I --> J[Thread 3 processa<br/>comandos do Cliente 3]
    
    K[Thread 1] --> L[Acessa MySQL]
    M[Thread 2] --> L
    N[Thread 3] --> L
    
    L --> O[MySQL gerencia<br/>transações concorrentes]
```

### 4. Validação de Comandos

```mermaid
flowchart TD
    A[Protocol recebe comando] --> B{Comando<br/>requer login?}
    B -->|Sim| C{Cliente está<br/>logado?}
    B -->|Não| D[Processa comando]
    C -->|Não| E[Retorna ERROR:<br/>Não autenticado]
    C -->|Sim| F{Comando requer<br/>privilégio admin?}
    F -->|Sim| G{Usuário<br/>é admin?}
    F -->|Não| D
    G -->|Não| H[Retorna ERROR:<br/>Acesso negado]
    G -->|Sim| D
    D --> I[Valida parâmetros]
    I --> J{Parâmetros<br/>válidos?}
    J -->|Não| K[Retorna ERROR:<br/>Dados inválidos]
    J -->|Sim| L[Executa no DAO]
    L --> M[Acessa MySQL]
    M --> N{Sucesso?}
    N -->|Sim| O[Retorna OK com dados]
    N -->|Não| P[Retorna ERROR com mensagem]
```

---

## 🔄 Sincronização

### 1. Fluxo de Sincronização Automática

```mermaid
flowchart TD
    A[EnhancedSyncService inicia] --> B[Aguarda 5 minutos]
    B --> C{Tem conexão<br/>com internet?}
    C -->|Não| D[Registra para tentar<br/>quando tiver conexão]
    C -->|Sim| E[Busca dados não sincronizados<br/>no SQLite]
    D --> B
    E --> F{Tem dados<br/>para enviar?}
    F -->|Não| G[Pula para receber]
    F -->|Sim| H[Para cada dado não sincronizado]
    H --> I[Envia comando<br/>para servidor]
    I --> J{Servidor<br/>aceitou?}
    J -->|Sim| K[Marca como sincronizado<br/>no SQLite]
    J -->|Não| L[Registra erro<br/>Tenta na próxima]
    K --> M{Tem mais<br/>dados?}
    L --> M
    M -->|Sim| H
    M -->|Não| G
    G --> N[Busca timestamp da<br/>última sincronização]
    N --> O[Envia GET_UPDATES<br/>desde timestamp]
    O --> P[Servidor retorna<br/>dados novos/alterados]
    P --> Q[Para cada atualização]
    Q --> R{Dado existe<br/>localmente?}
    R -->|Sim| S{Conflito?}
    R -->|Não| T[INSERT no SQLite]
    S -->|Sim| U[ConflictResolutionManager]
    S -->|Não| V[UPDATE no SQLite]
    U --> W{Qual versão<br/>vence?}
    W -->|Servidor| V
    W -->|Local| X[Mantém local]
    T --> Y{Tem mais<br/>atualizações?}
    V --> Y
    X --> Y
    Y -->|Sim| Q
    Y -->|Não| Z[Atualiza timestamp<br/>da última sync]
    Z --> AA[Notifica usuário:<br/>Dados sincronizados]
    AA --> B
```

### 2. Sincronização Manual

```mermaid
flowchart TD
    A[Usuário clica em<br/>Sincronizar Agora] --> B[SettingsActivity]
    B --> C[Chama EnhancedSyncService.<br/>syncNow]
    C --> D[Mostra ProgressDialog:<br/>Sincronizando...]
    D --> E{Tem<br/>internet?}
    E -->|Não| F[Fecha ProgressDialog]
    F --> G[Toast: Sem conexão]
    E -->|Sim| H[Executa sync imediatamente<br/>mesmo fluxo da automática]
    H --> I{Sync<br/>completa?}
    I -->|Sim| J[Fecha ProgressDialog]
    I -->|Erro| K[Fecha ProgressDialog]
    J --> L[Toast: Dados sincronizados com sucesso]
    K --> M[Toast: Erro ao sincronizar]
    L --> N[Atualiza telas abertas]
    M --> N
```

### 3. Resolução de Conflitos

```mermaid
flowchart TD
    A[ConflictResolutionManager<br/>detecta conflito] --> B{Tipo de<br/>conflito?}
    B -->|Mesmo dado alterado<br/>local e servidor| C[Compara timestamps]
    B -->|Dado deletado local<br/>mas alterado servidor| D[Estratégia: DELETE_WINS]
    B -->|Dado deletado servidor<br/>mas alterado local| E[Estratégia: SERVER_WINS]
    
    C --> F{Qual é<br/>mais recente?}
    F -->|Local| G[Mantém versão local]
    F -->|Servidor| H[Aceita versão servidor]
    F -->|Igual| I[Usa estratégia padrão:<br/>SERVER_WINS]
    
    D --> J[Confirma delete local]
    J --> K[Envia DELETE para servidor]
    
    E --> L[Aceita delete do servidor]
    L --> M[Remove do SQLite local]
    
    G --> N[Envia UPDATE para servidor<br/>forçar versão local]
    H --> O[Atualiza SQLite local<br/>com versão servidor]
    I --> O
    
    N --> P[Marca como sincronizado]
    O --> P
    K --> P
    M --> P
    P --> Q[Log do conflito resolvido]
```

### 4. Modo Offline

```mermaid
flowchart TD
    A[App detecta<br/>sem internet] --> B[Ativa modo offline]
    B --> C[Toast: Modo offline ativado]
    C --> D[Usuário usa app normalmente]
    D --> E{Usuário<br/>faz ação?}
    E -->|Adicionar dado| F[Salva no SQLite]
    E -->|Editar dado| G[Atualiza no SQLite]
    E -->|Deletar dado| H[Marca como deletado no SQLite]
    F --> I[Marca como não sincronizado]
    G --> I
    H --> I
    I --> J[Badge/indicador:<br/>X itens não sincronizados]
    J --> D
    
    K[Internet volta] --> L[App detecta conexão]
    L --> M[Toast: Online - Sincronizando...]
    M --> N[EnhancedSyncService<br/>inicia sync automática]
    N --> O[Envia todos os dados<br/>não sincronizados]
    O --> P[Recebe atualizações<br/>do servidor]
    P --> Q[Atualiza SQLite]
    Q --> R[Remove badge]
    R --> S[Toast: Sincronização completa]
```

---

## 📊 Diagramas de Dados

### 1. Fluxo de Dados - Adicionar Movimentação

```mermaid
graph LR
    A[📱 UI:<br/>MovementsActivity] --> B[📝 Dados:<br/>valor, categoria, conta]
    B --> C[💾 SQLite:<br/>INSERT lancamento]
    C --> D{🌐 Online?}
    D -->|Sim| E[📡 Network:<br/>ADD_MOVIMENTACAO]
    D -->|Não| F[🔖 Flag:<br/>não_sincronizado]
    E --> G[🖥️ Servidor:<br/>ClientHandler]
    G --> H[⚙️ Protocol:<br/>processa comando]
    H --> I[🗄️ DAO:<br/>MovimentacaoDAO]
    I --> J[💿 MySQL:<br/>INSERT lancamento]
    J --> K[✅ ID gerado]
    K --> L[📡 Resposta:<br/>OK com ID]
    L --> M[💾 SQLite:<br/>atualiza ID]
    M --> N[🔖 Flag:<br/>sincronizado]
    F --> O[⏳ Aguarda<br/>próxima sync]
    N --> P[📱 UI:<br/>atualiza lista]
    O --> P
```

### 2. Modelo de Dados (Entidades)

```mermaid
erDiagram
    USUARIO ||--o{ CONTA : possui
    USUARIO ||--o{ LANCAMENTO : registra
    CONTA ||--o{ LANCAMENTO : tem
    CATEGORIA ||--o{ LANCAMENTO : classifica
    
    USUARIO {
        int id PK
        string nome
        string email UK
        string senha_hash
        string tipo_usuario
        datetime data_criacao
    }
    
    CONTA {
        int id PK
        string nome
        decimal saldo
        int usuario_id FK
        datetime criada_em
    }
    
    CATEGORIA {
        int id PK
        string nome
        string cor_hex
        string tipo
    }
    
    LANCAMENTO {
        int id PK
        decimal valor
        date data
        string descricao
        string tipo
        int conta_id FK
        int categoria_id FK
        int usuario_id FK
        datetime criado_em
        boolean sincronizado
    }
```

---

## 🎬 Casos de Uso Completos

### 1. Usuário Registra e Usa o App pela Primeira Vez

```mermaid
flowchart TD
    A[📱 Baixa o app] --> B[🚀 Abre pela primeira vez]
    B --> C[👁️ Ve LoginActivity]
    C --> D[👆 Clica em Criar conta]
    D --> E[📝 RegisterActivity abre]
    E --> F[✍️ Preenche:<br/>João, joao@email.com, senha123]
    F --> G[✔️ Clica em Registrar]
    G --> H[📡 Envia para servidor]
    H --> I[🖥️ Servidor valida]
    I --> J{✅ Email<br/>disponível?}
    J -->|Não| K[❌ Erro: Email já existe]
    K --> F
    J -->|Sim| L[💾 Cria no MySQL]
    L --> M[🔐 Hash da senha SHA-256]
    M --> N[✅ Usuário criado]
    N --> O[📲 App recebe OK]
    O --> P[👁️ Volta para LoginActivity]
    P --> Q[✍️ Faz login]
    Q --> R[🏠 MenuActivity abre]
    R --> S[👁️ Ve dashboard vazio]
    S --> T[👆 Clica em Contas]
    T --> U[➕ Adiciona Conta Corrente]
    U --> V[👆 Clica em Categorias]
    V --> W[➕ Adiciona Alimentação, Transporte]
    W --> X[👆 Clica em Movimentações]
    X --> Y[➕ Adiciona primeira despesa]
    Y --> Z[🎉 Começa a usar o sistema!]
```

### 2. Admin Gerencia Usuários no Desktop

```mermaid
flowchart TD
    A[💻 Admin abre Desktop] --> B[👁️ LoginView aparece]
    B --> C[✍️ Digita credenciais admin]
    C --> D[🔐 Faz login]
    D --> E[✅ Validado como admin]
    E --> F[🏠 AdminDashboardView abre]
    F --> G[👁️ Ve tabela com todos os usuários]
    G --> H{🤔 O que fazer?}
    H -->|Ver detalhes| I[👆 Clica em usuário]
    H -->|Editar| J[✏️ Abre EditarUsuarioDialog]
    H -->|Mudar senha| K[🔑 Abre dialog de senha]
    H -->|Excluir| L[🗑️ Confirma exclusão]
    I --> M[ℹ️ Mostra dados completos]
    J --> N[✏️ Altera nome/email]
    N --> O[💾 Salva no servidor]
    K --> P[🔑 Digita nova senha]
    P --> Q[💾 Atualiza no banco]
    L --> R[❌ Remove do sistema]
    R --> S[🧹 Deleta CASCADE<br/>contas e movimentações]
    O --> T[🔄 Atualiza tabela]
    Q --> T
    S --> T
    M --> H
    T --> H
```

---

## 🔍 Explicações Adicionais

### Símbolos Usados

- 📱 Mobile App
- 💻 Desktop
- 🖥️ Servidor
- 🗄️ Banco de Dados
- 👤 Usuário
- 📡 Rede/Internet
- 💾 Salvar
- ✅ Sucesso
- ❌ Erro
- 🔐 Segurança/Login
- ⚙️ Processamento
- 🔄 Atualização

### Como Ler os Diagramas

1. **Flowchart** (→): Fluxo de execução do código
2. **Sequence** (↓): Ordem de chamadas entre componentes
3. **ER Diagram**: Relacionamento entre tabelas
4. **Graph**: Arquitetura geral

### Cores nos Diagramas

- **Verde**: Sucesso, OK
- **Vermelho**: Erro, falha
- **Azul**: Processamento normal
- **Amarelo**: Decisão, verificação

---

## 🎓 Resumo Final

### Padrões Identificados

1. **Cliente → Servidor → Banco**: Sempre esse fluxo
2. **Validação em 3 níveis**: UI → Controller → Servidor
3. **Offline-first no Mobile**: SQLite primeiro, servidor depois
4. **Comandos por String**: Protocolo simples com separador "|"
5. **Thread por Cliente**: Servidor suporta múltiplos clientes

### Principais Fluxos

1. **Autenticação**: Login → Validação → Sessão
2. **CRUD**: Criar → Salvar Local → Enviar Servidor → Sincronizar
3. **Sincronização**: Offline → Fila → Online → Enviar → Receber → Atualizar

---

**🎯 Use este documento para entender visualmente como o sistema funciona!**

**💡 Dica**: No GitHub, esses diagramas Mermaid são renderizados automaticamente. Visualize lá para melhor experiência!
