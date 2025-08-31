package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.finanza.model.Lancamento;
import java.util.List;

@Dao
public interface LancamentoDao {
    @Insert
    long inserir(Lancamento lancamento);

    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC")
    List<Lancamento> listarPorUsuario(int usuarioId);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId")
    Double somaPorTipo(String tipo, int usuarioId);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND contaId = :contaId AND usuarioId = :usuarioId")
    Double somaPorTipoConta(String tipo, int contaId, int usuarioId);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE contaId = :contaId AND usuarioId = :usuarioId")
    Double saldoPorConta(int contaId, int usuarioId);

    // --- Adicione este método ---
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND data >= :inicio AND data <= :fim ORDER BY data DESC")
    List<Lancamento> listarPorUsuarioPeriodo(int usuarioId, long inicio, long fim);
}