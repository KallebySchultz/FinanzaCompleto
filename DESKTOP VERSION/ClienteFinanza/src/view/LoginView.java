package view;

import controller.AuthController;
import controller.AuthController.LoginResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela de login para administradores
 */
public class LoginView extends JFrame {
    private AuthController authController;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;

    public LoginView() {
        this.authController = new AuthController();
        initComponents();
        setupUI();
        setupEvents();
    }

    private void initComponents() {
        setTitle("Finanza Desktop - Login Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Componentes
        emailField = new JTextField(20);
        senhaField = new JPasswordField(20);
        loginButton = new JButton("Entrar");
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Finanza Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(titleLabel, gbc);

        // Email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(emailField);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(emailPanel, gbc);

        // Senha
        JPanel senhaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        senhaPanel.add(new JLabel("Senha:"));
        senhaPanel.add(senhaField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(senhaPanel, gbc);

        // Botão de login
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(loginButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Acesso exclusivo para administradores");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        // Enter nos campos
        ActionListener enterAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        };

        emailField.addActionListener(enterAction);
        senhaField.addActionListener(enterAction);
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!authController.conectarServidor()) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setButtonsEnabled(false);

        SwingWorker<LoginResult, Void> worker = new SwingWorker<LoginResult, Void>() {
            @Override
            protected LoginResult doInBackground() throws Exception {
                return authController.login(email, senha);
            }

            @Override
            protected void done() {
                try {
                    LoginResult result = get();
                    setButtonsEnabled(true);

                    if (result.isSucesso()) {
                        abrirDashboard(result.getUsuario());
                    } else {
                        JOptionPane.showMessageDialog(LoginView.this, result.getMensagem(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    setButtonsEnabled(true);
                    JOptionPane.showMessageDialog(LoginView.this, "Erro interno: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
    }

    private void abrirDashboard(model.Usuario usuario) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            AdminDashboardView dashboard = new AdminDashboardView(authController, usuario);
            dashboard.setVisible(true);
        });
    }
}