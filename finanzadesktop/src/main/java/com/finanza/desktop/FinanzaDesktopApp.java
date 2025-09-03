package com.finanza.desktop;

import com.finanza.desktop.ui.LoginFrame;
import com.finanza.desktop.database.DatabaseManager;
import javax.swing.*;

/**
 * Classe principal da aplicação desktop Finanza
 */
public class FinanzaDesktopApp {
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            // Usar Nimbus se disponível, senão usar padrão
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
        }

        // Configurar tratamento de exceções não capturadas
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Erro não tratado: " + exception.getMessage());
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Ocorreu um erro inesperado: " + exception.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        });

        // Inicializar banco de dados
        try {
            DatabaseManager.getInstance();
            System.out.println("Banco de dados inicializado com sucesso");
        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Erro ao conectar com o banco de dados. O aplicativo será fechado.",
                "Erro Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Executar interface gráfica na thread de eventos
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                System.err.println("Erro ao iniciar aplicação: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Adicionar shutdown hook para fechar banco graciosamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Fechando aplicação...");
            DatabaseManager.getInstance().close();
        }));
    }
}