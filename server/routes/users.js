const express = require('express');
const bcrypt = require('bcrypt');
const firebase = require('../config/firebase');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// Obter perfil do usuário autenticado
router.get('/profile', authenticateToken, async (req, res) => {
  try {
    const user = await firebase.get('usuarios', req.user.id);

    if (!user) {
      return res.status(404).json({ error: 'Usuário não encontrado' });
    }

    // Remove password from response
    const { senha, ...userProfile } = user;
    res.json(userProfile);
  } catch (error) {
    console.error('Erro ao obter perfil:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Atualizar perfil do usuário
router.put('/profile', authenticateToken, async (req, res) => {
  try {
    const { nome, email, senhaAtual, novaSenha } = req.body;

    if (!nome || !email) {
      return res.status(400).json({ error: 'Nome e email são obrigatórios' });
    }

    // Verificar se o email já está em uso por outro usuário
    const usuarios = await firebase.get('usuarios');
    const existingUser = usuarios ? Object.values(usuarios).find(u => u.email === email && u.id !== req.user.id) : null;
    if (existingUser) {
      return res.status(409).json({ error: 'Email já está em uso' });
    }

    let updateData = { nome, email };

    // Se quiser alterar a senha
    if (novaSenha) {
      if (!senhaAtual) {
        return res.status(400).json({ error: 'Senha atual é necessária para alterar a senha' });
      }

      if (novaSenha.length < 6) {
        return res.status(400).json({ error: 'Nova senha deve ter pelo menos 6 caracteres' });
      }

      // Verificar senha atual
      const user = await firebase.get('usuarios', req.user.id);
      const isValidPassword = await bcrypt.compare(senhaAtual, user.senha);
      if (!isValidPassword) {
        return res.status(400).json({ error: 'Senha atual incorreta' });
      }

      // Hash da nova senha
      const hashedPassword = await bcrypt.hash(novaSenha, 10);
      updateData.senha = hashedPassword;
    }

    await firebase.update('usuarios', req.user.id, updateData);

    const updatedUser = await firebase.get('usuarios', req.user.id);
    const { senha, ...userProfile } = updatedUser;

    res.json({
      message: 'Perfil atualizado com sucesso',
      user: userProfile
    });
  } catch (error) {
    console.error('Erro ao atualizar perfil:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Excluir conta do usuário
router.delete('/profile', authenticateToken, async (req, res) => {
  try {
    const { senha } = req.body;

    if (!senha) {
      return res.status(400).json({ error: 'Senha é necessária para excluir a conta' });
    }

    // Verificar senha
    const user = await firebase.get('usuarios', req.user.id);
    const isValidPassword = await bcrypt.compare(senha, user.senha);
    if (!isValidPassword) {
      return res.status(400).json({ error: 'Senha incorreta' });
    }

    // Excluir dados relacionados (Firebase doesn't have CASCADE, so we need to delete manually)
    const contas = await firebase.query('contas');
    const contasUsuario = contas.filter(conta => conta.usuario_id === req.user.id);
    for (const conta of contasUsuario) {
      await firebase.delete('contas', conta.id);
    }

    const lancamentos = await firebase.query('lancamentos');
    const lancamentosUsuario = lancamentos.filter(lancamento => lancamento.usuario_id === req.user.id);
    for (const lancamento of lancamentosUsuario) {
      await firebase.delete('lancamentos', lancamento.id);
    }

    // Excluir usuário
    await firebase.delete('usuarios', req.user.id);

    res.json({ message: 'Conta excluída com sucesso' });
  } catch (error) {
    console.error('Erro ao excluir conta:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter resumo financeiro do usuário
router.get('/financial-summary', authenticateToken, async (req, res) => {
  try {
    // Get all accounts for the user
    const contas = await firebase.query('contas');
    const contasUsuario = contas.filter(conta => conta.usuario_id === req.user.id);
    
    // Get all transactions for the user
    const lancamentos = await firebase.query('lancamentos');
    const lancamentosUsuario = lancamentos.filter(lancamento => lancamento.usuario_id === req.user.id);

    // Calculate total balance (initial balance + transactions)
    let saldo_total = 0;
    for (const conta of contasUsuario) {
      saldo_total += conta.saldo_inicial;
      const transacoesConta = lancamentosUsuario.filter(l => l.conta_id === conta.id);
      for (const transacao of transacoesConta) {
        saldo_total += transacao.tipo === 'receita' ? transacao.valor : transacao.valor;
      }
    }

    // Calculate monthly totals
    const startOfMonth = new Date();
    startOfMonth.setDate(1);
    startOfMonth.setHours(0, 0, 0, 0);
    const startTimestamp = startOfMonth.getTime();

    const lancamentosDoMes = lancamentosUsuario.filter(l => l.data >= startTimestamp);
    
    const receitas_mes = lancamentosDoMes
      .filter(l => l.tipo === 'receita')
      .reduce((sum, l) => sum + l.valor, 0);
      
    const despesas_mes = lancamentosDoMes
      .filter(l => l.tipo === 'despesa')
      .reduce((sum, l) => sum + Math.abs(l.valor), 0);

    res.json({
      saldo_total: saldo_total,
      receitas_mes: receitas_mes,
      despesas_mes: despesas_mes,
      total_contas: contasUsuario.length
    });
  } catch (error) {
    console.error('Erro ao obter resumo financeiro:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;