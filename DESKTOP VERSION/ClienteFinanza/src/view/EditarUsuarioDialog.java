package view;

import controller.AuthController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo para editar informações de um usuário
 */
public class EditarUsuarioDialog extends JDialog {
    private AuthController authController;
    private Usuario usuario;
    private boolean confirmado = false;
    
    private JTextField nomeField;
    private JTextField emailField;
    private JPasswordField novaSenhaField;
    private JButton salvarButton;
    private JButton cancelarButton;
    
    public EditarUsuarioDialog(Frame parent, AuthController authController, Usuario usuario) {
        super(parent, "Editar Usuário", true);
        this.authController = authController;
        this.usuario = usuario;
        
        initComponents();
        setupUI();
        setupEvents();
    }
    
    private void initComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        nomeField = new JTextField(20);
        emailField = new JTextField(20);
        novaSenhaField = new JPasswordField(20);
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // ID (apenas visualização)
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(String.valueOf(usuario.getId())), gbc);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        nomeField.setText(usuario.getNome());
        mainPanel.add(nomeField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField.setText(usuario.getEmail());
        mainPanel.add(emailField, gbc);
        
        // Nova senha (opcional)
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Nova Senha:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(novaSenhaField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("(Deixe em branco para não alterar a senha)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        mainPanel.add(infoLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEvents() {
        salvarButton.addActionListener(e -> salvarAlteracoes());
        cancelarButton.addActionListener(e -> dispose());
        
        // Permitir salvar com Enter
        getRootPane().setDefaultButton(salvarButton);
    }
    
    private void salvarAlteracoes() {
        String novoNome = nomeField.getText().trim();
        String novoEmail = emailField.getText().trim();
        String novaSenha = new String(novaSenhaField.getPassword());
        
        // Validações
        if (novoNome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "O nome não pode estar vazio",
                "Erro de Validação",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (novoEmail.isEmpty() || !novoEmail.contains("@")) {
            JOptionPane.showMessageDialog(this,
                "Digite um email válido",
                "Erro de Validação",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Desabilitar botões durante salvamento
        salvarButton.setEnabled(false);
        cancelarButton.setEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Atualizar perfil
                boolean sucesso = authController.atualizarUsuario(usuario.getId(), novoNome, novoEmail);
                
                // Se forneceu nova senha, atualizar também
                if (sucesso && !novaSenha.isEmpty()) {
                    sucesso = authController.atualizarSenhaUsuario(usuario.getId(), novaSenha);
                }
                
                return sucesso;
            }
            
            @Override
            protected void done() {
                try {
                    Boolean sucesso = get();
                    
                    if (sucesso) {
                        JOptionPane.showMessageDialog(EditarUsuarioDialog.this,
                            "Usuário atualizado com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                        confirmado = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(EditarUsuarioDialog.this,
                            "Erro ao atualizar usuário",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                        salvarButton.setEnabled(true);
                        cancelarButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EditarUsuarioDialog.this,
                        "Erro ao salvar: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                    salvarButton.setEnabled(true);
                    cancelarButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
}
