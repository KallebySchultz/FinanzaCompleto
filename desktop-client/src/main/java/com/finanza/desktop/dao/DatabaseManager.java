package com.finanza.desktop.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Gerenciador de conexão com banco de dados SQLite
 * Responsável por criar e gerenciar conexões com o banco
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:finanza.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Banco de dados SQLite conectado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Tabela de usuários
        String createUsersTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "senha TEXT NOT NULL," +
                "data_criacao INTEGER NOT NULL" +
                ")";

        // Tabela de categorias
        String createCategoriesTable = "CREATE TABLE IF NOT EXISTS categorias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "cor_hex TEXT NOT NULL," +
                "tipo TEXT NOT NULL CHECK(tipo IN ('receita', 'despesa'))" +
                ")";

        // Tabela de contas
        String createAccountsTable = "CREATE TABLE IF NOT EXISTS contas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "saldo_inicial REAL NOT NULL DEFAULT 0," +
                "usuario_id INTEGER NOT NULL," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE" +
                ")";

        // Tabela de lançamentos
        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS lancamentos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "valor REAL NOT NULL," +
                "data INTEGER NOT NULL," +
                "descricao TEXT NOT NULL," +
                "conta_id INTEGER NOT NULL," +
                "categoria_id INTEGER," +
                "usuario_id INTEGER NOT NULL," +
                "tipo TEXT NOT NULL CHECK(tipo IN ('receita', 'despesa'))," +
                "FOREIGN KEY (conta_id) REFERENCES contas (id) ON DELETE CASCADE," +
                "FOREIGN KEY (categoria_id) REFERENCES categorias (id) ON DELETE SET NULL," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE" +
                ")";

        stmt.execute(createUsersTable);
        stmt.execute(createCategoriesTable);
        stmt.execute(createAccountsTable);
        stmt.execute(createTransactionsTable);

        // Inserir categorias padrão se não existirem
        insertDefaultCategories();

        stmt.close();
    }

    private void insertDefaultCategories() throws SQLException {
        String checkCategories = "SELECT COUNT(*) FROM categorias";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(checkCategories);
        
        if (rs.next() && rs.getInt(1) == 0) {
            // Inserir categorias padrão
            String insertCategories = "INSERT INTO categorias (nome, cor_hex, tipo) VALUES " +
                    "('Salário', '#4CAF50', 'receita')," +
                    "('Freelance', '#2196F3', 'receita')," +
                    "('Investimentos', '#FF9800', 'receita')," +
                    "('Outros Ganhos', '#9C27B0', 'receita')," +
                    "('Alimentação', '#F44336', 'despesa')," +
                    "('Transporte', '#FF5722', 'despesa')," +
                    "('Moradia', '#795548', 'despesa')," +
                    "('Saúde', '#E91E63', 'despesa')," +
                    "('Educação', '#3F51B5', 'despesa')," +
                    "('Lazer', '#FFEB3B', 'despesa')," +
                    "('Compras', '#607D8B', 'despesa')," +
                    "('Outras Despesas', '#9E9E9E', 'despesa')";
            stmt.execute(insertCategories);
        }
        
        rs.close();
        stmt.close();
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}