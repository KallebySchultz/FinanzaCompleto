package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

/**
 * DatabaseManager - Manages SQLite database connections and initialization
 * Creates and manages the finanza.db database
 */
public class DatabaseManager {
    private static final String DB_NAME = "finanza.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static Connection connection;
    
    /**
     * Get database connection (singleton pattern)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }
        return connection;
    }
    
    /**
     * Initialize database with schema if it doesn't exist
     */
    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create tables if they don't exist
            createTables(stmt);
            insertDefaultData(stmt);
        }
    }
    
    /**
     * Create all required tables
     */
    private static void createTables(Statement stmt) throws SQLException {
        // Create usuarios table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                data_criacao INTEGER NOT NULL
            )
        """);
        
        // Create contas table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS contas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                saldo_inicial REAL NOT NULL DEFAULT 0,
                usuario_id INTEGER NOT NULL,
                FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
            )
        """);
        
        // Create categorias table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS categorias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cor_hex TEXT,
                tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa'))
            )
        """);
        
        // Create lancamentos table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS lancamentos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                valor REAL NOT NULL,
                data INTEGER NOT NULL,
                descricao TEXT,
                conta_id INTEGER NOT NULL,
                categoria_id INTEGER,
                usuario_id INTEGER NOT NULL,
                tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa')),
                FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE,
                FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
                FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
            )
        """);
        
        // Create indexes
        createIndexes(stmt);
    }
    
    /**
     * Create database indexes for better performance
     */
    private static void createIndexes(Statement stmt) throws SQLException {
        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email)",
            "CREATE INDEX IF NOT EXISTS idx_contas_usuario ON contas(usuario_id)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_usuario ON lancamentos(usuario_id)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_conta ON lancamentos(conta_id)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_categoria ON lancamentos(categoria_id)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_data ON lancamentos(data)",
            "CREATE INDEX IF NOT EXISTS idx_lancamentos_tipo ON lancamentos(tipo)"
        };
        
        for (String index : indexes) {
            stmt.execute(index);
        }
    }
    
    /**
     * Insert default categories and test user
     */
    private static void insertDefaultData(Statement stmt) throws SQLException {
        // Insert default categories - Despesas
        stmt.execute("""
            INSERT OR IGNORE INTO categorias (nome, cor_hex, tipo) VALUES
            ('Alimentação', '#FF6B6B', 'despesa'),
            ('Transporte', '#4ECDC4', 'despesa'),
            ('Saúde', '#45B7D1', 'despesa'),
            ('Educação', '#96CEB4', 'despesa'),
            ('Lazer', '#FFEAA7', 'despesa'),
            ('Casa', '#DDA0DD', 'despesa'),
            ('Roupas', '#98D8C8', 'despesa'),
            ('Tecnologia', '#F7DC6F', 'despesa'),
            ('Viagem', '#BB8FCE', 'despesa'),
            ('Outros', '#85929E', 'despesa')
        """);
        
        // Insert default categories - Receitas
        stmt.execute("""
            INSERT OR IGNORE INTO categorias (nome, cor_hex, tipo) VALUES
            ('Salário', '#2ECC71', 'receita'),
            ('Freelance', '#3498DB', 'receita'),
            ('Investimentos', '#9B59B6', 'receita'),
            ('Vendas', '#E67E22', 'receita'),
            ('Prêmios', '#F1C40F', 'receita'),
            ('Restituição', '#1ABC9C', 'receita'),
            ('Outros', '#34495E', 'receita')
        """);
        
        // Insert test user
        stmt.execute(
            "INSERT OR IGNORE INTO usuarios (nome, email, senha, data_criacao) VALUES " +
            "('Administrador', 'admin@finanza.com', 'admin', " + System.currentTimeMillis() + ")");
        
        // Insert test accounts for admin user
        stmt.execute("""
            INSERT OR IGNORE INTO contas (nome, saldo_inicial, usuario_id) VALUES
            ('Conta Corrente', 1000.00, 1),
            ('Poupança', 5000.00, 1)
        """);
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}