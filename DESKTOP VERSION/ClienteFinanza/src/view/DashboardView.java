package view;

import controller.AuthController;
import controller.FinanceController;
import model.Usuario;
import view.components.NavigationPanel;
import view.panels.DashboardPanel;
import view.panels.TicketsPanel;
import view.panels.UsuariosPanel;
import view.panels.DepartamentosPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela principal do dashboard com navegação lateral
 */
public class DashboardView extends JFrame {
    private AuthController authController;
    private FinanceController financeController;
    private Usuario usuario;
    
    // UI Components
    private NavigationPanel navigationPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Content panels
    private DashboardPanel dashboardPanel;
    
    public DashboardView(AuthController authController, Usuario usuario) {
        this.authController = authController;
        this.financeController = new FinanceController(authController.getNetworkClient());
        this.usuario = usuario;
        initComponents();
        setupUI();
        setupEvents();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Sistema de Gestão Financeira");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Navigation panel on the left
        navigationPanel = new NavigationPanel(usuario);
        add(navigationPanel, BorderLayout.WEST);
        
        // Content area with CardLayout for switching between panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 249, 250));
        
        // Create content panels
        createContentPanels();
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(52, 73, 94));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Conectado ao servidor | Usuário: " + usuario.getEmail() + " (" + usuario.getTipo() + ")");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void createContentPanels() {
        // Dashboard panel
        dashboardPanel = new DashboardPanel(financeController, usuario);
        contentPanel.add(dashboardPanel, "dashboard");
        
        // Placeholder panels for other sections (will be replaced with actual panels)
        contentPanel.add(createPlaceholderPanel("Movimentações", "Aqui serão exibidas as movimentações financeiras"), "movimentacoes");
        contentPanel.add(createPlaceholderPanel("Contas", "Aqui será o gerenciamento de contas"), "contas");
        contentPanel.add(createPlaceholderPanel("Categorias", "Aqui será o gerenciamento de categorias"), "categorias");
        contentPanel.add(createPlaceholderPanel("Relatórios", "Aqui serão exibidos os relatórios"), "relatorios");
        contentPanel.add(createPlaceholderPanel("Exportação", "Aqui será a funcionalidade de exportação"), "exportacao");
        contentPanel.add(createPlaceholderPanel("Perfil", "Aqui serão as configurações do perfil"), "perfil");
        
        // Admin panels (only if user is admin)
        if (usuario.isAdmin()) {
            // Tickets panel - fully implemented
            TicketsPanel ticketsPanel = new TicketsPanel(financeController, usuario);
            contentPanel.add(ticketsPanel, "tickets");
            
            // Departments panel - fully implemented
            DepartamentosPanel departamentosPanel = new DepartamentosPanel(financeController, usuario);
            contentPanel.add(departamentosPanel, "departamentos");
            
            // Users panel - fully implemented
            UsuariosPanel usuariosPanel = new UsuariosPanel(financeController, usuario);
            contentPanel.add(usuariosPanel, "usuarios");
            
            contentPanel.add(createPlaceholderPanel("Agentes", "Aqui será o gerenciamento de agentes"), "agentes");
        }
        
        // Show dashboard by default
        cardLayout.show(contentPanel, "dashboard");
        navigationPanel.setSelectedButton(navigationPanel.getDashboardBtn());
    }
    
    private JPanel createPlaceholderPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descLabel.setForeground(new Color(127, 140, 141));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel("🚧");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEvents() {
        // Navigation button events
        navigationPanel.getDashboardBtn().addActionListener(e -> {
            cardLayout.show(contentPanel, "dashboard");
            navigationPanel.setSelectedButton(navigationPanel.getDashboardBtn());
            dashboardPanel.refresh();
        });
        
        navigationPanel.getMovimentacoesBtn().addActionListener(e -> {
            cardLayout.show(contentPanel, "movimentacoes");
            navigationPanel.setSelectedButton(navigationPanel.getMovimentacoesBtn());
        });
        
        navigationPanel.getContasBtn().addActionListener(e -> {
            cardLayout.show(contentPanel, "contas");
            navigationPanel.setSelectedButton(navigationPanel.getContasBtn());
        });
        
        navigationPanel.getCategoriasBtn().addActionListener(e -> {
            cardLayout.show(contentPanel, "categorias");
            navigationPanel.setSelectedButton(navigationPanel.getCategoriasBtn());
        });
        
        navigationPanel.getRelatoriosBtn().addActionListener(e -> {
            cardLayout.show(contentPanel, "relatorios");
            navigationPanel.setSelectedButton(navigationPanel.getRelatoriosBtn());
        });
        
        navigationPanel.getExportacaoBtn().addActionListener(e -> {
            cardLayout.show(contentPanel, "exportacao");
            navigationPanel.setSelectedButton(navigationPanel.getExportacaoBtn());
        });
        
        navigationPanel.getPerfilBtn().addActionListener(e -> {
            cardLayout.show(contentPanel, "perfil");
            navigationPanel.setSelectedButton(navigationPanel.getPerfilBtn());
        });
        
        // Admin button events (only if user is admin)
        if (usuario.isAdmin()) {
            navigationPanel.getTicketsBtn().addActionListener(e -> {
                cardLayout.show(contentPanel, "tickets");
                navigationPanel.setSelectedButton(navigationPanel.getTicketsBtn());
            });
            
            navigationPanel.getDepartamentosBtn().addActionListener(e -> {
                cardLayout.show(contentPanel, "departamentos");
                navigationPanel.setSelectedButton(navigationPanel.getDepartamentosBtn());
            });
            
            navigationPanel.getUsuariosBtn().addActionListener(e -> {
                cardLayout.show(contentPanel, "usuarios");
                navigationPanel.setSelectedButton(navigationPanel.getUsuariosBtn());
            });
            
            navigationPanel.getAgentesBtn().addActionListener(e -> {
                cardLayout.show(contentPanel, "agentes");
                navigationPanel.setSelectedButton(navigationPanel.getAgentesBtn());
            });
        }
        
        // Logout event
        navigationPanel.addPropertyChangeListener("logout", evt -> {
            if ((Boolean) evt.getNewValue()) {
                realizarLogout();
            }
        });
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
}
