package com.finanza.desktop.ui;

import com.finanza.desktop.model.*;
import com.finanza.desktop.database.*;
import com.finanza.desktop.util.UIUtils;
import com.finanza.desktop.util.FormatUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Calendar;

/**
 * Tela de movimentações/transações
 */
public class MovementsFrame extends JFrame {
    private Usuario usuario;
    private LancamentoDao lancamentoDao;
    private ContaDao contaDao;
    private CategoriaDao categoriaDao;

    private JTable movementsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JLabel totalReceitasLabel;
    private JLabel totalDespesasLabel;
    private JLabel saldoMesLabel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton closeButton;

    private Calendar currentMonth;

    public MovementsFrame(Usuario usuario) {
        this.usuario = usuario;
        this.lancamentoDao = new LancamentoDao();
        this.contaDao = new ContaDao();
        this.categoriaDao = new CategoriaDao();
        this.currentMonth = Calendar.getInstance();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureFrame();
        loadMovements();
    }

    private void initializeComponents() {
        // Combos de período
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                         "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        monthCombo = new JComboBox<>(meses);
        monthCombo.setSelectedIndex(currentMonth.get(Calendar.MONTH));

        yearCombo = new JComboBox<>();
        int currentYear = currentMonth.get(Calendar.YEAR);
        for (int i = currentYear - 3; i <= currentYear + 1; i++) {
            yearCombo.addItem(i);
        }
        yearCombo.setSelectedItem(currentYear);

        // Labels de resumo
        totalReceitasLabel = new JLabel("R$ 0,00");
        totalDespesasLabel = new JLabel("R$ 0,00");
        saldoMesLabel = new JLabel("R$ 0,00");

        // Tabela de movimentações
        String[] columnNames = {"Data", "Descrição", "Categoria", "Conta", "Tipo", "Valor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movementsTable = new JTable(tableModel);
        movementsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movementsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        movementsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        movementsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        movementsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        movementsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        movementsTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Botões
        addButton = new JButton("+ Nova Transação");
        UIUtils.configureButton(addButton);

        editButton = new JButton("Editar");
        UIUtils.configureSecondaryButton(editButton);

        deleteButton = new JButton("Excluir");
        deleteButton.setBackground(UIUtils.ERROR_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        deleteButton.setFocusPainted(false);

        closeButton = new JButton("Fechar");
        UIUtils.configureSecondaryButton(closeButton);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel superior com título e filtros
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        JLabel titleLabel = UIUtils.createTitleLabel("Movimentações");
        
        // Painel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        filterPanel.add(new JLabel("Período: "));
        filterPanel.add(monthCombo);
        filterPanel.add(yearCombo);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        // Painel de resumo
        JPanel summaryPanel = createSummaryPanel();

        // Tabela
        JScrollPane scrollPane = new JScrollPane(movementsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(closeButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(summaryPanel, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, summaryPanel, contentPanel);
        splitPane.setDividerLocation(120);
        splitPane.setResizeWeight(0.0);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        summaryPanel.setPreferredSize(new Dimension(0, 100));

        // Card de receitas
        JPanel receitaCard = UIUtils.createCard();
        receitaCard.setLayout(new BorderLayout());
        JLabel receitaTitleLabel = UIUtils.createSectionLabel("Total Receitas");
        totalReceitasLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalReceitasLabel.setForeground(UIUtils.ACCENT_COLOR);
        receitaCard.add(receitaTitleLabel, BorderLayout.NORTH);
        receitaCard.add(totalReceitasLabel, BorderLayout.CENTER);

        // Card de despesas
        JPanel despesaCard = UIUtils.createCard();
        despesaCard.setLayout(new BorderLayout());
        JLabel despesaTitleLabel = UIUtils.createSectionLabel("Total Despesas");
        totalDespesasLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalDespesasLabel.setForeground(UIUtils.ERROR_COLOR);
        despesaCard.add(despesaTitleLabel, BorderLayout.NORTH);
        despesaCard.add(totalDespesasLabel, BorderLayout.CENTER);

        // Card de saldo
        JPanel saldoCard = UIUtils.createCard();
        saldoCard.setLayout(new BorderLayout());
        JLabel saldoTitleLabel = UIUtils.createSectionLabel("Saldo do Período");
        saldoMesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        saldoMesLabel.setForeground(UIUtils.PRIMARY_BLUE);
        saldoCard.add(saldoTitleLabel, BorderLayout.NORTH);
        saldoCard.add(saldoMesLabel, BorderLayout.CENTER);

        summaryPanel.add(receitaCard);
        summaryPanel.add(despesaCard);
        summaryPanel.add(saldoCard);

        return summaryPanel;
    }

    private void setupEventListeners() {
        addButton.addActionListener(this::addTransaction);
        editButton.addActionListener(this::editTransaction);
        deleteButton.addActionListener(this::deleteTransaction);
        closeButton.addActionListener(e -> dispose());

        // Atualizar ao mudar período
        monthCombo.addActionListener(e -> loadMovements());
        yearCombo.addActionListener(e -> loadMovements());

        // Habilitar/desabilitar botões baseado na seleção
        movementsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = movementsTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });
    }

    private void loadMovements() {
        tableModel.setRowCount(0);
        
        // Calcular período selecionado
        int month = monthCombo.getSelectedIndex();
        int year = (Integer) yearCombo.getSelectedItem();
        
        Calendar startCal = Calendar.getInstance();
        startCal.set(year, month, 1, 0, 0, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(year, month, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        
        long startTime = startCal.getTimeInMillis();
        long endTime = endCal.getTimeInMillis();

        // Carregar lançamentos do período
        List<Lancamento> lancamentos = lancamentoDao.listarPorPeriodo(usuario.getId(), startTime, endTime);
        
        double totalReceitas = 0;
        double totalDespesas = 0;

        for (Lancamento lancamento : lancamentos) {
            // Buscar informações relacionadas
            Conta conta = contaDao.buscarPorId(lancamento.getContaId());
            Categoria categoria = categoriaDao.buscarPorId(lancamento.getCategoriaId());
            
            String nomeCategoria = categoria != null ? categoria.getNome() : "Sem categoria";
            String nomeConta = conta != null ? conta.getNome() : "Conta não encontrada";
            
            Object[] rowData = {
                FormatUtils.formatarData(lancamento.getData()),
                lancamento.getDescricao(),
                nomeCategoria,
                nomeConta,
                lancamento.getTipo().substring(0, 1).toUpperCase() + lancamento.getTipo().substring(1),
                FormatUtils.formatarMoeda(lancamento.getValor())
            };
            tableModel.addRow(rowData);

            // Somar totais
            if ("receita".equals(lancamento.getTipo())) {
                totalReceitas += lancamento.getValor();
            } else {
                totalDespesas += lancamento.getValor();
            }
        }

        // Atualizar resumo
        totalReceitasLabel.setText(FormatUtils.formatarMoeda(totalReceitas));
        totalDespesasLabel.setText(FormatUtils.formatarMoeda(totalDespesas));
        double saldo = totalReceitas - totalDespesas;
        saldoMesLabel.setText(FormatUtils.formatarMoeda(saldo));
        saldoMesLabel.setForeground(saldo >= 0 ? UIUtils.ACCENT_COLOR : UIUtils.ERROR_COLOR);

        // Desabilitar botões se não há seleção
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void addTransaction(ActionEvent e) {
        AddTransactionDialog dialog = new AddTransactionDialog(this, usuario);
        dialog.setVisible(true);
        loadMovements(); // Recarregar após adicionar
    }

    private void editTransaction(ActionEvent e) {
        int selectedRow = movementsTable.getSelectedRow();
        if (selectedRow == -1) return;

        // Encontrar o lançamento correspondente
        String dataStr = (String) tableModel.getValueAt(selectedRow, 0);
        String descricao = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Buscar lançamento por descrição e data (simplificado)
        List<Lancamento> lancamentos = lancamentoDao.listarPorUsuario(usuario.getId());
        Lancamento lancamentoSelecionado = null;
        
        for (Lancamento l : lancamentos) {
            if (l.getDescricao().equals(descricao) && 
                FormatUtils.formatarData(l.getData()).equals(dataStr)) {
                lancamentoSelecionado = l;
                break;
            }
        }
        
        if (lancamentoSelecionado != null) {
            EditTransactionDialog dialog = new EditTransactionDialog(this, lancamentoSelecionado, usuario);
            dialog.setVisible(true);
            loadMovements(); // Recarregar após editar
        }
    }

    private void deleteTransaction(ActionEvent e) {
        int selectedRow = movementsTable.getSelectedRow();
        if (selectedRow == -1) return;

        String descricao = (String) tableModel.getValueAt(selectedRow, 1);
        String valor = (String) tableModel.getValueAt(selectedRow, 5);

        if (UIUtils.showConfirmation(this, "Deseja realmente excluir a transação \"" + descricao + "\" de " + valor + "?")) {
            // Encontrar o lançamento correspondente
            String dataStr = (String) tableModel.getValueAt(selectedRow, 0);
            
            List<Lancamento> lancamentos = lancamentoDao.listarPorUsuario(usuario.getId());
            Lancamento lancamentoSelecionado = null;
            
            for (Lancamento l : lancamentos) {
                if (l.getDescricao().equals(descricao) && 
                    FormatUtils.formatarData(l.getData()).equals(dataStr)) {
                    lancamentoSelecionado = l;
                    break;
                }
            }
            
            if (lancamentoSelecionado != null) {
                try {
                    if (lancamentoDao.deletar(lancamentoSelecionado.getId())) {
                        UIUtils.showSuccess(this, "Transação excluída com sucesso!");
                        loadMovements();
                    } else {
                        UIUtils.showError(this, "Erro ao excluir transação");
                    }
                } catch (Exception ex) {
                    UIUtils.showError(this, "Erro ao excluir transação: " + ex.getMessage());
                }
            }
        }
    }

    private void configureFrame() {
        setTitle("Finanza Desktop - Movimentações");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // Dialog para editar transação (simplificado)
    private static class EditTransactionDialog extends JDialog {
        private Lancamento lancamento;
        private Usuario usuario;
        private LancamentoDao lancamentoDao;
        
        private JTextField descricaoField;
        private JTextField valorField;
        
        public EditTransactionDialog(JFrame parent, Lancamento lancamento, Usuario usuario) {
            super(parent, "Editar Transação", true);
            this.lancamento = lancamento;
            this.usuario = usuario;
            this.lancamentoDao = new LancamentoDao();
            
            initComponents();
            setupLayout();
            loadData();
        }
        
        private void initComponents() {
            descricaoField = new JTextField();
            UIUtils.configureTextField(descricaoField);
            
            valorField = new JTextField();
            UIUtils.configureTextField(valorField);
        }
        
        private void setupLayout() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Descrição:"), gbc);
            
            gbc.gridx = 1;
            add(descricaoField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Valor:"), gbc);
            
            gbc.gridx = 1;
            add(valorField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel();
            JButton salvarButton = new JButton("Salvar");
            JButton cancelarButton = new JButton("Cancelar");
            
            UIUtils.configureButton(salvarButton);
            UIUtils.configureSecondaryButton(cancelarButton);
            
            salvarButton.addActionListener(e -> salvar());
            cancelarButton.addActionListener(e -> dispose());
            
            buttonPanel.add(salvarButton);
            buttonPanel.add(cancelarButton);
            add(buttonPanel, gbc);
            
            pack();
            setLocationRelativeTo(getParent());
        }
        
        private void loadData() {
            descricaoField.setText(lancamento.getDescricao());
            valorField.setText(FormatUtils.formatarValorSemSimbolo(lancamento.getValor()));
        }
        
        private void salvar() {
            String descricao = descricaoField.getText().trim();
            String valorStr = valorField.getText().trim();
            
            if (descricao.isEmpty()) {
                UIUtils.showError(this, "Digite uma descrição");
                return;
            }
            
            try {
                double valor = FormatUtils.parseDouble(valorStr);
                if (valor <= 0) {
                    UIUtils.showError(this, "Valor deve ser maior que zero");
                    return;
                }
                
                lancamento.setDescricao(descricao);
                lancamento.setValor(valor);
                
                if (lancamentoDao.atualizar(lancamento)) {
                    UIUtils.showSuccess(this, "Transação atualizada!");
                    dispose();
                } else {
                    UIUtils.showError(this, "Erro ao atualizar");
                }
            } catch (Exception ex) {
                UIUtils.showError(this, "Valor inválido");
            }
        }
    }
}