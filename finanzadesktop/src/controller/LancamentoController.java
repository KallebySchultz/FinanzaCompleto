package controller;

import model.Lancamento;
import database.LancamentoDAO;
import java.sql.SQLException;
import java.util.List;

/**
 * LancamentoController - Controller for transaction operations
 */
public class LancamentoController {
    private final LancamentoDAO lancamentoDAO;
    
    public LancamentoController() {
        this.lancamentoDAO = new LancamentoDAO();
    }
    
    /**
     * Create new transaction
     */
    public boolean criarLancamento(double valor, String descricao, int contaId, int categoriaId, int usuarioId, String tipo) {
        try {
            if (!isValorValido(valor) || !isTipoValido(tipo)) {
                return false;
            }
            
            // Ensure expenses are negative
            if ("despesa".equals(tipo) && valor > 0) {
                valor = -valor;
            }
            
            // Ensure income is positive  
            if ("receita".equals(tipo) && valor < 0) {
                valor = Math.abs(valor);
            }
            
            Lancamento novoLancamento = new Lancamento(
                valor, 
                System.currentTimeMillis(), 
                descricao != null ? descricao.trim() : "", 
                contaId, 
                categoriaId, 
                usuarioId, 
                tipo
            );
            
            return lancamentoDAO.inserir(novoLancamento);
        } catch (SQLException e) {
            System.err.println("Erro ao criar lançamento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update transaction
     */
    public boolean atualizarLancamento(int id, double valor, String descricao, int contaId, int categoriaId, String tipo) {
        try {
            if (!isValorValido(valor) || !isTipoValido(tipo)) {
                return false;
            }
            
            Lancamento lancamento = lancamentoDAO.buscarPorId(id);
            if (lancamento == null) {
                return false;
            }
            
            // Ensure expenses are negative
            if ("despesa".equals(tipo) && valor > 0) {
                valor = -valor;
            }
            
            // Ensure income is positive  
            if ("receita".equals(tipo) && valor < 0) {
                valor = Math.abs(valor);
            }
            
            lancamento.setValor(valor);
            lancamento.setDescricao(descricao != null ? descricao.trim() : "");
            lancamento.setContaId(contaId);
            lancamento.setCategoriaId(categoriaId);
            lancamento.setTipo(tipo);
            
            return lancamentoDAO.atualizar(lancamento);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar lançamento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete transaction
     */
    public boolean excluirLancamento(int id) {
        try {
            return lancamentoDAO.deletar(id);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir lançamento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get transaction by ID
     */
    public Lancamento obterLancamento(int id) {
        try {
            return lancamentoDAO.buscarPorId(id);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar lançamento: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List all transactions for a user
     */
    public List<Lancamento> listarLancamentos(int usuarioId) {
        try {
            return lancamentoDAO.listarPorUsuario(usuarioId);
        } catch (SQLException e) {
            System.err.println("Erro ao listar lançamentos: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List transactions by account
     */
    public List<Lancamento> listarPorConta(int contaId) {
        try {
            return lancamentoDAO.listarPorConta(contaId);
        } catch (SQLException e) {
            System.err.println("Erro ao listar lançamentos por conta: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List income transactions for a user
     */
    public List<Lancamento> listarReceitas(int usuarioId) {
        try {
            return lancamentoDAO.listarPorTipo(usuarioId, "receita");
        } catch (SQLException e) {
            System.err.println("Erro ao listar receitas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List expense transactions for a user
     */
    public List<Lancamento> listarDespesas(int usuarioId) {
        try {
            return lancamentoDAO.listarPorTipo(usuarioId, "despesa");
        } catch (SQLException e) {
            System.err.println("Erro ao listar despesas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get total income for a user
     */
    public double obterTotalReceitas(int usuarioId) {
        try {
            return lancamentoDAO.obterTotalReceitas(usuarioId);
        } catch (SQLException e) {
            System.err.println("Erro ao obter total de receitas: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Get total expenses for a user
     */
    public double obterTotalDespesas(int usuarioId) {
        try {
            return lancamentoDAO.obterTotalDespesas(usuarioId);
        } catch (SQLException e) {
            System.err.println("Erro ao obter total de despesas: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Get balance for a user (income - expenses)
     */
    public double obterSaldoUsuario(int usuarioId) {
        try {
            return lancamentoDAO.obterSaldoUsuario(usuarioId);
        } catch (SQLException e) {
            System.err.println("Erro ao obter saldo do usuário: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Validate transaction value
     */
    public boolean isValorValido(double valor) {
        return valor != 0; // Value cannot be zero
    }
    
    /**
     * Validate transaction type
     */
    public boolean isTipoValido(String tipo) {
        return "receita".equals(tipo) || "despesa".equals(tipo);
    }
    
    /**
     * Get transactions summary for dashboard
     */
    public TransactionSummary obterResumo(int usuarioId) {
        try {
            double totalReceitas = lancamentoDAO.obterTotalReceitas(usuarioId);
            double totalDespesas = lancamentoDAO.obterTotalDespesas(usuarioId);
            double saldo = totalReceitas - totalDespesas;
            
            List<Lancamento> lancamentos = lancamentoDAO.listarPorUsuario(usuarioId);
            int totalTransacoes = lancamentos != null ? lancamentos.size() : 0;
            
            return new TransactionSummary(totalReceitas, totalDespesas, saldo, totalTransacoes);
        } catch (SQLException e) {
            System.err.println("Erro ao obter resumo de transações: " + e.getMessage());
            return new TransactionSummary(0, 0, 0, 0);
        }
    }
    
    /**
     * Inner class for transaction summary
     */
    public static class TransactionSummary {
        private final double totalReceitas;
        private final double totalDespesas;
        private final double saldo;
        private final int totalTransacoes;
        
        public TransactionSummary(double totalReceitas, double totalDespesas, double saldo, int totalTransacoes) {
            this.totalReceitas = totalReceitas;
            this.totalDespesas = totalDespesas;
            this.saldo = saldo;
            this.totalTransacoes = totalTransacoes;
        }
        
        public double getTotalReceitas() { return totalReceitas; }
        public double getTotalDespesas() { return totalDespesas; }
        public double getSaldo() { return saldo; }
        public int getTotalTransacoes() { return totalTransacoes; }
    }
}