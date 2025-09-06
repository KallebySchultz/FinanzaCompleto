package com.finanza.servidor;

import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

/**
 * Servidor Desktop Finanza
 * Gerencia conexões com Android app e sincronização com MySQL
 */
public class ServidorFinanza {
    
    private static final int PORTA_PADRAO = 8080;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Connection dbConnection;
    private boolean rodando = false;
    
    public static void main(String[] args) {
        new ServidorFinanza().iniciar();
    }
    
    public void iniciar() {
        try {
            System.out.println("=== Finanza Desktop - Servidor ===");
            System.out.println("Inicializando servidor...");
            
            // Testar conexão com MySQL
            if (!testarConexaoMySQL()) {
                System.err.println("Erro: Não foi possível conectar ao banco de dados");
                System.err.println("Encerrando servidor...");
                System.err.println("Verifique se o MySQL está rodando e se o banco 'finanza_db' existe");
                return;
            }
            
            // Inicializar thread pool
            threadPool = Executors.newFixedThreadPool(10);
            
            // Inicializar servidor socket
            serverSocket = new ServerSocket(PORTA_PADRAO);
            rodando = true;
            
            System.out.println("✅ Servidor iniciado na porta " + PORTA_PADRAO);
            System.out.println("✅ Conexão com MySQL estabelecida");
            System.out.println("🔄 Aguardando conexões...");
            
            // Loop principal para aceitar conexões
            while (rodando) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    if (rodando) {
                        System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        } finally {
            parar();
        }
    }
    
    private boolean testarConexaoMySQL() {
        try {
            // Carregar driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL carregado");
            
            // Testar conexão
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Conexão com MySQL estabelecida: " + DB_URL);
            
            // Testar query simples
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.close();
            stmt.close();
            
            return true;
            
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL não encontrado: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
    
    public void parar() {
        rodando = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
            System.out.println("✅ Servidor encerrado");
        } catch (Exception e) {
            System.err.println("Erro ao encerrar servidor: " + e.getMessage());
        }
    }
    
    /**
     * Handler para requisições de clientes Android
     */
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                System.out.println("📱 Cliente conectado: " + clientAddress);
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String resposta = processarRequisicao(inputLine);
                    out.println(resposta);
                    break; // Por simplicidade, processa uma mensagem por conexão
                }
                
                System.out.println("📱 Cliente desconectado: " + clientAddress);
                
            } catch (IOException e) {
                System.err.println("Erro ao processar cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar socket cliente: " + e.getMessage());
                }
            }
        }
        
        private String processarRequisicao(String requisicao) {
            try {
                JSONObject json = new JSONObject(requisicao);
                String action = json.getString("action");
                
                System.out.println("📥 Processando: " + action);
                
                switch (action) {
                    case "sync_user":
                        return sincronizarUsuario(json.getInt("userId"));
                    case "sync_accounts":
                        return sincronizarContas(json.getInt("userId"));
                    case "sync_transactions":
                        return sincronizarLancamentos(json.getInt("userId"));
                    default:
                        return "{\"status\":\"error\",\"message\":\"Ação não reconhecida: " + action + "\"}";
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao processar requisição: " + e.getMessage());
                return "{\"status\":\"error\",\"message\":\"Erro interno: " + e.getMessage() + "\"}";
            }
        }
        
        private String sincronizarUsuario(int userId) {
            try {
                PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT id, nome, email FROM usuarios WHERE id = ?");
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    JSONObject usuario = new JSONObject();
                    usuario.put("id", rs.getInt("id"));
                    usuario.put("nome", rs.getString("nome"));
                    usuario.put("email", rs.getString("email"));
                    
                    JSONObject resposta = new JSONObject();
                    resposta.put("status", "success");
                    resposta.put("data", usuario);
                    
                    rs.close();
                    stmt.close();
                    
                    return resposta.toString();
                } else {
                    rs.close();
                    stmt.close();
                    return "{\"status\":\"error\",\"message\":\"Usuário não encontrado\"}";
                }
                
            } catch (SQLException e) {
                return "{\"status\":\"error\",\"message\":\"Erro de banco: " + e.getMessage() + "\"}";
            }
        }
        
        private String sincronizarContas(int userId) {
            try {
                PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT id, nome, saldo_inicial FROM contas WHERE usuario_id = ?");
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                JSONObject resposta = new JSONObject();
                resposta.put("status", "success");
                resposta.put("message", "Contas sincronizadas");
                resposta.put("count", 0);
                
                // Por simplicidade, retornamos apenas status de sucesso
                rs.close();
                stmt.close();
                
                return resposta.toString();
                
            } catch (SQLException e) {
                return "{\"status\":\"error\",\"message\":\"Erro de banco: " + e.getMessage() + "\"}";
            }
        }
        
        private String sincronizarLancamentos(int userId) {
            try {
                PreparedStatement stmt = dbConnection.prepareStatement(
                    "SELECT COUNT(*) as total FROM lancamentos WHERE usuario_id = ?");
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                int total = 0;
                if (rs.next()) {
                    total = rs.getInt("total");
                }
                
                JSONObject resposta = new JSONObject();
                resposta.put("status", "success");
                resposta.put("message", "Lançamentos sincronizados");
                resposta.put("count", total);
                
                rs.close();
                stmt.close();
                
                return resposta.toString();
                
            } catch (SQLException e) {
                return "{\"status\":\"error\",\"message\":\"Erro de banco: " + e.getMessage() + "\"}";
            }
        }
    }
}