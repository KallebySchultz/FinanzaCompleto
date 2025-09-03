package com.finanza.desktop.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Utilitários para criação de componentes UI modernos
 * Fornece métodos para criar botões, painéis e componentes estilizados
 */
public class ModernUIHelper {
    
    // Cores do tema (baseadas no app móvel)
    public static final Color PRIMARY_DARK_BLUE = new Color(27, 42, 87);    // #1B2A57
    public static final Color ACCENT_BLUE = new Color(74, 124, 245);        // #4A7CF5
    public static final Color POSITIVE_GREEN = new Color(33, 200, 122);     // #21C87A
    public static final Color NEGATIVE_RED = new Color(229, 57, 53);        // #E53935
    public static final Color WHITE = Color.WHITE;
    public static final Color GRAY = new Color(245, 245, 245);              // #F5F5F5
    public static final Color DARK_GRAY = new Color(102, 102, 102);         // #666666
    public static final Color LIGHT_GRAY = new Color(224, 224, 224);        // #E0E0E0
    
    // Ícones usando Unicode
    public static final String ICON_HOME = "🏠";
    public static final String ICON_ACCOUNTS = "💳";
    public static final String ICON_MOVEMENTS = "📊";
    public static final String ICON_PROFILE = "👤";
    public static final String ICON_SETTINGS = "⚙️";
    public static final String ICON_LOGOUT = "🚪";
    public static final String ICON_CONNECT = "🔗";
    public static final String ICON_DISCONNECT = "❌";
    public static final String ICON_SUCCESS = "✅";
    public static final String ICON_ERROR = "❌";
    public static final String ICON_MONEY = "💰";
    public static final String ICON_UP = "⬆️";
    public static final String ICON_DOWN = "⬇️";
    
    /**
     * Cria um botão moderno com hover effect
     */
    public static JButton createModernButton(String text, Color backgroundColor) {
        return createModernButton(text, null, backgroundColor);
    }
    
    public static JButton createModernButton(String text, String icon, Color backgroundColor) {
        JButton button = new JButton();
        
        // Configurar texto e ícone
        if (icon != null && !icon.isEmpty()) {
            button.setText(icon + " " + text);
        } else {
            button.setText(text);
        }
        
        // Estilo básico
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(new RoundedBorder(8));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Adicionar efeitos hover
        addHoverEffect(button, backgroundColor);
        
        return button;
    }
    
    /**
     * Cria um botão de navegação
     */
    public static JButton createNavButton(String text, String icon) {
        JButton button = createModernButton(text, icon, PRIMARY_DARK_BLUE);
        button.setPreferredSize(new Dimension(140, 45));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }
    
    /**
     * Cria um painel com bordas arredondadas e sombra
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(new ShadowBorder());
        return panel;
    }
    
    /**
     * Cria um campo de texto moderno
     */
    public static JTextField createModernTextField(String placeholder, int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Adicionar placeholder effect
        if (placeholder != null && !placeholder.isEmpty()) {
            addPlaceholderEffect(field, placeholder);
        }
        
        return field;
    }
    
    /**
     * Cria um campo de senha moderno
     */
    public static JPasswordField createModernPasswordField(String placeholder, int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        return field;
    }
    
    /**
     * Cria um label de status com ícone
     */
    public static JLabel createStatusLabel(String text, String icon, Color color) {
        JLabel label = new JLabel(icon + " " + text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(color);
        return label;
    }
    
    /**
     * Adiciona efeito hover a um botão
     */
    private static void addHoverEffect(JButton button, Color originalColor) {
        Color hoverColor = darker(originalColor, 0.1f);
        Color pressedColor = darker(originalColor, 0.2f);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });
    }
    
    /**
     * Adiciona efeito placeholder a um campo de texto
     */
    private static void addPlaceholderEffect(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(DARK_GRAY);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(DARK_GRAY);
                }
            }
        });
    }
    
    /**
     * Escurece uma cor
     */
    private static Color darker(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * (1 - factor)), 0),
            Math.max((int)(color.getGreen() * (1 - factor)), 0),
            Math.max((int)(color.getBlue() * (1 - factor)), 0),
            color.getAlpha()
        );
    }
    
    /**
     * Borda arredondada personalizada
     */
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        
        public RoundedBorder(int radius) {
            this(radius, LIGHT_GRAY);
        }
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
    }
    
    /**
     * Borda com sombra personalizada
     */
    public static class ShadowBorder extends AbstractBorder {
        private final int shadowSize = 4;
        private final Color shadowColor = new Color(0, 0, 0, 50);
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Desenhar sombra
            g2.setColor(shadowColor);
            g2.fillRoundRect(x + shadowSize, y + shadowSize, 
                width - shadowSize, height - shadowSize, 10, 10);
            
            // Desenhar borda principal
            g2.setColor(LIGHT_GRAY);
            g2.drawRoundRect(x, y, width - shadowSize - 1, height - shadowSize - 1, 10, 10);
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, shadowSize + 2, shadowSize + 2);
        }
    }
}