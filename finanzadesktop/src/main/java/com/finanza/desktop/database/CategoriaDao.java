package com.finanza.desktop.database;

import com.finanza.desktop.model.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Categoria
 */
public class CategoriaDao {
    private final DatabaseManager dbManager;

    public CategoriaDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int inserir(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, cor_hex, tipo) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getCorHex());
            stmt.setString(3, categoria.getTipo());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        categoria.setId(id);
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir categoria", e);
        }
        
        return 0;
    }

    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria", e);
        }
        
        return null;
    }

    public List<Categoria> listarPorTipo(String tipo) {
        String sql = "SELECT * FROM categorias WHERE tipo = ? ORDER BY nome";
        List<Categoria> categorias = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar categorias por tipo", e);
        }
        
        return categorias;
    }

    public List<Categoria> listarTodas() {
        String sql = "SELECT * FROM categorias ORDER BY tipo, nome";
        List<Categoria> categorias = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as categorias", e);
        }
        
        return categorias;
    }

    public boolean atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, cor_hex = ?, tipo = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getCorHex());
            stmt.setString(3, categoria.getTipo());
            stmt.setInt(4, categoria.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria", e);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar categoria", e);
        }
    }

    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setCorHex(rs.getString("cor_hex"));
        categoria.setTipo(rs.getString("tipo"));
        return categoria;
    }
}