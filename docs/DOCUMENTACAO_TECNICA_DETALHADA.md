# 📚 DOCUMENTAÇÃO TÉCNICA DETALHADA - Sistema Finanza

> Guia completo e detalhado do sistema de gestão financeira com versões Desktop (Java Swing) e Mobile (Android)

---

## 📋 Índice

1. [Criptografia (Senha)](#1-criptografia-senha)
2. [Login e Cadastro](#2-login-e-cadastro)
3. [Listas (Contas, Categorias, Movimentações)](#3-listas-contas-categorias-movimentações)
4. [Conexão com Servidor](#4-conexão-com-servidor)
5. [Conectores e Acesso ao Banco de Dados](#5-conectores-e-acesso-ao-banco-de-dados)

---

## 1. Criptografia (Senha)

### 📁 Localização do Código de Criptografia

**Arquivo Principal:** `DESKTOP VERSION/ServidorFinanza/src/util/SecurityUtil.java`

### 🔐 Como Funciona a Criptografia

O sistema utiliza o algoritmo **SHA-256** para fazer o hash das senhas. A criptografia é feita **no servidor**, não no cliente.

#### Classe SecurityUtil - Código Completo com Explicação

```java
// Arquivo: DESKTOP VERSION/ServidorFinanza/src/util/SecurityUtil.java
// Linhas 1-56

package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Classe utilitária para operações de segurança e criptografia
 */
public class SecurityUtil {
    
    /**
     * MÉTODO PRINCIPAL DE CRIPTOGRAFIA - LINHA 18-26
     * Gera hash SHA-256 de uma senha
     * 
     * @param senha senha original (texto plano)
     * @return hash da senha (string Base64)
     */
    public static String hashSenha(String senha) {
        try {
            // LINHA 20: Obtém instância do algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // LINHA 21: Converte senha para bytes UTF-8 e gera hash
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            
            // LINHA 22: Converte bytes do hash para string Base64
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
    
    /**
     * MÉTODO DE VERIFICAÇÃO - LINHA 34-37
     * Verifica se uma senha corresponde ao hash
     * 
     * @param senha senha a verificar (texto plano)
     * @param hash hash armazenado no banco
     * @return true se a senha estiver correta
     */
    public static boolean verificarSenha(String senha, String hash) {
        // LINHA 35: Gera hash da senha informada
        String senhaHash = hashSenha(senha);
        // LINHA 36: Compara com hash armazenado
        return senhaHash.equals(hash);
    }
}
```

### 🔄 Fluxo da Criptografia

#### Desktop (Cliente → Servidor)

```
1. CLIENTE DESKTOP (LoginView.java)
   ├── Usuário digita senha em texto plano
   ├── Linha 133-136: realizarLogin() captura email e senha
   └── Envia para AuthController

2. AUTHCONTROLLER (AuthController.java)
   ├── Linha 44-101: método login()
   ├── Linha 50: Monta comando "LOGIN|email|senha|admin"
   ├── Linha 51: networkClient.sendCommand(comando)
   └── Senha enviada em TEXTO PLANO pela rede ⚠️

3. SERVIDOR (ClientHandler.java)
   ├── Linha 477-529: processarLogin()
   ├── Linha 507: usuarioDAO.autenticar(email, senha)
   └── Delega para UsuarioDAO

4. USUARIODAO (UsuarioDAO.java)
   ├── Linha 106-114: método autenticar()
   ├── Linha 107: Busca usuário por email
   ├── Linha 109: SecurityUtil.verificarSenha(senha, usuario.getSenhaHash())
   └── Verifica se hash confere

5. SECURITYUTIL (SecurityUtil.java)
   ├── Linha 34-37: verificarSenha()
   ├── Gera hash da senha informada
   └── Compara com hash armazenado no banco
```

#### Mobile (Android)

```
1. LOGINACTIVITY (LoginActivity.java)
   ├── Linha 133-182: realizarLogin()
   ├── Linha 159: authManager.login(email, senha, callback)
   └── Envia para AuthManager

2. AUTHMANAGER (AuthManager.java)
   ├── Linha 112-219: método login()
   ├── Linha 123: serverClient.login(email, senha, callback)
   └── Envia para ServerClient

3. SERVERCLIENT (ServerClient.java)
   ├── Linha 470-500: método login()
   ├── Linha 472: Monta comando "LOGIN|email|senha|mobile"
   ├── Linha 475: enviarComando(comando, callback)
   └── Senha enviada em TEXTO PLANO pela rede ⚠️

4. SERVIDOR (mesmo processo do desktop)
   └── ClientHandler → UsuarioDAO → SecurityUtil
```

### ⚠️ Observações Importantes sobre Segurança

| Aspecto | Status | Localização |
|---------|--------|-------------|
| Algoritmo | SHA-256 | SecurityUtil.java, linha 20 |
| Onde é criptografado | Servidor | UsuarioDAO.java, linha 152 (ao inserir) |
| Transmissão | Texto plano | NetworkClient/ServerClient |
| Salt | ⚠️ **NÃO UTILIZADO** | - |

### ⚠️ ALERTAS CRÍTICOS DE SEGURANÇA

> **ATENÇÃO:** O sistema atual possui vulnerabilidades de segurança que devem ser corrigidas antes de uso em produção com dados reais financeiros.

#### 🔴 Problemas Críticos Identificados:

1. **Falta de Salt nas Senhas**
   - O hash SHA-256 é gerado **sem salt**, tornando senhas vulneráveis a ataques de rainbow tables
   - **Recomendação:** Migrar para **BCrypt** ou **PBKDF2** que incluem salt automático

2. **Transmissão em Texto Plano**
   - As senhas são enviadas pela rede **sem criptografia de transporte**
   - **Recomendação CRÍTICA:** Implementar **TLS/SSL** obrigatório para todas as conexões

3. **Algoritmo SHA-256 não recomendado para senhas**
   - SHA-256 é um algoritmo de hash rápido, facilitando ataques de força bruta
   - **Recomendação:** Usar algoritmos específicos para senhas como BCrypt, Argon2 ou scrypt

#### 📋 Plano de Ação Sugerido:

```java
// Exemplo de migração para BCrypt (servidor)
// Substituir em SecurityUtil.java:
import org.mindrot.jbcrypt.BCrypt;

public static String hashSenha(String senha) {
    return BCrypt.hashpw(senha, BCrypt.gensalt(12));
}

public static boolean verificarSenha(String senha, String hash) {
    return BCrypt.checkpw(senha, hash);
}
```

---

## 2. Login e Cadastro

### 📱 Fluxo Completo - MOBILE (Android)

#### 2.1 Tela de Login (LoginActivity.java)

**Arquivo:** `app/src/main/java/com/example/finanza/ui/LoginActivity.java`

```
FLUXO DE LOGIN MOBILE:

╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 1: INTERFACE (LoginActivity.java)                                    ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 70-117: onCreate()                                                   ║
║  - Linha 80: authManager = AuthManager.getInstance(this)                    ║
║  - Linha 83-89: Verifica se já está logado (sessão ativa)                   ║
║  - Linha 101: btnLogin.setOnClickListener(v -> realizarLogin())             ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 2: VALIDAÇÃO (LoginActivity.java)                                    ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 133-182: realizarLogin()                                             ║
║  - Linha 135-136: Captura email e senha dos campos                          ║
║  - Linha 144-152: Validação de campos vazios                                ║
║  - Linha 155-156: Desabilita botão e mostra "Entrando..."                   ║
║  - Linha 159: authManager.login(email, senha, callback)                     ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 3: GERENCIAMENTO DE AUTH (AuthManager.java)                          ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 112-219: login(email, senha, callback)                               ║
║  - Linha 114: Verifica usuário localmente primeiro                          ║
║  - Linha 117-218: Tenta conectar ao servidor                                ║
║    ├── Se conectado: linha 123 - serverClient.login()                       ║
║    └── Se offline: linha 194-199 - usa autenticação local                   ║
║  - Linha 154: salvarSessao(usuario) - salva ID e email em SharedPreferences ║
║  - Linha 165: SyncService.sincronizarTudo() - sincroniza dados              ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 4: COMUNICAÇÃO COM SERVIDOR (ServerClient.java)                      ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 470-500: login(email, senha, callback)                               ║
║  - Linha 472: Monta comando "LOGIN|email|senha|mobile"                      ║
║  - Linha 475-499: enviarComando() → AsyncTask em background                 ║
║  - Linha 380-385: Envia via output.println() e aguarda input.readLine()     ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 5: PROCESSAMENTO NO SERVIDOR (ClientHandler.java)                    ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 477-529: processarLogin(partes)                                      ║
║  - Linha 483-485: Extrai email, senha e tipo de cliente                     ║
║  - Linha 507: usuarioDAO.autenticar(email, senha)                           ║
║  - Linha 509-519: Verifica tipo de cliente vs tipo de usuário               ║
║  - Linha 521-525: Monta resposta "OK|id;nome;email;tipo"                    ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 6: VERIFICAÇÃO NO BANCO (UsuarioDAO.java)                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 106-114: autenticar(email, senha)                                    ║
║  - Linha 107: Usuario usuario = buscarPorEmail(email)                       ║
║  - Linha 109: SecurityUtil.verificarSenha(senha, usuario.getSenhaHash())    ║
║  - Retorna: Usuario autenticado ou null                                     ║
╚════════════════════════════════════════════════════════════════════════════╝
```

#### 2.2 Tela de Cadastro Mobile (RegisterActivity.java)

**Arquivo:** `app/src/main/java/com/example/finanza/ui/RegisterActivity.java`

```
FLUXO DE CADASTRO MOBILE:

╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 1: INTERFACE → VALIDAÇÃO                                             ║
╠════════════════════════════════════════════════════════════════════════════╣
║  RegisterActivity:                                                          ║
║  - Captura: nome, email, senha, confirmar senha                             ║
║  - Valida: campos vazios, formato email, senhas iguais                      ║
║  - Chama: authManager.registrar(nome, email, senha, callback)               ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 2: AUTHMANAGER (AuthManager.java)                                    ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 235-285: registrar(nome, email, senha, callback)                     ║
║  - Linha 237-240: Verifica se usuário já existe localmente                  ║
║  - Linha 242-273: Se conectado, registra no servidor primeiro               ║
║    └── Linha 244: serverClient.registrar(nome, email, senha, callback)      ║
║  - Linha 274-284: Se offline, cria apenas localmente                        ║
║  - Linha 303-322: criarUsuarioLocal() - insere no Room Database             ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 3: SERVERCLIENT (ServerClient.java)                                  ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 547-576: registrar(nome, email, senha, callback)                     ║
║  - Linha 549: Monta "REGISTER|nome|email|senha|mobile"                      ║
║  - Linha 552: enviarComando() → servidor                                    ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 4: SERVIDOR (ClientHandler.java)                                     ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 534-592: processarRegistro(partes)                                   ║
║  - Linha 539-541: Extrai nome, email, senha                                 ║
║  - Linha 544-554: Validações (campos vazios, formato email, senha mínima)   ║
║  - Linha 574: Verifica se email já existe no banco                          ║
║  - Linha 579: SecurityUtil.hashSenha(senha) - CRIPTOGRAFA AQUI!             ║
║  - Linha 582: usuarioDAO.inserir(novoUsuario) - salva no MySQL              ║
║  - Linha 584-588: Monta resposta "OK|id;nome;email;tipo"                    ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 5: USUARIODAO - INSERÇÃO NO BANCO (UsuarioDAO.java)                  ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 21-48: inserir(usuario)                                              ║
║  - Linha 22: SQL "INSERT INTO usuario (nome, email, senha_hash, tipo)..."   ║
║  - Linha 27-30: Define parâmetros (nome, email, senha já hashada, tipo)     ║
║  - Linha 32: stmt.executeUpdate()                                           ║
║  - Linha 35-38: Recupera ID gerado e seta no objeto                         ║
╚════════════════════════════════════════════════════════════════════════════╝
```

### 🖥️ Fluxo Completo - DESKTOP (Java Swing)

#### 2.3 Tela de Login Desktop (LoginView.java)

**Arquivo:** `DESKTOP VERSION/ClienteFinanza/src/view/LoginView.java`

```
FLUXO DE LOGIN DESKTOP:

╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 1: INTERFACE SWING                                                   ║
╠════════════════════════════════════════════════════════════════════════════╣
║  LoginView.java:                                                            ║
║  - Campos: JTextField para email, JPasswordField para senha                 ║
║  - Botão login executa em SwingWorker (thread background)                   ║
║  - Chama: authController.login(email, senha)                                ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 2: AUTHCONTROLLER (AuthController.java)                              ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 44-101: login(email, senha)                                          ║
║  - Linha 45-47: Verifica conexão com servidor                               ║
║  - Linha 50: Monta comando "LOGIN|email|senha|admin"                        ║
║  - Linha 51: networkClient.sendCommand(comando)                             ║
║  - Linha 53-100: Processa resposta (OK, INVALID_CREDENTIALS, etc)           ║
║  - Linha 63-68: Parse dos dados "id;nome;email;tipo"                        ║
║  - Retorna: LoginResult com usuário ou erro                                 ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 3: NETWORKCLIENT (NetworkClient.java)                                ║
╠════════════════════════════════════════════════════════════════════════════╣
║  Linha 39-55: sendCommand(command)                                          ║
║  - Linha 40-42: Verifica se está conectado                                  ║
║  - Linha 45: output.println(command) - envia comando                        ║
║  - Linha 46: input.readLine() - aguarda resposta (BLOQUEANTE)               ║
║  - Retorna: String com resposta do servidor                                 ║
╚════════════════════════════════════════════════════════════════════════════╝
                                    │
                                    ▼
╔════════════════════════════════════════════════════════════════════════════╗
║  PASSO 4-6: IGUAL AO MOBILE                                                 ║
║  ClientHandler → UsuarioDAO → SecurityUtil → Banco MySQL                    ║
╚════════════════════════════════════════════════════════════════════════════╝
```

#### 2.4 Cadastro Desktop (AuthController.java)

**Arquivo:** `DESKTOP VERSION/ClienteFinanza/src/controller/AuthController.java`

```java
// Linha 106-149: registrar(nome, email, senha)
public LoginResult registrar(String nome, String email, String senha) {
    // Linha 107-109: Verifica conexão
    if (!networkClient.isConnected()) {
        return new LoginResult(false, "Não conectado ao servidor", null);
    }
    
    // Linha 112: Monta comando REGISTER com tipo "admin"
    String comando = CMD_REGISTER + SEPARATOR + nome + SEPARATOR + email + 
                     SEPARATOR + senha + SEPARATOR + "admin";
    
    // Linha 113: Envia ao servidor
    String resposta = networkClient.sendCommand(comando);
    
    // Linha 115-148: Processa resposta
    // ...
}
```

---

## 3. Listas (Contas, Categorias, Movimentações)

### 📱 Como Funciona no MOBILE (Android)

#### 3.1 Arquitetura de Dados - Room Database

O Android usa **Room Database** (SQLite local) para armazenar dados offline.

**Arquivo Principal:** `app/src/main/java/com/example/finanza/db/AppDatabase.java`

```java
// Linha 42-47: Configuração do banco
@Database(
    entities = {Usuario.class, Conta.class, Categoria.class, Lancamento.class},
    version = 6,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    // Linha 56-74: DAOs disponíveis
    public abstract UsuarioDao usuarioDao();    // Linha 56
    public abstract ContaDao contaDao();        // Linha 62
    public abstract CategoriaDao categoriaDao(); // Linha 68
    public abstract LancamentoDao lancamentoDao(); // Linha 74
}
```

#### 3.2 Listagem de Lançamentos no Mobile

**Arquivo:** `app/src/main/java/com/example/finanza/db/LancamentoDao.java`

```java
// CONSULTAS PRINCIPAIS:

// Linha 98-99: Lista TODOS os lançamentos de um usuário (ordenado por data DESC)
@Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC")
List<Lancamento> listarPorUsuario(int usuarioId);

// Linha 107-108: Lista apenas lançamentos ATIVOS (não deletados)
@Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND isDeleted = 0 ORDER BY data DESC")
List<Lancamento> listarAtivosPorUsuario(int usuarioId);

// Linha 117-118: Lista últimos N lançamentos
@Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC LIMIT :limit")
List<Lancamento> listarUltimasPorUsuario(int usuarioId, int limit);

// Linha 127-129: Lista por período (entre datas)
@Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND data >= :inicio AND data <= :fim ORDER BY data DESC")
List<Lancamento> listarPorUsuarioPeriodo(int usuarioId, long inicio, long fim);

// Linha 148-149: Lista por CONTA específica
@Query("SELECT * FROM Lancamento WHERE contaId = :contaId ORDER BY data DESC")
List<Lancamento> listarPorConta(int contaId);

// Linha 166-168: Lista por CATEGORIA
@Query("SELECT * FROM Lancamento WHERE categoriaId = :categoriaId ORDER BY data DESC")
List<Lancamento> listarPorCategoria(int categoriaId);
```

#### 3.3 Cálculos e Somas no Mobile

```java
// Linha 178-179: Soma por tipo (receita ou despesa)
@Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId")
Double somaPorTipo(String tipo, int usuarioId);

// Linha 189-190: Soma por tipo E conta
@Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId AND contaId = :contaId")
Double somaPorTipoConta(String tipo, int usuarioId, int contaId);

// Linha 199-200: Saldo de uma conta
@Query("SELECT SUM(valor) FROM Lancamento WHERE contaId = :contaId AND usuarioId = :usuarioId")
Double saldoPorConta(int contaId, int usuarioId);
```

### 🖥️ Como Funciona no DESKTOP

#### 3.4 Listagem via FinanceController

**Arquivo:** `DESKTOP VERSION/ClienteFinanza/src/controller/FinanceController.java`

```
FLUXO DE LISTAGEM DESKTOP:

╔════════════════════════════════════════════════════════════════════════════╗
║  EXEMPLO: LISTAR CONTAS                                                     ║
╠════════════════════════════════════════════════════════════════════════════╣
║  FinanceController.java - Linha 105-168: listarContas()                     ║
║                                                                             ║
║  1. Verifica conexão (linha 106-108)                                        ║
║  2. Envia comando "LIST_CONTAS" (linha 110)                                 ║
║  3. Recebe resposta do servidor                                             ║
║  4. Faz parse da resposta (linha 113-167)                                   ║
║     - Formato: "OK|id,nome,tipo,saldoInicial,saldoAtual;id2,nome2,..."      ║
║  5. Retorna OperationResult<List<Conta>>                                    ║
╚════════════════════════════════════════════════════════════════════════════╝
```

**Código Detalhado:**

```java
// Linha 105-168: listarContas()
public OperationResult<List<Conta>> listarContas() {
    // Linha 106-108: Verifica conexão
    if (!networkClient.isConnected()) {
        return new OperationResult<>(false, "Não conectado ao servidor", null);
    }
    
    // Linha 110: Envia comando ao servidor
    String resposta = networkClient.sendCommand(CMD_LIST_CONTAS);
    
    // Linha 111: Separa resposta por SEPARATOR (|)
    String[] partes = resposta.split("\\" + SEPARATOR);
    
    // Linha 113: Verifica se status é OK
    if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
        List<Conta> contas = new ArrayList<>();
        
        // Linha 116-117: Verifica se há dados
        if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
            // Linha 118: Separa registros por FIELD_SEPARATOR (;)
            String[] contasData = partes[1].split(FIELD_SEPARATOR);
            
            // Linha 120-161: Para cada registro, faz parse
            for (String contaStr : contasData) {
                // Linha 126: Separa campos por vírgula
                String[] campos = contaStr.split(",");
                // Parse: id, nome, tipo, saldoInicial, saldoAtual
                // ...
            }
        }
        return new OperationResult<>(true, "Contas carregadas", contas);
    }
    // ...
}
```

#### 3.5 Processamento no Servidor (ClientHandler.java)

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java`

```java
// Linha 756-804: processarListContas()
private String processarListContas() {
    // Linha 757-759: Verifica se usuário está logado
    if (usuarioLogado == null) {
        return Protocol.createErrorResponse("Usuário não está logado");
    }
    
    // Linha 768-770: Busca contas no DAO
    List<Conta> contas = contaDAO.listarPorUsuario(usuarioLogado.getId());
    
    // Linha 776-795: Monta resposta com dados formatados
    StringBuilder contasData = new StringBuilder();
    for (int i = 0; i < contas.size(); i++) {
        Conta conta = contas.get(i);
        double saldoAtual = contaDAO.calcularSaldoAtual(conta.getId());
        
        // Linha 790-794: Formato: id,nome,tipo,saldoInicial,saldoAtual
        contasData.append(conta.getId()).append(",")
                  .append(conta.getNome()).append(",")
                  .append(conta.getTipo().getValor()).append(",")
                  .append(saldoInicialStr).append(",")
                  .append(saldoAtualStr);
    }
    
    return Protocol.createSuccessResponse(contasData.toString());
}
```

### 📊 Resumo dos Métodos de Listagem

| Entidade | Desktop (Controller) | Servidor (DAO) | Mobile (Room DAO) |
|----------|---------------------|----------------|-------------------|
| **Contas** | `listarContas()` linha 105 | `ContaDAO.listarPorUsuario()` | `ContaDao.listarPorUsuario()` |
| **Categorias** | `listarCategorias()` linha 230 | `CategoriaDAO.listarPorUsuario()` | `CategoriaDao.listarPorUsuario()` |
| **Movimentações** | `listarMovimentacoes()` linha 367 | `MovimentacaoDAO.listarPorUsuario()` | `LancamentoDao.listarPorUsuario()` |

---

## 4. Conexão com Servidor

### 🖥️ Conexão Desktop

#### 4.1 NetworkClient (Cliente Desktop)

**Arquivo:** `DESKTOP VERSION/ClienteFinanza/src/util/NetworkClient.java`

```java
// CONFIGURAÇÃO DE CONEXÃO - Linhas 10-11
private static final String SERVER_HOST = "localhost";  // Endereço do servidor
private static final int SERVER_PORT = 8080;           // Porta TCP

// VARIÁVEIS DE CONEXÃO - Linhas 13-16
private Socket socket;              // Socket TCP
private BufferedReader input;       // Lê respostas do servidor
private PrintWriter output;         // Envia comandos ao servidor
private boolean connected = false;  // Flag de conexão

// MÉTODO CONECTAR - Linhas 21-34
public boolean connect() {
    try {
        // Linha 23: Cria socket TCP para host:porta
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        
        // Linha 24: Cria leitor para receber dados
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // Linha 25: Cria escritor para enviar dados (auto-flush)
        output = new PrintWriter(socket.getOutputStream(), true);
        
        connected = true;
        return true;
    } catch (IOException e) {
        connected = false;
        return false;
    }
}

// MÉTODO ENVIAR COMANDO - Linhas 39-55
public String sendCommand(String command) {
    // Linha 40-42: Verifica se está conectado
    if (!connected) {
        return "ERROR|Não conectado ao servidor";
    }
    
    try {
        // Linha 45: ENVIA comando (adiciona \n automaticamente)
        output.println(command);
        
        // Linha 46: AGUARDA resposta (BLOQUEANTE até receber \n)
        String response = input.readLine();
        
        return response;
    } catch (IOException e) {
        disconnect();
        return "ERROR|Erro de comunicação";
    }
}
```

### 📱 Conexão Mobile

#### 4.2 ServerClient (Cliente Android)

**Arquivo:** `app/src/main/java/com/example/finanza/network/ServerClient.java`

```java
// CONFIGURAÇÃO - Linhas 65-76
private static final String TAG = "ServerClient";
private static final int CONNECTION_TIMEOUT = 5000;  // 5 segundos timeout
private static final String PREFS_NAME = "FinanzaServerConfig";
private static final String PREF_HOST = "server_host";
private static final String PREF_PORT = "server_port";

// VARIÁVEIS - Linhas 80-99
private Context context;
private String serverHost;     // Configurável via SharedPreferences
private int serverPort;
private Socket socket;
private BufferedReader input;
private PrintWriter output;
private boolean connected = false;
private static ServerClient instance;  // Singleton

// SINGLETON - Linhas 112-117
public static synchronized ServerClient getInstance(Context context) {
    if (instance == null) {
        instance = new ServerClient(context.getApplicationContext());
    }
    return instance;
}

// CONFIGURAR SERVIDOR - Linhas 194-207
public void configurarServidor(String host, int port) {
    this.serverHost = host;
    this.serverPort = port;
    
    // Salva nas SharedPreferences para persistir
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    prefs.edit()
         .putString(PREF_HOST, host)
         .putInt(PREF_PORT, port)
         .apply();
}

// CONECTAR (Assíncrono) - Linhas 231-297
public void conectar(String host, int port, ServerCallback<String> callback) {
    new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Linha 246: Fecha conexão anterior
                disconnect();
                
                // Linha 249: Cria novo socket
                socket = new Socket();
                
                // Linha 252: Conecta COM TIMEOUT (5 segundos)
                socket.connect(new java.net.InetSocketAddress(host, port), CONNECTION_TIMEOUT);
                
                // Linha 255: Cria stream de entrada
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // Linha 258: Cria stream de saída
                output = new PrintWriter(socket.getOutputStream(), true);
                
                connected = true;
                return "Conectado ao servidor: " + host + ":" + port;
                
            } catch (SocketTimeoutException e) {
                return null;  // Timeout
            } catch (IOException e) {
                return null;  // Erro de conexão
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onError(errorMessage);
            }
        }
    }.execute();
}

// ENVIAR COMANDO (Assíncrono) - Linhas 351-428
public void enviarComando(String comando, ServerCallback<String> callback) {
    new AsyncTask<String, Void, String>() {
        @Override
        protected String doInBackground(String... commands) {
            String command = commands[0];
            
            // Validações
            if (!connected) return null;
            if (socket == null || socket.isClosed()) return null;
            
            try {
                // Linha 381: Envia comando
                output.println(command);
                output.flush();
                
                // Linha 384: Aguarda resposta (BLOQUEANTE)
                String response = input.readLine();
                
                if (response == null) {
                    connected = false;
                    return null;
                }
                
                return response;
            } catch (IOException e) {
                connected = false;
                return null;
            }
        }
        // ...
    }.execute(comando);
}
```

### 🔌 Conexão do Servidor

#### 4.3 FinanzaServer (Servidor TCP)

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/server/FinanzaServer.java`

```java
// O servidor escuta na porta 8080 e aceita conexões de clientes
// Para cada cliente conectado, cria uma nova thread (ClientHandler)
```

#### 4.4 ClientHandler - Thread por Cliente

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java`

```java
// CONSTRUTOR - Linhas 128-141
public ClientHandler(Socket clientSocket, boolean testMode) {
    this.clientSocket = clientSocket;
    this.testMode = testMode;
    
    // Inicializa DAOs para acesso ao banco
    this.usuarioDAO = new UsuarioDAO();
    this.contaDAO = new ContaDAO();
    this.categoriaDAO = new CategoriaDAO();
    this.movimentacaoDAO = new MovimentacaoDAO();
}

// MÉTODO RUN (Loop principal) - Linhas 183-244
@Override
public void run() {
    try {
        // Linha 189-191: Cria streams de entrada e saída
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        
        // Linha 202-225: Loop de processamento de comandos
        String comando;
        while ((comando = input.readLine()) != null) {
            // Linha 214: Processa comando e obtém resposta
            String resposta = processarComando(comando);
            
            // Linha 222: Envia resposta de volta ao cliente
            output.println(resposta);
        }
    } catch (IOException e) {
        // Erro de comunicação
    } finally {
        fecharConexao();
    }
}

// PROCESSAMENTO DE COMANDOS - Linhas 339-472
private String processarComando(String comando) {
    // Linha 343: Parse do comando
    String[] partes = Protocol.parseCommand(comando);
    
    // Linha 357-459: Switch para cada tipo de comando
    switch (cmd) {
        case Protocol.CMD_LOGIN:
            return processarLogin(partes);
        case Protocol.CMD_REGISTER:
            return processarRegistro(partes);
        case Protocol.CMD_LIST_CONTAS:
            return processarListContas();
        case Protocol.CMD_LIST_CATEGORIAS:
            return processarListCategorias();
        case Protocol.CMD_LIST_MOVIMENTACOES:
            return processarListMovimentacoes();
        // ... mais 40+ comandos
    }
}
```

### 📡 Protocolo de Comunicação

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/server/Protocol.java`

```java
// COMANDOS DISPONÍVEIS - Linhas 9-58
public static final String CMD_LOGIN = "LOGIN";
public static final String CMD_REGISTER = "REGISTER";
public static final String CMD_LOGOUT = "LOGOUT";
public static final String CMD_GET_DASHBOARD = "GET_DASHBOARD";

// Contas
public static final String CMD_LIST_CONTAS = "LIST_CONTAS";
public static final String CMD_ADD_CONTA = "ADD_CONTA";
public static final String CMD_UPDATE_CONTA = "UPDATE_CONTA";
public static final String CMD_DELETE_CONTA = "DELETE_CONTA";

// Categorias
public static final String CMD_LIST_CATEGORIAS = "LIST_CATEGORIAS";
public static final String CMD_ADD_CATEGORIA = "ADD_CATEGORIA";
// ...

// STATUS DE RESPOSTA - Linhas 60-65
public static final String STATUS_OK = "OK";
public static final String STATUS_ERROR = "ERROR";
public static final String STATUS_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
public static final String STATUS_USER_EXISTS = "USER_EXISTS";

// SEPARADORES - Linhas 68-69
public static final String SEPARATOR = "|";           // Entre comando e dados
public static final String FIELD_SEPARATOR = ";";    // Entre registros

// FORMATO DOS COMANDOS:
// Envio:    COMANDO|param1|param2|param3
// Resposta: STATUS|dados
// 
// Exemplos:
// Login:    "LOGIN|joao@email.com|123456|admin"
// Resposta: "OK|1;João;joao@email.com;admin"
```

### 🔗 Diagrama de Conexão Completo

```
┌──────────────────────┐                    ┌──────────────────────┐
│   CLIENTE DESKTOP    │                    │   CLIENTE MOBILE     │
│                      │                    │                      │
│  NetworkClient.java  │                    │  ServerClient.java   │
│  - localhost:8080    │                    │  - IP:8080           │
│  - Síncrono          │                    │  - Assíncrono        │
└──────────┬───────────┘                    └──────────┬───────────┘
           │                                           │
           │         TCP/IP Socket                     │
           │         Protocolo texto                   │
           │         Separador: |                      │
           │                                           │
           └─────────────────┬─────────────────────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │       SERVIDOR        │
                 │    FinanzaServer      │
                 │    Porta: 8080        │
                 │                       │
                 │   ┌───────────────┐   │
                 │   │ ClientHandler │   │  ← Thread por cliente
                 │   │   (Thread)    │   │
                 │   └───────┬───────┘   │
                 │           │           │
                 │           ▼           │
                 │   ┌───────────────┐   │
                 │   │   Protocol    │   │  ← Parse de comandos
                 │   └───────┬───────┘   │
                 │           │           │
                 │           ▼           │
                 │   ┌───────────────┐   │
                 │   │     DAOs      │   │  ← Acesso ao banco
                 │   │ Usuario/Conta │   │
                 │   │ Categoria/Mov │   │
                 │   └───────┬───────┘   │
                 │           │           │
                 └───────────┼───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │    BANCO DE DADOS     │
                 │       MySQL           │
                 │   finanza_db:3306     │
                 └───────────────────────┘
```

---

## 5. Conectores e Acesso ao Banco de Dados

### 🗄️ DatabaseUtil - Conexão com MySQL

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/util/DatabaseUtil.java`

```java
// CONFIGURAÇÃO DO BANCO - Linhas 12-14
private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";

// CARREGAMENTO DO DRIVER - Linhas 16-22
static {
    try {
        // Linha 18: Carrega driver JDBC do MySQL
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        System.err.println("Driver MySQL não encontrado: " + e.getMessage());
    }
}

// OBTER CONEXÃO - Linhas 29-31
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
}

// INICIALIZAR TABELAS - Linhas 63-146
public static void initializeDatabase() {
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {
        
        // Linha 68-75: Tabela USUARIO
        stmt.execute("CREATE TABLE IF NOT EXISTS usuario (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "email VARCHAR(150) UNIQUE NOT NULL, " +
                "senha_hash VARCHAR(255) NOT NULL, " +
                "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")");
        
        // Linha 78-86: Tabela CONTA
        stmt.execute("CREATE TABLE IF NOT EXISTS conta (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "tipo ENUM('corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro') NOT NULL, " +
                "saldo_inicial DECIMAL(10,2) DEFAULT 0.00, " +
                "id_usuario INT NOT NULL, " +
                "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                ")");
        
        // Linha 89-97: Tabela CATEGORIA
        stmt.execute("CREATE TABLE IF NOT EXISTS categoria (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "tipo ENUM('receita', 'despesa') NOT NULL, " +
                "id_usuario INT NOT NULL, " +
                "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                ")");
        
        // Linha 99-114: Tabela MOVIMENTACAO
        stmt.execute("CREATE TABLE IF NOT EXISTS movimentacao (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "valor DECIMAL(10,2) NOT NULL, " +
                "data DATE NOT NULL, " +
                "descricao TEXT, " +
                "tipo ENUM('receita', 'despesa') NOT NULL, " +
                "id_conta INT NOT NULL, " +
                "id_categoria INT NOT NULL, " +
                "id_usuario INT NOT NULL, " +
                "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_conta) REFERENCES conta(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                ")");
    }
}
```

### 👤 UsuarioDAO - Operações com Usuários

**Arquivo:** `DESKTOP VERSION/ServidorFinanza/src/dao/UsuarioDAO.java`

```java
// INSERIR USUÁRIO - Linhas 21-48
public boolean inserir(Usuario usuario) {
    String sql = "INSERT INTO usuario (nome, email, senha_hash, tipo_usuario) VALUES (?, ?, ?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        stmt.setString(1, usuario.getNome());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getSenhaHash());  // Senha JÁ criptografada
        stmt.setString(4, usuario.getTipoUsuario());
        
        int rowsAffected = stmt.executeUpdate();
        
        if (rowsAffected > 0) {
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));  // Seta ID gerado
                }
            }
            return true;
        }
    } catch (SQLException e) {
        System.err.println("Erro ao inserir usuário: " + e.getMessage());
    }
    return false;
}

