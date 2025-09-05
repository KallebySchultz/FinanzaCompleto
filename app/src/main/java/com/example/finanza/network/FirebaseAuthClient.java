package com.example.finanza.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cliente Firebase Authentication usando REST API
 * Implementa autenticação Firebase sem precisar do SDK
 */
public class FirebaseAuthClient {
    // Firebase Auth REST API
    private static final String FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:";
    // IMPORTANTE: Esta chave deve ser configurada com a chave correta do seu projeto Firebase
    // Para produção, considere armazenar em SharedPreferences ou arquivo de configuração
    private static final String API_KEY = "AIzaSyDnmRVgLMKg9-wXZKXEjIUjAhOxRfXlJEI"; // Substitua pela sua chave da API Firebase
    
    private ExecutorService networkExecutor;
    private Handler mainHandler;
    private Context context;
    
    public interface AuthCallback {
        void onSuccess(String token, String userId, String email);
        void onError(String error);
    }
    
    public FirebaseAuthClient(Context context) {
        this.context = context;
        networkExecutor = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Fazer login com email e senha via Firebase Auth REST API
     */
    public void signInWithEmailAndPassword(String email, String password, AuthCallback callback) {
        networkExecutor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("email", email);
                requestBody.put("password", password);
                requestBody.put("returnSecureToken", true);
                
                String response = makeAuthRequest("signInWithPassword", requestBody.toString());
                JSONObject responseJson = new JSONObject(response);
                
                if (responseJson.has("idToken")) {
                    String token = responseJson.getString("idToken");
                    String userId = responseJson.getString("localId");
                    String userEmail = responseJson.getString("email");
                    
                    // Salvar token localmente
                    saveAuthToken(token, userId, userEmail);
                    
                    mainHandler.post(() -> callback.onSuccess(token, userId, userEmail));
                } else {
                    String error = responseJson.optString("error", "Erro desconhecido");
                    mainHandler.post(() -> callback.onError("Login falhou: " + error));
                }
                
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Erro de conexão: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Criar conta com email e senha via Firebase Auth REST API
     */
    public void createUserWithEmailAndPassword(String email, String password, AuthCallback callback) {
        networkExecutor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("email", email);
                requestBody.put("password", password);
                requestBody.put("returnSecureToken", true);
                
                String response = makeAuthRequest("signUp", requestBody.toString());
                JSONObject responseJson = new JSONObject(response);
                
                if (responseJson.has("idToken")) {
                    String token = responseJson.getString("idToken");
                    String userId = responseJson.getString("localId");
                    String userEmail = responseJson.getString("email");
                    
                    // Salvar token localmente
                    saveAuthToken(token, userId, userEmail);
                    
                    mainHandler.post(() -> callback.onSuccess(token, userId, userEmail));
                } else {
                    String error = responseJson.optString("error", "Erro desconhecido");
                    mainHandler.post(() -> callback.onError("Cadastro falhou: " + error));
                }
                
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Erro de conexão: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Verificar se o usuário está logado
     */
    public boolean isUserLoggedIn() {
        SharedPreferences prefs = context.getSharedPreferences("FinanzaFirebaseAuth", Context.MODE_PRIVATE);
        String token = prefs.getString("firebase_token", null);
        return token != null && !token.isEmpty();
    }
    
    /**
     * Obter token salvo
     */
    public String getSavedToken() {
        SharedPreferences prefs = context.getSharedPreferences("FinanzaFirebaseAuth", Context.MODE_PRIVATE);
        return prefs.getString("firebase_token", null);
    }
    
    /**
     * Obter ID do usuário salvo
     */
    public String getSavedUserId() {
        SharedPreferences prefs = context.getSharedPreferences("FinanzaFirebaseAuth", Context.MODE_PRIVATE);
        return prefs.getString("firebase_user_id", null);
    }
    
    /**
     * Obter email do usuário salvo
     */
    public String getSavedUserEmail() {
        SharedPreferences prefs = context.getSharedPreferences("FinanzaFirebaseAuth", Context.MODE_PRIVATE);
        return prefs.getString("firebase_user_email", null);
    }
    
    /**
     * Fazer logout
     */
    public void signOut() {
        SharedPreferences prefs = context.getSharedPreferences("FinanzaFirebaseAuth", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    private String makeAuthRequest(String endpoint, String jsonData) throws Exception {
        URL url = new URL(FIREBASE_AUTH_URL + endpoint + "?key=" + API_KEY);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes("utf-8");
            os.write(input, 0, input.length);
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
            return response.toString();
        } else {
            throw new Exception("HTTP " + responseCode + ": " + response.toString());
        }
    }
    
    private void saveAuthToken(String token, String userId, String email) {
        SharedPreferences prefs = context.getSharedPreferences("FinanzaFirebaseAuth", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("firebase_token", token)
                .putString("firebase_user_id", userId)
                .putString("firebase_user_email", email)
                .apply();
    }
    
    public void fechar() {
        if (networkExecutor != null && !networkExecutor.isShutdown()) {
            networkExecutor.shutdown();
        }
    }
}