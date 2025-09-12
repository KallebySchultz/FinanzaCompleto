package view.panels;

import controller.FinanceController;
import model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel de gerenciamento de usuários para administradores
 */
public class UsuariosPanel extends JPanel {
    private FinanceController financeController;
    private Usuario usuario;
    
    // UI Components
    private JTable usuariosTable;
    private DefaultTableModel tableModel;
    private JButton novoUsuarioBtn;
    private JButton editarBtn;
    private JButton excluirBtn;
    private JButton atualizarBtn;
    
    // Demo data
    private List<Usuario> usuarios;
    
    public UsuariosPanel(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        initDemoData();
        setupUI();
        carregarUsuarios();
    }
    
    private void initDemoData() {
        usuarios = new ArrayList<>();
        usuarios.add(new Usuario(1, "Admin Demo", "admin@demo.com", Usuario.TipoUsuario.ADMIN));
        usuarios.add(new Usuario(2, "João Silva", "joao@email.com", Usuario.TipoUsuario.USUARIO));
        usuarios.add(new Usuario(3, "Maria Santos", "maria@email.com", Usuario.TipoUsuario.USUARIO));
        usuarios.add(new Usuario(4, "Pedro Costa", "pedro@email.com", Usuario.TipoUsuario.AGENTE));
        usuarios.add(new Usuario(5, "Ana Oliveira", "ana@email.com", Usuario.TipoUsuario.AGENTE));
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gerenciamento de Usuários");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        novoUsuarioBtn = new JButton("Novo Usuário");
        editarBtn = new JButton("Editar");
        excluirBtn = new JButton("Excluir");
        atualizarBtn = new JButton("Atualizar");
        
        // Style buttons
        styleButton(novoUsuarioBtn, new Color(39, 174, 96));
        styleButton(editarBtn, new Color(41, 128, 185));
        styleButton(excluirBtn, new Color(231, 76, 60));
        styleButton(atualizarBtn, new Color(230, 126, 34));
        
        buttonPanel.add(novoUsuarioBtn);
        buttonPanel.add(editarBtn);
        buttonPanel.add(excluirBtn);
        buttonPanel.add(atualizarBtn);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        setupTable();
        
        // Setup events
        setupEvents();
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void setupTable() {
        String[] columns = {"ID", "Nome", "Email", "Tipo", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usuariosTable = new JTable(tableModel);
        usuariosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usuariosTable.setFont(new Font("Arial", Font.PLAIN, 12));
        usuariosTable.setRowHeight(25);
        usuariosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        usuariosTable.getTableHeader().setBackground(new Color(52, 73, 94));
        usuariosTable.getTableHeader().setForeground(Color.WHITE);
        
        // Configure column widths
        usuariosTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        usuariosTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        usuariosTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        usuariosTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        usuariosTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(usuariosTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Lista de Usuários",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        novoUsuarioBtn.addActionListener(e -> abrirFormularioNovoUsuario());
        editarBtn.addActionListener(e -> editarUsuarioSelecionado());
        excluirBtn.addActionListener(e -> excluirUsuarioSelecionado());
        atualizarBtn.addActionListener(e -> carregarUsuarios());
    }
    
    private void carregarUsuarios() {
        tableModel.setRowCount(0);
        
        for (Usuario user : usuarios) {
            Object[] row = {
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getTipo().toString(),
                "Ativo"
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void abrirFormularioNovoUsuario() {
        UsuarioFormDialog dialog = new UsuarioFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Novo Usuário",
            null
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Usuario novoUsuario = dialog.getUsuario();
            novoUsuario.setId(getNextUsuarioId());
            usuarios.add(novoUsuario);
            carregarUsuarios();
            
            JOptionPane.showMessageDialog(this, 
                "Usuário criado com sucesso!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editarUsuarioSelecionado() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um usuário para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int usuarioId = (int) tableModel.getValueAt(selectedRow, 0);
        Usuario user = findUsuarioById(usuarioId);
        
        if (user != null) {
            UsuarioFormDialog dialog = new UsuarioFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Editar Usuário",
                user
            );
            
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Usuario usuarioEditado = dialog.getUsuario();
                // Update the existing user
                user.setNome(usuarioEditado.getNome());
                user.setEmail(usuarioEditado.getEmail());
                user.setTipo(usuarioEditado.getTipo());
                
                carregarUsuarios();
                
                JOptionPane.showMessageDialog(this, 
                    "Usuário atualizado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void excluirUsuarioSelecionado() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um usuário para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int usuarioId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Don't allow deleting current admin user
        if (usuarioId == usuario.getId()) {
            JOptionPane.showMessageDialog(this, 
                "Não é possível excluir o usuário atual", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este usuário?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            usuarios.removeIf(u -> u.getId() == usuarioId);
            carregarUsuarios();
            
            JOptionPane.showMessageDialog(this, 
                "Usuário excluído com sucesso!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private Usuario findUsuarioById(int id) {
        for (Usuario user : usuarios) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
    
    private int getNextUsuarioId() {
        int maxId = 0;
        for (Usuario user : usuarios) {
            if (user.getId() > maxId) {
                maxId = user.getId();
            }
        }
        return maxId + 1;
    }
    
    public void refresh() {
        carregarUsuarios();
    }
}