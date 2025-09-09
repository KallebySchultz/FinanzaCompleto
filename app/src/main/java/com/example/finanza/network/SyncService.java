package com.example.finanza.network;

import android.content.Context;
import android.util.Log;

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

    private void ensureExecutorAvailable() {
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newSingleThreadExecutor();
            Log.d(TAG, "Executor recriado após shutdown");
        }
    }

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

                if (callback != null) callback.onSyncProgress("Baixando categorias do servidor...");
                if (!buscarCategoriasDoServidor(usuarioId)) {
                    message.append("Aviso: falha ao baixar categorias. ");
                }

                if (callback != null) callback.onSyncProgress("Baixando contas do servidor...");
                if (!buscarContasDoServidor(usuarioId)) {
                    message.append("Aviso: falha ao baixar contas. ");
                }

                if (callback != null) callback.onSyncProgress("Baixando movimentações do servidor...");
                if (!buscarMovimentacoesDoServidor(usuarioId)) {
                    message.append("Aviso: falha ao baixar movimentações. ");
                }

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
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Categoria sincronizada: " + categoria.nome + " - " + result);
                            try {
                                String[] partes = Protocol.parseCommand(result);
                                if (partes.length >= 2 && Protocol.STATUS_OK.equals(partes[0])) {
                                    int serverId = Integer.parseInt(partes[1]);
                                    if (categoria.id != serverId) {
                                        Log.d(TAG, "Atualizando categoria local ID " + categoria.id + " para server ID " + serverId);
                                        database.lancamentoDao().atualizarCategoriaId(categoria.id, serverId);
                                        Categoria serverCategoria = new Categoria();
                                        serverCategoria.id = serverId;
                                        serverCategoria.nome = categoria.nome;
                                        serverCategoria.tipo = categoria.tipo;
                                        serverCategoria.corHex = categoria.corHex;
                                        serverCategoria.uuid = categoria.uuid;
                                        serverCategoria.syncStatus = 1;
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
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Conta sincronizada: " + conta.nome + " - " + result);
                            try {
                                String[] partes = Protocol.parseCommand(result);
                                if (partes.length >= 2 && Protocol.STATUS_OK.equals(partes[0])) {
                                    int serverId = Integer.parseInt(partes[1]);
                                    if (conta.id != serverId) {
                                        Log.d(TAG, "Atualizando conta local ID " + conta.id + " para server ID " + serverId);
                                        database.lancamentoDao().atualizarContaId(conta.id, serverId);
                                        Conta serverConta = new Conta();
                                        serverConta.id = serverId;
                                        serverConta.nome = conta.nome;
                                        serverConta.tipo = conta.tipo;
                                        serverConta.saldoInicial = conta.saldoInicial;
                                        serverConta.saldoAtual = conta.saldoAtual;
                                        serverConta.usuarioId = conta.usuarioId;
                                        serverConta.uuid = conta.uuid;
                                        serverConta.syncStatus = 1;
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

    private boolean sincronizarLancamentos(int usuarioId) {
        try {
            List<Lancamento> lancamentos = database.lancamentoDao().listarPorUsuario(usuarioId);
            for (Lancamento lancamento : lancamentos) {
                String comando = Protocol.buildCommand(
                        Protocol.CMD_ADD_MOVIMENTACAO,
                        String.valueOf(lancamento.valor),
                        new java.sql.Date(lancamento.data).toString(),
                        lancamento.descricao,
                        lancamento.tipo,
                        String.valueOf(lancamento.contaId),
                        String.valueOf(lancamento.categoriaId)
                );
                Log.d(TAG, "Sincronizando lançamento: " + lancamento.descricao);
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Lançamento sincronizado: " + lancamento.descricao);
                        }
                        @Override
                        public void onError(String error) {
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

    public void adicionarCategoria(Categoria categoria, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
                long id = database.categoriaDao().inserir(categoria);
                categoria.id = (int) id;
                Log.d(TAG, "Categoria salva localmente: " + categoria.nome);
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

    public void adicionarConta(Conta conta, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
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

    public void adicionarLancamento(Lancamento lancamento, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
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
                            new java.sql.Date(lancamento.data).toString(),
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

    public void atualizarLancamento(Lancamento lancamento, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
                // Update locally first
                database.lancamentoDao().atualizar(lancamento);
                Log.d(TAG, "Lançamento atualizado localmente: " + lancamento.descricao);
                
                // Sync with server if connected
                if (serverClient.isConnected()) {
                    Log.d(TAG, "Sincronizando atualização de lançamento com servidor...");
                    String comando = Protocol.buildCommand(
                            Protocol.CMD_UPDATE_MOVIMENTACAO,
                            String.valueOf(lancamento.id),
                            String.valueOf(lancamento.valor),
                            new java.sql.Date(lancamento.data).toString(),
                            lancamento.descricao,
                            lancamento.tipo,
                            String.valueOf(lancamento.contaId),
                            String.valueOf(lancamento.categoriaId)
                    );
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Atualização de lançamento sincronizada com servidor: " + lancamento.descricao);
                            if (callback != null) {
                                callback.onSyncCompleted(true, "Movimentação atualizada e sincronizada");
                            }
                        }
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Erro ao sincronizar atualização com servidor: " + error);
                            if (callback != null) {
                                callback.onSyncCompleted(true, "Movimentação atualizada localmente (erro no servidor: " + error + ")");
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Servidor não conectado - atualização salva apenas localmente");
                    if (callback != null) {
                        callback.onSyncCompleted(true, "Movimentação atualizada localmente (servidor offline)");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar lançamento: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao atualizar lançamento: " + e.getMessage());
                }
            }
        });
    }

    public void deletarLancamento(Lancamento lancamento, SyncCallback callback) {
        ensureExecutorAvailable();
        executor.execute(() -> {
            try {
                // Delete locally first
                database.lancamentoDao().deletar(lancamento);
                Log.d(TAG, "Lançamento deletado localmente: " + lancamento.descricao);
                
                // Sync with server if connected
                if (serverClient.isConnected()) {
                    Log.d(TAG, "Sincronizando exclusão de lançamento com servidor...");
                    String comando = Protocol.buildCommand(
                            Protocol.CMD_DELETE_MOVIMENTACAO,
                            String.valueOf(lancamento.id)
                    );
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Exclusão de lançamento sincronizada com servidor: " + lancamento.descricao);
                            if (callback != null) {
                                callback.onSyncCompleted(true, "Movimentação excluída e sincronizada");
                            }
                        }
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Erro ao sincronizar exclusão com servidor: " + error);
                            if (callback != null) {
                                callback.onSyncCompleted(true, "Movimentação excluída localmente (erro no servidor: " + error + ")");
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Servidor não conectado - exclusão salva apenas localmente");
                    if (callback != null) {
                        callback.onSyncCompleted(true, "Movimentação excluída localmente (servidor offline)");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao deletar lançamento: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onSyncCompleted(false, "Erro ao deletar lançamento: " + e.getMessage());
                }
            }
        });
    }

    public boolean isOnline() {
        return serverClient.isConnected();
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

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
            synchronized (lock) {
                try {
                    lock.wait(10000);
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
            synchronized (lock) {
                try {
                    lock.wait(10000);
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
            synchronized (lock) {
                try {
                    lock.wait(10000);
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

    private boolean processarCategoriasDoServidor(String response, int usuarioId) {
        try {
            if (response == null || response.trim().isEmpty()) {
                Log.w(TAG, "Resposta vazia para categorias");
                return true;
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
                return true;
            }
            String dados = partes[1];
            String[] categorias = Protocol.parseFields(dados);
            Log.d(TAG, "Processando " + categorias.length + " categorias do servidor");
            int categoriasProcessadas = 0;
            for (String categoriaData : categorias) {
                if (categoriaData == null || categoriaData.trim().isEmpty()) continue;
                String[] campos = categoriaData.split(",");
                if (campos.length >= 3) {
                    try {
                        int serverId = Integer.parseInt(campos[0].trim());
                        String nome = campos[1].trim();
                        String tipo = campos[2].trim();
                        String cor = campos.length > 3 ? campos[3].trim() : "#666666";
                        if (nome.isEmpty() || tipo.isEmpty()) {
                            Log.w(TAG, "Categoria com dados inválidos: " + categoriaData);
                            continue;
                        }
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

    private boolean processarContasDoServidor(String response, int usuarioId) {
        try {
            if (response == null || response.trim().isEmpty()) {
                Log.w(TAG, "Resposta vazia para contas");
                return true;
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
                return true;
            }
            String dados = partes[1];
            String[] contas = Protocol.parseFields(dados);
            Log.d(TAG, "Processando " + contas.length + " contas do servidor");
            int contasProcessadas = 0;
            for (String contaData : contas) {
                if (contaData == null || contaData.trim().isEmpty()) continue;
                String[] campos = contaData.split(",");
                // Suporta tanto formato antigo quanto novo (4 campos = id, nome, tipo, saldo)
                if (campos.length >= 4) {
                    try {
                        int serverId = Integer.parseInt(campos[0].trim());
                        String nome = campos[1].trim();
                        String tipo = campos[2].trim();
                        double saldo = Double.parseDouble(campos[3]);
                        if (nome.isEmpty()) {
                            Log.w(TAG, "Conta com nome vazio: " + contaData);
                            continue;
                        }
                        Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
                        if (usuario != null) {
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

            // REMOVE TODOS OS LANÇAMENTOS DO USUÁRIO ANTES DE IMPORTAR (corrige duplicidade/soma)
            database.lancamentoDao().deletarTodosDoUsuario(usuarioId);

            int movimentacoesProcessadas = 0;
            for (String movData : movimentacoes) {
                if (movData == null || movData.trim().isEmpty()) continue;
                String[] campos = movData.split(",");
                if (campos.length >= 8) {
                    try {
                        int serverId = Integer.parseInt(campos[0].trim());
                        String valorStr = campos[1].trim() + "." + campos[2].trim();
                        double valor = Double.parseDouble(valorStr.replace(",", "."));
                        long data;
                        try {
                            java.sql.Date sqlDate = java.sql.Date.valueOf(campos[3].trim());
                            data = sqlDate.getTime();
                        } catch (Exception dateError) {
                            Log.w(TAG, "Erro ao converter data da movimentação: " + campos[3]);
                            data = System.currentTimeMillis();
                        }
                        String descricao = campos[4].trim();
                        String tipo = campos[5].trim();
                        int serverContaId = Integer.parseInt(campos[6].trim());
                        int contaId = mapearContaServidor(serverContaId, usuarioId);
                        int categoriaId = Integer.parseInt(campos[7].trim());
                        if (descricao.isEmpty()) {
                            Log.w(TAG, "Movimentação com descrição vazia: " + movData);
                            continue;
                        }
                        if (contaId == -1) {
                            Log.w(TAG, "Conta não encontrada para movimentação: " + descricao + " (serverContaId: " + serverContaId + ")");
                            continue;
                        }
                        Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
                        Conta conta = database.contaDao().buscarPorId(contaId);
                        if (usuario != null && conta != null) {
                            Lancamento lancamento = new Lancamento();
                            lancamento.valor = valor;
                            lancamento.data = data;
                            lancamento.descricao = descricao;
                            lancamento.contaId = contaId;
                            lancamento.categoriaId = categoriaId;
                            lancamento.usuarioId = usuarioId;
                            lancamento.tipo = tipo;
                            long lancamentoId = database.lancamentoDao().inserirSeguro(lancamento);
                            if (lancamentoId > 0) {
                                movimentacoesProcessadas++;
                                Log.d(TAG, "Movimentação adicionada localmente: " + descricao);
                            }
                        } else {
                            Log.w(TAG, "Não é possível criar movimentação - referências inválidas. " +
                                    "Usuario: " + (usuario != null) + ", Conta: " + (conta != null) +
                                    " (contaId=" + contaId + ") para movimentação: " + descricao);
                        }
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

    private int mapearContaServidor(int serverContaId, int usuarioId) {
        try {
            List<Conta> contas = database.contaDao().listarPorUsuario(usuarioId);
            for (Conta conta : contas) {
                if (conta.id == serverContaId) {
                    Log.d(TAG, "Mapeando serverContaId " + serverContaId + " para conta local " + conta.id + " (nome: " + conta.nome + ")");
                    return conta.id;
                }
            }
            Log.w(TAG, "ATENÇÃO: Nenhuma conta encontrada para o usuário " + usuarioId + " com serverContaId " + serverContaId + " - criando conta padrão");
            Conta contaEmergencia = new Conta();
            contaEmergencia.nome = "Conta Padrão (Criada Automaticamente)";
            contaEmergencia.tipo = "corrente";
            contaEmergencia.saldoInicial = 0.0;
            contaEmergencia.usuarioId = usuarioId;
            long contaId = database.contaDao().inserir(contaEmergencia);
            Log.d(TAG, "Conta de emergência criada com ID: " + contaId);
            return (int) contaId;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao mapear conta do servidor: " + e.getMessage(), e);
            return -1;
        }
    }
}