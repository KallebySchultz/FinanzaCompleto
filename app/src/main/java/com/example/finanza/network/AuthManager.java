package com.example.finanza.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Usuario;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Categoria;

/**
 * AuthManager - Gerenciador de Autenticação Híbrido (Online/Offline)
 * 
 * Classe singleton responsável por gerenciar a autenticação de usuários
 * no aplicativo Finanza, suportando tanto modo online (com sincronização
 * ao servidor desktop) quanto modo offline (apenas local).
 * 
 * Características principais:
 * - Autenticação prioritária com servidor desktop via TCP sockets
 * - Fallback automático para autenticação local quando servidor indisponível
 * - Gerenciamento de sessão persistente com SharedPreferences
 * - Sincronização automática de dados após login bem-sucedido
 * - Criação automática de dados padrão (contas e categorias) para novos usuários
 * 
 * Fluxo de Autenticação:
 * 1. Verifica credenciais localmente
 * 2. Tenta conectar ao servidor desktop
 * 3. Se conectado: autentica no servidor e sincroniza dados
 * 4. Se desconectado: usa autenticação local
 * 5. Salva sessão e inicializa dados padrão se necessário
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class AuthManager {

    private static final String TAG = "AuthManager";
    
    // Constantes para SharedPreferences
    private static final String PREFS_NAME = "FinanzaAuth";
    private static final String PREF_USER_ID = "usuarioId";
    private static final String PREF_USER_EMAIL = "userEmail";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";

    // Componentes principais
    private Context context;
    private AppDatabase database;
    private ServerClient serverClient;
    private static AuthManager instance;

    /**
     * Interface de callback para operações de autenticação
     */
    public interface AuthCallback {
        /**
         * Chamado quando a autenticação é bem-sucedida
         * @param usuario Objeto do usuário autenticado
         */
        void onSuccess(Usuario usuario);
        
        /**
         * Chamado quando ocorre erro na autenticação
         * @param error Mensagem de erro
         */
        void onError(String error);
    }

    /**
     * Construtor privado para implementação Singleton
     * 
     * @param context Contexto da aplicação
     */
    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.serverClient = ServerClient.getInstance(context);
        
        // Create test user for development/testing
        criarUsuarioTesteSeMecessario();
    }

    /**
     * Obtém a instância singleton do AuthManager
     * 
     * @param context Contexto da aplicação
     * @return Instância única do AuthManager
     */
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    /**
     * Realiza login do usuário com autenticação híbrida
     * 
     * Processo:
     * 1. Verifica credenciais localmente
     * 2. Tenta autenticar no servidor (se disponível)
     * 3. Sincroniza dados após login bem-sucedido
     * 4. Fallback para modo offline se servidor indisponível
     * 
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @param callback Callback para resultado da operação
     */
    public void login(String email, String senha, AuthCallback callback) {
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
                                    database.usuarioDao().atualizar(usuario);
                                }
                            }
                        }

                        if (usuario == null) {
                            usuario = usuarioLocal != null ? usuarioLocal : criarUsuarioLocal("", email, senha);
                        }

                        if (usuario != null) {
                            salvarSessao(usuario);

                            // Sempre chamar o callback de sucesso primeiro, independente da sincronização
                            callback.onSuccess(usuario);

                            // Correção: tornar usuario final para uso dentro do callback
                            final Usuario usuarioFinal = usuario;

                            // Iniciar sincronização de dados após login bem-sucedido (em background)
                            SyncService syncService = SyncService.getInstance(context);
                            syncService.sincronizarTudo(usuarioFinal.id, new SyncService.SyncCallback() {
                                @Override
                                public void onSyncStarted() {
                                    Log.d(TAG, "Iniciando sincronização pós-login...");
                                }

                                @Override
                                public void onSyncCompleted(boolean success, String message) {
                                    Log.d(TAG, "Sincronização pós-login concluída: " + message);

                                    // Use usuarioFinal
                                    verificarECriarDadosSeNecessario(usuarioFinal.id);
                                }

                                @Override
                                public void onSyncProgress(String operation) {
                                    Log.d(TAG, "Sincronização: " + operation);
                                }
                            });
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
    }

    /**
     * Registra um novo usuário no sistema
     * 
     * Processo:
     * 1. Verifica se usuário já existe localmente
     * 2. Tenta registrar no servidor (se disponível)
     * 3. Cria usuário local independentemente do resultado do servidor
     * 4. Salva sessão e inicializa dados padrão
     * 
     * @param nome Nome completo do usuário
     * @param email Email do usuário (deve ser único)
     * @param senha Senha do usuário
     * @param callback Callback para resultado da operação
     */
    public void registrar(String nome, String email, String senha, AuthCallback callback) {
        // Verifica se usuário já existe localmente
        if (buscarUsuarioLocal(email, senha) != null) {
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
    }

    /**
     * Busca usuário local por email e valida a senha
     */
    private Usuario buscarUsuarioLocal(String email, String senha) {
        try {
            // Busca usuário por email e senha usando o método de login do DAO
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

            long id = database.usuarioDao().inserir(usuario);
            usuario.id = (int) id;

            // Create default data for new user
            criarDadosIniciais(usuario.id);

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
     * Creates default test user if it doesn't exist (for development/testing)
     */
    private void criarUsuarioTesteSeMecessario() {
        try {
            // Check if test user already exists
            Usuario usuarioTeste = database.usuarioDao().buscarPorEmail("teste1@gmail.com");
            if (usuarioTeste == null) {
                Log.d(TAG, "Criando usuário de teste local...");
                
                // Create test user
                Usuario usuario = new Usuario();
                usuario.nome = "Usuário Teste";
                usuario.email = "teste1@gmail.com";
                usuario.senha = "123456"; // Plain text for local auth
                usuario.dataCriacao = System.currentTimeMillis();

                long id = database.usuarioDao().inserir(usuario);
                usuario.id = (int) id;

                // Create default data for test user
                criarDadosIniciais(usuario.id);
                
                Log.d(TAG, "Usuário de teste criado com sucesso");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar usuário de teste: " + e.getMessage(), e);
        }
    }

    /**
     * Creates initial data for new users (no default data - clean start)
     */
    private void criarDadosIniciais(int usuarioId) {
        try {
            Log.d(TAG, "Usuário criado sem dados padrão: " + usuarioId);
            // User starts with no default accounts or categories - clean slate
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar dados iniciais: " + e.getMessage(), e);
        }
    }

    /**
     * Removed: No longer creates default categories - users start with clean slate
     */
    private void criarCategoriasPadrao() {
        // Method removed - no default categories are created
        Log.d(TAG, "Default category creation disabled - users start with clean slate");
    }

    /**
     * No longer creates default data - users manage their own accounts and categories
     */
    private void verificarECriarDadosSeNecessario(int usuarioId) {
        try {
            Log.d(TAG, "User data verification - no default data creation");
            // No automatic creation of default accounts or categories
            // Users must create their own data as needed
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar dados: " + e.getMessage(), e);
        }
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