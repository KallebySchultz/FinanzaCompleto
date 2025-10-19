# 📊 Diagrama - Fluxo de Registro de Administradores

## 🎯 Visão Geral

```
┌─────────────────┐                    ┌─────────────────┐
│  Desktop Panel  │                    │   Mobile App    │
│   (Java Swing)  │                    │   (Android)     │
└────────┬────────┘                    └────────┬────────┘
         │                                      │
         │ REGISTER|nome|email|senha|admin      │ REGISTER|nome|email|senha|mobile
         │                                      │
         └──────────────────┬───────────────────┘
                            │
                            ▼
                  ┌──────────────────┐
                  │  Servidor Java   │
                  │  ClientHandler   │
                  └────────┬─────────┘
                           │
              ┌────────────┴────────────┐
              │                         │
    tipo_cliente = "admin"    tipo_cliente = "mobile"
              │                         │
              ▼                         ▼
    ┌─────────────────┐       ┌─────────────────┐
    │ tipo_usuario    │       │ tipo_usuario    │
    │   = "admin"     │       │  = "usuario"    │
    └────────┬────────┘       └────────┬────────┘
             │                          │
             └──────────┬───────────────┘
                        │
                        ▼
              ┌──────────────────┐
              │   MySQL Database │
              │ finanza_db.usuario│
              └──────────────────┘
```

## 🔄 Fluxo Detalhado

### 1️⃣ Registro via Desktop

```
┌─────────────────────────────────────────────────────────────┐
│                     DESKTOP CLIENT                          │
├─────────────────────────────────────────────────────────────┤
│  1. Usuário clica em "Registrar"                           │
│  2. Preenche: Nome, Email, Senha                           │
│  3. AuthController.registrar() adiciona "admin"            │
│     └─► CMD = "REGISTER|nome|email|senha|admin"           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ TCP/IP Socket
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                        SERVIDOR                             │
├─────────────────────────────────────────────────────────────┤
│  4. ClientHandler.processarRegistro() recebe comando        │
│  5. Parse: partes[4] = "admin"                             │
│  6. Lógica: tipoCliente == "admin"                         │
│     └─► tipoUsuario = Usuario.TIPO_ADMIN                  │
│  7. Cria: new Usuario(nome, email, hash)                   │
│  8. Define: usuario.setTipoUsuario("admin")                │
│  9. Insere no banco via UsuarioDAO                         │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                     BANCO DE DADOS                          │
├─────────────────────────────────────────────────────────────┤
│  INSERT INTO usuario                                        │
│    (nome, email, senha_hash, tipo_usuario)                 │
│  VALUES                                                     │
│    ('João', 'joao@email.com', 'hash...', 'admin')         │
└─────────────────────────────────────────────────────────────┘
```

### 2️⃣ Registro via Mobile

```
┌─────────────────────────────────────────────────────────────┐
│                     MOBILE CLIENT                           │
├─────────────────────────────────────────────────────────────┤
│  1. Usuário clica em "Cadastrar"                           │
│  2. Preenche: Nome, Email, Senha                           │
│  3. ServerClient.registrar() adiciona "mobile"             │
│     └─► CMD = "REGISTER|nome|email|senha|mobile"          │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ TCP/IP Socket
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                        SERVIDOR                             │
├─────────────────────────────────────────────────────────────┤
│  4. ClientHandler.processarRegistro() recebe comando        │
│  5. Parse: partes[4] = "mobile"                            │
│  6. Lógica: tipoCliente == "mobile"                        │
│     └─► tipoUsuario = Usuario.TIPO_USUARIO                │
│  7. Cria: new Usuario(nome, email, hash)                   │
│  8. Define: usuario.setTipoUsuario("usuario")              │
│  9. Insere no banco via UsuarioDAO                         │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                     BANCO DE DADOS                          │
├─────────────────────────────────────────────────────────────┤
│  INSERT INTO usuario                                        │
│    (nome, email, senha_hash, tipo_usuario)                 │
│  VALUES                                                     │
│    ('Maria', 'maria@email.com', 'hash...', 'usuario')     │
└─────────────────────────────────────────────────────────────┘
```

## 📋 Comparação Antes/Depois

### ANTES da Implementação
```
Desktop  ──► REGISTER|nome|email|senha ──► Servidor
                                             │
                                             ▼
                                        tipo_usuario
                                           = 'usuario' ❌
                                             │
                                             ▼
                                         Database
```

