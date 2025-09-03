package com.finanza.server.database;

import java.sql.*;
import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gerenciador de banco de dados SQLite para o servidor Finanza
 * Responsável por criar e gerenciar o banco de dados local
 */
public class DatabaseManager {
    private static final String DATABASE_DIR = "finanza-data";
    private static final String DATABASE_FILE = "finanza.db";
    private static final String DATABASE_PATH = DATABASE_DIR + File.separator + DATABASE_FILE;
    private static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_PATH;
    
    private static DatabaseManager instance;
    private static final ReentrantLock lock = new ReentrantLock();
    
    private DatabaseManager() {
        initializeDatabase();
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    /**
     * Inicializa o banco de dados e cria as tabelas necessárias
     */
    private void initializeDatabase() {
        try {
            // Criar diretório se não existir
            File dataDir = new File(DATABASE_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("Diretório de dados criado: " + DATABASE_DIR);
            }
            
            // Carregar driver SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Criar tabelas
            createTables();
            
            System.out.println("Banco de dados SQLite inicializado: " + DATABASE_PATH);
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cria as tabelas do banco de dados
     */
    private void createTables() throws SQLException {
        try (Connection conn = getConnection()) {
            // Tabela de usuários
            String createUsersTable = 
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    nome TEXT NOT NULL," +
                "    email TEXT UNIQUE NOT NULL," +
                "    senha_hash TEXT NOT NULL," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    ultima_atividade TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            
            // Tabela de contas
            String createAccountsTable = 
                "CREATE TABLE IF NOT EXISTS contas (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    usuario_id INTEGER NOT NULL," +
                "    nome TEXT NOT NULL," +
                "    saldo DECIMAL(15,2) DEFAULT 0.00," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE" +
                ")";
            
            // Tabela de categorias
            String createCategoriesTable = 
                "CREATE TABLE IF NOT EXISTS categorias (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    usuario_id INTEGER NOT NULL," +
                "    nome TEXT NOT NULL," +
                "    tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa'))," +
                "    cor TEXT DEFAULT '#4A7CF5'," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE" +
                ")";
            
            // Tabela de lançamentos
            String createTransactionsTable = 
                "CREATE TABLE IF NOT EXISTS lancamentos (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    usuario_id INTEGER NOT NULL," +
                "    conta_id INTEGER NOT NULL," +
                "    categoria_id INTEGER," +
                "    descricao TEXT NOT NULL," +
                "    valor DECIMAL(15,2) NOT NULL," +
                "    tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa'))," +
                "    data_lancamento TIMESTAMP NOT NULL," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE," +
                "    FOREIGN KEY (conta_id) REFERENCES contas (id) ON DELETE CASCADE," +
                "    FOREIGN KEY (categoria_id) REFERENCES categorias (id) ON DELETE SET NULL" +
                ")";
            
            // Tabela de sincronização (para rastrear mudanças)
            String createSyncTable = 
                "CREATE TABLE IF NOT EXISTS sincronizacao (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    tabela TEXT NOT NULL," +
                "    registro_id INTEGER NOT NULL," +
                "    operacao TEXT NOT NULL CHECK (operacao IN ('INSERT', 'UPDATE', 'DELETE'))," +
                "    data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    sincronizado BOOLEAN DEFAULT FALSE" +
                ")";
            
            // Executar criação das tabelas
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createUsersTable);
                stmt.executeUpdate(createAccountsTable);
                stmt.executeUpdate(createCategoriesTable);
                stmt.executeUpdate(createTransactionsTable);
                stmt.executeUpdate(createSyncTable);
                
                System.out.println("Tabelas do banco de dados criadas com sucesso.");
            }
            
            // Criar índices para melhor performance
            createIndexes(conn);
            
            // Inserir dados de exemplo se for primeira execução
            insertSampleData(conn);
        }
    }
    
    /**
     * Cria índices para otimizar consultas
     */
    private void createIndexes(Connection conn) throws SQLException {
        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios (email)",
            "CREATE INDEX IF NOT EXISTS idx_contas_usuario ON contas (usuario_id)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_usuario ON lancamentos (usuario_id)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_conta ON lancamentos (conta_id)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_data ON lancamentos (data_lancamento)",
            "CREATE INDEX IF NOT EXISTS idx_sincronizacao_tabela ON sincronizacao (tabela, registro_id)"
        };
        
        try (Statement stmt = conn.createStatement()) {
            for (String index : indexes) {
                stmt.executeUpdate(index);
            }
            System.out.println("Índices criados com sucesso.");
        }
    }
    
    /**
     * Insere dados de exemplo se for primeira execução
     */
    private void insertSampleData(Connection conn) throws SQLException {
        // Verificar se já existem usuários
        String checkUsersQuery = "SELECT COUNT(*) FROM usuarios";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkUsersQuery)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Inserir usuário de exemplo
                String insertUser = 
                    "INSERT INTO usuarios (nome, email, senha_hash) " +
                    "VALUES ('Admin Finanza', 'admin@finanza.com', 'hashed_password_example')";
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.executeUpdate();
                    
                    ResultSet keys = pstmt.getGeneratedKeys();
                    if (keys.next()) {
                        int userId = keys.getInt(1);
                        
                        // Inserir conta de exemplo
                        String insertAccount = 
                            "INSERT INTO contas (usuario_id, nome, saldo) " +
                            "VALUES (?, 'Conta Corrente', 1000.00)";
                        
                        try (PreparedStatement accountStmt = conn.prepareStatement(insertAccount)) {
                            accountStmt.setInt(1, userId);
                            accountStmt.executeUpdate();
                        }
                        
                        System.out.println("Dados de exemplo inseridos com sucesso.");
                    }
                }
            }
        }
    }
    
    /**
     * Obtém uma conexão com o banco de dados
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
    
    /**
     * Verifica se o banco de dados está funcionando
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5); // Timeout de 5 segundos
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Executa uma query de teste para verificar a integridade do banco
     */
    public void testDatabase() {
        try (Connection conn = getConnection()) {
            String testQuery = "SELECT COUNT(*) as total_usuarios FROM usuarios";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(testQuery)) {
                
                if (rs.next()) {
                    int totalUsers = rs.getInt("total_usuarios");
                    System.out.println("Banco de dados operacional. Total de usuários: " + totalUsers);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao testar banco de dados: " + e.getMessage());
        }
    }
    
    /**
     * Fecha recursos e limpa conexões
     */
    public void shutdown() {
        // SQLite não requer limpeza especial, mas podemos implementar
        // procedimentos de backup ou limpeza se necessário
        System.out.println("DatabaseManager finalizado.");
    }
}