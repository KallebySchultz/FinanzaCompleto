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
    
    /**
     * Find categoria by UUID for cross-platform sync
     */
    @Query("SELECT * FROM Categoria WHERE uuid = :uuid LIMIT 1")
    Categoria buscarPorUuid(String uuid);
    
    /**
     * Get categorias that need to be synced to server
     */
    @Query("SELECT * FROM Categoria WHERE syncStatus = 2 OR syncStatus = 3")
    List<Categoria> obterPendentesSync();
    
    /**
     * Get categorias modified after specific timestamp for incremental sync
     */
    @Query("SELECT * FROM Categoria WHERE lastModified > :timestamp")
    List<Categoria> obterModificadosApos(long timestamp);
    
    /**
     * Mark categoria as synced
     */
    @Query("UPDATE Categoria SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);
    
    /**
     * Mark categoria as needing sync
     */
    @Query("UPDATE Categoria SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);
    
    /**
     * Update sync metadata by UUID
     */
    @Query("UPDATE Categoria SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);
    
    /**
     * Batch update sync status
     */
    @Query("UPDATE Categoria SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);
    
    /**
     * Get last sync time for incremental sync
     */
    @Query("SELECT MAX(lastSyncTime) FROM Categoria WHERE syncStatus = 1")
    Long obterUltimoTempoSync();
    
    /**
     * Find duplicate categoria by name and type (enhanced duplicate detection)
     */
    @Query("SELECT * FROM Categoria WHERE nome = :nome AND tipo = :tipo AND uuid != :excludeUuid LIMIT 1")
    Categoria buscarDuplicataPorNomeETipo(String nome, String tipo, String excludeUuid);
    
    /**
     * Find categoria by name and type
     */
    @Query("SELECT * FROM Categoria WHERE nome = :nome AND tipo = :tipo LIMIT 1")
    Categoria buscarPorNomeETipo(String nome, String tipo);
    
    /**
     * Insert or update categoria based on UUID (for server data)
     */
    @Transaction
    default long inserirOuAtualizar(Categoria categoria) {
        Categoria existente = buscarPorUuid(categoria.uuid);
        if (existente != null) {
            // Check for conflicts based on lastModified timestamp
            if (categoria.lastModified > existente.lastModified) {
                categoria.id = existente.id;
                atualizar(categoria);
                return existente.id;
            } else {
                // Server data is older, mark as conflict
                existente.syncStatus = 3; // conflict
                atualizar(existente);
                return existente.id;
            }
        } else {
            // Check for duplicates by business logic (name + type)
            Categoria duplicata = buscarPorNomeETipo(categoria.nome, categoria.tipo);
            if (duplicata != null) {
                // Merge with existing record
                categoria.id = duplicata.id;
                categoria.uuid = duplicata.uuid; // Keep existing UUID
                atualizar(categoria);
                return duplicata.id;
            } else {
                return inserir(categoria);
            }
        }
    }
    
    /**
     * Safe insert with automatic duplicate prevention
     */
    @Transaction
    default long inserirSeguro(Categoria categoria) {
        // Check for existing UUID first
        Categoria existenteUuid = buscarPorUuid(categoria.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        
        // Check for duplicate by business logic
        Categoria duplicata = buscarPorNomeETipo(categoria.nome, categoria.tipo);
        if (duplicata != null) {
            return duplicata.id;
        }
        
        // Safe to insert
        categoria.markAsModified();
        return inserir(categoria);
    }
}