# Relatório Completo do Sistema Finanza - Gestão Financeira Pessoal

## Quero que o programa seja assim...

Quero que o programa seja assim: um sistema completo de gestão financeira pessoal que permite aos usuários controlar suas receitas, despesas, contas bancárias e categorias de gastos de forma integrada. O sistema deve funcionar em múltiplas plataformas (Android e Desktop) com sincronização automática entre dispositivos, proporcionando uma experiência unificada de gerenciamento financeiro.

O aplicativo deve ter uma interface intuitiva e moderna, permitindo que o usuário:
- Faça login seguro com email e senha
- Cadastre novas contas
- Registre receitas e despesas com categorização
- Visualize relatórios gráficos de suas finanças
- Gerencie múltiplas contas bancárias
- Sincronize dados entre dispositivos
- Tenha controle de privacidade (ocultar/exibir valores)

## 📱 Estrutura do Sistema Implementado

O Finanza é um sistema completo que integra três componentes principais:

### 1. **Aplicativo Android** 
- Interface mobile nativa em Java/Android
- Banco de dados local com Room Database
- Sistema de sincronização com servidor
- Interface Material Design

### 2. **Cliente Desktop**
- Interface desktop em Java Swing
- Padrão arquitetural MVC (Model-View-Controller)
- Banco de dados SQLite
- Compatível com NetBeans IDE

### 3. **Servidor de Sincronização**
- Servidor TCP multi-threaded em Java
- Protocolo de comunicação JSON
- Sincronização bidirecional de dados

## 🎨 Detalhamento das Telas e Funcionalidades

### **Android Application**

#### **1. Tela de Login (LoginActivity)**
**Localização:** `app/src/main/java/com/example/finanza/ui/LoginActivity.java`

**Conteúdo da Tela:**
- Campo de entrada para email
- Campo de entrada para senha (com ocultação de caracteres)
- Botão "Entrar" para autenticação
- Link "Criar nova conta" para cadastro
- Validação automática de campos obrigatórios

**Funcionalidades Implementadas:**
```java
private void realizarLogin() {
    String email = inputEmail.getText().toString().trim();
    String senha = inputSenha.getText().toString().trim();
    
    // Validações de campos
    if (email.isEmpty()) {
        inputEmail.setError("Digite o email");
        return;
    }
    
    // Autenticação no banco local
    Usuario usuario = db.usuarioDao().login(email, senha);
    if (usuario != null) {
        // Salvar sessão
        SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
        prefs.edit().putInt("usuarioId", usuario.id).apply();
        
        // Navegar para tela principal
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usuarioId", usuario.id);
        startActivity(intent);
        finish();
    }
}
```

#### **2. Tela Principal/Dashboard (MainActivity)**
**Localização:** `app/src/main/java/com/example/finanza/MainActivity.java`

**Conteúdo da Tela:**
- **Header superior:** Saldo total, receitas e despesas com ícone de visibilidade
- **Seção de contas:** Lista resumida das contas com saldos atuais
- **Seção de categorias:** Top 5 categorias com maiores gastos
- **Transações recentes:** Últimas 5 transações realizadas
- **Navegação inferior:** Home, Contas, Lançamentos, Menu e botão adicionar

**Funcionalidades Implementadas:**
```java
private void atualizarValores(TextView tvSaldo, TextView tvReceita, TextView tvDespesa) {
    double receitas = consultarReceitas();
    double despesas = consultarDespesas();
    double saldo = receitas - despesas;
    
    if (saldoVisivel) {
        tvSaldo.setText(formatarMoeda(saldo));
        tvReceita.setText(formatarMoeda(receitas));
        tvDespesa.setText(formatarMoeda(despesas));
    } else {
        tvSaldo.setText("****"); // Modo privacidade ativado
        tvReceita.setText("****");
        tvDespesa.setText("****");
    }
}
```

