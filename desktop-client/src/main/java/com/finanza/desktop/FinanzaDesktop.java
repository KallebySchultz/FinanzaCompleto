package com.finanza.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Aplicação desktop Finanza
 * Interface gráfica similar ao aplicativo móvel
 */
public class FinanzaDesktop extends JFrame {
    
    // Cores do tema (baseadas no app móvel)
    private static final Color PRIMARY_DARK_BLUE = new Color(27, 42, 87);  // #1B2A57
    private static final Color ACCENT_BLUE = new Color(74, 124, 245);      // #4A7CF5
    private static final Color POSITIVE_GREEN = new Color(33, 200, 122);   // #21C87A
    private static final Color NEGATIVE_RED = new Color(229, 57, 53);      // #E53935
    private static final Color WHITE = Color.WHITE;
    private static final Color GRAY = new Color(245, 245, 245);            // #F5F5F5
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Telas
    private JPanel loginPanel;
    private JPanel dashboardPanel;
    private JPanel accountsPanel;
    private JPanel movementsPanel;
    private JPanel profilePanel;
    
    public FinanzaDesktop() {
        initializeUI();
        showLoginScreen();
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
        
        // Adicionar telas ao layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(accountsPanel, "ACCOUNTS");
        mainPanel.add(movementsPanel, "MOVEMENTS");
        mainPanel.add(profilePanel, "PROFILE");
        
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
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formGbc.gridx = 0; formGbc.gridy = 0;
        formPanel.add(emailLabel, formGbc);
        
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formGbc.gridy = 1;
        formPanel.add(emailField, formGbc);
        
        // Campo senha
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formGbc.gridy = 2;
        formPanel.add(passwordLabel, formGbc);
        
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formGbc.gridy = 3;
        formPanel.add(passwordField, formGbc);
        
        // Botão login
        JButton loginButton = createStyledButton("Entrar", ACCENT_BLUE);
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
        
        JButton homeButton = createNavButton("Dashboard");
        homeButton.addActionListener(e -> showDashboard());
        
        JButton accountsButton = createNavButton("Contas");
        accountsButton.addActionListener(e -> showAccounts());
        
        JButton movementsButton = createNavButton("Movimentações");
        movementsButton.addActionListener(e -> showMovements());
        
        JButton profileButton = createNavButton("Perfil");
        profileButton.addActionListener(e -> showProfile());
        
        JButton logoutButton = createNavButton("Sair");
        logoutButton.addActionListener(e -> showLoginScreen());
        
        navPanel.add(homeButton);
        navPanel.add(accountsButton);
        navPanel.add(movementsButton);
        navPanel.add(profileButton);
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