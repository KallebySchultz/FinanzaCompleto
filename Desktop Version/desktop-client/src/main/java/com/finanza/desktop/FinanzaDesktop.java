package com.finanza.desktop;

import com.finanza.desktop.config.SettingsManager;
import com.finanza.desktop.network.NetworkManager;
import com.finanza.desktop.ui.ModernUIHelper;
import com.finanza.desktop.controller.AuthController;
import com.finanza.desktop.controller.FinanceController;
import com.finanza.desktop.view.*;

import javax.swing.*;
import java.awt.*;

/**
 * Aplicação desktop Finanza
 * Interface gráfica similar ao aplicativo móvel
 */
public class FinanzaDesktop extends JFrame {
    
    // Cores do tema (baseadas no app móvel) - usando ModernUIHelper
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color WHITE = ModernUIHelper.WHITE;
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Managers
    private SettingsManager settingsManager;
    private NetworkManager networkManager;
    
    // Controllers MVC
    private AuthController authController;
    private FinanceController financeController;
    
    // Views separadas
    private LoginView loginView;
    private DashboardView dashboardView;
    private AccountsView accountsView;
    private MovementsView movementsView;
    private ProfileView profileView;
    private SettingsView settingsView;

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
        
        // Criar views
        createViews();
        
        // Adicionar views ao layout
        mainPanel.add(loginView, "LOGIN");
        mainPanel.add(dashboardView, "DASHBOARD");
        mainPanel.add(accountsView, "ACCOUNTS");
        mainPanel.add(movementsView, "MOVEMENTS");
        mainPanel.add(profileView, "PROFILE");
        mainPanel.add(settingsView, "SETTINGS");
        
        add(mainPanel);
    }
    
    private void createViews() {
        // Criar LoginView com callback para login bem-sucedido
        loginView = new LoginView(authController, new LoginView.OnLoginSuccessListener() {
            @Override
            public void onLoginSuccess() {
                updateDashboard();
                showDashboard();
            }
        });
        
        // Criar DashboardView
        dashboardView = new DashboardView(authController, financeController, new DashboardView.OnNavigationNeededListener() {
            @Override
            public void onNavigationNeeded() {
                // Implementar navegação se necessário
            }
        });
        
        // Criar outras views
        accountsView = new AccountsView(authController, financeController);
        movementsView = new MovementsView(authController, financeController);
        profileView = new ProfileView(authController);
        settingsView = new SettingsView(settingsManager, networkManager);
        
        // Adicionar navegação inferior a todas as views (exceto login)
        addNavigationToViews();
    }
    
    private void addNavigationToViews() {
        JPanel dashboardNav = createNavigationPanel();
        JPanel accountsNav = createNavigationPanel();
        JPanel movementsNav = createNavigationPanel();
        JPanel profileNav = createNavigationPanel();
        JPanel settingsNav = createNavigationPanel();
        
        dashboardView.add(dashboardNav, BorderLayout.SOUTH);
        accountsView.add(accountsNav, BorderLayout.SOUTH);
        movementsView.add(movementsNav, BorderLayout.SOUTH);
        profileView.add(profileNav, BorderLayout.SOUTH);
        settingsView.add(settingsNav, BorderLayout.SOUTH);
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
    
    // Métodos de navegação
    private void showLoginScreen() {
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private void showDashboard() {
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    
    private void showAccounts() {
        accountsView.updateAccountsList();
        cardLayout.show(mainPanel, "ACCOUNTS");
    }
    
    private void showMovements() {
        movementsView.updateMovementsList();
        cardLayout.show(mainPanel, "MOVEMENTS");
    }
    
    private void showProfile() {
        profileView.updateProfile();
        cardLayout.show(mainPanel, "PROFILE");
    }
    
    private void showSettings() {
        settingsView.updateConnectionStatusDisplay();
        cardLayout.show(mainPanel, "SETTINGS");
    }
    
    private void updateDashboard() {
        if (authController.isLogado()) {
            dashboardView.updateDashboard();
        }
    }
    
    private void testConnection() {
        try {
            networkManager.testConnection();
        } catch (Exception e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // Usar look and feel padrão
        SwingUtilities.invokeLater(() -> {
            new FinanzaDesktop().setVisible(true);
        });
    }
}