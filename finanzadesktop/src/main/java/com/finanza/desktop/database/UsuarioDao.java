package com.finanza.desktop.database;

import com.finanza.desktop.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Usuario
 */
public class UsuarioDao {
    private final DatabaseManager dbManager;

    public UsuarioDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, data_criacao) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setLong(4, usuario.getDataCriacao());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        usuario.setId(id);
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário", e);
        }
        
        return 0;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
        
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por email", e);
        }
        
        return null;
    }

    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário", e);
        }
        
        return null;
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        }
        
        return usuarios;
    }

    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setInt(3, usuario.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário", e);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário", e);
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setDataCriacao(rs.getLong("data_criacao"));
        return usuario;
    }
}