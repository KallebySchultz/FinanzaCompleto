# Finanza - Sistema de Gestão Financeira Completo

## Visão Geral

O sistema Finanza agora conta com autenticação completa, comunicação com servidor Java e aplicação desktop, atendendo aos requisitos do projeto técnico.

## Funcionalidades Implementadas

### ✅ 1. Sistema de Login e Cadastro
- **LoginActivity**: Tela de login com design seguindo o tema do app
- **RegisterActivity**: Tela de cadastro com validações completas
- **Autenticação persistente**: Uso de SharedPreferences para manter login
- **Logout**: Funcionalidade de sair disponível no menu
- **Tema consistente**: Cores e estilo do app original mantidos

### ✅ 2. Arquitetura de Comunicação com Servidor
- **ServerClient**: Cliente de rede com threading apropriado
- **SyncService**: Serviço de sincronização integrado ao app
- **Comunicação assíncrona**: Operações de rede em threads separadas
- **Interface de callback**: Para tratamento de sucesso/erro

### ✅ 3. Servidor Java
- **FinanzaServer**: Servidor multi-thread para receber dados
- **Threading**: ExecutorService para múltiplas conexões simultâneas
- **Protocolo JSON**: Comunicação estruturada com o aplicativo
- **Scripts de execução**: Para Linux (.sh) e Windows (.bat)

### ✅ 4. Aplicação Desktop
- **FinanzaDesktop**: Interface gráfica Swing com design similar ao mobile
- **Tema consistente**: Cores e layout baseados no app móvel
- **Telas principais**: Login, Dashboard, Contas, Movimentações
- **Navegação**: Sistema de navegação similar ao app mobile

## Estrutura do Projeto

```
Finanza/
├── app/                          # Aplicativo Android
│   ├── src/main/java/com/example/finanza/
│   │   ├── ui/                   # Activities (Login, Register, etc.)
│   │   ├── network/              # Comunicação com servidor
│   │   ├── model/                # Modelos de dados
│   │   └── db/                   # Database e DAOs
│   └── src/main/res/             # Layouts e recursos
├── server-java/                  # Servidor Java
│   ├── src/main/java/com/finanza/server/
│   │   └── FinanzaServer.java   # Servidor principal
│   ├── run-server.sh            # Script Linux
│   └── run-server.bat           # Script Windows
└── desktop-client/              # Cliente Desktop
    ├── src/main/java/com/finanza/desktop/
    │   └── FinanzaDesktop.java  # Aplicação desktop
    ├── run-desktop.sh           # Script Linux
    └── run-desktop.bat          # Script Windows
```

## Como Usar

### 1. Executar o Servidor Java

**Linux/Mac:**
```bash
cd server-java
./run-server.sh
```

**Windows:**
```cmd
cd server-java
run-server.bat
```

O servidor será iniciado na porta 8080 e ficará aguardando conexões.

### 2. Executar a Aplicação Desktop

**Linux/Mac:**
```bash
cd desktop-client
./run-desktop.sh
```

**Windows:**
```cmd
cd desktop-client
run-desktop.bat
```

### 3. Usar o Aplicativo Android

1. **Login/Cadastro**: Primeira tela permite criar conta ou fazer login
2. **Dashboard**: Visão geral dos saldos e transações recentes
3. **Menu**: Acesso às opções de sincronização e logout
4. **Sincronização**: No menu, escolha "Sincronizar com Servidor"

## Principais Alterações Técnicas

### Database
- **Versão atualizada**: Database v2 com novos campos para autenticação
- **Migração**: fallbackToDestructiveMigration para mudanças no schema
- **Novos campos**: senha, dataCriacao no modelo Usuario

### Autenticação
- **Launcher**: LoginActivity agora é a tela inicial
- **Sessão**: SharedPreferences para manter login persistente
- **Validação**: Todas as activities verificam autenticação

### Networking
- **Threading**: ExecutorService para operações de rede
- **Callbacks**: Interface assíncrona para sucesso/erro
- **Permissões**: INTERNET e ACCESS_NETWORK_STATE adicionadas

## Características Técnicas

### Aplicativo Android
- **Tema**: Mantém cores originais (#1B2A57, #4A7CF5, etc.)
- **Threading**: Operações de rede em background
- **Persistência**: SQLite local + sincronização remota
- **Autenticação**: Sistema completo de login/registro

### Servidor Java
- **Arquitetura**: Multi-thread com pool de conexões
- **Protocolo**: JSON para estruturação de dados
- **Escalabilidade**: Suporta múltiplos clientes simultâneos
- **Logging**: Console detalhado para debug

### Desktop Client
- **Interface**: Java Swing com Look&Feel nativo
- **Design**: Cores e layout consistentes com mobile
- **Funcionalidades**: Dashboard, contas, movimentações
- **Navegação**: Sistema de telas similar ao mobile

## Observações Importantes

1. **Gradle/SDK/AGP**: Não foram alterados conforme solicitado
2. **Compatibilidade**: Código compatível com configurações existentes
3. **Modularidade**: Cada componente pode ser executado independentemente
4. **Extensibilidade**: Estrutura preparada para futuras implementações

## Próximos Passos Sugeridos

1. **Implementar persistência no servidor**: Banco de dados para armazenar dados sincronizados
2. **Melhorar protocolo de comunicação**: Autenticação, criptografia
3. **Adicionar funcionalidades ao desktop**: Criação/edição de transações
4. **Implementar sincronização bidirecional**: Servidor → App e App → Servidor
5. **Adicionar configurações de servidor**: IP/porta configuráveis no app

O sistema está funcional e atende aos requisitos estabelecidos, proporcionando uma base sólida para o projeto técnico.