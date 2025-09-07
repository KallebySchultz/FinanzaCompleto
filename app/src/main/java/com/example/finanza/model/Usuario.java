package com.example.finanza.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Usuario - Entidade de Usuário do Sistema Finanza
 * 
 * Representa um usuário do aplicativo com suporte completo para:
 * - Autenticação local e remota
 * - Sincronização bidirecional com servidor desktop
 * - Resolução de conflitos por timestamp
 * - Controle de estado de sincronização
 * 
 * Campos de Sincronização:
 * - uuid: Identificador único universal para sincronização cross-platform
 * - lastModified: Timestamp da última modificação para resolução de conflitos
 * - syncStatus: Estado da sincronização (local, sincronizado, necessita sync, conflito)
 * - lastSyncTime: Timestamp da última sincronização bem-sucedida
 * 
 * Estados de Sincronização:
 * - 0 = LOCAL_ONLY: Dados apenas locais, não sincronizados
 * - 1 = SYNCED: Dados sincronizados com servidor
 * - 2 = NEEDS_SYNC: Dados modificados, necessitam sincronização
 * - 3 = CONFLICT: Conflito detectado durante sincronização
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
@Entity
public class Usuario {
    
    // ================== CAMPOS PRINCIPAIS ==================
    
    /** ID único local (auto-incremento) */
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    /** Nome completo do usuário */
    public String nome;
    
    /** Email do usuário (usado para login) */
    public String email;
    
    /** Senha criptografada do usuário */
    public String senha;
    
    /** Timestamp de criação da conta */
    public long dataCriacao;
    
    // ================== METADADOS DE SINCRONIZAÇÃO ==================
    
    /** UUID universal para sincronização cross-platform */
    @ColumnInfo(defaultValue = "''")
    public String uuid;
    
    /** Timestamp da última modificação para resolução de conflitos */
    @ColumnInfo(defaultValue = "0")
    public long lastModified;
    
    /** Estado da sincronização (0=local, 1=sincronizado, 2=precisa sync, 3=conflito) */
    @ColumnInfo(defaultValue = "2")
    public int syncStatus;
    
    /** Timestamp da última sincronização bem-sucedida */
    @ColumnInfo(defaultValue = "0")
    public long lastSyncTime;
    
    // ================== CONSTANTES DE SINCRONIZAÇÃO ==================
    
    public static final int SYNC_STATUS_LOCAL_ONLY = 0;
    public static final int SYNC_STATUS_SYNCED = 1;
    public static final int SYNC_STATUS_NEEDS_SYNC = 2;
    public static final int SYNC_STATUS_CONFLICT = 3;
    
    // ================== CONSTRUTOR E MÉTODOS ==================
    
    /**
     * Construtor padrão
     * 
     * Inicializa automaticamente:
     * - UUID único universal
     * - Timestamp de modificação atual
     * - Estado como "necessita sincronização"
     * - Tempo de sincronização zerado
     */
    public Usuario() {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = SYNC_STATUS_NEEDS_SYNC;
        this.lastSyncTime = 0;
    }
    
    /**
     * Marca o usuário como modificado
     * 
     * Atualiza o timestamp de modificação e define o estado
     * como "necessita sincronização" para garantir que as
     * alterações sejam enviadas ao servidor na próxima sync.
     */
    public void markAsModified() {
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = SYNC_STATUS_NEEDS_SYNC;
    }
    
    /**
     * Marca o usuário como sincronizado
     * 
     * Define o estado como "sincronizado" e atualiza o timestamp
     * da última sincronização bem-sucedida.
     */
    public void markAsSynced() {
        this.syncStatus = SYNC_STATUS_SYNCED;
        this.lastSyncTime = System.currentTimeMillis();
    }
    
    /**
     * Verifica se o usuário precisa de sincronização
     * 
     * @return true se o estado indica necessidade de sincronização
     */
    public boolean needsSync() {
        return syncStatus == SYNC_STATUS_NEEDS_SYNC;
    }
    
    /**
     * Verifica se há conflito de sincronização
     * 
     * @return true se o estado indica conflito
     */
    public boolean hasConflict() {
        return syncStatus == SYNC_STATUS_CONFLICT;
    }
    
    /**
     * Representação em string do usuário
     * 
     * @return String com informações básicas do usuário
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", uuid='" + uuid + '\'' +
                ", syncStatus=" + syncStatus +
                '}';
    }
}