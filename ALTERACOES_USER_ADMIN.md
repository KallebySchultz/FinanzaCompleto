# Alterações - Diferenciação entre Usuário e Admin

## Problema Resolvido

**Situação Anterior:**
- Todos os usuários eram iguais, sem distinção entre admin e usuário comum
- Administradores podiam fazer login no aplicativo mobile
- Usuários comuns podiam fazer login no painel administrativo desktop

**Situação Atual:**
- Sistema diferencia claramente entre administradores e usuários comuns
- Administradores têm acesso **exclusivo** ao painel desktop
- Usuários comuns têm acesso **exclusivo** ao aplicativo mobile
- Tentativas de acesso cruzado são bloqueadas com mensagem clara

## Arquivos Modificados

### 1. Banco de Dados

#### `DESKTOP VERSION/banco/script_inicial.sql`
- Adicionado campo `tipo_usuario ENUM('admin', 'usuario')` na tabela `usuario`
- Criados dois usuários de teste:
  - Admin: `admin@finanza.com` (tipo_usuario='admin')
  - Usuário: `teste1@gmail.com` (tipo_usuario='usuario')

#### `DESKTOP VERSION/banco/migration_add_tipo_usuario.sql` (NOVO)
- Script de migração para bancos existentes
- Adiciona coluna `tipo_usuario` sem quebrar dados existentes
- Define primeiro usuário como admin automaticamente

#### `DESKTOP VERSION/banco/README_MIGRACAO.md` (NOVO)
- Documentação completa do processo de migração
- Guia de testes
- FAQ e troubleshooting

### 2. Servidor (ServidorFinanza)

#### `src/model/Usuario.java`
- Adicionado campo `tipoUsuario`
- Adicionadas constantes `TIPO_ADMIN` e `TIPO_USUARIO`
- Adicionados métodos `isAdmin()` e `isUsuario()`
- Atualizado construtor para suportar tipo de usuário

#### `src/dao/UsuarioDAO.java`
- Método `inserir()` atualizado para incluir `tipo_usuario`
- Método `mapResultSetToUsuario()` atualizado para ler `tipo_usuario` do banco

#### `src/server/Protocol.java`
- Adicionada constante `STATUS_ACCESS_DENIED` para bloqueio de acesso

#### `src/server/ClientHandler.java`
- Método `processarLogin()` modificado:
  - Recebe parâmetro adicional: tipo de cliente ('admin' ou 'mobile')
  - Valida se tipo de usuário corresponde ao tipo de cliente
  - Retorna `ACCESS_DENIED` se houver incompatibilidade
  - Inclui `tipo_usuario` na resposta de login
- Método `processarRegistro()` modificado:
  - Novos usuários sempre criados como 'usuario' (segurança)
  - Admin só pode ser criado via banco de dados

### 3. Cliente Desktop (ClienteFinanza)

#### `src/model/Usuario.java`
- Adicionado campo `tipoUsuario`
- Adicionadas constantes e métodos similares ao servidor
- Atualizado construtor

#### `src/controller/AuthController.java`
- Adicionada constante `STATUS_ACCESS_DENIED`
- Método `login()` modificado:
  - Envia "admin" como tipo de cliente no comando LOGIN
  - Recebe e processa `tipo_usuario` na resposta
  - Trata erro `ACCESS_DENIED` adequadamente

### 4. Aplicativo Mobile (Android)

#### `app/src/main/java/com/example/finanza/network/ServerClient.java`
- Método `login()` modificado:
  - Envia "mobile" como tipo de cliente no comando LOGIN
  - Comentário explicativo adicionado

## Fluxo de Autenticação

### Desktop Admin (Sucesso)
```
Cliente Desktop → LOGIN|admin@finanza.com|senha|admin
Servidor        → Valida: usuário tem tipo_usuario='admin' ✓
Servidor        → OK|1;Administrador;admin@finanza.com;admin
Cliente Desktop → Login bem-sucedido ✓
```

