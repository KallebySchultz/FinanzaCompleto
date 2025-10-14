package util;

import java.io.*;
import java.net.Socket;

/**
 * Classe para gerenciar comunicação com o servidor
 */
public class NetworkClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean connected = false;
    
    /**
     * Conecta ao servidor
     */
    public boolean connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            System.out.println("Conectado ao servidor: " + SERVER_HOST + ":" + SERVER_PORT);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    
    /**
     * Envia comando para o servidor e retorna resposta
     */
    public String sendCommand(String command) {
        if (!connected) {
            return "ERROR|Não conectado ao servidor";
        }
        
        try {
            output.println(command);
            String response = input.readLine();
            System.out.println("Comando enviado: " + command);
            System.out.println("Resposta recebida (length=" + (response != null ? response.length() : 0) + "): " + response);
            return response;
        } catch (IOException e) {
            System.err.println("Erro na comunicação: " + e.getMessage());
            disconnect();
            return "ERROR|Erro de comunicação";
        }
    }
    
    /**
     * Desconecta do servidor
     */
    public void disconnect() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            connected = false;
            System.out.println("Desconectado do servidor");
        } catch (IOException e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }
    
    /**
     * Verifica se está conectado
     */
    public boolean isConnected() {
        return connected;
    }
}