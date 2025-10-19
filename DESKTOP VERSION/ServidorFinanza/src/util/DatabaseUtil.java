package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária para gerenciar conexões com o banco de dados
 */
public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL não encontrado: " + e.getMessage());
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
    
    /**
     * Inicializa as tabelas do banco de dados se não existirem
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Tabela de usuários
            stmt.execute("CREATE TABLE IF NOT EXISTS usuario (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(150) UNIQUE NOT NULL, " +
                    "senha_hash VARCHAR(255) NOT NULL, " +
                    "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")");
            
            // Tabela de contas
            stmt.execute("CREATE TABLE IF NOT EXISTS conta (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "tipo ENUM('corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro') NOT NULL, " +
                    "saldo_inicial DECIMAL(10,2) DEFAULT 0.00, " +
                    "id_usuario INT NOT NULL, " +
                    "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                    ")");
            
            // Tabela de categorias
            stmt.execute("CREATE TABLE IF NOT EXISTS categoria (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "tipo ENUM('receita', 'despesa') NOT NULL, " +
                    "id_usuario INT NOT NULL, " +
                    "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                    ")");
            
            // Tabela de movimentações
            stmt.execute("CREATE TABLE IF NOT EXISTS movimentacao (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "valor DECIMAL(10,2) NOT NULL, " +
                    "data DATE NOT NULL, " +
                    "descricao TEXT, " +
                    "tipo ENUM('receita', 'despesa') NOT NULL, " +
                    "id_conta INT NOT NULL, " +
                    "id_categoria INT NOT NULL, " +
                    "id_usuario INT NOT NULL, " +
                    "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (id_conta) REFERENCES conta(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                    ")");
            
            // Criar índices
            try {
                stmt.execute("CREATE INDEX idx_movimentacao_data ON movimentacao(data)");
            } catch (SQLException e) {
                // Índice já existe, ignorar
            }
            
            try {
                stmt.execute("CREATE INDEX idx_movimentacao_usuario ON movimentacao(id_usuario)");
            } catch (SQLException e) {
                // Índice já existe, ignorar
            }
            
            try {
                stmt.execute("CREATE INDEX idx_conta_usuario ON conta(id_usuario)");
            } catch (SQLException e) {
                // Índice já existe, ignorar
            }
            
            try {
                stmt.execute("CREATE INDEX idx_categoria_usuario ON categoria(id_usuario)");
            } catch (SQLException e) {
                // Índice já existe, ignorar
            }
            
            System.out.println("Tabelas do banco de dados verificadas/criadas com sucesso");
            
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}