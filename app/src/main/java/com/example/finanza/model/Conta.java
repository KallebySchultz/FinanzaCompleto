package com.example.finanza.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Conta {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nome;
    public double saldoInicial;
    public int usuarioId;
}