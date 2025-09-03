package com.finanza.desktop.ui;

import com.finanza.desktop.model.Usuario;
import com.finanza.desktop.database.UsuarioDao;
import com.finanza.desktop.util.UIUtils;
import com.finanza.desktop.util.FormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Tela de cadastro de usuário
 */
public class RegisterFrame extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private UsuarioDao usuarioDao;
    private LoginFrame parentFrame;

    public RegisterFrame(LoginFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.usuarioDao = new UsuarioDao();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureFrame();
    }

    private void initializeComponents() {
        // Campos de entrada
        nameField = new JTextField();
        UIUtils.configureTextField(nameField);

        emailField = new JTextField();
        UIUtils.configureTextField(emailField);

        passwordField = new JPasswordField();
        UIUtils.configurePasswordField(passwordField);

        confirmPasswordField = new JPasswordField();
        UIUtils.configurePasswordField(confirmPasswordField);

        // Botões
        registerButton = new JButton("Criar Conta");
        UIUtils.configureButton(registerButton);

        backButton = new JButton("Voltar");
        UIUtils.configureSecondaryButton(backButton);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Card de registro
        JPanel registerCard = UIUtils.createCard();
        registerCard.setLayout(new GridBagLayout());
        registerCard.setPreferredSize(new Dimension(400, 550));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(10, 20, 10, 20);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        JLabel titleLabel = UIUtils.createTitleLabel("Criar Conta");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerCard.add(titleLabel, cardGbc);

        cardGbc.gridy++;
        JLabel subtitleLabel = UIUtils.createSubtitleLabel("Preencha os dados para criar sua conta");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerCard.add(subtitleLabel, cardGbc);

        // Espaçamento
        cardGbc.gridy++;
        cardGbc.insets = new Insets(20, 20, 10, 20);
        registerCard.add(Box.createVerticalStrut(10), cardGbc);

        // Nome
        cardGbc.gridy++;
        cardGbc.gridwidth = 1;
        cardGbc.insets = new Insets(5, 20, 5, 20);
        JLabel nameLabel = new JLabel("Nome completo:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        registerCard.add(nameLabel, cardGbc);

        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 10, 20);
        registerCard.add(nameField, cardGbc);

        // Email
        cardGbc.gridy++;
        cardGbc.insets = new Insets(5, 20, 5, 20);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        registerCard.add(emailLabel, cardGbc);

        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 10, 20);
        registerCard.add(emailField, cardGbc);

        // Senha
        cardGbc.gridy++;
        cardGbc.insets = new Insets(5, 20, 5, 20);
        JLabel passwordLabel = new JLabel("Senha (mínimo 6 caracteres):");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        registerCard.add(passwordLabel, cardGbc);

        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 10, 20);
        registerCard.add(passwordField, cardGbc);

        // Confirmar senha
        cardGbc.gridy++;
        cardGbc.insets = new Insets(5, 20, 5, 20);
        JLabel confirmPasswordLabel = new JLabel("Confirmar senha:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        registerCard.add(confirmPasswordLabel, cardGbc);

        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 20, 20);
        registerCard.add(confirmPasswordField, cardGbc);

        // Botão de registro
        cardGbc.gridy++;
        cardGbc.insets = new Insets(10, 20, 10, 20);
        registerCard.add(registerButton, cardGbc);

        // Botão voltar
        cardGbc.gridy++;
        cardGbc.insets = new Insets(0, 20, 20, 20);
        registerCard.add(backButton, cardGbc);

        // Adicionar card ao painel principal
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(registerCard, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        registerButton.addActionListener(this::performRegister);
        backButton.addActionListener(this::goBack);
        
        // Enter para registrar
        getRootPane().setDefaultButton(registerButton);
    }

    private void performRegister(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validações
        if (name.isEmpty()) {
            UIUtils.showError(this, "Por favor, digite seu nome");
            nameField.requestFocus();
            return;
        }

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

        if (!FormatUtils.isValidPassword(password)) {
            UIUtils.showError(this, "A senha deve ter pelo menos 6 caracteres");
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            UIUtils.showError(this, "As senhas não coincidem");
            confirmPasswordField.requestFocus();
            return;
        }

        // Desabilitar botão durante registro
        registerButton.setEnabled(false);
        registerButton.setText("Criando conta...");

        try {
            // Verificar se email já existe
            Usuario existingUser = usuarioDao.buscarPorEmail(email);
            if (existingUser != null) {
                UIUtils.showError(this, "Este email já está em uso");
                emailField.requestFocus();
                return;
            }

            // Criar novo usuário
            Usuario novoUsuario = new Usuario(name, email, password);
            int userId = usuarioDao.inserir(novoUsuario);

            if (userId > 0) {
                UIUtils.showSuccess(this, "Conta criada com sucesso! Faça login para continuar.");
                goBack(null);
            } else {
                UIUtils.showError(this, "Erro ao criar conta. Tente novamente.");
            }

        } catch (Exception ex) {
            UIUtils.showError(this, "Erro ao criar conta: " + ex.getMessage());
        } finally {
            registerButton.setEnabled(true);
            registerButton.setText("Criar Conta");
        }
    }

    private void goBack(ActionEvent e) {
        this.dispose();
        parentFrame.setVisible(true);
    }

    private void configureFrame() {
        setTitle("Finanza Desktop - Criar Conta");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
        UIUtils.centerWindow(this);

        // Fechar e voltar para login
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack(null);
            }
        });
    }
}