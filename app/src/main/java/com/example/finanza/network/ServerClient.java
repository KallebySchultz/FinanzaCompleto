package com.example.finanza.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

/**
 * Cliente de comunicação com servidor Java
 * Gerencia conexões e threading para operações de rede
 */
public class ServerClient {
    private static final String DEFAULT_HOST = "192.168.1.100"; // Mudança: IP padrão mais realista
    private static final int DEFAULT_PORT = 8080;
    private static final String PREFS_NAME = "FinanzaServerConfig";
    private static final String PREF_SERVER_HOST = "server_host";
    private static final String PREF_SERVER_PORT = "server_port";
    
    private ExecutorService networkExecutor;
    private Handler mainHandler;
    private Context context;
    
    public interface ServerCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    public ServerClient(Context context) {
        this.context = context;
        networkExecutor = Executors.newFixedThreadPool(3);
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Obtém o host do servidor configurado
     */
    private String getServerHost() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_SERVER_HOST, DEFAULT_HOST);
    }
    
    /**
     * Obtém a porta do servidor configurada
     */
    private int getServerPort() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_SERVER_PORT, DEFAULT_PORT);
    }
    
    /**
     * Configura o servidor
     */
    public void configurarServidor(String host, int port) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
             .putString(PREF_SERVER_HOST, host)
             .putInt(PREF_SERVER_PORT, port)
             .apply();
    }
    
    public void conectar(String host, int port, ServerCallback<String> callback) {
        networkExecutor.execute(() -> {
            try {
                Socket socket = new Socket(host, port);
                String result = "Conectado ao servidor: " + host + ":" + port;
                mainHandler.post(() -> callback.onSuccess(result));
                socket.close();
            } catch (IOException e) {
                mainHandler.post(() -> callback.onError("Erro de conexão: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Testa conexão com o servidor configurado
     */
    public void testarConexao(ServerCallback<String> callback) {
        String host = getServerHost();
        int port = getServerPort();
        conectar(host, port, callback);
    }
    
    public void enviarDados(String dados, ServerCallback<String> callback) {
        String host = getServerHost();
        int port = getServerPort();
        
        networkExecutor.execute(() -> {
            try {
                Socket socket = new Socket(host, port);
                
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                out.println(dados);
                String resposta = in.readLine();
                
                socket.close();
                mainHandler.post(() -> callback.onSuccess(resposta));
            } catch (IOException e) {
                mainHandler.post(() -> callback.onError("Erro ao enviar dados: " + e.getMessage()));
            }
        });
    }
    
    public void sincronizarUsuario(int usuarioId, ServerCallback<String> callback) {
        String dadosJson = "{\"action\":\"sync_user\",\"userId\":" + usuarioId + "}";
        enviarDados(dadosJson, callback);
    }
    
    public void sincronizarContas(int usuarioId, ServerCallback<String> callback) {
        String dadosJson = "{\"action\":\"sync_accounts\",\"userId\":" + usuarioId + "}";
        enviarDados(dadosJson, callback);
    }
    
    public void sincronizarLancamentos(int usuarioId, ServerCallback<String> callback) {
        String dadosJson = "{\"action\":\"sync_transactions\",\"userId\":" + usuarioId + "}";
        enviarDados(dadosJson, callback);
    }
    
    public void fechar() {
        if (networkExecutor != null && !networkExecutor.isShutdown()) {
            networkExecutor.shutdown();
        }
    }
}