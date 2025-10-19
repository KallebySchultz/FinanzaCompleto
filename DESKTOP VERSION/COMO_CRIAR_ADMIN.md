# Como Criar Administradores no Sistema Finanza

Este guia explica como criar usuários administradores no sistema Finanza após a implementação da funcionalidade de registro via painel desktop.

## 🎯 Visão Geral

A partir desta implementação, o sistema possui dois métodos para criar administradores:

1. ✅ **Via Painel Desktop** (NOVO) - Recomendado
2. ✅ **Diretamente no Banco de Dados** - Para casos especiais

## 📋 Método 1: Via Painel Desktop (Recomendado)

### Passo a Passo

1. **Inicie o Servidor**
   ```bash
   cd "DESKTOP VERSION/ServidorFinanza"
   java -cp bin MainServidor
   ```

2. **Inicie o Cliente Desktop**
   ```bash
   cd "DESKTOP VERSION/ClienteFinanza"
   java -cp bin MainCliente
   ```

3. **Na Tela de Login do Desktop**
   - Clique em "Registrar" ou "Cadastrar Novo Usuário"

4. **Preencha o Formulário de Registro**
   - Nome: Digite o nome do administrador
   - Email: Digite um email válido
   - Senha: Digite uma senha (mínimo 6 caracteres)
   - Clique em "Cadastrar"

5. **Resultado**
   - ✅ O usuário será criado automaticamente como **administrador**
   - ✅ Você será logado automaticamente no painel desktop
   - ✅ O usuário terá acesso a todas as funcionalidades administrativas

### Exemplo Visual

```
┌─────────────────────────────────────┐
│      Sistema Finanza - Desktop     │
├─────────────────────────────────────┤
│                                     │
│  Nome:     [João da Silva        ] │
│  Email:    [joao@empresa.com     ] │
│  Senha:    [••••••••             ] │
│                                     │
│           [ Cadastrar ]             │
│                                     │
└─────────────────────────────────────┘
```

**Resultado no banco de dados:**
```sql
INSERT INTO usuario (nome, email, senha_hash, tipo_usuario)
VALUES ('João da Silva', 'joao@empresa.com', 'hash...', 'admin');
```

## 🗄️ Método 2: Diretamente no Banco de Dados

Use este método quando:
- Precisa criar o primeiro administrador do sistema
- Precisa fazer manutenção emergencial
- Precisa converter usuário existente em administrador

### Opção A: Criar Novo Administrador

```sql
-- Conectar ao banco
mysql -u root -p finanza_db

-- Criar administrador
INSERT INTO usuario (nome, email, senha_hash, tipo_usuario) 
VALUES (
    'Administrador Principal',
    'admin@sistema.com',
    '$2a$10$...hash_da_senha...',  -- Use SecurityUtil.hashSenha() ou bcrypt
    'admin'
);
```

### Opção B: Promover Usuário Existente

```sql
-- Ver usuários atuais
SELECT id, nome, email, tipo_usuario FROM usuario;

-- Promover usuário específico
UPDATE usuario 
SET tipo_usuario = 'admin' 
WHERE id = 5;  -- Substitua pelo ID desejado

-- Ou por email
UPDATE usuario 
SET tipo_usuario = 'admin' 
WHERE email = 'usuario@exemplo.com';
```

### Opção C: Rebaixar Admin para Usuário

```sql
UPDATE usuario 
SET tipo_usuario = 'usuario' 
WHERE id = 3;  -- Substitua pelo ID do admin
```

## 🔍 Verificar Tipo de Usuário

### No Banco de Dados

```sql
-- Ver todos os usuários e seus tipos
SELECT 
    id, 
    nome, 
    email, 
    tipo_usuario,
    data_criacao
FROM usuario
ORDER BY tipo_usuario, nome;

-- Ver apenas administradores
SELECT id, nome, email 
FROM usuario 
WHERE tipo_usuario = 'admin';

-- Ver apenas usuários comuns
SELECT id, nome, email 
FROM usuario 
WHERE tipo_usuario = 'usuario';
```

### Via Aplicação Desktop

1. Faça login como administrador
2. Acesse "Gerenciar Usuários"
3. Você verá a lista de todos os usuários com seus tipos

## ⚠️ Importante: Diferença Desktop vs Mobile

### Desktop (Cria Administradores)
```
✅ Registro via Desktop → tipo_usuario = 'admin'
✅ Pode acessar painel administrativo
✅ Pode gerenciar todos os usuários
✅ Pode ver dados de todos os usuários
❌ NÃO pode acessar o app mobile
```

