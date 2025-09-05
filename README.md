# 💰 FINANZA - Sistema de Gestão Financeira Pessoal

Sistema completo de gestão financeira pessoal com interface web e app Android.

## ⚡ Início Rápido

### 🖥️ Windows
```batch
# 1. Verificar sistema
verificar_sistema.bat

# 2. Instalar dependências
instalar_dependencias.bat

# 3. Iniciar sistema completo
iniciar_tudo.bat
```

### 🐧 Linux/Mac
```bash
# 1. Verificar sistema
./verificar_sistema.sh

# 2. Instalar dependências
./instalar_dependencias.sh

# 3. Iniciar sistema completo
./iniciar_tudo.sh
```

### 📱 Acesso ao Sistema

**Desktop**: http://localhost:3001
- Email: `admin@finanza.com`
- Senha: `admin`

**Android**: Configure no app
1. Execute `descobrir_ip` (Windows/Linux)
2. Use o IP mostrado + porta 8080
3. Exemplo: `192.168.1.100:8080`

## 📋 Requisitos

### Obrigatórios
- **Node.js** 18.19.0+ (recomendado: versão LTS mais recente)
- **npm** 8+
- **Sistema**: Windows 10/11, Linux, ou macOS

### Verificar Instalação
```bash
node --version  # Deve ser v18.19.0 ou superior
npm --version   # Deve ser v8.0.0 ou superior
```

**Se não estiver instalado:**
1. Acesse: https://nodejs.org
2. Baixe a versão **LTS** (recomendada)
3. Instale normalmente
4. Reinicie o terminal/computador

## 🚀 Scripts Disponíveis

### Utilitários
- `verificar_sistema` - Verifica se tudo está configurado
- `instalar_dependencias` - Instala todas as dependências
- `descobrir_ip` - Encontra IP para configurar Android
- `cleanup_files` - Remove arquivos inúteis (economiza ~1.5MB)

### Execução
- `iniciar_tudo` - Inicia servidor + cliente + abre navegador
- `iniciar_servidor` - Apenas API (porta 8080)
- `iniciar_cliente` - Apenas cliente web (porta 3001)
- `parar_sistema` - Para todos os serviços

## 📁 Estrutura do Projeto

```
Finanza/
├── server/                 # API REST (Node.js + Express)
│   ├── routes/            # Rotas da API
│   ├── config/            # Configurações
│   ├── middleware/        # Middlewares
│   └── package.json       # Dependências do servidor
├── DESKTOP VERSION/       # Cliente web (HTML/CSS/JS)
│   ├── html/             # Páginas HTML
│   ├── css/              # Estilos
│   ├── js/               # Scripts
│   └── package.json      # Dependências do cliente
├── database/             # Banco de dados SQLite
│   └── finanza.sql       # Schema do banco
├── app/                  # Código fonte Android
├── *.bat                 # Scripts Windows
├── *.sh                  # Scripts Linux/Mac
└── README.md             # Este arquivo
```

## 🔧 Solução de Problemas

### "Node.js não foi encontrado"
1. Instale o Node.js: https://nodejs.org
2. **Use versão 18.19.0 ou superior**
3. Reinicie o terminal/computador
4. Tente novamente

### "Erro ao instalar dependências"
1. Verifique conexão com internet
2. Execute como administrador (Windows) ou sudo (Linux/Mac)
3. Limpe cache: `npm cache clean --force`
4. Delete pastas `node_modules` e tente novamente

### "Porta já está em uso"
1. Execute o script para parar sistema
2. Aguarde alguns segundos
3. Tente novamente

### "Site não abre no navegador"
- ✅ Servidor está rodando? (porta 8080)
- ✅ Cliente está rodando? (porta 3001)
- ✅ URL correta: http://localhost:3001

### "Celular não conecta"
- ✅ PC e celular na mesma rede WiFi?
- ✅ Execute script descobrir_ip
- ✅ Configure IP correto no app (porta 8080)
- ✅ Servidor está rodando?
- ✅ Firewall não está bloqueando?

### "Não aparece dados"
- ✅ Fez login com admin@finanza.com / admin?
- ✅ Banco de dados foi criado? (pasta database/finanza.db)

## 🌐 Configuração para Android

1. Execute o script para descobrir IP:
   ```bash
   # Linux/Mac
   ./descobrir_ip.sh
   
   # Windows
   descobrir_ip.bat
   ```

2. No app Android:
   - Vá em Configurações → Servidor
   - Digite o IP encontrado
   - Mantenha porta 8080
   - Teste conexão

## 📊 Funcionalidades

### Desktop/Web
- ✅ Dashboard financeiro
- ✅ Gestão de contas
- ✅ Lançamentos (receitas/despesas)
- ✅ Relatórios e gráficos
- ✅ Perfil do usuário
- ✅ Painel administrativo

### Android
- ✅ Sincronização com desktop
- ✅ Lançamentos rápidos
- ✅ Visualização de saldo
- ✅ Modo offline básico

## 🔒 Informações de Login

### Administrador Padrão
- **Email**: admin@finanza.com
- **Senha**: admin

### Usuário Teste
- **Email**: teste@finanza.com
- **Senha**: teste123

## 💾 Backup dos Dados

Seus dados ficam em: `database/finanza.db`
- Copie este arquivo para fazer backup
- Para restaurar: substitua o arquivo e reinicie o servidor

## 🛑 Parar Sistema

**Windows**: `parar_sistema.bat`
**Linux/Mac**: `./parar_sistema.sh`

## 🆘 Suporte e Ajuda

### Para reportar problemas:
1. Execute `verificar_sistema` e compartilhe o resultado
2. Informe sistema operacional e versão Node.js
3. Descreva o erro específico que está ocorrendo

### Logs e Depuração
- Os serviços mostram logs detalhados no terminal
- Mantenha os terminais abertos para ver mensagens
- Em caso de erro, copie a mensagem completa

---

**💡 Dica**: Se algo não funcionar, sempre execute `verificar_sistema` primeiro!