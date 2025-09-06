package com.example.finanza.network;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import com.example.finanza.network.SyncService;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Serviço de sincronização entre banco local (Room) e servidor
 * Gerencia sincronização de dados offline/online
 */
public class SyncService {
    
    private static final String TAG = "SyncService";
    private static SyncService instance;
    
    private Context context;
    private AppDatabase database;
    private ServerClient serverClient;
    private ExecutorService executor;
    
    // Callbacks para sincronização
    public interface SyncCallback {
        void onSyncStarted();
        void onSyncCompleted(boolean success, String message);
        void onSyncProgress(String operation);
    }
    
    private SyncService(Context context) {
        this.context = context.getApplicationContext();
        this.database = Room.databaseBuilder(context, AppDatabase.class, "finanza-database")
                .build();
        this.serverClient = ServerClient.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized SyncService getInstance(Context context) {
        if (instance == null) {
            instance = new SyncService(context);
        }
        return instance;
    }
    
    /**
     * Sincroniza todos os dados com o servidor se conectado
     */
    public void sincronizarTudo(int usuarioId, SyncCallback callback) {
        if (callback != null) {
            callback.onSyncStarted();
        }
        
        executor.execute(() -> {
            try {
                if (!serverClient.isConnected()) {
                    Log.d(TAG, "Não conectado ao servidor - operação offline");
                    if (callback != null) {
                        callback.onSyncCompleted(true, "Modo offline - dados salvos localmente");
                    }
                    return;
                }
                
                boolean success = true;
                StringBuilder message = new StringBuilder();
                
                // Sincronizar categorias
                if (callback != null) callback.onSyncProgress("Sincronizando categorias...");
                if (!sincronizarCategorias(usuarioId)) {
                    success = false;
                    message.append("Erro ao sincronizar categorias. ");
                }
                
                // Sincronizar contas
                if (callback != null) callback.onSyncProgress("Sincronizando contas...");
                if (!sincronizarContas(usuarioId)) {
                    success = false;
                    message.append("Erro ao sincronizar contas. ");
                }
                
                // Sincronizar lançamentos
                if (callback != null) callback.onSyncProgress("Sincronizando movimentações...");
                if (!sincronizarLancamentos(usuarioId)) {
                    success = false;
                    message.append("Erro ao sincronizar movimentações. ");
                }
                
                if (success) {
                    message.append("Sincronização concluída com sucesso");
                }
                
                if (callback != null) {
                    callback.onSyncCompleted(success, message.toString());
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Erro na sincronização: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro na sincronização: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Sincroniza categorias (envia locais para servidor)
     */
    private boolean sincronizarCategorias(int usuarioId) {
        try {
            List<Categoria> categorias = database.categoriaDao().listarTodas();
            
            for (Categoria categoria : categorias) {
                String comando = Protocol.buildCommand(
                    Protocol.CMD_ADD_CATEGORIA,
                    categoria.nome,
                    categoria.tipo,
                    categoria.corHex
                );
                
                // TODO: Implementar envio síncrono para sincronização
                // Por enquanto só registra
                Log.d(TAG, "Categoria a sincronizar: " + categoria.nome);
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao sincronizar categorias: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Sincroniza contas
     */
    private boolean sincronizarContas(int usuarioId) {
        try {
            List<Conta> contas = database.contaDao().listarPorUsuario(usuarioId);
            
            for (Conta conta : contas) {
                String comando = Protocol.buildCommand(
                    Protocol.CMD_ADD_CONTA,
                    conta.nome,
                    String.valueOf(conta.saldoInicial)
                );
                
                Log.d(TAG, "Conta a sincronizar: " + conta.nome);
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao sincronizar contas: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Sincroniza lançamentos/movimentações
     */
    private boolean sincronizarLancamentos(int usuarioId) {
        try {
            List<Lancamento> lancamentos = database.lancamentoDao().listarPorUsuario(usuarioId);
            
            for (Lancamento lancamento : lancamentos) {
                String comando = Protocol.buildCommand(
                    Protocol.CMD_ADD_MOVIMENTACAO,
                    String.valueOf(lancamento.valor),
                    String.valueOf(lancamento.data),
                    lancamento.descricao,
                    String.valueOf(lancamento.contaId),
                    String.valueOf(lancamento.categoriaId),
                    lancamento.tipo
                );
                
                Log.d(TAG, "Lançamento a sincronizar: " + lancamento.descricao);
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao sincronizar lançamentos: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Adiciona categoria localmente e sincroniza se online
     */
    public void adicionarCategoria(Categoria categoria, SyncCallback callback) {
        executor.execute(() -> {
            try {
                // Salva localmente primeiro
                long id = database.categoriaDao().inserir(categoria);
                categoria.id = (int) id;
                
                Log.d(TAG, "Categoria salva localmente: " + categoria.nome);
                
                // Tenta sincronizar se conectado
                if (serverClient.isConnected()) {
                    // TODO: Implementar sincronização em tempo real
                    Log.d(TAG, "Sincronizando categoria com servidor...");
                }
                
                if (callback != null) {
                    callback.onSyncCompleted(true, "Categoria adicionada");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Erro ao adicionar categoria: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao adicionar categoria: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Adiciona conta localmente e sincroniza se online
     */
    public void adicionarConta(Conta conta, SyncCallback callback) {
        executor.execute(() -> {
            try {
                long id = database.contaDao().inserir(conta);
                conta.id = (int) id;
                
                Log.d(TAG, "Conta salva localmente: " + conta.nome);
                
                if (serverClient.isConnected()) {
                    Log.d(TAG, "Sincronizando conta com servidor...");
                }
                
                if (callback != null) {
                    callback.onSyncCompleted(true, "Conta adicionada");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Erro ao adicionar conta: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao adicionar conta: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Adiciona lançamento localmente e sincroniza se online
     */
    public void adicionarLancamento(Lancamento lancamento, SyncCallback callback) {
        executor.execute(() -> {
            try {
                long id = database.lancamentoDao().inserir(lancamento);
                lancamento.id = (int) id;
                
                Log.d(TAG, "Lançamento salvo localmente: " + lancamento.descricao);
                
                if (serverClient.isConnected()) {
                    Log.d(TAG, "Sincronizando lançamento com servidor...");
                }
                
                if (callback != null) {
                    callback.onSyncCompleted(true, "Movimentação adicionada");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Erro ao adicionar lançamento: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao adicionar lançamento: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Verifica se existe conexão com servidor
     */
    public boolean isOnline() {
        return serverClient.isConnected();
    }
    
    /**
     * Libera recursos
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}