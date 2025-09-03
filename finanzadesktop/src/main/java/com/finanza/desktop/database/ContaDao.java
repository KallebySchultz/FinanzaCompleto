package com.finanza.desktop.database;

import com.finanza.desktop.model.Conta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para Conta
 */
public class ContaDao {
    private final DatabaseManager dbManager;

    public ContaDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int inserir(Conta conta) {
        String sql = "INSERT INTO contas (nome, saldo_inicial, usuario_id) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, conta.getNome());
            stmt.setDouble(2, conta.getSaldoInicial());
            stmt.setInt(3, conta.getUsuarioId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        conta.setId(id);
                        return id;
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir conta", e);
        }
        
        return 0;
    }

    public Conta buscarPorId(int id) {
        String sql = "SELECT * FROM contas WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConta(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta", e);
        }
        
        return null;
    }

    public List<Conta> listarPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM contas WHERE usuario_id = ? ORDER BY nome";
        List<Conta> contas = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contas.add(mapResultSetToConta(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contas", e);
        }
        
        return contas;
    }

    public List<Conta> listarTodas() {
        String sql = "SELECT * FROM contas ORDER BY nome";
        List<Conta> contas = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                contas.add(mapResultSetToConta(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as contas", e);
        }
        
        return contas;
    }

    public boolean atualizar(Conta conta) {
        String sql = "UPDATE contas SET nome = ?, saldo_inicial = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conta.getNome());
            stmt.setDouble(2, conta.getSaldoInicial());
            stmt.setInt(3, conta.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar conta", e);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM contas WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar conta", e);
        }
    }

    public double calcularSaldoAtual(int contaId) {
        String sql = "SELECT c.saldo_inicial + COALESCE(" +
            "(SELECT SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END)" +
            " FROM lancamentos l WHERE l.conta_id = c.id), 0" +
            ") as saldo_atual" +
            " FROM contas c WHERE c.id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo_atual");
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular saldo atual", e);
        }
        
        return 0.0;
    }

    private Conta mapResultSetToConta(ResultSet rs) throws SQLException {
        Conta conta = new Conta();
        conta.setId(rs.getInt("id"));
        conta.setNome(rs.getString("nome"));
        conta.setSaldoInicial(rs.getDouble("saldo_inicial"));
        conta.setUsuarioId(rs.getInt("usuario_id"));
        return conta;
    }
}