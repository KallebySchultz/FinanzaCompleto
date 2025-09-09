# 🔄 Fluxo de Dados - Sistema Finanza

## 📋 Índice

1. [Visão Geral do Fluxo](#-visão-geral-do-fluxo)
2. [Comunicação TCP](#-comunicação-tcp)
3. [Sincronização de Dados](#-sincronização-de-dados)
4. [Estados dos Dados](#-estados-dos-dados)
5. [Resolução de Conflitos](#-resolução-de-conflitos)
6. [Monitoramento](#-monitoramento)

---

## 🌊 Visão Geral do Fluxo

### 🎯 **Arquitetura de Dados**

```
┌──────────────────┐    TCP/IP    ┌──────────────────┐    JDBC    ┌─────────────┐
│  MOBILE CLIENT   │◄────────────►│   TCP SERVER     │◄──────────►│   MYSQL     │
│                  │              │                  │            │  DATABASE   │
│ ┌──────────────┐ │              │ ┌──────────────┐ │            │             │
│ │ SQLite Cache │ │              │ │ ClientHandler│ │            │   Tables:   │
│ │   (Room)     │ │              │ │   +  DAO     │ │            │ • usuario   │
│ └──────────────┘ │              │ └──────────────┘ │            │ • conta     │
└──────────────────┘              └──────────────────┘            │ • categoria │
         ▲                                   ▲                    │ • moviment  │
         │                                   │                    └─────────────┘
         ▼                                   ▼
┌──────────────────┐              ┌──────────────────┐
│ DESKTOP CLIENT   │              │  DESKTOP CLIENT  │
│                  │              │     (Other)      │
│ ┌──────────────┐ │              │ ┌──────────────┐ │
│ │   No Cache   │ │              │ │   No Cache   │ │
│ │ (Direct TCP) │ │              │ │ (Direct TCP) │ │
│ └──────────────┘ │              │ └──────────────┘ │
└──────────────────┘              └──────────────────┘
```

### 📊 **Tipos de Dados e Fluxos**

| Tipo de Dado | Origem | Destino | Frequência | Cache Local |
|--------------|--------|---------|------------|-------------|
| **Autenticação** | Cliente | Servidor | Por sessão | ❌ Não |
| **Movimentações** | Cliente | MySQL via Servidor | Tempo real | ✅ Mobile |
| **Contas** | Cliente | MySQL via Servidor | Baixa | ✅ Mobile |
| **Categorias** | Cliente | MySQL via Servidor | Baixa | ✅ Mobile |
| **Relatórios** | Servidor | Cliente | Sob demanda | ❌ Não |
| **Sincronização** | Mobile | Servidor | 30s / Evento | ✅ Mobile |

---

## 📡 Comunicação TCP

### 🔌 **Estabelecimento de Conexão**

#### **1. Cliente Conecta**
```java
// NetworkClient.java (Desktop) / ServerClient.java (Mobile)
┌─────────────────────────────────────────────────────────────┐
│ 🔌 ESTABELECIMENTO DE CONEXÃO                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ 1. Cliente inicia conexão:                                 │
│    Socket socket = new Socket("192.168.1.100", 8080);      │
│                                                             │
│ 2. Servidor aceita conexão:                                │
│    Socket clientSocket = serverSocket.accept();            │
│                                                             │
│ 3. Cria thread para cliente:                               │
│    new ClientHandler(clientSocket).start();                │
│                                                             │
│ 4. Estabelece streams:                                      │
│    PrintWriter out = new PrintWriter(socket.getOutput...); │
│    BufferedReader in = new BufferedReader(socket.getInp...);│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### **2. Protocolo de Handshake**
```
Cliente  ──┐
           │  CONNECT|client_type|version
           └─────────────────────────────────→  Servidor
           
Cliente  ←┐
           │  OK|server_version|session_id
           └─────────────────────────────────  Servidor
           
Cliente  ──┐
           │  LOGIN|user@email.com|password
           └─────────────────────────────────→  Servidor
           
Cliente  ←┐
           │  OK|user_id|user_name|user_email
           └─────────────────────────────────  Servidor
```

### 📋 **Formato de Mensagens**

#### **Estrutura Padrão:**
```
COMANDO|PARAM1|PARAM2|PARAM3|...
```

#### **Comandos de Dados:**
```java
// CREATE operations
ADD_MOVEMENT|descrição|valor|data|tipo|id_conta|id_categoria
ADD_ACCOUNT|nome|tipo|saldo_inicial
ADD_CATEGORY|nome|tipo|cor

// READ operations  
LIST_MOVEMENTS|mes|ano
LIST_ACCOUNTS
LIST_CATEGORIES
GET_MOVEMENT|id

// UPDATE operations
UPDATE_MOVEMENT|id|descrição|valor|data|tipo|id_conta|id_categoria
UPDATE_ACCOUNT|id|nome|tipo
UPDATE_CATEGORY|id|nome|tipo|cor

// DELETE operations
DELETE_MOVEMENT|id
DELETE_ACCOUNT|id
DELETE_CATEGORY|id
```

#### **Respostas do Servidor:**
```java
// Sucesso
OK|dados_retornados

// Sucesso com ID retornado
OK|123|Movimentação criada com ID 123

// Lista de dados
OK|item1;campo1,campo2,campo3|item2;campo1,campo2,campo3

// Erros
ERROR|Mensagem de erro específica
INVALID_CREDENTIALS|Email ou senha incorretos
INVALID_DATA|Campo 'valor' deve ser maior que zero
NOT_FOUND|Movimentação não encontrada
PERMISSION_DENIED|Conta não pertence ao usuário
```

### 🔄 **Ciclo de Vida da Comunicação**

```
┌─────────────────────────────────────────────────────────────┐
│ 📱 CLIENTE ENVIA COMANDO                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ 1. Usuario clica em "Salvar Movimentação"                  │
│    ↓                                                        │
│ 2. View coleta dados do formulário                         │
│    ↓                                                        │
│ 3. Controller valida dados localmente                      │
│    ↓                                                        │
│ 4. Controller monta comando:                               │
│    "ADD_MOVEMENT|Almoço|45.90|2024-03-15|DESPESA|1|2"      │
│    ↓                                                        │
│ 5. NetworkClient envia via TCP                             │
│    ↓                                                        │
│ 6. Aguarda resposta do servidor                            │
│    ↓                                                        │
│ 7. Processa resposta:                                      │
│    • "OK|123" → Sucesso, ID da movimentação = 123          │
│    • "ERROR|..." → Mostra erro para usuário                │
│    ↓                                                        │
│ 8. Atualiza interface conforme resultado                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ 🖥️ SERVIDOR PROCESSA COMANDO                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ 1. ClientHandler recebe comando via TCP                    │
│    ↓                                                        │
│ 2. Protocol.parseCommand() interpreta comando              │
│    ↓                                                        │
│ 3. Valida se usuário está autenticado                      │
│    ↓                                                        │
│ 4. Extrai parâmetros do comando                            │
│    ↓                                                        │
│ 5. Valida dados recebidos                                  │
│    ↓                                                        │
│ 6. Chama DAO apropriado:                                   │
│    MovimentacaoDAO.inserir(movimentacao)                   │
│    ↓                                                        │
│ 7. DAO executa SQL no MySQL                                │
│    ↓                                                        │
│ 8. Processa resultado do banco                             │
│    ↓                                                        │
│ 9. Monta resposta:                                         │
│    • Sucesso: "OK|id_gerado"                               │
│    • Erro: "ERROR|mensagem_erro"                           │
│    ↓                                                        │
│ 10. Envia resposta de volta ao cliente                     │
│    ↓                                                        │
│ 11. Notifica outros clientes conectados (se necessário)    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Sincronização de Dados

### 📱 **Mobile: Sistema de Cache Local**

#### **Arquitetura Room Database:**
```java
@Database(
    entities = {Usuario.class, Conta.class, Categoria.class, Movimentacao.class},
    version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract ContaDao contaDao();
    public abstract CategoriaDao categoriaDao();
    public abstract MovimentacaoDao movimentacaoDao();
}
```

#### **Estados de Sincronização:**
```java
// Enum para status de sync
public enum SyncStatus {
    SYNCED,         // Sincronizado com servidor
    PENDING_SYNC,   // Aguardando sincronização
    SYNC_CONFLICT,  // Conflito detectado
    SYNC_ERROR      // Erro na sincronização
}

// Campo adicionado nas entidades
@Entity
public class Movimentacao {
    // ... outros campos
    
    @ColumnInfo(name = "sync_status")
    private SyncStatus syncStatus = SyncStatus.PENDING_SYNC;
    
    @ColumnInfo(name = "last_sync")
    private Date lastSync;
    
    @ColumnInfo(name = "server_id")
    private Integer serverId; // ID no servidor (pode ser diferente do local)
}
```

### 🔄 **Processo de Sincronização**

#### **1. Sincronização Automática (Mobile)**
```java
// SyncService.java
public class SyncService extends IntentService {
    private static final long SYNC_INTERVAL = 30000; // 30 segundos
    
    @Override
    protected void onHandleIntent(Intent intent) {
        if (isConnectedToServer()) {
            syncPendingData();
            fetchServerUpdates();
        }
    }
    
    private void syncPendingData() {
        // 1. Buscar dados com status PENDING_SYNC
        List<Movimentacao> pending = movimentacaoDao.getPendingSync();
        
        for (Movimentacao mov : pending) {
            try {
                // 2. Enviar para servidor
                String resultado = serverClient.sendMovement(mov);
                
                if (resultado.startsWith("OK")) {
                    // 3. Atualizar status local
                    mov.setSyncStatus(SyncStatus.SYNCED);
                    mov.setLastSync(new Date());
                    
                    // 4. Extrair server_id da resposta
                    String[] partes = resultado.split("\\|");
                    if (partes.length > 1) {
                        mov.setServerId(Integer.parseInt(partes[1]));
                    }
                    
                    movimentacaoDao.update(mov);
                } else {
                    // Marcar como erro
                    mov.setSyncStatus(SyncStatus.SYNC_ERROR);
                    movimentacaoDao.update(mov);
                }
            } catch (Exception e) {
                Log.e("SyncService", "Erro no sync", e);
            }
        }
    }
    
    private void fetchServerUpdates() {
        // 1. Pegar timestamp da última sincronização
        Date lastSync = SharedPrefs.getLastSyncTime();
        
        // 2. Solicitar atualizações do servidor
        String comando = "SYNC_REQUEST|" + lastSync.getTime();
        String resposta = serverClient.sendCommand(comando);
        
        if (resposta.startsWith("OK")) {
            // 3. Processar dados retornados
            processServerUpdates(resposta);
        }
    }
}
```

#### **2. Triggers de Sincronização**
```java
// Triggers que iniciam sincronização
public class SyncTriggers {
    
    // 1. Após inserir/atualizar dados
    public void onDataChanged() {
        Intent syncIntent = new Intent(context, SyncService.class);
        context.startService(syncIntent);
    }
    
    // 2. Timer automático
    private void schedulePeriodicSync() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, SyncService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            30000, // 30 segundos
            pendingIntent
        );
    }
    
    // 3. Quando app volta do background
    @Override
    protected void onResume() {
        super.onResume();
        triggerSync();
    }
    
    // 4. Quando conexão de rede é restaurada
    private void onNetworkAvailable() {
        triggerSync();
    }
}
```

---

## 📊 Estados dos Dados

### 🎯 **Estados Possíveis**

#### **Mobile (com cache local):**
```
┌─────────────────┐
│ DADOS CRIADOS   │ → [PENDING_SYNC] → Aguardando envio ao servidor
│ LOCALMENTE      │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│ ENVIANDO PARA   │ → [SYNCING] → Em processo de sincronização
│ SERVIDOR        │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│ SINCRONIZADO    │ → [SYNCED] → Dados confirmados no servidor
│ COM SUCESSO     │
└─────────────────┘

         │ (se erro)
         ▼
┌─────────────────┐
│ ERRO NA         │ → [SYNC_ERROR] → Retentar mais tarde
│ SINCRONIZAÇÃO   │
└─────────────────┘

         │ (se conflito)
         ▼
┌─────────────────┐
│ CONFLITO        │ → [SYNC_CONFLICT] → Resolver conflito
│ DETECTADO       │
└─────────────────┘
```

#### **Desktop (sem cache local):**
```
┌─────────────────┐
│ DADOS CRIADOS   │ → Enviado imediatamente via TCP
│ NA INTERFACE    │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│ RESPOSTA DO     │ → Sucesso: Atualiza interface
│ SERVIDOR        │ → Erro: Mostra mensagem de erro
└─────────────────┘
```

### 📋 **Rastreamento de Estados**

#### **Mobile: Tabela de Controle**
```sql
-- Adicionar campos de controle nas tabelas
ALTER TABLE movimentacao ADD COLUMN sync_status VARCHAR(20) DEFAULT 'PENDING_SYNC';
ALTER TABLE movimentacao ADD COLUMN last_sync TIMESTAMP NULL;
ALTER TABLE movimentacao ADD COLUMN server_id INT NULL;
ALTER TABLE movimentacao ADD COLUMN local_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE movimentacao ADD COLUMN server_updated TIMESTAMP NULL;
```

#### **Servidor: Log de Sincronização**
```sql
-- Tabela para logs de sincronização
CREATE TABLE sync_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    client_id VARCHAR(50),
    client_type ENUM('MOBILE', 'DESKTOP'),
    operation VARCHAR(20),
    table_name VARCHAR(50),
    record_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SUCCESS', 'ERROR', 'CONFLICT'),
    details TEXT
);
```

---

## ⚔️ Resolução de Conflitos

### 🎯 **Tipos de Conflitos**

#### **1. Conflito de Timestamp**
```
Cenário: Mesmo registro modificado no mobile e desktop simultaneamente

Mobile:   Movimentação ID=123 modificada em 14:30:15
Desktop:  Movimentação ID=123 modificada em 14:30:18

Resolução: Desktop vence (timestamp mais recente)
Backup:   Versão do mobile salva na tabela conflict_backup
```

#### **2. Conflito de Deletação**
```
Cenário: Registro deletado no mobile mas modificado no desktop

Mobile:   Movimentação ID=123 deletada
Desktop:  Movimentação ID=123 modificada

Resolução: Modificação vence, deletação é cancelada
Notificação: Usuário mobile é avisado que deletação foi cancelada
```

#### **3. Conflito de Criação Dupla**
```
Cenário: Mesmo registro criado no mobile e desktop (rare case)

Mobile:   Nova categoria "Transporte" criada
Desktop:  Nova categoria "Transporte" criada

Resolução: Merge automático se dados são idênticos
          Manual se há diferenças (cor, descrição)
```

### 🔧 **Implementação da Resolução**

#### **Algoritmo de Resolução Automática:**
```java
// ConflictResolutionManager.java
public class ConflictResolutionManager {
    
    public ResolutionResult resolveConflict(ConflictData conflict) {
        // 1. Estratégia baseada em timestamp (padrão)
        if (conflict.getServerTimestamp().after(conflict.getClientTimestamp())) {
            return ResolutionResult.keepServer(conflict);
        } else {
            return ResolutionResult.keepClient(conflict);
        }
    }
    
    public ResolutionResult resolveDeleteConflict(ConflictData conflict) {
        // 2. Deletação vs Modificação: Modificação sempre vence
        if (conflict.getOperation() == Operation.DELETE) {
            return ResolutionResult.cancelDelete(conflict);
        }
        return ResolutionResult.keepModification(conflict);
    }
    
    public ResolutionResult resolveDuplicateCreation(ConflictData conflict) {
        // 3. Criação dupla: Merge se possível
        if (canMerge(conflict.getClientData(), conflict.getServerData())) {
            return ResolutionResult.merge(conflict);
        } else {
            return ResolutionResult.requireManualResolution(conflict);
        }
    }
}
```

#### **Resolução Manual (quando necessário):**
```java
// ConflictResolutionDialog.java (Desktop)
public class ConflictResolutionDialog extends JDialog {
    
    public void showConflict(ConflictData conflict) {
        // Mostra ambas versões para o usuário
        displayClientVersion(conflict.getClientData());
        displayServerVersion(conflict.getServerData());
        
        // Opções para o usuário
        JButton keepClientBtn = new JButton("Manter Versão Local");
        JButton keepServerBtn = new JButton("Manter Versão do Servidor");
        JButton mergeBtn = new JButton("Combinar Versões");
        
        // Handlers para cada opção
        keepClientBtn.addActionListener(e -> resolveKeepClient(conflict));
        keepServerBtn.addActionListener(e -> resolveKeepServer(conflict));
        mergeBtn.addActionListener(e -> showMergeDialog(conflict));
    }
}
```

---

## 📊 Monitoramento

### 📈 **Métricas de Sincronização**

#### **Dashboard do Servidor:**
```java
// SyncMonitor.java
public class SyncMonitor {
    private static final Map<String, SyncStats> clientStats = new ConcurrentHashMap<>();
    
    public static void recordSync(String clientId, String operation, boolean success) {
        SyncStats stats = clientStats.computeIfAbsent(clientId, k -> new SyncStats());
        
        if (success) {
            stats.incrementSuccess(operation);
        } else {
            stats.incrementError(operation);
        }
        
        stats.setLastSync(new Date());
    }
    
    public static void printStats() {
        System.out.println("📊 ESTATÍSTICAS DE SINCRONIZAÇÃO");
        System.out.println("═══════════════════════════════════");
        
        clientStats.forEach((clientId, stats) -> {
            System.out.printf("📱 Cliente: %s\n", clientId);
            System.out.printf("   ✅ Sucessos: %d\n", stats.getSuccessCount());
            System.out.printf("   ❌ Erros: %d\n", stats.getErrorCount());
            System.out.printf("   🕐 Última sync: %s\n", stats.getLastSync());
            System.out.printf("   📊 Taxa sucesso: %.1f%%\n\n", stats.getSuccessRate());
        });
    }
}
```

#### **Logs Estruturados:**
```java
// SyncLogger.java
public class SyncLogger {
    private static final Logger logger = LoggerFactory.getLogger(SyncLogger.class);
    
    public static void logSyncStart(String clientId, String operation) {
        logger.info("🔄 [{}] Iniciando sync: {}", clientId, operation);
    }
    
    public static void logSyncSuccess(String clientId, String operation, long duration) {
        logger.info("✅ [{}] Sync concluído: {} ({}ms)", clientId, operation, duration);
    }
    
    public static void logSyncError(String clientId, String operation, String error) {
        logger.error("❌ [{}] Erro no sync: {} - {}", clientId, operation, error);
    }
    
    public static void logConflict(String clientId, String operation, String details) {
        logger.warn("⚔️ [{}] Conflito detectado: {} - {}", clientId, operation, details);
    }
}
```

### 🚨 **Alertas e Notificações**

#### **Sistema de Alertas:**
```java
// AlertSystem.java
public class AlertSystem {
    
    public static void checkSyncHealth() {
        clientStats.forEach((clientId, stats) -> {
            // Alerta se taxa de erro > 10%
            if (stats.getErrorRate() > 0.1) {
                sendAlert("Alto índice de erro para cliente: " + clientId);
            }
            
            // Alerta se última sync > 5 minutos atrás
            if (stats.getTimeSinceLastSync() > 300000) {
                sendAlert("Cliente sem sincronizar: " + clientId);
            }
        });
    }
    
    private static void sendAlert(String message) {
        // Email, Slack, ou log especial
        logger.warn("🚨 ALERTA: {}", message);
    }
}
```

Este documento mostra **exatamente como os dados fluem** pelo sistema, permitindo entender e debugar qualquer problema de sincronização ou comunicação!