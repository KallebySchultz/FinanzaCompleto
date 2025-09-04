# 📱 Configuração de Rede WiFi - Finanza Mobile + Desktop

Este guia explica como conectar o aplicativo Android Finanza com o servidor desktop na mesma rede WiFi.

## 🚀 Iniciando o Sistema

### 1. Iniciar o Servidor API
```batch
# Execute no Windows:
iniciar_servidor.bat
```
- O servidor rodará na **porta 8080** (compatível com Android)
- Acesso local: `http://localhost:8080`

### 2. Iniciar o Cliente Desktop
```batch
# Execute no Windows:
iniciar_cliente.bat
```
- O cliente rodará na **porta 3001**
- Acesso: `http://localhost:3001`

## 🌐 Configurando Conexão de Rede

### Passo 1: Descobrir o IP do Computador

**No Windows:**
```cmd
ipconfig
```
Procure pelo adaptador da sua rede WiFi e anote o "Endereço IPv4".
Exemplo: `192.168.1.100`

**No Mac/Linux:**
```bash
ifconfig
```

### Passo 2: Configurar no Android

1. **Abra o app Finanza** no seu celular
2. **Vá para Configurações** (Settings)
3. **Configure o servidor:**
   - **Host/IP:** O IP do seu computador (ex: `192.168.1.100`)
   - **Porta:** `8080`
4. **Teste a conexão** usando o botão "Testar Conexão"
5. **Salve as configurações**

### Exemplo de Configuração:
```
Host: 192.168.1.100
Porta: 8080
URL completa: http://192.168.1.100:8080
```

## 🔧 Configurações Técnicas

### Portas Utilizadas:
- **Servidor API:** 8080 (para Android e Desktop)
- **Cliente Desktop:** 3001 (apenas interface web)

### CORS e Segurança:
- CORS configurado para aceitar conexões da rede local
- Rate limiting aplicado para segurança
- Conexões HTTPs recomendadas em produção

## 🛠️ Solucionando Problemas

### Problema: "Erro de Conexão" no Android

**Soluções:**
1. **Verifique se ambos dispositivos estão na mesma rede WiFi**
2. **Confirme o IP do computador:**
   ```cmd
   ipconfig
   ```
3. **Teste a conexão do navegador do celular:**
   - Abra o navegador do celular
   - Acesse: `http://[IP_DO_COMPUTADOR]:8080/api/health`
   - Deve retornar status do servidor

4. **Verifique o firewall do Windows:**
   - Permita a porta 8080 no firewall
   - Ou temporariamente desabilite o firewall para teste

### Problema: Servidor não inicia

**Soluções:**
1. **Instale Node.js:** https://nodejs.org
2. **Execute como administrador**
3. **Verifique se a porta 8080 está disponível:**
   ```cmd
   netstat -an | find "8080"
   ```

### Problema: Desktop não conecta ao servidor

**Verificar:**
1. Servidor API está rodando na porta 8080
2. Arquivo `js/api.js` aponta para `localhost:8080`

## 📋 Banco de Dados

### Localização:
- **Arquivo:** `database/finanza.db`
- **Schema:** `finanza_completo.sql`

### Usuário Padrão:
- **Email:** admin@finanza.com
- **Senha:** admin

### Dados de Exemplo:
- Contas pré-criadas (Corrente, Poupança)
- Categorias padrão (Alimentação, Transporte, etc.)
- Lançamentos de exemplo

## 🎯 Fluxo de Sincronização

1. **Android** conecta ao servidor via WiFi (IP:8080)
2. **Desktop** conecta ao servidor via localhost:8080
3. **Dados compartilhados** através do banco SQLite
4. **Sincronização automática** quando dispositivos estão online

## 📞 Suporte

### Comandos Úteis:

**Ver IP atual:**
```cmd
ipconfig | find "IPv4"
```

**Testar conectividade:**
```cmd
ping [IP_DO_DISPOSITIVO]
```

**Verificar porta:**
```cmd
telnet [IP] 8080
```

### Logs do Servidor:
- Verifique a janela do `iniciar_servidor.bat` para logs de conexão
- Logs incluem tentativas de conexão e erros

---

## ✅ Checklist de Configuração

- [ ] Node.js instalado
- [ ] Servidor API rodando na porta 8080
- [ ] Cliente Desktop rodando na porta 3001
- [ ] IP do computador identificado
- [ ] Android configurado com IP correto
- [ ] Teste de conexão realizado com sucesso
- [ ] Ambos dispositivos na mesma rede WiFi
- [ ] Firewall configurado (se necessário)

**🎉 Sistema pronto para uso! Agora você pode sincronizar dados entre Android e Desktop!**