package model;

/**
 * Model class for Lancamento (Transaction)
 * Represents a financial transaction in the Finanza system
 */
public class Lancamento {
    private int id;
    private double valor;
    private long data;
    private String descricao;
    private int contaId;
    private int categoriaId;
    private int usuarioId;
    private String tipo; // 'receita' or 'despesa'
    
    // Constructors
    public Lancamento() {}
    
    public Lancamento(double valor, long data, String descricao, int contaId, int categoriaId, int usuarioId, String tipo) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.contaId = contaId;
        this.categoriaId = categoriaId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
    }
    
    public Lancamento(int id, double valor, long data, String descricao, int contaId, int categoriaId, int usuarioId, String tipo) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.contaId = contaId;
        this.categoriaId = categoriaId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double getValor() {
        return valor;
    }
    
    public void setValor(double valor) {
        this.valor = valor;
    }
    
    public long getData() {
        return data;
    }
    
    public void setData(long data) {
        this.data = data;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public int getContaId() {
        return contaId;
    }
    
    public void setContaId(int contaId) {
        this.contaId = contaId;
    }
    
    public int getCategoriaId() {
        return categoriaId;
    }
    
    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
    
    public int getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    @Override
    public String toString() {
        return "Lancamento{" +
                "id=" + id +
                ", valor=" + valor +
                ", data=" + data +
                ", descricao='" + descricao + '\'' +
                ", contaId=" + contaId +
                ", categoriaId=" + categoriaId +
                ", usuarioId=" + usuarioId +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}