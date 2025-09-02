package com.example.finanza.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

/**
 * Cliente de comunicação com servidor Java
 * Gerencia conexões e threading para operações de rede
 */
public class ServerClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    
    private ExecutorService networkExecutor;
    private Handler mainHandler;
    
    public interface ServerCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    public ServerClient() {
        networkExecutor = Executors.newFixedThreadPool(3);
        mainHandler = new Handler(Looper.getMainLooper());
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
    
    public void enviarDados(String dados, ServerCallback<String> callback) {
        networkExecutor.execute(() -> {
            try {
                Socket socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
                
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