# ✅ Resposta: Como dar start no servidor?

## 🎯 Resposta Direta

Para iniciar o servidor Finanza, você tem várias opções:

### 1. 🚀 **RECOMENDADO**: Sistema Completo
```bash
# Linux/macOS
./iniciar_tudo.sh

# Windows  
iniciar_tudo.bat
```
☑️ Inicia servidor + cliente desktop + abre navegador automaticamente

### 2. 🖥️ Apenas o Servidor API
```bash
# Linux/macOS
./iniciar_servidor.sh

# Windows
iniciar_servidor.bat
```
☑️ Inicia apenas o servidor na porta 8080

### 3. 📦 Manual (para desenvolvedores)
```bash
cd server
npm install    # primeira vez
npm start      # iniciar
```

## 📍 URLs Importantes

- **Sistema Web**: http://localhost:3001
- **API Health**: http://localhost:8080/api/health  
- **Login**: admin@finanza.com / admin

## 🔍 Verificação Rápida

1. **Verificar pré-requisitos**: `./verificar_sistema.sh`
2. **Instalar dependências**: `./instalar_dependencias.sh`
3. **Iniciar sistema**: `./iniciar_tudo.sh`

## 📖 Documentação Completa

Ver: **[COMO_INICIAR_SERVIDOR.md](COMO_INICIAR_SERVIDOR.md)** para guia completo com troubleshooting.

---

**Status**: ✅ Sistema testado e funcionando
**Ambiente**: Node.js v20.19.4, npm v10.8.2, Firebase conectado
**Dependências**: ✅ Instaladas (servidor: 255 pacotes, cliente: 185 pacotes)