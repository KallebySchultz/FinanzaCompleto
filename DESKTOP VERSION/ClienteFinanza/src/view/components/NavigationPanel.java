package view.components;

import model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * Painel de navegação lateral
 */
public class NavigationPanel extends JPanel {
    private Usuario usuario;
    private JPanel menuPanel;
    
    // Navigation buttons
    private JButton dashboardBtn;
    private JButton movimentacoesBtn;
    private JButton contasBtn;
    private JButton categoriasBtn;
    private JButton relatoriosBtn;
    private JButton exportacaoBtn;
    private JButton perfilBtn;
    
    // Admin buttons
    private JButton ticketsBtn;
    private JButton departamentosBtn;
    private JButton usuariosBtn;
    private JButton agentesBtn;
    
    public NavigationPanel(Usuario usuario) {
        this.usuario = usuario;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBackground(new Color(44, 62, 80)); // Dark blue-gray
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // User info panel
        JPanel userPanel = createUserPanel();
        add(userPanel, BorderLayout.NORTH);
        
        // Menu panel
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        
        createMenuButtons();
        
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        // Logout button at bottom
        JButton logoutBtn = createMenuButton("Logout", "Sair do sistema");
        logoutBtn.setBackground(new Color(231, 76, 60)); // Red
        logoutBtn.addActionListener(e -> {
            // This will be handled by the parent component
            firePropertyChange("logout", false, true);
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // User icon
        JLabel iconLabel = new JLabel("USER");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 16));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // User info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(usuario.getNome());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel(usuario.getTipo().toString());
        roleLabel.setForeground(new Color(189, 195, 199));
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(roleLabel);
        
        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void createMenuButtons() {
        // Common buttons
        dashboardBtn = createMenuButton("Dashboard", "Tela principal");
        movimentacoesBtn = createMenuButton("Movimentações", "Gerenciar movimentações financeiras");
        contasBtn = createMenuButton("Contas", "Gerenciar contas");
        categoriasBtn = createMenuButton("Categorias", "Gerenciar categorias");
        relatoriosBtn = createMenuButton("Relatórios", "Visualizar relatórios");
        exportacaoBtn = createMenuButton("Exportação", "Exportar dados");
        perfilBtn = createMenuButton("Perfil", "Configurações do perfil");
        
        menuPanel.add(dashboardBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(movimentacoesBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(contasBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(categoriasBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(relatoriosBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(exportacaoBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(perfilBtn);
        
        // Admin buttons
        if (usuario.isAdmin()) {
            menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            
            JLabel adminLabel = new JLabel("ADMINISTRAÇÃO");
            adminLabel.setForeground(new Color(241, 196, 15));
            adminLabel.setFont(new Font("Arial", Font.BOLD, 12));
            adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuPanel.add(adminLabel);
            
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            ticketsBtn = createMenuButton("Tickets", "Gerenciar chamados/tickets");
            departamentosBtn = createMenuButton("Departamentos", "Gerenciar departamentos");
            usuariosBtn = createMenuButton("Usuários", "Gerenciar usuários");
            agentesBtn = createMenuButton("Agentes", "Gerenciar agentes");
            
            menuPanel.add(ticketsBtn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            menuPanel.add(departamentosBtn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            menuPanel.add(usuariosBtn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            menuPanel.add(agentesBtn);
        }
    }
    
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 40));
        button.setPreferredSize(new Dimension(230, 40));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(69, 90, 111));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(new Color(41, 128, 185))) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
        });
        
        return button;
    }
    
    // Getter methods for buttons
    public JButton getDashboardBtn() { return dashboardBtn; }
    public JButton getMovimentacoesBtn() { return movimentacoesBtn; }
    public JButton getContasBtn() { return contasBtn; }
    public JButton getCategoriasBtn() { return categoriasBtn; }
    public JButton getRelatoriosBtn() { return relatoriosBtn; }
    public JButton getExportacaoBtn() { return exportacaoBtn; }
    public JButton getPerfilBtn() { return perfilBtn; }
    public JButton getTicketsBtn() { return ticketsBtn; }
    public JButton getDepartamentosBtn() { return departamentosBtn; }
    public JButton getUsuariosBtn() { return usuariosBtn; }
    public JButton getAgentesBtn() { return agentesBtn; }
    
    public void setSelectedButton(JButton selectedBtn) {
        // Reset all buttons
        resetButtonColors();
        
        // Highlight selected button
        if (selectedBtn != null) {
            selectedBtn.setBackground(new Color(41, 128, 185)); // Blue
        }
    }
    
    private void resetButtonColors() {
        for (Component comp : menuPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setBackground(new Color(52, 73, 94));
            }
        }
    }
}