package com.example.finanza.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Cliente para comunicação com o servidor desktop via sockets
 * Gerencia conexão, autenticação e sincronização de dados
 */
public class ServerClient {
    
    private static final String TAG = "ServerClient";
    private static final int CONNECTION_TIMEOUT = 5000; // 5 segundos
    private static final String PREFS_NAME = "FinanzaServerConfig";
    private static final String PREF_HOST = "server_host";
    private static final String PREF_PORT = "server_port";
    
    private Context context;
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean connected = false;
    
    // Singleton pattern
    private static ServerClient instance;
    
    public static synchronized ServerClient getInstance(Context context) {
        if (instance == null) {
            instance = new ServerClient(context.getApplicationContext());
        }
        return instance;
    }
    
    private ServerClient(Context context) {
        this.context = context;
        loadServerConfig();
    }
    
    /**
     * Interface para callbacks de operações assíncronas
     */
    public interface ServerCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    /**
     * Carrega configurações do servidor das SharedPreferences
     */
    private void loadServerConfig() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        serverHost = prefs.getString(PREF_HOST, "192.168.1.100");
        serverPort = prefs.getInt(PREF_PORT, 8080);
    }
    
    /**
     * Configura servidor e salva nas preferências
     */
    public void configurarServidor(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
             .putString(PREF_HOST, host)
             .putInt(PREF_PORT, port)
             .apply();
             
        Log.d(TAG, "Servidor configurado: " + host + ":" + port);
    }
    
    /**
     * Conecta ao servidor de forma assíncrona
     */
    public void conectar(String host, int port, ServerCallback<String> callback) {
        new AsyncTask<Void, Void, String>() {
            private String errorMessage = null;
            
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Fecha conexão anterior se existir
                    disconnect();
                    
                    socket = new Socket();
                    socket.connect(new java.net.InetSocketAddress(host, port), CONNECTION_TIMEOUT);
                    
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);
                    connected = true;
                    
                    return "Conectado ao servidor: " + host + ":" + port;
                    
                } catch (SocketTimeoutException e) {
                    errorMessage = "Timeout na conexão - Verifique se o servidor está rodando";
                    return null;
                } catch (IOException e) {
                    errorMessage = "Erro de conexão: " + e.getMessage();
                    return null;
                } catch (Exception e) {
                    errorMessage = "Erro inesperado: " + e.getMessage();
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(errorMessage);
                }
            }
        }.execute();
    }
    
    /**
     * Conecta usando as configurações salvas
     */
    public void conectar(ServerCallback<String> callback) {
        conectar(serverHost, serverPort, callback);
    }
    
    /**
     * Envia comando para o servidor de forma assíncrona
     */
    public void enviarComando(String comando, ServerCallback<String> callback) {
        new AsyncTask<String, Void, String>() {
            private String errorMessage = null;
            
            @Override
            protected String doInBackground(String... commands) {
                String command = commands[0];
                
                if (!connected) {
                    errorMessage = "Não conectado ao servidor";
                    return null;
                }
                
                try {
                    output.println(command);
                    String response = input.readLine();
                    
                    if (response == null) {
                        errorMessage = "Conexão perdida com o servidor";
                        connected = false;
                        return null;
                    }
                    
                    Log.d(TAG, "Comando enviado: " + command);
                    Log.d(TAG, "Resposta recebida: " + response);
                    
                    return response;
                    
                } catch (IOException e) {
                    errorMessage = "Erro na comunicação: " + e.getMessage();
                    connected = false;
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(errorMessage);
                }
            }
        }.execute(comando);
    }
    
    /**
     * Faz login no servidor
     */
    public void login(String email, String senha, ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LOGIN, email, senha);
        enviarComando(comando, new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String[] partes = Protocol.parseCommand(result);
                if (partes.length > 0 && Protocol.STATUS_OK.equals(partes[0])) {
                    Log.d(TAG, "Login bem-sucedido, mantendo conexão para sincronização");
                    callback.onSuccess(result); // Retorna a resposta completa para parsing posterior
                } else if (partes.length > 1) {
                    callback.onError(partes[1]);
                } else {
                    callback.onError("Resposta inválida do servidor");
                }
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Registra novo usuário no servidor
     */
    public void registrar(String nome, String email, String senha, ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_REGISTER, nome, email, senha);
        enviarComando(comando, new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String[] partes = Protocol.parseCommand(result);
                if (partes.length > 0 && Protocol.STATUS_OK.equals(partes[0])) {
                    callback.onSuccess("Usuário registrado com sucesso");
                } else if (partes.length > 1) {
                    callback.onError(partes[1]);
                } else {
                    callback.onError("Resposta inválida do servidor");
                }
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Desconecta do servidor
     */
    public void disconnect() {
        try {
            connected = false;
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            Log.d(TAG, "Desconectado do servidor");
        } catch (IOException e) {
            Log.e(TAG, "Erro ao desconectar: " + e.getMessage());
        }
    }
    
    /**
     * Verifica se está conectado
     */
    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    /**
     * Testa conexão simples
     */
    public void testarConexao(ServerCallback<String> callback) {
        conectar(new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                disconnect(); // Desconecta após teste
                callback.onSuccess("Servidor acessível");
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Lista contas do usuário
     */
    public void listarContas(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_CONTAS);
        enviarComando(comando, callback);
    }
    
    /**
     * Lista categorias do usuário
     */
    public void listarCategorias(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_CATEGORIAS);
        enviarComando(comando, callback);
    }
    
    /**
     * Lista categorias por tipo
     */
    public void listarCategoriasPorTipo(String tipo, ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_CATEGORIAS_TIPO, tipo);
        enviarComando(comando, callback);
    }
    
    /**
     * Lista movimentações do usuário
     */
    public void listarMovimentacoes(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_MOVIMENTACOES);
        enviarComando(comando, callback);
    }
    
    /**
     * Obtém perfil do usuário
     */
    public void obterPerfil(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_GET_PERFIL);
        enviarComando(comando, callback);
    }
}