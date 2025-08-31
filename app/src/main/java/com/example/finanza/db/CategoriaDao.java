package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.finanza.model.Categoria;
import java.util.List;

@Dao
public interface CategoriaDao {
    @Insert
    long inserir(Categoria categoria);

    @Query("SELECT * FROM Categoria WHERE tipo = :tipo")
    List<Categoria> listarPorTipo(String tipo);

    @Query("SELECT * FROM Categoria")
    List<Categoria> listarTodas();
}