// BUSCAR POR EMAIL - Linhas 55-73
public Usuario buscarPorEmail(String email) {
    String sql = "SELECT * FROM usuario WHERE email = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, email);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
        }
    } catch (SQLException e) {
        System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
    }
    return null;
}

// AUTENTICAR (Login) - Linhas 106-114
public Usuario autenticar(String email, String senha) {
    Usuario usuario = buscarPorEmail(email);
    
    if (usuario != null && SecurityUtil.verificarSenha(senha, usuario.getSenhaHash())) {
        return usuario;  // Senha confere
    }
    
    return null;  // Email não existe ou senha errada
}

// ATUALIZAR SENHA - Linhas 146-162
public boolean atualizarSenha(int idUsuario, String novaSenha) {
    String sql = "UPDATE usuario SET senha_hash = ? WHERE id = ?";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // Linha 152: CRIPTOGRAFA a nova senha antes de salvar
        stmt.setString(1, SecurityUtil.hashSenha(novaSenha));
        stmt.setInt(2, idUsuario);
        
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Erro ao atualizar senha: " + e.getMessage());
    }
    return false;
}

// LISTAR TODOS - Linhas 168-190
public List<Usuario> listarTodos() {
    List<Usuario> usuarios = new ArrayList<>();
    String sql = "SELECT * FROM usuario ORDER BY id";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            Usuario usuario = mapResultSetToUsuario(rs);
            usuarios.add(usuario);
        }
    } catch (SQLException e) {
        System.err.println("Erro ao listar usuários: " + e.getMessage());
    }
    return usuarios;
}

