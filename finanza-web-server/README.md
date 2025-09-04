# 🌐 Finanza Web Server

Uma versão web HTML/CSS do sistema Finanza que funciona como ponte para o servidor Java TCP existente, fornecendo uma interface web moderna e responsiva.

## 🚀 Funcionalidades

### ✅ Interface Web Completa
- **Dashboard interativo** com estatísticas em tempo real
- **Gestão de contas** financeiras
- **Controle de lançamentos** (receitas e despesas)
- **Painel de administração** do servidor
- **Interface responsiva** para desktop e mobile

### ✅ API REST
- **Endpoints compatíveis** com o servidor Java TCP
- **Autenticação JWT** para segurança
- **Comunicação em tempo real** via WebSocket
- **Proxy transparente** para o servidor Java

### ✅ Integração com Servidor Java
- **Comunicação TCP/JSON** com o servidor existente
- **Protocolo compatível** com app Android
- **Fallback automático** em caso de indisponibilidade
- **Monitoramento de status** em tempo real

## 📋 Pré-requisitos

- **Node.js** 14.0.0 ou superior
- **npm** ou **yarn**
- **Servidor Java** Finanza rodando na porta 8080 (opcional)

## 🛠️ Instalação

1. **Instalar dependências:**
```bash
cd finanza-web-server
npm install
```

2. **Configurar variáveis de ambiente (opcional):**
```bash
export PORT=3000
export JAVA_SERVER_HOST=localhost
export JAVA_SERVER_PORT=8080
```

3. **Iniciar o servidor:**
```bash
# Modo produção
npm start

# Modo desenvolvimento (com auto-restart)
npm run dev
```

## 🌐 Acessos

- **Interface Web:** http://localhost:3000
- **Dashboard:** http://localhost:3000/dashboard
- **Painel do Servidor:** http://localhost:3000/server
- **API REST:** http://localhost:3000/api/

### 🔐 Login Demo
- **Email:** admin@finanza.com
- **Senha:** admin

## 📚 Endpoints da API

### Autenticação
- `POST /api/auth/login` - Login de usuário
- `POST /api/auth/register` - Cadastro de usuário

### Sincronização (compatível com servidor Java)
- `GET /api/ping` - Teste de conectividade
- `GET /api/sync/user/:userId` - Sincronizar dados do usuário
- `GET /api/sync/accounts/:userId` - Sincronizar contas
- `GET /api/sync/transactions/:userId` - Sincronizar lançamentos
- `GET /api/sync/categories` - Sincronizar categorias

### Status do Sistema
- `GET /api/status` - Status dos servidores (Web + Java)

## 🏗️ Arquitetura

```
📦 finanza-web-server/
├── 🖥️ server.js              # Servidor principal Express
├── 📁 routes/                # Rotas da aplicação
│   ├── api.js               # Endpoints REST da API
│   ├── web.js               # Rotas das páginas web
│   └── server.js            # Rotas de administração
├── 📁 views/                 # Templates EJS
│   ├── index.ejs            # Página inicial
│   ├── dashboard.ejs        # Dashboard principal
│   ├── login.ejs            # Página de login
│   └── server/              # Páginas de administração
├── 📁 public/                # Arquivos estáticos
│   ├── css/main.css         # Estilos principais
│   └── js/main.js           # JavaScript cliente
├── 📁 utils/                 # Utilitários
│   └── javaServerProxy.js   # Proxy para servidor Java
├── 📁 middleware/            # Middlewares Express
│   ├── auth.js              # Autenticação JWT
│   └── errorHandler.js      # Tratamento de erros
└── 📄 package.json          # Dependências e scripts
```

## 🔧 Como Funciona

### 1. **Modo Proxy (Recomendado)**
O servidor web atua como proxy, repassando requisições para o servidor Java TCP:

```
Cliente Web → Node.js Server → Java TCP Server → Database
            ↙ HTTP/WebSocket  ↙ TCP/JSON
```

### 2. **Modo Standalone**
O servidor web pode funcionar independentemente (implementação futura):

```
Cliente Web → Node.js Server → SQLite Database
            ↙ HTTP/WebSocket
```

## 📊 Monitoramento

### Dashboard do Servidor (`/server`)
- **Status em tempo real** dos servidores
- **Métricas de performance** (uptime, memória, conexões)
- **Teste de conectividade** com servidor Java
- **Configuração dinâmica** de endpoints

### WebSocket em Tempo Real
- **Notificações automáticas** de mudanças
- **Sincronização instantânea** entre clientes
- **Monitoramento de conexão** contínuo

## 🚀 Deploy em Produção

### 1. **Usando PM2**
```bash
npm install -g pm2
pm2 start server.js --name finanza-web
pm2 startup
pm2 save
```

### 2. **Usando Docker**
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
EXPOSE 3000
CMD ["npm", "start"]
```

### 3. **Variáveis de Ambiente**
```bash
PORT=3000
NODE_ENV=production
JAVA_SERVER_HOST=your-java-server.com
JAVA_SERVER_PORT=8080
JWT_SECRET=your-secret-key
```

## 🔍 Testes

```bash
# Testar API
npm test

# Testar conectividade com servidor Java
curl http://localhost:3000/api/ping

# Testar autenticação
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@finanza.com","senha":"admin"}'
```

## 📈 Recursos Avançados

### WebSocket Events
- `connection` - Cliente conectado
- `join-room` - Entrar em sala específica
- `sync-update` - Atualização de sincronização
- `server-status` - Mudança de status do servidor

### Middleware Personalizado
- **Rate limiting** para proteção da API
- **Logging detalhado** de requisições
- **Compressão automática** de respostas
- **CORS configurável** para diferentes origens

## 🤝 Integração com Sistema Existente

Esta implementação web é **100% compatível** com:
- ✅ **Servidor Java TCP** existente
- ✅ **Aplicativo Android** (via mesma API)
- ✅ **Banco SQLite** (via servidor Java)
- ✅ **Protocolo JSON** estabelecido

## 📝 Exemplo de Uso

```javascript
// Conectar via WebSocket
const socket = io();

// Fazer login
const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: 'admin@finanza.com', senha: 'admin' })
});

// Sincronizar dados
const userData = await fetch('/api/sync/user/1');
const accounts = await fetch('/api/sync/accounts/1');
```

## 🛡️ Segurança

- **JWT tokens** para autenticação
- **Middleware de validação** de entrada
- **Rate limiting** para prevenir abuso
- **Sanitização automática** de dados
- **HTTPS ready** para produção

## 📞 Suporte

Para problemas relacionados ao servidor web, verifique:

1. **Logs do servidor:** `console.log` no terminal
2. **Status do Java:** `/server` no navegador
3. **Conectividade:** `/api/ping` endpoint
4. **WebSocket:** Console do navegador (F12)

---

**🎯 Objetivo Alcançado:** Sistema web completo que permite acesso HTML/CSS às funcionalidades do Finanza, mantendo compatibilidade total com a arquitetura Java/Android existente!