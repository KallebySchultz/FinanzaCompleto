package view;

import controller.AuthController;
import controller.AuthController.LoginResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

/**
 * Tela de login e registro do sistema
 */
public class LoginView extends JFrame {
    private AuthController authController;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JTextField nomeField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton toggleButton;
    private JButton recuperarSenhaButton; // NOVO
    private JPanel registerPanel;
    private boolean isLoginMode = true;

    public LoginView() {
        this.authController = new AuthController();
        initComponents();
        setupUI();
        setupEvents();
    }

    private void initComponents() {
        setTitle("Finanza Desktop - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Componentes
        emailField = new JTextField(20);
        senhaField = new JPasswordField(20);
        nomeField = new JTextField(20);
        loginButton = new JButton("Entrar");
        registerButton = new JButton("Cadastrar");
        toggleButton = new JButton("Não tem conta? Cadastre-se");
        recuperarSenhaButton = new JButton("Esqueceu a senha?"); // NOVO
        recuperarSenhaButton.setForeground(new Color(0, 102, 204));
        recuperarSenhaButton.setBorderPainted(false); // Estilo de link
        recuperarSenhaButton.setContentAreaFilled(false);
        recuperarSenhaButton.setFocusPainted(false);
        recuperarSenhaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Finanza Desktop", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(titleLabel, gbc);

        // Painel de registro (inicialmente oculto)
        registerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        registerPanel.add(new JLabel("Nome:"));
        registerPanel.add(nomeField);
        registerPanel.setVisible(false);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(registerPanel, gbc);

        // Email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(emailField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(emailPanel, gbc);

        // Senha
        JPanel senhaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        senhaPanel.add(new JLabel("Senha:"));
        senhaPanel.add(senhaField);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(senhaPanel, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setVisible(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // NOVO: Botão de recuperar senha
        if (isLoginMode) {
            buttonPanel.add(recuperarSenhaButton);
        }
        recuperarSenhaButton.setVisible(isLoginMode);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(buttonPanel, gbc);

        // Botão alternar modo
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        mainPanel.add(toggleButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pronto para conectar");
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

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarRegistro();
            }
        });

        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMode();
            }
        });

        recuperarSenhaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirRecuperarSenha();
            }
        });

        // Enter nos campos
        ActionListener enterAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoginMode) {
                    realizarLogin();
                } else {
                    realizarRegistro();
                }
            }
        };

        emailField.addActionListener(enterAction);
        senhaField.addActionListener(enterAction);
        nomeField.addActionListener(enterAction);
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;

        if (isLoginMode) {
            setTitle("Finanza Desktop - Login");
            registerPanel.setVisible(false);
            loginButton.setVisible(true);
            registerButton.setVisible(false);
            toggleButton.setText("Não tem conta? Cadastre-se");
            recuperarSenhaButton.setVisible(true);
        } else {
            setTitle("Finanza Desktop - Cadastro");
            registerPanel.setVisible(true);
            loginButton.setVisible(false);
            registerButton.setVisible(true);
            toggleButton.setText("Já tem conta? Faça login");
            recuperarSenhaButton.setVisible(false);
        }

        pack();
        setLocationRelativeTo(null);
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

    private void realizarRegistro() {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
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
                return authController.registrar(nome, email, senha);
            }

            @Override
            protected void done() {
                try {
                    LoginResult result = get();
                    setButtonsEnabled(true);

                    if (result.isSucesso()) {
                        abrirDashboard(result.getUsuario());
                    } else {
                        JOptionPane.showMessageDialog(LoginView.this, result.getMensagem(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
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
        registerButton.setEnabled(enabled);
        toggleButton.setEnabled(enabled);
        recuperarSenhaButton.setEnabled(enabled);
    }

    private void abrirDashboard(model.Usuario usuario) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            DashboardView dashboard = new DashboardView(authController, usuario);
            dashboard.setVisible(true);
        });
    }

    private void abrirRecuperarSenha() {
        try {
            Desktop.getDesktop().browse(new URI("https://finanza.example.com/recuperar-senha"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir a página de recuperação de senha.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}