const express = require('express');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// Registro de usuário
router.post('/register', async (req, res) => {
  try {
    const { nome, email, senha } = req.body;

    // Validações básicas
    if (!nome || !email || !senha) {
      return res.status(400).json({ error: 'Nome, email e senha são obrigatórios' });
    }

    if (senha.length < 6) {
      return res.status(400).json({ error: 'Senha deve ter pelo menos 6 caracteres' });
    }

    // Verificar se email já existe
    const existingUser = await db.get('SELECT id FROM usuarios WHERE email = ?', [email]);
    if (existingUser) {
      return res.status(409).json({ error: 'Email já está em uso' });
    }

    // Hash da senha
    const hashedPassword = await bcrypt.hash(senha, 10);
    
    // Criar usuário
    const result = await db.run(
      'INSERT INTO usuarios (nome, email, senha, data_criacao) VALUES (?, ?, ?, ?)',
      [nome, email, hashedPassword, Date.now()]
    );

    // Criar conta padrão para o usuário
    await db.run(
      'INSERT INTO contas (nome, saldo_inicial, usuario_id) VALUES (?, ?, ?)',
      ['Conta Principal', 0, result.id]
    );

    // Gerar token
    const token = jwt.sign({ userId: result.id }, process.env.JWT_SECRET, { expiresIn: '24h' });

    res.status(201).json({
      message: 'Usuário criado com sucesso',
      token,
      user: {
        id: result.id,
        nome,
        email
      }
    });
  } catch (error) {
    console.error('Erro no registro:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Login
router.post('/login', async (req, res) => {
  try {
    const { email, senha } = req.body;

    if (!email || !senha) {
      return res.status(400).json({ error: 'Email e senha são obrigatórios' });
    }

    // Buscar usuário
    const user = await db.get('SELECT id, nome, email, senha FROM usuarios WHERE email = ?', [email]);
    if (!user) {
      return res.status(401).json({ error: 'Email ou senha incorretos' });
    }

    // Verificar senha
    const isValidPassword = await bcrypt.compare(senha, user.senha);
    if (!isValidPassword) {
      return res.status(401).json({ error: 'Email ou senha incorretos' });
    }

    // Gerar token
    const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET, { expiresIn: '24h' });

    res.json({
      message: 'Login realizado com sucesso',
      token,
      user: {
        id: user.id,
        nome: user.nome,
        email: user.email
      }
    });
  } catch (error) {
    console.error('Erro no login:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Verificar token
router.get('/verify', authenticateToken, (req, res) => {
  res.json({
    valid: true,
    user: req.user
  });
});

// Logout (invalidar token - em uma implementação real, você manteria uma blacklist)
router.post('/logout', authenticateToken, (req, res) => {
  res.json({ message: 'Logout realizado com sucesso' });
});

module.exports = router;