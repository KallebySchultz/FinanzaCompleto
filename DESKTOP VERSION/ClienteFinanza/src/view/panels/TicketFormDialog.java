package view.panels;

import model.Ticket;
import model.Departamento;
import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Dialog para criar/editar tickets
 */
public class TicketFormDialog extends JDialog {
    private boolean confirmed = false;
    private Ticket ticket;
    
    // UI Components
    private JTextField tituloField;
    private JTextArea descricaoArea;
    private JComboBox<String> statusCombo;
    private JComboBox<String> prioridadeCombo;
    private JComboBox<Departamento> departamentoCombo;
    private JComboBox<String> agenteCombo;
    private JTextArea observacoesArea;
    
    // Data
    private List<Departamento> departamentos;
    private Usuario usuario;
    
    public TicketFormDialog(JFrame parent, String title, Ticket ticket, List<Departamento> departamentos, Usuario usuario) {
        super(parent, title, true);
        this.ticket = ticket;
        this.departamentos = departamentos;
        this.usuario = usuario;
        
        setupUI();
        
        if (ticket != null) {
            preencherFormulario(ticket);
        }
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Título:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tituloField = new JTextField(30);
        tituloField.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(tituloField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Descrição:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        descricaoArea = new JTextArea(4, 30);
        descricaoArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descricaoArea);
        descScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(descScrollPane, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        statusCombo = new JComboBox<>(new String[]{
            "ABERTO", "EM_ANDAMENTO", "RESOLVIDO", "FECHADO", "CANCELADO"
        });
        statusCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(statusCombo, gbc);
        
        // Priority
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Prioridade:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        prioridadeCombo = new JComboBox<>(new String[]{
            "1 - Baixa", "2 - Média", "3 - Alta", "4 - Crítica"
        });
        prioridadeCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        prioridadeCombo.setSelectedIndex(1); // Default to "Média"
        mainPanel.add(prioridadeCombo, gbc);
        
        // Department
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Departamento:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        departamentoCombo = new JComboBox<>();
        for (Departamento dept : departamentos) {
            departamentoCombo.addItem(dept);
        }
        departamentoCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(departamentoCombo, gbc);
        
        // Agent (only for admin/agent)
        if (usuario.isAdmin() || usuario.isAgente()) {
            gbc.gridx = 0; gbc.gridy = 5;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            mainPanel.add(new JLabel("Agente:"), gbc);
            
            gbc.gridx = 1; gbc.gridy = 5;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            agenteCombo = new JComboBox<>(new String[]{
                "Não atribuído", "Agente 1", "Agente 2", "Agente 3"
            });
            agenteCombo.setFont(new Font("Arial", Font.PLAIN, 12));
            mainPanel.add(agenteCombo, gbc);
        }
        
        // Observations
        int nextRow = usuario.isAdmin() || usuario.isAgente() ? 6 : 5;
        gbc.gridx = 0; gbc.gridy = nextRow;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Observações:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = nextRow;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        observacoesArea = new JTextArea(3, 30);
        observacoesArea.setFont(new Font("Arial", Font.PLAIN, 12));
        observacoesArea.setLineWrap(true);
        observacoesArea.setWrapStyleWord(true);
        JScrollPane obsScrollPane = new JScrollPane(observacoesArea);
        obsScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(obsScrollPane, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton salvarBtn = new JButton("Salvar");
        JButton cancelarBtn = new JButton("Cancelar");
        
        salvarBtn.setBackground(new Color(39, 174, 96));
        salvarBtn.setForeground(Color.WHITE);
        salvarBtn.setPreferredSize(new Dimension(100, 35));
        salvarBtn.setFocusPainted(false);
        
        cancelarBtn.setBackground(new Color(231, 76, 60));
        cancelarBtn.setForeground(Color.WHITE);
        cancelarBtn.setPreferredSize(new Dimension(100, 35));
        cancelarBtn.setFocusPainted(false);
        
        salvarBtn.addActionListener(e -> salvar());
        cancelarBtn.addActionListener(e -> cancelar());
        
        buttonPanel.add(salvarBtn);
        buttonPanel.add(cancelarBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void preencherFormulario(Ticket ticket) {
        tituloField.setText(ticket.getTitulo());
        descricaoArea.setText(ticket.getDescricao());
        statusCombo.setSelectedItem(ticket.getStatus().toString());
        prioridadeCombo.setSelectedIndex(ticket.getPrioridade() - 1);
        
        // Select department
        for (int i = 0; i < departamentoCombo.getItemCount(); i++) {
            Departamento dept = departamentoCombo.getItemAt(i);
            if (dept.getId() == ticket.getDepartamentoId()) {
                departamentoCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Select agent if applicable
        if (agenteCombo != null) {
            if (ticket.getAgenteId() == 0) {
                agenteCombo.setSelectedIndex(0); // "Não atribuído"
            } else {
                agenteCombo.setSelectedIndex(ticket.getAgenteId());
            }
        }
        
        if (ticket.getObservacoes() != null) {
            observacoesArea.setText(ticket.getObservacoes());
        }
    }
    
    private void salvar() {
        // Validate required fields
        if (tituloField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O título é obrigatório", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (descricaoArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "A descrição é obrigatória", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (departamentoCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um departamento", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create or update ticket
        if (ticket == null) {
            ticket = new Ticket();
            ticket.setUsuarioId(usuario.getId());
        }
        
        ticket.setTitulo(tituloField.getText().trim());
        ticket.setDescricao(descricaoArea.getText().trim());
        ticket.setStatus(Ticket.StatusTicket.valueOf((String) statusCombo.getSelectedItem()));
        ticket.setPrioridade(prioridadeCombo.getSelectedIndex() + 1);
        ticket.setDepartamentoId(((Departamento) departamentoCombo.getSelectedItem()).getId());
        
        if (agenteCombo != null) {
            ticket.setAgenteId(agenteCombo.getSelectedIndex());
        }
        
        ticket.setObservacoes(observacoesArea.getText().trim());
        
        confirmed = true;
        dispose();
    }
    
    private void cancelar() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Ticket getTicket() {
        return ticket;
    }
}