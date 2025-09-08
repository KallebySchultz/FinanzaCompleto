# 7. DIAGRAMA DE CLASSE - FINANZA

## 📋 Visão Geral

O diagrama de classes do sistema Finanza apresenta a estrutura orientada a objetos completa, mostrando as principais entidades do domínio, seus atributos, métodos e relacionamentos. O modelo suporta tanto a aplicação mobile Android quanto a aplicação desktop Java.

---

## 🏗️ Arquitetura de Classes

```mermaid
classDiagram
    %% Classes Principais do Domínio
    class Usuario {
        -String uuid
        -String nome
        -String email
        -String senhaHash
        -Date dataCriacao
        -Date ultimoLogin
        -boolean ativo
        -String configuracoes
        +Usuario()
        +String getUuid()
        +void setUuid(String uuid)
        +String getNome()
        +void setNome(String nome)
        +String getEmail()
        +void setEmail(String email)
        +boolean validarSenha(String senha)
        +void criptografarSenha(String senha)
        +boolean isAtivo()
        +void ativar()
        +void desativar()
        +Date getUltimoLogin()
        +void atualizarUltimoLogin()
        +String toString()
        +boolean equals(Object obj)
        +int hashCode()
    }
    
    class Conta {
        -String uuid
        -String nome
        -String tipo
        -BigDecimal saldo
        -String banco
        -String agencia
        -String numero
        -boolean ativa
        -Date dataCriacao
        -String uuidUsuario
        +Conta()
        +Conta(String nome, String tipo, String uuidUsuario)
        +String getUuid()
        +void setUuid(String uuid)
        +String getNome()
        +void setNome(String nome)
        +String getTipo()
        +void setTipo(String tipo)
        +BigDecimal getSaldo()
        +void setSaldo(BigDecimal saldo)
        +void adicionarValor(BigDecimal valor)
        +void subtrairValor(BigDecimal valor)
        +boolean isAtiva()
        +void ativar()
        +void desativar()
        +String getUuidUsuario()
        +void setUuidUsuario(String uuidUsuario)
        +String toString()
        +boolean equals(Object obj)
        +int hashCode()
    }
    
    class Categoria {
        -String uuid
        -String nome
        -String tipo
        -String cor
        -String icone
        -boolean ativa
        -Date dataCriacao
        -String uuidUsuario
        +Categoria()
        +Categoria(String nome, String tipo, String uuidUsuario)
        +String getUuid()
        +void setUuid(String uuid)
        +String getNome()
        +void setNome(String nome)
        +String getTipo()
        +void setTipo(String tipo)
        +String getCor()
        +void setCor(String cor)
        +String getIcone()
        +void setIcone(String icone)
        +boolean isAtiva()
        +void ativar()
        +void desativar()
        +String getUuidUsuario()
        +void setUuidUsuario(String uuidUsuario)
        +String toString()
        +boolean equals(Object obj)
        +int hashCode()
    }
    
    class Transacao {
        -String uuid
        -BigDecimal valor
        -String descricao
        -Date data
        -String tipo
        -String status
        -String observacoes
        -Date dataModificacao
        -String uuidConta
        -String uuidCategoria
        -String uuidUsuario
        +Transacao()
        +Transacao(BigDecimal valor, String tipo, String uuidConta, String uuidCategoria, String uuidUsuario)
        +String getUuid()
        +void setUuid(String uuid)
        +BigDecimal getValor()
        +void setValor(BigDecimal valor)
        +String getDescricao()
        +void setDescricao(String descricao)
        +Date getData()
        +void setData(Date data)
        +String getTipo()
        +void setTipo(String tipo)
        +String getStatus()
        +void setStatus(String status)
        +void confirmar()
        +void cancelar()
        +void estornar()
        +String getUuidConta()
        +void setUuidConta(String uuidConta)
        +String getUuidCategoria()
        +void setUuidCategoria(String uuidCategoria)
        +String getUuidUsuario()
        +void setUuidUsuario(String uuidUsuario)
        +boolean isReceita()
        +boolean isDespesa()
        +String toString()
        +boolean equals(Object obj)
        +int hashCode()
    }
    
    %% Classes de Sincronização
    class SyncData {
        -String uuid
        -String tipoEntidade
        -String uuidEntidade
        -String acao
        -Date timestamp
        -String dadosJson
        -boolean sincronizado
        -String uuidUsuario
        +SyncData()
        +SyncData(String tipoEntidade, String uuidEntidade, String acao, String uuidUsuario)
        +String getUuid()
        +void setUuid(String uuid)
        +String getTipoEntidade()
        +void setTipoEntidade(String tipoEntidade)
        +String getUuidEntidade()
        +void setUuidEntidade(String uuidEntidade)
        +String getAcao()
        +void setAcao(String acao)
        +Date getTimestamp()
        +void setTimestamp(Date timestamp)
        +String getDadosJson()
        +void setDadosJson(String dadosJson)
        +boolean isSincronizado()
        +void marcarComoSincronizado()
        +void marcarComoNaoSincronizado()
        +String getUuidUsuario()
        +void setUuidUsuario(String uuidUsuario)
        +String toString()
        +boolean equals(Object obj)
        +int hashCode()
    }
    
    class ConflictResolution {
        -String uuid
        -String tipoConflito
        -String uuidEntidade
        -String dadosServidor
        -String dadosCliente
        -String resolucao
        -Date timestampConflito
        -Date timestampResolucao
        -String uuidUsuario
        +ConflictResolution()
        +String getUuid()
        +void setUuid(String uuid)
        +String getTipoConflito()
        +void setTipoConflito(String tipoConflito)
        +String getUuidEntidade()
        +void setUuidEntidade(String uuidEntidade)
        +String getDadosServidor()
        +void setDadosServidor(String dadosServidor)
        +String getDadosCliente()
        +void setDadosCliente(String dadosCliente)
        +String getResolucao()
        +void setResolucao(String resolucao)
        +Date getTimestampConflito()
        +void setTimestampConflito(Date timestampConflito)
        +Date getTimestampResolucao()
        +void setTimestampResolucao(Date timestampResolucao)
        +void resolverConflito(String estrategia)
        +boolean isResolvido()
        +String getUuidUsuario()
        +void setUuidUsuario(String uuidUsuario)
        +String toString()
        +boolean equals(Object obj)
        +int hashCode()
    }
    
    %% Classes de Relatório
    class Relatorio {
        -String uuid
        -String tipo
        -String titulo
        -Date dataInicio
        -Date dataFim
        -String parametros
        -String formato
        -Date dataGeracao
        -String caminhoArquivo
        -String uuidUsuario
        +Relatorio()
        +Relatorio(String tipo, String titulo, Date dataInicio, Date dataFim, String uuidUsuario)
        +String getUuid()
        +void setUuid(String uuid)
        +String getTipo()
        +void setTipo(String tipo)
        +String getTitulo()
        +void setTitulo(String titulo)
        +Date getDataInicio()
        +void setDataInicio(Date dataInicio)
        +Date getDataFim()
        +void setDataFim(Date dataFim)
        +String getParametros()
        +void setParametros(String parametros)
        +String getFormato()
        +void setFormato(String formato)
        +Date getDataGeracao()
        +void setDataGeracao(Date dataGeracao)
        +String getCaminhoArquivo()
        +void setCaminhoArquivo(String caminhoArquivo)
        +void gerar()
        +void exportar(String formato)
        +boolean isGerado()
        +String getUuidUsuario()
        +void setUuidUsuario(String uuidUsuario)
        +String toString()
        +boolean equals(Object obj)
        +int hashCode()
    }
    
    %% Classes de Controle/Serviço
    class AuthService {
        -static AuthService instance
        -SessionManager sessionManager
        +static AuthService getInstance()
        +Usuario login(String email, String senha)
        +boolean logout(String uuidUsuario)
        +Usuario registrar(String nome, String email, String senha)
        +boolean recuperarSenha(String email)
        +boolean alterarSenha(String uuidUsuario, String senhaAtual, String novaSenha)
        +boolean isUsuarioLogado(String uuidUsuario)
        +Session getSession(String uuidUsuario)
        +boolean validarToken(String token)
        +String gerarToken(Usuario usuario)
    }
    
    class SyncService {
        -static SyncService instance
        -Queue~SyncData~ filaSincronizacao
        -boolean sincronizandoAtivo
        +static SyncService getInstance()
        +void adicionarParaSincronizacao(SyncData dados)
        +void sincronizarTudo(String uuidUsuario)
        +void sincronizarEntidade(String tipoEntidade, String uuidEntidade)
        +List~ConflictResolution~ detectarConflitos(String uuidUsuario)
        +void resolverConflito(ConflictResolution conflito, String estrategia)
        +boolean isSincronizandoAtivo()
        +void iniciarSincronizacaoAutomatica()
        +void pararSincronizacaoAutomatica()
        +SyncStatus getStatusSincronizacao(String uuidUsuario)
    }
    
    class DatabaseManager {
        -static DatabaseManager instance
        -Connection connection
        -String connectionString
        +static DatabaseManager getInstance()
        +Connection getConnection()
        +void closeConnection()
        +boolean executarQuery(String sql, Object[] parametros)
        +ResultSet executarSelect(String sql, Object[] parametros)
        +boolean testarConexao()
        +void configurarPool()
        +void backupDatabase(String caminho)
        +void restoreDatabase(String caminho)
    }
    
    %% Classes DAO (Data Access Object)
    class UsuarioDAO {
        -DatabaseManager dbManager
        +UsuarioDAO()
        +boolean inserir(Usuario usuario)
        +Usuario buscarPorUuid(String uuid)
        +Usuario buscarPorEmail(String email)
        +List~Usuario~ listarTodos()
        +boolean atualizar(Usuario usuario)
        +boolean excluir(String uuid)
        +List~Usuario~ buscarPorNome(String nome)
        +boolean emailJaExiste(String email)
    }
    
    class ContaDAO {
        -DatabaseManager dbManager
        +ContaDAO()
        +boolean inserir(Conta conta)
        +Conta buscarPorUuid(String uuid)
        +List~Conta~ listarPorUsuario(String uuidUsuario)
        +boolean atualizar(Conta conta)
        +boolean excluir(String uuid)
        +BigDecimal calcularSaldoTotal(String uuidUsuario)
        +List~Conta~ buscarPorTipo(String tipo, String uuidUsuario)
    }
    
    class CategoriaDAO {
        -DatabaseManager dbManager
        +CategoriaDAO()
        +boolean inserir(Categoria categoria)
        +Categoria buscarPorUuid(String uuid)
        +List~Categoria~ listarPorUsuario(String uuidUsuario)
        +List~Categoria~ listarPorTipo(String tipo, String uuidUsuario)
        +boolean atualizar(Categoria categoria)
        +boolean excluir(String uuid)
        +List~Categoria~ buscarPorNome(String nome, String uuidUsuario)
    }
    
    class TransacaoDAO {
        -DatabaseManager dbManager
        +TransacaoDAO()
        +boolean inserir(Transacao transacao)
        +Transacao buscarPorUuid(String uuid)
        +List~Transacao~ listarPorUsuario(String uuidUsuario)
        +List~Transacao~ listarPorPeriodo(Date inicio, Date fim, String uuidUsuario)
        +List~Transacao~ listarPorConta(String uuidConta)
        +List~Transacao~ listarPorCategoria(String uuidCategoria)
        +boolean atualizar(Transacao transacao)
        +boolean excluir(String uuid)
        +BigDecimal somarPorTipo(String tipo, String uuidUsuario)
        +BigDecimal somarPorCategoria(String uuidCategoria)
        +List~Transacao~ buscarPorDescricao(String descricao, String uuidUsuario)
    }
    
    %% Relacionamentos
    Usuario ||--o{ Conta : possui
    Usuario ||--o{ Categoria : cria
    Usuario ||--o{ Transacao : realiza
    Usuario ||--o{ SyncData : gera
    Usuario ||--o{ ConflictResolution : possui
    Usuario ||--o{ Relatorio : solicita
    
    Conta ||--o{ Transacao : contém
    Categoria ||--o{ Transacao : classifica
    
    Transacao ||--o| SyncData : sincroniza
    
    AuthService --> Usuario : autentica
    AuthService --> SessionManager : utiliza
    
    SyncService --> SyncData : gerencia
    SyncService --> ConflictResolution : resolve
    
    UsuarioDAO --> Usuario : persiste
    ContaDAO --> Conta : persiste
    CategoriaDAO --> Categoria : persiste
    TransacaoDAO --> Transacao : persiste
    
    UsuarioDAO --> DatabaseManager : utiliza
    ContaDAO --> DatabaseManager : utiliza
    CategoriaDAO --> DatabaseManager : utiliza
    TransacaoDAO --> DatabaseManager : utiliza
    
    SyncService --> TransacaoDAO : acessa
    SyncService --> ContaDAO : acessa
    SyncService --> CategoriaDAO : acessa
    
    AuthService --> UsuarioDAO : acessa
```

