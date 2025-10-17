# 🗺️ MAPEAMENTO COMPLETO DO SISTEMA FINANZA

## 📱 VERSÃO MOBILE (Android)

### Estrutura de Diretórios

```
app/src/main/java/com/example/finanza/
├── MainActivity.java                    # Atividade principal do app
├── db/                                  # Camada de persistência local
│   ├── AppDatabase.java                 # Configuração do banco Room
│   ├── CategoriaDao.java                # DAO para operações de Categoria
│   ├── ContaDao.java                    # DAO para operações de Conta
│   ├── LancamentoDao.java              # DAO para operações de Lançamento
│   └── UsuarioDao.java                  # DAO para operações de Usuário
├── model/                               # Modelos de dados
│   ├── Categoria.java                   # Entidade Categoria
│   ├── Conta.java                       # Entidade Conta
│   ├── Lancamento.java                  # Entidade Lançamento (Transação)
│   └── Usuario.java                     # Entidade Usuário
├── network/                             # Camada de rede e sincronização
│   ├── AuthManager.java                 # Gerenciamento de autenticação
│   ├── ConflictResolutionManager.java  # Resolução de conflitos de sync
│   ├── EnhancedSyncService.java        # Serviço avançado de sincronização
│   ├── Protocol.java                    # Protocolo de comunicação com servidor
│   ├── ServerClient.java                # Cliente de comunicação via socket
│   └── SyncService.java                 # Serviço base de sincronização
├── ui/                                  # Camada de interface do usuário
│   ├── AccountsActivity.java            # Tela de gerenciamento de contas
│   ├── CategoriaActivity.java           # Tela de gerenciamento de categorias
│   ├── LoginActivity.java               # Tela de login
│   ├── MenuActivity.java                # Tela de menu/dashboard principal
│   ├── MovementsActivity.java           # Tela de movimentações/transações
│   ├── ProfileActivity.java             # Tela de perfil do usuário
│   ├── RegisterActivity.java            # Tela de registro de novo usuário
│   └── SettingsActivity.java            # Tela de configurações
└── util/                                # Utilitários
    └── DataIntegrityValidator.java      # Validador de integridade de dados
```

### Fluxo de Funcionalidades - Mobile

#### 1. LoginActivity.java
**Função:** Tela de autenticação do usuário
- Recebe email e senha do usuário
- Usa `AuthManager.java` para validar credenciais via servidor
- Comunica com servidor através de `ServerClient.java` usando `Protocol.java`
- Em caso de sucesso, armazena dados do usuário em `UsuarioDao.java`
- Redireciona para `MenuActivity.java`

#### 2. RegisterActivity.java
**Função:** Cadastro de novos usuários
- Coleta dados do novo usuário (nome, email, senha)
- Envia dados ao servidor via `ServerClient.java`
- Usa criptografia para senha (implementada em `Protocol.java`)
- Após registro bem-sucedido, redireciona para `LoginActivity.java`

#### 3. MenuActivity.java (Dashboard)
**Função:** Tela principal com visão geral financeira
- Exibe saldo total de todas as contas
- Mostra resumo de receitas e despesas
- Busca dados de `ContaDao.java` e `LancamentoDao.java`
- Inicia `SyncService.java` para sincronizar dados com servidor
- Navega para outras activities (Accounts, Movements, Categories, Profile, Settings)

#### 4. AccountsActivity.java
**Função:** Gerenciamento de contas bancárias
- Lista todas as contas do usuário obtidas de `ContaDao.java`
- Permite criar novas contas (corrente, poupança, cartão, investimento)
- Editar e excluir contas existentes
- Sincroniza alterações com servidor via `EnhancedSyncService.java`
- Atualiza saldos baseado em lançamentos de `LancamentoDao.java`

#### 5. CategoriaActivity.java
**Função:** Gerenciamento de categorias de transações
- Lista categorias obtidas de `CategoriaDao.java`
- Permite criar, editar e excluir categorias
- Categorias podem ser de receita ou despesa
- Sincroniza com servidor usando `SyncService.java`

