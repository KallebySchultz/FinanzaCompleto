package view;

import controller.FinanceController;
import model.Conta;
import model.Usuario;
import util.ColorScheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Tela de gerenciamento de contas
 */
public class ContasView extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private JTable contasTable;
    private DefaultTableModel tableModel;
    private NumberFormat currencyFormat;
    
    public ContasView(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        initComponents();
        setupUI();
        carregarContas();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Gerenciar Contas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel superior com título e botões
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gerenciamento de Contas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton novaContaBtn = new JButton("Nova Conta");
        JButton editarBtn = new JButton("Editar");
        JButton excluirBtn = new JButton("Excluir");
        JButton atualizarBtn = new JButton("Atualizar");
        
        novaContaBtn.setPreferredSize(new Dimension(120, 30));
        editarBtn.setPreferredSize(new Dimension(100, 30));
        excluirBtn.setPreferredSize(new Dimension(100, 30));
        atualizarBtn.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(novaContaBtn);
        buttonPanel.add(editarBtn);
        buttonPanel.add(excluirBtn);
        buttonPanel.add(atualizarBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Tabela de contas
        String[] colunas = {"ID", "Nome", "Tipo", "Saldo Inicial", "Saldo Atual"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };
        
        contasTable = new JTable(tableModel);
        contasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contasTable.getTableHeader().setReorderingAllowed(false);
        
        // Configurar larguras das colunas
        contasTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        contasTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        contasTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        contasTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        contasTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(contasTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pronto");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Eventos dos botões
        novaContaBtn.addActionListener(e -> abrirFormularioNovaConta());
        editarBtn.addActionListener(e -> editarContaSelecionada());
        excluirBtn.addActionListener(e -> excluirContaSelecionada());
        atualizarBtn.addActionListener(e -> carregarContas());
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
                    JOptionPane.showMessageDialog(ContasView.this, 
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
            Object[] row = {
                conta.getId(),
                conta.getNome(),
                conta.getTipo().getDescricao(),
                currencyFormat.format(conta.getSaldoInicial()),
                currencyFormat.format(conta.getSaldoAtual())
            };
            tableModel.addRow(row);
        }
    }
    
    private void abrirFormularioNovaConta() {
        ContaFormDialog dialog = new ContaFormDialog(this, "Nova Conta", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            Conta novaConta = dialog.getConta();
            criarConta(novaConta);
        }
    }
    
    private void editarContaSelecionada() {
        int selectedRow = contasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma conta para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Buscar conta pelos dados da tabela
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nome = (String) tableModel.getValueAt(selectedRow, 1);
        String tipoDesc = (String) tableModel.getValueAt(selectedRow, 2);
        
        // Encontrar o tipo correspondente
        Conta.TipoConta tipo = null;
        for (Conta.TipoConta t : Conta.TipoConta.values()) {
            if (t.getDescricao().equals(tipoDesc)) {
                tipo = t;
                break;
            }
        }
        
        if (tipo == null) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao identificar tipo da conta", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extrair valor numérico do saldo inicial
        String saldoStr = (String) tableModel.getValueAt(selectedRow, 3);
        double saldoInicial = 0.0;
        try {
            // Remove formatação da moeda
            String numberStr = saldoStr.replaceAll("[^\\d,.-]", "").replace(",", ".");
            saldoInicial = Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            saldoInicial = 0.0;
        }
        
        Conta conta = new Conta(nome, tipo, saldoInicial);
        conta.setId(id);
        
        ContaFormDialog dialog = new ContaFormDialog(this, "Editar Conta", conta);
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            Conta contaEditada = dialog.getConta();
            atualizarConta(contaEditada);
        }
    }
    
    private void excluirContaSelecionada() {
        int selectedRow = contasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma conta para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String nome = (String) tableModel.getValueAt(selectedRow, 1);
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente excluir a conta '" + nome + "'?\n" +
            "Esta ação não pode ser desfeita e todas as movimentações\n" +
            "associadas a esta conta também serão removidas.", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            removerConta(id);
        }
    }
    
    private void criarConta(Conta conta) {
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                FinanceController.OperationResult<Integer> result = financeController.adicionarConta(
                    conta.getNome(), conta.getTipo(), conta.getSaldoInicial());
                if (result.isSucesso()) {
                    return result.getDados();
                } else {
                    throw new Exception(result.getMensagem());
                }
            }
            
            @Override
            protected void done() {
                try {
                    Integer id = get();
                    JOptionPane.showMessageDialog(ContasView.this, 
                        "Conta criada com sucesso!", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    carregarContas();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ContasView.this, 
                        "Erro ao criar conta: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarConta(Conta conta) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                FinanceController.OperationResult<Void> result = financeController.atualizarConta(
                    conta.getId(), conta.getNome(), conta.getTipo(), conta.getSaldoInicial());
                if (!result.isSucesso()) {
                    throw new Exception(result.getMensagem());
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(ContasView.this, 
                        "Conta atualizada com sucesso!", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    carregarContas();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ContasView.this, 
                        "Erro ao atualizar conta: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void removerConta(int id) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                FinanceController.OperationResult<Void> result = financeController.removerConta(id);
                if (!result.isSucesso()) {
                    throw new Exception(result.getMensagem());
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(ContasView.this, 
                        "Conta removida com sucesso!", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    carregarContas();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ContasView.this, 
                        "Erro ao remover conta: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}