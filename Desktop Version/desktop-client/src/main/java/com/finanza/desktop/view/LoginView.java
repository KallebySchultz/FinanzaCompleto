package com.finanza.desktop.view;

import com.finanza.desktop.controller.AuthController;
import com.finanza.desktop.ui.ModernUIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de Login/Registro
 * Interface de autenticação do usuário
 */
public class LoginView extends JPanel {
    
    // Cores do tema
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color ACCENT_BLUE = ModernUIHelper.ACCENT_BLUE;
    private static final Color NEGATIVE_RED = ModernUIHelper.NEGATIVE_RED;
    private static final Color WHITE = ModernUIHelper.WHITE;
    
    private AuthController authController;
    public interface OnLoginSuccessListener {
        void onLoginSuccess();
    }
    
    private OnLoginSuccessListener onLoginSuccessListener;
    
    public LoginView(AuthController authController, OnLoginSuccessListener onLoginSuccessListener) {
        this.authController = authController;
        this.onLoginSuccessListener = onLoginSuccessListener;
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(PRIMARY_DARK_BLUE);
        
        // Painel central para formulário
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(PRIMARY_DARK_BLUE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Logo/Título
        JLabel titleLabel = new JLabel("Finanza");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Sistema de Gestão Financeira");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        centerPanel.add(subtitleLabel, gbc);
        
        // Painel do formulário (branco)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        formPanel.setPreferredSize(new Dimension(450, 400));
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 0, 10, 0);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.anchor = GridBagConstraints.CENTER;
        
        // Tab para Login/Registro
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Tab de Login
        JPanel loginTab = createLoginTab();
        tabbedPane.addTab("Entrar", loginTab);
        
        // Tab de Registro
        JPanel registerTab = createRegisterTab();
        tabbedPane.addTab("Criar Conta", registerTab);
        
        formGbc.gridx = 0; formGbc.gridy = 0;
        formGbc.weightx = 1.0; formGbc.weighty = 1.0;
        formGbc.fill = GridBagConstraints.BOTH;
        formPanel.add(tabbedPane, formGbc);
        
        gbc.gridy = 2; gbc.gridwidth = 1;
        centerPanel.add(formPanel, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createLoginTab() {
        JPanel loginTab = new JPanel(new GridBagLayout());
        loginTab.setBackground(WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Campo email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        loginTab.add(emailLabel, gbc);
        
        JTextField emailField = ModernUIHelper.createModernTextField("Digite seu email", 20);
        gbc.gridy = 1;
        loginTab.add(emailField, gbc);
        
        // Campo senha
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        loginTab.add(passwordLabel, gbc);
        
        JPasswordField passwordField = ModernUIHelper.createModernPasswordField("Digite sua senha", 20);
        gbc.gridy = 3;
        loginTab.add(passwordField, gbc);
        
        // Label para mensagens de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        loginTab.add(messageLabel, gbc);
        
        // Botão login
        JButton loginButton = ModernUIHelper.createModernButton("Entrar", ModernUIHelper.ICON_SUCCESS, ACCENT_BLUE);
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String senha = new String(passwordField.getPassword());
            
            if (email.isEmpty() || senha.isEmpty()) {
                messageLabel.setText("Por favor, preencha todos os campos.");
                return;
            }
            
            if (authController.login(email, senha)) {
                messageLabel.setText(" ");
                onLoginSuccessListener.onLoginSuccess();
            } else {
                messageLabel.setText("Email ou senha incorretos.");
                passwordField.setText("");
            }
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 20, 10, 20);
        loginTab.add(loginButton, gbc);
        
        return loginTab;
    }

    private JPanel createRegisterTab() {
        JPanel registerTab = new JPanel(new GridBagLayout());
        registerTab.setBackground(WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Campo nome
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        registerTab.add(nameLabel, gbc);
        
        JTextField nameField = ModernUIHelper.createModernTextField("Digite seu nome completo", 20);
        gbc.gridy = 1;
        registerTab.add(nameField, gbc);
        
        // Campo email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        registerTab.add(emailLabel, gbc);
        
        JTextField emailField = ModernUIHelper.createModernTextField("Digite seu email", 20);
        gbc.gridy = 3;
        registerTab.add(emailField, gbc);
        
        // Campo senha
        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 4;
        registerTab.add(passwordLabel, gbc);
        
        JPasswordField passwordField = ModernUIHelper.createModernPasswordField("Digite sua senha", 20);
        gbc.gridy = 5;
        registerTab.add(passwordField, gbc);
        
        // Campo confirmar senha
        JLabel confirmPasswordLabel = new JLabel("Confirmar Senha:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 6;
        registerTab.add(confirmPasswordLabel, gbc);
        
        JPasswordField confirmPasswordField = ModernUIHelper.createModernPasswordField("Confirme sua senha", 20);
        gbc.gridy = 7;
        registerTab.add(confirmPasswordField, gbc);
        
        // Label para mensagens de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8;
        registerTab.add(messageLabel, gbc);
        
        // Botão registro
        JButton registerButton = ModernUIHelper.createModernButton("Criar Conta", ModernUIHelper.ICON_USER_ADD, ModernUIHelper.POSITIVE_GREEN);
        registerButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            String email = emailField.getText().trim();
            String senha = new String(passwordField.getPassword());
            String confirmarSenha = new String(confirmPasswordField.getPassword());
            
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                messageLabel.setText("Por favor, preencha todos os campos.");
                return;
            }
            
            if (!senha.equals(confirmarSenha)) {
                messageLabel.setText("As senhas não coincidem.");
                return;
            }
            
            if (senha.length() < 6) {
                messageLabel.setText("A senha deve ter pelo menos 6 caracteres.");
                return;
            }
            
            if (!email.contains("@")) {
                messageLabel.setText("Por favor, digite um email válido.");
                return;
            }
            
            if (authController.registrar(nome, email, senha)) {
                messageLabel.setText(" ");
                JOptionPane.showMessageDialog(this, "Conta criada com sucesso!\nFaça login para continuar.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // Limpar campos
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } else {
                messageLabel.setText("Erro ao criar conta. Email pode já estar em uso.");
            }
        });
        gbc.gridy = 9;
        gbc.insets = new Insets(15, 20, 10, 20);
        registerTab.add(registerButton, gbc);
        
        return registerTab;
    }
}