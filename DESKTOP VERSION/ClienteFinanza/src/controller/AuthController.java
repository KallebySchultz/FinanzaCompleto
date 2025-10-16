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
    private static final String CMD_LIST_USERS = "LIST_USERS";
    private static final String CMD_UPDATE_USER = "UPDATE_USER";
    private static final String CMD_UPDATE_USER_PASSWORD = "UPDATE_USER_PASSWORD";
    private static final String CMD_DELETE_USER = "DELETE_USER";
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
                            int userId = Integer.parseInt(dadosUsuario[0]);
                            String nome = dadosUsuario[1];
                            String emailUsuario = dadosUsuario[2];
                            String role = dadosUsuario.length >= 4 ? dadosUsuario[3] : "user";
                            
                            usuarioLogado = new Usuario(userId, nome, emailUsuario, "", role);
                            
                            // Validate that only admins can access desktop panel
                            if (!"admin".equals(role)) {
                                return new LoginResult(false, "Acesso negado. Apenas administradores podem acessar o painel desktop.", null);
                            }
                            
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
     * Lista todos os usuários (apenas para admin)
     */
    public java.util.List<Usuario> listarUsuarios() {
        java.util.List<Usuario> usuarios = new java.util.ArrayList<>();
        
        if (!networkClient.isConnected()) {
            System.out.println("listarUsuarios - Cliente não está conectado");
            return usuarios;
        }
        
        String comando = CMD_LIST_USERS;
        String resposta = networkClient.sendCommand(comando);
        
        System.out.println("listarUsuarios - Resposta recebida: " + resposta);
        
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] usuariosData = partes[1].split(FIELD_SEPARATOR);
                System.out.println("listarUsuarios - Registros de dados: " + usuariosData.length);
                for (String userData : usuariosData) {
                    if (!userData.trim().isEmpty()) {
                        String[] campos = userData.split(",");
                        System.out.println("listarUsuarios - Processando usuário: " + userData + " (campos: " + campos.length + ")");
                        if (campos.length >= 3) {
                            Usuario usuario = new Usuario(
                                Integer.parseInt(campos[0].trim()),
                                campos[1].trim(),
                                campos[2].trim()
                            );
                            // Parse dataCriacao if available
                            if (campos.length >= 4 && !campos[3].trim().isEmpty()) {
                                try {
                                    usuario.setDataCriacao(java.sql.Timestamp.valueOf(campos[3].trim()));
                                } catch (Exception e) {
                                    System.out.println("listarUsuarios - Erro ao parsear data de criação: " + e.getMessage());
                                }
                            }
                            usuarios.add(usuario);
                            System.out.println("listarUsuarios - Usuário adicionado: ID=" + usuario.getId() + ", Nome=" + usuario.getNome());
                        }
                    }
                }
            }
        } else {
            System.out.println("listarUsuarios - Resposta não é OK ou não tem dados: " + resposta);
        }
        
        System.out.println("listarUsuarios - Total de usuários carregados: " + usuarios.size());
        return usuarios;
    }
    
    /**
     * Atualiza informações de um usuário
     */
    public boolean atualizarUsuario(int userId, String novoNome, String novoEmail) {
        if (!networkClient.isConnected()) {
            return false;
        }
        
        String comando = CMD_UPDATE_USER + SEPARATOR + userId + SEPARATOR + novoNome + SEPARATOR + novoEmail;
        String resposta = networkClient.sendCommand(comando);
        
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            // Se o usuário editado é o próprio usuário logado, atualizar os dados locais
            if (usuarioLogado != null && usuarioLogado.getId() == userId) {
                usuarioLogado = new Usuario(userId, novoNome, novoEmail);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Atualiza senha de um usuário
     */
    public boolean atualizarSenhaUsuario(int userId, String novaSenha) {
        if (!networkClient.isConnected()) {
            return false;
        }
        
        String comando = CMD_UPDATE_USER_PASSWORD + SEPARATOR + userId + SEPARATOR + novaSenha;
        String resposta = networkClient.sendCommand(comando);
        
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        return partes.length >= 1 && STATUS_OK.equals(partes[0]);
    }
    
    /**
     * Exclui um usuário
     */
    public boolean excluirUsuario(int userId) {
        if (!networkClient.isConnected()) {
            return false;
        }
        
        String comando = CMD_DELETE_USER + SEPARATOR + userId;
        String resposta = networkClient.sendCommand(comando);
        
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        return partes.length >= 1 && STATUS_OK.equals(partes[0]);
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