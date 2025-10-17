package com.example.finanza.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

/**
 * Conta - Entidade de Conta Bancária do Sistema Finanza
 * 
 * Representa uma conta financeira do usuário (conta corrente, poupança,
 * cartão de crédito, investimentos ou dinheiro físico).
 * 
 * Funcionalidades:
 * - Armazenamento de saldo inicial da conta
 * - Cálculo automático de saldo atual baseado em lançamentos
 * - Suporte a múltiplos tipos de conta
 * - Sincronização bidirecional com servidor desktop
 * - Resolução de conflitos por timestamp
 * 
 * Tipos de Conta Suportados:
 * - corrente: Conta corrente bancária
 * - poupanca: Conta poupança
 * - cartao: Cartão de crédito
 * - investimento: Conta de investimentos
 * - dinheiro: Dinheiro físico em espécie
 * 
 * Relacionamentos:
 * - Pertence a um Usuário (ForeignKey com CASCADE)
 * - Pode ter múltiplos Lançamentos associados
 * 
 * Campos de Sincronização:
 * - uuid: Identificador único universal para sincronização cross-platform
 * - lastModified: Timestamp da última modificação para resolução de conflitos
 * - syncStatus: Estado da sincronização (0=local, 1=sync, 2=precisa sync, 3=conflito)
 * - lastSyncTime: Timestamp da última sincronização bem-sucedida
 * - serverHash: Hash dos dados do servidor para detectar alterações
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
@Entity(
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(name = "index_Conta_uuid", value = {"uuid"}),
                @Index(name = "index_Conta_syncStatus", value = {"syncStatus"})
        }
)
public class Conta {
    // ================== CAMPOS PRINCIPAIS ==================
    
    /** ID único local (auto-incremento) */
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    /** Nome da conta (ex: "Banco do Brasil - Corrente", "Nubank Cartão") */
    public String nome;
    
    /** Tipo da conta: corrente, poupanca, cartao, investimento, dinheiro */
    @ColumnInfo(defaultValue = "corrente")
    public String tipo;
    
    /** Saldo inicial da conta ao ser criada */
    public double saldoInicial;
    
    /** Saldo atual calculado (saldo inicial + receitas - despesas) */
    public double saldoAtual;
    
    /** ID do usuário dono desta conta */
    public int usuarioId;

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
    
    /** Hash dos dados do servidor para detectar mudanças */
    @ColumnInfo(defaultValue = "''")
    public String serverHash;

    /**
     * Construtor padrão
     * 
     * Inicializa automaticamente:
     * - UUID único universal
     * - Timestamp de modificação atual
     * - Estado como "necessita sincronização"
     * - Tipo padrão como "corrente"
     */
    public Conta() {
        // Gera UUID para novas contas
        this.uuid = java.util.UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync por padrão
        this.lastSyncTime = 0;
        this.serverHash = "";
        this.tipo = "corrente"; // tipo padrão de conta
        this.saldoAtual = 0; // inicialização padrão do campo adicionado
    }

    /**
     * Marca a conta como modificada
     * 
     * Atualiza o timestamp de modificação e define o estado
     * como "necessita sincronização" para garantir que as
     * alterações sejam enviadas ao servidor na próxima sync.
     */
    public void markAsModified() {
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync
    }

    /**
     * Marca a conta como sincronizada
     * 
     * Define o estado como "sincronizado" e atualiza o timestamp
     * da última sincronização bem-sucedida.
     */
    public void markAsSynced() {
        this.syncStatus = 1; // synced
        this.lastSyncTime = System.currentTimeMillis();
    }

    /**
     * Gera hash dos dados para detecção de duplicatas e resolução de conflitos
     * 
     * O hash é calculado baseado nos campos principais da conta:
     * - Nome da conta
     * - Tipo da conta
     * - Saldo inicial
     * - ID do usuário
     * 
     * @return String com o hash dos dados da conta
     */
    public String generateDataHash() {
        String data = nome + "|" + (tipo != null ? tipo : "corrente") + "|" + saldoInicial + "|" + usuarioId;
        return String.valueOf(data.hashCode());
    }
}