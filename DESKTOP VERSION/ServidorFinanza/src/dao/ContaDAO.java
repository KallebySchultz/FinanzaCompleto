package dao;

import model.Conta;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Conta
 */
public class ContaDAO {
    
    /**
     * Verifica se uma conta já existe para o usuário
     * @param nome nome da conta
     * @param idUsuario ID do usuário
     * @return Conta existente ou null se não existe
     */
    public Conta buscarPorNomeEUsuario(String nome, int idUsuario) {
        String sql = "SELECT * FROM conta WHERE nome = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            stmt.setInt(2, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConta(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar conta por nome e usuário: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Insere uma nova conta no banco de dados
     * @param conta objeto Conta a ser inserido
     * @return true se inserido com sucesso
     */
    public boolean inserir(Conta conta) {
        // Verifica se a conta já existe
        Conta existente = buscarPorNomeEUsuario(conta.getNome(), conta.getIdUsuario());
        if (existente != null) {
            // Conta já existe, definir o ID e retornar true
            conta.setId(existente.getId());
            return true;
        }
        
        String sql = "INSERT INTO conta (nome, tipo, saldo_inicial, id_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, conta.getNome());
            stmt.setString(2, conta.getTipo().getValor());
            stmt.setDouble(3, conta.getSaldoInicial());
            stmt.setInt(4, conta.getIdUsuario());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        conta.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir conta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Busca conta por ID
     * @param id ID da conta
     * @return Conta encontrada ou null
     */
    public Conta buscarPorId(int id) {
        String sql = "SELECT * FROM conta WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConta(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar conta por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Lista todas as contas de um usuário
     * @param idUsuario ID do usuário
     * @return Lista de contas
     */
    public List<Conta> listarPorUsuario(int idUsuario) {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM conta WHERE id_usuario = ? ORDER BY nome";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contas.add(mapResultSetToConta(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar contas do usuário: " + e.getMessage());
        }
        
        return contas;
    }
    
    /**
     * Atualiza dados da conta
     * @param conta Conta com dados atualizados
     * @return true se atualizada com sucesso
     */
    public boolean atualizar(Conta conta) {
        String sql = "UPDATE conta SET nome = ?, tipo = ?, saldo_inicial = ? WHERE id = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conta.getNome());
            stmt.setString(2, conta.getTipo().getValor());
            stmt.setDouble(3, conta.getSaldoInicial());
            stmt.setInt(4, conta.getId());
            stmt.setInt(5, conta.getIdUsuario());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar conta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Remove conta do banco de dados
     * @param id ID da conta
     * @param idUsuario ID do usuário (para segurança)
     * @return true se removida com sucesso
     */
    public boolean remover(int id, int idUsuario) {
        String sql = "DELETE FROM conta WHERE id = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setInt(2, idUsuario);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover conta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Exclui conta do banco de dados (admin - sem verificação de usuário)
     * @param id ID da conta
     * @return true se excluída com sucesso
     */
    public boolean excluir(int id) {
        String sql = "DELETE FROM conta WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao excluir conta: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Calcula saldo atual da conta (saldo inicial + movimentações)
     * @param idConta ID da conta
     * @return saldo atual
     */
    public double calcularSaldoAtual(int idConta) {
        String sql = "SELECT c.saldo_inicial, " +
                    "COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0) as receitas, " +
                    "COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0) as despesas " +
                    "FROM conta c " +
                    "LEFT JOIN movimentacao m ON c.id = m.id_conta " +
                    "WHERE c.id = ? " +
                    "GROUP BY c.id, c.saldo_inicial";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double saldoInicial = rs.getDouble("saldo_inicial");
                    double receitas = rs.getDouble("receitas");
                    double despesas = rs.getDouble("despesas");
                    return saldoInicial + receitas - despesas;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao calcular saldo da conta: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Mapeia ResultSet para objeto Conta
     */
    private Conta mapResultSetToConta(ResultSet rs) throws SQLException {
        Conta conta = new Conta();
        conta.setId(rs.getInt("id"));
        conta.setNome(rs.getString("nome"));
        conta.setTipo(Conta.TipoConta.fromString(rs.getString("tipo")));
        conta.setSaldoInicial(rs.getDouble("saldo_inicial"));
        conta.setIdUsuario(rs.getInt("id_usuario"));
        conta.setDataCriacao(rs.getTimestamp("data_criacao"));
        return conta;
    }
}