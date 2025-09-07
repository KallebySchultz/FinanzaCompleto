package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;
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

    @Query("DELETE FROM Conta WHERE usuarioId = :usuarioId")
    void excluirPorUsuario(int usuarioId);

    @Query("DELETE FROM Conta WHERE usuarioId = :usuarioId")
    void deletarTodosDoUsuario(int usuarioId);

    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId")
    List<Conta> listarPorUsuario(int usuarioId);

    @Query("SELECT * FROM Conta WHERE id = :id")
    Conta buscarPorId(int id);

    @Query("SELECT * FROM Conta")
    List<Conta> listarTodos();

    // ========== SYNC SUPPORT METHODS ==========

    @Query("SELECT * FROM Conta WHERE uuid = :uuid LIMIT 1")
    Conta buscarPorUuid(String uuid);

    @Query("SELECT * FROM Conta WHERE syncStatus = 2 OR syncStatus = 3")
    List<Conta> obterPendentesSync();

    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId AND (syncStatus = 2 OR syncStatus = 3)")
    List<Conta> obterPendentesSyncPorUsuario(int usuarioId);

    @Query("SELECT * FROM Conta WHERE lastModified > :timestamp")
    List<Conta> obterModificadosApos(long timestamp);

    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId AND lastModified > :timestamp")
    List<Conta> obterModificadosAposPorUsuario(int usuarioId, long timestamp);

    @Query("UPDATE Conta SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);

    @Query("UPDATE Conta SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);

    @Query("UPDATE Conta SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);

    @Query("UPDATE Conta SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);

    @Query("SELECT MAX(lastSyncTime) FROM Conta WHERE syncStatus = 1")
    Long obterUltimoTempoSync();

    @Query("SELECT * FROM Conta WHERE nome = :nome AND usuarioId = :usuarioId AND uuid != :excludeUuid LIMIT 1")
    Conta buscarDuplicataPorNomeEUsuario(String nome, int usuarioId, String excludeUuid);

    @Query("SELECT * FROM Conta WHERE nome = :nome AND usuarioId = :usuarioId LIMIT 1")
    Conta buscarPorNomeEUsuario(String nome, int usuarioId);

    @Transaction
    default long inserirOuAtualizar(Conta conta) {
        Conta existente = buscarPorUuid(conta.uuid);
        if (existente != null) {
            if (conta.lastModified > existente.lastModified) {
                conta.id = existente.id;
                atualizar(conta);
                return existente.id;
            } else {
                existente.syncStatus = 3; // conflito
                atualizar(existente);
                return existente.id;
            }
        } else {
            Conta duplicata = buscarPorNomeEUsuario(conta.nome, conta.usuarioId);
            if (duplicata != null) {
                conta.id = duplicata.id;
                conta.uuid = duplicata.uuid;
                atualizar(conta);
                return duplicata.id;
            } else {
                return inserir(conta);
            }
        }
    }

    @Transaction
    default long inserirSeguro(Conta conta) {
        Conta existenteUuid = buscarPorUuid(conta.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        Conta duplicata = buscarPorNomeEUsuario(conta.nome, conta.usuarioId);
        if (duplicata != null) {
            return duplicata.id;
        }
        conta.markAsModified();
        return inserir(conta);
    }

    @Transaction
    default long sincronizarDoServidor(int serverId, String nome, String tipo, double saldo, int usuarioId) {
        Conta existenteServerId = buscarPorId(serverId);
        if (existenteServerId != null) {
            return serverId;
        }
        Conta localConta = buscarPorNomeEUsuario(nome, usuarioId);
        if (localConta != null && localConta.id != serverId) {
            Conta serverConta = new Conta();
            serverConta.id = serverId;
            serverConta.nome = nome;
            serverConta.tipo = tipo;
            serverConta.saldoInicial = saldo;
            serverConta.saldoAtual = saldo;
            serverConta.usuarioId = usuarioId;
            serverConta.syncStatus = 1;
            serverConta.lastSyncTime = System.currentTimeMillis();
            deletar(localConta);
            return inserir(serverConta);
        }
        Conta conta = new Conta();
        conta.id = serverId;
        conta.nome = nome;
        conta.tipo = tipo;
        conta.saldoInicial = saldo;
        conta.saldoAtual = saldo;
        conta.usuarioId = usuarioId;
        conta.syncStatus = 1;
        conta.lastSyncTime = System.currentTimeMillis();
        return inserir(conta);
    }
}