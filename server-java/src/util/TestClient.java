package util;

import java.io.*;
import java.net.*;

/**
 * TestClient - Simple test client to validate server communication
 * Simulates Android/Desktop client communication with server
 */
public class TestClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Finanza Test Client");
        System.out.println("=================================================");
        
        TestClient client = new TestClient();
        
        // Test basic connectivity
        client.testConnection();
        
        // Test various commands
        client.testPing();
        client.testLogin();
        client.testSyncUser();
        client.testSyncAccounts();
        client.testSyncTransactions();
        
        System.out.println("=================================================");
        System.out.println("Testes concluídos!");
        System.out.println("=================================================");
    }
    
    /**
     * Test basic connection
     */
    public void testConnection() {
        System.out.println("\n--- Teste de Conexão ---");
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("✅ Conexão estabelecida com o servidor");
        } catch (IOException e) {
            System.out.println("❌ Erro ao conectar: " + e.getMessage());
        }
    }
    
    /**
     * Test ping command
     */
    public void testPing() {
        System.out.println("\n--- Teste Ping ---");
        String response = sendCommand("{\"action\":\"ping\"}");
        System.out.println("Resposta: " + response);
    }
    
    /**
     * Test login command
     */
    public void testLogin() {
        System.out.println("\n--- Teste Login ---");
        String command = "{\"action\":\"login\",\"email\":\"admin@finanza.com\",\"senha\":\"admin\"}";
        String response = sendCommand(command);
        System.out.println("Resposta: " + response);
    }
    
    /**
     * Test user sync command
     */
    public void testSyncUser() {
        System.out.println("\n--- Teste Sincronização Usuário ---");
        String command = "{\"action\":\"sync_user\",\"userId\":1}";
        String response = sendCommand(command);
        System.out.println("Resposta: " + response);
    }
    
    /**
     * Test accounts sync command
     */
    public void testSyncAccounts() {
        System.out.println("\n--- Teste Sincronização Contas ---");
        String command = "{\"action\":\"sync_accounts\",\"userId\":1}";
        String response = sendCommand(command);
        System.out.println("Resposta: " + response);
    }
    
    /**
     * Test transactions sync command
     */
    public void testSyncTransactions() {
        System.out.println("\n--- Teste Sincronização Lançamentos ---");
        String command = "{\"action\":\"sync_transactions\",\"userId\":1}";
        String response = sendCommand(command);
        System.out.println("Resposta: " + response);
    }
    
    /**
     * Send command to server and get response
     */
    private String sendCommand(String command) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Skip welcome message
            in.readLine();
            
            // Send command
            out.println(command);
            
            // Read response
            return in.readLine();
            
        } catch (IOException e) {
            return "Erro: " + e.getMessage();
        }
    }
}