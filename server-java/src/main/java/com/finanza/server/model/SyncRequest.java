package com.finanza.server.model;

/**
 * Modelo para requisições de sincronização
 */
public class SyncRequest {
    private String action;
    private String clientId;
    private long timestamp;
    private Object data;

    public SyncRequest() {
        this.timestamp = System.currentTimeMillis();
    }

    public SyncRequest(String action, String clientId, Object data) {
        this();
        this.action = action;
        this.clientId = clientId;
        this.data = data;
    }

    // Getters e Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}