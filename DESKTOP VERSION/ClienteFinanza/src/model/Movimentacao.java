package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Classe modelo para representar uma movimentação financeira (versão cliente)
 */
public class Movimentacao {
    private int id;
    private double valor;
    private Date data;
    private String descricao;
    private TipoMovimentacao tipo;
    private int idConta;
    private int idCategoria;
    private int idUsuario;
    private Timestamp dataCriacao;
    private Timestamp dataAtualizacao;
    
    // Campos adicionais para exibição
    private String nomeConta;
    private String nomeCategoria;
    
    public enum TipoMovimentacao {
        RECEITA("receita"),
        DESPESA("despesa");
        
        private final String valor;
        
        TipoMovimentacao(String valor) {
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
        
        public static TipoMovimentacao fromString(String tipo) {
            for (TipoMovimentacao t : TipoMovimentacao.values()) {
                if (t.getValor().equalsIgnoreCase(tipo)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Tipo de movimentação inválido: " + tipo);
        }
    }
    
    // Constructors
    public Movimentacao() {}
    
    public Movimentacao(double valor, Date data, String descricao, TipoMovimentacao tipo, int idConta, int idCategoria) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.tipo = tipo;
        this.idConta = idConta;
        this.idCategoria = idCategoria;
    }
    
    public Movimentacao(int id, double valor, Date data, String descricao, TipoMovimentacao tipo, int idConta, int idCategoria) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.tipo = tipo;
        this.idConta = idConta;
        this.idCategoria = idCategoria;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    
    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    
    public int getIdConta() { return idConta; }
    public void setIdConta(int idConta) { this.idConta = idConta; }
    
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public Timestamp getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Timestamp dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    
    public String getNomeConta() { return nomeConta; }
    public void setNomeConta(String nomeConta) { this.nomeConta = nomeConta; }
    
    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }
    
    @Override
    public String toString() {
        return String.format("%.2f - %s (%s)", valor, descricao != null ? descricao : "Sem descrição", tipo.getDescricao());
    }
}