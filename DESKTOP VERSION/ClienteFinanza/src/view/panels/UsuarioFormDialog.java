package view.panels;

import model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog para criar/editar usuários
 */
public class UsuarioFormDialog extends JDialog {
    private boolean confirmed = false;
    private Usuario usuario;
    
    // UI Components
    private JTextField nomeField;
    private JTextField emailField;
    private JComboBox<Usuario.TipoUsuario> tipoCombo;
    private JPasswordField senhaField;
    private JPasswordField confirmarSenhaField;
    
    public UsuarioFormDialog(JFrame parent, String title, Usuario usuario) {
        super(parent, title, true);
        this.usuario = usuario;
        
        setupUI();
        
        if (usuario != null) {
            preencherFormulario(usuario);
        }
        
        setSize(450, 350);
        setLocationRelativeTo(parent);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Nome:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nomeField = new JTextField(20);
        nomeField.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(nomeField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Email:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(emailField, gbc);
        
        // Tipo
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Tipo:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tipoCombo = new JComboBox<>(Usuario.TipoUsuario.values());
        tipoCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(tipoCombo, gbc);
        
        // Senha (only for new users)
        if (usuario == null) {
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            mainPanel.add(new JLabel("Senha:*"), gbc);
            
            gbc.gridx = 1; gbc.gridy = 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            senhaField = new JPasswordField(20);
            senhaField.setFont(new Font("Arial", Font.PLAIN, 12));
            mainPanel.add(senhaField, gbc);
            
            // Confirmar senha
            gbc.gridx = 0; gbc.gridy = 4;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            mainPanel.add(new JLabel("Confirmar Senha:*"), gbc);
            
            gbc.gridx = 1; gbc.gridy = 4;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            confirmarSenhaField = new JPasswordField(20);
            confirmarSenhaField.setFont(new Font("Arial", Font.PLAIN, 12));
            mainPanel.add(confirmarSenhaField, gbc);
        }
        
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
    
    private void preencherFormulario(Usuario usuario) {
        nomeField.setText(usuario.getNome());
        emailField.setText(usuario.getEmail());
        tipoCombo.setSelectedItem(usuario.getTipo());
    }
    
    private void salvar() {
        // Validate required fields
        if (nomeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome é obrigatório", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O email é obrigatório", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Email deve ter formato válido", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate passwords for new users
        if (usuario == null) {
            if (senhaField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "A senha é obrigatória", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!java.util.Arrays.equals(senhaField.getPassword(), confirmarSenhaField.getPassword())) {
                JOptionPane.showMessageDialog(this, "As senhas não coincidem", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (senhaField.getPassword().length < 6) {
                JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Create or update user
        if (usuario == null) {
            usuario = new Usuario();
        }
        
        usuario.setNome(nomeField.getText().trim());
        usuario.setEmail(emailField.getText().trim());
        usuario.setTipo((Usuario.TipoUsuario) tipoCombo.getSelectedItem());
        
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
    
    public Usuario getUsuario() {
        return usuario;
    }
}