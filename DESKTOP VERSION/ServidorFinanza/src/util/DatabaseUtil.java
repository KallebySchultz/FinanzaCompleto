package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

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
     * Verifica se as tabelas necessárias existem no banco de dados
     * @return true se todas as tabelas existem
     */
    public static boolean checkTablesExist() {
        String[] requiredTables = {"usuario", "conta", "categoria", "movimentacao"};
        
        try (Connection conn = getConnection()) {
            for (String tableName : requiredTables) {
                if (!tableExists(conn, tableName)) {
                    System.err.println("Tabela '" + tableName + "' não encontrada no banco de dados");
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar tabelas: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se uma tabela específica existe
     */
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }
    
    /**
     * Inicializa o banco de dados criando as tabelas necessárias
     * @return true se a inicialização foi bem-sucedida
     */
    public static boolean initializeDatabase() {
        System.out.println("Inicializando estrutura do banco de dados...");
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Criar tabela de usuários
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS usuario (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    nome VARCHAR(100) NOT NULL," +
                "    email VARCHAR(150) UNIQUE NOT NULL," +
                "    senha_hash VARCHAR(255) NOT NULL," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")"
            );
            System.out.println("✓ Tabela 'usuario' verificada/criada");
            
            // Criar tabela de contas
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS conta (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    nome VARCHAR(100) NOT NULL," +
                "    tipo ENUM('corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro') NOT NULL," +
                "    saldo_inicial DECIMAL(10,2) DEFAULT 0.00," +
                "    id_usuario INT NOT NULL," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                ")"
            );
            System.out.println("✓ Tabela 'conta' verificada/criada");
            
            // Criar tabela de categorias
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS categoria (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    nome VARCHAR(100) NOT NULL," +
                "    tipo ENUM('receita', 'despesa') NOT NULL," +
                "    id_usuario INT NOT NULL," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                ")"
            );
            System.out.println("✓ Tabela 'categoria' verificada/criada");
            
            // Criar tabela de movimentações
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS movimentacao (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    valor DECIMAL(10,2) NOT NULL," +
                "    data DATE NOT NULL," +
                "    descricao TEXT," +
                "    tipo ENUM('receita', 'despesa') NOT NULL," +
                "    id_conta INT NOT NULL," +
                "    id_categoria INT NOT NULL," +
                "    id_usuario INT NOT NULL," +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (id_conta) REFERENCES conta(id) ON DELETE CASCADE," +
                "    FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE," +
                "    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE" +
                ")"
            );
            System.out.println("✓ Tabela 'movimentacao' verificada/criada");
            
            // Criar índices
            createIndexIfNotExists(stmt, "idx_movimentacao_data", "movimentacao", "data");
            createIndexIfNotExists(stmt, "idx_movimentacao_usuario", "movimentacao", "id_usuario");
            createIndexIfNotExists(stmt, "idx_conta_usuario", "conta", "id_usuario");
            createIndexIfNotExists(stmt, "idx_categoria_usuario", "categoria", "id_usuario");
            
            System.out.println("✓ Banco de dados inicializado com sucesso");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cria um índice se ele não existir
     */
    private static void createIndexIfNotExists(Statement stmt, String indexName, String tableName, String columnName) {
        try {
            stmt.execute("CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")");
        } catch (SQLException e) {
            // Índice já existe, ignorar erro
            if (!e.getMessage().contains("Duplicate key name")) {
                System.err.println("Aviso ao criar índice " + indexName + ": " + e.getMessage());
            }
        }
    }
}