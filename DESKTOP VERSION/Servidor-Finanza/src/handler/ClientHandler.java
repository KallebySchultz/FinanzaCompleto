package handler;

import server.FinanzaServer;
import util.JsonUtils;
import java.io.*;
import java.net.*;
import java.util.Map;

/**
 * ClientHandler - Handles individual client connections
 * Processes JSON commands from Android and Desktop clients
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final FinanzaServer server;
    private BufferedReader input;
    private PrintWriter output;
    private boolean isConnected = true;
    
    public ClientHandler(Socket clientSocket, FinanzaServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            // Setup input/output streams
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            
            // Send welcome message
            sendResponse("connected", "Conectado ao Finanza Server", true);
            
            String inputLine;
            while (isConnected && (inputLine = input.readLine()) != null) {
                try {
                    processCommand(inputLine);
                } catch (Exception e) {
                    System.err.println("Erro ao processar comando: " + e.getMessage());
                    sendError("Erro interno do servidor: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erro na conexão com cliente: " + e.getMessage());
        } finally {
            close();
        }
    }
    
    /**
     * Process incoming command from client
     */
    private void processCommand(String command) {
        try {
            Map<String, Object> request = JsonUtils.parseJson(command);
            String action = (String) request.get("action");
            
            if (action == null) {
                sendError("Ação não especificada");
                return;
            }
            
            switch (action) {
                case "ping":
                    handlePing();
                    break;
                    
                case "sync_user":
                    handleSyncUser(request);
                    break;
                    
                case "sync_accounts":
                    handleSyncAccounts(request);
                    break;
                    
                case "sync_transactions":
                    handleSyncTransactions(request);
                    break;
                    
                case "sync_categories":
                    handleSyncCategories(request);
                    break;
                    
                case "login":
                    handleLogin(request);
                    break;
                    
                case "register":
                    handleRegister(request);
                    break;
                    
                default:
                    sendError("Ação não reconhecida: " + action);
            }
            
        } catch (Exception e) {
            sendError("Comando JSON inválido: " + e.getMessage());
        }
    }
    
    /**
     * Handle ping command
     */
    private void handlePing() {
        sendResponse("pong", "Servidor ativo", true);
    }
    
    /**
     * Handle user synchronization
     */
    private void handleSyncUser(Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        if (userId == null) {
            sendError("userId é obrigatório");
            return;
        }
        
        // For now, just echo back success
        // TODO: Implement actual database synchronization
        sendResponse("sync_user", "Usuário sincronizado: " + userId, true);
    }
    
    /**
     * Handle accounts synchronization
     */
    private void handleSyncAccounts(Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        if (userId == null) {
            sendError("userId é obrigatório");
            return;
        }
        
        // For now, just echo back success
        // TODO: Implement actual database synchronization
        sendResponse("sync_accounts", "Contas sincronizadas para usuário: " + userId, true);
    }
    
    /**
     * Handle transactions synchronization
     */
    private void handleSyncTransactions(Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        if (userId == null) {
            sendError("userId é obrigatório");
            return;
        }
        
        // For now, just echo back success
        // TODO: Implement actual database synchronization
        sendResponse("sync_transactions", "Lançamentos sincronizados para usuário: " + userId, true);
    }
    
    /**
     * Handle categories synchronization
     */
    private void handleSyncCategories(Map<String, Object> request) {
        // For now, just echo back success
        // TODO: Implement actual database synchronization
        sendResponse("sync_categories", "Categorias sincronizadas", true);
    }
    
    /**
     * Handle login command
     */
    private void handleLogin(Map<String, Object> request) {
        String email = (String) request.get("email");
        String senha = (String) request.get("senha");
        
        if (email == null || senha == null) {
            sendError("Email e senha são obrigatórios");
            return;
        }
        
        // For now, just echo back success
        // TODO: Implement actual authentication
        sendResponse("login", "Login realizado com sucesso para: " + email, true);
    }
    
    /**
     * Handle register command
     */
    private void handleRegister(Map<String, Object> request) {
        String nome = (String) request.get("nome");
        String email = (String) request.get("email");
        String senha = (String) request.get("senha");
        
        if (nome == null || email == null || senha == null) {
            sendError("Nome, email e senha são obrigatórios");
            return;
        }
        
        // For now, just echo back success
        // TODO: Implement actual registration
        sendResponse("register", "Usuário registrado com sucesso: " + email, true);
    }
    
    /**
     * Send success response
     */
    private void sendResponse(String action, String message, boolean success) {
        try {
            String response = JsonUtils.createResponse(action, message, success);
            output.println(response);
        } catch (Exception e) {
            System.err.println("Erro ao enviar resposta: " + e.getMessage());
        }
    }
    
    /**
     * Send error response
     */
    private void sendError(String errorMessage) {
        sendResponse("error", errorMessage, false);
    }
    
    /**
     * Close client connection
     */
    public void close() {
        isConnected = false;
        
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
        
        // Remove from server's active clients list
        server.removeClient(this);
    }
}