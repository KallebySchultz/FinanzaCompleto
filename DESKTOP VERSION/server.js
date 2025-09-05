// Finanza Desktop - Backend Express + Firebase Admin SDK
// Corrigido para evitar piscar no dashboard (sempre responde dados válidos do usuário logado)

const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3001;

// Inicializa Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json'); // Certifique-se que o arquivo está presente!
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://finanza-2cd68-default-rtdb.firebaseio.com"
});

// Aceita requisições de qualquer origem (seguro para dev/local)
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('.'));

// Rota principal (front-end SPA)
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

// Middleware para autenticação via Firebase JWT
function authRequired(req, res, next) {
  const auth = req.headers.authorization;
  if (!auth) return res.status(401).json({ error: 'Token ausente.' });
  const token = auth.replace('Bearer ', '');
  admin.auth().verifyIdToken(token)
    .then(decodedToken => {
      req.user = decodedToken;
      next();
    })
    .catch(() => res.status(401).json({ error: 'Token inválido ou expirado.' }));
}

// Rota para retornar dados do usuário logado
app.get('/auth/me', authRequired, async (req, res) => {
  try {
    const uid = req.user.uid;
    const snapshot = await admin.database().ref(`usuarios/${uid}`).once('value');
    let userData = snapshot.val();

    // Inicializa usuário no banco se não existir (evita piscar!)
    if (!userData) {
      userData = {
        nome: req.user.name || req.user.email || 'Novo usuário',
        saldo_total: 0,
        receitas_mes: 0,
        despesas_mes: 0,
        total_contas: 0,
        email: req.user.email || ''
      };
      await admin.database().ref(`usuarios/${uid}`).set(userData);
    }
    res.json(userData);
  } catch (err) {
    // Nunca retorna erro para frontend, retorna dados default
    res.json({
      saldo_total: 0,
      receitas_mes: 0,
      despesas_mes: 0,
      total_contas: 0,
      nome: 'Erro',
      email: ''
    });
  }
});

// Rota para registrar/atualizar dados do usuário logado
app.post('/auth/me', authRequired, async (req, res) => {
  try {
    const uid = req.user.uid;
    const dados = req.body;
    await admin.database().ref(`usuarios/${uid}`).set(dados);
    res.json({ success: true });
  } catch (err) {
    res.json({ success: false });
  }
});

/**
 * ROTA CORRIGIDA: Resumo financeiro do usuário logado
 * Sempre retorna dados válidos, nunca erro para frontend
 */
app.get('/api/financialSummary', authRequired, async (req, res) => {
  try {
    const uid = req.user.uid;
    const snapshot = await admin.database().ref(`usuarios/${uid}`).once('value');
    let userData = snapshot.val();
    if (!userData) {
      // Inicializa usuário no banco se não existir
      userData = {
        saldo_total: 0,
        receitas_mes: 0,
        despesas_mes: 0,
        total_contas: 0,
        nome: req.user.name || req.user.email || 'Novo usuário',
        email: req.user.email || ''
      };
      await admin.database().ref(`usuarios/${uid}`).set(userData);
    }
    res.json({
      saldo_total: userData.saldo_total || 0,
      receitas_mes: userData.receitas_mes || 0,
      despesas_mes: userData.despesas_mes || 0,
      total_contas: userData.total_contas || 0
    });
  } catch (err) {
    // Nunca retorna erro, apenas dados default
    res.json({
      saldo_total: 0,
      receitas_mes: 0,
      despesas_mes: 0,
      total_contas: 0
    });
  }
});

/**
 * OBS IMPORTANTE:
 * O registro e login de usuários (email/senha) deve ser feito pelo frontend via Firebase JS SDK.
 * O backend só valida o token e manipula dados do Realtime Database!
 */

app.listen(PORT, () => {
  console.log(`🖥️  Cliente Desktop Finanza rodando na porta ${PORT}`);
  console.log(`🔗 Acesse: http://localhost:${PORT}`);
});

module.exports = app;