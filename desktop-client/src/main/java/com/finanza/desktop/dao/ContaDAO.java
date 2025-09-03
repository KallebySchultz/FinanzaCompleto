package com.finanza.desktop.dao;

import com.finanza.desktop.model.Conta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com contas bancárias
 */
public class ContaDAO {
    private DatabaseManager dbManager;

    public ContaDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Cria uma nova conta
     */
    public boolean criar(Conta conta) {
        String sql = "INSERT INTO contas (nome, saldo_inicial, usuario_id) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, conta.getNome());
            stmt.setDouble(2, conta.getSaldoInicial());
            stmt.setInt(3, conta.getUsuarioId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    conta.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar conta: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Busca uma conta por ID
     */
    public Conta buscarPorId(int id) {
        String sql = "SELECT * FROM contas WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Conta conta = new Conta();
                conta.setId(rs.getInt("id"));
                conta.setNome(rs.getString("nome"));
                conta.setSaldoInicial(rs.getDouble("saldo_inicial"));
                conta.setUsuarioId(rs.getInt("usuario_id"));
                return conta;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar conta: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Lista todas as contas de um usuário
     */
    public List<Conta> listarPorUsuario(int usuarioId) {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas WHERE usuario_id = ? ORDER BY nome";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Conta conta = new Conta();
                conta.setId(rs.getInt("id"));
                conta.setNome(rs.getString("nome"));
                conta.setSaldoInicial(rs.getDouble("saldo_inicial"));
                conta.setUsuarioId(rs.getInt("usuario_id"));
                contas.add(conta);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
        }
        
        return contas;
    }

    /**
     * Calcula o saldo atual de uma conta (saldo inicial + lançamentos)
     */
    public double calcularSaldoAtual(int contaId) {
        String sql = "SELECT " +
                "c.saldo_inicial," +
                "COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as movimento_total " +
                "FROM contas c " +
                "LEFT JOIN lancamentos l ON c.id = l.conta_id " +
                "WHERE c.id = ? " +
                "GROUP BY c.id, c.saldo_inicial";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, contaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double saldoInicial = rs.getDouble("saldo_inicial");
                double movimentoTotal = rs.getDouble("movimento_total");
                return saldoInicial + movimentoTotal;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao calcular saldo: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Atualiza uma conta
     */
    public boolean atualizar(Conta conta) {
        String sql = "UPDATE contas SET nome = ?, saldo_inicial = ? WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, conta.getNome());
            stmt.setDouble(2, conta.getSaldoInicial());
            stmt.setInt(3, conta.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar conta: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Remove uma conta
     */
    public boolean remover(int id) {
        String sql = "DELETE FROM contas WHERE id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao remover conta: " + e.getMessage());
        }
        
        return false;
    }
}