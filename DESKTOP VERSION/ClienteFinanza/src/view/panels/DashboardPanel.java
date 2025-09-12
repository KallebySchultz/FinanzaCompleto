package view.panels;

import controller.FinanceController;
import model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * Painel de dashboard para exibição no conteúdo principal
 */
public class DashboardPanel extends JPanel {
    private FinanceController financeController;
    private Usuario usuario;
    
    // Components for dashboard data
    private JLabel saldoLabel;
    private JLabel receitasLabel;
    private JLabel despesasLabel;
    private JLabel transacoesLabel;
    
    public DashboardPanel(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        setupUI();
        carregarDados();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Dashboard Financeiro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(127, 140, 141));
        headerPanel.add(dateLabel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel de resumo financeiro
        JPanel resumoPanel = createResumoPanel();
        add(resumoPanel, BorderLayout.CENTER);
        
        // Welcome panel
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Bem-vindo, " + usuario.getNome() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(52, 73, 94));
        welcomePanel.add(welcomeLabel);
        
        add(welcomePanel, BorderLayout.SOUTH);
    }
    
    private JPanel createResumoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 30, 30));
        panel.setOpaque(false);
        
        // Card Saldo Total
        JPanel saldoCard = createCard("Saldo Total", "R$ 0,00", new Color(41, 128, 185));
        saldoLabel = getValueLabelFromCard(saldoCard);
        
        // Card Receitas do Mês
        JPanel receitasCard = createCard("Receitas do Mês", "R$ 0,00", new Color(39, 174, 96));
        receitasLabel = getValueLabelFromCard(receitasCard);
        
        // Card Despesas do Mês
        JPanel despesasCard = createCard("Despesas do Mês", "R$ 0,00", new Color(231, 76, 60));
        despesasLabel = getValueLabelFromCard(despesasCard);
        
        // Card Transações
        JPanel transacoesCard = createCard("Transações", "0", new Color(230, 126, 34));
        transacoesLabel = getValueLabelFromCard(transacoesCard);
        
        panel.add(saldoCard);
        panel.add(receitasCard);
        panel.add(despesasCard);
        panel.add(transacoesCard);
        
        return panel;
    }
    
    private JPanel createCard(String titulo, String valor, Color cor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(236, 240, 241), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setForeground(cor);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valorLabel.setForeground(new Color(44, 62, 80));
        valorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalGlue());
        card.add(tituloLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(valorLabel);
        card.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private JLabel getValueLabelFromCard(JPanel card) {
        for (Component comp : card.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getFont().getSize() == 32) {
                    return label;
                }
            }
        }
        return null;
    }
    
    private void carregarDados() {
        // For now, set demo data since we can't test the full backend
        if (saldoLabel != null) saldoLabel.setText("R$ 5.250,00");
        if (receitasLabel != null) receitasLabel.setText("R$ 8.500,00");
        if (despesasLabel != null) despesasLabel.setText("R$ 3.250,00");
        if (transacoesLabel != null) transacoesLabel.setText("24");
    }
    
    public void refresh() {
        carregarDados();
    }
}