#### 6. MovementsActivity.java
**Função:** Gerenciamento de lançamentos/transações
- Lista todos os lançamentos de `LancamentoDao.java`
- Permite criar novos lançamentos (receita ou despesa)
- Editar e excluir lançamentos
- Associa lançamento a uma conta (`Conta.java`) e categoria (`Categoria.java`)
- Sincroniza alterações com servidor via `EnhancedSyncService.java`
- Valida integridade usando `DataIntegrityValidator.java`

#### 7. ProfileActivity.java
**Função:** Visualização e edição de perfil
- Exibe dados do usuário de `UsuarioDao.java`
- Permite editar nome e email
- Permite alterar senha (criptografada)
- Sincroniza alterações com servidor

#### 8. SettingsActivity.java
**Função:** Configurações do aplicativo
- Configurações de sincronização
- Preferências de notificação
- Opções de logout

### Camada de Dados - Mobile

#### AppDatabase.java
**Função:** Configuração central do banco Room (SQLite)
- Define versão do banco
- Lista todas as entidades (Usuario, Conta, Categoria, Lancamento)
- Fornece acesso aos DAOs
- Implementa padrão Singleton

#### DAOs (Data Access Objects)
- **UsuarioDao.java:** CRUD de usuários no banco local
- **ContaDao.java:** CRUD de contas, consultas de saldo
- **CategoriaDao.java:** CRUD de categorias
- **LancamentoDao.java:** CRUD de lançamentos, consultas por período/categoria

### Camada de Rede - Mobile

#### ServerClient.java
**Função:** Cliente de comunicação via sockets TCP/IP
- Conecta ao servidor na porta especificada
- Envia comandos usando `Protocol.java`
- Recebe respostas do servidor
- Gerencia timeout e reconexão

#### Protocol.java
**Função:** Define o protocolo de comunicação
- Comandos: LOGIN, REGISTER, SYNC_CONTAS, SYNC_CATEGORIAS, SYNC_LANCAMENTOS
- Formato de mensagens entre cliente e servidor
- Serialização/deserialização de dados

#### AuthManager.java
**Função:** Gerencia autenticação
- Login e logout
- Renovação de tokens
- Armazenamento seguro de credenciais

#### SyncService.java
**Função:** Serviço base de sincronização
- Sincroniza dados locais com servidor
- Detecta conflitos
- Processa fila de operações offline

#### EnhancedSyncService.java
**Função:** Serviço avançado de sincronização
- Sincronização incremental
- Priorização de operações
- Resolução de conflitos complexos

#### ConflictResolutionManager.java
**Função:** Resolução de conflitos de dados
- Estratégias: última modificação vence, prioridade servidor, merge
- Notifica usuário em conflitos críticos

### Utilitários - Mobile

#### DataIntegrityValidator.java
**Função:** Validação de integridade de dados
- Valida consistência de saldos
- Verifica integridade referencial
- Detecta anomalias nos dados

---

## 💻 VERSÃO DESKTOP

### Cliente Desktop

```
DESKTOP VERSION/ClienteFinanza/src/
├── MainCliente.java                     # Classe principal do cliente
├── controller/                          # Controladores MVC
│   ├── AuthController.java              # Controle de autenticação
│   └── FinanceController.java           # Controle de operações financeiras
├── view/                                # Interface gráfica Swing
│   ├── AdminDashboardView.java          # Dashboard do administrador
│   ├── EditarUsuarioDialog.java         # Dialog de edição de usuário
│   └── LoginView.java                   # Tela de login
├── model/                               # Modelos de dados
│   ├── Categoria.java                   # Modelo Categoria
│   ├── Conta.java                       # Modelo Conta
│   ├── Movimentacao.java                # Modelo Movimentação
│   └── Usuario.java                     # Modelo Usuário
└── util/                                # Utilitários
    └── NetworkClient.java               # Cliente de rede
```

### Servidor Desktop

