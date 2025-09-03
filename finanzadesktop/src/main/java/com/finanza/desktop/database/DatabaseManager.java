package com.finanza.desktop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerenciador de conexões com banco de dados SQLite
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:finanza.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro ao conectar com banco de dados", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão", e);
        }
        return connection;
    }

    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Criar tabela de usuários
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "senha TEXT NOT NULL," +
                "data_criacao INTEGER NOT NULL" +
            ")");

            // Criar tabela de contas
            stmt.execute("CREATE TABLE IF NOT EXISTS contas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "saldo_inicial REAL NOT NULL DEFAULT 0," +
                "usuario_id INTEGER NOT NULL," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
            ")");

            // Criar tabela de categorias
            stmt.execute("CREATE TABLE IF NOT EXISTS categorias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "cor_hex TEXT," +
                "tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa'))" +
            ")");

            // Criar tabela de lançamentos
            stmt.execute("CREATE TABLE IF NOT EXISTS lancamentos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "valor REAL NOT NULL," +
                "data INTEGER NOT NULL," +
                "descricao TEXT," +
                "conta_id INTEGER NOT NULL," +
                "categoria_id INTEGER," +
                "usuario_id INTEGER NOT NULL," +
                "tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa'))," +
                "FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE," +
                "FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
            ")");

            // Inserir categorias padrão se não existirem
            insertDefaultCategories();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco de dados", e);
        }
    }

    private void insertDefaultCategories() throws SQLException {
        // Verificar se já existem categorias
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM categorias")) {
            var rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Já existem categorias
            }
        }

        // Inserir categorias padrão
        String[] categoriasReceita = {
            "Salário#4CAF50", "Freelance#8BC34A", "Investimentos#2196F3", "Outros#9E9E9E"
        };
        
        String[] categoriasDespesa = {
            "Alimentação#FF5722", "Transporte#FF9800", "Moradia#795548", 
            "Saúde#E91E63", "Educação#3F51B5", "Lazer#9C27B0", "Outros#9E9E9E"
        };

        try (PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO categorias (nome, cor_hex, tipo) VALUES (?, ?, ?)")) {
            
            for (String cat : categoriasReceita) {
                String[] parts = cat.split("#");
                stmt.setString(1, parts[0]);
                stmt.setString(2, "#" + parts[1]);
                stmt.setString(3, "receita");
                stmt.addBatch();
            }
            
            for (String cat : categoriasDespesa) {
                String[] parts = cat.split("#");
                stmt.setString(1, parts[0]);
                stmt.setString(2, "#" + parts[1]);
                stmt.setString(3, "despesa");
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}