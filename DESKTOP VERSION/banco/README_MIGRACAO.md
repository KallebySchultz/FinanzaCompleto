# Guia de Migração - Diferenciação de Usuário e Admin

## Visão Geral

Este guia explica como migrar um banco de dados existente do Finanza para suportar a diferenciação entre usuários comuns e administradores.

## Problema Resolvido

**Antes:** Todos os usuários eram iguais - não havia distinção entre admin e usuário comum. Isso permitia que:
- Administradores pudessem fazer login no aplicativo mobile
- Usuários comuns pudessem fazer login no painel administrativo desktop

**Depois:** Agora existe diferenciação clara entre tipos de usuário:
- **Administradores** (`tipo_usuario='admin'`): Acesso exclusivo ao painel desktop
- **Usuários** (`tipo_usuario='usuario'`): Acesso exclusivo ao aplicativo mobile

## Mudanças no Banco de Dados

### Nova Coluna

Foi adicionada a coluna `tipo_usuario` na tabela `usuario`:

```sql
tipo_usuario ENUM('admin', 'usuario') NOT NULL DEFAULT 'usuario'
```

### Valores Possíveis

- `'admin'` - Administrador do sistema (acesso desktop)
- `'usuario'` - Usuário comum (acesso mobile)

## Como Migrar

### Opção 1: Banco de Dados Novo

Se você está criando um novo banco de dados, use o script atualizado:

```bash
mysql -u root -p < script_inicial.sql
```

Este script já inclui:
- A coluna `tipo_usuario` na definição da tabela
- Um usuário admin padrão (`admin@finanza.com`)
- Um usuário comum de teste (`teste1@gmail.com`)

### Opção 2: Banco de Dados Existente

Se você já tem um banco de dados Finanza em produção, use o script de migração:

```bash
mysql -u root -p finanza_db < migration_add_tipo_usuario.sql
```

Este script irá:
1. Adicionar a coluna `tipo_usuario` à tabela `usuario`
2. Definir o primeiro usuário (ID=1) como admin
3. Definir todos os outros usuários como usuários comuns
4. Exibir a lista de usuários e seus tipos

### Ajustes Manuais (Opcional)

Após a migração, você pode ajustar manualmente os tipos de usuário conforme necessário:

```sql
-- Promover um usuário a admin
UPDATE usuario SET tipo_usuario = 'admin' WHERE id = 2;

-- Rebaixar um admin para usuário comum
UPDATE usuario SET tipo_usuario = 'usuario' WHERE id = 1;
```

## Mudanças no Protocolo de Comunicação

### Login

O comando de login agora inclui um terceiro parâmetro indicando o tipo de cliente:

**Desktop Admin:**
```
LOGIN|email@exemplo.com|senha|admin
```

**Mobile App:**
```
LOGIN|email@exemplo.com|senha|mobile
```

### Resposta de Login

A resposta de login bem-sucedida agora inclui o tipo de usuário:

**Antes:**
```
OK|userId;nome;email
```

**Depois:**
```
OK|userId;nome;email;tipo_usuario
```

### Novos Status de Erro

Foi adicionado um novo status de erro:

```
ACCESS_DENIED|Mensagem de erro
```

Exemplos de uso:
- Usuário comum tentando acessar desktop: `ACCESS_DENIED|Acesso negado. Apenas administradores podem acessar o painel desktop.`
- Admin tentando acessar mobile: `ACCESS_DENIED|Acesso negado. Administradores não podem acessar o aplicativo mobile.`

## Verificando a Migração

Execute esta query para verificar os tipos de usuário:

```sql
SELECT id, nome, email, tipo_usuario FROM usuario;
```

Resultado esperado:
```
+----+----------------+---------------------+--------------+
| id | nome           | email               | tipo_usuario |
+----+----------------+---------------------+--------------+
|  1 | Administrador  | admin@finanza.com   | admin        |
|  2 | Usuário Teste  | teste1@gmail.com    | usuario      |
+----+----------------+---------------------+--------------+
```

## Testando a Implementação

### Teste 1: Login Admin no Desktop
1. Abra o cliente desktop
2. Faça login com `admin@finanza.com`
3. **Resultado esperado:** Login bem-sucedido ✓

### Teste 2: Login Usuário no Desktop
1. Abra o cliente desktop
2. Faça login com `teste1@gmail.com`
3. **Resultado esperado:** Erro "Acesso negado. Apenas administradores podem acessar o painel desktop." ✗

### Teste 3: Login Usuário no Mobile
1. Abra o app mobile
2. Faça login com `teste1@gmail.com`
3. **Resultado esperado:** Login bem-sucedido ✓

### Teste 4: Login Admin no Mobile
1. Abra o app mobile
2. Faça login com `admin@finanza.com`
3. **Resultado esperado:** Erro "Acesso negado. Administradores não podem acessar o aplicativo mobile." ✗

## Perguntas Frequentes

### P: O que acontece com usuários existentes após a migração?

R: O script de migração define automaticamente:
- O primeiro usuário (ID=1) como admin
- Todos os outros como usuários comuns
- Você pode ajustar manualmente conforme necessário

### P: Posso ter múltiplos administradores?

R: Sim! Você pode promover quantos usuários quiser para admin usando o comando UPDATE.

### P: Um usuário pode ser admin e usuário comum ao mesmo tempo?

R: Não. Cada usuário tem um único tipo (admin OU usuario). Se precisar acessar ambas as plataformas, seria necessário criar duas contas separadas.

### P: É possível criar um novo admin através do app?

R: Não. Por segurança, novos registros via app sempre criam usuários comuns. Para criar um admin, é necessário acesso direto ao banco de dados.

## Rollback (Reverter Mudanças)

Se precisar reverter as mudanças:

```sql
-- Remove a coluna tipo_usuario
ALTER TABLE usuario DROP COLUMN tipo_usuario;
```

**⚠️ ATENÇÃO:** Isso removerá permanentemente a diferenciação entre admin e usuários. Use apenas se realmente necessário.

## Suporte

Em caso de dúvidas ou problemas durante a migração, consulte a documentação completa do projeto ou entre em contato com a equipe de desenvolvimento.
