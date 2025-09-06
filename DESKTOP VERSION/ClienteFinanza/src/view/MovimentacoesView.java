package view;

import controller.FinanceController;
import model.Movimentacao;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Tela de gerenciamento de movimentações
 */
public class MovimentacoesView extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private JTable movimentacoesTable;
    private DefaultTableModel tableModel;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    
    public MovimentacoesView(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        initComponents();
        setupUI();
        carregarMovimentacoes();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Movimentações");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Movimentações Financeiras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton novaMovimentacaoBtn = new JButton("Nova Movimentação");
        JButton editarBtn = new JButton("Editar");
        JButton excluirBtn = new JButton("Excluir");
        JButton atualizarBtn = new JButton("Atualizar");
        
        novaMovimentacaoBtn.setPreferredSize(new Dimension(150, 30));
        editarBtn.setPreferredSize(new Dimension(100, 30));
        excluirBtn.setPreferredSize(new Dimension(100, 30));
        atualizarBtn.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(novaMovimentacaoBtn);
        buttonPanel.add(editarBtn);
        buttonPanel.add(excluirBtn);
        buttonPanel.add(atualizarBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Tabela de movimentações
        String[] colunas = {"ID", "Data", "Descrição", "Tipo", "Valor", "Categoria"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        movimentacoesTable = new JTable(tableModel);
        movimentacoesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar larguras das colunas
        movimentacoesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        movimentacoesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        movimentacoesTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        movimentacoesTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        movimentacoesTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        movimentacoesTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(movimentacoesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pronto");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Eventos
        novaMovimentacaoBtn.addActionListener(e -> abrirFormularioNovaMovimentacao());
        editarBtn.addActionListener(e -> editarMovimentacaoSelecionada());
        excluirBtn.addActionListener(e -> excluirMovimentacaoSelecionada());
        atualizarBtn.addActionListener(e -> carregarMovimentacoes());
    }
    
    private void carregarMovimentacoes() {
        SwingWorker<List<Movimentacao>, Void> worker = new SwingWorker<List<Movimentacao>, Void>() {
            @Override
            protected List<Movimentacao> doInBackground() throws Exception {
                FinanceController.OperationResult<List<Movimentacao>> result = financeController.listarMovimentacoes();
                if (result.isSucesso()) {
                    return result.getDados();
                } else {
                    throw new Exception(result.getMensagem());
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Movimentacao> movimentacoes = get();
                    atualizarTabela(movimentacoes);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MovimentacoesView.this, 
                        "Erro ao carregar movimentações: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarTabela(List<Movimentacao> movimentacoes) {
        tableModel.setRowCount(0);
        
        for (Movimentacao mov : movimentacoes) {
            Object[] row = {
                mov.getId(),
                dateFormat.format(mov.getData()),
                mov.getDescricao() != null ? mov.getDescricao() : "Sem descrição",
                mov.getTipo().getDescricao(),
                currencyFormat.format(mov.getValor()),
                mov.getNomeCategoria() != null ? mov.getNomeCategoria() : "Sem categoria"
            };
            tableModel.addRow(row);
        }
    }
    
    private void abrirFormularioNovaMovimentacao() {
        MovimentacaoFormDialog dialog = new MovimentacaoFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            "Nova Movimentação", 
            null, 
            financeController
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmado()) {
            carregarMovimentacoes();
        }
    }
    
    private void editarMovimentacaoSelecionada() {
        int selectedRow = movimentacoesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma movimentação para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int movimentacaoId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Buscar movimentação completa
        FinanceController.OperationResult<List<Movimentacao>> result = financeController.listarMovimentacoes();
        if (result.isSucesso()) {
            Movimentacao movimentacao = null;
            for (Movimentacao mov : result.getDados()) {
                if (mov.getId() == movimentacaoId) {
                    movimentacao = mov;
                    break;
                }
            }
            
            if (movimentacao != null) {
                MovimentacaoFormDialog dialog = new MovimentacaoFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), 
                    "Editar Movimentação", 
                    movimentacao, 
                    financeController
                );
                
                dialog.setVisible(true);
                
                if (dialog.isConfirmado()) {
                    carregarMovimentacoes();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Movimentação não encontrada", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados da movimentação: " + result.getMensagem(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirMovimentacaoSelecionada() {
        int selectedRow = movimentacoesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma movimentação para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int movimentacaoId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String descricao = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir a movimentação:\n" + descricao + "?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            FinanceController.OperationResult<Void> result = financeController.removerMovimentacao(movimentacaoId);
            
            if (result.isSucesso()) {
                JOptionPane.showMessageDialog(this, 
                    "Movimentação excluída com sucesso", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                carregarMovimentacoes();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao excluir movimentação: " + result.getMensagem(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}