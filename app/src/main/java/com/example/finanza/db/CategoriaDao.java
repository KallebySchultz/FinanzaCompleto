package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.finanza.model.Categoria;
import java.util.List;

@Dao
public interface CategoriaDao {
    @Insert
    long inserir(Categoria categoria);

    @Update
    void atualizar(Categoria categoria);

    @Delete
    void deletar(Categoria categoria);

    @Query("SELECT * FROM Categoria WHERE tipo = :tipo")
    List<Categoria> listarPorTipo(String tipo);

    @Query("SELECT * FROM Categoria")
    List<Categoria> listarTodas();

    @Query("SELECT * FROM Categoria WHERE id = :id")
    Categoria buscarPorId(int id);
}