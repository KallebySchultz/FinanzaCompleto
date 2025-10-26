# 🗺️ MAPEAMENTO DE CÓDIGO - Sistema Finanza

## 📖 Índice Rápido

1. [O Que Chama O Quê](#o-que-chama-o-quê)
2. [Mobile - Fluxos Completos](#mobile---fluxos-completos)
3. [Servidor - Fluxos Completos](#servidor---fluxos-completos)
4. [Desktop Cliente - Fluxos](#desktop-cliente---fluxos)
5. [Referência Rápida de Arquivos](#referência-rápida-de-arquivos)

---

## 🎯 O Que Chama O Quê

### Mobile (Android) - Estrutura de Chamadas

```
┌─────────────────────────────────────────────────────────────┐
│                     CAMADA DE UI (Activities)                │
│  LoginActivity, RegisterActivity, MenuActivity, etc         │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ├─► AuthManager (gerencia login/sessão)
                   │   └─► ServerClient (comunica com servidor)
                   │       └─► Socket TCP (porta 8080)
                   │
                   ├─► DAOs (acesso ao banco local SQLite)
                   │   ├─► UsuarioDao.buscarPorEmail()
                   │   ├─► ContaDao.listarPorUsuario()
                   │   ├─► LancamentoDao.inserir()
                   │   └─► CategoriaDao.listarPorTipo()
                   │
                   ├─► SyncService (sincronização)
                   │   ├─► ContaDao.obterPendentesSyncPorUsuario()
                   │   ├─► ServerClient.sendCommand()
                   │   └─► ConflictResolutionManager.resolve()
                   │
                   └─► DataIntegrityValidator (validação)
                       ├─► ContaDao.buscarPorId()
                       ├─► CategoriaDao.buscarPorId()
                       └─► LancamentoDao.buscarDuplicata()
```

### Servidor (Java) - Estrutura de Chamadas

```
┌─────────────────────────────────────────────────────────────┐
│            MainServidor (Entry Point)                        │
│            main() → FinanzaServer.start()                    │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│            FinanzaServer (Servidor TCP)                      │
│            - Escuta porta 8080                               │
│            - Aceita conexões                                 │
│            - Cria thread ClientHandler                       │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│         ClientHandler (Processa cliente)                     │
│         - processarComando()                                 │
│         - Roteamento de 40+ comandos                         │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ├─► Protocol (parse e format de comandos)
                   │
                   ├─► DAOs (acesso ao banco MySQL)
                   │   ├─► UsuarioDAO.buscarPorEmail()
                   │   ├─► ContaDAO.listar()
                   │   ├─► MovimentacaoDAO.inserir()
                   │   └─► CategoriaDAO.atualizar()
                   │
                   └─► SecurityUtil (criptografia)
                       └─► hashPassword()
```

### Desktop Cliente (Admin) - Estrutura de Chamadas

```
┌─────────────────────────────────────────────────────────────┐
│                  CAMADA DE UI (Views)                        │
│      LoginView, AdminDashboardView, EditarUsuarioDialog     │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ├─► Controllers (lógica de negócio)
                   │   ├─► AuthController.login()
                   │   └─► FinanceController.atualizarUsuario()
                   │
                   └─► NetworkClient (comunicação)
                       ├─► sendCommand()
                       └─► Socket TCP (porta 8080)
```

---

## 📱 Mobile - Fluxos Completos

### Fluxo 1: Login do Usuário

```
INÍCIO: Usuário abre o app
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. LoginActivity.onCreate()                                 │
│    Arquivo: app/.../ui/LoginActivity.java (linha 70)        │
│    O que faz:                                                │
│    - Carrega layout activity_login.xml                       │
│    - Inicializa campos (inputEmail, inputSenha, btnLogin)   │
│    - Verifica se já está logado → authManager.isLoggedIn()  │
│    - Se sim: vai direto para MainActivity                    │
│    - Se não: exibe formulário de login                       │
└─────────────────────────────────────────────────────────────┘
    ↓
USUÁRIO: Digita email e senha, clica "Entrar"
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. LoginActivity.realizarLogin()                            │
│    Arquivo: app/.../ui/LoginActivity.java (linha 133)       │
│    O que faz:                                                │
│    - Extrai email = inputEmail.getText()                     │
│    - Extrai senha = inputSenha.getText()                     │
│    - Valida campos não vazios                                │
│    - Desabilita botão e mostra "Entrando..."                 │
│    CHAMA → authManager.login(email, senha, callback)         │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. AuthManager.login()                                      │
│    Arquivo: app/.../network/AuthManager.java                │
│    O que faz:                                                │
│    - Criptografa senha usando SHA-256                        │
│      CHAMA → SecurityUtil.hashPassword(senha)                │
│    - Formata comando: "LOGIN|email|senhaHash"                │
│    - Envia ao servidor                                       │
│      CHAMA → ServerClient.sendCommand(comando)               │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. ServerClient.sendCommand()                               │
│    Arquivo: app/.../network/ServerClient.java               │
│    O que faz:                                                │
│    - Conecta ao servidor via Socket TCP                      │
│    - socket = new Socket(SERVER_HOST, 8080)                  │
│    - Envia comando via PrintWriter                           │
│    - output.println("LOGIN|email|hash")                      │
│    - Aguarda resposta via BufferedReader                     │
│    - String resposta = input.readLine()                      │
│    - Fecha conexão                                           │
│    RETORNA → resposta para AuthManager                       │
└─────────────────────────────────────────────────────────────┘
    ↓
【 COMUNICAÇÃO VIA INTERNET/REDE LOCAL 】
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. FinanzaServer.accept() [SERVIDOR]                        │
│    Arquivo: DESKTOP.../server/FinanzaServer.java (linha 49) │
│    O que faz:                                                │
│    - Socket clientSocket = serverSocket.accept()             │
│    - Cria nova thread para processar cliente                 │
│    CHAMA → new ClientHandler(clientSocket).start()           │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. ClientHandler.run() [SERVIDOR]                           │
│    Arquivo: DESKTOP.../server/ClientHandler.java (linha 41) │
│    O que faz:                                                │
│    - Cria streams de entrada e saída                         │
│    - Lê comando: String cmd = input.readLine()               │
│    - cmd = "LOGIN|email|hash"                                │
│    CHAMA → processarComando(cmd)                             │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. ClientHandler.processarComando() [SERVIDOR]              │
│    Arquivo: DESKTOP.../server/ClientHandler.java (linha 67) │
│    O que faz:                                                │
│    - Faz parse: String[] partes = Protocol.parseCommand()    │
│    - partes = ["LOGIN", "email", "hash"]                     │
│    - Identifica comando: switch(partes[0])                   │
│    - case "LOGIN": CHAMA → processarLogin(partes)            │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. ClientHandler.processarLogin() [SERVIDOR]                │
│    Arquivo: DESKTOP.../server/ClientHandler.java            │
│    O que faz:                                                │
│    - Extrai: email = partes[1], senha = partes[2]            │
│    - Busca usuário no banco MySQL                            │
│      CHAMA → usuarioDAO.buscarPorEmail(email)                │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 9. UsuarioDAO.buscarPorEmail() [SERVIDOR]                   │
│    Arquivo: DESKTOP.../dao/UsuarioDAO.java                  │
│    O que faz:                                                │
│    - Conecta ao MySQL: conn = DatabaseUtil.getConnection()   │
│    - Executa query SQL:                                      │
│      SELECT * FROM usuario WHERE email = ?                   │
│    - Retorna objeto Usuario ou null                          │
│    RETORNA → usuario para ClientHandler                      │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 10. ClientHandler.processarLogin() [SERVIDOR - continua]    │
│     O que faz:                                               │
│     - Compara senhas: usuario.getSenhaHash() == senha        │
│     - Se iguais: usuarioLogado = usuario                     │
│     - Formata resposta: "OK|{id:1,nome:João,email:...}"      │
│       CHAMA → Protocol.createResponse()                      │
│     - Envia: output.println(resposta)                        │
│     Se diferentes: "ERROR|Credenciais inválidas"             │
└─────────────────────────────────────────────────────────────┘
    ↓
【 RESPOSTA VIA INTERNET/REDE LOCAL 】
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 11. ServerClient.sendCommand() [MOBILE - continua]          │
│     - Recebe: resposta = input.readLine()                    │
│     - resposta = "OK|{dados_usuario}"                        │
│     RETORNA → resposta para AuthManager                      │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 12. AuthManager.login() [MOBILE - continua]                 │
│     O que faz:                                               │
│     - Parse da resposta JSON                                 │
│     - Cria objeto Usuario com dados recebidos                │
│     - Salva no banco local SQLite:                           │
│       CHAMA → usuarioDao.inserir(usuario)                    │
│     - Salva ID na sessão (SharedPreferences)                 │
│     - Chama callback.onSuccess(usuario)                      │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 13. LoginActivity.realizarLogin() [callback.onSuccess]      │
│     O que faz:                                               │
│     - runOnUiThread() volta para thread principal            │
│     - Toast.show("Login realizado com sucesso!")             │
│     - Intent intent = new Intent(this, MainActivity.class)   │
│     - intent.putExtra("usuarioId", usuario.id)               │
│     - startActivity(intent)                                  │
│     - finish() → fecha LoginActivity                         │
└─────────────────────────────────────────────────────────────┘
    ↓
FIM: MainActivity é aberta (Dashboard do app)
```

**Arquivos envolvidos:**
- Mobile (7 arquivos): LoginActivity, AuthManager, ServerClient, SecurityUtil, UsuarioDao, Protocol, MainActivity
- Servidor (5 arquivos): FinanzaServer, ClientHandler, Protocol, UsuarioDAO, DatabaseUtil

**Tempo total:** 0.5-2 segundos (depende da rede)

---

### Fluxo 2: Adicionar Uma Despesa

```
INÍCIO: Usuário está em MovementsActivity e clica no botão "+"
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. MovementsActivity.onClickFAB()                           │
│    Arquivo: app/.../ui/MovementsActivity.java               │
│    O que faz:                                                │
│    - FloatingActionButton é clicado                          │
│    - Infla layout do dialog:                                 │
│      View dialogView = inflater.inflate(                     │
│          R.layout.dialog_add_transaction_movements)          │
│    - Inicializa campos do formulário                         │
│    - Carrega spinners de Conta e Categoria                   │
│      CHAMA → contaDao.listarPorUsuario(usuarioId)            │
│      CHAMA → categoriaDao.listarPorUsuario(usuarioId)        │
│    - Exibe dialog para usuário                               │
└─────────────────────────────────────────────────────────────┘
    ↓
USUÁRIO: Preenche formulário (descrição, valor, data, tipo, conta, categoria)
    ↓
USUÁRIO: Clica em "Salvar"
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. MovementsActivity.onSaveTransaction()                    │
│    Arquivo: app/.../ui/MovementsActivity.java               │
│    O que faz:                                                │
│    - Extrai dados dos campos:                                │
│      String descricao = etDescricao.getText()                │
│      double valor = Double.parse(etValor.getText())          │
│      long data = datePicker.getDate()                        │
│      String tipo = radioGroup.getCheckedValue()              │
│      Conta conta = spinnerConta.getSelectedItem()            │
│      Categoria categoria = spinnerCategoria.getSelected()    │
│                                                              │
│    - Valida dados:                                           │
│      if (descricao.isEmpty()) → Toast erro                   │
│      if (valor <= 0) → Toast erro                            │
│      if (conta == null) → Toast erro                         │
│                                                              │
│    - Cria objeto Lancamento:                                 │
│      Lancamento lancamento = new Lancamento()                │
│      lancamento.setUuid(UUID.randomUUID())                   │
│      lancamento.setDescricao(descricao)                      │
│      lancamento.setValor(valor)                              │
│      lancamento.setData(data)                                │
│      lancamento.setTipo(tipo)                                │
│      lancamento.setContaId(conta.getId())                    │
│      lancamento.setCategoriaId(categoria.getId())            │
│      lancamento.setUsuarioId(usuarioId)                      │
│      lancamento.setLastModified(System.currentTimeMillis())  │
│      lancamento.setSyncStatus(2) // NEEDS_SYNC               │
│      lancamento.setIsDeleted(0) // Ativo                     │
│                                                              │
│    CHAMA → validarEInserir(lancamento)                       │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. MovementsActivity.validarEInserir()                      │
│    Arquivo: app/.../ui/MovementsActivity.java               │
│    O que faz:                                                │
│    - Exibe ProgressDialog "Salvando..."                      │
│    - Cria nova thread para não bloquear UI                   │
│      new Thread(() -> { ... }).start()                       │
│    - Valida integridade dos dados:                           │
│      CHAMA → DataIntegrityValidator.validarLancamento()      │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. DataIntegrityValidator.validarLancamento()               │
│    Arquivo: app/.../util/DataIntegrityValidator.java        │
│    O que faz:                                                │
│    - Valida valor positivo: if (valor <= 0)                  │
│    - Valida conta existe:                                    │
│      CHAMA → contaDao.buscarPorId(lancamento.getContaId())   │
│      if (conta == null) → erro "Conta não encontrada"        │
│                                                              │
│    - Valida categoria existe:                                │
│      CHAMA → categoriaDao.buscarPorId(categoriaId)           │
│      if (categoria == null) → erro                           │
│                                                              │
│    - Valida tipo corresponde:                                │
│      if (!categoria.getTipo().equals(lancamento.getTipo()))  │
│                                                              │
│    - Verifica duplicatas recentes (últimos 5 minutos):       │
│      CHAMA → lancamentoDao.buscarSimilares(valor, data, ...) │
│      if (!similares.isEmpty()) → aviso de duplicata          │
│                                                              │
│    RETORNA → ValidationResult (ok ou erro)                   │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. MovementsActivity.validarEInserir() [continua]           │
│    O que faz após validação:                                 │
│    - Se validação falhou: Toast com erro, return             │
│    - Se passou: Insere no banco local                        │
│      CHAMA → lancamentoDao.inserirSeguro(lancamento)         │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. LancamentoDao.inserirSeguro()                            │
│    Arquivo: app/.../db/LancamentoDao.java                   │
│    O que faz:                                                │
│    - Room executa INSERT no SQLite:                          │
│      INSERT INTO lancamento (uuid, valor, data, descricao,   │
│          tipo, contaId, categoriaId, usuarioId,              │
│          dataCriacao, lastModified, syncStatus, isDeleted)   │
│      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)             │
│                                                              │
│    - Retorna ID do lançamento inserido (ex: 123)             │
│    RETORNA → long idInserido                                 │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. MovementsActivity.validarEInserir() [continua]           │
│    O que faz após inserção:                                  │
│    - Atualiza saldo da conta:                                │
│      CHAMA → contaDao.buscarPorId(lancamento.getContaId())   │
│      double novoSaldo = conta.getSaldoAtual()                │
│      if (tipo == "receita") novoSaldo += valor               │
│      if (tipo == "despesa") novoSaldo -= valor               │
│      conta.setSaldoAtual(novoSaldo)                          │
│      conta.setLastModified(System.currentTimeMillis())       │
│      conta.setSyncStatus(2) // Marca para sincronização      │
│      CHAMA → contaDao.atualizar(conta)                       │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. ContaDao.atualizar()                                     │
│    Arquivo: app/.../db/ContaDao.java                        │
│    O que faz:                                                │
│    - Room executa UPDATE no SQLite:                          │
│      UPDATE conta                                            │
│      SET saldoAtual = ?, lastModified = ?, syncStatus = ?    │
│      WHERE id = ?                                            │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 9. MovementsActivity.validarEInserir() [continua]           │
│    O que faz após atualizar conta:                           │
│    - Inicia sincronização com servidor (em background):      │
│      CHAMA → EnhancedSyncService.syncLancamento(lancamento)  │
│      CHAMA → EnhancedSyncService.syncConta(conta)            │
│                                                              │
│    - Atualiza UI (na thread principal):                      │
│      runOnUiThread(() -> {                                   │
│          progressDialog.dismiss()                            │
│          dialog.dismiss()                                    │
│          Toast.show("Lançamento salvo!")                     │
│          recarregarLancamentos()                             │
│      })                                                      │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 10. EnhancedSyncService.syncLancamento()                    │
│     Arquivo: app/.../network/EnhancedSyncService.java       │
│     O que faz (em background):                               │
│     - Formata dados para envio:                              │
│       String params = formatarLancamentoParaSync()           │
│       params = "uuid|valor|data|desc|tipo|conta|cat|..."     │
│                                                              │
│     - Monta comando:                                         │
│       String comando = Protocol.CMD_ADD_MOVIMENTACAO_ENHANCED│
│       comando += "|" + params                                │
│                                                              │
│     - Envia ao servidor:                                     │
│       CHAMA → ServerClient.sendCommand(comando, params)      │
└─────────────────────────────────────────────────────────────┘
    ↓
【 COMUNICAÇÃO VIA INTERNET/REDE LOCAL 】
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 11. ClientHandler.processarAddMovimentacao() [SERVIDOR]     │
│     Arquivo: DESKTOP.../server/ClientHandler.java           │
│     O que faz:                                               │
│     - Parse dos parâmetros: String[] partes = ...            │
│     - Extrai: uuid, valor, data, descricao, tipo, etc        │
│     - Cria objeto Movimentacao                               │
│     - Salva no MySQL:                                        │
│       CHAMA → movimentacaoDAO.inserir(movimentacao)          │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 12. MovimentacaoDAO.inserir() [SERVIDOR]                    │
│     Arquivo: DESKTOP.../dao/MovimentacaoDAO.java            │
│     O que faz:                                               │
│     - Conecta ao MySQL                                       │
│     - Executa INSERT:                                        │
│       INSERT INTO movimentacao (valor, data, descricao,      │
│           tipo, id_conta, id_categoria, id_usuario,          │
│           data_criacao, data_atualizacao)                    │
│       VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())             │
│                                                              │
│     - Obtém ID gerado (ex: 542)                              │
│     - Atualiza saldo da conta no servidor:                   │
│       CHAMA → contaDAO.buscarPorId(idConta)                  │
│       CHAMA → contaDAO.atualizarSaldo(idConta, novoSaldo)    │
│                                                              │
│     RETORNA → ID inserido (542)                              │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 13. ClientHandler.processarAddMovimentacao() [SERVIDOR]     │
│     O que faz após inserção:                                 │
│     - Formata resposta: "OK|542"                             │
│     - Envia ao cliente: output.println("OK|542")             │
└─────────────────────────────────────────────────────────────┘
    ↓
【 RESPOSTA VIA INTERNET/REDE LOCAL 】
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 14. EnhancedSyncService.syncLancamento() [MOBILE]           │
│     O que faz após receber resposta:                         │
│     - Parse da resposta: "OK|542"                            │
│     - Extrai serverId = 542                                  │
│     - Atualiza metadados locais:                             │
│       CHAMA → lancamentoDao.marcarComoSincronizado(id, time) │
│       CHAMA → lancamentoDao.atualizarMetadataSync(...)       │
│                                                              │
│     - Log: "✓ Lançamento sincronizado: 542"                  │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 15. LancamentoDao.marcarComoSincronizado()                  │
│     Arquivo: app/.../db/LancamentoDao.java                  │
│     O que faz:                                               │
│     - Room executa UPDATE:                                   │
│       UPDATE lancamento                                      │
│       SET syncStatus = 1,  -- SYNCED                         │
│           lastSyncTime = ?                                   │
│       WHERE id = ?                                           │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 16. MovementsActivity.recarregarLancamentos()               │
│     O que faz (já na thread UI):                             │
│     - Busca lançamentos atualizados:                         │
│       CHAMA → lancamentoDao.listarAtivosPorUsuario(id)       │
│     - Atualiza RecyclerView:                                 │
│       adapter.setLancamentos(lista)                          │
│       adapter.notifyDataSetChanged()                         │
│     - Rola para o topo (lançamento mais recente):            │
│       recyclerView.smoothScrollToPosition(0)                 │
│     - Atualiza resumo financeiro no topo da tela             │
└─────────────────────────────────────────────────────────────┘
    ↓
FIM: Usuário vê nova despesa na lista!
```

**Arquivos envolvidos:**
- Mobile (10 arquivos): MovementsActivity, LancamentoDao, ContaDao, CategoriaDao, DataIntegrityValidator, EnhancedSyncService, ServerClient, Protocol
- Servidor (5 arquivos): ClientHandler, Protocol, MovimentacaoDAO, ContaDAO, DatabaseUtil

**Modificações no banco de dados:**
- SQLite (mobile): 2 INSERTs (lancamento) + 1 UPDATE (conta) + 1 UPDATE (syncStatus)
- MySQL (servidor): 1 INSERT (movimentacao) + 1 UPDATE (conta)

**Tempo total:** 0.5-3 segundos

---

## 🖥️ Servidor - Fluxos Completos

### Fluxo 1: Inicialização do Servidor

```
INÍCIO: Executar java MainServidor
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. MainServidor.main()                                      │
│    Arquivo: DESKTOP.../MainServidor.java (linha 8)          │
│    O que faz:                                                │
│    - Exibe banner de inicialização                           │
│    - Verifica argumentos: boolean testMode = args[0]=="--test"│
│    - Cria instância do servidor:                             │
│      FinanzaServer server = new FinanzaServer(testMode)      │
│    - Registra shutdown hook para Ctrl+C:                     │
│      Runtime.getRuntime().addShutdownHook(...)               │
│    CHAMA → server.start()                                    │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. FinanzaServer.start()                                    │
│    Arquivo: DESKTOP.../server/FinanzaServer.java (linha 26) │
│    O que faz:                                                │
│    FASE 1: Validação do Banco                                │
│    - if (!testMode):                                         │
│        CHAMA → DatabaseUtil.testConnection()                 │
│        Se falhar: exibe erro e return                        │
│        Se OK: exibe "✓ Conexão com banco OK"                 │
│                                                              │
│    FASE 2: Inicialização do Banco                            │
│    - CHAMA → DatabaseUtil.initializeDatabase()               │
│    - Cria tabelas se não existirem                           │
│    - Exibe "✓ Banco de dados inicializado"                   │
│                                                              │
│    FASE 3: Abertura do Servidor                              │
│    - serverSocket = new ServerSocket(8080)                   │
│    - running = true                                          │
│    - Exibe "Servidor iniciado na porta 8080"                 │
│                                                              │
│    FASE 4: Loop Principal (while running)                    │
│    - Socket clientSocket = serverSocket.accept()             │
│      [BLOQUEIA AQUI esperando cliente conectar]              │
│    - Quando conecta:                                         │
│        ClientHandler handler = new ClientHandler(socket)     │
│        handler.start() → nova thread                         │
│    - Volta ao accept() para próximo cliente                  │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. DatabaseUtil.testConnection()                            │
│    Arquivo: DESKTOP.../util/DatabaseUtil.java               │
│    O que faz:                                                │
│    - try {                                                   │
│        Connection conn = DriverManager.getConnection(        │
│            "jdbc:mysql://localhost:3306/finanza_db",         │
│            "root", "senha")                                  │
│        conn.close()                                          │
│        return true                                           │
│      } catch (SQLException) {                                │
│        return false                                          │
│      }                                                       │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. DatabaseUtil.initializeDatabase()                        │
│    Arquivo: DESKTOP.../util/DatabaseUtil.java               │
│    O que faz:                                                │
│    - Cria tabelas se não existirem:                          │
│      CREATE TABLE IF NOT EXISTS usuario (...)                │
│      CREATE TABLE IF NOT EXISTS conta (...)                  │
│      CREATE TABLE IF NOT EXISTS categoria (...)              │
│      CREATE TABLE IF NOT EXISTS movimentacao (...)           │
│                                                              │
│    - Cria constraints e foreign keys                         │
│    - Cria índices para performance                           │
│    - Insere usuário admin padrão se não existir              │
└─────────────────────────────────────────────────────────────┘
    ↓
FIM: Servidor rodando, esperando conexões na porta 8080
```

### Fluxo 2: Processar Comando de Cliente

```
INÍCIO: Cliente conecta e envia comando
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. ServerSocket.accept() → retorna Socket do cliente        │
│    Nova thread ClientHandler é criada                       │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. ClientHandler.run()                                      │
│    Arquivo: DESKTOP.../server/ClientHandler.java (linha 41) │
│    O que faz:                                                │
│    - Cria streams:                                           │
│      input = new BufferedReader(...)                         │
│      output = new PrintWriter(...)                           │
│                                                              │
│    - Loop de comandos:                                       │
│      while ((comando = input.readLine()) != null) {          │
│          String resposta = processarComando(comando)         │
│          output.println(resposta)                            │
│      }                                                       │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. ClientHandler.processarComando()                         │
│    Arquivo: DESKTOP.../server/ClientHandler.java (linha 67) │
│    O que faz:                                                │
│    - Parse: String[] partes = Protocol.parseCommand(cmd)     │
│    - Switch no tipo de comando (40+ cases)                   │
│    - Delega para método específico                           │
│                                                              │
│    Exemplos:                                                 │
│    - case "LOGIN": → processarLogin(partes)                  │
│    - case "ADD_CONTA": → processarAddConta(partes)           │
│    - case "LIST_MOVIMENTACOES": → processarListMovimentacoes()│
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. Método processar<TipoComando>()                          │
│    Exemplos:                                                 │
│    processarLogin():                                         │
│    - Extrai email e senha                                    │
│    - CHAMA → usuarioDAO.buscarPorEmail(email)                │
│    - Compara senhas (hashes SHA-256)                         │
│    - Se OK: retorna dados do usuário                         │
│    - Se erro: retorna mensagem de erro                       │
│                                                              │
│    processarAddConta():                                      │
│    - Extrai nome, tipo, saldo, usuarioId                     │
│    - Cria objeto Conta                                       │
│    - CHAMA → contaDAO.inserir(conta)                         │
│    - Retorna ID da conta criada                              │
│                                                              │
│    processarListMovimentacoes():                             │
│    - CHAMA → movimentacaoDAO.listar(usuarioId)               │
│    - Formata lista em String                                 │
│    - Retorna lista serializada                               │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. DAO acessa MySQL                                         │
│    Exemplos de DAOs:                                         │
│    UsuarioDAO.buscarPorEmail():                              │
│    - Connection conn = DatabaseUtil.getConnection()          │
│    - PreparedStatement stmt = conn.prepareStatement(         │
│        "SELECT * FROM usuario WHERE email = ?")              │
│    - stmt.setString(1, email)                                │
│    - ResultSet rs = stmt.executeQuery()                      │
│    - Monta objeto Usuario a partir do ResultSet              │
│    - return usuario                                          │
│                                                              │
│    ContaDAO.inserir():                                       │
│    - INSERT INTO conta (...) VALUES (...)                    │
│    - return ID gerado                                        │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. Resposta formatada e enviada                             │
│    - Protocol.createResponse(status, dados)                  │
│    - output.println(resposta)                                │
│    - Cliente recebe resposta                                 │
└─────────────────────────────────────────────────────────────┘
    ↓
FIM: Cliente processa resposta
```

---

## 💻 Desktop Cliente - Fluxos

### Fluxo 1: Admin Faz Login

```
INÍCIO: Executar ClienteFinanza.jar
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. MainCliente.main()                                       │
│    Arquivo: DESKTOP.../ClienteFinanza/src/MainCliente.java  │
│    O que faz:                                                │
│    - Inicializa Swing Look and Feel                          │
│    - Cria e exibe LoginView                                  │
│      LoginView loginView = new LoginView()                   │
│      loginView.setVisible(true)                              │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. LoginView exibido                                        │
│    Arquivo: DESKTOP.../view/LoginView.java                  │
│    O que contém:                                             │
│    - JTextField para email                                   │
│    - JPasswordField para senha                               │
│    - JButton "Entrar"                                        │
│    - ActionListener no botão                                 │
└─────────────────────────────────────────────────────────────┘
    ↓
USUÁRIO: Digita email e senha, clica "Entrar"
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. LoginView.actionPerformed() → btnEntrar                  │
│    O que faz:                                                │
│    - String email = txtEmail.getText()                       │
│    - String senha = new String(txtSenha.getPassword())       │
│    - Valida campos não vazios                                │
│    - Desabilita botão, mostra "Entrando..."                  │
│    CHAMA → AuthController.login(email, senha)                │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. AuthController.login()                                   │
│    Arquivo: DESKTOP.../controller/AuthController.java       │
│    O que faz:                                                │
│    - Criptografa senha: hash = SecurityUtil.hashPassword()   │
│    - Formata comando: "LOGIN|email|hash|admin"               │
│      (parâmetro "admin" indica cliente desktop)              │
│    CHAMA → NetworkClient.sendCommand(comando)                │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. NetworkClient.sendCommand()                              │
│    Arquivo: DESKTOP.../util/NetworkClient.java              │
│    O que faz:                                                │
│    - Socket socket = new Socket("localhost", 8080)           │
│    - PrintWriter out = new PrintWriter(socket.getOut...)     │
│    - out.println(comando)                                    │
│    - BufferedReader in = new BufferedReader(socket.get...)   │
│    - String resposta = in.readLine()                         │
│    - socket.close()                                          │
│    RETORNA → resposta                                        │
└─────────────────────────────────────────────────────────────┘
    ↓
【 COMUNICAÇÃO COM SERVIDOR (ver fluxo servidor acima) 】
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. AuthController.login() [continua]                        │
│    O que faz após receber resposta:                          │
│    - if (resposta.startsWith("OK|")):                        │
│        Parse dados do admin: Usuario admin = ...             │
│        Armazena na sessão                                    │
│        return true                                           │
│      else:                                                   │
│        return false                                          │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. LoginView.actionPerformed() [callback]                   │
│    O que faz se login OK:                                    │
│    - JOptionPane.showMessage("Login realizado!")             │
│    - Fecha LoginView: this.dispose()                         │
│    - Abre AdminDashboardView:                                │
│      AdminDashboardView dashboard = new ...                  │
│      dashboard.setVisible(true)                              │
└─────────────────────────────────────────────────────────────┘
    ↓
FIM: AdminDashboardView exibido com lista de usuários
```

### Fluxo 2: Admin Edita Usuário

```
INÍCIO: Admin seleciona usuário na tabela e clica "Editar"
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 1. AdminDashboardView.btnEditarActionPerformed()            │
│    Arquivo: DESKTOP.../view/AdminDashboardView.java         │
│    O que faz:                                                │
│    - int selectedRow = table.getSelectedRow()                │
│    - Usuario usuario = tableModel.getUsuario(selectedRow)    │
│    - Cria e exibe dialog de edição:                          │
│      EditarUsuarioDialog dialog = new ...                    │
│      dialog.setUsuario(usuario)                              │
│      dialog.setVisible(true)                                 │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. EditarUsuarioDialog exibido                              │
│    Arquivo: DESKTOP.../view/EditarUsuarioDialog.java        │
│    O que contém:                                             │
│    - JTextField para nome (editável)                         │
│    - JTextField para email (editável)                        │
│    - JButton "Salvar" e "Cancelar"                           │
│    - Campos preenchidos com dados do usuário                 │
└─────────────────────────────────────────────────────────────┘
    ↓
ADMIN: Altera nome ou email, clica "Salvar"
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. EditarUsuarioDialog.btnSalvarActionPerformed()           │
│    O que faz:                                                │
│    - String novoNome = txtNome.getText()                     │
│    - String novoEmail = txtEmail.getText()                   │
│    - Valida campos                                           │
│    - usuario.setNome(novoNome)                               │
│    - usuario.setEmail(novoEmail)                             │
│    CHAMA → FinanceController.atualizarUsuario(usuario)       │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. FinanceController.atualizarUsuario()                     │
│    Arquivo: DESKTOP.../controller/FinanceController.java    │
│    O que faz:                                                │
│    - Formata comando:                                        │
│      "UPDATE_USER|id|nome|email"                             │
│    CHAMA → NetworkClient.sendCommand(comando)                │
└─────────────────────────────────────────────────────────────┘
    ↓
【 COMUNICAÇÃO COM SERVIDOR 】
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. ClientHandler.processarUpdateUser() [SERVIDOR]           │
│    O que faz:                                                │
│    - Parse parâmetros                                        │
│    - usuario.setNome(novoNome)                               │
│    - usuario.setEmail(novoEmail)                             │
│    CHAMA → usuarioDAO.atualizar(usuario)                     │
│    - Retorna "OK|Usuário atualizado"                         │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. UsuarioDAO.atualizar() [SERVIDOR]                        │
│    Arquivo: DESKTOP.../dao/UsuarioDAO.java                  │
│    O que faz:                                                │
│    - Connection conn = DatabaseUtil.getConnection()          │
│    - UPDATE usuario                                          │
│      SET nome = ?, email = ?                                 │
│      WHERE id = ?                                            │
│    - return true se sucesso                                  │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. EditarUsuarioDialog [recebe resposta]                    │
│    O que faz:                                                │
│    - if (resposta == "OK"):                                  │
│        JOptionPane.show("Usuário atualizado!")               │
│        this.dispose() → fecha dialog                         │
│        Notifica AdminDashboardView para recarregar           │
└─────────────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. AdminDashboardView.recarregarUsuarios()                  │
│    O que faz:                                                │
│    CHAMA → FinanceController.listarUsuarios()                │
│    - Atualiza JTable com lista nova                          │
│    - tableModel.fireTableDataChanged()                       │
└─────────────────────────────────────────────────────────────┘
    ↓
FIM: Tabela atualizada com dados do usuário modificado
```

---

## 📚 Referência Rápida de Arquivos

### Mobile (Android) - 25 arquivos Java

#### Activities (Telas) - 8 arquivos
| Arquivo | Linha Aproximada | O que faz |
|---------|------------------|-----------|
| **LoginActivity.java** | 45-265 | Login do usuário |
| **RegisterActivity.java** | ~400 | Cadastro de novo usuário |
| **MainActivity.java** | ~300 | Dashboard principal (redirect) |
| **MenuActivity.java** | ~500 | Menu e dashboard |
| **AccountsActivity.java** | ~600 | Gerenciar contas bancárias |
| **CategoriaActivity.java** | ~500 | Gerenciar categorias |
| **MovementsActivity.java** | ~800 | Lançamentos financeiros |
| **ProfileActivity.java** | ~400 | Perfil do usuário |
| **SettingsActivity.java** | ~300 | Configurações |

#### DAOs (Acesso ao Banco) - 4 arquivos
| Arquivo | Métodos | O que faz |
|---------|---------|-----------|
| **UsuarioDao.java** | 20+ | CRUD usuários no SQLite |
| **ContaDao.java** | 27+ | CRUD contas no SQLite |
| **CategoriaDao.java** | 15+ | CRUD categorias no SQLite |
| **LancamentoDao.java** | 40+ | CRUD lançamentos no SQLite |

#### Models - 4 arquivos
- **Usuario.java**: Entidade usuário (@Entity)
- **Conta.java**: Entidade conta bancária
- **Categoria.java**: Entidade categoria
- **Lancamento.java**: Entidade lançamento/transação

#### Network - 6 arquivos
| Arquivo | O que faz |
|---------|-----------|
| **ServerClient.java** | Conexão TCP com servidor |
| **Protocol.java** | Define 50+ comandos |
| **AuthManager.java** | Gerencia login e sessão |
| **SyncService.java** | Sincronização básica |
| **EnhancedSyncService.java** | Sincronização avançada |
| **ConflictResolutionManager.java** | Resolve conflitos |

#### Util - 1 arquivo
- **DataIntegrityValidator.java**: Valida dados antes de salvar

---

### Servidor (Java) - 10 arquivos

#### Entry Point - 1 arquivo
- **MainServidor.java**: Método main(), inicia servidor

#### Server - 3 arquivos
| Arquivo | Linhas | O que faz |
|---------|--------|-----------|
| **FinanzaServer.java** | ~200 | Servidor TCP porta 8080 |
| **ClientHandler.java** | ~2000 | Processa comandos (40+) |
| **Protocol.java** | ~400 | Define protocolo |

#### DAOs - 4 arquivos
- **UsuarioDAO.java**: CRUD usuários no MySQL
- **ContaDAO.java**: CRUD contas no MySQL
- **CategoriaDAO.java**: CRUD categorias no MySQL
- **MovimentacaoDAO.java**: CRUD movimentações no MySQL

#### Utils - 2 arquivos
- **DatabaseUtil.java**: Conexão e inicialização MySQL
- **SecurityUtil.java**: Criptografia SHA-256

---

### Desktop Cliente - 8 arquivos

#### Entry Point - 1 arquivo
- **MainCliente.java**: Método main(), inicia interface

#### Views (Interface Swing) - 3 arquivos
- **LoginView.java**: Tela de login admin
- **AdminDashboardView.java**: Tabela de usuários
- **EditarUsuarioDialog.java**: Dialog edição

#### Controllers - 2 arquivos
- **AuthController.java**: Lógica de autenticação
- **FinanceController.java**: Lógica de finanças

#### Models - 4 arquivos
- **Usuario.java, Conta.java, Categoria.java, Movimentacao.java**

#### Util - 1 arquivo
- **NetworkClient.java**: Cliente TCP para servidor

---

## 📊 Tabela de Comandos do Protocolo

| Comando | Origem | Parâmetros | Resposta | Arquivo que Processa |
|---------|---------|-----------|----------|---------------------|
| **LOGIN** | Mobile/Desktop | email\|senha_hash | OK\|dados ou ERROR | ClientHandler.processarLogin() |
| **REGISTER** | Mobile | nome\|email\|senha\|uuid | OK\|id ou ERROR | ClientHandler.processarRegistro() |
| **ADD_CONTA** | Mobile | nome\|tipo\|saldo\|userId | OK\|id ou ERROR | ClientHandler.processarAddConta() |
| **LIST_CONTAS** | Mobile | userId | OK\|lista ou ERROR | ClientHandler.processarListContas() |
| **ADD_MOVIMENTACAO** | Mobile | valor\|data\|desc\|tipo\|... | OK\|id ou ERROR | ClientHandler.processarAddMovimentacao() |
| **LIST_USERS** | Desktop Admin | - | OK\|lista ou ERROR | ClientHandler.processarListUsers() |
| **UPDATE_USER** | Desktop Admin | id\|nome\|email | OK ou ERROR | ClientHandler.processarUpdateUser() |

*(40+ comandos no total - ver Protocol.java para lista completa)*

---

## 🔍 Como Usar Este Mapeamento

### Para Entender um Fluxo:
1. Identifique o ponto de partida (ex: usuário clica botão)
2. Siga a sequência numerada
3. Veja quais arquivos são chamados em cada etapa
4. Use os números de linha como referência

### Para Debugar um Problema:
1. Identifique onde o erro ocorre
2. Veja o fluxo relacionado neste documento
3. Verifique cada chamada na sequência
4. Use logs para confirmar em qual etapa falha

### Para Adicionar Funcionalidade:
1. Veja fluxos similares existentes
2. Identifique quais arquivos precisam modificação
3. Siga o padrão de chamadas existente
4. Atualize este documento com o novo fluxo

---

## 📝 Convenções de Nomenclatura

- **Activities**: Terminam com "Activity" (LoginActivity)
- **DAOs**: Terminam com "Dao" ou "DAO" (ContaDao, UsuarioDAO)
- **Views**: Terminam com "View" ou "Dialog" (LoginView, EditarUsuarioDialog)
- **Controllers**: Terminam com "Controller" (AuthController)
- **Utils**: Terminam com "Util" (DatabaseUtil)
- **Models**: Nome da entidade (Usuario, Conta)
- **Services**: Terminam com "Service" (SyncService)
- **Managers**: Terminam com "Manager" (AuthManager)

---

**Última atualização:** 2024  
**Autor:** Equipe Finanza - IFSUL Campus Venâncio Aires
