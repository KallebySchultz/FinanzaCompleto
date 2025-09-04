package ui;

import view.LoginView;
import view.HomeView;
import view.MovementsView;
import view.AccountsView;
import view.MenuView;
import view.ConfigView;
import view.CadastroView;
import view.CategoriasView;
import controller.UsuarioController;
import database.DatabaseManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * FinanzaDesktop - Main application class for Finanza Desktop Client
 * Manages navigation between views and application lifecycle
 */
public class FinanzaDesktop {
    
    // Static instance for singleton pattern
    private static FinanzaDesktop instance;
    
    // Controllers
    private UsuarioController usuarioController;
    
    // Views
    private LoginView loginView;
    private HomeView homeView;
    private MovementsView movementsView;
    private AccountsView accountsView;
    private MenuView menuView;
    private ConfigView configView;
    private CadastroView cadastroView;
    private CategoriasView categoriasView;
    
    // View integrator
    private ViewIntegrator viewIntegrator;
    
    // Current view
    private JFrame currentView;
    
    private FinanzaDesktop() {
        initializeControllers();
        initializeViews();
    }
    
    public static FinanzaDesktop getInstance() {
        if (instance == null) {
            instance = new FinanzaDesktop();
        }
        return instance;
    }
    
    private void initializeControllers() {
        usuarioController = new UsuarioController();
    }
    
    private void initializeViews() {
        // Initialize all views but don't show them yet
        loginView = new LoginView();
        homeView = new HomeView();
        movementsView = new MovementsView();
        accountsView = new AccountsView();
        menuView = new MenuView();
        configView = new ConfigView();
        cadastroView = new CadastroView();
        categoriasView = new CategoriasView();
        
        // Configure window closing behavior
        configureWindowClosing(loginView);
        configureWindowClosing(homeView);
        configureWindowClosing(movementsView);
        configureWindowClosing(accountsView);
        configureWindowClosing(menuView);
        configureWindowClosing(configView);
        configureWindowClosing(cadastroView);
        configureWindowClosing(categoriasView);
        
        // Initialize view integrator to connect views with controllers
        viewIntegrator = new ViewIntegrator(this);
    }
    
    private void configureWindowClosing(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    public void start() {
        try {
            // Test database connection
            if (!DatabaseManager.testConnection()) {
                JOptionPane.showMessageDialog(null, 
                    "Erro: Não foi possível conectar ao banco de dados!", 
                    "Erro de Conexão", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            
            // Set look and feel
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Use default look and feel if system LAF fails
            }
            
            // Show login view
            showLoginView();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erro ao iniciar aplicação: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    // Navigation methods
    public void showLoginView() {
        switchToView(loginView);
    }
    
    public void showHomeView() {
        switchToView(homeView);
    }
    
    public void showMovementsView() {
        switchToView(movementsView);
    }
    
    public void showAccountsView() {
        switchToView(accountsView);
    }
    
    public void showMenuView() {
        switchToView(menuView);
    }
    
    public void showConfigView() {
        switchToView(configView);
    }
    
    public void showCadastroView() {
        switchToView(cadastroView);
    }
    
    public void showCategoriasView() {
        switchToView(categoriasView);
    }
    
    private void switchToView(JFrame newView) {
        if (currentView != null) {
            currentView.setVisible(false);
        }
        currentView = newView;
        currentView.setVisible(true);
        currentView.toFront();
    }
    
    // Getter methods for controllers
    public UsuarioController getUsuarioController() {
        return usuarioController;
    }
    
    // Getter methods for views
    public LoginView getLoginView() {
        return loginView;
    }
    
    public HomeView getHomeView() {
        return homeView;
    }
    
    public MovementsView getMovementsView() {
        return movementsView;
    }
    
    public AccountsView getAccountsView() {
        return accountsView;
    }
    
    public MenuView getMenuView() {
        return menuView;
    }
    
    public ConfigView getConfigView() {
        return configView;
    }
    
    public CadastroView getCadastroView() {
        return cadastroView;
    }
    
    public CategoriasView getCategoriasView() {
        return categoriasView;
    }
    
    public void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
            currentView,
            "Deseja realmente sair do Finanza?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Logout if user is logged in
            if (usuarioController.isLogado()) {
                usuarioController.logout();
            }
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanzaDesktop app = FinanzaDesktop.getInstance();
            app.start();
        });
    }
}