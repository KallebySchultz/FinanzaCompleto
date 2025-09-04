package ui;

import model.Categoria;
import model.Lancamento;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * ChartUtils - Utility class for creating simple charts in Swing
 */
public class ChartUtils {
    
    /**
     * Creates a simple pie chart panel for expense categories
     */
    public static JPanel createExpensePieChart(List<Lancamento> despesas, List<Categoria> categorias, int width, int height) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (despesas == null || despesas.isEmpty()) {
                    drawNoDataMessage(g, width, height, "Nenhuma despesa encontrada");
                    return;
                }
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Calculate category totals
                Map<Integer, Double> categoryTotals = new HashMap<>();
                double totalAmount = 0;
                
                for (Lancamento lancamento : despesas) {
                    if ("despesa".equals(lancamento.getTipo())) {
                        double valor = Math.abs(lancamento.getValor());
                        categoryTotals.merge(lancamento.getCategoriaId(), valor, Double::sum);
                        totalAmount += valor;
                    }
                }
                
                if (totalAmount == 0) {
                    drawNoDataMessage(g, width, height, "Nenhuma despesa encontrada");
                    return;
                }
                
                // Draw pie chart
                int centerX = width / 2;
                int centerY = height / 2;
                int radius = Math.min(width, height) / 3;
                
                double startAngle = 0;
                Color[] colors = {
                    new Color(255, 107, 107),
                    new Color(78, 205, 196),
                    new Color(69, 183, 209),
                    new Color(150, 206, 180),
                    new Color(255, 234, 167),
                    new Color(221, 160, 221),
                    new Color(152, 216, 200),
                    new Color(247, 220, 111)
                };
                
