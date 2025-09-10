package dao;

import model.Categoria;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Categoria
 */
public class CategoriaDAO {
    
    /**
     * Verifica se uma categoria já existe para o usuário
     * @param nome nome da categoria
     * @param tipo tipo da categoria
     * @param idUsuario ID do usuário
     * @return Categoria existente ou null se não existe
     */
    public Categoria buscarPorNomeETipo(String nome, Categoria.TipoCategoria tipo, int idUsuario) {
        String sql = "SELECT * FROM categoria WHERE nome = ? AND tipo = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            stmt.setString(2, tipo.getValor());
            stmt.setInt(3, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria por nome e tipo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Insere uma nova categoria no banco de dados
     * @param categoria objeto Categoria a ser inserido
     * @return true se inserido com sucesso
     */
    public boolean inserir(Categoria categoria) {
        // Verifica se a categoria já existe
        Categoria existente = buscarPorNomeETipo(categoria.getNome(), categoria.getTipo(), categoria.getIdUsuario());
        if (existente != null) {
            // Categoria já existe, definir o ID e retornar true
            categoria.setId(existente.getId());
            return true;
        }
        
        String sql = "INSERT INTO categoria (nome, tipo, id_usuario) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo().getValor());
            stmt.setInt(3, categoria.getIdUsuario());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        categoria.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir categoria: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Busca categoria por ID
     * @param id ID da categoria
     * @return Categoria encontrada ou null
     */
    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categoria WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Lista todas as categorias de um usuário
     * @param idUsuario ID do usuário
     * @return Lista de categorias
     */
    public List<Categoria> listarPorUsuario(int idUsuario) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria WHERE id_usuario = ? ORDER BY tipo, nome";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias do usuário: " + e.getMessage());
        }
        
        return categorias;
    }
    
    /**
     * Lista categorias por tipo de um usuário
     * @param idUsuario ID do usuário
     * @param tipo tipo da categoria (receita/despesa)
     * @return Lista de categorias
     */
    public List<Categoria> listarPorTipo(int idUsuario, Categoria.TipoCategoria tipo) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria WHERE id_usuario = ? AND tipo = ? ORDER BY nome";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setString(2, tipo.getValor());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias por tipo: " + e.getMessage());
        }
        
        return categorias;
    }
    
    /**
     * Atualiza dados da categoria
     * @param categoria Categoria com dados atualizados
     * @return true se atualizada com sucesso
     */
    public boolean atualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nome = ?, tipo = ? WHERE id = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo().getValor());
            stmt.setInt(3, categoria.getId());
            stmt.setInt(4, categoria.getIdUsuario());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Remove categoria do banco de dados
     * @param id ID da categoria
     * @param idUsuario ID do usuário (para segurança)
     * @return true se removida com sucesso
     */
    public boolean remover(int id, int idUsuario) {
        // Primeiro verifica se a categoria está sendo usada em movimentações
        if (estaEmUso(id)) {
            System.err.println("Categoria não pode ser removida pois está em uso");
            return false;
        }
        
        String sql = "DELETE FROM categoria WHERE id = ? AND id_usuario = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setInt(2, idUsuario);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover categoria: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Verifica se a categoria está sendo usada em movimentações
     * @param idCategoria ID da categoria
     * @return true se está em uso
     */
    public boolean estaEmUso(int idCategoria) {
        String sql = "SELECT COUNT(*) FROM movimentacao WHERE id_categoria = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategoria);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao verificar uso da categoria: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Mapeia ResultSet para objeto Categoria
     */
    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setTipo(Categoria.TipoCategoria.fromString(rs.getString("tipo")));
        categoria.setIdUsuario(rs.getInt("id_usuario"));
        categoria.setDataCriacao(rs.getTimestamp("data_criacao"));
        return categoria;
    }
}