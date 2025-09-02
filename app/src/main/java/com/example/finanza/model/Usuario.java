package com.example.finanza.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nome;
    public String email;
    public String senha; // Adicionado para autenticação
    public long dataCriacao; // Data de criação da conta
}