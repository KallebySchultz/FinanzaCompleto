package server;

import dao.UsuarioDAO;
import model.Usuario;
import util.SecurityUtil;

import java.io.*;
import java.net.Socket;

/**
 * Manipulador de clientes conectados ao servidor
 * Processa comandos recebidos dos clientes e envia respostas
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean testMode;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;
    
    public ClientHandler(Socket clientSocket, boolean testMode) {
        this.clientSocket = clientSocket;
        this.testMode = testMode;
        this.usuarioDAO = new UsuarioDAO();
    }
    
    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String clientAddress = clientSocket.getRemoteSocketAddress().toString();
            System.out.println("Cliente conectado: " + clientAddress);
            
            String comando;
            while ((comando = input.readLine()) != null) {
                System.out.println("Comando recebido: " + comando);
                String resposta = processarComando(comando);
                System.out.println("Resposta enviada: " + resposta);
                output.println(resposta);
            }
            
        } catch (IOException e) {
            System.err.println("Erro na comunicação com cliente: " + e.getMessage());
        } finally {
            fecharConexao();
        }
    }
    
    /**
     * Processa comando recebido do cliente
     */
    private String processarComando(String comando) {
        String[] partes = Protocol.parseCommand(comando);
        
        if (partes.length == 0) {
            return Protocol.createErrorResponse("Comando inválido");
        }
        
        String cmd = partes[0];
        
        try {
            switch (cmd) {
                case Protocol.CMD_LOGIN:
                    return processarLogin(partes);
                    
                case Protocol.CMD_REGISTER:
                    return processarRegistro(partes);
                    
                case Protocol.CMD_LOGOUT:
                    return processarLogout();
                    
                case Protocol.CMD_RESET_PASSWORD:
                    return processarResetSenha(partes);
                    
                case Protocol.CMD_CHANGE_PASSWORD:
                    return processarAlterarSenha(partes);
                    
                default:
                    return Protocol.createErrorResponse("Comando não reconhecido: " + cmd);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao processar comando '" + cmd + "': " + e.getMessage());
            return Protocol.createErrorResponse("Erro interno do servidor");
        }
    }
    
    /**
     * Processa comando de login
     */
    private String processarLogin(String[] partes) {
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para login");
        }
        
        String email = partes[1];
        String senha = partes[2];
        
        // Validação básica
        if (email.isEmpty() || senha.isEmpty()) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email e senha são obrigatórios");
        }
        
        if (!SecurityUtil.validarEmail(email)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido");
        }
        
        // Modo de teste - sempre retorna sucesso
        if (testMode) {
            usuarioLogado = new Usuario(1, "Usuário Teste", email, "");
            String userData = usuarioLogado.getId() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getNome() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getEmail();
            return Protocol.createSuccessResponse(userData);
        }
        
        // Autenticação real
        Usuario usuario = usuarioDAO.autenticar(email, senha);
        if (usuario != null) {
            usuarioLogado = usuario;
            String userData = usuario.getId() + Protocol.FIELD_SEPARATOR + 
                             usuario.getNome() + Protocol.FIELD_SEPARATOR + 
                             usuario.getEmail();
            return Protocol.createSuccessResponse(userData);
        } else {
            return Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Email ou senha inválidos");
        }
    }
    
    /**
     * Processa comando de registro
     */
    private String processarRegistro(String[] partes) {
        if (partes.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para registro");
        }
        
        String nome = partes[1];
        String email = partes[2];
        String senha = partes[3];
        
        // Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Todos os campos são obrigatórios");
        }
        
        if (!SecurityUtil.validarEmail(email)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido");
        }
        
        if (!SecurityUtil.validarSenha(senha)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Senha deve ter pelo menos 6 caracteres");
        }
        
        // Modo de teste
        if (testMode) {
            usuarioLogado = new Usuario(1, nome, email, "");
            String userData = usuarioLogado.getId() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getNome() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getEmail();
            return Protocol.createSuccessResponse(userData);
        }
        
        // Verifica se email já existe
        if (usuarioDAO.buscarPorEmail(email) != null) {
            return Protocol.createResponse(Protocol.STATUS_USER_EXISTS, "Email já cadastrado");
        }
        
        // Cria novo usuário
        Usuario novoUsuario = new Usuario(nome, email, SecurityUtil.hashSenha(senha));
        if (usuarioDAO.inserir(novoUsuario)) {
            usuarioLogado = novoUsuario;
            String userData = novoUsuario.getId() + Protocol.FIELD_SEPARATOR + 
                             novoUsuario.getNome() + Protocol.FIELD_SEPARATOR + 
                             novoUsuario.getEmail();
            return Protocol.createSuccessResponse(userData);
        } else {
            return Protocol.createErrorResponse("Erro ao criar usuário");
        }
    }
    
    /**
     * Processa comando de logout
     */
    private String processarLogout() {
        usuarioLogado = null;
        return Protocol.createSuccessResponse("Logout realizado");
    }
    
    /**
     * Processa reset de senha por email
     */
    private String processarResetSenha(String[] partes) {
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Email é obrigatório para reset de senha");
        }
        
        String email = partes[1];
        
        if (!SecurityUtil.validarEmail(email)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Reset de senha enviado por email (modo teste)");
        }
        
        // Verifica se usuário existe
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            // Por segurança, não revelamos se o email existe ou não
            return Protocol.createSuccessResponse("Se o email existe, instruções foram enviadas");
        }
        
        // Aqui seria implementado o envio de email real
        // Por simplicidade, vamos apenas gerar uma nova senha temporária
        String novaSenhaTemp = gerarSenhaTemporaria();
        
        if (usuarioDAO.atualizarSenha(usuario.getId(), novaSenhaTemp)) {
            // Em uma implementação real, enviaria por email
            // Para teste, retornamos a nova senha
            return Protocol.createSuccessResponse("Nova senha temporária: " + novaSenhaTemp);
        } else {
            return Protocol.createErrorResponse("Erro ao resetar senha");
        }
    }
    
    /**
     * Processa alteração de senha
     */
    private String processarAlterarSenha(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Senha atual e nova senha são obrigatórias");
        }
        
        String senhaAtual = partes[1];
        String novaSenha = partes[2];
        
        if (!SecurityUtil.validarSenha(novaSenha)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nova senha deve ter pelo menos 6 caracteres");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Senha alterada com sucesso (modo teste)");
        }
        
        // Verifica senha atual
        Usuario usuario = usuarioDAO.autenticar(usuarioLogado.getEmail(), senhaAtual);
        if (usuario == null) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Senha atual incorreta");
        }
        
        // Atualiza senha
        if (usuarioDAO.atualizarSenha(usuarioLogado.getId(), novaSenha)) {
            return Protocol.createSuccessResponse("Senha alterada com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao alterar senha");
        }
    }
    
    /**
     * Gera uma senha temporária simples
     */
    private String gerarSenhaTemporaria() {
        return "temp" + System.currentTimeMillis() % 10000;
    }
    
    /**
     * Fecha conexão com cliente
     */
    private void fecharConexao() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Conexão com cliente encerrada");
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}