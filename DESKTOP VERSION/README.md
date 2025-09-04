# Finanza Desktop - Tutorial de Instalação e Uso

## 📋 Visão Geral

O Finanza Desktop foi refatorado para ter uma arquitetura modular com páginas HTML separadas, proporcionando melhor organização, manutenibilidade e escalabilidade.

### 🗂️ Nova Estrutura de Pastas

```
DESKTOP VERSION/
├── html/                    # Páginas HTML separadas
│   ├── login.html          # Página de login
│   ├── register.html       # Página de cadastro
│   ├── dashboard.html      # Dashboard principal
│   ├── accounts.html       # Gerenciamento de contas
│   ├── transactions.html   # Gerenciamento de transações
│   ├── profile.html        # Perfil do usuário
│   └── admin.html          # Painel administrativo
├── css/                     # Estilos CSS modulares
│   ├── common.css          # Estilos compartilhados
│   ├── auth.css            # Estilos de autenticação
│   └── dashboard.css       # Estilos do dashboard
├── js/                      # Scripts JavaScript modulares
│   ├── api.js              # Camada de comunicação com API
│   ├── auth.js             # Utilitários de autenticação
│   └── navigation.js       # Utilitários de navegação
├── assets/                  # Recursos estáticos
│   └── images/             # Imagens e ícones
├── index.html              # Página principal (redireciona)
├── package.json            # Dependências do projeto
└── server.js               # Servidor estático
```

## 🚀 Como Instalar e Executar

### Pré-requisitos

- Node.js (versão 14 ou superior)
- npm (incluído com Node.js)
- Servidor backend Finanza rodando na porta 3000

### Passo 1: Instalação das Dependências

```bash
cd "DESKTOP VERSION"
npm install
```

### Passo 2: Iniciar o Servidor

```bash
npm start
```

O servidor iniciará na porta 3001. Acesse: http://localhost:3001

### Passo 3: Configuração do Backend

Certifique-se de que o servidor backend está rodando na porta 3000. Se necessário, altere a configuração em `js/api.js`:

```javascript
this.baseURL = 'http://localhost:3000/api';
```

## 🎨 Mudanças Visuais

### Tema de Cores
- **Gradiente Principal**: #667eea → #764ba2 (tons de azul/roxo)
- **Compatível com as imagens fornecidas na pasta IMAGENS**

### Ícones
- Substituição de ícones SVG por imagens PNG da pasta IMAGENS
- Ícones utilizados:
  - 🏠 **ícone home.png** - Dashboard
  - 🏦 **ícone banco.png** - Contas
  - 💰 **ícone movimentações.png** - Transações
  - 👤 **ícone menu.png** - Perfil e Admin

## 📱 Funcionalidades

### Páginas Disponíveis

1. **Login (`html/login.html`)**
   - Autenticação de usuários
   - Link para cadastro

2. **Cadastro (`html/register.html`)**
   - Criação de nova conta
   - Validação de senha

3. **Dashboard (`html/dashboard.html`)**
   - Visão geral financeira
   - Estatísticas do usuário
   - Resumo de contas e transações

4. **Contas (`html/accounts.html`)**
   - Criar, editar e excluir contas
   - Visualizar saldos
   - Diferentes tipos de conta

5. **Transações (`html/transactions.html`)**
   - Criar, editar e excluir transações
   - Filtros e categorias
   - Histórico completo

6. **Perfil (`html/profile.html`)**
   - Editar informações pessoais
   - Alterar senha
   - Estatísticas do usuário

7. **Admin (`html/admin.html`)**
   - Painel administrativo (apenas para admin@finanza.com)
   - Estatísticas do sistema
   - Lista de usuários

### Navegação

- **Sidebar fixa** com navegação entre páginas
- **URLs diretas** para cada página
- **Autenticação automática** - redirecionamento baseado no estado de login

## 🔧 Desenvolvimento

### Arquitetura Modular

- **api.js**: Todas as chamadas para o backend
- **auth.js**: Gerenciamento de autenticação e utilitários
- **navigation.js**: Geração de sidebar e navegação
- **CSS modular**: Estilos separados por funcionalidade

### Adicionando Nova Página

1. Criar arquivo HTML em `html/`
2. Incluir scripts necessários:
   ```html
   <script src="../js/api.js"></script>
   <script src="../js/auth.js"></script>
   <script src="../js/navigation.js"></script>
   ```
3. Adicionar rota em `navigation.js`
4. Implementar inicialização da página

### Customização de Estilos

- **common.css**: Estilos base e componentes reutilizáveis
- **auth.css**: Estilos específicos para login/cadastro
- **dashboard.css**: Estilos para páginas internas

## 🐛 Solução de Problemas

### Erro de Conexão com API
- Verifique se o backend está rodando na porta 3000
- Confirme a URL da API em `js/api.js`

### Problemas de Autenticação
- Limpe o localStorage: `localStorage.clear()`
- Verifique se o token está sendo salvo corretamente

### Problemas de CORS
- Configure o backend para aceitar requisições do frontend
- Adicione headers CORS apropriados

## 🔄 Migração da Versão Anterior

A nova versão mantém compatibilidade com a API existente. As principais mudanças são estruturais:

- **Antes**: Single Page Application (SPA)
- **Depois**: Multi Page Application (MPA)
- **Benefícios**: 
  - Melhor SEO
  - Carregamento mais rápido
  - Estrutura mais organizadas
  - Facilita manutenção
  - URLs diretas para cada funcionalidade

## 📞 Suporte

Para problemas ou dúvidas:
1. Verifique os logs do console do navegador
2. Confirme se todas as dependências estão instaladas
3. Certifique-se de que o backend está rodando
4. Verifique a configuração da API

---

**Versão**: 2.0.0 (Refatorada)  
**Data**: 2024  
**Autor**: Sistema Finanza