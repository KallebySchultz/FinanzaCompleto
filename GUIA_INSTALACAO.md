# Guia de Instalação e Execução - Finanza

## Pré-requisitos

- **Java JDK 8 ou superior** (para servidor e desktop)
- **Android Studio** (para aplicativo móvel)
- **Dispositivo Android ou Emulador** (para teste do app)

## Passo a Passo

### 1. Preparar o Ambiente

1. Clone o repositório
2. Certifique-se de que o Java está instalado:
   ```bash
   java -version
   javac -version
   ```

### 2. Executar o Servidor (PRIMEIRO)

**Windows:**
1. Abra o Prompt de Comando
2. Navegue até `server-java`
3. Execute: `run-server.bat`

**Linux/Mac:**
1. Abra o Terminal
2. Navegue até `server-java`
3. Execute: `chmod +x run-server.sh && ./run-server.sh`

**Você deve ver:**
```
=== Servidor Finanza ===
Servidor iniciado na porta 8080
Aguardando conexões...
```

### 3. Testar o Cliente Desktop

**Windows:**
1. Novo Prompt de Comando
2. Navegue até `desktop-client`
3. Execute: `run-desktop.bat`

**Linux/Mac:**
1. Novo Terminal
2. Navegue até `desktop-client`
3. Execute: `chmod +x run-desktop.sh && ./run-desktop.sh`

### 4. Compilar e Instalar o App Android

1. Abra o projeto no Android Studio
2. Aguarde a sincronização do Gradle
3. Execute o app em dispositivo/emulador

### 5. Testar o Sistema Completo

1. **App Android**: 
   - Crie uma conta na tela de registro
   - Navegue pelo sistema
   - Vá ao Menu → "Sincronizar com Servidor"

2. **Desktop Client**:
   - Clique em "Entrar" na tela de login
   - Navegue pelas telas (Dashboard, Contas, Movimentações)

3. **Servidor**:
   - Observe os logs no console do servidor
   - Verá as mensagens de conexão dos clientes

## Solução de Problemas

### Erro "Address already in use"
- Feche o servidor anterior (Ctrl+C)
- Aguarde alguns segundos e tente novamente

### Erro de compilação Java
- Verifique se o JDK está instalado
- Verifique se está na pasta correta

### App não compila no Android Studio
- O projeto usa AGP 8.12.2 - talvez precise ajustar para versão disponível
- Sync Project with Gradle Files
- Clean Project → Rebuild Project

### Conexão rejeitada no app
- Certifique-se de que o servidor está rodando
- Se usando emulador, use IP 10.0.2.2 em vez de localhost
- Para dispositivo físico, use IP da máquina na rede local

## Dicas

1. **Ordem de execução**: Sempre inicie o servidor primeiro
2. **Logs úteis**: Observe o console do servidor para debug
3. **Teste gradual**: Teste servidor → desktop → mobile
4. **Rede local**: Para teste em dispositivo real, ajuste IP no código

## Estrutura dos Dados

O sistema sincroniza:
- **Usuários**: Informações de login e perfil
- **Contas**: Contas bancárias e saldos
- **Lançamentos**: Receitas e despesas

## Próximas Implementações

O sistema está preparado para:
- Persistência de dados no servidor
- Sincronização bidirecional completa
- Configuração de servidor via interface
- Mais funcionalidades no cliente desktop