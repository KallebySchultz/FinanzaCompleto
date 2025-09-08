import util.NetworkClient;

/**
 * Test client to verify server communication
 */
public class TestClient {
    public static void main(String[] args) {
        System.out.println("=== Test Client - Finanza ===");
        
        NetworkClient client = new NetworkClient();
        
        // Test connection
        System.out.println("Conectando ao servidor...");
        if (client.connect()) {
            System.out.println("✓ Conectado com sucesso!");
            
            // Test login
            System.out.println("\nTestando login...");
            String response = client.sendCommand("LOGIN|test@email.com|senha123");
            System.out.println("Resposta do login: " + response);
            
            // Test password recovery
            System.out.println("\nTestando recuperação de senha...");
            String recoveryResponse = client.sendCommand("RESET_PASSWORD|test@email.com");
            System.out.println("Resposta da recuperação: " + recoveryResponse);
            
            // Test registration
            System.out.println("\nTestando registro...");
            String registerResponse = client.sendCommand("REGISTER|Test User|test2@email.com|senha123");
            System.out.println("Resposta do registro: " + registerResponse);
            
            client.disconnect();
            System.out.println("\n✓ Testes concluídos!");
        } else {
            System.out.println("✗ Falha na conexão!");
        }
    }
}