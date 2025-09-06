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
        this.database = Room.databaseBuilder(context, AppDatabase.class, "finanza-database")
                .build();
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
                
                // Primeiro buscar dados do servidor
                if (callback != null) callback.onSyncProgress("Baixando dados do servidor...");
                if (!buscarDadosDoServidor(usuarioId)) {
                    Log.w(TAG, "Erro ao buscar dados do servidor, mas continuando...");
                    message.append("Aviso: alguns dados podem não estar atualizados. ");
                }
                
                // Depois sincronizar dados locais para o servidor
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
                    categoria.tipo
                    // Removido corHex - servidor não espera este parâmetro
                );
                
                Log.d(TAG, "Sincronizando categoria: " + categoria.nome);
                
                // Enviar comando para o servidor se conectado
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Categoria sincronizada: " + categoria.nome);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Erro ao sincronizar categoria " + categoria.nome + ": " + error);
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
                    "corrente", // tipo padrão se não especificado
                    String.valueOf(conta.saldoInicial)
                );
                
                Log.d(TAG, "Sincronizando conta: " + conta.nome);
                
                // Enviar comando para o servidor se conectado
                if (serverClient.isConnected()) {
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Conta sincronizada: " + conta.nome);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Erro ao sincronizar conta " + conta.nome + ": " + error);
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
                    String.valueOf(lancamento.data),
                    lancamento.descricao,
                    String.valueOf(lancamento.contaId),
                    String.valueOf(lancamento.categoriaId),
                    lancamento.tipo
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
                            Log.e(TAG, "Erro ao sincronizar lançamento " + lancamento.descricao + ": " + error);
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
                        categoria.tipo
                        // Removido corHex - servidor não espera este parâmetro
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
                        "corrente", // tipo padrão
                        String.valueOf(conta.saldoInicial)
                    );
                    
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Conta sincronizada com servidor: " + conta.nome);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Erro ao sincronizar conta com servidor: " + error);
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
                        String.valueOf(lancamento.data),
                        lancamento.descricao,
                        String.valueOf(lancamento.contaId),
                        String.valueOf(lancamento.categoriaId),
                        lancamento.tipo
                    );
                    
                    serverClient.enviarComando(comando, new ServerClient.ServerCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Lançamento sincronizado com servidor: " + lancamento.descricao);
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Erro ao sincronizar lançamento com servidor: " + error);
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
     * Busca todos os dados do servidor e armazena localmente
     */
    private boolean buscarDadosDoServidor(int usuarioId) {
        try {
            Log.d(TAG, "Iniciando busca de dados do servidor...");
            
            final boolean[] allCompleted = {false};
            final boolean[] success = {true};
            final Object lock = new Object();
            final int[] completedRequests = {0};
            final int totalRequests = 3;
            
            // Buscar categorias do servidor
            serverClient.listarCategorias(new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "Categorias recebidas do servidor: " + result);
                    if (!processarCategoriasDoServidor(result, usuarioId)) {
                        success[0] = false;
                    }
                    synchronized (lock) {
                        completedRequests[0]++;
                        if (completedRequests[0] >= totalRequests) {
                            allCompleted[0] = true;
                            lock.notifyAll();
                        }
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erro ao buscar categorias: " + error);
                    synchronized (lock) {
                        completedRequests[0]++;
                        if (completedRequests[0] >= totalRequests) {
                            allCompleted[0] = true;
                            lock.notifyAll();
                        }
                    }
                }
            });
            
            // Buscar contas
            serverClient.listarContas(new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "Contas recebidas do servidor: " + result);
                    if (!processarContasDoServidor(result, usuarioId)) {
                        success[0] = false;
                    }
                    synchronized (lock) {
                        completedRequests[0]++;
                        if (completedRequests[0] >= totalRequests) {
                            allCompleted[0] = true;
                            lock.notifyAll();
                        }
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erro ao buscar contas: " + error);
                    synchronized (lock) {
                        completedRequests[0]++;
                        if (completedRequests[0] >= totalRequests) {
                            allCompleted[0] = true;
                            lock.notifyAll();
                        }
                    }
                }
            });
            
            // Buscar movimentações
            serverClient.listarMovimentacoes(new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "Movimentações recebidas do servidor: " + result);
                    if (!processarMovimentacoesDoServidor(result, usuarioId)) {
                        success[0] = false;
                    }
                    synchronized (lock) {
                        completedRequests[0]++;
                        if (completedRequests[0] >= totalRequests) {
                            allCompleted[0] = true;
                            lock.notifyAll();
                        }
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erro ao buscar movimentações: " + error);
                    synchronized (lock) {
                        completedRequests[0]++;
                        if (completedRequests[0] >= totalRequests) {
                            allCompleted[0] = true;
                            lock.notifyAll();
                        }
                    }
                }
            });
            
            // Aguardar todas as operações completarem (com timeout)
            synchronized (lock) {
                long startTime = System.currentTimeMillis();
                while (!allCompleted[0] && (System.currentTimeMillis() - startTime) < 15000) { // 15 segundos timeout
                    try {
                        lock.wait(1000); // Verifica a cada segundo
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        Log.w(TAG, "Thread interrompida durante sincronização");
                        break;
                    }
                }
            }
            
            Log.d(TAG, "Busca de dados concluída - Completadas: " + completedRequests[0] + 
                       "/" + totalRequests + ", Sucesso: " + success[0]);
            
            return success[0] && completedRequests[0] >= totalRequests;
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar dados do servidor: " + e.getMessage(), e);
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
            
            for (String categoriaData : categorias) {
                if (categoriaData == null || categoriaData.trim().isEmpty()) {
                    continue;
                }
                
                String[] campos = categoriaData.split(",");
                if (campos.length >= 3) {
                    // Format: id,nome,tipo,cor
                    String nome = campos[1].trim();
                    String tipo = campos[2].trim();
                    String cor = campos.length > 3 ? campos[3].trim() : "#666666";
                    
                    if (nome.isEmpty() || tipo.isEmpty()) {
                        Log.w(TAG, "Categoria com dados inválidos: " + categoriaData);
                        continue;
                    }
                    
                    // Verifica se categoria já existe localmente
                    List<Categoria> existentes = database.categoriaDao().listarPorTipo(tipo);
                    boolean existe = false;
                    for (Categoria cat : existentes) {
                        if (cat.nome.equals(nome)) {
                            existe = true;
                            break;
                        }
                    }
                    
                    if (!existe) {
                        Categoria categoria = new Categoria();
                        categoria.nome = nome;
                        categoria.tipo = tipo;
                        categoria.corHex = cor;
                        database.categoriaDao().inserir(categoria);
                        Log.d(TAG, "Categoria adicionada localmente: " + nome);
                    }
                } else {
                    Log.w(TAG, "Categoria com formato inválido: " + categoriaData);
                }
            }
            
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
            String[] partes = Protocol.parseCommand(response);
            if (partes.length < 2 || !Protocol.STATUS_OK.equals(partes[0])) {
                Log.w(TAG, "Resposta inválida para contas: " + response);
                return false;
            }
            
            String dados = partes[1];
            if (dados == null || dados.trim().isEmpty()) {
                Log.d(TAG, "Nenhuma conta no servidor");
                return true;
            }
            
            String[] contas = Protocol.parseFields(dados);
            Log.d(TAG, "Processando " + contas.length + " contas do servidor");
            
            for (String contaData : contas) {
                String[] campos = contaData.split(",");
                if (campos.length >= 3) {
                    // Format: id,nome,saldo
                    String nome = campos[1];
                    double saldo = Double.parseDouble(campos[2]);
                    
                    // Verifica se conta já existe localmente
                    List<Conta> existentes = database.contaDao().listarPorUsuario(usuarioId);
                    boolean existe = false;
                    for (Conta conta : existentes) {
                        if (conta.nome.equals(nome)) {
                            existe = true;
                            break;
                        }
                    }
                    
                    if (!existe) {
                        // Verificar se o usuário existe antes de criar a conta
                        Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
                        if (usuario != null) {
                            Conta conta = new Conta();
                            conta.nome = nome;
                            conta.saldoInicial = saldo;
                            conta.usuarioId = usuarioId;
                            database.contaDao().inserir(conta);
                            Log.d(TAG, "Conta adicionada localmente: " + nome);
                        } else {
                            Log.w(TAG, "Não é possível criar conta para usuário inexistente: " + usuarioId);
                        }
                    }
                }
            }
            
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
            
            for (String movData : movimentacoes) {
                String[] campos = movData.split(",");
                if (campos.length >= 6) {
                    // Format: id,valor,data,descricao,contaId,categoriaId,tipo
                    try {
                        double valor = Double.parseDouble(campos[1]);
                        long data = Long.parseLong(campos[2]);
                        String descricao = campos[3];
                        int contaId = Integer.parseInt(campos[4]);
                        int categoriaId = Integer.parseInt(campos[5]);
                        String tipo = campos.length > 6 ? campos[6] : "despesa";
                        
                        // Verificar se todas as foreign keys existem antes de criar a movimentação
                        Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
                        Conta conta = database.contaDao().buscarPorId(contaId);
                        // Categoria pode ser null, permitindo categoriaId = 0
                        
                        if (usuario != null && conta != null) {
                            Lancamento lancamento = new Lancamento();
                            lancamento.valor = valor;
                            lancamento.data = data;
                            lancamento.descricao = descricao;
                            lancamento.contaId = contaId;
                            lancamento.categoriaId = categoriaId;
                            lancamento.usuarioId = usuarioId;
                            lancamento.tipo = tipo;
                            
                            database.lancamentoDao().inserir(lancamento);
                            Log.d(TAG, "Movimentação adicionada localmente: " + descricao);
                        } else {
                            Log.w(TAG, "Não é possível criar movimentação - referências inválidas. Usuario: " + 
                                (usuario != null) + ", Conta: " + (conta != null));
                        }
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Erro ao converter dados da movimentação: " + movData);
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar movimentações: " + e.getMessage(), e);
            return false;
        }
    }
}