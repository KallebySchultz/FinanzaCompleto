package controller;

import model.Usuario;
import util.NetworkClient;

/**
 * Controller para operações de autenticação
 */
public class AuthController {
    private NetworkClient networkClient;
    private Usuario usuarioLogado;
    
    // Constantes do protocolo
    private static final String CMD_LOGIN = "LOGIN";
    private static final String CMD_REGISTER = "REGISTER";
    private static final String CMD_LOGOUT = "LOGOUT";
    private static final String CMD_RECOVER_PASSWORD = "RECOVER_PASSWORD";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ERROR";
    private static final String STATUS_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    private static final String STATUS_USER_EXISTS = "USER_EXISTS";
    private static final String STATUS_INVALID_DATA = "INVALID_DATA";
    private static final String SEPARATOR = "|";
    private static final String FIELD_SEPARATOR = ";";
    
    public AuthController() {
        this.networkClient = new NetworkClient();
    }
    
    /**
     * Conecta ao servidor
     */
    public boolean conectarServidor() {
        return networkClient.connect();
    }
    
    /**
     * Realiza login no sistema
     */
    public LoginResult login(String email, String senha) {
        if (!networkClient.isConnected()) {
            return new LoginResult(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_LOGIN + SEPARATOR + email + SEPARATOR + senha;
        String resposta = networkClient.sendCommand(comando);
        
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1) {
            String status = partes[0];
            
            switch (status) {
                case STATUS_OK:
                    if (partes.length >= 2) {
                        String[] dadosUsuario = partes[1].split(FIELD_SEPARATOR);
                        if (dadosUsuario.length >= 3) {
                            usuarioLogado = new Usuario(
                                Integer.parseInt(dadosUsuario[0]),
                                dadosUsuario[1],
                                dadosUsuario[2]
                            );
                            return new LoginResult(true, "Login realizado com sucesso", usuarioLogado);
                        }
                    }
                    return new LoginResult(false, "Erro ao processar dados do usuário", null);
                    
                case STATUS_INVALID_CREDENTIALS:
                    return new LoginResult(false, "Email ou senha inválidos", null);
                    
                case STATUS_INVALID_DATA:
                    String mensagem = partes.length >= 2 ? partes[1] : "Dados inválidos";
                    return new LoginResult(false, mensagem, null);
                    
                default:
                    String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
                    return new LoginResult(false, erro, null);
            }
        }
        
        return new LoginResult(false, "Resposta inválida do servidor", null);
    }
    
    /**
     * Realiza registro de novo usuário
     */
    public LoginResult registrar(String nome, String email, String senha) {
        if (!networkClient.isConnected()) {
            return new LoginResult(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_REGISTER + SEPARATOR + nome + SEPARATOR + email + SEPARATOR + senha;
        String resposta = networkClient.sendCommand(comando);
        
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1) {
            String status = partes[0];
            
            switch (status) {
                case STATUS_OK:
                    if (partes.length >= 2) {
                        String[] dadosUsuario = partes[1].split(FIELD_SEPARATOR);
                        if (dadosUsuario.length >= 3) {
                            usuarioLogado = new Usuario(
                                Integer.parseInt(dadosUsuario[0]),
                                dadosUsuario[1],
                                dadosUsuario[2]
                            );
                            return new LoginResult(true, "Usuário cadastrado com sucesso", usuarioLogado);
                        }
                    }
                    return new LoginResult(false, "Erro ao processar dados do usuário", null);
                    
                case STATUS_USER_EXISTS:
                    return new LoginResult(false, "Email já cadastrado", null);
                    
                case STATUS_INVALID_DATA:
                    String mensagem = partes.length >= 2 ? partes[1] : "Dados inválidos";
                    return new LoginResult(false, mensagem, null);
                    
                default:
                    String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
                    return new LoginResult(false, erro, null);
            }
        }
        
        return new LoginResult(false, "Resposta inválida do servidor", null);
    }
    
    /**
     * Recupera senha do usuário
     */
    public LoginResult recuperarSenha(String email, String novaSenha) {
        if (!networkClient.isConnected()) {
            return new LoginResult(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_RECOVER_PASSWORD + SEPARATOR + email + SEPARATOR + novaSenha;
        String resposta = networkClient.sendCommand(comando);
        
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1) {
            String status = partes[0];
            
            switch (status) {
                case STATUS_OK:
                    return new LoginResult(true, "Senha alterada com sucesso", null);
                    
                default:
                    String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
                    return new LoginResult(false, erro, null);
            }
        }
        
        return new LoginResult(false, "Resposta inválida do servidor", null);
    }
    
    /**
     * Realiza logout
     */
    public void logout() {
        if (networkClient.isConnected()) {
            networkClient.sendCommand(CMD_LOGOUT);
        }
        usuarioLogado = null;
    }
    
    /**
     * Desconecta do servidor
     */
    public void desconectar() {
        logout();
        networkClient.disconnect();
    }
    
    /**
     * Retorna usuário logado
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    /**
     * Verifica se usuário está logado
     */
    public boolean isLogado() {
        return usuarioLogado != null;
    }
    
    /**
     * Retorna o cliente de rede
     */
    public NetworkClient getNetworkClient() {
        return networkClient;
    }
    
    /**
     * Classe para encapsular resultado do login
     */
    public static class LoginResult {
        private final boolean sucesso;
        private final String mensagem;
        private final Usuario usuario;
        
        public LoginResult(boolean sucesso, String mensagem, Usuario usuario) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.usuario = usuario;
        }
        
        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }
        public Usuario getUsuario() { return usuario; }
    }
}