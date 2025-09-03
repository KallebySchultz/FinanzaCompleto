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

/**
 * Tela de gerenciamento de contas
 */
public class AccountsFrame extends JFrame {
    private Usuario usuario;
    private ContaDao contaDao;
    private LancamentoDao lancamentoDao;

    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton closeButton;

    public AccountsFrame(Usuario usuario) {
        this.usuario = usuario;
        this.contaDao = new ContaDao();
        this.lancamentoDao = new LancamentoDao();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureFrame();
        loadAccounts();
    }

    private void initializeComponents() {
        // Tabela de contas
        String[] columnNames = {"ID", "Nome", "Saldo Inicial", "Saldo Atual"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };
        accountsTable = new JTable(tableModel);
        accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        accountsTable.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Botões
        addButton = new JButton("+ Nova Conta");
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

        // Título
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIUtils.BACKGROUND_COLOR);
        JLabel titleLabel = UIUtils.createTitleLabel("Gerenciar Contas");
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);

        // Tabela
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(closeButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupEventListeners() {
        addButton.addActionListener(this::addAccount);
        editButton.addActionListener(this::editAccount);
        deleteButton.addActionListener(this::deleteAccount);
        closeButton.addActionListener(e -> dispose());

        // Habilitar/desabilitar botões baseado na seleção
        accountsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = accountsTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        List<Conta> contas = contaDao.listarPorUsuario(usuario.getId());
        
        for (Conta conta : contas) {
            double saldoAtual = contaDao.calcularSaldoAtual(conta.getId());
            Object[] rowData = {
                conta.getId(),
                conta.getNome(),
                FormatUtils.formatarMoeda(conta.getSaldoInicial()),
                FormatUtils.formatarMoeda(saldoAtual)
            };
            tableModel.addRow(rowData);
        }

        // Desabilitar botões se não há seleção
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void addAccount(ActionEvent e) {
        AccountDialog dialog = new AccountDialog(this, null, usuario);
        dialog.setVisible(true);
        loadAccounts(); // Recarregar após adicionar
    }

    private void editAccount(ActionEvent e) {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int contaId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Conta conta = contaDao.buscarPorId(contaId);
        
        if (conta != null) {
            AccountDialog dialog = new AccountDialog(this, conta, usuario);
            dialog.setVisible(true);
            loadAccounts(); // Recarregar após editar
        }
    }

    private void deleteAccount(ActionEvent e) {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int contaId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nomeConta = (String) tableModel.getValueAt(selectedRow, 1);

        // Verificar se há lançamentos
        List<Lancamento> lancamentos = lancamentoDao.listarPorConta(contaId);
        if (!lancamentos.isEmpty()) {
            UIUtils.showError(this, "Não é possível excluir a conta \"" + nomeConta + 
                "\" pois ela possui " + lancamentos.size() + " transação(ões) associada(s).");
            return;
        }

        if (UIUtils.showConfirmation(this, "Deseja realmente excluir a conta \"" + nomeConta + "\"?")) {
            try {
                if (contaDao.deletar(contaId)) {
                    UIUtils.showSuccess(this, "Conta excluída com sucesso!");
                    loadAccounts();
                } else {
                    UIUtils.showError(this, "Erro ao excluir conta");
                }
            } catch (Exception ex) {
                UIUtils.showError(this, "Erro ao excluir conta: " + ex.getMessage());
            }
        }
    }

    private void configureFrame() {
        setTitle("Finanza Desktop - Gerenciar Contas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // Dialog para adicionar/editar conta
    private static class AccountDialog extends JDialog {
        private JTextField nomeField;
        private JTextField saldoField;
        private JButton salvarButton;
        private JButton cancelarButton;
        
        private Conta conta; // null para nova conta
        private Usuario usuario;
        private ContaDao contaDao;

        public AccountDialog(JFrame parent, Conta conta, Usuario usuario) {
            super(parent, conta == null ? "Nova Conta" : "Editar Conta", true);
            this.conta = conta;
            this.usuario = usuario;
            this.contaDao = new ContaDao();
            
            initializeComponents();
            setupLayout();
            setupEventListeners();
            configureDialog();
            
            if (conta != null) {
                loadAccountData();
            }
        }

        private void initializeComponents() {
            nomeField = new JTextField();
            UIUtils.configureTextField(nomeField);

            saldoField = new JTextField();
            UIUtils.configureTextField(saldoField);

            salvarButton = new JButton("Salvar");
            UIUtils.configureButton(salvarButton);

            cancelarButton = new JButton("Cancelar");
            UIUtils.configureSecondaryButton(cancelarButton);
        }

        private void setupLayout() {
            setLayout(new BorderLayout());

            JPanel mainPanel = UIUtils.createCard();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setPreferredSize(new Dimension(350, 250));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Título
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JLabel titleLabel = UIUtils.createTitleLabel(conta == null ? "Nova Conta" : "Editar Conta");
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(titleLabel, gbc);

            // Nome
            gbc.gridy++;
            gbc.gridwidth = 1;
            JLabel nomeLabel = new JLabel("Nome da Conta:");
            nomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel.add(nomeLabel, gbc);

            gbc.gridx = 1;
            mainPanel.add(nomeField, gbc);

            // Saldo inicial
            gbc.gridx = 0;
            gbc.gridy++;
            JLabel saldoLabel = new JLabel("Saldo Inicial:");
            saldoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel.add(saldoLabel, gbc);

            gbc.gridx = 1;
            mainPanel.add(saldoField, gbc);

            // Botões
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(salvarButton);
            buttonPanel.add(cancelarButton);
            mainPanel.add(buttonPanel, gbc);

            add(mainPanel, BorderLayout.CENTER);
        }

        private void setupEventListeners() {
            salvarButton.addActionListener(this::salvarConta);
            cancelarButton.addActionListener(e -> dispose());
        }

        private void loadAccountData() {
            nomeField.setText(conta.getNome());
            saldoField.setText(FormatUtils.formatarValorSemSimbolo(conta.getSaldoInicial()));
        }

        private void salvarConta(ActionEvent e) {
            String nome = nomeField.getText().trim();
            String saldoStr = saldoField.getText().trim();

            // Validações
            if (nome.isEmpty()) {
                UIUtils.showError(this, "Por favor, digite o nome da conta");
                nomeField.requestFocus();
                return;
            }

            double saldo = 0.0;
            if (!saldoStr.isEmpty()) {
                try {
                    saldo = FormatUtils.parseDouble(saldoStr);
                } catch (NumberFormatException ex) {
                    UIUtils.showError(this, "Saldo inválido. Use apenas números e vírgula/ponto para decimais");
                    saldoField.requestFocus();
                    return;
                }
            }

            try {
                if (conta == null) {
                    // Nova conta
                    Conta novaConta = new Conta(nome, saldo, usuario.getId());
                    int id = contaDao.inserir(novaConta);
                    
                    if (id > 0) {
                        UIUtils.showSuccess(this, "Conta criada com sucesso!");
                        dispose();
                    } else {
                        UIUtils.showError(this, "Erro ao criar conta");
                    }
                } else {
                    // Editar conta existente
                    conta.setNome(nome);
                    conta.setSaldoInicial(saldo);
                    
                    if (contaDao.atualizar(conta)) {
                        UIUtils.showSuccess(this, "Conta atualizada com sucesso!");
                        dispose();
                    } else {
                        UIUtils.showError(this, "Erro ao atualizar conta");
                    }
                }
            } catch (Exception ex) {
                UIUtils.showError(this, "Erro ao salvar conta: " + ex.getMessage());
            }
        }

        private void configureDialog() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setLocationRelativeTo(getParent());
            setResizable(false);
        }
    }
}