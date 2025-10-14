# Finanza Desktop - Admin Panel

## Descrição

O cliente desktop é o **painel completo de administração** do sistema Finanza. Administradores podem gerenciar não apenas usuários, mas também todas as contas, categorias e movimentações financeiras de todos os usuários do sistema através de uma interface tabular intuitiva com 4 abas principais.

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
Após o login, você verá o painel de administração com **4 abas**:

#### Aba "Usuários"
- **Cabeçalho** com informações do admin logado e total de usuários
- **Campo de busca** para filtrar usuários por nome, email ou ID
- **Tabela de usuários** com ID, Nome, Email e Data de Criação
- **Botões de ação**:
  - **Atualizar Lista**: Recarrega a lista de usuários do servidor
  - **Editar**: Edita o usuário selecionado
  - **Excluir**: Remove o usuário selecionado (com confirmação)

#### Aba "Contas"
- **Filtro por usuário**: Dropdown para filtrar contas de um usuário específico ou ver todas
- **Tabela de contas** com ID, Nome, Tipo, Saldo Inicial e ID do Usuário
- **Botões de ação**:
  - **Atualizar Lista**: Recarrega a lista de contas
  - **Editar**: Edita a conta selecionada (nome, tipo, saldo)
  - **Excluir**: Remove a conta selecionada

#### Aba "Categorias"
- **Filtro por usuário**: Dropdown para filtrar categorias de um usuário específico ou ver todas
- **Tabela de categorias** com ID, Nome, Tipo e ID do Usuário
- **Botões de ação**:
  - **Atualizar Lista**: Recarrega a lista de categorias
  - **Editar**: Edita a categoria selecionada (nome, tipo)
  - **Excluir**: Remove a categoria selecionada

#### Aba "Movimentações"
- **Filtro por usuário**: Dropdown para filtrar movimentações de um usuário específico ou ver todas
- **Tabela de movimentações** com ID, Valor, Data, Descrição, Tipo, ID Conta e ID Categoria
- **Botões de ação**:
  - **Atualizar Lista**: Recarrega a lista de movimentações
  - **Editar**: Edita a movimentação selecionada (todos os campos)
  - **Excluir**: Remove a movimentação selecionada

#### Botões Globais (rodapé)
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

### 8. Gerenciar Contas de Usuários
1. Acesse a aba **"Contas"**
2. Use o filtro para selecionar um usuário específico ou visualizar todas as contas
3. Clique em **"Filtrar"** para carregar as contas
4. Para **editar**: Selecione uma conta e clique em **"Editar"**
   - Altere nome, tipo (corrente, poupança, cartão, investimento, dinheiro) ou saldo inicial
5. Para **excluir**: Selecione uma conta e clique em **"Excluir"**
   - ⚠️ Isso removerá todas as movimentações associadas!

### 9. Gerenciar Categorias de Usuários
1. Acesse a aba **"Categorias"**
2. Use o filtro para selecionar um usuário específico ou visualizar todas as categorias
3. Clique em **"Filtrar"** para carregar as categorias
4. Para **editar**: Selecione uma categoria e clique em **"Editar"**
   - Altere nome ou tipo (receita/despesa)
5. Para **excluir**: Selecione uma categoria e clique em **"Excluir"**

### 10. Gerenciar Movimentações de Usuários
1. Acesse a aba **"Movimentações"**
2. Use o filtro para selecionar um usuário específico ou visualizar todas as movimentações
3. Clique em **"Filtrar"** para carregar as movimentações
4. Para **editar**: Selecione uma movimentação e clique em **"Editar"**
   - Altere valor, data, descrição, tipo, conta ou categoria
5. Para **excluir**: Selecione uma movimentação e clique em **"Excluir"**

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

### Comandos de Gerenciamento de Usuários
- `LIST_USERS` - Lista todos os usuários
- `UPDATE_USER|userId|novoNome|novoEmail` - Atualiza dados do usuário
- `UPDATE_USER_PASSWORD|userId|novaSenha` - Atualiza senha do usuário
- `DELETE_USER|userId` - Exclui um usuário (não pode ser o usuário logado)

