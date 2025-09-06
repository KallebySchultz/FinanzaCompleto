package model;

import java.sql.Timestamp;

/**
 * Classe modelo para representar uma categoria de movimentação (versão cliente)
 */
public class Categoria {
    private int id;
    private String nome;
    private TipoCategoria tipo;
    private int idUsuario;
    private Timestamp dataCriacao;
    
    public enum TipoCategoria {
        RECEITA("receita"),
        DESPESA("despesa");
        
        private final String valor;
        
        TipoCategoria(String valor) {
            this.valor = valor;
        }
        
        public String getValor() {
            return valor;
        }
        
        public String getDescricao() {
            switch (this) {
                case RECEITA: return "Receita";
                case DESPESA: return "Despesa";
                default: return valor;
            }
        }
        
        public static TipoCategoria fromString(String tipo) {
            for (TipoCategoria t : TipoCategoria.values()) {
                if (t.getValor().equalsIgnoreCase(tipo)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Tipo de categoria inválido: " + tipo);
        }
    }
    
    // Constructors
    public Categoria() {}
    
    public Categoria(String nome, TipoCategoria tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
    
    public Categoria(int id, String nome, TipoCategoria tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public TipoCategoria getTipo() { return tipo; }
    public void setTipo(TipoCategoria tipo) { this.tipo = tipo; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }
    
    @Override
    public String toString() {
        return nome + " (" + tipo.getDescricao() + ")";
    }
}