package controller;

import model.Categoria;
import database.CategoriaDAO;
import java.sql.SQLException;
import java.util.List;

/**
 * CategoriaController - Controller for category operations
 */
public class CategoriaController {
    private final CategoriaDAO categoriaDAO;
    
    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }
    
    /**
     * Create new category
     */
    public boolean criarCategoria(String nome, String corHex, String tipo) {
        try {
            if (!isNomeValido(nome) || !isTipoValido(tipo)) {
                return false;
            }
            
            Categoria novaCategoria = new Categoria(nome.trim(), corHex, tipo);
            return categoriaDAO.inserir(novaCategoria);
        } catch (SQLException e) {
            System.err.println("Erro ao criar categoria: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update category
     */
    public boolean atualizarCategoria(int id, String nome, String corHex, String tipo) {
        try {
            if (!isNomeValido(nome) || !isTipoValido(tipo)) {
                return false;
            }
            
            Categoria categoria = categoriaDAO.buscarPorId(id);
            if (categoria == null) {
                return false;
            }
            
            categoria.setNome(nome.trim());
            categoria.setCorHex(corHex);
            categoria.setTipo(tipo);
            
            return categoriaDAO.atualizar(categoria);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete category
     */
    public boolean excluirCategoria(int id) {
        try {
            return categoriaDAO.deletar(id);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir categoria: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get category by ID
     */
    public Categoria obterCategoria(int id) {
        try {
            return categoriaDAO.buscarPorId(id);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List all categories
     */
    public List<Categoria> listarCategorias() {
        try {
            return categoriaDAO.listarTodas();
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List expense categories
     */
    public List<Categoria> listarCategoriasDespesas() {
        try {
            return categoriaDAO.listarDespesas();
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias de despesas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List income categories
     */
    public List<Categoria> listarCategoriasReceitas() {
        try {
            return categoriaDAO.listarReceitas();
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias de receitas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * List categories by type
     */
    public List<Categoria> listarPorTipo(String tipo) {
        try {
            if (!isTipoValido(tipo)) {
                return null;
            }
            return categoriaDAO.listarPorTipo(tipo);
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias por tipo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate category name
     */
    public boolean isNomeValido(String nome) {
        return nome != null && !nome.trim().isEmpty() && nome.trim().length() <= 50;
    }
    
    /**
     * Validate category type
     */
    public boolean isTipoValido(String tipo) {
        return "receita".equals(tipo) || "despesa".equals(tipo);
    }
    
    /**
     * Validate color hex format
     */
    public boolean isCorHexValida(String corHex) {
        if (corHex == null || corHex.isEmpty()) {
            return true; // Color is optional
        }
        return corHex.matches("^#[0-9A-Fa-f]{6}$");
    }
    
    /**
     * Get default colors for categories
     */
    public String[] getCoresDisponiveis() {
        return new String[]{
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85929E",
            "#2ECC71", "#3498DB", "#9B59B6", "#E67E22", "#F1C40F",
            "#1ABC9C", "#34495E", "#E74C3C", "#F39C12", "#8E44AD"
        };
    }
    
    /**
     * Get random color from available colors
     */
    public String getCorAleatoria() {
        String[] cores = getCoresDisponiveis();
        int index = (int) (Math.random() * cores.length);
        return cores[index];
    }
}