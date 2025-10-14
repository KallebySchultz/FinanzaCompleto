# Finanza Desktop - Admin Panel

## Descrição

O cliente desktop foi simplificado para ser **exclusivamente para administradores**. Agora o sistema possui apenas funcionalidades de login e gerenciamento de usuários.

## Mudanças Realizadas

### ✅ Removido
- Funcionalidade de registro de novos usuários
- Funcionalidade de recuperação de senha
- Todas as telas de gestão financeira:
  - Dashboard financeiro
  - Movimentações
  - Contas
  - Categorias
  - Relatórios
  - Exportação de dados
  - Perfil do usuário

### ✅ Adicionado
- Tela de login simplificada para administradores
- Painel de administração com listagem de usuários
- Funcionalidade de editar usuários:
  - Alterar nome
  - Alterar email
  - Alterar senha
- Interface limpa e focada em gerenciamento de usuários

## Como Usar

### 1. Iniciar o Servidor
```bash
cd "DESKTOP VERSION/ServidorFinanza"
# Certifique-se de que o MySQL está rodando com o banco de dados configurado
java -cp bin:lib/* MainServidor
```

### 2. Iniciar o Cliente Admin
```bash
cd "DESKTOP VERSION/ClienteFinanza"
java -cp bin MainCliente
```

### 3. Login
Use uma das contas de administrador:

**Conta 1:**
- Email: `kallebyschultz@gmail.com`
- Senha: `92659580`

**Conta 2:**
- Email: `kauanluft@gmail.com`
- Senha: `123456`

### 4. Gerenciar Usuários
Após o login, você verá:
- Lista de todos os usuários cadastrados no sistema
- Opção de editar qualquer usuário (duplo clique ou botão "Editar")
- Opção de editar seu próprio perfil
- Botão de logout

### 5. Editar Usuários
Ao editar um usuário, você pode:
- Alterar o nome
- Alterar o email
- Alterar a senha (opcional - deixe em branco para não alterar)

## Estrutura de Arquivos

```
DESKTOP VERSION/
├── ClienteFinanza/
│   └── src/
│       ├── controller/
│       │   └── AuthController.java (+ métodos de gerenciamento de usuários)
│       ├── model/
│       │   └── Usuario.java
│       ├── util/
│       │   └── NetworkClient.java
│       ├── view/
│       │   ├── LoginView.java (simplificado)
│       │   ├── AdminDashboardView.java (novo)
│       │   └── EditarUsuarioDialog.java (novo)
│       └── MainCliente.java
│
└── ServidorFinanza/
    └── src/
        ├── dao/
        │   └── UsuarioDAO.java (+ método listarTodos)
        ├── server/
        │   ├── Protocol.java (+ comandos admin)
        │   └── ClientHandler.java (+ handlers admin)
        └── MainServidor.java
```

## Comandos do Protocolo (Servidor)

Novos comandos adicionados:

- `LIST_USERS` - Lista todos os usuários
- `UPDATE_USER|userId|novoNome|novoEmail` - Atualiza dados do usuário
- `UPDATE_USER_PASSWORD|userId|novaSenha` - Atualiza senha do usuário

## Requisitos

- Java JDK 8 ou superior
- MySQL Server rodando
- Banco de dados `finanza_db` configurado com as tabelas necessárias
- As credenciais dos administradores devem estar cadastradas no banco

## Notas Importantes

⚠️ **Segurança**: Este é um painel administrativo. Certifique-se de que apenas administradores têm acesso às credenciais de login.

⚠️ **Banco de Dados**: O servidor precisa de uma conexão MySQL ativa. Verifique as configurações em `DatabaseUtil.java`.

⚠️ **Funcionalidade Mobile**: O aplicativo mobile continua com todas as funcionalidades de gestão financeira para os usuários finais.

## Arquitetura

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────┐
│  Desktop Admin  │ ◄─────► │  Servidor Java   │ ◄─────► │    MySQL    │
│    (Cliente)    │  TCP/IP │   (Servidor)     │  JDBC   │  (Banco)    │
└─────────────────┘         └──────────────────┘         └─────────────┘
        │
        │
        └── Gerencia apenas usuários (sem dados financeiros)

┌─────────────────┐         ┌──────────────────┐         ┌─────────────┐
│   Mobile App    │ ◄─────► │  Servidor Java   │ ◄─────► │    MySQL    │
│   (Android)     │  TCP/IP │   (Servidor)     │  JDBC   │  (Banco)    │
└─────────────────┘         └──────────────────┘         └─────────────┘
        │
        │
        └── Acesso completo a dados financeiros pessoais
```

## Futuras Melhorias

- [ ] Adicionar filtros e pesquisa na lista de usuários
- [ ] Adicionar paginação para grandes volumes de dados
- [ ] Adicionar logs de ações administrativas
- [ ] Adicionar estatísticas de uso do sistema
- [ ] Implementar controle de permissões mais granular
