package view;

import controller.FinanceController;
import model.Movimentacao;
import model.Usuario;
import view.components.ResponsivePanel;
import view.components.MovimentacaoEditPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Tela de gerenciamento de movimentações com interface estilo ticket
 * Versão melhorada com layout responsivo e edição inline
 */
public class MovimentacoesViewImproved extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private JTable movimentacoesTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private MovimentacaoEditPanel editPanel;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    // Painéis responsivos
    private ResponsivePanel mainPanel;
    private ResponsivePanel headerPanel;
    private ResponsivePanel contentPanel;
    private ResponsivePanel sidePanel;
    private ResponsivePanel tablePanel;
    
    // Componentes de filtro
    private JTextField searchField;
    private JComboBox<String> tipoFilter;
    private JButton clearFiltersBtn;
    
    public MovimentacoesViewImproved(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        initComponents();
        setupLayout();
        setupEvents();
        carregarMovimentacoes();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Movimentações");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        
        // Criar painéis responsivos
        mainPanel = new ResponsivePanel(ResponsivePanel.PanelType.MAIN_CONTENT, new BorderLayout());
        headerPanel = new ResponsivePanel(ResponsivePanel.PanelType.HEADER, new BorderLayout());
        contentPanel = new ResponsivePanel(ResponsivePanel.PanelType.MAIN_CONTENT, new BorderLayout());
        sidePanel = new ResponsivePanel(ResponsivePanel.PanelType.SIDEBAR, new BorderLayout());
        tablePanel = new ResponsivePanel(ResponsivePanel.PanelType.CARD, new BorderLayout());
        
        // Painel de edição
        editPanel = new MovimentacaoEditPanel(financeController);
        editPanel.setOnOperationComplete(this::carregarMovimentacoes);
        
        // Tabela de movimentações
        String[] colunas = {"ID", "Data", "Descrição", "Tipo", "Valor", "Conta", "Categoria"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Double.class; // Valor para ordenação numérica
                return String.class;
            }
        };
        
        movimentacoesTable = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        movimentacoesTable.setRowSorter(tableSorter);
        
        // Configurar tabela
        movimentacoesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movimentacoesTable.getTableHeader().setReorderingAllowed(false);
        movimentacoesTable.setRowHeight(25);
        movimentacoesTable.setShowVerticalLines(false);
        movimentacoesTable.setGridColor(Color.LIGHT_GRAY);
        
        // Configurar larguras das colunas
        configureColumnWidths();
        
        // Componentes de filtro
        searchField = new JTextField(15);
        searchField.setToolTipText("Buscar por descrição...");
        tipoFilter = new JComboBox<>(new String[]{"Todos", "receita", "despesa"});
        clearFiltersBtn = ResponsivePanel.createStyledButton("Limpar", false);
    }
    
    private void configureColumnWidths() {
        movimentacoesTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        movimentacoesTable.getColumnModel().getColumn(0).setMaxWidth(80);
        movimentacoesTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Data
        movimentacoesTable.getColumnModel().getColumn(2).setPreferredWidth(250);  // Descrição
        movimentacoesTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Tipo
        movimentacoesTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Valor
        movimentacoesTable.getColumnModel().getColumn(5).setPreferredWidth(150);  // Conta
        movimentacoesTable.getColumnModel().getColumn(6).setPreferredWidth(150);  // Categoria
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel titleLabel = headerPanel.createStyledTitle("💰 Movimentações Financeiras");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton novaBtn = ResponsivePanel.createStyledButton("Nova Movimentação", true);
        JButton refreshBtn = ResponsivePanel.createStyledButton("Atualizar", false);
        novaBtn.addActionListener(e -> editPanel.novaMovimentacao());
        refreshBtn.addActionListener(e -> carregarMovimentacoes());
        
        headerButtonsPanel.add(refreshBtn);
        headerButtonsPanel.add(novaBtn);
        headerPanel.add(headerButtonsPanel, BorderLayout.EAST);
        
        // Filtros na parte superior da tabela
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Buscar:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Tipo:"));
        filterPanel.add(tipoFilter);
        filterPanel.add(clearFiltersBtn);
        
        // Tabela
        JScrollPane scrollPane = new JScrollPane(movimentacoesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Clique duplo em uma linha para editar"));
        tablePanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Lado direito - painel de edição
        sidePanel.add(editPanel, BorderLayout.CENTER);
        
        // Layout principal
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(sidePanel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Aplicar comportamento responsivo
        mainPanel.applyResponsiveBehavior(this);
        sidePanel.applyResponsiveBehavior(this);
    }
    
    private void setupEvents() {
        // Double-click para editar
        movimentacoesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarMovimentacaoSelecionada();
                }
            }
        });
        
        // Filtros
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
        });
        
        tipoFilter.addActionListener(e -> aplicarFiltros());
        clearFiltersBtn.addActionListener(e -> limparFiltros());
        
        // Context menu
        JPopupMenu contextMenu = createContextMenu();
        movimentacoesTable.setComponentPopupMenu(contextMenu);
    }
    
    private JPopupMenu createContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem editItem = new JMenuItem("Editar");
        editItem.addActionListener(e -> editarMovimentacaoSelecionada());
        menu.add(editItem);
        
        JMenuItem deleteItem = new JMenuItem("Excluir");
        deleteItem.addActionListener(e -> excluirMovimentacaoSelecionada());
        menu.add(deleteItem);
        
        menu.addSeparator();
        
        JMenuItem newItem = new JMenuItem("Nova Movimentação");
        newItem.addActionListener(e -> editPanel.novaMovimentacao());
        menu.add(newItem);
        
        return menu;
    }
    
    private void aplicarFiltros() {
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String searchText = searchField.getText().toLowerCase();
                String tipoSelecionado = (String) tipoFilter.getSelectedItem();
                
                // Filtro por descrição
                if (!searchText.isEmpty()) {
                    String descricao = entry.getStringValue(2).toLowerCase();
                    if (!descricao.contains(searchText)) {
                        return false;
                    }
                }
                
                // Filtro por tipo
                if (!"Todos".equals(tipoSelecionado)) {
                    String tipo = entry.getStringValue(3).toLowerCase();
                    if (!tipo.equals(tipoSelecionado)) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        
        tableSorter.setRowFilter(filter);
    }
    
    private void limparFiltros() {
        searchField.setText("");
        tipoFilter.setSelectedItem("Todos");
    }
    
    private void carregarMovimentacoes() {
        SwingWorker<List<Movimentacao>, Void> worker = new SwingWorker<List<Movimentacao>, Void>() {
            @Override
            protected List<Movimentacao> doInBackground() throws Exception {
                FinanceController.OperationResult<List<Movimentacao>> result = financeController.listarMovimentacoes();
                if (result.isSucesso()) {
                    return result.getDados();
                } else {
                    throw new Exception(result.getMensagem());
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Movimentacao> movimentacoes = get();
                    atualizarTabela(movimentacoes);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MovimentacoesViewImproved.this, 
                        "Erro ao carregar movimentações: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarTabela(List<Movimentacao> movimentacoes) {
        tableModel.setRowCount(0);
        
        for (Movimentacao mov : movimentacoes) {
            Object[] row = {
                mov.getId(),
                dateFormat.format(mov.getData()),
                mov.getDescricao() != null ? mov.getDescricao() : "Sem descrição",
                mov.getTipo().getDescricao(),
                mov.getValor(), // Valor numérico para ordenação
                mov.getNomeCategoria() != null ? mov.getNomeCategoria() : "Sem categoria",
                mov.getNomeConta() != null ? mov.getNomeConta() : "Sem conta"
            };
            tableModel.addRow(row);
        }
        
        // Aplicar formatação customizada para valores
        movimentacoesTable.getColumnModel().getColumn(4).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel();
            if (value instanceof Double) {
                double val = (Double) value;
                label.setText(currencyFormat.format(val));
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                
                // Cor baseada no tipo (receita = verde, despesa = vermelho)
                String tipo = (String) table.getValueAt(row, 3);
                if ("Receita".equals(tipo)) {
                    label.setForeground(new Color(40, 167, 69));
                } else {
                    label.setForeground(new Color(220, 53, 69));
                }
            }
            
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(table.getSelectionBackground());
            }
            
            return label;
        });
    }
    
    private void editarMovimentacaoSelecionada() {
        int selectedRow = movimentacoesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma movimentação para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Converter índice para modelo (considerando filtros)
        int modelRow = movimentacoesTable.convertRowIndexToModel(selectedRow);
        int movimentacaoId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Buscar movimentação completa
        FinanceController.OperationResult<List<Movimentacao>> result = financeController.listarMovimentacoes();
        if (result.isSucesso()) {
            Movimentacao movimentacao = null;
            for (Movimentacao mov : result.getDados()) {
                if (mov.getId() == movimentacaoId) {
                    movimentacao = mov;
                    break;
                }
            }
            
            if (movimentacao != null) {
                editPanel.editarMovimentacao(movimentacao);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Movimentação não encontrada", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados da movimentação: " + result.getMensagem(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirMovimentacaoSelecionada() {
        int selectedRow = movimentacoesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma movimentação para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = movimentacoesTable.convertRowIndexToModel(selectedRow);
        int movimentacaoId = (Integer) tableModel.getValueAt(modelRow, 0);
        String descricao = (String) tableModel.getValueAt(modelRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir a movimentação:\n" + descricao + "?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FinanceController.OperationResult<Void> result = financeController.removerMovimentacao(movimentacaoId);
            
            if (result.isSucesso()) {
                JOptionPane.showMessageDialog(this, 
                    "Movimentação excluída com sucesso", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                carregarMovimentacoes();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao excluir movimentação: " + result.getMensagem(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}