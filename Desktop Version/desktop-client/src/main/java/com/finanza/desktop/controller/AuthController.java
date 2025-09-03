package com.finanza.desktop.controller;

import com.finanza.desktop.dao.UsuarioDAO;
import com.finanza.desktop.model.Usuario;

/**
 * Controller para gerenciar autenticação e operações de usuário
 */
public class AuthController {
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;

    public AuthController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Realiza login do usuário
     */
    public boolean login(String email, String senha) {
        Usuario usuario = usuarioDAO.autenticar(email, senha);
        if (usuario != null) {
            this.usuarioLogado = usuario;
            return true;
        }
        return false;
    }

    /**
     * Realiza logout do usuário
     */
    public void logout() {
        this.usuarioLogado = null;
    }

    /**
     * Registra um novo usuário
     */
    public boolean registrar(String nome, String email, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            return false;
        }
        if (senha == null || senha.length() < 6) {
            return false;
        }

        Usuario novoUsuario = new Usuario(nome.trim(), email.trim().toLowerCase(), senha);
        return usuarioDAO.cadastrar(novoUsuario);
    }

    /**
     * Verifica se o email é válido
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * Verifica se há um usuário logado
     */
    public boolean isLogado() {
        return usuarioLogado != null;
    }

    /**
     * Retorna o usuário logado
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Atualiza os dados do usuário logado
     */
    public boolean atualizarPerfil(String nome, String email) {
        if (usuarioLogado == null) {
            return false;
        }

        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            return false;
        }

        usuarioLogado.setNome(nome.trim());
        usuarioLogado.setEmail(email.trim().toLowerCase());
        
        return usuarioDAO.atualizar(usuarioLogado);
    }

    /**
     * Altera a senha do usuário logado
     */
    public boolean alterarSenha(String novaSenha) {
        if (usuarioLogado == null) {
            return false;
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            return false;
        }

        return usuarioDAO.atualizarSenha(usuarioLogado.getId(), novaSenha);
    }
}