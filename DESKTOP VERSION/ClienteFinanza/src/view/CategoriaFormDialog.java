package view;

import model.Categoria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog para criação/edição de categorias
 */
public class CategoriaFormDialog extends JDialog {
    private JTextField nomeField;
    private JComboBox<String> tipoCombo;
    private JButton salvarBtn;
    private JButton cancelarBtn;
    
    private boolean confirmado = false;
    private Categoria categoria;
    
    public CategoriaFormDialog(Frame parent, String title, Categoria categoriaExistente) {
        super(parent, title, true);
        this.categoria = categoriaExistente;
        
        initComponents();
        setupUI();
        setupEvents();
        
        if (categoriaExistente != null) {
            preencherFormulario(categoriaExistente);
        }
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel principal com formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Nome da categoria
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nomeField = new JTextField(20);
        formPanel.add(nomeField, gbc);
        
        // Tipo da categoria
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tipoCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        formPanel.add(tipoCombo, gbc);
        
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
        salvarBtn.addActionListener(e -> salvarCategoria());
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
    
    private void preencherFormulario(Categoria cat) {
        nomeField.setText(cat.getNome());
        tipoCombo.setSelectedItem(cat.getTipo().getValor());
    }
    
    private void salvarCategoria() {
        if (!validarFormulario()) {
            return;
        }
        
        confirmado = true;
        dispose();
    }
    
    private boolean validarFormulario() {
        // Validar nome
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "O nome da categoria é obrigatório", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            nomeField.requestFocus();
            return false;
        }
        
        if (nome.length() > 100) {
            JOptionPane.showMessageDialog(this, 
                "O nome da categoria deve ter no máximo 100 caracteres", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            nomeField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public String getNomeCategoria() {
        return nomeField.getText().trim();
    }
    
    public Categoria.TipoCategoria getTipoCategoria() {
        String tipo = (String) tipoCombo.getSelectedItem();
        return "receita".equals(tipo) ? Categoria.TipoCategoria.RECEITA : Categoria.TipoCategoria.DESPESA;
    }
}