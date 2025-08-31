package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.finanza.model.Usuario;
import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    long inserir(Usuario usuario);

    @Query("SELECT * FROM Usuario WHERE id = :id")
    Usuario buscarPorId(int id);

    @Query("SELECT * FROM Usuario")
    List<Usuario> listarTodos();
}