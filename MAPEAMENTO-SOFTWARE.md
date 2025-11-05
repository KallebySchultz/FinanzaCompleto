# 📚 MAPEAMENTO SOFTWARE - Finanza

> Sistema de gestão financeira completo com versões Desktop (Java Swing) e Mobile (Android)

---

## 📋 Índice

- [Desktop - Cliente](#-desktop---cliente)
  - [Ponto de Entrada](#ponto-de-entrada)
  - [Models](#models)
  - [Controllers](#controllers)
  - [Views](#views)
  - [Utils](#utils)
- [Desktop - Servidor](#-desktop---servidor)
  - [Ponto de Entrada](#ponto-de-entrada-servidor)
  - [Models](#models-servidor)
  - [DAOs](#daos)
  - [Server](#server)
  - [Utils](#utils-servidor)
- [Mobile - Android](#-mobile---android)
  - [Ponto de Entrada](#ponto-de-entrada-mobile)
  - [Models](#models-mobile)
  - [Database (Room)](#database-room)
  - [Network](#network)
  - [UI Activities](#ui-activities)
  - [Utils](#utils-mobile)

---

## 🖥️ DESKTOP - CLIENTE

### Ponto de Entrada

#### `MainCliente`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/MainCliente.java`

**O que é?**
- Classe principal do cliente desktop com método `main()`
- Ponto de entrada da aplicação GUI

**O que faz?**
- Inicializa a interface gráfica criando `LoginView`
- Usa `SwingUtilities.invokeLater()` para criar a UI na Event Dispatch Thread (EDT)
- Em caso de erro, exibe `JOptionPane` e encerra com `System.exit(1)`

**Pontos importantes:**
- ✅ Uso correto do EDT para thread safety do Swing
- ⚠️ Tratamento genérico de exceções força encerramento da aplicação
- 💡 Bloco de Look & Feel comentado contém erro - usar `UIManager.getSystemLookAndFeelClassName()`

---

### Models

#### `Categoria`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/model/Categoria.java`

**O que é?**
- Modelo que representa categorias de movimentação financeira

**O que faz?**
- Armazena: `id`, `nome`, `tipo` (enum RECEITA/DESPESA), `idUsuario`, `dataCriacao`
- Fornece getters/setters
- Conversão String → enum via `fromString()`

**Pontos importantes:**
- ⚠️ `fromString()` lança `IllegalArgumentException` para valores inválidos
- ⚠️ `toString()` usa `tipo.getDescricao()` - pode causar NPE se `tipo` for null
- 📅 Usa `java.sql.Timestamp` - considere migrar para `java.time`
- 🔍 Não implementa `equals()`/`hashCode()` - necessário para uso em coleções

---

#### `Conta`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/model/Conta.java`

**O que é?**
- Modelo que representa uma conta financeira

**O que faz?**
- Armazena: `id`, `nome`, `tipo` (enum TipoConta), `saldoInicial`, `saldoAtual`, `idUsuario`, `dataCriacao`
- Enum `TipoConta`: CORRENTE, POUPANCA, CARTAO, INVESTIMENTO, DINHEIRO
- Fornece `getValor()`, `getDescricao()` e `fromString()`

**Pontos importantes:**
- ⚠️ `TipoConta.fromString()` lança `IllegalArgumentException` - validar entradas
- ⚠️ `toString()` pode causar NPE se `tipo` for null
- 💰 Usa `double` para valores monetários - **recomenda-se `BigDecimal`** para precisão
- 📅 Usa `java.sql.Timestamp` - considere `java.time` (Instant/LocalDateTime)
- 🔍 Não implementa `equals()`/`hashCode()` nem `Serializable`
- 🔗 Campos `idUsuario` e `dataCriacao` indicam relacionamento com usuário

---

#### `Movimentacao`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/model/Movimentacao.java`

**O que é?**
- Modelo que representa uma movimentação financeira (receita ou despesa)

**O que faz?**
- Armazena: `id`, `descricao`, `valor`, `data`, `tipo` (enum TipoMovimentacao), `idCategoria`, `nomeCategoria`, `idConta`, `idUsuario`
- Enum `TipoMovimentacao`: RECEITA, DESPESA
- Fornece construtores, getters/setters e conversões

**Pontos importantes:**
- 💰 Usa `double` para valores - **recomenda-se `BigDecimal`**
- 📅 Usa `java.sql.Date` - considere `java.time.LocalDate`
- ⚠️ `fromString()` lança `IllegalArgumentException`
- 🔗 Relaciona-se com Categoria, Conta e Usuario
- 📝 Campo `nomeCategoria` facilita exibição mas indica possível desnormalização

---

#### `Usuario`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/model/Usuario.java`

**O que é?**
- Modelo que representa um usuário do sistema

**O que faz?**
- Armazena: `id`, `nome`, `email`, `senha`, `perfil` (enum Perfil), `ativo`, `dataCriacao`
- Enum `Perfil`: ADMIN, USUARIO
- Fornece validação e conversões

**Pontos importantes:**
- 🔐 Armazena senha em texto - **atenção à segurança**
- ⚠️ `Perfil.fromString()` lança `IllegalArgumentException`
- ✅ Campo `ativo` permite soft delete
- 📅 Usa `java.sql.Timestamp` para `dataCriacao`
- 📧 Não há validação de formato de email no modelo

---

### Controllers

#### `AuthController`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/controller/AuthController.java`

**O que é?**
- Controlador que gerencia autenticação e autorização

**O que faz?**
- Login/logout de usuários
- Cadastro de novos usuários
- Listagem de usuários (para admin)
- Comunicação com servidor via `NetworkClient`
- Retorna `OperationResult<T>` com status de sucesso/falha

**Pontos importantes:**
- 🔗 Depende de `NetworkClient` para comunicação
- 📡 **Protocolo frágil:** usa separadores "|" e ";" para parsing
- ⚠️ Chama `resposta.split()` sem verificar null - risco de NPE
- 🔓 **Credenciais em texto plano** - necessita TLS
- 📊 Parsing inseguro com `Integer.parseInt()` e `Timestamp.valueOf()`
- 🔍 Expõe `NetworkClient` via `getNetworkClient()` - aumenta acoplamento
- 📝 Usa `System.out.println` para logging - usar logger configurável
- ⚙️ Tratamento parcial para versões antigas do protocolo

**Melhorias recomendadas:**
- ✨ Validar null antes do split
- ✨ Tratar parsing com try/catch adequado
- ✨ Centralizar parsing/serialização do protocolo
- ✨ Migrar para JSON/DTOs
- ✨ Remover exposição direta do NetworkClient
- ✨ Garantir canal seguro (TLS) para senhas

---

#### `FinanceController`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/controller/FinanceController.java`

**O que é?**
- Controlador principal para operações financeiras

**O que faz?**
- Dashboard com resumo financeiro
- CRUD de contas, categorias e movimentações
- Gestão de perfil do usuário
- Usa `NetworkClient` para comunicação com servidor
- Retorna `OperationResult<T>` e `DashboardData`
- Converte formatos numéricos brasileiros (vírgula decimal)

**Pontos importantes:**
- 🔗 **Dependências:** `NetworkClient`, models (Conta, Categoria, Movimentacao, Usuario)
- 📡 **Protocolo frágil:** SEPARATOR="|" e FIELD_SEPARATOR=";"
- ⚠️ Chama `resposta.split()` sem verificar null - **risco de NPE**
- 🔢 Parsing complexo: múltiplos splits e reconstruções
- ⚠️ Nem todos os parses (`parseInt`, `Date.valueOf`) estão protegidos
- 💰 Usa `double` e `parsePortugueseDouble` - **recomenda-se BigDecimal**
- 📅 Usa `java.sql.Date` e `Date.valueOf` (formato yyyy-MM-dd)
- 📝 Logging com `System.out`/`System.err` - usar logger configurável
- 🔗 Possível exposição do NetworkClient para UI
- 🔄 Duplicação de lógica de parsing em vários métodos
- ⏱️ **Métodos síncronos (bloqueantes)** - UI deve chamar em background (SwingWorker)
- 🔒 Entradas concatenadas diretamente - **validar/escapar separadores**
- 💡 `carregarNomesCategorias()` faz chamada extra - considerar cache

**Melhorias recomendadas:**
- ✨ Validar respostas antes do parsing
- ✨ Centralizar utilitários de parsing/serialização
- ✨ Usar BigDecimal para valores monetários
- ✨ Migrar para java.time
- ✨ Implementar cache para categorias
- ✨ Adicionar escape/validação de separadores

---

### Views

#### `LoginView`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/view/LoginView.java`

**O que é?**
- Tela de login da aplicação desktop

**O que faz?**
- Interface para autenticação de usuários
- Campos para email e senha
- Botões de login e cadastro
- Usa `AuthController` para autenticação
- Usa `SwingWorker` para operações assíncronas

**Pontos importantes:**
- 🎨 Interface Swing com layout personalizado
- ⏱️ **Operações de rede em background** (SwingWorker) - boa prática
- 🔄 Redireciona para AdminDashboardView ou outra view após login
- ⚠️ Tratamento de erros com JOptionPane
- 🔐 Não exibe senha durante digitação (JPasswordField)

---

#### `AdminDashboardView`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/view/AdminDashboardView.java`

**O que é?**
- Dashboard principal para usuários administradores

**O que faz?**
- Exibe resumo financeiro
- Menu de navegação para diferentes funcionalidades
- Gestão de usuários (admin)
- Visualização de contas e movimentações
- Usa `FinanceController` para operações

**Pontos importantes:**
- 🎨 Interface complexa com múltiplos painéis
- 📊 Exibe dados do dashboard em tempo real
- 🔄 Usa SwingWorker para operações assíncronas
- 👥 Funcionalidades administrativas (listagem/edição de usuários)
- 📝 Tabelas para exibição de dados
- ⚠️ Possível sobrecarga se carregar muitos dados

---

#### `EditarUsuarioDialog`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/view/EditarUsuarioDialog.java`

**O que é?**
- Diálogo modal para edição de usuários

**O que faz?**
- Formulário para editar dados do usuário
- Validação de campos
- Salva alterações via controller
- Retorna resultado da operação

**Pontos importantes:**
- 🎨 JDialog modal - bloqueia janela principal
- ✅ Validação de campos antes de salvar
- 🔄 Usa SwingWorker para operações de rede
- ⚠️ Tratamento de erros com feedback visual
- 📝 Campos: nome, email, perfil, status ativo

---

### Utils

#### `NetworkClient`
**Localização:** `DESKTOP VERSION/ClienteFinanza/src/util/NetworkClient.java`

**O que é?**
- Componente que gerencia conexão TCP com o servidor

**O que faz?**
- Abre Socket para `SERVER_HOST:SERVER_PORT`
- Cria `BufferedReader`/`PrintWriter` para I/O
- Envia comandos com `output.println()`
- Lê respostas com `input.readLine()`
- Fornece: `connect()`, `sendCommand()`, `disconnect()`, `isConnected()`

**Pontos importantes:**
- 📡 **Protocolo síncrono por linha** - uma linha por comando/resposta
- ⚠️ **`sendCommand()` não é sincronizado** - risco de mistura de mensagens em multi-thread
- ⚠️ Se `readLine()` retorna null (servidor fechou), pode causar NPE em clientes
- 🔀 Mistura controle por string ("ERROR|...") e exceções
- ⏱️ **Sem timeout** - `readLine()` pode bloquear indefinidamente
- 🔓 **Host/Port hardcoded** - sem TLS/SSL
- 📝 Charset padrão da plataforma - forçar UTF-8
- 📝 Logging com `System.out`/`err` - usar logger

**Melhorias recomendadas (priorizadas):**
1. 🔒 **Sincronizar `sendCommand()`** - adicionar `synchronized` ou lock dedicado
2. ✅ **Tratar resposta nula** - lançar IOException ou retornar OperationResult claro
3. ⏱️ **Adicionar timeout:**
   ```java
   socket = new Socket();
   socket.connect(new InetSocketAddress(host, port), connectTimeout);
   socket.setSoTimeout(readTimeout);
   ```
4. 🌐 **Forçar UTF-8:**
   ```java
   input = new BufferedReader(new InputStreamReader(
       socket.getInputStream(), StandardCharsets.UTF_8));
   output = new PrintWriter(new OutputStreamWriter(
       socket.getOutputStream(), StandardCharsets.UTF_8), true);
   ```
5. ⚙️ **Tornar configurável** - host/port via properties
6. 🔐 **Suportar TLS** - usar SSLSocket para produção
7. 📊 **Usar logger** - substituir System.out.println

---

## 🖥️ DESKTOP - SERVIDOR

### Ponto de Entrada (Servidor)

#### `MainServidor`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/MainServidor.java`

**O que é?**
- Classe principal do servidor com método `main()`

**O que faz?**
- Inicializa banco de dados via `DatabaseUtil`
- Cria e inicia `FinanzaServer`
- Escuta conexões de clientes
- Trata sinais de shutdown gracefully

**Pontos importantes:**
- 🚀 Ponto de entrada do servidor
- 🗄️ Inicializa conexão com banco de dados
- 🔄 Loop principal aceita conexões
- ⚠️ Tratamento de exceções de inicialização
- 🛑 Cleanup em shutdown (fechar conexões, DB)

---

### Models (Servidor)

#### `Categoria` (Servidor)
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/model/Categoria.java`

**O que é?**
- Modelo servidor para categorias

**O que faz?**
- Mesma estrutura do cliente
- Persistência via DAO
- Conversões para protocolo de comunicação

**Pontos importantes:**
- 🔄 Similar ao modelo do cliente
- 🗄️ Mapeado para banco de dados
- ⚠️ Validações devem ser feitas no servidor
- 📅 Usa `java.sql.Timestamp`

---

#### `Conta` (Servidor)
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/model/Conta.java`

**O que é?**
- Modelo servidor para contas financeiras

**O que faz?**
- Armazena dados de contas
- Persistência via DAO
- Validações de negócio

**Pontos importantes:**
- 💰 Validação de saldos no servidor
- 🔒 Regras de negócio centralizadas
- 🗄️ Persistência com ContaDAO

---

#### `Movimentacao` (Servidor)
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/model/Movimentacao.java`

**O que é?**
- Modelo servidor para movimentações

**O que faz?**
- Registra transações financeiras
- Atualiza saldos de contas
- Validações de integridade

**Pontos importantes:**
- 💰 Atualização de saldos deve ser transacional
- 🔒 Validações de categoria e conta
- 📅 Registro de data/hora da transação

---

#### `Usuario` (Servidor)
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/model/Usuario.java`

**O que é?**
- Modelo servidor para usuários

**O que faz?**
- Armazena dados de autenticação
- Hash de senhas com `SecurityUtil`
- Controle de perfis (ADMIN/USUARIO)

**Pontos importantes:**
- 🔐 Senhas devem ser hasheadas (BCrypt)
- ✅ Validação de email
- 🔒 Controle de acesso por perfil
- 📝 Auditoria com dataCriacao

---

### DAOs

#### `CategoriaDAO`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/dao/CategoriaDAO.java`

**O que é?**
- Data Access Object para Categoria

**O que faz?**
- CRUD de categorias no banco
- Consultas por usuário
- Validação de duplicatas

**Pontos importantes:**
- 🗄️ Usa `DatabaseUtil` para conexões
- 🔍 Filtro por `idUsuario`
- ⚠️ Tratamento de SQLException
- ✅ Validação de unicidade (nome + tipo + usuário)

---

#### `ContaDAO`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/dao/ContaDAO.java`

**O que é?**
- Data Access Object para Conta

**O que faz?**
- CRUD de contas
- Atualização de saldos
- Consultas por usuário

**Pontos importantes:**
- 💰 Métodos específicos para atualizar saldos
- 🔒 Transações ao modificar saldos
- 🔍 Listagem por usuário
- ⚠️ Validação de saldo suficiente

---

#### `MovimentacaoDAO`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/dao/MovimentacaoDAO.java`

**O que é?**
- Data Access Object para Movimentacao

**O que faz?**
- Registra movimentações
- Atualiza saldo da conta associada
- Consultas e relatórios
- Filtros por período, categoria, conta

**Pontos importantes:**
- 💰 **Operação transacional** - movimentação + atualização de saldo
- 🔄 Rollback em caso de erro
- 📊 Consultas para dashboard
- 📅 Filtros por data
- 🔍 Agregações (soma receitas/despesas)

---

#### `UsuarioDAO`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/dao/UsuarioDAO.java`

**O que é?**
- Data Access Object para Usuario

**O que faz?**
- CRUD de usuários
- Autenticação (busca por email)
- Validação de unicidade de email
- Listagem para admin

**Pontos importantes:**
- 🔐 Busca por email para login
- ✅ Validação de email único
- 🔒 Senhas já devem estar hasheadas
- 👥 Listagem de todos usuários (admin)
- ⚠️ Soft delete com campo `ativo`

---

### Server

#### `FinanzaServer`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/server/FinanzaServer.java`

**O que é?**
- Servidor TCP principal

**O que faz?**
- Cria `ServerSocket` na porta configurada
- Aceita conexões de clientes
- Cria `ClientHandler` para cada cliente
- Gerencia pool de threads

**Pontos importantes:**
- 🌐 Multi-threaded - um thread por cliente
- ⚠️ Limite de conexões simultâneas
- 🔄 Loop infinito aceita conexões
- 🛑 Shutdown graceful
- 📊 Logging de conexões

---

#### `ClientHandler`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java`

**O que é?**
- Handler para cada conexão de cliente

**O que faz?**
- Implementa `Runnable` para execução em thread
- Lê comandos do cliente
- Delega para `Protocol` processar
- Envia respostas ao cliente
- Trata desconexões

**Pontos importantes:**
- 🔄 Loop de leitura de comandos
- 📡 Usa `Protocol` para processar
- ⚠️ Tratamento de IOExceptions
- 🔐 Mantém estado da sessão
- 🧹 Cleanup ao desconectar

---

#### `Protocol`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/server/Protocol.java`

**O que é?**
- Processador de comandos do protocolo

**O que faz?**
- Parse de comandos recebidos
- Roteamento para DAOs apropriados
- Construção de respostas
- Validações de autorização
- Serialização de objetos para string

**Pontos importantes:**
- 📡 **Protocolo baseado em texto** com separadores
- 🔀 Switch/case grande para tipos de comando
- 🔐 Validação de sessão/permissões
- 💰 Parse de valores monetários
- ⚠️ Tratamento de exceções → mensagens de erro
- 📝 Formato: `COMMAND|param1;param2;param3`
- ✅ Respostas: `SUCCESS|dados` ou `ERROR|mensagem`

**Comandos suportados:**
- `LOGIN`, `REGISTER`, `LOGOUT`
- `GET_DASHBOARD`, `LIST_CONTAS`, `ADD_CONTA`, `UPDATE_CONTA`, `DELETE_CONTA`
- `LIST_CATEGORIAS`, `ADD_CATEGORIA`, `UPDATE_CATEGORIA`, `DELETE_CATEGORIA`
- `LIST_MOVIMENTACOES`, `ADD_MOVIMENTACAO`, `UPDATE_MOVIMENTACAO`, `DELETE_MOVIMENTACAO`
- `GET_PROFILE`, `UPDATE_PROFILE`
- `LIST_USUARIOS` (admin only)

---

### Utils (Servidor)

#### `DatabaseUtil`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/util/DatabaseUtil.java`

**O que é?**
- Utilitário para gerenciamento de banco de dados

**O que faz?**
- Cria pool de conexões
- Fornece conexões para DAOs
- Inicializa schema do banco
- Scripts de migração/setup

**Pontos importantes:**
- 🗄️ Connection pooling (HikariCP ou similar)
- ⚙️ Configuração via properties/env vars
- 🔧 Inicialização de tabelas
- ⚠️ Validação de conexões
- 🔒 Credenciais do banco

---

#### `SecurityUtil`
**Localização:** `DESKTOP VERSION/ServidorFinanza/src/util/SecurityUtil.java`

**O que é?**
- Utilitário para segurança e criptografia

**O que faz?**
- Hash de senhas com BCrypt
- Verificação de senhas
- Geração de tokens/sessions
- Validações de segurança

**Pontos importantes:**
- 🔐 **BCrypt para hash de senhas** - não usar MD5/SHA1
- 🔒 Salt automático no BCrypt
- ✅ Método `verify()` para validar senha
- 🎲 Geração segura de tokens
- ⚠️ Não armazenar senhas em plain text

---

## 📱 MOBILE - ANDROID

### Ponto de Entrada (Mobile)

#### `MainActivity`
**Localização:** `app/src/main/java/com/example/finanza/MainActivity.java`

**O que é?**
- Activity principal do app Android

**O que faz?**
- Ponto de entrada da aplicação mobile
- Verifica autenticação
- Redireciona para LoginActivity ou MenuActivity
- Inicializa banco local (Room)

**Pontos importantes:**
- 🚀 Launcher activity
- 🔐 Verifica se usuário está logado
- 🗄️ Inicializa AppDatabase
- 🔄 Redireciona baseado em estado de auth

---

### Models (Mobile)

#### `Usuario` (Mobile)
**Localização:** `app/src/main/java/com/example/finanza/model/Usuario.java`

**O que é?**
- Entidade Room para usuários

**O que faz?**
- Armazena dados do usuário logado
- Mapeamento para tabela local
- Sincronização com servidor

**Pontos importantes:**
- 🗄️ `@Entity` do Room
- 🔑 `@PrimaryKey` com id do servidor
- 🔄 Campos de sincronização (lastSync, syncStatus)
- 📱 Armazenamento local para offline

---

#### `Conta` (Mobile)
**Localização:** `app/src/main/java/com/example/finanza/model/Conta.java`

**O que é?**
- Entidade Room para contas

**O que faz?**
- Armazena contas financeiras localmente
- Sincronização com servidor
- Estado de modificação para sync

**Pontos importantes:**
- 🗄️ `@Entity` com relacionamento a Usuario
- 💰 Campos de saldo (inicial e atual)
- 🔄 Status de sincronização
- 📱 Permite uso offline

---

#### `Categoria` (Mobile)
**Localização:** `app/src/main/java/com/example/finanza/model/Categoria.java`

**O que é?**
- Entidade Room para categorias

**O que faz?**
- Categorias de movimentações
- Cache local
- Sincronização bidirecional

**Pontos importantes:**
- 🗄️ `@Entity` Room
- 🏷️ Tipo (RECEITA/DESPESA)
- 🔄 Sync com servidor
- 👤 Por usuário

---

#### `Lancamento`
**Localização:** `app/src/main/java/com/example/finanza/model/Lancamento.java`

**O que é?**
- Entidade Room para lançamentos (movimentações)

**O que faz?**
- Registra transações financeiras
- Permite edição offline
- Sincronização com conflitos

**Pontos importantes:**
- 🗄️ `@Entity` com foreign keys
- 💰 Valor da transação
- 📅 Data do lançamento
- 🔄 Estado de sincronização (SYNCED, PENDING, CONFLICT)
- ⚠️ Resolução de conflitos

---

### Database (Room)

#### `AppDatabase`
**Localização:** `app/src/main/java/com/example/finanza/db/AppDatabase.java`

**O que é?**
- Banco de dados Room local

**O que faz?**
- Define schema do banco SQLite
- Fornece DAOs
- Gerencia migrações
- Singleton para acesso global

**Pontos importantes:**
- 🗄️ `@Database` do Room com versão
- 📊 Entidades: Usuario, Conta, Categoria, Lancamento
- 🔧 Migrations para versionamento
- 🔒 Singleton pattern
- 💾 Armazenamento local persistente

---

#### `UsuarioDao`
**Localização:** `app/src/main/java/com/example/finanza/db/UsuarioDao.java`

**O que é?**
- DAO Room para Usuario

**O que faz?**
- CRUD no banco local
- Queries customizadas
- Observables para UI (LiveData/Flow)

**Pontos importantes:**
- 🗄️ `@Dao` do Room
- 🔍 Queries: `@Query`, `@Insert`, `@Update`, `@Delete`
- 📊 Retorna LiveData para observação na UI
- 🔐 Busca usuário logado

---

#### `ContaDao`
**Localização:** `app/src/main/java/com/example/finanza/db/ContaDao.java`

**O que é?**
- DAO Room para Conta

**O que faz?**
- Operações CRUD locais
- Listagem por usuário
- Atualização de saldos

**Pontos importantes:**
- 🗄️ Queries otimizadas
- 💰 Métodos para atualizar saldo
- 🔍 Filtro por idUsuario
- 📊 LiveData para UI reativa

---

#### `CategoriaDao`
**Localização:** `app/src/main/java/com/example/finanza/db/CategoriaDao.java`

**O que é?**
- DAO Room para Categoria

**O que faz?**
- CRUD local de categorias
- Listagem por tipo
- Cache para uso offline

**Pontos importantes:**
- 🗄️ Queries por tipo (RECEITA/DESPESA)
- 👤 Filtro por usuário
- 📊 LiveData para observação
- 🔄 Sincronização incremental

---

#### `LancamentoDao`
**Localização:** `app/src/main/java/com/example/finanza/db/LancamentoDao.java`

**O que é?**
- DAO Room para Lancamento

**O que faz?**
- CRUD de lançamentos
- Queries por período
- Filtros por conta/categoria
- Identificação de pendências de sync

**Pontos importantes:**
- 🗄️ Queries complexas com joins
- 📅 Filtros por data
- 💰 Agregações (soma receitas/despesas)
- 🔄 Busca lançamentos não sincronizados
- ⚠️ Detecção de conflitos

---

### Network

#### `ServerClient`
**Localização:** `app/src/main/java/com/example/finanza/network/ServerClient.java`

**O que é?**
- Cliente de rede para Android

**O que faz?**
- Similar ao NetworkClient do desktop
- Conexão TCP com servidor
- Envia comandos e recebe respostas
- Gerencia timeout e reconexão

**Pontos importantes:**
- 📡 Protocolo idêntico ao desktop
- ⏱️ Timeout configurável
- 🔄 Retry automático
- ⚠️ Tratamento de rede móvel instável
- 🔐 Preparado para TLS

---

#### `Protocol` (Mobile)
**Localização:** `app/src/main/java/com/example/finanza/network/Protocol.java`

**O que é?**
- Definição do protocolo de comunicação

**O que faz?**
- Constantes de comandos
- Builders para mensagens
- Parsers de resposta
- Validação de formato

**Pontos importantes:**
- 📡 Mesmos comandos do servidor
- 🔧 Métodos helper para construir/parsear
- ✅ Validação de respostas
- ⚠️ Tratamento de erros de protocolo

---

#### `AuthManager`
**Localização:** `app/src/main/java/com/example/finanza/network/AuthManager.java`

**O que é?**
- Gerenciador de autenticação mobile

**O que faz?**
- Login/logout
- Armazena token/sessão
- Verifica estado de autenticação
- SharedPreferences para persistência

**Pontos importantes:**
- 🔐 Armazena credenciais seguras (SharedPreferences)
- 🔑 Gestão de sessão/token
- ✅ Verifica expiração
- 🔄 Logout automático
- 📱 Singleton

---

#### `SyncService`
**Localização:** `app/src/main/java/com/example/finanza/network/SyncService.java`

**O que é?**
- Serviço de sincronização básico

**O que faz?**
- Sincroniza dados locais com servidor
- Upload de pendências
- Download de atualizações
- Executa em background

**Pontos importantes:**
- 🔄 Sincronização bidirecional
- ⏱️ Pode ser agendada (WorkManager)
- 📊 Sincroniza: contas, categorias, lançamentos
- ⚠️ Tratamento de erros de rede
- 📱 Apenas em WiFi (configurável)

---

#### `EnhancedSyncService`
**Localização:** `app/src/main/java/com/example/finanza/network/EnhancedSyncService.java`

**O que é?**
- Serviço de sincronização avançado

**O que faz?**
- Extends `SyncService`
- Sincronização incremental (apenas mudanças)
- Timestamps para eficiência
- Retry inteligente
- Sincronização por entidade

**Pontos importantes:**
- ⚡ **Mais eficiente** - apenas deltas
- 🕐 Usa lastSync timestamp
- 🔄 Retry com backoff exponencial
- 📊 Progress callbacks para UI
- ⚠️ Detecção de conflitos
- 💾 Cache de resultados

---

#### `ConflictResolutionManager`
**Localização:** `app/src/main/java/com/example/finanza/network/ConflictResolutionManager.java`

**O que é?**
- Gerenciador de resolução de conflitos

**O que faz?**
- Detecta conflitos de sincronização
- Estratégias de resolução (server wins, client wins, manual)
- UI para resolução manual
- Merge de dados conflitantes

**Pontos importantes:**
- ⚠️ **Essencial para sync offline**
- 🔀 Estratégias: SERVER_WINS, CLIENT_WINS, LAST_WRITE_WINS, MANUAL
- 📊 Detecta conflitos por timestamp
- 🎨 Interface para usuário escolher
- 💾 Backup antes de resolver
- 📝 Log de conflitos resolvidos

---

### UI Activities

#### `LoginActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/LoginActivity.java`

**O que é?**
- Tela de login do app

**O que faz?**
- Formulário de autenticação
- Validação de campos
- Login via AuthManager
- Navegação para MenuActivity

**Pontos importantes:**
- 🎨 Material Design
- ✅ Validação de email/senha
- ⏱️ Login assíncrono
- 📱 Feedback visual (ProgressBar)
- 🔄 Link para RegisterActivity
- 💾 Opção "Lembrar-me"

---

#### `RegisterActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/RegisterActivity.java`

**O que é?**
- Tela de cadastro

**O que faz?**
- Formulário para novo usuário
- Validações (email único, senha forte)
- Cadastro via AuthManager
- Auto-login após cadastro

**Pontos importantes:**
- 🎨 Material Design
- ✅ Validações múltiplas
- 🔐 Confirmação de senha
- ⏱️ Assíncrono
- 📱 Feedback de erros
- 🔄 Volta para Login após sucesso

---

#### `MenuActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/MenuActivity.java`

**O que é?**
- Menu principal / Dashboard

**O que faz?**
- Exibe resumo financeiro
- Cartões de navegação
- Saldo total, receitas, despesas
- Acesso a todas funcionalidades

**Pontos importantes:**
- 🎨 Material Design com cards
- 📊 Dashboard com resumo
- 🔄 Atualização em tempo real (LiveData)
- 📱 Navigation drawer
- 🔐 Logout
- ⚙️ Acesso a configurações

---

#### `AccountsActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/AccountsActivity.java`

**O que é?**
- Gerenciamento de contas

**O que faz?**
- Lista contas do usuário
- Adicionar/editar/excluir contas
- Visualizar saldo de cada conta
- CRUD completo

**Pontos importantes:**
- 🎨 RecyclerView com cards
- 💰 Exibe tipo e saldo
- ➕ FAB para adicionar
- ✏️ Edição inline ou dialog
- 🗑️ Confirmação para excluir
- 🔄 Sync automática

---

#### `CategoriaActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/CategoriaActivity.java`

**O que é?**
- Gerenciamento de categorias

**O que faz?**
- Lista categorias
- Filtro por tipo (Receita/Despesa)
- CRUD de categorias
- Organização visual

**Pontos importantes:**
- 🎨 Tabs para Receitas/Despesas
- 🏷️ Ícones e cores por categoria
- ➕ Adicionar categoria
- ✏️ Editar/excluir
- 🔄 Sincronização

---

#### `MovementsActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/MovementsActivity.java`

**O que é?**
- Gerenciamento de movimentações/lançamentos

**O que faz?**
- Lista lançamentos
- Adicionar receita/despesa
- Editar/excluir lançamentos
- Filtros (data, categoria, conta)
- Busca

**Pontos importantes:**
- 🎨 RecyclerView com swipe actions
- 📅 Filtro por período
- 🔍 Busca por descrição
- 🏷️ Filtro por categoria/conta
- 💰 Diferenciação visual (receita verde, despesa vermelho)
- ➕ FAB com opções (receita/despesa)
- ✏️ Edição rápida
- 🗑️ Deslizar para excluir
- 🔄 Pull to refresh

---

#### `ProfileActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/ProfileActivity.java`

**O que é?**
- Perfil do usuário

**O que faz?**
- Exibe dados do usuário
- Editar nome, email, senha
- Configurações de conta
- Logout

**Pontos importantes:**
- 🎨 Material Design
- 👤 Exibe avatar/inicial
- ✏️ Editar informações
- 🔐 Trocar senha
- 🔄 Sincronizar dados
- 🚪 Logout
- ⚠️ Excluir conta

---

#### `SettingsActivity`
**Localização:** `app/src/main/java/com/example/finanza/ui/SettingsActivity.java`

**O que é?**
- Configurações do app

**O que faz?**
- Preferências do usuário
- Configurações de sincronização
- Tema (claro/escuro)
- Notificações
- Sobre o app

**Pontos importantes:**
- ⚙️ PreferenceScreen
- 🔄 Configurar auto-sync
- 📶 Sincronizar apenas em WiFi
- 🔔 Notificações
- 🎨 Tema
- 📊 Limpar cache
- ℹ️ Versão do app

---

### Utils (Mobile)

#### `DataIntegrityValidator`
**Localização:** `app/src/main/java/com/example/finanza/util/DataIntegrityValidator.java`

**O que é?**
- Validador de integridade de dados

**O que faz?**
- Valida consistência de dados locais
- Verifica saldos de contas
- Detecta inconsistências
- Correção automática ou alerta

**Pontos importantes:**
- ✅ Valida saldos calculados vs armazenados
- 🔍 Detecta lançamentos órfãos
- ⚠️ Alerta sobre problemas
- 🔧 Opções de correção
- 📊 Relatório de validação
- 🔄 Executa antes de sync

---

## 📊 Visão Geral do Sistema

### Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENTES                            │
├──────────────────────┬──────────────────────────────────────┤
│   Desktop (Swing)    │      Mobile (Android)                │
│   - MainCliente      │      - MainActivity                   │
│   - Views            │      - UI Activities                  │
│   - Controllers      │      - Room Database                  │
│   - Models           │      - Network (Sync)                 │
│   - NetworkClient    │      - Models                         │
└──────────────────────┴──────────────────────────────────────┘
                           │
                           │ TCP Socket Protocol
                           │ (Text-based with separators)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVIDOR                                │
│   - FinanzaServer (TCP Server)                              │
│   - ClientHandler (Thread per client)                       │
│   - Protocol (Command processor)                            │
│   - DAOs (Database access)                                  │
│   - Models                                                   │
│   - Utils (DB, Security)                                    │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │   DATABASE  │
                    │  (SQL/JDBC) │
                    └─────────────┘
```

### Fluxo de Dados

**Desktop:**
1. User → View (Swing)
2. View → Controller (via SwingWorker)
3. Controller → NetworkClient
4. NetworkClient → Servidor (TCP)
5. Servidor processa e retorna
6. Controller parseia resposta
7. View atualiza UI (EDT)

**Mobile:**
1. User → Activity (UI)
2. Activity → DAO (Room) - operação local
3. Activity → SyncService (background)
4. SyncService → ServerClient
5. ServerClient → Servidor (TCP)
6. Servidor processa
7. SyncService atualiza Room
8. LiveData notifica UI
9. Activity atualiza interface

### Protocolo de Comunicação

**Formato:** `COMMAND|param1;param2;param3`

**Exemplos:**
- Login: `LOGIN|email;senha`
- Listar contas: `LIST_CONTAS|idUsuario`
- Adicionar movimentação: `ADD_MOVIMENTACAO|descricao;valor;data;tipo;idCategoria;idConta;idUsuario`

**Respostas:**
- Sucesso: `SUCCESS|dados`
- Erro: `ERROR|mensagem`

---

## 🔒 Considerações de Segurança

### Atuais
- ⚠️ Senhas em texto plano no protocolo
- ⚠️ Sem TLS/SSL
- ⚠️ Host/Port hardcoded
- ✅ Hash BCrypt no servidor
- ⚠️ Protocolo baseado em string (frágil)

### Recomendações
1. 🔐 Implementar TLS/SSL obrigatório
2. 🔑 Usar tokens JWT em vez de senhas repetidas
3. 🔒 Validar e escapar todos inputs
4. 🛡️ Rate limiting no servidor
5. 📝 Audit log de operações sensíveis
6. 🔐 Criptografia de dados sensíveis no mobile

---

## 🚀 Melhorias Futuras Sugeridas

### Protocolo
- [ ] Migrar para JSON em vez de separadores
- [ ] Implementar versionamento de API
- [ ] Adicionar compressão (gzip)
- [ ] WebSocket para notificações push

### Desktop
- [ ] Migrar para JavaFX (Swing está deprecado)
- [ ] Implementar cache local
- [ ] Adicionar gráficos e relatórios
- [ ] Suporte a múltiplas moedas

### Mobile
- [ ] Biometria para login
- [ ] Widget para dashboard
- [ ] Notificações de lembretes
- [ ] Exportar/importar dados (CSV, PDF)
- [ ] Gráficos interativos
- [ ] Categorias personalizadas com ícones
- [ ] Anexar comprovantes (fotos)

### Servidor
- [ ] API REST adicional (além do TCP)
- [ ] Websockets para real-time
- [ ] Backup automático
- [ ] Múltiplas moedas
- [ ] Relatórios e analytics
- [ ] Sistema de notificações

### Geral
- [ ] Testes automatizados (JUnit, Espresso)
- [ ] CI/CD pipeline
- [ ] Documentação OpenAPI/Swagger
- [ ] Containerização (Docker)
- [ ] Monitoring e observabilidade

---

## 📝 Notas de Manutenção

### Pontos de Atenção
- ⚠️ NetworkClient não é thread-safe
- ⚠️ Protocolo frágil com parsing manual
- ⚠️ Double para valores monetários (usar BigDecimal)
- ⚠️ java.sql.Date/Timestamp (migrar para java.time)
- ⚠️ Falta equals()/hashCode() em models

### Ao Adicionar Nova Funcionalidade
1. Adicionar comando ao Protocol
2. Implementar no ClientHandler/Protocol servidor
3. Adicionar DAO se necessário
4. Implementar no Controller (desktop)
5. Implementar no ServerClient/SyncService (mobile)
6. Criar/atualizar View/Activity
7. Testar sincronização e conflitos

---

## 📚 Referências

### Tecnologias Utilizadas
- **Desktop:** Java 8+, Swing, JDBC
- **Mobile:** Android SDK, Java, Room, Material Design
- **Servidor:** Java, TCP Sockets, JDBC
- **Database:** SQLite (mobile), MySQL/PostgreSQL (servidor)
- **Segurança:** BCrypt
- **Build:** Gradle

### Estrutura de Arquivos
```
FinanzaCompleto/
├── DESKTOP VERSION/
│   ├── ClienteFinanza/
│   │   └── src/
│   │       ├── MainCliente.java
│   │       ├── model/
│   │       ├── view/
│   │       ├── controller/
│   │       └── util/
│   └── ServidorFinanza/
│       └── src/
│           ├── MainServidor.java
│           ├── model/
│           ├── dao/
│           ├── server/
│           └── util/
└── app/
    └── src/main/java/com/example/finanza/
        ├── MainActivity.java
        ├── model/
        ├── db/
        ├── network/
        ├── ui/
        └── util/
```

---

**Última atualização:** 2025-11-05  
**Versão do documento:** 2.0  
**Autor:** Documentação Técnica Finanza
