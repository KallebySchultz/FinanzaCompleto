# Finanza - Sistema de Gestão Financeira Pessoal

Finanza é um sistema completo de gestão financeira pessoal que inclui uma aplicação móvel Android, API REST em Node.js e cliente desktop web.

## 🏗️ Arquitetura do Sistema

### 📱 Aplicação Mobile (Android)
- **Localização**: `/app/`
- **Tecnologias**: Java, Android SDK, Room Database, SQLite
- **Recursos**: Gestão de usuários, contas, transações, categorias, sincronização com servidor

### 🌐 API REST (Node.js)
- **Localização**: `/server/`
- **Tecnologias**: Node.js, Express, SQLite3, JWT, bcrypt
- **Porta**: 3000
- **Endpoints**: Autenticação, usuários, contas, transações, categorias, administração

### 🖥️ Cliente Desktop (Web)
- **Localização**: `/desktop-client/`
- **Tecnologias**: HTML5, CSS3, JavaScript ES6+, Express (servidor estático)
- **Porta**: 3001
- **Recursos**: Dashboard, gestão de contas, transações, perfil, painel administrativo

### 🗄️ Banco de Dados
- **Localização**: `/database/`
- **Tecnologia**: SQLite
- **Schema**: `/database/finanza.sql`

## 🚀 Como Executar

### Pré-requisitos
- Node.js 18+ 
- SQLite3
- Android Studio (para mobile)

### 1. Preparar o Banco de Dados
```bash
cd database/
sqlite3 finanza.db < finanza.sql
```

### 2. Executar a API (Terminal 1)
```bash
cd server/
npm install
npm start
```
A API estará disponível em: http://localhost:3000

### 3. Executar o Cliente Desktop (Terminal 2)
```bash
cd desktop-client/
npm install
npm start
```
O cliente desktop estará disponível em: http://localhost:3001

### 4. Configurar Mobile App
A aplicação mobile já está configurada para conectar com a API local. 
Compile e execute usando Android Studio.

## 📋 Funcionalidades Implementadas

### ✅ API REST Completa
- **Autenticação**
  - POST `/api/auth/login` - Login de usuário
  - POST `/api/auth/register` - Registro de usuário
  - GET `/api/auth/verify` - Verificar token
  - POST `/api/auth/logout` - Logout

- **Gestão de Usuários**
  - GET `/api/users/profile` - Perfil do usuário
  - PUT `/api/users/profile` - Atualizar perfil
  - DELETE `/api/users/profile` - Excluir conta
  - GET `/api/users/financial-summary` - Resumo financeiro

- **Gestão de Contas**
  - GET `/api/accounts` - Listar contas
  - POST `/api/accounts` - Criar conta
  - PUT `/api/accounts/:id` - Atualizar conta
  - DELETE `/api/accounts/:id` - Excluir conta
  - GET `/api/accounts/:id/transactions` - Extrato da conta

- **Gestão de Transações**
  - GET `/api/transactions` - Listar transações (com filtros)
  - POST `/api/transactions` - Criar transação
  - PUT `/api/transactions/:id` - Atualizar transação
  - DELETE `/api/transactions/:id` - Excluir transação
  - GET `/api/transactions/summary/period` - Resumo por período

- **Gestão de Categorias**
  - GET `/api/categories` - Listar categorias
  - POST `/api/categories` - Criar categoria
  - PUT `/api/categories/:id` - Atualizar categoria
  - DELETE `/api/categories/:id` - Excluir categoria
  - GET `/api/categories/stats/usage` - Estatísticas de uso

- **Administração**
  - GET `/api/admin/users` - Listar todos os usuários
  - GET `/api/admin/users/:id` - Detalhes de usuário
  - PUT `/api/admin/users/:id` - Atualizar usuário
  - DELETE `/api/admin/users/:id` - Excluir usuário
  - POST `/api/admin/users` - Criar usuário
  - GET `/api/admin/stats` - Estatísticas do sistema
  - GET `/api/admin/activity` - Atividade recente

### ✅ Cliente Desktop
- **Interface Moderna**: Design com gradientes e glassmorphism
- **Autenticação**: Login e registro de usuários
- **Dashboard**: Resumo financeiro com cards informativos
- **Gestão de Contas**: Listar, criar, editar e excluir contas
- **Navegação**: Sidebar responsiva com navegação entre módulos
- **Admin Panel**: Acesso administrativo para usuários autorizados

### ✅ Segurança Implementada
- **Autenticação JWT**: Tokens seguros para sessões
- **Hash de Senhas**: bcrypt para senhas seguras
- **Rate Limiting**: Proteção contra ataques de força bruta
- **CORS**: Configuração adequada para requisições cross-origin
- **Validação**: Validação rigorosa de dados de entrada
- **Autorização**: Middleware para verificar permissões

## 👤 Usuário Padrão
- **Email**: admin@finanza.com
- **Senha**: admin
- **Privilégios**: Administrador completo

## 📊 Dados de Exemplo
O banco vem com dados de exemplo:
- 1 usuário administrador
- 2 contas (Conta Corrente e Poupança)
- 5 transações de exemplo
- Categorias padrão para receitas e despesas

## 🎨 Design
O cliente desktop segue o design moderno baseado nos mockups fornecidos:
- Cores: Gradiente azul/roxo (#667eea para #764ba2)
- Tipografia: Segoe UI, clean e moderna
- Layout: Cards com glassmorphism e sombras sutis
- Responsivo: Adaptável para diferentes tamanhos de tela

## 🔧 Configuração Avançada

### Variáveis de Ambiente (server/.env)
```env
NODE_ENV=development
PORT=3000
JWT_SECRET=your-super-secret-jwt-key
DB_PATH=../database/finanza.db
CORS_ORIGIN=http://localhost:3001
```

### Personalização
- Para alterar cores: edite `/desktop-client/assets/css/main.css`
- Para adicionar endpoints: edite arquivos em `/server/routes/`
- Para modificar banco: edite `/database/finanza.sql`

## 🚨 Notas de Produção
- Altere JWT_SECRET em produção
- Configure HTTPS para segurança
- Use banco de dados mais robusto (PostgreSQL/MySQL)
- Implemente logs estruturados
- Configure monitoramento e backups

## 🤝 Contribuição
1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença
MIT License - veja o arquivo LICENSE para detalhes.