# Guia de Instalação e Execução - Finanza v2.0

## 🚀 Novidades da Versão 2.0

### Cliente Desktop Modernizado
- **⚙️ Interface de Configurações** - Configure IP e porta do servidor facilmente
- **🎨 Design Moderno** - Interface similar ao aplicativo móvel com ícones e efeitos
- **🔗 Teste de Conexão** - Verificação automática da conectividade
- **💾 Configurações Persistentes** - Suas configurações são salvas automaticamente

## ⚙️ Pré-requisitos

- **Java JDK 8 ou superior** (para servidor e desktop)
- **Maven** (para compilação do cliente desktop)
- **Android Studio** (para aplicativo móvel)
- **Dispositivo Android ou Emulador** (para teste do app)

## 🚀 Passo a Passo

### 1. Preparar o Ambiente

1. Clone o repositório
2. Certifique-se de que o Java está instalado:
   ```bash
   java -version
   javac -version
   ```
3. Certifique-se de que o Maven está instalado:
   ```bash
   mvn -version
   ```

### 2. Executar o Servidor (PRIMEIRO)

**Opção 1 - Scripts Automáticos:**

**Windows:**
1. Abra o Prompt de Comando
2. Navegue até `server-java`
3. Execute: `run-server.bat`

**Linux/Mac:**
1. Abra o Terminal
2. Navegue até `server-java`
3. Execute: `chmod +x run-server.sh && ./run-server.sh`

**Opção 2 - Comando Manual:**
```bash
cd server-java
javac -d . src/main/java/com/finanza/server/FinanzaServer.java
java com.finanza.server.FinanzaServer
```

**Você deve ver:**
```
=== Servidor Finanza ===
Servidor iniciado na porta 8080
Aguardando conexões...
```

### 3. Executar o Cliente Desktop

**Opção 1 - Maven (Recomendado):**
```bash
cd desktop-client
mvn compile
mvn exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"
```

**Opção 2 - Scripts:**

**Windows:**
1. Novo Prompt de Comando
2. Navegue até `desktop-client`
3. Execute: `run-desktop.bat`

**Linux/Mac:**
1. Novo Terminal
2. Navegue até `desktop-client`
3. Execute: `chmod +x run-desktop.sh && ./run-desktop.sh`

### 4. Configurar Rede no Cliente Desktop

1. **Quando o cliente desktop abrir**, clique em **⚙️ Configurações**
2. **Configure as informações de rede:**
   - **IP do Servidor:** Use o IP do computador (ex: 192.168.1.100)
   - **Porta:** 8080 (padrão)
3. **Teste a conexão** clicando em "🔗 Testar Conexão"
4. **Salve as configurações** clicando em "✅ Salvar"

**Para descobrir o IP do computador:**

**Windows:**
```cmd
ipconfig
```

**Linux/Mac:**
```bash
ip addr show
# ou
ifconfig
```

### 5. Compilar e Instalar o App Android

1. Abra o projeto no Android Studio
2. Aguarde a sincronização do Gradle
3. Execute o app em dispositivo/emulador

**Nota:** Conforme solicitado, não alteramos as configurações do Android (gradle, SDK, AGP).

### 6. Testar o Sistema Completo

1. **Servidor**:
   - Deve estar rodando e mostrando logs no console
   
2. **Desktop Client**:
   - Faça login na tela inicial
   - Use **⚙️ Configurações** para testar conectividade
   - Navegue pelas telas: 🏠 Dashboard, 💳 Contas, 📊 Movimentações, 👤 Perfil

3. **App Android**: 
   - Crie uma conta na tela de registro
   - Navegue pelo sistema
   - Use funcionalidades de sincronização (se disponíveis)

## 🔧 Solução de Problemas

### ❌ Servidor - "Address already in use"
- Feche o servidor anterior (Ctrl+C)
- Aguarde alguns segundos e tente novamente
- Verifique se nenhum outro processo está usando a porta 8080:
  ```bash
  netstat -an | grep 8080
  ```

### ❌ Erro de compilação Java
- Verifique se o JDK está instalado corretamente
- Verifique se está na pasta correta
- Para o cliente desktop, use Maven:
  ```bash
  mvn clean compile
  ```

### ❌ Cliente Desktop não conecta
1. **Use a tela de configurações** para verificar e ajustar as configurações
2. **Teste a conectividade** usando o botão de teste
3. **Verifique firewall** - deve permitir porta 8080
4. **Certifique-se** que servidor está rodando

### ❌ App Android não compila
- O projeto mantém configurações originais (AGP, SDK, etc.)
- Sync Project with Gradle Files
- Clean Project → Rebuild Project

### ❌ Conexão rejeitada no app móvel
- Certifique-se de que o servidor está rodando
- Use o cliente desktop para verificar se o servidor está acessível
- Para emulador: IP pode ser 10.0.2.2
- Para dispositivo físico: use IP da máquina na rede local

## 💡 Dicas de Uso

1. **Ordem de execução**: Sempre inicie o servidor primeiro
2. **Use o cliente desktop para configuração inicial** - interface mais amigável
3. **Monitore o status da conexão** através dos indicadores visuais
4. **Teste gradual**: Servidor → Desktop → Mobile
5. **Configurações automáticas**: O cliente desktop salva suas configurações
6. **Logs úteis**: Observe o console do servidor para debug

## 🏗️ Estrutura dos Dados

O sistema sincroniza:
- **Usuários**: Informações de login e perfil
- **Contas**: Contas bancárias e saldos
- **Lançamentos**: Receitas e despesas

### Arquitetura v2.0
```
📱 Android App ←→ 🖥️ Java Server ←→ 💻 Desktop Client
    (Principal)      (Hub Central)      (Config + UI)
```

## 🆕 Funcionalidades Implementadas v2.0

### Cliente Desktop:
- ✅ **Interface de Configurações** - Tela dedicada para configurar rede
- ✅ **Teste de Conexão** - Verificação automática da conectividade
- ✅ **UI Moderna** - Ícones, efeitos hover, componentes card
- ✅ **Configurações Persistentes** - Arquivo `finanza-config.properties`
- ✅ **Status em Tempo Real** - Indicadores visuais do status da conexão

### Sistema:
- ✅ **Gerenciamento de Configurações** - SettingsManager
- ✅ **Gerenciamento de Rede** - NetworkManager com operações assíncronas
- ✅ **UI Helper** - Componentes modernos reutilizáveis

## 🔮 Próximas Implementações

O sistema está preparado para:
- Persistência de dados no servidor (banco de dados)
- Sincronização bidirecional completa
- Mais funcionalidades no cliente desktop
- Interface de configuração no app Android (se necessário)

---

*Finanza v2.0 - Agora com interface moderna e configuração simplificada!*