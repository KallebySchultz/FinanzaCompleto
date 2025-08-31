package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.finanza.model.Conta;
import java.util.List;

@Dao
public interface ContaDao {
    @Insert
    long inserir(Conta conta);

    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId")
    List<Conta> listarPorUsuario(int usuarioId);

    @Query("SELECT * FROM Conta WHERE id = :id")
    Conta buscarPorId(int id);
}