### Mobile (Cria Usuários Comuns)
```
✅ Registro via Mobile → tipo_usuario = 'usuario'
✅ Pode acessar aplicativo mobile
✅ Pode gerenciar seus próprios dados
❌ NÃO pode acessar painel desktop
❌ NÃO pode ver dados de outros usuários
```

## 🔒 Segurança

### Boas Práticas

1. **Limite o Número de Admins**
   - Crie apenas os administradores necessários
   - Não transforme todos os usuários em admins

2. **Senhas Fortes**
   - Use senhas com pelo menos 8 caracteres
   - Combine letras, números e símbolos
   - Não use senhas óbvias

3. **Auditoria**
   - Monitore criações de administradores
   - Revise periodicamente a lista de admins
   ```sql
   SELECT 
       nome, 
       email, 
       data_criacao 
   FROM usuario 
   WHERE tipo_usuario = 'admin'
   ORDER BY data_criacao DESC;
   ```

4. **Separação de Funções**
   - Administradores: Gerenciamento via Desktop
   - Usuários: Operações diárias via Mobile
   - Não misture as responsabilidades

## 📊 Cenários de Uso

### Cenário 1: Primeira Instalação

```
1. Instalar banco de dados (script_inicial.sql)
   → Cria admin padrão: admin@finanza.com
   
2. Iniciar servidor

3. Fazer login no desktop com admin padrão

4. Criar administradores adicionais via desktop
```

### Cenário 2: Adicionar Novo Administrador

```
1. Login no desktop com conta admin existente

2. Ir para tela de registro

3. Preencher dados do novo administrador

4. Sistema cria automaticamente como admin
```

### Cenário 3: Converter Usuário em Admin

```
1. Identificar ID do usuário no banco

2. Executar SQL:
   UPDATE usuario SET tipo_usuario = 'admin' WHERE id = X;

3. Usuário agora pode acessar desktop
```

## 🧪 Testar a Funcionalidade

### Teste Rápido

1. **Criar admin via desktop**
   ```bash
   # Iniciar servidor
   cd "DESKTOP VERSION/ServidorFinanza"
   java -cp bin MainServidor
   
   # Em outro terminal, executar teste
   java test_register_admin
   ```

2. **Verificar no banco**
   ```sql
   SELECT * FROM usuario 
   WHERE email LIKE '%test.com' 
   ORDER BY data_criacao DESC 
   LIMIT 1;
   ```
   
   Deve mostrar `tipo_usuario = 'admin'`

### Teste Completo

Execute o arquivo de teste:
```bash
cd "DESKTOP VERSION/ServidorFinanza"
java test_register_admin
```

Resultado esperado:
```
✓ SUCCESS: Desktop registration created admin user
✓ SUCCESS: Mobile registration created regular user
✓ SUCCESS: Backward compatible registration works
```

## ❓ Perguntas Frequentes

### P: Posso criar admin pelo app mobile?
**R:** Não. Por segurança, o app mobile só cria usuários comuns. Admins devem ser criados via desktop ou banco de dados.

### P: O que acontece se um admin tentar acessar o mobile?
**R:** O sistema bloqueia com mensagem: "Acesso negado. Administradores não podem acessar o aplicativo mobile."

### P: Posso ter um usuário que acessa desktop E mobile?
**R:** Não. Por design, administradores acessam apenas desktop e usuários apenas mobile. É uma separação de responsabilidades.

### P: Como resetar senha de um admin?
**R:** Via banco de dados ou via desktop (se você for outro admin):
```sql
UPDATE usuario 
SET senha_hash = '[novo_hash]' 
WHERE id = [id_do_admin];
```

### P: Perdi acesso ao admin. Como recuperar?
**R:** Crie um novo admin via banco de dados:
```sql
INSERT INTO usuario (nome, email, senha_hash, tipo_usuario)
VALUES ('Admin Recuperação', 'recovery@admin.com', '[hash]', 'admin');
```

## 📚 Documentação Relacionada

- **[GUIA_RAPIDO_ADMIN_USER.md](../GUIA_RAPIDO_ADMIN_USER.md)** - Guia geral do sistema admin/usuário
- **[ALTERACOES_REGISTRO_ADMIN.md](ALTERACOES_REGISTRO_ADMIN.md)** - Detalhes técnicos da implementação
- **[banco/README_MIGRACAO.md](banco/README_MIGRACAO.md)** - Migração de banco de dados

## 🆘 Suporte

Em caso de problemas:
1. Verifique se o servidor está rodando
2. Confirme que o banco tem a coluna `tipo_usuario`
3. Execute o teste `test_register_admin.java`
4. Consulte os logs do servidor
5. Verifique a documentação relacionada

---

**Dica:** Mantenha um backup regular da tabela `usuario` para facilitar recuperação em caso de problemas.
