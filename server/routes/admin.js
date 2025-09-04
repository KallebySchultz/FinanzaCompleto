const express = require('express');
const bcrypt = require('bcrypt');
const db = require('../config/database');
const { authenticateToken, requireAdmin } = require('../middleware/auth');

const router = express.Router();

// Aplicar middleware de autenticação e autorização em todas as rotas admin
router.use(authenticateToken);
router.use(requireAdmin);

// Listar todos os usuários
router.get('/users', async (req, res) => {
  try {
    const { limit = 50, offset = 0, search = '' } = req.query;

    let whereClause = '';
    let params = [];

    if (search) {
      whereClause = 'WHERE nome LIKE ? OR email LIKE ?';
      params.push(`%${search}%`, `%${search}%`);
    }

    const users = await db.all(`
      SELECT 
        u.id,
        u.nome,
        u.email,
        u.data_criacao,
        COUNT(DISTINCT c.id) as total_contas,
        COUNT(l.id) as total_transacoes,
        COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as saldo_total
      FROM usuarios u
      LEFT JOIN contas c ON u.id = c.usuario_id
      LEFT JOIN lancamentos l ON u.id = l.usuario_id
      ${whereClause}
      GROUP BY u.id, u.nome, u.email, u.data_criacao
      ORDER BY u.data_criacao DESC
      LIMIT ? OFFSET ?
    `, [...params, parseInt(limit), parseInt(offset)]);

    const totalResult = await db.get(`
      SELECT COUNT(*) as count 
      FROM usuarios 
      ${whereClause}
    `, params);

    res.json({
      users,
      total: totalResult.count,
      limit: parseInt(limit),
      offset: parseInt(offset)
    });
  } catch (error) {
    console.error('Erro ao listar usuários:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter detalhes de um usuário específico
router.get('/users/:id', async (req, res) => {
  try {
    const user = await db.get(`
      SELECT 
        u.id,
        u.nome,
        u.email,
        u.data_criacao,
        COUNT(DISTINCT c.id) as total_contas,
        COUNT(l.id) as total_transacoes,
        COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as saldo_total
      FROM usuarios u
      LEFT JOIN contas c ON u.id = c.usuario_id
      LEFT JOIN lancamentos l ON u.id = l.usuario_id
      WHERE u.id = ?
      GROUP BY u.id, u.nome, u.email, u.data_criacao
    `, [req.params.id]);

    if (!user) {
      return res.status(404).json({ error: 'Usuário não encontrado' });
    }

    // Obter contas do usuário
    const accounts = await db.all(`
      SELECT 
        c.id,
        c.nome,
        c.saldo_inicial,
        COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as movimentacao,
        c.saldo_inicial + COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as saldo_atual
      FROM contas c
      LEFT JOIN lancamentos l ON c.id = l.conta_id
      WHERE c.usuario_id = ?
      GROUP BY c.id, c.nome, c.saldo_inicial
      ORDER BY c.nome
    `, [req.params.id]);

    // Transações recentes
    const recentTransactions = await db.all(`
      SELECT 
        l.id,
        l.valor,
        l.data,
        l.descricao,
        l.tipo,
        c.nome as conta_nome,
        cat.nome as categoria_nome
      FROM lancamentos l
      JOIN contas c ON l.conta_id = c.id
      LEFT JOIN categorias cat ON l.categoria_id = cat.id
      WHERE l.usuario_id = ?
      ORDER BY l.data DESC
      LIMIT 10
    `, [req.params.id]);

    res.json({
      user,
      accounts,
      recent_transactions: recentTransactions
    });
  } catch (error) {
    console.error('Erro ao obter detalhes do usuário:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Atualizar dados de um usuário
router.put('/users/:id', async (req, res) => {
  try {
    const { nome, email, nova_senha } = req.body;

    if (!nome || !email) {
      return res.status(400).json({ error: 'Nome e email são obrigatórios' });
    }

    // Verificar se o usuário existe
    const existingUser = await db.get('SELECT id FROM usuarios WHERE id = ?', [req.params.id]);
    if (!existingUser) {
      return res.status(404).json({ error: 'Usuário não encontrado' });
    }

    // Verificar se o email já está em uso por outro usuário
    const emailInUse = await db.get('SELECT id FROM usuarios WHERE email = ? AND id != ?', [email, req.params.id]);
    if (emailInUse) {
      return res.status(409).json({ error: 'Email já está em uso por outro usuário' });
    }

    let updateFields = 'nome = ?, email = ?';
    let updateValues = [nome, email];

    // Se uma nova senha foi fornecida
    if (nova_senha) {
      if (nova_senha.length < 6) {
        return res.status(400).json({ error: 'Nova senha deve ter pelo menos 6 caracteres' });
      }

      const hashedPassword = await bcrypt.hash(nova_senha, 10);
      updateFields += ', senha = ?';
      updateValues.push(hashedPassword);
    }

    updateValues.push(req.params.id);

    await db.run(
      `UPDATE usuarios SET ${updateFields} WHERE id = ?`,
      updateValues
    );

    const updatedUser = await db.get(
      'SELECT id, nome, email, data_criacao FROM usuarios WHERE id = ?',
      [req.params.id]
    );

    res.json({
      message: 'Usuário atualizado com sucesso',
      user: updatedUser
    });
  } catch (error) {
    console.error('Erro ao atualizar usuário:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Excluir usuário
router.delete('/users/:id', async (req, res) => {
  try {
    // Verificar se o usuário existe
    const user = await db.get('SELECT id, email FROM usuarios WHERE id = ?', [req.params.id]);
    if (!user) {
      return res.status(404).json({ error: 'Usuário não encontrado' });
    }

    // Não permitir excluir o próprio admin
    if (user.id === req.user.id) {
      return res.status(400).json({ error: 'Não é possível excluir sua própria conta' });
    }

    // Excluir usuário (cascade irá remover contas e transações)
    await db.run('DELETE FROM usuarios WHERE id = ?', [req.params.id]);

    res.json({ message: 'Usuário excluído com sucesso' });
  } catch (error) {
    console.error('Erro ao excluir usuário:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Criar novo usuário
router.post('/users', async (req, res) => {
  try {
    const { nome, email, senha } = req.body;

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

    const newUser = await db.get(
      'SELECT id, nome, email, data_criacao FROM usuarios WHERE id = ?',
      [result.id]
    );

    res.status(201).json({
      message: 'Usuário criado com sucesso',
      user: newUser
    });
  } catch (error) {
    console.error('Erro ao criar usuário:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter estatísticas gerais do sistema
router.get('/stats', async (req, res) => {
  try {
    const stats = {};

    // Total de usuários
    const usersResult = await db.get('SELECT COUNT(*) as total FROM usuarios');
    stats.total_usuarios = usersResult.total;

    // Total de contas
    const accountsResult = await db.get('SELECT COUNT(*) as total FROM contas');
    stats.total_contas = accountsResult.total;

    // Total de transações
    const transactionsResult = await db.get('SELECT COUNT(*) as total FROM lancamentos');
    stats.total_transacoes = transactionsResult.total;

    // Valor total em receitas
    const revenueResult = await db.get(`
      SELECT COALESCE(SUM(valor), 0) as total 
      FROM lancamentos 
      WHERE tipo = 'receita'
    `);
    stats.total_receitas = revenueResult.total;

    // Valor total em despesas
    const expensesResult = await db.get(`
      SELECT COALESCE(SUM(ABS(valor)), 0) as total 
      FROM lancamentos 
      WHERE tipo = 'despesa'
    `);
    stats.total_despesas = expensesResult.total;

    // Usuários ativos no último mês
    const oneMonthAgo = Date.now() - (30 * 24 * 60 * 60 * 1000);
    const activeUsersResult = await db.get(`
      SELECT COUNT(DISTINCT usuario_id) as total 
      FROM lancamentos 
      WHERE data >= ?
    `, [oneMonthAgo]);
    stats.usuarios_ativos_mes = activeUsersResult.total;

    // Categoria mais usada
    const topCategoryResult = await db.get(`
      SELECT c.nome, COUNT(l.id) as total_uso
      FROM categorias c
      JOIN lancamentos l ON c.id = l.categoria_id
      GROUP BY c.id, c.nome
      ORDER BY total_uso DESC
      LIMIT 1
    `);
    stats.categoria_mais_usada = topCategoryResult || { nome: 'Nenhuma', total_uso: 0 };

    res.json(stats);
  } catch (error) {
    console.error('Erro ao obter estatísticas:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter atividade recente no sistema
router.get('/activity', async (req, res) => {
  try {
    const { limit = 20 } = req.query;

    const recentActivity = await db.all(`
      SELECT 
        'transaction' as tipo,
        l.id,
        l.data as timestamp,
        l.valor,
        l.descricao,
        l.tipo as transaction_tipo,
        u.nome as usuario_nome,
        u.email as usuario_email,
        c.nome as conta_nome
      FROM lancamentos l
      JOIN usuarios u ON l.usuario_id = u.id
      JOIN contas c ON l.conta_id = c.id
      ORDER BY l.data DESC
      LIMIT ?
    `, [parseInt(limit)]);

    res.json(recentActivity);
  } catch (error) {
    console.error('Erro ao obter atividade:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;