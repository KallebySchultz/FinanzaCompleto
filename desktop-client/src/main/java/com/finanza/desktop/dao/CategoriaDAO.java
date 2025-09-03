package com.finanza.desktop.dao;

import com.finanza.desktop.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com categorias
 */
public class CategoriaDAO {
    private DatabaseManager dbManager;

    public CategoriaDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Cria uma nova categoria
     */
    public boolean criar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, cor_hex, tipo) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getCorHex());
            stmt.setString(3, categoria.getTipo());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar categoria: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Busca uma categoria por ID
     */
    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categoria.setCorHex(rs.getString("cor_hex"));
                categoria.setTipo(rs.getString("tipo"));
                return categoria;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Lista todas as categorias
     */
    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY tipo, nome";
        
        try (Statement stmt = dbManager.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categoria.setCorHex(rs.getString("cor_hex"));
                categoria.setTipo(rs.getString("tipo"));
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        }
        
        return categorias;
    }

    /**
     * Lista categorias por tipo (receita ou despesa)
     */
    public List<Categoria> listarPorTipo(String tipo) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE tipo = ? ORDER BY nome";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categoria.setCorHex(rs.getString("cor_hex"));
                categoria.setTipo(rs.getString("tipo"));
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias por tipo: " + e.getMessage());
        }
        
        return categorias;
    }

    /**
     * Atualiza uma categoria
     */
    public boolean atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, cor_hex = ?, tipo = ? WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getCorHex());
            stmt.setString(3, categoria.getTipo());
            stmt.setInt(4, categoria.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Remove uma categoria
     */
    public boolean remover(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao remover categoria: " + e.getMessage());
        }
        
        return false;
    }
}