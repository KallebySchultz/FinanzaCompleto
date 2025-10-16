# 👨‍💻 Guia do Desenvolvedor - Sistema Finanza

## 📋 Índice

1. [Como Navegar no Código](#-como-navegar-no-código)
2. [Entendendo o Fluxo de Dados](#-entendendo-o-fluxo-de-dados)
3. [Adicionando Novas Funcionalidades](#-adicionando-novas-funcionalidades)
4. [Debugging e Troubleshooting](#-debugging-e-troubleshooting)
5. [Ambiente de Desenvolvimento](#-ambiente-de-desenvolvimento)
6. [Casos de Uso Comuns](#-casos-de-uso-comuns)

---

## 🧭 Como Navegar no Código

### 🎯 **Quando você quiser entender como algo funciona**

#### **Passo 1: Identifique o ponto de entrada**
```bash
# Para funcionalidade no Desktop:
1. Encontre a View correspondente (ex: PerfilView.java)
2. Procure pelo método do botão/ação (ex: botaoAlterarSenha)

# Para funcionalidade no Mobile:
1. Encontre a Activity correspondente (ex: ProfileActivity.java)
2. Procure pelo método do botão/ação (ex: onChangePasswordClick)
```

#### **Passo 2: Siga o fluxo MVC**
```java
// 🖼️ VIEW (Interface do usuário)
PerfilView.java → abrirFormularioAlterarSenha()
    ↓
// 🎮 CONTROLLER (Lógica de negócio)  
FinanceController.java → alterarSenha()
    ↓
// 🌐 NETWORK (Comunicação)
NetworkClient.java → sendCommand()
    ↓
// 🖥️ SERVER (Processamento)
ClientHandler.java → processarAlterarSenha()
    ↓
// 💾 DAO (Acesso aos dados)
UsuarioDAO.java → atualizarSenha()
    ↓
// 🗄️ DATABASE (Persistência)
MySQL → UPDATE usuario SET senha_hash = ?
```

### 🔍 **Guia de Busca por Funcionalidade**

| Funcionalidade | Desktop View | Mobile Activity | Server Handler | DAO |
|----------------|--------------|-----------------|----------------|-----|
| **Login** | LoginView.java | LoginActivity.java | processarLogin() | UsuarioDAO.autenticar() |
| **Alterar Senha** | PerfilView.java | ProfileActivity.java | processarAlterarSenha() | UsuarioDAO.atualizarSenha() |
| **Movimentações** | MovimentacoesView.java | MovementsActivity.java | processarAdicionarMovimentacao() | MovimentacaoDAO.inserir() |
| **Contas** | ContasView.java | AccountsActivity.java | processarAdicionarConta() | ContaDAO.inserir() |
| **Categorias** | CategoriasView.java | CategoriaActivity.java | processarAdicionarCategoria() | CategoriaDAO.inserir() |
| **Relatórios** | RelatoriosView.java | ReportsActivity.java | processarRelatorio() | MovimentacaoDAO.listarPorPeriodo() |

### 🗂️ **Mapa de Arquivos Importantes**

#### **📁 Desktop Cliente**
```
DESKTOP VERSION/ClienteFinanza/src/
├── MainCliente.java              # 🚀 Ponto de entrada da aplicação
├── controller/
│   ├── AuthController.java       # 🔐 Login/logout/autenticação
│   └── FinanceController.java    # 💰 Operações financeiras principais
├── view/
│   ├── LoginView.java           # 🔑 Tela de login
│   ├── DashboardView.java       # 🏠 Dashboard principal
│   ├── PerfilView.java          # 👤 Perfil e alteração de senha
│   ├── MovimentacoesView.java   # 💸 Lista e CRUD de movimentações
│   ├── ContasView.java          # 🏦 Gerenciar contas bancárias
│   ├── CategoriasView.java      # 📂 Gerenciar categorias
│   └── RelatoriosView.java      # 📊 Relatórios e gráficos
└── util/
    └── NetworkClient.java       # 📡 Cliente TCP para servidor
```

#### **📁 Desktop Servidor**
```
DESKTOP VERSION/ServidorFinanza/src/
├── MainServidor.java            # 🚀 Ponto de entrada do servidor
├── server/
│   ├── FinanzaServer.java       # 🌐 Servidor TCP principal
│   ├── ClientHandler.java       # 👥 Manipula cada cliente conectado
│   └── Protocol.java            # 📋 Protocolo de comunicação
├── dao/
│   ├── UsuarioDAO.java          # 👤 CRUD de usuários
│   ├── ContaDAO.java            # 🏦 CRUD de contas
│   ├── CategoriaDAO.java        # 📂 CRUD de categorias
│   └── MovimentacaoDAO.java     # 💸 CRUD de movimentações
└── util/
    ├── DatabaseUtil.java        # 🗄️ Conexão com MySQL
    └── SecurityUtil.java        # 🔐 Hash de senhas
```

#### **📁 Mobile Android**
```
app/src/main/java/com/example/finanza/
├── MainActivity.java            # 🏠 Dashboard mobile
├── ui/
│   ├── LoginActivity.java       # 🔑 Login mobile
│   ├── ProfileActivity.java     # 👤 Perfil mobile
│   ├── MovementsActivity.java   # 💸 Movimentações mobile
│   └── AccountsActivity.java    # 🏦 Contas mobile
├── network/
│   ├── ServerClient.java        # 📡 Cliente TCP mobile
│   └── SyncService.java         # 🔄 Sincronização automática
└── db/
    ├── AppDatabase.java         # 💾 Configuração Room
    └── [Entity]Dao.java         # 💾 DAOs locais (SQLite)
```

---

## 📊 Entendendo o Fluxo de Dados

### 🔄 **Ciclo Completo: Adicionar Nova Movimentação**

#### **1. Usuário preenche formulário**
```java
// Desktop: MovimentacaoFormDialog.java
private void salvarMovimentacao() {
    Movimentacao mov = new Movimentacao();
    mov.setDescricao(campoDescricao.getText());
    mov.setValor(new BigDecimal(campoValor.getText()));
    // ... outros campos
    
    // Chama controller
    FinanceController.getInstance().adicionarMovimentacao(mov);
}
```

#### **2. Controller processa**
```java
// FinanceController.java
public OperationResult<Void> adicionarMovimentacao(Movimentacao mov) {
    // Valida dados
    if (!validarMovimentacao(mov)) {
        return new OperationResult<>(false, "Dados inválidos", null);
    }
    
    // Serializa para protocolo
    String comando = CMD_ADD_MOVEMENT + SEPARATOR + 
                    mov.getDescricao() + SEPARATOR +
                    mov.getValor() + SEPARATOR + 
                    // ... outros campos
    
    // Envia ao servidor
    String resposta = networkClient.sendCommand(comando);
    return processarResposta(resposta);
}
```

#### **3. Servidor recebe e processa**
```java
// ClientHandler.java
private String processarAdicionarMovimentacao(String[] partes) {
    // Reconstrói objeto
    Movimentacao mov = new Movimentacao();
    mov.setDescricao(partes[1]);
    mov.setValor(new BigDecimal(partes[2]));
    // ... outros campos
    
    // Valida permissões
    if (!validarPropriedade(mov.getIdConta(), usuarioLogado.getId())) {
        return Protocol.createErrorResponse("Conta não pertence ao usuário");
    }
    
    // Salva no banco
    if (movimentacaoDAO.inserir(mov)) {
        return Protocol.createSuccessResponse("Movimentação criada");
    } else {
        return Protocol.createErrorResponse("Erro ao salvar");
    }
}
```

#### **4. DAO persiste no banco**
```java
// MovimentacaoDAO.java
public boolean inserir(Movimentacao mov) {
    String sql = "INSERT INTO movimentacao (descricao, valor, data, id_conta, id_categoria, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, mov.getDescricao());
        stmt.setBigDecimal(2, mov.getValor());
        stmt.setDate(3, mov.getData());
        stmt.setInt(4, mov.getIdConta());
        stmt.setInt(5, mov.getIdCategoria());
        stmt.setInt(6, mov.getIdUsuario());
        
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        logger.error("Erro ao inserir movimentação", e);
        return false;
    }
}
```

### 📡 **Protocolo de Comunicação**

#### **Comandos Disponíveis:**
```java
// Autenticação
LOGIN|email|senha
LOGOUT

// Gestão de senha  
CHANGE_PASSWORD|senha_atual|nova_senha
RESET_PASSWORD|email

// CRUD Movimentações
ADD_MOVEMENT|descricao|valor|data|tipo|id_conta|id_categoria
UPDATE_MOVEMENT|id|descricao|valor|data|tipo|id_conta|id_categoria
DELETE_MOVEMENT|id
LIST_MOVEMENTS|mes|ano

// CRUD Contas
ADD_ACCOUNT|nome|tipo|saldo_inicial
UPDATE_ACCOUNT|id|nome|tipo
DELETE_ACCOUNT|id
LIST_ACCOUNTS

// CRUD Categorias
ADD_CATEGORY|nome|tipo|cor
UPDATE_CATEGORY|id|nome|tipo|cor
DELETE_CATEGORY|id
LIST_CATEGORIES

// Relatórios
REPORT_MONTHLY|mes|ano
REPORT_CATEGORY|id_categoria|mes|ano
EXPORT_DATA|formato|periodo
```

#### **Respostas do Servidor:**
```java
// Sucesso
OK|dados_retornados

// Erros específicos
ERROR|mensagem_erro
INVALID_CREDENTIALS|Credenciais inválidas
INVALID_DATA|Dados inválidos
NOT_FOUND|Registro não encontrado
PERMISSION_DENIED|Sem permissão

// Estados especiais
CONFLICT|dados_cliente|dados_servidor  // Para resolução de conflitos
SYNC_REQUIRED|timestamp               // Indica necessidade de sync
```

---

## ➕ Adicionando Novas Funcionalidades

### 🎯 **Exemplo Prático: Adicionar funcionalidade "Orçamento Mensal"**

#### **1. Definir a Entidade (Model)**
```java
// Criar: shared/model/Orcamento.java
public class Orcamento {
    private int id;
    private int idCategoria;
    private int idUsuario;
    private BigDecimal valorLimite;
    private int mes;
    private int ano;
    private Date dataCriacao;
    
    // Getters e Setters
}
```

#### **2. Criar DAO**
```java
// Criar: ServidorFinanza/src/dao/OrcamentoDAO.java
public class OrcamentoDAO {
    public boolean inserir(Orcamento orcamento) {
        String sql = "INSERT INTO orcamento (id_categoria, id_usuario, valor_limite, mes, ano) VALUES (?, ?, ?, ?, ?)";
        // Implementação...
    }
    
    public List<Orcamento> listarPorUsuario(int idUsuario, int mes, int ano) {
        // Implementação...
    }
    
    public boolean atualizar(Orcamento orcamento) {
        // Implementação...
    }
    
    public boolean excluir(int id) {
        // Implementação...
    }
}
```

#### **3. Adicionar Protocolo**
```java
// Atualizar: Protocol.java
public static final String CMD_ADD_BUDGET = "ADD_BUDGET";
public static final String CMD_LIST_BUDGETS = "LIST_BUDGETS";
public static final String CMD_UPDATE_BUDGET = "UPDATE_BUDGET";
public static final String CMD_DELETE_BUDGET = "DELETE_BUDGET";
```

#### **4. Implementar no Servidor**
```java
// Atualizar: ClientHandler.java
private String processarComando(String comando) {
    String[] partes = comando.split("\\" + Protocol.SEPARATOR);
    
    switch (partes[0]) {
        // ... casos existentes
        case Protocol.CMD_ADD_BUDGET:
            return processarAdicionarOrcamento(partes);
        case Protocol.CMD_LIST_BUDGETS:
            return processarListarOrcamentos(partes);
        // ... outros casos
    }
}

private String processarAdicionarOrcamento(String[] partes) {
    // Validações e processamento
    Orcamento orcamento = new Orcamento();
    // Popular objeto...
    
    if (orcamentoDAO.inserir(orcamento)) {
        return Protocol.createSuccessResponse("Orçamento criado");
    } else {
        return Protocol.createErrorResponse("Erro ao criar orçamento");
    }
}
```

#### **5. Implementar Controller (Desktop)**
```java
// Atualizar: FinanceController.java
public OperationResult<Void> adicionarOrcamento(Orcamento orcamento) {
    if (!networkClient.isConnected()) {
        return new OperationResult<>(false, "Não conectado", null);
    }
    
    String comando = Protocol.CMD_ADD_BUDGET + Protocol.SEPARATOR +
                    orcamento.getIdCategoria() + Protocol.SEPARATOR +
                    orcamento.getValorLimite() + Protocol.SEPARATOR +
                    orcamento.getMes() + Protocol.SEPARATOR +
                    orcamento.getAno();
    
    String resposta = networkClient.sendCommand(comando);
    return processarResposta(resposta);
}
```

#### **6. Criar Interface (Desktop)**
```java
// Criar: ClienteFinanza/src/view/OrcamentosView.java
public class OrcamentosView extends JFrame {
    private JTable tabelaOrcamentos;
    private FinanceController financeController;
    
    public OrcamentosView() {
        initComponents();
        carregarOrcamentos();
    }
    
    private void initComponents() {
        // Configurar interface Swing
    }
    
    private void adicionarOrcamento() {
        // Abrir formulário para novo orçamento
    }
    
    private void carregarOrcamentos() {
        // Carregar lista do servidor
    }
}
```

#### **7. Atualizar Mobile (se necessário)**
```java
// Criar: app/src/main/java/ui/BudgetActivity.java
public class BudgetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        
        setupRecyclerView();
        loadBudgets();
    }
    
    private void addBudget() {
        // Implementar adição de orçamento
    }
}
```

#### **8. Atualizar Banco de Dados**
```sql
-- Criar: banco/update_orcamento.sql
CREATE TABLE orcamento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_categoria INT NOT NULL,
    id_usuario INT NOT NULL,
    valor_limite DECIMAL(10,2) NOT NULL,
    mes INT NOT NULL,
    ano INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    UNIQUE KEY uk_orcamento (id_categoria, id_usuario, mes, ano)
);
```

---

## 🐛 Debugging e Troubleshooting

### 🔍 **Problemas Comuns e Soluções**

#### **1. "Não conecta ao servidor"**
```java
// Debug: NetworkClient.java
public boolean connect() {
    try {
        socket = new Socket(serverIP, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // 🔍 DEBUG: Adicione logs
        System.out.println("Tentando conectar em: " + serverIP + ":" + serverPort);
        System.out.println("Conectado com sucesso!");
        
        return true;
    } catch (IOException e) {
        // 🔍 DEBUG: Log detalhado do erro
        System.err.println("Erro de conexão: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// ✅ VERIFICAÇÕES:
// 1. Servidor está rodando? (check porta 8080)
// 2. Firewall bloqueando?
// 3. IP correto?
// 4. MySQL está rodando?
```

#### **2. "Erro ao alterar senha"**
```java
// Debug: ClientHandler.java
private String processarAlterarSenha(String[] partes) {
    // 🔍 DEBUG: Log dos parâmetros recebidos
    System.out.println("Parâmetros recebidos: " + Arrays.toString(partes));
    
    if (usuarioLogado == null) {
        System.out.println("❌ Usuário não está logado");
        return Protocol.createErrorResponse("Usuário não está logado");
    }
    
    String senhaAtual = partes[1];
    String novaSenha = partes[2];
    
    // 🔍 DEBUG: Verificar autenticação
    Usuario usuario = usuarioDAO.autenticar(usuarioLogado.getEmail(), senhaAtual);
    if (usuario == null) {
        System.out.println("❌ Senha atual incorreta para: " + usuarioLogado.getEmail());
        return Protocol.createErrorResponse("Senha atual incorreta");
    }
    
    // 🔍 DEBUG: Verificar update
    boolean resultado = usuarioDAO.atualizarSenha(usuarioLogado.getId(), novaSenha);
    System.out.println("Resultado do update: " + resultado);
    
    return resultado ? 
        Protocol.createSuccessResponse("Senha alterada") :
        Protocol.createErrorResponse("Erro no banco de dados");
}

// ✅ VERIFICAÇÕES:
// 1. Usuário está realmente logado?
// 2. Senha atual está correta?
// 3. Conexão com MySQL funciona?
// 4. Hash da senha está sendo gerado corretamente?
```

#### **3. "Dados não sincronizam"**
```java
// Debug: SyncService.java (Mobile)
public void syncWithServer() {
    // 🔍 DEBUG: Log do processo de sync
    Log.d("SyncService", "Iniciando sincronização...");
    
    List<Movimentacao> pendingMovements = movimentacaoDao.getPendingSync();
    Log.d("SyncService", "Movimentações pendentes: " + pendingMovements.size());
    
    for (Movimentacao mov : pendingMovements) {
        try {
            boolean success = serverClient.sendMovement(mov);
            Log.d("SyncService", "Sync movimento " + mov.getId() + ": " + success);
            
            if (success) {
                mov.setSyncStatus(SyncStatus.SYNCED);
                movimentacaoDao.update(mov);
            }
        } catch (Exception e) {
            Log.e("SyncService", "Erro no sync: " + e.getMessage(), e);
        }
    }
}

// ✅ VERIFICAÇÕES:
// 1. Mobile está conectado ao servidor?
// 2. Dados estão marcados como "pending_sync"?
// 3. Servidor está processando comandos?
// 4. Não há conflitos de timestamp?
```

### 🛠️ **Ferramentas de Debug**

#### **1. Logs Estruturados**
```java
// Implementar: util/Logger.java
public class Logger {
    private static final boolean DEBUG_MODE = true;
    
    public static void debug(String tag, String message) {
        if (DEBUG_MODE) {
            System.out.println("[DEBUG][" + tag + "] " + message);
        }
    }
    
    public static void error(String tag, String message, Exception e) {
        System.err.println("[ERROR][" + tag + "] " + message);
        if (e != null) e.printStackTrace();
    }
}

// Uso:
Logger.debug("NetworkClient", "Enviando comando: " + comando);
Logger.error("DAO", "Erro no banco", e);
```

#### **2. Monitor de Conexões**
```java
// Implementar: server/ConnectionMonitor.java
public class ConnectionMonitor {
    private static Map<String, ClientHandler> connections = new ConcurrentHashMap<>();
    
    public static void addConnection(String clientId, ClientHandler handler) {
        connections.put(clientId, handler);
        System.out.println("📱 Cliente conectado: " + clientId + " (Total: " + connections.size() + ")");
    }
    
    public static void removeConnection(String clientId) {
        connections.remove(clientId);
        System.out.println("📱 Cliente desconectado: " + clientId + " (Total: " + connections.size() + ")");
    }
    
    public static void printStatus() {
        System.out.println("🌐 Conexões ativas: " + connections.size());
        connections.keySet().forEach(id -> System.out.println("  - " + id));
    }
}
```

---

## 💻 Ambiente de Desenvolvimento

### ⚙️ **Setup Completo**

#### **1. Pré-requisitos**
```bash
# Java Development Kit
java -version    # Deve ser 8+
javac -version

# MySQL Server
mysql --version  # Deve ser 8.0+

# IDEs
# Desktop: NetBeans 12+ ou IntelliJ IDEA
# Mobile: Android Studio 4.0+

# Git
git --version
```

#### **2. Configuração do Banco**
```sql
-- 1. Criar database
CREATE DATABASE finanza;
USE finanza;

-- 2. Executar scripts
source DESKTOP\ VERSION/banco/script_inicial.sql;

-- 3. Verificar tabelas
SHOW TABLES;
DESCRIBE usuario;
```

#### **3. Configuração do Servidor**
```bash
# 1. Compilar servidor
cd "DESKTOP VERSION/ServidorFinanza"
javac -cp "lib/*" src/**/*.java -d build/

# 2. Executar servidor  
java -cp "build:lib/*" MainServidor

# 3. Verificar logs
# Deve aparecer: "Servidor iniciado na porta 8080"
```

#### **4. Configuração do Cliente Desktop**
```bash
# 1. Compilar cliente
cd "DESKTOP VERSION/ClienteFinanza"  
javac -cp "lib/*" src/**/*.java -d build/

# 2. Executar cliente
java -cp "build:lib/*" MainCliente

# 3. Testar conexão
# IP: localhost (ou IP do servidor)
# Porta: 8080
```

#### **5. Configuração Mobile**
```bash
# 1. Abrir no Android Studio
# File → Open → selecionar pasta 'app'

# 2. Sync Gradle
# Build → Make Project

# 3. Configurar emulador ou device físico

# 4. Executar app
# Run → Run 'app'
```

### 🔧 **Scripts Úteis**

#### **Scripts de Build**
```bash
# build.sh
#!/bin/bash
echo "🏗️ Building Finanza System..."

# Build Server
echo "📦 Building Server..."
cd "DESKTOP VERSION/ServidorFinanza"
javac -cp "lib/*" src/**/*.java -d build/

# Build Client  
echo "📦 Building Client..."
cd "../ClienteFinanza"
javac -cp "lib/*" src/**/*.java -d build/

echo "✅ Build completed!"
```

#### **Scripts de Deploy**
```bash
# deploy.sh
#!/bin/bash
echo "🚀 Deploying Finanza System..."

# Start MySQL
sudo systemctl start mysql

# Start Server
cd "DESKTOP VERSION/ServidorFinanza"
java -cp "build:lib/*" MainServidor &
SERVER_PID=$!

echo "✅ Server started (PID: $SERVER_PID)"
echo "🌐 Server running on port 8080"
```

---

## 🎯 Casos de Uso Comuns

### 📋 **1. "Preciso adicionar um novo campo na movimentação"**

#### **Passo a passo:**
```java
// 1. Atualizar Model
// model/Movimentacao.java
private String observacoes; // Novo campo

public String getObservacoes() { return observacoes; }
public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

// 2. Atualizar Banco
// banco/update_add_observacoes.sql
ALTER TABLE movimentacao ADD COLUMN observacoes TEXT;

// 3. Atualizar DAO
// MovimentacaoDAO.java
// No método inserir():
stmt.setString(7, mov.getObservacoes()); // Novo parâmetro

// No método mapResultSetToMovimentacao():
mov.setObservacoes(rs.getString("observacoes"));

// 4. Atualizar Protocolo
// Protocol.java - adicionar novo parâmetro no comando
// ADD_MOVEMENT|desc|valor|data|tipo|conta|categoria|observacoes

// 5. Atualizar Controller
// FinanceController.java
String comando = CMD_ADD_MOVEMENT + SEPARATOR + /* outros campos */ + SEPARATOR + mov.getObservacoes();

// 6. Atualizar Views
// MovimentacaoFormDialog.java - adicionar novo campo no formulário
private JTextArea campoObservacoes = new JTextArea(3, 20);
```

### 📋 **2. "Quero criar um novo tipo de relatório"**

#### **Implementação:**
```java
// 1. Criar método no DAO
// MovimentacaoDAO.java
public List<Movimentacao> relatorioGastosPorSemana(int idUsuario, Date inicio, Date fim) {
    String sql = "SELECT *, WEEK(data) as semana FROM movimentacao WHERE id_usuario = ? AND data BETWEEN ? AND ? AND tipo = 'DESPESA' ORDER BY semana, data";
    // Implementação...
}

// 2. Adicionar comando no protocolo  
// Protocol.java
public static final String CMD_REPORT_WEEKLY = "REPORT_WEEKLY";

// 3. Implementar no servidor
// ClientHandler.java
case Protocol.CMD_REPORT_WEEKLY:
    return processarRelatorioSemanal(partes);

private String processarRelatorioSemanal(String[] partes) {
    Date inicio = Date.valueOf(partes[1]); // formato: YYYY-MM-DD
    Date fim = Date.valueOf(partes[2]);
    
    List<Movimentacao> dados = movimentacaoDAO.relatorioGastosPorSemana(usuarioLogado.getId(), inicio, fim);
    return Protocol.createSuccessResponse(serializarMovimentacoes(dados));
}

// 4. Implementar no controller
// FinanceController.java
public OperationResult<List<Movimentacao>> relatorioSemanal(Date inicio, Date fim) {
    String comando = Protocol.CMD_REPORT_WEEKLY + Protocol.SEPARATOR + inicio + Protocol.SEPARATOR + fim;
    String resposta = networkClient.sendCommand(comando);
    return processarRespostaLista(resposta, Movimentacao.class);
}

// 5. Criar interface
// RelatoriosView.java - adicionar novo botão e método
private void gerarRelatorioSemanal() {
    // Coleta período
    Date inicio = datePickerInicio.getDate();
    Date fim = datePickerFim.getDate();
    
    // Busca dados
    OperationResult<List<Movimentacao>> result = financeController.relatorioSemanal(inicio, fim);
    
    if (result.isSucesso()) {
        exibirRelatorioSemanal(result.getDados());
    }
}
```

### 📋 **3. "Como adicionar uma nova validação?"**

#### **Implementação de validação robusta:**
```java
// 1. Criar classe de validação
// util/ValidationUtil.java
public class ValidationUtil {
    public static ValidationResult validarMovimentacao(Movimentacao mov) {
        if (mov.getDescricao() == null || mov.getDescricao().trim().isEmpty()) {
            return ValidationResult.error("Descrição é obrigatória");
        }
        
        if (mov.getDescricao().length() > 200) {
            return ValidationResult.error("Descrição deve ter no máximo 200 caracteres");
        }
        
        if (mov.getValor() == null || mov.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.error("Valor deve ser maior que zero");
        }
        
        if (mov.getData() == null) {
            return ValidationResult.error("Data é obrigatória");
        }
        
        if (mov.getData().after(new Date())) {
            return ValidationResult.error("Data não pode ser futura");
        }
        
        return ValidationResult.success();
    }
}

// 2. Usar validação no controller
// FinanceController.java
public OperationResult<Void> adicionarMovimentacao(Movimentacao mov) {
    // Validar primeiro
    ValidationResult validation = ValidationUtil.validarMovimentacao(mov);
    if (!validation.isValid()) {
        return new OperationResult<>(false, validation.getMessage(), null);
    }
    
    // Continuar processamento...
}

// 3. Usar validação no servidor
// ClientHandler.java
private String processarAdicionarMovimentacao(String[] partes) {
    Movimentacao mov = construirMovimentacao(partes);
    
    // Validar no servidor também
    ValidationResult validation = ValidationUtil.validarMovimentacao(mov);
    if (!validation.isValid()) {
        return Protocol.createResponse("INVALID_DATA", validation.getMessage());
    }
    
    // Continuar processamento...
}
```

Este guia te dá um **mapa completo** de como navegar, entender e modificar o sistema Finanza. Use-o como referência sempre que precisar trabalhar com o código!