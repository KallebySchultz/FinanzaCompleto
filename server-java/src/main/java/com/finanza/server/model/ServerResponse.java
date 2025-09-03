package com.finanza.server.model;

/**
 * Modelo para resposta do servidor
 */
public class ServerResponse {
    private String status;
    private String message;
    private Object data;

    public ServerResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ServerResponse(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ServerResponse success(String message) {
        return new ServerResponse("success", message);
    }

    public static ServerResponse success(String message, Object data) {
        return new ServerResponse("success", message, data);
    }

    public static ServerResponse error(String message) {
        return new ServerResponse("error", message);
    }

    // Getters e Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}