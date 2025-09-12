package view.components;

import controller.FinanceController;
import model.Conta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Painel de edição inline para contas
 */
public class ContaEditPanel extends ResponsivePanel {
    private FinanceController financeController;
    private Conta conta;
    private boolean isEditMode;
    
    // Campos do formulário
    private JTextField nomeField;
    private JComboBox<Conta.TipoConta> tipoCombo;
    private JTextField saldoInicialField;
    
    // Botões de ação
    private JButton salvarBtn;
    private JButton cancelarBtn;
    private JButton limparBtn;
    
    // Formatadores
    private NumberFormat currencyFormat;
    
    // Callback para quando a operação for concluída
    private Runnable onOperationComplete;
    
    public ContaEditPanel(FinanceController financeController) {
        super(PanelType.CARD, new BorderLayout());
        this.financeController = financeController;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        this.isEditMode = false;
        
        initComponents();
        setupLayout();
        setupEvents();
    }
    
    public void setOnOperationComplete(Runnable callback) {
        this.onOperationComplete = callback;
    }
    
    public void editarConta(Conta conta) {
        this.conta = conta;
        this.isEditMode = true;
        preencherFormulario();
        updateTitle();
    }
    
    public void novaConta() {
        this.conta = null;
        this.isEditMode = false;
        limparFormulario();
        updateTitle();
    }
    
    private void initComponents() {
        // Nome
        nomeField = new JTextField(20);
        nomeField.setToolTipText("Nome da conta");
        
        // Tipo
        tipoCombo = new JComboBox<>(Conta.TipoConta.values());
        
        // Saldo inicial
        saldoInicialField = new JTextField(15);
        saldoInicialField.setToolTipText("Ex: 1000.50");
        
        // Botões
        salvarBtn = ResponsivePanel.createStyledButton("Salvar", true);
        cancelarBtn = ResponsivePanel.createStyledButton("Cancelar", false);
        limparBtn = ResponsivePanel.createStyledButton("Limpar", false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = createStyledTitle("Nova Conta");
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Formulário principal
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        formPanel.add(nomeField, gbc);
        
        // Tipo
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(tipoCombo, gbc);
        
        // Saldo inicial
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Saldo Inicial:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(saldoInicialField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(limparBtn);
        buttonPanel.add(cancelarBtn);
        buttonPanel.add(salvarBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEvents() {
        salvarBtn.addActionListener(this::salvarConta);
        cancelarBtn.addActionListener(e -> {
            limparFormulario();
            if (onOperationComplete != null) {
                onOperationComplete.run();
            }
        });
        limparBtn.addActionListener(e -> limparFormulario());
    }
    
    private void preencherFormulario() {
        if (conta == null) return;
        
        nomeField.setText(conta.getNome());
        tipoCombo.setSelectedItem(conta.getTipo());
        saldoInicialField.setText(String.valueOf(conta.getSaldoInicial()));
    }
    
    private void limparFormulario() {
        nomeField.setText("");
        tipoCombo.setSelectedIndex(0);
        saldoInicialField.setText("");
        conta = null;
        isEditMode = false;
        updateTitle();
    }
    
    private void updateTitle() {
        Component titleComponent = ((JPanel) getComponent(0)).getComponent(0);
        if (titleComponent instanceof JLabel) {
            JLabel titleLabel = (JLabel) titleComponent;
            titleLabel.setText(isEditMode ? "Editar Conta" : "Nova Conta");
        }
    }
    
    private void salvarConta(ActionEvent e) {
        try {
            // Validar campos
            if (nomeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome é obrigatório", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (saldoInicialField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O saldo inicial é obrigatório", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Preparar dados
            String nome = nomeField.getText().trim();
            Conta.TipoConta tipo = (Conta.TipoConta) tipoCombo.getSelectedItem();
            double saldoInicial = Double.parseDouble(saldoInicialField.getText().replace(",", "."));
            
            // Salvar
            FinanceController.OperationResult<Void> result;
            if (isEditMode && conta != null) {
                result = financeController.atualizarConta(conta.getId(), nome, tipo, saldoInicial);
            } else {
                FinanceController.OperationResult<Integer> addResult = financeController.adicionarConta(nome, tipo, saldoInicial);
                result = new FinanceController.OperationResult<>(addResult.isSucesso(), addResult.getMensagem(), null);
            }
            
            if (result.isSucesso()) {
                JOptionPane.showMessageDialog(this,
                    isEditMode ? "Conta atualizada com sucesso!" : "Conta criada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                if (onOperationComplete != null) {
                    onOperationComplete.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao salvar conta: " + result.getMensagem(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Saldo inicial inválido", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}