// MAPEAMENTO ResultSet → Objeto - Linhas 217-227
private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
    Usuario usuario = new Usuario();
    usuario.setId(rs.getInt("id"));
    usuario.setNome(rs.getString("nome"));
    usuario.setEmail(rs.getString("email"));
    usuario.setSenhaHash(rs.getString("senha_hash"));
    usuario.setTipoUsuario(rs.getString("tipo_usuario"));
    usuario.setDataCriacao(rs.getTimestamp("data_criacao"));
    usuario.setDataAtualizacao(rs.getTimestamp("data_atualizacao"));
    return usuario;
}
```

### 📊 Diagrama de Classes DAO

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              DATABASE LAYER                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐                                                        │
│  │  DatabaseUtil   │ ← Gerencia conexões com MySQL                          │
│  │  (Singleton)    │   - getConnection(): Obtém conexão                     │
│  │                 │   - initializeDatabase(): Cria tabelas                 │
│  └────────┬────────┘                                                        │
│           │                                                                 │
│           │ provides Connection                                             │
│           │                                                                 │
│  ┌────────┼────────────────────────────────────────────────┐               │
│  │        │                                                 │               │
│  ▼        ▼                ▼                    ▼           │               │
│ ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐   │
│ │ UsuarioDAO   │  │  ContaDAO    │  │ CategoriaDAO │  │MovimentacaoDAO │   │
│ ├──────────────┤  ├──────────────┤  ├──────────────┤  ├────────────────┤   │
│ │ inserir()    │  │ inserir()    │  │ inserir()    │  │ inserir()      │   │
│ │ buscarPorId()│  │ buscarPorId()│  │ buscarPorId()│  │ buscarPorId()  │   │
│ │ buscarPorEmail│ │ listarPorUsuario│ listarPorUsuario │ listarPorUsuario││
│ │ autenticar() │  │ atualizar()  │  │ atualizar()  │  │ atualizar()    │   │
│ │ atualizar()  │  │ remover()    │  │ remover()    │  │ remover()      │   │
│ │ atualizarSenha│ │ calcularSaldo │  │ listarPorTipo│  │ calcularTotais │   │
│ │ listarTodos()│  │              │  │              │  │                │   │
│ │ excluir()    │  │              │  │              │  │                │   │
│ └──────────────┘  └──────────────┘  └──────────────┘  └────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ JDBC
                                    ▼
                        ┌─────────────────────┐
                        │       MySQL         │
                        │    finanza_db       │
                        │                     │
                        │  Tables:            │
                        │  - usuario          │
                        │  - conta            │
                        │  - categoria        │
                        │  - movimentacao     │
                        └─────────────────────┘
```