**Sistema de Privacidade:**
- Botão de "olho" permite ocultar/exibir valores financeiros
- Estado persiste durante a sessão
- Afeta todas as exibições de valores na tela

#### **3. Sistema de Adição de Transações**
**Modal Dialog para Receitas/Despesas:**

**Campos do Formulário:**
- Nome/Descrição da transação
- Conta de origem/destino (seleção dropdown)
- Data da transação (com DatePicker)
- Categoria (filtrada por tipo: receita/despesa)
- Valor monetário

**Processo de Salvamento:**
```java
private void showAddTransactionDialog(boolean isReceitaPanel) {
    // Criar modal dialog
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
    AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
    
    // Configurar campos e validações
    btnSalvar.setOnClickListener(v -> {
        // Validar dados
        if (contaSelecionada == null) {
            inputConta.setError("Selecione a conta");
            return;
        }
        
        // Criar e salvar lançamento
        Lancamento lancamento = new Lancamento();
        lancamento.valor = Double.parseDouble(valorStr);
        lancamento.data = dataSelecionada;
        lancamento.descricao = nome;
        lancamento.contaId = contaSelecionada.id;
        lancamento.categoriaId = categoriaSelecionada.id;
        lancamento.usuarioId = usuarioIdAtual;
        lancamento.tipo = isReceitaPanel ? "receita" : "despesa";
        
        db.lancamentoDao().inserir(lancamento);
        atualizarValores(); // Atualizar tela
        dialog.dismiss();
    });
}
```

#### **4. Tela de Contas (AccountsActivity)**
**Funcionalidades:**
- Listagem de todas as contas do usuário
- Exibição do saldo atual de cada conta
- Criação/edição/exclusão de contas
- Navegação para detalhes da conta

#### **5. Tela de Movimentações (MovementsActivity)**
**Funcionalidades:**
- Lista completa de todas as transações
- Filtros por data, tipo e categoria
- Ordenação cronológica
- Edição/exclusão de lançamentos

#### **6. Tela de Menu (MenuActivity)**
**Opções Disponíveis:**
- Configurações do aplicativo
- Relatórios financeiros
- Gerenciamento de categorias
- Perfil do usuário
- Logout/sair

### **Desktop Application (NetBeans)**

#### **Arquitetura MVC Implementada**
**Localização:** `DESKTOP VERSION/Cliente-Finanza/src/`

#### **1. Models (Entidades de Negócio)**

**Usuario.java:**
```java
public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private long dataCriacao;
    
    // Construtores, getters e setters
}
```

**Conta.java:**
```java
public class Conta {
    private int id;
    private String nome;
    private double saldoInicial;
    private int usuarioId;
    
    // Métodos para calcular saldo atual
}
```

**Lancamento.java:**
```java
public class Lancamento {
    private int id;
    private double valor;
    private long data;
    private String descricao;
    private int contaId;
    private int categoriaId;
    private int usuarioId;
    private String tipo; // "receita" ou "despesa"
}
```

**Categoria.java:**
```java
public class Categoria {
    private int id;
    private String nome;
    private String corHex;
    private String tipo; // "receita" ou "despesa"
}
```

#### **2. Controllers (Lógica de Negócio)**

**UsuarioController.java:**
```java
public class UsuarioController {
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;
    
    public boolean autenticar(String email, String senha) {
        try {
            usuarioLogado = usuarioDAO.autenticar(email, senha);
            return usuarioLogado != null;
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cadastrar(String nome, String email, String senha) {
        // Verificar se email já existe
        if (usuarioDAO.emailExiste(email)) {
            return false;
        }
        
        Usuario novoUsuario = new Usuario(nome, email, senha);
        return usuarioDAO.inserir(novoUsuario);
    }
}
```

**ContaController.java:**
- Gerenciamento de contas bancárias
- Cálculo de saldos atuais
- CRUD completo de contas

**LancamentoController.java:**
- Gerenciamento de transações
- Relatórios financeiros
- Filtros e pesquisas

