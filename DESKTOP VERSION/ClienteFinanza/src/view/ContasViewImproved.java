package view;

import controller.FinanceController;
import model.Conta;
import model.Usuario;
import view.components.ResponsivePanel;
import view.components.ContaEditPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Tela de gerenciamento de contas com interface responsiva
 */
public class ContasViewImproved extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private JTable contasTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private ContaEditPanel editPanel;
    private NumberFormat currencyFormat;
    
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
    
    public ContasViewImproved(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        initComponents();
        setupLayout();
        setupEvents();
        carregarContas();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Contas");
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
        editPanel = new ContaEditPanel(financeController);
        editPanel.setOnOperationComplete(this::carregarContas);
        
        // Tabela de contas
        String[] colunas = {"ID", "Nome", "Tipo", "Saldo Inicial", "Saldo Atual", "Variação"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3 || column == 4 || column == 5) return Double.class;
                return String.class;
            }
        };
        
        contasTable = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        contasTable.setRowSorter(tableSorter);
        
        // Configurar tabela
        contasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contasTable.getTableHeader().setReorderingAllowed(false);
        contasTable.setRowHeight(25);
        contasTable.setShowVerticalLines(false);
        contasTable.setGridColor(Color.LIGHT_GRAY);
        
        // Configurar larguras das colunas
        configureColumnWidths();
        
        // Componentes de filtro
        searchField = new JTextField(15);
        searchField.setToolTipText("Buscar por nome...");
        tipoFilter = new JComboBox<>(new String[]{"Todos", "Corrente", "Poupança", "Cartão", "Investimento", "Dinheiro"});
        clearFiltersBtn = ResponsivePanel.createStyledButton("Limpar", false);
    }
    
    private void configureColumnWidths() {
        contasTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        contasTable.getColumnModel().getColumn(0).setMaxWidth(80);
        contasTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Nome
        contasTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Tipo
        contasTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Saldo Inicial
        contasTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Saldo Atual
        contasTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Variação
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel titleLabel = headerPanel.createStyledTitle("💳 Gerenciamento de Contas");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton novaBtn = ResponsivePanel.createStyledButton("Nova Conta", true);
        JButton refreshBtn = ResponsivePanel.createStyledButton("Atualizar", false);
        novaBtn.addActionListener(e -> editPanel.novaConta());
        refreshBtn.addActionListener(e -> carregarContas());
        
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
        JScrollPane scrollPane = new JScrollPane(contasTable);
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
        contasTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarContaSelecionada();
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
        contasTable.setComponentPopupMenu(contextMenu);
    }
    
    private JPopupMenu createContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem editItem = new JMenuItem("Editar");
        editItem.addActionListener(e -> editarContaSelecionada());
        menu.add(editItem);
        
        JMenuItem deleteItem = new JMenuItem("Excluir");
        deleteItem.addActionListener(e -> excluirContaSelecionada());
        menu.add(deleteItem);
        
        menu.addSeparator();
        
        JMenuItem newItem = new JMenuItem("Nova Conta");
        newItem.addActionListener(e -> editPanel.novaConta());
        menu.add(newItem);
        
        return menu;
    }
    
    private void aplicarFiltros() {
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String searchText = searchField.getText().toLowerCase();
                String tipoSelecionado = (String) tipoFilter.getSelectedItem();
                
                // Filtro por nome
                if (!searchText.isEmpty()) {
                    String nome = entry.getStringValue(1).toLowerCase();
                    if (!nome.contains(searchText)) {
                        return false;
                    }
                }
                
                // Filtro por tipo
                if (!"Todos".equals(tipoSelecionado)) {
                    String tipo = entry.getStringValue(2);
                    if (!tipo.contains(tipoSelecionado)) {
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
    
    private void carregarContas() {
        SwingWorker<List<Conta>, Void> worker = new SwingWorker<List<Conta>, Void>() {
            @Override
            protected List<Conta> doInBackground() throws Exception {
                FinanceController.OperationResult<List<Conta>> result = financeController.listarContas();
                if (result.isSucesso()) {
                    return result.getDados();
                } else {
                    throw new Exception(result.getMensagem());
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Conta> contas = get();
                    atualizarTabela(contas);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ContasViewImproved.this, 
                        "Erro ao carregar contas: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarTabela(List<Conta> contas) {
        tableModel.setRowCount(0);
        
        for (Conta conta : contas) {
            double variacao = conta.getSaldoAtual() - conta.getSaldoInicial();
            
            Object[] row = {
                conta.getId(),
                conta.getNome(),
                conta.getTipo().getDescricao(),
                conta.getSaldoInicial(),
                conta.getSaldoAtual(),
                variacao
            };
            tableModel.addRow(row);
        }
        
        // Aplicar formatação customizada para valores monetários
        setupCurrencyRenderers();
    }
    
    private void setupCurrencyRenderers() {
        // Renderer para saldo inicial
        contasTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel();
            if (value instanceof Double) {
                label.setText(currencyFormat.format((Double) value));
                label.setHorizontalAlignment(SwingConstants.RIGHT);
            }
            
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(table.getSelectionBackground());
            }
            
            return label;
        });
        
        // Renderer para saldo atual
        contasTable.getColumnModel().getColumn(4).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel();
            if (value instanceof Double) {
                double val = (Double) value;
                label.setText(currencyFormat.format(val));
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                
                // Cor baseada no valor
                if (val >= 0) {
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
        
        // Renderer para variação
        contasTable.getColumnModel().getColumn(5).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel();
            if (value instanceof Double) {
                double val = (Double) value;
                label.setText(currencyFormat.format(val));
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                
                // Cor baseada na variação
                if (val > 0) {
                    label.setForeground(new Color(40, 167, 69));
                } else if (val < 0) {
                    label.setForeground(new Color(220, 53, 69));
                } else {
                    label.setForeground(Color.GRAY);
                }
            }
            
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(table.getSelectionBackground());
            }
            
            return label;
        });
    }
    
    private void editarContaSelecionada() {
        int selectedRow = contasTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma conta para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Converter índice para modelo (considerando filtros)
        int modelRow = contasTable.convertRowIndexToModel(selectedRow);
        int contaId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Buscar conta completa
        FinanceController.OperationResult<List<Conta>> result = financeController.listarContas();
        if (result.isSucesso()) {
            Conta conta = null;
            for (Conta c : result.getDados()) {
                if (c.getId() == contaId) {
                    conta = c;
                    break;
                }
            }
            
            if (conta != null) {
                editPanel.editarConta(conta);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Conta não encontrada", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados da conta: " + result.getMensagem(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirContaSelecionada() {
        int selectedRow = contasTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma conta para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = contasTable.convertRowIndexToModel(selectedRow);
        int contaId = (Integer) tableModel.getValueAt(modelRow, 0);
        String nome = (String) tableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir a conta:\n" + nome + "?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FinanceController.OperationResult<Void> result = financeController.removerConta(contaId);
            
            if (result.isSucesso()) {
                JOptionPane.showMessageDialog(this, 
                    "Conta excluída com sucesso", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                carregarContas();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao excluir conta: " + result.getMensagem(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}