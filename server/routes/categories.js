const express = require('express');
const firebase = require('../config/firebase');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// Listar todas as categorias
router.get('/', authenticateToken, async (req, res) => {
  try {
    const { tipo } = req.query;

    let categorias = await firebase.query('categorias');
    
    if (tipo && (tipo === 'receita' || tipo === 'despesa')) {
      categorias = categorias.filter(cat => cat.tipo === tipo);
    }

    // Sort by type and name
    categorias.sort((a, b) => {
      if (a.tipo !== b.tipo) {
        return a.tipo.localeCompare(b.tipo);
      }
      return a.nome.localeCompare(b.nome);
    });

    const categories = categorias.map(cat => ({
      id: cat.id,
      nome: cat.nome,
      cor_hex: cat.cor_hex,
      tipo: cat.tipo
    }));

    res.json(categories);
  } catch (error) {
    console.error('Erro ao listar categorias:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter uma categoria específica
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const categoria = await firebase.get('categorias', req.params.id);

    if (!categoria) {
      return res.status(404).json({ error: 'Categoria não encontrada' });
    }

    const category = {
      id: categoria.id,
      nome: categoria.nome,
      cor_hex: categoria.cor_hex,
      tipo: categoria.tipo
    };

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
    const categorias = await firebase.query('categorias');
    const existingCategory = categorias.find(cat => cat.nome === nome && cat.tipo === tipo);

    if (existingCategory) {
      return res.status(409).json({ error: 'Já existe uma categoria com esse nome e tipo' });
    }

    const categoriaData = {
      nome,
      cor_hex: cor_hex || '#666666',
      tipo
    };

    const newCategory = await firebase.create('categorias', categoriaData);

    res.status(201).json({
      message: 'Categoria criada com sucesso',
      category: {
        id: newCategory.id,
        nome: newCategory.nome,
        cor_hex: newCategory.cor_hex,
        tipo: newCategory.tipo
      }
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
    const existingCategory = await firebase.get('categorias', req.params.id);

    if (!existingCategory) {
      return res.status(404).json({ error: 'Categoria não encontrada' });
    }

    // Verificar se já existe outra categoria com esse nome e tipo
    const categorias = await firebase.query('categorias');
    const duplicateCategory = categorias.find(cat => 
      cat.nome === nome && 
      cat.tipo === tipo && 
      cat.id !== req.params.id
    );

    if (duplicateCategory) {
      return res.status(409).json({ error: 'Já existe uma categoria com esse nome e tipo' });
    }

    const updateData = {
      nome,
      cor_hex: cor_hex || '#666666',
      tipo
    };

    await firebase.update('categorias', req.params.id, updateData);

    const updatedCategory = await firebase.get('categorias', req.params.id);

    res.json({
      message: 'Categoria atualizada com sucesso',
      category: {
        id: updatedCategory.id,
        nome: updatedCategory.nome,
        cor_hex: updatedCategory.cor_hex,
        tipo: updatedCategory.tipo
      }
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
    const lancamentos = await firebase.query('lancamentos');
    const transactionCount = lancamentos.filter(l => l.categoria_id === req.params.id).length;

    if (transactionCount > 0) {
      return res.status(400).json({ 
        error: 'Não é possível excluir categoria com transações associadas' 
      });
    }

    // Check if category exists
    const categoria = await firebase.get('categorias', req.params.id);
    if (!categoria) {
      return res.status(404).json({ error: 'Categoria não encontrada' });
    }

    await firebase.delete('categorias', req.params.id);

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

    // Get all categories and transactions
    const categorias = await firebase.query('categorias');
    const lancamentos = await firebase.query('lancamentos');

    // Filter transactions based on query parameters
    let filteredLancamentos = lancamentos;
    
    if (usuario_id) {
      filteredLancamentos = filteredLancamentos.filter(l => l.usuario_id === parseInt(usuario_id));
    }

    if (start_date) {
      filteredLancamentos = filteredLancamentos.filter(l => l.data >= parseInt(start_date));
    }

    if (end_date) {
      filteredLancamentos = filteredLancamentos.filter(l => l.data <= parseInt(end_date));
    }

    // Calculate stats for each category
    const categoryStats = categorias.map(categoria => {
      const transacoesCategoria = filteredLancamentos.filter(l => l.categoria_id === categoria.id);
      const total_transacoes = transacoesCategoria.length;
      const total_valor = transacoesCategoria.reduce((sum, l) => sum + Math.abs(l.valor), 0);

      return {
        id: categoria.id,
        nome: categoria.nome,
        cor_hex: categoria.cor_hex,
        tipo: categoria.tipo,
        total_transacoes,
        total_valor
      };
    });

    // Sort by total value (descending)
    categoryStats.sort((a, b) => b.total_valor - a.total_valor);

    res.json(categoryStats);
  } catch (error) {
    console.error('Erro ao obter estatísticas:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;