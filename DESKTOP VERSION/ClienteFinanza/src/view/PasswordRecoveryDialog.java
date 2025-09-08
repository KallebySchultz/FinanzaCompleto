package view;

import controller.AuthController;
import controller.AuthController.LoginResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog para recuperação de senha
 */
public class PasswordRecoveryDialog extends JDialog {
    private AuthController authController;
    private JTextField emailField;
    private JTextField tokenField;
    private JPasswordField novaSenhaField;
    private JPasswordField confirmarSenhaField;
    private JButton solicitarTokenButton;
    private JButton redefinirSenhaButton;
    private JButton cancelarButton;
    private JPanel tokenPanel;
    private boolean tokenSolicitado = false;
    
    public PasswordRecoveryDialog(Frame parent, AuthController authController) {
        super(parent, "Recuperar Senha", true);
        this.authController = authController;
        initComponents();
        setupUI();
        setupEvents();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Componentes
        emailField = new JTextField(20);
        tokenField = new JTextField(20);
        novaSenhaField = new JPasswordField(20);
        confirmarSenhaField = new JPasswordField(20);
        solicitarTokenButton = new JButton("Solicitar Código");
        redefinirSenhaButton = new JButton("Redefinir Senha");
        cancelarButton = new JButton("Cancelar");
        
        // Painel de token (inicialmente oculto)
        tokenPanel = new JPanel();
        tokenPanel.setVisible(false);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Recuperação de Senha", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Instruções
        JLabel instrucaoLabel = new JLabel("<html><center>Digite seu email para receber<br>um código de recuperação</center></html>", SwingConstants.CENTER);
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
        
        // Painel de token e nova senha
        setupTokenPanel();
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(tokenPanel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        solicitarTokenButton.setPreferredSize(new Dimension(120, 30));
        redefinirSenhaButton.setPreferredSize(new Dimension(120, 30));
        cancelarButton.setPreferredSize(new Dimension(80, 30));
        redefinirSenhaButton.setVisible(false);
        
        buttonPanel.add(solicitarTokenButton);
        buttonPanel.add(redefinirSenhaButton);
        buttonPanel.add(cancelarButton);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupTokenPanel() {
        tokenPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Código de recuperação
        JPanel codigoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        codigoPanel.add(new JLabel("Código:"));
        codigoPanel.add(tokenField);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        tokenPanel.add(codigoPanel, gbc);
        
        // Nova senha
        JPanel novaSenhaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        novaSenhaPanel.add(new JLabel("Nova senha:"));
        novaSenhaPanel.add(novaSenhaField);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        tokenPanel.add(novaSenhaPanel, gbc);
        
        // Confirmar senha
        JPanel confirmarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmarPanel.add(new JLabel("Confirmar:"));
        confirmarPanel.add(confirmarSenhaField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        tokenPanel.add(confirmarPanel, gbc);
    }
    
    private void setupEvents() {
        solicitarTokenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solicitarToken();
            }
        });
        
        redefinirSenhaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redefinirSenha();
            }
        });
        
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter nos campos
        emailField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!tokenSolicitado) {
                    solicitarToken();
                }
            }
        });
        
        tokenField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redefinirSenha();
            }
        });
        
        novaSenhaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redefinirSenha();
            }
        });
        
        confirmarSenhaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redefinirSenha();
            }
        });
    }
    
    private void solicitarToken() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite seu email", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Conecta ao servidor se necessário
        if (!authController.conectarServidor()) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Desabilita botão durante solicitação
        solicitarTokenButton.setEnabled(false);
        
        SwingWorker<LoginResult, Void> worker = new SwingWorker<LoginResult, Void>() {
            @Override
            protected LoginResult doInBackground() throws Exception {
                return authController.solicitarRecuperacaoSenha(email);
            }
            
            @Override
            protected void done() {
                try {
                    LoginResult result = get();
                    solicitarTokenButton.setEnabled(true);
                    
                    if (result.isSucesso()) {
                        // Mostra o código gerado
                        String mensagem = result.getMensagem();
                        JOptionPane.showMessageDialog(PasswordRecoveryDialog.this, 
                            mensagem, 
                            "Código de Recuperação", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Mostra campos para redefinir senha
                        tokenSolicitado = true;
                        tokenPanel.setVisible(true);
                        solicitarTokenButton.setVisible(false);
                        redefinirSenhaButton.setVisible(true);
                        emailField.setEnabled(false);
                        pack();
                        setLocationRelativeTo(getParent());
                    } else {
                        JOptionPane.showMessageDialog(PasswordRecoveryDialog.this, 
                            result.getMensagem(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    solicitarTokenButton.setEnabled(true);
                    JOptionPane.showMessageDialog(PasswordRecoveryDialog.this, 
                        "Erro interno: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void redefinirSenha() {
        String token = tokenField.getText().trim();
        String novaSenha = new String(novaSenhaField.getPassword());
        String confirmarSenha = new String(confirmarSenhaField.getPassword());
        
        if (token.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o código de recuperação", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite a nova senha", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!novaSenha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this, "Senhas não coincidem", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (novaSenha.length() < 6) {
            JOptionPane.showMessageDialog(this, "Nova senha deve ter pelo menos 6 caracteres", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Desabilita botão durante redefinição
        redefinirSenhaButton.setEnabled(false);
        
        SwingWorker<LoginResult, Void> worker = new SwingWorker<LoginResult, Void>() {
            @Override
            protected LoginResult doInBackground() throws Exception {
                return authController.redefinirSenha(token, novaSenha);
            }
            
            @Override
            protected void done() {
                try {
                    LoginResult result = get();
                    redefinirSenhaButton.setEnabled(true);
                    
                    if (result.isSucesso()) {
                        JOptionPane.showMessageDialog(PasswordRecoveryDialog.this, 
                            "Senha redefinida com sucesso!", 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(PasswordRecoveryDialog.this, 
                            result.getMensagem(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    redefinirSenhaButton.setEnabled(true);
                    JOptionPane.showMessageDialog(PasswordRecoveryDialog.this, 
                        "Erro interno: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}