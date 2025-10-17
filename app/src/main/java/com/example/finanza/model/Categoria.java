package com.example.finanza.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Categoria - Entidade de Categoria Financeira do Sistema Finanza
 * 
 * Representa uma categoria para classificação de transações financeiras.
 * As categorias ajudam o usuário a organizar e entender seus gastos e receitas.
 * 
 * Funcionalidades:
 * - Classificação de receitas e despesas
 * - Personalização visual com cores
 * - Geração de relatórios por categoria
 * - Sincronização bidirecional com servidor desktop
 * - Resolução de conflitos por timestamp
 * 
 * Tipos de Categoria:
 * - receita: Categorias para entrada de dinheiro (ex: Salário, Freelance, Vendas)
 * - despesa: Categorias para saída de dinheiro (ex: Alimentação, Transporte, Lazer)
 * 
 * Exemplos de Uso:
 * - Despesas: Alimentação, Transporte, Moradia, Lazer, Saúde, Educação
 * - Receitas: Salário, Freelance, Investimentos, Vendas, Outros
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
        indices = {
                @Index(name = "index_Categoria_uuid", value = {"uuid"}),
                @Index(name = "index_Categoria_syncStatus", value = {"syncStatus"})
        }
)
public class Categoria {
    // ================== CAMPOS PRINCIPAIS ==================
    
    /** ID único local (auto-incremento) */
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    /** Nome da categoria (ex: "Alimentação", "Transporte", "Salário") */
    public String nome;
    
    /** Cor em hexadecimal para representação visual na UI (ex: "#FF5733") */
    public String corHex;
    
    /** Tipo da categoria: "receita" ou "despesa" */
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
    
    /**
     * Construtor padrão
     * 
     * Inicializa automaticamente:
     * - UUID único universal
     * - Timestamp de modificação atual
     * - Estado como "necessita sincronização"
     */
    public Categoria() {
        // Gera UUID para novas categorias
        this.uuid = java.util.UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
        this.syncStatus = 2; // needs_sync por padrão
        this.lastSyncTime = 0;
        this.serverHash = "";
    }
    
    /**
     * Marca a categoria como modificada
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
     * Marca a categoria como sincronizada
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
     * O hash é calculado baseado nos campos principais da categoria:
     * - Nome da categoria
     * - Tipo (receita/despesa)
     * - Cor hexadecimal
     * 
     * @return String com o hash dos dados da categoria
     */
    public String generateDataHash() {
        String data = nome + "|" + tipo + "|" + (corHex != null ? corHex : "");
        return String.valueOf(data.hashCode());
    }
}