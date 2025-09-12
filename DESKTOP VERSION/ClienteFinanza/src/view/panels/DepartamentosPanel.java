package view.panels;

import controller.FinanceController;
import model.Usuario;
import model.Departamento;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel de gerenciamento de departamentos
 */
public class DepartamentosPanel extends JPanel {
    private FinanceController financeController;
    private Usuario usuario;
    
    // UI Components
    private JTable departamentosTable;
    private DefaultTableModel tableModel;
    private JButton novoDepartamentoBtn;
    private JButton editarBtn;
    private JButton excluirBtn;
    private JButton atualizarBtn;
    
    // Demo data
    private List<Departamento> departamentos;
    
    public DepartamentosPanel(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        initDemoData();
        setupUI();
        carregarDepartamentos();
    }
    
    private void initDemoData() {
        departamentos = new ArrayList<>();
        departamentos.add(new Departamento(1, "TI", "Tecnologia da Informação", true));
        departamentos.add(new Departamento(2, "Financeiro", "Departamento Financeiro", true));
        departamentos.add(new Departamento(3, "RH", "Recursos Humanos", true));
        departamentos.add(new Departamento(4, "Suporte", "Suporte ao Cliente", true));
        departamentos.add(new Departamento(5, "Marketing", "Marketing e Vendas", false));
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gerenciamento de Departamentos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        novoDepartamentoBtn = new JButton("Novo Depto");
        editarBtn = new JButton("Editar");
        excluirBtn = new JButton("Excluir");
        atualizarBtn = new JButton("Atualizar");
        
        // Style buttons
        styleButton(novoDepartamentoBtn, new Color(39, 174, 96));
        styleButton(editarBtn, new Color(41, 128, 185));
        styleButton(excluirBtn, new Color(231, 76, 60));
        styleButton(atualizarBtn, new Color(230, 126, 34));
        
        buttonPanel.add(novoDepartamentoBtn);
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
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void setupTable() {
        String[] columns = {"ID", "Nome", "Descrição", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        departamentosTable = new JTable(tableModel);
        departamentosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departamentosTable.setFont(new Font("Arial", Font.PLAIN, 12));
        departamentosTable.setRowHeight(25);
        departamentosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        departamentosTable.getTableHeader().setBackground(new Color(52, 73, 94));
        departamentosTable.getTableHeader().setForeground(Color.WHITE);
        
        // Configure column widths
        departamentosTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        departamentosTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        departamentosTable.getColumnModel().getColumn(2).setPreferredWidth(350);
        departamentosTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(departamentosTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Lista de Departamentos",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        novoDepartamentoBtn.addActionListener(e -> abrirFormularioNovoDepartamento());
        editarBtn.addActionListener(e -> editarDepartamentoSelecionado());
        excluirBtn.addActionListener(e -> excluirDepartamentoSelecionado());
        atualizarBtn.addActionListener(e -> carregarDepartamentos());
    }
    
    private void carregarDepartamentos() {
        tableModel.setRowCount(0);
        
        for (Departamento dept : departamentos) {
            Object[] row = {
                dept.getId(),
                dept.getNome(),
                dept.getDescricao(),
                dept.isAtivo() ? "Ativo" : "Inativo"
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void abrirFormularioNovoDepartamento() {
        String nome = JOptionPane.showInputDialog(this, "Nome do Departamento:", "Novo Departamento", JOptionPane.QUESTION_MESSAGE);
        if (nome != null && !nome.trim().isEmpty()) {
            String descricao = JOptionPane.showInputDialog(this, "Descrição:", "Novo Departamento", JOptionPane.QUESTION_MESSAGE);
            if (descricao != null) {
                Departamento novoDept = new Departamento(getNextDepartamentoId(), nome.trim(), descricao.trim(), true);
                departamentos.add(novoDept);
                carregarDepartamentos();
                
                JOptionPane.showMessageDialog(this, 
                    "Departamento criado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void editarDepartamentoSelecionado() {
        int selectedRow = departamentosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um departamento para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int deptId = (int) tableModel.getValueAt(selectedRow, 0);
        Departamento dept = findDepartamentoById(deptId);
        
        if (dept != null) {
            String novoNome = (String) JOptionPane.showInputDialog(this, 
                "Nome do Departamento:", 
                "Editar Departamento", 
                JOptionPane.QUESTION_MESSAGE, 
                null, null, dept.getNome());
            
            if (novoNome != null && !novoNome.trim().isEmpty()) {
                String novaDescricao = (String) JOptionPane.showInputDialog(this, 
                    "Descrição:", 
                    "Editar Departamento", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, null, dept.getDescricao());
                
                if (novaDescricao != null) {
                    // Ask about status
                    String[] options = {"Ativo", "Inativo"};
                    int statusChoice = JOptionPane.showOptionDialog(this,
                        "Status do departamento:",
                        "Editar Departamento",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        dept.isAtivo() ? options[0] : options[1]);
                    
                    if (statusChoice != -1) {
                        dept.setNome(novoNome.trim());
                        dept.setDescricao(novaDescricao.trim());
                        dept.setAtivo(statusChoice == 0);
                        carregarDepartamentos();
                        
                        JOptionPane.showMessageDialog(this, 
                            "Departamento atualizado com sucesso!", 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }
    
    private void excluirDepartamentoSelecionado() {
        int selectedRow = departamentosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um departamento para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este departamento?\n" +
            "Esta ação pode afetar tickets associados.", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            int deptId = (int) tableModel.getValueAt(selectedRow, 0);
            departamentos.removeIf(d -> d.getId() == deptId);
            carregarDepartamentos();
            
            JOptionPane.showMessageDialog(this, 
                "Departamento excluído com sucesso!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private Departamento findDepartamentoById(int id) {
        for (Departamento dept : departamentos) {
            if (dept.getId() == id) {
                return dept;
            }
        }
        return null;
    }
    
    private int getNextDepartamentoId() {
        int maxId = 0;
        for (Departamento dept : departamentos) {
            if (dept.getId() > maxId) {
                maxId = dept.getId();
            }
        }
        return maxId + 1;
    }
    
    public void refresh() {
        carregarDepartamentos();
    }
}