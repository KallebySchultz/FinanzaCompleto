package com.finanza.desktop.network;

import com.finanza.desktop.model.Usuario;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

/**
 * Cliente para comunicação com o servidor Finanza
 */
public class FinanzaClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private String clientId;
    private Gson gson;

    public FinanzaClient() {
        this.clientId = UUID.randomUUID().toString();
        this.gson = new Gson();
    }

    public ServerResponse ping() {
        return ping(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ServerResponse ping(String host, int port) {
        SyncRequest request = new SyncRequest("ping", clientId, null);
        return sendRequest(request, host, port);
    }

    public ServerResponse syncUser(Usuario usuario) {
        return syncUser(usuario, DEFAULT_HOST, DEFAULT_PORT);
    }

    public ServerResponse syncUser(Usuario usuario, String host, int port) {
        SyncRequest request = new SyncRequest("sync_user", clientId, usuario);
        return sendRequest(request, host, port);
    }

    public ServerResponse getServerInfo() {
        return getServerInfo(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ServerResponse getServerInfo(String host, int port) {
        SyncRequest request = new SyncRequest("get_server_info", clientId, null);
        return sendRequest(request, host, port);
    }

    private ServerResponse sendRequest(SyncRequest request, String host, int port) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String jsonRequest = gson.toJson(request);
            out.println(jsonRequest);

            String jsonResponse = in.readLine();
            return gson.fromJson(jsonResponse, ServerResponse.class);

        } catch (IOException e) {
            return new ServerResponse("error", "Erro de conexão: " + e.getMessage());
        } catch (Exception e) {
            return new ServerResponse("error", "Erro interno: " + e.getMessage());
        }
    }

    public boolean testConnection(String host, int port) {
        try {
            ServerResponse response = ping(host, port);
            return "success".equals(response.getStatus());
        } catch (Exception e) {
            return false;
        }
    }

    // Classes auxiliares
    public static class SyncRequest {
        private String action;
        private String clientId;
        private long timestamp;
        private Object data;

        public SyncRequest(String action, String clientId, Object data) {
            this.action = action;
            this.clientId = clientId;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getAction() { return action; }
        public String getClientId() { return clientId; }
        public long getTimestamp() { return timestamp; }
        public Object getData() { return data; }
    }

    public static class ServerResponse {
        private String status;
        private String message;
        private Object data;

        public ServerResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        // Getters
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public Object getData() { return data; }

        public boolean isSuccess() {
            return "success".equals(status);
        }
    }
}