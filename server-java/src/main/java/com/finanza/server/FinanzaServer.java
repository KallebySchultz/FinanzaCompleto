package com.finanza.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            // Implementar lógica de sincronização de usuário
            return "{\"status\":\"success\",\"message\":\"Usuário sincronizado\"}";
        }
        
        private String processarSyncAccounts(String comando) {
            // Implementar lógica de sincronização de contas
            return "{\"status\":\"success\",\"message\":\"Contas sincronizadas\"}";
        }
        
        private String processarSyncTransactions(String comando) {
            // Implementar lógica de sincronização de lançamentos
            return "{\"status\":\"success\",\"message\":\"Lançamentos sincronizados\"}";
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