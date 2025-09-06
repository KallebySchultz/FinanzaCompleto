package view;

import controller.FinanceController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Tela de relatórios financeiros
 */
public class RelatoriosView extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private NumberFormat currencyFormat;
    
    private JLabel saldoTotalLabel;
    private JLabel receitasMesLabel;
    private JLabel despesasMesLabel;
    private JLabel saldoMesLabel;
    private JLabel totalTransacoesLabel;
    
    public RelatoriosView(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        initComponents();
        setupUI();
        carregarRelatorio();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Relatórios Financeiros");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Relatório Financeiro - " + LocalDate.now().getMonth() + "/" + LocalDate.now().getYear());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton atualizarBtn = new JButton("Atualizar");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(atualizarBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Painel principal com dados do relatório
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Painel de resumo financeiro
        JPanel resumoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        resumoPanel.setBorder(BorderFactory.createTitledBorder("Resumo Financeiro"));
        
        resumoPanel.add(new JLabel("Saldo Total das Contas:"));
        saldoTotalLabel = new JLabel("R$ 0,00");
        saldoTotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resumoPanel.add(saldoTotalLabel);
        
        resumoPanel.add(new JLabel("Receitas do Mês:"));
        receitasMesLabel = new JLabel("R$ 0,00");
        receitasMesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        receitasMesLabel.setForeground(Color.GREEN);
        resumoPanel.add(receitasMesLabel);
        
        resumoPanel.add(new JLabel("Despesas do Mês:"));
        despesasMesLabel = new JLabel("R$ 0,00");
        despesasMesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        despesasMesLabel.setForeground(Color.RED);
        resumoPanel.add(despesasMesLabel);
        
        resumoPanel.add(new JLabel("Saldo do Mês:"));
        saldoMesLabel = new JLabel("R$ 0,00");
        saldoMesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resumoPanel.add(saldoMesLabel);
        
        resumoPanel.add(new JLabel("Total de Transações:"));
        totalTransacoesLabel = new JLabel("0");
        totalTransacoesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resumoPanel.add(totalTransacoesLabel);
        
        mainPanel.add(resumoPanel, BorderLayout.NORTH);
        
        // Área de texto para observações/análise
        JTextArea analiseArea = new JTextArea();
        analiseArea.setEditable(false);
        analiseArea.setText("Análise Financeira:\n\n" +
                           "• Este relatório apresenta um resumo da sua situação financeira atual.\n" +
                           "• O saldo total representa a soma de todas as suas contas.\n" +
                           "• As receitas e despesas são referentes ao mês atual.\n" +
                           "• O saldo do mês é a diferença entre receitas e despesas.\n\n" +
                           "Dicas:\n" +
                           "• Mantenha suas despesas sempre menores que suas receitas.\n" +
                           "• Monitore regularmente suas movimentações financeiras.\n" +
                           "• Categorize adequadamente suas transações para melhor controle.");
        analiseArea.setBackground(getBackground());
        analiseArea.setBorder(BorderFactory.createTitledBorder("Análise"));
        
        mainPanel.add(analiseArea, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Relatório gerado em: " + LocalDate.now());
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Eventos
        atualizarBtn.addActionListener(e -> carregarRelatorio());
    }
    
    private void carregarRelatorio() {
        SwingWorker<FinanceController.DashboardData, Void> worker = new SwingWorker<FinanceController.DashboardData, Void>() {
            @Override
            protected FinanceController.DashboardData doInBackground() throws Exception {
                return financeController.getDashboard();
            }
            
            @Override
            protected void done() {
                try {
                    FinanceController.DashboardData data = get();
                    if (data.isSucesso()) {
                        atualizarRelatorio(data);
                    } else {
                        JOptionPane.showMessageDialog(RelatoriosView.this, 
                            "Erro ao carregar dados: " + data.getMensagem(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, 
                        "Erro ao carregar relatório: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarRelatorio(FinanceController.DashboardData data) {
        saldoTotalLabel.setText(currencyFormat.format(data.getSaldoTotal()));
        receitasMesLabel.setText(currencyFormat.format(data.getReceitasMes()));
        despesasMesLabel.setText(currencyFormat.format(data.getDespesasMes()));
        totalTransacoesLabel.setText(String.valueOf(data.getNumTransacoes()));
        
        // Calcular saldo do mês (receitas - despesas)
        double saldoMes = data.getReceitasMes() - data.getDespesasMes();
        saldoMesLabel.setText(currencyFormat.format(saldoMes));
        
        // Colorir saldo do mês baseado no valor
        if (saldoMes > 0) {
            saldoMesLabel.setForeground(Color.GREEN);
        } else if (saldoMes < 0) {
            saldoMesLabel.setForeground(Color.RED);
        } else {
            saldoMesLabel.setForeground(Color.BLACK);
        }
        
        // Colorir saldo total baseado no valor
        if (data.getSaldoTotal() < 0) {
            saldoTotalLabel.setForeground(Color.RED);
        } else {
            saldoTotalLabel.setForeground(Color.BLACK);
        }
    }
}