package com.finanza.server;

import com.finanza.server.database.ServerDatabaseManager;
import com.finanza.server.model.ServerResponse;
import com.finanza.server.model.SyncRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor Java para receber dados do aplicativo Finanza
 * Implementa threading para múltiplas conexões simultâneas
 * Versão aprimorada com banco de dados e processamento JSON
 */
public class FinanzaServer {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private boolean running = false;
    private ServerDatabaseManager dbManager;
    private Gson gson;
    
    public FinanzaServer() {
        executor = Executors.newFixedThreadPool(10);
        dbManager = ServerDatabaseManager.getInstance();
        gson = new Gson();
    }
    
    public void iniciar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        running = true;
        
        System.out.println("=====================================");
        System.out.println("    🚀 Servidor Finanza v2.0");
        System.out.println("=====================================");
        System.out.println("✅ Servidor iniciado na porta " + PORT);
        System.out.println("✅ Banco de dados inicializado");
        System.out.println("⏳ Aguardando conexões...");
        System.out.println("=====================================");
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("📱 Nova conexão de: " + clientSocket.getInetAddress().getHostAddress());
                executor.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("❌ Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        }
    }
    
    public void parar() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (executor != null) {
            executor.shutdown();
        }
        if (dbManager != null) {
            dbManager.close();
        }
    }
    
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String clientIp;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientIp = socket.getInetAddress().getHostAddress();
        }
        
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                String inputLine = in.readLine();
                System.out.println("📨 Recebido de " + clientIp + ": " + inputLine);
                
                // Processar comando recebido
                ServerResponse response = processarComando(inputLine);
                String jsonResponse = gson.toJson(response);
                
                out.println(jsonResponse);
                System.out.println("📤 Resposta enviada para " + clientIp + ": " + response.getStatus());
                
            } catch (IOException e) {
                System.err.println("❌ Erro ao processar cliente " + clientIp + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("❌ Erro ao fechar conexão com " + clientIp + ": " + e.getMessage());
                }
            }
        }
        
        private ServerResponse processarComando(String comando) {
            if (comando == null || comando.trim().isEmpty()) {
                return ServerResponse.error("Comando vazio");
            }
            
            try {
                // Verificar se é JSON válido
                if (comando.startsWith("{") && comando.endsWith("}")) {
                    SyncRequest request = gson.fromJson(comando, SyncRequest.class);
                    
                    // Atualizar último acesso do cliente
                    dbManager.updateClientConnection(request.getClientId(), clientIp);
                    
                    return processarSyncRequest(request);
                } else {
                    // Comando simples de teste
                    return processarComandoSimples(comando);
                }
                
            } catch (JsonSyntaxException e) {
                dbManager.logSyncOperation("unknown", "parse_error", comando, "error");
                return ServerResponse.error("JSON inválido: " + e.getMessage());
            } catch (Exception e) {
                dbManager.logSyncOperation("unknown", "general_error", comando, "error");
                return ServerResponse.error("Erro ao processar: " + e.getMessage());
            }
        }
        
        private ServerResponse processarSyncRequest(SyncRequest request) {
            String action = request.getAction();
            String clientId = request.getClientId();
            
            try {
                switch (action) {
                    case "ping":
                        return processarPing(request);
                    case "sync_user":
                        return processarSyncUser(request);
                    case "sync_accounts":
                        return processarSyncAccounts(request);
                    case "sync_transactions":
                        return processarSyncTransactions(request);
                    case "sync_categories":
                        return processarSyncCategories(request);
                    case "get_server_info":
                        return processarServerInfo(request);
                    default:
                        dbManager.logSyncOperation(clientId, action, "unknown_action", "error");
                        return ServerResponse.error("Ação não reconhecida: " + action);
                }
            } catch (Exception e) {
                dbManager.logSyncOperation(clientId, action, e.getMessage(), "error");
                return ServerResponse.error("Erro ao processar " + action + ": " + e.getMessage());
            }
        }
        
        private ServerResponse processarComandoSimples(String comando) {
            // Para compatibilidade com comandos simples
            return ServerResponse.success("Conexão estabelecida", 
                "Echo: " + comando + " | Server: Finanza v2.0 | Time: " + System.currentTimeMillis());
        }
        
        private ServerResponse processarPing(SyncRequest request) {
            dbManager.logSyncOperation(request.getClientId(), "ping", "ping", "success");
            return ServerResponse.success("Pong", 
                java.util.Map.of(
                    "serverTime", System.currentTimeMillis(),
                    "serverVersion", "2.0",
                    "clientIp", clientIp
                ));
        }
        
        private ServerResponse processarSyncUser(SyncRequest request) {
            dbManager.logSyncOperation(request.getClientId(), "sync_user", 
                gson.toJson(request.getData()), "success");
            
            // TODO: Implementar lógica real de sincronização de usuário
            return ServerResponse.success("Usuário sincronizado", 
                java.util.Map.of(
                    "syncedAt", System.currentTimeMillis(),
                    "operation", "sync_user"
                ));
        }
        
        private ServerResponse processarSyncAccounts(SyncRequest request) {
            dbManager.logSyncOperation(request.getClientId(), "sync_accounts", 
                gson.toJson(request.getData()), "success");
            
            // TODO: Implementar lógica real de sincronização de contas
            return ServerResponse.success("Contas sincronizadas",
                java.util.Map.of(
                    "syncedAt", System.currentTimeMillis(),
                    "operation", "sync_accounts"
                ));
        }
        
        private ServerResponse processarSyncTransactions(SyncRequest request) {
            dbManager.logSyncOperation(request.getClientId(), "sync_transactions", 
                gson.toJson(request.getData()), "success");
            
            // TODO: Implementar lógica real de sincronização de lançamentos
            return ServerResponse.success("Transações sincronizadas",
                java.util.Map.of(
                    "syncedAt", System.currentTimeMillis(),
                    "operation", "sync_transactions"
                ));
        }
        
        private ServerResponse processarSyncCategories(SyncRequest request) {
            dbManager.logSyncOperation(request.getClientId(), "sync_categories", 
                gson.toJson(request.getData()), "success");
            
            // TODO: Implementar lógica real de sincronização de categorias
            return ServerResponse.success("Categorias sincronizadas",
                java.util.Map.of(
                    "syncedAt", System.currentTimeMillis(),
                    "operation", "sync_categories"
                ));
        }
        
        private ServerResponse processarServerInfo(SyncRequest request) {
            dbManager.logSyncOperation(request.getClientId(), "get_server_info", "", "success");
            
            return ServerResponse.success("Informações do servidor",
                java.util.Map.of(
                    "serverVersion", "2.0",
                    "supportedActions", java.util.Arrays.asList(
                        "ping", "sync_user", "sync_accounts", 
                        "sync_transactions", "sync_categories", "get_server_info"
                    ),
                    "maxConnections", 10,
                    "uptime", System.currentTimeMillis() - startTime
                ));
        }
    }
    
    private static long startTime;
    
    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        FinanzaServer server = new FinanzaServer();
        
        // Adicionar shutdown hook para fechar servidor graciosamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("\\n🔴 Fechando servidor...");
                server.parar();
                System.out.println("✅ Servidor fechado com sucesso");
            } catch (IOException e) {
                System.err.println("❌ Erro ao fechar servidor: " + e.getMessage());
            }
        }));
        
        try {
            server.iniciar();
        } catch (IOException e) {
            System.err.println("❌ Erro ao iniciar servidor: " + e.getMessage());
        }
    }
}