---

## 📊 Detalhamento das Classes Principais

### **🔐 Classe Usuario**
**Responsabilidade:** Representar usuários do sistema
- **Atributos Principais:**
  - `uuid`: Identificador único universal
  - `email`: Credencial de login única
  - `senhaHash`: Senha criptografada SHA-256
  - `ativo`: Status da conta
- **Métodos Principais:**
  - `validarSenha()`: Verifica credenciais
  - `criptografarSenha()`: Aplica hash de segurança
  - `atualizarUltimoLogin()`: Registra acesso

### **💳 Classe Conta**
**Responsabilidade:** Gerenciar contas bancárias e carteiras
- **Atributos Principais:**
  - `saldo`: Valor atual da conta
  - `tipo`: Conta corrente, poupança, cartão, etc.
  - `banco`: Instituição financeira
- **Métodos Principais:**
  - `adicionarValor()`: Incrementa saldo
  - `subtrairValor()`: Decrementa saldo
  - `isAtiva()`: Verifica status

### **🏷️ Classe Categoria**
**Responsabilidade:** Organizar transações por categoria
- **Atributos Principais:**
  - `tipo`: Receita ou despesa
  - `cor`: Identificação visual
  - `icone`: Representação gráfica
- **Métodos Principais:**
  - `ativar()/desativar()`: Controle de status

