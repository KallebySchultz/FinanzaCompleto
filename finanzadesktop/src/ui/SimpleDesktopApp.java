package ui;

import controller.*;
import database.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * SimpleDesktopApp - Simplified desktop application to demonstrate integration
 * This shows how the existing Views should be connected to Controllers
 */
public class SimpleDesktopApp extends JFrame {
    private UsuarioController usuarioController;
    private ContaController contaController;
    private LancamentoController lancamentoController;
    private CategoriaController categoriaController;
    
    private JTextField emailField;
    private JPasswordField senhaField;
    private JLabel statusLabel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public SimpleDesktopApp() {
        initializeControllers();
        initializeGUI();
    }
    
    private void initializeControllers() {
        usuarioController = new UsuarioController();
        contaController = new ContaController();
        lancamentoController = new LancamentoController();
        categoriaController = new CategoriaController();
    }
    
    private void initializeGUI() {
        setTitle("Finanza Desktop - Sistema de Gestão Financeira");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create login panel
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");
        
        // Create dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        mainPanel.add(dashboardPanel, "DASHBOARD");
        
        add(mainPanel);
        
        // Show login panel first
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(27, 44, 92));
        
        // Title
        JLabel titleLabel = new JLabel("FINANZA", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        // Login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setText("admin@finanza.com"); // Default for testing
        formPanel.add(emailField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        senhaField = new JPasswordField(20);
        senhaField.setText("admin"); // Default for testing
        formPanel.add(senhaField, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("ENTRAR");
        loginButton.setBackground(new Color(27, 44, 92));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(this::handleLogin);
        formPanel.add(loginButton, gbc);
        
        // Status label
        gbc.gridy = 3;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);
        
        // Center the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(27, 44, 92));
        centerPanel.add(formPanel);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(27, 44, 92));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Finanza Desktop!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton logoutButton = new JButton("Sair");
        logoutButton.addActionListener(e -> {
            usuarioController.logout();
            cardLayout.show(mainPanel, "LOGIN");
            statusLabel.setText(" ");
        });
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Content area
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Summary cards
        contentPanel.add(createSummaryCard("Contas", "Gerencie suas contas", this::showAccountsInfo));
        contentPanel.add(createSummaryCard("Lançamentos", "Receitas e Despesas", this::showTransactionsInfo));
        contentPanel.add(createSummaryCard("Categorias", "Organize suas transações", this::showCategoriesInfo));
        contentPanel.add(createSummaryCard("Relatórios", "Visão geral financeira", this::showReportsInfo));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String description, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.GRAY);
        
        JButton actionButton = new JButton("Abrir");
        actionButton.addActionListener(action);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);
        
        card.add(textPanel, BorderLayout.CENTER);
        card.add(actionButton, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());
        
        if (email.isEmpty() || senha.isEmpty()) {
            statusLabel.setText("Preencha todos os campos");
            return;
        }
        
        boolean success = usuarioController.autenticar(email, senha);
        
        if (success) {
            statusLabel.setText("Login realizado com sucesso!");
            statusLabel.setForeground(Color.GREEN);
            
            // Switch to dashboard after a brief delay
            Timer timer = new Timer(1000, evt -> {
                cardLayout.show(mainPanel, "DASHBOARD");
                statusLabel.setText(" ");
                statusLabel.setForeground(Color.RED);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            statusLabel.setText("Email ou senha incorretos");
        }
    }
    
    private void showAccountsInfo(ActionEvent e) {
        if (usuarioController.isLogado()) {
            var contas = contaController.listarContasComSaldo(usuarioController.getUsuarioLogadoId());
            StringBuilder info = new StringBuilder("Suas Contas:\n\n");
            
            if (contas != null && !contas.isEmpty()) {
                for (var conta : contas) {
                    info.append(String.format("• %s: R$ %.2f\n", 
                        conta.getConta().getNome(), 
                        conta.getSaldoAtual()));
                }
            } else {
                info.append("Nenhuma conta encontrada.");
            }
            
            JOptionPane.showMessageDialog(this, info.toString(), "Contas", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showTransactionsInfo(ActionEvent e) {
        if (usuarioController.isLogado()) {
            var summary = lancamentoController.obterResumo(usuarioController.getUsuarioLogadoId());
            
            String info = String.format(
                "Resumo Financeiro:\n\n" +
                "Total de Receitas: R$ %.2f\n" +
                "Total de Despesas: R$ %.2f\n" +
                "Saldo: R$ %.2f\n" +
                "Total de Transações: %d",
                summary.getTotalReceitas(),
                summary.getTotalDespesas(),
                summary.getSaldo(),
                summary.getTotalTransacoes()
            );
            
            JOptionPane.showMessageDialog(this, info, "Lançamentos", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showCategoriesInfo(ActionEvent e) {
        var categorias = categoriaController.listarCategorias();
        StringBuilder info = new StringBuilder("Categorias Disponíveis:\n\n");
        
        if (categorias != null && !categorias.isEmpty()) {
            for (var categoria : categorias) {
                info.append(String.format("• %s (%s)\n", 
                    categoria.getNome(), 
                    categoria.getTipo()));
            }
        } else {
            info.append("Nenhuma categoria encontrada.");
        }
        
        JOptionPane.showMessageDialog(this, info.toString(), "Categorias", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showReportsInfo(ActionEvent e) {
        JOptionPane.showMessageDialog(this, 
            "Funcionalidade de relatórios será implementada em breve.\n\n" +
            "Esta tela demonstra como conectar as Views existentes\n" +
            "do NetBeans aos Controllers implementados.", 
            "Relatórios", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        // Test database connection
        if (!DatabaseManager.testConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao conectar com o banco de dados!\nVerifique se o SQLite está disponível.",
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Use default look and feel
            }
            
            new SimpleDesktopApp().setVisible(true);
        });
    }
}