### Desktop com Usuário Comum (Bloqueado)
```
Cliente Desktop → LOGIN|teste1@gmail.com|senha|admin
Servidor        → Valida: usuário tem tipo_usuario='usuario' ✗
Servidor        → ACCESS_DENIED|Acesso negado. Apenas administradores...
Cliente Desktop → Exibe erro de acesso negado ✗
```

### Mobile com Usuário Comum (Sucesso)
```
App Mobile     → LOGIN|teste1@gmail.com|senha|mobile
Servidor       → Valida: usuário tem tipo_usuario='usuario' ✓
Servidor       → OK|2;Usuário Teste;teste1@gmail.com;usuario
App Mobile     → Login bem-sucedido ✓
```

### Mobile com Admin (Bloqueado)
```
App Mobile     → LOGIN|admin@finanza.com|senha|mobile
Servidor       → Valida: usuário tem tipo_usuario='admin' ✗
Servidor       → ACCESS_DENIED|Acesso negado. Administradores não podem...
App Mobile     → Exibe erro de acesso negado ✗
```

## Protocolo de Comunicação

### Comando LOGIN (Atualizado)
```
Sintaxe: LOGIN|email|senha|tipo_cliente

Parâmetros:
- email: email do usuário
- senha: senha do usuário
- tipo_cliente: 'admin' (desktop) ou 'mobile' (app)
```

### Resposta LOGIN (Atualizada)
```
Sucesso: OK|userId;nome;email;tipo_usuario
Erro de credenciais: INVALID_CREDENTIALS|Email ou senha inválidos
Erro de acesso: ACCESS_DENIED|Mensagem explicativa
```

## Compatibilidade

### Retrocompatibilidade
- Servidor aceita login sem tipo_cliente (assume 'mobile')
- Cliente desktop aceita resposta sem tipo_usuario (assume 'admin')
- Garante funcionamento básico mesmo com versões antigas

### Forward Compatibility
- Novos campos são opcionais no protocolo
- Sistema funciona com ou sem o campo adicional
- Facilita atualizações graduais

## Segurança

### Regras Implementadas

1. **Criação de Admin:**
   - Apenas via banco de dados diretamente
   - Registro via app/desktop sempre cria usuário comum
   - Previne escalação não autorizada de privilégios

2. **Validação no Servidor:**
   - Verificação de tipo ocorre no servidor (não confia no cliente)
   - Dupla verificação: tipo de usuário vs tipo de cliente
   - Mensagens de erro claras mas não revelam informações sensíveis

3. **Separação de Contextos:**
   - Admin não pode acessar dados via mobile
   - Usuários comuns não podem acessar painel admin
   - Cada contexto tem suas próprias funcionalidades

## Testes Realizados

✅ Compilação do servidor Java bem-sucedida
✅ Compilação do cliente desktop Java bem-sucedida
✅ Sintaxe do código mobile Android validada
✅ Scripts SQL validados

## Como Usar

### Para Novos Projetos
1. Use `script_inicial.sql` para criar o banco
2. Compile e execute o servidor
3. Use as credenciais padrão para testar

### Para Projetos Existentes
1. Execute `migration_add_tipo_usuario.sql`
2. Verifique os tipos de usuário no banco
3. Ajuste manualmente se necessário
4. Recompile servidor e cliente
5. Teste com ambos os tipos de usuário

## Próximos Passos Sugeridos

- [ ] Adicionar testes automatizados de autenticação
- [ ] Implementar registro de audit log para tentativas de acesso
- [ ] Adicionar interface no painel admin para promover/rebaixar usuários
- [ ] Implementar recuperação de senha com distinção de tipo
- [ ] Adicionar permissões granulares além de admin/usuario

## Documentação Adicional

- `DESKTOP VERSION/banco/README_MIGRACAO.md` - Guia completo de migração
- `DESKTOP VERSION/banco/migration_add_tipo_usuario.sql` - Script de migração
- `DESKTOP VERSION/banco/script_inicial.sql` - Schema completo atualizado
