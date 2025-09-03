package database;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UsuarioDAO - Data Access Object for Usuario
 */
public class UsuarioDAO {
    
    /**
     * Find user by email and password for login
     */
    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getLong("data_criacao")
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * Find user by email
     */
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getLong("data_criacao")
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * Find user by ID
     */
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getLong("data_criacao")
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * Insert new user
     */
    public boolean inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, data_criacao) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setLong(4, usuario.getDataCriacao());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update user
     */
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete user
     */
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * List all users
     */
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha"),
                    rs.getLong("data_criacao")
                ));
            }
        }
        return usuarios;
    }
    
    /**
     * Check if email exists
     */
    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}