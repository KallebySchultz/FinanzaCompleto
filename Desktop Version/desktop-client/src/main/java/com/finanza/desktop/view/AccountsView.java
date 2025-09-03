package com.finanza.desktop.view;

import com.finanza.desktop.controller.AuthController;
import com.finanza.desktop.controller.FinanceController;
import com.finanza.desktop.model.Conta;
import com.finanza.desktop.ui.ModernUIHelper;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

/**
 * Tela de Contas
 * Gerenciamento de contas financeiras do usuário
 */
public class AccountsView extends JPanel {
    
    // Cores do tema
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color POSITIVE_GREEN = ModernUIHelper.POSITIVE_GREEN;
    private static final Color NEGATIVE_RED = ModernUIHelper.NEGATIVE_RED;
    private static final Color WHITE = ModernUIHelper.WHITE;
    private static final Color GRAY = ModernUIHelper.GRAY;
    
    private AuthController authController;
    private FinanceController financeController;
    private JPanel contentScrollPanel;
    
    public AccountsView(AuthController authController, FinanceController financeController) {
        this.authController = authController;
        this.financeController = financeController;
        
        initializeComponents();
    }
    
    public void updateAccountsList() {
        if (contentScrollPanel != null) {
            contentScrollPanel.removeAll();
            loadAccountsContent();
            contentScrollPanel.revalidate();
            contentScrollPanel.repaint();
        }
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(WHITE);
        
        // Header com título e botão adicionar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel(ModernUIHelper.ICON_ACCOUNTS + " Contas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addAccountButton = ModernUIHelper.createModernButton("Nova Conta", ModernUIHelper.ICON_ADD, POSITIVE_GREEN);
        addAccountButton.addActionListener(e -> showAddAccountDialog());
        headerPanel.add(addAccountButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Conteúdo das contas em scroll pane
        contentScrollPanel = new JPanel();
        contentScrollPanel.setLayout(new BoxLayout(contentScrollPanel, BoxLayout.Y_AXIS));
        contentScrollPanel.setBackground(WHITE);
        contentScrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        loadAccountsContent();
        
        JScrollPane scrollPane = new JScrollPane(contentScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadAccountsContent() {
        // Buscar contas reais do banco de dados
        if (authController.isLogado()) {
            List<Conta> contas = financeController.obterContas();
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            
            if (contas != null && !contas.isEmpty()) {
                for (Conta conta : contas) {
                    String saldoFormatado = currencyFormat.format(conta.getSaldo());
                    Color iconColor = conta.getSaldo() >= 0 ? PRIMARY_DARK_BLUE : NEGATIVE_RED;
                    
                    JPanel accountItem = createAccountItemWithActions(conta.getNome(), saldoFormatado, iconColor, conta);
                    contentScrollPanel.add(accountItem);
                    contentScrollPanel.add(Box.createVerticalStrut(10));
                }
            } else {
                JLabel noAccountsLabel = new JLabel("Nenhuma conta cadastrada. Clique em 'Nova Conta' para começar.");
                noAccountsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                noAccountsLabel.setForeground(Color.GRAY);
                noAccountsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noAccountsLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
                contentScrollPanel.add(noAccountsLabel);
            }
        }
    }
    
    private JPanel createAccountItemWithActions(String name, String balance, Color iconColor, Conta conta) {
        JPanel item = ModernUIHelper.createCardPanel();
        item.setLayout(new BorderLayout());
        item.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Ícone e informações da conta
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(WHITE);
        
        JLabel iconLabel = new JLabel(ModernUIHelper.ICON_ACCOUNTS);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        iconLabel.setForeground(iconColor);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        leftPanel.add(iconLabel);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(WHITE);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.BLACK);
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        
        JLabel balanceLabel = new JLabel("Saldo: " + balance);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        balanceLabel.setForeground(iconColor);
        infoPanel.add(balanceLabel, BorderLayout.SOUTH);
        
        leftPanel.add(infoPanel);
        item.add(leftPanel, BorderLayout.WEST);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setBackground(WHITE);
        
        JButton editButton = ModernUIHelper.createModernButton("", ModernUIHelper.ICON_EDIT, ModernUIHelper.ACCENT_BLUE);
        editButton.setPreferredSize(new Dimension(40, 35));
        editButton.addActionListener(e -> showEditAccountDialog(conta));
        actionPanel.add(editButton);
        
        JButton deleteButton = ModernUIHelper.createModernButton("", ModernUIHelper.ICON_DELETE, NEGATIVE_RED);
        deleteButton.setPreferredSize(new Dimension(40, 35));
        deleteButton.addActionListener(e -> deleteAccount(conta));
        actionPanel.add(deleteButton);
        
        item.add(actionPanel, BorderLayout.EAST);
        
        return item;
    }
    
    private void showAddAccountDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nova Conta", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo nome
        JLabel nameLabel = new JLabel("Nome da Conta:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(nameLabel, gbc);
        
        JTextField nameField = ModernUIHelper.createModernTextField("Ex: Conta Corrente", 25);
        gbc.gridy = 1;
        contentPanel.add(nameField, gbc);
        
        // Campo saldo inicial
        JLabel balanceLabel = new JLabel("Saldo Inicial:");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(balanceLabel, gbc);
        
        JTextField balanceField = ModernUIHelper.createModernTextField("0.00", 25);
        gbc.gridy = 3;
        contentPanel.add(balanceField, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", ModernUIHelper.ICON_CANCEL, GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", ModernUIHelper.ICON_SAVE, POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            String saldoText = balanceField.getText().trim().replace(",", ".");
            
            if (nome.isEmpty()) {
                messageLabel.setText("Por favor, digite o nome da conta.");
                return;
            }
            
            try {
                double saldoInicial = Double.parseDouble(saldoText);
                
                if (financeController.criarConta(nome, saldoInicial)) {
                    dialog.dispose();
                    updateAccountsList();
                    JOptionPane.showMessageDialog(this, "Conta criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    messageLabel.setText("Erro ao criar conta. Tente novamente.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Saldo deve ser um número válido.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showEditAccountDialog(Conta conta) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Conta", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo nome
        JLabel nameLabel = new JLabel("Nome da Conta:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(nameLabel, gbc);
        
        JTextField nameField = ModernUIHelper.createModernTextField("", 25);
        nameField.setText(conta.getNome());
        gbc.gridy = 1;
        contentPanel.add(nameField, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", ModernUIHelper.ICON_CANCEL, GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", ModernUIHelper.ICON_SAVE, POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            
            if (nome.isEmpty()) {
                messageLabel.setText("Por favor, digite o nome da conta.");
                return;
            }
            
            if (financeController.atualizarConta(conta.getId(), nome, conta.getSaldo())) {
                dialog.dispose();
                updateAccountsList();
                JOptionPane.showMessageDialog(this, "Conta atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                messageLabel.setText("Erro ao atualizar conta. Tente novamente.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void deleteAccount(Conta conta) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir a conta '" + conta.getNome() + "'?\nTodas as transações associadas também serão excluídas.",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (financeController.excluirConta(conta.getId())) {
                updateAccountsList();
                JOptionPane.showMessageDialog(this, "Conta excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir conta.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}