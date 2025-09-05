# Finanza Firebase Configuration Guide

## 🔥 Configuração do Firebase

Para configurar o Firebase no projeto Finanza, siga os passos abaixo:

### 1. Criar Projeto no Firebase

1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Clique em "Adicionar projeto"
3. Nome do projeto: `finanza-app`
4. Ative o Google Analytics (opcional)

### 2. Configurar Realtime Database

1. No painel lateral, clique em "Realtime Database"
2. Clique em "Criar banco de dados"
3. Escolha o local (ex: us-central1)
4. Comece no modo de teste (regras podem ser ajustadas depois)

### 3. Obter Configurações

#### Para o Servidor (Node.js)

No Firebase Console:
1. Vá em Configurações do projeto (⚙️)
2. Aba "Geral"
3. Role até "Seus aplicativos"
4. Clique em "Adicionar app" → Web
5. Copie a configuração

Edite o arquivo `server/config/firebase.js`:

```javascript
const firebaseConfig = {
  apiKey: "sua-api-key",
  authDomain: "finanza-app.firebaseapp.com",
  databaseURL: "https://finanza-app-default-rtdb.firebaseio.com/",
  projectId: "finanza-app",
  storageBucket: "finanza-app.appspot.com",
  messagingSenderId: "123456789",
  appId: "1:123456789:web:abcdef"
};
```

#### Para o Android

1. No Firebase Console, adicione um app Android
2. Package name: `com.example.finanza`
3. Baixe o arquivo `google-services.json`
4. Coloque em `app/google-services.json`

### 4. Configurar Regras de Segurança

No Realtime Database, configure as regras:

```json
{
  "rules": {
    "usuarios": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "contas": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "lancamentos": {
      "$uid": {
        ".read": "$uid === auth.uid", 
        ".write": "$uid === auth.uid"
      }
    },
    "categorias": {
      ".read": true,
      ".write": "auth != null"
    }
  }
}
```

### 5. Variáveis de Ambiente

Adicione no arquivo `server/.env`:

```env
# Firebase Configuration
FIREBASE_PROJECT_ID=finanza-app
FIREBASE_DATABASE_URL=https://finanza-app-default-rtdb.firebaseio.com/
FIREBASE_API_KEY=sua-api-key
```

### 6. Configurar Android App

No arquivo `app/src/main/java/com/example/finanza/network/FirebaseClient.java`:

```java
private static final String FIREBASE_URL = "https://finanza-app-default-rtdb.firebaseio.com";
```

### 7. Testar Conexão

Execute o teste de conexão:

```bash
cd server
npm start
```

Verifique no console se aparece:
```
✅ Firebase Realtime Database inicializado
🔗 Database URL: https://finanza-app-default-rtdb.firebaseio.com/
```

### 8. Estrutura de Dados no Firebase

O Firebase será estruturado assim:

```
finanza-app/
├── usuarios/
│   └── [userId]/
│       ├── nome: "João"
│       ├── email: "joao@email.com"
│       └── data_criacao: 1234567890
├── contas/
│   └── [userId]/
│       └── [contaId]/
│           ├── nome: "Conta Corrente"
│           ├── saldo_inicial: 1000.00
│           └── data_criacao: 1234567890
├── lancamentos/
│   └── [userId]/
│       └── [lancamentoId]/
│           ├── valor: -150.00
│           ├── descricao: "Supermercado"
│           ├── tipo: "despesa"
│           ├── conta_id: "conta123"
│           ├── categoria_id: "cat456"
│           └── data: 1234567890
└── categorias/
    └── [categoriaId]/
        ├── nome: "Alimentação"
        ├── cor_hex: "#FF6B6B"
        └── tipo: "despesa"
```

## 🔧 Troubleshooting

### Problemas Comuns

1. **Erro de permissão no Firebase**
   - Verifique as regras de segurança
   - Certifique-se de que o usuário está autenticado

2. **App Android não conecta**
   - Verifique se o `google-services.json` está correto
   - Confirme o package name no Firebase

3. **Erro de inicialização**
   - Verifique as configurações no arquivo `firebase.js`
   - Confirme se a URL do database está correta

### Logs Úteis

Para debug, ative logs detalhados:

```javascript
// No arquivo firebase.js
console.log('Firebase Config:', firebaseConfig);
console.log('Database URL:', this.database);
```

## 🚀 Próximos Passos

Após a configuração:

1. Teste a sincronização entre app e servidor
2. Configure backup automático
3. Implemente autenticação Firebase (opcional)
4. Configure notificações push
5. Otimize as regras de segurança

---

**Nota:** Mantenha as credenciais do Firebase seguras e nunca as commite no repositório público!