const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3001;

// Inicializa Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://finanza-2cd68-default-rtdb.firebaseio.com"
});

// Aceita requisições de qualquer origem
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('.'));

// Rota principal
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

// Middleware de autenticação Firebase JWT
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

// Garante sempre resposta válida para /auth/me
app.get('/auth/me', authRequired, async (req, res) => {
  try {
    const uid = req.user.uid;
    const ref = admin.database().ref(`usuarios/${uid}`);
    let userData = (await ref.once('value')).val();

    if (!userData) {
      userData = {
        nome: req.user.name || req.user.email || 'Novo usuário',
        saldo_total: 0,
        receitas_mes: 0,
        despesas_mes: 0,
        total_contas: 0,
        email: req.user.email || ''
      };
      await ref.set(userData);
    }
    res.status(200).json(userData);
  } catch (err) {
    // NUNCA retorna erro fatal ao dashboard
    res.status(200).json({
      saldo_total: 0,
      receitas_mes: 0,
      despesas_mes: 0,
      total_contas: 0,
      nome: 'Erro',
      email: ''
    });
  }
});

// Atualiza dados do usuário
app.post('/auth/me', authRequired, async (req, res) => {
  try {
    const uid = req.user.uid;
    await admin.database().ref(`usuarios/${uid}`).set(req.body);
    res.status(200).json({ success: true });
  } catch (err) {
    res.status(200).json({ success: false });
  }
});

// Responde sempre campos válidos para o dashboard
app.get('/api/financialSummary', authRequired, async (req, res) => {
  try {
    const uid = req.user.uid;
    const ref = admin.database().ref(`usuarios/${uid}`);
    let userData = (await ref.once('value')).val();

    if (!userData) {
      userData = {
        saldo_total: 0,
        receitas_mes: 0,
        despesas_mes: 0,
        total_contas: 0,
        nome: req.user.name || req.user.email || 'Novo usuário',
        email: req.user.email || ''
      };
      await ref.set(userData);
    }
    // Sempre responde os campos esperados
    res.status(200).json({
      saldo_total: userData.saldo_total || 0,
      receitas_mes: userData.receitas_mes || 0,
      despesas_mes: userData.despesas_mes || 0,
      total_contas: userData.total_contas || 0
    });
  } catch (err) {
    // Nunca retorna erro, sempre objeto default
    res.status(200).json({
      saldo_total: 0,
      receitas_mes: 0,
      despesas_mes: 0,
      total_contas: 0
    });
  }
});

app.listen(PORT, () => {
  console.log(`🖥️  Cliente Desktop Finanza rodando na porta ${PORT}`);
  console.log(`🔗 Acesse: http://localhost:${PORT}`);
});

module.exports = app;