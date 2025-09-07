package com.example.finanza.network;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.util.DataIntegrityValidator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Legacy Sync Service - Maintained for backward compatibility
 * Delegates to EnhancedSyncService for improved functionality
 * 
 * @deprecated Use EnhancedSyncService for new implementations
 */
@Deprecated
public class SyncService {
    
    private static final String TAG = "SyncService";
    private static SyncService instance;
    
    private Context context;
    private AppDatabase database;
    private ServerClient serverClient;
    private ExecutorService executor;
    private EnhancedSyncService enhancedSyncService;
    
    // Callbacks for legacy compatibility
    public interface SyncCallback {
        void onSyncStarted();
        void onSyncCompleted(boolean success, String message);
        void onSyncProgress(String operation);
    }
    
    private SyncService(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.serverClient = ServerClient.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
        this.enhancedSyncService = EnhancedSyncService.getInstance(context);
    }
    
    public static synchronized SyncService getInstance(Context context) {
        if (instance == null) {
            instance = new SyncService(context);
        }
        return instance;
    }
    
    /**
     * Legacy sync method - delegates to EnhancedSyncService for improved functionality
     * @deprecated Use EnhancedSyncService.performFullSync() directly
     */
    @Deprecated
    public void sincronizarTudo(int usuarioId, SyncCallback callback) {
        Log.d(TAG, "Legacy sync called - delegating to EnhancedSyncService");
        
        // Convert legacy callback to enhanced callback
        EnhancedSyncService.EnhancedSyncCallback enhancedCallback = new EnhancedSyncService.EnhancedSyncCallback() {
            @Override
            public void onSyncStarted() {
                if (callback != null) {
                    callback.onSyncStarted();
                }
            }
            
            @Override
            public void onSyncProgress(String operation, int progress, int total) {
                if (callback != null) {
                    callback.onSyncProgress(operation);
                }
            }
            
            @Override
            public void onSyncCompleted(boolean success, String message, EnhancedSyncService.SyncResult result) {
                if (callback != null) {
                    String legacyMessage = success ? 
                        "Sync completed successfully" : 
                        "Sync failed: " + message;
                    callback.onSyncCompleted(success, legacyMessage);
                }
            }
            
            @Override
            public void onConflictDetected(String entityType, String details) {
                Log.w(TAG, "Conflict detected: " + entityType + " - " + details);
            }
        };
        
        // Delegate to enhanced sync service
        enhancedSyncService.performFullSync(usuarioId, enhancedCallback);
    }
    
    /**
     * Enhanced sync method - recommended for new implementations
     */
    public void sincronizarComMelhorias(int usuarioId, boolean incremental, SyncCallback callback) {
        Log.d(TAG, "Enhanced sync called with incremental=" + incremental);
        
        // Convert legacy callback
        EnhancedSyncService.EnhancedSyncCallback enhancedCallback = createEnhancedCallback(callback);
        
        if (incremental) {
            enhancedSyncService.performIncrementalSync(usuarioId, enhancedCallback);
        } else {
            enhancedSyncService.performFullSync(usuarioId, enhancedCallback);
        }
    }
    
    /**
     * Helper method to convert legacy callback to enhanced callback
     */
    private EnhancedSyncService.EnhancedSyncCallback createEnhancedCallback(SyncCallback callback) {
        return new EnhancedSyncService.EnhancedSyncCallback() {
            @Override
            public void onSyncStarted() {
                if (callback != null) {
                    callback.onSyncStarted();
                }
            }
            
            @Override
            public void onSyncProgress(String operation, int progress, int total) {
                if (callback != null) {
                    String progressMessage = String.format("%s (%d/%d)", operation, progress, total);
                    callback.onSyncProgress(progressMessage);
                }
            }
            
            @Override
            public void onSyncCompleted(boolean success, String message, EnhancedSyncService.SyncResult result) {
                if (callback != null) {
                    String detailedMessage = success ? 
                        String.format("Sync completed: %s", result.toString()) : 
                        String.format("Sync failed: %s", message);
                    callback.onSyncCompleted(success, detailedMessage);
                }
            }
            
            @Override
            public void onConflictDetected(String entityType, String details) {
                Log.w(TAG, "Conflict detected during sync: " + entityType + " - " + details);
                if (callback != null) {
                    callback.onSyncProgress("Resolving conflicts for " + entityType);
                }
            }
        };
    }
    
    // ========== LEGACY METHODS WITH ENHANCED IMPLEMENTATIONS ==========
    
