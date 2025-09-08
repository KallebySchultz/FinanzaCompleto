package util;

import java.awt.Color;

/**
 * Define o esquema de cores oficial do Finanza seguindo o padrão do app Android
 * Mantém consistência visual entre as versões mobile e desktop
 */
public class ColorScheme {
    
    // Cores principais do brand (baseadas no app Android)
    public static final Color PRIMARY_DARK_BLUE = new Color(0x1B2A57);  // #1B2A57
    public static final Color ACCENT_BLUE = new Color(0x4A7CF5);        // #4A7CF5
    public static final Color POSITIVE_GREEN = new Color(0x21C87A);     // #21C87A
    public static final Color NEGATIVE_RED = new Color(0xE53935);       // #E53935
    
    // Cores auxiliares
    public static final Color WHITE = Color.WHITE;                      // #FFFFFF
    public static final Color GRAY = new Color(0xF5F5F5);              // #F5F5F5
    public static final Color DARK_GRAY = new Color(0x666666);          // #666666
    public static final Color LIGHT_GRAY = new Color(0xE0E0E0);         // #E0E0E0
    
    // Cores derivadas para diferentes elementos
    public static final Color BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = Color.BLACK;
    public static final Color TEXT_SECONDARY = DARK_GRAY;
    public static final Color BORDER = LIGHT_GRAY;
    public static final Color CARD_BACKGROUND = Color.WHITE;
    
    // Cores para diferentes tipos de dados financeiros
    public static final Color INCOME_COLOR = POSITIVE_GREEN;
    public static final Color EXPENSE_COLOR = NEGATIVE_RED;
    public static final Color BALANCE_POSITIVE = POSITIVE_GREEN;
    public static final Color BALANCE_NEGATIVE = NEGATIVE_RED;
    public static final Color NEUTRAL_COLOR = ACCENT_BLUE;
    public static final Color WARNING_COLOR = new Color(0xFF9800);      // Orange
    
    /**
     * Retorna cor baseada no valor financeiro (positivo = verde, negativo = vermelho)
     */
    public static Color getBalanceColor(double value) {
        return value >= 0 ? BALANCE_POSITIVE : BALANCE_NEGATIVE;
    }
    
    /**
     * Retorna cor mais clara (com transparência) da cor base
     */
    public static Color getLighterColor(Color baseColor, int alpha) {
        return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
    }
    
    /**
     * Retorna cor mais escura da cor base
     */
    public static Color getDarkerColor(Color baseColor) {
        return baseColor.darker();
    }
}