```
DESKTOP VERSION/ServidorFinanza/src/
├── MainServidor.java                    # Classe principal do servidor
├── server/                              # Lógica do servidor
│   ├── ClientHandler.java               # Handler para cada cliente conectado
│   ├── FinanzaServer.java               # Servidor principal TCP/IP
│   └── Protocol.java                    # Protocolo de comunicação
├── dao/                                 # Acesso a dados (MySQL)
│   ├── CategoriaDAO.java                # DAO Categoria
│   ├── ContaDAO.java                    # DAO Conta
│   ├── MovimentacaoDAO.java             # DAO Movimentação
│   └── UsuarioDAO.java                  # DAO Usuário
├── model/                               # Modelos de dados
│   ├── Categoria.java                   # Modelo Categoria
│   ├── Conta.java                       # Modelo Conta
│   ├── Movimentacao.java                # Modelo Movimentação
│   └── Usuario.java                     # Modelo Usuário
└── util/                                # Utilitários
    ├── DatabaseUtil.java                # Utilitário de banco de dados
    └── SecurityUtil.java                # Utilitário de segurança (criptografia)
```

### Fluxo de Funcionalidades - Desktop Cliente

#### 1. MainCliente.java
**Função:** Ponto de entrada do cliente desktop
- Inicializa a aplicação
- Carrega configurações
- Exibe `LoginView.java`

#### 2. LoginView.java
**Função:** Interface de login para administradores
- Coleta credenciais do administrador
- Usa `AuthController.java` para autenticação
- Conecta ao servidor via `NetworkClient.java`
- Exibe `AdminDashboardView.java` após login

#### 3. AdminDashboardView.java
**Função:** Dashboard administrativo
- Lista todos os usuários do sistema
- Permite visualizar detalhes de cada usuário
- Acessa `EditarUsuarioDialog.java` para editar usuários
- Usa `FinanceController.java` para operações
- Busca dados do servidor via `NetworkClient.java`

#### 4. EditarUsuarioDialog.java
**Função:** Dialog para edição de usuários
- Permite editar nome, email
- Permite alterar senha
- Salva alterações no servidor via `FinanceController.java`

#### 5. AuthController.java
**Função:** Controla autenticação
- Valida credenciais de administrador
- Gerencia sessão
- Comunica com servidor usando `NetworkClient.java`

#### 6. FinanceController.java
**Função:** Controla operações financeiras
- Busca lista de usuários
- Atualiza dados de usuários
- Gerencia operações CRUD via servidor

#### 7. NetworkClient.java
**Função:** Cliente de comunicação com servidor
- Estabelece conexão TCP/IP
- Envia comandos usando protocolo definido
- Recebe e processa respostas

### Fluxo de Funcionalidades - Servidor

#### 1. MainServidor.java
**Função:** Ponto de entrada do servidor
- Inicializa `FinanzaServer.java`
- Configura porta de escuta (geralmente 12345)
- Inicializa conexão com banco MySQL via `DatabaseUtil.java`

#### 2. FinanzaServer.java
**Função:** Servidor TCP/IP principal
- Escuta conexões na porta configurada
- Aceita conexões de clientes (mobile e desktop)
- Cria uma thread `ClientHandler.java` para cada cliente

#### 3. ClientHandler.java
**Função:** Processa requisições de um cliente
- Recebe comandos do cliente
- Interpreta usando `Protocol.java`
- Executa operações via DAOs apropriados
- Retorna respostas ao cliente
- Gerencia autenticação usando `SecurityUtil.java`

#### 4. Protocol.java
**Função:** Define protocolo de comunicação
- Comandos suportados:
  - LOGIN: Autenticação
  - REGISTER: Registro de novo usuário
  - LISTAR_USUARIOS: Lista todos usuários (admin)
  - ATUALIZAR_USUARIO: Atualiza dados de usuário
  - SYNC_CONTAS: Sincroniza contas
  - SYNC_CATEGORIAS: Sincroniza categorias
  - SYNC_LANCAMENTOS: Sincroniza lançamentos
  - CREATE/UPDATE/DELETE para cada entidade
- Define formato de resposta (SUCCESS, ERROR, DATA)

#### 5. DAOs do Servidor

