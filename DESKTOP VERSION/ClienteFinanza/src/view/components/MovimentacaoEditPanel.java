package view.components;

import controller.FinanceController;
import model.Categoria;
import model.Conta;
import model.Movimentacao;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
 * Painel de edição inline para movimentações (estilo ticket)
 * Substitui o dialog modal por uma interface mais amigável
 */
public class MovimentacaoEditPanel extends ResponsivePanel {
    private FinanceController financeController;
    private Movimentacao movimentacao;
    private boolean isEditMode;
    
    // Campos do formulário
    private JTextField valorField;
    private JComboBox<String> tipoCombo;
    private JTextField dataField;
    private JTextField descricaoField;
    private JComboBox<Conta> contaCombo;
    private JComboBox<Categoria> categoriaCombo;
    
    // Botões de ação
    private JButton salvarBtn;
    private JButton cancelarBtn;
    private JButton limparBtn;
    
    // Formatadores
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    // Callback para quando a operação for concluída
    private Runnable onOperationComplete;
    
    public MovimentacaoEditPanel(FinanceController financeController) {
        super(PanelType.CARD, new BorderLayout());
        this.financeController = financeController;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.isEditMode = false;
        
        initComponents();
        setupLayout();
        setupEvents();
        carregarDados();
    }
    
    public void setOnOperationComplete(Runnable callback) {
        this.onOperationComplete = callback;
    }
    
    public void editarMovimentacao(Movimentacao movimentacao) {
        this.movimentacao = movimentacao;
        this.isEditMode = true;
        preencherFormulario();
        updateTitle();
    }
    
    public void novaMovimentacao() {
        this.movimentacao = null;
        this.isEditMode = false;
        limparFormulario();
        updateTitle();
    }
    
