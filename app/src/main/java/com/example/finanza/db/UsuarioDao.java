package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Transaction;
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
    
    // ========== SYNC SUPPORT METHODS ==========
    
    /**
     * Find user by UUID for cross-platform sync
     */
    @Query("SELECT * FROM Usuario WHERE uuid = :uuid LIMIT 1")
    Usuario buscarPorUuid(String uuid);
    
    /**
     * Get users that need to be synced to server
     */
    @Query("SELECT * FROM Usuario WHERE syncStatus = 2 OR syncStatus = 3")
    List<Usuario> obterPendentesSync();
    
    /**
     * Get users modified after specific timestamp for incremental sync
     */
    @Query("SELECT * FROM Usuario WHERE lastModified > :timestamp")
    List<Usuario> obterModificadosApos(long timestamp);
    
    /**
     * Mark user as synced
     */
    @Query("UPDATE Usuario SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);
    
    /**
     * Mark user as needing sync
     */
    @Query("UPDATE Usuario SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);
    
    /**
     * Update sync metadata by UUID
     */
    @Query("UPDATE Usuario SET syncStatus = :status, lastSyncTime = :syncTime WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime);
    
    /**
     * Batch update sync status
     */
    @Query("UPDATE Usuario SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);
    
    /**
     * Get last sync time for incremental sync
     */
    @Query("SELECT MAX(lastSyncTime) FROM Usuario WHERE syncStatus = 1")
    Long obterUltimoTempoSync();
    
    /**
     * Insert or update user based on UUID (for server data)
     */
    @Transaction
    default long inserirOuAtualizar(Usuario usuario) {
        Usuario existente = buscarPorUuid(usuario.uuid);
        if (existente != null) {
            usuario.id = existente.id;
            atualizar(usuario);
            return existente.id;
        } else {
            return inserir(usuario);
        }
    }
}