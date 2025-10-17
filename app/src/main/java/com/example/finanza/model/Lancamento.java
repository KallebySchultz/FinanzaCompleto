package com.example.finanza.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

/**
 * Lancamento - Entidade de Transação Financeira do Sistema Finanza
 * 
 * Representa uma movimentação financeira (receita ou despesa) do usuário.
 * É a entidade central do sistema, registrando todas as entradas e saídas
 * de dinheiro das contas do usuário.
 * 
 * Funcionalidades:
 * - Registro de receitas e despesas
 * - Associação com conta e categoria específicas
 * - Soft delete para manter histórico
 * - Sincronização bidirecional com servidor desktop
 * - Resolução de conflitos por timestamp
 * 
 * Tipos de Lançamento:
 * - receita: Entrada de dinheiro (salário, venda, etc.)
 * - despesa: Saída de dinheiro (compra, pagamento, etc.)
 * 
 * Relacionamentos:
 * - Pertence a um Usuário (ForeignKey com CASCADE)
 * - Pertence a uma Conta (ForeignKey com CASCADE)
 * - Pertence a uma Categoria (ForeignKey com SET_NULL)
 * 
 * Campos de Sincronização:
 * - uuid: Identificador único universal para sincronização cross-platform
 * - lastModified: Timestamp da última modificação para resolução de conflitos
 * - syncStatus: Estado da sincronização (0=local, 1=sync, 2=precisa sync, 3=conflito)
 * - lastSyncTime: Timestamp da última sincronização bem-sucedida
 * - serverHash: Hash dos dados do servidor para detectar alterações
 * - isDeleted: Flag de soft delete para manter histórico
 * - serverId: ID do servidor para operações de sincronização
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
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
        },
        indices = {
                @Index(name = "index_Lancamento_uuid", value = {"uuid"}),
                @Index(name = "index_Lancamento_syncStatus", value = {"syncStatus"}),
                @Index(name = "index_Lancamento_lastModified", value = {"lastModified"})
        }
)
public class Lancamento {
    // ================== CAMPOS PRINCIPAIS ==================
    
    /** ID único local (auto-incremento) */
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    /** Valor da transação em reais (sempre positivo) */
    public double valor;
    
    /** Data da transação (timestamp em milissegundos) */
    public long data;
    
    /** Descrição/observação da transação */
    public String descricao;
    
    /** ID da conta onde a transação foi realizada */
    public int contaId;
    
    /** ID da categoria da transação */
    public int categoriaId;
    
    /** ID do usuário dono desta transação */
    public int usuarioId;
    
    /** Tipo da transação: "receita" ou "despesa" */
    public String tipo;

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
    
    /** Flag de soft delete - transação removida mas mantida no histórico */
    @ColumnInfo(defaultValue = "0")
    public boolean isDeleted;
    
    /** ID do servidor para operações de sincronização */
    @ColumnInfo(defaultValue = "0")
    public int serverId;

    /**
     * Construtor padrão
     * 
     * Inicializa automaticamente:
     * - UUID único universal
     * - Timestamp de modificação atual
     * - Estado como "necessita sincronização"
     * - Flag de deletado como false
     */
    public Lancamento() {
        // Gera UUID para novas transações
        this.uuid = java.util.UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync por padrão
        this.lastSyncTime = 0;
        this.serverHash = "";
        this.isDeleted = false;
    }

    /**
     * Marca o lançamento como modificado
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
     * Marca o lançamento como sincronizado
     * 
     * Define o estado como "sincronizado" e atualiza o timestamp
     * da última sincronização bem-sucedida.
     */
    public void markAsSynced() {
        this.syncStatus = 1; // synced
        this.lastSyncTime = System.currentTimeMillis();
    }

    /**
     * Marca o lançamento como deletado (soft delete)
     * 
     * Define a flag isDeleted como true e marca como modificado
     * para sincronizar a remoção com o servidor. A transação não
     * é removida fisicamente do banco, mantendo o histórico.
     */
    public void markAsDeleted() {
        this.isDeleted = true;
        this.markAsModified();
    }

    /**
     * Gera hash dos dados para detecção de duplicatas e resolução de conflitos
     * 
     * O hash é calculado baseado em todos os campos principais do lançamento:
     * - Valor, data, descrição
     * - IDs de conta, categoria e usuário
     * - Tipo (receita/despesa)
     * - Flag de deletado
     * 
     * @return String com o hash dos dados do lançamento
     */
    public String generateDataHash() {
        String dataStr = valor + "|" + this.data + "|" + (descricao != null ? descricao : "") +
                "|" + contaId + "|" + categoriaId + "|" + usuarioId + "|" +
                (tipo != null ? tipo : "") + "|" + isDeleted;
        return String.valueOf(dataStr.hashCode());
    }
}