    private void initComponents() {
        // Tipo da movimentação
        tipoCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        tipoCombo.addActionListener(e -> atualizarCategorias());
        
        // Valor
        valorField = new JTextField(15);
        valorField.setToolTipText("Ex: 150.50");
        
        // Data
        dataField = new JTextField(15);
        dataField.setToolTipText("dd/MM/yyyy");
        dataField.setText(dateFormat.format(new Date()));
        
        // Descrição
        descricaoField = new JTextField(20);
        
        // Combos
        contaCombo = new JComboBox<>();
        categoriaCombo = new JComboBox<>();
        
        // Botões
        salvarBtn = ResponsivePanel.createStyledButton("Salvar", true);
        cancelarBtn = ResponsivePanel.createStyledButton("Cancelar", false);
        limparBtn = ResponsivePanel.createStyledButton("Limpar", false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = createStyledTitle("Nova Movimentação");
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Formulário principal
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Linha 1: Tipo e Data
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(tipoCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Data:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        
        JPanel dataPanel = new JPanel(new BorderLayout(5, 0));
        JButton hojeBtn = new JButton("Hoje");
        hojeBtn.setPreferredSize(new Dimension(60, dataField.getPreferredSize().height));
        hojeBtn.addActionListener(e -> dataField.setText(dateFormat.format(new Date())));
        dataPanel.add(dataField, BorderLayout.CENTER);
        dataPanel.add(hojeBtn, BorderLayout.EAST);
        formPanel.add(dataPanel, gbc);
        
        // Linha 2: Valor e Descrição
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Valor:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(valorField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(descricaoField, gbc);
        
        // Linha 3: Conta e Categoria
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Conta:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(contaCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(categoriaCombo, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(limparBtn);
        buttonPanel.add(cancelarBtn);
        buttonPanel.add(salvarBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEvents() {
        salvarBtn.addActionListener(this::salvarMovimentacao);
        cancelarBtn.addActionListener(e -> {
            limparFormulario();
            if (onOperationComplete != null) {
                onOperationComplete.run();
            }
        });
        limparBtn.addActionListener(e -> limparFormulario());
    }
    
    private void carregarDados() {
        carregarContas();
        atualizarCategorias();
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
                    contaCombo.removeAllItems();
                    for (Conta conta : contas) {
                        contaCombo.addItem(conta);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MovimentacaoEditPanel.this,
                        "Erro ao carregar contas: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void atualizarCategorias() {
        String tipoSelecionado = (String) tipoCombo.getSelectedItem();
        if (tipoSelecionado == null) return;
        
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
                    List<Categoria> todasCategorias = get();
                    categoriaCombo.removeAllItems();
                    
                    for (Categoria categoria : todasCategorias) {
                        if (categoria.getTipo().name().toLowerCase().equals(tipoSelecionado)) {
                            categoriaCombo.addItem(categoria);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MovimentacaoEditPanel.this,
                        "Erro ao carregar categorias: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void preencherFormulario() {
        if (movimentacao == null) return;
        
        tipoCombo.setSelectedItem(movimentacao.getTipo().name().toLowerCase());
        valorField.setText(String.valueOf(movimentacao.getValor()));
        dataField.setText(dateFormat.format(movimentacao.getData()));
        descricaoField.setText(movimentacao.getDescricao());
        
        // Selecionar conta e categoria
        for (int i = 0; i < contaCombo.getItemCount(); i++) {
            Conta conta = contaCombo.getItemAt(i);
            if (conta.getId() == movimentacao.getIdConta()) {
                contaCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Aguardar categorias serem carregadas
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < categoriaCombo.getItemCount(); i++) {
                Categoria categoria = categoriaCombo.getItemAt(i);
                if (categoria.getId() == movimentacao.getIdCategoria()) {
                    categoriaCombo.setSelectedIndex(i);
                    break;
                }
            }
        });
    }
    
    private void limparFormulario() {
        tipoCombo.setSelectedIndex(0);
        valorField.setText("");
        dataField.setText(dateFormat.format(new Date()));
        descricaoField.setText("");
        if (contaCombo.getItemCount() > 0) {
            contaCombo.setSelectedIndex(0);
        }
        movimentacao = null;
        isEditMode = false;
        updateTitle();
    }
    
    private void updateTitle() {
        Component titleComponent = ((JPanel) getComponent(0)).getComponent(0);
        if (titleComponent instanceof JLabel) {
            JLabel titleLabel = (JLabel) titleComponent;
            titleLabel.setText(isEditMode ? "Editar Movimentação" : "Nova Movimentação");
        }
    }
    
    private void salvarMovimentacao(ActionEvent e) {
        try {
            // Validar campos
            if (valorField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O valor é obrigatório", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (descricaoField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A descrição é obrigatória", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Preparar dados
            double valor = Double.parseDouble(valorField.getText().replace(",", "."));
            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.valueOf(((String) tipoCombo.getSelectedItem()).toUpperCase());
            java.util.Date utilDate = dateFormat.parse(dataField.getText());
            java.sql.Date data = new java.sql.Date(utilDate.getTime());
            String descricao = descricaoField.getText().trim();
            
            Conta contaSelecionada = (Conta) contaCombo.getSelectedItem();
            Categoria categoriaSelecionada = (Categoria) categoriaCombo.getSelectedItem();
            
            if (contaSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma conta", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (categoriaSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma categoria", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int idConta = contaSelecionada.getId();
            int idCategoria = categoriaSelecionada.getId();
            
            // Salvar
            FinanceController.OperationResult<Void> result;
            if (isEditMode && movimentacao != null) {
                result = financeController.atualizarMovimentacao(movimentacao.getId(), valor, data, descricao, tipo, idConta, idCategoria);
            } else {
                FinanceController.OperationResult<Integer> addResult = financeController.adicionarMovimentacao(valor, data, descricao, tipo, idConta, idCategoria);
                result = new FinanceController.OperationResult<>(addResult.isSucesso(), addResult.getMensagem(), null);
            }
            
            if (result.isSucesso()) {
                JOptionPane.showMessageDialog(this,
                    isEditMode ? "Movimentação atualizada com sucesso!" : "Movimentação criada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                if (onOperationComplete != null) {
                    onOperationComplete.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao salvar movimentação: " + result.getMensagem(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}