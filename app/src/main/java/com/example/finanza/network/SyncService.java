package com.example.finanza.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.example.finanza.model.*;
import com.example.finanza.db.AppDatabase;
import androidx.room.Room;

/**
 * Serviço de sincronização com servidor
 * Integra operações locais com comunicação remota
 * Inclui sincronização automática quando online
 */
public class SyncService {
    private Context context;
    private FirebaseClient firebaseClient;
    private AppDatabase localDb;
    private Handler autoSyncHandler;
    private Runnable autoSyncRunnable;
    private static final long AUTO_SYNC_INTERVAL = 30000; // 30 segundos
    private boolean autoSyncEnabled = true;

    public SyncService(Context context) {
        this.context = context;
        this.firebaseClient = new FirebaseClient(context);
        this.localDb = Room.databaseBuilder(context,
                        AppDatabase.class, "finanza-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        // Inicializar auto-sync
        iniciarAutoSync();
    }

    private void iniciarAutoSync() {
        autoSyncHandler = new Handler(Looper.getMainLooper());
        autoSyncRunnable = new Runnable() {
            @Override
            public void run() {
                if (autoSyncEnabled && isOnline()) {
                    // Obter usuário ID dos SharedPreferences
                    SharedPreferences prefs = context.getSharedPreferences("FinanzaAuth", Context.MODE_PRIVATE);
                    int usuarioId = prefs.getInt("usuarioId", -1);
                    if (usuarioId != -1) {
                        sincronizarTudoSilencioso(usuarioId);
                    }
                }
                // Reagendar próxima sincronização
                autoSyncHandler.postDelayed(this, AUTO_SYNC_INTERVAL);
            }
        };
        // Iniciar primeira sincronização após 5 segundos
        autoSyncHandler.postDelayed(autoSyncRunnable, 5000);
    }

    public void pararAutoSync() {
        autoSyncEnabled = false;
        if (autoSyncHandler != null && autoSyncRunnable != null) {
            autoSyncHandler.removeCallbacks(autoSyncRunnable);
        }
    }

    public void iniciarAutoSyncNovamente() {
        autoSyncEnabled = true;
        if (autoSyncHandler != null && autoSyncRunnable != null) {
            autoSyncHandler.postDelayed(autoSyncRunnable, AUTO_SYNC_INTERVAL);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void sincronizarTudo(int usuarioId) {
        // Sincronização manual com feedback ao usuário
        sincronizarTudoComFeedback(usuarioId, true);
    }

    private void sincronizarTudoSilencioso(int usuarioId) {
        // Sincronização automática sem feedback ao usuário
        sincronizarTudoComFeedback(usuarioId, false);
    }

    private void sincronizarTudoComFeedback(int usuarioId, boolean mostrarFeedback) {
        // Sincronizar em sequência: usuário -> contas -> lançamentos
        sincronizarUsuario(usuarioId, () -> {
            sincronizarContas(usuarioId, () -> {
                sincronizarLancamentos(usuarioId, () -> {
                    if (mostrarFeedback) {
                        Toast.makeText(context, "Sincronização completa!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    // REMOVIDO O MÉTODO DUPLICADO! Mantido apenas este:
    public void testarConexao() {
        firebaseClient.testarConexao(new FirebaseClient.FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sincronizarUsuario(int usuarioId, Runnable onComplete) {
        serverClient.sincronizarUsuario(usuarioId, new ServerClient.ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Processar resposta do servidor e atualizar dados locais
                // Por enquanto, apenas continua para próxima sincronização
                onComplete.run();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Erro ao sincronizar usuário: " + error, Toast.LENGTH_SHORT).show();
                onComplete.run(); // Continua mesmo com erro
            }
        });
    }

    private void sincronizarContas(int usuarioId, Runnable onComplete) {
        serverClient.sincronizarContas(usuarioId, new ServerClient.ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onComplete.run();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Erro ao sincronizar contas: " + error, Toast.LENGTH_SHORT).show();
                onComplete.run();
            }
        });
    }

    private void sincronizarLancamentos(int usuarioId, Runnable onComplete) {
        serverClient.sincronizarLancamentos(usuarioId, new ServerClient.ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onComplete.run();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Erro ao sincronizar lançamentos: " + error, Toast.LENGTH_SHORT).show();
                onComplete.run();
            }
        });
    }

    public void fechar() {
        if (serverClient != null) {
            serverClient.fechar();
        }
    }
}