### **💰 Classe Transacao**
**Responsabilidade:** Representar movimentações financeiras
- **Atributos Principais:**
  - `valor`: Quantia da transação
  - `tipo`: Receita ou despesa
  - `status`: Confirmada, pendente, cancelada
- **Métodos Principais:**
  - `confirmar()`: Efetiva transação
  - `estornar()`: Reverte transação
  - `isReceita()/isDespesa()`: Verificação de tipo

---

## 🔄 Classes de Sincronização

### **📡 Classe SyncData**
**Responsabilidade:** Controlar dados de sincronização
- **Atributos Principais:**
  - `tipoEntidade`: Tipo do objeto (Usuario, Conta, etc.)
  - `acao`: Criar, atualizar, excluir
  - `timestamp`: Momento da modificação
- **Métodos Principais:**
  - `marcarComoSincronizado()`: Confirma sincronização

### **⚖️ Classe ConflictResolution**
**Responsabilidade:** Resolver conflitos de sincronização
- **Atributos Principais:**
  - `dadosServidor`: Versão do servidor
  - `dadosCliente`: Versão do cliente
  - `resolucao`: Estratégia aplicada
- **Métodos Principais:**
  - `resolverConflito()`: Aplica estratégia de resolução

---

## 🛠️ Classes de Serviço

