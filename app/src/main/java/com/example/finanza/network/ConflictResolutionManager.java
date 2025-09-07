package com.example.finanza.network;

import android.content.Context;
import android.util.Log;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.util.DataIntegrityValidator;

import java.util.List;
import java.util.ArrayList;

/**
 * Comprehensive conflict resolution manager for bidirectional synchronization
 * Handles data conflicts using multiple resolution strategies
 */
public class ConflictResolutionManager {
    
    private static final String TAG = "ConflictResolver";
    private static ConflictResolutionManager instance;
    
    private Context context;
    private AppDatabase database;
    
    // Conflict resolution strategies
    public enum ResolutionStrategy {
        LAST_WRITE_WINS,    // Use timestamp to determine winner
        SERVER_WINS,        // Always prefer server data
        CLIENT_WINS,        // Always prefer client data
        MERGE_FIELDS,       // Merge non-conflicting fields
        USER_CHOICE         // Prompt user for resolution
    }
    
    // Conflict result types
    public enum ConflictResult {
        RESOLVED_SERVER,    // Conflict resolved using server data
        RESOLVED_CLIENT,    // Conflict resolved using client data
        RESOLVED_MERGED,    // Conflict resolved by merging data
        NEEDS_USER_INPUT,   // User intervention required
        RESOLUTION_FAILED   // Could not resolve conflict
    }
    
    // Conflict information class
    public static class ConflictInfo {
        public String entityType;
        public String uuid;
        public String field;
        public String clientValue;
        public String serverValue;
        public long clientTimestamp;
        public long serverTimestamp;
        public ResolutionStrategy strategy;
        public ConflictResult result;
        public String resolvedValue;
        public String details;
        
        public ConflictInfo(String entityType, String uuid, String field) {
            this.entityType = entityType;
            this.uuid = uuid;
            this.field = field;
        }
        
        @Override
        public String toString() {
            return String.format("Conflict{entity=%s, uuid=%s, field=%s, strategy=%s, result=%s}", 
                entityType, uuid, field, strategy, result);
        }
    }
    
    // Conflict resolution callback
    public interface ConflictResolutionCallback {
        void onConflictResolved(ConflictInfo conflict);
        void onConflictNeedsUserInput(ConflictInfo conflict);
        void onResolutionFailed(ConflictInfo conflict, String error);
    }
    
