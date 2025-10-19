package server;

import util.DatabaseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe principal do servidor Finanza
 */
public class FinanzaServer {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private boolean running;
    private boolean testMode;
    
    public FinanzaServer() {
        this(false);
    }
    
    public FinanzaServer(boolean testMode) {
        this.testMode = testMode;
    }
    
    public void start() {
        try {
            // Testa conexão com banco de dados apenas se não estiver em modo de teste
            if (!testMode) {
                if (!DatabaseUtil.testConnection()) {
                    System.err.println("Erro: Não foi possível conectar ao banco de dados");
                    System.err.println("Verifique se o MySQL está rodando e se o banco 'finanza_db' existe");
                    return;
                }
                System.out.println("Conexão com banco de dados OK");
                
                // Inicializa as tabelas do banco de dados
                DatabaseUtil.initializeDatabase();
            } else {
                System.out.println("Servidor iniciado em MODO DE TESTE (sem banco de dados)");
            }
            
            serverSocket = new ServerSocket(PORT);
            running = true;
            
            System.out.println("Servidor Finanza iniciado na porta " + PORT);
            System.out.println("Aguardando conexões de clientes...");
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, testMode);
                    clientHandler.start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor encerrado");
            }
        } catch (IOException e) {
            System.err.println("Erro ao encerrar servidor: " + e.getMessage());
        }
    }
}