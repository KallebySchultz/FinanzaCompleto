# 📋 Referência Rápida - Registro de Administradores

## 🎯 O Que Mudou?

```
ANTES: Todos os registros criavam usuários comuns
AGORA: Desktop cria admins, Mobile cria usuários
```

## 🚀 Como Usar

### Criar Admin (Desktop)
1. Abrir cliente desktop
2. Clicar em "Registrar"
3. Preencher dados
4. **Resultado: Administrador criado ✅**

### Criar Usuário (Mobile)
1. Abrir app mobile
2. Clicar em "Cadastrar"
3. Preencher dados
4. **Resultado: Usuário comum criado ✅**

## 💻 Protocolo

### Desktop → Servidor
```
REGISTER|nome|email|senha|admin
         ↑ Cria como administrador
```

### Mobile → Servidor
```
REGISTER|nome|email|senha|mobile
         ↑ Cria como usuário comum
```

### Servidor → Cliente
```
OK|id;nome;email;tipo_usuario
                  ↑ "admin" ou "usuario"
```

## 🔍 Verificar no Banco

```sql
-- Ver tipo do usuário
SELECT nome, email, tipo_usuario FROM usuario WHERE email = 'teste@email.com';

-- Listar todos os admins
SELECT * FROM usuario WHERE tipo_usuario = 'admin';

-- Contar usuários por tipo
SELECT tipo_usuario, COUNT(*) FROM usuario GROUP BY tipo_usuario;
```

## 🛠️ Operações Comuns

### Promover para Admin
```sql
UPDATE usuario SET tipo_usuario = 'admin' WHERE id = 5;
```

### Rebaixar para Usuário
```sql
UPDATE usuario SET tipo_usuario = 'usuario' WHERE id = 3;
```

### Criar Admin Manual
```sql
INSERT INTO usuario (nome, email, senha_hash, tipo_usuario)
VALUES ('Admin', 'admin@test.com', 'hash...', 'admin');
```

## ✅ Teste Rápido

```bash
# Compile e execute
cd "DESKTOP VERSION/ServidorFinanza"
javac test_register_admin.java
java test_register_admin

# Deve mostrar:
# ✓ Desktop registration created admin user
# ✓ Mobile registration created regular user
```

## 🔒 Regras de Acesso

| Tipo      | Desktop | Mobile |
|-----------|---------|--------|
| Admin     | ✅ Sim  | ❌ Não |
| Usuário   | ❌ Não  | ✅ Sim |

## 📚 Documentação Completa

- **Guia do Usuário**: [COMO_CRIAR_ADMIN.md](COMO_CRIAR_ADMIN.md)
- **Detalhes Técnicos**: [ALTERACOES_REGISTRO_ADMIN.md](ALTERACOES_REGISTRO_ADMIN.md)
- **Guia Geral**: [../GUIA_RAPIDO_ADMIN_USER.md](../GUIA_RAPIDO_ADMIN_USER.md)

## 🐛 Troubleshooting

### "Usuário criado como 'usuario' em vez de 'admin'"
→ Verifique se desktop está enviando parâmetro "admin"

### "ACCESS_DENIED ao tentar logar"
→ Verifique tipo do usuário no banco: `SELECT tipo_usuario FROM usuario WHERE email = '...'`

### Teste falha
→ Certifique-se que o servidor está rodando na porta 8080

## 💡 Dica

Para desenvolvimento rápido, use credenciais padrão:
- **Admin**: `admin@finanza.com` / `admin123`
- **Usuário**: `teste1@gmail.com` / `teste123`

---

**Mantenha esta página aberta durante o desenvolvimento!** 🚀
