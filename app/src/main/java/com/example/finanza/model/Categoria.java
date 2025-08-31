package com.example.finanza.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Categoria {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nome;
    public String corHex; // Cor para ícone na UI
    public String tipo; // "receita" ou "despesa"
}