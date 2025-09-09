import util.NetworkClient;

/**
 * Test specifically for LIST_MOVIMENTACOES functionality
 */
public class TestListMovimentacoes {
    public static void main(String[] args) {
        System.out.println("=== Test LIST_MOVIMENTACOES - Finanza ===");
        
        NetworkClient client = new NetworkClient();
        
        // Test connection
        System.out.println("Conectando ao servidor...");
        if (client.connect()) {
            System.out.println("✓ Conectado com sucesso!");
            
            // Test login first
            System.out.println("\nTestando login...");
            String loginResponse = client.sendCommand("LOGIN|test@email.com|senha123");
            System.out.println("Resposta do login: " + loginResponse);
            
            if (loginResponse.startsWith("OK")) {
                // Test LIST_MOVIMENTACOES
                System.out.println("\nTestando LIST_MOVIMENTACOES...");
                String movResponse = client.sendCommand("LIST_MOVIMENTACOES");
                System.out.println("Resposta das movimentações: " + movResponse);
                
                // Verify response format
                if (movResponse.startsWith("OK")) {
                    String data = movResponse.substring(3); // Remove "OK|"
                    System.out.println("\nDados das movimentações:");
                    System.out.println("Raw data: " + data);
                    
                    if (!data.isEmpty()) {
                        String[] movimentacoes = data.split(";");
                        System.out.println("Número de movimentações: " + movimentacoes.length);
                        
                        for (int i = 0; i < movimentacoes.length; i++) {
                            System.out.println("Movimentação " + (i+1) + ": " + movimentacoes[i]);
                            String[] campos = movimentacoes[i].split(",");
                            if (campos.length >= 8) {
                                System.out.println("  ID: " + campos[0]);
                                System.out.println("  Valor: " + campos[1] + "." + campos[2]);
                                System.out.println("  Data: " + campos[3]);
                                System.out.println("  Descrição: " + campos[4]);
                                System.out.println("  Tipo: " + campos[5]);
                                System.out.println("  ID Conta: " + campos[6]);
                                System.out.println("  ID Categoria: " + campos[7]);
                            }
                        }
                    } else {
                        System.out.println("Nenhuma movimentação encontrada.");
                    }
                    
                    System.out.println("\n✓ Teste LIST_MOVIMENTACOES executado com sucesso!");
                } else {
                    System.out.println("✗ Erro na resposta: " + movResponse);
                }
            } else {
                System.out.println("✗ Falha no login");
            }
            
            client.disconnect();
            System.out.println("\n✓ Teste concluído!");
        } else {
            System.out.println("✗ Falha na conexão!");
        }
    }
}