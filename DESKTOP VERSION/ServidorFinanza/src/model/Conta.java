package model;

import java.sql.Timestamp;

/**
 * Classe modelo para representar uma conta bancária
 */
public class Conta {
    private int id;
    private String nome;
    private TipoConta tipo;
    private double saldoInicial;
    private int idUsuario;
    private Timestamp dataCriacao;
    
    public enum TipoConta {
        CORRENTE("corrente"),
        POUPANCA("poupanca"), 
        CARTAO("cartao"),
        INVESTIMENTO("investimento"),
        DINHEIRO("dinheiro");
        
        private final String valor;
        
        TipoConta(String valor) {
            this.valor = valor;
        }
        
        public String getValor() {
            return valor;
        }
        
        public static TipoConta fromString(String tipo) {
            for (TipoConta t : TipoConta.values()) {
                if (t.getValor().equalsIgnoreCase(tipo)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Tipo de conta inválido: " + tipo);
        }
    }
    
    // Constructors
    public Conta() {}
    
    public Conta(String nome, TipoConta tipo, double saldoInicial, int idUsuario) {
        this.nome = nome;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.idUsuario = idUsuario;
    }
    
    public Conta(int id, String nome, TipoConta tipo, double saldoInicial, int idUsuario) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.idUsuario = idUsuario;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public TipoConta getTipo() { return tipo; }
    public void setTipo(TipoConta tipo) { this.tipo = tipo; }
    
    public double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(double saldoInicial) { this.saldoInicial = saldoInicial; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }
    
    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", tipo=" + tipo +
                ", saldoInicial=" + saldoInicial +
                ", idUsuario=" + idUsuario +
                '}';
    }
}