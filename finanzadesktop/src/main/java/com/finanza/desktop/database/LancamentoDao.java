package com.finanza.desktop.database;

import com.finanza.desktop.model.Lancamento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Lancamento
 */
public class LancamentoDao {
    private final DatabaseManager dbManager;

    public LancamentoDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int inserir(Lancamento lancamento) {
        String sql = "INSERT INTO lancamentos (valor, data, descricao, conta_id, categoria_id, usuario_id, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDouble(1, lancamento.getValor());
            stmt.setLong(2, lancamento.getData());
            stmt.setString(3, lancamento.getDescricao());
            stmt.setInt(4, lancamento.getContaId());
            stmt.setInt(5, lancamento.getCategoriaId());
            stmt.setInt(6, lancamento.getUsuarioId());
            stmt.setString(7, lancamento.getTipo());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        lancamento.setId(id);
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir lançamento", e);
        }
        
        return 0;
    }

    public Lancamento buscarPorId(int id) {
        String sql = "SELECT * FROM lancamentos WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLancamento(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar lançamento", e);
        }
        
        return null;
    }

    public List<Lancamento> listarPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? ORDER BY data DESC";
        List<Lancamento> lancamentos = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lancamentos.add(mapResultSetToLancamento(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar lançamentos por usuário", e);
        }
        
        return lancamentos;
    }

    public List<Lancamento> listarPorConta(int contaId) {
        String sql = "SELECT * FROM lancamentos WHERE conta_id = ? ORDER BY data DESC";
        List<Lancamento> lancamentos = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lancamentos.add(mapResultSetToLancamento(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar lançamentos por conta", e);
        }
        
        return lancamentos;
    }

    public List<Lancamento> listarPorPeriodo(int usuarioId, long dataInicio, long dataFim) {
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? AND data >= ? AND data <= ? ORDER BY data DESC";
        List<Lancamento> lancamentos = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setLong(2, dataInicio);
            stmt.setLong(3, dataFim);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lancamentos.add(mapResultSetToLancamento(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar lançamentos por período", e);
        }
        
        return lancamentos;
    }

    public double somarPorTipo(String tipo, int usuarioId) {
        String sql = "SELECT COALESCE(SUM(valor), 0) as total FROM lancamentos WHERE tipo = ? AND usuario_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            stmt.setInt(2, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao somar lançamentos por tipo", e);
        }
        
        return 0.0;
    }

    public double somarPorTipoEPeriodo(String tipo, int usuarioId, long dataInicio, long dataFim) {
        String sql = "SELECT COALESCE(SUM(valor), 0) as total FROM lancamentos WHERE tipo = ? AND usuario_id = ? AND data >= ? AND data <= ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            stmt.setInt(2, usuarioId);
            stmt.setLong(3, dataInicio);
            stmt.setLong(4, dataFim);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao somar lançamentos por tipo e período", e);
        }
        
        return 0.0;
    }

    public boolean atualizar(Lancamento lancamento) {
        String sql = "UPDATE lancamentos SET valor = ?, data = ?, descricao = ?, conta_id = ?, categoria_id = ?, tipo = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, lancamento.getValor());
            stmt.setLong(2, lancamento.getData());
            stmt.setString(3, lancamento.getDescricao());
            stmt.setInt(4, lancamento.getContaId());
            stmt.setInt(5, lancamento.getCategoriaId());
            stmt.setString(6, lancamento.getTipo());
            stmt.setInt(7, lancamento.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar lançamento", e);
        }
    }

    public List<Lancamento> listarPorUsuarioPeriodo(int usuarioId, long dataInicio, long dataFim) {
        return listarPorPeriodo(usuarioId, dataInicio, dataFim);
    }

    public List<Lancamento> listarUltimosPorUsuario(int usuarioId, int limite) {
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? ORDER BY data DESC LIMIT ?";
        List<Lancamento> lancamentos = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lancamentos.add(mapResultSetToLancamento(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar últimos lançamentos", e);
        }
        
        return lancamentos;
    }

    public Double somaPorTipo(String tipo, int usuarioId) {
        return somarPorTipo(tipo, usuarioId);
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM lancamentos WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar lançamento", e);
        }
    }

    private Lancamento mapResultSetToLancamento(ResultSet rs) throws SQLException {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(rs.getInt("id"));
        lancamento.setValor(rs.getDouble("valor"));
        lancamento.setData(rs.getLong("data"));
        lancamento.setDescricao(rs.getString("descricao"));
        lancamento.setContaId(rs.getInt("conta_id"));
        lancamento.setCategoriaId(rs.getInt("categoria_id"));
        lancamento.setUsuarioId(rs.getInt("usuario_id"));
        lancamento.setTipo(rs.getString("tipo"));
        return lancamento;
    }
}