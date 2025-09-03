package com.finanza.desktop.view;

import com.finanza.desktop.controller.FinanceController;
import com.finanza.desktop.model.Lancamento;
import com.finanza.desktop.model.Categoria;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Classe para criar gráficos financeiros
 * Preparada para integração com JFreeChart quando necessário
 */
public class ChartManager {
    private FinanceController financeController;
    
    public ChartManager(FinanceController financeController) {
        this.financeController = financeController;
    }
    
    /**
     * Cria um painel de gráfico de pizza para receitas/despesas
     */
    public JPanel createPieChartPanel() {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        // Por enquanto, criar um placeholder
        JLabel placeholder = new JLabel("Gráfico de Receitas vs Despesas", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 16));
        placeholder.setForeground(new Color(27, 42, 87));
        
        Map<String, Double> resumo = financeController.calcularResumoFinanceiro();
        if (resumo != null) {
            double receitas = resumo.get("receitas");
            double despesas = resumo.get("despesas");
            
            JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            infoPanel.add(new JLabel("Receitas: R$ " + String.format("%.2f", receitas)));
            infoPanel.add(createColorBox(new Color(33, 200, 122))); // Verde
            infoPanel.add(new JLabel("Despesas: R$ " + String.format("%.2f", despesas)));
            infoPanel.add(createColorBox(new Color(229, 57, 53))); // Vermelho
            
            chartPanel.add(placeholder, BorderLayout.NORTH);
            chartPanel.add(infoPanel, BorderLayout.CENTER);
        } else {
            chartPanel.add(placeholder, BorderLayout.CENTER);
        }
        
        return chartPanel;
    }
    
    /**
     * Cria um painel de gráfico de barras para categorias
     */
    public JPanel createCategoryBarChartPanel() {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(500, 400));
        
        JLabel placeholder = new JLabel("Gráfico por Categorias", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 16));
        placeholder.setForeground(new Color(27, 42, 87));
        
        // Calcular dados por categoria
        Map<String, Double> gastosPorCategoria = calcularGastosPorCategoria();
        
        if (!gastosPorCategoria.isEmpty()) {
            JPanel dataPanel = new JPanel();
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
            dataPanel.setBackground(Color.WHITE);
            dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            
            for (Map.Entry<String, Double> entry : gastosPorCategoria.entrySet()) {
                JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                
                JLabel nameLabel = new JLabel(entry.getKey());
                nameLabel.setPreferredSize(new Dimension(150, 25));
                
                JLabel valueLabel = new JLabel("R$ " + String.format("%.2f", entry.getValue()));
                valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                
                itemPanel.add(nameLabel, BorderLayout.WEST);
                itemPanel.add(valueLabel, BorderLayout.EAST);
                
                dataPanel.add(itemPanel);
                dataPanel.add(Box.createVerticalStrut(5));
            }
            
            JScrollPane scrollPane = new JScrollPane(dataPanel);
            scrollPane.setBorder(null);
            
            chartPanel.add(placeholder, BorderLayout.NORTH);
            chartPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            JLabel noDataLabel = new JLabel("Sem dados para exibir", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noDataLabel.setForeground(Color.GRAY);
            
            chartPanel.add(placeholder, BorderLayout.NORTH);
            chartPanel.add(noDataLabel, BorderLayout.CENTER);
        }
        
        return chartPanel;
    }
    
    /**
     * Cria um painel de gráfico de linha para evolução temporal
     */
    public JPanel createTimeLineChartPanel() {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        
        JLabel placeholder = new JLabel("Evolução Temporal - Últimos Lançamentos", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 16));
        placeholder.setForeground(new Color(27, 42, 87));
        
        List<Lancamento> lancamentos = financeController.listarLancamentos();
        
        if (lancamentos != null && !lancamentos.isEmpty()) {
            JPanel timelinePanel = new JPanel();
            timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
            timelinePanel.setBackground(Color.WHITE);
            timelinePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            
            int count = 0;
            for (Lancamento lancamento : lancamentos) {
                if (count >= 10) break; // Mostrar apenas os 10 mais recentes
                
                JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                
                JLabel descLabel = new JLabel(lancamento.getDescricao());
                JLabel valueLabel = new JLabel("R$ " + String.format("%.2f", lancamento.getValor()));
                
                if ("receita".equals(lancamento.getTipo())) {
                    valueLabel.setForeground(new Color(33, 200, 122)); // Verde
                } else {
                    valueLabel.setForeground(new Color(229, 57, 53)); // Vermelho
                }
                
                itemPanel.add(descLabel, BorderLayout.WEST);
                itemPanel.add(valueLabel, BorderLayout.EAST);
                
                timelinePanel.add(itemPanel);
                timelinePanel.add(Box.createVerticalStrut(3));
                count++;
            }
            
            JScrollPane scrollPane = new JScrollPane(timelinePanel);
            scrollPane.setBorder(null);
            
            chartPanel.add(placeholder, BorderLayout.NORTH);
            chartPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            JLabel noDataLabel = new JLabel("Nenhum lançamento encontrado", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noDataLabel.setForeground(Color.GRAY);
            
            chartPanel.add(placeholder, BorderLayout.NORTH);
            chartPanel.add(noDataLabel, BorderLayout.CENTER);
        }
        
        return chartPanel;
    }
    
    private JPanel createColorBox(Color color) {
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return colorBox;
    }
    
    private Map<String, Double> calcularGastosPorCategoria() {
        Map<String, Double> gastosPorCategoria = new HashMap<>();
        
        List<Lancamento> lancamentos = financeController.listarLancamentos();
        List<Categoria> categorias = financeController.listarCategorias();
        
        if (lancamentos == null || categorias == null) {
            return gastosPorCategoria;
        }
        
        // Criar mapa de categoria ID para nome
        Map<Integer, String> categoriaNomes = new HashMap<>();
        for (Categoria categoria : categorias) {
            categoriaNomes.put(categoria.getId(), categoria.getNome());
        }
        
        // Calcular gastos por categoria
        for (Lancamento lancamento : lancamentos) {
            String nomeCategoria = categoriaNomes.get(lancamento.getCategoriaId());
            if (nomeCategoria != null) {
                gastosPorCategoria.merge(nomeCategoria, lancamento.getValor(), Double::sum);
            }
        }
        
        return gastosPorCategoria;
    }
    
    /**
     * Método para futuro: integração com JFreeChart
     * Quando JFreeChart for adicionado, este método criará gráficos reais
     */
    /*
    public JPanel createRealPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> resumo = financeController.calcularResumoFinanceiro();
        
        dataset.setValue("Receitas", resumo.get("receitas"));
        dataset.setValue("Despesas", resumo.get("despesas"));
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Distribuição Financeira",
            dataset,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    */
}