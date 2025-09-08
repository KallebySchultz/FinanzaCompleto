package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária para gerenciar conexões com o banco de dados
 */
public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:finanza_test.db";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite não encontrado: " + e.getMessage());
        }
    }
    
    /**
     * Obtém uma nova conexão com o banco de dados
     * @return Connection objeto de conexão
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Fecha uma conexão de forma segura
     * @param connection conexão a ser fechada
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
    
    /**
     * Testa a conexão com o banco de dados
     * @return true se a conexão for bem-sucedida
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
}