    /**
     * Add category with enhanced validation and duplicate prevention
     */
    public void adicionarCategoria(Categoria categoria, SyncCallback callback) {
        Log.d(TAG, "Adding category with validation: " + categoria.nome);
        
        executor.execute(() -> {
            try {
                // Validate data before processing
                DataIntegrityValidator.ValidationResult validation = DataIntegrityValidator.validateCategoria(categoria);
                if (!validation.isValid) {
                    Log.e(TAG, "Category validation failed: " + validation);
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Validation failed: " + validation.errorMessage);
                    }
                    return;
                }
                
                // Use safe insert with duplicate prevention
                long id = database.categoriaDao().inserirSeguro(categoria);
                if (id > 0) {
                    Log.d(TAG, "Category saved with ID: " + id);
                    
                    // Try to sync with server if connected
                    if (serverClient.isConnected()) {
                        String comando = Protocol.buildCategoriaEnhanced(
                            categoria.uuid, categoria.nome, categoria.tipo, 
                            categoria.corHex, categoria.lastModified
                        );
                        
                        serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                database.categoriaDao().marcarComoSincronizado((int)id, System.currentTimeMillis());
                                Log.d(TAG, "Category synced with server: " + categoria.nome);
                            }
                            
                            @Override
                            public void onError(String error) {
                                Log.w(TAG, "Failed to sync category with server: " + error);
                            }
                        });
                    }
                    
                    if (callback != null) {
                        callback.onSyncCompleted(true, "Category added successfully");
                    }
                } else {
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Failed to save category");
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error adding category", e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Error: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Add account with enhanced validation and duplicate prevention
     */
    public void adicionarConta(Conta conta, SyncCallback callback) {
        Log.d(TAG, "Adding account with validation: " + conta.nome);
        
        executor.execute(() -> {
            try {
                // Validate data before processing
                DataIntegrityValidator.ValidationResult validation = DataIntegrityValidator.validateConta(conta);
                if (!validation.isValid) {
                    Log.e(TAG, "Account validation failed: " + validation);
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Validation failed: " + validation.errorMessage);
                    }
                    return;
                }
                
                // Use safe insert with duplicate prevention
                long id = database.contaDao().inserirSeguro(conta);
                if (id > 0) {
                    Log.d(TAG, "Account saved with ID: " + id);
                    
                    // Try to sync with server if connected
                    if (serverClient.isConnected()) {
                        String comando = Protocol.buildContaEnhanced(
                            conta.uuid, conta.nome, "corrente", 
                            conta.saldoInicial, conta.lastModified
                        );
                        
                        serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                database.contaDao().marcarComoSincronizado((int)id, System.currentTimeMillis());
                                Log.d(TAG, "Account synced with server: " + conta.nome);
                            }
                            
                            @Override
                            public void onError(String error) {
                                Log.w(TAG, "Failed to sync account with server: " + error);
                            }
                        });
                    }
                    
                    if (callback != null) {
                        callback.onSyncCompleted(true, "Account added successfully");
                    }
                } else {
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Failed to save account");
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error adding account", e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Error: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Add transaction with enhanced validation and duplicate prevention
     */
    public void adicionarLancamento(Lancamento lancamento, SyncCallback callback) {
        Log.d(TAG, "Adding transaction with validation: " + lancamento.descricao);
        
        executor.execute(() -> {
            try {
                // Validate data before processing
                DataIntegrityValidator.ValidationResult validation = DataIntegrityValidator.validateLancamento(lancamento);
                if (!validation.isValid) {
                    Log.e(TAG, "Transaction validation failed: " + validation);
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Validation failed: " + validation.errorMessage);
                    }
                    return;
                }
                
                // Check referential integrity
                DataIntegrityValidator.ValidationResult integrityCheck = 
                    DataIntegrityValidator.checkTransactionIntegrity(lancamento, database);
                if (!integrityCheck.isValid) {
                    Log.e(TAG, "Transaction integrity check failed: " + integrityCheck);
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Integrity check failed: " + integrityCheck.errorMessage);
                    }
                    return;
                }
                
                // Use safe insert with duplicate prevention
                long id = database.lancamentoDao().inserirSeguro(lancamento);
                if (id > 0) {
                    Log.d(TAG, "Transaction saved with ID: " + id);
                    
                    // Try to sync with server if connected
                    if (serverClient.isConnected()) {
                        String comando = Protocol.buildMovimentacaoEnhanced(
                            lancamento.uuid, lancamento.valor, lancamento.data,
                            lancamento.descricao, lancamento.tipo, 
                            lancamento.contaId, lancamento.categoriaId,
                            lancamento.lastModified, lancamento.isDeleted
                        );
                        
                        serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                database.lancamentoDao().marcarComoSincronizado((int)id, System.currentTimeMillis());
                                Log.d(TAG, "Transaction synced with server: " + lancamento.descricao);
                            }
                            
                            @Override
                            public void onError(String error) {
                                Log.w(TAG, "Failed to sync transaction with server: " + error);
                            }
                        });
                    }
                    
                    if (callback != null) {
                        callback.onSyncCompleted(true, "Transaction added successfully");
                    }
                } else {
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Failed to save transaction");
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error adding transaction", e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Error: " + e.getMessage());
                }
            }
        });
    }
    
    // ========== UTILITY METHODS ==========
    
    /**
     * Check if service is online
     */
    public boolean isOnline() {
        return serverClient.isConnected();
    }
    
    /**
     * Get enhanced sync service for advanced operations
     */
    public EnhancedSyncService getEnhancedSyncService() {
        return enhancedSyncService;
    }
    
    /**
     * Get conflict resolution manager
     */
    public ConflictResolutionManager getConflictResolutionManager() {
        return ConflictResolutionManager.getInstance(context);
    }
    
    /**
     * Shutdown the service and cleanup resources
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        if (enhancedSyncService != null) {
            enhancedSyncService.shutdown();
        }
    }
}
}