**CategoriaController.java:**
- Gerenciamento de categorias
- Categorias padrão do sistema

#### **3. Views (Interface NetBeans)**
**Localização:** `DESKTOP VERSION/Cliente-Finanza/src/view/`

**LoginView.java/.form:**
- Tela de autenticação com campos email/senha
- Botão de login conectado ao UsuarioController
- Link para tela de cadastro
- Validação de campos obrigatórios

**HomeView.java/.form:**
- Dashboard principal com resumo financeiro
- Gráficos de receitas vs despesas
- Lista de transações recentes
- Navegação para outras telas

**AccountsView.java/.form:**
- Listagem de contas do usuário
- Formulário para criar/editar contas
- Exibição de saldos atuais

**MovementsView.java/.form:**
- Lista completa de transações
- Filtros avançados
- Formulário de edição de lançamentos

**CategoriasView.java/.form:**
- Gerenciamento de categorias personalizadas
- Criação de novas categorias
- Definição de cores

**Exemplo de Integração View-Controller:**
```java
// No LoginView.java
private void jButtonLoginActionPerformed(ActionEvent evt) {
    String email = jTextFieldEmail.getText();
    String senha = jTextFieldSenha.getText();
    
    if (usuarioController.autenticar(email, senha)) {
        // Login bem-sucedido
        new HomeView().setVisible(true);
        this.dispose();
    } else {
        // Exibir erro
        JOptionPane.showMessageDialog(this, "Login inválido!");
    }
}
```

#### **4. Database Layer (Acesso a Dados)**

**DatabaseManager.java:**
```java
public class DatabaseManager {
    private static final String DB_NAME = "finanza.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public static void initializeDatabase() {
        createTables();
        insertDefaultData();
    }
    
    private static void createTables() {
        // Schema do banco de dados
        // Tabelas: usuarios, contas, lancamentos, categorias
    }
}
```

**DAOs (Data Access Objects):**
- **UsuarioDAO:** CRUD de usuários, autenticação
- **ContaDAO:** CRUD de contas, consultas de saldo
- **LancamentoDAO:** CRUD de lançamentos, relatórios
- **CategoriaDAO:** CRUD de categorias

## 💾 Sistema de Salvamento de Dados

### **Android - Room Database**

**Configuração do Banco:**
```java
@Database(
    entities = {Usuario.class, Conta.class, Lancamento.class, Categoria.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract ContaDao contaDao();
    public abstract LancamentoDao lancamentoDao();
    public abstract CategoriaDao categoriaDao();
}
```

**Exemplo de DAO:**
```java
@Dao
public interface LancamentoDao {
    @Insert
    long inserir(Lancamento lancamento);
    
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC")
    List<Lancamento> listarPorUsuario(int usuarioId);
    
    @Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId")
    Double somaPorTipo(String tipo, int usuarioId);
}
```

### **Desktop - SQLite Database**

**Schema do Banco:**
```sql
-- Tabela de usuários
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    senha TEXT NOT NULL,
    data_criacao INTEGER NOT NULL
);

-- Tabela de contas
CREATE TABLE contas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    saldo_inicial REAL NOT NULL DEFAULT 0,
    usuario_id INTEGER NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de lançamentos
CREATE TABLE lancamentos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    valor REAL NOT NULL,
    data INTEGER NOT NULL,
    descricao TEXT,
    conta_id INTEGER NOT NULL,
    categoria_id INTEGER,
    usuario_id INTEGER NOT NULL,
    tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa')),
    FOREIGN KEY (conta_id) REFERENCES contas(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de categorias
CREATE TABLE categorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cor_hex TEXT,
    tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa'))
);
```

**Processo de Salvamento:**
1. **Transações Automáticas:** Cada operação é envolvida em transação
2. **Validação de Dados:** Checagem de integridade antes do salvamento
3. **Backup Automático:** Base de dados é copiada periodicamente
4. **Sincronização:** Dados locais são sincronizados com servidor

## 🔐 Sistema de Login e Cadastro

