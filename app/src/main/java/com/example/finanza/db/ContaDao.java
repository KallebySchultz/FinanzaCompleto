package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.finanza.model.Conta;
import java.util.List;

@Dao
public interface ContaDao {
    @Insert
    long inserir(Conta conta);

    @Update
    void atualizar(Conta conta);

    @Delete
    void deletar(Conta conta);

    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId")
    List<Conta> listarPorUsuario(int usuarioId);

    @Query("SELECT * FROM Conta WHERE id = :id")
    Conta buscarPorId(int id);

    @Query("SELECT * FROM Conta")
    List<Conta> listarTodos(); // <-- Novo método para buscar todas as contas
}