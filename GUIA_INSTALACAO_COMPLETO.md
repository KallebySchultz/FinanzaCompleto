# Guia de Instalação e Uso - Finanza

## Visão Geral

O Finanza é um sistema completo de gestão financeira que inclui:
- **Aplicativo Android** - Interface principal do usuário
- **Servidor Java** - Sincronização de dados
- **Cliente Desktop** - Interface desktop similar ao mobile

## Pré-requisitos

### Para o Servidor Java
- Java 8 ou superior
- NetBeans IDE (recomendado)

### Para o Cliente Desktop  
- Java 8 ou superior
- NetBeans IDE (recomendado)

### Para o Aplicativo Android
- Android Studio
- Dispositivo Android com API 24+ (Android 7.0+)

## Configuração de Rede

### 1. Preparação do Ambiente
Para que o aplicativo móvel se conecte ao servidor no computador via WiFi:

1. **Conecte o computador e o dispositivo móvel na mesma rede WiFi**
2. **Descubra o IP do computador:**

**No Windows:**
```cmd
ipconfig
```
Procure por "Endereço IPv4" na seção da sua rede WiFi (ex: 192.168.1.100)

**No Linux/Mac:**
```bash
ip addr show
# ou
ifconfig
```
Procure pelo IP da interface da rede WiFi (ex: 192.168.1.100)

3. **Configure o firewall para permitir conexões na porta 8080**

**Windows:**
- Vá em Configurações > Rede e Internet > Firewall do Windows Defender
- Clique em "Permitir um aplicativo através do firewall"
- Adicione Java/javaw.exe à lista de exceções

**Linux:**
```bash
sudo ufw allow 8080
```

**Mac:**
- Vá em Preferências do Sistema > Segurança e Privacidade > Firewall
- Adicione exceção para Java na porta 8080

## Instalação e Execução

### 1. Executar o Servidor Java

#### Usando NetBeans:
1. Abra o NetBeans IDE
2. Importe o projeto do diretório `server-java`
3. Clique com botão direito no projeto > "Run"
4. O servidor iniciará na porta 8080

#### Usando linha de comando:
**Linux/Mac:**
```bash
cd server-java
chmod +x run-server.sh
./run-server.sh
```

**Windows:**
```cmd
cd server-java
run-server.bat
```

**Saída esperada:**
```
Servidor Finanza iniciado na porta 8080
Aguardando conexões...
```

### 2. Executar o Cliente Desktop

#### Usando NetBeans:
1. Abra o NetBeans IDE
2. Importe o projeto do diretório `desktop-client`
3. Clique com botão direito no projeto > "Run"
4. A interface desktop será exibida

#### Usando linha de comando:
**Linux/Mac:**
```bash
cd desktop-client
chmod +x run-desktop.sh
./run-desktop.sh
```

**Windows:**
```cmd
cd desktop-client
run-desktop.bat
```

### 3. Instalar e Usar o Aplicativo Android

#### Instalação:
1. Abra o Android Studio
2. Importe o projeto do diretório raiz (que contém a pasta `app`)
3. Conecte seu dispositivo Android ou inicie um emulador
4. Clique em "Run" no Android Studio

#### Configuração de Rede no App:
1. **Abra o aplicativo no dispositivo**
2. **Vá para Menu > Configurações** (se disponível)
3. **Configure o IP do servidor:**
   - Use o IP descoberto anteriormente (ex: 192.168.1.100)
   - Porta: 8080
   - Exemplo: `192.168.1.100:8080`

## Uso do Sistema

### Primeiro Acesso

1. **No aplicativo mobile:**
   - Toque em "Criar conta" na tela de login
   - Preencha: Nome, Email, Senha
   - Toque em "Cadastrar"

2. **Login será feito automaticamente**

3. **A sincronização ocorre automaticamente** quando há conexão com a rede

### Funcionalidades Principais

#### No Aplicativo Mobile:
- **Dashboard:** Visão geral dos saldos e transações
- **Contas:** Criar e gerenciar contas bancárias
- **Movimentações:** Registrar receitas e despesas
- **Perfil:** Editar dados pessoais e excluir conta
- **Menu:** Acesso a todas as funcionalidades

#### No Cliente Desktop:
- **Interface similar ao mobile**
- **Dashboard:** Resumo financeiro
- **Contas:** Visualização de contas
- **Movimentações:** Histórico de transações
- **Perfil:** Informações do usuário

### Sincronização

#### Automática:
- **Ocorre automaticamente a cada 30 segundos** quando o dispositivo está online
- **Sincroniza:** Usuários, Contas, Lançamentos

#### Manual:
- **Botão "Sincronizar"** no menu aparece apenas quando offline
- **Use quando:** Perdeu conexão temporariamente

## Solução de Problemas

### Servidor não inicia:
1. Verifique se a porta 8080 não está em uso:
   ```bash
   netstat -an | grep 8080
   ```
2. Se estiver em uso, finalize o processo ou mude a porta no código

### App não conecta ao servidor:
1. **Verifique se estão na mesma rede WiFi**
2. **Teste a conectividade:**
   ```bash
   ping [IP_DO_COMPUTADOR]
   ```
3. **Verifique o firewall** - deve permitir porta 8080
4. **Verifique o IP** - pode ter mudado se o roteador reiniciou

### Dados não sincronizam:
1. **Verifique conexão de rede** no dispositivo
2. **Reinicie o servidor** se necessário
3. **Use sincronização manual** no menu quando voltar online

### Desktop não abre:
1. **Verifique se Java está instalado:**
   ```bash
   java -version
   ```
2. **Se usando NetBeans:** Verifique se o projeto foi importado corretamente
3. **Verifique logs** no console para erros específicos

## Configurações Avançadas

### Mudar porta do servidor:
Edite o arquivo `FinanzaServer.java` e altere:
```java
private static final int PORT = 8080; // Mude para outra porta
```

### Configurar IP específico:
No app, configure manualmente o endereço do servidor nas preferências (se implementado).

## Dicas de Uso

1. **Mantenha o servidor sempre rodando** para sincronização contínua
2. **Use o cliente desktop** para visualizações mais detalhadas
3. **O app mobile é a interface principal** - use-o para a maioria das operações
4. **Backup regular** - exporte dados via menu do app
5. **Perfil seguro** - use senhas fortes e atualize regularmente

## Suporte

Para problemas técnicos:
1. Verifique os logs do servidor no console
2. Verifique os logs do Android via Android Studio
3. Teste conectividade de rede
4. Reinicie todos os componentes se necessário

---

**Finanza v1.0** - Sistema de Gestão Financeira Completo