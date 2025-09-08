package view;

import controller.AuthController;
import util.NetworkClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog para recuperação de senha
 */
public class RecuperarSenhaDialog extends JDialog {
    private AuthController authController;
    private JTextField emailField;
    private JButton enviarButton;
    private JButton cancelarButton;
    
    public RecuperarSenhaDialog(JFrame parent, AuthController authController) {
        super(parent, "Recuperar Senha", true);
        this.authController = authController;
        initComponents();
        setupUI();
        setupEvents();
    }
    
    private void initComponents() {
        setSize(400, 200);
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
        
        // Instruções
        JLabel instrucaoLabel = new JLabel("<html>Digite seu email para receber<br>instruções de recuperação de senha:</html>");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(instrucaoLabel, gbc);
        
        // Email
        JPanel emailPanel = new JPanel(new FlowLayout());
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(emailField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(emailPanel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        enviarButton.setPreferredSize(new Dimension(80, 30));
        cancelarButton.setPreferredSize(new Dimension(80, 30));
        buttonPanel.add(enviarButton);
        buttonPanel.add(cancelarButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
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
            JOptionPane.showMessageDialog(this, "Digite o email", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validação simples de email
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Digite um email válido", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Conecta ao servidor se não estiver conectado
        if (!authController.conectarServidor()) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        enviarButton.setEnabled(false);
        enviarButton.setText("Enviando...");
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return enviarResetSenha(email);
            }
            
            @Override
            protected void done() {
                try {
                    String resultado = get();
                    enviarButton.setEnabled(true);
                    enviarButton.setText("Enviar");
                    
                    if (resultado.startsWith("OK|")) {
                        String mensagem = resultado.substring(3);
                        JOptionPane.showMessageDialog(RecuperarSenhaDialog.this, 
                            mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        String erro = resultado.contains("|") ? resultado.substring(resultado.indexOf("|") + 1) : "Erro desconhecido";
                        JOptionPane.showMessageDialog(RecuperarSenhaDialog.this, 
                            erro, "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    enviarButton.setEnabled(true);
                    enviarButton.setText("Enviar");
                    JOptionPane.showMessageDialog(RecuperarSenhaDialog.this, 
                        "Erro interno: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private String enviarResetSenha(String email) {
        NetworkClient networkClient = authController.getNetworkClient();
        String comando = "RESET_PASSWORD|" + email;
        return networkClient.sendCommand(comando);
    }
}