### **🔐 Classe AuthService (Singleton)**
**Responsabilidade:** Gerenciar autenticação e segurança
- **Padrão:** Singleton para instância única
- **Funcionalidades:**
  - Login/logout de usuários
  - Geração e validação de tokens
  - Recuperação de senhas

### **🔄 Classe SyncService (Singleton)**
**Responsabilidade:** Coordenar sincronização
- **Padrão:** Singleton para controle centralizado
- **Funcionalidades:**
  - Fila de sincronização
  - Detecção de conflitos
  - Sincronização automática

### **🗄️ Classe DatabaseManager (Singleton)**
**Responsabilidade:** Gerenciar conexões com banco
- **Padrão:** Singleton para pool de conexões
- **Funcionalidades:**
  - Controle de conexões
  - Backup/restore
  - Pool de conexões

---

## 📁 Classes DAO (Data Access Object)

### **Padrão DAO Implementado:**
- **UsuarioDAO**: Persistência de usuários
- **ContaDAO**: Persistência de contas
- **CategoriaDAO**: Persistência de categorias
- **TransacaoDAO**: Persistência de transações

### **Funcionalidades Comuns:**
- CRUD completo (Create, Read, Update, Delete)
- Consultas específicas por critério
- Validações de integridade

---

## 🏗️ Padrões de Design Utilizados

