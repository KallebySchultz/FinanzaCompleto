const express = require('express');
const db = require('../config/database');
const { authenticateToken, checkResourceOwnership } = require('../middleware/auth');

const router = express.Router();

// Listar todas as contas do usuário
router.get('/', authenticateToken, async (req, res) => {
  try {
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
    `, [req.user.id]);

    res.json(accounts);
  } catch (error) {
    console.error('Erro ao listar contas:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter uma conta específica
router.get('/:id', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    const account = await db.get(`
      SELECT 
        c.id,
        c.nome,
        c.saldo_inicial,
        COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as movimentacao,
        c.saldo_inicial + COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as saldo_atual
      FROM contas c
      LEFT JOIN lancamentos l ON c.id = l.conta_id
      WHERE c.id = ? AND c.usuario_id = ?
      GROUP BY c.id, c.nome, c.saldo_inicial
    `, [req.params.id, req.user.id]);

    if (!account) {
      return res.status(404).json({ error: 'Conta não encontrada' });
    }

    res.json(account);
  } catch (error) {
    console.error('Erro ao obter conta:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Criar nova conta
router.post('/', authenticateToken, async (req, res) => {
  try {
    const { nome, saldo_inicial = 0 } = req.body;

    if (!nome) {
      return res.status(400).json({ error: 'Nome da conta é obrigatório' });
    }

    if (typeof saldo_inicial !== 'number') {
      return res.status(400).json({ error: 'Saldo inicial deve ser um número' });
    }

    // Verificar se já existe uma conta com esse nome para o usuário
    const existingAccount = await db.get(
      'SELECT id FROM contas WHERE nome = ? AND usuario_id = ?',
      [nome, req.user.id]
    );

    if (existingAccount) {
      return res.status(409).json({ error: 'Já existe uma conta com esse nome' });
    }

    const result = await db.run(
      'INSERT INTO contas (nome, saldo_inicial, usuario_id) VALUES (?, ?, ?)',
      [nome, saldo_inicial, req.user.id]
    );

    const newAccount = await db.get(
      'SELECT id, nome, saldo_inicial, saldo_inicial as saldo_atual FROM contas WHERE id = ?',
      [result.id]
    );

    res.status(201).json({
      message: 'Conta criada com sucesso',
      account: newAccount
    });
  } catch (error) {
    console.error('Erro ao criar conta:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Atualizar conta
router.put('/:id', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    const { nome, saldo_inicial } = req.body;

    if (!nome) {
      return res.status(400).json({ error: 'Nome da conta é obrigatório' });
    }

    if (typeof saldo_inicial !== 'number') {
      return res.status(400).json({ error: 'Saldo inicial deve ser um número' });
    }

    // Verificar se já existe outra conta com esse nome para o usuário
    const existingAccount = await db.get(
      'SELECT id FROM contas WHERE nome = ? AND usuario_id = ? AND id != ?',
      [nome, req.user.id, req.params.id]
    );

    if (existingAccount) {
      return res.status(409).json({ error: 'Já existe uma conta com esse nome' });
    }

    await db.run(
      'UPDATE contas SET nome = ?, saldo_inicial = ? WHERE id = ? AND usuario_id = ?',
      [nome, saldo_inicial, req.params.id, req.user.id]
    );

    const updatedAccount = await db.get(`
      SELECT 
        c.id,
        c.nome,
        c.saldo_inicial,
        COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as movimentacao,
        c.saldo_inicial + COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as saldo_atual
      FROM contas c
      LEFT JOIN lancamentos l ON c.id = l.conta_id
      WHERE c.id = ?
      GROUP BY c.id, c.nome, c.saldo_inicial
    `, [req.params.id]);

    res.json({
      message: 'Conta atualizada com sucesso',
      account: updatedAccount
    });
  } catch (error) {
    console.error('Erro ao atualizar conta:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Excluir conta
router.delete('/:id', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    // Verificar se a conta tem transações
    const transactionCount = await db.get(
      'SELECT COUNT(*) as count FROM lancamentos WHERE conta_id = ?',
      [req.params.id]
    );

    if (transactionCount.count > 0) {
      return res.status(400).json({ 
        error: 'Não é possível excluir conta com transações. Exclua as transações primeiro.' 
      });
    }

    await db.run(
      'DELETE FROM contas WHERE id = ? AND usuario_id = ?',
      [req.params.id, req.user.id]
    );

    res.json({ message: 'Conta excluída com sucesso' });
  } catch (error) {
    console.error('Erro ao excluir conta:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter extrato da conta
router.get('/:id/transactions', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    const { limit = 50, offset = 0 } = req.query;

    const transactions = await db.all(`
      SELECT 
        l.id,
        l.valor,
        l.data,
        l.descricao,
        l.tipo,
        cat.nome as categoria_nome,
        cat.cor_hex as categoria_cor
      FROM lancamentos l
      LEFT JOIN categorias cat ON l.categoria_id = cat.id
      WHERE l.conta_id = ? AND l.usuario_id = ?
      ORDER BY l.data DESC
      LIMIT ? OFFSET ?
    `, [req.params.id, req.user.id, parseInt(limit), parseInt(offset)]);

    const total = await db.get(
      'SELECT COUNT(*) as count FROM lancamentos WHERE conta_id = ? AND usuario_id = ?',
      [req.params.id, req.user.id]
    );

    res.json({
      transactions,
      total: total.count,
      limit: parseInt(limit),
      offset: parseInt(offset)
    });
  } catch (error) {
    console.error('Erro ao obter extrato:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;