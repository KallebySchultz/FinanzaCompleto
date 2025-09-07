# 🚀 Guia de Instalação e Configuração - Finanza

## 📋 Índice

1. [Requisitos do Sistema](#-requisitos-do-sistema)
2. [Instalação do Mobile (Android)](#-instalação-do-mobile-android)
3. [Instalação do Desktop (Java)](#-instalação-do-desktop-java)
4. [Configuração do Banco de Dados](#-configuração-do-banco-de-dados)
5. [Configuração de Rede](#-configuração-de-rede)
6. [Primeiro Uso](#-primeiro-uso)
7. [Solução de Problemas](#-solução-de-problemas)
8. [Configurações Avançadas](#-configurações-avançadas)

## 🔧 Requisitos do Sistema

### 📱 **Mobile (Android)**

| Componente | Requisito Mínimo | Recomendado |
|------------|------------------|-------------|
| **Android Version** | 7.0 (API 24) | 10.0+ (API 29+) |
| **RAM** | 2GB | 4GB+ |
| **Storage** | 100MB | 500MB+ |
| **Network** | Wi-Fi ou 4G | Wi-Fi para sync |
| **Permissions** | Internet, Network State | - |

### 🖥️ **Desktop (Servidor)**

| Componente | Requisito Mínimo | Recomendado |
|------------|------------------|-------------|
| **Java** | JDK 11 | JDK 17+ |
| **RAM** | 1GB | 4GB+ |
| **Storage** | 500MB | 2GB+ |
| **MySQL** | 8.0+ | 8.0.35+ |
| **Network** | LAN/Wi-Fi | Ethernet |
| **OS** | Windows 10, macOS 10.15, Ubuntu 18.04 | Mais recentes |

### 🌐 **Rede**

- **Protocolo**: TCP/IP
- **Porta Padrão**: 8080
- **Firewall**: Permitir entrada na porta 8080
- **Conectividade**: Mobile e Desktop na mesma rede

## 📱 Instalação do Mobile (Android)

### 📥 **Método 1: Download do APK (Recomendado)**

1. **Baixe o APK**
   ```bash
   # Se disponível em releases
   wget https://github.com/KallebySchultz/Finanza-Mobile/releases/latest/finanza.apk
   ```

2. **Instale no dispositivo**
   - Transfira o APK para o dispositivo Android
   - Habilite "Fontes desconhecidas" nas configurações
   - Abra o APK e instale

3. **Verificação**
   - Abra o app "Finanza"
   - Verifique se abre a tela de login
   - Teste criação de conta offline

### 🔧 **Método 2: Compilação do Código (Desenvolvimento)**

#### **Pré-requisitos**
```bash
# Instalar Android Studio
# Baixar de: https://developer.android.com/studio

# Configurar ANDROID_HOME
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

#### **Clone e Build**
```bash
# 1. Clone o repositório
git clone https://github.com/KallebySchultz/Finanza-Mobile.git
cd Finanza-Mobile

# 2. Abra no Android Studio
# File > Open > Selecione a pasta do projeto

# 3. Aguarde sincronização do Gradle
# O Android Studio baixará dependências automaticamente

# 4. Configure emulador ou device
# Tools > AVD Manager > Create Virtual Device
# Ou conecte device USB com debug habilitado

# 5. Execute o projeto
# Run > Run 'app' (Shift + F10)
```

#### **Build via Linha de Comando**
```bash
# Verificar ambiente
./gradlew --version

# Build debug
./gradlew assembleDebug

# Build release (necessita keystore)
./gradlew assembleRelease

# Instalar no dispositivo conectado
./gradlew installDebug

# APK gerado em:
# app/build/outputs/apk/debug/app-debug.apk
```

### 📋 **Configurações Pós-Instalação**

1. **Permissões necessárias**
   - Internet: Sincronização com servidor
   - Network State: Detecção de conectividade

2. **Configurações do Android**
   - Desabilitar "Battery Optimization" para o app
   - Permitir execução em segundo plano

## 🖥️ Instalação do Desktop (Java)

### ☕ **Configuração do Ambiente Java**

#### **Windows**
```batch
# 1. Baixar JDK 11+
# https://adoptium.net/temurin/releases/

# 2. Instalar JDK
# Executar o installer baixado

# 3. Configurar JAVA_HOME
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.XX-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

# 4. Verificar instalação
java -version
javac -version
```

#### **macOS**
```bash
# 1. Instalar via Homebrew (recomendado)
brew install openjdk@11

# 2. Configurar JAVA_HOME
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@11' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 3. Verificar instalação
java -version
```

#### **Ubuntu/Debian**
```bash
# 1. Atualizar repositórios
sudo apt update

# 2. Instalar OpenJDK 11
sudo apt install openjdk-11-jdk

# 3. Configurar JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc

# 4. Verificar instalação
java -version
javac -version
```

### 📥 **Download e Compilação**

```bash
# 1. Clone o repositório (se ainda não foi feito)
git clone https://github.com/KallebySchultz/Finanza-Mobile.git
cd Finanza-Mobile

# 2. Navegue para o desktop
cd "DESKTOP VERSION"

# 3. Compile o servidor
cd ServidorFinanza
javac -cp ".:lib/*" src/**/*.java -d build/

# 4. Compile o cliente (opcional)
cd ../ClienteFinanza
javac -cp ".:lib/*" src/**/*.java -d build/
```

### 📦 **Dependências do Desktop**

Certifique-se de ter as bibliotecas necessárias:

```
DESKTOP VERSION/
├── lib/
│   ├── mysql-connector-java-8.0.33.jar
│   ├── json-simple-1.1.1.jar
│   └── outros-jars-necessários.jar
```

**Download das dependências:**
```bash
# MySQL Connector
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.33.jar

# JSON Simple
wget https://repo1.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar
```

## 🗄️ Configuração do Banco de Dados

### 🐬 **Instalação do MySQL**

#### **Windows**
```bash
# 1. Baixar MySQL Installer
# https://dev.mysql.com/downloads/installer/

# 2. Executar installer
# Selecionar "Developer Default"
# Configurar senha root
# Iniciar MySQL Service

# 3. Verificar instalação
mysql --version
```

#### **macOS**
```bash
# 1. Instalar via Homebrew
brew install mysql

# 2. Iniciar serviço
brew services start mysql

# 3. Configurar segurança
mysql_secure_installation

# 4. Verificar instalação
mysql --version
```

#### **Ubuntu/Debian**
```bash
# 1. Atualizar repositórios
sudo apt update

# 2. Instalar MySQL Server
sudo apt install mysql-server

# 3. Configurar segurança
sudo mysql_secure_installation

# 4. Iniciar serviço
sudo systemctl start mysql
sudo systemctl enable mysql

# 5. Verificar instalação
mysql --version
```

### 🏗️ **Criação do Banco de Dados**

```sql
-- 1. Conectar ao MySQL como root
mysql -u root -p

-- 2. Criar banco de dados
CREATE DATABASE finanza_db;

-- 3. Criar usuário (opcional, mais seguro)
CREATE USER 'finanza_user'@'localhost' IDENTIFIED BY 'finanza_password';
GRANT ALL PRIVILEGES ON finanza_db.* TO 'finanza_user'@'localhost';
FLUSH PRIVILEGES;

-- 4. Usar o banco criado
USE finanza_db;

-- 5. Executar scripts de criação das tabelas
SOURCE /caminho/para/DESKTOP VERSION/banco/create_tables.sql;

-- 6. Inserir dados de exemplo (opcional)
SOURCE /caminho/para/DESKTOP VERSION/banco/sample_data.sql;

-- 7. Verificar tabelas criadas
SHOW TABLES;
DESCRIBE usuarios;
```

### 📝 **Script de Criação das Tabelas**

Crie o arquivo `create_tables.sql`:

```sql
-- Tabela de usuários
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uuid VARCHAR(36) UNIQUE DEFAULT (UUID()),
    last_modified BIGINT DEFAULT 0,
    sync_status INT DEFAULT 1,
    last_sync_time BIGINT DEFAULT 0,
    INDEX idx_usuario_uuid (uuid),
    INDEX idx_usuario_email (email),
    INDEX idx_usuario_sync (sync_status, last_modified)
);

-- Tabela de contas
CREATE TABLE contas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0,
    saldo_atual DECIMAL(15,2) NOT NULL DEFAULT 0,
    usuario_id INT NOT NULL,
    uuid VARCHAR(36) UNIQUE DEFAULT (UUID()),
    last_modified BIGINT DEFAULT 0,
    sync_status INT DEFAULT 1,
    last_sync_time BIGINT DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_conta_usuario (usuario_id),
    INDEX idx_conta_uuid (uuid)
);

-- Tabela de categorias
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo ENUM('receita', 'despesa') NOT NULL,
    cor VARCHAR(7) DEFAULT '#2196F3',
    usuario_id INT NOT NULL,
    uuid VARCHAR(36) UNIQUE DEFAULT (UUID()),
    last_modified BIGINT DEFAULT 0,
    sync_status INT DEFAULT 1,
    last_sync_time BIGINT DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_categoria_usuario (usuario_id),
    INDEX idx_categoria_tipo (tipo),
    INDEX idx_categoria_uuid (uuid)
);

-- Tabela de lançamentos/movimentações
CREATE TABLE movimentacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(15,2) NOT NULL,
    data BIGINT NOT NULL,
    descricao TEXT NOT NULL,
    tipo ENUM('receita', 'despesa') NOT NULL,
    conta_id INT NOT NULL,
    categoria_id INT NOT NULL,
    usuario_id INT NOT NULL,
    uuid VARCHAR(36) UNIQUE DEFAULT (UUID()),
    last_modified BIGINT DEFAULT 0,
    sync_status INT DEFAULT 1,
    last_sync_time BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_movimentacao_usuario (usuario_id),
    INDEX idx_movimentacao_conta (conta_id),
    INDEX idx_movimentacao_categoria (categoria_id),
    INDEX idx_movimentacao_data (data),
    INDEX idx_movimentacao_uuid (uuid),
    INDEX idx_movimentacao_sync (sync_status, last_modified)
);

-- Tabela de logs de sincronização (opcional)
CREATE TABLE sync_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    entity_type VARCHAR(50),
    entity_uuid VARCHAR(36),
    operation VARCHAR(20),
    timestamp BIGINT,
    success BOOLEAN,
    error_message TEXT,
    INDEX idx_sync_log_usuario (usuario_id),
    INDEX idx_sync_log_timestamp (timestamp)
);
```

### 🔐 **Configuração de Acesso**

Edite o arquivo `DatabaseUtil.java`:

```java
public class DatabaseUtil {
    // Configurações de conexão
    private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
    private static final String DB_USER = "finanza_user"; // ou "root"
    private static final String DB_PASSWORD = "finanza_password"; // sua senha
    
    // Configurações de connection pool (opcional)
    private static final int MAX_CONNECTIONS = 10;
    private static final int CONNECTION_TIMEOUT = 30000; // 30 segundos
}
```

## 🌐 Configuração de Rede

### 🔧 **Configurações do Servidor**

1. **Porta do Servidor** (padrão: 8080)
   ```java
   // Em ServerSocket.java
   private static final int PORT = 8080;
   ```

2. **Configurações de Firewall**
   ```bash
   # Windows (PowerShell como Admin)
   New-NetFirewallRule -DisplayName "Finanza Server" -Direction Inbound -Port 8080 -Protocol TCP -Action Allow
   
   # macOS
   sudo /usr/libexec/ApplicationFirewall/socketfilterfw --add /path/to/java
   
   # Ubuntu/Linux
   sudo ufw allow 8080
   sudo ufw reload
   ```

3. **Teste de Conectividade**
   ```bash
   # Verificar se porta está aberta (no servidor)
   netstat -an | grep 8080
   
   # Teste de conexão (do mobile/outro PC)
   telnet IP_DO_SERVIDOR 8080
   ```

### 📱 **Configurações do Mobile**

1. **Descobrir IP do Servidor**
   ```bash
   # No PC do servidor
   
   # Windows
   ipconfig
   
   # macOS/Linux
   ifconfig
   # ou
   ip addr show
   ```

2. **Script Automático de Descoberta**
   ```bash
   # Use o script fornecido
   ./descobrir_ip.sh
   ```

3. **Configuração no App**
   - Abra o app Finanza
   - Vá em Configurações → Servidor
   - Digite o IP do servidor (ex: 192.168.1.100)
   - Digite a porta (8080)
   - Teste a conexão

## 🎯 Primeiro Uso

### 🖥️ **Iniciando o Servidor Desktop**

```bash
# 1. Navegue para o diretório do servidor
cd "DESKTOP VERSION/ServidorFinanza"

# 2. Inicie o servidor
java -cp ".:lib/*:build" MainServidor

# 3. Aguarde mensagem de confirmação
# "Servidor iniciado na porta 8080"
# "Aguardando conexões..."
```

### 📱 **Configuração Inicial do Mobile**

1. **Primeiro Acesso**
   - Abra o app Finanza
   - Tela de login será exibida
   - Clique em "Criar Nova Conta"

2. **Criação de Conta**
   ```
   Nome: Seu Nome Completo
   Email: seu@email.com
   Senha: sua_senha_segura
   Confirmar Senha: sua_senha_segura
   ```

3. **Configuração de Contas**
   - Após login, vá em "Contas"
   - Adicione sua primeira conta:
     ```
     Nome: Conta Corrente
     Tipo: Corrente
     Saldo Inicial: 1000.00
     ```

4. **Configuração de Categorias**
   - Vá em "Menu" → "Categorias"
   - Adicione categorias de receita:
     ```
     Salário, Freelance, Investimentos
     ```
   - Adicione categorias de despesa:
     ```
     Alimentação, Transporte, Saúde, Lazer
     ```

5. **Configuração do Servidor**
   - Vá em "Menu" → "Configurações"
   - Configure servidor:
     ```
     IP: 192.168.1.100 (IP do seu desktop)
     Porta: 8080
     ```
   - Teste conexão
   - Se aparecer "🟢 Conectado", a sincronização está funcionando

### 🔄 **Teste de Sincronização**

1. **Adicione uma transação no mobile**
   - Tela principal → Botão "+" → "Receita"
   - Descrição: "Teste de Sincronização"
   - Valor: 100.00
   - Salve

2. **Verifique no servidor**
   - Observe logs do servidor
   - Deve aparecer: "ADD_MOVIMENTACAO recebido"

3. **Verifique no banco**
   ```sql
   USE finanza_db;
   SELECT * FROM movimentacoes ORDER BY id DESC LIMIT 1;
   ```

## 🚨 Solução de Problemas

### 📱 **Problemas do Mobile**

#### **App não instala**
```
Problema: "App não instalado"
Solução:
1. Habilitar "Fontes desconhecidas" nas configurações
2. Verificar espaço de armazenamento
3. Limpar cache do instalador
4. Tentar instalar via ADB: adb install app.apk
```

#### **App trava no login**
```
Problema: Aplicativo trava na tela de login
Solução:
1. Verificar logs: adb logcat | grep Finanza
2. Limpar dados do app
3. Reinstalar aplicativo
4. Verificar versão do Android (mín. 7.0)
```

#### **Sincronização não funciona**
```
Problema: Dados não sincronizam
Diagnóstico:
1. Verificar indicador de conexão (🟢/🔴)
2. Testar conectividade: ping IP_SERVIDOR
3. Verificar se servidor está rodando
4. Conferir IP e porta nas configurações

Solução:
1. Corrigir configurações de rede
2. Reiniciar servidor
3. Reiniciar app mobile
4. Verificar firewall
```

### 🖥️ **Problemas do Desktop**

#### **Erro de conexão MySQL**
```
Problema: "SQLException: Access denied"
Solução:
1. Verificar credenciais em DatabaseUtil.java
2. Testar conexão manual:
   mysql -u finanza_user -p finanza_db
3. Verificar se MySQL está rodando:
   Windows: services.msc → MySQL
   macOS: brew services list | grep mysql
   Linux: systemctl status mysql
4. Recriar usuário e permissões
```

#### **Erro de porta em uso**
```
Problema: "Port 8080 already in use"
Solução:
1. Verificar processo usando a porta:
   Windows: netstat -ano | findstr :8080
   macOS/Linux: lsof -i :8080
2. Encerrar processo conflitante
3. Ou alterar porta no código:
   private static final int PORT = 8081;
```

#### **Erro de compilação Java**
```
Problema: "ClassNotFoundException" ou "NoClassDefFoundError"
Solução:
1. Verificar JAVA_HOME: echo $JAVA_HOME
2. Verificar classpath: verificar lib/*.jar
3. Baixar dependências faltantes
4. Recompilar com classpath correto:
   javac -cp ".:lib/*" src/**/*.java
```

### 🌐 **Problemas de Rede**

#### **Mobile não encontra servidor**
```
Diagnóstico:
1. Verificar se estão na mesma rede
2. Ping do mobile para servidor
3. Verificar firewall
4. Testar com IP fixo

Solução:
1. Conectar ambos na mesma rede Wi-Fi
2. Configurar IP estático no servidor
3. Liberar porta 8080 no firewall
4. Desabilitar VPN temporariamente
```

## ⚙️ Configurações Avançadas

### 🔧 **Configurações de Performance**

#### **Mobile**
```java
// SyncService.java - Configurações de sincronização
private static final int SYNC_INTERVAL = 30000; // 30 segundos
private static final int MAX_RETRY_ATTEMPTS = 3;
private static final int CONNECTION_TIMEOUT = 15000; // 15 segundos
private static final int READ_TIMEOUT = 30000; // 30 segundos
```

#### **Desktop**
```java
// ServerSocket.java - Configurações do servidor
private static final int MAX_CLIENTS = 50; // Máximo de clientes
private static final int SOCKET_TIMEOUT = 60000; // 60 segundos
private static final int THREAD_POOL_SIZE = 10; // Pool de threads
```

### 🗄️ **Configurações de Banco**

```sql
-- Otimizações de performance MySQL
SET innodb_buffer_pool_size = 256M;
SET innodb_log_file_size = 64M;
SET max_connections = 100;

-- Índices adicionais para performance
CREATE INDEX idx_movimentacao_data_usuario ON movimentacoes(data, usuario_id);
CREATE INDEX idx_sync_pending ON movimentacoes(sync_status) WHERE sync_status = 2;
```

### 🔐 **Configurações de Segurança**

#### **Banco de Dados**
```sql
-- Criar usuário específico com permissões limitadas
CREATE USER 'finanza_app'@'localhost' IDENTIFIED BY 'senha_forte_123!';
GRANT SELECT, INSERT, UPDATE, DELETE ON finanza_db.* TO 'finanza_app'@'localhost';
GRANT CREATE TEMPORARY TABLES ON finanza_db.* TO 'finanza_app'@'localhost';
FLUSH PRIVILEGES;
```

#### **Firewall**
```bash
# Restringir acesso apenas à rede local
sudo ufw allow from 192.168.1.0/24 to any port 8080

# Ou apenas IPs específicos
sudo ufw allow from 192.168.1.100 to any port 8080
```

### 📊 **Monitoramento e Logs**

#### **Configuração de Logs**
```java
// Logger configuration
Logger logger = Logger.getLogger("FinanzaServer");
logger.setLevel(Level.INFO);

FileHandler fileHandler = new FileHandler("finanza-server.log", true);
fileHandler.setFormatter(new SimpleFormatter());
logger.addHandler(fileHandler);
```

#### **Scripts de Monitoramento**
```bash
#!/bin/bash
# monitor_server.sh
while true; do
    if ! pgrep -f "MainServidor" > /dev/null; then
        echo "$(date): Servidor parado, reiniciando..."
        cd "DESKTOP VERSION/ServidorFinanza"
        nohup java -cp ".:lib/*:build" MainServidor &
    fi
    sleep 30
done
```

### 🔄 **Backup e Restore**

#### **Script de Backup**
```bash
#!/bin/bash
# backup_finanza.sh
BACKUP_DIR="/backup/finanza"
DATE=$(date +%Y%m%d_%H%M%S)

# Backup MySQL
mysqldump -u finanza_user -p finanza_db > "$BACKUP_DIR/finanza_db_$DATE.sql"

# Backup aplicação
tar -czf "$BACKUP_DIR/finanza_app_$DATE.tar.gz" "DESKTOP VERSION/"

echo "Backup concluído: $DATE"
```

#### **Script de Restore**
```bash
#!/bin/bash
# restore_finanza.sh
BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "Uso: $0 <arquivo_backup.sql>"
    exit 1
fi

# Restore MySQL
mysql -u finanza_user -p finanza_db < "$BACKUP_FILE"
echo "Restore concluído"
```

---

## 🎉 Conclusão

Após seguir este guia, você deve ter:

- ✅ Sistema Finanza completamente instalado e configurado
- ✅ Mobile Android funcionando offline e online
- ✅ Servidor desktop rodando com MySQL
- ✅ Sincronização bidirecional operacional
- ✅ Banco de dados configurado e populado
- ✅ Rede configurada corretamente

Para suporte adicional, consulte:
- 📚 [README.md](README.md) - Visão geral do projeto
- 🏗️ [ARCHITECTURE.md](ARCHITECTURE.md) - Documentação da arquitetura
- 🐛 [Issues do GitHub](https://github.com/KallebySchultz/Finanza-Mobile/issues) - Problemas conhecidos e soluções

**Bom uso do sistema Finanza! 💰**