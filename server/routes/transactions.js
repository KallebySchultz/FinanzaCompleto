const express = require('express');
const firebase = require('../config/firebase');
const { authenticateToken, checkResourceOwnership } = require('../middleware/auth');

const router = express.Router();

// Listar transações do usuário
router.get('/', authenticateToken, async (req, res) => {
  try {
    const { limit = 50, offset = 0, conta_id, categoria_id, tipo, start_date, end_date } = req.query;

    // Get all data
    const lancamentos = await firebase.query('lancamentos');
    const contas = await firebase.query('contas');
    const categorias = await firebase.query('categorias');

    // Filter transactions for this user
    let userTransactions = lancamentos.filter(l => l.usuario_id === req.user.id);

    // Apply filters
    if (conta_id) {
      userTransactions = userTransactions.filter(l => l.conta_id === conta_id);
    }

    if (categoria_id) {
      userTransactions = userTransactions.filter(l => l.categoria_id === categoria_id);
    }

    if (tipo && (tipo === 'receita' || tipo === 'despesa')) {
      userTransactions = userTransactions.filter(l => l.tipo === tipo);
    }

    if (start_date) {
      userTransactions = userTransactions.filter(l => l.data >= parseInt(start_date));
    }

    if (end_date) {
      userTransactions = userTransactions.filter(l => l.data <= parseInt(end_date));
    }

    // Sort by date (newest first)
    userTransactions.sort((a, b) => b.data - a.data);

    // Paginate
    const startIndex = parseInt(offset);
    const endIndex = startIndex + parseInt(limit);
    const paginatedTransactions = userTransactions.slice(startIndex, endIndex);

    // Add related data
    const transactions = paginatedTransactions.map(l => {
      const conta = contas.find(c => c.id === l.conta_id);
      const categoria = categorias.find(cat => cat.id === l.categoria_id);

      return {
        id: l.id,
        valor: l.valor,
        data: l.data,
        descricao: l.descricao,
        tipo: l.tipo,
        conta_nome: conta ? conta.nome : null,
        categoria_nome: categoria ? categoria.nome : null,
        categoria_cor: categoria ? categoria.cor_hex : null
      };
    });

    res.json({
      transactions,
      total: userTransactions.length,
      limit: parseInt(limit),
      offset: parseInt(offset)
    });
  } catch (error) {
    console.error('Erro ao listar transações:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter uma transação específica
router.get('/:id', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    const transaction = await db.get(`
      SELECT 
        l.id,
        l.valor,
        l.data,
        l.descricao,
        l.tipo,
        l.conta_id,
        l.categoria_id,
        c.nome as conta_nome,
        cat.nome as categoria_nome,
        cat.cor_hex as categoria_cor
      FROM lancamentos l
      JOIN contas c ON l.conta_id = c.id
      LEFT JOIN categorias cat ON l.categoria_id = cat.id
      WHERE l.id = ? AND l.usuario_id = ?
    `, [req.params.id, req.user.id]);

    if (!transaction) {
      return res.status(404).json({ error: 'Transação não encontrada' });
    }

    res.json(transaction);
  } catch (error) {
    console.error('Erro ao obter transação:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Criar nova transação
router.post('/', authenticateToken, async (req, res) => {
  try {
    const { valor, data, descricao, conta_id, categoria_id, tipo } = req.body;

    // Validações
    if (!valor || !data || !conta_id || !tipo) {
      return res.status(400).json({ 
        error: 'Valor, data, conta e tipo são obrigatórios' 
      });
    }

    if (typeof valor !== 'number' || valor === 0) {
      return res.status(400).json({ error: 'Valor deve ser um número diferente de zero' });
    }

    if (tipo !== 'receita' && tipo !== 'despesa') {
      return res.status(400).json({ error: 'Tipo deve ser "receita" ou "despesa"' });
    }

    // Verificar se a conta pertence ao usuário
    const account = await db.get(
      'SELECT id FROM contas WHERE id = ? AND usuario_id = ?',
      [conta_id, req.user.id]
    );

    if (!account) {
      return res.status(400).json({ error: 'Conta não encontrada' });
    }

    // Verificar categoria se fornecida
    if (categoria_id) {
      const category = await db.get(
        'SELECT id, tipo FROM categorias WHERE id = ?',
        [categoria_id]
      );

      if (!category) {
        return res.status(400).json({ error: 'Categoria não encontrada' });
      }

      if (category.tipo !== tipo) {
        return res.status(400).json({ 
          error: `Categoria de ${category.tipo} não pode ser usada em transação de ${tipo}` 
        });
      }
    }

    // Ajustar valor baseado no tipo (despesas devem ser negativas)
    const adjustedValue = tipo === 'despesa' ? -Math.abs(valor) : Math.abs(valor);

    const result = await db.run(`
      INSERT INTO lancamentos (valor, data, descricao, conta_id, categoria_id, usuario_id, tipo)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    `, [adjustedValue, data, descricao, conta_id, categoria_id, req.user.id, tipo]);

    const newTransaction = await db.get(`
      SELECT 
        l.id,
        l.valor,
        l.data,
        l.descricao,
        l.tipo,
        c.nome as conta_nome,
        cat.nome as categoria_nome,
        cat.cor_hex as categoria_cor
      FROM lancamentos l
      JOIN contas c ON l.conta_id = c.id
      LEFT JOIN categorias cat ON l.categoria_id = cat.id
      WHERE l.id = ?
    `, [result.id]);

    res.status(201).json({
      message: 'Transação criada com sucesso',
      transaction: newTransaction
    });
  } catch (error) {
    console.error('Erro ao criar transação:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Atualizar transação
router.put('/:id', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    const { valor, data, descricao, conta_id, categoria_id, tipo } = req.body;

    // Validações
    if (!valor || !data || !conta_id || !tipo) {
      return res.status(400).json({ 
        error: 'Valor, data, conta e tipo são obrigatórios' 
      });
    }

    if (typeof valor !== 'number' || valor === 0) {
      return res.status(400).json({ error: 'Valor deve ser um número diferente de zero' });
    }

    if (tipo !== 'receita' && tipo !== 'despesa') {
      return res.status(400).json({ error: 'Tipo deve ser "receita" ou "despesa"' });
    }

    // Verificar se a conta pertence ao usuário
    const account = await db.get(
      'SELECT id FROM contas WHERE id = ? AND usuario_id = ?',
      [conta_id, req.user.id]
    );

    if (!account) {
      return res.status(400).json({ error: 'Conta não encontrada' });
    }

    // Verificar categoria se fornecida
    if (categoria_id) {
      const category = await db.get(
        'SELECT id, tipo FROM categorias WHERE id = ?',
        [categoria_id]
      );

      if (!category) {
        return res.status(400).json({ error: 'Categoria não encontrada' });
      }

      if (category.tipo !== tipo) {
        return res.status(400).json({ 
          error: `Categoria de ${category.tipo} não pode ser usada em transação de ${tipo}` 
        });
      }
    }

    // Ajustar valor baseado no tipo
    const adjustedValue = tipo === 'despesa' ? -Math.abs(valor) : Math.abs(valor);

    await db.run(`
      UPDATE lancamentos 
      SET valor = ?, data = ?, descricao = ?, conta_id = ?, categoria_id = ?, tipo = ?
      WHERE id = ? AND usuario_id = ?
    `, [adjustedValue, data, descricao, conta_id, categoria_id, tipo, req.params.id, req.user.id]);

    const updatedTransaction = await db.get(`
      SELECT 
        l.id,
        l.valor,
        l.data,
        l.descricao,
        l.tipo,
        c.nome as conta_nome,
        cat.nome as categoria_nome,
        cat.cor_hex as categoria_cor
      FROM lancamentos l
      JOIN contas c ON l.conta_id = c.id
      LEFT JOIN categorias cat ON l.categoria_id = cat.id
      WHERE l.id = ?
    `, [req.params.id]);

    res.json({
      message: 'Transação atualizada com sucesso',
      transaction: updatedTransaction
    });
  } catch (error) {
    console.error('Erro ao atualizar transação:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Excluir transação
router.delete('/:id', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    await db.run(
      'DELETE FROM lancamentos WHERE id = ? AND usuario_id = ?',
      [req.params.id, req.user.id]
    );

    res.json({ message: 'Transação excluída com sucesso' });
  } catch (error) {
    console.error('Erro ao excluir transação:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter resumo de transações por período
router.get('/summary/period', authenticateToken, async (req, res) => {
  try {
    const { start_date, end_date } = req.query;

    if (!start_date || !end_date) {
      return res.status(400).json({ error: 'Data inicial e final são obrigatórias' });
    }

    const summary = await db.get(`
      SELECT 
        COALESCE(SUM(CASE WHEN tipo = 'receita' THEN valor ELSE 0 END), 0) as total_receitas,
        COALESCE(SUM(CASE WHEN tipo = 'despesa' THEN ABS(valor) ELSE 0 END), 0) as total_despesas,
        COUNT(CASE WHEN tipo = 'receita' THEN 1 END) as count_receitas,
        COUNT(CASE WHEN tipo = 'despesa' THEN 1 END) as count_despesas
      FROM lancamentos
      WHERE usuario_id = ? AND data >= ? AND data <= ?
    `, [req.user.id, parseInt(start_date), parseInt(end_date)]);

    summary.saldo_periodo = summary.total_receitas - summary.total_despesas;

    res.json(summary);
  } catch (error) {
    console.error('Erro ao obter resumo:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;