const express = require('express');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// Listar todas as categorias
router.get('/', authenticateToken, async (req, res) => {
  try {
    const { tipo } = req.query;

    let whereClause = '';
    let params = [];

    if (tipo && (tipo === 'receita' || tipo === 'despesa')) {
      whereClause = 'WHERE tipo = ?';
      params.push(tipo);
    }

    const categories = await db.all(`
      SELECT id, nome, cor_hex, tipo
      FROM categorias 
      ${whereClause}
      ORDER BY tipo, nome
    `, params);

    res.json(categories);
  } catch (error) {
    console.error('Erro ao listar categorias:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter uma categoria específica
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const category = await db.get(
      'SELECT id, nome, cor_hex, tipo FROM categorias WHERE id = ?',
      [req.params.id]
    );

    if (!category) {
      return res.status(404).json({ error: 'Categoria não encontrada' });
    }

    res.json(category);
  } catch (error) {
    console.error('Erro ao obter categoria:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Criar nova categoria (apenas para administradores)
router.post('/', authenticateToken, async (req, res) => {
  try {
    // Por enquanto, vamos permitir que qualquer usuário crie categorias
    // Em produção, isso poderia ser restrito apenas a administradores
    const { nome, cor_hex, tipo } = req.body;

    if (!nome || !tipo) {
      return res.status(400).json({ error: 'Nome e tipo são obrigatórios' });
    }

    if (tipo !== 'receita' && tipo !== 'despesa') {
      return res.status(400).json({ error: 'Tipo deve ser "receita" ou "despesa"' });
    }

    // Verificar se já existe uma categoria com esse nome e tipo
    const existingCategory = await db.get(
      'SELECT id FROM categorias WHERE nome = ? AND tipo = ?',
      [nome, tipo]
    );

    if (existingCategory) {
      return res.status(409).json({ error: 'Já existe uma categoria com esse nome e tipo' });
    }

    const result = await db.run(
      'INSERT INTO categorias (nome, cor_hex, tipo) VALUES (?, ?, ?)',
      [nome, cor_hex || '#666666', tipo]
    );

    const newCategory = await db.get(
      'SELECT id, nome, cor_hex, tipo FROM categorias WHERE id = ?',
      [result.id]
    );

    res.status(201).json({
      message: 'Categoria criada com sucesso',
      category: newCategory
    });
  } catch (error) {
    console.error('Erro ao criar categoria:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Atualizar categoria (apenas para administradores)
router.put('/:id', authenticateToken, async (req, res) => {
  try {
    const { nome, cor_hex, tipo } = req.body;

    if (!nome || !tipo) {
      return res.status(400).json({ error: 'Nome e tipo são obrigatórios' });
    }

    if (tipo !== 'receita' && tipo !== 'despesa') {
      return res.status(400).json({ error: 'Tipo deve ser "receita" ou "despesa"' });
    }

    // Verificar se a categoria existe
    const existingCategory = await db.get(
      'SELECT id FROM categorias WHERE id = ?',
      [req.params.id]
    );

    if (!existingCategory) {
      return res.status(404).json({ error: 'Categoria não encontrada' });
    }

    // Verificar se já existe outra categoria com esse nome e tipo
    const duplicateCategory = await db.get(
      'SELECT id FROM categorias WHERE nome = ? AND tipo = ? AND id != ?',
      [nome, tipo, req.params.id]
    );

    if (duplicateCategory) {
      return res.status(409).json({ error: 'Já existe uma categoria com esse nome e tipo' });
    }

    await db.run(
      'UPDATE categorias SET nome = ?, cor_hex = ?, tipo = ? WHERE id = ?',
      [nome, cor_hex || '#666666', tipo, req.params.id]
    );

    const updatedCategory = await db.get(
      'SELECT id, nome, cor_hex, tipo FROM categorias WHERE id = ?',
      [req.params.id]
    );

    res.json({
      message: 'Categoria atualizada com sucesso',
      category: updatedCategory
    });
  } catch (error) {
    console.error('Erro ao atualizar categoria:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Excluir categoria (apenas para administradores)
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    // Verificar se a categoria tem transações associadas
    const transactionCount = await db.get(
      'SELECT COUNT(*) as count FROM lancamentos WHERE categoria_id = ?',
      [req.params.id]
    );

    if (transactionCount.count > 0) {
      return res.status(400).json({ 
        error: 'Não é possível excluir categoria com transações associadas' 
      });
    }

    const result = await db.run(
      'DELETE FROM categorias WHERE id = ?',
      [req.params.id]
    );

    if (result.changes === 0) {
      return res.status(404).json({ error: 'Categoria não encontrada' });
    }

    res.json({ message: 'Categoria excluída com sucesso' });
  } catch (error) {
    console.error('Erro ao excluir categoria:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter estatísticas de uso das categorias
router.get('/stats/usage', authenticateToken, async (req, res) => {
  try {
    const { usuario_id, start_date, end_date } = req.query;

    let whereClause = '';
    let params = [];

    if (usuario_id) {
      whereClause = 'WHERE l.usuario_id = ?';
      params.push(usuario_id);
    }

    if (start_date) {
      whereClause += (whereClause ? ' AND' : 'WHERE') + ' l.data >= ?';
      params.push(parseInt(start_date));
    }

    if (end_date) {
      whereClause += (whereClause ? ' AND' : 'WHERE') + ' l.data <= ?';
      params.push(parseInt(end_date));
    }

    const categoryStats = await db.all(`
      SELECT 
        c.id,
        c.nome,
        c.cor_hex,
        c.tipo,
        COUNT(l.id) as total_transacoes,
        COALESCE(SUM(ABS(l.valor)), 0) as total_valor
      FROM categorias c
      LEFT JOIN lancamentos l ON c.id = l.categoria_id ${whereClause ? 'AND ' + whereClause.replace('WHERE ', '') : ''}
      GROUP BY c.id, c.nome, c.cor_hex, c.tipo
      ORDER BY total_valor DESC
    `, params);

    res.json(categoryStats);
  } catch (error) {
    console.error('Erro ao obter estatísticas:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;