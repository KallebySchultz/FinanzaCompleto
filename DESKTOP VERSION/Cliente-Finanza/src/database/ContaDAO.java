package database;

import model.Conta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ContaDAO - Data Access Object for Conta
 */
public class ContaDAO {
    
    /**
     * Find account by ID
     */
    public Conta buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM contas WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Conta(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("saldo_inicial"),
                        rs.getInt("usuario_id")
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * List all accounts for a user
     */
    public List<Conta> listarPorUsuario(int usuarioId) throws SQLException {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas WHERE usuario_id = ? ORDER BY nome";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contas.add(new Conta(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("saldo_inicial"),
                        rs.getInt("usuario_id")
                    ));
                }
            }
        }
        return contas;
    }
    
    /**
     * Insert new account
     */
    public boolean inserir(Conta conta) throws SQLException {
        String sql = "INSERT INTO contas (nome, saldo_inicial, usuario_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, conta.getNome());
            stmt.setDouble(2, conta.getSaldoInicial());
            stmt.setInt(3, conta.getUsuarioId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        conta.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update account
     */
    public boolean atualizar(Conta conta) throws SQLException {
        String sql = "UPDATE contas SET nome = ?, saldo_inicial = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conta.getNome());
            stmt.setDouble(2, conta.getSaldoInicial());
            stmt.setInt(3, conta.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete account
     */
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM contas WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get current balance for an account (initial balance + transactions)
     */
    public double obterSaldoAtual(int contaId) throws SQLException {
        String sql = """
            SELECT c.saldo_inicial + COALESCE(SUM(
                CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END
            ), 0) as saldo_atual
            FROM contas c
            LEFT JOIN lancamentos l ON c.id = l.conta_id
            WHERE c.id = ?
            GROUP BY c.id, c.saldo_inicial
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo_atual");
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Get accounts with current balance for a user
     */
    public List<ContaComSaldo> listarComSaldoPorUsuario(int usuarioId) throws SQLException {
        List<ContaComSaldo> contas = new ArrayList<>();
        String sql = """
            SELECT c.id, c.nome, c.saldo_inicial, c.usuario_id,
                   c.saldo_inicial + COALESCE(SUM(
                       CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END
                   ), 0) as saldo_atual
            FROM contas c
            LEFT JOIN lancamentos l ON c.id = l.conta_id
            WHERE c.usuario_id = ?
            GROUP BY c.id, c.nome, c.saldo_inicial, c.usuario_id
            ORDER BY c.nome
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Conta conta = new Conta(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("saldo_inicial"),
                        rs.getInt("usuario_id")
                    );
                    double saldoAtual = rs.getDouble("saldo_atual");
                    contas.add(new ContaComSaldo(conta, saldoAtual));
                }
            }
        }
        return contas;
    }
    
    /**
     * Inner class to hold account with current balance
     */
    public static class ContaComSaldo {
        private final Conta conta;
        private final double saldoAtual;
        
        public ContaComSaldo(Conta conta, double saldoAtual) {
            this.conta = conta;
            this.saldoAtual = saldoAtual;
        }
        
        public Conta getConta() {
            return conta;
        }
        
        public double getSaldoAtual() {
            return saldoAtual;
        }
    }
}