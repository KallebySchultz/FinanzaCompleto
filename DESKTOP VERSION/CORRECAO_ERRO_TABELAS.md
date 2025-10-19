# Correção do Erro: Table 'finanza_db.usuario' doesn't exist

## Problema

Ao tentar fazer login ou registro no sistema Finanza Desktop, os seguintes erros ocorriam:

```
Erro ao buscar usuário por email: Table 'finanza_db.usuario' doesn't exist
Erro ao inserir usuário: Table 'finanza_db.usuario' doesn't exist
```

### Causa Raiz

O servidor conseguia conectar-se ao banco de dados MySQL (`finanza_db`), mas as tabelas necessárias (`usuario`, `conta`, `categoria`, `movimentacao`) não existiam. Anteriormente, era necessário executar manualmente o script SQL `banco/script_inicial.sql` antes de iniciar o servidor.

## Solução Implementada

### Mudanças no Código

#### 1. `DatabaseUtil.java` - Novo Método de Inicialização

Adicionado o método `initializeDatabase()` que:
- Cria automaticamente todas as tabelas necessárias se elas não existirem
- Usa `CREATE TABLE IF NOT EXISTS` para segurança
- Cria os índices necessários para melhor performance
- Trata exceções de índices duplicados (caso já existam)

**Tabelas criadas:**
- `usuario` - Armazena dados dos usuários
- `conta` - Armazena contas financeiras
- `categoria` - Armazena categorias de transações
- `movimentacao` - Armazena transações financeiras

**Índices criados:**
- `idx_movimentacao_data` - Para busca rápida por data
- `idx_movimentacao_usuario` - Para busca rápida por usuário
- `idx_conta_usuario` - Para busca rápida de contas por usuário
- `idx_categoria_usuario` - Para busca rápida de categorias por usuário

#### 2. `FinanzaServer.java` - Chamada da Inicialização

Modificado o método `start()` para chamar `DatabaseUtil.initializeDatabase()` após verificar a conexão com o banco de dados e antes de aceitar conexões de clientes.

### Vantagens da Solução

1. **Automática**: Não é mais necessário executar scripts SQL manualmente
2. **Idempotente**: Pode ser executada múltiplas vezes sem causar erros
3. **Segura**: Não sobrescreve dados existentes
4. **Compatível**: Funciona com instalações novas e existentes

## Como Usar

### Para Novas Instalações

1. Instale o MySQL Server
2. Crie o banco de dados:
   ```sql
   CREATE DATABASE finanza_db;
   ```
3. Configure as credenciais em `DatabaseUtil.java` (se necessário)
4. Execute o servidor - as tabelas serão criadas automaticamente!

### Para Instalações Existentes

Se você já tinha as tabelas criadas manualmente, nada muda. O servidor detectará que as tabelas já existem e continuará funcionando normalmente.

## Verificação

Após a correção, ao iniciar o servidor você verá:

```
=== Finanza Desktop - Servidor ===
Inicializando servidor...
Conexão com banco de dados OK
Tabelas do banco de dados verificadas/criadas com sucesso
Servidor Finanza iniciado na porta 8080
Aguardando conexões de clientes...
```

E o login/registro funcionará corretamente sem erros de tabelas inexistentes.

## Arquivos Modificados

1. `DESKTOP VERSION/ServidorFinanza/src/util/DatabaseUtil.java`
   - Adicionado método `initializeDatabase()`
   - +92 linhas de código

2. `DESKTOP VERSION/ServidorFinanza/src/server/FinanzaServer.java`
   - Adicionada chamada para `DatabaseUtil.initializeDatabase()`
   - +3 linhas de código

3. `DESKTOP VERSION/README.md` (novo arquivo)
   - Documentação completa de instalação e uso
   - Guia de solução de problemas

## Notas Técnicas

- O código usa `CREATE TABLE IF NOT EXISTS` para evitar erros em tabelas existentes
- As foreign keys mantêm a integridade referencial
- Os índices melhoram a performance de consultas
- O tratamento de exceções garante que índices duplicados não causem falhas
- A solução é compatível com MySQL 5.7+

## Teste de Compilação

Todos os arquivos foram compilados com sucesso:
- ✅ `DatabaseUtil.java` compila sem erros
- ✅ `FinanzaServer.java` compila sem erros  
- ✅ Todos os arquivos do servidor compilam sem erros
- ✅ Todos os arquivos do cliente compilam sem erros

## Próximos Passos

Após aplicar esta correção:
1. Compile o projeto servidor
2. Inicie o servidor
3. As tabelas serão criadas automaticamente
4. Faça login ou registro no cliente
5. Tudo deve funcionar corretamente!
