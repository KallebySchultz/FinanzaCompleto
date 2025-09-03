package com.finanza.desktop.model;

/**
 * Modelo de categoria para o cliente desktop
 * Representa uma categoria de lançamento financeiro
 */
public class Categoria {
    private int id;
    private String nome;
    private String corHex;
    private String tipo; // "receita" ou "despesa"

    public Categoria() {
    }

    public Categoria(String nome, String corHex, String tipo) {
        this.nome = nome;
        this.corHex = corHex;
        this.tipo = tipo;
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

    public String getCorHex() {
        return corHex;
    }

    public void setCorHex(String corHex) {
        this.corHex = corHex;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", corHex='" + corHex + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}