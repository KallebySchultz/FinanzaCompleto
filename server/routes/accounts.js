const express = require('express');
const firebase = require('../config/firebase');
const { authenticateToken, checkResourceOwnership } = require('../middleware/auth');

const router = express.Router();

// Listar todas as contas do usuário
router.get('/', authenticateToken, async (req, res) => {
  try {
    // Get all accounts for the user
    const contas = await firebase.query('contas');
    const contasUsuario = contas.filter(conta => conta.usuario_id === req.user.id);
    
    // Get all transactions to calculate current balance
    const lancamentos = await firebase.query('lancamentos');
    
    const accounts = contasUsuario.map(conta => {
      const transacoesConta = lancamentos.filter(l => l.conta_id === conta.id);
      const movimentacao = transacoesConta.reduce((sum, t) => {
        return sum + (t.tipo === 'receita' ? t.valor : t.valor);
      }, 0);
      
      return {
        id: conta.id,
        nome: conta.nome,
        saldo_inicial: conta.saldo_inicial,
        movimentacao: movimentacao,
        saldo_atual: conta.saldo_inicial + movimentacao
      };
    });

    accounts.sort((a, b) => a.nome.localeCompare(b.nome));
    res.json(accounts);
  } catch (error) {
    console.error('Erro ao listar contas:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter uma conta específica
router.get('/:id', authenticateToken, checkResourceOwnership(), async (req, res) => {
  try {
    const conta = await firebase.get('contas', req.params.id);
    
    if (!conta || conta.usuario_id !== req.user.id) {
      return res.status(404).json({ error: 'Conta não encontrada' });
    }

    // Get transactions for this account to calculate current balance
    const lancamentos = await firebase.query('lancamentos');
    const transacoesConta = lancamentos.filter(l => l.conta_id === conta.id);
    const movimentacao = transacoesConta.reduce((sum, t) => {
      return sum + (t.tipo === 'receita' ? t.valor : t.valor);
    }, 0);

    const account = {
      id: conta.id,
      nome: conta.nome,
      saldo_inicial: conta.saldo_inicial,
      movimentacao: movimentacao,
      saldo_atual: conta.saldo_inicial + movimentacao
    };

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
    const contas = await firebase.query('contas');
    const existingAccount = contas.find(conta => conta.nome === nome && conta.usuario_id === req.user.id);

    if (existingAccount) {
      return res.status(409).json({ error: 'Já existe uma conta com esse nome' });
    }

    const contaData = {
      nome,
      saldo_inicial,
      usuario_id: req.user.id
    };
    
    const newAccount = await firebase.create('contas', contaData);

    res.status(201).json({
      message: 'Conta criada com sucesso',
      account: {
        id: newAccount.id,
        nome: newAccount.nome,
        saldo_inicial: newAccount.saldo_inicial,
        saldo_atual: newAccount.saldo_inicial
      }
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
    const contas = await firebase.query('contas');
    const existingAccount = contas.find(conta => 
      conta.nome === nome && 
      conta.usuario_id === req.user.id && 
      conta.id !== req.params.id
    );

    if (existingAccount) {
      return res.status(409).json({ error: 'Já existe uma conta com esse nome' });
    }

    const updateData = { nome, saldo_inicial };
    await firebase.update('contas', req.params.id, updateData);

    // Get updated account with current balance
    const updatedConta = await firebase.get('contas', req.params.id);
    const lancamentos = await firebase.query('lancamentos');
    const transacoesConta = lancamentos.filter(l => l.conta_id === req.params.id);
    const movimentacao = transacoesConta.reduce((sum, t) => {
      return sum + (t.tipo === 'receita' ? t.valor : t.valor);
    }, 0);

    const updatedAccount = {
      id: updatedConta.id,
      nome: updatedConta.nome,
      saldo_inicial: updatedConta.saldo_inicial,
      movimentacao: movimentacao,
      saldo_atual: updatedConta.saldo_inicial + movimentacao
    };

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
    const lancamentos = await firebase.query('lancamentos');
    const transactionCount = lancamentos.filter(l => l.conta_id === req.params.id).length;

    if (transactionCount > 0) {
      return res.status(400).json({ 
        error: 'Não é possível excluir conta com transações. Exclua as transações primeiro.' 
      });
    }

    await firebase.delete('contas', req.params.id);

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

    // Get transactions for this account
    const allLancamentos = await firebase.query('lancamentos');
    const contaLancamentos = allLancamentos.filter(l => 
      l.conta_id === req.params.id && l.usuario_id === req.user.id
    );

    // Get categories for lookup
    const categorias = await firebase.query('categorias');
    
    // Sort by date (newest first) and paginate
    contaLancamentos.sort((a, b) => b.data - a.data);
    const startIndex = parseInt(offset);
    const endIndex = startIndex + parseInt(limit);
    const paginatedTransactions = contaLancamentos.slice(startIndex, endIndex);

    // Add category details
    const transactions = paginatedTransactions.map(l => {
      const categoria = categorias.find(cat => cat.id === l.categoria_id);
      return {
        id: l.id,
        valor: l.valor,
        data: l.data,
        descricao: l.descricao,
        tipo: l.tipo,
        categoria_nome: categoria ? categoria.nome : null,
        categoria_cor: categoria ? categoria.cor_hex : null
      };
    });

    res.json({
      transactions,
      total: contaLancamentos.length,
      limit: parseInt(limit),
      offset: parseInt(offset)
    });
  } catch (error) {
    console.error('Erro ao obter extrato:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;