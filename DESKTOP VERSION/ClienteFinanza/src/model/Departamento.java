package model;

/**
 * Classe modelo para representar um departamento
 */
public class Departamento {
    private int id;
    private String nome;
    private String descricao;
    private boolean ativo;
    
    // Constructors
    public Departamento() {
        this.ativo = true;
    }
    
    public Departamento(String nome, String descricao) {
        this();
        this.nome = nome;
        this.descricao = descricao;
    }
    
    public Departamento(int id, String nome, String descricao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    @Override
    public String toString() {
        return nome; // Para uso em ComboBox
    }
}