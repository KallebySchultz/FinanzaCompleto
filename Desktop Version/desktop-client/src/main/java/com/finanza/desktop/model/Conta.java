package com.finanza.desktop.model;

/**
 * Modelo de conta bancária para o cliente desktop
 * Representa uma conta bancária do usuário
 */
public class Conta {
    private int id;
    private String nome;
    private double saldoInicial;
    private int usuarioId;

    public Conta() {
    }

    public Conta(String nome, double saldoInicial, int usuarioId) {
        this.nome = nome;
        this.saldoInicial = saldoInicial;
        this.usuarioId = usuarioId;
    }

    // Getters and setters
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

    /**
     * Retorna o saldo da conta (inicialmente igual ao saldo inicial)
     * Este método pode ser expandido para calcular o saldo baseado nas transações
     */
    public double getSaldo() {
        return saldoInicial;
    }

    @Override
    public String toString() {
        return nome; // Para exibição em ComboBox
    }
}