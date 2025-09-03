package com.finanza.server;

import com.finanza.server.data.DataStore;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

/**
 * Servidor Java para receber dados do aplicativo Finanza
 * Implementa threading para múltiplas conexões simultâneas
 */
public class FinanzaServer {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private boolean running = false;
    
    public FinanzaServer() {
        executor = Executors.newFixedThreadPool(10);
    }
    
    public void iniciar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        running = true;
        
        System.out.println("=== Servidor Finanza ===");
        System.out.println("Servidor iniciado na porta " + PORT);
        System.out.println("Aguardando conexões...");
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Erro ao aceitar conexão: " + e.getMessage());
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
    }
    
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                String inputLine = in.readLine();
                System.out.println("Recebido de " + clientSocket.getInetAddress() + ": " + inputLine);
                
                // Processar comando recebido
                String response = processarComando(inputLine);
                out.println(response);
                
                System.out.println("Resposta enviada: " + response);
                
            } catch (IOException e) {
                System.err.println("Erro ao processar cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
        
        private String processarComando(String comando) {
            if (comando == null || comando.trim().isEmpty()) {
                return "{\"status\":\"error\",\"message\":\"Comando vazio\"}";
            }
            
            try {
                // Verificar se é JSON
                if (comando.startsWith("{") && comando.endsWith("}")) {
                    if (comando.contains("\"action\":\"sync_user\"")) {
                        return processarSyncUser(comando);
                    } else if (comando.contains("\"action\":\"sync_accounts\"")) {
                        return processarSyncAccounts(comando);
                    } else if (comando.contains("\"action\":\"sync_transactions\"")) {
                        return processarSyncTransactions(comando);
                    } else {
                        return "{\"status\":\"error\",\"message\":\"Ação não reconhecida\"}";
                    }
                } else {
                    return "{\"status\":\"success\",\"message\":\"Conexão estabelecida\",\"echo\":\"" + comando + "\"}";
                }
            } catch (Exception e) {
                return "{\"status\":\"error\",\"message\":\"Erro ao processar: " + e.getMessage() + "\"}";
            }
        }
        
        private String processarSyncUser(String comando) {
            try {
                DataStore dataStore = DataStore.getInstance();
                
                // Extrair userId do comando JSON (simulação simples)
                String userId = extractValue(comando, "userId");
                if (userId != null) {
                    // Salvar dados do usuário
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String userData = "{\"userId\":\"" + userId + "\",\"lastSync\":\"" + timestamp + "\"}";
                    dataStore.saveUser(userId, userData);
                    
                    System.out.println("Usuário sincronizado: " + userId);
                    return "{\"status\":\"success\",\"message\":\"Usuário " + userId + " sincronizado\",\"timestamp\":\"" + timestamp + "\"}";
                } else {
                    return "{\"status\":\"error\",\"message\":\"userId não encontrado no comando\"}";
                }
            } catch (Exception e) {
                return "{\"status\":\"error\",\"message\":\"Erro ao sincronizar usuário: " + e.getMessage() + "\"}";
            }
        }
        
        private String processarSyncAccounts(String comando) {
            try {
                DataStore dataStore = DataStore.getInstance();
                
                String userId = extractValue(comando, "userId");
                if (userId != null) {
                    // Buscar contas existentes do usuário
                    Map<String, Object> userAccounts = dataStore.getAccountsByUser(userId);
                    
                    // Salvar timestamp de sincronização
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String accountId = "acc_" + userId + "_" + timestamp;
                    String accountData = "{\"accountId\":\"" + accountId + "\",\"userId\":\"" + userId + "\",\"lastSync\":\"" + timestamp + "\"}";
                    dataStore.saveAccount(accountId, accountData);
                    
                    System.out.println("Contas sincronizadas para usuário: " + userId + " (" + userAccounts.size() + " contas existentes)");
                    return "{\"status\":\"success\",\"message\":\"Contas sincronizadas\",\"userId\":\"" + userId + "\",\"accountCount\":" + userAccounts.size() + ",\"timestamp\":\"" + timestamp + "\"}";
                } else {
                    return "{\"status\":\"error\",\"message\":\"userId não encontrado no comando\"}";
                }
            } catch (Exception e) {
                return "{\"status\":\"error\",\"message\":\"Erro ao sincronizar contas: " + e.getMessage() + "\"}";
            }
        }
        
        private String processarSyncTransactions(String comando) {
            try {
                DataStore dataStore = DataStore.getInstance();
                
                String userId = extractValue(comando, "userId");
                if (userId != null) {
                    // Buscar transações existentes do usuário
                    Map<String, Object> userTransactions = dataStore.getTransactionsByUser(userId);
                    
                    // Salvar timestamp de sincronização
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String transactionId = "txn_" + userId + "_" + timestamp;
                    String transactionData = "{\"transactionId\":\"" + transactionId + "\",\"userId\":\"" + userId + "\",\"lastSync\":\"" + timestamp + "\"}";
                    dataStore.saveTransaction(transactionId, transactionData);
                    
                    System.out.println("Transações sincronizadas para usuário: " + userId + " (" + userTransactions.size() + " transações existentes)");
                    return "{\"status\":\"success\",\"message\":\"Transações sincronizadas\",\"userId\":\"" + userId + "\",\"transactionCount\":" + userTransactions.size() + ",\"timestamp\":\"" + timestamp + "\"}";
                } else {
                    return "{\"status\":\"error\",\"message\":\"userId não encontrado no comando\"}";
                }
            } catch (Exception e) {
                return "{\"status\":\"error\",\"message\":\"Erro ao sincronizar transações: " + e.getMessage() + "\"}";
            }
        }
        
        /**
         * Extrai um valor de um JSON simples (implementação básica)
         */
        private String extractValue(String json, String key) {
            String searchPattern = "\"" + key + "\":";
            int startIndex = json.indexOf(searchPattern);
            if (startIndex == -1) return null;
            
            startIndex += searchPattern.length();
            
            // Pular espaços e encontrar o início do valor
            while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
                startIndex++;
            }
            
            if (startIndex >= json.length()) return null;
            
            // Se o valor está entre aspas
            if (json.charAt(startIndex) == '"') {
                startIndex++; // pular a primeira aspa
                int endIndex = json.indexOf('"', startIndex);
                if (endIndex == -1) return null;
                return json.substring(startIndex, endIndex);
            } else {
                // Valor numérico ou booleano
                int endIndex = startIndex;
                while (endIndex < json.length() && 
                       json.charAt(endIndex) != ',' && 
                       json.charAt(endIndex) != '}' && 
                       !Character.isWhitespace(json.charAt(endIndex))) {
                    endIndex++;
                }
                return json.substring(startIndex, endIndex);
            }
        }
    }
    
    public static void main(String[] args) {
        FinanzaServer server = new FinanzaServer();
        
        // Adicionar shutdown hook para fechar servidor graciosamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("\nFechando servidor...");
                server.parar();
            } catch (IOException e) {
                System.err.println("Erro ao fechar servidor: " + e.getMessage());
            }
        }));
        
        try {
            server.iniciar();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }
}