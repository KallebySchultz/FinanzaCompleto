package com.finanza.desktop.util;

import javax.swing.*;
import java.awt.*;

/**
 * Utilitários para interface gráfica
 */
public class UIUtils {
    // Cores do tema Finanza
    public static final Color PRIMARY_BLUE = new Color(33, 150, 243);
    public static final Color PRIMARY_DARK_BLUE = new Color(25, 118, 210);
    public static final Color ACCENT_COLOR = new Color(76, 175, 80);
    public static final Color ERROR_COLOR = new Color(244, 67, 54);
    public static final Color WARNING_COLOR = new Color(255, 152, 0);
    public static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    public static void configureButton(JButton button) {
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK_BLUE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_BLUE);
            }
        });
    }

    public static void configureSecondaryButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_BLUE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void configureTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
    }

    public static void configurePasswordField(JPasswordField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean showConfirmation(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirmação", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void centerWindow(Window window) {
        window.setLocationRelativeTo(null);
    }
}