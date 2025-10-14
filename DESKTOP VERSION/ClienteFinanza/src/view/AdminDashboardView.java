package view;

import controller.AuthController;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tela principal de administração para gerenciar usuários e seus dados
 */
public class AdminDashboardView extends JFrame {
    private AuthController authController;
    private Usuario adminUsuario;
    
    // Componentes principais
    private JTabbedPane tabbedPane;
    private JLabel totalUsersLabel;
    private JButton editProfileButton;
    private JButton logoutButton;
    
    // Aba de Usuários
    private JTable usuariosTable;
    private DefaultTableModel usuariosTableModel;
    private JButton refreshUsersButton;
    private JButton editUserButton;
    private JButton deleteUserButton;
    private JTextField searchUsersField;
    private List<Usuario> todosUsuarios;
    
    // Aba de Contas
    private JTable contasTable;
    private DefaultTableModel contasTableModel;
    private JButton refreshContasButton;
    private JButton editContaButton;
    private JButton deleteContaButton;
    private JComboBox<String> userFilterContas;
    private JTextField searchContasField;
    private List<Object[]> todasContas;
    
    // Aba de Categorias
    private JTable categoriasTable;
    private DefaultTableModel categoriasTableModel;
    private JButton refreshCategoriasButton;
    private JButton editCategoriaButton;
    private JButton deleteCategoriaButton;
    private JComboBox<String> userFilterCategorias;
    private JTextField searchCategoriasField;
    private List<Object[]> todasCategorias;
    
    // Aba de Movimentações
    private JTable movimentacoesTable;
    private DefaultTableModel movimentacoesTableModel;
    private JButton refreshMovimentacoesButton;
    private JButton editMovimentacaoButton;
    private JButton deleteMovimentacaoButton;
    private JComboBox<String> userFilterMovimentacoes;
    private JTextField searchMovimentacoesField;
    private List<Object[]> todasMovimentacoes;
    
    public AdminDashboardView(AuthController authController, Usuario usuario) {
        this.authController = authController;
        this.adminUsuario = usuario;
        initComponents();
        setupUI();
        setupEvents();
        carregarUsuarios();
    }
    
