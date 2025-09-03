package com.finanza.desktop.ui;

import com.finanza.desktop.model.*;
import com.finanza.desktop.database.*;
import com.finanza.desktop.util.UIUtils;
import com.finanza.desktop.util.FormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Tela principal do aplicativo (Dashboard)
 */
public class MainFrame extends JFrame {
    private Usuario usuarioLogado;
    private UsuarioDao usuarioDao;
    private ContaDao contaDao;
    private CategoriaDao categoriaDao;
    private LancamentoDao lancamentoDao;

    // Componentes de navegação
    private JButton homeButton;
    private JButton accountsButton;
    private JButton movementsButton;
    private JButton menuButton;
    private JButton logoutButton;

    // Componentes do dashboard
    private JLabel saldoLabel;
    private JLabel receitaLabel;
    private JLabel despesaLabel;
    private JPanel recentTransactionsPanel;
    private JPanel accountsSummaryPanel;
    private boolean saldoVisivel = true;

    public MainFrame(Usuario usuario) {
        this.usuarioLogado = usuario;
        this.usuarioDao = new UsuarioDao();
        this.contaDao = new ContaDao();
        this.categoriaDao = new CategoriaDao();
        this.lancamentoDao = new LancamentoDao();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureFrame();
        updateDashboard();
    }

