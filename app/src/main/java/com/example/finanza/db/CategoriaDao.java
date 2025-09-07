package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;
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

    // ========== SYNC SUPPORT METHODS ==========

    @Query("SELECT * FROM Categoria WHERE uuid = :uuid LIMIT 1")
    Categoria buscarPorUuid(String uuid);

    @Query("SELECT * FROM Categoria WHERE syncStatus = 2 OR syncStatus = 3")
    List<Categoria> obterPendentesSync();

    @Query("SELECT * FROM Categoria WHERE lastModified > :timestamp")
    List<Categoria> obterModificadosApos(long timestamp);

    @Query("UPDATE Categoria SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);

    @Query("UPDATE Categoria SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);

    @Query("UPDATE Categoria SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);

    @Query("UPDATE Categoria SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);

    @Query("SELECT MAX(lastSyncTime) FROM Categoria WHERE syncStatus = 1")
    Long obterUltimoTempoSync();

    @Query("SELECT * FROM Categoria WHERE nome = :nome AND tipo = :tipo AND uuid != :excludeUuid LIMIT 1")
    Categoria buscarDuplicataPorNomeETipo(String nome, String tipo, String excludeUuid);

    @Query("SELECT * FROM Categoria WHERE nome = :nome AND tipo = :tipo LIMIT 1")
    Categoria buscarPorNomeETipo(String nome, String tipo);

    @Transaction
    default long inserirOuAtualizar(Categoria categoria) {
        Categoria existente = buscarPorUuid(categoria.uuid);
        if (existente != null) {
            if (categoria.lastModified > existente.lastModified) {
                categoria.id = existente.id;
                atualizar(categoria);
                return existente.id;
            } else {
                existente.syncStatus = 3; // conflict
                atualizar(existente);
                return existente.id;
            }
        } else {
            Categoria duplicata = buscarPorNomeETipo(categoria.nome, categoria.tipo);
            if (duplicata != null) {
                categoria.id = duplicata.id;
                categoria.uuid = duplicata.uuid;
                atualizar(categoria);
                return duplicata.id;
            } else {
                return inserir(categoria);
            }
        }
    }

    @Transaction
    default long inserirSeguro(Categoria categoria) {
        Categoria existenteUuid = buscarPorUuid(categoria.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        Categoria duplicata = buscarPorNomeETipo(categoria.nome, categoria.tipo);
        if (duplicata != null) {
            return duplicata.id;
        }
        categoria.markAsModified();
        return inserir(categoria);
    }

    @Query("UPDATE Categoria SET id = :novoId WHERE id = :antigoId")
    void atualizarId(int antigoId, int novoId);

    @Transaction
    default long sincronizarDoServidor(int serverId, String nome, String tipo, String corHex) {
        Categoria existenteServerId = buscarPorId(serverId);
        if (existenteServerId != null) {
            return serverId;
        }
        Categoria localCategory = buscarPorNomeETipo(nome, tipo);
        if (localCategory != null && localCategory.id != serverId) {
            Categoria serverCategoria = new Categoria();
            serverCategoria.id = serverId;
            serverCategoria.nome = nome;
            serverCategoria.tipo = tipo;
            serverCategoria.corHex = corHex != null ? corHex : "#666666";
            serverCategoria.syncStatus = 1;
            serverCategoria.lastSyncTime = System.currentTimeMillis();
            deletar(localCategory);
            return inserir(serverCategoria);
        }
        Categoria categoria = new Categoria();
        categoria.id = serverId;
        categoria.nome = nome;
        categoria.tipo = tipo;
        categoria.corHex = corHex != null ? corHex : "#666666";
        categoria.syncStatus = 1;
        categoria.lastSyncTime = System.currentTimeMillis();
        return inserir(categoria);
    }
}