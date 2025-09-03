package com.finanza.desktop.view;

import com.finanza.desktop.controller.AuthController;
import com.finanza.desktop.controller.FinanceController;
import com.finanza.desktop.model.Conta;
import com.finanza.desktop.model.Lancamento;
import com.finanza.desktop.ui.ModernUIHelper;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Tela de Movimentações
 * Gerenciamento de lançamentos financeiros
 */
public class MovementsView extends JPanel {
    
    // Cores do tema
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color POSITIVE_GREEN = ModernUIHelper.POSITIVE_GREEN;
    private static final Color NEGATIVE_RED = ModernUIHelper.NEGATIVE_RED;
    private static final Color WHITE = ModernUIHelper.WHITE;
    private static final Color GRAY = ModernUIHelper.GRAY;
    
    private AuthController authController;
    private FinanceController financeController;
    private JPanel contentScrollPanel;
    private JComboBox<String> typeFilter;
    private JComboBox<String> accountFilter;
    
    public MovementsView(AuthController authController, FinanceController financeController) {
        this.authController = authController;
        this.financeController = financeController;
        
        initializeComponents();
    }
    
    public void updateMovementsList() {
        if (contentScrollPanel != null) {
            contentScrollPanel.removeAll();
            loadMovementsContent();
            contentScrollPanel.revalidate();
            contentScrollPanel.repaint();
        }
        updateFilters();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(WHITE);
        
        // Header com título e botão adicionar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel(ModernUIHelper.ICON_MOVEMENTS + " Movimentações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout());
        rightPanel.setBackground(WHITE);
        
        JButton addTransactionButton = ModernUIHelper.createModernButton("Nova Transação", ModernUIHelper.ICON_ADD, POSITIVE_GREEN);
        addTransactionButton.addActionListener(e -> showAddTransactionDialog());
        rightPanel.add(addTransactionButton);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        
        JLabel filterLabel = new JLabel("Filtros:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterPanel.add(filterLabel);
        
        typeFilter = new JComboBox<>(new String[]{"Todos", "Receitas", "Despesas"});
        typeFilter.addActionListener(e -> updateMovementsList());
        filterPanel.add(typeFilter);
        
        accountFilter = new JComboBox<>();
        accountFilter.addItem("Todas as Contas");
        updateFilters();
        accountFilter.addActionListener(e -> updateMovementsList());
        filterPanel.add(accountFilter);
        
        // Container principal que inclui filtros e conteúdo
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(filterPanel, BorderLayout.NORTH);
        
        // Conteúdo das movimentações em scroll pane
        contentScrollPanel = new JPanel();
        contentScrollPanel.setLayout(new BoxLayout(contentScrollPanel, BoxLayout.Y_AXIS));
        contentScrollPanel.setBackground(WHITE);
        contentScrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        loadMovementsContent();
        
        JScrollPane scrollPane = new JScrollPane(contentScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private void updateFilters() {
        // Atualizar filtro de contas
        if (accountFilter != null) {
            String selectedAccount = (String) accountFilter.getSelectedItem();
            accountFilter.removeAllItems();
            accountFilter.addItem("Todas as Contas");
            
            if (authController.isLogado()) {
                List<Conta> contas = financeController.obterContas();
                if (contas != null) {
                    for (Conta conta : contas) {
                        accountFilter.addItem(conta.getNome());
                    }
                }
            }
            
            // Restaurar seleção anterior se possível
            if (selectedAccount != null) {
                accountFilter.setSelectedItem(selectedAccount);
            }
        }
    }
    
    private void loadMovementsContent() {
        // Buscar movimentações reais do banco de dados
        if (authController.isLogado()) {
            List<Lancamento> lancamentos = financeController.obterTodosLancamentos();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            
            if (lancamentos != null && !lancamentos.isEmpty()) {
                for (Lancamento lancamento : lancamentos) {
                    String valor = currencyFormat.format(lancamento.getValor());
                    String data = dateFormat.format(new Date(lancamento.getData()));
                    boolean isReceita = "receita".equals(lancamento.getTipo());
                    
                    JPanel transactionItem = createTransactionItemWithActions(
                        lancamento.getDescricao(), valor, data, isReceita, lancamento);
                    contentScrollPanel.add(transactionItem);
                    contentScrollPanel.add(Box.createVerticalStrut(10));
                }
            } else {
                JLabel noMovementsLabel = new JLabel("Nenhuma movimentação encontrada. Clique em 'Nova Transação' para começar.");
                noMovementsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                noMovementsLabel.setForeground(Color.GRAY);
                noMovementsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noMovementsLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
                contentScrollPanel.add(noMovementsLabel);
            }
        }
    }
    
    private JPanel createTransactionItemWithActions(String description, String value, String date, boolean isIncome, Lancamento lancamento) {
        JPanel item = ModernUIHelper.createCardPanel();
        item.setLayout(new BorderLayout());
        item.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        
        // Ícone e descrição
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(WHITE);
        
        String icon = isIncome ? ModernUIHelper.ICON_INCOME : ModernUIHelper.ICON_EXPENSE;
        Color iconColor = isIncome ? POSITIVE_GREEN : NEGATIVE_RED;
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        iconLabel.setForeground(iconColor);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        leftPanel.add(iconLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(WHITE);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descLabel.setForeground(Color.BLACK);
        textPanel.add(descLabel, BorderLayout.NORTH);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        textPanel.add(dateLabel, BorderLayout.SOUTH);
        
        leftPanel.add(textPanel);
        item.add(leftPanel, BorderLayout.WEST);
        
        // Painel central com valor
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(iconColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        item.add(valueLabel, BorderLayout.CENTER);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setBackground(WHITE);
        
        JButton editButton = ModernUIHelper.createModernButton("", ModernUIHelper.ICON_EDIT, ModernUIHelper.ACCENT_BLUE);
        editButton.setPreferredSize(new Dimension(35, 30));
        editButton.addActionListener(e -> showEditTransactionDialog(lancamento));
        actionPanel.add(editButton);
        
        JButton deleteButton = ModernUIHelper.createModernButton("", ModernUIHelper.ICON_DELETE, NEGATIVE_RED);
        deleteButton.setPreferredSize(new Dimension(35, 30));
        deleteButton.addActionListener(e -> deleteTransaction(lancamento));
        actionPanel.add(deleteButton);
        
        item.add(actionPanel, BorderLayout.EAST);
        
        return item;
    }
    
    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nova Transação", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo descrição
        JLabel descLabel = new JLabel("Descrição:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(descLabel, gbc);
        
        JTextField descField = ModernUIHelper.createModernTextField("Ex: Salário, Conta de luz...", 25);
        gbc.gridy = 1;
        contentPanel.add(descField, gbc);
        
        // Campo valor
        JLabel valueLabel = new JLabel("Valor:");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(valueLabel, gbc);
        
        JTextField valueField = ModernUIHelper.createModernTextField("0.00", 25);
        gbc.gridy = 3;
        contentPanel.add(valueField, gbc);
        
        // Tipo da transação
        JLabel typeLabel = new JLabel("Tipo:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        contentPanel.add(typeLabel, gbc);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 5;
        contentPanel.add(typeCombo, gbc);
        
        // Conta
        JLabel accountLabel = new JLabel("Conta:");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 6;
        contentPanel.add(accountLabel, gbc);
        
        JComboBox<Conta> accountCombo = new JComboBox<>();
        List<Conta> contas = financeController.obterContas();
        if (contas != null) {
            for (Conta conta : contas) {
                accountCombo.addItem(conta);
            }
        }
        accountCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 7;
        contentPanel.add(accountCombo, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", ModernUIHelper.ICON_CANCEL, GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", ModernUIHelper.ICON_SAVE, POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String descricao = descField.getText().trim();
            String valorText = valueField.getText().trim().replace(",", ".");
            String tipo = (String) typeCombo.getSelectedItem();
            Conta conta = (Conta) accountCombo.getSelectedItem();
            
            if (descricao.isEmpty()) {
                messageLabel.setText("Por favor, digite a descrição.");
                return;
            }
            
            if (conta == null) {
                messageLabel.setText("Por favor, selecione uma conta.");
                return;
            }
            
            try {
                double valor = Double.parseDouble(valorText);
                if (valor <= 0) {
                    messageLabel.setText("Valor deve ser maior que zero.");
                    return;
                }
                
                if (financeController.criarLancamento(descricao, valor, tipo, conta.getId(), null)) {
                    dialog.dispose();
                    updateMovementsList();
                    JOptionPane.showMessageDialog(this, "Transação criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    messageLabel.setText("Erro ao criar transação. Tente novamente.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Valor deve ser um número válido.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 9;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showEditTransactionDialog(Lancamento lancamento) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Transação", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo descrição
        JLabel descLabel = new JLabel("Descrição:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(descLabel, gbc);
        
        JTextField descField = ModernUIHelper.createModernTextField("", 25);
        descField.setText(lancamento.getDescricao());
        gbc.gridy = 1;
        contentPanel.add(descField, gbc);
        
        // Campo valor
        JLabel valueLabel = new JLabel("Valor:");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(valueLabel, gbc);
        
        JTextField valueField = ModernUIHelper.createModernTextField("", 25);
        valueField.setText(String.valueOf(lancamento.getValor()));
        gbc.gridy = 3;
        contentPanel.add(valueField, gbc);
        
        // Tipo da transação
        JLabel typeLabel = new JLabel("Tipo:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        contentPanel.add(typeLabel, gbc);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        typeCombo.setSelectedItem(lancamento.getTipo());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 5;
        contentPanel.add(typeCombo, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", ModernUIHelper.ICON_CANCEL, GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", ModernUIHelper.ICON_SAVE, POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String descricao = descField.getText().trim();
            String valorText = valueField.getText().trim().replace(",", ".");
            String tipo = (String) typeCombo.getSelectedItem();
            
            if (descricao.isEmpty()) {
                messageLabel.setText("Por favor, digite a descrição.");
                return;
            }
            
            try {
                double valor = Double.parseDouble(valorText);
                if (valor <= 0) {
                    messageLabel.setText("Valor deve ser maior que zero.");
                    return;
                }
                
                if (financeController.atualizarLancamento(lancamento.getId(), descricao, valor, tipo, lancamento.getContaId(), lancamento.getCategoriaId())) {
                    dialog.dispose();
                    updateMovementsList();
                    JOptionPane.showMessageDialog(this, "Transação atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    messageLabel.setText("Erro ao atualizar transação. Tente novamente.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Valor deve ser um número válido.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void deleteTransaction(Lancamento lancamento) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir a transação '" + lancamento.getDescricao() + "'?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (financeController.excluirLancamento(lancamento.getId())) {
                updateMovementsList();
                JOptionPane.showMessageDialog(this, "Transação excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir transação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}