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

    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId")
    List<Conta> listarPorUsuario(int usuarioId);

    @Query("SELECT * FROM Conta WHERE id = :id")
    Conta buscarPorId(int id);

    @Query("SELECT * FROM Conta")
    List<Conta> listarTodos(); // <-- Novo método para buscar todas as contas
    
    // ========== SYNC SUPPORT METHODS ==========
    
    /**
     * Find conta by UUID for cross-platform sync
     */
    @Query("SELECT * FROM Conta WHERE uuid = :uuid LIMIT 1")
    Conta buscarPorUuid(String uuid);
    
    /**
     * Get contas that need to be synced to server
     */
    @Query("SELECT * FROM Conta WHERE syncStatus = 2 OR syncStatus = 3")
    List<Conta> obterPendentesSync();
    
    /**
     * Get contas for specific user that need sync
     */
    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId AND (syncStatus = 2 OR syncStatus = 3)")
    List<Conta> obterPendentesSyncPorUsuario(int usuarioId);
    
    /**
     * Get contas modified after specific timestamp for incremental sync
     */
    @Query("SELECT * FROM Conta WHERE lastModified > :timestamp")
    List<Conta> obterModificadosApos(long timestamp);
    
    /**
     * Get contas modified after timestamp for specific user
     */
    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId AND lastModified > :timestamp")
    List<Conta> obterModificadosAposPorUsuario(int usuarioId, long timestamp);
    
    /**
     * Mark conta as synced
     */
    @Query("UPDATE Conta SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);
    
    /**
     * Mark conta as needing sync
     */
    @Query("UPDATE Conta SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);
    
    /**
     * Update sync metadata by UUID
     */
    @Query("UPDATE Conta SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);
    
    /**
     * Batch update sync status
     */
    @Query("UPDATE Conta SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);
    
    /**
     * Get last sync time for incremental sync
     */
    @Query("SELECT MAX(lastSyncTime) FROM Conta WHERE syncStatus = 1")
    Long obterUltimoTempoSync();
    
    /**
     * Find duplicate conta by name and user (enhanced duplicate detection)
     */
    @Query("SELECT * FROM Conta WHERE nome = :nome AND usuarioId = :usuarioId AND uuid != :excludeUuid LIMIT 1")
    Conta buscarDuplicataPorNomeEUsuario(String nome, int usuarioId, String excludeUuid);
    
    /**
     * Find conta by name and user
     */
    @Query("SELECT * FROM Conta WHERE nome = :nome AND usuarioId = :usuarioId LIMIT 1")
    Conta buscarPorNomeEUsuario(String nome, int usuarioId);
    
    /**
     * Insert or update conta based on UUID (for server data)
     */
    @Transaction
    default long inserirOuAtualizar(Conta conta) {
        Conta existente = buscarPorUuid(conta.uuid);
        if (existente != null) {
            // Check for conflicts based on lastModified timestamp
            if (conta.lastModified > existente.lastModified) {
                conta.id = existente.id;
                atualizar(conta);
                return existente.id;
            } else {
                // Server data is older, mark as conflict
                existente.syncStatus = 3; // conflict
                atualizar(existente);
                return existente.id;
            }
        } else {
            // Check for duplicates by business logic (name + user)
            Conta duplicata = buscarPorNomeEUsuario(conta.nome, conta.usuarioId);
            if (duplicata != null) {
                // Merge with existing record
                conta.id = duplicata.id;
                conta.uuid = duplicata.uuid; // Keep existing UUID
                atualizar(conta);
                return duplicata.id;
            } else {
                return inserir(conta);
            }
        }
    }
    
    /**
     * Safe insert with automatic duplicate prevention
     */
    @Transaction
    default long inserirSeguro(Conta conta) {
        // Check for existing UUID first
        Conta existenteUuid = buscarPorUuid(conta.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        
        // Check for duplicate by business logic
        Conta duplicata = buscarPorNomeEUsuario(conta.nome, conta.usuarioId);
        if (duplicata != null) {
            return duplicata.id;
        }
        
        // Safe to insert
        conta.markAsModified();
        return inserir(conta);
    }
}