### Comandos Admin para Gerenciamento de Contas (Novo!)
- `ADMIN_LIST_CONTAS_USER|userId` - Lista todas as contas de um usuário
- `ADMIN_UPDATE_CONTA|contaId|nome|tipo|saldoInicial` - Atualiza uma conta
- `ADMIN_DELETE_CONTA|contaId` - Exclui uma conta

### Comandos Admin para Gerenciamento de Categorias (Novo!)
- `ADMIN_LIST_CATEGORIAS_USER|userId` - Lista todas as categorias de um usuário
- `ADMIN_UPDATE_CATEGORIA|categoriaId|nome|tipo` - Atualiza uma categoria
- `ADMIN_DELETE_CATEGORIA|categoriaId` - Exclui uma categoria

### Comandos Admin para Gerenciamento de Movimentações (Novo!)
- `ADMIN_LIST_MOVIMENTACOES_USER|userId` - Lista todas as movimentações de um usuário
- `ADMIN_UPDATE_MOVIMENTACAO|movId|valor|data|descricao|tipo|idConta|idCategoria` - Atualiza uma movimentação
- `ADMIN_DELETE_MOVIMENTACAO|movimentacaoId` - Exclui uma movimentação

**Nota**: Todos os comandos admin são compatíveis com o aplicativo Android. O app mobile continua usando seus próprios comandos (`LIST_CONTAS`, `ADD_CONTA`, etc.) sem modificações.

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
        └── Gerencia TUDO: Usuários, Contas, Categorias e Movimentações

┌─────────────────┐         ┌──────────────────┐         ┌─────────────┐
│   Mobile App    │ ◄─────► │  Servidor Java   │ ◄─────► │    MySQL    │
│   (Android)     │  TCP/IP │   (Servidor)     │  JDBC   │  (Banco)    │
└─────────────────┘         └──────────────────┘         └─────────────┘
        │
        │
        └── Acesso completo a dados financeiros pessoais
```

## Recursos Implementados ✅

### Gerenciamento de Usuários
- ✅ Login de administradores
- ✅ Registro de novos administradores
- ✅ Listagem de todos os usuários cadastrados
- ✅ Edição de usuários (nome, email, senha)
- ✅ Exclusão de usuários
- ✅ Busca/filtro de usuários por nome, email ou ID
- ✅ Estatísticas do dashboard (total de usuários)
- ✅ Edição do próprio perfil
- ✅ Logout seguro

### Gerenciamento de Contas (Novo!)
- ✅ Visualização de todas as contas de todos os usuários
- ✅ Filtro de contas por usuário específico
- ✅ Edição de contas (nome, tipo, saldo inicial)
- ✅ Exclusão de contas
- ✅ Atualização em tempo real

### Gerenciamento de Categorias (Novo!)
- ✅ Visualização de todas as categorias de todos os usuários
- ✅ Filtro de categorias por usuário específico
- ✅ Edição de categorias (nome, tipo)
- ✅ Exclusão de categorias
- ✅ Atualização em tempo real

### Gerenciamento de Movimentações (Novo!)
- ✅ Visualização de todas as movimentações de todos os usuários
- ✅ Filtro de movimentações por usuário específico
- ✅ Edição de movimentações (valor, data, descrição, tipo, conta, categoria)
- ✅ Exclusão de movimentações
- ✅ Atualização em tempo real

## Interface

O painel administrativo agora possui **4 abas principais**:

1. **Usuários**: Gerenciamento completo de usuários do sistema
2. **Contas**: Visualização e gerenciamento de todas as contas bancárias
3. **Categorias**: Visualização e gerenciamento de todas as categorias
4. **Movimentações**: Visualização e gerenciamento de todas as transações financeiras

## Futuras Melhorias

- [ ] Adicionar paginação para grandes volumes de dados
- [ ] Adicionar logs de ações administrativas
- [ ] Adicionar mais estatísticas de uso do sistema (totais, gráficos)
- [ ] Implementar controle de permissões mais granular
- [ ] Adicionar exportação de dados (CSV, Excel)
- [ ] Adicionar busca avançada com múltiplos critérios
