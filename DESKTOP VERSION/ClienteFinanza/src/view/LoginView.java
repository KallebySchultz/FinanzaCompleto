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

    private JTextField nomeField;
    private JButton registrarButton;
    private boolean modoRegistro = false;

    private void initComponents() {
        setTitle("Finanza Desktop - Login Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        // Componentes
        nomeField = new JTextField();
        emailField = new JTextField();
        senhaField = new JPasswordField();
        loginButton = new JButton("Entrar");
        registrarButton = new JButton("Registrar Admin");

        // Opcional: defina o tamanho preferido para consistência visual
        nomeField.setPreferredSize(new Dimension(200, 30));
        emailField.setPreferredSize(new Dimension(200, 30));
        senhaField.setPreferredSize(new Dimension(200, 30));
        loginButton.setPreferredSize(new Dimension(120, 35));
        registrarButton.setPreferredSize(new Dimension(150, 35));
    }

    private JLabel nomeLabel;
    private JPanel nomePanelLabel;
    private JPanel nomePanelField;

    private void setupUI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Finanza Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Nome Label (inicialmente oculto)
        nomeLabel = new JLabel("Nome:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        nomeLabel.setVisible(false);
        mainPanel.add(nomeLabel, gbc);

        // Nome Field (inicialmente oculto)
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nomeField.setVisible(false);
        mainPanel.add(nomeField, gbc);

        // Email Label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Email:"), gbc);

        // Email Field
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(emailField, gbc);

        // Senha Label
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Senha:"), gbc);

        // Senha Field
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(senhaField, gbc);

        // Botão de login
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 5, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(loginButton, gbc);

        // Botão de registro
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(registrarButton, gbc);

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
                if (modoRegistro) {
                    realizarRegistro();
                } else {
                    realizarLogin();
                }
            }
        });

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alternarModoRegistro();
            }
        });

        // Enter nos campos
        ActionListener enterAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modoRegistro) {
                    realizarRegistro();
                } else {
                    realizarLogin();
                }
            }
        };

        nomeField.addActionListener(enterAction);
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
        registrarButton.setEnabled(enabled);
    }

    private void alternarModoRegistro() {
        modoRegistro = !modoRegistro;
        
        if (modoRegistro) {
            // Modo de registro
            setTitle("Finanza Desktop - Registrar Admin");
            nomeLabel.setVisible(true);
            nomeField.setVisible(true);
            loginButton.setText("Criar Conta");
            registrarButton.setText("Voltar ao Login");
            
            // Aviso sobre criação de contas admin
            JOptionPane.showMessageDialog(this, 
                "ATENÇÃO: Novos usuários são criados como usuários comuns.\n" +
                "Para criar um administrador, edite o tipo no banco de dados.\n" +
                "Apenas administradores podem fazer login neste painel.",
                "Informação Importante", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Modo de login
            setTitle("Finanza Desktop - Login Admin");
            nomeLabel.setVisible(false);
            nomeField.setVisible(false);
            nomeField.setText("");
            loginButton.setText("Entrar");
            registrarButton.setText("Registrar Usuário");
        }
        
        pack();
        setLocationRelativeTo(null);
    }

    private void realizarRegistro() {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 6 caracteres", "Erro", JOptionPane.ERROR_MESSAGE);
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
                        JOptionPane.showMessageDialog(LoginView.this, 
                            "Administrador registrado com sucesso!\nFaça login para continuar.", 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        alternarModoRegistro(); // Volta ao modo de login
                        emailField.setText(email);
                        senhaField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(LoginView.this, result.getMensagem(), "Erro de Registro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    setButtonsEnabled(true);
                    JOptionPane.showMessageDialog(LoginView.this, "Erro interno: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void abrirDashboard(model.Usuario usuario) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            AdminDashboardView dashboard = new AdminDashboardView(authController, usuario);
            dashboard.setVisible(true);
        });
    }
}