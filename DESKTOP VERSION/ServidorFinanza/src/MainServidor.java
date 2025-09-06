import server.FinanzaServer;

/**
 * Classe principal para iniciar o servidor Finanza
 */
public class MainServidor {
    
    public static void main(String[] args) {
        System.out.println("=== Finanza Desktop - Servidor ===");
        System.out.println("Inicializando servidor...");
        
        // Verificar se é modo de teste
        boolean testMode = args.length > 0 && "--test".equals(args[0]);
        FinanzaServer server = new FinanzaServer(testMode);
        
        // Adiciona shutdown hook para encerrar servidor graciosamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nEncerrando servidor...");
            server.stop();
        }));
        
        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Erro fatal no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}