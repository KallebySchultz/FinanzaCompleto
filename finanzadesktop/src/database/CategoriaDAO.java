package database;

import model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoriaDAO - Data Access Object for Categoria
 */
public class CategoriaDAO {
    
    /**
     * Find category by ID
     */
    public Categoria buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cor_hex"),
                        rs.getString("tipo")
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * List all categories
     */
    public List<Categoria> listarTodas() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY tipo, nome";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(new Categoria(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cor_hex"),
                    rs.getString("tipo")
                ));
            }
        }
        return categorias;
    }
    
    /**
     * List categories by type
     */
    public List<Categoria> listarPorTipo(String tipo) throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE tipo = ? ORDER BY nome";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cor_hex"),
                        rs.getString("tipo")
                    ));
                }
            }
        }
        return categorias;
    }
    
    /**
     * Insert new category
     */
    public boolean inserir(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome, cor_hex, tipo) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getCorHex());
            stmt.setString(3, categoria.getTipo());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update category
     */
    public boolean atualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categorias SET nome = ?, cor_hex = ?, tipo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getCorHex());
            stmt.setString(3, categoria.getTipo());
            stmt.setInt(4, categoria.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete category
     */
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get categories for expense
     */
    public List<Categoria> listarDespesas() throws SQLException {
        return listarPorTipo("despesa");
    }
    
    /**
     * Get categories for income
     */
    public List<Categoria> listarReceitas() throws SQLException {
        return listarPorTipo("receita");
    }
}