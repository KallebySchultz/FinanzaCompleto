# 🏗️ Arquitetura do Sistema Finanza

## 📋 Índice

1. [Visão Geral](#-visão-geral)
2. [Arquitetura Geral](#-arquitetura-geral)
3. [Componentes Desktop](#-componentes-desktop)
4. [Componentes Mobile](#-componentes-mobile)
5. [Comunicação Entre Componentes](#-comunicação-entre-componentes)
6. [Fluxo de Dados](#-fluxo-de-dados)
7. [Padrões Arquiteturais](#-padrões-arquiteturais)
8. [Tecnologias Utilizadas](#-tecnologias-utilizadas)

## 🌟 Visão Geral

O **Finanza** é um sistema de controle financeiro distribuído que utiliza arquitetura **cliente-servidor** com sincronização bidirecional. O sistema permite que múltiplos clientes (mobile e desktop) compartilhem dados financeiros através de um servidor centralizado.

### Características Principais
- 📱 **Cliente Mobile (Android)**: Interface portable com suporte offline
- 🖥️ **Cliente Desktop (Java Swing)**: Interface rica para administração
- 🌐 **Servidor TCP**: Centraliza dados e coordena sincronização
- 💾 **Banco MySQL**: Armazenamento persistente centralizado
- 📱 **SQLite Local**: Cache local no mobile para modo offline

## 🏛️ Arquitetura Geral

```
┌─────────────────────────────────────────────────────────────────┐
│                        SISTEMA FINANZA                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────┐ │
│  │   MOBILE APP    │    │  DESKTOP CLIENT │    │   SERVIDOR  │ │
│  │   (Android)     │    │  (Java Swing)   │    │  (Java TCP) │ │
│  │                 │    │                 │    │             │ │
│  │ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────┐ │ │
│  │ │     UI      │ │    │ │    View     │ │    │ │ Handler │ │ │
│  │ │ Activities  │ │    │ │   (Swing)   │ │    │ │ Client  │ │ │
│  │ └─────────────┘ │    │ └─────────────┘ │    │ └─────────┘ │ │
│  │ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────┐ │ │
│  │ │   Network   │ │    │ │ Controllers │ │    │ │Protocol │ │ │
│  │ │   Manager   │ │    │ │    (MVC)    │ │    │ │Processor│ │ │
│  │ └─────────────┘ │    │ └─────────────┘ │    │ └─────────┘ │ │
│  │ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────┐ │ │
│  │ │Room Database│ │    │ │   Models    │ │    │ │   DAO   │ │ │
│  │ │  (SQLite)   │ │    │ │ (Entities)  │ │    │ │ Classes │ │ │
│  │ └─────────────┘ │    │ └─────────────┘ │    │ └─────────┘ │ │
│  └─────────────────┘    └─────────────────┘    └─────────────┘ │
│           │                       │                      │     │
│           └───────────┐           │           ┌──────────┘     │
│                       │           │           │                │
│                    ┌──▼───────────▼───────────▼──┐             │
│                    │        MYSQL DATABASE       │             │
│                    │     (Armazenamento         │             │
│                    │      Centralizado)         │             │
│                    └────────────────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

## 🖥️ Componentes Desktop

### 🎯 Cliente Desktop (ClienteFinanza)

**Localização**: `/DESKTOP VERSION/ClienteFinanza/`

#### Estrutura MVC:

```
ClienteFinanza/src/
├── view/                 # 🖼️ Interface Gráfica (Swing)
│   ├── LoginView.java          → Tela de autenticação
│   ├── DashboardView.java      → Dashboard principal
│   ├── PerfilView.java         → Perfil do usuário
│   ├── MovimentacoesView.java  → Lista de transações
│   ├── ContasView.java         → Gerenciar contas
│   ├── CategoriasView.java     → Gerenciar categorias
│   ├── RelatoriosView.java     → Relatórios e gráficos
│   └── ExportacaoView.java     → Exportação de dados
├── controller/           # 🎮 Controladores (Lógica de Negócio)
│   ├── AuthController.java     → Autenticação
│   └── FinanceController.java  → Operações financeiras
├── model/               # 📊 Modelos de Dados
│   ├── Usuario.java           → Entidade usuário
│   ├── Conta.java             → Entidade conta
│   ├── Categoria.java         → Entidade categoria
│   └── Movimentacao.java      → Entidade movimentação
├── util/                # 🛠️ Utilitários
│   └── NetworkClient.java     → Cliente de rede TCP
└── MainCliente.java     # 🚀 Classe principal
```

### 🌐 Servidor (ServidorFinanza)

**Localização**: `/DESKTOP VERSION/ServidorFinanza/`

#### Estrutura:

```
ServidorFinanza/src/
├── server/              # 🌐 Servidor TCP
│   ├── FinanzaServer.java     → Servidor principal
│   ├── ClientHandler.java     → Manipulador de clientes
│   └── Protocol.java          → Protocolo de comunicação
├── dao/                 # 💾 Acesso a Dados
│   ├── UsuarioDAO.java        → CRUD usuários
│   ├── ContaDAO.java          → CRUD contas
│   ├── CategoriaDAO.java      → CRUD categorias
│   └── MovimentacaoDAO.java   → CRUD movimentações
├── model/               # 📊 Modelos de Dados (igual ao cliente)
├── util/                # 🛠️ Utilitários
│   ├── DatabaseUtil.java     → Conexão com MySQL
│   └── SecurityUtil.java     → Criptografia e segurança
└── MainServidor.java    # 🚀 Classe principal
```

## 📱 Componentes Mobile

### 🎯 Aplicativo Android

**Localização**: `/app/src/main/java/com/example/finanza/`

#### Estrutura MVVM:

```
app/src/main/java/com/example/finanza/
├── ui/                  # 🖼️ Interface do Usuário
│   ├── LoginActivity.java      → Tela de login
│   ├── RegisterActivity.java   → Cadastro de usuário
│   ├── MainActivity.java       → Dashboard principal
│   ├── MenuActivity.java       → Menu de navegação
│   ├── MovementsActivity.java  → Lista de movimentações
│   ├── AccountsActivity.java   → Gerenciar contas
│   ├── CategoriaActivity.java  → Gerenciar categorias
│   ├── ProfileActivity.java    → Perfil do usuário
│   ├── ReportsActivity.java    → Relatórios
│   └── SettingsActivity.java   → Configurações
├── network/             # 🌐 Comunicação de Rede
│   ├── ServerClient.java       → Cliente TCP
│   ├── AuthManager.java        → Gerenciador de autenticação
│   ├── SyncService.java        → Serviço de sincronização
│   ├── EnhancedSyncService.java → Sincronização avançada
│   ├── ConflictResolutionManager.java → Resolução de conflitos
│   └── Protocol.java           → Protocolo de comunicação
├── db/                  # 💾 Banco Local (Room)
│   ├── AppDatabase.java        → Configuração do Room
│   ├── UsuarioDao.java         → DAO local usuário
│   ├── ContaDao.java           → DAO local conta
│   ├── CategoriaDao.java       → DAO local categoria
│   └── MovimentacaoDao.java    → DAO local movimentação
├── model/               # 📊 Modelos de Dados
│   └── [entidades compartilhadas]
└── util/                # 🛠️ Utilitários
    └── [classes auxiliares]
```

## 🔄 Comunicação Entre Componentes

### 📡 Protocolo de Comunicação

O sistema utiliza **TCP sockets** com protocolo customizado baseado em **pipes (|)** como separadores.

#### Formato das Mensagens:

```
COMANDO|PARAM1|PARAM2|PARAM3
```

#### Exemplos de Comandos:

```bash
# Login
LOGIN|email@exemplo.com|senha123

# Resposta
OK|id_usuario|nome|email

# Alterar Senha
CHANGE_PASSWORD|senha_atual|nova_senha

# Resposta
OK|Senha alterada com sucesso
```

### 🌐 Fluxo de Comunicação

```
┌─────────────┐    TCP/IP     ┌─────────────┐    MySQL     ┌─────────────┐
│   CLIENTE   │ ────────────→ │  SERVIDOR   │ ───────────→ │    BANCO    │
│             │               │             │              │   DE DADOS  │
│ Mobile/     │ ←──────────── │ Handler +   │ ←─────────── │             │
│ Desktop     │   Response    │ Protocol    │   ResultSet  │   MySQL     │
└─────────────┘               └─────────────┘              └─────────────┘
```

### 🔄 Sincronização

1. **Cliente → Servidor**: Envia dados modificados
2. **Servidor → MySQL**: Persiste no banco central
3. **Servidor → Clientes**: Propaga mudanças para outros clientes
4. **Cliente → Local DB**: Atualiza cache local (mobile)

## 📊 Fluxo de Dados

### 🔄 Ciclo Completo de uma Operação

Exemplo: **Adicionar Nova Movimentação**

```
┌─────────────────────────────────────────────────────────────────────┐
│                     FLUXO: ADICIONAR MOVIMENTAÇÃO                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. 👤 USUÁRIO                                                     │
│     ↓ Preenche formulário e clica "Salvar"                        │
│                                                                     │
│  2. 🖼️ VIEW (Activity/JFrame)                                      │
│     ↓ Coleta dados do formulário                                   │
│     ↓ Valida campos obrigatórios                                   │
│                                                                     │
│  3. 🎮 CONTROLLER                                                   │
│     ↓ Recebe dados da View                                         │
│     ↓ Valida regras de negócio                                     │
│     ↓ Cria objeto Movimentacao                                     │
│                                                                     │
│  4. 🌐 NETWORK CLIENT                                              │
│     ↓ Serializa dados para protocolo                              │
│     ↓ Envia comando TCP: ADD_MOVEMENT|dados                        │
│                                                                     │
│  5. 🌐 SERVIDOR                                                     │
│     ↓ ClientHandler recebe comando                                 │
│     ↓ Protocol processa e valida                                   │
│                                                                     │
│  6. 💾 DAO                                                          │
│     ↓ MovimentacaoDAO.inserir()                                    │
│     ↓ Executa SQL INSERT no MySQL                                  │
│                                                                     │
│  7. 🔄 RESPOSTA                                                     │
│     ↓ MySQL → DAO → Servidor → Cliente                            │
│     ↓ Cliente atualiza interface                                   │
│     ↓ Mobile salva no SQLite local                                │
│                                                                     │
│  8. 📡 SINCRONIZAÇÃO                                               │
│     ↓ Servidor notifica outros clientes                           │
│     ↓ Clientes conectados recebem atualização                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

## 🎨 Padrões Arquiteturais

### 🏗️ Desktop: MVC (Model-View-Controller)

```
┌─────────────┐    user input    ┌─────────────┐    manipulates    ┌─────────────┐
│    VIEW     │ ───────────────→ │ CONTROLLER  │ ─────────────────→ │    MODEL    │
│   (Swing)   │                  │  (Logic)    │                   │ (Entities)  │
│             │ ←─────────────── │             │ ←───────────────── │             │
└─────────────┘    updates       └─────────────┘     notifies      └─────────────┘
```

**Vantagens**:
- Separação clara de responsabilidades
- Facilita manutenção e testes
- Reutilização de componentes

### 📱 Mobile: MVVM (Model-View-ViewModel)

```
┌─────────────┐    data binding   ┌─────────────┐    observes    ┌─────────────┐
│    VIEW     │ ←───────────────→ │ VIEWMODEL   │ ─────────────→ │    MODEL    │
│ (Activity)  │                   │ (LiveData)  │               │ (Room/Net)  │
└─────────────┘                   └─────────────┘               └─────────────┘
```

**Vantagens**:
- Data binding automático
- Ciclo de vida gerenciado
- Observadores reativos

### 🌐 Servidor: DAO Pattern

```
┌─────────────┐    uses    ┌─────────────┐    queries    ┌─────────────┐
│   SERVER    │ ─────────→ │     DAO     │ ────────────→ │  DATABASE   │
│ (Business)  │            │ (Data Access│               │   (MySQL)   │
└─────────────┘            │  Objects)   │               └─────────────┘
                           └─────────────┘
```

**Vantagens**:
- Abstração do banco de dados
- Facilita mudanças de BD
- Operações CRUD organizadas

## 🛠️ Tecnologias Utilizadas

### 📋 Stack Completo

| Componente | Tecnologia | Versão | Responsabilidade |
|------------|------------|--------|------------------|
| **Mobile Frontend** | Android SDK | API 24+ | Interface mobile |
| **Mobile Database** | Room (SQLite) | 2.4+ | Cache local |
| **Desktop Frontend** | Java Swing | Java 8+ | Interface desktop |
| **Backend Server** | Java SE | Java 8+ | Servidor de aplicação |
| **Database** | MySQL | 8.0+ | Persistência central |
| **Communication** | TCP Sockets | - | Comunicação cliente-servidor |
| **IDE Mobile** | Android Studio | 4.0+ | Desenvolvimento mobile |
| **IDE Desktop** | NetBeans | 12+ | Desenvolvimento desktop |

### 🔧 Bibliotecas e Frameworks

#### Mobile (Android)
```xml
<!-- Room Database -->
implementation "androidx.room:room-runtime:2.4.3"
implementation "androidx.room:room-rxjava2:2.4.3"

<!-- Network -->
implementation 'com.squareup.okhttp3:okhttp:4.9.3'

<!-- UI Components -->
implementation 'com.google.android.material:material:1.6.1'
```

#### Desktop (Java)
```xml
<!-- MySQL Connector -->
mysql:mysql-connector-java:8.0.33

<!-- Swing Look and Feel -->
com.formdev:flatlaf:2.6
```

### 🗄️ Esquema de Banco de Dados

#### Estrutura Principal:

```sql
-- Usuários do sistema
CREATE TABLE usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Contas financeiras
CREATE TABLE conta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('CORRENTE', 'POUPANCA', 'CARTAO', 'INVESTIMENTO'),
    saldo_inicial DECIMAL(10,2) DEFAULT 0,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

-- Categorias de movimentação
CREATE TABLE categoria (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('RECEITA', 'DESPESA'),
    cor VARCHAR(7) DEFAULT '#CCCCCC',
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

-- Movimentações financeiras
CREATE TABLE movimentacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    descricao VARCHAR(200) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data DATE NOT NULL,
    tipo ENUM('RECEITA', 'DESPESA'),
    id_conta INT,
    id_categoria INT,
    id_usuario INT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conta) REFERENCES conta(id),
    FOREIGN KEY (id_categoria) REFERENCES categoria(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);
```

## 🔄 Principais Desafios Arquiteturais

### 1. 🔄 Sincronização de Dados
- **Problema**: Manter dados consistentes entre múltiplos clientes
- **Solução**: Timestamp-based conflict resolution + Event sourcing

### 2. 📱 Modo Offline
- **Problema**: Funcionar sem conexão no mobile
- **Solução**: Cache local (SQLite) + Sync queue

### 3. 🔐 Segurança
- **Problema**: Proteger dados financeiros sensíveis
- **Solução**: Hash SHA-256 + Validação dupla + TCP seguro

### 4. ⚡ Performance
- **Problema**: Sincronização eficiente de grandes volumes
- **Solução**: Sync incremental + Compressão + Pool de conexões

---

Esta arquitetura garante **escalabilidade**, **confiabilidade** e **usabilidade** para o sistema Finanza, permitindo evolução gradual e manutenção simplificada.