    private void initializeComponents() {
        // Botões de navegação
        homeButton = new JButton("Home");
        accountsButton = new JButton("Contas");
        movementsButton = new JButton("Movimentos");
        menuButton = new JButton("Menu");
        logoutButton = new JButton("Sair");

        // Configurar botões
        UIUtils.configureButton(homeButton);
        UIUtils.configureSecondaryButton(accountsButton);
        UIUtils.configureSecondaryButton(movementsButton);
        UIUtils.configureSecondaryButton(menuButton);
        UIUtils.configureSecondaryButton(logoutButton);

        // Labels de resumo financeiro
        saldoLabel = new JLabel("R$ 0,00");
        receitaLabel = new JLabel("R$ 0,00");
        despesaLabel = new JLabel("R$ 0,00");

        // Painéis de conteúdo
        recentTransactionsPanel = new JPanel();
        recentTransactionsPanel.setLayout(new BoxLayout(recentTransactionsPanel, BoxLayout.Y_AXIS));
        recentTransactionsPanel.setBackground(Color.WHITE);

        accountsSummaryPanel = new JPanel();
        accountsSummaryPanel.setLayout(new BoxLayout(accountsSummaryPanel, BoxLayout.Y_AXIS));
        accountsSummaryPanel.setBackground(Color.WHITE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel de navegação superior
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.NORTH);

        // Painel principal com dashboard
        JPanel mainPanel = createDashboardPanel();
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(UIUtils.PRIMARY_BLUE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo e título
        JLabel titleLabel = new JLabel("Finanza Desktop");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // Saudação ao usuário
        JLabel userLabel = new JLabel("Olá, " + usuarioLogado.getNome());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        // Painel de botões
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(UIUtils.PRIMARY_BLUE);
        buttonsPanel.add(homeButton);
        buttonsPanel.add(accountsButton);
        buttonsPanel.add(movementsButton);
        buttonsPanel.add(menuButton);

        // Painel do usuário
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(UIUtils.PRIMARY_BLUE);
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(logoutButton);

        navPanel.add(titleLabel, BorderLayout.WEST);
        navPanel.add(buttonsPanel, BorderLayout.CENTER);
        navPanel.add(userPanel, BorderLayout.EAST);

        return navPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de resumo financeiro
        JPanel summaryPanel = createSummaryPanel();
        
        // Painel de conteúdo
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        // Transações recentes
        JPanel recentPanel = createRecentTransactionsPanel();
        
        // Resumo de contas
        JPanel accountsPanel = createAccountsSummaryPanel();

        contentPanel.add(recentPanel);
        contentPanel.add(accountsPanel);

        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Card de saldo
        JPanel saldoCard = UIUtils.createCard();
        saldoCard.setLayout(new BorderLayout());
        JLabel saldoTitleLabel = UIUtils.createSectionLabel("Saldo Total");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        saldoLabel.setForeground(UIUtils.PRIMARY_BLUE);
        
        JButton toggleSaldoButton = new JButton("👁");
        toggleSaldoButton.setPreferredSize(new Dimension(30, 30));
        toggleSaldoButton.addActionListener(e -> toggleSaldoVisibility());
        
        JPanel saldoTopPanel = new JPanel(new BorderLayout());
        saldoTopPanel.setBackground(Color.WHITE);
        saldoTopPanel.add(saldoTitleLabel, BorderLayout.WEST);
        saldoTopPanel.add(toggleSaldoButton, BorderLayout.EAST);
        
        saldoCard.add(saldoTopPanel, BorderLayout.NORTH);
        saldoCard.add(saldoLabel, BorderLayout.CENTER);

        // Card de receitas
        JPanel receitaCard = UIUtils.createCard();
        receitaCard.setLayout(new BorderLayout());
        JLabel receitaTitleLabel = UIUtils.createSectionLabel("Receitas");
        receitaLabel.setFont(new Font("Arial", Font.BOLD, 20));
        receitaLabel.setForeground(UIUtils.ACCENT_COLOR);
        receitaCard.add(receitaTitleLabel, BorderLayout.NORTH);
        receitaCard.add(receitaLabel, BorderLayout.CENTER);

        // Card de despesas
        JPanel despesaCard = UIUtils.createCard();
        despesaCard.setLayout(new BorderLayout());
        JLabel despesaTitleLabel = UIUtils.createSectionLabel("Despesas");
        despesaLabel.setFont(new Font("Arial", Font.BOLD, 20));
        despesaLabel.setForeground(UIUtils.ERROR_COLOR);
        despesaCard.add(despesaTitleLabel, BorderLayout.NORTH);
        despesaCard.add(despesaLabel, BorderLayout.CENTER);

        summaryPanel.add(saldoCard);
        summaryPanel.add(receitaCard);
        summaryPanel.add(despesaCard);

        return summaryPanel;
    }

    private JPanel createRecentTransactionsPanel() {
        JPanel panel = UIUtils.createCard();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = UIUtils.createSectionLabel("Transações Recentes");
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(recentTransactionsPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("+ Nova Transação");
        UIUtils.configureButton(addButton);
        addButton.addActionListener(this::showAddTransactionDialog);
        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAccountsSummaryPanel() {
        JPanel panel = UIUtils.createCard();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = UIUtils.createSectionLabel("Minhas Contas");
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(accountsSummaryPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupEventListeners() {
        homeButton.addActionListener(e -> updateDashboard());
        accountsButton.addActionListener(e -> openAccountsWindow());
        movementsButton.addActionListener(e -> openMovementsWindow());
        menuButton.addActionListener(e -> openMenuWindow());
        logoutButton.addActionListener(this::logout);
    }

    private void updateDashboard() {
        // Atualizar resumo financeiro
        double receitas = lancamentoDao.somarPorTipo("receita", usuarioLogado.getId());
        double despesas = lancamentoDao.somarPorTipo("despesa", usuarioLogado.getId());
        double saldo = receitas - despesas;

        if (saldoVisivel) {
            saldoLabel.setText(FormatUtils.formatarMoeda(saldo));
            receitaLabel.setText(FormatUtils.formatarMoeda(receitas));
            despesaLabel.setText(FormatUtils.formatarMoeda(despesas));
        } else {
            saldoLabel.setText("****");
            receitaLabel.setText("****");
            despesaLabel.setText("****");
        }

        // Atualizar transações recentes
        updateRecentTransactions();
        
        // Atualizar resumo de contas
        updateAccountsSummary();
    }

    private void updateRecentTransactions() {
        recentTransactionsPanel.removeAll();
        
        List<Lancamento> recentTransactions = lancamentoDao.listarPorUsuario(usuarioLogado.getId());
        
        if (recentTransactions.isEmpty()) {
            JLabel noDataLabel = new JLabel("Nenhuma transação encontrada");
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noDataLabel.setForeground(UIUtils.TEXT_SECONDARY);
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            recentTransactionsPanel.add(noDataLabel);
        } else {
            // Mostrar até 10 transações mais recentes
            int count = Math.min(recentTransactions.size(), 10);
            for (int i = 0; i < count; i++) {
                Lancamento lancamento = recentTransactions.get(i);
                JPanel transactionPanel = createTransactionItem(lancamento);
                recentTransactionsPanel.add(transactionPanel);
                
                if (i < count - 1) {
                    recentTransactionsPanel.add(Box.createVerticalStrut(5));
                }
            }
        }
        
        recentTransactionsPanel.revalidate();
        recentTransactionsPanel.repaint();
    }

    private JPanel createTransactionItem(Lancamento lancamento) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Ícone do tipo
        JLabel iconLabel = new JLabel(lancamento.getTipo().equals("receita") ? "📈" : "📉");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Informações
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);

        JLabel descricaoLabel = new JLabel(lancamento.getDescricao());
        descricaoLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel dataLabel = new JLabel(FormatUtils.formatarData(lancamento.getData()));
        dataLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dataLabel.setForeground(UIUtils.TEXT_SECONDARY);

        infoPanel.add(descricaoLabel, BorderLayout.NORTH);
        infoPanel.add(dataLabel, BorderLayout.SOUTH);

        // Valor
        JLabel valorLabel = new JLabel(saldoVisivel ? FormatUtils.formatarMoeda(lancamento.getValor()) : "****");
        valorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        valorLabel.setForeground(lancamento.getTipo().equals("receita") ? 
            UIUtils.ACCENT_COLOR : UIUtils.ERROR_COLOR);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(valorLabel, BorderLayout.EAST);

        return panel;
    }

    private void updateAccountsSummary() {
        accountsSummaryPanel.removeAll();
        
        List<Conta> contas = contaDao.listarPorUsuario(usuarioLogado.getId());
        
        if (contas.isEmpty()) {
            JLabel noDataLabel = new JLabel("Nenhuma conta cadastrada");
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noDataLabel.setForeground(UIUtils.TEXT_SECONDARY);
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            accountsSummaryPanel.add(noDataLabel);
        } else {
            for (int i = 0; i < contas.size(); i++) {
                Conta conta = contas.get(i);
                JPanel accountPanel = createAccountItem(conta);
                accountsSummaryPanel.add(accountPanel);
                
                if (i < contas.size() - 1) {
                    accountsSummaryPanel.add(Box.createVerticalStrut(5));
                }
            }
        }
        
        accountsSummaryPanel.revalidate();
        accountsSummaryPanel.repaint();
    }

    private JPanel createAccountItem(Conta conta) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel iconLabel = new JLabel("🏦");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel nomeLabel = new JLabel(conta.getNome());
        nomeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        double saldoAtual = contaDao.calcularSaldoAtual(conta.getId());
        JLabel saldoLabel = new JLabel(saldoVisivel ? FormatUtils.formatarMoeda(saldoAtual) : "****");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        saldoLabel.setForeground(saldoAtual >= 0 ? UIUtils.ACCENT_COLOR : UIUtils.ERROR_COLOR);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(nomeLabel, BorderLayout.CENTER);
        panel.add(saldoLabel, BorderLayout.EAST);

        return panel;
    }

    private void toggleSaldoVisibility() {
        saldoVisivel = !saldoVisivel;
        updateDashboard();
    }

    private void showAddTransactionDialog(ActionEvent e) {
        new AddTransactionDialog(this, usuarioLogado).setVisible(true);
    }

    private void openAccountsWindow() {
        new AccountsFrame(usuarioLogado).setVisible(true);
    }

    private void openMovementsWindow() {
        new MovementsFrame(usuarioLogado).setVisible(true);
    }

    private void openMenuWindow() {
        new MenuFrame(usuarioLogado).setVisible(true);
    }

    private void logout(ActionEvent e) {
        if (UIUtils.showConfirmation(this, "Deseja realmente sair?")) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public void refreshDashboard() {
        updateDashboard();
    }

    private void configureFrame() {
        setTitle("Finanza Desktop - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        UIUtils.centerWindow(this);
    }
}