    private void initComponents() {
        setTitle("Finanza Admin - Painel de Administração Completo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        todosUsuarios = new java.util.ArrayList<>();
        todasContas = new java.util.ArrayList<>();
        todasCategorias = new java.util.ArrayList<>();
        todasMovimentacoes = new java.util.ArrayList<>();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 248, 255));
        
        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setOpaque(false);
        JLabel titleLabel = new JLabel("Painel de Administração Completo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalUsersLabel = new JLabel("Total de usuários: 0");
        totalUsersLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        headerLeft.add(titleLabel);
        headerLeft.add(totalUsersLabel);
        headerPanel.add(headerLeft, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel("Admin: " + adminUsuario.getNome() + " (" + adminUsuario.getEmail() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Criar abas
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Usuários", createUsuariosPanel());
        tabbedPane.addTab("Contas", createContasPanel());
        tabbedPane.addTab("Categorias", createCategoriasPanel());
        tabbedPane.addTab("Movimentações", createMovimentacoesPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer com botões de ações
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        editProfileButton = new JButton("Editar Meu Perfil");
        logoutButton = new JButton("Sair");
        
        footerPanel.add(editProfileButton);
        footerPanel.add(logoutButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createUsuariosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchUsersField = new JTextField(30);
        searchPanel.add(searchUsersField);
        JButton searchButton = new JButton("Filtrar");
        searchButton.addActionListener(e -> filtrarUsuarios());
        searchPanel.add(searchButton);
        JButton clearButton = new JButton("Limpar");
        clearButton.addActionListener(e -> {
            searchUsersField.setText("");
            filtrarUsuarios();
        });
        searchPanel.add(clearButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabela de usuários
        String[] columnNames = {"ID", "Nome", "Email", "Data de Criação"};
        usuariosTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usuariosTable = new JTable(usuariosTableModel);
        usuariosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usuariosTable.getTableHeader().setReorderingAllowed(false);
        usuariosTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(usuariosTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshUsersButton = new JButton("Atualizar Lista");
        editUserButton = new JButton("Editar");
        editUserButton.setEnabled(false);
        deleteUserButton = new JButton("Excluir");
        deleteUserButton.setEnabled(false);
        deleteUserButton.setForeground(Color.RED);
        
        buttonPanel.add(refreshUsersButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(deleteUserButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createContasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior com filtros e busca
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Painel de filtro por usuário
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrar por usuário:"));
        userFilterContas = new JComboBox<>();
        userFilterContas.addItem("Todos");
        filterPanel.add(userFilterContas);
        JButton filterButton = new JButton("Filtrar");
        filterButton.addActionListener(e -> carregarContas());
        filterPanel.add(filterButton);
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchContasField = new JTextField(30);
        searchPanel.add(searchContasField);
        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> filtrarContas());
        searchPanel.add(searchButton);
        JButton clearButton = new JButton("Limpar");
        clearButton.addActionListener(e -> {
            searchContasField.setText("");
            filtrarContas();
        });
        searchPanel.add(clearButton);
        
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tabela de contas
        String[] columnNames = {"ID", "Nome", "Saldo Inicial", "Usuário", "Data de Criação"};
        contasTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        contasTable = new JTable(contasTableModel);
        contasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contasTable.getTableHeader().setReorderingAllowed(false);
        contasTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(contasTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshContasButton = new JButton("Atualizar Lista");
        editContaButton = new JButton("Editar");
        editContaButton.setEnabled(false);
        deleteContaButton = new JButton("Excluir");
        deleteContaButton.setEnabled(false);
        deleteContaButton.setForeground(Color.RED);
        
        buttonPanel.add(refreshContasButton);
        buttonPanel.add(editContaButton);
        buttonPanel.add(deleteContaButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCategoriasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior com filtros e busca
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Painel de filtro por usuário
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrar por usuário:"));
        userFilterCategorias = new JComboBox<>();
        userFilterCategorias.addItem("Todos");
        filterPanel.add(userFilterCategorias);
        JButton filterButton = new JButton("Filtrar");
        filterButton.addActionListener(e -> carregarCategorias());
        filterPanel.add(filterButton);
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchCategoriasField = new JTextField(30);
        searchPanel.add(searchCategoriasField);
        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> filtrarCategorias());
        searchPanel.add(searchButton);
        JButton clearButton = new JButton("Limpar");
        clearButton.addActionListener(e -> {
            searchCategoriasField.setText("");
            filtrarCategorias();
        });
        searchPanel.add(clearButton);
        
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tabela de categorias
        String[] columnNames = {"ID", "Nome", "Tipo", "Usuário"};
        categoriasTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoriasTable = new JTable(categoriasTableModel);
        categoriasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoriasTable.getTableHeader().setReorderingAllowed(false);
        categoriasTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(categoriasTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshCategoriasButton = new JButton("Atualizar Lista");
        editCategoriaButton = new JButton("Editar");
        editCategoriaButton.setEnabled(false);
        deleteCategoriaButton = new JButton("Excluir");
        deleteCategoriaButton.setEnabled(false);
        deleteCategoriaButton.setForeground(Color.RED);
        
        buttonPanel.add(refreshCategoriasButton);
        buttonPanel.add(editCategoriaButton);
        buttonPanel.add(deleteCategoriaButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMovimentacoesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior com filtros e busca
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Painel de filtro por usuário
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrar por usuário:"));
        userFilterMovimentacoes = new JComboBox<>();
        userFilterMovimentacoes.addItem("Todos");
        filterPanel.add(userFilterMovimentacoes);
        JButton filterButton = new JButton("Filtrar");
        filterButton.addActionListener(e -> carregarMovimentacoes());
        filterPanel.add(filterButton);
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchMovimentacoesField = new JTextField(30);
        searchPanel.add(searchMovimentacoesField);
        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> filtrarMovimentacoes());
        searchPanel.add(searchButton);
        JButton clearButton = new JButton("Limpar");
        clearButton.addActionListener(e -> {
            searchMovimentacoesField.setText("");
            filtrarMovimentacoes();
        });
        searchPanel.add(clearButton);
        
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tabela de movimentações
        String[] columnNames = {"ID", "Usuário", "Valor", "Data", "Descrição", "Tipo", "Conta", "Categoria"};
        movimentacoesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movimentacoesTable = new JTable(movimentacoesTableModel);
        movimentacoesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movimentacoesTable.getTableHeader().setReorderingAllowed(false);
        movimentacoesTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(movimentacoesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshMovimentacoesButton = new JButton("Atualizar Lista");
        editMovimentacaoButton = new JButton("Editar");
        editMovimentacaoButton.setEnabled(false);
        deleteMovimentacaoButton = new JButton("Excluir");
        deleteMovimentacaoButton.setEnabled(false);
        deleteMovimentacaoButton.setForeground(Color.RED);
        
        buttonPanel.add(refreshMovimentacoesButton);
        buttonPanel.add(editMovimentacaoButton);
        buttonPanel.add(deleteMovimentacaoButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEvents() {
        // Eventos da aba de usuários
        refreshUsersButton.addActionListener(e -> carregarUsuarios());
        editUserButton.addActionListener(e -> editarUsuarioSelecionado());
        deleteUserButton.addActionListener(e -> excluirUsuarioSelecionado());
        searchUsersField.addActionListener(e -> filtrarUsuarios());
        
        usuariosTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = usuariosTable.getSelectedRow() != -1;
            editUserButton.setEnabled(hasSelection);
            deleteUserButton.setEnabled(hasSelection);
        });
        
        usuariosTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarUsuarioSelecionado();
                }
            }
        });
        
        // Eventos da aba de contas
        refreshContasButton.addActionListener(e -> carregarContas());
        editContaButton.addActionListener(e -> editarContaSelecionada());
        deleteContaButton.addActionListener(e -> excluirContaSelecionada());
        searchContasField.addActionListener(e -> filtrarContas());
        
        contasTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = contasTable.getSelectedRow() != -1;
            editContaButton.setEnabled(hasSelection);
            deleteContaButton.setEnabled(hasSelection);
        });
        
        // Eventos da aba de categorias
        refreshCategoriasButton.addActionListener(e -> carregarCategorias());
        editCategoriaButton.addActionListener(e -> editarCategoriaSelecionada());
        deleteCategoriaButton.addActionListener(e -> excluirCategoriaSelecionada());
        searchCategoriasField.addActionListener(e -> filtrarCategorias());
        
        categoriasTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = categoriasTable.getSelectedRow() != -1;
            editCategoriaButton.setEnabled(hasSelection);
            deleteCategoriaButton.setEnabled(hasSelection);
        });
        
        // Eventos da aba de movimentações
        refreshMovimentacoesButton.addActionListener(e -> carregarMovimentacoes());
        editMovimentacaoButton.addActionListener(e -> editarMovimentacaoSelecionada());
        deleteMovimentacaoButton.addActionListener(e -> excluirMovimentacaoSelecionada());
        searchMovimentacoesField.addActionListener(e -> filtrarMovimentacoes());
        
        movimentacoesTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = movimentacoesTable.getSelectedRow() != -1;
            editMovimentacaoButton.setEnabled(hasSelection);
            deleteMovimentacaoButton.setEnabled(hasSelection);
        });
        
        // Eventos gerais
        editProfileButton.addActionListener(e -> editarPerfil());
        logoutButton.addActionListener(e -> realizarLogout());
        
        // Evento de mudança de aba
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 1 && contasTableModel.getRowCount() == 0) {
                carregarContas();
            } else if (selectedIndex == 2 && categoriasTableModel.getRowCount() == 0) {
                carregarCategorias();
            } else if (selectedIndex == 3 && movimentacoesTableModel.getRowCount() == 0) {
                carregarMovimentacoes();
            }
        });
    }
    
    private void carregarUsuarios() {
        // Limpar tabela
        usuariosTableModel.setRowCount(0);
        
        SwingWorker<List<Usuario>, Void> worker = new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                // Obter lista de usuários do servidor
                return authController.listarUsuarios();
            }
            
            @Override
            protected void done() {
                try {
                    List<Usuario> usuarios = get();
                    if (usuarios != null) {
                        todosUsuarios = usuarios;
                        totalUsersLabel.setText("Total de usuários: " + usuarios.size());
                        
                        // Atualizar filtros de usuário
                        atualizarFiltrosUsuario();
                        
                        for (Usuario usuario : usuarios) {
                            Object[] row = {
                                usuario.getId(),
                                usuario.getNome(),
                                usuario.getEmail(),
                                "N/A" // Data de criação não disponível no modelo cliente
                            };
                            usuariosTableModel.addRow(row);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Erro ao carregar usuários: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void atualizarFiltrosUsuario() {
        // Limpar e repovoar os comboboxes de filtro
        userFilterContas.removeAllItems();
        userFilterCategorias.removeAllItems();
        userFilterMovimentacoes.removeAllItems();
        
        userFilterContas.addItem("Todos");
        userFilterCategorias.addItem("Todos");
        userFilterMovimentacoes.addItem("Todos");
        
        for (Usuario u : todosUsuarios) {
            String item = u.getId() + " - " + u.getNome();
            userFilterContas.addItem(item);
            userFilterCategorias.addItem(item);
            userFilterMovimentacoes.addItem(item);
        }
    }
    
    private void filtrarUsuarios() {
        String busca = searchUsersField.getText().trim().toLowerCase();
        usuariosTableModel.setRowCount(0);
        
        int count = 0;
        for (Usuario usuario : todosUsuarios) {
            if (busca.isEmpty() || 
                usuario.getNome().toLowerCase().contains(busca) ||
                usuario.getEmail().toLowerCase().contains(busca) ||
                String.valueOf(usuario.getId()).contains(busca)) {
                Object[] row = {
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    "N/A"
                };
                usuariosTableModel.addRow(row);
                count++;
            }
        }
        
        if (!busca.isEmpty()) {
            totalUsersLabel.setText("Total de usuários: " + todosUsuarios.size() + " (Filtrados: " + count + ")");
        } else {
            totalUsersLabel.setText("Total de usuários: " + todosUsuarios.size());
        }
    }
    
    private void carregarContas() {
        contasTableModel.setRowCount(0);
        todasContas.clear();
        
        String selectedUser = (String) userFilterContas.getSelectedItem();
        Integer userId = null;
        
        if (selectedUser != null && !selectedUser.equals("Todos")) {
            userId = Integer.parseInt(selectedUser.split(" - ")[0]);
        }
        
        final Integer finalUserId = userId;
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (finalUserId == null) {
                    // Carregar contas de todos os usuários com comando otimizado
                    String comando = "ADMIN_LIST_ALL_CONTAS";
                    String resposta = authController.getNetworkClient().sendCommand(comando);
                    
                    if (resposta != null && resposta.startsWith("OK")) {
                        String[] partes = resposta.split("\\|");
                        if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                            String[] contas = partes[1].split(";");
                            for (String contaData : contas) {
                                String[] campos = contaData.split(",");
                                if (campos.length >= 4) {
                                    Object[] row = {
                                        campos[0].trim(), // ID
                                        campos[1].trim(), // Nome
                                        campos[2].trim(), // Saldo Inicial
                                        campos[3].trim(), // Usuário
                                        campos.length >= 5 ? campos[4].trim() : "N/A"  // Data de Criação
                                    };
                                    todasContas.add(row);
                                }
                            }
                        }
                    }
                } else {
                    // Carregar contas de um usuário específico
                    carregarContasDoUsuario(finalUserId);
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    filtrarContas();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Erro ao carregar contas: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void carregarContasDoUsuario(int userId) {
        String comando = "ADMIN_LIST_CONTAS_USER|" + userId;
        String resposta = authController.getNetworkClient().sendCommand(comando);
        
        if (resposta != null && resposta.startsWith("OK")) {
            String[] partes = resposta.split("\\|");
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] contas = partes[1].split(";");
                for (String contaData : contas) {
                    String[] campos = contaData.split(",");
                    if (campos.length >= 4) {
                        Object[] row = {
                            campos[0].trim(), // ID
                            campos[1].trim(), // Nome
                            campos[2].trim(), // Saldo Inicial
                            campos[3].trim(), // Usuário
                            campos.length >= 5 ? campos[4].trim() : "N/A"  // Data de Criação
                        };
                        todasContas.add(row);
                    }
                }
            }
        }
    }
    
    private void filtrarContas() {
        String busca = searchContasField.getText().trim().toLowerCase();
        contasTableModel.setRowCount(0);
        
        for (Object[] conta : todasContas) {
            if (busca.isEmpty() || 
                conta[0].toString().toLowerCase().contains(busca) ||  // ID
                conta[1].toString().toLowerCase().contains(busca) ||  // Nome
                conta[3].toString().toLowerCase().contains(busca)) {  // Usuário
                contasTableModel.addRow(conta);
            }
        }
    }
    
    private void carregarCategorias() {
        categoriasTableModel.setRowCount(0);
        todasCategorias.clear();
        
        String selectedUser = (String) userFilterCategorias.getSelectedItem();
        Integer userId = null;
        
        if (selectedUser != null && !selectedUser.equals("Todos")) {
            userId = Integer.parseInt(selectedUser.split(" - ")[0]);
        }
        
        final Integer finalUserId = userId;
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (finalUserId == null) {
                    // Carregar categorias de todos os usuários com comando otimizado
                    String comando = "ADMIN_LIST_ALL_CATEGORIAS";
                    String resposta = authController.getNetworkClient().sendCommand(comando);
                    
                    if (resposta != null && resposta.startsWith("OK")) {
                        String[] partes = resposta.split("\\|");
                        if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                            String[] categorias = partes[1].split(";");
                            for (String catData : categorias) {
                                String[] campos = catData.split(",");
                                if (campos.length >= 4) {
                                    Object[] row = {
                                        campos[0].trim(), // ID
                                        campos[1].trim(), // Nome
                                        campos[2].trim(), // Tipo
                                        campos[3]  // Usuário
                                    };
                                    todasCategorias.add(row);
                                }
                            }
                        }
                    }
                } else {
                    // Carregar categorias de um usuário específico
                    carregarCategoriasDoUsuario(finalUserId);
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    filtrarCategorias();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Erro ao carregar categorias: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void carregarCategoriasDoUsuario(int userId) {
        String comando = "ADMIN_LIST_CATEGORIAS_USER|" + userId;
        String resposta = authController.getNetworkClient().sendCommand(comando);
        
        if (resposta != null && resposta.startsWith("OK")) {
            String[] partes = resposta.split("\\|");
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] categorias = partes[1].split(";");
                for (String catData : categorias) {
                    String[] campos = catData.split(",");
                    if (campos.length >= 4) {
                        Object[] row = {
                            campos[0].trim(), // ID
                            campos[1].trim(), // Nome
                            campos[2].trim(), // Tipo
                            campos[3].trim()  // Usuário
                        };
                        todasCategorias.add(row);
                    }
                }
            }
        }
    }
    
    private void filtrarCategorias() {
        String busca = searchCategoriasField.getText().trim().toLowerCase();
        categoriasTableModel.setRowCount(0);
        
        for (Object[] categoria : todasCategorias) {
            if (busca.isEmpty() || 
                categoria[0].toString().toLowerCase().contains(busca) ||  // ID
                categoria[1].toString().toLowerCase().contains(busca) ||  // Nome
                categoria[2].toString().toLowerCase().contains(busca) ||  // Tipo
                categoria[3].toString().toLowerCase().contains(busca)) {  // Usuário
                categoriasTableModel.addRow(categoria);
            }
        }
    }
    
    private void carregarMovimentacoes() {
        movimentacoesTableModel.setRowCount(0);
        todasMovimentacoes.clear();
        
        String selectedUser = (String) userFilterMovimentacoes.getSelectedItem();
        Integer userId = null;
        
        if (selectedUser != null && !selectedUser.equals("Todos")) {
            userId = Integer.parseInt(selectedUser.split(" - ")[0]);
        }
        
        final Integer finalUserId = userId;
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (finalUserId == null) {
                    // Carregar movimentações de todos os usuários com comando otimizado
                    String comando = "ADMIN_LIST_ALL_MOVIMENTACOES";
                    String resposta = authController.getNetworkClient().sendCommand(comando);
                    
                    if (resposta != null && resposta.startsWith("OK")) {
                        String[] partes = resposta.split("\\|");
                        if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                            String[] movimentacoes = partes[1].split(";");
                            for (String movData : movimentacoes) {
                                String[] campos = movData.split(",");
                                if (campos.length >= 8) {
                                    Object[] row = {
                                        campos[0].trim(), // ID
                                        campos[1].trim(), // Usuário
                                        campos[2].trim(), // Valor
                                        campos[3].trim(), // Data
                                        campos[4].trim(), // Descrição
                                        campos[5].trim(), // Tipo
                                        campos[6].trim(), // Conta
                                        campos[7].trim()  // Categoria
                                    };
                                    todasMovimentacoes.add(row);
                                }
                            }
                        }
                    }
                } else {
                    // Carregar movimentações de um usuário específico
                    carregarMovimentacoesDoUsuario(finalUserId);
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    filtrarMovimentacoes();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Erro ao carregar movimentações: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void carregarMovimentacoesDoUsuario(int userId) {
        String comando = "ADMIN_LIST_MOVIMENTACOES_USER|" + userId;
        String resposta = authController.getNetworkClient().sendCommand(comando);
        
        if (resposta != null && resposta.startsWith("OK")) {
            String[] partes = resposta.split("\\|");
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] movimentacoes = partes[1].split(";");
                for (String movData : movimentacoes) {
                    String[] campos = movData.split(",");
                    if (campos.length >= 8) {
                        Object[] row = {
                            campos[0].trim(), // ID
                            campos[1].trim(), // Usuário
                            campos[2].trim(), // Valor
                            campos[3].trim(), // Data
                            campos[4].trim(), // Descrição
                            campos[5].trim(), // Tipo
                            campos[6].trim(), // Conta
                            campos[7].trim()  // Categoria
                        };
                        todasMovimentacoes.add(row);
                    }
                }
            }
        }
    }
    
    private void filtrarMovimentacoes() {
        String busca = searchMovimentacoesField.getText().trim().toLowerCase();
        movimentacoesTableModel.setRowCount(0);
        
        for (Object[] movimentacao : todasMovimentacoes) {
            if (busca.isEmpty() || 
                movimentacao[0].toString().toLowerCase().contains(busca) ||  // ID
                movimentacao[1].toString().toLowerCase().contains(busca) ||  // Usuário
                movimentacao[2].toString().toLowerCase().contains(busca) ||  // Valor
                movimentacao[3].toString().toLowerCase().contains(busca) ||  // Data
                movimentacao[4].toString().toLowerCase().contains(busca) ||  // Descrição
                movimentacao[5].toString().toLowerCase().contains(busca) ||  // Tipo
                movimentacao[6].toString().toLowerCase().contains(busca) ||  // Conta
                movimentacao[7].toString().toLowerCase().contains(busca)) {  // Categoria
                movimentacoesTableModel.addRow(movimentacao);
            }
        }
    }
    
    private void editarUsuarioSelecionado() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int userId = (int) usuariosTableModel.getValueAt(selectedRow, 0);
        String userName = (String) usuariosTableModel.getValueAt(selectedRow, 1);
        String userEmail = (String) usuariosTableModel.getValueAt(selectedRow, 2);
        
        Usuario usuario = new Usuario(userId, userName, userEmail);
        
        // Abrir diálogo de edição
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(this, authController, usuario);
        dialog.setVisible(true);
        
        // Se o diálogo foi confirmado, atualizar lista
        if (dialog.isConfirmado()) {
            carregarUsuarios();
        }
    }
    
    private void excluirUsuarioSelecionado() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int userId = (int) usuariosTableModel.getValueAt(selectedRow, 0);
        String userName = (String) usuariosTableModel.getValueAt(selectedRow, 1);
        String userEmail = (String) usuariosTableModel.getValueAt(selectedRow, 2);
        
        // Não permitir excluir o próprio usuário logado
        if (userId == adminUsuario.getId()) {
            JOptionPane.showMessageDialog(this,
                "Não é possível excluir o próprio usuário logado.",
                "Ação Não Permitida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmação
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir o usuário:\n" +
            "Nome: " + userName + "\n" +
            "Email: " + userEmail + "\n\n" +
            "Esta ação não pode ser desfeita!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Excluir usuário
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authController.excluirUsuario(userId);
            }
            
            @Override
            protected void done() {
                try {
                    Boolean sucesso = get();
                    
                    if (sucesso) {
                        JOptionPane.showMessageDialog(AdminDashboardView.this,
                            "Usuário excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                        carregarUsuarios();
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboardView.this,
                            "Erro ao excluir usuário",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Erro ao excluir: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void editarPerfil() {
        // Abrir diálogo de edição do próprio perfil
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(this, authController, adminUsuario);
        dialog.setVisible(true);
        
        // Se o diálogo foi confirmado, atualizar informações do header
        if (dialog.isConfirmado()) {
            // Recarregar dados do usuário logado
            Usuario usuarioAtualizado = authController.getUsuarioLogado();
            if (usuarioAtualizado != null) {
                adminUsuario = usuarioAtualizado;
                // Atualizar header
                setupUI();
            }
            carregarUsuarios();
        }
    }
    
    // ========== MÉTODOS PARA GERENCIAR CONTAS ==========
    
    private void editarContaSelecionada() {
        int selectedRow = contasTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int contaId = Integer.parseInt(contasTableModel.getValueAt(selectedRow, 0).toString());
        String nome = contasTableModel.getValueAt(selectedRow, 1).toString();
        String saldoStr = contasTableModel.getValueAt(selectedRow, 2).toString();
        
        // Criar diálogo de edição (sem tipo, apenas nome e saldo)
        JTextField nomeField = new JTextField(nome);
        JTextField saldoField = new JTextField(saldoStr);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Saldo Inicial:"));
        panel.add(saldoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Conta", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String novoNome = nomeField.getText().trim();
            double novoSaldo = Double.parseDouble(saldoField.getText().trim());
            
            // Usar tipo padrão "corrente" já que não é mais exibido/editável
            String comando = "ADMIN_UPDATE_CONTA|" + contaId + "|" + novoNome + "|corrente|" + novoSaldo;
            String resposta = authController.getNetworkClient().sendCommand(comando);
            
            if (resposta != null && resposta.startsWith("OK")) {
                JOptionPane.showMessageDialog(this, "Conta atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarContas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar conta", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void excluirContaSelecionada() {
        int selectedRow = contasTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int contaId = Integer.parseInt(contasTableModel.getValueAt(selectedRow, 0).toString());
        String nome = contasTableModel.getValueAt(selectedRow, 1).toString();
        
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir a conta:\n" + nome + "\n\nEsta ação não pode ser desfeita!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            String comando = "ADMIN_DELETE_CONTA|" + contaId;
            String resposta = authController.getNetworkClient().sendCommand(comando);
            
            if (resposta != null && resposta.startsWith("OK")) {
                JOptionPane.showMessageDialog(this, "Conta excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarContas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir conta", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ========== MÉTODOS PARA GERENCIAR CATEGORIAS ==========
    
    private void editarCategoriaSelecionada() {
        int selectedRow = categoriasTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int categoriaId = Integer.parseInt(categoriasTableModel.getValueAt(selectedRow, 0).toString());
        String nome = categoriasTableModel.getValueAt(selectedRow, 1).toString();
        String tipo = categoriasTableModel.getValueAt(selectedRow, 2).toString();
        
        // Criar diálogo de edição
        JTextField nomeField = new JTextField(nome);
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        tipoCombo.setSelectedItem(tipo);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Categoria", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String novoNome = nomeField.getText().trim();
            String novoTipo = tipoCombo.getSelectedItem().toString();
            
            String comando = "ADMIN_UPDATE_CATEGORIA|" + categoriaId + "|" + novoNome + "|" + novoTipo;
            String resposta = authController.getNetworkClient().sendCommand(comando);
            
            if (resposta != null && resposta.startsWith("OK")) {
                JOptionPane.showMessageDialog(this, "Categoria atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarCategorias();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar categoria", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void excluirCategoriaSelecionada() {
        int selectedRow = categoriasTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int categoriaId = Integer.parseInt(categoriasTableModel.getValueAt(selectedRow, 0).toString());
        String nome = categoriasTableModel.getValueAt(selectedRow, 1).toString();
        
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir a categoria:\n" + nome + "\n\nEsta ação não pode ser desfeita!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            String comando = "ADMIN_DELETE_CATEGORIA|" + categoriaId;
            String resposta = authController.getNetworkClient().sendCommand(comando);
            
            if (resposta != null && resposta.startsWith("OK")) {
                JOptionPane.showMessageDialog(this, "Categoria excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarCategorias();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir categoria", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ========== MÉTODOS PARA GERENCIAR MOVIMENTAÇÕES ==========
    
    private void editarMovimentacaoSelecionada() {
        int selectedRow = movimentacoesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int movimentacaoId = Integer.parseInt(movimentacoesTableModel.getValueAt(selectedRow, 0).toString());
        String valor = movimentacoesTableModel.getValueAt(selectedRow, 1).toString();
        String data = movimentacoesTableModel.getValueAt(selectedRow, 2).toString();
        String descricao = movimentacoesTableModel.getValueAt(selectedRow, 3).toString();
        String tipo = movimentacoesTableModel.getValueAt(selectedRow, 4).toString();
        String idConta = movimentacoesTableModel.getValueAt(selectedRow, 5).toString();
        String idCategoria = movimentacoesTableModel.getValueAt(selectedRow, 6).toString();
        
        // Criar diálogo de edição
        JTextField valorField = new JTextField(valor);
        JTextField dataField = new JTextField(data);
        JTextField descricaoField = new JTextField(descricao);
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"receita", "despesa"});
        tipoCombo.setSelectedItem(tipo);
        JTextField idContaField = new JTextField(idConta);
        JTextField idCategoriaField = new JTextField(idCategoria);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("Valor:"));
        panel.add(valorField);
        panel.add(new JLabel("Data (YYYY-MM-DD):"));
        panel.add(dataField);
        panel.add(new JLabel("Descrição:"));
        panel.add(descricaoField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoCombo);
        panel.add(new JLabel("ID Conta:"));
        panel.add(idContaField);
        panel.add(new JLabel("ID Categoria:"));
        panel.add(idCategoriaField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Movimentação", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String novoValor = valorField.getText().trim();
                String novaData = dataField.getText().trim();
                String novaDescricao = descricaoField.getText().trim();
                String novoTipo = tipoCombo.getSelectedItem().toString();
                String novoIdConta = idContaField.getText().trim();
                String novoIdCategoria = idCategoriaField.getText().trim();
                
                String comando = "ADMIN_UPDATE_MOVIMENTACAO|" + movimentacaoId + "|" + novoValor + "|" + 
                                novaData + "|" + novaDescricao + "|" + novoTipo + "|" + novoIdConta + "|" + novoIdCategoria;
                String resposta = authController.getNetworkClient().sendCommand(comando);
                
                if (resposta != null && resposta.startsWith("OK")) {
                    JOptionPane.showMessageDialog(this, "Movimentação atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarMovimentacoes();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar movimentação", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void excluirMovimentacaoSelecionada() {
        int selectedRow = movimentacoesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int movimentacaoId = Integer.parseInt(movimentacoesTableModel.getValueAt(selectedRow, 0).toString());
        String descricao = movimentacoesTableModel.getValueAt(selectedRow, 3).toString();
        
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir a movimentação:\n" + descricao + "\n\nEsta ação não pode ser desfeita!",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            String comando = "ADMIN_DELETE_MOVIMENTACAO|" + movimentacaoId;
            String resposta = authController.getNetworkClient().sendCommand(comando);
            
            if (resposta != null && resposta.startsWith("OK")) {
                JOptionPane.showMessageDialog(this, "Movimentação excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarMovimentacoes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir movimentação", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void realizarLogout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente fazer logout?",
            "Confirmar Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            authController.desconectar();
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginView().setVisible(true);
            });
        }
    }
}