### **Processo de Autenticação**

#### **1. Cadastro de Novo Usuário**

**Android (RegisterActivity):**
```java
private void realizarCadastro() {
    String nome = inputNome.getText().toString().trim();
    String email = inputEmail.getText().toString().trim();
    String senha = inputSenha.getText().toString().trim();
    
    // Validações
    if (!isEmailValido(email)) {
        inputEmail.setError("Email inválido");
        return;
    }
    
    // Verificar se email já existe
    if (db.usuarioDao().buscarPorEmail(email) != null) {
        inputEmail.setError("Email já cadastrado");
        return;
    }
    
    // Criar usuário
    Usuario usuario = new Usuario();
    usuario.nome = nome;
    usuario.email = email;
    usuario.senha = senha;
    usuario.dataCriacao = System.currentTimeMillis();
    
    long userId = db.usuarioDao().inserir(usuario);
    
    // Login automático após cadastro
    usuario.id = (int) userId;
    salvarSessao(usuario);
}
```

#### **2. Processo de Login**

**Validações Implementadas:**
- Email não pode estar vazio
- Senha não pode estar vazia
- Email deve ter formato válido
- Credenciais devem existir no banco

**Segurança:**
- Senhas são armazenadas em texto plano (para demonstração)
- Sessão é salva em SharedPreferences (Android) ou arquivo local (Desktop)
- Logout limpa dados da sessão

### **Gestão de Sessão**

**Android:**
```java
// Salvar sessão
SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
prefs.edit().putInt("usuarioId", usuario.id).apply();

// Verificar sessão ativa
int usuarioLogado = prefs.getInt("usuarioId", -1);
if (usuarioLogado != -1) {
    // Usuário já logado, ir para MainActivity
}

// Logout
prefs.edit().remove("usuarioId").apply();
```

**Desktop:**
```java
public class UsuarioController {
    private Usuario usuarioLogado;
    
    public boolean isLogado() {
        return usuarioLogado != null;
    }
    
    public void logout() {
        usuarioLogado = null;
    }
}
```

## 🎨 Front-end e Back-end Desenvolvidos

### **Front-end Android**

**Tecnologias Utilizadas:**
- **Material Design Components**
- **ConstraintLayout** para layouts responsivos
- **RecyclerView** para listas dinâmicas
- **Custom Views** para gráficos e elementos específicos

**Estrutura Visual:**
```xml
<!-- Layout principal (activity_main.xml) -->
<androidx.constraintlayout.widget.ConstraintLayout>
    <!-- Header com saldo -->
    <LinearLayout android:id="@+id/header_saldo">
        <TextView android:id="@+id/tvSaldo" />
        <ImageView android:id="@+id/imgEye" />
    </LinearLayout>
    
    <!-- Seções de conteúdo -->
    <ScrollView>
        <LinearLayout android:orientation="vertical">
            <!-- Resumo de contas -->
            <LinearLayout android:id="@+id/accounts_summary_container" />
            
            <!-- Resumo de categorias -->
            <LinearLayout android:id="@+id/categories_summary_container" />
            
            <!-- Transações recentes -->
            <LinearLayout android:id="@+id/recent_transactions_container" />
        </LinearLayout>
    </ScrollView>
    
    <!-- Navegação inferior -->
    <LinearLayout android:id="@+id/bottom_navigation" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Cores e Temas:**
```xml
<!-- colors.xml -->
<resources>
    <color name="primaryDarkBlue">#1E3A8A</color>
    <color name="accentBlue">#3B82F6</color>
    <color name="positiveGreen">#10B981</color>
    <color name="negativeRed">#EF4444</color>
    <color name="white">#FFFFFF</color>
