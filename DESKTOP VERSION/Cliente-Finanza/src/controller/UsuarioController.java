package controller;

import model.Usuario;
import database.UsuarioDAO;
import java.sql.SQLException;

/**
 * UsuarioController - Controller for user operations
 */
public class UsuarioController {
    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;
    
    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Authenticate user login
     */
    public boolean autenticar(String email, String senha) {
        try {
            usuarioLogado = usuarioDAO.autenticar(email, senha);
            return usuarioLogado != null;
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Register new user
     */
    public boolean cadastrar(String nome, String email, String senha) {
        try {
            // Check if email already exists
            if (usuarioDAO.emailExiste(email)) {
                return false; // Email already exists
            }
            
            Usuario novoUsuario = new Usuario(nome, email, senha);
            return usuarioDAO.inserir(novoUsuario);
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update user profile
     */
    public boolean atualizarPerfil(String nome, String email, String senha) {
        if (usuarioLogado == null) {
            return false;
        }
        
        try {
            // Check if email already exists for another user
            Usuario usuarioComEmail = usuarioDAO.buscarPorEmail(email);
            if (usuarioComEmail != null && usuarioComEmail.getId() != usuarioLogado.getId()) {
                return false; // Email already exists for another user
            }
            
            usuarioLogado.setNome(nome);
            usuarioLogado.setEmail(email);
            usuarioLogado.setSenha(senha);
            
            boolean sucesso = usuarioDAO.atualizar(usuarioLogado);
            if (sucesso) {
                // Update logged user with new data
                usuarioLogado = usuarioDAO.buscarPorId(usuarioLogado.getId());
            }
            return sucesso;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar perfil: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete user account
     */
    public boolean excluirConta() {
        if (usuarioLogado == null) {
            return false;
        }
        
        try {
            boolean sucesso = usuarioDAO.deletar(usuarioLogado.getId());
            if (sucesso) {
                usuarioLogado = null; // Clear logged user
            }
            return sucesso;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir conta: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logout user
     */
    public void logout() {
        usuarioLogado = null;
    }
    
    /**
     * Get logged user
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLogado() {
        return usuarioLogado != null;
    }
    
    /**
     * Get logged user ID
     */
    public int getUsuarioLogadoId() {
        return usuarioLogado != null ? usuarioLogado.getId() : -1;
    }
    
    /**
     * Validate email format
     */
    public boolean isEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    /**
     * Validate password strength
     */
    public boolean isSenhaValida(String senha) {
        return senha != null && senha.length() >= 4; // Minimum 4 characters
    }
    
    /**
     * Check if email exists in database
     */
    public boolean emailExiste(String email) {
        try {
            return usuarioDAO.emailExiste(email);
        } catch (SQLException e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }
}