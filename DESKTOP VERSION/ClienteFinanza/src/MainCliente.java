import view.LoginView;

import javax.swing.*;

/**
 * Classe principal para iniciar o cliente Finanza
 */
public class MainCliente {
    
    public static void main(String[] args) {
        System.out.println("=== Finanza Desktop - Cliente ===");
        
        // Configurar Look and Feel para melhor aparência nativa
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
            // Continua com Look and Feel padrão em caso de erro
        }
        
        // Inicializar interface gráfica na thread da GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Erro ao inicializar interface: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                        null,
                        "Erro ao inicializar aplicação: " + e.getMessage(),
                        "Erro Fatal",
                        JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                }
            }
        });
    }
}