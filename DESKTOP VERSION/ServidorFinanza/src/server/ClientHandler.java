package server;

import dao.UsuarioDAO;
import model.Usuario;

import java.io.*;
import java.net.Socket;

/**
 * Manipulador de conexões de clientes
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean testMode;
    private UsuarioDAO usuarioDAO;
    
    public ClientHandler(Socket socket, boolean testMode) {
        this.clientSocket = socket;
        this.testMode = testMode;
        this.usuarioDAO = new UsuarioDAO();
    }
    
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processCommand(inputLine);
                out.println(response);
            }
            
        } catch (IOException e) {
            System.err.println("Erro ao processar cliente: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
    
    private String processCommand(String command) {
        try {
            String[] parts = Protocol.parseCommand(command);
            
            if (parts.length == 0) {
                return Protocol.createErrorResponse("Comando vazio");
            }
            
            String cmd = parts[0];
            
            switch (cmd) {
                case Protocol.CMD_LOGIN:
                    return processLogin(parts);
                    
                case Protocol.CMD_REGISTER:
                    return processRegister(parts);
                    
                case Protocol.CMD_RECOVER_PASSWORD:
                    return processRecoverPassword(parts);
                    
                case Protocol.CMD_LOGOUT:
                    return processLogout(parts);
                    
                default:
                    return Protocol.createErrorResponse("Comando não reconhecido: " + cmd);
            }
            
        } catch (Exception e) {
            return Protocol.createErrorResponse("Erro interno do servidor: " + e.getMessage());
        }
    }
    
    private String processLogin(String[] parts) {
        if (parts.length < 3) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para login");
        }
        
        String email = parts[1];
        String senha = parts[2];
        
        if (testMode) {
            // Modo de teste - retorna sucesso simulado
            return Protocol.createSuccessResponse("1;Test User;" + email);
        }
        
        Usuario usuario = usuarioDAO.autenticar(email, senha);
        
        if (usuario != null) {
            String userData = usuario.getId() + ";" + usuario.getNome() + ";" + usuario.getEmail();
            return Protocol.createSuccessResponse(userData);
        } else {
            return Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Email ou senha inválidos");
        }
    }
    
    private String processRegister(String[] parts) {
        if (parts.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para registro");
        }
        
        String nome = parts[1];
        String email = parts[2];
        String senha = parts[3];
        
        if (testMode) {
            // Modo de teste - retorna sucesso simulado
            return Protocol.createSuccessResponse("1;" + nome + ";" + email);
        }
        
        // Verifica se usuário já existe
        if (usuarioDAO.buscarPorEmail(email) != null) {
            return Protocol.createResponse(Protocol.STATUS_USER_EXISTS, "Email já cadastrado");
        }
        
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(util.SecurityUtil.hashSenha(senha));
        
        if (usuarioDAO.inserir(usuario)) {
            String userData = usuario.getId() + ";" + usuario.getNome() + ";" + usuario.getEmail();
            return Protocol.createSuccessResponse(userData);
        } else {
            return Protocol.createErrorResponse("Erro ao cadastrar usuário");
        }
    }
    
    private String processRecoverPassword(String[] parts) {
        if (parts.length < 3) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para recuperação");
        }
        
        String email = parts[1];
        String novaSenha = parts[2];
        
        if (testMode) {
            // Modo de teste - retorna sucesso simulado
            return Protocol.createSuccessResponse("Senha alterada com sucesso");
        }
        
        // Verifica se usuário existe
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            return Protocol.createErrorResponse("Email não encontrado");
        }
        
        // Atualiza a senha
        if (usuarioDAO.atualizarSenha(usuario.getId(), novaSenha)) {
            return Protocol.createSuccessResponse("Senha alterada com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao alterar senha");
        }
    }
    
    private String processLogout(String[] parts) {
        // Para logout, simplesmente retornamos sucesso
        return Protocol.createSuccessResponse("Logout realizado com sucesso");
    }
}