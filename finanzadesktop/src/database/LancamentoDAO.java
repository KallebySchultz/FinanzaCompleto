package database;

import model.Lancamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LancamentoDAO - Data Access Object for Lancamento
 */
public class LancamentoDAO {
    
    /**
     * Find transaction by ID
     */
    public Lancamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM lancamentos WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Lancamento(
                        rs.getInt("id"),
                        rs.getDouble("valor"),
                        rs.getLong("data"),
                        rs.getString("descricao"),
                        rs.getInt("conta_id"),
                        rs.getInt("categoria_id"),
                        rs.getInt("usuario_id"),
                        rs.getString("tipo")
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * List all transactions for a user
     */
    public List<Lancamento> listarPorUsuario(int usuarioId) throws SQLException {
        List<Lancamento> lancamentos = new ArrayList<>();
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? ORDER BY data DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lancamentos.add(new Lancamento(
                        rs.getInt("id"),
                        rs.getDouble("valor"),
                        rs.getLong("data"),
                        rs.getString("descricao"),
                        rs.getInt("conta_id"),
                        rs.getInt("categoria_id"),
                        rs.getInt("usuario_id"),
                        rs.getString("tipo")
                    ));
                }
            }
        }
        return lancamentos;
    }
    
    /**
     * List transactions by account
     */
    public List<Lancamento> listarPorConta(int contaId) throws SQLException {
        List<Lancamento> lancamentos = new ArrayList<>();
        String sql = "SELECT * FROM lancamentos WHERE conta_id = ? ORDER BY data DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lancamentos.add(new Lancamento(
                        rs.getInt("id"),
                        rs.getDouble("valor"),
                        rs.getLong("data"),
                        rs.getString("descricao"),
                        rs.getInt("conta_id"),
                        rs.getInt("categoria_id"),
                        rs.getInt("usuario_id"),
                        rs.getString("tipo")
                    ));
                }
            }
        }
        return lancamentos;
    }
    
    /**
     * List transactions by type for a user
     */
    public List<Lancamento> listarPorTipo(int usuarioId, String tipo) throws SQLException {
        List<Lancamento> lancamentos = new ArrayList<>();
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? AND tipo = ? ORDER BY data DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setString(2, tipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lancamentos.add(new Lancamento(
                        rs.getInt("id"),
                        rs.getDouble("valor"),
                        rs.getLong("data"),
                        rs.getString("descricao"),
                        rs.getInt("conta_id"),
                        rs.getInt("categoria_id"),
                        rs.getInt("usuario_id"),
                        rs.getString("tipo")
                    ));
                }
            }
        }
        return lancamentos;
    }
    
    /**
     * Insert new transaction
     */
    public boolean inserir(Lancamento lancamento) throws SQLException {
        String sql = "INSERT INTO lancamentos (valor, data, descricao, conta_id, categoria_id, usuario_id, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDouble(1, lancamento.getValor());
            stmt.setLong(2, lancamento.getData());
            stmt.setString(3, lancamento.getDescricao());
            stmt.setInt(4, lancamento.getContaId());
            stmt.setInt(5, lancamento.getCategoriaId());
            stmt.setInt(6, lancamento.getUsuarioId());
            stmt.setString(7, lancamento.getTipo());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lancamento.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update transaction
     */
    public boolean atualizar(Lancamento lancamento) throws SQLException {
        String sql = "UPDATE lancamentos SET valor = ?, data = ?, descricao = ?, conta_id = ?, categoria_id = ?, tipo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, lancamento.getValor());
            stmt.setLong(2, lancamento.getData());
            stmt.setString(3, lancamento.getDescricao());
            stmt.setInt(4, lancamento.getContaId());
            stmt.setInt(5, lancamento.getCategoriaId());
            stmt.setString(6, lancamento.getTipo());
            stmt.setInt(7, lancamento.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete transaction
     */
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM lancamentos WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get total income for a user
     */
    public double obterTotalReceitas(int usuarioId) throws SQLException {
        String sql = "SELECT SUM(valor) as total FROM lancamentos WHERE usuario_id = ? AND tipo = 'receita'";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Get total expenses for a user
     */
    public double obterTotalDespesas(int usuarioId) throws SQLException {
        String sql = "SELECT SUM(ABS(valor)) as total FROM lancamentos WHERE usuario_id = ? AND tipo = 'despesa'";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Get balance for a user (income - expenses)
     */
    public double obterSaldoUsuario(int usuarioId) throws SQLException {
        return obterTotalReceitas(usuarioId) - obterTotalDespesas(usuarioId);
    }
}