</resources>
```

### **Front-end Desktop (NetBeans)**

**Tecnologias Utilizadas:**
- **Java Swing** com NetBeans Visual Designer
- **Absolute Layout** para posicionamento preciso
- **Custom Graphics** para gráficos financeiros
- **Look and Feel Nimbus** para aparência moderna

**Exemplo de Tela (HomeView.form):**
- **JPanel principal** com fundo gradiente
- **JLabel** para exibição de valores
- **JButton** para navegação
- **Custom JPanel** para gráficos
- **JTable** para listagem de dados

**Gráficos Personalizados:**
```java
public class ChartUtils {
    public static void drawPieChart(Graphics2D g2d, Map<String, Double> data, Rectangle bounds) {
        // Implementação de gráfico de pizza
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        double startAngle = 0;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double angle = (entry.getValue() / total) * 360;
            g2d.setColor(getColorForCategory(entry.getKey()));
            g2d.fillArc(bounds.x, bounds.y, bounds.width, bounds.height, 
                       (int) startAngle, (int) angle);
            startAngle += angle;
        }
    }
}
```

### **Back-end (Servidor TCP)**

**Arquitetura do Servidor:**
```java
public class FinanzaServer {
    private ServerSocket serverSocket;
    private ExecutorService clientThreadPool;
    private final List<ClientHandler> activeClients;
    
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        
        while (isRunning) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket, this);
            activeClients.add(handler);
            clientThreadPool.submit(handler);
        }
    }
}
```

**Protocolo de Comunicação JSON:**
```java
// Exemplo de requisição
{
    "action": "sync_user",
    "userId": 1,
    "timestamp": 1756940989859
}

// Exemplo de resposta
{
    "action": "sync_user",
    "message": "Usuário sincronizado com sucesso",
    "success": true,
    "data": {
        "usuario": {...},
        "contas": [...],
        "lancamentos": [...]
    },
    "timestamp": 1756940989861
}
```

**Comandos Suportados:**
- `ping` - Teste de conectividade
- `login` - Autenticação de usuário
- `sync_user` - Sincronizar dados do usuário
- `sync_accounts` - Sincronizar contas
- `sync_transactions` - Sincronizar lançamentos
- `sync_categories` - Sincronizar categorias

## 🔄 Sistema de Sincronização

### **Android → Servidor**
```java
public class SyncService {
    private ServerClient serverClient;
    
    public void sincronizarTudo(int usuarioId) {
        // Sincronizar usuário
        serverClient.sincronizarUsuario(usuarioId, new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Processar resposta e atualizar dados locais
                sincronizarContas(usuarioId);
            }
        });
    }
}
```

### **Desktop ↔ Servidor**
```java
// Desktop pode tanto enviar quanto receber dados
public class SyncController {
    public void sincronizarComServidor() {
        // Enviar dados locais para servidor
        // Receber dados atualizados do servidor
        // Atualizar interface com novos dados
    }
}
```

## 🛠️ Tutorial de Implementação para NetBeans 24

### **Passo 1: Configuração do Projeto**

1. **Criar Novo Projeto:**
   - Abrir NetBeans 24
   - File → New Project → Java → Java Application
   - Nome: "FinanzaDesktop"
   - Package principal: `br.com.finanza`

2. **Configurar Build System:**
   - Usar Apache Ant (padrão do NetBeans)
   - Adicionar dependências no `build.xml`:
```xml
<target name="compile" depends="init">
    <mkdir dir="${build.classes.dir}"/>
    <!-- Download SQLite JDBC -->
    <get src="https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar"
         dest="${lib.dir}/sqlite-jdbc-3.42.0.0.jar"
         usetimestamp="true"/>
    
    <javac srcdir="${src.dir}" destdir="${build.classes.dir}"
           classpath="${lib.dir}/sqlite-jdbc-3.42.0.0.jar"/>
