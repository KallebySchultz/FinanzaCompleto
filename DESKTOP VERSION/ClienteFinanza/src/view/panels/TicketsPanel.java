package view.panels;

import controller.FinanceController;
import model.Usuario;
import model.Ticket;
import model.Departamento;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel de gerenciamento de tickets/chamados
 */
public class TicketsPanel extends JPanel {
    private FinanceController financeController;
    private Usuario usuario;
    
    // UI Components
    private JTable ticketsTable;
    private DefaultTableModel tableModel;
    private JButton novoTicketBtn;
    private JButton editarBtn;
    private JButton excluirBtn;
    private JButton atualizarBtn;
    private JButton visualizarBtn;
    
    // Demo data
    private List<Ticket> tickets;
    private List<Departamento> departamentos;
    
    public TicketsPanel(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        initDemoData();
        setupUI();
        carregarTickets();
    }
    
    private void initDemoData() {
        // Initialize demo departments
        departamentos = new ArrayList<>();
        departamentos.add(new Departamento(1, "TI", "Tecnologia da Informação", true));
        departamentos.add(new Departamento(2, "Financeiro", "Departamento Financeiro", true));
        departamentos.add(new Departamento(3, "RH", "Recursos Humanos", true));
        departamentos.add(new Departamento(4, "Suporte", "Suporte ao Cliente", true));
        
        // Initialize demo tickets
        tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket("Sistema lento", "O sistema está muito lento na tela de relatórios", 3, 1, 2);
        ticket1.setId(1);
        ticket1.setAgenteId(0); // Não atribuído
        ticket1.setStatus(Ticket.StatusTicket.ABERTO);
        
        Ticket ticket2 = new Ticket("Erro ao exportar dados", "Erro ao tentar exportar dados em Excel", 4, 1, 3);
        ticket2.setId(2);
        ticket2.setAgenteId(1); // Atribuído ao agente 1
        ticket2.setStatus(Ticket.StatusTicket.EM_ANDAMENTO);
        
        Ticket ticket3 = new Ticket("Solicitação de novo usuário", "Criar acesso para novo funcionário", 2, 3, 1);
        ticket3.setId(3);
        ticket3.setAgenteId(1);
        ticket3.setStatus(Ticket.StatusTicket.RESOLVIDO);
        
        tickets.add(ticket1);
        tickets.add(ticket2);
        tickets.add(ticket3);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gerenciamento de Tickets");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        novoTicketBtn = new JButton("Novo Ticket");
        editarBtn = new JButton("Editar");
        excluirBtn = new JButton("Excluir");
        visualizarBtn = new JButton("Visualizar");
        atualizarBtn = new JButton("Atualizar");
        
        // Style buttons
        styleButton(novoTicketBtn, new Color(39, 174, 96));
        styleButton(editarBtn, new Color(41, 128, 185));
        styleButton(excluirBtn, new Color(231, 76, 60));
        styleButton(visualizarBtn, new Color(142, 68, 173));
        styleButton(atualizarBtn, new Color(230, 126, 34));
        
        buttonPanel.add(novoTicketBtn);
        buttonPanel.add(visualizarBtn);
        buttonPanel.add(editarBtn);
        buttonPanel.add(excluirBtn);
        buttonPanel.add(atualizarBtn);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        setupTable();
        
        // Setup events
        setupEvents();
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void setupTable() {
        String[] columns = {"ID", "Título", "Status", "Prioridade", "Departamento", "Usuário", "Agente", "Data Abertura"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ticketsTable = new JTable(tableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ticketsTable.setRowHeight(25);
        ticketsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ticketsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        ticketsTable.getTableHeader().setForeground(Color.WHITE);
        
        // Configure column widths
        ticketsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        ticketsTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        ticketsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        ticketsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        ticketsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        ticketsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        ticketsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        ticketsTable.getColumnModel().getColumn(7).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Lista de Tickets",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        novoTicketBtn.addActionListener(e -> abrirFormularioNovoTicket());
        editarBtn.addActionListener(e -> editarTicketSelecionado());
        excluirBtn.addActionListener(e -> excluirTicketSelecionado());
        visualizarBtn.addActionListener(e -> visualizarTicketSelecionado());
        atualizarBtn.addActionListener(e -> carregarTickets());
        
        // Double click to view ticket
        ticketsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    visualizarTicketSelecionado();
                }
            }
        });
    }
    
    private void carregarTickets() {
        tableModel.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Ticket ticket : tickets) {
            String departamento = getDepartamentoNome(ticket.getDepartamentoId());
            String agente = ticket.getAgenteId() > 0 ? "Agente " + ticket.getAgenteId() : "Não atribuído";
            String dataAbertura = ticket.getDataAbertura().format(formatter);
            
            Object[] row = {
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getStatus().toString(),
                ticket.getPrioridadeString(),
                departamento,
                "User " + ticket.getUsuarioId(),
                agente,
                dataAbertura
            };
            
            tableModel.addRow(row);
        }
    }
    
    private String getDepartamentoNome(int deptoId) {
        for (Departamento dept : departamentos) {
            if (dept.getId() == deptoId) {
                return dept.getNome();
            }
        }
        return "Desconhecido";
    }
    
    private void abrirFormularioNovoTicket() {
        TicketFormDialog dialog = new TicketFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Novo Ticket",
            null,
            departamentos,
            usuario
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Ticket novoTicket = dialog.getTicket();
            novoTicket.setId(getNextTicketId());
            novoTicket.setDataAbertura(LocalDateTime.now());
            tickets.add(novoTicket);
            carregarTickets();
            
            JOptionPane.showMessageDialog(this, 
                "Ticket criado com sucesso!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editarTicketSelecionado() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um ticket para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        Ticket ticket = findTicketById(ticketId);
        
        if (ticket != null) {
            TicketFormDialog dialog = new TicketFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Editar Ticket",
                ticket,
                departamentos,
                usuario
            );
            
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Ticket ticketEditado = dialog.getTicket();
                // Update the existing ticket
                ticket.setTitulo(ticketEditado.getTitulo());
                ticket.setDescricao(ticketEditado.getDescricao());
                ticket.setStatus(ticketEditado.getStatus());
                ticket.setPrioridade(ticketEditado.getPrioridade());
                ticket.setDepartamentoId(ticketEditado.getDepartamentoId());
                ticket.setAgenteId(ticketEditado.getAgenteId());
                ticket.setObservacoes(ticketEditado.getObservacoes());
                
                carregarTickets();
                
                JOptionPane.showMessageDialog(this, 
                    "Ticket atualizado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void excluirTicketSelecionado() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um ticket para excluir", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este ticket?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
            tickets.removeIf(t -> t.getId() == ticketId);
            carregarTickets();
            
            JOptionPane.showMessageDialog(this, 
                "Ticket excluído com sucesso!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void visualizarTicketSelecionado() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um ticket para visualizar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        Ticket ticket = findTicketById(ticketId);
        
        if (ticket != null) {
            TicketViewDialog dialog = new TicketViewDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                ticket,
                getDepartamentoNome(ticket.getDepartamentoId())
            );
            dialog.setVisible(true);
        }
    }
    
    private Ticket findTicketById(int id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        return null;
    }
    
    private int getNextTicketId() {
        int maxId = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getId() > maxId) {
                maxId = ticket.getId();
            }
        }
        return maxId + 1;
    }
    
    public void refresh() {
        carregarTickets();
    }
}

// Helper dialog classes will be created separately