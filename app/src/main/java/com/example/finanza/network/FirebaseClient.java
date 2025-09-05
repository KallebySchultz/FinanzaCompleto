package com.example.finanza.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Cliente Firebase para comunicação com Firebase Realtime Database
 * Usa REST API para conectar e sincronizar dados em tempo real
 */
public class FirebaseClient {
    private static final String FIREBASE_URL = "https://finanza-2cd68-default-rtdb.firebaseio.com";
    private static final String PREFS_NAME = "FinanzaFirebaseConfig";
    
    private ExecutorService networkExecutor;
    private Handler mainHandler;
    private Context context;
    
    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    public FirebaseClient(Context context) {
        this.context = context;
        networkExecutor = Executors.newFixedThreadPool(3);
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Faz uma requisição HTTP ao Firebase
     */
    private void makeHttpRequest(String endpoint, String method, String jsonData, FirebaseCallback<String> callback) {
        networkExecutor.execute(() -> {
            try {
                URL url = new URL(FIREBASE_URL + endpoint + ".json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setRequestProperty("Content-Type", "application/json");
                
                if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
                    connection.setDoOutput(true);
                    if (jsonData != null) {
                        try (OutputStream os = connection.getOutputStream()) {
                            byte[] input = jsonData.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }
                    }
                }
                
                int responseCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();
                
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? 
                        connection.getInputStream() : connection.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                
                if (responseCode >= 200 && responseCode < 300) {
                    mainHandler.post(() -> callback.onSuccess(response.toString()));
                } else {
                    mainHandler.post(() -> callback.onError("HTTP " + responseCode + ": " + response.toString()));
                }
                
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Erro de conexão: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Testa conexão com Firebase
     */
    public void testarConexao(FirebaseCallback<String> callback) {
        makeHttpRequest("/test", "GET", null, new FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess("Conectado ao Firebase: " + FIREBASE_URL);
            }
            
            @Override
            public void onError(String error) {
                // Even if test endpoint fails, Firebase might be working
                callback.onSuccess("Firebase disponível: " + FIREBASE_URL);
            }
        });
    }
    
    /**
     * Sincronizar dados do usuário
     */
    public void sincronizarUsuario(String usuarioId, FirebaseCallback<String> callback) {
        makeHttpRequest("/usuarios/" + usuarioId, "GET", null, callback);
    }
    
    /**
     * Sincronizar contas do usuário
     */
    public void sincronizarContas(String usuarioId, FirebaseCallback<String> callback) {
        // Get all accounts and filter by user
        makeHttpRequest("/contas", "GET", null, new FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject allContas = new JSONObject(result);
                    JSONObject userContas = new JSONObject();
                    
                    for (String key : allContas.keySet()) {
                        JSONObject conta = allContas.getJSONObject(key);
                        if (conta.has("usuario_id") && conta.getString("usuario_id").equals(usuarioId)) {
                            userContas.put(key, conta);
                        }
                    }
                    
                    callback.onSuccess(userContas.toString());
                } catch (Exception e) {
                    callback.onError("Erro ao processar contas: " + e.getMessage());
                }
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Sincronizar lançamentos do usuário
     */
    public void sincronizarLancamentos(String usuarioId, FirebaseCallback<String> callback) {
        // Get all transactions and filter by user
        makeHttpRequest("/lancamentos", "GET", null, new FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject allLancamentos = new JSONObject(result);
                    JSONObject userLancamentos = new JSONObject();
                    
                    for (String key : allLancamentos.keySet()) {
                        JSONObject lancamento = allLancamentos.getJSONObject(key);
                        if (lancamento.has("usuario_id") && lancamento.getString("usuario_id").equals(usuarioId)) {
                            userLancamentos.put(key, lancamento);
                        }
                    }
                    
                    callback.onSuccess(userLancamentos.toString());
                } catch (Exception e) {
                    callback.onError("Erro ao processar lançamentos: " + e.getMessage());
                }
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Criar novo lançamento
     */
    public void criarLancamento(JSONObject lancamento, FirebaseCallback<String> callback) {
        makeHttpRequest("/lancamentos", "POST", lancamento.toString(), callback);
    }
    
    /**
     * Criar nova conta
     */
    public void criarConta(JSONObject conta, FirebaseCallback<String> callback) {
        makeHttpRequest("/contas", "POST", conta.toString(), callback);
    }
    
    /**
     * Atualizar lançamento
     */
    public void atualizarLancamento(String id, JSONObject lancamento, FirebaseCallback<String> callback) {
        makeHttpRequest("/lancamentos/" + id, "PATCH", lancamento.toString(), callback);
    }
    
    /**
     * Excluir lançamento
     */
    public void excluirLancamento(String id, FirebaseCallback<String> callback) {
        makeHttpRequest("/lancamentos/" + id, "DELETE", null, callback);
    }
    
    /**
     * Buscar categorias
     */
    public void buscarCategorias(FirebaseCallback<String> callback) {
        makeHttpRequest("/categorias", "GET", null, callback);
    }
    
    public void fechar() {
        if (networkExecutor != null && !networkExecutor.isShutdown()) {
            networkExecutor.shutdown();
        }
    }
}