package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.finanza.model.Lancamento;
import java.util.List;

@Dao
public interface LancamentoDao {
    @Insert
    long inserir(Lancamento lancamento);

    @Update
    void atualizar(Lancamento lancamento);

    @Delete
    void deletar(Lancamento lancamento);

    @Query("DELETE FROM Lancamento WHERE usuarioId = :usuarioId")
    void excluirPorUsuario(int usuarioId);

    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC")
    List<Lancamento> listarPorUsuario(int usuarioId);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId")
    Double somaPorTipo(String tipo, int usuarioId);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId AND contaId = :contaId")
    Double somaPorTipoConta(String tipo, int usuarioId, int contaId);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE contaId = :contaId AND usuarioId = :usuarioId")
    Double saldoPorConta(int contaId, int usuarioId);

    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND data >= :inicio AND data <= :fim ORDER BY data DESC")
    List<Lancamento> listarPorUsuarioPeriodo(int usuarioId, long inicio, long fim);

    @Query("SELECT * FROM Lancamento WHERE id = :id")
    Lancamento buscarPorId(int id);

    @Query("SELECT * FROM Lancamento WHERE contaId = :contaId ORDER BY data DESC")
    List<Lancamento> listarPorConta(int contaId);

    @Query("SELECT * FROM Lancamento WHERE contaId = :contaId ORDER BY data DESC")
    List<Lancamento> buscarPorConta(int contaId); // <-- método adicionado

    @Query("SELECT * FROM Lancamento WHERE categoriaId = :categoriaId ORDER BY data DESC")
    List<Lancamento> listarPorCategoria(int categoriaId);

    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND (descricao LIKE :searchTerm OR CAST(valor AS TEXT) LIKE :searchTerm) ORDER BY data DESC")
    List<Lancamento> buscarPorDescricaoOuValor(int usuarioId, String searchTerm);

    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC LIMIT :limit")
    List<Lancamento> listarUltimasPorUsuario(int usuarioId, int limit);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE contaId = :contaId AND tipo = :tipo")
    Double somaPorContaETipo(int contaId, String tipo);

    @Query("SELECT SUM(valor) FROM Lancamento WHERE categoriaId = :categoriaId AND usuarioId = :usuarioId")
    Double somaPorCategoria(int categoriaId, int usuarioId);
}