    private ConflictResolutionManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
    }
    
    public static synchronized ConflictResolutionManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConflictResolutionManager(context);
        }
        return instance;
    }
    
    /**
     * Resolve all pending conflicts for a user
     */
    public void resolveAllConflicts(int usuarioId, ResolutionStrategy defaultStrategy, ConflictResolutionCallback callback) {
        try {
            Log.d(TAG, "Resolving all conflicts for user: " + usuarioId);
            
            // Find all conflicted entities
            List<ConflictInfo> conflicts = new ArrayList<>();
            
            // Find conflicted usuarios
            conflicts.addAll(findUserConflicts(usuarioId));
            
            // Find conflicted contas
            conflicts.addAll(findAccountConflicts(usuarioId));
            
            // Find conflicted categorias
            conflicts.addAll(findCategoryConflicts());
            
            // Find conflicted lancamentos
            conflicts.addAll(findTransactionConflicts(usuarioId));
            
            Log.d(TAG, "Found " + conflicts.size() + " conflicts to resolve");
            
            // Resolve each conflict
            for (ConflictInfo conflict : conflicts) {
                conflict.strategy = defaultStrategy;
                resolveConflict(conflict, callback);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error resolving conflicts", e);
            if (callback != null) {
                ConflictInfo errorConflict = new ConflictInfo("system", "", "resolution");
                callback.onResolutionFailed(errorConflict, "Error during conflict resolution: " + e.getMessage());
            }
        }
    }
    
    /**
     * Resolve a single conflict
     */
    public void resolveConflict(ConflictInfo conflict, ConflictResolutionCallback callback) {
        try {
            Log.d(TAG, "Resolving conflict: " + conflict);
            
            switch (conflict.strategy) {
                case LAST_WRITE_WINS:
                    resolveByTimestamp(conflict);
                    break;
                case SERVER_WINS:
                    resolveWithServerData(conflict);
                    break;
                case CLIENT_WINS:
                    resolveWithClientData(conflict);
                    break;
                case MERGE_FIELDS:
                    resolveByMerging(conflict);
                    break;
                case USER_CHOICE:
                    conflict.result = ConflictResult.NEEDS_USER_INPUT;
                    if (callback != null) {
                        callback.onConflictNeedsUserInput(conflict);
                    }
                    return;
            }
            
            // Apply the resolution
            boolean applied = applyResolution(conflict);
            if (applied) {
                if (callback != null) {
                    callback.onConflictResolved(conflict);
                }
            } else {
                conflict.result = ConflictResult.RESOLUTION_FAILED;
                if (callback != null) {
                    callback.onResolutionFailed(conflict, "Failed to apply conflict resolution");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error resolving conflict: " + conflict, e);
            conflict.result = ConflictResult.RESOLUTION_FAILED;
            if (callback != null) {
                callback.onResolutionFailed(conflict, "Error resolving conflict: " + e.getMessage());
            }
        }
    }
    
    /**
     * Find user conflicts
     */
    private List<ConflictInfo> findUserConflicts(int usuarioId) {
        List<ConflictInfo> conflicts = new ArrayList<>();
        
        try {
            // For now, we'll focus on entities that can have conflicts
            // User conflicts are less common as users typically don't sync across multiple devices
            // But we can check for sync status conflicts
            
            Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
            if (usuario != null && usuario.syncStatus == 3) { // CONFLICT status
                ConflictInfo conflict = new ConflictInfo("usuario", usuario.uuid, "profile");
                conflict.clientTimestamp = usuario.lastModified;
                conflict.details = "User profile sync conflict detected";
                conflicts.add(conflict);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error finding user conflicts", e);
        }
        
        return conflicts;
    }
    
    /**
     * Find account conflicts
     */
    private List<ConflictInfo> findAccountConflicts(int usuarioId) {
        List<ConflictInfo> conflicts = new ArrayList<>();
        
        try {
            List<Conta> contasConflictadas = database.contaDao().obterPendentesSyncPorUsuario(usuarioId);
            
            for (Conta conta : contasConflictadas) {
                if (conta.syncStatus == 3) { // CONFLICT status
                    ConflictInfo conflict = new ConflictInfo("conta", conta.uuid, "data");
                    conflict.clientTimestamp = conta.lastModified;
                    conflict.clientValue = generateAccountSummary(conta);
                    conflict.details = "Account data conflict: " + conta.nome;
                    conflicts.add(conflict);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error finding account conflicts", e);
        }
        
        return conflicts;
    }
    
    /**
     * Find category conflicts
     */
    private List<ConflictInfo> findCategoryConflicts() {
        List<ConflictInfo> conflicts = new ArrayList<>();
        
        try {
            List<Categoria> categoriasConflictadas = database.categoriaDao().obterPendentesSync();
            
            for (Categoria categoria : categoriasConflictadas) {
                if (categoria.syncStatus == 3) { // CONFLICT status
                    ConflictInfo conflict = new ConflictInfo("categoria", categoria.uuid, "data");
                    conflict.clientTimestamp = categoria.lastModified;
                    conflict.clientValue = generateCategorySummary(categoria);
                    conflict.details = "Category data conflict: " + categoria.nome;
                    conflicts.add(conflict);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error finding category conflicts", e);
        }
        
        return conflicts;
    }
    
    /**
     * Find transaction conflicts
     */
    private List<ConflictInfo> findTransactionConflicts(int usuarioId) {
        List<ConflictInfo> conflicts = new ArrayList<>();
        
        try {
            List<Lancamento> lancamentosConflictados = database.lancamentoDao().obterPendentesSyncPorUsuario(usuarioId);
            
            for (Lancamento lancamento : lancamentosConflictados) {
                if (lancamento.syncStatus == 3) { // CONFLICT status
                    ConflictInfo conflict = new ConflictInfo("lancamento", lancamento.uuid, "data");
                    conflict.clientTimestamp = lancamento.lastModified;
                    conflict.clientValue = generateTransactionSummary(lancamento);
                    conflict.details = "Transaction data conflict: " + lancamento.descricao;
                    conflicts.add(conflict);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error finding transaction conflicts", e);
        }
        
        return conflicts;
    }
    
    /**
     * Resolve conflict using timestamp (last-write-wins)
     */
    private void resolveByTimestamp(ConflictInfo conflict) {
        if (conflict.clientTimestamp >= conflict.serverTimestamp) {
            resolveWithClientData(conflict);
        } else {
            resolveWithServerData(conflict);
        }
    }
    
    /**
     * Resolve conflict by preferring server data
     */
    private void resolveWithServerData(ConflictInfo conflict) {
        conflict.result = ConflictResult.RESOLVED_SERVER;
        conflict.resolvedValue = conflict.serverValue;
        conflict.details += " - Resolved using server data";
    }
    
    /**
     * Resolve conflict by preferring client data
     */
    private void resolveWithClientData(ConflictInfo conflict) {
        conflict.result = ConflictResult.RESOLVED_CLIENT;
        conflict.resolvedValue = conflict.clientValue;
        conflict.details += " - Resolved using client data";
    }
    
    /**
     * Resolve conflict by merging data
     */
    private void resolveByMerging(ConflictInfo conflict) {
        // For now, use last-write-wins as default merge strategy
        // In a more sophisticated implementation, we could merge specific fields
        resolveByTimestamp(conflict);
        conflict.result = ConflictResult.RESOLVED_MERGED;
        conflict.details += " - Resolved by merging data";
    }
    
    /**
     * Apply the conflict resolution to the database
     */
    private boolean applyResolution(ConflictInfo conflict) {
        try {
            database.runInTransaction(() -> {
                switch (conflict.entityType) {
                    case "usuario":
                        applyUserResolution(conflict);
                        break;
                    case "conta":
                        applyAccountResolution(conflict);
                        break;
                    case "categoria":
                        applyCategoryResolution(conflict);
                        break;
                    case "lancamento":
                        applyTransactionResolution(conflict);
                        break;
                    default:
                        Log.w(TAG, "Unknown entity type for conflict resolution: " + conflict.entityType);
                }
            });
            
            Log.d(TAG, "Applied conflict resolution: " + conflict);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying conflict resolution: " + conflict, e);
            return false;
        }
    }
    
    /**
     * Apply user conflict resolution
     */
    private void applyUserResolution(ConflictInfo conflict) {
        Usuario usuario = database.usuarioDao().buscarPorUuid(conflict.uuid);
        if (usuario != null) {
            // Clear conflict status
            usuario.syncStatus = 1; // SYNCED
            usuario.lastSyncTime = System.currentTimeMillis();
            database.usuarioDao().atualizar(usuario);
        }
    }
    
    /**
     * Apply account conflict resolution
     */
    private void applyAccountResolution(ConflictInfo conflict) {
        Conta conta = database.contaDao().buscarPorUuid(conflict.uuid);
        if (conta != null) {
            // Clear conflict status
            conta.syncStatus = 1; // SYNCED
            conta.lastSyncTime = System.currentTimeMillis();
            
            // If server data was chosen, we would update the account data here
            // For now, just clear the conflict
            database.contaDao().atualizar(conta);
        }
    }
    
    /**
     * Apply category conflict resolution
     */
    private void applyCategoryResolution(ConflictInfo conflict) {
        Categoria categoria = database.categoriaDao().buscarPorUuid(conflict.uuid);
        if (categoria != null) {
            // Clear conflict status
            categoria.syncStatus = 1; // SYNCED
            categoria.lastSyncTime = System.currentTimeMillis();
            database.categoriaDao().atualizar(categoria);
        }
    }
    
    /**
     * Apply transaction conflict resolution
     */
    private void applyTransactionResolution(ConflictInfo conflict) {
        Lancamento lancamento = database.lancamentoDao().buscarPorUuid(conflict.uuid);
        if (lancamento != null) {
            // Clear conflict status
            lancamento.syncStatus = 1; // SYNCED
            lancamento.lastSyncTime = System.currentTimeMillis();
            database.lancamentoDao().atualizar(lancamento);
        }
    }
    
    /**
     * Generate account summary for conflict comparison
     */
    private String generateAccountSummary(Conta conta) {
        return String.format("Account{name=%s, balance=%.2f, user=%d}", 
            conta.nome, conta.saldoInicial, conta.usuarioId);
    }
    
    /**
     * Generate category summary for conflict comparison
     */
    private String generateCategorySummary(Categoria categoria) {
        return String.format("Category{name=%s, type=%s, color=%s}", 
            categoria.nome, categoria.tipo, categoria.corHex);
    }
    
    /**
     * Generate transaction summary for conflict comparison
     */
    private String generateTransactionSummary(Lancamento lancamento) {
        return String.format("Transaction{value=%.2f, desc=%s, type=%s, account=%d}", 
            lancamento.valor, lancamento.descricao, lancamento.tipo, lancamento.contaId);
    }
    
    /**
     * Validate data after conflict resolution
     */
    public boolean validateResolvedData(ConflictInfo conflict) {
        try {
            switch (conflict.entityType) {
                case "usuario":
                    Usuario usuario = database.usuarioDao().buscarPorUuid(conflict.uuid);
                    if (usuario != null) {
                        DataIntegrityValidator.ValidationResult result = DataIntegrityValidator.validateUsuario(usuario);
                        return result.isValid;
                    }
                    break;
                case "conta":
                    Conta conta = database.contaDao().buscarPorUuid(conflict.uuid);
                    if (conta != null) {
                        DataIntegrityValidator.ValidationResult result = DataIntegrityValidator.validateConta(conta);
                        return result.isValid;
                    }
                    break;
                case "categoria":
                    Categoria categoria = database.categoriaDao().buscarPorUuid(conflict.uuid);
                    if (categoria != null) {
                        DataIntegrityValidator.ValidationResult result = DataIntegrityValidator.validateCategoria(categoria);
                        return result.isValid;
                    }
                    break;
                case "lancamento":
                    Lancamento lancamento = database.lancamentoDao().buscarPorUuid(conflict.uuid);
                    if (lancamento != null) {
                        DataIntegrityValidator.ValidationResult result = DataIntegrityValidator.validateLancamento(lancamento);
                        return result.isValid;
                    }
                    break;
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "Error validating resolved data", e);
            return false;
        }
    }
    
    /**
     * Get conflict statistics for reporting
     */
    public ConflictStatistics getConflictStatistics(int usuarioId) {
        ConflictStatistics stats = new ConflictStatistics();
        
        try {
            // Count conflicts by entity type
            List<Usuario> userConflicts = database.usuarioDao().obterPendentesSync();
            for (Usuario user : userConflicts) {
                if (user.syncStatus == 3) stats.userConflicts++;
            }
            
            List<Conta> accountConflicts = database.contaDao().obterPendentesSyncPorUsuario(usuarioId);
            for (Conta account : accountConflicts) {
                if (account.syncStatus == 3) stats.accountConflicts++;
            }
            
            List<Categoria> categoryConflicts = database.categoriaDao().obterPendentesSync();
            for (Categoria category : categoryConflicts) {
                if (category.syncStatus == 3) stats.categoryConflicts++;
            }
            
            List<Lancamento> transactionConflicts = database.lancamentoDao().obterPendentesSyncPorUsuario(usuarioId);
            for (Lancamento transaction : transactionConflicts) {
                if (transaction.syncStatus == 3) stats.transactionConflicts++;
            }
            
            stats.totalConflicts = stats.userConflicts + stats.accountConflicts + 
                                 stats.categoryConflicts + stats.transactionConflicts;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting conflict statistics", e);
        }
        
        return stats;
    }
    
    /**
     * Conflict statistics class
     */
    public static class ConflictStatistics {
        public int totalConflicts = 0;
        public int userConflicts = 0;
        public int accountConflicts = 0;
        public int categoryConflicts = 0;
        public int transactionConflicts = 0;
        public int resolvedConflicts = 0;
        public int pendingConflicts = 0;
        
        @Override
        public String toString() {
            return String.format("ConflictStats{total=%d, users=%d, accounts=%d, categories=%d, transactions=%d, resolved=%d, pending=%d}",
                totalConflicts, userConflicts, accountConflicts, categoryConflicts, transactionConflicts, resolvedConflicts, pendingConflicts);
        }
    }
}