package com.example.finanza.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
                @Index(name = "index_Categoria_uuid", value = {"uuid"}),
                @Index(name = "index_Categoria_syncStatus", value = {"syncStatus"})
        }
)
public class Categoria {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nome;
    public String corHex; // Cor para ícone na UI
    public String tipo; // "receita" ou "despesa"
    
    // Sync metadata for bidirectional synchronization
    @ColumnInfo(defaultValue = "''")
    public String uuid; // Universal unique identifier for cross-platform sync
    @ColumnInfo(defaultValue = "0")
    public long lastModified; // Timestamp of last modification for conflict resolution
    @ColumnInfo(defaultValue = "2")
    public int syncStatus; // 0=local_only, 1=synced, 2=needs_sync, 3=conflict
    @ColumnInfo(defaultValue = "0")
    public long lastSyncTime; // Timestamp of last successful sync
    @ColumnInfo(defaultValue = "''")
    public String serverHash; // Hash of server data to detect changes
    
    public Categoria() {
        // Generate UUID for new categories
        this.uuid = java.util.UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync by default
        this.lastSyncTime = 0;
        this.serverHash = "";
    }
    
    public void markAsModified() {
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync
    }
    
    public void markAsSynced() {
        this.syncStatus = 1; // synced
        this.lastSyncTime = System.currentTimeMillis();
    }
    
    /**
     * Generate hash for duplicate detection and conflict resolution
     */
    public String generateDataHash() {
        String data = nome + "|" + tipo + "|" + (corHex != null ? corHex : "");
        return String.valueOf(data.hashCode());
    }
}