package view;

import controller.AuthController;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tela principal de administração para gerenciar usuários
 */
public class AdminDashboardView extends JFrame {
    private AuthController authController;
    private Usuario adminUsuario;
    
    private JTable usuariosTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton editButton;
    private JButton editProfileButton;
    private JButton logoutButton;
    
    public AdminDashboardView(AuthController authController, Usuario usuario) {
        this.authController = authController;
        this.adminUsuario = usuario;
        initComponents();
        setupUI();
        setupEvents();
        carregarUsuarios();
    }
    
    private void initComponents() {
        setTitle("Finanza Admin - Gerenciamento de Usuários");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Painel de Administração");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel("Admin: " + adminUsuario.getNome() + " (" + adminUsuario.getEmail() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel principal com tabela
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabela de usuários
        String[] columnNames = {"ID", "Nome", "Email", "Data de Criação"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usuariosTable = new JTable(tableModel);
        usuariosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usuariosTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(usuariosTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Atualizar Lista");
        editButton = new JButton("Editar Usuário Selecionado");
        editButton.setEnabled(false);
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(editButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer com botões de ações
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        editProfileButton = new JButton("Editar Meu Perfil");
        logoutButton = new JButton("Sair");
        
        footerPanel.add(editProfileButton);
        footerPanel.add(logoutButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void setupEvents() {
        // Atualizar lista
        refreshButton.addActionListener(e -> carregarUsuarios());
        
        // Editar usuário selecionado
        editButton.addActionListener(e -> editarUsuarioSelecionado());
        
        // Editar perfil do admin
        editProfileButton.addActionListener(e -> editarPerfil());
        
        // Logout
        logoutButton.addActionListener(e -> realizarLogout());
        
        // Habilitar botão de editar quando uma linha for selecionada
        usuariosTable.getSelectionModel().addListSelectionListener(e -> {
            editButton.setEnabled(usuariosTable.getSelectedRow() != -1);
        });
        
        // Double click para editar
        usuariosTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarUsuarioSelecionado();
                }
            }
        });
    }
    
    private void carregarUsuarios() {
        // Limpar tabela
        tableModel.setRowCount(0);
        
        SwingWorker<List<Usuario>, Void> worker = new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                // Obter lista de usuários do servidor
                return authController.listarUsuarios();
            }
            
            @Override
            protected void done() {
                try {
                    List<Usuario> usuarios = get();
                    if (usuarios != null) {
                        for (Usuario usuario : usuarios) {
                            Object[] row = {
                                usuario.getId(),
                                usuario.getNome(),
                                usuario.getEmail(),
                                usuario.getDataCriacao() != null ? usuario.getDataCriacao().toString() : "N/A"
                            };
                            tableModel.addRow(row);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Erro ao carregar usuários: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void editarUsuarioSelecionado() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        String userEmail = (String) tableModel.getValueAt(selectedRow, 2);
        
        Usuario usuario = new Usuario(userId, userName, userEmail);
        
        // Abrir diálogo de edição
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(this, authController, usuario);
        dialog.setVisible(true);
        
        // Se o diálogo foi confirmado, atualizar lista
        if (dialog.isConfirmado()) {
            carregarUsuarios();
        }
    }
    
    private void editarPerfil() {
        // Abrir diálogo de edição do próprio perfil
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(this, authController, adminUsuario);
        dialog.setVisible(true);
        
        // Se o diálogo foi confirmado, atualizar informações do header
        if (dialog.isConfirmado()) {
            // Recarregar dados do usuário logado
            Usuario usuarioAtualizado = authController.getUsuarioLogado();
            if (usuarioAtualizado != null) {
                adminUsuario = usuarioAtualizado;
                // Atualizar header
                setupUI();
            }
            carregarUsuarios();
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
}