**UsuarioDAO.java**
- Função: Gerencia usuários no MySQL
- Métodos:
  - autenticar(email, senha): Valida login
  - criar(usuario): Cria novo usuário
  - atualizar(usuario): Atualiza dados
  - listarTodos(): Lista usuários (admin)
  - buscarPorId(id): Busca usuário específico

**ContaDAO.java**
- Função: Gerencia contas no MySQL
- Métodos:
  - criar(conta): Cria nova conta
  - atualizar(conta): Atualiza conta
  - deletar(id): Remove conta
  - listarPorUsuario(usuarioId): Lista contas do usuário
  - atualizarSaldo(contaId, valor): Atualiza saldo

**CategoriaDAO.java**
- Função: Gerencia categorias no MySQL
- Métodos:
  - criar(categoria): Cria categoria
  - atualizar(categoria): Atualiza categoria
  - deletar(id): Remove categoria
  - listarPorUsuario(usuarioId): Lista categorias do usuário

**MovimentacaoDAO.java**
- Função: Gerencia movimentações no MySQL
- Métodos:
  - criar(movimentacao): Cria lançamento
  - atualizar(movimentacao): Atualiza lançamento
  - deletar(id): Remove lançamento
  - listarPorUsuario(usuarioId): Lista lançamentos
  - listarPorConta(contaId): Lista por conta
  - listarPorCategoria(categoriaId): Lista por categoria
  - listarPorPeriodo(dataInicio, dataFim): Lista por período

#### 6. Utilitários do Servidor

**DatabaseUtil.java**
- Função: Gerencia conexões com MySQL
- Métodos:
  - getConnection(): Obtém conexão do pool
  - closeConnection(): Fecha conexão
  - inicializarBanco(): Cria tabelas se não existirem
  - Configurações: host, porta, database, usuário, senha

**SecurityUtil.java**
- Função: Segurança e criptografia
- Métodos:
  - hashSenha(senha): Gera hash SHA-256 da senha
  - verificarSenha(senha, hash): Valida senha
  - gerarToken(): Gera token de sessão

---

## 🔄 FLUXO DE DADOS COMPLETO

### Exemplo 1: Login de Usuário (Mobile)

1. **Usuario** insere credenciais em `LoginActivity.java`
2. `LoginActivity` chama `AuthManager.login(email, senha)`
3. `AuthManager` usa `ServerClient.sendCommand(Protocol.LOGIN, dados)`
4. `ServerClient` envia via socket TCP/IP para servidor
5. **Servidor** recebe em `ClientHandler.processCommand()`
6. `ClientHandler` chama `UsuarioDAO.autenticar(email, senha)`
7. `UsuarioDAO` consulta MySQL, valida senha com `SecurityUtil.verificarSenha()`
8. Resposta SUCCESS retorna para `ClientHandler`
9. `ClientHandler` envia resposta ao `ServerClient`
10. `ServerClient` retorna para `AuthManager`
11. `AuthManager` salva usuário em `UsuarioDao` (local)
12. `LoginActivity` redireciona para `MenuActivity`

### Exemplo 2: Criar Lançamento (Mobile)

1. **Usuario** preenche formulário em `MovementsActivity.java`
2. `MovementsActivity` cria objeto `Lancamento`
3. Salva localmente em `LancamentoDao.insert(lancamento)`
4. Chama `EnhancedSyncService.syncLancamento(lancamento)`
5. `EnhancedSyncService` envia ao servidor via `ServerClient`
6. **Servidor** (`ClientHandler`) recebe comando CREATE_LANCAMENTO
7. `ClientHandler` chama `MovimentacaoDAO.criar(movimentacao)`
8. `MovimentacaoDAO` insere no MySQL e atualiza saldo via `ContaDAO`
9. Resposta SUCCESS retorna ao mobile
10. `MovementsActivity` atualiza UI com novo lançamento

### Exemplo 3: Administrador Edita Usuário (Desktop)

