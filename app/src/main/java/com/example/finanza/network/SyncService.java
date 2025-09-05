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
import java.util.List;

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
        firebaseClient.sincronizarUsuario(String.valueOf(usuarioId), new FirebaseClient.FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Processar resposta do Firebase e atualizar dados locais
                    processarUsuarioFromFirebase(result, usuarioId);
                } catch (Exception e) {
                    // Se ocorrer erro no processamento, continua sem falhar
                    Toast.makeText(context, "Erro ao processar dados do usuário", Toast.LENGTH_SHORT).show();
                }
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
        firebaseClient.sincronizarContas(String.valueOf(usuarioId), new FirebaseClient.FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Processar contas do Firebase e sincronizar com SQLite
                    processarContasFromFirebase(result, usuarioId);
                } catch (Exception e) {
                    Toast.makeText(context, "Erro ao processar contas", Toast.LENGTH_SHORT).show();
                }
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
        firebaseClient.sincronizarLancamentos(String.valueOf(usuarioId), new FirebaseClient.FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Processar lançamentos do Firebase e sincronizar com SQLite
                    processarLancamentosFromFirebase(result, usuarioId);
                } catch (Exception e) {
                    Toast.makeText(context, "Erro ao processar lançamentos", Toast.LENGTH_SHORT).show();
                }
                onComplete.run();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Erro ao sincronizar lançamentos: " + error, Toast.LENGTH_SHORT).show();
                onComplete.run();
            }
        });
    }

    // Métodos para processar dados do Firebase
    private void processarUsuarioFromFirebase(String jsonData, int usuarioId) {
        try {
            if (jsonData != null && !jsonData.equals("null")) {
                org.json.JSONObject userData = new org.json.JSONObject(jsonData);
                // Atualizar dados do usuário local se necessário
                // Por enquanto, apenas logamos que recebemos dados
                android.util.Log.d("SyncService", "Dados do usuário recebidos do Firebase");
            }
        } catch (Exception e) {
            android.util.Log.e("SyncService", "Erro ao processar usuário: " + e.getMessage());
        }
    }

    private void processarContasFromFirebase(String jsonData, int usuarioId) {
        try {
            if (jsonData != null && !jsonData.equals("null") && !jsonData.equals("{}")) {
                org.json.JSONObject contasObj = new org.json.JSONObject(jsonData);
                
                // Primeiro, enviar dados locais para Firebase se necessário
                enviarContasLocaisParaFirebase(usuarioId);
                
                // Depois, processar contas do Firebase
                for (java.util.Iterator<String> it = contasObj.keys(); it.hasNext(); ) {
                    String firebaseId = it.next();
                    org.json.JSONObject contaJson = contasObj.getJSONObject(firebaseId);
                    
                    // Verificar se a conta já existe localmente
                    // Para simplificar, por enquanto apenas logamos
                    android.util.Log.d("SyncService", "Conta do Firebase: " + contaJson.toString());
                }
            } else {
                // Não há contas no Firebase, enviar as locais
                enviarContasLocaisParaFirebase(usuarioId);
            }
        } catch (Exception e) {
            android.util.Log.e("SyncService", "Erro ao processar contas: " + e.getMessage());
        }
    }

    private void processarLancamentosFromFirebase(String jsonData, int usuarioId) {
        try {
            if (jsonData != null && !jsonData.equals("null") && !jsonData.equals("{}")) {
                org.json.JSONObject lancamentosObj = new org.json.JSONObject(jsonData);
                
                // Primeiro, enviar dados locais para Firebase se necessário
                enviarLancamentosLocaisParaFirebase(usuarioId);
                
                // Depois, processar lançamentos do Firebase
                for (java.util.Iterator<String> it = lancamentosObj.keys(); it.hasNext(); ) {
                    String firebaseId = it.next();
                    org.json.JSONObject lancamentoJson = lancamentosObj.getJSONObject(firebaseId);
                    
                    // Verificar se o lançamento já existe localmente
                    // Para simplificar, por enquanto apenas logamos
                    android.util.Log.d("SyncService", "Lançamento do Firebase: " + lancamentoJson.toString());
                }
            } else {
                // Não há lançamentos no Firebase, enviar os locais
                enviarLancamentosLocaisParaFirebase(usuarioId);
            }
        } catch (Exception e) {
            android.util.Log.e("SyncService", "Erro ao processar lançamentos: " + e.getMessage());
        }
    }

    private void enviarContasLocaisParaFirebase(int usuarioId) {
        try {
            List<com.example.finanza.model.Conta> contasLocais = localDb.contaDao().listarPorUsuario(usuarioId);
            for (com.example.finanza.model.Conta conta : contasLocais) {
                org.json.JSONObject contaJson = new org.json.JSONObject();
                contaJson.put("nome", conta.nome);
                contaJson.put("saldo_inicial", conta.saldoInicial);
                contaJson.put("usuario_id", String.valueOf(conta.usuarioId));
                
                firebaseClient.criarConta(contaJson, new FirebaseClient.FirebaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        android.util.Log.d("SyncService", "Conta enviada para Firebase: " + conta.nome);
                    }
                    
                    @Override
                    public void onError(String error) {
                        android.util.Log.e("SyncService", "Erro ao enviar conta: " + error);
                    }
                });
            }
        } catch (Exception e) {
            android.util.Log.e("SyncService", "Erro ao enviar contas locais: " + e.getMessage());
        }
    }

    private void enviarLancamentosLocaisParaFirebase(int usuarioId) {
        try {
            List<com.example.finanza.model.Lancamento> lancamentosLocais = localDb.lancamentoDao().listarPorUsuario(usuarioId);
            for (com.example.finanza.model.Lancamento lancamento : lancamentosLocais) {
                org.json.JSONObject lancamentoJson = new org.json.JSONObject();
                lancamentoJson.put("valor", lancamento.valor);
                lancamentoJson.put("data", lancamento.data);
                lancamentoJson.put("descricao", lancamento.descricao);
                lancamentoJson.put("conta_id", lancamento.contaId);
                lancamentoJson.put("categoria_id", lancamento.categoriaId);
                lancamentoJson.put("usuario_id", String.valueOf(lancamento.usuarioId));
                lancamentoJson.put("tipo", lancamento.tipo);
                
                firebaseClient.criarLancamento(lancamentoJson, new FirebaseClient.FirebaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        android.util.Log.d("SyncService", "Lançamento enviado para Firebase: " + lancamento.descricao);
                    }
                    
                    @Override
                    public void onError(String error) {
                        android.util.Log.e("SyncService", "Erro ao enviar lançamento: " + error);
                    }
                });
            }
        } catch (Exception e) {
            android.util.Log.e("SyncService", "Erro ao enviar lançamentos locais: " + e.getMessage());
        }
    }

    public void fechar() {
        if (firebaseClient != null) {
            firebaseClient.fechar();
        }
    }
}