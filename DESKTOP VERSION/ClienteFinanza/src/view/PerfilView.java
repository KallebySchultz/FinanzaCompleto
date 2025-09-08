package view;

import controller.FinanceController;
import model.Usuario;
import util.ColorScheme;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de perfil do usuário
 */
public class PerfilView extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private JTextField nomeField;
    private JTextField emailField;
    
    public PerfilView(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        
        initComponents();
        setupUI();
        carregarPerfil();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Perfil do Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Perfil do Usuário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Painel do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nomeField = new JTextField(20);
        formPanel.add(nomeField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Botões
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton salvarBtn = new JButton("Salvar Alterações");
        JButton alterarSenhaBtn = new JButton("Alterar Senha");
        
        buttonPanel.add(salvarBtn);
        buttonPanel.add(alterarSenhaBtn);
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pronto");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Eventos
        salvarBtn.addActionListener(e -> salvarPerfil());
        alterarSenhaBtn.addActionListener(e -> abrirFormularioAlterarSenha());
    }
    
    private void carregarPerfil() {
        SwingWorker<Usuario, Void> worker = new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                FinanceController.OperationResult<Usuario> result = financeController.obterPerfil();
                if (result.isSucesso()) {
                    return result.getDados();
                } else {
                    throw new Exception(result.getMensagem());
                }
            }
            
            @Override
            protected void done() {
                try {
                    Usuario perfil = get();
                    nomeField.setText(perfil.getNome());
                    emailField.setText(perfil.getEmail());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PerfilView.this, 
                        "Erro ao carregar perfil: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void salvarPerfil() {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Nome é obrigatório", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Email é obrigatório", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                FinanceController.OperationResult<Void> result = financeController.atualizarPerfil(nome, email);
                if (!result.isSucesso()) {
                    throw new Exception(result.getMensagem());
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(PerfilView.this, 
                        "Perfil atualizado com sucesso!", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PerfilView.this, 
                        "Erro ao atualizar perfil: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void abrirFormularioAlterarSenha() {
        JPasswordField senhaAtualField = new JPasswordField();
        JPasswordField novaSenhaField = new JPasswordField();
        JPasswordField confirmarSenhaField = new JPasswordField();
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Senha atual:"));
        panel.add(senhaAtualField);
        panel.add(new JLabel("Nova senha:"));
        panel.add(novaSenhaField);
        panel.add(new JLabel("Confirmar nova senha:"));
        panel.add(confirmarSenhaField);
        
        int option = JOptionPane.showConfirmDialog(this, panel, "Alterar Senha", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String senhaAtual = new String(senhaAtualField.getPassword());
            String novaSenha = new String(novaSenhaField.getPassword());
            String confirmarSenha = new String(confirmarSenhaField.getPassword());
            
            if (senhaAtual.isEmpty() || novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Todos os campos são obrigatórios", 
                    "Erro de Validação", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!novaSenha.equals(confirmarSenha)) {
                JOptionPane.showMessageDialog(this, 
                    "Nova senha e confirmação não coincidem", 
                    "Erro de Validação", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (novaSenha.length() < 6) {
                JOptionPane.showMessageDialog(this, 
                    "Nova senha deve ter pelo menos 6 caracteres", 
                    "Erro de Validação", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            alterarSenha(senhaAtual, novaSenha);
        }
    }
    
    private void alterarSenha(String senhaAtual, String novaSenha) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                FinanceController.OperationResult<Void> result = financeController.alterarSenha(senhaAtual, novaSenha);
                if (!result.isSucesso()) {
                    throw new Exception(result.getMensagem());
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(PerfilView.this, 
                        "Senha alterada com sucesso!", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PerfilView.this, 
                        "Erro ao alterar senha: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}