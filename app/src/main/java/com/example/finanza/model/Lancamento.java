package com.example.finanza.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Usuario.class,
                        parentColumns = "id",
                        childColumns = "usuarioId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Conta.class,
                        parentColumns = "id",
                        childColumns = "contaId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Categoria.class,
                        parentColumns = "id",
                        childColumns = "categoriaId",
                        onDelete = ForeignKey.SET_NULL)
        }
)
public class Lancamento {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public double valor;
    public long data; // timestamp
    public String descricao;
    public int contaId;
    public int categoriaId;
    public int usuarioId;
    public String tipo; // "receita" ou "despesa"

    // Sync metadata for bidirectional synchronization
    public String uuid; // Universal unique identifier for cross-platform sync
    public long lastModified; // Timestamp of last modification for conflict resolution
    public int syncStatus; // 0=local_only, 1=synced, 2=needs_sync, 3=conflict
    public long lastSyncTime; // Timestamp of last successful sync
    public String serverHash; // Hash of server data to detect changes
    public boolean isDeleted; // Soft delete flag for sync

    public Lancamento() {
        // Generate UUID for new transactions
        this.uuid = java.util.UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync by default
        this.lastSyncTime = 0;
        this.serverHash = "";
        this.isDeleted = false;
    }

    public void markAsModified() {
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync
    }

    public void markAsSynced() {
        this.syncStatus = 1; // synced
        this.lastSyncTime = System.currentTimeMillis();
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.markAsModified();
    }

    /**
     * Generate hash for duplicate detection and conflict resolution
     */
    public String generateDataHash() {
        String dataStr = valor + "|" + this.data + "|" + (descricao != null ? descricao : "") +
                "|" + contaId + "|" + categoriaId + "|" + usuarioId + "|" +
                (tipo != null ? tipo : "") + "|" + isDeleted;
        return String.valueOf(dataStr.hashCode());
    }
}