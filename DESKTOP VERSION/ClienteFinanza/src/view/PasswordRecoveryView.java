package view;

import controller.AuthController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela de recuperação de senha
 */
public class PasswordRecoveryView extends JDialog {
    private AuthController authController;
    private JTextField emailField;
    private JPasswordField novaSenhaField;
    private JPasswordField confirmarSenhaField;
    private JButton recuperarButton;
    private JButton cancelarButton;
    
    public PasswordRecoveryView(Frame parent, AuthController authController) {
        super(parent, "Recuperar Senha", true);
        this.authController = authController;
        initComponents();
        setupUI();
        setupEvents();
    }
    
    private void initComponents() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        emailField = new JTextField(20);
        novaSenhaField = new JPasswordField(20);
        confirmarSenhaField = new JPasswordField(20);
        recuperarButton = new JButton("Recuperar Senha");
        cancelarButton = new JButton("Cancelar");
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Recuperar Senha", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Instruções
        JLabel instrucaoLabel = new JLabel("<html>Digite seu email e a nova senha:</html>");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(instrucaoLabel, gbc);
        
        // Email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(emailField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(emailPanel, gbc);
        
        // Nova senha
        JPanel novaSenhaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        novaSenhaPanel.add(new JLabel("Nova senha:"));
        novaSenhaPanel.add(novaSenhaField);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(novaSenhaPanel, gbc);
        
        // Confirmar senha
        JPanel confirmarSenhaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmarSenhaPanel.add(new JLabel("Confirmar senha:"));
        confirmarSenhaPanel.add(confirmarSenhaField);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(confirmarSenhaPanel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        recuperarButton.setPreferredSize(new Dimension(130, 30));
        cancelarButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(recuperarButton);
        buttonPanel.add(cancelarButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        recuperarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recuperarSenha();
            }
        });
        
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter nos campos
        ActionListener enterAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recuperarSenha();
            }
        };
        
        emailField.addActionListener(enterAction);
        novaSenhaField.addActionListener(enterAction);
        confirmarSenhaField.addActionListener(enterAction);
    }
    
    private void recuperarSenha() {
        String email = emailField.getText().trim();
        String novaSenha = new String(novaSenhaField.getPassword());
        String confirmarSenha = new String(confirmarSenhaField.getPassword());
        
        // Validações
        if (email.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Todos os campos são obrigatórios", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!novaSenha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this, 
                "As senhas não coincidem", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (novaSenha.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "A nova senha deve ter pelo menos 6 caracteres", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Conecta ao servidor
        if (!authController.conectarServidor()) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao conectar ao servidor", 
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setButtonsEnabled(false);
        
        SwingWorker<AuthController.LoginResult, Void> worker = new SwingWorker<AuthController.LoginResult, Void>() {
            @Override
            protected AuthController.LoginResult doInBackground() throws Exception {
                return authController.recuperarSenha(email, novaSenha);
            }
            
            @Override
            protected void done() {
                try {
                    AuthController.LoginResult result = get();
                    setButtonsEnabled(true);
                    
                    if (result.isSucesso()) {
                        JOptionPane.showMessageDialog(PasswordRecoveryView.this, 
                            result.getMensagem(), 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(PasswordRecoveryView.this, 
                            result.getMensagem(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    setButtonsEnabled(true);
                    JOptionPane.showMessageDialog(PasswordRecoveryView.this, 
                        "Erro interno: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void setButtonsEnabled(boolean enabled) {
        recuperarButton.setEnabled(enabled);
        cancelarButton.setEnabled(enabled);
        emailField.setEnabled(enabled);
        novaSenhaField.setEnabled(enabled);
        confirmarSenhaField.setEnabled(enabled);
    }
}