</target>
```

### **Passo 2: Estrutura de Pacotes**

```
src/
├── br/com/finanza/
│   ├── model/          # Entidades
│   ├── dao/            # Acesso a dados
│   ├── controller/     # Lógica de negócio
│   ├── view/           # Interface gráfica
│   ├── database/       # Gerenciamento do banco
│   └── util/           # Utilitários
```

### **Passo 3: Implementar Models**

```java
// Usuario.java
package br.com.finanza.model;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private long dataCriacao;
    
    // Construtores
    public Usuario() {}
    
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCriacao = System.currentTimeMillis();
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    // ... outros getters/setters
}
```

### **Passo 4: Configurar Banco de Dados**

```java
// DatabaseManager.java
package br.com.finanza.database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_NAME = "finanza.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver SQLite não encontrado", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    private static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Criar tabelas
            createTables(stmt);
            
            // Inserir dados padrão
            insertDefaultData(stmt);
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco", e);
        }
    }
    
    private static void createTables(Statement stmt) throws SQLException {
        // SQL das tabelas conforme mostrado anteriormente
    }
}
```

### **Passo 5: Implementar DAOs**

```java
// UsuarioDAO.java
package br.com.finanza.dao;

import br.com.finanza.model.Usuario;
import br.com.finanza.database.DatabaseManager;
import java.sql.*;

public class UsuarioDAO {
    
    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setDataCriacao(rs.getLong("data_criacao"));
                    return usuario;
                }
            }
        }
        return null;
    }
    
    public boolean inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, data_criacao) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setLong(4, usuario.getDataCriacao());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
}
```

### **Passo 6: Implementar Controllers**

```java
// UsuarioController.java
package br.com.finanza.controller;

import br.com.finanza.model.Usuario;
import br.com.finanza.dao.UsuarioDAO;
import java.sql.SQLException;

