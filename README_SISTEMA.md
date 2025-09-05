# 🚀 FINANZA - Sistema de Gestão Financeira Pessoal

## 📋 Visão Geral
Sistema completo de gestão financeira pessoal com:
- **API REST** (Node.js + Express + SQLite)
- **Cliente Desktop** (HTML/CSS/JS)
- **App Android** (conecta via API)

## 🖥️ Compatibilidade de Plataformas

### Windows
Use os arquivos `.bat`:
- `iniciar_tudo.bat` - Inicia sistema completo
- `iniciar_servidor.bat` - Apenas servidor API
- `iniciar_cliente.bat` - Apenas cliente desktop
- `parar_sistema.bat` - Para todos os serviços
- `descobrir_ip.bat` - Encontra IP para Android

### Linux/Mac
Use os arquivos `.sh`:
- `./iniciar_tudo.sh` - Inicia sistema completo
- `./iniciar_servidor.sh` - Apenas servidor API
- `./iniciar_cliente.sh` - Apenas cliente desktop
- `./parar_sistema.sh` - Para todos os serviços
- `./descobrir_ip.sh` - Encontra IP para Android

## ⚡ Início Rápido

### 1. Verificar Sistema
```bash
# Linux/Mac
./verificar_sistema.sh

# Windows
verificar_sistema.bat
```

### 2. Instalar Dependências
```bash
# Linux/Mac
./instalar_dependencias.sh

# Windows
instalar_dependencias.bat
```

### 3. Iniciar Sistema
```bash
# Linux/Mac
./iniciar_tudo.sh

# Windows
iniciar_tudo.bat
```

### 4. Acessar Sistema
- **Desktop**: http://localhost:3001
- **Login**: admin@finanza.com / admin

## 📋 Requisitos

### Obrigatórios
- **Node.js** 18.19.0+ (recomendado: versão LTS mais recente)
- **npm** 8+
- **Sistema**: Windows 10/11, Linux, ou macOS

### ⚠️ Versões Incompatíveis
- **Node.js v18.18.x**: Possui problemas conhecidos de compatibilidade
- Use **v18.19.0 ou superior** para evitar erros

### Verificar se Node.js está instalado
```bash
node --version
npm --version
```

Se não estiver instalado:
1. Acesse: https://nodejs.org
2. Baixe a versão **LTS** (recomendada)
3. Instale normalmente
4. Reinicie o terminal/computador

## 🔧 Solução de Problemas

### "Node.js não foi encontrado"
1. Instale o Node.js: https://nodejs.org
2. **Use versão 18.19.0 ou superior** (v18.18.x tem problemas conhecidos)
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

### "Celular não conecta"
1. ✅ PC e celular na mesma rede WiFi?
2. ✅ Execute script descobrir_ip
3. ✅ Configure IP correto no app (porta 8080)
4. ✅ Servidor está rodando?
5. ✅ Firewall não está bloqueando?

### "Site não abre no navegador"
1. ✅ Servidor rodando? (porta 8080)
2. ✅ Cliente rodando? (porta 3001)
3. ✅ URL correta: http://localhost:3001

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

## 📁 Estrutura do Projeto

```
Finanza/
├── server/                 # API REST (Node.js)
│   ├── routes/            # Rotas da API
│   ├── config/            # Configurações
│   ├── middleware/        # Middlewares
│   └── package.json       # Dependências do servidor
├── DESKTOP VERSION/       # Cliente web
│   ├── html/             # Páginas HTML
│   ├── css/              # Estilos
│   ├── js/               # Scripts
│   └── package.json      # Dependências do cliente
├── database/             # Banco de dados SQLite
├── app/                  # Código fonte Android
├── *.bat                 # Scripts Windows
├── *.sh                  # Scripts Linux/Mac
└── README.md             # Este arquivo
```

## 🚀 Scripts Disponíveis

### Utilitários
- `verificar_sistema` - Verifica se tudo está configurado
- `instalar_dependencias` - Instala todas as dependências
- `descobrir_ip` - Encontra IP para configurar Android

### Execução
- `iniciar_tudo` - Inicia servidor + cliente + abre navegador
- `iniciar_servidor` - Apenas API (porta 8080)
- `iniciar_cliente` - Apenas cliente web (porta 3001)
- `parar_sistema` - Para todos os serviços

## 🔒 Informações de Login

### Administrador Padrão
- **Email**: admin@finanza.com
- **Senha**: admin

### Usuário Teste
- **Email**: teste@finanza.com
- **Senha**: teste123

## 🆘 Suporte e Ajuda

### Para reportar problemas:
1. Execute `verificar_sistema` e compartilhe o resultado
2. Informe sistema operacional e versão Node.js
3. Descreva o erro específico que está ocorrendo

### Logs e Depuração
- Os serviços mostram logs detalhados no terminal
- Mantenha os terminais abertos para ver mensagens
- Em caso de erro, copie a mensagem completa

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

## 🔄 Atualizações

Para atualizar o sistema:
1. Pare todos os serviços
2. Faça backup do banco de dados
3. Atualize os arquivos
4. Execute `instalar_dependencias`
5. Reinicie o sistema

---

**💡 Dica**: Sempre execute `verificar_sistema` antes de reportar problemas!