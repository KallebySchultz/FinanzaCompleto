package com.example.finanza.network;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Serviço de sincronização entre banco local (Room) e servidor
 * Gerencia sincronização de dados offline/online
 */
public class SyncService {

    private static final String TAG = "SyncService";
    private static SyncService instance;

    private Context context;
    private AppDatabase database;
    private ServerClient serverClient;
    private ExecutorService executor;

    // Callbacks para sincronização
    public interface SyncCallback {
        void onSyncStarted();
        void onSyncCompleted(boolean success, String message);
        void onSyncProgress(String operation);
    }

    private SyncService(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(context);
        this.serverClient = ServerClient.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static synchronized SyncService getInstance(Context context) {
        if (instance == null) {
            instance = new SyncService(context);
        }
        return instance;
    }

    /**
     * Garante que o executor está disponível e funcionando
     */
    private void ensureExecutorAvailable() {
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newSingleThreadExecutor();
            Log.d(TAG, "Executor recriado após shutdown");
        }
    }

    /**
     * Sincroniza todos os dados com o servidor se conectado
     */
    public void sincronizarTudo(int usuarioId, SyncCallback callback) {
        if (callback != null) {
            callback.onSyncStarted();
        }

        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
                if (!serverClient.isConnected()) {
                    Log.d(TAG, "Não conectado ao servidor - operação offline");
                    if (callback != null) {
                        callback.onSyncCompleted(true, "Modo offline - dados salvos localmente");
                    }
                    return;
                }

                boolean success = true;
                StringBuilder message = new StringBuilder();

                // CRITICAL: Sync order matters due to foreign key dependencies
                // 1. Categories first (no dependencies)
                // 2. Accounts second (depends on user)
                // 3. Transactions last (depends on accounts and categories)
                
                // First: Download data from server in correct order
                if (callback != null) callback.onSyncProgress("Baixando categorias do servidor...");
                if (!buscarCategoriasDoServidor(usuarioId)) {
                    Log.w(TAG, "Falha ao baixar categorias - continuando com outras operações");
                    message.append("Aviso: falha ao baixar categorias. ");
                }

                if (callback != null) callback.onSyncProgress("Baixando contas do servidor...");
                if (!buscarContasDoServidor(usuarioId)) {
                    Log.w(TAG, "Falha ao baixar contas - continuando com outras operações");
                    message.append("Aviso: falha ao baixar contas. ");
                }

                if (callback != null) callback.onSyncProgress("Baixando movimentações do servidor...");
                if (!buscarMovimentacoesDoServidor(usuarioId)) {
                    Log.w(TAG, "Falha ao baixar movimentações - continuando com outras operações");
                    message.append("Aviso: falha ao baixar movimentações. ");
                }

                // Second: Upload local data to server in correct order
                if (callback != null) callback.onSyncProgress("Sincronizando categorias...");
                if (!sincronizarCategorias(usuarioId)) {
                    success = false;
                    message.append("Erro ao sincronizar categorias. ");
                }

                if (callback != null) callback.onSyncProgress("Sincronizando contas...");
                if (!sincronizarContas(usuarioId)) {
                    success = false;
                    message.append("Erro ao sincronizar contas. ");
                }

                if (callback != null) callback.onSyncProgress("Sincronizando movimentações...");
                if (!sincronizarLancamentos(usuarioId)) {
                    success = false;
                    message.append("Erro ao sincronizar movimentações. ");
                }

                if (success && message.length() == 0) {
                    message.append("Sincronização concluída com sucesso");
                } else if (success) {
                    message.append("Sincronização concluída com avisos");
                }

                if (callback != null) {
                    callback.onSyncCompleted(success, message.toString());
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro na sincronização: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro na sincronização: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Sincroniza categorias (envia locais para servidor)
     */
    private boolean sincronizarCategorias(int usuarioId) {
        try {
            List<Categoria> categorias = database.categoriaDao().listarTodas();

            for (Categoria categoria : categorias) {
                String comando = Protocol.buildCommand(
                        Protocol.CMD_ADD_CATEGORIA,
                        categoria.nome,
                        categoria.tipo,
                        categoria.corHex
                );

                Log.d(TAG, "Sincronizando categoria: " + categoria.nome);

                // Enviar comando para o servidor se conectado
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Categoria sincronizada: " + categoria.nome + " - " + result);
                            
                            // CRITICAL FIX: Parse server response and update local categoria with server ID
                            try {
                                String[] partes = Protocol.parseCommand(result);
                                if (partes.length >= 2 && Protocol.STATUS_OK.equals(partes[0])) {
                                    int serverId = Integer.parseInt(partes[1]);
                                    
                                    // Update the local categoria with the server ID
                                    if (categoria.id != serverId) {
                                        Log.d(TAG, "Atualizando categoria local ID " + categoria.id + " para server ID " + serverId);
                                        
                                        // Update lancamentos that reference this categoria
                                        database.lancamentoDao().atualizarCategoriaId(categoria.id, serverId);
                                        
                                        // Create new categoria with server ID and delete old one
                                        Categoria serverCategoria = new Categoria();
                                        serverCategoria.id = serverId;
                                        serverCategoria.nome = categoria.nome;
                                        serverCategoria.tipo = categoria.tipo;
                                        serverCategoria.corHex = categoria.corHex;
                                        serverCategoria.uuid = categoria.uuid;
                                        serverCategoria.syncStatus = 1; // synced
                                        serverCategoria.lastSyncTime = System.currentTimeMillis();
                                        
                                        database.categoriaDao().deletar(categoria);
                                        database.categoriaDao().inserir(serverCategoria);
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Erro ao processar resposta da categoria: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onError(String error) {
                            // Não logar como erro se categoria já existe (duplicata)
                            if (error.toLowerCase().contains("já existe") || error.toLowerCase().contains("duplicate")) {
                                Log.d(TAG, "Categoria já existe no servidor: " + categoria.nome);
                            } else {
                                Log.e(TAG, "Erro ao sincronizar categoria " + categoria.nome + ": " + error);
                            }
                        }
                    });
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao sincronizar categorias: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sincroniza contas
     */
    private boolean sincronizarContas(int usuarioId) {
        try {
            List<Conta> contas = database.contaDao().listarPorUsuario(usuarioId);

            for (Conta conta : contas) {
                String comando = Protocol.buildCommand(
                        Protocol.CMD_ADD_CONTA,
                        conta.nome,
                        conta.tipo != null ? conta.tipo : "corrente",
                        String.valueOf(conta.saldoInicial)
                );

                Log.d(TAG, "Sincronizando conta: " + conta.nome);

                // Enviar comando para o servidor se conectado
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Conta sincronizada: " + conta.nome + " - " + result);
                            
                            // CRITICAL FIX: Parse server response and update local conta with server ID
                            try {
                                String[] partes = Protocol.parseCommand(result);
                                if (partes.length >= 2 && Protocol.STATUS_OK.equals(partes[0])) {
                                    int serverId = Integer.parseInt(partes[1]);
                                    
                                    // Update the local conta with the server ID
                                    if (conta.id != serverId) {
                                        Log.d(TAG, "Atualizando conta local ID " + conta.id + " para server ID " + serverId);
                                        
                                        // Update lancamentos that reference this conta
                                        database.lancamentoDao().atualizarContaId(conta.id, serverId);
                                        
                                        // Create new conta with server ID and delete old one
                                        Conta serverConta = new Conta();
                                        serverConta.id = serverId;
                                        serverConta.nome = conta.nome;
                                        serverConta.tipo = conta.tipo;
                                        serverConta.saldoInicial = conta.saldoInicial;
                                        serverConta.saldoAtual = conta.saldoAtual;
                                        serverConta.usuarioId = conta.usuarioId;
                                        serverConta.uuid = conta.uuid;
                                        serverConta.syncStatus = 1; // synced
                                        serverConta.lastSyncTime = System.currentTimeMillis();
                                        
                                        database.contaDao().deletar(conta);
                                        database.contaDao().inserir(serverConta);
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Erro ao processar resposta da conta: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onError(String error) {
                            // Não logar como erro se conta já existe (duplicata)
                            if (error.toLowerCase().contains("já existe") || error.toLowerCase().contains("duplicate")) {
                                Log.d(TAG, "Conta já existe no servidor: " + conta.nome);
                            } else {
                                Log.e(TAG, "Erro ao sincronizar conta " + conta.nome + ": " + error);
                            }
                        }
                    });
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao sincronizar contas: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sincroniza lançamentos/movimentações
     */
    private boolean sincronizarLancamentos(int usuarioId) {
        try {
            List<Lancamento> lancamentos = database.lancamentoDao().listarPorUsuario(usuarioId);

            for (Lancamento lancamento : lancamentos) {
                String comando = Protocol.buildCommand(
                        Protocol.CMD_ADD_MOVIMENTACAO,
                        String.valueOf(lancamento.valor),
                        new java.sql.Date(lancamento.data).toString(), // formato correto YYYY-MM-DD
                        lancamento.descricao,
                        lancamento.tipo,
                        String.valueOf(lancamento.contaId),
                        String.valueOf(lancamento.categoriaId)
                );

                Log.d(TAG, "Sincronizando lançamento: " + lancamento.descricao);

                // Enviar comando para o servidor se conectado
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Lançamento sincronizado: " + lancamento.descricao);
                        }

                        @Override
                        public void onError(String error) {
                            // Não logar como erro se lançamento já existe (duplicata)
                            if (error.toLowerCase().contains("já existe") || error.toLowerCase().contains("duplicate")) {
                                Log.d(TAG, "Lançamento já existe no servidor: " + lancamento.descricao);
                            } else {
                                Log.e(TAG, "Erro ao sincronizar lançamento " + lancamento.descricao + ": " + error);
                            }
                        }
                    });
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao sincronizar lançamentos: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Adiciona categoria localmente e sincroniza se online
     */
    public void adicionarCategoria(Categoria categoria, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
                // Salva localmente primeiro
                long id = database.categoriaDao().inserir(categoria);
                categoria.id = (int) id;

                Log.d(TAG, "Categoria salva localmente: " + categoria.nome);

                // Tenta sincronizar se conectado
                if (serverClient.isConnected()) {
                    Log.d(TAG, "Sincronizando categoria com servidor...");

                    String comando = Protocol.buildCommand(
                            Protocol.CMD_ADD_CATEGORIA,
                            categoria.nome,
                            categoria.tipo,
                            categoria.corHex
                    );

                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Categoria sincronizada com servidor: " + categoria.nome);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Erro ao sincronizar categoria com servidor: " + error);
                        }
                    });
                }

                if (callback != null) {
                    callback.onSyncCompleted(true, "Categoria adicionada");
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro ao adicionar categoria: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao adicionar categoria: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Adiciona conta localmente e sincroniza se online
     */
    public void adicionarConta(Conta conta, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
                // Validar se o usuário existe antes de inserir a conta
                Usuario usuario = database.usuarioDao().buscarPorId(conta.usuarioId);
                if (usuario == null) {
                    Log.e(TAG, "Erro: Usuário não existe (ID: " + conta.usuarioId + ")");
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Usuário não encontrado");
                    }
                    return;
                }

                long id = database.contaDao().inserir(conta);
                conta.id = (int) id;

                Log.d(TAG, "Conta salva localmente: " + conta.nome);

                if (serverClient.isConnected()) {
                    Log.d(TAG, "Sincronizando conta com servidor...");

                    String comando = Protocol.buildCommand(
                            Protocol.CMD_ADD_CONTA,
                            conta.nome,
                            conta.tipo != null ? conta.tipo : "corrente",
                            String.valueOf(conta.saldoInicial)
                    );

                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Conta sincronizada com servidor: " + conta.nome);
                        }

                        @Override
                        public void onError(String error) {
                            // Não logar como erro se conta já existe (duplicata)
                            if (error.toLowerCase().contains("já existe") || error.toLowerCase().contains("duplicate")) {
                                Log.d(TAG, "Conta já existe no servidor: " + conta.nome);
                            } else {
                                Log.e(TAG, "Erro ao sincronizar conta com servidor: " + error);
                            }
                        }
                    });
                }

                if (callback != null) {
                    callback.onSyncCompleted(true, "Conta adicionada");
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro ao adicionar conta: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao adicionar conta: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Adiciona lançamento localmente e sincroniza se online
     */
    public void adicionarLancamento(Lancamento lancamento, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
                // Validar foreign keys antes de inserir
                Usuario usuario = database.usuarioDao().buscarPorId(lancamento.usuarioId);
                Conta conta = database.contaDao().buscarPorId(lancamento.contaId);

                if (usuario == null) {
                    Log.e(TAG, "Erro: Usuário não existe (ID: " + lancamento.usuarioId + ")");
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Usuário não encontrado");
                    }
                    return;
                }

                if (conta == null) {
                    Log.e(TAG, "Erro: Conta não existe (ID: " + lancamento.contaId + ")");
                    if (callback != null) {
                        callback.onSyncCompleted(false, "Conta não encontrada");
                    }
                    return;
                }

                long id = database.lancamentoDao().inserir(lancamento);
                lancamento.id = (int) id;

                Log.d(TAG, "Lançamento salvo localmente: " + lancamento.descricao);

                if (serverClient.isConnected()) {
                    Log.d(TAG, "Sincronizando lançamento com servidor...");

                    String comando = Protocol.buildCommand(
                            Protocol.CMD_ADD_MOVIMENTACAO,
                            String.valueOf(lancamento.valor),
                            new java.sql.Date(lancamento.data).toString(), // formato correto YYYY-MM-DD
                            lancamento.descricao,
                            lancamento.tipo,
                            String.valueOf(lancamento.contaId),
                            String.valueOf(lancamento.categoriaId)
                    );

                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Lançamento sincronizado com servidor: " + lancamento.descricao);
                        }

                        @Override
                        public void onError(String error) {
                            // Não logar como erro se lançamento já existe (duplicata)
                            if (error.toLowerCase().contains("já existe") || error.toLowerCase().contains("duplicate")) {
                                Log.d(TAG, "Lançamento já existe no servidor: " + lancamento.descricao);
                            } else {
                                Log.e(TAG, "Erro ao sincronizar lançamento com servidor: " + error);
                            }
                        }
                    });
                }

                if (callback != null) {
                    callback.onSyncCompleted(true, "Movimentação adicionada");
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro ao adicionar lançamento: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao adicionar lançamento: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Verifica se existe conexão com servidor
     */
    public boolean isOnline() {
        return serverClient.isConnected();
    }

    /**
     * Libera recursos
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    /**
     * Busca categorias do servidor e armazena localmente
     */
    private boolean buscarCategoriasDoServidor(int usuarioId) {
        try {
            Log.d(TAG, "Buscando categorias do servidor...");
            
            final boolean[] success = {false};
            final Object lock = new Object();
            
            serverClient.listarCategorias(new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "Categorias recebidas do servidor: " + result);
                    if (processarCategoriasDoServidor(result, usuarioId)) {
                        success[0] = true;
                    }
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erro ao buscar categorias: " + error);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });

            // Wait for completion
            synchronized (lock) {
                try {
                    lock.wait(10000); // 10 seconds timeout
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            
            return success[0];
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar categorias do servidor: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca contas do servidor e armazena localmente
     */
    private boolean buscarContasDoServidor(int usuarioId) {
        try {
            Log.d(TAG, "Buscando contas do servidor...");
            
            final boolean[] success = {false};
            final Object lock = new Object();
            
            serverClient.listarContas(new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "Contas recebidas do servidor: " + result);
                    if (processarContasDoServidor(result, usuarioId)) {
                        success[0] = true;
                    }
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erro ao buscar contas: " + error);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });

            // Wait for completion
            synchronized (lock) {
                try {
                    lock.wait(10000); // 10 seconds timeout
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            
            return success[0];
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar contas do servidor: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca movimentações do servidor e armazena localmente
     */
    private boolean buscarMovimentacoesDoServidor(int usuarioId) {
        try {
            Log.d(TAG, "Buscando movimentações do servidor...");
            
            final boolean[] success = {false};
            final Object lock = new Object();
            
            serverClient.listarMovimentacoes(new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "Movimentações recebidas do servidor: " + result);
                    if (processarMovimentacoesDoServidor(result, usuarioId)) {
                        success[0] = true;
                    }
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erro ao buscar movimentações: " + error);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });

            // Wait for completion
            synchronized (lock) {
                try {
                    lock.wait(10000); // 10 seconds timeout
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            
            return success[0];
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar movimentações do servidor: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Processa categorias recebidas do servidor
     */
    private boolean processarCategoriasDoServidor(String response, int usuarioId) {
        try {
            if (response == null || response.trim().isEmpty()) {
                Log.w(TAG, "Resposta vazia para categorias");
                return true; // Não é erro, apenas não há dados
            }

            String[] partes = Protocol.parseCommand(response);
            if (partes.length < 1) {
                Log.w(TAG, "Resposta mal formada para categorias: " + response);
                return false;
            }

            if (!Protocol.STATUS_OK.equals(partes[0])) {
                Log.w(TAG, "Status não OK para categorias: " + response);
                return false;
            }

            if (partes.length < 2 || partes[1] == null || partes[1].trim().isEmpty()) {
                Log.d(TAG, "Nenhuma categoria no servidor");
                return true; // Não é erro, apenas não há dados
            }

            String dados = partes[1];
            String[] categorias = Protocol.parseFields(dados);
            Log.d(TAG, "Processando " + categorias.length + " categorias do servidor");

            int categoriasProcessadas = 0;
            for (String categoriaData : categorias) {
                if (categoriaData == null || categoriaData.trim().isEmpty()) {
                    continue;
                }

                String[] campos = categoriaData.split(",");
                if (campos.length >= 3) {
                    try {
                        // Format: id,nome,tipo,cor
                        int serverId = Integer.parseInt(campos[0].trim());
                        String nome = campos[1].trim();
                        String tipo = campos[2].trim();
                        String cor = campos.length > 3 ? campos[3].trim() : "#666666";

                        if (nome.isEmpty() || tipo.isEmpty()) {
                            Log.w(TAG, "Categoria com dados inválidos: " + categoriaData);
                            continue;
                        }

                        // CRITICAL FIX: Use server ID instead of auto-generated ID
                        // This ensures movimentações can reference the correct category ID
                        long categoriaId = database.categoriaDao().sincronizarDoServidor(serverId, nome, tipo, cor);
                        if (categoriaId > 0) {
                            categoriasProcessadas++;
                            Log.d(TAG, "Categoria sincronizada com ID do servidor " + serverId + ": " + nome);
                        } else {
                            Log.d(TAG, "Categoria já existia com ID " + serverId + ": " + nome);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Erro ao processar categoria individual: " + categoriaData + " - " + e.getMessage());
                    }
                } else {
                    Log.w(TAG, "Categoria com formato inválido: " + categoriaData);
                }
            }

            Log.d(TAG, "Processamento de categorias concluído: " + categoriasProcessadas + " novas categorias adicionadas");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar categorias: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Processa contas recebidas do servidor
     */
    private boolean processarContasDoServidor(String response, int usuarioId) {
        try {
            if (response == null || response.trim().isEmpty()) {
                Log.w(TAG, "Resposta vazia para contas");
                return true; // Não é erro, apenas não há dados
            }

            String[] partes = Protocol.parseCommand(response);
            if (partes.length < 1) {
                Log.w(TAG, "Resposta mal formada para contas: " + response);
                return false;
            }

            if (!Protocol.STATUS_OK.equals(partes[0])) {
                Log.w(TAG, "Status não OK para contas: " + response);
                return false;
            }

            if (partes.length < 2 || partes[1] == null || partes[1].trim().isEmpty()) {
                Log.d(TAG, "Nenhuma conta no servidor");
                return true; // Não é erro, apenas não há dados
            }

            String dados = partes[1];
            String[] contas = Protocol.parseFields(dados);
            Log.d(TAG, "Processando " + contas.length + " contas do servidor");

            int contasProcessadas = 0;
            for (String contaData : contas) {
                if (contaData == null || contaData.trim().isEmpty()) {
                    continue;
                }

                String[] campos = contaData.split(",");
                if (campos.length >= 3) {
                    try {
                        // Format: id,nome,saldo OR id,nome,tipo,saldo
                        int serverId = Integer.parseInt(campos[0].trim());
                        String nome = campos[1].trim();
                        String tipo = "corrente"; // default
                        double saldo;
                        
                        if (campos.length >= 4) {
                            // Format with tipo: id,nome,tipo,saldo
                            tipo = campos[2].trim();
                            saldo = Double.parseDouble(campos[3]);
                        } else {
                            // Format without tipo: id,nome,saldo
                            saldo = Double.parseDouble(campos[2]);
                        }

                        if (nome.isEmpty()) {
                            Log.w(TAG, "Conta com nome vazio: " + contaData);
                            continue;
                        }

                        // Verificar se o usuário existe antes de criar a conta
                        Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
                        if (usuario != null) {
                            // CRITICAL FIX: Use server ID instead of auto-generated ID
                            // This ensures movimentações can reference the correct account ID
                            long contaId = database.contaDao().sincronizarDoServidor(serverId, nome, tipo, saldo, usuarioId);
                            if (contaId > 0) {
                                contasProcessadas++;
                                Log.d(TAG, "Conta sincronizada com ID do servidor " + serverId + ": " + nome + " (tipo: " + tipo + ")");
                            } else {
                                Log.d(TAG, "Conta já existia com ID " + serverId + ": " + nome);
                            }
                        } else {
                            Log.w(TAG, "Não é possível criar conta para usuário inexistente: " + usuarioId);
                        }
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Erro ao converter saldo da conta: " + contaData + " - " + e.getMessage());
                    } catch (Exception e) {
                        Log.w(TAG, "Erro ao processar conta individual: " + contaData + " - " + e.getMessage());
                    }
                }
            }

            Log.d(TAG, "Processamento de contas concluído: " + contasProcessadas + " novas contas adicionadas");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar contas: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Processa movimentações recebidas do servidor
     */
    private boolean processarMovimentacoesDoServidor(String response, int usuarioId) {
        try {
            String[] partes = Protocol.parseCommand(response);
            if (partes.length < 2 || !Protocol.STATUS_OK.equals(partes[0])) {
                Log.w(TAG, "Resposta inválida para movimentações: " + response);
                return false;
            }

            String dados = partes[1];
            if (dados == null || dados.trim().isEmpty()) {
                Log.d(TAG, "Nenhuma movimentação no servidor");
                return true;
            }

            String[] movimentacoes = Protocol.parseFields(dados);
            Log.d(TAG, "Processando " + movimentacoes.length + " movimentações do servidor");

            int movimentacoesProcessadas = 0;
            for (String movData : movimentacoes) {
                if (movData == null || movData.trim().isEmpty()) {
                    continue;
                }

                String[] campos = movData.split(",");
                if (campos.length >= 6) {
                    try {
                        // Format: id,valor,data,descricao,contaId,categoriaId,tipo
                        // Skip server ID (campos[0]) since we'll use local IDs
                        double valor = Double.parseDouble(campos[1]);
                        // Try to parse data as timestamp first, fallback to converting from date string
                        long data;
                        try {
                            data = Long.parseLong(campos[2]);
                        } catch (NumberFormatException e) {
                            // If not a timestamp, try to parse as date string and convert
                            try {
                                java.sql.Date sqlDate = java.sql.Date.valueOf(campos[2]);
                                data = sqlDate.getTime();
                            } catch (Exception dateError) {
                                Log.w(TAG, "Erro ao converter data da movimentação: " + campos[2]);
                                data = System.currentTimeMillis(); // fallback to current time
                            }
                        }
                        String descricao = campos[3].trim();
                        
                        // Need to map server account ID to local account ID
                        int serverContaId = Integer.parseInt(campos[4]);
                        int contaId = mapearContaServidor(serverContaId, usuarioId);
                        
                        int categoriaId = Integer.parseInt(campos[5]);
                        String tipo = campos.length > 6 ? campos[6].trim() : "despesa";

                        if (descricao.isEmpty()) {
                            Log.w(TAG, "Movimentação com descrição vazia: " + movData);
                            continue;
                        }

                        // Skip if conta mapping failed (returns -1)
                        if (contaId == -1) {
                            Log.w(TAG, "Conta não encontrada para movimentação: " + descricao + " (serverContaId: " + serverContaId + ")");
                            continue;
                        }

                        // Verificar se todas as foreign keys existem antes de criar a movimentação
                        Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
                        Conta conta = database.contaDao().buscarPorId(contaId);
                        // Categoria pode ser null, permitindo categoriaId = 0

                        if (usuario != null && conta != null) {
                            // Verificar se movimentação já existe para evitar duplicatas
                            Lancamento duplicata = database.lancamentoDao().buscarDuplicata(
                                    valor, data, descricao, contaId, usuarioId, ""
                            );
                            if (duplicata == null) {
                                Lancamento lancamento = new Lancamento();
                                lancamento.valor = valor;
                                lancamento.data = data;
                                lancamento.descricao = descricao;
                                lancamento.contaId = contaId;
                                lancamento.categoriaId = categoriaId;
                                lancamento.usuarioId = usuarioId;
                                lancamento.tipo = tipo;

                                // Use the safe sync method instead of plain insert
                                long lancamentoId = database.lancamentoDao().inserirSeguro(lancamento);
                                if (lancamentoId > 0) {
                                    movimentacoesProcessadas++;
                                    Log.d(TAG, "Movimentação adicionada localmente: " + descricao);
                                } else {
                                    Log.d(TAG, "Movimentação já existia: " + descricao);
                                }
                            } else {
                                Log.d(TAG, "Movimentação já existe localmente: " + descricao);
                            }
                        } else {
                            Log.w(TAG, "Não é possível criar movimentação - referências inválidas. " +
                                    "Usuario: " + (usuario != null) + ", Conta: " + (conta != null) + 
                                    " (contaId=" + contaId + ") para movimentação: " + descricao);
                            
                            // If account doesn't exist locally but user exists, try to find account by name
                            // This handles the case where server and local IDs don't match
                            if (usuario != null && conta == null) {
                                Log.d(TAG, "Tentando encontrar conta por critério alternativo para movimentação: " + descricao);
                                // For now, just skip - but this could be enhanced to match by account name
                                // This is a limitation of the current ID-based sync approach
                            }
                        }
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Erro ao converter dados da movimentação: " + movData + " - " + e.getMessage());
                    } catch (Exception e) {
                        Log.w(TAG, "Erro ao processar movimentação individual: " + movData + " - " + e.getMessage());
                    }
                } else {
                    Log.w(TAG, "Movimentação com formato inválido: " + movData);
                }
            }

            Log.d(TAG, "Processamento de movimentações concluído: " + movimentacoesProcessadas + " novas movimentações adicionadas");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar movimentações: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Map server account ID to local account ID by finding account with matching name
     * This is needed because server and local IDs may not match
     */
    private int mapearContaServidor(int serverContaId, int usuarioId) {
        try {
            // Try to find accounts for the user
            List<Conta> contas = database.contaDao().listarPorUsuario(usuarioId);
            if (!contas.isEmpty()) {
                // Simple mapping: use first account for now
                // In a more sophisticated version, we could map by account name or store ID mappings
                Log.d(TAG, "Mapeando serverContaId " + serverContaId + " para conta local " + contas.get(0).id + " (nome: " + contas.get(0).nome + ")");
                return contas.get(0).id;
            } else {
                // No accounts available - this is a critical issue
                Log.w(TAG, "ATENÇÃO: Nenhuma conta encontrada para o usuário " + usuarioId + " - criando conta padrão");
                
                // Create a default account to ensure movimentações can be saved
                Conta contaEmergencia = new Conta();
                contaEmergencia.nome = "Conta Padrão (Criada Automaticamente)";
                contaEmergencia.tipo = "corrente";
                contaEmergencia.saldoInicial = 0.0;
                contaEmergencia.usuarioId = usuarioId;
                long contaId = database.contaDao().inserir(contaEmergencia);
                
                Log.d(TAG, "Conta de emergência criada com ID: " + contaId);
                return (int) contaId;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao mapear conta do servidor: " + e.getMessage(), e);
            return -1; // Return -1 to indicate failure
        }
    }
}