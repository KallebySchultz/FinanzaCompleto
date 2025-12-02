# 🏗️ ARQUITETURA DE COMUNICAÇÃO DO SISTEMA FINANZA

> Documentação completa da comunicação Cliente-Servidor, Mobile-Servidor, protocolos de rede, configuração de banco de dados e fluxo de dados do sistema Finanza.

---

## 📋 Índice

1. [Visão Geral da Arquitetura](#1-visão-geral-da-arquitetura)
2. [Servidor - Onde Escuta e Como Recebe](#2-servidor---onde-escuta-e-como-recebe)
3. [Comunicação Desktop → Servidor](#3-comunicação-desktop--servidor)
4. [Comunicação Mobile → Servidor](#4-comunicação-mobile--servidor)
5. [Protocolo de Comunicação](#5-protocolo-de-comunicação)
6. [Banco de Dados e Conectores](#6-banco-de-dados-e-conectores)
7. [Sincronização de Dados](#7-sincronização-de-dados)
8. [Diagrama Completo de Arquitetura](#8-diagrama-completo-de-arquitetura)

---

## 1. Visão Geral da Arquitetura

O sistema Finanza utiliza uma **arquitetura cliente-servidor centralizada** com os seguintes componentes principais:

### 🧩 Componentes do Sistema

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                           ARQUITETURA FINANZA                                     │
├──────────────────────────────────────────────────────────────────────────────────┤
│                                                                                   │
│  ┌─────────────────────┐     ┌─────────────────────┐                             │
│  │   CLIENTE DESKTOP   │     │   CLIENTE MOBILE    │                             │
│  │    (Java Swing)     │     │     (Android)       │                             │
│  │                     │     │                     │                             │
│  │  • NetworkClient    │     │  • ServerClient     │                             │
│  │  • AuthController   │     │  • AuthManager      │                             │
│  │  • FinanceController│     │  • SyncService      │                             │
│  │  • Views (UI)       │     │  • Activities (UI)  │                             │
│  └──────────┬──────────┘     └──────────┬──────────┘                             │
│             │                           │                                         │
│             │       TCP/IP Socket       │                                         │
│             │        Porta 8080         │                                         │
│             └───────────┬───────────────┘                                         │
│                         │                                                         │
│                         ▼                                                         │
│           ┌─────────────────────────────┐                                        │
│           │        SERVIDOR             │                                        │
│           │      FinanzaServer          │                                        │
│           │      (Java TCP/IP)          │                                        │
│           │                             │                                        │
│           │  • Escuta porta 8080        │                                        │
│           │  • ClientHandler (Thread)   │                                        │
│           │  • Protocol (Parsing)       │                                        │
│           │  • DAOs (Acesso BD)         │                                        │
│           │  • SecurityUtil             │                                        │
│           └──────────────┬──────────────┘                                        │
│                          │                                                        │
│                          │ JDBC                                                   │
│                          │ mysql-connector-java                                   │
│                          ▼                                                        │
│           ┌─────────────────────────────┐                                        │
│           │      BANCO DE DADOS         │                                        │
│           │         MySQL               │                                        │
│           │     localhost:3306          │                                        │
│           │     finanza_db              │                                        │
│           └─────────────────────────────┘                                        │
│                                                                                   │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### 📂 Localização dos Arquivos Principais

| Componente | Localização | Função |
|------------|-------------|--------|
| **Servidor** | `DESKTOP VERSION/ServidorFinanza/src/` | Processa todas as requisições |
| **Cliente Desktop** | `DESKTOP VERSION/ClienteFinanza/src/` | Interface administrativa |
| **Cliente Mobile** | `app/src/main/java/com/example/finanza/` | App Android para usuários |
| **Banco de Dados** | `database/finanza_completo.sql` | Script de criação do BD |

---

## 2. Servidor - Onde Escuta e Como Recebe

### 🔌 Configuração de Rede do Servidor

O servidor Finanza é implementado em **Java puro** usando **Sockets TCP/IP** e escuta conexões na seguinte configuração:

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/server/FinanzaServer.java`

```java
// Linha 46: Porta de escuta definida como constante
private static final int PORT = 8080;

// Linha 155: Criação do ServerSocket que escuta na porta
serverSocket = new ServerSocket(PORT);
```

### 📍 Onde o Servidor Escuta

| Configuração | Valor | Descrição |
|--------------|-------|-----------|
| **Endereço** | `0.0.0.0` (todas interfaces) | Aceita conexões de qualquer IP |
| **Porta** | `8080` | Porta TCP padrão do sistema |
| **Protocolo** | TCP/IP | Conexão persistente, confiável |
| **Tipo de Socket** | `java.net.ServerSocket` | Socket TCP bloqueante |

### 🔄 Fluxo de Inicialização do Servidor

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                    FLUXO DE INICIALIZAÇÃO DO SERVIDOR                           │
├────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. MAIN (MainServidor.java)                                                    │
│     └── Cria instância de FinanzaServer                                         │
│     └── Chama server.start()                                                    │
│                                                                                 │
│  2. TESTE DE CONEXÃO COM BANCO (linha 130-136)                                  │
│     └── DatabaseUtil.testConnection()                                           │
│     └── Se falhar: exibe erro e encerra                                         │
│     └── Se sucesso: continua inicialização                                      │
│                                                                                 │
│  3. INICIALIZAÇÃO DO BANCO (linha 144)                                          │
│     └── DatabaseUtil.initializeDatabase()                                       │
│     └── Cria tabelas se não existirem                                           │
│     └── Aplica índices e constraints                                            │
│                                                                                 │
│  4. ABERTURA DO SERVIDOR (linha 155)                                            │
│     └── serverSocket = new ServerSocket(8080)                                   │
│     └── Marca running = true                                                    │
│     └── Exibe: "Servidor Finanza iniciado na porta 8080"                        │
│                                                                                 │
│  5. LOOP PRINCIPAL - ACEITAR CONEXÕES (linha 165-189)                           │
│     └── while (running) {                                                       │
│           Socket clientSocket = serverSocket.accept();  // BLOQUEIA aqui        │
│           ClientHandler handler = new ClientHandler(clientSocket);              │
│           handler.start();  // Nova thread para cliente                         │
│         }                                                                       │
│                                                                                 │
└────────────────────────────────────────────────────────────────────────────────┘
```

### 📨 Como o Servidor Recebe Comandos

O servidor recebe comandos através de **threads dedicadas** chamadas `ClientHandler`. Cada cliente conectado tem sua própria thread.

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java`

```java
// Linha 189-195: Criação dos streams de comunicação
input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
output = new PrintWriter(clientSocket.getOutputStream(), true);

// Linha 202-225: Loop de processamento de comandos
String comando;
while ((comando = input.readLine()) != null) {  // BLOQUEIA esperando comando
    String resposta = processarComando(comando);  // Processa
    output.println(resposta);                     // Envia resposta
}
```

### 🔀 Roteamento de Comandos

O método `processarComando()` (linha 339-472) roteia os comandos para seus processadores específicos:

```java
switch (cmd) {
    case Protocol.CMD_LOGIN:
        return processarLogin(partes);
    case Protocol.CMD_REGISTER:
        return processarRegistro(partes);
    case Protocol.CMD_LIST_CONTAS:
        return processarListContas();
    case Protocol.CMD_ADD_MOVIMENTACAO:
        return processarAddMovimentacao(partes);
    // ... mais 40+ comandos
}
```

---

## 3. Comunicação Desktop → Servidor

### 🖥️ NetworkClient - Cliente de Rede Desktop

**Arquivo:** `DESKTOP VERSION/ClienteFinanza/src/util/NetworkClient.java`

O cliente desktop utiliza comunicação **síncrona** (bloqueante) com o servidor.

```java
// Linhas 10-11: Configuração de conexão
private static final String SERVER_HOST = "localhost";
private static final int SERVER_PORT = 8080;

// Linhas 13-16: Variáveis de conexão
private Socket socket;              // Socket TCP
private BufferedReader input;       // Lê respostas
private PrintWriter output;         // Envia comandos
private boolean connected = false;
```

### 🔗 Fluxo de Conexão Desktop

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                    FLUXO DE CONEXÃO - CLIENTE DESKTOP                           │
├────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. USUÁRIO INICIA APLICAÇÃO                                                    │
│     └── MainCliente.java                                                        │
│     └── Cria NetworkClient                                                      │
│                                                                                 │
│  2. CONECTAR AO SERVIDOR (NetworkClient.connect())                              │
│     │                                                                           │
│     │  try {                                                                    │
│     │      // Linha 23: Cria socket TCP                                         │
│     │      socket = new Socket("localhost", 8080);                              │
│     │                                                                           │
│     │      // Linha 24: Stream de entrada                                       │
│     │      input = new BufferedReader(new InputStreamReader(                    │
│     │          socket.getInputStream()));                                       │
│     │                                                                           │
│     │      // Linha 25: Stream de saída com auto-flush                          │
│     │      output = new PrintWriter(socket.getOutputStream(), true);            │
│     │                                                                           │
│     │      connected = true;                                                    │
│     │  }                                                                        │
│     │                                                                           │
│     └── Retorna: true (sucesso) ou false (falha)                                │
│                                                                                 │
│  3. ENVIAR COMANDO (NetworkClient.sendCommand())                                │
│     │                                                                           │
│     │  // Linha 45: Envia comando                                               │
│     │  output.println(comando);                                                 │
│     │                                                                           │
│     │  // Linha 46: Aguarda resposta (BLOQUEANTE)                               │
│     │  String response = input.readLine();                                      │
│     │                                                                           │
│     └── Retorna: resposta do servidor                                           │
│                                                                                 │
└────────────────────────────────────────────────────────────────────────────────┘
```

### 📡 Exemplo de Comunicação Desktop

```
┌─────────────────────┐                      ┌─────────────────────┐
│   CLIENTE DESKTOP   │                      │      SERVIDOR       │
│   (NetworkClient)   │                      │   (ClientHandler)   │
└─────────┬───────────┘                      └───────────┬─────────┘
          │                                              │
          │  1. socket = new Socket("localhost", 8080)   │
          │ ─────────────────────────────────────────────>│
          │                                              │
          │  2. Conexão estabelecida                     │
          │<─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
          │                                              │
          │  3. output.println("LOGIN|user@email|senha") │
          │ ─────────────────────────────────────────────>│
          │                                              │ Processa comando
          │                                              │ Autentica no BD
          │                                              │
          │  4. "OK|1;João;user@email;usuario"           │
          │<─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
          │                                              │
          │  5. output.println("LIST_CONTAS")            │
          │ ─────────────────────────────────────────────>│
          │                                              │ Busca contas
          │                                              │
          │  6. "OK|1,Nubank,corrente,0,00,1500,50;..."  │
          │<─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
          │                                              │
```

---

## 4. Comunicação Mobile → Servidor

### 📱 ServerClient - Cliente de Rede Mobile

**Arquivo:** `app/src/main/java/com/example/finanza/network/ServerClient.java`

O cliente mobile utiliza comunicação **assíncrona** para não bloquear a thread principal (UI).

```java
// Linhas 65-77: Configuração
private static final int CONNECTION_TIMEOUT = 5000;  // 5 segundos
private static final String PREFS_NAME = "FinanzaServerConfig";
private static final String PREF_HOST = "server_host";
private static final String PREF_PORT = "server_port";

// Linhas 80-99: Variáveis de conexão
private String serverHost;     // IP configurável (SharedPreferences)
private int serverPort;        // Porta configurável
private Socket socket;
private BufferedReader input;
private PrintWriter output;
private boolean connected = false;
```

### 🔗 Fluxo de Conexão Mobile

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                    FLUXO DE CONEXÃO - CLIENTE MOBILE (ANDROID)                  │
├────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. OBTER INSTÂNCIA DO SERVERCLIENT (Singleton)                                 │
│     │                                                                           │
│     │  ServerClient client = ServerClient.getInstance(context);                 │
│     │                                                                           │
│     └── Carrega configurações do SharedPreferences                              │
│         └── Host padrão: "192.168.1.100"                                        │
│         └── Porta padrão: 8080                                                  │
│                                                                                 │
│  2. CONECTAR AO SERVIDOR (Assíncrono via AsyncTask)                             │
│     │                                                                           │
│     │  client.conectar(new ServerCallback<String>() {                           │
│     │      @Override                                                            │
│     │      public void onSuccess(String result) { ... }                         │
│     │      @Override                                                            │
│     │      public void onError(String error) { ... }                            │
│     │  });                                                                      │
│     │                                                                           │
│     │  // Executado em background thread (AsyncTask):                           │
│     │  socket = new Socket();                                                   │
│     │  socket.connect(new InetSocketAddress(host, port), 5000);  // timeout     │
│     │  input = new BufferedReader(new InputStreamReader(...));                  │
│     │  output = new PrintWriter(socket.getOutputStream(), true);                │
│     │                                                                           │
│     └── Callback na UI thread                                                   │
│                                                                                 │
│  3. ENVIAR COMANDO (Assíncrono via AsyncTask)                                   │
│     │                                                                           │
│     │  client.enviarComando("COMANDO|param1", callback);                        │
│     │                                                                           │
│     │  // Background:                                                           │
│     │  output.println(comando);                                                 │
│     │  String response = input.readLine();                                      │
│     │                                                                           │
│     └── Callback com resultado na UI thread                                     │
│                                                                                 │
└────────────────────────────────────────────────────────────────────────────────┘
```

### 📱 Configuração de Servidor no Mobile

O usuário pode configurar o IP e porta do servidor através da tela de configurações:

```java
// Linha 194-207: Configurar servidor
public void configurarServidor(String host, int port) {
    this.serverHost = host;
    this.serverPort = port;
    
    // Persiste nas SharedPreferences
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    prefs.edit()
         .putString(PREF_HOST, host)  // Ex: "192.168.1.100"
         .putInt(PREF_PORT, port)     // Ex: 8080
         .apply();
}
```

### 📡 Exemplo de Comunicação Mobile

```
┌─────────────────────┐                      ┌─────────────────────┐
│   CLIENTE MOBILE    │                      │      SERVIDOR       │
│   (ServerClient)    │                      │   (ClientHandler)   │
└─────────┬───────────┘                      └───────────┬─────────┘
          │                                              │
          │  1. conectar("192.168.1.100", 8080, callback) │
          │     └── AsyncTask em background              │
          │ ─────────────────────────────────────────────>│
          │                                              │
          │  2. callback.onSuccess("Conectado")          │
          │<─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
          │                                              │
          │  3. login("user@email", "senha", callback)   │
          │     └── AsyncTask em background              │
          │     └── Envia: "LOGIN|user@email|senha|mobile"
          │ ─────────────────────────────────────────────>│
          │                                              │ Processa
          │                                              │ Verifica tipo
          │                                              │
          │  4. callback.onSuccess("OK|1;João;...")      │
          │<─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
          │     └── Atualiza UI na main thread           │
          │                                              │
```

### 🔐 Diferença de Autenticação (Admin vs Mobile)

```java
// ClientHandler.java - Linha 477-529: processarLogin()

// Desktop envia: "LOGIN|email|senha|admin"
// Mobile envia:  "LOGIN|email|senha|mobile"

// Servidor valida o tipo de usuário:
if ("admin".equals(tipoCliente) && !usuario.isAdmin()) {
    return Protocol.createResponse(Protocol.STATUS_ACCESS_DENIED, 
        "Acesso negado. Apenas administradores podem acessar o painel desktop.");
}

if ("mobile".equals(tipoCliente) && usuario.isAdmin()) {
    return Protocol.createResponse(Protocol.STATUS_ACCESS_DENIED, 
        "Acesso negado. Administradores não podem acessar o aplicativo mobile.");
}
```

---

## 5. Protocolo de Comunicação

### 📜 Formato dos Comandos

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/server/Protocol.java`

O protocolo utiliza **texto simples** com separadores específicos:

```java
// Separadores definidos (linhas 68-69)
public static final String SEPARATOR = "|";        // Entre comando e parâmetros
public static final String FIELD_SEPARATOR = ";";  // Entre registros/campos
```

### 📤 Formato de Envio (Cliente → Servidor)

```
COMANDO|param1|param2|param3|...

Exemplos:
- LOGIN|joao@email.com|senha123|admin
- REGISTER|João Silva|joao@email.com|senha123|mobile
- ADD_CONTA|Nubank|corrente|1500.00
- ADD_MOVIMENTACAO|150.50|2024-01-15|Supermercado|despesa|1|5
- LIST_CONTAS
- DELETE_MOVIMENTACAO|42
```

### 📥 Formato de Resposta (Servidor → Cliente)

```
STATUS|dados_ou_mensagem

Status possíveis:
- OK                     (sucesso)
- ERROR                  (erro genérico)
- INVALID_CREDENTIALS    (login/senha inválidos)
- USER_EXISTS            (email já cadastrado)
- INVALID_DATA           (dados inválidos)
- ACCESS_DENIED          (sem permissão)

Exemplos:
- OK|1;João Silva;joao@email.com;usuario
- OK|1,Nubank,corrente,0,00,1500,50;2,Poupança,poupanca,1000,00,1200,00
- ERROR|Usuário não está logado
- INVALID_CREDENTIALS|Email ou senha inválidos
```

### 📋 Lista Completa de Comandos

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         COMANDOS DO PROTOCOLO FINANZA                            │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ╔═════════════════════════════════════════════════════════════════════════════╗│
│  ║  AUTENTICAÇÃO                                                               ║│
│  ╠═════════════════════════════════════════════════════════════════════════════╣│
│  ║  LOGIN              │ LOGIN|email|senha|tipo_cliente                        ║│
│  ║  REGISTER           │ REGISTER|nome|email|senha|tipo_cliente                ║│
│  ║  LOGOUT             │ LOGOUT                                                ║│
│  ║  CHANGE_PASSWORD    │ CHANGE_PASSWORD|senha_atual|nova_senha                ║│
│  ║  RESET_PASSWORD     │ RESET_PASSWORD|email                                  ║│
│  ╚═════════════════════════════════════════════════════════════════════════════╝│
│                                                                                  │
│  ╔═════════════════════════════════════════════════════════════════════════════╗│
│  ║  CONTAS                                                                     ║│
│  ╠═════════════════════════════════════════════════════════════════════════════╣│
│  ║  LIST_CONTAS        │ LIST_CONTAS                                           ║│
│  ║  ADD_CONTA          │ ADD_CONTA|nome|tipo|saldo_inicial                     ║│
│  ║  UPDATE_CONTA       │ UPDATE_CONTA|id|nome|tipo|saldo_inicial               ║│
│  ║  DELETE_CONTA       │ DELETE_CONTA|id                                       ║│
│  ╚═════════════════════════════════════════════════════════════════════════════╝│
│                                                                                  │
│  ╔═════════════════════════════════════════════════════════════════════════════╗│
│  ║  CATEGORIAS                                                                 ║│
│  ╠═════════════════════════════════════════════════════════════════════════════╣│
│  ║  LIST_CATEGORIAS      │ LIST_CATEGORIAS                                     ║│
│  ║  LIST_CATEGORIAS_TIPO │ LIST_CATEGORIAS_TIPO|tipo                           ║│
│  ║  ADD_CATEGORIA        │ ADD_CATEGORIA|nome|tipo                             ║│
│  ║  UPDATE_CATEGORIA     │ UPDATE_CATEGORIA|id|nome|tipo                       ║│
│  ║  DELETE_CATEGORIA     │ DELETE_CATEGORIA|id                                 ║│
│  ╚═════════════════════════════════════════════════════════════════════════════╝│
│                                                                                  │
│  ╔═════════════════════════════════════════════════════════════════════════════╗│
│  ║  MOVIMENTAÇÕES                                                              ║│
│  ╠═════════════════════════════════════════════════════════════════════════════╣│
│  ║  LIST_MOVIMENTACOES          │ LIST_MOVIMENTACOES                           ║│
│  ║  LIST_MOVIMENTACOES_PERIODO  │ LIST_MOVIMENTACOES_PERIODO|data_ini|data_fim ║│
│  ║  LIST_MOVIMENTACOES_CONTA    │ LIST_MOVIMENTACOES_CONTA|id_conta            ║│
│  ║  ADD_MOVIMENTACAO            │ ADD_MOVIMENTACAO|valor|data|desc|tipo|       ║│
│  ║                              │     id_conta|id_categoria                     ║│
│  ║  UPDATE_MOVIMENTACAO         │ UPDATE_MOVIMENTACAO|id|valor|data|desc|tipo| ║│
│  ║                              │     id_conta|id_categoria                     ║│
│  ║  DELETE_MOVIMENTACAO         │ DELETE_MOVIMENTACAO|id                       ║│
│  ╚═════════════════════════════════════════════════════════════════════════════╝│
│                                                                                  │
│  ╔═════════════════════════════════════════════════════════════════════════════╗│
│  ║  DASHBOARD E PERFIL                                                         ║│
│  ╠═════════════════════════════════════════════════════════════════════════════╣│
│  ║  GET_DASHBOARD      │ GET_DASHBOARD                                         ║│
│  ║  GET_PERFIL         │ GET_PERFIL                                            ║│
│  ║  UPDATE_PERFIL      │ UPDATE_PERFIL|nome|email                              ║│
│  ╚═════════════════════════════════════════════════════════════════════════════╝│
│                                                                                  │
│  ╔═════════════════════════════════════════════════════════════════════════════╗│
│  ║  COMANDOS ADMIN (apenas para administradores)                               ║│
│  ╠═════════════════════════════════════════════════════════════════════════════╣│
│  ║  LIST_USERS                      │ LIST_USERS                               ║│
│  ║  UPDATE_USER                     │ UPDATE_USER|id|nome|email                ║│
│  ║  UPDATE_USER_PASSWORD            │ UPDATE_USER_PASSWORD|id|nova_senha       ║│
│  ║  DELETE_USER                     │ DELETE_USER|id                           ║│
│  ║  ADMIN_LIST_CONTAS_USER          │ ADMIN_LIST_CONTAS_USER|user_id           ║│
│  ║  ADMIN_LIST_CATEGORIAS_USER      │ ADMIN_LIST_CATEGORIAS_USER|user_id       ║│
│  ║  ADMIN_LIST_MOVIMENTACOES_USER   │ ADMIN_LIST_MOVIMENTACOES_USER|user_id    ║│
│  ║  ADMIN_LIST_ALL_CONTAS           │ ADMIN_LIST_ALL_CONTAS                    ║│
│  ║  ADMIN_LIST_ALL_CATEGORIAS       │ ADMIN_LIST_ALL_CATEGORIAS                ║│
│  ║  ADMIN_LIST_ALL_MOVIMENTACOES    │ ADMIN_LIST_ALL_MOVIMENTACOES             ║│
│  ║  ADMIN_DELETE_CONTA              │ ADMIN_DELETE_CONTA|id                    ║│
│  ║  ADMIN_DELETE_CATEGORIA          │ ADMIN_DELETE_CATEGORIA|id                ║│
│  ║  ADMIN_DELETE_MOVIMENTACAO       │ ADMIN_DELETE_MOVIMENTACAO|id             ║│
│  ╚═════════════════════════════════════════════════════════════════════════════╝│
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Banco de Dados e Conectores

### 🗄️ Servidor - MySQL (Banco Central)

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/util/DatabaseUtil.java`

O servidor utiliza **MySQL** como banco de dados central. A conexão é feita via **JDBC**.

```java
// Linhas 12-14: Configuração do banco
private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";

// Linhas 16-22: Carregamento do driver JDBC
static {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");  // Driver MySQL 8.x
    } catch (ClassNotFoundException e) {
        System.err.println("Driver MySQL não encontrado: " + e.getMessage());
    }
}

// Linhas 29-31: Método para obter conexão
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
}
```

### 📊 Estrutura do Banco MySQL

**Arquivo:** `database/finanza_completo.sql`

```sql
-- Tabela de usuários
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,           -- SHA-256
    tipo_usuario ENUM('admin', 'usuario') NOT NULL DEFAULT 'usuario',
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de contas
CREATE TABLE conta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro') NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela de categorias
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('receita', 'despesa') NOT NULL,
    cor_hex VARCHAR(7) DEFAULT '#808080',
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela de movimentações
CREATE TABLE movimentacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(15,2) NOT NULL,
    data DATE NOT NULL,
    descricao VARCHAR(500),
    tipo ENUM('receita', 'despesa') NOT NULL,
    id_conta INT NOT NULL,
    id_categoria INT NOT NULL,
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conta) REFERENCES conta(id) ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);
```

### 📲 Mobile - Room Database (SQLite Local)

**Arquivo:** `app/src/main/java/com/example/finanza/db/AppDatabase.java`

O mobile utiliza **Room Database** (abstração sobre SQLite) para armazenamento local offline.

```java
// Linhas 42-46: Configuração do Room
@Database(
    entities = {Usuario.class, Conta.class, Categoria.class, Lancamento.class},
    version = 6,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    // Linhas 268-282: Criação do banco (Singleton)
    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class, 
                        "finanza-database"  // Nome do arquivo .db
                    )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()  // ⚠️ Apenas desenvolvimento
                    .build();
            }
        }
        return INSTANCE;
    }
    
    // DAOs disponíveis
    public abstract UsuarioDao usuarioDao();
    public abstract ContaDao contaDao();
    public abstract CategoriaDao categoriaDao();
    public abstract LancamentoDao lancamentoDao();
}
```

### 🔄 Comparação: MySQL vs Room

| Aspecto | MySQL (Servidor) | Room (Mobile) |
|---------|-----------------|---------------|
| **Localização** | Servidor central | Dispositivo local |
| **Driver/Lib** | mysql-connector-java | androidx.room |
| **Tipo** | Banco relacional | SQLite (relacional) |
| **Acesso** | Via JDBC | Via DAOs |
| **Arquivo** | Servidor MySQL | `/data/data/app/databases/finanza-database` |
| **Sincronização** | Fonte da verdade | Sincroniza com servidor |

---

## 7. Sincronização de Dados

### 🔄 Serviço de Sincronização

**Arquivo:** `app/src/main/java/com/example/finanza/network/SyncService.java`

O `SyncService` gerencia a sincronização bidirecional entre o banco local (Room) e o servidor (MySQL).

### 📤 Fluxo de Sincronização

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                    FLUXO DE SINCRONIZAÇÃO MOBILE ↔ SERVIDOR                     │
├────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. VERIFICAÇÃO DE CONEXÃO                                                      │
│     └── Se offline: opera apenas localmente                                     │
│     └── Se online: inicia sincronização                                         │
│                                                                                 │
│  2. AUTENTICAÇÃO NO SERVIDOR                                                    │
│     └── ensureServerAuthentication(usuarioId)                                   │
│     └── Envia credenciais para login no servidor                                │
│                                                                                 │
│  3. UPLOAD - Dados Pendentes → Servidor                                         │
│     └── sincronizarDadosPendentes(usuarioId)                                    │
│         ├── Categorias com syncStatus = 2 (pendente)                            │
│         ├── Contas com syncStatus = 2                                           │
│         └── Lançamentos com syncStatus = 2                                      │
│                                                                                 │
│  4. DOWNLOAD - Servidor → Mobile                                                │
│     └── buscarCategoriasDoServidor() → processarCategoriasDoServidor()          │
│     └── buscarContasDoServidor() → processarContasDoServidor()                  │
│     └── buscarMovimentacoesDoServidor() → processarMovimentacoesDoServidor()    │
│                                                                                 │
│  5. ATUALIZAÇÃO DE STATUS                                                       │
│     └── Marca registros como syncStatus = 1 (sincronizado)                      │
│     └── Atualiza lastSyncTime                                                   │
│                                                                                 │
└────────────────────────────────────────────────────────────────────────────────┘
```

