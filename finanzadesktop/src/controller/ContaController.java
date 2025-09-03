package controller;

import model.Conta;
import database.ContaDAO;
import database.ContaDAO.ContaComSaldo;
import java.sql.SQLException;
import java.util.List;

/**
 * ContaController - Controller for account operations
 */
public class ContaController {
    private final ContaDAO contaDAO;
    
    public ContaController() {
        this.contaDAO = new ContaDAO();
    }
    
    /**
     * Create new account
     */
    public boolean criarConta(String nome, double saldoInicial, int usuarioId) {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                return false; // Invalid name
            }
            
            Conta novaConta = new Conta(nome.trim(), saldoInicial, usuarioId);
            return contaDAO.inserir(novaConta);
        } catch (SQLException e) {
            System.err.println("Erro ao criar conta: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update account
     */
    public boolean atualizarConta(int contaId, String nome, double saldoInicial) {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                return false; // Invalid name
            }
            
            Conta conta = contaDAO.buscarPorId(contaId);
            if (conta == null) {
                return false; // Account not found
            }
            
            conta.setNome(nome.trim());
            conta.setSaldoInicial(saldoInicial);
            
            return contaDAO.atualizar(conta);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar conta: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete account
     */
    public boolean excluirConta(int contaId) {
        try {
            return contaDAO.deletar(contaId);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir conta: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get account by ID
     */
    public Conta obterConta(int contaId) {
        try {
            return contaDAO.buscarPorId(contaId);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar conta: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List all accounts for a user
     */
    public List<Conta> listarContas(int usuarioId) {
        try {
            return contaDAO.listarPorUsuario(usuarioId);
        } catch (SQLException e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List accounts with current balance for a user
     */
    public List<ContaComSaldo> listarContasComSaldo(int usuarioId) {
        try {
            return contaDAO.listarComSaldoPorUsuario(usuarioId);
        } catch (SQLException e) {
            System.err.println("Erro ao listar contas com saldo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get current balance for an account
     */
    public double obterSaldoAtual(int contaId) {
        try {
            return contaDAO.obterSaldoAtual(contaId);
        } catch (SQLException e) {
            System.err.println("Erro ao obter saldo atual: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Get total balance for all user accounts
     */
    public double obterSaldoTotal(int usuarioId) {
        try {
            List<ContaComSaldo> contas = contaDAO.listarComSaldoPorUsuario(usuarioId);
            return contas.stream()
                    .mapToDouble(ContaComSaldo::getSaldoAtual)
                    .sum();
        } catch (SQLException e) {
            System.err.println("Erro ao obter saldo total: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Validate account name
     */
    public boolean isNomeValido(String nome) {
        return nome != null && !nome.trim().isEmpty() && nome.trim().length() <= 100;
    }
    
    /**
     * Check if user has any accounts
     */
    public boolean possuiContas(int usuarioId) {
        try {
            List<Conta> contas = contaDAO.listarPorUsuario(usuarioId);
            return contas != null && !contas.isEmpty();
        } catch (SQLException e) {
            System.err.println("Erro ao verificar contas: " + e.getMessage());
            return false;
        }
    }
}