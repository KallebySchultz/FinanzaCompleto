# 🗄️ Configuração do Banco de Dados - Finanza Desktop

## 📋 Visão Geral

O servidor Finanza Desktop agora **inicializa automaticamente** o banco de dados MySQL na primeira execução. Você não precisa mais executar scripts SQL manualmente!

## ✅ Configuração Automática

Quando você inicia o servidor pela primeira vez, ele:

1. ✓ Conecta ao banco de dados MySQL `finanza_db`
2. ✓ Verifica se as tabelas existem
3. ✓ Cria automaticamente todas as tabelas necessárias:
   - `usuario` - Dados dos usuários
   - `conta` - Contas financeiras
   - `categoria` - Categorias de transações
   - `movimentacao` - Transações financeiras
4. ✓ Cria índices para melhor performance

## 🚀 Passos para Configuração

### 1. Instalar e Configurar MySQL

#### Windows
```bash
# Baixe e instale MySQL do site oficial
# https://dev.mysql.com/downloads/installer/
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
```

#### macOS
```bash
brew install mysql
brew services start mysql
```

### 2. Criar o Banco de Dados

Conecte ao MySQL e crie o banco de dados:

```sql
mysql -u root -p

CREATE DATABASE IF NOT EXISTS finanza_db;
exit;
```

### 3. Configurar Credenciais (Opcional)

Se você usar credenciais diferentes das padrões, edite o arquivo:
`ServidorFinanza/src/util/DatabaseUtil.java`

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
private static final String DB_USER = "root";        // Altere se necessário
private static final String DB_PASSWORD = "";        // Altere se necessário
```

### 4. Iniciar o Servidor

Execute o servidor normalmente:

```bash
cd "DESKTOP VERSION"
./run_server.sh
```

**Você verá a mensagem de sucesso:**
```
=== Finanza Desktop - Servidor ===
Inicializando servidor...
Conexão com banco de dados OK
Inicializando estrutura do banco de dados...
✓ Tabela 'usuario' verificada/criada
✓ Tabela 'conta' verificada/criada
✓ Tabela 'categoria' verificada/criada
✓ Tabela 'movimentacao' verificada/criada
✓ Banco de dados inicializado com sucesso
Servidor Finanza iniciado na porta 8080
Aguardando conexões de clientes...
```

## 🔧 Solução de Problemas

### Erro: "Não foi possível conectar ao banco de dados"

**Causa:** MySQL não está rodando ou banco `finanza_db` não existe.

**Solução:**
```bash
# Verifique se MySQL está rodando
sudo systemctl status mysql  # Linux
# ou
brew services list           # macOS

# Crie o banco de dados manualmente
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS finanza_db;"
```

### Erro: "Table 'finanza_db.usuario' doesn't exist"

**Causa:** Erro na inicialização automática do banco de dados.

**Solução:** Execute o script SQL manualmente:
```bash
cd "DESKTOP VERSION/banco"
mysql -u root -p finanza_db < script_inicial.sql
```

### Erro: "Access denied for user 'root'@'localhost'"

**Causa:** Credenciais incorretas no código.

**Solução:** Atualize as credenciais em `DatabaseUtil.java` ou redefina a senha do MySQL:
```bash
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'sua_nova_senha';
```

## 📝 Script Manual (Opcional)

Se preferir executar o script SQL manualmente:

```bash
cd "DESKTOP VERSION/banco"
mysql -u root -p finanza_db < script_inicial.sql
```

O script cria:
- Todas as tabelas necessárias
- Índices para performance
- Usuário de teste (teste1@gmail.com)
- Categorias padrão

## 🔒 Segurança

### Produção

Para ambiente de produção, **altere as credenciais padrão**:

1. Crie um usuário específico para o Finanza:
```sql
CREATE USER 'finanza_user'@'localhost' IDENTIFIED BY 'senha_segura';
GRANT ALL PRIVILEGES ON finanza_db.* TO 'finanza_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Atualize `DatabaseUtil.java`:
```java
private static final String DB_USER = "finanza_user";
private static final String DB_PASSWORD = "senha_segura";
```

## ✨ Recursos

- ✅ Inicialização automática do banco de dados
- ✅ Verificação de integridade na inicialização
- ✅ Criação de tabelas com `IF NOT EXISTS` (seguro para re-execução)
- ✅ Índices automáticos para performance
- ✅ Logs detalhados de inicialização
- ✅ Tratamento de erros com mensagens claras

## 📞 Suporte

Se encontrar problemas durante a configuração:

1. Verifique os logs do servidor
2. Confirme que o MySQL está rodando
3. Verifique as credenciais em `DatabaseUtil.java`
4. Tente executar o script SQL manualmente
5. Consulte a documentação completa em `README.md`

---

**Última atualização:** Outubro 2024  
**Versão:** 1.1 - Com inicialização automática do banco de dados