### 📱 Room Database (Mobile)

**Arquivo:** `app/src/main/java/com/example/finanza/db/AppDatabase.java`

```java
// CONFIGURAÇÃO - Linhas 42-47
@Database(
    entities = {Usuario.class, Conta.class, Categoria.class, Lancamento.class},
    version = 6,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    // SINGLETON - Linhas 268-282
    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, 
                            "finanza-database"  // Nome do arquivo .db
                        )
                        .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()  // ⚠️ APENAS PARA DESENVOLVIMENTO!
                        .build();
                }
            }
        }
        return INSTANCE;
    }
    
    // DAOs DISPONÍVEIS
    public abstract UsuarioDao usuarioDao();     // Linha 56
    public abstract ContaDao contaDao();         // Linha 62
    public abstract CategoriaDao categoriaDao(); // Linha 68
    public abstract LancamentoDao lancamentoDao(); // Linha 74
}
```

> ⚠️ **AVISO IMPORTANTE:** O método `allowMainThreadQueries()` está configurado apenas para facilitar o desenvolvimento. Em **produção**, esta opção deve ser **removida** para evitar problemas de ANR (Application Not Responsive).

**Solução recomendada para produção:**
- Usar **Coroutines** (Kotlin) ou **AsyncTask/Executors** (Java) para operações de banco
- Implementar **LiveData** ou **Flow** para observar mudanças no banco de forma reativa

