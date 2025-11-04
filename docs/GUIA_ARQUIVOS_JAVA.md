# 📚 Guia Completo de Arquivos Java - Sistema Finanza

## 📖 Sobre este Guia

Este documento lista **TODOS os arquivos .java** do sistema Finanza e explica **de forma simples** o que cada um faz, qual sua responsabilidade e como se encaixa na arquitetura geral.

---

## 🗂️ Índice

1. [Desktop Admin - Cliente](#desktop-admin---cliente)
2. [Servidor Java](#servidor-java)
3. [Mobile Android](#mobile-android)

---

## 💻 Desktop Admin - Cliente

### 📂 Localização: `DESKTOP VERSION/ClienteFinanza/src/`

### 🏠 Arquivo Principal

#### `MainCliente.java`
- **Caminho**: `DESKTOP VERSION/ClienteFinanza/src/MainCliente.java`
- **Tipo**: Ponto de entrada da aplicação
- **O que faz**: Inicia o programa Desktop Admin
- **Como funciona**:
  - Cria e exibe a tela de login (LoginView)
  - É o primeiro código que executa quando você abre o Desktop Admin
- **Arquitetura**: Não faz parte do MVC, é só o inicializador
- **Analogia**: É como o botão de ligar de um computador

---

### 📦 MODEL - Modelos de Dados

#### `model/Usuario.java`
- **O que faz**: Representa um usuário do sistema
- **Atributos principais**:
  - `id` - Identificador único
  - `nome` - Nome completo
  - `email` - Email (usado no login)
  - `tipoUsuario` - "admin" ou "usuario"
  - `dataCriacao` - Quando foi cadastrado
- **Métodos importantes**:
  - `isAdmin()` - Verifica se é administrador
  - Getters e Setters para todos os campos
- **Arquitetura**: MODEL
- **Analogia**: Ficha cadastral de uma pessoa

#### `model/Conta.java`
- **O que faz**: Representa uma conta bancária
- **Atributos principais**:
  - `id` - Identificador único
  - `nome` - Nome da conta (ex: "Conta Corrente")
  - `saldo` - Saldo atual
  - `usuarioId` - Dono da conta
- **Arquitetura**: MODEL
- **Analogia**: Extrato de uma conta bancária

#### `model/Categoria.java`
- **O que faz**: Representa uma categoria de transação
- **Atributos principais**:
  - `id` - Identificador único
  - `nome` - Nome da categoria (ex: "Alimentação")
  - `cor` - Cor em hexadecimal (ex: "#FF5733")
  - `tipo` - "receita" ou "despesa"
- **Arquitetura**: MODEL
- **Analogia**: Etiqueta para organizar gastos

#### `model/Movimentacao.java`
- **O que faz**: Representa uma transação financeira
- **Atributos principais**:
  - `id` - Identificador único
  - `valor` - Valor da transação
  - `data` - Data da transação
  - `descricao` - Descrição opcional
  - `contaId` - Conta relacionada
  - `categoriaId` - Categoria relacionada
  - `tipo` - "receita" ou "despesa"
- **Arquitetura**: MODEL
- **Analogia**: Comprovante de pagamento ou depósito

---

### 👁️ VIEW - Interface Gráfica

#### `view/LoginView.java`
- **O que faz**: Tela de login para administradores
- **Elementos visuais**:
  - Campo de texto para email
  - Campo de senha
  - Botão "Entrar"
  - Botão "Registrar Admin"
- **Funcionalidades**:
  - Captura email e senha digitados
  - Valida se campos estão preenchidos
  - Chama `AuthController.login()` quando clica em "Entrar"
  - Mostra mensagens de erro se login falhar
  - Abre `AdminDashboardView` se login for bem-sucedido
- **Arquitetura**: VIEW
- **Analogia**: Porta de entrada com interfone

#### `view/AdminDashboardView.java`
- **O que faz**: Tela principal do administrador após login
- **Elementos visuais**:
  - Tabela com lista de todos os usuários
  - Colunas: ID, Nome, Email, Tipo, Data de Criação
  - Botões: Editar, Alterar Senha, Excluir
  - Menu: Perfil, Atualizar, Sair
- **Funcionalidades**:
  - Lista todos os usuários do sistema
  - Permite selecionar um usuário
  - Abrir diálogo de edição
  - Alterar senha de usuários
  - Excluir usuários
  - Atualizar lista
  - Editar perfil do próprio admin
- **Arquitetura**: VIEW
- **Analogia**: Painel de controle de um prédio

#### `view/EditarUsuarioDialog.java`
- **O que faz**: Janela popup para editar dados de um usuário
- **Elementos visuais**:
  - Campo para nome
  - Campo para email
  - Botão "Salvar"
  - Botão "Cancelar"
- **Funcionalidades**:
  - Mostra dados atuais do usuário
  - Permite alterar nome e email
  - Valida se campos estão preenchidos
  - Chama `FinanceController.atualizarUsuario()` ao salvar
  - Fecha janela após salvar
- **Arquitetura**: VIEW
- **Analogia**: Formulário de atualização cadastral

---

### 🎮 CONTROLLER - Lógica de Controle

#### `controller/AuthController.java`
- **O que faz**: Controla toda a parte de autenticação e gerenciamento de usuários
- **Responsabilidades**:
  - Gerenciar conexão com servidor
  - Fazer login de administradores
  - Registrar novos administradores
  - Fazer logout
  - Listar todos os usuários
  - Atualizar dados de usuários
  - Alterar senhas de usuários
  - Excluir usuários
- **Métodos principais**:
  - `conectarServidor()` - Conecta ao servidor
  - `login(email, senha)` - Faz login
  - `registrar(nome, email, senha)` - Cadastra admin
  - `logout()` - Desconecta
  - `listarUsuarios()` - Busca todos os usuários
  - `atualizarUsuario(id, nome, email)` - Atualiza dados
  - `atualizarSenhaUsuario(id, novaSenha)` - Muda senha
  - `deletarUsuario(id)` - Remove usuário
- **Como funciona**:
  - Usa `NetworkClient` para se comunicar com servidor
  - Envia comandos no formato: `COMANDO|parametro1|parametro2`
  - Recebe respostas no formato: `STATUS|dados`
  - Processa respostas e retorna resultados para a View
- **Arquitetura**: CONTROLLER
- **Analogia**: Gerente que coordena todas as operações

#### `controller/FinanceController.java`
- **O que faz**: Controla operações financeiras (menos usado no Desktop Admin)
- **Responsabilidades**:
  - Gerenciar contas
  - Gerenciar categorias
  - Gerenciar movimentações
- **Arquitetura**: CONTROLLER
- **Nota**: Este controller é mais usado no app mobile

---

### 🔧 UTIL - Utilitários

#### `util/NetworkClient.java`
- **O que faz**: Gerencia a conexão de rede com o servidor
- **Responsabilidades**:
  - Conectar ao servidor (localhost:8080)
  - Manter conexão aberta
  - Enviar comandos (String)
  - Receber respostas (String)
  - Fechar conexão
- **Como funciona**:
  - Usa `Socket` Java para conexão TCP/IP
  - Usa `BufferedReader` e `PrintWriter` para comunicação
  - Cada comando é uma linha de texto
- **Métodos principais**:
  - `connect()` - Estabelece conexão
  - `sendCommand(comando)` - Envia comando
  - `receiveResponse()` - Recebe resposta
  - `disconnect()` - Fecha conexão
  - `isConnected()` - Verifica se está conectado
- **Arquitetura**: UTIL (Utilitário)
- **Analogia**: Telefone que liga para o servidor

---

## 🖥️ Servidor Java

### 📂 Localização: `DESKTOP VERSION/ServidorFinanza/src/`

### 🏠 Arquivo Principal

#### `MainServidor.java`
- **Caminho**: `DESKTOP VERSION/ServidorFinanza/src/MainServidor.java`
- **Tipo**: Ponto de entrada do servidor
- **O que faz**: Inicia o servidor Finanza
- **Como funciona**:
  - Cria uma instância de `FinanzaServer`
  - Chama `start()` para começar a escutar conexões
  - Exibe mensagem "Servidor Finanza iniciado..."
- **Analogia**: Porteiro que abre as portas do prédio

---

### 📦 MODEL - Modelos de Dados

#### `model/Usuario.java`
- **O que faz**: Representa um usuário no servidor
- **Atributos principais**:
  - `id` - Identificador único
  - `nome` - Nome completo
  - `email` - Email único
  - `senhaHash` - Senha criptografada (SHA-256)
  - `tipoUsuario` - "admin" ou "usuario"
  - `dataCriacao` - Data de cadastro
- **Constantes**:
  - `TIPO_ADMIN` = "admin"
  - `TIPO_USUARIO` = "usuario"
- **Diferença do cliente**: Tem `senhaHash` em vez de senha em texto
- **Arquitetura**: MODEL
- **Analogia**: Registro de funcionário em um RH

#### `model/Conta.java`
- **O que faz**: Representa uma conta bancária no servidor
- **Igual ao cliente**: Mesmos atributos
- **Arquitetura**: MODEL

#### `model/Categoria.java`
- **O que faz**: Representa uma categoria no servidor
- **Igual ao cliente**: Mesmos atributos
- **Arquitetura**: MODEL

#### `model/Movimentacao.java`
- **O que faz**: Representa uma transação no servidor
- **Igual ao cliente**: Mesmos atributos
- **Arquitetura**: MODEL

---

### 🗄️ DAO - Acesso a Dados

#### `dao/UsuarioDAO.java`
- **O que faz**: Gerencia usuários no banco de dados MySQL
- **Responsabilidades**: Executar operações CRUD (Create, Read, Update, Delete)
- **Métodos principais**:
  - `inserir(Usuario)` - INSERT INTO usuario
  - `buscarPorEmail(email)` - SELECT WHERE email = ?
  - `buscarPorId(id)` - SELECT WHERE id = ?
  - `atualizar(Usuario)` - UPDATE usuario SET ...
  - `atualizarSenha(id, senhaHash)` - UPDATE senha
  - `deletar(id)` - DELETE FROM usuario
  - `listarTodos()` - SELECT * FROM usuario
- **Como funciona**:
  - Usa `DatabaseUtil.getConnection()` para obter conexão
  - Cria `PreparedStatement` para evitar SQL Injection
  - Executa SQL no MySQL
  - Retorna objetos `Usuario` ou listas
- **Arquitetura**: DAO (Data Access Object)
- **Analogia**: Bibliotecário que busca e guarda livros

#### `dao/ContaDAO.java`
- **O que faz**: Gerencia contas no banco de dados
- **Métodos principais**:
  - `inserir(Conta)` - Adiciona nova conta
  - `buscarPorId(id)` - Busca conta específica
  - `listarPorUsuario(usuarioId)` - Contas de um usuário
  - `atualizar(Conta)` - Atualiza dados da conta
  - `deletar(id)` - Remove conta
- **Tabela**: `conta`
- **Arquitetura**: DAO

#### `dao/CategoriaDAO.java`
- **O que faz**: Gerencia categorias no banco de dados
- **Métodos principais**:
  - `inserir(Categoria)` - Adiciona categoria
  - `buscarPorId(id)` - Busca categoria
  - `listarTodas()` - Lista todas
  - `listarPorTipo(tipo)` - Filtra por receita/despesa
  - `atualizar(Categoria)` - Atualiza categoria
  - `deletar(id)` - Remove categoria
- **Tabela**: `categoria`
- **Arquitetura**: DAO

#### `dao/MovimentacaoDAO.java`
- **O que faz**: Gerencia transações no banco de dados
- **Métodos principais**:
  - `inserir(Movimentacao)` - Adiciona transação
  - `buscarPorId(id)` - Busca transação
  - `listarPorUsuario(usuarioId)` - Transações do usuário
  - `listarPorConta(contaId)` - Transações da conta
  - `listarPorPeriodo(inicio, fim)` - Filtra por data
  - `atualizar(Movimentacao)` - Atualiza transação
  - `deletar(id)` - Remove transação
- **Tabela**: `lancamento`
- **Arquitetura**: DAO

---

### 🌐 SERVER - Servidor e Protocolo

#### `server/FinanzaServer.java`
- **O que faz**: Servidor principal que escuta conexões
- **Responsabilidades**:
  - Iniciar ServerSocket na porta 8080
  - Aceitar conexões de múltiplos clientes
  - Para cada cliente, criar thread com ClientHandler
  - Gerenciar conexões simultâneas
- **Como funciona**:
  1. Valida conexão com MySQL
  2. Cria `ServerSocket` na porta 8080
  3. Entra em loop infinito:
     - Aguarda cliente se conectar (`accept()`)
     - Cria novo `ClientHandler` em thread separada
     - Volta a aguardar próximo cliente
- **Métodos principais**:
  - `start()` - Inicia o servidor
  - `stop()` - Para o servidor (se implementado)
- **Arquitetura**: SERVER
- **Analogia**: Central de atendimento que distribui ligações

#### `server/ClientHandler.java`
- **O que faz**: Gerencia comunicação com UM cliente específico
- **Responsabilidades**:
  - Receber comandos do cliente
  - Processar comandos
  - Enviar respostas
  - Manter estado da sessão (usuário logado)
  - Fechar conexão quando cliente desconectar
- **Como funciona**:
  1. Recebe conexão Socket do FinanzaServer
  2. Entra em loop lendo comandos:
     - Lê linha de comando
     - Passa para `Protocol.processCommand()`
     - Recebe resposta do Protocol
     - Envia resposta de volta ao cliente
  3. Loop continua até cliente desconectar
- **Métodos principais**:
  - `run()` - Executa em thread (implementa Runnable)
  - Loop de leitura/processamento/resposta
- **Arquitetura**: SERVER
- **Analogia**: Atendente dedicado para cada cliente

#### `server/Protocol.java`
- **O que faz**: Define e processa o protocolo de comunicação
- **Responsabilidades**:
  - Definir formato dos comandos
  - Processar cada tipo de comando
  - Chamar DAOs apropriados
  - Formatar respostas
  - Validar sessões e permissões
- **Comandos suportados**:
  - **Autenticação**: LOGIN, REGISTER, LOGOUT, CHANGE_PASSWORD
  - **Usuários**: LIST_USERS, UPDATE_USER, UPDATE_USER_PASSWORD, DELETE_USER
  - **Contas**: LIST_CONTAS, ADD_CONTA, UPDATE_CONTA, DELETE_CONTA
  - **Categorias**: LIST_CATEGORIAS, ADD_CATEGORIA, UPDATE_CATEGORIA, DELETE_CATEGORIA
  - **Movimentações**: LIST_MOVIMENTACOES, ADD_MOVIMENTACAO, UPDATE_MOVIMENTACAO, DELETE_MOVIMENTACAO
  - **Dashboard**: GET_DASHBOARD, GET_PERFIL, UPDATE_PERFIL
- **Formato dos comandos**: `COMANDO|param1|param2|param3`
- **Formato das respostas**: `STATUS|dados` ou `ERROR|mensagem`
- **Como funciona**:
  1. Recebe comando completo
  2. Separa por "|" (pipe)
  3. Identifica o comando (primeira parte)
  4. Extrai parâmetros (partes seguintes)
  5. Chama DAO correspondente
  6. Formata e retorna resposta
- **Exemplo de processamento**:
  ```
  Comando: LOGIN|usuario@email.com|senha123|mobile
  1. Identifica comando: LOGIN
  2. Parâmetros: email, senha, tipo
  3. Chama: UsuarioDAO.buscarPorEmail()
  4. Valida senha com SecurityUtil
  5. Retorna: OK|id|nome|email|tipo
  ```
- **Arquitetura**: SERVER (Protocolo)
- **Analogia**: Intérprete que entende diferentes idiomas (comandos)

---

### 🔧 UTIL - Utilitários

#### `util/DatabaseUtil.java`
- **O que faz**: Gerencia conexões com banco de dados MySQL
- **Responsabilidades**:
  - Fornecer conexão com MySQL
  - Gerenciar pool de conexões (se implementado)
  - Inicializar tabelas do banco
  - Configurar parâmetros de conexão
- **Configuração padrão**:
  - URL: `jdbc:mysql://localhost:3306/finanza`
  - Usuário: `root`
  - Senha: (vazia ou configurável)
- **Métodos principais**:
  - `getConnection()` - Retorna conexão JDBC
  - `testConnection()` - Testa se consegue conectar
  - `initDatabase()` - Cria tabelas se não existirem
- **Como funciona**:
  - Usa `DriverManager.getConnection()`
  - Cada DAO chama `getConnection()` quando precisa
- **Arquitetura**: UTIL
- **Analogia**: Chaveiro que fornece chaves para acessar o cofre

#### `util/SecurityUtil.java`
- **O que faz**: Funções de segurança e criptografia
- **Responsabilidades**:
  - Gerar hash de senhas
  - Validar senhas (comparar hash)
  - Outras funções de segurança
- **Como funciona**:
  - Usa algoritmo SHA-256 para hash
  - Hash é irreversível (não dá pra descobrir senha original)
- **Métodos principais**:
  - `hashPassword(senha)` - Gera hash SHA-256
  - `verifyPassword(senha, hash)` - Compara senha com hash
- **Arquitetura**: UTIL
- **Analogia**: Cofre que transforma senha em código secreto

---

## 📱 Mobile Android

### 📂 Localização: `app/src/main/java/com/example/finanza/`

### 🏠 Arquivo Principal

#### `MainActivity.java`
- **Caminho**: `app/src/main/java/com/example/finanza/MainActivity.java`
- **Tipo**: Activity principal do app
- **O que faz**: Ponto de entrada do app Android
- **Como funciona**:
  - Verifica se usuário está logado
  - Se sim: abre MenuActivity
  - Se não: abre LoginActivity
- **Arquitetura**: Não faz parte do MVVM, é inicializador
- **Analogia**: Recepção que direciona para o lugar certo

---

### 📦 MODEL - Modelos de Dados

#### `model/Usuario.java`
- **O que faz**: Representa usuário no app Android
- **Anotações Room**: `@Entity(tableName = "usuario")`
- **Atributos**: id, nome, email, senha, tipo
- **Diferença do servidor**: Armazena no SQLite local
- **Arquitetura**: MODEL

#### `model/Conta.java`
- **O que faz**: Representa conta no app
- **Anotações Room**: `@Entity(tableName = "conta")`
- **Arquitetura**: MODEL

#### `model/Categoria.java`
- **O que faz**: Representa categoria no app
- **Anotações Room**: `@Entity(tableName = "categoria")`
- **Arquitetura**: MODEL

#### `model/Lancamento.java`
- **O que faz**: Representa transação no app
- **Anotações Room**: `@Entity(tableName = "lancamento")`
- **Campos extras**: `sincronizado` (boolean) - indica se já foi sincronizado com servidor
- **Arquitetura**: MODEL

---

### 🗄️ DB - Banco de Dados Local (SQLite)

#### `db/AppDatabase.java`
- **O que faz**: Configura banco de dados SQLite local do app
- **Responsabilidades**:
  - Definir versão do banco
  - Listar entidades (tabelas)
  - Fornecer DAOs
  - Gerenciar migrações
- **Como funciona**:
  - Usa Room Database (biblioteca Android)
  - Padrão Singleton (só existe uma instância)
- **Entidades**: Usuario, Conta, Categoria, Lancamento
- **Arquitetura**: DATABASE
- **Analogia**: Arquivo local no celular para trabalhar offline

#### `db/UsuarioDao.java`
- **O que faz**: DAO para usuários no SQLite local
- **Métodos** (com anotações Room):
  - `@Insert` - Insere usuário
  - `@Update` - Atualiza usuário
  - `@Delete` - Remove usuário
  - `@Query` - Busca usuários
- **Arquitetura**: DAO (local)

#### `db/ContaDao.java`
- **O que faz**: DAO para contas no SQLite local
- **Métodos**: Insert, Update, Delete, Query
- **Arquitetura**: DAO (local)

#### `db/CategoriaDao.java`
- **O que faz**: DAO para categorias no SQLite local
- **Métodos**: Insert, Update, Delete, Query
- **Arquitetura**: DAO (local)

#### `db/LancamentoDao.java`
- **O que faz**: DAO para transações no SQLite local
- **Métodos**: Insert, Update, Delete, Query
- **Queries específicas**:
  - Buscar por período
  - Buscar por conta
  - Buscar não sincronizados
- **Arquitetura**: DAO (local)

---

### 📱 UI - Telas (Activities)

#### `ui/LoginActivity.java`
- **O que faz**: Tela de login do usuário
- **Elementos**:
  - EditText para email
  - EditText para senha
  - Button "Entrar"
  - TextView "Criar conta" (vai para RegisterActivity)
- **Funcionalidades**:
  - Validar campos preenchidos
  - Chamar `AuthManager.login()`
  - Se sucesso: salvar sessão e ir para MenuActivity
  - Se erro: mostrar mensagem
- **Arquitetura**: VIEW (Activity)

#### `ui/RegisterActivity.java`
- **O que faz**: Tela de cadastro de novo usuário
- **Elementos**:
  - EditText para nome
  - EditText para email
  - EditText para senha
  - EditText para confirmar senha
  - Button "Registrar"
- **Funcionalidades**:
  - Validar campos
  - Validar formato de email
  - Validar se senhas coincidem
  - Chamar `AuthManager.register()`
  - Se sucesso: ir para LoginActivity
- **Arquitetura**: VIEW (Activity)

#### `ui/MenuActivity.java`
- **O que faz**: Menu principal / Dashboard do app
- **Elementos**:
  - CardViews com resumo financeiro
  - Saldo total
  - Receitas do mês
  - Despesas do mês
  - Botões: Contas, Categorias, Movimentações, Perfil
  - Menu: Sincronizar, Configurações, Sair
- **Funcionalidades**:
  - Buscar e exibir resumo financeiro
  - Navegar para outras telas
  - Iniciar sincronização
- **Arquitetura**: VIEW (Activity)

#### `ui/AccountsActivity.java`
- **O que faz**: Gerenciar contas bancárias
- **Elementos**:
  - RecyclerView com lista de contas
  - FloatingActionButton "+" para adicionar
  - Cada item mostra: nome, saldo, botões editar/excluir
- **Funcionalidades**:
  - Listar contas do banco local
  - Adicionar nova conta (diálogo)
  - Editar conta existente
  - Excluir conta
  - Sincronizar com servidor
- **Arquitetura**: VIEW (Activity)

#### `ui/CategoriaActivity.java`
- **O que faz**: Gerenciar categorias de transações
- **Elementos**:
  - Tabs: Receitas / Despesas
  - RecyclerView com categorias
  - FloatingActionButton "+" para adicionar
  - Cada item mostra: nome, cor, botões editar/excluir
- **Funcionalidades**:
  - Listar categorias por tipo
  - Adicionar categoria
  - Editar categoria
  - Excluir categoria
  - Escolher cor
- **Arquitetura**: VIEW (Activity)

#### `ui/MovementsActivity.java`
- **O que faz**: Gerenciar transações (receitas e despesas)
- **Elementos**:
  - Filtros: período, conta, categoria
  - RecyclerView com transações
  - FloatingActionButton "+" para adicionar
  - Cada item mostra: descrição, valor, data, categoria, conta
- **Funcionalidades**:
  - Listar transações com filtros
  - Adicionar transação
  - Editar transação
  - Excluir transação
  - Filtrar por data/conta
- **Arquitetura**: VIEW (Activity)

#### `ui/ProfileActivity.java`
- **O que faz**: Visualizar e editar perfil do usuário
- **Elementos**:
  - EditText para nome
  - EditText para email
  - Button "Salvar"
  - Button "Alterar Senha"
- **Funcionalidades**:
  - Mostrar dados atuais
  - Editar nome e email
  - Abrir diálogo para mudar senha
  - Sincronizar alterações com servidor
- **Arquitetura**: VIEW (Activity)

#### `ui/SettingsActivity.java`
- **O que faz**: Configurações do app
- **Elementos**:
  - Switch para sincronização automática
  - Button "Sincronizar Agora"
  - Button "Limpar Cache"
  - Button "Sobre"
  - Button "Sair"
- **Funcionalidades**:
  - Ativar/desativar sync automático
  - Forçar sincronização manual
  - Fazer logout
- **Arquitetura**: VIEW (Activity)

---

### 🌐 NETWORK - Comunicação com Servidor

#### `network/ServerClient.java`
- **O que faz**: Cliente Socket que se conecta ao servidor
- **Responsabilidades**:
  - Estabelecer conexão TCP/IP com servidor
  - Enviar comandos
  - Receber respostas
  - Gerenciar timeout
- **Como funciona**:
  - Usa `Socket` Java em thread separada (para não travar UI)
  - Conecta em: `servidor_ip:8080`
- **Métodos principais**:
  - `connect(ip)` - Conecta ao servidor
  - `sendCommand(comando)` - Envia comando
  - `receiveResponse()` - Recebe resposta
  - `disconnect()` - Fecha conexão
- **Arquitetura**: NETWORK
- **Analogia**: Telefone celular fazendo ligação

#### `network/Protocol.java`
- **O que faz**: Define comandos do protocolo (lado do cliente)
- **Responsabilidades**:
  - Constantes com nomes dos comandos
  - Métodos para montar comandos
  - Separadores e formatos
- **Comandos**: Mesmos do servidor (LOGIN, REGISTER, etc.)
- **Arquitetura**: NETWORK

#### `network/AuthManager.java`
- **O que faz**: Gerencia autenticação no app
- **Responsabilidades**:
  - Fazer login
  - Fazer registro
  - Fazer logout
  - Alterar senha
  - Manter sessão do usuário
  - Salvar token/sessão localmente
- **Como funciona**:
  - Usa `ServerClient` para comunicar
  - Salva sessão em SharedPreferences
  - Fornece usuário logado para outras telas
- **Métodos principais**:
  - `login(email, senha)` - Autentica
  - `register(nome, email, senha)` - Cadastra
  - `logout()` - Encerra sessão
  - `isLoggedIn()` - Verifica se está logado
  - `getCurrentUser()` - Retorna usuário atual
- **Arquitetura**: NETWORK (Manager)

#### `network/SyncService.java`
- **O que faz**: Serviço de sincronização básico
- **Responsabilidades**:
  - Sincronizar dados locais com servidor
  - Enviar dados novos/alterados
  - Baixar atualizações do servidor
  - Resolver conflitos básicos
- **Como funciona**:
  - Roda em background (Service Android)
  - Verifica periodicamente se há dados para sincronizar
  - Marca dados como sincronizados após sucesso
- **Arquitetura**: NETWORK (Service)

#### `network/EnhancedSyncService.java`
- **O que faz**: Serviço de sincronização avançado
- **Responsabilidades**:
  - Tudo do SyncService, mas melhorado
  - Sincronização incremental (só o que mudou)
  - Fila de sincronização
  - Retry em caso de falha
  - Sincronização inteligente
- **Melhorias**:
  - Mais eficiente
  - Menos uso de dados
  - Mais robusto contra erros
- **Arquitetura**: NETWORK (Service)

#### `network/ConflictResolutionManager.java`
- **O que faz**: Resolve conflitos de sincronização
- **Responsabilidades**:
  - Detectar conflitos (mesmo dado alterado no app e servidor)
  - Decidir qual versão manter
  - Aplicar estratégia de resolução
- **Estratégias**:
  - Server Wins (servidor tem prioridade)
  - Client Wins (cliente tem prioridade)
  - Last Modified Wins (última modificação vence)
  - Manual (pergunta ao usuário)
- **Arquitetura**: NETWORK (Manager)

---

### 🔧 UTIL - Utilitários

#### `util/DataIntegrityValidator.java`
- **O que faz**: Valida integridade e consistência dos dados
- **Responsabilidades**:
  - Validar formatos (email, valores)
  - Verificar consistência (saldos, datas)
  - Garantir dados válidos antes de salvar
- **Validações**:
  - Email válido
  - Valor numérico positivo
  - Datas válidas
  - Campos obrigatórios preenchidos
- **Arquitetura**: UTIL

---

## 📊 Resumo Comparativo

### Desktop Admin vs Mobile Android

| Aspecto | Desktop Admin | Mobile Android |
|---------|---------------|----------------|
| **Arquitetura** | MVC | MVVM |
| **Interface** | Swing (Java) | Activities (Android) |
| **Banco Local** | Não tem | SQLite com Room |
| **Usuários** | Só administradores | Usuários comuns |
| **Funcionalidade** | Gerenciar usuários | Controle financeiro completo |
| **Offline** | Não funciona | Funciona offline com sync |

### Servidor vs Cliente

| Aspecto | Servidor | Cliente (Desktop/Mobile) |
|---------|----------|--------------------------|
| **Model** | Tem senhaHash | Não armazena senha |
| **DAO** | Acessa MySQL | Desktop: não tem / Mobile: SQLite |
| **Protocol** | Processa comandos | Envia comandos |
| **Responsabilidade** | Única fonte da verdade | Consulta e envia dados |

---

## 🎯 Fluxo de Dados Completo

### Exemplo: Adicionar Transação no Mobile

```
1. MovementsActivity.java (UI)
   ↓ Usuário preenche e clica "Salvar"
   
2. Validação local
   ↓ DataIntegrityValidator.java

3. Salva no SQLite local
   ↓ LancamentoDao.java (db)

4. Verifica conexão
   ↓ ServerClient.java (network)

5. Se online: envia para servidor
   ↓ Protocol.java monta comando
   ↓ "ADD_MOVIMENTACAO|50.00|2024-11-04|Almoço|1|2|despesa"

6. Servidor recebe
   ↓ FinanzaServer.java aceita conexão
   ↓ ClientHandler.java lê comando

7. Processa comando
   ↓ Protocol.java (servidor) identifica ADD_MOVIMENTACAO
   
8. Salva no MySQL
   ↓ MovimentacaoDAO.java (servidor)
   ↓ INSERT INTO lancamento...

9. Resposta
   ↓ Protocol.java formata: "OK|123"
   ↓ ClientHandler.java envia

10. App recebe
    ↓ ServerClient.java
    ↓ Marca como sincronizado no SQLite
    ↓ LancamentoDao.java update

11. UI atualiza
    ↓ MovementsActivity.java
    ↓ RecyclerView mostra novo item
```

---

## 💡 Dicas para Estudar o Código

### Por onde começar?

1. **Iniciantes**: Comece pelos Models
   - São os mais simples
   - Apenas classes com atributos
   
2. **Intermediário**: Estude as Views
   - Veja como interfaces são criadas
   - Entenda componentes visuais

3. **Avançado**: Explore Controllers e DAOs
   - Entenda a lógica de negócio
   - Veja como dados são salvos

4. **Expert**: Analise Server e Protocol
   - Compreenda comunicação em rede
   - Estude arquitetura distribuída

### Como ler um arquivo Java?

1. Veja o **pacote** (`package`) - mostra onde está
2. Veja os **imports** - mostra dependências
3. Leia os **comentários** no topo - explicação geral
4. Veja os **atributos** - dados que a classe tem
5. Analise os **métodos** - o que a classe faz

---

## 📞 Glossário Técnico

- **Socket**: Conexão de rede entre dois computadores
- **TCP/IP**: Protocolo de comunicação da internet
- **SHA-256**: Algoritmo de criptografia de senhas
- **JDBC**: Biblioteca Java para conexão com bancos de dados
- **Room**: Biblioteca Android para SQLite
- **Thread**: Linha de execução paralela
- **DAO**: Classe especializada em banco de dados
- **Activity**: Tela no Android
- **PreparedStatement**: Comando SQL seguro (evita SQL Injection)
- **Singleton**: Padrão que garante só uma instância de uma classe

---

**🎓 Conclusão**: Todos os arquivos trabalham juntos como uma orquestra. Cada um tem sua função específica, mas o resultado final é um sistema completo e funcional!
