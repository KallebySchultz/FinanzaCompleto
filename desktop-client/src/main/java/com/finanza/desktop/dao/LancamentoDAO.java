package com.finanza.desktop.dao;

import com.finanza.desktop.model.Lancamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com lançamentos financeiros
 */
public class LancamentoDAO {
    private DatabaseManager dbManager;

    public LancamentoDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Cria um novo lançamento
     */
    public boolean criar(Lancamento lancamento) {
        String sql = "INSERT INTO lancamentos (valor, data, descricao, conta_id, categoria_id, usuario_id, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, lancamento.getValor());
            stmt.setLong(2, lancamento.getData());
            stmt.setString(3, lancamento.getDescricao());
            stmt.setInt(4, lancamento.getContaId());
            stmt.setInt(5, lancamento.getCategoriaId());
            stmt.setInt(6, lancamento.getUsuarioId());
            stmt.setString(7, lancamento.getTipo());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    lancamento.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar lançamento: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Busca um lançamento por ID
     */
    public Lancamento buscarPorId(int id) {
        String sql = "SELECT * FROM lancamentos WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
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
        } catch (SQLException e) {
            System.err.println("Erro ao buscar lançamento: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Lista todos os lançamentos de um usuário
     */
    public List<Lancamento> listarPorUsuario(int usuarioId) {
        List<Lancamento> lancamentos = new ArrayList<>();
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? ORDER BY data DESC";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Lancamento lancamento = new Lancamento();
                lancamento.setId(rs.getInt("id"));
                lancamento.setValor(rs.getDouble("valor"));
                lancamento.setData(rs.getLong("data"));
                lancamento.setDescricao(rs.getString("descricao"));
                lancamento.setContaId(rs.getInt("conta_id"));
                lancamento.setCategoriaId(rs.getInt("categoria_id"));
                lancamento.setUsuarioId(rs.getInt("usuario_id"));
                lancamento.setTipo(rs.getString("tipo"));
                lancamentos.add(lancamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar lançamentos: " + e.getMessage());
        }
        
        return lancamentos;
    }

    /**
     * Lista lançamentos por conta
     */
    public List<Lancamento> listarPorConta(int contaId) {
        List<Lancamento> lancamentos = new ArrayList<>();
        String sql = "SELECT * FROM lancamentos WHERE conta_id = ? ORDER BY data DESC";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Lancamento lancamento = new Lancamento();
                lancamento.setId(rs.getInt("id"));
                lancamento.setValor(rs.getDouble("valor"));
                lancamento.setData(rs.getLong("data"));
                lancamento.setDescricao(rs.getString("descricao"));
                lancamento.setContaId(rs.getInt("conta_id"));
                lancamento.setCategoriaId(rs.getInt("categoria_id"));
                lancamento.setUsuarioId(rs.getInt("usuario_id"));
                lancamento.setTipo(rs.getString("tipo"));
                lancamentos.add(lancamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar lançamentos por conta: " + e.getMessage());
        }
        
        return lancamentos;
    }

    /**
     * Lista lançamentos por período
     */
    public List<Lancamento> listarPorPeriodo(int usuarioId, long dataInicio, long dataFim) {
        List<Lancamento> lancamentos = new ArrayList<>();
        String sql = "SELECT * FROM lancamentos WHERE usuario_id = ? AND data BETWEEN ? AND ? ORDER BY data DESC";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setLong(2, dataInicio);
            stmt.setLong(3, dataFim);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Lancamento lancamento = new Lancamento();
                lancamento.setId(rs.getInt("id"));
                lancamento.setValor(rs.getDouble("valor"));
                lancamento.setData(rs.getLong("data"));
                lancamento.setDescricao(rs.getString("descricao"));
                lancamento.setContaId(rs.getInt("conta_id"));
                lancamento.setCategoriaId(rs.getInt("categoria_id"));
                lancamento.setUsuarioId(rs.getInt("usuario_id"));
                lancamento.setTipo(rs.getString("tipo"));
                lancamentos.add(lancamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar lançamentos por período: " + e.getMessage());
        }
        
        return lancamentos;
    }

    /**
     * Calcula o total de receitas de um usuário
     */
    public double calcularTotalReceitas(int usuarioId) {
        String sql = "SELECT COALESCE(SUM(valor), 0) FROM lancamentos WHERE usuario_id = ? AND tipo = 'receita'";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao calcular total de receitas: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Calcula o total de despesas de um usuário
     */
    public double calcularTotalDespesas(int usuarioId) {
        String sql = "SELECT COALESCE(SUM(valor), 0) FROM lancamentos WHERE usuario_id = ? AND tipo = 'despesa'";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao calcular total de despesas: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Atualiza um lançamento
     */
    public boolean atualizar(Lancamento lancamento) {
        String sql = "UPDATE lancamentos SET valor = ?, data = ?, descricao = ?, conta_id = ?, categoria_id = ?, tipo = ? WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, lancamento.getValor());
            stmt.setLong(2, lancamento.getData());
            stmt.setString(3, lancamento.getDescricao());
            stmt.setInt(4, lancamento.getContaId());
            stmt.setInt(5, lancamento.getCategoriaId());
            stmt.setString(6, lancamento.getTipo());
            stmt.setInt(7, lancamento.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar lançamento: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Remove um lançamento
     */
    public boolean remover(int id) {
        String sql = "DELETE FROM lancamentos WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao remover lançamento: " + e.getMessage());
        }
        
        return false;
    }
}