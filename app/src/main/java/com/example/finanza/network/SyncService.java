package com.example.finanza.network;

import android.content.Context;
import android.widget.Toast;
import com.example.finanza.model.*;
import com.example.finanza.db.AppDatabase;
import androidx.room.Room;

/**
 * Serviço de sincronização com servidor
 * Integra operações locais com comunicação remota
 */
public class SyncService {
    private Context context;
    private ServerClient serverClient;
    private AppDatabase localDb;
    
    public SyncService(Context context) {
        this.context = context;
        this.serverClient = new ServerClient();
        this.localDb = Room.databaseBuilder(context,
                AppDatabase.class, "finanza-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
    
    public void sincronizarTudo(int usuarioId) {
        // Sincronizar em sequência: usuário -> contas -> lançamentos
        sincronizarUsuario(usuarioId, () -> {
            sincronizarContas(usuarioId, () -> {
                sincronizarLancamentos(usuarioId, () -> {
                    Toast.makeText(context, "Sincronização completa!", Toast.LENGTH_SHORT).show();
                });
            });
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
    
    public void testarConexao(String host, int port) {
        serverClient.conectar(host, port, new ServerClient.ServerCallback<String>() {
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
    
    public void fechar() {
        if (serverClient != null) {
            serverClient.fechar();
        }
    }
}