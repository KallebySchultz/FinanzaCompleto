package ui;

import database.DatabaseManager;
import controller.UsuarioController;
import javax.swing.*;
import java.awt.*;

/**
 * FinanzaDesktop - Main class for the desktop application
 * Entry point for the Finanza Desktop Financial Management System
 */
public class FinanzaDesktop {
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, use the default look and feel
            System.err.println("Couldn't set Nimbus look and feel: " + e.getMessage());
        }
        
        // Test database connection
        if (!DatabaseManager.testConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao conectar com o banco de dados!\nVerifique se o SQLite está disponível.",
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        System.out.println("Finanza Desktop Application iniciado com sucesso!");
        System.out.println("Banco de dados conectado e inicializado.");
        
        // Initialize controllers
        UsuarioController usuarioController = new UsuarioController();
        
        // For now, just show a simple message
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, 
                "Finanza Desktop Application\n" +
                "Sistema de Gestão Financeira\n\n" +
                "Banco de dados inicializado com sucesso!\n" +
                "Controllers criados e prontos para uso.\n\n" +
                "Próximo passo: Conectar às Views existentes.",
                "Finanza Desktop", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Keep application running for now
        // Later this will be replaced with the main window
        System.out.println("Aplicação pronta. Pressione Ctrl+C para sair.");
    }
}