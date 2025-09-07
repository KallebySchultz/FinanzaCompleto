package com.example.finanza.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Usuario;

/**
 * Gerenciador de autenticação que suporta modo offline e online
 * Prioriza autenticação local, sincroniza com servidor quando disponível
 */
public class AuthManager {
    
    private static final String TAG = "AuthManager";
    private static final String PREFS_NAME = "FinanzaAuth";
    private static final String PREF_USER_ID = "usuarioId";
    private static final String PREF_USER_EMAIL = "userEmail";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";
    
    private Context context;
    private AppDatabase database;
    private ServerClient serverClient;
    private static AuthManager instance;
    
    public interface AuthCallback {
        void onSuccess(Usuario usuario);
        void onError(String error);
    }
    
    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.serverClient = ServerClient.getInstance(context);
    }
    
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }
    
    /**
     * Faz login - tenta servidor primeiro, fallback para local
     */
    public void login(String email, String senha, AuthCallback callback) {
        // Executa operações de banco em thread separada
        new Thread(() -> {
            // Verifica primeiro localmente
            Usuario usuarioLocal = buscarUsuarioLocal(email, senha);
            
            // Primeiro tenta conectar ao servidor
            serverClient.conectar(new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String connectionResult) {
                    Log.d(TAG, "Conectado ao servidor, tentando login...");
                    
                    // Agora faz login
                    serverClient.login(email, senha, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Login no servidor bem-sucedido: " + result);
                            
                            // Parse da resposta do servidor para obter dados do usuário
                            String[] partes = Protocol.parseCommand(result);
                            Usuario usuario = usuarioLocal;
                            
                            if (partes.length > 1) {
                                // Resposta: OK|userId;nome;email
                                String[] userData = partes[1].split(";");
                                if (userData.length >= 3) {
                                    String nome = userData[1];
                                    String emailServidor = userData[2];
                                    
                                    // Atualiza ou cria usuário local com dados do servidor
                                    if (usuario == null) {
                                        usuario = criarUsuarioLocal(nome, emailServidor, senha);
                                    } else {
                                        usuario.nome = nome;
                                        usuario.email = emailServidor;
                                        new Thread(() -> database.usuarioDao().atualizar(usuario)).start();
                                    }
                                }
                            }
                            
                            if (usuario == null) {
                                usuario = usuarioLocal != null ? usuarioLocal : criarUsuarioLocal("", email, senha);
                            }
                            
                            if (usuario != null) {
                                salvarSessao(usuario);
                                
                                // Iniciar sincronização de dados após login bem-sucedido
                                SyncService syncService = SyncService.getInstance(context);
                                syncService.sincronizarTudo(usuario.id, new SyncService.SyncCallback() {
                                    @Override
                                    public void onSyncStarted() {
                                        Log.d(TAG, "Iniciando sincronização pós-login...");
                                    }

                                    @Override
                                    public void onSyncCompleted(boolean success, String message) {
                                        Log.d(TAG, "Sincronização pós-login concluída: " + message);
                                    }

                                    @Override
                                    public void onSyncProgress(String operation) {
                                        Log.d(TAG, "Sincronização: " + operation);
                                    }
                                });
                                
                                callback.onSuccess(usuario);
                            } else {
                                callback.onError("Erro ao salvar dados locais");
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.d(TAG, "Login no servidor falhou: " + error);
                            
                            // Fallback para autenticação local
                            if (usuarioLocal != null) {
                                Log.d(TAG, "Usando autenticação local offline");
                                salvarSessao(usuarioLocal);
                                callback.onSuccess(usuarioLocal);
                            } else {
                                callback.onError("Credenciais inválidas (offline)");
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.d(TAG, "Falha na conexão com servidor: " + error);
                    
                    // Fallback para autenticação local
                    if (usuarioLocal != null) {
                        Log.d(TAG, "Usando autenticação local offline");
                        salvarSessao(usuarioLocal);
                        callback.onSuccess(usuarioLocal);
                    } else {
                        callback.onError("Credenciais inválidas (modo offline)");
                    }
                }
            });
        }).start();
    }
    
    /**
     * Registra novo usuário
     */
    public void registrar(String nome, String email, String senha, AuthCallback callback) {
        // Executa operações de banco em thread separada
        new Thread(() -> {
            // Verifica se usuário já existe localmente
            Usuario usuarioExistente = null;
            try {
                usuarioExistente = database.usuarioDao().buscarPorEmail(email);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao verificar usuário existente: " + e.getMessage());
            }
            
            if (usuarioExistente != null) {
                callback.onError("Usuário já existe localmente");
                return;
            }
            
            if (serverClient.isConnected()) {
                // Registra no servidor primeiro
                serverClient.registrar(nome, email, senha, new ServerClient.ServerCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "Registro no servidor bem-sucedido");
                        
                        // Cria usuário local
                        Usuario usuario = criarUsuarioLocal(nome, email, senha);
                        if (usuario != null) {
                            salvarSessao(usuario);
                            callback.onSuccess(usuario);
                        } else {
                            callback.onError("Erro ao criar usuário local");
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.d(TAG, "Registro no servidor falhou: " + error);
                        
                        // Ainda assim cria localmente para uso offline
                        Usuario usuario = criarUsuarioLocal(nome, email, senha);
                        if (usuario != null) {
                            Log.d(TAG, "Usuário criado localmente (será sincronizado depois)");
                            salvarSessao(usuario);
                            callback.onSuccess(usuario);
                        } else {
                            callback.onError("Erro ao criar usuário: " + error);
                        }
                    }
                });
            } else {
                // Modo offline - cria só localmente
                Usuario usuario = criarUsuarioLocal(nome, email, senha);
                if (usuario != null) {
                    Log.d(TAG, "Usuário criado offline");
                    salvarSessao(usuario);
                    callback.onSuccess(usuario);
                } else {
                    callback.onError("Erro ao criar usuário offline");
                }
            }
        }).start();
    }
    
    /**
     * Busca usuário local por email e valida senha
     */
    private Usuario buscarUsuarioLocal(String email, String senha) {
        try {
            // Busca usuário com email e senha válidos
            return database.usuarioDao().login(email, senha);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar usuário local: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cria usuário local
     */
    private Usuario criarUsuarioLocal(String nome, String email, String senha) {
        try {
            Usuario usuario = new Usuario();
            usuario.nome = nome;
            usuario.email = email;
            usuario.senha = senha; // Em produção, deveria ser hash
            usuario.dataCriacao = System.currentTimeMillis();
            
            // Executa operação de banco em thread separada e aguarda resultado
            Thread dbThread = new Thread(() -> {
                try {
                    long id = database.usuarioDao().inserir(usuario);
                    usuario.id = (int) id;
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao inserir usuário no banco: " + e.getMessage());
                }
            });
            dbThread.start();
            dbThread.join(); // Aguarda a operação completar
            
            return usuario;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar usuário local: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Sobrecarga para usuários existentes
     */
    private Usuario criarUsuarioLocal(String email, String senha) {
        return criarUsuarioLocal("", email, senha);
    }
    
    /**
     * Salva sessão do usuário
     */
    private void salvarSessao(Usuario usuario) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
             .putInt(PREF_USER_ID, usuario.id)
             .putString(PREF_USER_EMAIL, usuario.email)
             .putBoolean(PREF_IS_LOGGED_IN, true)
             .apply();
             
        Log.d(TAG, "Sessão salva para usuário: " + usuario.email);
    }
    
    /**
     * Verifica se usuário está logado
     */
    public boolean isLoggedIn() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_IS_LOGGED_IN, false);
    }
    
    /**
     * Obtém ID do usuário logado
     */
    public int getLoggedUserId() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_USER_ID, -1);
    }
    
    /**
     * Obtém email do usuário logado
     */
    public String getLoggedUserEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_USER_EMAIL, "");
    }
    
    /**
     * Faz logout
     */
    public void logout() {
        // Desconecta do servidor se conectado
        if (serverClient.isConnected()) {
            serverClient.disconnect();
        }
        
        // Limpa sessão local
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Log.d(TAG, "Logout realizado");
    }
    
    /**
     * Obtém dados do usuário logado
     */
    public Usuario getLoggedUser() {
        int userId = getLoggedUserId();
        if (userId != -1) {
            return database.usuarioDao().buscarPorId(userId);
        }
        return null;
    }
}