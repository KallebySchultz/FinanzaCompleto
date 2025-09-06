package view;

import controller.FinanceController;
import model.Categoria;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela de gerenciamento de categorias
 */
public class CategoriasView extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private JTable categoriasTable;
    private DefaultTableModel tableModel;
    
    public CategoriasView(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        
        initComponents();
        setupUI();
        carregarCategorias();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Gerenciar Categorias");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gerenciamento de Categorias");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton novaCategoriaBtn = new JButton("Nova Categoria");
        JButton editarBtn = new JButton("Editar");
        JButton excluirBtn = new JButton("Excluir");
        JButton atualizarBtn = new JButton("Atualizar");
        
        novaCategoriaBtn.setPreferredSize(new Dimension(130, 30));
        editarBtn.setPreferredSize(new Dimension(100, 30));
        excluirBtn.setPreferredSize(new Dimension(100, 30));
        atualizarBtn.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(novaCategoriaBtn);
        buttonPanel.add(editarBtn);
        buttonPanel.add(excluirBtn);
        buttonPanel.add(atualizarBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Tabela de categorias
        String[] colunas = {"ID", "Nome", "Tipo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        categoriasTable = new JTable(tableModel);
        categoriasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(categoriasTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pronto");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Eventos
        novaCategoriaBtn.addActionListener(e -> abrirFormularioNovaCategoria());
        editarBtn.addActionListener(e -> editarCategoriaSelecionada());
        excluirBtn.addActionListener(e -> excluirCategoriaSelecionada());
        atualizarBtn.addActionListener(e -> carregarCategorias());
    }
    
    private void carregarCategorias() {
        SwingWorker<List<Categoria>, Void> worker = new SwingWorker<List<Categoria>, Void>() {
            @Override
            protected List<Categoria> doInBackground() throws Exception {
                FinanceController.OperationResult<List<Categoria>> result = financeController.listarCategorias();
                if (result.isSucesso()) {
                    return result.getDados();
                } else {
                    throw new Exception(result.getMensagem());
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Categoria> categorias = get();
                    atualizarTabela(categorias);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CategoriasView.this, 
                        "Erro ao carregar categorias: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarTabela(List<Categoria> categorias) {
        tableModel.setRowCount(0);
        
        for (Categoria categoria : categorias) {
            Object[] row = {
                categoria.getId(),
                categoria.getNome(),
                categoria.getTipo().getDescricao()
            };
            tableModel.addRow(row);
        }
    }
    
    private void abrirFormularioNovaCategoria() {
        CategoriaFormDialog dialog = new CategoriaFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            "Nova Categoria", 
            null
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            criarCategoria(dialog.getNomeCategoria(), dialog.getTipoCategoria());
        }
    }
    
    private void criarCategoria(String nome, Categoria.TipoCategoria tipo) {
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                FinanceController.OperationResult<Integer> result = financeController.adicionarCategoria(nome, tipo);
                if (result.isSucesso()) {
                    return result.getDados();
                } else {
                    throw new Exception(result.getMensagem());
                }
            }
            
            @Override
            protected void done() {
                try {
                    Integer id = get();
                    JOptionPane.showMessageDialog(CategoriasView.this, 
                        "Categoria criada com sucesso!", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    carregarCategorias();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CategoriasView.this, 
                        "Erro ao criar categoria: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void editarCategoriaSelecionada() {
        int selectedRow = categoriasTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma categoria para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int categoriaId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nome = (String) tableModel.getValueAt(selectedRow, 1);
        String tipoStr = (String) tableModel.getValueAt(selectedRow, 2);
        
        Categoria.TipoCategoria tipo = "Receita".equals(tipoStr) ? 
            Categoria.TipoCategoria.RECEITA : Categoria.TipoCategoria.DESPESA;
        Categoria categoria = new Categoria(categoriaId, nome, tipo);
        
        CategoriaFormDialog dialog = new CategoriaFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            "Editar Categoria", 
            categoria
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            atualizarCategoria(categoriaId, dialog.getNomeCategoria(), dialog.getTipoCategoria());
        }
    }
    
    private void atualizarCategoria(int id, String nome, Categoria.TipoCategoria tipo) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                FinanceController.OperationResult<Void> result = financeController.atualizarCategoria(id, nome, tipo);
                if (!result.isSucesso()) {
                    throw new Exception(result.getMensagem());
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(CategoriasView.this, 
                        "Categoria atualizada com sucesso", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    carregarCategorias();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CategoriasView.this, 
                        "Erro ao atualizar categoria: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void excluirCategoriaSelecionada() {
        int selectedRow = categoriasTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma categoria para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int categoriaId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nome = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir a categoria:\n" + nome + "?\n\n" +
            "ATENÇÃO: Todas as movimentações associadas a esta categoria também serão afetadas.", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    FinanceController.OperationResult<Void> result = financeController.removerCategoria(categoriaId);
                    if (!result.isSucesso()) {
                        throw new Exception(result.getMensagem());
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(CategoriasView.this, 
                            "Categoria excluída com sucesso", 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        carregarCategorias();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(CategoriasView.this, 
                            "Erro ao excluir categoria: " + e.getMessage(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            worker.execute();
        }
    }
}