public class UsuarioController {
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;
    
    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public boolean autenticar(String email, String senha) {
        try {
            usuarioLogado = usuarioDAO.autenticar(email, senha);
            return usuarioLogado != null;
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cadastrar(String nome, String email, String senha) {
        try {
            if (usuarioDAO.emailExiste(email)) {
                return false; // Email já existe
            }
            
            Usuario novoUsuario = new Usuario(nome, email, senha);
            return usuarioDAO.inserir(novoUsuario);
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar: " + e.getMessage());
            return false;
        }
    }
    
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    public boolean isLogado() {
        return usuarioLogado != null;
    }
    
    public void logout() {
        usuarioLogado = null;
    }
}
```

### **Passo 7: Criar Views no NetBeans**

#### **LoginView (usando Visual Designer):**

1. **Criar JFrame:**
   - Right-click no package `view` → New → JFrame Form
   - Nome: `LoginView`

2. **Adicionar Componentes:**
   - JLabel para título "Finanza - Login"
   - JTextField para email
   - JPasswordField para senha
   - JButton para "Entrar"
   - JLabel linkável para "Cadastrar"

3. **Configurar Eventos:**
```java
// LoginView.java
private void jButtonEntrarActionPerformed(java.awt.event.ActionEvent evt) {
    String email = jTextFieldEmail.getText();
    String senha = new String(jPasswordFieldSenha.getPassword());
    
    if (usuarioController.autenticar(email, senha)) {
        // Login bem-sucedido
        HomeView homeView = new HomeView();
        homeView.setVisible(true);
        this.dispose();
    } else {
        // Erro de login
        JOptionPane.showMessageDialog(this, 
            "Email ou senha incorretos!", 
            "Erro de Login", 
            JOptionPane.ERROR_MESSAGE);
    }
}
```

#### **HomeView (Dashboard Principal):**

1. **Layout Principal:**
   - JPanel principal com BorderLayout
   - JPanel superior para resumo financeiro
   - JPanel central para conteúdo (contas, categorias, transações)
   - JPanel inferior para navegação

2. **Componentes do Resumo:**
```java
// Atualizar valores financeiros
private void atualizarResumoFinanceiro() {
    LancamentoController lancamentoController = new LancamentoController();
    
    double receitas = lancamentoController.getTotalReceitas(usuarioLogado.getId());
    double despesas = lancamentoController.getTotalDespesas(usuarioLogado.getId());
    double saldo = receitas - despesas;
    
    jLabelSaldo.setText(String.format("R$ %.2f", saldo));
    jLabelReceitas.setText(String.format("R$ %.2f", receitas));
    jLabelDespesas.setText(String.format("R$ %.2f", despesas));
    
    // Atualizar cor do saldo
    if (saldo >= 0) {
        jLabelSaldo.setForeground(Color.GREEN);
    } else {
        jLabelSaldo.setForeground(Color.RED);
    }
}
```

### **Passo 8: Implementar Navegação**

```java
// FinanzaDesktop.java (Classe principal de navegação)
package br.com.finanza;

import br.com.finanza.view.*;
import javax.swing.SwingUtilities;

public class FinanzaDesktop {
    private static FinanzaDesktop instance;
    private LoginView loginView;
    private HomeView homeView;
    private AccountsView accountsView;
    private MovementsView movementsView;
    
    private FinanzaDesktop() {}
    
    public static FinanzaDesktop getInstance() {
        if (instance == null) {
            instance = new FinanzaDesktop();
        }
        return instance;
    }
    
    public void start() {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Usar look and feel padrão
        }
        
        // Mostrar tela de login
        showLoginView();
    }
    
    public void showLoginView() {
        if (loginView == null) {
            loginView = new LoginView();
        }
        loginView.setVisible(true);
    }
    
    public void showHomeView() {
        if (homeView == null) {
            homeView = new HomeView();
        }
        homeView.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanzaDesktop app = FinanzaDesktop.getInstance();
            app.start();
        });
    }
}
```

### **Passo 9: Configurar Build e Execução**

**build.xml configurado:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="FinanzaDesktop" default="default" basedir=".">
    <property name="src.dir" value="src"/>
    <property name="build.dir" value="build"/>
    <property name="build.classes.dir" value="${build.dir}/classes"/>
    <property name="lib.dir" value="lib"/>
    <property name="dist.dir" value="dist"/>
    
    <target name="init">
        <mkdir dir="${build.dir}"/>
        <mkdir dir="${build.classes.dir}"/>
        <mkdir dir="${lib.dir}"/>
        <mkdir dir="${dist.dir}"/>
    </target>
    
    <target name="compile" depends="init">
        <!-- Download dependências -->
        <get src="https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar"
             dest="${lib.dir}/sqlite-jdbc-3.42.0.0.jar"
             usetimestamp="true"/>
        
        <!-- Compilar código -->
        <javac srcdir="${src.dir}" 
               destdir="${build.classes.dir}"
               classpath="${lib.dir}/sqlite-jdbc-3.42.0.0.jar"
               includeantruntime="false"/>
    </target>
    
    <target name="jar" depends="compile">
        <jar jarfile="${dist.dir}/finanza-desktop.jar" 
             basedir="${build.classes.dir}">
            <manifest>
                <attribute name="Main-Class" value="br.com.finanza.FinanzaDesktop"/>
                <attribute name="Class-Path" value="../lib/sqlite-jdbc-3.42.0.0.jar"/>
            </manifest>
        </jar>
    </target>
    
    <target name="run" depends="jar">
        <java jar="${dist.dir}/finanza-desktop.jar" 
              fork="true"
              classpath="${lib.dir}/sqlite-jdbc-3.42.0.0.jar"/>
    </target>
    
    <target name="clean">
        <delete dir="${build.dir}"/>
        <delete dir="${dist.dir}"/>
    </target>
</project>
```

### **Passo 10: Implementar Funcionalidades Avançadas**