1. **Admin** seleciona usuário em `AdminDashboardView.java`
2. Clica em "Editar", abre `EditarUsuarioDialog.java`
3. Admin altera dados e clica "Salvar"
4. `EditarUsuarioDialog` chama `FinanceController.atualizarUsuario(usuario)`
5. `FinanceController` usa `NetworkClient.sendCommand(Protocol.ATUALIZAR_USUARIO, dados)`
6. **Servidor** (`ClientHandler`) recebe e chama `UsuarioDAO.atualizar(usuario)`
7. `UsuarioDAO` atualiza MySQL
8. Resposta SUCCESS retorna ao desktop
9. `AdminDashboardView` atualiza lista de usuários

### Exemplo 4: Sincronização Automática (Mobile)

1. `MenuActivity` inicia `SyncService` em background
2. `SyncService` verifica dados pendentes em todos os DAOs
3. Para cada operação pendente:
   - Envia ao servidor via `ServerClient`
   - Aguarda confirmação
   - Marca como sincronizado ou trata erro
4. Busca atualizações do servidor
5. Aplica atualizações localmente via DAOs
6. `ConflictResolutionManager` resolve conflitos se houver
7. Notifica activities sobre dados atualizados

---

## 🎨 CAMADA DE INTERFACE

### Mobile - Activities e Layouts

- **activity_login.xml** → LoginActivity.java
- **activity_register.xml** → RegisterActivity.java
- **activity_menu.xml** → MenuActivity.java (Dashboard)
- **activity_accounts.xml** → AccountsActivity.java
- **activity_categoria.xml** → CategoriaActivity.java
- **activity_movements.xml** → MovementsActivity.java
- **activity_profile.xml** → ProfileActivity.java
- **activity_settings.xml** → SettingsActivity.java

### Desktop - Views Swing

- **LoginView.java** → JFrame de login
- **AdminDashboardView.java** → JFrame principal com JTable de usuários
- **EditarUsuarioDialog.java** → JDialog modal para edição

---

## 🗄️ BANCO DE DADOS

### Mobile - Room (SQLite)

Tabelas:
- **usuario**: id, nome, email, senha_hash, data_criacao
- **conta**: id, usuario_id, nome, tipo, saldo, data_criacao
- **categoria**: id, usuario_id, nome, tipo, data_criacao
- **lancamento**: id, usuario_id, conta_id, categoria_id, valor, tipo, descricao, data, sincronizado

### Servidor - MySQL

Tabelas (mesma estrutura):
- **usuarios**
- **contas**
- **categorias**
- **movimentacoes**

---

## 📡 COMUNICAÇÃO

### Protocolo TCP/IP

- **Porta padrão:** 12345
- **Formato:** Texto com delimitadores
- **Estrutura:** COMANDO|PARAM1|PARAM2|...
- **Resposta:** SUCCESS|dados ou ERROR|mensagem

### Comandos Principais

- LOGIN|email|senha
- REGISTER|nome|email|senha
- SYNC_CONTAS|usuario_id
- SYNC_CATEGORIAS|usuario_id
- SYNC_LANCAMENTOS|usuario_id
- CREATE_CONTA|dados
- UPDATE_CONTA|dados
- DELETE_CONTA|id
- (similar para categorias e lançamentos)

---

## 🔐 SEGURANÇA

- **Senhas:** Hash SHA-256 via SecurityUtil
- **Comunicação:** Socket TCP/IP (pode ser atualizado para SSL/TLS)
- **Autenticação:** Email + senha hash
- **Sessão:** Mantida enquanto conexão ativa

---

## 🚀 EXECUÇÃO

### Mobile
1. Compilar no Android Studio
2. Configurar IP do servidor em ServerClient.java
3. Instalar APK no dispositivo

### Desktop (Servidor)
1. Configurar MySQL
2. Executar script SQL de criação de tabelas
3. Compilar e executar MainServidor.java
4. Servidor fica escutando na porta 12345

### Desktop (Cliente)
1. Compilar no NetBeans ou IDE
2. Executar MainCliente.java
3. Fazer login como administrador

---

Este mapeamento fornece uma visão completa da arquitetura, fluxos e organização do sistema Finanza.
