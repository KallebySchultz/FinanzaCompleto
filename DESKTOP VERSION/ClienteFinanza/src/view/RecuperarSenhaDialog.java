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
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(200, 25));
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        enviarButton = new JButton("Enviar");
        enviarButton.setPreferredSize(new Dimension(100, 35));
        enviarButton.setFont(new Font("Arial", Font.BOLD, 12));
        enviarButton.setBackground(new Color(0, 102, 204));
        enviarButton.setForeground(Color.WHITE);
        enviarButton.setFocusPainted(false);
        enviarButton.setBorderPainted(false);
        enviarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setPreferredSize(new Dimension(100, 35));
        cancelarButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelarButton.setBackground(new Color(220, 220, 220));
        cancelarButton.setForeground(Color.BLACK);
        cancelarButton.setFocusPainted(false);
        cancelarButton.setBorderPainted(false);
        cancelarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Ícone/Título principal
        JLabel titleLabel = new JLabel("Recuperação de Senha", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Subtítulo/Instruções mais elaborado
        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<p style='margin: 0; font-size: 12px; color: #666666;'>" +
            "Digite seu email para receber instruções de<br>" +
            "recuperação de senha em sua caixa de entrada." +
            "</p></div></html>", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(subtitleLabel, gbc);
        
        // Painel do campo email com label
        JPanel emailContainerPanel = new JPanel(new BorderLayout(10, 5));
        emailContainerPanel.setBackground(Color.WHITE);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        emailLabel.setForeground(new Color(60, 60, 60));
        emailContainerPanel.add(emailLabel, BorderLayout.NORTH);
        emailContainerPanel.add(emailField, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(emailContainerPanel, gbc);
        
        // Painel de botões melhorado
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(enviarButton);
        buttonPanel.add(cancelarButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        // Painel de rodapé com informações adicionais
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<p style='margin: 0; font-size: 10px; color: #999999;'>" +
            "Caso não receba o email, verifique sua caixa de spam<br>" +
            "ou entre em contato com o suporte." +
            "</p></div></html>", SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(infoLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Adiciona borda superior colorida para dar um toque visual
        JPanel topBorder = new JPanel();
        topBorder.setBackground(new Color(0, 102, 204));
        topBorder.setPreferredSize(new Dimension(0, 4));
        add(topBorder, BorderLayout.NORTH);
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
            showError("Digite o email para recuperação de senha");
            return;
        }
        
        // Validação simples de email
        if (!email.contains("@") || !email.contains(".")) {
            showError("Digite um email válido (exemplo: usuario@email.com)");
            return;
        }
        
        // Conecta ao servidor se não estiver conectado
        if (!authController.conectarServidor()) {
            showError("Erro ao conectar ao servidor.\nVerifique sua conexão e tente novamente.");
            return;
        }
        
        // Desabilita botão e mostra estado de carregamento
        enviarButton.setEnabled(false);
        enviarButton.setText("Enviando...");
        emailField.setEnabled(false);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return enviarResetSenha(email);
            }
            
            @Override
            protected void done() {
                try {
                    String resultado = get();
                    
                    // Reabilita componentes
                    enviarButton.setEnabled(true);
                    enviarButton.setText("Enviar");
                    emailField.setEnabled(true);
                    
                    if (resultado.startsWith("OK|")) {
                        String mensagem = resultado.substring(3);
                        showSuccess("Email enviado com sucesso!\n\n" + mensagem);
                        dispose();
                    } else {
                        String erro = resultado.contains("|") ? resultado.substring(resultado.indexOf("|") + 1) : "Erro desconhecido";
                        showError("Falha ao enviar email de recuperação:\n\n" + erro);
                    }
                } catch (Exception e) {
                    // Reabilita componentes em caso de erro
                    enviarButton.setEnabled(true);
                    enviarButton.setText("Enviar");
                    emailField.setEnabled(true);
                    showError("Erro interno:\n\n" + e.getMessage());
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
    
    /**
     * Mostra mensagem de erro com estilo consistente
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        emailField.requestFocus();
        emailField.selectAll();
    }
    
    /**
     * Mostra mensagem de sucesso com estilo consistente
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}