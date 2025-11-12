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
 * AdminDashboardView - Painel de Administração Desktop do Sistema Finanza
 * 
 * Esta classe representa a interface gráfica (Swing) principal para
 * administradores do sistema Finanza. Permite gerenciar todos os usuários
 * e seus dados financeiros a partir de um aplicativo desktop Java.
 * 
 * Funcionalidades Principais:
 * - Visualizar lista de todos os usuários do sistema
 * - Editar informações de usuários (nome, email, senha)
 * - Excluir usuários e seus dados
 * - Visualizar contas financeiras de todos os usuários
 * - Visualizar categorias de todos os usuários
 * - Visualizar movimentações financeiras de todos os usuários
 * - Editar/excluir contas, categorias e movimentações
 * - Filtrar dados por usuário específico
 * - Buscar dados por texto
 * - Editar próprio perfil de administrador
 * - Fazer logout do sistema
 * 
 * Arquitetura:
 * - Padrão MVC (Model-View-Controller)
 * - Interface Swing com JFrame principal
 * - JTabbedPane com 4 abas (Usuários, Contas, Categorias, Movimentações)
 * - Comunicação com servidor via AuthController
 * - Uso de JTable para exibir dados tabulares
 * - DefaultTableModel para gerenciar dados das tabelas
 * 
 * Abas disponíveis:
 * 1. Usuários: Gerenciamento completo de usuários do sistema
 * 2. Contas: Visualização e edição de contas bancárias
 * 3. Categorias: Visualização e edição de categorias financeiras
 * 4. Movimentações: Visualização e edição de lançamentos financeiros
 * 
 * Segurança:
 * - Acesso exclusivo para usuários com tipo "admin"
 * - Validação de permissões via AuthController
 * - Confirmação antes de ações destrutivas (exclusão)
 * - Não permite administrador excluir a si próprio
 * 
 * Layout:
 * - Header: Título, contador de usuários, informações do admin logado
 * - Centro: JTabbedPane com as 4 abas de gerenciamento
 * - Footer: Botões "Editar Meu Perfil" e "Sair"
 * 
 * Tecnologias utilizadas:
 * - Java Swing para interface gráfica
 * - JTable/DefaultTableModel para tabelas
 * - BorderLayout e FlowLayout para organização
 * - MouseAdapter para interação com tabelas
 * - JDialog para janelas modais
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class AdminDashboardView extends JFrame {
    
    // ================== CONTROLADORES E DADOS DO ADMIN ==================
    
    /** Controlador de autenticação e comunicação com servidor */
    private AuthController authController;
    
    /** Objeto do usuário administrador atualmente logado */
    private Usuario adminUsuario;

    // ================== COMPONENTES PRINCIPAIS DA INTERFACE ==================
    
    /** Painel de abas principal (Usuários, Contas, Categorias, Movimentações) */
    private JTabbedPane tabbedPane;
    
    /** Label que exibe o total de usuários cadastrados no sistema */
    private JLabel totalUsersLabel;
    
    /** Botão para editar perfil do próprio administrador */
    private JButton editProfileButton;
    
    /** Botão para fazer logout e retornar à tela de login */
    private JButton logoutButton;

    // ================== COMPONENTES DA ABA DE USUÁRIOS ==================
    
    /** Tabela que exibe lista de todos os usuários do sistema */
    private JTable usuariosTable;
    
    /** Modelo de dados da tabela de usuários (gerencia linhas e colunas) */
    private DefaultTableModel usuariosTableModel;
    
    /** Botão para recarregar lista de usuários do servidor */
    private JButton refreshUsersButton;
    
    /** Botão para editar usuário selecionado na tabela */
    private JButton editUserButton;
    
    /** Botão para excluir usuário selecionado da tabela */
    private JButton deleteUserButton;
    
    /** Campo de texto para buscar usuários por nome ou email */
    private JTextField searchUsersField;
    
    /** Lista completa de usuários (antes de filtros de busca) */
    private List<Usuario> todosUsuarios;

    // ================== COMPONENTES DA ABA DE CONTAS ==================
    
    /** Tabela que exibe todas as contas financeiras do sistema */
    private JTable contasTable;
    
    /** Modelo de dados da tabela de contas */
    private DefaultTableModel contasTableModel;
    
    /** Botão para recarregar lista de contas do servidor */
    private JButton refreshContasButton;
    
    /** Botão para editar conta selecionada */
    private JButton editContaButton;
    
    /** Botão para excluir conta selecionada */
    private JButton deleteContaButton;
    
    /** ComboBox para filtrar contas por usuário específico */
    private JComboBox<String> userFilterContas;
    
    /** Campo de texto para buscar contas por nome */
    private JTextField searchContasField;
    
    /** Lista completa de contas (antes de filtros) */
    private List<Object[]> todasContas;

    // ================== COMPONENTES DA ABA DE CATEGORIAS ==================
    
    /** Tabela que exibe todas as categorias financeiras do sistema */
    private JTable categoriasTable;
    
    /** Modelo de dados da tabela de categorias */
    private DefaultTableModel categoriasTableModel;
    
    /** Botão para recarregar lista de categorias do servidor */
    private JButton refreshCategoriasButton;
    
    /** Botão para editar categoria selecionada */
    private JButton editCategoriaButton;
    
    /** Botão para excluir categoria selecionada */
    private JButton deleteCategoriaButton;
    
    /** ComboBox para filtrar categorias por usuário específico */
    private JComboBox<String> userFilterCategorias;
    
    /** Campo de texto para buscar categorias por nome */
    private JTextField searchCategoriasField;
    
    /** Lista completa de categorias (antes de filtros) */
    private List<Object[]> todasCategorias;

    // ================== COMPONENTES DA ABA DE MOVIMENTAÇÕES ==================
    
    /** Tabela que exibe todas as movimentações financeiras do sistema */
    private JTable movimentacoesTable;
    
    /** Modelo de dados da tabela de movimentações */
    private DefaultTableModel movimentacoesTableModel;
    
    /** Botão para recarregar lista de movimentações do servidor */
    private JButton refreshMovimentacoesButton;
    
    /** Botão para editar movimentação selecionada */
    private JButton editMovimentacaoButton;
    
    /** Botão para excluir movimentação selecionada */
    private JButton deleteMovimentacaoButton;
    
    /** ComboBox para filtrar movimentações por usuário específico */
    private JComboBox<String> userFilterMovimentacoes;
    
    /** Campo de texto para buscar movimentações por descrição */
    private JTextField searchMovimentacoesField;
    
    /** Lista completa de movimentações (antes de filtros) */
    private List<Object[]> todasMovimentacoes;

    /**
     * Construtor da tela de administração
     * 
     * Inicializa a interface gráfica completa para gerenciamento de
     * usuários e dados financeiros do sistema Finanza.
     * 
     * Fluxo de inicialização:
     * 1. Armazena controlador e dados do admin logado
     * 2. Inicializa componentes visuais (janela, botões, tabelas)
     * 3. Configura layout da interface (header, abas, footer)
     * 4. Configura listeners de eventos (cliques, mudanças de aba)
     * 5. Carrega lista inicial de usuários do servidor
     * 
     * @param authController Controlador para comunicação com servidor
     * @param usuario Objeto do usuário administrador logado
     */
    public AdminDashboardView(AuthController authController, Usuario usuario) {
        this.authController = authController;
        this.adminUsuario = usuario;
        
        // Passo 1: Inicializar componentes básicos
        initComponents();
        
        // Passo 2: Configurar layout da interface
        setupUI();
        
        // Passo 3: Configurar listeners de eventos
        setupEvents();
        
        // Passo 4: Carregar dados iniciais
        carregarUsuarios();
    }

    /**
     * Inicializa componentes básicos da janela
     * 
     * Configurações realizadas:
     * - Título da janela
     * - Comportamento ao fechar (EXIT_ON_CLOSE)
     * - Tamanho da janela (1200x750)
     * - Centralização na tela
     * - Inicialização de listas de dados
     */
    private void initComponents() {
        setTitle("Finanza Admin - Painel de Administração Completo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750); // Tamanho adequado para exibir tabelas
        setLocationRelativeTo(null); // Centraliza na tela
        
        // Inicializa listas vazias que serão preenchidas ao carregar dados
        todosUsuarios = new java.util.ArrayList<>();
        todasContas = new java.util.ArrayList<>();
        todasCategorias = new java.util.ArrayList<>();
        todasMovimentacoes = new java.util.ArrayList<>();
    }

    /**
     * Configura o layout principal da interface
     * 
     * Cria e organiza todos os componentes visuais da janela:
     * - Header (topo): Título, contador de usuários, info do admin
     * - Centro: JTabbedPane com 4 abas de gerenciamento
     * - Footer (rodapé): Botões de editar perfil e sair
     * 
     * Layout utilizado: BorderLayout
     * - NORTH: Header
     * - CENTER: Abas
     * - SOUTH: Footer
     */
    private void setupUI() {
        setLayout(new BorderLayout());

        // ========== CRIAÇÃO DO HEADER (TOPO) ==========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margem interna
        headerPanel.setBackground(new Color(240, 248, 255)); // Azul claro suave

        // Lado esquerdo do header: Título e contador
        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setOpaque(false); // Transparente para mostrar cor do pai
        
        JLabel titleLabel = new JLabel("Painel de Administração Completo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        totalUsersLabel = new JLabel("Total de usuários: 0");
        totalUsersLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerLeft.add(titleLabel);
        headerLeft.add(totalUsersLabel);
        headerPanel.add(headerLeft, BorderLayout.WEST);

        // Lado direito do header: Informações do admin logado
        JLabel userLabel = new JLabel("Admin: " + adminUsuario.getNome() + " (" + adminUsuario.getEmail() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ========== CRIAÇÃO DAS ABAS (CENTRO) ==========
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Usuários", createUsuariosPanel());           // Aba 1: Gerenciar usuários
        tabbedPane.addTab("Contas", createContasPanel());               // Aba 2: Visualizar contas
        tabbedPane.addTab("Categorias", createCategoriasPanel());       // Aba 3: Visualizar categorias
        tabbedPane.addTab("Movimentações", createMovimentacoesPanel()); // Aba 4: Visualizar movimentações

        add(tabbedPane, BorderLayout.CENTER);

        // ========== CRIAÇÃO DO FOOTER (RODAPÉ) ==========
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Botão para editar perfil do próprio administrador
        editProfileButton = new JButton("Editar Meu Perfil");
        
        // Botão para fazer logout
        logoutButton = new JButton("Sair");

        footerPanel.add(editProfileButton);
        footerPanel.add(logoutButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Cria o painel da aba de Usuários
     * 
     * Componentes do painel:
     * - Barra de busca (topo): Campo de texto + botões Filtrar e Limpar
     * - Tabela (centro): Lista de usuários com colunas: ID, Nome, Email, Data Criação
     * - Barra de ações (rodapé): Botões Atualizar, Editar e Excluir
     * 
     * @return JPanel configurado com todos os componentes da aba de usuários
     */
    private JPanel createUsuariosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margem interna

        // ========== PAINEL DE BUSCA (TOPO) ==========
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        
        // Campo de texto para buscar por nome ou email
        searchUsersField = new JTextField(30);
        searchPanel.add(searchUsersField);
        
        // Botão para aplicar filtro de busca
        JButton searchButton = new JButton("Filtrar");
        searchButton.addActionListener(e -> filtrarUsuarios());
        searchPanel.add(searchButton);
        
        // Botão para limpar busca e mostrar todos novamente
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
        String[] columnNames = {"ID", "Nome", "Tipo", "Usuário", "Data de Criação"};
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
                            String dataCriacaoFormatada = "N/A";
                            if (usuario.getDataCriacao() != null) {
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                                dataCriacaoFormatada = sdf.format(usuario.getDataCriacao());
                            }
                            Object[] row = {
                                usuario.getId(),
                                usuario.getNome(),
                                usuario.getEmail(),
                                dataCriacaoFormatada
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
                String dataCriacaoFormatada = "N/A";
                if (usuario.getDataCriacao() != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    dataCriacaoFormatada = sdf.format(usuario.getDataCriacao());
                }
                Object[] row = {
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    dataCriacaoFormatada
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
                               
                                if (campos.length >= 5) { // Agora espera 5 campos
    // Formatar a data de criação
    String dataOriginal = campos[4].trim();
    String dataFormatada = dataOriginal;
    try {
        java.text.SimpleDateFormat formatoEntrada;
        if (dataOriginal.length() > 10) {
            formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd");
        }
        java.text.SimpleDateFormat formatoSaida = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.util.Date data = formatoEntrada.parse(dataOriginal);
        dataFormatada = formatoSaida.format(data);
    } catch (Exception ex) {
        // Se der erro, mantém a data original
    }
    Object[] row = {
        campos[0].trim(), // ID
        campos[1].trim(), // Nome
        campos[2].trim(), // Tipo
        campos[3].trim(), // Usuário
        dataFormatada     // Data de Criação
    };
    todasCategorias.add(row);
} else if (campos.length >= 4) { // fallback caso não exista data
    Object[] row = {
        campos[0].trim(),
        campos[1].trim(),
        campos[2].trim(),
        campos[3].trim(),
        "N/A"
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
    categoria[3].toString().toLowerCase().contains(busca) ||  // Usuário
    categoria[4].toString().toLowerCase().contains(busca)) {  // Data de Criação
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
                                    // Formatar a data para dd/MM/yyyy
                                    String dataOriginal = campos[3].trim();
                                    String dataFormatada = dataOriginal;
                                    try {
                                        java.text.SimpleDateFormat formatoEntrada;
                                        if (dataOriginal.length() > 10) {
                                            formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        } else {
                                            formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                        }
                                        java.text.SimpleDateFormat formatoSaida = new java.text.SimpleDateFormat("dd/MM/yyyy");
                                        java.util.Date data = formatoEntrada.parse(dataOriginal);
                                        dataFormatada = formatoSaida.format(data);
                                    } catch (Exception ex) {
                                        // Se der erro, mantém a data original
                                    }

                                    Object[] row = {
                                        campos[0].trim(), // ID
                                        campos[1].trim(), // Usuário
                                        campos[2].trim(), // Valor
                                        dataFormatada,    // Data formatada
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
                        // Formatar a data para dd/MM/yyyy
                        String dataOriginal = campos[3].trim();
                        String dataFormatada = dataOriginal;
                        try {
                            java.text.SimpleDateFormat formatoEntrada;
                            if (dataOriginal.length() > 10) {
                                formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            } else {
                                formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd");
                            }
                            java.text.SimpleDateFormat formatoSaida = new java.text.SimpleDateFormat("dd/MM/yyyy");
                            java.util.Date data = formatoEntrada.parse(dataOriginal);
                            dataFormatada = formatoSaida.format(data);
                        } catch (Exception ex) {
                            // Se der erro, mantém a data original
                        }

                        Object[] row = {
                            campos[0].trim(), // ID
                            campos[1].trim(), // Usuário
                            campos[2].trim(), // Valor
                            dataFormatada,    // Data formatada
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