package com.finanza.desktop.controller;

import com.finanza.desktop.model.Categoria;
import com.finanza.desktop.database.CategoriaDao;
import java.util.List;

/**
 * Controller para operações de categoria
 * Segue padrões similares aos Controllers do Android
 */
public class CategoriaController {
    private final CategoriaDao categoriaDao;
    
    public CategoriaController() {
        this.categoriaDao = new CategoriaDao();
    }
    
    /**
     * Cria uma nova categoria
     * @param nome Nome da categoria
     * @param corHex Cor em hexadecimal
     * @param tipo Tipo da categoria ("receita" ou "despesa")
     * @return ID da categoria criada
     */
    public int criarCategoria(String nome, String corHex, String tipo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio");
        }
        
        if (tipo == null || (!tipo.equals("receita") && !tipo.equals("despesa"))) {
            throw new IllegalArgumentException("Tipo deve ser 'receita' ou 'despesa'");
        }
        
        if (corHex != null && !validarCorHex(corHex)) {
            throw new IllegalArgumentException("Cor deve estar no formato hexadecimal válido");
        }
        
        Categoria categoria = new Categoria();
        categoria.setNome(nome.trim());
        categoria.setCorHex(corHex);
        categoria.setTipo(tipo);
        
        return categoriaDao.inserir(categoria);
    }
    
    /**
     * Lista todas as categorias
     * @return Lista de categorias
     */
    public List<Categoria> listarTodasCategorias() {
        return categoriaDao.listarTodas();
    }
    
    /**
     * Lista categorias por tipo
     * @param tipo Tipo da categoria ("receita" ou "despesa")
     * @return Lista de categorias do tipo especificado
     */
    public List<Categoria> listarCategoriasPorTipo(String tipo) {
        if (tipo == null || (!tipo.equals("receita") && !tipo.equals("despesa"))) {
            throw new IllegalArgumentException("Tipo deve ser 'receita' ou 'despesa'");
        }
        
        return categoriaDao.listarPorTipo(tipo);
    }
    
    /**
     * Busca categoria por ID
     * @param id ID da categoria
     * @return Categoria encontrada ou null
     */
    public Categoria buscarCategoriaPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID da categoria deve ser válido");
        }
        
        return categoriaDao.buscarPorId(id);
    }
    
    /**
     * Atualiza uma categoria
     * @param categoria Categoria com dados atualizados
     * @return true se atualizado com sucesso
     */
    public boolean atualizarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        
        if (categoria.getId() <= 0) {
            throw new IllegalArgumentException("ID da categoria deve ser válido");
        }
        
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio");
        }
        
        if (categoria.getTipo() == null || 
            (!categoria.getTipo().equals("receita") && !categoria.getTipo().equals("despesa"))) {
            throw new IllegalArgumentException("Tipo deve ser 'receita' ou 'despesa'");
        }
        
        if (categoria.getCorHex() != null && !validarCorHex(categoria.getCorHex())) {
            throw new IllegalArgumentException("Cor deve estar no formato hexadecimal válido");
        }
        
        return categoriaDao.atualizar(categoria);
    }
    
    /**
     * Remove uma categoria
     * @param id ID da categoria
     * @return true se removido com sucesso
     */
    public boolean removerCategoria(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID da categoria deve ser válido");
        }
        
        return categoriaDao.deletar(id);
    }
    
    /**
     * Busca categorias por nome
     * @param nome Nome ou parte do nome da categoria
     * @return Lista de categorias encontradas
     */
    public List<Categoria> buscarCategoriasPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return listarTodasCategorias();
        }
        
        return categoriaDao.buscarPorNome(nome.trim());
    }
    
    /**
     * Valida se uma cor está no formato hexadecimal válido
     * @param corHex Cor em hexadecimal
     * @return true se válida
     */
    private boolean validarCorHex(String corHex) {
        if (corHex == null) {
            return true; // Cor é opcional
        }
        
        // Aceita formatos #RRGGBB ou RRGGBB
        return corHex.matches("^#?[0-9A-Fa-f]{6}$");
    }
    
    /**
     * Normaliza cor hexadecimal para o formato #RRGGBB
     * @param corHex Cor em hexadecimal
     * @return Cor normalizada
     */
    public String normalizarCorHex(String corHex) {
        if (corHex == null) {
            return null;
        }
        
        corHex = corHex.trim().toUpperCase();
        
        if (!corHex.startsWith("#")) {
            corHex = "#" + corHex;
        }
        
        return validarCorHex(corHex) ? corHex : null;
    }
    
    /**
     * Obtém cores padrão para categorias
     * @param tipo Tipo da categoria
     * @return Array de cores padrão
     */
    public String[] getCoresPadrao(String tipo) {
        if ("receita".equals(tipo)) {
            return new String[]{
                "#2ECC71", "#3498DB", "#9B59B6", "#E67E22", 
                "#F1C40F", "#1ABC9C", "#34495E"
            };
        } else {
            return new String[]{
                "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4",
                "#FFEAA7", "#DDA0DD", "#98D8C8", "#F7DC6F",
                "#BB8FCE", "#85929E"
            };
        }
    }
    
    /**
     * Verifica se uma categoria pode ser removida
     * (implementar lógica de negócio se necessário)
     * @param categoriaId ID da categoria
     * @return true se pode ser removida
     */
    public boolean podeRemoverCategoria(int categoriaId) {
        if (categoriaId <= 0) {
            return false;
        }
        
        Categoria categoria = categoriaDao.buscarPorId(categoriaId);
        return categoria != null;
    }
}