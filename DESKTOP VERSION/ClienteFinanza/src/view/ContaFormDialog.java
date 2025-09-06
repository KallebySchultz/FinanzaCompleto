package view;

import model.Conta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog para criação/edição de contas
 */
public class ContaFormDialog extends JDialog {
    private JTextField nomeField;
    private JComboBox<Conta.TipoConta> tipoCombo;
    private JTextField saldoField;
    private JButton salvarBtn;
    private JButton cancelarBtn;
    
    private boolean confirmado = false;
    private Conta conta;
    
    public ContaFormDialog(Frame parent, String title, Conta contaExistente) {
        super(parent, title, true);
        this.conta = contaExistente;
        
        initComponents();
        setupUI();
        setupEvents();
        
        if (contaExistente != null) {
            preencherFormulario(contaExistente);
        }
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(getParent());
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel principal com formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Nome da conta
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(new JLabel("Nome da Conta:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nomeField = new JTextField(20);
        formPanel.add(nomeField, gbc);
        
        // Tipo da conta
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tipoCombo = new JComboBox<>(Conta.TipoConta.values());
        tipoCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Conta.TipoConta) {
                    setText(((Conta.TipoConta) value).getDescricao());
                }
                return this;
            }
        });
        formPanel.add(tipoCombo, gbc);
        
        // Saldo inicial
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Saldo Inicial:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        saldoField = new JTextField(20);
        saldoField.setText("0,00");
        formPanel.add(saldoField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        salvarBtn = new JButton("Salvar");
        cancelarBtn = new JButton("Cancelar");
        
        salvarBtn.setPreferredSize(new Dimension(100, 30));
        cancelarBtn.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(cancelarBtn);
        buttonPanel.add(salvarBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEvents() {
        salvarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarFormulario()) {
                    confirmarFormulario();
                }
            }
        });
        
        cancelarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter no campo nome foca no próximo campo
        nomeField.addActionListener(e -> tipoCombo.requestFocus());
        
        // Enter no campo saldo tenta salvar
        saldoField.addActionListener(e -> {
            if (validarFormulario()) {
                confirmarFormulario();
            }
        });
    }
    
    private void preencherFormulario(Conta conta) {
        nomeField.setText(conta.getNome());
        tipoCombo.setSelectedItem(conta.getTipo());
        saldoField.setText(String.format("%.2f", conta.getSaldoInicial()).replace(".", ","));
    }
    
    private boolean validarFormulario() {
        // Validar nome
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Nome da conta é obrigatório", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            nomeField.requestFocus();
            return false;
        }
        
        // Validar saldo
        String saldoStr = saldoField.getText().trim().replace(",", ".");
        try {
            double saldo = Double.parseDouble(saldoStr);
            // Saldo pode ser negativo para cartões de crédito
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Saldo inicial deve ser um número válido", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            saldoField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void confirmarFormulario() {
        try {
            String nome = nomeField.getText().trim();
            Conta.TipoConta tipo = (Conta.TipoConta) tipoCombo.getSelectedItem();
            double saldo = Double.parseDouble(saldoField.getText().trim().replace(",", "."));
            
            if (conta == null) {
                // Nova conta
                conta = new Conta(nome, tipo, saldo);
            } else {
                // Editar conta existente
                conta.setNome(nome);
                conta.setTipo(tipo);
                conta.setSaldoInicial(saldo);
            }
            
            confirmado = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao processar dados: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public Conta getConta() {
        return conta;
    }
}