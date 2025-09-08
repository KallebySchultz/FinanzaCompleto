package dao;

import model.Usuario;
import util.DatabaseUtil;
import util.SecurityUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Usuario
 */
public class UsuarioDAO {
    
    /**
     * Insere um novo usuário no banco de dados
     * @param usuario objeto Usuario a ser inserido
     * @return true se inserido com sucesso
     */
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email, senha_hash) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Busca usuário por email
     * @param email email do usuário
     * @return Usuario encontrado ou null
     */
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Busca usuário por ID
     * @param id ID do usuário
     * @return Usuario encontrado ou null
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Autentica usuário com email e senha
     * @param email email do usuário
     * @param senha senha do usuário
     * @return Usuario autenticado ou null
     */
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        
        if (usuario != null && SecurityUtil.verificarSenha(senha, usuario.getSenhaHash())) {
            return usuario;
        }
        
        return null;
    }
    
    /**
     * Atualiza dados do usuário
     * @param usuario Usuario com dados atualizados
     * @return true se atualizado com sucesso
     */
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setInt(3, usuario.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Atualiza senha do usuário
     * @param idUsuario ID do usuário
     * @param novaSenha nova senha
     * @return true se atualizada com sucesso
     */
    public boolean atualizarSenha(int idUsuario, String novaSenha) {
        String sql = "UPDATE usuario SET senha_hash = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, SecurityUtil.hashSenha(novaSenha));
            stmt.setInt(2, idUsuario);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar senha: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Gera e armazena token de recuperação de senha
     * @param email email do usuário
     * @return token de recuperação ou null em caso de erro
     */
    public String gerarTokenRecuperacao(String email) {
        String token = SecurityUtil.gerarTokenRecuperacao();
        String sql = "UPDATE usuario SET password_reset_token = ?, reset_token_expiry = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            stmt.setString(2, email);
            
            if (stmt.executeUpdate() > 0) {
                return token;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao gerar token de recuperação: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Valida token de recuperação de senha
     * @param token token de recuperação
     * @return Usuario se token válido, null caso contrário
     */
    public Usuario validarTokenRecuperacao(String token) {
        String sql = "SELECT * FROM usuario WHERE password_reset_token = ? AND reset_token_expiry > NOW()";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao validar token de recuperação: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Redefine senha usando token de recuperação
     * @param token token de recuperação
     * @param novaSenha nova senha
     * @return true se redefinida com sucesso
     */
    public boolean redefinirSenhaComToken(String token, String novaSenha) {
        String sql = "UPDATE usuario SET senha_hash = ?, password_reset_token = NULL, reset_token_expiry = NULL WHERE password_reset_token = ? AND reset_token_expiry > NOW()";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, SecurityUtil.hashSenha(novaSenha));
            stmt.setString(2, token);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao redefinir senha com token: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Mapeia ResultSet para objeto Usuario
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        usuario.setDataCriacao(rs.getTimestamp("data_criacao"));
        usuario.setDataAtualizacao(rs.getTimestamp("data_atualizacao"));
        return usuario;
    }
}