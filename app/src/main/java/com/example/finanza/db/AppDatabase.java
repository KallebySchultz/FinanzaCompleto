package com.example.finanza.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.finanza.model.*;

@Database(
        entities = {Usuario.class, Conta.class, Categoria.class, Lancamento.class},
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract ContaDao contaDao();
    public abstract CategoriaDao categoriaDao();
    public abstract LancamentoDao lancamentoDao();
}