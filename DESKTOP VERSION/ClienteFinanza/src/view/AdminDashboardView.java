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
    private JButton deleteButton;
    private JButton editProfileButton;
    private JButton logoutButton;
    private JTextField searchField;
    private JLabel totalUsersLabel;
    private List<Usuario> todosUsuarios;
    
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
        setSize(1000, 650);
        setLocationRelativeTo(null);
        todosUsuarios = new java.util.ArrayList<>();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 248, 255));
        
        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setOpaque(false);
        JLabel titleLabel = new JLabel("Painel de Administração");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalUsersLabel = new JLabel("Total de usuários: 0");
        totalUsersLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        headerLeft.add(titleLabel);
        headerLeft.add(totalUsersLabel);
        headerPanel.add(headerLeft, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel("Admin: " + adminUsuario.getNome() + " (" + adminUsuario.getEmail() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel principal com tabela
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchField = new JTextField(30);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Filtrar");
        searchButton.addActionListener(e -> filtrarUsuarios());
        searchPanel.add(searchButton);
        JButton clearButton = new JButton("Limpar");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            filtrarUsuarios();
        });
        searchPanel.add(clearButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
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
        usuariosTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(usuariosTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Atualizar Lista");
        editButton = new JButton("Editar");
        editButton.setEnabled(false);
        deleteButton = new JButton("Excluir");
        deleteButton.setEnabled(false);
        deleteButton.setForeground(Color.RED);
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
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
        
        // Excluir usuário selecionado
        deleteButton.addActionListener(e -> excluirUsuarioSelecionado());
        
        // Editar perfil do admin
        editProfileButton.addActionListener(e -> editarPerfil());
        
        // Logout
        logoutButton.addActionListener(e -> realizarLogout());
        
        // Habilitar botões quando uma linha for selecionada
        usuariosTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = usuariosTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
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
        
        // Enter no campo de busca
        searchField.addActionListener(e -> filtrarUsuarios());
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
                        todosUsuarios = usuarios;
                        totalUsersLabel.setText("Total de usuários: " + usuarios.size());
                        for (Usuario usuario : usuarios) {
                            Object[] row = {
                                usuario.getId(),
                                usuario.getNome(),
                                usuario.getEmail(),
                                "N/A" // Data de criação não disponível no modelo cliente
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
    
    private void filtrarUsuarios() {
        String busca = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        
        int count = 0;
        for (Usuario usuario : todosUsuarios) {
            if (busca.isEmpty() || 
                usuario.getNome().toLowerCase().contains(busca) ||
                usuario.getEmail().toLowerCase().contains(busca) ||
                String.valueOf(usuario.getId()).contains(busca)) {
                Object[] row = {
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    "N/A"
                };
                tableModel.addRow(row);
                count++;
            }
        }
        
        if (!busca.isEmpty()) {
            totalUsersLabel.setText("Total de usuários: " + todosUsuarios.size() + " (Filtrados: " + count + ")");
        } else {
            totalUsersLabel.setText("Total de usuários: " + todosUsuarios.size());
        }
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
    
    private void excluirUsuarioSelecionado() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        String userEmail = (String) tableModel.getValueAt(selectedRow, 2);
        
        // Não permitir excluir o próprio usuário logado
        if (userId == adminUsuario.getId()) {
            JOptionPane.showMessageDialog(this,
                "Não é possível excluir o próprio usuário logado.",
                "Ação Não Permitida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmação
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir o usuário:\n" +
            "Nome: " + userName + "\n" +
            "Email: " + userEmail + "\n\n" +
            "Esta ação não pode ser desfeita!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Excluir usuário
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authController.excluirUsuario(userId);
            }
            
            @Override
            protected void done() {
                try {
                    Boolean sucesso = get();
                    
                    if (sucesso) {
                        JOptionPane.showMessageDialog(AdminDashboardView.this,
                            "Usuário excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                        carregarUsuarios();
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboardView.this,
                            "Erro ao excluir usuário",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Erro ao excluir: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
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
