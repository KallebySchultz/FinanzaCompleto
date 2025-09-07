package com.example.finanza.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nome;
    public String email;
    public String senha; // Adicionado para autenticação
    public long dataCriacao; // Data de criação da conta
    
    // Sync metadata for bidirectional synchronization
    public String uuid; // Universal unique identifier for cross-platform sync
    public long lastModified; // Timestamp of last modification for conflict resolution
    public int syncStatus; // 0=local_only, 1=synced, 2=needs_sync, 3=conflict
    public long lastSyncTime; // Timestamp of last successful sync
    
    public Usuario() {
        // Generate UUID for new users
        this.uuid = java.util.UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync by default
        this.lastSyncTime = 0;
    }
    
    public void markAsModified() {
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync
    }
    
    public void markAsSynced() {
        this.syncStatus = 1; // synced
        this.lastSyncTime = System.currentTimeMillis();
    }
}