### 📊 Status de Sincronização

```java
// Valores de syncStatus nas entidades
syncStatus = 1  // Sincronizado (sem modificações pendentes)
syncStatus = 2  // Pendente (criado/modificado localmente, não enviado)
```

---

## 8. Diagrama Completo de Arquitetura

```
┌──────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    ARQUITETURA COMPLETA DO SISTEMA FINANZA                                        │
├──────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                                   │
│  ┌───────────────────────────────────────────┐        ┌───────────────────────────────────────────┐              │
│  │         CLIENTE DESKTOP (Java Swing)       │        │           CLIENTE MOBILE (Android)        │              │
│  │                                            │        │                                            │              │
│  │  ┌────────────────────────────────────┐   │        │   ┌────────────────────────────────────┐  │              │
│  │  │           CAMADA DE VIEW            │   │        │   │          CAMADA DE UI              │  │              │
│  │  │  • LoginView.java                   │   │        │   │  • LoginActivity.java              │  │              │
│  │  │  • MainView.java                    │   │        │   │  • RegisterActivity.java           │  │              │
│  │  │  • ContasView.java                  │   │        │   │  • MainActivity.java               │  │              │
│  │  │  • CategoriasView.java              │   │        │   │  • ContasActivity.java             │  │              │
│  │  │  • MovimentacoesView.java           │   │        │   │  • LancamentosActivity.java        │  │              │
│  │  └──────────────┬─────────────────────┘   │        │   └──────────────┬─────────────────────┘  │              │
│  │                 │                          │        │                  │                         │              │
│  │                 ▼                          │        │                  ▼                         │              │
│  │  ┌────────────────────────────────────┐   │        │   ┌────────────────────────────────────┐  │              │
│  │  │         CAMADA DE CONTROLLER        │   │        │   │         CAMADA DE NETWORK          │  │              │
│  │  │  • AuthController.java              │   │        │   │  • AuthManager.java                │  │              │
│  │  │  • FinanceController.java           │   │        │   │  • ServerClient.java               │  │              │
│  │  │  • UserController.java              │   │        │   │  • SyncService.java                │  │              │
│  │  └──────────────┬─────────────────────┘   │        │   │  • Protocol.java                   │  │              │
│  │                 │                          │        │   └──────────────┬─────────────────────┘  │              │
│  │                 ▼                          │        │                  │                         │              │
│  │  ┌────────────────────────────────────┐   │        │                  │                         │              │
│  │  │         CAMADA DE NETWORK           │   │        │                  │                         │              │
│  │  │  • NetworkClient.java               │   │        │   ┌──────────────┴─────────────────────┐  │              │
│  │  │    └── Socket TCP (localhost:8080)  │   │        │   │         CAMADA DE DATABASE         │  │              │
│  │  │    └── BufferedReader/PrintWriter   │   │        │   │  • AppDatabase.java (Room)         │  │              │
│  │  │    └── Comunicação SÍNCRONA         │   │        │   │  • UsuarioDao.java                 │  │              │
│  │  └──────────────┬─────────────────────┘   │        │   │  • ContaDao.java                   │  │              │
│  │                 │                          │        │   │  • CategoriaDao.java               │  │              │
│  │                 │                          │        │   │  • LancamentoDao.java              │  │              │
│  └─────────────────┼──────────────────────────┘        │   │    └── SQLite Local                │  │              │
│                    │                                    │   └────────────────────────────────────┘  │              │
│                    │                                    └───────────────────┬───────────────────────┘              │
│                    │                                                        │                                      │
│                    │           TCP/IP Socket                               │                                      │
│                    │           Protocolo: Texto                            │                                      │
│                    │           Porta: 8080                                 │                                      │
│                    │           Separadores: | e ;                          │                                      │
│                    │                                                        │                                      │
│                    └─────────────────────────┬──────────────────────────────┘                                      │
│                                              │                                                                      │
│                                              ▼                                                                      │
│  ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐│
│  │                                            SERVIDOR FINANZA                                                    ││
│  │                                                                                                                ││
│  │   ┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐  ││
│  │   │                                     CAMADA DE REDE (server/)                                            │  ││
│  │   │                                                                                                         │  ││
│  │   │   ┌─────────────────────────────┐    ┌─────────────────────────────┐    ┌──────────────────────────┐   │  ││
│  │   │   │      FinanzaServer.java     │    │      ClientHandler.java     │    │      Protocol.java       │   │  ││
│  │   │   │                             │    │                             │    │                          │   │  ││
│  │   │   │  • ServerSocket(8080)       │───>│  • Thread por cliente       │───>│  • Parse de comandos     │   │  ││
│  │   │   │  • Accept loop              │    │  • BufferedReader/Writer    │    │  • Formato: CMD|p1|p2    │   │  ││
│  │   │   │  • Cria ClientHandler       │    │  • processarComando()       │    │  • Status: OK/ERROR      │   │  ││
│  │   │   │                             │    │  • 40+ comandos suportados  │    │  • Separadores: | e ;    │   │  ││
│  │   │   └─────────────────────────────┘    └──────────────┬──────────────┘    └──────────────────────────┘   │  ││
│  │   │                                                     │                                                   │  ││
│  │   └─────────────────────────────────────────────────────┼───────────────────────────────────────────────────┘  ││
│  │                                                         │                                                      ││
│  │   ┌─────────────────────────────────────────────────────┼───────────────────────────────────────────────────┐  ││
│  │   │                                     CAMADA DE ACESSO A DADOS (dao/)                                      │  ││
│  │   │                                                     │                                                    │  ││
│  │   │   ┌───────────────────┐   ┌───────────────────┐   ┌───────────────────┐   ┌───────────────────────┐     │  ││
│  │   │   │   UsuarioDAO.java │   │    ContaDAO.java  │   │  CategoriaDAO.java│   │  MovimentacaoDAO.java │     │  ││
│  │   │   │                   │   │                   │   │                   │   │                       │     │  ││
│  │   │   │  • inserir()      │   │  • inserir()      │   │  • inserir()      │   │  • inserir()          │     │  ││
│  │   │   │  • buscarPorId()  │   │  • buscarPorId()  │   │  • buscarPorId()  │   │  • buscarPorId()      │     │  ││
│  │   │   │  • buscarPorEmail()   │  • listarPorUsuario│   │  • listarPorUsuario│   │  • listarPorUsuario() │     │  ││
│  │   │   │  • autenticar()   │   │  • atualizar()    │   │  • atualizar()    │   │  • atualizar()        │     │  ││
│  │   │   │  • atualizar()    │   │  • remover()      │   │  • remover()      │   │  • remover()          │     │  ││
│  │   │   │  • listarTodos()  │   │  • calcularSaldo()│   │  • listarPorTipo()│   │  • calcularTotais()   │     │  ││
│  │   │   └─────────┬─────────┘   └─────────┬─────────┘   └─────────┬─────────┘   └───────────┬───────────┘     │  ││
│  │   │             │                       │                       │                         │                 │  ││
│  │   └─────────────┼───────────────────────┼───────────────────────┼─────────────────────────┼─────────────────┘  ││
│  │                 │                       │                       │                         │                    ││
│  │   ┌─────────────┴───────────────────────┴───────────────────────┴─────────────────────────┴─────────────────┐  ││
│  │   │                                     CAMADA DE UTILITÁRIOS (util/)                                        │  ││
│  │   │                                                                                                          │  ││
│  │   │   ┌────────────────────────────────────────┐    ┌────────────────────────────────────────────┐           │  ││
│  │   │   │          DatabaseUtil.java             │    │            SecurityUtil.java               │           │  ││
│  │   │   │                                        │    │                                            │           │  ││
│  │   │   │  • DB_URL: jdbc:mysql://localhost:3306/│    │  • hashSenha(senha): SHA-256               │           │  ││
│  │   │   │            finanza_db                  │    │  • verificarSenha(senha, hash): boolean    │           │  ││
│  │   │   │  • DB_USER: root                       │    │  • validarEmail(email): boolean            │           │  ││
│  │   │   │  • DB_PASSWORD: ""                     │    │  • validarSenha(senha): boolean            │           │  ││
│  │   │   │  • getConnection(): Connection         │    │                                            │           │  ││
│  │   │   │  • testConnection(): boolean           │    │                                            │           │  ││
│  │   │   │  • initializeDatabase(): void          │    │                                            │           │  ││
│  │   │   └───────────────────┬────────────────────┘    └────────────────────────────────────────────┘           │  ││
│  │   │                       │                                                                                   │  ││
│  │   └───────────────────────┼───────────────────────────────────────────────────────────────────────────────────┘  ││
│  │                           │                                                                                      ││
│  └───────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────┘│
│                              │                                                                                       │
│                              │  JDBC (mysql-connector-java)                                                          │
│                              │                                                                                       │
│                              ▼                                                                                       │
│  ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐  │
│  │                                          BANCO DE DADOS MYSQL                                                  │  │
│  │                                                                                                                │  │
│  │   Servidor: localhost                                                                                          │  │
│  │   Porta: 3306                                                                                                  │  │
│  │   Banco: finanza_db                                                                                            │  │
│  │   Charset: utf8mb4                                                                                             │  │
│  │                                                                                                                │  │
│  │   ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────────┐                   │  │
│  │   │     usuario     │   │      conta      │   │    categoria    │   │    movimentacao     │                   │  │
│  │   ├─────────────────┤   ├─────────────────┤   ├─────────────────┤   ├─────────────────────┤                   │  │
│  │   │ id (PK)         │   │ id (PK)         │   │ id (PK)         │   │ id (PK)             │                   │  │
│  │   │ nome            │   │ nome            │   │ nome            │   │ valor               │                   │  │
│  │   │ email (UNIQUE)  │   │ tipo            │   │ tipo            │   │ data                │                   │  │
│  │   │ senha_hash      │   │ saldo_inicial   │   │ cor_hex         │   │ descricao           │                   │  │
│  │   │ tipo_usuario    │   │ id_usuario (FK) │   │ id_usuario (FK) │   │ tipo                │                   │  │
│  │   │ ativo           │   │ data_criacao    │   │ data_criacao    │   │ id_conta (FK)       │                   │  │
│  │   │ data_criacao    │   └────────┬────────┘   └────────┬────────┘   │ id_categoria (FK)   │                   │  │
│  │   │ data_atualizacao│            │                     │            │ id_usuario (FK)     │                   │  │
│  │   └────────┬────────┘            │                     │            │ data_criacao        │                   │  │
│  │            │                     │                     │            │ data_atualizacao    │                   │  │
│  │            │                     │                     │            └─────────────────────┘                   │  │
│  │            │                     │                     │                                                       │  │
│  │            │                     │                     │                                                       │  │
│  │            └─────────────────────┴─────────────────────┴──── FOREIGN KEYS (ON DELETE CASCADE)                  │  │
│  │                                                                                                                │  │
│  └────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                                                       │
└───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📌 Resumo das Configurações de Conexão

| Componente | Tipo | Host | Porta | Configuração |
|------------|------|------|-------|--------------|
| **Servidor Finanza** | TCP Server | 0.0.0.0 | 8080 | `ServerSocket(8080)` |
| **Cliente Desktop** | TCP Client | localhost | 8080 | `Socket("localhost", 8080)` |
| **Cliente Mobile** | TCP Client | Configurável | 8080 | SharedPreferences |
| **MySQL** | Database | localhost | 3306 | JDBC URL |
| **Room (SQLite)** | Database Local | - | - | Arquivo `.db` no dispositivo |

---

## 📚 Arquivos de Referência

### Servidor
- `DESKTOP VERSION/ServidorFinanza/src/server/FinanzaServer.java` - Servidor principal
- `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java` - Processador de clientes
- `DESKTOP VERSION/ServidorFinanza/src/server/Protocol.java` - Protocolo de comunicação
- `DESKTOP VERSION/ServidorFinanza/src/util/DatabaseUtil.java` - Conexão MySQL
- `DESKTOP VERSION/ServidorFinanza/src/util/SecurityUtil.java` - Criptografia

### Cliente Desktop
- `DESKTOP VERSION/ClienteFinanza/src/util/NetworkClient.java` - Cliente de rede
- `DESKTOP VERSION/ClienteFinanza/src/controller/AuthController.java` - Autenticação
- `DESKTOP VERSION/ClienteFinanza/src/controller/FinanceController.java` - Operações financeiras

### Cliente Mobile
- `app/src/main/java/com/example/finanza/network/ServerClient.java` - Cliente de rede
- `app/src/main/java/com/example/finanza/network/AuthManager.java` - Autenticação
- `app/src/main/java/com/example/finanza/network/SyncService.java` - Sincronização
- `app/src/main/java/com/example/finanza/db/AppDatabase.java` - Banco local

### Banco de Dados
- `database/finanza_completo.sql` - Script completo MySQL

---

**Última atualização:** 2025-12-02  
**Versão do documento:** 1.0  
**Autor:** Documentação Técnica Sistema Finanza
