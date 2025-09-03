package com.finanza.desktop;

import com.finanza.desktop.config.SettingsManager;
import com.finanza.desktop.network.NetworkManager;
import com.finanza.desktop.ui.ModernUIHelper;
import com.finanza.desktop.controller.AuthController;
import com.finanza.desktop.controller.FinanceController;
import com.finanza.desktop.model.Usuario;
import com.finanza.desktop.model.Conta;
import com.finanza.desktop.model.Lancamento;
import com.finanza.desktop.model.Categoria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Aplicação desktop Finanza
 * Interface gráfica similar ao aplicativo móvel
 */
public class FinanzaDesktop extends JFrame {
    
    // Cores do tema (baseadas no app móvel) - usando ModernUIHelper
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color ACCENT_BLUE = ModernUIHelper.ACCENT_BLUE;
    private static final Color POSITIVE_GREEN = ModernUIHelper.POSITIVE_GREEN;
    private static final Color NEGATIVE_RED = ModernUIHelper.NEGATIVE_RED;
    private static final Color WHITE = ModernUIHelper.WHITE;
    private static final Color GRAY = ModernUIHelper.GRAY;
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Managers
    private SettingsManager settingsManager;
    private NetworkManager networkManager;
    
    // Controllers MVC
    private AuthController authController;
    private FinanceController financeController;
    
    // Status da conexão
    private JLabel connectionStatusLabel;
    
    // Telas
    private JPanel loginPanel;
    private JPanel dashboardPanel;
    private JPanel accountsPanel;
    private JPanel movementsPanel;
    private JPanel profilePanel;
    private JPanel settingsPanel;
    
    public FinanzaDesktop() {
        // Inicializar managers
        settingsManager = SettingsManager.getInstance();
        networkManager = NetworkManager.getInstance();
        
        // Inicializar controllers MVC
        authController = new AuthController();
        financeController = new FinanceController(authController);
        
        initializeUI();
        showLoginScreen();
        
        // Testar conexão inicial se auto-connect estiver habilitado
        if (settingsManager.isAutoConnect()) {
            testConnection();
        }
    }
    
