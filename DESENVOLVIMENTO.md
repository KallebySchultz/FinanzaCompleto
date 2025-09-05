# 💰 Finanza - Sistema de Gestão Financeira Pessoal

## 📋 Visão Geral

O **Finanza** é um sistema completo de gestão financeira pessoal composto por:

- **📱 App Android nativo** (Java + Room Database)
- **🔧 API REST** (Node.js + Express + Firebase)
- **🗄️ Banco de Dados** (SQLite local + Firebase Realtime Database)

## 🏗️ Arquitetura do Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   APP ANDROID   │    │   API REST      │    │   FIREBASE      │
│                 │    │                 │    │                 │
│ • Room Database │◄──►│ • Express.js    │◄──►│ • Realtime DB   │
│ • UI Activities │    │ • JWT Auth      │    │ • Cloud Sync    │
│ • Sync Service  │    │ • SQLite Local  │    │ • Backup        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 Como Executar o Projeto

### 1. Configurar o Servidor

```bash
cd server
npm install
npm start
```

O servidor estará disponível em: `http://localhost:8080`

### 2. Configurar o App Android

1. Abra o projeto no Android Studio
2. Configure o IP do servidor em `ServerClient.java`
3. Execute o app em um device/emulador

### 3. Testar a API

```bash
# Health check
curl http://localhost:8080/api/health

# Criar usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste","email":"teste@example.com","senha":"123456"}'
```

## 📊 Funcionalidades Principais

### 👤 **Gestão de Usuários**
- ✅ Cadastro e login
- ✅ Autenticação JWT
- ✅ Perfil do usuário
- ✅ Sistema de administração

### 🏦 **Gestão de Contas**
- ✅ Múltiplas contas (corrente, poupança, etc.)
- ✅ Saldo inicial e atual
- ✅ Histórico de movimentações
- ✅ Edição e exclusão

### 📝 **Transações Financeiras**
- ✅ Receitas e despesas
- ✅ Categorização automática
- ✅ Descrição e data
- ✅ Filtros por período/tipo

### 🏷️ **Categorias**
- ✅ Categorias pré-definidas
- ✅ Cores personalizadas
- ✅ Separação receitas/despesas
- ✅ Criação de novas categorias

### 📈 **Relatórios**
- ✅ Resumo financeiro
- ✅ Análise por categoria
- ✅ Gráficos de gastos
- ✅ Exportação de dados

### ☁️ **Sincronização**
- ✅ Backup na nuvem (Firebase)
- ✅ Sync automática
- ✅ Funcionamento offline
- ✅ Resolução de conflitos

## 🗄️ Estrutura do Banco de Dados

### Tabelas Principais

| Tabela | Descrição | Campos Principais |
|--------|-----------|-------------------|
| `usuarios` | Dados dos usuários | id, nome, email, senha |
| `contas` | Contas financeiras | id, nome, saldo_inicial, usuario_id |
| `categorias` | Categorias de transações | id, nome, cor_hex, tipo |
| `lancamentos` | Transações financeiras | id, valor, data, descricao, conta_id |

### Views Úteis

- `v_saldo_contas` - Saldo atual de cada conta
- `v_resumo_categorias` - Totais por categoria
- `v_lancamentos_detalhados` - Transações com detalhes completos

## 🔧 Estrutura de Arquivos

### Backend (Node.js)
```
server/
├── config/           # Configurações (Firebase, DB)
├── middleware/       # Middlewares (auth, cors, etc.)
├── routes/          # Rotas da API
│   ├── auth.js      # Autenticação
│   ├── users.js     # Usuários
│   ├── accounts.js  # Contas
│   ├── transactions.js # Transações
│   ├── categories.js   # Categorias
│   └── admin.js     # Administração
├── .env            # Variáveis de ambiente
├── package.json    # Dependências
└── server.js       # Servidor principal
```

### Android App
```
app/src/main/java/com/example/finanza/
├── db/             # Room Database (DAO, Entities)
├── model/          # Models de dados
├── network/        # Comunicação (Firebase, Servidor)
├── ui/             # Activities da interface
├── MainActivity.java # Activity principal
└── AndroidManifest.xml
```

## 📱 Principais Activities

| Activity | Função |
|----------|--------|
| `LoginActivity` | Tela de login/registro |
| `MenuActivity` | Menu principal |
| `AccountsActivity` | Gestão de contas |
| `MovementsActivity` | Transações |
| `CategoriaActivity` | Categorias |
| `ReportsActivity` | Relatórios |
| `SettingsActivity` | Configurações |

## 🔐 Segurança

- **Autenticação JWT** com refresh tokens
- **Bcrypt** para hash de senhas
- **Rate limiting** contra ataques
- **CORS** configurado
- **Helmet** para headers de segurança
- **Validação** de dados em todas as rotas

## 🌐 APIs Disponíveis

### Autenticação
- `POST /api/auth/register` - Cadastro
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Renovar token

### Usuários
- `GET /api/users/profile` - Perfil
- `PUT /api/users/profile` - Atualizar perfil
- `GET /api/users/financial-summary` - Resumo financeiro

### Contas
- `GET /api/accounts` - Listar contas
- `POST /api/accounts` - Criar conta
- `PUT /api/accounts/:id` - Atualizar conta
- `DELETE /api/accounts/:id` - Excluir conta

### Transações
- `GET /api/transactions` - Listar transações
- `POST /api/transactions` - Criar transação
- `PUT /api/transactions/:id` - Atualizar transação
- `DELETE /api/transactions/:id` - Excluir transação

## 🔧 Configurações de Desenvolvimento

### Variáveis de Ambiente (.env)
```env
NODE_ENV=development
PORT=8080
JWT_SECRET=your-super-secret-jwt-key
DB_PATH=../database/finanza.db
CORS_ORIGIN=*
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100
```

### Firebase Config
Configure as credenciais do Firebase em `server/config/firebase.js`

## 🐛 Troubleshooting

### Problemas Comuns

1. **Servidor não inicia**
   - Verifique se as dependências estão instaladas: `npm install`
   - Verifique se a porta 8080 está livre

2. **App não conecta ao servidor**
   - Configure o IP correto em `ServerClient.java`
   - Verifique se o servidor está rodando
   - Teste a conectividade: `curl http://IP:8080/api/health`

3. **Erro de autenticação**
   - Verifique se o JWT_SECRET está configurado
   - Confira se os tokens não expiraram

4. **Problemas de sincronização**
   - Verifique a conexão com Firebase
   - Confira as configurações de rede

## 📈 Próximas Funcionalidades

- [ ] Dashboard com gráficos avançados
- [ ] Metas e orçamentos
- [ ] Notificações push
- [ ] Importação de extratos bancários
- [ ] Modo escuro
- [ ] Backup/restore completo
- [ ] Múltiplas moedas
- [ ] Relatórios em PDF

## 🤝 Como Contribuir

1. Faça um fork do projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Faça um push para a branch
5. Abra um Pull Request

## 📞 Suporte

Para dúvidas ou problemas:
- 📧 Email: suporte@finanza.com
- 🐛 Issues: GitHub Issues
- 📖 Docs: Este arquivo

---

**Finanza** - Sua gestão financeira simplificada! 💰✨