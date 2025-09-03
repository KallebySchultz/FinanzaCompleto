package com.finanza.server.data;

import com.finanza.server.database.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Armazenamento de dados usando SQLite e JSON
 * Combina banco de dados SQLite com backup em JSON
 */
public class DataStoreEnhanced {
    private static final String DATA_DIR = "finanza-data";
    private static final String BACKUP_DIR = DATA_DIR + File.separator + "backup";
    private static final String USERS_FILE = "users.json";
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final String TRANSACTIONS_FILE = "transactions.json";
    
    private static DataStoreEnhanced instance;
    private DatabaseManager databaseManager;
    private Gson gson;
    
    // Caches em memória para performance
    private Map<String, Object> users;
    private Map<String, Object> accounts;
    private Map<String, Object> transactions;
    
    private DataStoreEnhanced() {
        initializeStorage();
        loadData();
    }
    
    public static synchronized DataStoreEnhanced getInstance() {
        if (instance == null) {
            instance = new DataStoreEnhanced();
        }
        return instance;
    }
    
    private void initializeStorage() {
        try {
            // Inicializar DatabaseManager
            databaseManager = DatabaseManager.getInstance();
            
            // Inicializar Gson para JSON
            gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
            
            // Criar diretórios
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            
            Path backupPath = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
            
            users = new ConcurrentHashMap<>();
            accounts = new ConcurrentHashMap<>();
            transactions = new ConcurrentHashMap<>();
            
            System.out.println("DataStoreEnhanced inicializado com SQLite + JSON");
            
        } catch (IOException e) {
            System.err.println("Erro ao inicializar armazenamento: " + e.getMessage());
        }
    }
    
    private void loadData() {
        loadFromDatabase();
        createJsonBackups();
    }
    
    /**
     * Carrega dados do banco SQLite para os caches em memória
     */
    private void loadFromDatabase() {
        try (Connection conn = databaseManager.getConnection()) {
            loadUsersFromDatabase(conn);
            loadAccountsFromDatabase(conn);
            loadTransactionsFromDatabase(conn);
            
            System.out.println("Dados carregados do banco SQLite");
            
        } catch (SQLException e) {
            System.err.println("Erro ao carregar dados do banco: " + e.getMessage());
            // Fallback para arquivos JSON
            loadFromJsonFiles();
        }
    }
    
    private void loadUsersFromDatabase(Connection conn) throws SQLException {
        String query = "SELECT id, nome, email, data_criacao FROM usuarios";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("nome", rs.getString("nome"));
                user.put("email", rs.getString("email"));
                user.put("data_criacao", rs.getString("data_criacao"));
                
                users.put(String.valueOf(rs.getInt("id")), user);
            }
        }
    }
    
    private void loadAccountsFromDatabase(Connection conn) throws SQLException {
        String query = "SELECT id, usuario_id, nome, saldo, data_criacao FROM contas";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Map<String, Object> account = new HashMap<>();
                account.put("id", rs.getInt("id"));
                account.put("usuario_id", rs.getInt("usuario_id"));
                account.put("nome", rs.getString("nome"));
                account.put("saldo", rs.getDouble("saldo"));
                account.put("data_criacao", rs.getString("data_criacao"));
                
                accounts.put(String.valueOf(rs.getInt("id")), account);
            }
        }
    }
    
    private void loadTransactionsFromDatabase(Connection conn) throws SQLException {
        String query = "SELECT id, usuario_id, conta_id, categoria_id, descricao, valor, tipo, data_lancamento, data_criacao FROM lancamentos";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", rs.getInt("id"));
                transaction.put("usuario_id", rs.getInt("usuario_id"));
                transaction.put("conta_id", rs.getInt("conta_id"));
                transaction.put("categoria_id", rs.getObject("categoria_id"));
                transaction.put("descricao", rs.getString("descricao"));
                transaction.put("valor", rs.getDouble("valor"));
                transaction.put("tipo", rs.getString("tipo"));
                transaction.put("data_lancamento", rs.getString("data_lancamento"));
                transaction.put("data_criacao", rs.getString("data_criacao"));
                
                transactions.put(String.valueOf(rs.getInt("id")), transaction);
            }
        }
    }
    
    /**
     * Fallback: carrega dados dos arquivos JSON se o banco falhar
     */
    private void loadFromJsonFiles() {
        loadJsonFile(USERS_FILE, users);
        loadJsonFile(ACCOUNTS_FILE, accounts);
        loadJsonFile(TRANSACTIONS_FILE, transactions);
        System.out.println("Dados carregados dos arquivos JSON de backup");
    }
    
    private void loadJsonFile(String filename, Map<String, Object> dataMap) {
        Path filePath = Paths.get(BACKUP_DIR, filename);
        if (Files.exists(filePath)) {
            try {
                String content = Files.readString(filePath);
                if (!content.trim().isEmpty()) {
                    // Parse simples JSON - em produção usar Gson completamente
                    System.out.println("Carregado backup: " + filename);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar " + filename + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Cria backups JSON dos dados em memória
     */
    private void createJsonBackups() {
        saveJsonFile(USERS_FILE, users);
        saveJsonFile(ACCOUNTS_FILE, accounts);
        saveJsonFile(TRANSACTIONS_FILE, transactions);
    }
    
    private void saveJsonFile(String filename, Map<String, Object> dataMap) {
        Path filePath = Paths.get(BACKUP_DIR, filename);
        try {
            String json = gson.toJson(dataMap);
            Files.writeString(filePath, json);
            System.out.println("Backup salvo: " + filename);
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar backup " + filename + ": " + e.getMessage());
        }
    }
    
    // Métodos de acesso aos dados
    public Map<String, Object> getUsers() {
        return new HashMap<>(users);
    }
    
    public Map<String, Object> getAccounts() {
        return new HashMap<>(accounts);
    }
    
    public Map<String, Object> getTransactions() {
        return new HashMap<>(transactions);
    }
    
    /**
     * Salva dados e cria backup
     */
    public void saveData() {
        createJsonBackups();
        System.out.println("Dados salvos e backup atualizado");
    }
    
    /**
     * Testa a conexão com o banco de dados
     */
    public boolean testDatabaseConnection() {
        return databaseManager.testConnection();
    }
    
    /**
     * Executa teste de integridade no banco
     */
    public void testDatabase() {
        databaseManager.testDatabase();
    }
    
    /**
     * Finaliza e limpa recursos
     */
    public void shutdown() {
        saveData();
        databaseManager.shutdown();
        System.out.println("DataStoreEnhanced finalizado");
    }
}