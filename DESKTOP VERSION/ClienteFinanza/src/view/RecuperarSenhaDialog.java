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
public class RecuperarSenhaDialog extends JDialog {
    private JTextField emailField;
    private JButton enviarButton;
    private JButton cancelarButton;
    private AuthController authController;
    
    public RecuperarSenhaDialog(Frame parent, AuthController authController) {
        super(parent, "Recuperar Senha", true);
        this.authController = authController;
        initComponents();
        setupUI();
        setupEvents();
    }
    
    private void initComponents() {
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        emailField = new JTextField(20);
        enviarButton = new JButton("Enviar");
        cancelarButton = new JButton("Cancelar");
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
        
        // Texto explicativo
        JLabel infoLabel = new JLabel("<html><center>Digite seu email para receber<br>uma nova senha temporária</center></html>");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(infoLabel, gbc);
        
        // Email
        JPanel emailPanel = new JPanel(new FlowLayout());
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(emailField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(emailPanel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        enviarButton.setPreferredSize(new Dimension(80, 30));
        cancelarButton.setPreferredSize(new Dimension(80, 30));
        buttonPanel.add(enviarButton);
        buttonPanel.add(cancelarButton);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarRecuperacao();
            }
        });
        
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter no campo email
        emailField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarRecuperacao();
            }
        });
    }
    
    private void enviarRecuperacao() {
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
        
        // Desabilita botão durante processamento
        enviarButton.setEnabled(false);
        enviarButton.setText("Enviando...");
        
        SwingWorker<LoginResult, Void> worker = new SwingWorker<LoginResult, Void>() {
            @Override
            protected LoginResult doInBackground() throws Exception {
                return authController.recuperarSenha(email);
            }
            
            @Override
            protected void done() {
                try {
                    LoginResult result = get();
                    enviarButton.setEnabled(true);
                    enviarButton.setText("Enviar");
                    
                    if (result.isSucesso()) {
                        JOptionPane.showMessageDialog(RecuperarSenhaDialog.this, 
                            result.getMensagem(), 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RecuperarSenhaDialog.this, 
                            result.getMensagem(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    enviarButton.setEnabled(true);
                    enviarButton.setText("Enviar");
                    JOptionPane.showMessageDialog(RecuperarSenhaDialog.this, 
                        "Erro interno: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}