const express = require('express');
const bcrypt = require('bcrypt');
const db = require('../config/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// Obter perfil do usuário autenticado
router.get('/profile', authenticateToken, async (req, res) => {
  try {
    const user = await db.get(
      'SELECT id, nome, email, data_criacao FROM usuarios WHERE id = ?',
      [req.user.id]
    );

    if (!user) {
      return res.status(404).json({ error: 'Usuário não encontrado' });
    }

    res.json(user);
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
    const existingUser = await db.get('SELECT id FROM usuarios WHERE email = ? AND id != ?', [email, req.user.id]);
    if (existingUser) {
      return res.status(409).json({ error: 'Email já está em uso' });
    }

    let updateFields = 'nome = ?, email = ?';
    let updateValues = [nome, email];

    // Se quiser alterar a senha
    if (novaSenha) {
      if (!senhaAtual) {
        return res.status(400).json({ error: 'Senha atual é necessária para alterar a senha' });
      }

      if (novaSenha.length < 6) {
        return res.status(400).json({ error: 'Nova senha deve ter pelo menos 6 caracteres' });
      }

      // Verificar senha atual
      const user = await db.get('SELECT senha FROM usuarios WHERE id = ?', [req.user.id]);
      const isValidPassword = await bcrypt.compare(senhaAtual, user.senha);
      if (!isValidPassword) {
        return res.status(400).json({ error: 'Senha atual incorreta' });
      }

      // Hash da nova senha
      const hashedPassword = await bcrypt.hash(novaSenha, 10);
      updateFields += ', senha = ?';
      updateValues.push(hashedPassword);
    }

    updateValues.push(req.user.id);

    await db.run(
      `UPDATE usuarios SET ${updateFields} WHERE id = ?`,
      updateValues
    );

    const updatedUser = await db.get(
      'SELECT id, nome, email, data_criacao FROM usuarios WHERE id = ?',
      [req.user.id]
    );

    res.json({
      message: 'Perfil atualizado com sucesso',
      user: updatedUser
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
    const user = await db.get('SELECT senha FROM usuarios WHERE id = ?', [req.user.id]);
    const isValidPassword = await bcrypt.compare(senha, user.senha);
    if (!isValidPassword) {
      return res.status(400).json({ error: 'Senha incorreta' });
    }

    // Excluir usuário (cascade irá remover contas e lançamentos)
    await db.run('DELETE FROM usuarios WHERE id = ?', [req.user.id]);

    res.json({ message: 'Conta excluída com sucesso' });
  } catch (error) {
    console.error('Erro ao excluir conta:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

// Obter resumo financeiro do usuário
router.get('/financial-summary', authenticateToken, async (req, res) => {
  try {
    // Saldo total das contas
    const saldoResult = await db.get(`
      SELECT COALESCE(SUM(saldo_atual), 0) as saldo_total
      FROM v_saldo_contas 
      WHERE usuario_id = ?
    `, [req.user.id]);

    // Total de receitas do mês atual
    const startOfMonth = new Date();
    startOfMonth.setDate(1);
    startOfMonth.setHours(0, 0, 0, 0);
    
    const receitasResult = await db.get(`
      SELECT COALESCE(SUM(valor), 0) as total_receitas
      FROM lancamentos 
      WHERE usuario_id = ? AND tipo = 'receita' AND data >= ?
    `, [req.user.id, startOfMonth.getTime()]);

    // Total de despesas do mês atual
    const despesasResult = await db.get(`
      SELECT COALESCE(SUM(ABS(valor)), 0) as total_despesas
      FROM lancamentos 
      WHERE usuario_id = ? AND tipo = 'despesa' AND data >= ?
    `, [req.user.id, startOfMonth.getTime()]);

    // Número de contas
    const contasResult = await db.get(`
      SELECT COUNT(*) as total_contas
      FROM contas 
      WHERE usuario_id = ?
    `, [req.user.id]);

    res.json({
      saldo_total: saldoResult.saldo_total,
      receitas_mes: receitasResult.total_receitas,
      despesas_mes: despesasResult.total_despesas,
      total_contas: contasResult.total_contas
    });
  } catch (error) {
    console.error('Erro ao obter resumo financeiro:', error);
    res.status(500).json({ error: 'Erro interno do servidor' });
  }
});

module.exports = router;