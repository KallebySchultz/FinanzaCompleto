# 📱💰 FINANZA - Guia de Instalação Super Fácil

> **Sistema de gestão financeira pessoal com sincronização entre PC e celular**

## 🎯 O que este sistema faz?

- ✅ Controla suas receitas e despesas
- ✅ Funciona no computador (site local)
- ✅ Funciona no celular Android
- ✅ Sincroniza dados entre PC e celular via WiFi
- ✅ Banco de dados local (não precisa de internet)

---

## 🚀 INSTALAÇÃO RÁPIDA - 3 PASSOS

### 📋 REQUISITOS
- Windows 10/11
- Node.js instalado
- Celular Android na mesma rede WiFi do PC

---

### ✅ **PASSO 1: Instalar Node.js**

1. Acesse: https://nodejs.org
2. Baixe a versão "LTS" (recomendada)
3. **IMPORTANTE:** Use versão 18.19.0 ou superior
4. Instale normalmente (next, next, next...)
5. Reinicie o computador

**⚠️ Versão v18.18.x possui problemas conhecidos!**

**Como testar se funcionou:**
- Abra o Prompt de Comando (cmd)
- Digite: `node --version`
- Deve aparecer algo como: `v20.x.x` ou `v18.19.x`

---

### ✅ **PASSO 2: Iniciar o Sistema**

1. **Abrir o servidor:**
   - Clique duas vezes em: `iniciar_servidor.bat`
   - Uma janela preta vai abrir
   - Aguarde aparecer: "Servidor Finanza API rodando na porta 8080"
   - **DEIXE ESTA JANELA ABERTA!** (Não feche)

2. **Abrir o cliente desktop:**
   - Clique duas vezes em: `iniciar_cliente.bat`
   - Outra janela preta vai abrir
   - Aguarde aparecer: "Cliente Desktop Finanza rodando na porta 3001"
   - **DEIXE ESTA JANELA ABERTA!** (Não feche)

3. **Acessar no navegador:**
   - Abra o Chrome/Edge/Firefox
   - Digite: `http://localhost:3001`
   - Deve aparecer a tela de login

---

### ✅ **PASSO 3: Configurar o Celular**

1. **Descobrir o IP do seu PC:**
   - Clique duas vezes em: `descobrir_ip.bat`
   - Procure por "Adaptador de Rede sem Fio"
   - Anote o número do "Endereço IPv4" (ex: 192.168.1.100)

2. **No celular Android:**
   - Instale o app Finanza (arquivo APK)
   - Abra o app
   - Vá em "Configurações" (ícone de engrenagem)
   - Configure:
     - **Host:** 192.168.1.100 (substitua pelo seu IP)
     - **Porta:** 8080
   - Toque em "Salvar Configurações"
   - Toque em "Testar Conexão"

---

## 🔐 LOGIN PADRÃO

**Para primeiro acesso:**
- **Email:** admin@finanza.com
- **Senha:** admin

*Você pode criar outros usuários depois!*

---

## 📊 COMO USAR

### No PC (http://localhost:3001):
- ✅ Cadastrar receitas e despesas
- ✅ Ver relatórios e gráficos
- ✅ Gerenciar contas e categorias

### No Celular:
- ✅ Lançar receitas/despesas rapidamente
- ✅ Ver saldo das contas
- ✅ Sincronizar com o PC automaticamente

---

## ❗ SOLUÇÃO DE PROBLEMAS

### **"Node.js não foi encontrado"**
- Instale o Node.js: https://nodejs.org
- **Use versão 18.19.0 ou superior** (v18.18.x tem problemas)
- Reinicie o computador
- Tente novamente

### **"Erro ao instalar dependências"**
- Verifique sua conexão com a internet
- Feche as janelas e tente novamente
- Execute como administrador

### **"Celular não conecta"**
- ✅ PC e celular na mesma rede WiFi?
- ✅ IP está correto? (use descobrir_ip.bat)
- ✅ Porta é 8080?
- ✅ Windows Firewall bloqueando? (permita o Node.js)

### **"Site não abre no navegador"**
- ✅ Servidor está rodando? (janela preta aberta)
- ✅ Cliente está rodando? (segunda janela preta)
- ✅ URL correta: http://localhost:3001

### **"Não aparece dados"**
- ✅ Fez login com admin@finanza.com / admin?
- ✅ Banco de dados foi criado? (pasta database/finanza.db)

---

## 📱 INSTALAÇÃO DO APP ANDROID

1. No seu Android, vá em:
   - **Configurações > Segurança > Fontes Desconhecidas**
   - Habilite a instalação de apps de fontes desconhecidas

2. Copie o arquivo `app-release.apk` para o celular
3. Abra o arquivo no celular e instale
4. Configure o IP do servidor (conforme Passo 3 acima)

---

## 🔧 COMANDOS ÚTEIS

### Para parar o sistema:
- Feche as janelas pretas (Ctrl+C) ou feche a janela

### Para reiniciar:
- Execute novamente os arquivos .bat

### Para ver o IP:
- Execute: `descobrir_ip.bat`

### Para verificar se está funcionando:
- Abra: http://localhost:8080/api/health
- Deve aparecer: `{"status":"OK",...}`

---

## 💾 BACKUP DOS DADOS

Seus dados ficam em: `database/finanza.db`
- Copie este arquivo para fazer backup
- Para restaurar: substitua o arquivo e reinicie o servidor

---

## 📞 ESTRUTURA DO PROJETO

```
Finanza/
├── iniciar_servidor.bat    ← Clique aqui primeiro
├── iniciar_cliente.bat     ← Clique aqui segundo  
├── descobrir_ip.bat        ← Para descobrir seu IP
├── server/                 ← Servidor da API
├── DESKTOP VERSION/        ← Cliente web (site)
├── app/                    ← Código fonte Android
├── database/               ← Banco de dados SQLite
│   └── finanza.db         ← Seus dados ficam aqui
└── README_INSTALACAO.md   ← Este arquivo
```

---

## ✅ CHECKLIST DE VERIFICAÇÃO

Depois de seguir todos os passos, verifique:

- [ ] Node.js instalado (node --version funciona)
- [ ] Servidor rodando (janela 1 aberta)
- [ ] Cliente rodando (janela 2 aberta)  
- [ ] Site abre em http://localhost:3001
- [ ] Login funciona (admin@finanza.com / admin)
- [ ] IP do PC descoberto
- [ ] App Android instalado
- [ ] App configurado com IP correto
- [ ] Teste de conexão no app passou
- [ ] Dados sincronizam entre PC e celular

---

## 🎉 PRONTO!

Se chegou até aqui, seu sistema Finanza está funcionando!

**Próximos passos:**
1. Cadastre suas contas bancárias
2. Crie categorias personalizadas  
3. Lance suas primeiras receitas e despesas
4. Explore os relatórios e gráficos

**Dica:** Use o celular para lançamentos rápidos no dia-a-dia e o PC para análises detalhadas!

---

*📧 Em caso de dúvidas, verifique se seguiu todos os passos exatamente como descritos acima.*