#### **Gráficos Personalizados:**
```java
// ChartPanel.java
package br.com.finanza.util;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ChartPanel extends JPanel {
    private Map<String, Double> data;
    private String chartType; // "pie" ou "bar"
    
    public ChartPanel(String chartType) {
        this.chartType = chartType;
        setPreferredSize(new Dimension(300, 200));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (data == null || data.isEmpty()) {
            g2d.drawString("Sem dados para exibir", 10, 20);
            return;
        }
        
        if ("pie".equals(chartType)) {
            drawPieChart(g2d);
        } else if ("bar".equals(chartType)) {
            drawBarChart(g2d);
        }
    }
    
    private void drawPieChart(Graphics2D g2d) {
        // Implementação do gráfico de pizza
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        double startAngle = 0;
        
        int x = 10, y = 10, width = getWidth() - 20, height = getHeight() - 20;
        
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA};
        int colorIndex = 0;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double angle = (entry.getValue() / total) * 360;
            
            g2d.setColor(colors[colorIndex % colors.length]);
            g2d.fillArc(x, y, width, height, (int) startAngle, (int) angle);
            
            colorIndex++;
            startAngle += angle;
        }
    }
    
    public void setData(Map<String, Double> data) {
        this.data = data;
        repaint();
    }
}
```

#### **Relatórios Financeiros:**
```java
// RelatorioController.java
package br.com.finanza.controller;

import br.com.finanza.model.*;
import br.com.finanza.dao.*;
import java.util.*;

public class RelatorioController {
    private LancamentoDAO lancamentoDAO;
    private CategoriaDAO categoriaDAO;
    
    public RelatorioController() {
        this.lancamentoDAO = new LancamentoDAO();
        this.categoriaDAO = new CategoriaDAO();
    }
    
    public Map<String, Double> getGastosPorCategoria(int usuarioId) {
        Map<String, Double> gastos = new HashMap<>();
        
        try {
            List<Categoria> categorias = categoriaDAO.listarPorTipo("despesa");
            
            for (Categoria categoria : categorias) {
                Double total = lancamentoDAO.somaPorCategoria(categoria.getId(), usuarioId);
                if (total != null && total > 0) {
                    gastos.put(categoria.getNome(), total);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter gastos por categoria: " + e.getMessage());
        }
        
        return gastos;
    }
    
    public Map<String, Double> getReceitasVsDespesas(int usuarioId) {
        Map<String, Double> dados = new HashMap<>();
        
        try {
            Double receitas = lancamentoDAO.somaPorTipo("receita", usuarioId);
            Double despesas = lancamentoDAO.somaPorTipo("despesa", usuarioId);
            
            dados.put("Receitas", receitas != null ? receitas : 0.0);
            dados.put("Despesas", despesas != null ? despesas : 0.0);
        } catch (SQLException e) {
            System.err.println("Erro ao obter receitas vs despesas: " + e.getMessage());
        }
        
        return dados;
    }
}
```

## 🚀 Execução e Teste

### **Comandos para Executar:**

#### **Desktop:**
```bash
# Compilar e executar
ant run

# Apenas compilar
ant compile

# Criar JAR
ant jar

# Executar JAR
java -jar dist/finanza-desktop.jar
```

#### **Android:**
```bash
# Compilar e instalar no dispositivo
./gradlew installDebug

# Executar testes
./gradlew test
```

#### **Servidor:**
```bash
# Compilar e executar servidor
cd DESKTOP VERSION/Servidor-Finanza
ant run

# Testar conectividade
ant test
```

### **Dados de Teste:**
- **Usuário padrão:** admin@finanza.com / admin
- **Contas padrão:** Conta Corrente, Poupança, Cartão
- **Categorias:** Alimentação, Transporte, Saúde, etc.

## 🎯 Considerações Finais

Este tutorial fornece uma base completa para implementar o sistema Finanza em NetBeans 24. O sistema está funcional e pode ser expandido com:

1. **Criptografia de senhas** usando BCrypt
2. **Relatórios em PDF** com iText
3. **Backup automático** para nuvem
4. **Notificações** de vencimento
5. **Importação/Exportação** de dados
6. **Multi-idiomas** com ResourceBundle
7. **Temas personalizáveis**

O código está modularizado, seguindo boas práticas de desenvolvimento e permitindo fácil manutenção e expansão das funcionalidades.