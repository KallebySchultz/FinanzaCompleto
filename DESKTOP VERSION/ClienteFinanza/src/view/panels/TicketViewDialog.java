package view.panels;

import model.Ticket;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Dialog para visualizar detalhes de um ticket
 */
public class TicketViewDialog extends JDialog {
    private Ticket ticket;
    private String departamentoNome;
    
    public TicketViewDialog(JFrame parent, Ticket ticket, String departamentoNome) {
        super(parent, "Detalhes do Ticket #" + ticket.getId(), true);
        this.ticket = ticket;
        this.departamentoNome = departamentoNome;
        
        setupUI();
        setSize(650, 450);
        setLocationRelativeTo(parent);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(getStatusColor(ticket.getStatus()));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(ticket.getTitulo());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel statusLabel = new JLabel(ticket.getStatus().toString());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Ticket info
        addInfoRow(infoPanel, gbc, 0, "ID:", "#" + ticket.getId());
        addInfoRow(infoPanel, gbc, 1, "Prioridade:", ticket.getPrioridadeString());
        addInfoRow(infoPanel, gbc, 2, "Departamento:", departamentoNome);
        addInfoRow(infoPanel, gbc, 3, "Usuário:", "User " + ticket.getUsuarioId());
        
        String agente = ticket.getAgenteId() > 0 ? "Agente " + ticket.getAgenteId() : "Não atribuído";
        addInfoRow(infoPanel, gbc, 4, "Agente:", agente);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        addInfoRow(infoPanel, gbc, 5, "Data Abertura:", ticket.getDataAbertura().format(formatter));
        
        if (ticket.getDataFechamento() != null) {
            addInfoRow(infoPanel, gbc, 6, "Data Fechamento:", ticket.getDataFechamento().format(formatter));
        }
        
        contentPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Description panel
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Descrição",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
        
        JTextArea descArea = new JTextArea(ticket.getDescricao());
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new Color(248, 249, 250));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setPreferredSize(new Dimension(0, 100));
        descPanel.add(descScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(descPanel, BorderLayout.CENTER);
        
        // Observations panel (if any)
        if (ticket.getObservacoes() != null && !ticket.getObservacoes().trim().isEmpty()) {
            JPanel obsPanel = new JPanel(new BorderLayout());
            obsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Observações",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                new Color(52, 73, 94)
            ));
            
            JTextArea obsArea = new JTextArea(ticket.getObservacoes());
            obsArea.setFont(new Font("Arial", Font.PLAIN, 12));
            obsArea.setEditable(false);
            obsArea.setLineWrap(true);
            obsArea.setWrapStyleWord(true);
            obsArea.setBackground(new Color(248, 249, 250));
            obsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JScrollPane obsScrollPane = new JScrollPane(obsArea);
            obsScrollPane.setPreferredSize(new Dimension(0, 80));
            obsPanel.add(obsScrollPane, BorderLayout.CENTER);
            
            contentPanel.add(obsPanel, BorderLayout.SOUTH);
        }
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton fecharBtn = new JButton("Fechar");
        fecharBtn.setBackground(new Color(52, 73, 94));
        fecharBtn.setForeground(Color.WHITE);
        fecharBtn.setPreferredSize(new Dimension(100, 35));
        fecharBtn.setFocusPainted(false);
        fecharBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(fecharBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setForeground(new Color(52, 73, 94));
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1; gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));
        valueComp.setForeground(new Color(44, 62, 80));
        
        // Special formatting for priority
        if (label.equals("Prioridade:")) {
            Color priorityColor = getPriorityColor(ticket.getPrioridade());
            valueComp.setForeground(priorityColor);
            valueComp.setFont(new Font("Arial", Font.BOLD, 12));
        }
        
        panel.add(valueComp, gbc);
    }
    
    private Color getStatusColor(Ticket.StatusTicket status) {
        switch (status) {
            case ABERTO:
                return new Color(231, 76, 60); // Red
            case EM_ANDAMENTO:
                return new Color(230, 126, 34); // Orange
            case RESOLVIDO:
                return new Color(39, 174, 96); // Green
            case FECHADO:
                return new Color(52, 73, 94); // Dark blue
            case CANCELADO:
                return new Color(149, 165, 166); // Gray
            default:
                return new Color(127, 140, 141);
        }
    }
    
    private Color getPriorityColor(int prioridade) {
        switch (prioridade) {
            case 1: return new Color(39, 174, 96); // Low - Green
            case 2: return new Color(230, 126, 34); // Medium - Orange
            case 3: return new Color(231, 76, 60); // High - Red
            case 4: return new Color(155, 89, 182); // Critical - Purple
            default: return new Color(127, 140, 141);
        }
    }
}