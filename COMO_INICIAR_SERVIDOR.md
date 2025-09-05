# 🚀 Como Iniciar o Servidor Finanza

Este guia explica as diferentes formas de iniciar o servidor do sistema Finanza.

## 📋 Pré-requisitos

- **Node.js**: versão 18.19.0 ou superior (recomendado: versão LTS mais recente)
- **npm**: versão 8.0.0 ou superior
- **Conexão com internet**: para Firebase e instalação de dependências

## 🔍 Verificação do Sistema

Antes de iniciar, verifique se seu sistema está pronto:

```bash
# Linux/macOS
./verificar_sistema.sh

# Windows
verificar_sistema.bat
```

## 🎯 Opções de Inicialização

### 1. 🚀 Inicialização Completa (RECOMENDADO)

Inicia servidor + cliente desktop automaticamente:

```bash
# Linux/macOS
./iniciar_tudo.sh

# Windows
iniciar_tudo.bat
```

**O que acontece:**
- ✅ Inicia o servidor API na porta 8080
- ✅ Inicia o cliente desktop na porta 3001  
- ✅ Abre automaticamente no navegador
- ✅ Sistema completo pronto para uso

### 2. 🖥️ Apenas Servidor API

Se você só precisa do servidor (para desenvolvimento ou uso com mobile):

```bash
# Linux/macOS
./iniciar_servidor.sh

# Windows
iniciar_servidor.bat
```

**O que acontece:**
- ✅ Inicia apenas o servidor API na porta 8080
- ✅ Health check disponível em: `http://localhost:8080/api/health`
- ✅ Pronto para receber conexões do app móvel

### 3. 🌐 Apenas Cliente Desktop

Se o servidor já está rodando e você quer só o cliente:

```bash
# Linux/macOS
./iniciar_cliente.sh

# Windows
iniciar_cliente.bat
```

**O que acontece:**
- ✅ Inicia apenas o cliente desktop na porta 3001
- ✅ Conecta-se ao servidor na porta 8080
- ✅ Interface web disponível em: `http://localhost:3001`

### 4. 📦 Instalação Manual de Dependências

Se for a primeira vez ou houver problemas:

```bash
# Linux/macOS
./instalar_dependencias.sh

# Windows
instalar_dependencias.bat
```

## 🔧 Comandos Manuais

### Servidor apenas:
```bash
cd server
npm install  # primeira vez
npm start    # iniciar servidor
```

### Cliente desktop apenas:
```bash
cd "DESKTOP VERSION"
npm install  # primeira vez  
npm start    # iniciar cliente
```

## 📍 URLs de Acesso

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **Cliente Desktop** | `http://localhost:3001` | Interface web principal |
| **Health Check** | `http://localhost:8080/api/health` | Status do servidor |
| **API Base** | `http://localhost:8080/api/` | Endpoints da API |

## 🔑 Login Padrão

- **Email**: `admin@finanza.com`
- **Senha**: `admin`

## 📱 Configuração para Android

1. Descubra seu IP local:
   ```bash
   # Linux/macOS
   ./descobrir_ip.sh
   
   # Windows
   descobrir_ip.bat
   ```

2. Configure no app Android: `[SEU_IP]:8080`
   - Exemplo: `192.168.1.100:8080`

## 🛑 Como Parar o Sistema

```bash
# Linux/macOS
./parar_sistema.sh

# Windows
parar_sistema.bat

# Ou manualmente: Ctrl+C nos terminais
```

## 🔍 Troubleshooting

### ❌ Problema: "Node.js não encontrado"
**Solução**: Instale Node.js LTS de [nodejs.org](https://nodejs.org)

### ❌ Problema: "Porta já em uso"
**Solução**: 
```bash
# Verificar o que está usando a porta
lsof -ti:8080  # Linux/macOS
netstat -ano | findstr :8080  # Windows

# Matar processo se necessário
kill -9 <PID>  # Linux/macOS
taskkill /PID <PID> /F  # Windows
```

### ❌ Problema: "Erro ao instalar dependências"
**Solução**:
```bash
# Limpar cache npm
npm cache clean --force

# Deletar node_modules e reinstalar
rm -rf node_modules package-lock.json  # Linux/macOS
npm install
```

### ❌ Problema: "Firebase connection error"
**Verificação**: 
- Conexão com internet ativa
- Firewall não está bloqueando
- URL do Firebase: `https://finanza-2cd68-default-rtdb.firebaseio.com/`

## 🎯 Resumo Rápido

**Para usar o sistema completo:**
```bash
./iniciar_tudo.sh    # Linux/macOS
iniciar_tudo.bat     # Windows
```

**Apenas para desenvolvimento da API:**
```bash
./iniciar_servidor.sh    # Linux/macOS  
iniciar_servidor.bat     # Windows
```

## 📊 Status dos Serviços

Após iniciar, você deve ver:

1. **Servidor rodando**:
   ```
   🚀 Servidor Finanza API rodando na porta 8080
   ✅ Firebase Realtime Database inicializado
   🔗 Database URL: https://finanza-2cd68-default-rtdb.firebaseio.com/
   ```

2. **Cliente rodando**:
   ```
   🌐 Cliente desktop rodando na porta 3001
   🔗 Conecta-se ao servidor API na porta 8080
   ```

3. **Sistema sincronizado**: Dados em tempo real entre desktop e mobile

---

## 💡 Dicas Importantes

- ⚠️ **Mantenha os terminais abertos** enquanto usar o sistema
- 🔄 **Para atualizações**, pare o sistema e inicie novamente
- 📱 **Para mobile**, configure o IP da sua rede local
- 🌐 **Para acesso externo**, configure seu roteador adequadamente
- 🔒 **Para produção**, altere as credenciais padrão

---

*Precisa de ajuda? Verifique os logs nos terminais ou execute `./verificar_sistema.sh` para diagnóstico.*