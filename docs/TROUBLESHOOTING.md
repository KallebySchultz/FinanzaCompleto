# 🛠️ Troubleshooting - Sistema Finanza

## 📋 Índice

1. [Problemas de Conexão](#-problemas-de-conexão)
2. [Problemas de Sincronização](#-problemas-de-sincronização)
3. [Problemas do Banco de Dados](#-problemas-do-banco-de-dados)
4. [Problemas de Interface](#-problemas-de-interface)
5. [Ferramentas de Diagnóstico](#-ferramentas-de-diagnóstico)
6. [Logs e Debugging](#-logs-e-debugging)

---

## 🌐 Problemas de Conexão

### ❌ **"Não consegue conectar ao servidor"**

#### **Sintomas:**
- Desktop não conecta na tela de login
- Mobile fica "Conectando..." indefinidamente
- Erro: "Connection refused" ou "Connection timeout"

#### **Diagnóstico:**
```bash
# 1. Verificar se servidor está rodando
ps aux | grep java | grep Servidor
# Deve aparecer: java ... MainServidor

# 2. Verificar se porta está em uso
netstat -tulpn | grep 8080
# Deve aparecer: tcp ... :8080 ... LISTEN

# 3. Testar conectividade
telnet localhost 8080
# Deve conectar sem erro

# 4. Verificar logs do servidor
tail -f servidor.log
```

#### **Soluções:**

##### **4.1. Servidor não está rodando**
```bash
# Iniciar servidor
cd "DESKTOP VERSION/ServidorFinanza"
java -cp "build:lib/*" MainServidor

# Verificar se iniciou
# Deve aparecer: "Servidor iniciado na porta 8080"
```

##### **4.2. Porta já está em uso**
```bash
# Verificar qual processo usa a porta
sudo lsof -i :8080

# Matar processo conflitante
sudo kill -9 <PID>

# Ou usar porta diferente
# Editar código para usar porta 8081, 8082, etc.
```

##### **4.3. Firewall bloqueando**
```bash
# Windows
netsh advfirewall firewall add rule name="Finanza Server" dir=in action=allow protocol=TCP localport=8080

# Linux/macOS
sudo ufw allow 8080
# ou
sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
```

##### **4.4. IP incorreto**
```java
// No cliente, verificar configuração
// NetworkClient.java ou ServerClient.java
private static final String SERVER_IP = "192.168.1.100"; // IP correto?
private static final int SERVER_PORT = 8080;             // Porta correta?

// Para descobrir IP do servidor:
// Windows: ipconfig
// Linux/macOS: ifconfig ou ip addr
```

### ❌ **"Conecta mas desconecta imediatamente"**

#### **Sintomas:**
- Conexão é estabelecida mas fecha em segundos
- Log mostra: "Connection reset by peer"

#### **Diagnóstico:**
```java
// Adicionar logs no ClientHandler.java
public void run() {
    try {
        System.out.println("Cliente conectado: " + socket.getInetAddress());
        
        while (!socket.isClosed()) {
            String comando = in.readLine();
            if (comando == null) {
                System.out.println("Cliente enviou null, desconectando");
                break;
            }
            System.out.println("Comando recebido: " + comando);
            // ... processar comando
        }
    } catch (IOException e) {
        System.err.println("Erro na comunicação: " + e.getMessage());
    }
}
```

#### **Soluções:**

##### **4.1. Cliente enviando dados inválidos**
```java
// No cliente, verificar se commands têm formato correto
String comando = "LOGIN|email|senha"; // ✅ Correto
String comando = "LOGIN email senha"; // ❌ Incorreto - falta separador |
```

##### **4.2. Timeout muito baixo**
```java
// Aumentar timeout no cliente
socket.setSoTimeout(30000); // 30 segundos
```

##### **4.3. Protocolo incompatível**
```java
// Verificar se Protocol.SEPARATOR é o mesmo em cliente e servidor
public static final String SEPARATOR = "|"; // Deve ser igual em todos
```

---

## 🔄 Problemas de Sincronização

### ❌ **"Dados não sincronizam do mobile"**

#### **Sintomas:**
- Movimentações criadas no mobile não aparecem no desktop
- Status fica "Aguardando sincronização"
- Mobile mostra offline mesmo com internet

#### **Diagnóstico:**
```java
// Verificar logs do SyncService (Android)
adb logcat | grep SyncService

// Verificar dados pendentes no SQLite
SELECT * FROM movimentacao WHERE sync_status = 'PENDING_SYNC';

// Verificar se service está rodando
adb shell ps | grep finanza
```

#### **Soluções:**

##### **4.1. SyncService não está rodando**
```java
// Forçar restart do service
Intent syncIntent = new Intent(this, SyncService.class);
stopService(syncIntent);
startService(syncIntent);
```

##### **4.2. Falta de permissão de rede**
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

##### **4.3. Dados malformados**
```java
// Verificar se serialização está correta
public String serializeMovementForSync(Movimentacao mov) {
    return Protocol.CMD_ADD_MOVEMENT + Protocol.SEPARATOR +
           mov.getDescricao() + Protocol.SEPARATOR +
           mov.getValor() + Protocol.SEPARATOR +
           mov.getData() + Protocol.SEPARATOR +
           mov.getTipo() + Protocol.SEPARATOR +
           mov.getIdConta() + Protocol.SEPARATOR +
           mov.getIdCategoria();
}
```

##### **4.4. Conflitos de timestamp**
```java
// Verificar se timestamps estão corretos
public void checkTimestamps() {
    Date now = new Date();
    Date dbTime = getLastSyncTime();
    
    System.out.println("Agora: " + now);
    System.out.println("Última sync: " + dbTime);
    
    if (Math.abs(now.getTime() - dbTime.getTime()) > 300000) { // 5 min
        System.out.println("⚠️ Diferença de tempo suspeita!");
    }
}
```

### ❌ **"Conflitos não são resolvidos"**

#### **Sintomas:**
- Dados duplicados aparecem
- Mesmo registro com valores diferentes
- Status fica "SYNC_CONFLICT" permanentemente

#### **Diagnóstico:**
```sql
-- Verificar conflitos pendentes
SELECT * FROM movimentacao WHERE sync_status = 'SYNC_CONFLICT';

-- Verificar log de conflitos no servidor
SELECT * FROM sync_log WHERE status = 'CONFLICT' ORDER BY timestamp DESC;
```

#### **Soluções:**

##### **4.1. Algoritmo de resolução não funciona**
```java
// Implementar resolução manual temporária
public void resolveConflictManually(int movimentacaoId) {
    // Pegar versão do servidor como "vencedora"
    Movimentacao serverVersion = getServerVersion(movimentacaoId);
    
    // Atualizar versão local
    movimentacaoDao.update(serverVersion);
    
    // Marcar como resolvido
    serverVersion.setSyncStatus(SyncStatus.SYNCED);
    movimentacaoDao.update(serverVersion);
}
```

##### **4.2. Timestamps inconsistentes**
```java
// Sincronizar relógios
public void syncServerTime() {
    String response = serverClient.sendCommand("GET_SERVER_TIME");
    if (response.startsWith("OK")) {
        long serverTime = Long.parseLong(response.split("\\|")[1]);
        long localTime = System.currentTimeMillis();
        long diff = serverTime - localTime;
        
        if (Math.abs(diff) > 60000) { // 1 minuto
            System.out.println("⚠️ Diferença de " + (diff/1000) + " segundos com servidor");
        }
    }
}
```

---

## 🗄️ Problemas do Banco de Dados

### ❌ **"Erro de conexão com MySQL"**

#### **Sintomas:**
- Servidor não consegue conectar ao MySQL
- Erro: "Access denied for user"
- Erro: "Unknown database 'finanza'"

#### **Diagnóstico:**
```bash
# Verificar se MySQL está rodando
sudo systemctl status mysql
# ou
brew services list | grep mysql

# Testar conexão manual
mysql -u root -p
USE finanza;
SHOW TABLES;
```

#### **Soluções:**

##### **4.1. MySQL não está rodando**
```bash
# Iniciar MySQL
sudo systemctl start mysql    # Linux
brew services start mysql     # macOS

# Windows (como administrador)
net start mysql
```

##### **4.2. Usuário/senha incorretos**
```java
// Verificar DatabaseUtil.java
private static final String URL = "jdbc:mysql://localhost:3306/finanza";
private static final String USERNAME = "root";        // ✅ Usuário correto?
private static final String PASSWORD = "sua_senha";   // ✅ Senha correta?

// Testar credenciais
mysql -u root -p
```

##### **4.3. Database não existe**
```sql
-- Criar database
CREATE DATABASE finanza;
USE finanza;

-- Executar scripts de criação
source DESKTOP\ VERSION/banco/script_inicial.sql;

-- Verificar se tabelas foram criadas
SHOW TABLES;
```

##### **4.4. Driver JDBC ausente**
```bash
# Verificar se mysql-connector-java está no classpath
ls lib/mysql-connector-java-*.jar

# Se não existir, baixar de:
# https://dev.mysql.com/downloads/connector/j/
```

### ❌ **"Queries muito lentas"**

#### **Sintomas:**
- Interface trava ao carregar dados
- Timeouts frequentes
- Log mostra queries demoradas

#### **Diagnóstico:**
```sql
-- Verificar queries lentas
SHOW PROCESSLIST;

-- Analisar performance de uma query
EXPLAIN SELECT * FROM movimentacao WHERE id_usuario = 1 AND data BETWEEN '2024-01-01' AND '2024-12-31';

-- Verificar tamanho das tabelas
SELECT table_name, table_rows, data_length, index_length 
FROM information_schema.tables 
WHERE table_schema = 'finanza';
```

#### **Soluções:**

##### **4.1. Faltam índices**
```sql
-- Criar índices importantes
CREATE INDEX idx_movimentacao_usuario_data ON movimentacao(id_usuario, data);
CREATE INDEX idx_movimentacao_conta ON movimentacao(id_conta);
CREATE INDEX idx_movimentacao_categoria ON movimentacao(id_categoria);
CREATE INDEX idx_conta_usuario ON conta(id_usuario);
CREATE INDEX idx_categoria_usuario ON categoria(id_usuario);
```

##### **4.2. Queries não otimizadas**
```java
// ❌ RUIM: Busca sem WHERE
String sql = "SELECT * FROM movimentacao";

// ✅ BOM: Busca específica
String sql = "SELECT * FROM movimentacao WHERE id_usuario = ? AND data >= ? ORDER BY data DESC LIMIT 100";
```

##### **4.3. Muitos dados históricos**
```sql
-- Implementar arquivamento de dados antigos
CREATE TABLE movimentacao_historico LIKE movimentacao;

-- Mover dados antigos (> 2 anos)
INSERT INTO movimentacao_historico 
SELECT * FROM movimentacao 
WHERE data < DATE_SUB(NOW(), INTERVAL 2 YEAR);

DELETE FROM movimentacao 
WHERE data < DATE_SUB(NOW(), INTERVAL 2 YEAR);
```

---

## 🖼️ Problemas de Interface

### ❌ **"Tela de login não funciona"**

#### **Sintomas:**
- Botão "Entrar" não responde
- Credenciais corretas são rejeitadas
- Interface trava após login

#### **Diagnóstico:**
```java
// Adicionar logs no AuthController
public boolean login(String email, String senha) {
    System.out.println("Tentativa de login: " + email);
    
    if (!networkClient.isConnected()) {
        System.out.println("❌ Não conectado ao servidor");
        return false;
    }
    
    String comando = "LOGIN|" + email + "|" + senha;
    System.out.println("Enviando comando: LOGIN|" + email + "|***");
    
    String resposta = networkClient.sendCommand(comando);
    System.out.println("Resposta recebida: " + resposta);
    
    return resposta.startsWith("OK");
}
```

#### **Soluções:**

##### **4.1. Evento do botão não está conectado**
```java
// Verificar se ActionListener está configurado
private void initComponents() {
    // ...
    loginButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            realizarLogin(); // ✅ Método deve ser chamado
        }
    });
}
```

##### **4.2. Validação muito restritiva**
```java
// Verificar validações desnecessárias
private boolean validarCredenciais(String email, String senha) {
    if (email.isEmpty()) return false;
    if (senha.isEmpty()) return false;
    
    // ❌ MUITO RESTRITIVO
    // if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) return false;
    
    // ✅ VALIDAÇÃO BÁSICA
    if (!email.contains("@")) return false;
    
    return true;
}
```

##### **4.3. Threading bloqueando UI**
```java
// ❌ RUIM: Login no thread da UI
private void realizarLogin() {
    boolean sucesso = authController.login(email, senha); // Bloqueia UI
    if (sucesso) {
        abrirDashboard();
    }
}

// ✅ BOM: Login em background
private void realizarLogin() {
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() throws Exception {
            return authController.login(email, senha);
        }
        
        @Override
        protected void done() {
            try {
                if (get()) {
                    abrirDashboard();
                } else {
                    mostrarErro("Credenciais inválidas");
                }
            } catch (Exception e) {
                mostrarErro("Erro na conexão: " + e.getMessage());
            }
        }
    };
    worker.execute();
}
```

### ❌ **"Formulários não salvam dados"**

#### **Sintomas:**
- Clicar "Salvar" não persiste dados
- Formulário limpa mas dados não aparecem na lista
- Não há mensagens de erro

#### **Diagnóstico:**
```java
// Adicionar logs no FinanceController
public OperationResult<Void> adicionarMovimentacao(Movimentacao mov) {
    System.out.println("📝 Salvando movimentação: " + mov.getDescricao());
    System.out.println("💰 Valor: " + mov.getValor());
    System.out.println("📅 Data: " + mov.getData());
    
    if (!networkClient.isConnected()) {
        System.out.println("❌ Não conectado");
        return new OperationResult<>(false, "Não conectado", null);
    }
    
    String comando = construirComando(mov);
    System.out.println("📤 Comando: " + comando);
    
    String resposta = networkClient.sendCommand(comando);
    System.out.println("📥 Resposta: " + resposta);
    
    return processarResposta(resposta);
}
```

#### **Soluções:**

##### **4.1. Validação impedindo salvamento**
```java
// Verificar todas as validações
private boolean validarMovimentacao(Movimentacao mov) {
    if (mov.getDescricao().trim().isEmpty()) {
        System.out.println("❌ Descrição vazia");
        return false;
    }
    
    if (mov.getValor().compareTo(BigDecimal.ZERO) <= 0) {
        System.out.println("❌ Valor inválido: " + mov.getValor());
        return false;
    }
    
    if (mov.getIdConta() <= 0) {
        System.out.println("❌ Conta não selecionada");
        return false;
    }
    
    if (mov.getIdCategoria() <= 0) {
        System.out.println("❌ Categoria não selecionada");
        return false;
    }
    
    return true;
}
```

##### **4.2. Serialização incorreta**
```java
// Verificar se dados estão sendo coletados corretamente
private Movimentacao coletarDadosFormulario() {
    Movimentacao mov = new Movimentacao();
    
    mov.setDescricao(campoDescricao.getText());
    
    // ❌ ERRO COMUM: Não converter vírgula para ponto
    String valorText = campoValor.getText().replace(",", ".");
    mov.setValor(new BigDecimal(valorText));
    
    // ❌ ERRO COMUM: Não converter Date
    mov.setData(new Date(datePicker.getDate().getTime()));
    
    // ❌ ERRO COMUM: Não pegar IDs dos combos
    Conta contaSelecionada = (Conta) comboConta.getSelectedItem();
    mov.setIdConta(contaSelecionada.getId());
    
    return mov;
}
```

---

## 🔧 Ferramentas de Diagnóstico

### 🔍 **Script de Verificação Completa**

```bash
#!/bin/bash
# diagnostico.sh

echo "🔍 DIAGNÓSTICO SISTEMA FINANZA"
echo "════════════════════════════════"

# 1. Verificar Java
echo "☕ Verificando Java..."
java -version 2>&1 | head -1
echo ""

# 2. Verificar MySQL
echo "🗄️ Verificando MySQL..."
if systemctl is-active --quiet mysql; then
    echo "✅ MySQL está rodando"
    mysql -u root -p -e "USE finanza; SHOW TABLES;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ Database 'finanza' acessível"
    else
        echo "❌ Problema ao acessar database 'finanza'"
    fi
else
    echo "❌ MySQL não está rodando"
fi
echo ""

# 3. Verificar porta do servidor
echo "🌐 Verificando porta 8080..."
if netstat -tulpn | grep -q ":8080.*LISTEN"; then
    echo "✅ Porta 8080 está em uso (servidor provavelmente rodando)"
else
    echo "❌ Porta 8080 livre (servidor não está rodando)"
fi
echo ""

# 4. Verificar conectividade
echo "📡 Testando conectividade..."
timeout 3 bash -c "</dev/tcp/localhost/8080" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ Conexão TCP com localhost:8080 OK"
else
    echo "❌ Não consegue conectar em localhost:8080"
fi
echo ""

# 5. Verificar arquivos compilados
echo "📦 Verificando build..."
if [ -d "DESKTOP VERSION/ServidorFinanza/build" ]; then
    echo "✅ Servidor compilado"
else
    echo "❌ Servidor não compilado"
fi

if [ -d "DESKTOP VERSION/ClienteFinanza/build" ]; then
    echo "✅ Cliente compilado"
else
    echo "❌ Cliente não compilado"
fi
echo ""

# 6. Verificar logs recentes
echo "📄 Logs recentes..."
if [ -f "servidor.log" ]; then
    echo "Últimas 5 linhas do log do servidor:"
    tail -5 servidor.log
else
    echo "❌ Arquivo servidor.log não encontrado"
fi

echo ""
echo "🏁 Diagnóstico concluído!"
```

### 📊 **Monitor de Sistema (Java)**

```java
// SystemMonitor.java
public class SystemMonitor {
    
    public static void printSystemStatus() {
        System.out.println("📊 STATUS DO SISTEMA FINANZA");
        System.out.println("═══════════════════════════════");
        
        // CPU e Memória
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        
        System.out.printf("💾 Memória: %dMB usada / %dMB disponível (max: %dMB)\n", 
                         usedMemory, totalMemory, maxMemory);
        
        // Conexões ativas
        int activeConnections = ClientHandler.getActiveConnections();
        System.out.printf("🌐 Conexões ativas: %d\n", activeConnections);
        
        // Status do banco
        try (Connection conn = DatabaseUtil.getConnection()) {
            System.out.println("🗄️ MySQL: ✅ Conectado");
        } catch (SQLException e) {
            System.out.println("🗄️ MySQL: ❌ Erro - " + e.getMessage());
        }
        
        // Uptime
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        System.out.printf("⏰ Tempo ativo: %d minutos\n", uptime / 60000);
        
        System.out.println();
    }
    
    public static void startPeriodicMonitoring() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                printSystemStatus();
            }
        }, 0, 60000); // A cada minuto
    }
}
```

---

## 📝 Logs e Debugging

### 🔍 **Configuração de Logs**

```java
// LogConfig.java
public class LogConfig {
    private static final boolean DEBUG_MODE = true;
    private static final String LOG_FILE = "finanza.log";
    
    public static void setupLogging() {
        if (DEBUG_MODE) {
            System.setProperty("java.util.logging.config.file", "logging.properties");
        }
    }
    
    public static void log(String tag, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = String.format("[%s] [%s] %s", timestamp, tag, message);
        
        System.out.println(logEntry);
        
        // Também salvar em arquivo
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(logEntry + "\n");
        } catch (IOException e) {
            System.err.println("Erro ao escrever log: " + e.getMessage());
        }
    }
    
    public static void error(String tag, String message, Exception e) {
        log(tag, "ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
}
```

### 📋 **Template de Debug**

```java
// DebugTemplate.java - Use este template para debugar problemas
public class DebugTemplate {
    
    public void debugMethodTemplate() {
        LogConfig.log("DEBUG", "=== INÍCIO DO DEBUG ===");
        
        try {
            // 1. Log dos parâmetros de entrada
            LogConfig.log("INPUT", "Parâmetro1: valor1");
            LogConfig.log("INPUT", "Parâmetro2: valor2");
            
            // 2. Log do estado atual
            LogConfig.log("STATE", "Conectado: " + isConnected());
            LogConfig.log("STATE", "Usuário logado: " + getCurrentUser());
            
            // 3. Executar operação com logs
            LogConfig.log("EXEC", "Iniciando operação...");
            String resultado = operacaoComProblema();
            LogConfig.log("EXEC", "Resultado: " + resultado);
            
            // 4. Log de validações
            if (resultado == null) {
                LogConfig.log("VALIDATION", "❌ Resultado é null!");
            } else if (resultado.isEmpty()) {
                LogConfig.log("VALIDATION", "❌ Resultado vazio!");
            } else {
                LogConfig.log("VALIDATION", "✅ Resultado válido");
            }
            
        } catch (Exception e) {
            LogConfig.error("EXCEPTION", "Erro durante execução", e);
        } finally {
            LogConfig.log("DEBUG", "=== FIM DO DEBUG ===");
        }
    }
}
```

### 🚨 **Checklist de Resolução de Problemas**

#### **Quando algo não funciona:**

**☑️ Passo 1: Verificar Conexões**
- [ ] Servidor está rodando?
- [ ] MySQL está rodando?
- [ ] Porta não está bloqueada?
- [ ] IP/porta estão corretos no cliente?

**☑️ Passo 2: Verificar Logs**
- [ ] Há erros nos logs do servidor?
- [ ] Há erros nos logs do cliente?
- [ ] Comandos estão sendo enviados?
- [ ] Respostas estão sendo recebidas?

**☑️ Passo 3: Verificar Dados**
- [ ] Dados estão sendo validados corretamente?
- [ ] Serialização está funcionando?
- [ ] Queries SQL estão corretas?
- [ ] Timestamps estão consistentes?

**☑️ Passo 4: Verificar Interface**
- [ ] Eventos estão conectados?
- [ ] Formulários coletam dados?
- [ ] Threading não bloqueia UI?
- [ ] Mensagens de erro aparecem?

**☑️ Passo 5: Verificar Sincronização (Mobile)**
- [ ] SyncService está rodando?
- [ ] Permissões de rede ok?
- [ ] Dados marcados para sync?
- [ ] Conflitos sendo resolvidos?

Este guia de troubleshooting te ajudará a resolver **99% dos problemas** que podem aparecer no sistema Finanza!