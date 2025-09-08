package view;

import controller.AuthController;
import controller.FinanceController;
import model.Usuario;
import util.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela principal do dashboard
 */
public class DashboardView extends JFrame {
    private AuthController authController;
    private FinanceController financeController;
    private Usuario usuario;
    
    // Components for dashboard data
    private JLabel saldoLabel;
    private JLabel receitasLabel;
    private JLabel despesasLabel;
    private JLabel transacoesLabel;
    
    public DashboardView(AuthController authController, Usuario usuario) {
        this.authController = authController;
        this.financeController = new FinanceController(authController.getNetworkClient());
        this.usuario = usuario;
        initComponents();
        setupUI();
        setupEvents();
        carregarDados();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Arquivo
        JMenu arquivoMenu = new JMenu("Arquivo");
        JMenuItem atualizarItem = new JMenuItem("Atualizar Dashboard");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem sairItem = new JMenuItem("Sair");
        arquivoMenu.add(atualizarItem);
        arquivoMenu.addSeparator();
        arquivoMenu.add(logoutItem);
        arquivoMenu.addSeparator();
        arquivoMenu.add(sairItem);
        
        // Menu Financeiro
        JMenu financeiroMenu = new JMenu("Financeiro");
        JMenuItem movimentacoesItem = new JMenuItem("Movimentações");
        JMenuItem contasItem = new JMenuItem("Contas");
        JMenuItem categoriasItem = new JMenuItem("Categorias");
        financeiroMenu.add(movimentacoesItem);
        financeiroMenu.add(contasItem);
        financeiroMenu.add(categoriasItem);
        
        // Menu Relatórios
        JMenu relatoriosMenu = new JMenu("Relatórios");
        JMenuItem relatorioMensalItem = new JMenuItem("Relatório Mensal");
        JMenuItem exportarItem = new JMenuItem("Exportar Dados");
        relatoriosMenu.add(relatorioMensalItem);
        relatoriosMenu.add(exportarItem);
        
        // Menu Usuário
        JMenu usuarioMenu = new JMenu("Usuário");
        JMenuItem perfilItem = new JMenuItem("Perfil");
        usuarioMenu.add(perfilItem);
        
        menuBar.add(arquivoMenu);
        menuBar.add(financeiroMenu);
        menuBar.add(relatoriosMenu);
        menuBar.add(usuarioMenu);
        setJMenuBar(menuBar);
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ColorScheme.BACKGROUND);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.BACKGROUND);
        JLabel welcomeLabel = new JLabel("Bem-vindo, " + usuario.getNome() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(ColorScheme.PRIMARY_DARK_BLUE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Painel de resumo financeiro
        JPanel resumoPanel = createResumoPanel();
        mainPanel.add(resumoPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(ColorScheme.GRAY);
        JLabel statusLabel = new JLabel("Conectado ao servidor | Usuário: " + usuario.getEmail());
        statusLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Eventos dos menus
        atualizarItem.addActionListener(e -> carregarDados());
        logoutItem.addActionListener(e -> realizarLogout());
        sairItem.addActionListener(e -> System.exit(0));
        
        movimentacoesItem.addActionListener(e -> abrirMovimentacoes());
        contasItem.addActionListener(e -> abrirContas());
        categoriasItem.addActionListener(e -> abrirCategorias());
        
        relatorioMensalItem.addActionListener(e -> abrirRelatorios());
        exportarItem.addActionListener(e -> exportarDados());
        
        perfilItem.addActionListener(e -> abrirPerfil());
    }
    
    private JPanel createResumoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(ColorScheme.BACKGROUND);
        
        // Card Saldo Total
        JPanel saldoCard = createCard("Saldo Total", "R$ 0,00", ColorScheme.PRIMARY_DARK_BLUE);
        saldoLabel = (JLabel) saldoCard.getComponent(3); // Get the value label
        
        // Card Receitas do Mês
        JPanel receitasCard = createCard("Receitas do Mês", "R$ 0,00", ColorScheme.POSITIVE_GREEN);
        receitasLabel = (JLabel) receitasCard.getComponent(3);
        
        // Card Despesas do Mês
        JPanel despesasCard = createCard("Despesas do Mês", "R$ 0,00", ColorScheme.NEGATIVE_RED);
        despesasLabel = (JLabel) despesasCard.getComponent(3);
        
        // Card Transações
        JPanel transacoesCard = createCard("Transações", "0", ColorScheme.ACCENT_BLUE);
        transacoesLabel = (JLabel) transacoesCard.getComponent(3);
        
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
            BorderFactory.createLineBorder(cor, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(ColorScheme.CARD_BACKGROUND);
        
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tituloLabel.setForeground(cor);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valorLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        valorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalGlue());
        card.add(tituloLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valorLabel);
        card.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private void setupEvents() {
        // Configurar eventos se necessário
    }
    
    private void carregarDados() {
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
                        atualizarDashboard(data);
                    } else {
                        System.err.println("Erro ao carregar dashboard: " + data.getMensagem());
                        // Manter valores padrão em caso de erro
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar dados do dashboard: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarDashboard(FinanceController.DashboardData data) {
        // Atualizar labels com dados reais
        saldoLabel.setText(String.format("R$ %.2f", data.getSaldoTotal()));
        receitasLabel.setText(String.format("R$ %.2f", data.getReceitasMes()));
        despesasLabel.setText(String.format("R$ %.2f", data.getDespesasMes()));
        transacoesLabel.setText(String.valueOf(data.getNumTransacoes()));
        
        // Atualizar cores baseadas nos valores
        if (data.getSaldoTotal() < 0) {
            saldoLabel.setForeground(ColorScheme.BALANCE_NEGATIVE);
        } else {
            saldoLabel.setForeground(ColorScheme.BALANCE_POSITIVE);
        }
    }
    
    private void realizarLogout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente fazer logout?",
            "Confirmar Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            authController.desconectar();
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginView().setVisible(true);
            });
        }
    }
    
    private void abrirMovimentacoes() {
        try {
            MovimentacoesView movimentacoesView = new MovimentacoesView(financeController, usuario);
            movimentacoesView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir movimentações: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirContas() {
        try {
            ContasView contasView = new ContasView(financeController, usuario);
            contasView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir contas: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirCategorias() {
        try {
            CategoriasView categoriasView = new CategoriasView(financeController, usuario);
            categoriasView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir categorias: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirRelatorios() {
        try {
            RelatoriosView relatoriosView = new RelatoriosView(financeController, usuario);
            relatoriosView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir relatórios: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportarDados() {
        try {
            ExportacaoView exportacaoView = new ExportacaoView(financeController, usuario);
            exportacaoView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir exportação: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirPerfil() {
        try {
            PerfilView perfilView = new PerfilView(financeController, usuario);
            perfilView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir perfil: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}