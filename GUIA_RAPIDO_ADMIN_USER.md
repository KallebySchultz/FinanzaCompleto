# Guia Rápido - Sistema de Autenticação Admin/Usuário

## 🎯 Resumo em 30 Segundos

O sistema Finanza agora diferencia **administradores** de **usuários comuns**:

- 👨‍💼 **Admin** → Acessa apenas o **painel desktop**
- 👤 **Usuário** → Acessa apenas o **app mobile**

## 🚀 Quick Start

### 1. Configurar Banco de Dados

**Banco Novo:**
```bash
mysql -u root -p < "DESKTOP VERSION/banco/script_inicial.sql"
```

**Banco Existente:**
```bash
mysql -u root -p finanza_db < "DESKTOP VERSION/banco/migration_add_tipo_usuario.sql"
```

### 2. Credenciais Padrão

| Tipo | Email | Senha | Onde Usar |
|------|-------|-------|-----------|
| Admin | `admin@finanza.com` | `admin123` | Desktop |
| Usuário | `teste1@gmail.com` | `teste123` | Mobile |

### 3. Testar

**Desktop (deve funcionar):**
```
Email: admin@finanza.com
Senha: admin123
Resultado: ✓ Login bem-sucedido
```

**Desktop com usuário comum (deve bloquear):**
```
Email: teste1@gmail.com
Senha: teste123
Resultado: ✗ "Acesso negado. Apenas administradores podem acessar o painel desktop."
```

**Mobile (deve funcionar):**
```
Email: teste1@gmail.com
Senha: teste123
Resultado: ✓ Login bem-sucedido
```

**Mobile com admin (deve bloquear):**
```
Email: admin@finanza.com
Senha: admin123
Resultado: ✗ "Acesso negado. Administradores não podem acessar o aplicativo mobile."
```

## 🔧 Operações Comuns

### Criar Novo Admin

```sql
-- Método 1: Criar usuário diretamente como admin
INSERT INTO usuario (nome, email, senha_hash, tipo_usuario) 
VALUES ('Novo Admin', 'novo@admin.com', 'hash_da_senha', 'admin');

-- Método 2: Promover usuário existente
UPDATE usuario SET tipo_usuario = 'admin' WHERE id = 5;
```

### Rebaixar Admin para Usuário

```sql
UPDATE usuario SET tipo_usuario = 'usuario' WHERE id = 1;
```

### Listar Todos os Usuários e Tipos

```sql
SELECT id, nome, email, tipo_usuario FROM usuario;
```

### Verificar Tipo de um Usuário

```sql
SELECT nome, tipo_usuario FROM usuario WHERE email = 'usuario@exemplo.com';
```

## 📝 Protocolo de Comunicação

### Desktop → Servidor
```
LOGIN|email|senha|admin
```

### Mobile → Servidor
```
LOGIN|email|senha|mobile
```

### Servidor → Cliente (Sucesso)
```
OK|userId;nome;email;tipo_usuario
```

### Servidor → Cliente (Bloqueio)
```
ACCESS_DENIED|Mensagem de erro
```

## 🛠️ Desenvolvimento

### Adicionar Validação de Admin em Novo Endpoint

```java
// No ClientHandler.java
private String processarNovoComando(String[] partes) {
    // Verifica se usuário está logado
    if (usuarioLogado == null) {
        return Protocol.createErrorResponse("Usuário não autenticado");
    }
    
    // Verifica se é admin (se necessário)
    if (!usuarioLogado.isAdmin()) {
        return Protocol.createResponse(Protocol.STATUS_ACCESS_DENIED, 
            "Apenas administradores podem executar esta ação");
    }
    
    // Lógica do comando...
}
```

### Verificar Tipo de Usuário no Cliente

```java
// Desktop Client
if (usuario.isAdmin()) {
    // Mostrar funcionalidades de admin
} else {
    // Bloquear acesso
}
```

### Verificar Tipo no Mobile (Kotlin/Java)

```kotlin
// Android
if (usuario.tipoUsuario == "admin") {
    // Este caso não deveria acontecer no mobile
    // Mas se acontecer, trate apropriadamente
}
```

## ⚠️ Avisos Importantes

### Segurança

1. **Nunca confie no cliente** - Sempre valide no servidor
2. **Hash as senhas** - Use bcrypt ou similar
3. **Admin só via banco** - Não permita criação via UI

### Migração

1. **Backup primeiro** - Sempre faça backup antes de migrar
2. **Teste em dev** - Teste a migração em ambiente de desenvolvimento
3. **Verifique resultados** - Confirme que usuários foram classificados corretamente

### Compatibilidade

- ✅ Servidor backward-compatible (aceita login sem tipo)
- ✅ Cliente backward-compatible (funciona sem tipo_usuario)
- ✅ Migração não-destrutiva (adiciona coluna sem apagar dados)

## 📚 Documentação Completa

- `ALTERACOES_USER_ADMIN.md` - Changelog detalhado
- `DESKTOP VERSION/banco/README_MIGRACAO.md` - Guia de migração completo
- `README.md` - Documentação geral do projeto

## 🐛 Troubleshooting

### "Access Denied" no Desktop
- **Causa:** Usuário não é admin
- **Solução:** Verificar/atualizar tipo no banco de dados

### Admin consegue logar no Mobile
- **Causa:** Cliente mobile não está enviando tipo "mobile"
- **Solução:** Verificar código em `ServerClient.java`

### Usuário consegue logar no Desktop
- **Causa:** Cliente desktop não está enviando tipo "admin"
- **Solução:** Verificar código em `AuthController.java`

### Coluna tipo_usuario não existe
- **Causa:** Migração não foi executada
- **Solução:** Executar `migration_add_tipo_usuario.sql`

## 💡 Dicas

- Use `tipo_usuario='admin'` apenas para quem realmente precisa
- Monitore tentativas de acesso negado (potencial ataque)
- Considere adicionar rate limiting para tentativas de login
- Implemente 2FA para contas admin

## 📞 Suporte

Em caso de dúvidas:
1. Consulte a documentação completa
2. Verifique os logs do servidor
3. Execute os testes manuais documentados
4. Entre em contato com a equipe de desenvolvimento
