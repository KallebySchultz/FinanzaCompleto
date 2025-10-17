# 🖥️ Finanza Desktop - Aplicação Java

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Swing](https://img.shields.io/badge/Swing-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![TCP](https://img.shields.io/badge/TCP-4285F4?style=for-the-badge&logo=internetexplorer&logoColor=white)](https://en.wikipedia.org/wiki/Transmission_Control_Protocol)

**Finanza Desktop** é uma aplicação Java robusta para controle financeiro empresarial e pessoal, desenvolvida com arquitetura cliente-servidor e comunicação TCP/IP para sincronização com dispositivos móveis.

## 🌟 Características Principais

### 🖥️ **Interface Desktop Profissional**
- **Java Swing**: Interface gráfica nativa e responsiva
- **Look & Feel Moderno**: Aparência consistente e profissional
- **Multi-janela**: Suporte a múltiplas telas e monitores
- **Navegação Intuitiva**: Menus e atalhos de teclado otimizados

### 🌐 **Servidor TCP Robusto**
- **Comunicação Direta**: Sockets TCP/IP para alta performance
- **Multi-cliente**: Suporte a múltiplas conexões simultâneas
- **Protocolo Customizado**: Comunicação eficiente com mobile
- **Monitoramento**: Logs detalhados de conexões e operações

### 🗄️ **Banco de Dados Empresarial**
- **MySQL**: Banco de dados robusto e escalável
- **Transações ACID**: Garantia de integridade dos dados
- **Backup Automático**: Rotinas de backup configuráveis
- **Performance**: Queries otimizadas e indexação

### 📊 **Relatórios Avançados**
- **Exportação Múltipla**: CSV, HTML, PDF
- **Gráficos Dinâmicos**: Visualizações interativas
- **Filtros Avançados**: Busca por múltiplos critérios
- **Impressão**: Relatórios formatados para impressão

## 📸 Interface da Aplicação

### Servidor Iniciando
![Servidor Desktop](../screenshots/desktop/01-server-start.png)
*Console do servidor mostrando inicialização e conexão com MySQL*

### Dashboard Principal
![Dashboard Desktop](../screenshots/desktop/03-dashboard.png)
*Interface principal com funcionalidades completas de gestão financeira*

### Monitor de Sincronização
![Sincronização Desktop](../screenshots/desktop/04-sync-monitor.png)
*Painel de monitoramento em tempo real das conexões mobile*

### Relatórios Financeiros
![Relatórios Desktop](../screenshots/desktop/05-reports.png)
*Sistema de relatórios com gráficos e exportação profissional*

## Estrutura do Projeto

```
Finanza/
├── ClienteFinanza/          # Aplicação cliente (GUI)
│   ├── controller/          # Controllers MVC
│   ├── model/              # Modelos de dados
│   ├── view/               # Interface gráfica
│   ├── util/               # Utilitários
│   └── MainCliente.java    # Classe principal do cliente
├── ServidorFinanza/        # Aplicação servidor
│   ├── controller/         # Controllers do servidor
│   ├── model/             # Modelos de dados
│   ├── dao/               # Data Access Objects
│   ├── server/            # Lógica do servidor
│   ├── util/              # Utilitários
│   └── MainServidor.java  # Classe principal do servidor
├── docs/                  # Documentação
├── banco/                 # Scripts do banco de dados
│   └── script_inicial.sql # Script de criação das tabelas
├── run_server.sh          # Script para executar servidor
└── run_client.sh          # Script para executar cliente
```

## 🚀 Funcionalidades

### ✅ **Implementadas**

#### 🔐 **Sistema de Autenticação**
- [x] **Login Seguro**: Autenticação com criptografia SHA-256
- [x] **Registro de Usuários**: Cadastro completo com validação
- [x] **Sessões**: Gerenciamento seguro de sessões de usuário
- [x] **Controle de Acesso**: Diferentes níveis de permissão

#### 💼 **Gestão Financeira Completa**
- [x] **Dashboard Executivo**: Resumo financeiro com métricas-chave
- [x] **Contas Bancárias**: CRUD completo de contas (corrente, poupança, cartão)
- [x] **Categorias**: Organização personalizada de receitas e despesas
- [x] **Movimentações**: Registro completo de transações financeiras
- [x] **Saldos Automáticos**: Cálculo em tempo real de saldos por conta

#### 🌐 **Servidor TCP/IP**
- [x] **Multi-cliente**: Suporte a conexões simultâneas de dispositivos mobile
- [x] **Protocolo Customizado**: Comunicação eficiente via pipe-separated values
- [x] **Monitoramento**: Logs detalhados de conexões e comandos
- [x] **Sincronização Bidirecional**: Dados atualizados em tempo real

#### 🗄️ **Banco de Dados Robusto**
- [x] **MySQL Integration**: Conectividade otimizada com MySQL
- [x] **Transações ACID**: Garantia de integridade dos dados
- [x] **Backup e Restore**: Funcionalidades de backup automático
- [x] **Performance**: Queries otimizadas e indexação adequada

#### 📊 **Relatórios Avançados**
- [x] **Exportação Múltipla**: CSV aprimorado, HTML profissional, CSV simples
- [x] **Gráficos**: Visualizações financeiras interativas
- [x] **Filtros Avançados**: Busca por período, categoria, conta
- [x] **Formatação**: Tabelas com bordas e totais automáticos

### 🔄 **Em Desenvolvimento**
- 🚧 **Interface Web**: Painel administrativo web
- 🚧 **APIs REST**: Endpoints para integração externa
- 🚧 **Notificações**: Sistema de alertas em tempo real
- 🚧 **Auditoria**: Logs de auditoria para compliance
- 🚧 **Multi-tenant**: Suporte a múltiplas empresas

### 🎯 **Destaque: Exportação Aprimorada**

O sistema oferece **3 formatos de exportação** profissionais:

#### 📄 **CSV Aprimorado**
```
┌─────────────────────────────────────────────────────┐
│                  RELATÓRIO FINANCEIRO               │
├─────────────────┬─────────────┬─────────────────────┤
│ Data           │ Valor       │ Descrição           │
├─────────────────┼─────────────┼─────────────────────┤
│ 2024-01-15     │ R$ 1.500,00 │ Salário             │
│ 2024-01-16     │ R$ -350,00  │ Supermercado        │
├─────────────────┼─────────────┼─────────────────────┤
│ TOTAL          │ R$ 1.150,00 │                     │
└─────────────────┴─────────────┴─────────────────────┘
```

#### 🌐 **HTML Profissional**
- Relatórios web responsivos para impressão
- Gráficos integrados e formatação avançada
- Logotipo e cabeçalho personalizáveis
- Compatível com todos os navegadores

#### 📋 **CSV Simples**
- Formato padrão para importação em outros sistemas
- Compatibilidade com Excel e Google Sheets
- Campos separados por vírgula ou ponto-e-vírgula

Para detalhes completos, consulte [documentação de exportação](docs/EXPORTACAO_APRIMORADA.md).

## 📋 Requisitos Técnicos

### 💻 **Sistema Operacional**
- **Windows**: 7, 8, 10, 11 (32/64-bit)
- **Linux**: Ubuntu 18.04+, CentOS 7+, Debian 9+
- **macOS**: 10.14+ (Mojave ou superior)

### ☕ **Java**
- **Versão**: Java 8 (JRE/JDK) ou superior
- **Recomendado**: Java 11 LTS ou Java 17 LTS
- **Memória**: Mínimo 2GB RAM, recomendado 4GB+
- **Armazenamento**: 500MB livres para aplicação

### 🗄️ **Banco de Dados**
- **MySQL**: 5.7, 8.0+ (recomendado 8.0+)
- **Memória**: Mínimo 1GB RAM dedicada
- **Armazenamento**: 1GB+ para dados
- **Conectividade**: Porta 3306 (padrão) ou customizada

### 🌐 **Rede**
- **Porta TCP**: 8080 (padrão) para comunicação mobile
- **Firewall**: Liberar porta escolhida para conexões locais
- **Conectividade**: Rede local Wi-Fi ou Ethernet

### 🛠️ **Desenvolvimento**
- **IDE**: NetBeans 12+, IntelliJ IDEA, Eclipse
- **Maven/Gradle**: Para gerenciamento de dependências (opcional)
- **Git**: Para controle de versão

## 🗄️ Configuração do Banco de Dados

> **✨ NOVO:** O servidor agora **inicializa automaticamente** o banco de dados! Você só precisa criar o database, e o servidor criará todas as tabelas na primeira execução.

### 📦 **Instalação do MySQL**

#### **Windows**
1. Baixe MySQL Installer do [site oficial](https://dev.mysql.com/downloads/installer/)
2. Execute o instalador e escolha "Developer Default"
3. Configure senha root e crie usuário `finanza`
4. Anote host, porta, usuário e senha

#### **Linux (Ubuntu/Debian)**
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
sudo mysql -u root -p
```

#### **macOS**
```bash
# Via Homebrew
brew install mysql
brew services start mysql
mysql_secure_installation
```

### ⚙️ **Configuração Rápida (Recomendado)**

#### 1. **Criar Database**
```sql
-- Conecte como root
mysql -u root -p

-- Crie apenas o banco de dados (as tabelas serão criadas automaticamente)
CREATE DATABASE finanza_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Opcionalmente**, crie um usuário específico:
```sql
CREATE USER 'finanza'@'localhost' IDENTIFIED BY 'senha_segura';
GRANT ALL PRIVILEGES ON finanza_db.* TO 'finanza'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. **Configurar Aplicação** (se necessário)
Edite `ServidorFinanza/src/util/DatabaseUtil.java` apenas se usar credenciais diferentes:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
private static final String DB_USER = "root";  // ou "finanza"
private static final String DB_PASSWORD = "";  // ou sua senha
```

#### 3. **Iniciar o Servidor**
Execute o servidor normalmente:
```bash
cd "DESKTOP VERSION"
./run_server.sh
```

**O servidor criará automaticamente todas as tabelas necessárias!** ✓

Você verá:
```
✓ Tabela 'usuario' verificada/criada
✓ Tabela 'conta' verificada/criada
✓ Tabela 'categoria' verificada/criada
✓ Tabela 'movimentacao' verificada/criada
✓ Banco de dados inicializado com sucesso
```

### 🔧 **Configuração Manual (Opcional)**

Se preferir criar as tabelas manualmente ou encontrar problemas:
```bash
# Execute o script de criação das tabelas
mysql -u root -p finanza_db < banco/script_inicial.sql
```

Para mais detalhes sobre configuração e solução de problemas, consulte [SETUP_DATABASE.md](SETUP_DATABASE.md).

### 🔧 **Configurações Avançadas**

#### **Performance MySQL**
Adicione ao `/etc/mysql/mysql.conf.d/mysqld.cnf`:
```ini
[mysqld]
innodb_buffer_pool_size = 1G
max_connections = 200
innodb_log_file_size = 256M
```

#### **Backup Automático**
```bash
# Criar script de backup
#!/bin/bash
mysqldump -u finanza -p finanza_db > backup_$(date +%Y%m%d).sql
```

## 🚀 Como Executar

### 📦 **Instalação Rápida**

#### 🔧 **Opção 1: Scripts Automatizados (Recomendado)**

**1. Preparar scripts**
```bash
# Tornar scripts executáveis
chmod +x run_server.sh
chmod +x run_client.sh
```

**2. Executar o Servidor**
```bash
# Inicia servidor TCP na porta 8080
./run_server.sh
```

**3. Executar o Cliente (Opcional)**
```bash
# Em outro terminal - interface desktop
./run_client.sh
```

#### ⚙️ **Opção 2: Execução Manual**

**Compilar e executar servidor:**
```bash
cd ServidorFinanza

# Compilar todos os arquivos
javac -cp ".:../lib/*" \
    model/*.java \
    util/*.java \
    dao/*.java \
    server/*.java \
    controller/*.java \
    MainServidor.java

# Executar servidor
java -cp ".:../lib/*" MainServidor
```

**Compilar e executar cliente:**
```bash
cd ClienteFinanza

# Compilar interface gráfica
javac -cp ".:../lib/*" \
    model/*.java \
    util/*.java \
    controller/*.java \
    view/*.java \
    MainCliente.java

# Executar cliente
java -cp ".:../lib/*" MainCliente
```

### 🏗️ **Setup para Desenvolvimento**

#### **1. Clone o Repositório**
```bash
git clone https://github.com/KallebySchultz/Finanza-Mobile.git
cd "Finanza-Mobile/DESKTOP VERSION"
```

#### **2. Configurar IDE**

##### **NetBeans**
1. File → Open Project
2. Selecione pasta "DESKTOP VERSION"
3. Clique direito no projeto → Properties
4. Libraries → Add JAR/Folder → Adicione mysql-connector-java

##### **IntelliJ IDEA**
1. File → Open → Selecione "DESKTOP VERSION"
2. File → Project Structure → Libraries
3. Adicione mysql-connector-java.jar

##### **Eclipse**
1. File → Import → Existing Projects into Workspace
2. Selecione "DESKTOP VERSION"
3. Right-click project → Properties → Java Build Path
4. Libraries → Add External JARs → mysql-connector-java

#### **3. Dependências Necessárias**
```bash
# Baixar driver MySQL (se não estiver incluído)
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.33.jar
# Coloque em lib/ ou adicione ao classpath
```

### 🖥️ **Primeiro Uso**

#### **1. Verificar Pré-requisitos**
```bash
# Verificar Java
java -version
# Deve mostrar 1.8 ou superior

# Verificar MySQL
mysql --version
systemctl status mysql  # Linux
# ou
brew services list | grep mysql  # macOS
```

#### **2. Inicializar Sistema**
1. **MySQL**: Confirme que está rodando
2. **Banco**: Execute scripts em `banco/script_inicial.sql`
3. **Servidor**: Execute `./run_server.sh`
4. **Teste**: Verifique logs "Servidor iniciado na porta 8080"

#### **3. Configuração Inicial**
- **Usuário Admin**: Criado automaticamente no primeiro boot
- **Porta TCP**: Padrão 8080 (configurável em código)
- **Conexões**: Aguardando dispositivos mobile

### 🔧 **Configurações Avançadas**

#### **Alterar Porta do Servidor**
Edite `ServidorFinanza/server/TCPServer.java`:
```java
private static final int PORT = 8080; // Altere aqui
```

#### **Configurar Logs**
Edite `ServidorFinanza/util/Logger.java`:
```java
private static final boolean DEBUG_MODE = true; // Para logs detalhados
```

#### **Otimizações de Performance**
```bash
# Executar com mais memória
java -Xmx2g -Xms1g -cp ".:../lib/*" MainServidor
```

## Protocolo de Comunicação

O sistema utiliza um protocolo simples baseado em texto sobre TCP/IP:

- **Formato:** `COMANDO|DADOS`
- **Separador de campos:** `;`
- **Porta padrão:** 8080

### Comandos Disponíveis

- `LOGIN|email|senha` - Realizar login
- `REGISTER|nome|email|senha` - Registrar novo usuário
- `LOGOUT` - Fazer logout
- `GET_DASHBOARD` - Obter dados do dashboard

### Respostas

- `OK|dados` - Operação bem-sucedida
- `ERROR|mensagem` - Erro genérico
- `INVALID_CREDENTIALS` - Credenciais inválidas
- `USER_EXISTS` - Usuário já existe
- `INVALID_DATA|mensagem` - Dados inválidos

## Modelo de Dados

### Usuário
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- nome (VARCHAR(100), NOT NULL)
- email (VARCHAR(150), UNIQUE, NOT NULL)
- senha_hash (VARCHAR(255), NOT NULL)
- data_criacao (TIMESTAMP)
- data_atualizacao (TIMESTAMP)

### Conta
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- nome (VARCHAR(100), NOT NULL)
- tipo (ENUM: 'corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro')
- saldo_inicial (DECIMAL(10,2))
- id_usuario (INT, FOREIGN KEY)
- data_criacao (TIMESTAMP)

### Categoria
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- nome (VARCHAR(100), NOT NULL)
- tipo (ENUM: 'receita', 'despesa')
- id_usuario (INT, FOREIGN KEY)
- data_criacao (TIMESTAMP)

### Movimentação
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- valor (DECIMAL(10,2), NOT NULL)
- data (DATE, NOT NULL)
- descricao (TEXT)
- tipo (ENUM: 'receita', 'despesa')
- id_conta (INT, FOREIGN KEY)
- id_categoria (INT, FOREIGN KEY)
- id_usuario (INT, FOREIGN KEY)
- data_criacao (TIMESTAMP)
- data_atualizacao (TIMESTAMP)

## Segurança

- Senhas são armazenadas como hash SHA-256
- Validação de dados no cliente e servidor
- Comunicação via sockets TCP/IP

## Desenvolvimento

O projeto segue o padrão MVC (Model-View-Controller) e possui separação clara entre:

- **Model:** Entidades de dados
- **View:** Interface gráfica (Swing)
- **Controller:** Lógica de negócio e comunicação
- **DAO:** Acesso aos dados (servidor)
- **Util:** Utilitários e helpers

## 🐛 Solução de Problemas

### ❌ **Problemas Comuns**

#### **Erro de Conexão MySQL**
```bash
# Verificar se MySQL está rodando
systemctl status mysql        # Linux
brew services list mysql      # macOS

# Testar conexão
mysql -u finanza -p finanza_db

# Verificar configurações
cat ServidorFinanza/util/DatabaseUtil.java
```

#### **Porta 8080 em Uso**
```bash
# Verificar processo usando a porta
netstat -tulpn | grep 8080    # Linux
lsof -i :8080                 # macOS

# Alterar porta no código ou matar processo
kill -9 [PID]
```

#### **Erro de Compilação**
```bash
# Verificar Java
java -version
javac -version

# Verificar classpath
echo $CLASSPATH

# Recompilar tudo
find . -name "*.class" -delete
./run_server.sh
```

#### **Mobile Não Conecta**
```bash
# Verificar firewall
sudo ufw status               # Linux
# Liberar porta se necessário
sudo ufw allow 8080

# Verificar IP da máquina
ip addr show                  # Linux
ifconfig                      # macOS
```

### 📊 **Logs e Monitoramento**

#### **Logs do Servidor**
- Console mostra conexões em tempo real
- Comandos recebidos são logados
- Erros de SQL são exibidos
- Status de sincronização é mostrado

#### **Ativar Debug Detalhado**
```java
// Em util/Logger.java
private static final boolean DEBUG = true;
```

## 🔒 Segurança

### 🛡️ **Recursos de Segurança**
- **Criptografia**: Senhas SHA-256
- **Validação**: Dados validados cliente e servidor
- **Sanitização**: Queries SQL preparadas
- **Sessão**: Controle de sessões de usuário

### ⚠️ **Considerações de Produção**
- Implementar SSL/TLS para comunicação
- Configurar firewall adequadamente
- Usar senhas fortes para MySQL
- Fazer backup regular do banco

## 📈 Performance e Otimização

### 🚀 **Otimizações Implementadas**
- Conexões de banco reutilizadas
- Queries SQL otimizadas
- Cache de dados frequentes
- Índices no banco de dados

### 📊 **Monitoramento**
```sql
-- Verificar performance do MySQL
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads%';
SHOW STATUS LIKE 'Connections';
```

## 🤝 Contribuição

### 🔧 **Setup de Desenvolvimento**
1. **Fork** o projeto no GitHub
2. **Clone** seu fork localmente
3. **Configure** ambiente de desenvolvimento
4. **Crie** branch para sua feature
5. **Teste** suas mudanças completamente
6. **Commit** com mensagens claras
7. **Push** e abra Pull Request

### 📝 **Padrões de Código**
- **Indentação**: 4 espaços
- **Nomenclatura**: camelCase para métodos, PascalCase para classes
- **Comentários**: JavaDoc para métodos públicos
- **Formatação**: Seguir Google Java Style Guide

### 🧪 **Testes**
```bash
# Executar testes (quando disponíveis)
mvn test

# Testes manuais recomendados
1. Login/logout de usuários
2. CRUD de contas/categorias
3. Sincronização com mobile
4. Exportação de relatórios
```

## 📄 Licença

Este projeto está licenciado sob a [MIT License](../LICENSE) - veja o arquivo de licença para detalhes.

## 📞 Suporte

### 🐛 **Reportar Bugs**
- [GitHub Issues](https://github.com/KallebySchultz/Finanza-Mobile/issues)
- Inclua logs, versão Java, SO
- Passos para reproduzir o problema

### 💡 **Solicitar Features**
- [GitHub Discussions](https://github.com/KallebySchultz/Finanza-Mobile/discussions)
- Descreva o caso de uso
- Inclua mockups se aplicável

### 📧 **Contato**
- Email: [finanza.desktop@exemplo.com](mailto:finanza.desktop@exemplo.com)
- Documentação: [Wiki do Projeto](https://github.com/KallebySchultz/Finanza-Mobile/wiki)

---

<div align="center">

**🖥️ Desenvolvido com ☕ para Desktop**

[⬆ Voltar ao topo](#️-finanza-desktop---aplicação-java)

</div>