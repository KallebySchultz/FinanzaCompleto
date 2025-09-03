package com.finanza.desktop.ui;

import com.finanza.desktop.model.*;
import com.finanza.desktop.database.*;
import com.finanza.desktop.util.UIUtils;
import com.finanza.desktop.util.FormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Calendar;

/**
 * Dialog para adicionar nova transação
 */
public class AddTransactionDialog extends JDialog {
    private Usuario usuario;
    private ContaDao contaDao;
    private CategoriaDao categoriaDao;
    private LancamentoDao lancamentoDao;
    private MainFrame parentFrame;

    private JTextField descricaoField;
    private JTextField valorField;
    private JComboBox<Conta> contaComboBox;
    private JComboBox<Categoria> categoriaComboBox;
    private JRadioButton receitaRadio;
    private JRadioButton despesaRadio;
    private JLabel dataLabel;
    private JButton dataButton;
    private JButton salvarButton;
    private JButton cancelarButton;

    private long dataSelecionada;

    public AddTransactionDialog(JFrame parent, Usuario usuario) {
        super(parent, "Nova Transação", true);
        this.parentFrame = parent instanceof MainFrame ? (MainFrame) parent : null;
        this.usuario = usuario;
        this.contaDao = new ContaDao();
        this.categoriaDao = new CategoriaDao();
        this.lancamentoDao = new LancamentoDao();
        this.dataSelecionada = System.currentTimeMillis();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureDialog();
        loadData();
    }