---

## 📝 Resumo Final

### Tabela de Referência Rápida

| Tópico | Desktop | Servidor | Mobile |
|--------|---------|----------|--------|
| **Criptografia** | - | `SecurityUtil.java` | - |
| **Login (Cliente)** | `AuthController.java` linha 44 | `ClientHandler.java` linha 477 | `LoginActivity.java` linha 133 |
| **Login (Auth)** | - | `UsuarioDAO.java` linha 106 | `AuthManager.java` linha 112 |
| **Cadastro** | `AuthController.java` linha 106 | `ClientHandler.java` linha 534 | `RegisterActivity.java` |
| **Listar Contas** | `FinanceController.java` linha 105 | `ContaDAO.java` | `ContaDao.java` |
| **Listar Categorias** | `FinanceController.java` linha 230 | `CategoriaDAO.java` | `CategoriaDao.java` |
| **Listar Movimentações** | `FinanceController.java` linha 367 | `MovimentacaoDAO.java` | `LancamentoDao.java` |
| **Conexão** | `NetworkClient.java` | `FinanzaServer.java` + `ClientHandler.java` | `ServerClient.java` |
| **Banco de Dados** | - | `DatabaseUtil.java` (MySQL) | `AppDatabase.java` (SQLite/Room) |

### Arquivos Mais Importantes

1. **Criptografia:** `DESKTOP VERSION/ServidorFinanza/src/util/SecurityUtil.java`
2. **Login Desktop:** `DESKTOP VERSION/ClienteFinanza/src/controller/AuthController.java`
3. **Login Mobile:** `app/src/main/java/com/example/finanza/network/AuthManager.java`
4. **Processamento Servidor:** `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java`
5. **Conexão Desktop:** `DESKTOP VERSION/ClienteFinanza/src/util/NetworkClient.java`
6. **Conexão Mobile:** `app/src/main/java/com/example/finanza/network/ServerClient.java`
7. **DAOs Servidor:** `DESKTOP VERSION/ServidorFinanza/src/dao/*.java`
8. **DAOs Mobile:** `app/src/main/java/com/example/finanza/db/*.java`

---

**Última atualização:** 2025-12-02  
**Versão do documento:** 1.0  
**Autor:** Documentação Técnica Gerada Automaticamente
