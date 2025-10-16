# 🚀 Guia Rápido - Atualização do Sistema Finanza

## O que mudou?

### ✅ 1. Separação entre Usuários e Administradores

**Antes:**
- Todos os usuários tinham acesso a tudo
- Não havia distinção entre admin e usuário regular

**Agora:**
- **Usuários Regulares**: Usam APENAS o aplicativo mobile (Android)
- **Administradores**: Usam APENAS o painel desktop (Java)

### ✅ 2. Sincronização Offline Melhorada

**Antes:**
- Erro "não está logado" ao tentar sincronizar offline
- Botão de sync desabilitado quando sem conexão
- Dados criados offline não sincronizavam

**Agora:**
- Funciona perfeitamente offline
- Botão de sync sempre disponível
- Dados criados offline sincronizam automaticamente quando reconectar

---

## 📱 Para Usuários do Mobile

### Como usar agora:

1. **Faça login normalmente** com sua conta de usuário
   - Se você é administrador, NÃO use o app mobile
   - Use o painel desktop ao invés

2. **Trabalhe offline sem preocupações**
   - Crie transações, contas, categorias normalmente
   - Tudo é salvo localmente no seu celular

3. **Sincronize quando quiser**
   - Vá em Menu → Configurações
   - Clique no botão "🔄 Sync"
   - Funciona mesmo offline (salva local)
   - Quando reconectar, seus dados vão para o servidor

### Estados da Conexão:

- **🟢 Conectado ao servidor**: Tudo sincronizando em tempo real
- **🔴 Modo offline**: Funcionando localmente, sync pendente

---

## 🖥️ Para Administradores do Desktop

### Como usar agora:

1. **Use suas credenciais de admin**
   - Email: `admin@finanza.com`
   - Senha: `admin` (MUDE ISSO!)

2. **Acesso exclusivo ao painel**
   - Gerenciar todos os usuários
   - Ver todas as transações
   - Administrar o sistema

3. **NÃO use o app mobile**
   - Conta de admin não funciona no mobile
   - É proposital para segurança

---

## 🔧 Para quem vai atualizar o sistema

### Passo 1: Atualizar o Banco de Dados (MySQL)

```bash
# 1. FAÇA BACKUP PRIMEIRO!
mysqldump -u root -p finanza_db > backup_$(date +%Y%m%d).sql

# 2. Execute a migração
mysql -u root -p finanza_db < "DESKTOP VERSION/banco/migration_add_role.sql"

# 3. Verifique se funcionou
mysql -u root -p finanza_db -e "SELECT id, nome, email, role FROM usuario;"
```

### Passo 2: Atualizar o App Mobile

O app mobile atualiza o banco automaticamente quando você:
- Desinstalar e reinstalar o app, OU
- Atualizar para a nova versão

### Passo 3: Criar Usuário Admin

Se você ainda não tem um admin:

```sql
-- No MySQL
mysql -u root -p finanza_db

-- Execute:
INSERT INTO usuario (nome, email, senha_hash, role) VALUES 
('Seu Nome', 'seuemail@exemplo.com', 'hash_da_senha', 'admin');
```

⚠️ **IMPORTANTE:** Troque a senha padrão do admin!

---

## 🧪 Testando se está funcionando

### Teste 1: Acesso ao Mobile (como usuário)
1. ✅ Crie/use conta de usuário normal
2. ✅ Faça login no app mobile
3. ✅ Deve funcionar normalmente

### Teste 2: Acesso ao Desktop (como admin)
1. ✅ Use conta de administrador
2. ✅ Faça login no painel desktop
3. ✅ Deve funcionar normalmente

### Teste 3: Bloqueio de Admin no Mobile
1. ✅ Tente fazer login no mobile com conta admin
2. ✅ Deve mostrar: "Acesso negado. Administradores devem usar o painel desktop."

### Teste 4: Bloqueio de User no Desktop
1. ✅ Tente fazer login no desktop com conta de usuário
2. ✅ Deve mostrar: "Acesso negado. Apenas administradores podem acessar o painel desktop."

### Teste 5: Sincronização Offline
1. ✅ Login no mobile
2. ✅ Ative modo avião (ou desligue servidor)
3. ✅ Crie algumas transações
4. ✅ Vá em Configurações
5. ✅ Botão "🔄 Sync" deve estar habilitado
6. ✅ Clique em Sync
7. ✅ Mensagem: "Modo offline - dados salvos localmente"
8. ✅ Desative modo avião
9. ✅ Clique em Sync novamente
10. ✅ Dados devem aparecer no servidor

---

## ❓ Perguntas Frequentes

### P: Meus dados antigos vão sumir?
**R:** Não! A migração preserva todos os dados. Apenas adiciona o campo "role".

### P: Preciso criar conta nova?
**R:** Não. Suas contas existentes continuam funcionando. Elas recebem role='user' automaticamente.

### P: Como transformo um usuário em admin?
**R:** No banco MySQL:
```sql
UPDATE usuario SET role = 'admin' WHERE email = 'email@exemplo.com';
```

### P: Como volto um admin para usuário normal?
**R:** No banco MySQL:
```sql
UPDATE usuario SET role = 'user' WHERE email = 'email@exemplo.com';
```

### P: E se eu esquecer a senha do admin?
**R:** Redefina no banco:
```sql
-- Senha 'admin' (use hash seguro em produção!)
UPDATE usuario 
SET senha_hash = 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=' 
WHERE email = 'admin@finanza.com';
```

### P: O app mobile funciona sem servidor?
**R:** Sim! Funciona 100% offline. Sincroniza quando reconectar.

### P: Posso ter vários admins?
**R:** Sim! Crie quantos precisar com role='admin'.

### P: Posso usar mesma conta no mobile e desktop?
**R:** Não. Contas de admin são exclusivas para desktop. Contas de usuário são exclusivas para mobile.

---

## 🐛 Problemas Comuns

### "Column role does not exist"
**Solução:** Execute o script de migração do banco.

### "Acesso negado" ao tentar logar
**Solução:** Verifique se você está usando o aplicativo certo:
- Admin → Desktop
- Usuário → Mobile

### Sync não funciona
**Solução:**
1. Verifique se está logado
2. Tente desconectar e reconectar
3. Verifique configurações do servidor

### Dados não aparecem após sync
**Solução:**
1. Verifique logs do servidor
2. Confirme que servidor está rodando
3. Teste conexão em Configurações

---

## 📞 Precisa de Ajuda?

1. **Documentação Completa:** Veja `ROLE_SYSTEM_DOCUMENTATION.md`
2. **Problemas técnicos:** Abra uma issue no GitHub
3. **Dúvidas:** Consulte o `USER_MANUAL.md`

---

**Versão:** 2.0.0  
**Data:** Outubro 2025  
**Autor:** Finanza Development Team
