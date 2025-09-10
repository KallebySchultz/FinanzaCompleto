package view;

import controller.FinanceController;
import model.Categoria;
import model.Conta;
import model.Movimentacao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Dialog para criação/edição de movimentações financeiras
 */
public class MovimentacaoFormDialog extends JDialog {
    private FinanceController financeController;
    private JTextField valorField;
    private JComboBox<String> tipoCombo;
    private JTextField dataField;
    private JTextField descricaoField;
    private JComboBox<Conta> contaCombo;
    private JComboBox<Categoria> categoriaCombo;
    private JButton salvarBtn;
    private JButton cancelarBtn;
    
    private boolean confirmado = false;
    private Movimentacao movimentacao;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    public MovimentacaoFormDialog(Frame parent, String title, Movimentacao movimentacaoExistente, FinanceController financeController) {
        super(parent, title, true);
        this.movimentacao = movimentacaoExistente;
        this.financeController = financeController;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        initComponents();
        setupUI();
        setupEvents();
        carregarDados();
        
        if (movimentacaoExistente != null) {
            preencherFormulario(movimentacaoExistente);
        } else {
            // Definir data atual como padrão
            dataField.setText(dateFormat.format(new Date()));
        }
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel principal com formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Tipo da movimentação
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tipoCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        tipoCombo.addActionListener(e -> atualizarCategorias());
        formPanel.add(tipoCombo, gbc);
        
        // Valor
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Valor:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        valorField = new JTextField(15);
        valorField.setToolTipText("Ex: 150.50");
        formPanel.add(valorField, gbc);
        
        // Data
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Data:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel dataPanel = new JPanel(new BorderLayout(5, 0));
        dataField = new JTextField(15);
        dataField.setToolTipText("dd/MM/yyyy");
        JButton hojeBtn = new JButton("Hoje");
        hojeBtn.setPreferredSize(new Dimension(60, dataField.getPreferredSize().height));
        hojeBtn.addActionListener(e -> dataField.setText(dateFormat.format(new Date())));
        dataPanel.add(dataField, BorderLayout.CENTER);
        dataPanel.add(hojeBtn, BorderLayout.EAST);
        formPanel.add(dataPanel, gbc);
        
        // Descrição
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Descrição:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        descricaoField = new JTextField(15);
        formPanel.add(descricaoField, gbc);
        
        // Conta
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Conta:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contaCombo = new JComboBox<>();
        contaCombo.setRenderer(new ContaComboRenderer());
        formPanel.add(contaCombo, gbc);
        
        // Categoria
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Categoria:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        categoriaCombo = new JComboBox<>();
        categoriaCombo.setRenderer(new CategoriaComboRenderer());
        formPanel.add(categoriaCombo, gbc);
        
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
        salvarBtn.addActionListener(e -> salvarMovimentacao());
        cancelarBtn.addActionListener(e -> dispose());
        
        // Enter para salvar, Escape para cancelar
        getRootPane().setDefaultButton(salvarBtn);
        
        KeyStroke escapeStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void carregarDados() {
        carregarContas();
        atualizarCategorias();
    }
    
    private void carregarContas() {
        contaCombo.removeAllItems();
        
        FinanceController.OperationResult<List<Conta>> result = financeController.listarContas();
        if (result.isSucesso()) {
            for (Conta conta : result.getDados()) {
                contaCombo.addItem(conta);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar contas: " + result.getMensagem(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarCategorias() {
        categoriaCombo.removeAllItems();
        
        String tipoSelecionado = (String) tipoCombo.getSelectedItem();
        if (tipoSelecionado == null) return;
        
        Categoria.TipoCategoria tipo = "receita".equals(tipoSelecionado) ? 
            Categoria.TipoCategoria.RECEITA : Categoria.TipoCategoria.DESPESA;
        
        FinanceController.OperationResult<List<Categoria>> result = financeController.listarCategoriasPorTipo(tipo);
        if (result.isSucesso()) {
            for (Categoria categoria : result.getDados()) {
                categoriaCombo.addItem(categoria);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar categorias: " + result.getMensagem(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void preencherFormulario(Movimentacao mov) {
        valorField.setText(String.format("%.2f", mov.getValor()));
        tipoCombo.setSelectedItem(mov.getTipo().getValor());
        dataField.setText(dateFormat.format(mov.getData()));
        descricaoField.setText(mov.getDescricao() != null ? mov.getDescricao() : "");
        
        // Selecionar conta e categoria após carregamento
        SwingUtilities.invokeLater(() -> {
            // Encontrar e selecionar a conta
            for (int i = 0; i < contaCombo.getItemCount(); i++) {
                Conta conta = contaCombo.getItemAt(i);
                if (conta.getId() == mov.getIdConta()) {
                    contaCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Encontrar e selecionar a categoria
            for (int i = 0; i < categoriaCombo.getItemCount(); i++) {
                Categoria categoria = categoriaCombo.getItemAt(i);
                if (categoria.getId() == mov.getIdCategoria()) {
                    categoriaCombo.setSelectedIndex(i);
                    break;
                }
            }
        });
    }
    
    private void salvarMovimentacao() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            double valor = Double.parseDouble(valorField.getText().replace(",", "."));
            String tipo = (String) tipoCombo.getSelectedItem();
            Date data = dateFormat.parse(dataField.getText());
            String descricao = descricaoField.getText().trim();
            Conta conta = (Conta) contaCombo.getSelectedItem();
            Categoria categoria = (Categoria) categoriaCombo.getSelectedItem();
            
            Movimentacao.TipoMovimentacao tipoMovimentacao = "receita".equals(tipo) ? 
                Movimentacao.TipoMovimentacao.RECEITA : Movimentacao.TipoMovimentacao.DESPESA;
            
            FinanceController.OperationResult<?> result;
            
            if (movimentacao == null) {
                // Nova movimentação
                result = financeController.adicionarMovimentacao(
                    valor, new java.sql.Date(data.getTime()), descricao, 
                    tipoMovimentacao, conta.getId(), categoria.getId()
                );
            } else {
                // Atualizar movimentação existente
                result = financeController.atualizarMovimentacao(
                    movimentacao.getId(), valor, new java.sql.Date(data.getTime()), 
                    descricao, tipoMovimentacao, conta.getId(), categoria.getId()
                );
            }
            
            if (result.isSucesso()) {
                confirmado = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao salvar movimentação: " + result.getMensagem(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao processar dados: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarFormulario() {
        // Validar valor
        try {
            double valor = Double.parseDouble(valorField.getText().replace(",", "."));
            if (valor <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser maior que zero", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                valorField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            valorField.requestFocus();
            return false;
        }
        
        // Validar data
        try {
            dateFormat.parse(dataField.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            dataField.requestFocus();
            return false;
        }
        
        // Validar conta
        if (contaCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            contaCombo.requestFocus();
            return false;
        }
        
        // Validar categoria
        if (categoriaCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            categoriaCombo.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    // Renderer customizado para combo de contas
    private static class ContaComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Conta) {
                Conta conta = (Conta) value;
                setText(conta.getNome() + " (" + conta.getTipo().getDescricao() + ")");
            }
            
            return this;
        }
    }
    
    // Renderer customizado para combo de categorias
    private static class CategoriaComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Categoria) {
                Categoria categoria = (Categoria) value;
                setText(categoria.getNome());
            }
            
            return this;
        }
    }
}