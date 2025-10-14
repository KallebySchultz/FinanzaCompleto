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
- **Funcionalidade de registro de novos administradores**
- Painel de administração com listagem de usuários
- Funcionalidade de editar usuários:
  - Alterar nome
  - Alterar email
  - Alterar senha
- **Funcionalidade de excluir usuários**
- **Busca/filtro de usuários por nome, email ou ID**
- **Estatísticas do dashboard (total de usuários, usuários filtrados)**
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

### 3. Login ou Registro

#### Login
Use uma das contas de administrador existentes:

**Conta 1:**
- Email: `kallebyschultz@gmail.com`
- Senha: `92659580`

**Conta 2:**
- Email: `kauanluft@gmail.com`
- Senha: `123456`

#### Registrar Novo Administrador
1. Na tela de login, clique no botão **"Registrar Admin"**
2. Preencha todos os campos:
   - Nome completo
   - Email
   - Senha (mínimo 6 caracteres)
3. Clique em **"Criar Conta"**
4. Após o registro, você será redirecionado para a tela de login
5. Faça login com as credenciais criadas

### 4. Dashboard de Administração
Após o login, você verá o painel de administração com:
- **Cabeçalho** com informações do admin logado e total de usuários
- **Campo de busca** para filtrar usuários por nome, email ou ID
- **Tabela de usuários** com ID, Nome, Email e Data de Criação
- **Botões de ação**:
  - **Atualizar Lista**: Recarrega a lista de usuários do servidor
  - **Editar**: Edita o usuário selecionado
  - **Excluir**: Remove o usuário selecionado (com confirmação)
- **Botões de perfil**:
  - **Editar Meu Perfil**: Permite editar seus próprios dados
  - **Sair**: Faz logout e retorna à tela de login

### 5. Buscar e Filtrar Usuários
1. Digite no campo de busca o nome, email ou ID do usuário
2. Clique em **"Filtrar"** ou pressione Enter
3. A tabela mostrará apenas os usuários que correspondem à busca
4. Clique em **"Limpar"** para mostrar todos os usuários novamente

### 6. Editar Usuários
1. Selecione um usuário na tabela (clique na linha)
2. Clique no botão **"Editar"** ou dê duplo clique na linha
3. No diálogo de edição, você pode alterar:
   - Nome do usuário
   - Email do usuário
   - Senha (opcional - deixe em branco para não alterar)
4. Clique em **"Salvar"** para confirmar as alterações

### 7. Excluir Usuários
1. Selecione um usuário na tabela
2. Clique no botão **"Excluir"** (vermelho)
3. Confirme a ação no diálogo de confirmação
4. ⚠️ **Atenção**: Não é possível excluir o próprio usuário logado
5. ⚠️ **Esta ação não pode ser desfeita!**

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

Comandos adicionados para gerenciamento de usuários:

- `LIST_USERS` - Lista todos os usuários
- `UPDATE_USER|userId|novoNome|novoEmail` - Atualiza dados do usuário
- `UPDATE_USER_PASSWORD|userId|novaSenha` - Atualiza senha do usuário
- `DELETE_USER|userId` - Exclui um usuário (não pode ser o usuário logado)

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

## Recursos Implementados ✅

- ✅ Login de administradores
- ✅ Registro de novos administradores
- ✅ Listagem de usuários
- ✅ Edição de usuários (nome, email, senha)
- ✅ Exclusão de usuários
- ✅ Busca/filtro de usuários
- ✅ Estatísticas do dashboard
- ✅ Edição do próprio perfil
- ✅ Logout seguro

## Futuras Melhorias

- [ ] Adicionar paginação para grandes volumes de dados
- [ ] Adicionar logs de ações administrativas
- [ ] Adicionar mais estatísticas de uso do sistema
- [ ] Implementar controle de permissões mais granular
- [ ] Adicionar exportação de dados de usuários
