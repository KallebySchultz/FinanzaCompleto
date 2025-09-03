package server;

import handler.ClientHandler;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/**
 * FinanzaServer - TCP Server for Finanza Financial Management System
 * Handles synchronization between Android and Desktop clients
 */
public class FinanzaServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_CLIENTS = 50;
    
    private ServerSocket serverSocket;
    private ExecutorService clientThreadPool;
    private boolean isRunning = false;
    private final List<ClientHandler> activeClients = new ArrayList<>();
    
    public FinanzaServer() {
        this.clientThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
    }
    
    /**
     * Start the server on default port
     */
    public void start() throws IOException {
        start(DEFAULT_PORT);
    }
    
    /**
     * Start the server on specified port
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;
        
        System.out.println("=================================================");
        System.out.println("Finanza Server iniciado na porta " + port);
        System.out.println("Aguardando conexões de clientes...");
        System.out.println("=================================================");
        
        // Accept client connections
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                // Create client handler
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                
                // Add to active clients list
                synchronized (activeClients) {
                    activeClients.add(clientHandler);
                }
                
                // Handle client in separate thread
                clientThreadPool.execute(clientHandler);
                
                System.out.println("Nova conexão aceita: " + clientSocket.getInetAddress());
                System.out.println("Clientes ativos: " + activeClients.size());
                
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Stop the server
     */
    public void stop() throws IOException {
        isRunning = false;
        
        // Close all client connections
        synchronized (activeClients) {
            for (ClientHandler client : activeClients) {
                client.close();
            }
            activeClients.clear();
        }
        
        // Shutdown thread pool
        clientThreadPool.shutdown();
        try {
            if (!clientThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                clientThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            clientThreadPool.shutdownNow();
        }
        
        // Close server socket
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        
        System.out.println("Servidor parado com sucesso.");
    }
    
    /**
     * Remove client from active list
     */
    public void removeClient(ClientHandler client) {
        synchronized (activeClients) {
            activeClients.remove(client);
        }
        System.out.println("Cliente desconectado. Clientes ativos: " + activeClients.size());
    }
    
    /**
     * Get number of active clients
     */
    public int getActiveClientsCount() {
        synchronized (activeClients) {
            return activeClients.size();
        }
    }
    
    /**
     * Check if server is running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Main method to start server
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        // Parse command line arguments
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida: " + args[0]);
                System.err.println("Usando porta padrão: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }
        
        FinanzaServer server = new FinanzaServer();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("\nParando servidor...");
                server.stop();
            } catch (IOException e) {
                System.err.println("Erro ao parar servidor: " + e.getMessage());
            }
        }));
        
        // Start server
        try {
            server.start(port);
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            System.exit(1);
        }
    }
}