    private void initializeUI() {
        setTitle("Finanza - Gestão Financeira");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Configurar layout principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Criar telas
        createLoginPanel();
        createDashboardPanel();
        createAccountsPanel();
        createMovementsPanel();
        createProfilePanel();
        createSettingsPanel();
        
        // Adicionar telas ao layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(accountsPanel, "ACCOUNTS");
        mainPanel.add(movementsPanel, "MOVEMENTS");
        mainPanel.add(profilePanel, "PROFILE");
        mainPanel.add(settingsPanel, "SETTINGS");
        
        add(mainPanel);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBackground(PRIMARY_DARK_BLUE);
        
        // Painel central para formulário
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(PRIMARY_DARK_BLUE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Logo/Título
        JLabel titleLabel = new JLabel("Finanza");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Sistema de Gestão Financeira");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        centerPanel.add(subtitleLabel, gbc);
        
        // Painel do formulário (branco)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        formPanel.setPreferredSize(new Dimension(450, 400));
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 0, 10, 0);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.anchor = GridBagConstraints.CENTER;
        
        // Tab para Login/Registro
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Tab de Login
        JPanel loginTab = createLoginTab();
        tabbedPane.addTab("Entrar", loginTab);
        
        // Tab de Registro
        JPanel registerTab = createRegisterTab();
        tabbedPane.addTab("Criar Conta", registerTab);
        
        formGbc.gridx = 0; formGbc.gridy = 0;
        formGbc.weightx = 1.0; formGbc.weighty = 1.0;
        formGbc.fill = GridBagConstraints.BOTH;
        formPanel.add(tabbedPane, formGbc);
        
        gbc.gridy = 2; gbc.gridwidth = 1;
        centerPanel.add(formPanel, gbc);
        
        loginPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createLoginTab() {
        JPanel loginTab = new JPanel(new GridBagLayout());
        loginTab.setBackground(WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Campo email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        loginTab.add(emailLabel, gbc);
        
        JTextField emailField = ModernUIHelper.createModernTextField("Digite seu email", 20);
        gbc.gridy = 1;
        loginTab.add(emailField, gbc);
        
        // Campo senha
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        loginTab.add(passwordLabel, gbc);
        
        JPasswordField passwordField = ModernUIHelper.createModernPasswordField("Digite sua senha", 20);
        gbc.gridy = 3;
        loginTab.add(passwordField, gbc);
        
        // Label para mensagens de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        loginTab.add(messageLabel, gbc);
        
        // Botão login
        JButton loginButton = ModernUIHelper.createModernButton("Entrar", ModernUIHelper.ICON_SUCCESS, ACCENT_BLUE);
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String senha = new String(passwordField.getPassword());
            
            if (email.isEmpty() || senha.isEmpty()) {
                messageLabel.setText("Por favor, preencha todos os campos.");
                return;
            }
            
            if (authController.login(email, senha)) {
                messageLabel.setText(" ");
                updateDashboard();
                showDashboard();
            } else {
                messageLabel.setText("Email ou senha incorretos.");
                passwordField.setText("");
            }
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 20, 10, 20);
        loginTab.add(loginButton, gbc);
        
        return loginTab;
    }

    private JPanel createRegisterTab() {
        JPanel registerTab = new JPanel(new GridBagLayout());
        registerTab.setBackground(WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Campo nome
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        registerTab.add(nameLabel, gbc);
        
        JTextField nameField = ModernUIHelper.createModernTextField("Digite seu nome completo", 20);
        gbc.gridy = 1;
        registerTab.add(nameField, gbc);
        
        // Campo email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        registerTab.add(emailLabel, gbc);
        
        JTextField emailField = ModernUIHelper.createModernTextField("Digite seu email", 20);
        gbc.gridy = 3;
        registerTab.add(emailField, gbc);
        
        // Campo senha
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 4;
        registerTab.add(passwordLabel, gbc);
        
        JPasswordField passwordField = ModernUIHelper.createModernPasswordField("Digite sua senha (mín. 6 caracteres)", 20);
        gbc.gridy = 5;
        registerTab.add(passwordField, gbc);
        
        // Campo confirmar senha
        JLabel confirmPasswordLabel = new JLabel("Confirmar Senha:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 6;
        registerTab.add(confirmPasswordLabel, gbc);
        
        JPasswordField confirmPasswordField = ModernUIHelper.createModernPasswordField("Confirme sua senha", 20);
        gbc.gridy = 7;
        registerTab.add(confirmPasswordField, gbc);
        
        // Label para mensagens
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8;
        registerTab.add(messageLabel, gbc);
        
        // Botão registro
        JButton registerButton = ModernUIHelper.createModernButton("Criar Conta", ModernUIHelper.ICON_ADD, POSITIVE_GREEN);
        registerButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            String email = emailField.getText().trim();
            String senha = new String(passwordField.getPassword());
            String confirmSenha = new String(confirmPasswordField.getPassword());
            
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmSenha.isEmpty()) {
                messageLabel.setText("Por favor, preencha todos os campos.");
                return;
            }
            
            if (!senha.equals(confirmSenha)) {
                messageLabel.setText("As senhas não coincidem.");
                confirmPasswordField.setText("");
                return;
            }
            
            if (senha.length() < 6) {
                messageLabel.setText("A senha deve ter pelo menos 6 caracteres.");
                return;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                messageLabel.setText("Por favor, digite um email válido.");
                return;
            }
            
            if (authController.registrar(nome, email, senha)) {
                messageLabel.setForeground(POSITIVE_GREEN);
                messageLabel.setText("Conta criada com sucesso! Faça login.");
                // Limpar campos
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } else {
                messageLabel.setForeground(NEGATIVE_RED);
                messageLabel.setText("Erro ao criar conta. Email já pode estar em uso.");
            }
        });
        gbc.gridy = 9;
        gbc.insets = new Insets(15, 20, 10, 20);
        registerTab.add(registerButton, gbc);
        
        return registerTab;
    }
    
    
    private void updateDashboard() {
        if (authController.isLogado()) {
            // Remover o painel antigo e criar um novo
            mainPanel.remove(dashboardPanel);
            createDashboardPanel();
            mainPanel.add(dashboardPanel, "DASHBOARD");
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(WHITE);
        
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
        
        dashboardPanel.add(headerPanel, BorderLayout.NORTH);
        
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
        
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Navegação inferior
        dashboardPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    private void createAccountsPanel() {
        accountsPanel = new JPanel(new BorderLayout());
        accountsPanel.setBackground(WHITE);
        
        // Header com título e botão adicionar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel(ModernUIHelper.ICON_ACCOUNTS + " Contas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addAccountButton = ModernUIHelper.createModernButton("Nova Conta", ModernUIHelper.ICON_ADD, POSITIVE_GREEN);
        addAccountButton.addActionListener(e -> showAddAccountDialog());
        headerPanel.add(addAccountButton, BorderLayout.EAST);
        
        accountsPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Conteúdo das contas (será atualizado dinamicamente)
        updateAccountsList();
        
        accountsPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    private void updateAccountsList() {
        // Remove o painel anterior se existir
        Component[] components = accountsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                accountsPanel.remove(comp);
                break;
            }
        }
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        // Buscar contas reais do banco de dados
        if (authController.isLogado()) {
            List<Conta> contas = financeController.obterContas();
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            
            if (contas != null && !contas.isEmpty()) {
                for (Conta conta : contas) {
                    String saldoFormatado = currencyFormat.format(conta.getSaldo());
                    Color iconColor = conta.getSaldo() >= 0 ? PRIMARY_DARK_BLUE : NEGATIVE_RED;
                    
                    JPanel accountItem = createAccountItemWithActions(conta.getNome(), saldoFormatado, iconColor, conta);
                    contentPanel.add(accountItem);
                    contentPanel.add(Box.createVerticalStrut(10));
                }
            } else {
                JLabel noAccountsLabel = new JLabel("Nenhuma conta cadastrada. Clique em 'Nova Conta' para começar.");
                noAccountsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                noAccountsLabel.setForeground(Color.GRAY);
                noAccountsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noAccountsLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
                contentPanel.add(noAccountsLabel);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        accountsPanel.add(scrollPane, BorderLayout.CENTER);
        accountsPanel.revalidate();
        accountsPanel.repaint();
    }
    
    private void createMovementsPanel() {
        movementsPanel = new JPanel(new BorderLayout());
        movementsPanel.setBackground(WHITE);
        
        // Header com título e botão adicionar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel(ModernUIHelper.ICON_MOVEMENTS + " Movimentações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout());
        rightPanel.setBackground(WHITE);
        
        JButton addTransactionButton = ModernUIHelper.createModernButton("Nova Transação", ModernUIHelper.ICON_ADD, POSITIVE_GREEN);
        addTransactionButton.addActionListener(e -> showAddTransactionDialog());
        rightPanel.add(addTransactionButton);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        movementsPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Painel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        
        JLabel filterLabel = new JLabel("Filtros:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterPanel.add(filterLabel);
        
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"Todos", "Receitas", "Despesas"});
        typeFilter.addActionListener(e -> updateMovementsList());
        filterPanel.add(typeFilter);
        
        JComboBox<String> accountFilter = new JComboBox<>();
        accountFilter.addItem("Todas as Contas");
        // Adicionar contas reais
        if (authController.isLogado()) {
            List<Conta> contas = financeController.obterContas();
            if (contas != null) {
                for (Conta conta : contas) {
                    accountFilter.addItem(conta.getNome());
                }
            }
        }
        accountFilter.addActionListener(e -> updateMovementsList());
        filterPanel.add(accountFilter);
        
        movementsPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Conteúdo das movimentações (será atualizado dinamicamente)
        updateMovementsList();
        
        movementsPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    private void updateMovementsList() {
        // Remove os painéis anteriores se existirem
        Component[] components = movementsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                movementsPanel.remove(comp);
                break;
            }
        }
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        // Buscar movimentações reais do banco de dados
        if (authController.isLogado()) {
            List<Lancamento> lancamentos = financeController.obterTodosLancamentos();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            
            if (lancamentos != null && !lancamentos.isEmpty()) {
                for (Lancamento lancamento : lancamentos) {
                    String valor = currencyFormat.format(lancamento.getValor());
                    String data = dateFormat.format(new Date(lancamento.getData()));
                    boolean isReceita = "receita".equals(lancamento.getTipo());
                    
                    JPanel transactionItem = createTransactionItemWithActions(
                        lancamento.getDescricao(), valor, data, isReceita, lancamento);
                    contentPanel.add(transactionItem);
                    contentPanel.add(Box.createVerticalStrut(10));
                }
            } else {
                JLabel noDataLabel = new JLabel("Nenhuma transação encontrada. Clique em 'Nova Transação' para começar.");
                noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                noDataLabel.setForeground(Color.GRAY);
                noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noDataLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
                contentPanel.add(noDataLabel);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        movementsPanel.add(scrollPane, BorderLayout.CENTER);
        movementsPanel.revalidate();
        movementsPanel.repaint();
    }
    
    private JPanel createBalanceCard(String title, String value, Color valueColor) {
        JPanel card = ModernUIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(valueColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTransactionItemWithActions(String description, String value, String date, boolean isIncome, Lancamento lancamento) {
        JPanel item = ModernUIHelper.createCardPanel();
        item.setLayout(new BorderLayout());
        item.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Ícone da transação
        String icon = isIncome ? ModernUIHelper.ICON_UP : ModernUIHelper.ICON_DOWN;
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setPreferredSize(new Dimension(30, 30));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(PRIMARY_DARK_BLUE);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(ModernUIHelper.DARK_GRAY);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(WHITE);
        leftPanel.add(descLabel, BorderLayout.NORTH);
        leftPanel.add(dateLabel, BorderLayout.SOUTH);
        
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setBackground(WHITE);
        leftContainer.add(iconLabel, BorderLayout.WEST);
        leftContainer.add(leftPanel, BorderLayout.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(isIncome ? POSITIVE_GREEN : NEGATIVE_RED);
        
        // Painel de ações
        JPanel actionsPanel = new JPanel(new FlowLayout());
        actionsPanel.setBackground(WHITE);
        
        JButton editButton = ModernUIHelper.createModernButton("Editar", ModernUIHelper.ICON_EDIT, ACCENT_BLUE);
        editButton.setPreferredSize(new Dimension(70, 25));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        editButton.addActionListener(e -> showEditTransactionDialog(lancamento));
        
        JButton deleteButton = ModernUIHelper.createModernButton("Excluir", ModernUIHelper.ICON_DELETE, NEGATIVE_RED);
        deleteButton.setPreferredSize(new Dimension(70, 25));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        deleteButton.addActionListener(e -> deleteTransaction(lancamento));
        
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(WHITE);
        rightPanel.add(valueLabel, BorderLayout.NORTH);
        rightPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        item.add(leftContainer, BorderLayout.WEST);
        item.add(rightPanel, BorderLayout.EAST);
        
        return item;
    }
    
    private JPanel createTransactionItem(String description, String value, String date, boolean isIncome) {
        JPanel item = ModernUIHelper.createCardPanel();
        item.setLayout(new BorderLayout());
        item.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        // Ícone da transação
        String icon = isIncome ? ModernUIHelper.ICON_UP : ModernUIHelper.ICON_DOWN;
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setPreferredSize(new Dimension(30, 30));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(PRIMARY_DARK_BLUE);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(ModernUIHelper.DARK_GRAY);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(WHITE);
        leftPanel.add(descLabel, BorderLayout.NORTH);
        leftPanel.add(dateLabel, BorderLayout.SOUTH);
        
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setBackground(WHITE);
        leftContainer.add(iconLabel, BorderLayout.WEST);
        leftContainer.add(leftPanel, BorderLayout.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(isIncome ? POSITIVE_GREEN : NEGATIVE_RED);
        
        item.add(leftContainer, BorderLayout.WEST);
        item.add(valueLabel, BorderLayout.EAST);
        
        return item;
    }
    
    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog(this, "Nova Transação", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo descrição
        JLabel descLabel = new JLabel("Descrição:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(descLabel, gbc);
        
        JTextField descField = ModernUIHelper.createModernTextField("Ex: Compras no supermercado", 25);
        gbc.gridy = 1;
        contentPanel.add(descField, gbc);
        
        // Campo valor
        JLabel valueLabel = new JLabel("Valor:");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(valueLabel, gbc);
        
        JTextField valueField = ModernUIHelper.createModernTextField("0.00", 25);
        gbc.gridy = 3;
        contentPanel.add(valueField, gbc);
        
        // Tipo da transação
        JLabel typeLabel = new JLabel("Tipo:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        contentPanel.add(typeLabel, gbc);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 5;
        contentPanel.add(typeCombo, gbc);
        
        // Conta
        JLabel accountLabel = new JLabel("Conta:");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 6;
        contentPanel.add(accountLabel, gbc);
        
        JComboBox<Conta> accountCombo = new JComboBox<>();
        List<Conta> contas = financeController.obterContas();
        if (contas != null) {
            for (Conta conta : contas) {
                accountCombo.addItem(conta);
            }
        }
        accountCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 7;
        contentPanel.add(accountCombo, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", "❌", GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", "💾", POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String descricao = descField.getText().trim();
            String valorText = valueField.getText().trim().replace(",", ".");
            String tipo = (String) typeCombo.getSelectedItem();
            Conta conta = (Conta) accountCombo.getSelectedItem();
            
            if (descricao.isEmpty()) {
                messageLabel.setText("Por favor, digite a descrição.");
                return;
            }
            
            if (conta == null) {
                messageLabel.setText("Por favor, selecione uma conta.");
                return;
            }
            
            try {
                double valor = Double.parseDouble(valorText);
                if (valor <= 0) {
                    messageLabel.setText("Valor deve ser maior que zero.");
                    return;
                }
                
                if (financeController.criarLancamento(descricao, valor, tipo, conta.getId(), null)) {
                    dialog.dispose();
                    updateMovementsList();
                    updateDashboard();
                    updateAccountsList();
                    JOptionPane.showMessageDialog(this, "Transação criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    messageLabel.setText("Erro ao criar transação. Tente novamente.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Valor deve ser um número válido.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 9;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showEditTransactionDialog(Lancamento lancamento) {
        JDialog dialog = new JDialog(this, "Editar Transação", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo descrição
        JLabel descLabel = new JLabel("Descrição:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(descLabel, gbc);
        
        JTextField descField = ModernUIHelper.createModernTextField("", 25);
        descField.setText(lancamento.getDescricao());
        gbc.gridy = 1;
        contentPanel.add(descField, gbc);
        
        // Campo valor
        JLabel valueLabel = new JLabel("Valor:");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(valueLabel, gbc);
        
        JTextField valueField = ModernUIHelper.createModernTextField("", 25);
        valueField.setText(String.valueOf(lancamento.getValor()));
        gbc.gridy = 3;
        contentPanel.add(valueField, gbc);
        
        // Tipo da transação
        JLabel typeLabel = new JLabel("Tipo:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        contentPanel.add(typeLabel, gbc);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        typeCombo.setSelectedItem(lancamento.getTipo());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 5;
        contentPanel.add(typeCombo, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", "❌", GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", "💾", POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String descricao = descField.getText().trim();
            String valorText = valueField.getText().trim().replace(",", ".");
            String tipo = (String) typeCombo.getSelectedItem();
            
            if (descricao.isEmpty()) {
                messageLabel.setText("Por favor, digite a descrição.");
                return;
            }
            
            try {
                double valor = Double.parseDouble(valorText);
                if (valor <= 0) {
                    messageLabel.setText("Valor deve ser maior que zero.");
                    return;
                }
                
                if (financeController.atualizarLancamento(lancamento.getId(), descricao, valor, tipo, lancamento.getContaId(), lancamento.getCategoriaId())) {
                    dialog.dispose();
                    updateMovementsList();
                    updateDashboard();
                    updateAccountsList();
                    JOptionPane.showMessageDialog(this, "Transação atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    messageLabel.setText("Erro ao atualizar transação. Tente novamente.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Valor deve ser um número válido.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void deleteTransaction(Lancamento lancamento) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir a transação '" + lancamento.getDescricao() + "'?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (financeController.excluirLancamento(lancamento.getId())) {
                updateMovementsList();
                updateDashboard();
                updateAccountsList();
                JOptionPane.showMessageDialog(this, "Transação excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir transação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createAccountItemWithActions(String name, String balance, Color iconColor, Conta conta) {
        JPanel item = ModernUIHelper.createCardPanel();
        item.setLayout(new BorderLayout());
        item.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Ícone colorido moderno
        JPanel iconPanel = new JPanel();
        iconPanel.setPreferredSize(new Dimension(50, 50));
        iconPanel.setBackground(iconColor);
        iconPanel.setBorder(new ModernUIHelper.RoundedBorder(25, iconColor));
        
        JLabel iconSymbol = new JLabel(ModernUIHelper.ICON_MONEY, SwingConstants.CENTER);
        iconSymbol.setFont(new Font("Segoe UI", Font.BOLD, 20));
        iconSymbol.setForeground(WHITE);
        iconPanel.setLayout(new BorderLayout());
        iconPanel.add(iconSymbol, BorderLayout.CENTER);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(PRIMARY_DARK_BLUE);
        
        JLabel balanceLabel = new JLabel(balance);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(PRIMARY_DARK_BLUE);
        
        // Painel de ações
        JPanel actionsPanel = new JPanel(new FlowLayout());
        actionsPanel.setBackground(WHITE);
        
        JButton editButton = ModernUIHelper.createModernButton("Editar", ModernUIHelper.ICON_EDIT, ACCENT_BLUE);
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        editButton.addActionListener(e -> showEditAccountDialog(conta));
        
        JButton deleteButton = ModernUIHelper.createModernButton("Excluir", ModernUIHelper.ICON_DELETE, NEGATIVE_RED);
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deleteButton.addActionListener(e -> deleteAccount(conta));
        
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);
        
        // Painel central com nome e saldo
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(WHITE);
        centerPanel.add(nameLabel, BorderLayout.NORTH);
        centerPanel.add(balanceLabel, BorderLayout.SOUTH);
        
        item.add(iconPanel, BorderLayout.WEST);
        item.add(centerPanel, BorderLayout.CENTER);
        item.add(actionsPanel, BorderLayout.EAST);
        
        return item;
    }
    
    private void showAddAccountDialog() {
        JDialog dialog = new JDialog(this, "Nova Conta", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo nome
        JLabel nameLabel = new JLabel("Nome da Conta:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(nameLabel, gbc);
        
        JTextField nameField = ModernUIHelper.createModernTextField("Ex: Conta Corrente", 20);
        gbc.gridy = 1;
        contentPanel.add(nameField, gbc);
        
        // Campo saldo inicial
        JLabel balanceLabel = new JLabel("Saldo Inicial:");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(balanceLabel, gbc);
        
        JTextField balanceField = ModernUIHelper.createModernTextField("0.00", 20);
        gbc.gridy = 3;
        contentPanel.add(balanceField, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", "❌", GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", "💾", POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            String saldoText = balanceField.getText().trim().replace(",", ".");
            
            if (nome.isEmpty()) {
                messageLabel.setText("Por favor, digite o nome da conta.");
                return;
            }
            
            try {
                double saldo = Double.parseDouble(saldoText);
                
                if (financeController.criarConta(nome, saldo)) {
                    dialog.dispose();
                    updateAccountsList();
                    JOptionPane.showMessageDialog(this, "Conta criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    messageLabel.setText("Erro ao criar conta. Tente novamente.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Saldo deve ser um número válido.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showEditAccountDialog(Conta conta) {
        JDialog dialog = new JDialog(this, "Editar Conta", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo nome
        JLabel nameLabel = new JLabel("Nome da Conta:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(nameLabel, gbc);
        
        JTextField nameField = ModernUIHelper.createModernTextField("", 20);
        nameField.setText(conta.getNome());
        gbc.gridy = 1;
        contentPanel.add(nameField, gbc);
        
        // Campo saldo atual (apenas informativo)
        JLabel balanceLabel = new JLabel("Saldo Atual:");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(balanceLabel, gbc);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        JLabel balanceValueLabel = new JLabel(currencyFormat.format(conta.getSaldo()));
        balanceValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        balanceValueLabel.setForeground(PRIMARY_DARK_BLUE);
        gbc.gridy = 3;
        contentPanel.add(balanceValueLabel, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", "❌", GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", "💾", POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            
            if (nome.isEmpty()) {
                messageLabel.setText("Por favor, digite o nome da conta.");
                return;
            }
            
            if (financeController.atualizarConta(conta.getId(), nome, conta.getSaldo())) {
                dialog.dispose();
                updateAccountsList();
                updateDashboard(); // Atualizar dashboard também
                JOptionPane.showMessageDialog(this, "Conta atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                messageLabel.setText("Erro ao atualizar conta. Tente novamente.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void deleteAccount(Conta conta) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir a conta '" + conta.getNome() + "'?\n" +
            "Esta ação também excluirá todas as transações desta conta!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (financeController.excluirConta(conta.getId())) {
                updateAccountsList();
                updateDashboard(); // Atualizar dashboard também
                JOptionPane.showMessageDialog(this, "Conta excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir conta.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.setBackground(PRIMARY_DARK_BLUE);
        navPanel.setPreferredSize(new Dimension(0, 60));
        
        JButton homeButton = ModernUIHelper.createNavButton("Dashboard", ModernUIHelper.ICON_HOME);
        homeButton.addActionListener(e -> showDashboard());
        
        JButton accountsButton = ModernUIHelper.createNavButton("Contas", ModernUIHelper.ICON_ACCOUNTS);
        accountsButton.addActionListener(e -> showAccounts());
        
        JButton movementsButton = ModernUIHelper.createNavButton("Movimentações", ModernUIHelper.ICON_MOVEMENTS);
        movementsButton.addActionListener(e -> showMovements());
        
        JButton profileButton = ModernUIHelper.createNavButton("Perfil", ModernUIHelper.ICON_PROFILE);
        profileButton.addActionListener(e -> showProfile());
        
        JButton settingsButton = ModernUIHelper.createNavButton("Configurações", ModernUIHelper.ICON_SETTINGS);
        settingsButton.addActionListener(e -> showSettings());
        
        JButton logoutButton = ModernUIHelper.createNavButton("Sair", ModernUIHelper.ICON_LOGOUT);
        logoutButton.addActionListener(e -> {
            authController.logout();
            showLoginScreen();
        });
        
        navPanel.add(homeButton);
        navPanel.add(accountsButton);
        navPanel.add(movementsButton);
        navPanel.add(profileButton);
        navPanel.add(settingsButton);
        navPanel.add(logoutButton);
        
        return navPanel;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(WHITE);
        button.setBackground(PRIMARY_DARK_BLUE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void showLoginScreen() {
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private void showDashboard() {
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    
    private void showAccounts() {
        updateAccountsList();
        cardLayout.show(mainPanel, "ACCOUNTS");
    }
    
    private void showMovements() {
        updateMovementsList();
        cardLayout.show(mainPanel, "MOVEMENTS");
    }
    
    private void showProfile() {
        // Atualizar o painel de perfil com dados do usuário atual
        mainPanel.remove(profilePanel);
        createProfilePanel();
        mainPanel.add(profilePanel, "PROFILE");
        cardLayout.show(mainPanel, "PROFILE");
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void showSettings() {
        cardLayout.show(mainPanel, "SETTINGS");
    }
    
    private void testConnection() {
        updateConnectionStatus("Testando conexão...", ModernUIHelper.DARK_GRAY);
        
        networkManager.testConnection().thenAccept(success -> {
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    updateConnectionStatus("Conectado", POSITIVE_GREEN);
                } else {
                    updateConnectionStatus("Desconectado - " + networkManager.getLastError(), NEGATIVE_RED);
                }
            });
        });
    }
    
    private void updateConnectionStatus(String text, Color color) {
        if (connectionStatusLabel != null) {
            connectionStatusLabel.setText(ModernUIHelper.ICON_CONNECT + " " + text);
            connectionStatusLabel.setForeground(color);
        }
    }
    
    private void createProfilePanel() {
        profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(PRIMARY_DARK_BLUE);
        
        // Painel principal centralizado
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(PRIMARY_DARK_BLUE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Título
        JLabel titleLabel = new JLabel("Perfil do Usuário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);
        
        // Container branco para informações
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        infoPanel.setPreferredSize(new Dimension(500, 400));
        
        GridBagConstraints infoPanelGbc = new GridBagConstraints();
        infoPanelGbc.insets = new Insets(15, 0, 15, 0);
        infoPanelGbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Avatar visual
        JPanel avatarPanel = new JPanel();
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        avatarPanel.setBackground(ACCENT_BLUE);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel avatarLabel = new JLabel("U", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 32));
        avatarLabel.setForeground(WHITE);
        avatarPanel.add(avatarLabel);
        
        infoPanelGbc.gridx = 0; infoPanelGbc.gridy = 0; infoPanelGbc.gridwidth = 2;
        infoPanelGbc.anchor = GridBagConstraints.CENTER;
        infoPanel.add(avatarPanel, infoPanelGbc);
        
        // Nome do usuário
        Usuario usuario = authController.getUsuarioLogado();
        String nomeUsuario = usuario != null ? usuario.getNome() : "Usuário";
        String emailUsuario = usuario != null ? usuario.getEmail() : "email@exemplo.com";
        
        JLabel nameLabel = new JLabel(nomeUsuario);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(PRIMARY_DARK_BLUE);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanelGbc.gridy = 1;
        infoPanel.add(nameLabel, infoPanelGbc);
        
        // Email
        JLabel emailLabel = new JLabel(emailUsuario);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(GRAY);
        emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanelGbc.gridy = 2;
        infoPanel.add(emailLabel, infoPanelGbc);
        
        // Data de criação
        String dataCriacao = "01/01/2024";
        if (usuario != null && usuario.getDataCriacao() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dataCriacao = dateFormat.format(new Date(usuario.getDataCriacao()));
        }
        JLabel dateLabel = new JLabel("Membro desde: " + dataCriacao);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(GRAY);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanelGbc.gridy = 3;
        infoPanel.add(dateLabel, infoPanelGbc);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        actionPanel.setBackground(WHITE);
        
        JButton editButton = ModernUIHelper.createModernButton("Editar Perfil", ModernUIHelper.ICON_PROFILE, ACCENT_BLUE);
        editButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Funcionalidade de edição será implementada na próxima versão.",
                "Editar Perfil", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton deleteButton = ModernUIHelper.createModernButton("Excluir Conta", ModernUIHelper.ICON_ERROR, NEGATIVE_RED);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir sua conta?\nEsta ação é irreversível!",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "Conta excluída com sucesso!\nVoltando para a tela de login.",
                    "Conta Excluída",
                    JOptionPane.INFORMATION_MESSAGE);
                showLoginScreen();
            }
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        
        infoPanelGbc.gridy = 4;
        infoPanelGbc.insets = new Insets(30, 0, 0, 0);
        infoPanel.add(actionPanel, infoPanelGbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(infoPanel, gbc);
        
        profilePanel.add(centerPanel, BorderLayout.CENTER);
        profilePanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    private void createSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBackground(PRIMARY_DARK_BLUE);
        
        // Painel principal centralizado
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(PRIMARY_DARK_BLUE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Título
        JLabel titleLabel = new JLabel(ModernUIHelper.ICON_SETTINGS + " Configurações");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);
        
        // Container branco para configurações
        JPanel configPanel = ModernUIHelper.createCardPanel();
        configPanel.setLayout(new GridBagLayout());
        configPanel.setPreferredSize(new Dimension(600, 500));
        configPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints configGbc = new GridBagConstraints();
        configGbc.insets = new Insets(15, 10, 15, 10);
        configGbc.fill = GridBagConstraints.HORIZONTAL;
        configGbc.anchor = GridBagConstraints.WEST;
        
        // Seção de Conexão
        JLabel connectionLabel = new JLabel("🌐 Configurações de Rede");
        connectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        connectionLabel.setForeground(PRIMARY_DARK_BLUE);
        configGbc.gridx = 0; configGbc.gridy = 0; configGbc.gridwidth = 2;
        configPanel.add(connectionLabel, configGbc);
        
        // Campo IP do Servidor
        JLabel hostLabel = new JLabel("IP do Servidor:");
        hostLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        configGbc.gridx = 0; configGbc.gridy = 1; configGbc.gridwidth = 1;
        configGbc.weightx = 0.3;
        configPanel.add(hostLabel, configGbc);
        
        JTextField hostField = ModernUIHelper.createModernTextField("", 20);
        hostField.setText(settingsManager.getServerHost());
        configGbc.gridx = 1; configGbc.gridy = 1;
        configGbc.weightx = 0.7;
        configPanel.add(hostField, configGbc);
        
        // Campo Porta
        JLabel portLabel = new JLabel("Porta:");
        portLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        configGbc.gridx = 0; configGbc.gridy = 2;
        configGbc.weightx = 0.3;
        configPanel.add(portLabel, configGbc);
        
        JTextField portField = ModernUIHelper.createModernTextField("", 10);
        portField.setText(String.valueOf(settingsManager.getServerPort()));
        configGbc.gridx = 1; configGbc.gridy = 2;
        configGbc.weightx = 0.7;
        configPanel.add(portField, configGbc);
        
        // Status da conexão
        connectionStatusLabel = ModernUIHelper.createStatusLabel(
            "Não testado", ModernUIHelper.ICON_DISCONNECT, ModernUIHelper.DARK_GRAY);
        configGbc.gridx = 0; configGbc.gridy = 3; configGbc.gridwidth = 2;
        configGbc.insets = new Insets(5, 10, 15, 10);
        configPanel.add(connectionStatusLabel, configGbc);
        
        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton testButton = ModernUIHelper.createModernButton("Testar Conexão", 
            ModernUIHelper.ICON_CONNECT, ACCENT_BLUE);
        testButton.addActionListener(e -> testConnection());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", 
            ModernUIHelper.ICON_SUCCESS, POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            // Validar e salvar configurações
            try {
                String host = hostField.getText().trim();
                int port = Integer.parseInt(portField.getText().trim());
                
                if (host.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "IP do servidor não pode estar vazio!", 
                        "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (port < 1 || port > 65535) {
                    JOptionPane.showMessageDialog(this, "Porta deve estar entre 1 e 65535!", 
                        "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Salvar configurações
                settingsManager.setServerHost(host);
                settingsManager.setServerPort(port);
                settingsManager.saveSettings();
                
                JOptionPane.showMessageDialog(this, "Configurações salvas com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                // Testar nova conexão
                testConnection();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Porta deve ser um número válido!", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(testButton);
        buttonPanel.add(saveButton);
        
        configGbc.gridx = 0; configGbc.gridy = 4; configGbc.gridwidth = 2;
        configGbc.insets = new Insets(20, 10, 10, 10);
        configPanel.add(buttonPanel, configGbc);
        
        // Seção de Informações
        JLabel infoLabel = new JLabel("ℹ️ Informações");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setForeground(PRIMARY_DARK_BLUE);
        configGbc.gridx = 0; configGbc.gridy = 5; configGbc.gridwidth = 2;
        configGbc.insets = new Insets(30, 10, 15, 10);
        configPanel.add(infoLabel, configGbc);
        
        JTextArea infoText = new JTextArea(
            "• Para conectar com o aplicativo móvel, ambos devem estar na mesma rede WiFi\n" +
            "• Descubra o IP do computador executando 'ipconfig' (Windows) ou 'ifconfig' (Linux/Mac)\n" +
            "• A porta padrão é 8080, mas pode ser alterada se necessário\n" +
            "• Exemplo de IP: 192.168.1.100"
        );
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoText.setForeground(ModernUIHelper.DARK_GRAY);
        infoText.setBackground(WHITE);
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        
        configGbc.gridx = 0; configGbc.gridy = 6; configGbc.gridwidth = 2;
        configGbc.insets = new Insets(5, 10, 10, 10);
        configPanel.add(infoText, configGbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(configPanel, gbc);
        
        settingsPanel.add(centerPanel, BorderLayout.CENTER);
        settingsPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new FinanzaDesktop().setVisible(true);
        });
    }
}