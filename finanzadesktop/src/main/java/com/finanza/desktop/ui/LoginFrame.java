package com.finanza.desktop.ui;

import com.finanza.desktop.model.Usuario;
import com.finanza.desktop.database.UsuarioDao;
import com.finanza.desktop.util.UIUtils;
import com.finanza.desktop.util.FormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela de login da aplicação
 */
public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UsuarioDao usuarioDao;

    public LoginFrame() {
        this.usuarioDao = new UsuarioDao();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureFrame();
    }

    private void initializeComponents() {
        // Logo/Título
        JLabel titleLabel = UIUtils.createTitleLabel("Finanza Desktop");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = UIUtils.createSubtitleLabel("Gerencie suas finanças de forma simples e eficiente");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Campos de entrada
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        emailField = new JTextField();
        UIUtils.configureTextField(emailField);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        passwordField = new JPasswordField();
        UIUtils.configurePasswordField(passwordField);

        // Botões
        loginButton = new JButton("Entrar");
        UIUtils.configureButton(loginButton);

        registerButton = new JButton("Criar Conta");
        UIUtils.configureSecondaryButton(registerButton);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Card de login
        JPanel loginCard = UIUtils.createCard();
        loginCard.setLayout(new GridBagLayout());
        loginCard.setPreferredSize(new Dimension(400, 450));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(10, 20, 10, 20);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        JLabel titleLabel = UIUtils.createTitleLabel("Finanza Desktop");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginCard.add(titleLabel, cardGbc);

        cardGbc.gridy++;
        JLabel subtitleLabel = UIUtils.createSubtitleLabel("Faça login para continuar");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginCard.add(subtitleLabel, cardGbc);

        // Espaçamento
        cardGbc.gridy++;
        cardGbc.insets = new Insets(20, 20, 10, 20);
        loginCard.add(Box.createVerticalStrut(10), cardGbc);

        // Email
        cardGbc.gridy++;
        cardGbc.gridwidth = 1;
        cardGbc.insets = new Insets(5, 20, 5, 20);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        loginCard.add(emailLabel, cardGbc);

        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 10, 20);
        loginCard.add(emailField, cardGbc);

        // Senha
        cardGbc.gridy++;
        cardGbc.insets = new Insets(5, 20, 5, 20);
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        loginCard.add(passwordLabel, cardGbc);

        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 20, 20);
        loginCard.add(passwordField, cardGbc);

        // Botão de login
        cardGbc.gridy++;
        cardGbc.insets = new Insets(10, 20, 10, 20);
        loginCard.add(loginButton, cardGbc);

        // Botão de registro
        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 20, 20);
        loginCard.add(registerButton, cardGbc);

        // Adicionar card ao painel principal
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(loginCard, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        loginButton.addActionListener(this::performLogin);
        registerButton.addActionListener(this::openRegisterWindow);
        
        // Enter para fazer login
        getRootPane().setDefaultButton(loginButton);
    }

    private void performLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validações
        if (email.isEmpty()) {
            UIUtils.showError(this, "Por favor, digite seu email");
            emailField.requestFocus();
            return;
        }

        if (!FormatUtils.isValidEmail(email)) {
            UIUtils.showError(this, "Por favor, digite um email válido");
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            UIUtils.showError(this, "Por favor, digite sua senha");
            passwordField.requestFocus();
            return;
        }

        // Desabilitar botão durante autenticação
        loginButton.setEnabled(false);
        loginButton.setText("Entrando...");

        try {
            Usuario usuario = usuarioDao.autenticar(email, password);
            
            if (usuario != null) {
                // Login bem-sucedido
                this.dispose();
                new MainFrame(usuario).setVisible(true);
            } else {
                UIUtils.showError(this, "Email ou senha incorretos");
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Erro ao fazer login: " + ex.getMessage());
        } finally {
            loginButton.setEnabled(true);
            loginButton.setText("Entrar");
        }
    }

    private void openRegisterWindow(ActionEvent e) {
        new RegisterFrame(this).setVisible(true);
        this.setVisible(false);
    }

    private void configureFrame() {
        setTitle("Finanza Desktop - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        UIUtils.centerWindow(this);

        // Ícone da aplicação (placeholder)
        try {
            // setIconImage(...); // Adicionar ícone quando disponível
        } catch (Exception e) {
            // Ignorar se não conseguir carregar ícone
        }
    }
}