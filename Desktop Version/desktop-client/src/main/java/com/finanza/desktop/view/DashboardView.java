package com.finanza.desktop.view;

import com.finanza.desktop.controller.AuthController;
import com.finanza.desktop.controller.FinanceController;
import com.finanza.desktop.model.Usuario;
import com.finanza.desktop.model.Lancamento;
import com.finanza.desktop.ui.ModernUIHelper;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Tela do Dashboard
 * Visão geral das finanças do usuário
 */
public class DashboardView extends JPanel {
    
    // Cores do tema
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color POSITIVE_GREEN = ModernUIHelper.POSITIVE_GREEN;
    private static final Color NEGATIVE_RED = ModernUIHelper.NEGATIVE_RED;
    private static final Color WHITE = ModernUIHelper.WHITE;
    
    private AuthController authController;
    private FinanceController financeController;
    public interface OnNavigationNeededListener {
        void onNavigationNeeded();
    }
    
    private OnNavigationNeededListener createNavigationListener;
    
    public DashboardView(AuthController authController, FinanceController financeController, OnNavigationNeededListener createNavigationListener) {
        this.authController = authController;
        this.financeController = financeController;
        this.createNavigationListener = createNavigationListener;
        
        initializeComponents();
    }
    
    public void updateDashboard() {
        removeAll();
        initializeComponents();
        revalidate();
        repaint();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(WHITE);
        
        // Header azul
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_DARK_BLUE);
        headerPanel.setPreferredSize(new Dimension(0, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Header personalizado
        Usuario usuario = authController.getUsuarioLogado();
        String nomeUsuario = usuario != null ? usuario.getNome() : "Usuário";
        JLabel welcomeLabel = new JLabel("Bem-vindo, " + nomeUsuario + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Painel de saldos com dados reais
        JPanel balancePanel = new JPanel(new GridLayout(1, 3, 20, 0));
        balancePanel.setBackground(PRIMARY_DARK_BLUE);
        
        // Calcular dados financeiros reais
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setMaximumFractionDigits(2);
        
        Map<String, Double> resumo = financeController.calcularResumoFinanceiro();
        double totalReceitas = resumo != null ? resumo.get("receitas") : 0.0;
        double totalDespesas = resumo != null ? resumo.get("despesas") : 0.0;
        double saldo = totalReceitas - totalDespesas;
        
        balancePanel.add(createBalanceCard("Saldo Total", currencyFormat.format(saldo), WHITE));
        balancePanel.add(createBalanceCard("Receitas", currencyFormat.format(totalReceitas), POSITIVE_GREEN));
        balancePanel.add(createBalanceCard("Despesas", currencyFormat.format(totalDespesas), NEGATIVE_RED));
        
        headerPanel.add(balancePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Conteúdo principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel transactionsLabel = new JLabel("Últimas Transações");
        transactionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        transactionsLabel.setForeground(PRIMARY_DARK_BLUE);
        contentPanel.add(transactionsLabel, BorderLayout.NORTH);
        
        // Lista de transações reais
        JPanel transactionsList = new JPanel();
        transactionsList.setLayout(new BoxLayout(transactionsList, BoxLayout.Y_AXIS));
        transactionsList.setBackground(WHITE);
        
        List<Lancamento> lancamentos = financeController.obterLancamentosMesAtual();
        if (lancamentos != null && !lancamentos.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            int count = 0;
            for (Lancamento lancamento : lancamentos) {
                if (count >= 5) break; // Mostrar apenas os 5 mais recentes
                
                String valor = currencyFormat.format(lancamento.getValor());
                String data = dateFormat.format(new Date(lancamento.getData()));
                boolean isReceita = "receita".equals(lancamento.getTipo());
                
                transactionsList.add(createTransactionItem(lancamento.getDescricao(), valor, data, isReceita));
                count++;
            }
        } else {
            JLabel noDataLabel = new JLabel("Nenhuma transação encontrada este mês.");
            noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noDataLabel.setForeground(Color.GRAY);
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noDataLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            transactionsList.add(noDataLabel);
        }
        
        contentPanel.add(transactionsList, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createBalanceCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Definir tamanho preferido para manter consistência
        card.setPreferredSize(new Dimension(200, 80));
        card.setMaximumSize(new Dimension(250, 80));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(valueColor);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTransactionItem(String description, String value, String date, boolean isIncome) {
        JPanel item = ModernUIHelper.createCardPanel();
        item.setLayout(new BorderLayout());
        item.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        // Ícone e descrição
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(WHITE);
        
        String icon = isIncome ? ModernUIHelper.ICON_INCOME : ModernUIHelper.ICON_EXPENSE;
        Color iconColor = isIncome ? POSITIVE_GREEN : NEGATIVE_RED;
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        iconLabel.setForeground(iconColor);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        leftPanel.add(iconLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(WHITE);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descLabel.setForeground(Color.BLACK);
        textPanel.add(descLabel, BorderLayout.NORTH);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        textPanel.add(dateLabel, BorderLayout.SOUTH);
        
        leftPanel.add(textPanel);
        item.add(leftPanel, BorderLayout.WEST);
        
        // Valor
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(iconColor);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        item.add(valueLabel, BorderLayout.EAST);
        
        return item;
    }
}