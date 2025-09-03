package com.finanza.desktop;

import com.finanza.desktop.config.SettingsManager;
import com.finanza.desktop.network.NetworkManager;
import com.finanza.desktop.ui.ModernUIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        
        JLabel subtitleLabel = new JLabel("Faça login para continuar");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        centerPanel.add(subtitleLabel, gbc);
        
        // Painel do formulário (branco)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        formPanel.setPreferredSize(new Dimension(400, 300));
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 0, 10, 0);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.anchor = GridBagConstraints.CENTER;
        
        // Campo email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(emailLabel, formGbc);
        
        JTextField emailField = ModernUIHelper.createModernTextField("Digite seu email", 20);
        formGbc.gridy = 1;
        formPanel.add(emailField, formGbc);
        
        // Campo senha
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formGbc.gridy = 2;
        formPanel.add(passwordLabel, formGbc);
        
        JPasswordField passwordField = ModernUIHelper.createModernPasswordField("Digite sua senha", 20);
        formGbc.gridy = 3;
        formPanel.add(passwordField, formGbc);
        
        // Botão login
        JButton loginButton = ModernUIHelper.createModernButton("Entrar", ModernUIHelper.ICON_SUCCESS, ACCENT_BLUE);
        loginButton.addActionListener(e -> showDashboard());
        formGbc.gridy = 4;
        formGbc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(loginButton, formGbc);
        
        // Link criar conta
        JLabel createAccountLabel = new JLabel("<html><u>Não tem conta? Criar conta</u></html>");
        createAccountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        createAccountLabel.setForeground(ACCENT_BLUE);
        createAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createAccountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formGbc.gridy = 5;
        formGbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(createAccountLabel, formGbc);
        
        gbc.gridy = 2; gbc.gridwidth = 1;
        centerPanel.add(formPanel, gbc);
        
        loginPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(WHITE);
        
        // Header azul
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_DARK_BLUE);
        headerPanel.setPreferredSize(new Dimension(0, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Finanza");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Painel de saldos
        JPanel balancePanel = new JPanel(new GridLayout(1, 3, 20, 0));
        balancePanel.setBackground(PRIMARY_DARK_BLUE);
        
        balancePanel.add(createBalanceCard("Saldo Total", "R$ 2.500,00", WHITE));
        balancePanel.add(createBalanceCard("Receitas", "R$ 3.200,00", POSITIVE_GREEN));
        balancePanel.add(createBalanceCard("Despesas", "R$ 700,00", NEGATIVE_RED));
        
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
        
        // Lista de transações simulada
        JPanel transactionsList = new JPanel();
        transactionsList.setLayout(new BoxLayout(transactionsList, BoxLayout.Y_AXIS));
        transactionsList.setBackground(WHITE);
        
        transactionsList.add(createTransactionItem("Salário", "R$ 2.500,00", "01/12/2024", true));
        transactionsList.add(createTransactionItem("Supermercado", "R$ 150,00", "30/11/2024", false));
        transactionsList.add(createTransactionItem("Freelancer", "R$ 500,00", "29/11/2024", true));
        
        contentPanel.add(transactionsList, BorderLayout.CENTER);
        
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Navegação inferior
        dashboardPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    private void createAccountsPanel() {
        accountsPanel = new JPanel(new BorderLayout());
        accountsPanel.setBackground(WHITE);
        
        JLabel titleLabel = new JLabel("Contas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        accountsPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Conteúdo das contas
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        contentPanel.add(createAccountItem("Conta Corrente", "R$ 1.500,00", PRIMARY_DARK_BLUE));
        contentPanel.add(createAccountItem("Poupança", "R$ 1.000,00", POSITIVE_GREEN));
        
        accountsPanel.add(contentPanel, BorderLayout.CENTER);
        accountsPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    private void createMovementsPanel() {
        movementsPanel = new JPanel(new BorderLayout());
        movementsPanel.setBackground(WHITE);
        
        JLabel titleLabel = new JLabel("Movimentações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        movementsPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Conteúdo das movimentações
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        contentPanel.add(createTransactionItem("Salário", "R$ 2.500,00", "01/12/2024", true));
        contentPanel.add(createTransactionItem("Supermercado", "R$ 150,00", "30/11/2024", false));
        contentPanel.add(createTransactionItem("Gasolina", "R$ 80,00", "29/11/2024", false));
        contentPanel.add(createTransactionItem("Freelancer", "R$ 500,00", "28/11/2024", true));
        
        movementsPanel.add(contentPanel, BorderLayout.CENTER);
        movementsPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createBalanceCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(valueColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTransactionItem(String description, String value, String date, boolean isIncome) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY),
            BorderFactory.createEmptyBorder(15, 0, 15, 0)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(PRIMARY_DARK_BLUE);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(WHITE);
        leftPanel.add(descLabel, BorderLayout.NORTH);
        leftPanel.add(dateLabel, BorderLayout.SOUTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        valueLabel.setForeground(isIncome ? POSITIVE_GREEN : NEGATIVE_RED);
        
        item.add(leftPanel, BorderLayout.WEST);
        item.add(valueLabel, BorderLayout.EAST);
        
        return item;
    }
    
    private JPanel createAccountItem(String name, String balance, Color iconColor) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Ícone colorido
        JPanel iconPanel = new JPanel();
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setBackground(iconColor);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(PRIMARY_DARK_BLUE);
        
        JLabel balanceLabel = new JLabel(balance);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(PRIMARY_DARK_BLUE);
        
        item.add(iconPanel, BorderLayout.WEST);
        item.add(nameLabel, BorderLayout.CENTER);
        item.add(balanceLabel, BorderLayout.EAST);
        
        return item;
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
        logoutButton.addActionListener(e -> showLoginScreen());
        
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
        cardLayout.show(mainPanel, "ACCOUNTS");
    }
    
    private void showMovements() {
        cardLayout.show(mainPanel, "MOVEMENTS");
    }
    
    private void showProfile() {
        cardLayout.show(mainPanel, "PROFILE");
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
        JLabel nameLabel = new JLabel("Usuário Demo");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(PRIMARY_DARK_BLUE);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanelGbc.gridy = 1;
        infoPanel.add(nameLabel, infoPanelGbc);
        
        // Email
        JLabel emailLabel = new JLabel("usuario@demo.com");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(GRAY);
        emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanelGbc.gridy = 2;
        infoPanel.add(emailLabel, infoPanelGbc);
        
        // Data de criação
        JLabel dateLabel = new JLabel("Membro desde: 01/01/2024");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(GRAY);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanelGbc.gridy = 3;
        infoPanel.add(dateLabel, infoPanelGbc);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        actionPanel.setBackground(WHITE);
        
        JButton editButton = createStyledButton("Editar Perfil", ACCENT_BLUE);
        editButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Funcionalidade de edição será implementada na próxima versão.",
                "Editar Perfil", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton deleteButton = createStyledButton("Excluir Conta", Color.RED);
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