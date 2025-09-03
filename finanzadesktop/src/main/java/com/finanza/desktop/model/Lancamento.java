package com.finanza.desktop.model;

/**
 * Modelo para lançamentos/transações financeiras
 */
public class Lancamento {
    private int id;
    private double valor;
    private long data; // timestamp
    private String descricao;
    private int contaId;
    private int categoriaId;
    private int usuarioId;
    private String tipo; // "receita" ou "despesa"

    public Lancamento() {}

    public Lancamento(double valor, long data, String descricao, int contaId, 
                     int categoriaId, int usuarioId, String tipo) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.contaId = contaId;
        this.categoriaId = categoriaId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
    }

    // Getters e Setters
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
                ", descricao='" + descricao + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}