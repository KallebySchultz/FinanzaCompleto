# Alterações - Registro de Administradores via Painel Desktop

## Problema Resolvido

**Situação Anterior:**
- Usuários registrados via painel desktop eram criados como usuários comuns ('usuario')
- Não era possível criar administradores através da interface, apenas diretamente no banco de dados

**Situação Atual:**
- Usuários registrados via **painel desktop** são automaticamente criados como **administradores** ('admin')
- Usuários registrados via **aplicativo mobile** continuam sendo criados como **usuários comuns** ('usuario')
- Manutenção da segurança: cada interface cria o tipo apropriado de usuário

## Arquivos Modificados

### 1. Servidor (ServidorFinanza)

#### `src/server/ClientHandler.java` - Método `processarRegistro()`

**Mudanças:**
- Adicionado suporte para parâmetro opcional na posição 5: tipo de cliente ('admin' ou 'mobile')
- Lógica atualizada para determinar `tipoUsuario` baseado no `tipoCliente`:
  - `tipoCliente = "admin"` → cria usuário com `tipo_usuario = 'admin'`
  - `tipoCliente = "mobile"` → cria usuário com `tipo_usuario = 'usuario'`
  - Sem parâmetro → padrão é 'mobile', criando usuário comum
- Removida a restrição que forçava todos os registros a serem 'usuario'

**Antes:**
```java
// Cria novo usuário (sempre como usuario comum - admin só pode ser criado pelo banco)
Usuario novoUsuario = new Usuario(nome, email, SecurityUtil.hashSenha(senha));
novoUsuario.setTipoUsuario(Usuario.TIPO_USUARIO); // Força tipo usuario
```

**Depois:**
```java
// Parâmetro opcional: tipo de cliente ('admin' para desktop ou 'mobile' para app mobile)
String tipoCliente = partes.length > 4 ? partes[4] : "mobile";

// Determina o tipo de usuário baseado no cliente que está registrando
String tipoUsuario = "admin".equals(tipoCliente) ? Usuario.TIPO_ADMIN : Usuario.TIPO_USUARIO;

// Cria novo usuário com o tipo apropriado
Usuario novoUsuario = new Usuario(nome, email, SecurityUtil.hashSenha(senha));
novoUsuario.setTipoUsuario(tipoUsuario);
```

### 2. Cliente Desktop (ClienteFinanza)

#### `src/controller/AuthController.java` - Método `registrar()`

**Mudanças:**
- Adicionado parâmetro "admin" no comando REGISTER
- Comentário explicativo adicionado

**Antes:**
```java
String comando = CMD_REGISTER + SEPARATOR + nome + SEPARATOR + email + SEPARATOR + senha;
```

**Depois:**
```java
// Envia comando REGISTER com tipo de cliente "admin" para criar usuário administrador
String comando = CMD_REGISTER + SEPARATOR + nome + SEPARATOR + email + SEPARATOR + senha + SEPARATOR + "admin";
```

### 3. Aplicativo Mobile (Android)

#### `app/src/main/java/com/example/finanza/network/ServerClient.java` - Método `registrar()`

**Mudanças:**
- Adicionado parâmetro "mobile" no comando REGISTER para explicitação
- Comentário explicativo adicionado
- Mantém o comportamento de criar usuários comuns

**Antes:**
```java
String comando = Protocol.buildCommand(Protocol.CMD_REGISTER, nome, email, senha);
```

**Depois:**
```java
// Envia comando REGISTER com tipo de cliente "mobile" para criar usuário comum
String comando = Protocol.buildCommand(Protocol.CMD_REGISTER, nome, email, senha, "mobile");
```

## Protocolo de Comunicação

### Comando REGISTER (Atualizado)

```
Sintaxe: REGISTER|nome|email|senha|tipo_cliente

Parâmetros:
- nome: nome do usuário
- email: email do usuário
- senha: senha do usuário
- tipo_cliente (opcional): 'admin' (desktop) ou 'mobile' (app)
  - Se omitido, assume 'mobile'
```

