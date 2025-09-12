package view.components;

import javax.swing.*;
import java.awt.*;

/**
 * Panel responsivo que se adapta ao tamanho da tela
 * Implementa padrões de design consistentes
 */
public class ResponsivePanel extends JPanel {
    
    public enum PanelType {
        MAIN_CONTENT,
        SIDEBAR,
        HEADER,
        FOOTER,
        CARD
    }
    
    private PanelType panelType;
    private static final Color PRIMARY_COLOR = new Color(102, 126, 234);
    private static final Color SECONDARY_COLOR = new Color(118, 75, 162);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_COLOR = Color.WHITE;
    
    public ResponsivePanel(PanelType type) {
        this.panelType = type;
        setupPanel();
    }
    
    public ResponsivePanel(PanelType type, LayoutManager layout) {
        super(layout);
        this.panelType = type;
        setupPanel();
    }
    
    private void setupPanel() {
        switch (panelType) {
            case MAIN_CONTENT:
                setBackground(BACKGROUND_COLOR);
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                break;
            case SIDEBAR:
                setBackground(CARD_COLOR);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                setPreferredSize(new Dimension(300, -1));
                setMinimumSize(new Dimension(250, 0));
                break;
            case HEADER:
                setBackground(PRIMARY_COLOR);
                setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
                setPreferredSize(new Dimension(-1, 60));
                break;
            case FOOTER:
                setBackground(Color.LIGHT_GRAY);
                setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                setPreferredSize(new Dimension(-1, 40));
                break;
            case CARD:
                setBackground(CARD_COLOR);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                break;
        }
    }
    
    /**
     * Cria um título estilizado para o painel
     */
    public JLabel createStyledTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        if (panelType == PanelType.HEADER) {
            title.setForeground(Color.WHITE);
        } else {
            title.setForeground(new Color(51, 51, 51));
        }
        
        return title;
    }
    
    /**
     * Cria um botão estilizado
     */
    public static JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        
        if (isPrimary) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(51, 51, 51));
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        }
        
        return button;
    }
    
    /**
     * Aplica responsividade baseada no tamanho da janela
     */
    public void applyResponsiveBehavior(Container parent) {
        parent.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustToParentSize(parent);
            }
        });
    }
    
    private void adjustToParentSize(Container parent) {
        Dimension parentSize = parent.getSize();
        
        if (panelType == PanelType.SIDEBAR) {
            // Em telas pequenas, reduzir largura da sidebar
            if (parentSize.width < 800) {
                setPreferredSize(new Dimension(200, -1));
            } else {
                setPreferredSize(new Dimension(300, -1));
            }
        }
        
        revalidate();
        repaint();
    }
}