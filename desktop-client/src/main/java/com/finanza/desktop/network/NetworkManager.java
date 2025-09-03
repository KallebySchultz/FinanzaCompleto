package com.finanza.desktop.network;

import com.finanza.desktop.config.SettingsManager;

import java.io.*;
import java.net.*;
import java.util.concurrent.CompletableFuture;

/**
 * Gerenciador de conexões de rede para o cliente desktop
 * Responsável por estabelecer e manter conexões com o servidor
 */
public class NetworkManager {
    private static NetworkManager instance;
    private SettingsManager settings;
    private boolean connected = false;
    private String lastError = "";
    
    public interface ConnectionCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    
    private NetworkManager() {
        settings = SettingsManager.getInstance();
    }
    
    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    /**
     * Testa a conexão com o servidor
     */
    public CompletableFuture<Boolean> testConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(
                    new InetSocketAddress(settings.getServerHost(), settings.getServerPort()),
                    settings.getConnectionTimeout()
                );
                
                // Envia um ping simples
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                out.println("PING");
                String response = in.readLine();
                
                socket.close();
                
                connected = true;
                lastError = "";
                return response != null;
                
            } catch (IOException e) {
                connected = false;
                lastError = "Erro de conexão: " + e.getMessage();
                return false;
            }
        });
    }
    
    /**
     * Envia dados para o servidor de forma assíncrona
     */
    public void sendData(String data, ConnectionCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(
                    new InetSocketAddress(settings.getServerHost(), settings.getServerPort()),
                    settings.getConnectionTimeout()
                );
                
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // Envia os dados
                out.println(data);
                
                // Lê a resposta
                String response = in.readLine();
                
                socket.close();
                
                connected = true;
                lastError = "";
                
                if (callback != null) {
                    callback.onSuccess(response != null ? response : "OK");
                }
                
            } catch (IOException e) {
                connected = false;
                lastError = "Erro ao enviar dados: " + e.getMessage();
                
                if (callback != null) {
                    callback.onError(lastError);
                }
            }
        });
    }
    
    /**
     * Sincroniza dados do usuário
     */
    public void syncUser(int userId, ConnectionCallback callback) {
        String jsonData = "{\"action\":\"sync_user\",\"userId\":" + userId + "}";
        sendData(jsonData, callback);
    }
    
    /**
     * Sincroniza contas do usuário
     */
    public void syncAccounts(int userId, ConnectionCallback callback) {
        String jsonData = "{\"action\":\"sync_accounts\",\"userId\":" + userId + "}";
        sendData(jsonData, callback);
    }
    
    /**
     * Sincroniza lançamentos do usuário
     */
    public void syncTransactions(int userId, ConnectionCallback callback) {
        String jsonData = "{\"action\":\"sync_transactions\",\"userId\":" + userId + "}";
        sendData(jsonData, callback);
    }
    
    /**
     * Verifica se está conectado
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Retorna o último erro ocorrido
     */
    public String getLastError() {
        return lastError;
    }
    
    /**
     * Retorna o endereço do servidor configurado
     */
    public String getServerAddress() {
        return settings.getServerAddress();
    }
}