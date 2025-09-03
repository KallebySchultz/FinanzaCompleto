package com.finanza.desktop.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Carregador e cache de ícones do projeto
 * Utiliza os ícones da pasta Icons para criar uma interface moderna
 */
public class IconLoader {
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    private static final String ICONS_PATH = "Icons/";
    
    // Mapeamento de ícones disponíveis
    public static final String HOME = "ic_home.png";
    public static final String MONEY = "ic_money.png";
    public static final String BANK = "ic_bank.png";
    public static final String MENU = "ic_menu.png";
    public static final String ARROW_UP = "ic_arrow_up.png";
    public static final String ARROW_DOWN = "ic_arrow_down.png";
    public static final String ARROWS = "ic_arrows.png";
    public static final String STONKS = "ic_arrow_stonks.png";
    public static final String NSTONKS = "ic_arrow_nstonks.png";
    public static final String NUBANK = "ic_nubank.png";
    public static final String ADD = "ic_add.png";
    public static final String CLOSE = "ic_close.png";
    public static final String EYE_OPEN = "ic_eye_open.png";
    public static final String EYE_CLOSED = "ic_eye_closed.png";
    public static final String LOGO = "logo.png";
    
    /**
     * Carrega um ícone com tamanho específico
     */
    public static ImageIcon loadIcon(String iconName, int width, int height) {
        String cacheKey = iconName + "_" + width + "x" + height;
        
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }
        
        try {
            File iconFile = new File(ICONS_PATH + iconName);
            if (!iconFile.exists()) {
                System.err.println("Ícone não encontrado: " + iconFile.getAbsolutePath());
                return createFallbackIcon(width, height);
            }
            
            BufferedImage originalImage = ImageIO.read(iconFile);
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            
            iconCache.put(cacheKey, icon);
            return icon;
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar ícone " + iconName + ": " + e.getMessage());
            return createFallbackIcon(width, height);
        }
    }
    
    /**
     * Carrega um ícone com tamanho padrão (24x24)
     */
    public static ImageIcon loadIcon(String iconName) {
        return loadIcon(iconName, 24, 24);
    }
    
    /**
     * Carrega um ícone grande (48x48)
     */
    public static ImageIcon loadLargeIcon(String iconName) {
        return loadIcon(iconName, 48, 48);
    }
    
    /**
     * Carrega um ícone pequeno (16x16)
     */
    public static ImageIcon loadSmallIcon(String iconName) {
        return loadIcon(iconName, 16, 16);
    }
    
    /**
     * Cria um ícone de fallback quando não consegue carregar o original
     */
    private static ImageIcon createFallbackIcon(int width, int height) {
        BufferedImage fallbackImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallbackImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ModernUIHelper.ACCENT_BLUE);
        g2d.fillOval(2, 2, width - 4, height - 4);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, Math.max(8, width / 3)));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "?";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return new ImageIcon(fallbackImage);
    }
    
    /**
     * Cria um ícone colorido baseado em uma cor
     */
    public static ImageIcon createColoredIcon(Color color, int width, int height) {
        BufferedImage coloredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = coloredImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillOval(2, 2, width - 4, height - 4);
        
        g2d.dispose();
        return new ImageIcon(coloredImage);
    }
    
    /**
     * Limpa o cache de ícones
     */
    public static void clearCache() {
        iconCache.clear();
    }
}