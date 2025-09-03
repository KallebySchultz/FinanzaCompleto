package com.finanza.desktop.ui;

import com.finanza.desktop.model.*;
import com.finanza.desktop.database.*;
import com.finanza.desktop.network.FinanzaClient;
import com.finanza.desktop.util.UIUtils;
import com.finanza.desktop.util.FormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Tela de menu/configurações
 */
public class MenuFrame extends JFrame {
    private Usuario usuario;
    private UsuarioDao usuarioDao;
    private ContaDao contaDao;
    private CategoriaDao categoriaDao;
    private LancamentoDao lancamentoDao;

    private JTextField nomeField;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JPasswordField confirmSenhaField;
    private JButton salvarPerfilButton;
    private JButton exportarDadosButton;
    private JButton gerenciarCategoriasButton;
    private JButton sobreButton;
    private JButton closeButton;

    public MenuFrame(Usuario usuario) {
        this.usuario = usuario;
        this.usuarioDao = new UsuarioDao();
        this.contaDao = new ContaDao();
        this.categoriaDao = new CategoriaDao();
        this.lancamentoDao = new LancamentoDao();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        configureFrame();
        loadUserData();
    }

    private void initializeComponents() {
        // Campos do perfil
        nomeField = new JTextField();
        UIUtils.configureTextField(nomeField);

        emailField = new JTextField();
        UIUtils.configureTextField(emailField);

        senhaField = new JPasswordField();
        UIUtils.configurePasswordField(senhaField);

        confirmSenhaField = new JPasswordField();
        UIUtils.configurePasswordField(confirmSenhaField);

        // Botões
        salvarPerfilButton = new JButton("Salvar Perfil");
        UIUtils.configureButton(salvarPerfilButton);

        exportarDadosButton = new JButton("Exportar Dados");
        UIUtils.configureSecondaryButton(exportarDadosButton);

        gerenciarCategoriasButton = new JButton("Gerenciar Categorias");
        UIUtils.configureSecondaryButton(gerenciarCategoriasButton);

        sobreButton = new JButton("Sobre");
        UIUtils.configureSecondaryButton(sobreButton);

        closeButton = new JButton("Fechar");
        UIUtils.configureSecondaryButton(closeButton);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = UIUtils.createTitleLabel("Menu e Configurações");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Seção do perfil
        JPanel profilePanel = createProfilePanel();
        mainPanel.add(profilePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Seção de ferramentas
        JPanel toolsPanel = createToolsPanel();
        mainPanel.add(toolsPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Botão fechar
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(closeButton);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        JPanel panel = UIUtils.createCard();
        panel.setLayout(new GridBagLayout());
        panel.setMaximumSize(new Dimension(600, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título da seção
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel sectionLabel = UIUtils.createSectionLabel("Perfil do Usuário");
        panel.add(sectionLabel, gbc);

        // Nome
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(nomeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(nomeField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(emailField, gbc);

        // Nova senha
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel senhaLabel = new JLabel("Nova Senha:");
        senhaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(senhaLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(senhaField, gbc);

        // Confirmar senha
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel confirmLabel = new JLabel("Confirmar Senha:");
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(confirmSenhaField, gbc);

        // Botão salvar
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(salvarPerfilButton, gbc);

        return panel;
    }

    private JPanel createToolsPanel() {
        JPanel panel = UIUtils.createCard();
        panel.setLayout(new GridBagLayout());
        panel.setMaximumSize(new Dimension(600, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título da seção
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel sectionLabel = UIUtils.createSectionLabel("Ferramentas");
        panel.add(sectionLabel, gbc);

        // Botões de ferramentas
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        panel.add(exportarDadosButton, gbc);

        gbc.gridx = 1;
        panel.add(gerenciarCategoriasButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JButton("Sincronizar com Servidor") {{
            UIUtils.configureSecondaryButton(this);
            addActionListener(e -> mostrarSincronizacao());
        }}, gbc);

        gbc.gridx = 1;
        panel.add(sobreButton, gbc);

        return panel;
    }

    private void setupEventListeners() {
        salvarPerfilButton.addActionListener(this::salvarPerfil);
        exportarDadosButton.addActionListener(this::exportarDados);
        gerenciarCategoriasButton.addActionListener(this::gerenciarCategorias);
        sobreButton.addActionListener(this::mostrarSobre);
        closeButton.addActionListener(e -> dispose());
    }

    private void loadUserData() {
        nomeField.setText(usuario.getNome());
        emailField.setText(usuario.getEmail());
    }

    private void salvarPerfil(ActionEvent e) {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String novaSenha = new String(senhaField.getPassword());
        String confirmSenha = new String(confirmSenhaField.getPassword());

        // Validações
        if (nome.isEmpty()) {
            UIUtils.showError(this, "Por favor, digite seu nome");
            nomeField.requestFocus();
            return;
        }

        if (!FormatUtils.isValidEmail(email)) {
            UIUtils.showError(this, "Por favor, digite um email válido");
            emailField.requestFocus();
            return;
        }

        // Verificar se nova senha foi digitada
        if (!novaSenha.isEmpty()) {
            if (!FormatUtils.isValidPassword(novaSenha)) {
                UIUtils.showError(this, "A senha deve ter pelo menos 6 caracteres");
                senhaField.requestFocus();
                return;
            }

            if (!novaSenha.equals(confirmSenha)) {
                UIUtils.showError(this, "As senhas não coincidem");
                confirmSenhaField.requestFocus();
                return;
            }
        }

        try {
            // Verificar se email já existe (exceto o próprio usuário)
            Usuario existingUser = usuarioDao.buscarPorEmail(email);
            if (existingUser != null && existingUser.getId() != usuario.getId()) {
                UIUtils.showError(this, "Este email já está em uso");
                emailField.requestFocus();
                return;
            }

            // Atualizar dados
            usuario.setNome(nome);
            usuario.setEmail(email);
            
            if (!novaSenha.isEmpty()) {
                usuario.setSenha(novaSenha);
            }

            if (usuarioDao.atualizar(usuario)) {
                UIUtils.showSuccess(this, "Perfil atualizado com sucesso!");
                senhaField.setText("");
                confirmSenhaField.setText("");
            } else {
                UIUtils.showError(this, "Erro ao atualizar perfil");
            }

        } catch (Exception ex) {
            UIUtils.showError(this, "Erro ao atualizar perfil: " + ex.getMessage());
        }
    }

    private void exportarDados(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar relatório de dados");
        fileChooser.setSelectedFile(new java.io.File("relatorio_finanza.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File arquivo = fileChooser.getSelectedFile();
                exportarRelatorio(arquivo);
                UIUtils.showSuccess(this, "Dados exportados com sucesso para: " + arquivo.getAbsolutePath());
            } catch (Exception ex) {
                UIUtils.showError(this, "Erro ao exportar dados: " + ex.getMessage());
            }
        }
    }

    private void exportarRelatorio(java.io.File arquivo) throws IOException {
        try (FileWriter writer = new FileWriter(arquivo)) {
            writer.write("=== RELATÓRIO FINANCEIRO FINANZA ===\n");
            writer.write("Gerado em: " + FormatUtils.formatarDataHora(System.currentTimeMillis()) + "\n");
            writer.write("Usuário: " + usuario.getNome() + "\n\n");

            // Resumo geral
            List<Lancamento> lancamentos = lancamentoDao.listarPorUsuario(usuario.getId());
            double totalReceitas = 0, totalDespesas = 0;

            for (Lancamento lanc : lancamentos) {
                if ("receita".equals(lanc.getTipo())) {
                    totalReceitas += lanc.getValor();
                } else {
                    totalDespesas += lanc.getValor();
                }
            }

            writer.write("RESUMO GERAL:\n");
            writer.write("Total de Receitas: " + FormatUtils.formatarMoeda(totalReceitas) + "\n");
            writer.write("Total de Despesas: " + FormatUtils.formatarMoeda(totalDespesas) + "\n");
            writer.write("Saldo Total: " + FormatUtils.formatarMoeda(totalReceitas - totalDespesas) + "\n");
            writer.write("Total de Transações: " + lancamentos.size() + "\n\n");

            // Contas
            List<Conta> contas = contaDao.listarPorUsuario(usuario.getId());
            writer.write("CONTAS:\n");
            for (Conta conta : contas) {
                double saldoAtual = contaDao.calcularSaldoAtual(conta.getId());
                writer.write("- " + conta.getNome() + ": " + FormatUtils.formatarMoeda(saldoAtual) + "\n");
            }

            writer.write("\nTRANSAÇÕES:\n");
            for (Lancamento lanc : lancamentos) {
                Categoria categoria = categoriaDao.buscarPorId(lanc.getCategoriaId());
                Conta conta = contaDao.buscarPorId(lanc.getContaId());
                
                writer.write(FormatUtils.formatarData(lanc.getData()) + " | " +
                           lanc.getDescricao() + " | " +
                           (categoria != null ? categoria.getNome() : "Sem categoria") + " | " +
                           (conta != null ? conta.getNome() : "Conta não encontrada") + " | " +
                           lanc.getTipo().toUpperCase() + " | " +
                           FormatUtils.formatarMoeda(lanc.getValor()) + "\n");
            }
        }
    }

    private void gerenciarCategorias(ActionEvent e) {
        CategoriesDialog dialog = new CategoriesDialog(this, categoriaDao);
        dialog.setVisible(true);
    }

    private void mostrarSobre(ActionEvent e) {
        String sobre = "Finanza Desktop v1.0.0\n\n" +
            "Sistema de Gestão Financeira Pessoal\n\n" +
            "Desenvolvido em Java com Swing\n" +
            "Banco de dados SQLite\n\n" +
            "Funcionalidades:\n" +
            "• Controle de receitas e despesas\n" +
            "• Gestão de contas bancárias\n" +
            "• Categorização de transações\n" +
            "• Relatórios e exportação de dados\n" +
            "• Interface intuitiva e moderna\n" +
            "• Sincronização com servidor\n\n" +
            "© 2024 Finanza Desktop";

        JTextArea textArea = new JTextArea(sobre);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Sobre o Finanza Desktop", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarSincronizacao() {
        SyncDialog dialog = new SyncDialog(this, usuario);
        dialog.setVisible(true);
    }

    private void configureFrame() {
        setTitle("Finanza Desktop - Menu");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // Dialog para gerenciar categorias
    private static class CategoriesDialog extends JDialog {
        private CategoriaDao categoriaDao;
        private JList<Categoria> categoriesList;
        private DefaultListModel<Categoria> listModel;
        
        public CategoriesDialog(JFrame parent, CategoriaDao categoriaDao) {
            super(parent, "Gerenciar Categorias", true);
            this.categoriaDao = categoriaDao;
            
            initComponents();
            setupLayout();
            loadCategories();
        }
        
        private void initComponents() {
            listModel = new DefaultListModel<>();
            categoriesList = new JList<>(listModel);
            categoriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel titleLabel = UIUtils.createTitleLabel("Categorias");
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            
            JScrollPane scrollPane = new JScrollPane(categoriesList);
            scrollPane.setPreferredSize(new Dimension(300, 400));
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton addButton = new JButton("+ Nova");
            JButton closeButton = new JButton("Fechar");
            
            UIUtils.configureButton(addButton);
            UIUtils.configureSecondaryButton(closeButton);
            
            addButton.addActionListener(e -> addCategory());
            closeButton.addActionListener(e -> dispose());
            
            buttonPanel.add(addButton);
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
            pack();
            setLocationRelativeTo(getParent());
        }
        
        private void loadCategories() {
            listModel.clear();
            List<Categoria> categorias = categoriaDao.listarTodas();
            for (Categoria categoria : categorias) {
                listModel.addElement(categoria);
            }
        }
        
        private void addCategory() {
            String nome = JOptionPane.showInputDialog(this, "Nome da categoria:");
            if (nome != null && !nome.trim().isEmpty()) {
                String[] tipos = {"receita", "despesa"};
                String tipo = (String) JOptionPane.showInputDialog(this, "Tipo:", "Selecionar Tipo", 
                    JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
                
                if (tipo != null) {
                    try {
                        Categoria categoria = new Categoria(nome.trim(), "#4CAF50", tipo);
                        categoriaDao.inserir(categoria);
                        loadCategories();
                        UIUtils.showSuccess(this, "Categoria criada com sucesso!");
                    } catch (Exception ex) {
                        UIUtils.showError(this, "Erro ao criar categoria: " + ex.getMessage());
                    }
                }
            }
        }
    }

    // Dialog para sincronização com servidor
    private static class SyncDialog extends JDialog {
        private FinanzaClient client;
        private Usuario usuario;
        
        private JTextField hostField;
        private JTextField portField;
        private JButton testButton;
        private JButton syncButton;
        private JTextArea logArea;
        
        public SyncDialog(JFrame parent, Usuario usuario) {
            super(parent, "Sincronização com Servidor", true);
            this.usuario = usuario;
            this.client = new FinanzaClient();
            
            initComponents();
            setupLayout();
        }
        
        private void initComponents() {
            hostField = new JTextField("localhost");
            UIUtils.configureTextField(hostField);
            
            portField = new JTextField("8080");
            UIUtils.configureTextField(portField);
            
            testButton = new JButton("Testar Conexão");
            UIUtils.configureSecondaryButton(testButton);
            
            syncButton = new JButton("Sincronizar");
            UIUtils.configureButton(syncButton);
            
            logArea = new JTextArea(10, 40);
            logArea.setEditable(false);
            logArea.setFont(new Font("Courier", Font.PLAIN, 12));
            logArea.setText("Pronto para sincronizar...\n");
            
            testButton.addActionListener(e -> testarConexao());
            syncButton.addActionListener(e -> sincronizar());
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel configPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            gbc.gridx = 0; gbc.gridy = 0;
            configPanel.add(new JLabel("Host:"), gbc);
            gbc.gridx = 1;
            configPanel.add(hostField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            configPanel.add(new JLabel("Porta:"), gbc);
            gbc.gridx = 1;
            configPanel.add(portField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(testButton);
            buttonPanel.add(syncButton);
            configPanel.add(buttonPanel, gbc);
            
            add(configPanel, BorderLayout.NORTH);
            add(new JScrollPane(logArea), BorderLayout.CENTER);
            
            pack();
            setLocationRelativeTo(getParent());
        }
        
        private void testarConexao() {
            String host = hostField.getText().trim();
            String portStr = portField.getText().trim();
            
            try {
                int port = Integer.parseInt(portStr);
                logArea.append("🔄 Testando conexão com " + host + ":" + port + "...\n");
                
                if (client.testConnection(host, port)) {
                    logArea.append("✅ Conexão bem-sucedida!\n");
                    FinanzaClient.ServerResponse info = client.getServerInfo(host, port);
                    if (info.isSuccess()) {
                        logArea.append("📋 Servidor: " + info.getMessage() + "\n");
                    }
                } else {
                    logArea.append("❌ Falha na conexão\n");
                }
            } catch (NumberFormatException e) {
                logArea.append("❌ Porta inválida\n");
            }
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
        
        private void sincronizar() {
            String host = hostField.getText().trim();
            String portStr = portField.getText().trim();
            
            try {
                int port = Integer.parseInt(portStr);
                logArea.append("🚀 Iniciando sincronização...\n");
                
                // Sincronizar usuário
                FinanzaClient.ServerResponse response = client.syncUser(usuario, host, port);
                if (response.isSuccess()) {
                    logArea.append("✅ Usuário sincronizado: " + response.getMessage() + "\n");
                } else {
                    logArea.append("❌ Erro ao sincronizar usuário: " + response.getMessage() + "\n");
                }
                
                logArea.append("🎉 Sincronização concluída!\n");
                
            } catch (NumberFormatException e) {
                logArea.append("❌ Porta inválida\n");
            }
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }
}