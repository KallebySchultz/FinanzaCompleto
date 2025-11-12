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
 * ServerClient - Cliente de Comunicação com Servidor Desktop via Sockets TCP/IP
 * 
 * Esta classe implementa o padrão Singleton e é responsável por toda a comunicação
 * entre o aplicativo Android e o servidor desktop Java do sistema Finanza.
 * 
 * Arquitetura de Comunicação:
 * - Protocolo: TCP/IP via Sockets
 * - Formato: Comandos de texto delimitados por pipes (|)
 * - Modo: Assíncrono usando AsyncTask
 * - Conexão: Persistente durante sessão do usuário
 * 
 * Funcionalidades principais:
 * - Estabelecer e gerenciar conexão TCP/IP com servidor desktop
 * - Enviar comandos ao servidor de forma assíncrona
 * - Receber e processar respostas do servidor
 * - Gerenciar estado de conexão (conectado/desconectado)
 * - Armazenar configurações de servidor (IP e porta)
 * - Fornecer interface de callback para operações assíncronas
 * 
 * Fluxo de uso típico:
 * 1. Obter instância via getInstance()
 * 2. Configurar servidor com configurarServidor() ou usar padrão
 * 3. Conectar ao servidor com conectar()
 * 4. Enviar comandos com enviarComando() ou métodos específicos (login, listarContas, etc)
 * 5. Receber respostas via callbacks
 * 6. Desconectar quando necessário com disconnect()
 * 
 * Tratamento de erros:
 * - Timeout de conexão (5 segundos)
 * - Perda de conexão durante operação
 * - Erros de I/O e rede
 * - Validação de estado de conexão antes de enviar comandos
 * 
 * Configuração padrão:
 * - Host: 192.168.1.100 (pode ser alterado)
 * - Porta: 8080 (pode ser alterada)
 * 
 * Thread-safety:
 * - Singleton sincronizado
 * - Operações de rede executadas em background (AsyncTask)
 * - Callbacks executados na thread principal (UI thread)
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class ServerClient {
    
    /** Tag para logs de debug do Android */
    private static final String TAG = "ServerClient";
    
    /** Timeout para estabelecer conexão com servidor (5 segundos) */
    private static final int CONNECTION_TIMEOUT = 5000;
    
    /** Nome do arquivo de SharedPreferences para configurações do servidor */
    private static final String PREFS_NAME = "FinanzaServerConfig";
    
    /** Chave para armazenar o host do servidor nas SharedPreferences */
    private static final String PREF_HOST = "server_host";
    
    /** Chave para armazenar a porta do servidor nas SharedPreferences */
    private static final String PREF_PORT = "server_port";
    
    /** Contexto da aplicação Android para acessar recursos do sistema */
    private Context context;
    
    /** Endereço IP ou hostname do servidor desktop */
    private String serverHost;
    
    /** Porta TCP onde o servidor desktop está escutando */
    private int serverPort;
    
    /** Socket TCP para comunicação com o servidor */
    private Socket socket;
    
    /** Stream de entrada para ler respostas do servidor */
    private BufferedReader input;
    
    /** Stream de saída para enviar comandos ao servidor */
    private PrintWriter output;
    
    /** Flag que indica se há conexão ativa com o servidor */
    private boolean connected = false;
    
    /** Instância única da classe (padrão Singleton) */
    private static ServerClient instance;
    
    /**
     * Obtém a instância única do ServerClient (padrão Singleton)
     * 
     * Este método é thread-safe e garante que apenas uma instância
     * do cliente seja criada, mesmo em ambientes multi-thread.
     * 
     * @param context Contexto da aplicação Android (será convertido para ApplicationContext)
     * @return Instância única e compartilhada do ServerClient
     */
    public static synchronized ServerClient getInstance(Context context) {
        if (instance == null) {
            instance = new ServerClient(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Construtor privado - uso exclusivo do padrão Singleton
     * 
     * Inicializa o cliente e carrega as configurações salvas do servidor
     * (IP e porta) a partir das SharedPreferences.
     * 
     * @param context Contexto da aplicação Android
     */
    private ServerClient(Context context) {
        this.context = context;
        loadServerConfig();
    }
    
    /**
     * Interface de callback para operações assíncronas do servidor
     * 
     * Todos os métodos que se comunicam com o servidor usam esta interface
     * para retornar resultados de forma assíncrona, permitindo que a UI
     * permaneça responsiva durante operações de rede.
     * 
     * @param <T> Tipo do resultado esperado (geralmente String)
     */
    public interface ServerCallback<T> {
        /**
         * Chamado quando a operação é concluída com sucesso
         * 
         * Este método é executado na thread principal (UI thread),
         * permitindo atualização segura da interface.
         * 
         * @param result Resultado da operação (resposta do servidor)
         */
        void onSuccess(T result);
        
        /**
         * Chamado quando ocorre um erro durante a operação
         * 
         * Este método é executado na thread principal (UI thread),
         * permitindo exibição segura de mensagens de erro.
         * 
         * @param error Mensagem descritiva do erro ocorrido
         */
        void onError(String error);
    }
    
    /**
     * Carrega configurações do servidor das SharedPreferences
     * 
     * Busca o endereço IP/hostname e porta do servidor salvos previamente.
     * Se não houver configurações salvas, usa valores padrão:
     * - Host padrão: 192.168.1.100 (rede local típica)
     * - Porta padrão: 8080 (porta comum para servidores Java)
     * 
     * Este método é chamado automaticamente no construtor.
     */
    private void loadServerConfig() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        serverHost = prefs.getString(PREF_HOST, "192.168.1.100");
        serverPort = prefs.getInt(PREF_PORT, 8080);
    }
    
    /**
     * Configura o endereço e porta do servidor desktop
     * 
     * Atualiza as configurações de conexão e persiste os valores nas
     * SharedPreferences para uso futuro. Esta configuração permanece
     * mesmo após o aplicativo ser fechado.
     * 
     * Uso típico:
     * - Quando usuário acessa tela de configurações
     * - Quando precisa conectar a servidor diferente
     * - Durante setup inicial do aplicativo
     * 
     * @param host Endereço IP ou hostname do servidor (ex: "192.168.1.100" ou "servidor.local")
     * @param port Porta TCP do servidor (ex: 8080)
     */
    public void configurarServidor(String host, int port) {
        // Atualiza configurações em memória
        this.serverHost = host;
        this.serverPort = port;
        
        // Persiste configurações para uso futuro
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
             .putString(PREF_HOST, host)
             .putInt(PREF_PORT, port)
             .apply();
             
        Log.d(TAG, "Servidor configurado: " + host + ":" + port);
    }
    
    /**
     * Conecta ao servidor desktop de forma assíncrona
     * 
     * Estabelece uma conexão TCP/IP com o servidor especificado, executando
     * a operação em background para não bloquear a thread principal (UI).
     * 
     * Processo de conexão:
     * 1. Fecha qualquer conexão anterior existente
     * 2. Cria novo socket TCP
     * 3. Tenta conectar com timeout de 5 segundos
     * 4. Se bem-sucedido, cria streams de entrada/saída
     * 5. Marca como conectado e notifica via callback
     * 
     * Tratamento de erros:
     * - SocketTimeoutException: Servidor não responde (pode estar offline)
     * - IOException: Problemas de rede ou servidor recusou conexão
     * - Exception genérica: Outros erros inesperados
     * 
     * @param host Endereço IP ou hostname do servidor (ex: "192.168.1.100")
     * @param port Porta TCP do servidor (ex: 8080)
     * @param callback Interface para receber resultado da operação
     */
    public void conectar(String host, int port, ServerCallback<String> callback) {
        new AsyncTask<Void, Void, String>() {
            /** Mensagem de erro caso ocorra falha na conexão */
            private String errorMessage = null;
            
            /**
             * Executa conexão em background thread (fora da UI thread)
             * 
             * @param voids Parâmetros não utilizados
             * @return Mensagem de sucesso ou null em caso de erro
             */
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Fecha conexão anterior para evitar vazamento de sockets
                    disconnect();
                    
                    // Cria novo socket TCP
                    socket = new Socket();
                    
                    // Conecta com timeout para evitar bloqueio indefinido
                    socket.connect(new java.net.InetSocketAddress(host, port), CONNECTION_TIMEOUT);
                    
                    // Cria stream de entrada para ler respostas do servidor
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    // Cria stream de saída para enviar comandos (auto-flush ativado)
                    output = new PrintWriter(socket.getOutputStream(), true);
                    
                    // Marca como conectado
                    connected = true;
                    
                    return "Conectado ao servidor: " + host + ":" + port;
                    
                } catch (SocketTimeoutException e) {
                    // Timeout: servidor não respondeu no tempo esperado (5s)
                    errorMessage = "Timeout na conexão - Verifique se o servidor está rodando";
                    return null;
                } catch (IOException e) {
                    // Erro de I/O: servidor offline, porta fechada, firewall bloqueando, etc
                    errorMessage = "Erro de conexão: " + e.getMessage();
                    return null;
                } catch (Exception e) {
                    // Outros erros não previstos
                    errorMessage = "Erro inesperado: " + e.getMessage();
                    return null;
                }
            }
            
            /**
             * Executa na thread principal (UI thread) após conclusão do background
             * Permite atualização segura da interface com o resultado
             * 
             * @param result Resultado da operação (mensagem de sucesso ou null)
             */
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    // Sucesso: notifica callback com mensagem de confirmação
                    callback.onSuccess(result);
                } else {
                    // Erro: notifica callback com mensagem de erro
                    callback.onError(errorMessage);
                }
            }
        }.execute();
    }
    
    /**
     * Conecta ao servidor usando as configurações salvas
     * 
     * Método de conveniência que usa o host e porta configurados
     * previamente via configurarServidor() ou valores padrão se nenhuma
     * configuração foi feita.
     * 
     * @param callback Interface para receber resultado da conexão
     * @see #conectar(String, int, ServerCallback)
     * @see #configurarServidor(String, int)
     */
    public void conectar(ServerCallback<String> callback) {
        conectar(serverHost, serverPort, callback);
    }
    
    /**
     * Envia comando ao servidor e recebe resposta de forma assíncrona
     * 
     * Este é o método central de comunicação com o servidor. Todos os comandos
     * (login, listar contas, adicionar movimentação, etc) utilizam este método.
     * 
     * Processo de comunicação:
     * 1. Valida se há conexão ativa
     * 2. Valida estado do socket  
     * 3. Envia comando via PrintWriter
     * 4. Aguarda resposta via BufferedReader  
     * 5. Processa resposta e notifica via callback
     * 
     * Formato do comando:
     * - Comandos são strings no formato: "COMANDO|param1|param2|..."
     * - Exemplo: "LOGIN|user@email.com|senha_hash|mobile"
     * - O formato é gerenciado pela classe Protocol
     * 
     * Formato da resposta:
     * - Respostas seguem padrão: "STATUS|dados"
     * - Exemplo sucesso: "OK|1|João Silva|joao@email.com|usuario"
     * - Exemplo erro: "ERROR|Credenciais inválidas"
     * 
     * Validações realizadas:
     * - Verifica flag 'connected' antes de enviar
     * - Verifica se socket não foi fechado ou desconectado
     * - Detecta perda de conexão quando resposta é null
     * 
     * Logs gerados:
     * - Log DEBUG do comando enviado
     * - Log DEBUG da resposta recebida
     * - Log ERROR em caso de falha de I/O
     * 
     * @param comando String com o comando formatado (use Protocol.buildCommand())
     * @param callback Interface para receber resposta ou erro
     * @see Protocol#buildCommand(String, String...)
     */
    public void enviarComando(String comando, ServerCallback<String> callback) {
        new AsyncTask<String, Void, String>() {
            /** Mensagem de erro caso ocorra falha no envio */
            private String errorMessage = null;
            
            /**
             * Executa envio do comando em background thread
             * 
             * @param commands Array com o comando a ser enviado (índice 0)
             * @return Resposta do servidor ou null em caso de erro
             */
            @Override
            protected String doInBackground(String... commands) {
                String command = commands[0];
                
                // Validação 1: Verifica se está conectado
                if (!connected) {
                    errorMessage = "Não conectado ao servidor";
                    return null;
                }
                
                // Validação 2: Verifica estado do socket
                if (socket == null || socket.isClosed() || !socket.isConnected()) {
                    errorMessage = "Conexão inválida";
                    connected = false; // Atualiza flag de conexão
                    return null;
                }
                
                try {
                    // Envia comando ao servidor
                    output.println(command);
                    output.flush(); // Garante envio imediato do comando
                    
                    // Aguarda resposta do servidor (BLOQUEANTE)
                    String response = input.readLine();
                    
                    // Verifica se conexão foi perdida
                    if (response == null) {
                        errorMessage = "Conexão perdida com o servidor";
                        connected = false;
                        return null;
                    }
                    
                    // Logs de debug para acompanhamento
                    Log.d(TAG, "Comando enviado: " + command);
                    Log.d(TAG, "Resposta recebida: " + response);
                    
                    return response;
                    
                } catch (IOException e) {
                    // Erro de comunicação: conexão caiu, servidor fechou, timeout, etc
                    errorMessage = "Erro na comunicação: " + e.getMessage();
                    connected = false;
                    Log.e(TAG, "Erro IO ao enviar comando: " + e.getMessage());
                    return null;
                } catch (Exception e) {
                    // Outros erros não previstos
                    errorMessage = "Erro inesperado: " + e.getMessage();
                    Log.e(TAG, "Erro inesperado ao enviar comando: " + e.getMessage());
                    return null;
                }
            }
            
            /**
             * Executa na UI thread após conclusão do envio
             * 
             * @param result Resposta do servidor ou null
             */
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(errorMessage != null ? errorMessage : "Erro desconhecido");
                }
            }
        }.execute(comando);
    }
    
    /**
     * Realiza login do usuário no servidor desktop
     * 
     * Envia credenciais para o servidor e autentica o usuário. O servidor
     * valida as credenciais no banco de dados MySQL e retorna os dados do
     * usuário se autenticação for bem-sucedida.
     * 
     * Processo de login:
     * 1. Monta comando LOGIN com email, senha e tipo de cliente ("mobile")
     * 2. Envia comando ao servidor via enviarComando()
     * 3. Servidor valida credenciais no banco de dados
     * 4. Servidor retorna dados do usuário ou erro
     * 5. Processa resposta e notifica callback
     * 
     * Formato do comando enviado:
     * "LOGIN|email@exemplo.com|senha_criptografada|mobile"
     * 
     * Formato da resposta bem-sucedida:
     * "OK|id_usuario|nome_usuario|email_usuario|tipo_usuario"
     * Exemplo: "OK|1|João Silva|joao@email.com|usuario"
     * 
     * Formato da resposta de erro:
     * "ERROR|mensagem_erro"
     * Exemplo: "ERROR|Email ou senha inválidos"
     * 
     * Tipos de cliente:
     * - "mobile": Usuário comum via aplicativo Android
     * - "admin": Administrador via aplicativo Desktop
     * 
     * Importante:
     * - A senha já deve vir criptografada (SHA-256) antes de chamar este método
     * - A conexão deve estar estabelecida antes de chamar este método
     * - Em caso de sucesso, a conexão é mantida para sincronização de dados
     * 
     * @param email Email do usuário cadastrado
     * @param senha Senha já criptografada (SHA-256)
     * @param callback Interface para receber resultado (resposta completa do servidor)
     * @see Protocol#CMD_LOGIN
     * @see AuthManager#login(String, String, AuthCallback)
     */
    public void login(String email, String senha, ServerCallback<String> callback) {
        // Monta comando LOGIN incluindo tipo de cliente "mobile" para diferenciar de login admin
        String comando = Protocol.buildCommand(Protocol.CMD_LOGIN, email, senha, "mobile");
        
        // Envia comando ao servidor de forma assíncrona
        enviarComando(comando, new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Faz parse da resposta do servidor
                String[] partes = Protocol.parseCommand(result);
                
                // Verifica se login foi bem-sucedido
                if (partes.length > 0 && Protocol.STATUS_OK.equals(partes[0])) {
                    Log.d(TAG, "Login bem-sucedido, mantendo conexão para sincronização");
                    callback.onSuccess(result); // Retorna resposta completa para parsing posterior
                } else if (partes.length > 1) {
                    // Login falhou - retorna mensagem de erro do servidor
                    callback.onError(partes[1]);
                } else {
                    // Resposta malformada
                    callback.onError("Resposta inválida do servidor");
                }
            }
            
            @Override
            public void onError(String error) {
                // Propaga erro de comunicação
                callback.onError(error);
            }
        });
    }
    
    /**
     * Registra novo usuário no servidor desktop
     * 
     * Cria uma nova conta de usuário no sistema Finanza. O servidor
     * valida os dados, verifica se email já existe, criptografa a senha
     * e salva no banco de dados MySQL.
     * 
     * Processo de registro:
     * 1. Monta comando REGISTER com nome, email, senha e tipo ("mobile")
     * 2. Envia ao servidor via enviarComando()
     * 3. Servidor valida dados (email único, senha mínima 6 caracteres, etc)
     * 4. Servidor criptografa senha e salva usuário no banco
     * 5. Servidor retorna confirmação ou erro
     * 6. Callback recebe resultado
     * 
     * Formato do comando enviado:
     * "REGISTER|Nome Completo|email@exemplo.com|senha_texto|mobile"
     * 
     * Formato da resposta bem-sucedida:
     * "OK|id_usuario|nome_usuario|email_usuario|tipo_usuario"
     * Exemplo: "OK|5|Maria Santos|maria@email.com|usuario"
     * 
     * Possíveis erros retornados:
     * - "EMAIL_EXISTS|Email já cadastrado" - Email já existe no sistema
     * - "INVALID_DATA|Email inválido" - Formato de email incorreto
     * - "INVALID_DATA|Senha deve ter pelo menos 6 caracteres" - Senha muito curta
     * - "ERROR|Erro ao criar usuário" - Erro ao salvar no banco de dados
     * 
     * Validações realizadas pelo servidor:
     * - Email deve ser válido (conter @)
     * - Email deve ser único no sistema
     * - Senha deve ter no mínimo 6 caracteres
     * - Todos os campos são obrigatórios
     * 
     * Tipo de usuário criado:
     * - "mobile" cria usuário comum (não-admin)
     * - Usuários mobile têm acesso ao app Android
     * - Administradores são criados apenas via desktop
     * 
     * @param nome Nome completo do novo usuário
     * @param email Email único do usuário (usado para login)
     * @param senha Senha em texto plano (será criptografada pelo servidor)
     * @param callback Interface para receber resultado do registro
     * @see Protocol#CMD_REGISTER
     */
    public void registrar(String nome, String email, String senha, ServerCallback<String> callback) {
        // Monta comando REGISTER com tipo de cliente "mobile" para criar usuário comum (não-admin)
        String comando = Protocol.buildCommand(Protocol.CMD_REGISTER, nome, email, senha, "mobile");
        
        // Envia comando ao servidor de forma assíncrona
        enviarComando(comando, new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Faz parse da resposta do servidor
                String[] partes = Protocol.parseCommand(result);
                
                // Verifica se registro foi bem-sucedido
                if (partes.length > 0 && Protocol.STATUS_OK.equals(partes[0])) {
                    callback.onSuccess("Usuário registrado com sucesso");
                } else if (partes.length > 1) {
                    // Registro falhou - retorna mensagem de erro (ex: email já existe)
                    callback.onError(partes[1]);
                } else {
                    // Resposta malformada
                    callback.onError("Resposta inválida do servidor");
                }
            }
            
            @Override
            public void onError(String error) {
                // Propaga erro de comunicação
                callback.onError(error);
            }
        });
    }
    
    /**
     * Desconecta do servidor desktop e libera recursos
     * 
     * Fecha a conexão TCP/IP com o servidor e libera todos os recursos
     * associados (streams e socket). Deve ser chamado quando:
     * - Usuário faz logout
     * - Aplicativo é fechado
     * - Precisa reconectar a servidor diferente
     * - Houve erro irrecuperável na conexão
     * 
     * Recursos liberados:
     * - BufferedReader (input stream)
     * - PrintWriter (output stream)
     * - Socket TCP
     * 
     * Segurança:
     * - Trata exceções de I/O silenciosamente
     * - Sempre marca como desconectado mesmo se houver erro
     * - Pode ser chamado múltiplas vezes sem problema
     * 
     * @see #conectar(ServerCallback)
     */
    public void disconnect() {
        try {
            // Marca como desconectado primeiro para evitar envio durante fechamento
            connected = false;
            
            // Fecha stream de entrada
            if (input != null) input.close();
            
            // Fecha stream de saída
            if (output != null) output.close();
            
            // Fecha socket TCP
            if (socket != null) socket.close();
            
            Log.d(TAG, "Desconectado do servidor");
        } catch (IOException e) {
            // Log de erro, mas não propaga exceção
            Log.e(TAG, "Erro ao desconectar: " + e.getMessage());
        }
    }
    
    /**
     * Verifica se há conexão ativa com o servidor
     * 
     * Realiza verificação completa do estado da conexão, checando:
     * - Flag de conexão interna (connected)
     * - Existência do socket
     * - Estado de conexão do socket
     * - Socket não está fechado
     * 
     * Uso típico:
     * - Antes de tentar sincronizar dados
     * - Antes de enviar comandos importantes
     * - Para exibir status de conexão na UI
     * - Para decidir entre modo online/offline
     * 
     * @return true se conectado e pronto para comunicação, false caso contrário
     * @see #conectar(ServerCallback)
     * @see #disconnect()
     */
    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    /**
     * Testa conectividade com o servidor sem manter a conexão
     * 
     * Realiza um teste rápido de conexão para verificar se o servidor
     * está acessível e respondendo. A conexão é fechada imediatamente
     * após o teste.
     * 
     * Uso típico:
     * - Validar configurações de IP/porta antes de salvar
     * - Diagnosticar problemas de conexão
     * - Verificar disponibilidade do servidor
     * - Tela de configurações do app
     * 
     * Processo:
     * 1. Tenta conectar ao servidor configurado
     * 2. Se conectar, fecha imediatamente
     * 3. Retorna "Servidor acessível" via callback
     * 4. Se falhar, retorna erro detalhado
     * 
     * @param callback Interface para receber resultado do teste
     * @see #conectar(ServerCallback)
     */
    public void testarConexao(ServerCallback<String> callback) {
        conectar(new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Desconecta imediatamente após confirmar que conectou
                disconnect();
                callback.onSuccess("Servidor acessível");
            }
            
            @Override
            public void onError(String error) {
                // Propaga erro de conexão
                callback.onError(error);
            }
        });
    }
    
    /**
     * Lista todas as contas financeiras do usuário autenticado
     * 
     * Busca do servidor todas as contas (corrente, poupança, cartão,
     * investimento, dinheiro) pertencentes ao usuário logado.
     * 
     * Resposta esperada:
     * "OK|id1,nome1,tipo1,saldoInicial1,saldoAtual1|id2,nome2,tipo2,saldoInicial2,saldoAtual2|..."
     * 
     * @param callback Interface para receber lista de contas
     * @see Protocol#CMD_LIST_CONTAS
     */
    public void listarContas(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_CONTAS);
        enviarComando(comando, callback);
    }
    
    /**
     * Lista todas as categorias (receita e despesa) do usuário
     * 
     * Busca do servidor todas as categorias financeiras cadastradas
     * pelo usuário autenticado.
     * 
     * Resposta esperada:
     * "OK|id1,nome1,tipo1|id2,nome2,tipo2|..."
     * 
     * @param callback Interface para receber lista de categorias
     * @see Protocol#CMD_LIST_CATEGORIAS
     */
    public void listarCategorias(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_CATEGORIAS);
        enviarComando(comando, callback);
    }
    
    /**
     * Lista categorias filtradas por tipo (receita ou despesa)
     * 
     * Busca apenas categorias de um tipo específico. Útil para
     * preencher dropdowns ou listas filtradas na interface.
     * 
     * @param tipo "receita" ou "despesa"
     * @param callback Interface para receber lista filtrada de categorias
     * @see Protocol#CMD_LIST_CATEGORIAS_TIPO
     */
    public void listarCategoriasPorTipo(String tipo, ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_CATEGORIAS_TIPO, tipo);
        enviarComando(comando, callback);
    }
    
    /**
     * Lista todas as movimentações financeiras (lançamentos) do usuário
     * 
     * Busca do servidor todos os lançamentos (receitas e despesas)
     * registrados pelo usuário autenticado.
     * 
     * Resposta esperada:
     * "OK|id1,valorInt,valorDec,data,desc,tipo,contaId,catId|..."
     * 
     * @param callback Interface para receber lista de movimentações
     * @see Protocol#CMD_LIST_MOVIMENTACOES
     */
    public void listarMovimentacoes(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_LIST_MOVIMENTACOES);
        enviarComando(comando, callback);
    }
    
    /**
     * Obtém dados do perfil do usuário autenticado
     * 
     * Busca informações do usuário logado (id, nome, email).
     * 
     * Resposta esperada:
     * "OK|id|nome|email"
     * 
     * @param callback Interface para receber dados do perfil
     * @see Protocol#CMD_GET_PERFIL
     */
    public void obterPerfil(ServerCallback<String> callback) {
        String comando = Protocol.buildCommand(Protocol.CMD_GET_PERFIL);
        enviarComando(comando, callback);
    }
    
    /**
     * Solicita recuperação de senha por email
     * 
     * Envia solicitação ao servidor para resetar a senha do usuário.
     * O servidor gera nova senha temporária e envia por email
     * (ou retorna diretamente em modo de desenvolvimento).
     * 
     * Processo:
     * 1. Servidor verifica se email existe
     * 2. Gera senha temporária
     * 3. Atualiza senha no banco de dados
     * 4. Envia por email (ou retorna na resposta)
     * 
     * Segurança:
     * - Não revela se email existe no sistema
     * - Gera senha aleatória temporária
     * - Força usuário a trocar senha no próximo login
     * 
     * @param email Email do usuário cadastrado
     * @param callback Interface para receber resultado da solicitação
     * @see Protocol#CMD_RESET_PASSWORD
     */
    public void recuperarSenha(String email, ServerCallback<String> callback) {
        // Monta comando de reset de senha
        String comando = Protocol.buildCommand(Protocol.CMD_RESET_PASSWORD, email);
        
        // Envia ao servidor
        enviarComando(comando, new ServerCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Faz parse da resposta
                String[] partes = Protocol.parseCommand(result);
                
                if (partes.length > 0 && Protocol.STATUS_OK.equals(partes[0])) {
                    // Sucesso: extrai mensagem (pode conter senha temporária em dev)
                    String mensagem = partes.length > 1 ? partes[1] : "Instruções enviadas por email";
                    callback.onSuccess(mensagem);
                } else if (partes.length > 1) {
                    // Erro: retorna mensagem do servidor
                    callback.onError(partes[1]);
                } else {
                    // Resposta malformada
                    callback.onError("Resposta inválida do servidor");
                }
            }
            
            @Override
            public void onError(String error) {
                // Propaga erro de comunicação
                callback.onError(error);
            }
        });
    }
}