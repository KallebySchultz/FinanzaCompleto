package model;

/**
 * Model class for Conta (Account)
 * Represents a financial account in the Finanza system
 */
public class Conta {
    private int id;
    private String nome;
    private double saldoInicial;
    private int usuarioId;
    
    // Constructors
    public Conta() {}
    
    public Conta(String nome, double saldoInicial, int usuarioId) {
        this.nome = nome;
        this.saldoInicial = saldoInicial;
        this.usuarioId = usuarioId;
    }
    
    public Conta(int id, String nome, double saldoInicial, int usuarioId) {
        this.id = id;
        this.nome = nome;
        this.saldoInicial = saldoInicial;
        this.usuarioId = usuarioId;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public double getSaldoInicial() {
        return saldoInicial;
    }
    
    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
    
    public int getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", saldoInicial=" + saldoInicial +
                ", usuarioId=" + usuarioId +
                '}';
    }
}