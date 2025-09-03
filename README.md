# 🏦 Finanza - Sistema de Gestão Financeira

## 📋 Visão Geral

O Finanza é um sistema completo de gestão financeira pessoal que inclui:

- **📱 Aplicativo Android** - Interface principal mobile
- **💻 Cliente Desktop** - Interface desktop moderna com funcionalidades avançadas
- **🖥️ Servidor Java** - Backend para sincronização de dados

## ✨ Novas Funcionalidades (v2.0)

### Cliente Desktop Modernizado
- ⚙️ **Configurações de Rede** - Configure IP e porta do servidor
- 🎨 **Interface Moderna** - Design similar ao aplicativo móvel
- 🔗 **Teste de Conexão** - Verificação em tempo real da conectividade
- 💾 **Configurações Persistentes** - Salva automaticamente suas preferências
- 🌐 **Conectividade Flexível** - Conecte-se via WiFi local ou internet

### Melhorias na Interface
- 🎯 **Ícones Modernos** - Interface visual aprimorada
- 🖱️ **Efeitos Hover** - Interação mais fluida
- 📦 **Componentes Card** - Elementos com sombras e bordas arredondadas
- 🎨 **Tipografia Melhorada** - Fonte Segoe UI para melhor legibilidade

## 🚀 Instalação Rápida

### Pré-requisitos
- **Java 8+** (para servidor e desktop)
- **Android Studio** (para app móvel)
- **Maven** (para compilação do desktop)

### 1. Servidor Java
```bash
cd server-java
javac -d . src/main/java/com/finanza/server/FinanzaServer.java
java com.finanza.server.FinanzaServer
```

### 2. Cliente Desktop
```bash
cd desktop-client
mvn compile
mvn exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"
```

### 3. Aplicativo Android
1. Abra o projeto no Android Studio
2. Conecte seu dispositivo ou use o emulador
3. Execute o projeto (Run ▶️)

## 📡 Configuração de Rede

### Descobrir IP do Computador

**Windows:**
```cmd
ipconfig
```
Procure por "Endereço IPv4" (ex: 192.168.1.100)

**Linux/Mac:**
```bash
ip addr show
# ou
ifconfig
```

### Configurar Cliente Desktop
1. Abra o cliente desktop
2. Clique em **⚙️ Configurações** na barra de navegação
3. Configure:
   - **IP do Servidor:** IP do computador onde o servidor está rodando
   - **Porta:** 8080 (padrão) ou porta personalizada
4. Clique em **🔗 Testar Conexão**
5. Clique em **✅ Salvar**

### Configurar App Android
> **Nota:** Conforme solicitado, não modificamos as configurações do Android. O app continuará usando localhost por padrão.

## 🛠️ Configurações Avançadas

### Mudar Porta do Servidor
Edite `FinanzaServer.java`:
```java
private static final int PORT = 8080; // Altere para sua porta
```

### Configurações do Desktop
O arquivo `finanza-config.properties` é criado automaticamente e contém:
```properties
server.host=192.168.1.100
server.port=8080
connection.timeout=5000
auto.connect=true
theme=light
```

## 🔧 Solução de Problemas

### ❌ Servidor não inicia
1. Verifique se a porta 8080 está livre:
   ```bash
   netstat -an | grep 8080
   ```
2. Se ocupada, mude a porta no código ou finalize o processo

### ❌ Desktop não conecta
1. **Verifique se estão na mesma rede WiFi**
2. **Teste conectividade:**
   ```bash
   ping [IP_DO_SERVIDOR]
   ```
3. **Configure firewall** para permitir porta 8080
4. **Verifique IP** - pode ter mudado se o roteador reiniciou

### ❌ App Android não conecta
1. Certifique-se que servidor e app estão na mesma rede
2. Use o cliente desktop para configurar e testar conexão primeiro
3. Verifique firewall do computador

## 🖥️ Interface do Cliente Desktop

### Telas Disponíveis
- **🏠 Dashboard** - Visão geral financeira
- **💳 Contas** - Gerenciamento de contas
- **📊 Movimentações** - Histórico de transações
- **👤 Perfil** - Informações do usuário
- **⚙️ Configurações** - Configurações de rede
- **🚪 Sair** - Logout

### Funcionalidades da Tela de Configurações
- **Configuração de IP/Porta** - Interface amigável
- **Teste de Conexão** - Botão para verificar conectividade
- **Status em Tempo Real** - Indicador visual do status da conexão
- **Informações de Ajuda** - Dicas sobre configuração de rede
- **Salvamento Automático** - Configurações persistem entre sessões

## 📱 Sincronização de Dados

### Como Funciona
1. **Servidor Java** atua como hub central
2. **App Android** envia dados para o servidor
3. **Cliente Desktop** sincroniza com o servidor
4. **Dados compartilhados** entre todas as plataformas

### Comandos de Sincronização
- `sync_user` - Sincroniza dados do usuário
- `sync_accounts` - Sincroniza contas
- `sync_transactions` - Sincroniza transações

## 🎨 Personalização

### Cores do Tema
```java
PRIMARY_DARK_BLUE = #1B2A57
ACCENT_BLUE = #4A7CF5
POSITIVE_GREEN = #21C87A
NEGATIVE_RED = #E53935
```

### Ícones Unicode Utilizados
- 🏠 Dashboard
- 💳 Contas  
- 📊 Movimentações
- 👤 Perfil
- ⚙️ Configurações
- 🚪 Logout

## 📊 Arquitetura do Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   📱 Android     │    │  🖥️ Servidor     │    │  💻 Desktop     │
│     Client      │◄──►│     Java        │◄──►│    Client       │
│                 │    │                 │    │                 │
│ • UI Principal  │    │ • Sync Hub      │    │ • Config UI     │
│ • Local DB      │    │ • Threading     │    │ • Settings      │
│ • Network       │    │ • JSON API      │    │ • Modern UI     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🆘 Suporte

### Logs e Debug
- **Servidor:** Logs no console
- **Desktop:** Mensagens de erro em popups
- **Android:** Logcat do Android Studio

### Contato
Para suporte adicional, consulte a documentação específica de cada componente ou abra uma issue no repositório.

---

*Finanza v2.0 - Sistema moderno de gestão financeira com interface aprimorada e configuração flexível de rede.*