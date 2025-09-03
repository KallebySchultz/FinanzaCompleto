package com.finanza.desktop.controller;

import com.finanza.desktop.model.Usuario;
import com.finanza.desktop.database.UsuarioDao;

/**
 * Controller para operações de usuário
 * Segue padrões similares aos Controllers do Android
 */
public class UsuarioController {
    private final UsuarioDao usuarioDao;
    
    public UsuarioController() {
        this.usuarioDao = new UsuarioDao();
    }
    
    /**
     * Autentica um usuário
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return Usuario autenticado ou null se falhou
     */
    public Usuario autenticar(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        return usuarioDao.autenticar(email.trim(), senha);
    }
    
    /**
     * Registra um novo usuário
     * @param nome Nome do usuário
     * @param email Email único do usuário
     * @param senha Senha do usuário
     * @return ID do usuário criado
     */
    public int registrarUsuario(String nome, String email, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        // Verificar se email já existe
        Usuario existente = usuarioDao.buscarPorEmail(email.trim());
        if (existente != null) {
            throw new IllegalArgumentException("Email já está em uso");
        }
        
        Usuario novoUsuario = new Usuario(nome.trim(), email.trim(), senha);
        return usuarioDao.inserir(novoUsuario);
    }
    
    /**
     * Busca usuário por ID
     * @param id ID do usuário
     * @return Usuario encontrado ou null
     */
    public Usuario buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser maior que zero");
        }
        
        return usuarioDao.buscarPorId(id);
    }
    
    /**
     * Atualiza dados do usuário
     * @param usuario Usuario com dados atualizados
     * @return true se atualizado com sucesso
     */
    public boolean atualizarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        
        if (usuario.getId() <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        
        // Verificar se email já existe para outro usuário
        Usuario existente = usuarioDao.buscarPorEmail(usuario.getEmail().trim());
        if (existente != null && existente.getId() != usuario.getId()) {
            throw new IllegalArgumentException("Email já está em uso por outro usuário");
        }
        
        return usuarioDao.atualizar(usuario);
    }
    
    /**
     * Valida se o email tem formato válido
     * @param email Email a ser validado
     * @return true se válido
     */
    public boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
    
    /**
     * Valida se a senha atende aos critérios mínimos
     * @param senha Senha a ser validada
     * @return true se válida
     */
    public boolean validarSenha(String senha) {
        if (senha == null) {
            return false;
        }
        
        // Mínimo 4 caracteres (simples para demo)
        return senha.length() >= 4;
    }
}