    private void initializeComponents() {
        // Campos de entrada
        descricaoField = new JTextField();
        UIUtils.configureTextField(descricaoField);

        valorField = new JTextField();
        UIUtils.configureTextField(valorField);

        // ComboBoxes
        contaComboBox = new JComboBox<>();
        categoriaComboBox = new JComboBox<>();

        // Radio buttons para tipo
        receitaRadio = new JRadioButton("Receita", true);
        despesaRadio = new JRadioButton("Despesa");
        ButtonGroup tipoGroup = new ButtonGroup();
        tipoGroup.add(receitaRadio);
        tipoGroup.add(despesaRadio);

        // Data
        dataLabel = new JLabel(FormatUtils.formatarData(dataSelecionada));
        dataButton = new JButton("Selecionar Data");
        UIUtils.configureSecondaryButton(dataButton);

        // Botões
        salvarButton = new JButton("Salvar");
        UIUtils.configureButton(salvarButton);

        cancelarButton = new JButton("Cancelar");
        UIUtils.configureSecondaryButton(cancelarButton);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = UIUtils.createCard();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(400, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = UIUtils.createTitleLabel("Nova Transação");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, gbc);

        // Tipo de transação
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel tipoLabel = new JLabel("Tipo:");
        tipoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(tipoLabel, gbc);

        gbc.gridx = 1;
        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoPanel.setBackground(Color.WHITE);
        tipoPanel.add(receitaRadio);
        tipoPanel.add(despesaRadio);
        mainPanel.add(tipoPanel, gbc);

        // Descrição
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel descricaoLabel = new JLabel("Descrição:");
        descricaoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(descricaoLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(descricaoField, gbc);

        // Valor
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel valorLabel = new JLabel("Valor:");
        valorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(valorLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(valorField, gbc);

        // Conta
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel contaLabel = new JLabel("Conta:");
        contaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(contaLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(contaComboBox, gbc);

        // Categoria
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel categoriaLabel = new JLabel("Categoria:");
        categoriaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(categoriaLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(categoriaComboBox, gbc);

        // Data
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel dataLabelTitle = new JLabel("Data:");
        dataLabelTitle.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(dataLabelTitle, gbc);

        gbc.gridx = 1;
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBackground(Color.WHITE);
        dataPanel.add(dataLabel, BorderLayout.CENTER);
        dataPanel.add(dataButton, BorderLayout.EAST);
        mainPanel.add(dataPanel, gbc);

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
        salvarButton.addActionListener(this::salvarTransacao);
        cancelarButton.addActionListener(e -> dispose());
        dataButton.addActionListener(this::selecionarData);
        
        // Atualizar categorias quando tipo mudar
        receitaRadio.addActionListener(e -> updateCategorias());
        despesaRadio.addActionListener(e -> updateCategorias());
    }

    private void loadData() {
        // Carregar contas
        List<Conta> contas = contaDao.listarPorUsuario(usuario.getId());
        for (Conta conta : contas) {
            contaComboBox.addItem(conta);
        }

        // Carregar categorias iniciais (receita)
        updateCategorias();
    }

    private void updateCategorias() {
        categoriaComboBox.removeAllItems();
        String tipo = receitaRadio.isSelected() ? "receita" : "despesa";
        List<Categoria> categorias = categoriaDao.listarPorTipo(tipo);
        for (Categoria categoria : categorias) {
            categoriaComboBox.addItem(categoria);
        }
    }

    private void selecionarData(ActionEvent e) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dataSelecionada);
        
        JDateChooser dateChooser = new JDateChooser(calendar.getTime());
        
        int option = JOptionPane.showConfirmDialog(this, dateChooser, 
            "Selecionar Data", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION && dateChooser.getDate() != null) {
            dataSelecionada = dateChooser.getDate().getTime();
            dataLabel.setText(FormatUtils.formatarData(dataSelecionada));
        }
    }

    private void salvarTransacao(ActionEvent e) {
        // Validações
        String descricao = descricaoField.getText().trim();
        String valorStr = valorField.getText().trim();
        Conta conta = (Conta) contaComboBox.getSelectedItem();
        Categoria categoria = (Categoria) categoriaComboBox.getSelectedItem();

        if (descricao.isEmpty()) {
            UIUtils.showError(this, "Por favor, digite uma descrição");
            descricaoField.requestFocus();
            return;
        }

        if (valorStr.isEmpty()) {
            UIUtils.showError(this, "Por favor, digite o valor");
            valorField.requestFocus();
            return;
        }

        double valor;
        try {
            valor = FormatUtils.parseDouble(valorStr);
            if (valor <= 0) {
                UIUtils.showError(this, "O valor deve ser maior que zero");
                valorField.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Valor inválido. Use apenas números e vírgula/ponto para decimais");
            valorField.requestFocus();
            return;
        }

        if (conta == null) {
            UIUtils.showError(this, "Por favor, selecione uma conta");
            return;
        }

        if (categoria == null) {
            UIUtils.showError(this, "Por favor, selecione uma categoria");
            return;
        }

        // Criar lançamento
        try {
            String tipo = receitaRadio.isSelected() ? "receita" : "despesa";
            Lancamento lancamento = new Lancamento(valor, dataSelecionada, descricao, 
                conta.getId(), categoria.getId(), usuario.getId(), tipo);
            
            int id = lancamentoDao.inserir(lancamento);
            
            if (id > 0) {
                UIUtils.showSuccess(this, "Transação salva com sucesso!");
                if (parentFrame != null) {
                    parentFrame.refreshDashboard();
                }
                dispose();
            } else {
                UIUtils.showError(this, "Erro ao salvar transação");
            }
            
        } catch (Exception ex) {
            UIUtils.showError(this, "Erro ao salvar transação: " + ex.getMessage());
        }
    }

    private void configureDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    // Classe auxiliar para seleção de data
    private static class JDateChooser extends JPanel {
        private JComboBox<Integer> dayCombo;
        private JComboBox<Integer> monthCombo;
        private JComboBox<Integer> yearCombo;
        private java.util.Date selectedDate;

        public JDateChooser(java.util.Date initialDate) {
            setLayout(new FlowLayout());
            Calendar cal = Calendar.getInstance();
            cal.setTime(initialDate);
            
            // Dia
            dayCombo = new JComboBox<>();
            for (int i = 1; i <= 31; i++) {
                dayCombo.addItem(i);
            }
            dayCombo.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));

            // Mês
            monthCombo = new JComboBox<>();
            String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                            "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
            for (int i = 0; i < 12; i++) {
                monthCombo.addItem(i + 1);
            }
            monthCombo.setSelectedItem(cal.get(Calendar.MONTH) + 1);

            // Ano
            yearCombo = new JComboBox<>();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = currentYear - 5; i <= currentYear + 1; i++) {
                yearCombo.addItem(i);
            }
            yearCombo.setSelectedItem(cal.get(Calendar.YEAR));

            add(new JLabel("Dia:"));
            add(dayCombo);
            add(new JLabel("Mês:"));
            add(monthCombo);
            add(new JLabel("Ano:"));
            add(yearCombo);
        }

        public java.util.Date getDate() {
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, (Integer) dayCombo.getSelectedItem());
                cal.set(Calendar.MONTH, (Integer) monthCombo.getSelectedItem() - 1);
                cal.set(Calendar.YEAR, (Integer) yearCombo.getSelectedItem());
                cal.set(Calendar.HOUR_OF_DAY, 12);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            } catch (Exception e) {
                return new java.util.Date();
            }
        }
    }
}