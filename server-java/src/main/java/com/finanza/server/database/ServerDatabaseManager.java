package com.finanza.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerenciador de conexões com banco de dados SQLite para o servidor
 */
public class ServerDatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:finanza_server.db";
    private static ServerDatabaseManager instance;
    private Connection connection;

    private ServerDatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro ao conectar com banco de dados do servidor", e);
        }
    }

    public static synchronized ServerDatabaseManager getInstance() {
        if (instance == null) {
            instance = new ServerDatabaseManager();
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
            // Criar tabela de log de sincronização
            stmt.execute("CREATE TABLE IF NOT EXISTS sync_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "client_id TEXT NOT NULL," +
                "action TEXT NOT NULL," +
                "timestamp INTEGER NOT NULL," +
                "data TEXT," +
                "status TEXT NOT NULL" +
            ")");

            // Criar tabela de clientes conectados
            stmt.execute("CREATE TABLE IF NOT EXISTS connected_clients (" +
                "client_id TEXT PRIMARY KEY," +
                "last_seen INTEGER NOT NULL," +
                "ip_address TEXT," +
                "user_agent TEXT" +
            ")");

            // Criar tabela de dados sincronizados (cache)
            stmt.execute("CREATE TABLE IF NOT EXISTS sync_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "client_id TEXT NOT NULL," +
                "data_type TEXT NOT NULL," +
                "data_id TEXT NOT NULL," +
                "data_content TEXT NOT NULL," +
                "last_modified INTEGER NOT NULL," +
                "UNIQUE(client_id, data_type, data_id)" +
            ")");

            System.out.println("Banco de dados do servidor inicializado com sucesso");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco de dados do servidor", e);
        }
    }

    public void logSyncOperation(String clientId, String action, String data, String status) {
        String sql = "INSERT INTO sync_log (client_id, action, timestamp, data, status) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, clientId);
            stmt.setString(2, action);
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setString(4, data);
            stmt.setString(5, status);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao log de sincronização: " + e.getMessage());
        }
    }

    public void updateClientConnection(String clientId, String ipAddress) {
        String sql = "INSERT OR REPLACE INTO connected_clients (client_id, last_seen, ip_address) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, clientId);
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setString(3, ipAddress);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente conectado: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão do servidor: " + e.getMessage());
        }
    }
}