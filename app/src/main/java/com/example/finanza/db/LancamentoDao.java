package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;
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
    
    // ========== SYNC SUPPORT METHODS ==========
    
    /**
     * Find lancamento by UUID for cross-platform sync
     */
    @Query("SELECT * FROM Lancamento WHERE uuid = :uuid LIMIT 1")
    Lancamento buscarPorUuid(String uuid);
    
    /**
     * Get lancamentos that need to be synced to server
     */
    @Query("SELECT * FROM Lancamento WHERE syncStatus = 2 OR syncStatus = 3")
    List<Lancamento> obterPendentesSync();
    
    /**
     * Get lancamentos for specific user that need sync
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND (syncStatus = 2 OR syncStatus = 3)")
    List<Lancamento> obterPendentesSyncPorUsuario(int usuarioId);
    
    /**
     * Get lancamentos modified after specific timestamp for incremental sync
     */
    @Query("SELECT * FROM Lancamento WHERE lastModified > :timestamp AND isDeleted = 0")
    List<Lancamento> obterModificadosApos(long timestamp);
    
    /**
     * Get active (non-deleted) lancamentos modified after timestamp for specific user
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND lastModified > :timestamp AND isDeleted = 0")
    List<Lancamento> obterModificadosAposPorUsuario(int usuarioId, long timestamp);
    
    /**
     * Get deleted lancamentos for sync (soft deletes)
     */
    @Query("SELECT * FROM Lancamento WHERE isDeleted = 1 AND lastModified > :timestamp")
    List<Lancamento> obterDeletadosApos(long timestamp);
    
    /**
     * Mark lancamento as synced
     */
    @Query("UPDATE Lancamento SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);
    
    /**
     * Mark lancamento as needing sync
     */
    @Query("UPDATE Lancamento SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);
    
    /**
     * Soft delete lancamento
     */
    @Query("UPDATE Lancamento SET isDeleted = 1, syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarComoExcluido(int id, long timestamp);
    
    /**
     * Update sync metadata by UUID
     */
    @Query("UPDATE Lancamento SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);
    
    /**
     * Batch update sync status
     */
    @Query("UPDATE Lancamento SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);
    
    /**
     * Get last sync time for incremental sync
     */
    @Query("SELECT MAX(lastSyncTime) FROM Lancamento WHERE syncStatus = 1")
    Long obterUltimoTempoSync();
    
    /**
     * Find potential duplicate by business logic
     */
    @Query("SELECT * FROM Lancamento WHERE valor = :valor AND data = :data AND descricao = :descricao AND contaId = :contaId AND usuarioId = :usuarioId AND uuid != :excludeUuid AND isDeleted = 0 LIMIT 1")
    Lancamento buscarDuplicata(double valor, long data, String descricao, int contaId, int usuarioId, String excludeUuid);
    
    /**
     * Find similar transactions for duplicate detection
     */
    @Query("SELECT * FROM Lancamento WHERE valor = :valor AND ABS(data - :data) < :timeWindow AND contaId = :contaId AND usuarioId = :usuarioId AND isDeleted = 0")
    List<Lancamento> buscarSimilares(double valor, long data, long timeWindow, int contaId, int usuarioId);
    
    /**
     * Get active (non-deleted) transactions for user
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND isDeleted = 0 ORDER BY data DESC")
    List<Lancamento> listarAtivosPorUsuario(int usuarioId);
    
    /**
     * Insert or update lancamento based on UUID (for server data)
     */
    @Transaction
    default long inserirOuAtualizar(Lancamento lancamento) {
        Lancamento existente = buscarPorUuid(lancamento.uuid);
        if (existente != null) {
            // Check for conflicts based on lastModified timestamp
            if (lancamento.lastModified > existente.lastModified) {
                lancamento.id = existente.id;
                atualizar(lancamento);
                return existente.id;
            } else {
                // Server data is older, mark as conflict
                existente.syncStatus = 3; // conflict
                atualizar(existente);
                return existente.id;
            }
        } else {
            // Check for duplicates by business logic
            Lancamento duplicata = buscarDuplicata(
                lancamento.valor, 
                lancamento.data, 
                lancamento.descricao != null ? lancamento.descricao : "",
                lancamento.contaId, 
                lancamento.usuarioId, 
                lancamento.uuid
            );
            if (duplicata != null) {
                // Merge with existing record - keep most recent data
                if (lancamento.lastModified > duplicata.lastModified) {
                    lancamento.id = duplicata.id;
                    lancamento.uuid = duplicata.uuid; // Keep existing UUID
                    atualizar(lancamento);
                }
                return duplicata.id;
            } else {
                return inserir(lancamento);
            }
        }
    }
    
    /**
     * Safe insert with comprehensive duplicate prevention
     */
    @Transaction
    default long inserirSeguro(Lancamento lancamento) {
        // Check for existing UUID first
        Lancamento existenteUuid = buscarPorUuid(lancamento.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        
        // Check for exact duplicate by business logic
        Lancamento duplicataExata = buscarDuplicata(
            lancamento.valor, 
            lancamento.data, 
            lancamento.descricao != null ? lancamento.descricao : "",
            lancamento.contaId, 
            lancamento.usuarioId, 
            lancamento.uuid
        );
        if (duplicataExata != null) {
            return duplicataExata.id;
        }
        
        // Check for similar transactions within 1 hour window
        long timeWindow = 60 * 60 * 1000; // 1 hour in milliseconds
        List<Lancamento> similares = buscarSimilares(
            lancamento.valor, 
            lancamento.data, 
            timeWindow, 
            lancamento.contaId, 
            lancamento.usuarioId
        );
        
        for (Lancamento similar : similares) {
            // If very similar (same description or very close in time), consider duplicate
            if (similar.descricao != null && lancamento.descricao != null && 
                similar.descricao.trim().equalsIgnoreCase(lancamento.descricao.trim())) {
                return similar.id;
            }
            // If within 5 minutes, likely duplicate
            if (Math.abs(similar.data - lancamento.data) < 5 * 60 * 1000) {
                return similar.id;
            }
        }
        
        // Safe to insert
        lancamento.markAsModified();
        return inserir(lancamento);
    }
    
    /**
     * Safely delete with conflict resolution
     */
    @Transaction
    default void excluirSeguro(int id) {
        Lancamento lancamento = buscarPorId(id);
        if (lancamento != null) {
            // Soft delete for sync purposes
            marcarComoExcluido(id, System.currentTimeMillis());
        }
    }
}