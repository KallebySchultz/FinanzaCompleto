package dao;

import model.Movimentacao;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Movimentacao
 */
public class MovimentacaoDAO {
    
    /**
     * Verifica se uma movimentação já existe
     * @param valor valor da movimentação
     * @param data data da movimentação
     * @param descricao descrição da movimentação
     * @param idConta ID da conta
     * @param idUsuario ID do usuário
     * @return Movimentacao existente ou null se não existe
     */
    public Movimentacao buscarDuplicata(double valor, Date data, String descricao, int idConta, int idUsuario) {
        String sql = "SELECT * FROM movimentacao WHERE valor = ? AND data = ? AND descricao = ? AND id_conta = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, valor);
            stmt.setDate(2, data);
            stmt.setString(3, descricao);
            stmt.setInt(4, idConta);
            stmt.setInt(5, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMovimentacao(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar movimentação duplicata: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Insere uma nova movimentação no banco de dados
     * @param movimentacao objeto Movimentacao a ser inserido
     * @return true se inserido com sucesso
     */
    public boolean inserir(Movimentacao movimentacao) {
        // Verifica se a movimentação já existe (evita duplicatas)
        Movimentacao existente = buscarDuplicata(
            movimentacao.getValor(), 
            movimentacao.getData(), 
            movimentacao.getDescricao(),
            movimentacao.getIdConta(),
            movimentacao.getIdUsuario()
        );
        if (existente != null) {
            // Movimentação já existe, definir o ID e retornar true
            movimentacao.setId(existente.getId());
            return true;
        }
        
        String sql = "INSERT INTO movimentacao (valor, data, descricao, tipo, id_conta, id_categoria, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDouble(1, movimentacao.getValor());
            stmt.setDate(2, movimentacao.getData());
            stmt.setString(3, movimentacao.getDescricao());
            stmt.setString(4, movimentacao.getTipo().getValor());
            stmt.setInt(5, movimentacao.getIdConta());
            stmt.setInt(6, movimentacao.getIdCategoria());
            stmt.setInt(7, movimentacao.getIdUsuario());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        movimentacao.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir movimentação: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Busca movimentação por ID
     * @param id ID da movimentação
     * @return Movimentacao encontrada ou null
     */
    public Movimentacao buscarPorId(int id) {
        String sql = "SELECT * FROM movimentacao WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMovimentacao(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar movimentação por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Busca movimentação por ID e usuário (para validação de segurança)
     * @param id ID da movimentação
     * @param idUsuario ID do usuário
     * @return Movimentacao encontrada ou null
     */
    public Movimentacao buscarPorIdEUsuario(int id, int idUsuario) {
        String sql = "SELECT * FROM movimentacao WHERE id = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setInt(2, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMovimentacao(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar movimentação por ID e usuário: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Lista todas as movimentações de um usuário
     * @param idUsuario ID do usuário
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarPorUsuario(int idUsuario) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT * FROM movimentacao WHERE id_usuario = ? ORDER BY data DESC, id DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(mapResultSetToMovimentacao(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar movimentações do usuário: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Lista movimentações de um usuário por período
     * @param idUsuario ID do usuário
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarPorPeriodo(int idUsuario, Date dataInicio, Date dataFim) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT * FROM movimentacao WHERE id_usuario = ? AND data BETWEEN ? AND ? ORDER BY data DESC, id DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setDate(2, dataInicio);
            stmt.setDate(3, dataFim);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(mapResultSetToMovimentacao(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar movimentações por período: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Lista movimentações por conta
     * @param idConta ID da conta
     * @param idUsuario ID do usuário (para segurança)
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarPorConta(int idConta, int idUsuario) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT * FROM movimentacao WHERE id_conta = ? AND id_usuario = ? ORDER BY data DESC, id DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idConta);
            stmt.setInt(2, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(mapResultSetToMovimentacao(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar movimentações por conta: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Lista movimentações por categoria
     * @param idCategoria ID da categoria
     * @param idUsuario ID do usuário (para segurança)
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarPorCategoria(int idCategoria, int idUsuario) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT * FROM movimentacao WHERE id_categoria = ? AND id_usuario = ? ORDER BY data DESC, id DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategoria);
            stmt.setInt(2, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(mapResultSetToMovimentacao(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar movimentações por categoria: " + e.getMessage());
        }
        
        return movimentacoes;
    }
    
    /**
     * Atualiza dados da movimentação
     * @param movimentacao Movimentacao com dados atualizados
     * @return true se atualizada com sucesso
     */
    public boolean atualizar(Movimentacao movimentacao) {
        // First check if the movimentacao exists and belongs to the user
        Movimentacao existente = buscarPorIdEUsuario(movimentacao.getId(), movimentacao.getIdUsuario());
        if (existente == null) {
            System.err.println("Erro ao atualizar movimentação: ID " + movimentacao.getId() + 
                             " não encontrado ou não pertence ao usuário " + movimentacao.getIdUsuario());
            return false;
        }
        
        String sql = "UPDATE movimentacao SET valor = ?, data = ?, descricao = ?, tipo = ?, id_conta = ?, id_categoria = ? WHERE id = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, movimentacao.getValor());
            stmt.setDate(2, movimentacao.getData());
            stmt.setString(3, movimentacao.getDescricao());
            stmt.setString(4, movimentacao.getTipo().getValor());
            stmt.setInt(5, movimentacao.getIdConta());
            stmt.setInt(6, movimentacao.getIdCategoria());
            stmt.setInt(7, movimentacao.getId());
            stmt.setInt(8, movimentacao.getIdUsuario());
            
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Movimentação atualizada: ID " + movimentacao.getId() + 
                             ", rows affected: " + rowsUpdated);
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL ao atualizar movimentação ID " + movimentacao.getId() + ": " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Remove movimentação do banco de dados
     * @param id ID da movimentação
     * @param idUsuario ID do usuário (para segurança)
     * @return true se removida com sucesso
     */
    public boolean remover(int id, int idUsuario) {
        // First check if the movimentacao exists and belongs to the user
        Movimentacao existente = buscarPorIdEUsuario(id, idUsuario);
        if (existente == null) {
            System.err.println("Erro ao remover movimentação: ID " + id + 
                             " não encontrado ou não pertence ao usuário " + idUsuario);
            return false;
        }
        
        String sql = "DELETE FROM movimentacao WHERE id = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setInt(2, idUsuario);
            
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Movimentação removida: ID " + id + 
                             ", rows affected: " + rowsDeleted);
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL ao remover movimentação ID " + id + ": " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Exclui movimentação do banco de dados (admin - sem verificação de usuário)
     * @param id ID da movimentação
     * @return true se excluída com sucesso
     */
    public boolean excluir(int id) {
        String sql = "DELETE FROM movimentacao WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Movimentação excluída (admin): ID " + id + 
                             ", rows affected: " + rowsDeleted);
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL ao excluir movimentação ID " + id + ": " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Calcula total de receitas de um usuário por período
     * @param idUsuario ID do usuário
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return total de receitas
     */
    public double calcularTotalReceitas(int idUsuario, Date dataInicio, Date dataFim) {
        String sql = "SELECT COALESCE(SUM(valor), 0) as total FROM movimentacao WHERE id_usuario = ? AND tipo = 'receita' AND data BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setDate(2, dataInicio);
            stmt.setDate(3, dataFim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao calcular total de receitas: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Calcula total de despesas de um usuário por período
     * @param idUsuario ID do usuário
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return total de despesas
     */
    public double calcularTotalDespesas(int idUsuario, Date dataInicio, Date dataFim) {
        String sql = "SELECT COALESCE(SUM(valor), 0) as total FROM movimentacao WHERE id_usuario = ? AND tipo = 'despesa' AND data BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setDate(2, dataInicio);
            stmt.setDate(3, dataFim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao calcular total de despesas: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Conta número de movimentações de um usuário
     * @param idUsuario ID do usuário
     * @return número de movimentações
     */
    public int contarMovimentacoes(int idUsuario) {
        String sql = "SELECT COUNT(*) as total FROM movimentacao WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao contar movimentações: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Mapeia ResultSet para objeto Movimentacao
     */
    private Movimentacao mapResultSetToMovimentacao(ResultSet rs) throws SQLException {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setId(rs.getInt("id"));
        movimentacao.setValor(rs.getDouble("valor"));
        movimentacao.setData(rs.getDate("data"));
        movimentacao.setDescricao(rs.getString("descricao"));
        movimentacao.setTipo(Movimentacao.TipoMovimentacao.fromString(rs.getString("tipo")));
        movimentacao.setIdConta(rs.getInt("id_conta"));
        movimentacao.setIdCategoria(rs.getInt("id_categoria"));
        movimentacao.setIdUsuario(rs.getInt("id_usuario"));
        movimentacao.setDataCriacao(rs.getTimestamp("data_criacao"));
        movimentacao.setDataAtualizacao(rs.getTimestamp("data_atualizacao"));
        return movimentacao;
    }
}