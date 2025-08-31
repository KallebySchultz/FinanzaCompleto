package com.example.finanza.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Usuario.class,
                        parentColumns = "id",
                        childColumns = "usuarioId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Conta.class,
                        parentColumns = "id",
                        childColumns = "contaId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Categoria.class,
                        parentColumns = "id",
                        childColumns = "categoriaId",
                        onDelete = ForeignKey.SET_NULL)
        }
)
public class Lancamento {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public double valor;
    public long data; // timestamp
    public String descricao;
    public int contaId;
    public int categoriaId;
    public int usuarioId;
    public String tipo; // "receita" ou "despesa"
}