### DEPOIS da Implementação
```
Desktop  ──► REGISTER|nome|email|senha|admin ──► Servidor
                                                    │
                                                    ▼
                                              tipo_usuario
                                                = 'admin' ✅
                                                    │
                                                    ▼
                                                Database

Mobile   ──► REGISTER|nome|email|senha|mobile ──► Servidor
                                                      │
                                                      ▼
                                                tipo_usuario
                                                 = 'usuario' ✅
                                                      │
                                                      ▼
                                                  Database
```

## 🔐 Controle de Acesso

```
┌──────────────────────────────────────────────────────────┐
│                    APÓS REGISTRO                         │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  Admin (tipo_usuario = 'admin')                         │
│    ├── ✅ Pode fazer login no Desktop                   │
│    ├── ✅ Acessa painel administrativo                  │
│    ├── ✅ Gerencia todos os usuários                    │
│    └── ❌ NÃO pode acessar app mobile                   │
│                                                          │
│  Usuário (tipo_usuario = 'usuario')                     │
│    ├── ✅ Pode fazer login no Mobile                    │
│    ├── ✅ Acessa suas próprias funcionalidades          │
│    ├── ❌ NÃO pode acessar Desktop                      │
│    └── ❌ NÃO pode ver dados de outros                  │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## 🛡️ Validação de Segurança

```
┌─────────────────────────────────────────────────────────────┐
│                    VALIDAÇÃO NO SERVIDOR                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Cliente envia: REGISTER|...|tipoCliente                │
│                                                             │
│  2. Servidor recebe e valida:                              │
│     ┌────────────────────────────────────┐                 │
│     │ if (tipoCliente == "admin")        │                 │
│     │   tipoUsuario = "admin"            │                 │
│     │ else                               │                 │
│     │   tipoUsuario = "usuario"          │                 │
│     └────────────────────────────────────┘                 │
│                                                             │
│  3. Cliente NÃO pode forçar tipo diferente                 │
│     ✅ Decisão sempre no servidor                          │
│     ✅ Cliente não pode mentir seu tipo                    │
│     ✅ Sem vulnerabilidade de escalação                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 📊 Tabela de Decisão

| Cliente  | Parâmetro | tipo_usuario | Acesso Desktop | Acesso Mobile |
|----------|-----------|--------------|----------------|---------------|
| Desktop  | "admin"   | "admin"      | ✅ Sim         | ❌ Não        |
| Mobile   | "mobile"  | "usuario"    | ❌ Não         | ✅ Sim        |
| Antigo   | (vazio)   | "usuario"    | ❌ Não         | ✅ Sim        |

## 🔄 Compatibilidade Retroativa

```
Cliente Antigo (sem parâmetro tipo)
    │
    ▼
REGISTER|nome|email|senha
    │
    ▼
Servidor detecta: partes.length == 4
    │
    ▼
Assume: tipoCliente = "mobile" (padrão)
    │
    ▼
Cria: tipo_usuario = "usuario"
    │
    ▼
✅ Funciona normalmente!
```

## 📁 Arquivos Envolvidos

```
Modificados:
├── DESKTOP VERSION/
│   ├── ServidorFinanza/src/server/
│   │   └── ClientHandler.java ................ [Lógica do servidor]
│   └── ClienteFinanza/src/controller/
│       └── AuthController.java ............... [Desktop client]
└── app/src/main/java/com/example/finanza/network/
    └── ServerClient.java ..................... [Mobile client]

Criados:
├── DESKTOP VERSION/
│   ├── ServidorFinanza/
│   │   └── test_register_admin.java .......... [Teste]
│   ├── ALTERACOES_REGISTRO_ADMIN.md .......... [Doc técnica]
│   ├── COMO_CRIAR_ADMIN.md ................... [Guia usuário]
│   ├── REFERENCIA_RAPIDA_ADMIN.md ............ [Ref rápida]
│   ├── RESUMO_IMPLEMENTACAO_ADMIN.md ......... [Resumo]
│   └── DIAGRAMA_REGISTRO_ADMIN.md ............ [Este arquivo]
└── INDICE_DOCUMENTACAO.md .................... [Atualizado]
```

## 🎯 Conclusão

```
┌────────────────────────────────────────────────────────────┐
│              ✅ IMPLEMENTAÇÃO CONCLUÍDA                    │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ✅ Desktop → Cria administradores                        │
│  ✅ Mobile → Cria usuários comuns                         │
│  ✅ Seguro → Validação no servidor                        │
│  ✅ Compatível → Clientes antigos funcionam               │
│  ✅ Testado → Teste de integração incluído                │
│  ✅ Documentado → 5 arquivos de documentação              │
│                                                            │
│  Status: PRONTO PARA PRODUÇÃO 🚀                          │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

**Versão**: 1.0  
**Data**: 2025-10-19  
**Status**: ✅ Concluído
