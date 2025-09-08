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
     * Busca usuário local por email
     */
    private Usuario buscarUsuarioLocal(String email, String senha) {
        try {
            // Busca por email primeiro
            return database.usuarioDao().buscarPorEmail(email);
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
     * Creates default data for new users (account and categories)
     */
    private void criarDadosIniciais(int usuarioId) {
        try {
            Log.d(TAG, "Criando dados iniciais para usuário: " + usuarioId);

            // Create default account
            Conta contaPadrao = new Conta();
            contaPadrao.nome = "Conta Padrão";
            contaPadrao.tipo = "corrente";
            contaPadrao.saldoInicial = 0.0;
            contaPadrao.usuarioId = usuarioId;
            database.contaDao().inserir(contaPadrao);

            // Create default expense categories if none exist
            if (database.categoriaDao().listarPorTipo("despesa").isEmpty()) {
                criarCategoriasPadrao();
            }

            Log.d(TAG, "Dados iniciais criados com sucesso");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar dados iniciais: " + e.getMessage(), e);
        }
    }

    /**
     * Creates default categories for the app
     */
    private void criarCategoriasPadrao() {
        try {
            // Default expense categories
            String[][] categoriasDespesa = {
                    {"Alimentação", "#FF6B6B"},
                    {"Transporte", "#4ECDC4"},
                    {"Saúde", "#45B7D1"},
                    {"Educação", "#96CEB4"},
                    {"Lazer", "#FFEAA7"},
                    {"Casa", "#DDA0DD"},
                    {"Roupas", "#98D8C8"},
                    {"Tecnologia", "#F7DC6F"},
                    {"Viagem", "#BB8FCE"},
                    {"Outros", "#85929E"}
            };

            for (String[] cat : categoriasDespesa) {
                Categoria categoria = new Categoria();
                categoria.nome = cat[0];
                categoria.tipo = "despesa";
                categoria.corHex = cat[1];
                database.categoriaDao().inserir(categoria);
            }

            // Default income categories
            String[][] categoriasReceita = {
                    {"Salário", "#2ECC71"},
                    {"Freelance", "#3498DB"},
                    {"Investimentos", "#9B59B6"},
                    {"Vendas", "#E67E22"},
                    {"Prêmios", "#F1C40F"},
                    {"Restituição", "#1ABC9C"},
                    {"Outros", "#34495E"}
            };

            for (String[] cat : categoriasReceita) {
                Categoria categoria = new Categoria();
                categoria.nome = cat[0];
                categoria.tipo = "receita";
                categoria.corHex = cat[1];
                database.categoriaDao().inserir(categoria);
            }

            Log.d(TAG, "Categorias padrão criadas");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar categorias padrão: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if user has necessary data and creates default data if needed
     */
    private void verificarECriarDadosSeNecessario(int usuarioId) {
        try {
            // Check if user has at least one account
            if (database.contaDao().listarPorUsuario(usuarioId).isEmpty()) {
                Log.d(TAG, "Usuário sem contas - criando conta padrão");
                Conta contaPadrao = new Conta();
                contaPadrao.nome = "Conta Padrão";
                contaPadrao.tipo = "corrente";
                contaPadrao.saldoInicial = 0.0;
                contaPadrao.usuarioId = usuarioId;
                database.contaDao().inserir(contaPadrao);
            }

            // Check if user has categories
            if (database.categoriaDao().listarTodas().isEmpty()) {
                Log.d(TAG, "Usuário sem categorias - criando categorias padrão");
                criarCategoriasPadrao();
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar dados necessários: " + e.getMessage(), e);
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
     * Recupera senha do usuário
     * 
     * @param email Email do usuário
     * @param callback Callback para resultado da operação
     */
    public void recuperarSenha(String email, AuthCallback callback) {
        try {
            // Buscar usuário por email
            Usuario usuario = database.usuarioDao().buscarPorEmail(email);
            
            if (usuario == null) {
                callback.onError("Usuário não encontrado com este email");
                return;
            }
            
            // Gerar senha temporária simples
            String senhaTemporaria = "temp" + String.valueOf(System.currentTimeMillis()).substring(7);
            
            // Atualizar senha no banco local
            usuario.senha = senhaTemporaria;
            database.usuarioDao().atualizar(usuario);
            
            Log.d(TAG, "Senha recuperada para usuário: " + email);
            callback.onSuccess(usuario);
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao recuperar senha: " + e.getMessage());
            callback.onError("Erro ao recuperar senha: " + e.getMessage());
        }
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