                int colorIndex = 0;
                for (Map.Entry<Integer, Double> entry : categoryTotals.entrySet()) {
                    double percentage = entry.getValue() / totalAmount;
                    double arcAngle = percentage * 360;
                    
                    g2d.setColor(colors[colorIndex % colors.length]);
                    g2d.fill(new Arc2D.Double(
                        centerX - radius, centerY - radius,
                        radius * 2, radius * 2,
                        startAngle, arcAngle, Arc2D.PIE
                    ));
                    
                    // Draw category name
                    String categoryName = getCategoryName(entry.getKey(), categorias);
                    String label = String.format("%s (%.1f%%)", categoryName, percentage * 100);
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int labelX = 10;
                    int labelY = 20 + (colorIndex * 20);
                    
                    g2d.setColor(colors[colorIndex % colors.length]);
                    g2d.fillRect(labelX, labelY - 10, 15, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(label, labelX + 20, labelY);
                    
                    startAngle += arcAngle;
                    colorIndex++;
                }
                
                // Draw title
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String title = "Distribuição de Despesas";
                int titleX = (width - fm.stringWidth(title)) / 2;
                g2d.drawString(title, titleX, 20);
            }
        };
    }
    
    /**
     * Creates a simple bar chart for income vs expenses
     */
    public static JPanel createIncomeExpenseBarChart(double receitas, double despesas, int width, int height) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Chart dimensions
                int margin = 40;
                int chartWidth = width - (2 * margin);
                int chartHeight = height - (2 * margin) - 30; // Space for title
                
                double maxValue = Math.max(receitas, Math.abs(despesas));
                if (maxValue == 0) {
                    drawNoDataMessage(g, width, height, "Nenhum dado financeiro");
                    return;
                }
                
                // Calculate bar heights
                int receitasHeight = (int) ((receitas / maxValue) * chartHeight);
                int despesasHeight = (int) ((Math.abs(despesas) / maxValue) * chartHeight);
                
                int barWidth = chartWidth / 3;
                int receitasX = margin;
                int despesasX = margin + barWidth + 20;
                
                // Draw bars
                g2d.setColor(new Color(46, 204, 113)); // Green for income
                g2d.fillRect(receitasX, margin + 30 + (chartHeight - receitasHeight), barWidth, receitasHeight);
                
                g2d.setColor(new Color(231, 76, 60)); // Red for expenses
                g2d.fillRect(despesasX, margin + 30 + (chartHeight - despesasHeight), barWidth, despesasHeight);
                
                // Draw labels
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                FontMetrics fm = g2d.getFontMetrics();
                
                String receitasLabel = "Receitas";
                int receitasLabelX = receitasX + (barWidth - fm.stringWidth(receitasLabel)) / 2;
                g2d.drawString(receitasLabel, receitasLabelX, height - 15);
                
                String despesasLabel = "Despesas";
                int despesasLabelX = despesasX + (barWidth - fm.stringWidth(despesasLabel)) / 2;
                g2d.drawString(despesasLabel, despesasLabelX, height - 15);
                
                // Draw values on bars
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                fm = g2d.getFontMetrics();
                
                if (receitasHeight > 20) {
                    String receitasValue = String.format("R$ %.0f", receitas);
                    int valueX = receitasX + (barWidth - fm.stringWidth(receitasValue)) / 2;
                    g2d.drawString(receitasValue, valueX, margin + 30 + (chartHeight - receitasHeight) + 15);
                }
                
                if (despesasHeight > 20) {
                    String despesasValue = String.format("R$ %.0f", Math.abs(despesas));
                    int valueX = despesasX + (barWidth - fm.stringWidth(despesasValue)) / 2;
                    g2d.drawString(despesasValue, valueX, margin + 30 + (chartHeight - despesasHeight) + 15);
                }
                
                // Draw title
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                fm = g2d.getFontMetrics();
                String title = "Receitas vs Despesas";
                int titleX = (width - fm.stringWidth(title)) / 2;
                g2d.drawString(title, titleX, 20);
            }
        };
    }
    
    /**
     * Creates a trend line chart for account balances over time
     */
    public static JPanel createBalanceTrendChart(List<Double> balanceHistory, int width, int height) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (balanceHistory == null || balanceHistory.size() < 2) {
                    drawNoDataMessage(g, width, height, "Dados insuficientes para gráfico");
                    return;
                }
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Chart dimensions
                int margin = 40;
                int chartWidth = width - (2 * margin);
                int chartHeight = height - (2 * margin) - 30;
                
                double minValue = balanceHistory.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                double maxValue = balanceHistory.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                double range = maxValue - minValue;
                
                if (range == 0) {
                    range = Math.abs(maxValue) * 0.1; // 10% of the value for some range
                    if (range == 0) range = 1; // Minimum range
                }
                
                // Draw line
                g2d.setColor(new Color(52, 152, 219)); // Blue
                g2d.setStroke(new BasicStroke(2));
                
                for (int i = 0; i < balanceHistory.size() - 1; i++) {
                    double value1 = balanceHistory.get(i);
                    double value2 = balanceHistory.get(i + 1);
                    
                    int x1 = margin + (i * chartWidth) / (balanceHistory.size() - 1);
                    int y1 = margin + 30 + (int) ((maxValue - value1) / range * chartHeight);
                    
                    int x2 = margin + ((i + 1) * chartWidth) / (balanceHistory.size() - 1);
                    int y2 = margin + 30 + (int) ((maxValue - value2) / range * chartHeight);
                    
                    g2d.drawLine(x1, y1, x2, y2);
                    
                    // Draw points
                    g2d.fillOval(x1 - 3, y1 - 3, 6, 6);
                    if (i == balanceHistory.size() - 2) {
                        g2d.fillOval(x2 - 3, y2 - 3, 6, 6);
                    }
                }
                
                // Draw title
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String title = "Evolução do Saldo";
                int titleX = (width - fm.stringWidth(title)) / 2;
                g2d.drawString(title, titleX, 20);
            }
        };
    }
    
    private static void drawNoDataMessage(Graphics g, int width, int height, String message) {
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(message)) / 2;
        int y = height / 2;
        g.drawString(message, x, y);
    }
    
    private static String getCategoryName(int categoryId, List<Categoria> categorias) {
        if (categorias != null) {
            for (Categoria categoria : categorias) {
                if (categoria.getId() == categoryId) {
                    return categoria.getNome();
                }
            }
        }
        return "Categoria " + categoryId;
    }
}