package com.finanza.desktop.view;

import com.finanza.desktop.controller.AuthController;
import com.finanza.desktop.model.Usuario;
import com.finanza.desktop.ui.ModernUIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Tela do Perfil do Usuário
 * Informações e configurações da conta
 */
public class ProfileView extends JPanel {
    
    // Cores do tema
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color ACCENT_BLUE = ModernUIHelper.ACCENT_BLUE;
    private static final Color POSITIVE_GREEN = ModernUIHelper.POSITIVE_GREEN;
    private static final Color NEGATIVE_RED = ModernUIHelper.NEGATIVE_RED;
    private static final Color WHITE = ModernUIHelper.WHITE;
    private static final Color GRAY = ModernUIHelper.GRAY;
    
    private AuthController authController;
    
    public ProfileView(AuthController authController) {
        this.authController = authController;
        
        initializeComponents();
    }
    
    public void updateProfile() {
        removeAll();
        initializeComponents();
        revalidate();
        repaint();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(PRIMARY_DARK_BLUE);
        
        // Painel central para as informações
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(PRIMARY_DARK_BLUE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Título
        JLabel titleLabel = new JLabel(ModernUIHelper.ICON_PROFILE + " Meu Perfil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);
        
        // Container branco para informações
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        infoPanel.setPreferredSize(new Dimension(500, 400));
        
        GridBagConstraints infoPanelGbc = new GridBagConstraints();
        infoPanelGbc.insets = new Insets(15, 0, 15, 0);
        infoPanelGbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Avatar visual
        JPanel avatarPanel = new JPanel();
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        avatarPanel.setBackground(ACCENT_BLUE);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel avatarLabel = new JLabel(ModernUIHelper.ICON_PROFILE);
        avatarLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        avatarLabel.setForeground(WHITE);
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarPanel.add(avatarLabel);
        
        infoPanelGbc.gridx = 0; infoPanelGbc.gridy = 0;
        infoPanelGbc.anchor = GridBagConstraints.CENTER;
        infoPanel.add(avatarPanel, infoPanelGbc);
        
        // Obter dados do usuário atual
        Usuario usuario = authController.getUsuarioLogado();
        
        if (usuario != null) {
            // Nome do usuário
            JLabel nameLabel = new JLabel(usuario.getNome());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
            nameLabel.setForeground(PRIMARY_DARK_BLUE);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanelGbc.gridy = 1;
            infoPanel.add(nameLabel, infoPanelGbc);
            
            // Email do usuário
            JLabel emailLabel = new JLabel(usuario.getEmail());
            emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emailLabel.setForeground(Color.GRAY);
            emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanelGbc.gridy = 2;
            infoPanel.add(emailLabel, infoPanelGbc);
        } else {
            JLabel errorLabel = new JLabel("Erro ao carregar dados do usuário");
            errorLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            errorLabel.setForeground(NEGATIVE_RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanelGbc.gridy = 1;
            infoPanel.add(errorLabel, infoPanelGbc);
        }
        
        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton editButton = ModernUIHelper.createModernButton("Editar Perfil", ModernUIHelper.ICON_EDIT, ACCENT_BLUE);
        editButton.addActionListener(e -> showEditProfileDialog());
        buttonPanel.add(editButton);
        
        JButton changePasswordButton = ModernUIHelper.createModernButton("Alterar Senha", "🔐", PRIMARY_DARK_BLUE);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        buttonPanel.add(changePasswordButton);
        
        infoPanelGbc.gridy = 3;
        infoPanelGbc.insets = new Insets(30, 0, 0, 0);
        infoPanel.add(buttonPanel, infoPanelGbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        centerPanel.add(infoPanel, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void showEditProfileDialog() {
        Usuario usuario = authController.getUsuarioLogado();
        if (usuario == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Perfil", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo nome
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(nameLabel, gbc);
        
        JTextField nameField = ModernUIHelper.createModernTextField("", 25);
        nameField.setText(usuario.getNome());
        gbc.gridy = 1;
        contentPanel.add(nameField, gbc);
        
        // Campo email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(emailLabel, gbc);
        
        JTextField emailField = ModernUIHelper.createModernTextField("", 25);
        emailField.setText(usuario.getEmail());
        gbc.gridy = 3;
        contentPanel.add(emailField, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", ModernUIHelper.ICON_CANCEL, GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar", ModernUIHelper.ICON_SAVE, POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            String email = emailField.getText().trim();
            
            if (nome.isEmpty()) {
                messageLabel.setText("Por favor, digite o nome.");
                return;
            }
            
            if (email.isEmpty() || !email.contains("@")) {
                messageLabel.setText("Por favor, digite um email válido.");
                return;
            }
            
            if (authController.atualizarPerfil(nome, email)) {
                dialog.dispose();
                updateProfile();
                JOptionPane.showMessageDialog(this, "Perfil atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                messageLabel.setText("Erro ao atualizar perfil. Email pode já estar em uso.");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Alterar Senha", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Senha atual
        JLabel currentPasswordLabel = new JLabel("Senha Atual:");
        currentPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(currentPasswordLabel, gbc);
        
        JPasswordField currentPasswordField = ModernUIHelper.createModernPasswordField("Digite sua senha atual", 25);
        gbc.gridy = 1;
        contentPanel.add(currentPasswordField, gbc);
        
        // Nova senha
        JLabel newPasswordLabel = new JLabel("Nova Senha:");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        contentPanel.add(newPasswordLabel, gbc);
        
        JPasswordField newPasswordField = ModernUIHelper.createModernPasswordField("Digite a nova senha", 25);
        gbc.gridy = 3;
        contentPanel.add(newPasswordField, gbc);
        
        // Confirmar nova senha
        JLabel confirmPasswordLabel = new JLabel("Confirmar Nova Senha:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        contentPanel.add(confirmPasswordLabel, gbc);
        
        JPasswordField confirmPasswordField = ModernUIHelper.createModernPasswordField("Confirme a nova senha", 25);
        gbc.gridy = 5;
        contentPanel.add(confirmPasswordField, gbc);
        
        // Mensagem de erro
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(NEGATIVE_RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        contentPanel.add(messageLabel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);
        
        JButton cancelButton = ModernUIHelper.createModernButton("Cancelar", ModernUIHelper.ICON_CANCEL, GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = ModernUIHelper.createModernButton("Alterar", ModernUIHelper.ICON_SAVE, POSITIVE_GREEN);
        saveButton.addActionListener(e -> {
            String senhaAtual = new String(currentPasswordField.getPassword());
            String novaSenha = new String(newPasswordField.getPassword());
            String confirmarSenha = new String(confirmPasswordField.getPassword());
            
            if (senhaAtual.isEmpty()) {
                messageLabel.setText("Por favor, digite a senha atual.");
                return;
            }
            
            if (novaSenha.isEmpty()) {
                messageLabel.setText("Por favor, digite a nova senha.");
                return;
            }
            
            if (novaSenha.length() < 6) {
                messageLabel.setText("A nova senha deve ter pelo menos 6 caracteres.");
                return;
            }
            
            if (!novaSenha.equals(confirmarSenha)) {
                messageLabel.setText("As senhas não coincidem.");
                return;
            }
            
            if (authController.login(authController.getUsuarioLogado().getEmail(), senhaAtual)) {
                if (authController.alterarSenha(novaSenha)) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    messageLabel.setText("Erro ao alterar senha.");
                }
            } else {
                messageLabel.setText("Senha atual incorreta.");
                currentPasswordField.setText("");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbc);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}