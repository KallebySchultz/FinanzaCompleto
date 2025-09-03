package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.example.finanza.model.Usuario;
import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    long inserir(Usuario usuario);

    @Update
    void atualizar(Usuario usuario);
    
    @Delete
    void deletar(Usuario usuario);

    @Query("SELECT * FROM Usuario WHERE id = :id")
    Usuario buscarPorId(int id);

    @Query("SELECT * FROM Usuario")
    List<Usuario> listarTodos();
    
    @Query("SELECT * FROM Usuario WHERE email = :email")
    Usuario buscarPorEmail(String email);
    
    @Query("SELECT * FROM Usuario WHERE email = :email AND senha = :senha")
    Usuario login(String email, String senha);
}