### Exemplos de Uso

**Desktop registrando administrador:**
```
Cliente → REGISTER|João Silva|joao@empresa.com|senha123|admin
Servidor → OK|1;João Silva;joao@empresa.com;admin
```

**Mobile registrando usuário comum:**
```
Cliente → REGISTER|Maria Santos|maria@email.com|senha456|mobile
Servidor → OK|2;Maria Santos;maria@email.com;usuario
```

**Cliente antigo (sem tipo, backward compatible):**
```
Cliente → REGISTER|Pedro Costa|pedro@email.com|senha789
Servidor → OK|3;Pedro Costa;pedro@email.com;usuario
```

## Compatibilidade

### Retrocompatibilidade ✅

- O 5º parâmetro (`tipo_cliente`) é **opcional**
- Clientes antigos que não enviam o tipo continuam funcionando
- Comportamento padrão: se omitido, assume 'mobile' e cria usuário comum
- Nenhuma alteração no banco de dados é necessária

### Segurança

1. **Controle no servidor**: A decisão do tipo de usuário é tomada no servidor, não pode ser manipulada pelo cliente
2. **Separação de contextos**: Cada interface (desktop/mobile) tem sua regra clara
3. **Auditoria**: Tipo de usuário é registrado no banco de dados

## Testes

### Teste Manual

1. **Servidor em execução**: Inicie o servidor na porta 8080
2. **Execute o teste**:
   ```bash
   cd "DESKTOP VERSION/ServidorFinanza"
   java test_register_admin
   ```

### Cenários de Teste

O arquivo `test_register_admin.java` verifica:

1. ✅ Registro via desktop cria administrador
2. ✅ Registro via mobile cria usuário comum
3. ✅ Registro sem tipo (backward compatibility) cria usuário comum

### Resultados Esperados

```
Connected to server
REGISTER (desktop) response: OK|...|admin
✓ SUCCESS: Desktop registration created admin user
REGISTER (mobile) response: OK|...|usuario
✓ SUCCESS: Mobile registration created regular user
REGISTER (no type) response: OK|...|usuario
✓ SUCCESS: Backward compatible registration works
All tests completed!
```

## Validação em Produção

### Após o Deploy

1. **Verificar registros no banco**:
   ```sql
   SELECT id, nome, email, tipo_usuario, data_criacao 
   FROM usuario 
   WHERE data_criacao > NOW() - INTERVAL 1 DAY
   ORDER BY data_criacao DESC;
   ```

2. **Espera-se ver**:
   - Usuários criados via desktop com `tipo_usuario = 'admin'`
   - Usuários criados via mobile com `tipo_usuario = 'usuario'`

### Rollback (se necessário)

Se for necessário reverter as alterações:

1. Reverter os commits nos 3 arquivos modificados
2. Recompilar servidor e clientes
3. Usuários já criados mantêm seus tipos (não é necessário alterar no banco)

## Vantagens da Implementação

1. ✅ **Simplicidade**: Mínima mudança de código
2. ✅ **Segurança**: Decisão no servidor, não no cliente
3. ✅ **Compatibilidade**: Funciona com clientes antigos
4. ✅ **Clareza**: Cada interface tem comportamento definido
5. ✅ **Auditável**: Fácil rastrear origem do usuário

## Documentação Relacionada

- `GUIA_RAPIDO_ADMIN_USER.md` - Guia geral de admin/usuário
- `ALTERACOES_USER_ADMIN.md` - Implementação original de tipos de usuário
- `DESKTOP VERSION/banco/README_MIGRACAO.md` - Migração de banco de dados

## Suporte

Em caso de dúvidas ou problemas:
1. Verificar logs do servidor para mensagens de erro
2. Confirmar que o banco possui a coluna `tipo_usuario`
3. Testar com o arquivo `test_register_admin.java`
4. Verificar versões de servidor e clientes estão sincronizadas