### **1. Singleton**
- `AuthService`: Instância única de autenticação
- `SyncService`: Controle centralizado de sincronização
- `DatabaseManager`: Pool único de conexões

### **2. DAO (Data Access Object)**
- Separação entre lógica de negócio e acesso a dados
- Facilita manutenção e testes
- Abstrai detalhes do banco de dados

### **3. Repository Pattern**
- Services utilizam DAOs como repositories
- Centraliza lógica de acesso a dados
- Facilita mudanças de persistência

### **4. Value Object**
- Classes de domínio são immutáveis quando possível
- Implementam equals() e hashCode()
- Validações no construtor

---

## 🔗 Relacionamentos e Cardinalidades

### **Relacionamentos Principais:**
- `Usuario 1:N Conta` - Um usuário possui várias contas
- `Usuario 1:N Categoria` - Um usuário cria várias categorias
- `Usuario 1:N Transacao` - Um usuário realiza várias transações
- `Conta 1:N Transacao` - Uma conta contém várias transações
- `Categoria 1:N Transacao` - Uma categoria classifica várias transações

### **Relacionamentos de Sincronização:**
- `Usuario 1:N SyncData` - Dados de sync por usuário
- `Usuario 1:N ConflictResolution` - Conflitos por usuário
- `Transacao 1:1 SyncData` - Cada transação gera dados de sync

---

## 📈 Métricas do Diagrama

### **Estatísticas:**
- **Total de Classes:** 13 classes principais
- **Classes de Domínio:** 7 classes
- **Classes de Serviço:** 3 classes
- **Classes DAO:** 4 classes
- **Relacionamentos:** 15 associações

### **Complexidade:**
- **Métodos por Classe:** Média de 12 métodos
- **Atributos por Classe:** Média de 8 atributos
- **Relacionamentos por Classe:** Média de 3 relacionamentos

---

## 🎯 Benefícios da Arquitetura

### **Manutenibilidade:**
- ✅ Separação clara de responsabilidades
- ✅ Baixo acoplamento entre classes
- ✅ Alta coesão dentro das classes

### **Escalabilidade:**
- ✅ Fácil adição de novas entidades
- ✅ Padrões consistentes
- ✅ Extensibilidade planejada

### **Testabilidade:**
- ✅ Classes isoladas
- ✅ Dependências injetáveis
- ✅ Métodos unitários

---

**Legenda do Diagrama:**
- **Classes azuis:** Entidades de domínio principais
- **Classes verdes:** Services e controllers
- **Classes amarelas:** DAOs e persistência
- **Setas sólidas:** Associações diretas
- **Setas tracejadas:** Dependências de uso

**Observações:**
- Diagrama segue padrões UML 2.0
- Arquitetura suporta tanto mobile quanto desktop
- Sincronização é tratada como concern transversal
- Padrões de design aplicados consistentemente

---

*Diagrama criado seguindo padrões UML 2.0 e Clean Architecture*  
*Versão: 1.0 | Data: Dezembro 2024*  
*Ferramenta: Mermaid + Análise de Arquitetura*