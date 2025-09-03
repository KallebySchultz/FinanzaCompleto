# 🏦 Finanza - Desktop Version

Esta pasta contém a versão desktop completa do sistema Finanza, incluindo o cliente desktop e o servidor de sincronização.

## 📁 Estrutura

```
Desktop Version/
├── Desktop Client/     # Cliente desktop Java (Swing)
├── Server/            # Servidor de sincronização
├── Icons/             # Ícones do projeto (do app móvel)
└── README.md          # Este arquivo
```

## 🚀 Como Executar

### 1. Iniciar o Servidor (PRIMEIRO)
```bash
cd Server
mvn compile exec:java -Dexec.mainClass="com.finanza.server.FinanzaServer"
```

Ou use os scripts:
- **Windows:** `Server/run-server.bat`
- **Linux/Mac:** `Server/run-server.sh`

### 2. Executar o Cliente Desktop
```bash
cd "Desktop Client"
mvn compile exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"
```

Ou use os scripts:
- **Windows:** `Desktop Client/run-desktop.bat`
- **Linux/Mac:** `Desktop Client/run-desktop.sh`

## ⚙️ Configuração de Rede

### Cliente Desktop
1. Abra o cliente desktop
2. Clique em **⚙️ Configurações** na barra de navegação
3. Configure:
   - **IP do Servidor:** IP do computador onde o servidor está rodando
   - **Porta:** 8080 (padrão)
4. Clique em **🔗 Testar Conexão**
5. Clique em **✅ Salvar**

### App Mobile
Use a nova tela de configurações no aplicativo Android:
1. No menu principal, clique em **⚙️ Configurações**
2. Configure o IP do servidor (mesmo IP usado no desktop)
3. Teste a conexão
4. Salve as configurações

## 🔧 Requisitos

- **Java JDK 8 ou superior**
- **Maven** (para compilação)
- **Conexão de rede** (WiFi local ou internet)

## 🌐 Conectividade

Para que o app móvel conecte-se ao servidor:

1. **Mesma rede WiFi:** PC e celular na mesma rede
2. **IP do PC:** Use `ipconfig` (Windows) ou `ifconfig` (Linux/Mac)
3. **Firewall:** Permita conexões na porta 8080
4. **Servidor rodando:** Sempre iniciar o servidor primeiro

### Exemplo de IPs:
- **Rede local:** 192.168.1.100
- **Emulador Android:** 10.0.2.2
- **Localhost (apenas PC):** localhost

## ✨ Funcionalidades

### Cliente Desktop
- 🏠 **Dashboard** - Visão geral financeira
- 💳 **Contas** - Gerenciamento de contas bancárias
- 📊 **Movimentações** - Histórico de transações
- 👤 **Perfil** - Informações do usuário
- ⚙️ **Configurações** - Configuração de rede moderna
- 🎨 **Interface moderna** - Usando ícones do projeto móvel

### Servidor
- 🔄 **Sincronização automática** - Dados entre mobile e desktop
- 💾 **Persistência** - Armazenamento em arquivos JSON
- 🌐 **Multi-cliente** - Suporte a múltiplas conexões
- 📈 **Logs detalhados** - Monitoramento em tempo real

## 🛠️ Desenvolvimento

### Arquitetura MVC
- **Model:** Classes de dados (Usuario, Conta, Lancamento)
- **View:** Interface Swing com componentes modernos
- **Controller:** Managers (NetworkManager, SettingsManager)

### Tecnologias Utilizadas
- **Java Swing** - Interface gráfica
- **Maven** - Gerenciamento de dependências
- **Socket TCP** - Comunicação cliente-servidor
- **JSON** - Formato de dados
- **Threading** - Operações assíncronas

## 🔧 Solução de Problemas

### ❌ Servidor - "Address already in use"
```bash
# Encontrar processo usando a porta 8080
netstat -an | grep 8080
# Matar processo (substitua PID)
kill -9 <PID>
```

### ❌ Cliente não conecta
- Verificar se servidor está rodando
- Confirmar IP e porta nas configurações
- Testar conexão usando botão "Testar"
- Verificar firewall

### ❌ App mobile não conecta
- Usar cliente desktop para validar servidor
- Configurar IP correto no app
- Para emulador: usar 10.0.2.2
- Para dispositivo físico: usar IP da máquina

## 📊 Dados Sincronizados

O sistema sincroniza:
- **👥 Usuários** - Informações de login e perfil
- **💳 Contas** - Contas bancárias e saldos
- **📊 Transações** - Receitas e despesas

### Fluxo de Dados
```
📱 App Android ←→ 🖥️ Servidor Java ←→ 💻 Cliente Desktop
    (Principal)      (Hub Central)       (Config + UI)
```

## 🎨 Personalização

Os ícones utilizados estão na pasta `Icons/` e podem ser personalizados. O cliente desktop carrega automaticamente os ícones do projeto móvel para manter consistência visual.

## 📝 Logs

- **Servidor:** Logs no console durante execução
- **Cliente:** Status na tela de configurações
- **Dados:** Salvos em `Server/finanza-data/`

---

**💡 Dica:** Sempre inicie o servidor antes do cliente desktop e configure as redes corretamente para sincronização perfeita entre dispositivos!