package server;

import util.DatabaseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * FinanzaServer - Servidor Principal do Sistema Finanza
 * 
 * Esta classe implementa o servidor TCP/IP que gerencia todas as conexões
 * de clientes (mobile Android e desktop admin) do sistema Finanza.
 * 
 * Funcionalidades:
 * - Escuta conexões na porta 8080
 * - Aceita múltiplos clientes simultaneamente
 * - Cria uma thread ClientHandler para cada cliente conectado
 * - Valida conexão com banco de dados MySQL antes de iniciar
 * - Suporta modo de teste (sem banco de dados)
 * - Inicializa tabelas do banco automaticamente
 * 
 * Arquitetura:
 * - Servidor aceita conexão → Cria nova thread ClientHandler
 * - ClientHandler processa comandos do cliente
 * - Múltiplos clientes podem estar conectados ao mesmo tempo
 * 
 * Protocolo de comunicação:
 * - TCP/IP via Sockets Java
 * - Porta padrão: 8080
 * - Formato de comandos definido em Protocol.java
 * 
 * Fluxo de inicialização:
 * 1. Testa conexão com MySQL
 * 2. Inicializa tabelas do banco
 * 3. Cria ServerSocket na porta 8080
 * 4. Entra em loop esperando conexões
 * 5. Para cada conexão, cria nova thread ClientHandler
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class FinanzaServer {
    /** Porta TCP onde o servidor escuta conexões */
    private static final int PORT = 8080;
    
    /** Socket do servidor que aceita conexões */
    private ServerSocket serverSocket;
    
    /** Flag que indica se servidor está em execução */
    private boolean running;
    
    /** Flag que indica se está em modo de teste (sem banco de dados) */
    private boolean testMode;
    
    /**
     * Construtor padrão - inicializa servidor em modo produção
     * 
     * Modo produção significa que o servidor irá:
     * - Conectar ao banco de dados MySQL
     * - Validar estrutura do banco
     * - Processar operações reais de clientes
     */
    public FinanzaServer() {
        this(false);
    }
    
    /**
     * Construtor com modo de teste
     * 
     * @param testMode true para modo de teste (sem banco), false para produção
     *                 
     * Modo de teste é útil para:
     * - Testes unitários e integração
     * - Desenvolvimento sem banco configurado
     * - Validação de protocolo de comunicação
     */
    public FinanzaServer(boolean testMode) {
        this.testMode = testMode;
    }
    
    /**
     * Inicia o servidor Finanza
     * 
     * Este método é responsável por toda a inicialização e execução do servidor.
     * 
     * Sequência de operações:
     * 1. VALIDAÇÃO DO BANCO DE DADOS
     *    - Testa conexão com MySQL (host, porta, credenciais)
     *    - Verifica se banco 'finanza_db' existe
     *    - Se falhar, exibe erro e encerra
     * 
     * 2. INICIALIZAÇÃO DO BANCO
     *    - Cria tabelas se não existirem (usuario, conta, categoria, movimentacao)
     *    - Aplica constraints e índices
     *    - Insere dados padrão se necessário (usuário admin)
     * 
     * 3. ABERTURA DO SERVIDOR
     *    - Cria ServerSocket na porta 8080
     *    - Marca servidor como running = true
     *    - Exibe mensagem de confirmação
     * 
     * 4. LOOP PRINCIPAL (aceitar conexões)
     *    - Bloqueia esperando cliente conectar (.accept())
     *    - Quando cliente conecta, cria nova thread ClientHandler
     *    - ClientHandler processa comandos do cliente em paralelo
     *    - Volta a esperar próximo cliente
     * 
     * 5. ENCERRAMENTO
     *    - Se running = false, sai do loop
     *    - Fecha ServerSocket
     *    - Libera recursos
     * 
     * Tratamento de erros:
     * - IOException na inicialização: Exibe erro e encerra
     * - IOException no accept: Exibe erro mas continua
     * - Finally: Sempre chama stop() para limpar recursos
     * 
     * Modo de teste:
     * - Pula validação e inicialização do banco
     * - Útil para testes sem MySQL configurado
     */
    public void start() {
        try {
            // ========== FASE 1: VALIDAÇÃO DO BANCO DE DADOS ==========
            // Testa conexão com banco de dados apenas se não estiver em modo de teste
            if (!testMode) {
                // Tenta conectar ao MySQL e executar query de teste
                if (!DatabaseUtil.testConnection()) {
                    System.err.println("Erro: Não foi possível conectar ao banco de dados");
                    System.err.println("Verifique se o MySQL está rodando e se o banco 'finanza_db' existe");
                    System.err.println("\nPara criar o banco, execute:");
                    System.err.println("  mysql -u root -p < database/script_inicial.sql");
                    return; // Encerra execução se não conectar
                }
                System.out.println("✓ Conexão com banco de dados OK");
                
                // ========== FASE 2: INICIALIZAÇÃO DO BANCO ==========
                // Inicializa as tabelas do banco de dados
                // - Cria tabelas se não existirem
                // - Aplica constraints e foreign keys
                // - Cria índices para performance
                DatabaseUtil.initializeDatabase();
                System.out.println("✓ Banco de dados inicializado");
            } else {
                // Modo de teste ativado - pula validações de banco
                System.out.println("⚠ Servidor iniciado em MODO DE TESTE (sem banco de dados)");
            }
            
            // ========== FASE 3: ABERTURA DO SERVIDOR ==========
            // Cria ServerSocket que escuta na porta 8080
            // - Porta 8080: Padrão para aplicações web/custom
            // - Se porta já estiver em uso, lança exception
            serverSocket = new ServerSocket(PORT);
            running = true; // Marca como em execução
            
            System.out.println("\n╔═══════════════════════════════════════════╗");
            System.out.println("║  Servidor Finanza iniciado na porta " + PORT + "  ║");
            System.out.println("║  Aguardando conexões de clientes...      ║");
            System.out.println("╚═══════════════════════════════════════════╝\n");
            
            // ========== FASE 4: LOOP PRINCIPAL ==========
            // Loop infinito que aceita conexões de clientes
            while (running) {
                try {
                    // BLOQUEIA aqui esperando cliente conectar
                    // Quando cliente conecta, retorna Socket do cliente
                    Socket clientSocket = serverSocket.accept();
                    
                    // Obtém endereço do cliente para log
                    String clientAddress = clientSocket.getRemoteSocketAddress().toString();
                    System.out.println("➤ Cliente conectado: " + clientAddress);
                    
                    // Cria nova thread para processar este cliente
                    // ClientHandler extends Thread, então .start() inicia a thread
                    // Cliente é processado em paralelo, servidor volta a aceitar novos
                    ClientHandler clientHandler = new ClientHandler(clientSocket, testMode);
                    clientHandler.start();
                    
                } catch (IOException e) {
                    // Erro ao aceitar conexão
                    // Se servidor ainda está running, foi erro temporário
                    // Se não está running, foi por causa do stop()
                    if (running) {
                        System.err.println("✗ Erro ao aceitar conexão: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            // Erro fatal ao iniciar servidor (porta ocupada, sem permissão, etc)
            System.err.println("✗ Erro FATAL ao iniciar servidor: " + e.getMessage());
            System.err.println("  Possíveis causas:");
            System.err.println("  - Porta 8080 já está em uso por outro programa");
            System.err.println("  - Sem permissão para abrir porta");
            System.err.println("  - Firewall bloqueando a porta");
        } finally {
            // ========== FASE 5: ENCERRAMENTO ==========
            // Sempre executa, mesmo se houver exception
            // Garante que recursos sejam liberados
            stop();
        }
    }
    
    /**
     * Encerra o servidor Finanza
     * 
     * Este método é responsável por encerrar graciosamente o servidor,
     * liberando todos os recursos e fechando conexões.
     * 
     * Sequência de encerramento:
     * 1. Marca running = false
     *    - Isso faz o loop while(running) parar
     *    - Novas conexões não serão mais aceitas
     * 
     * 2. Fecha o ServerSocket
     *    - Libera a porta 8080
     *    - Threads ClientHandler existentes continuam executando
     *    - Apenas novas conexões são impedidas
     * 
     * 3. Exibe mensagem de confirmação
     * 
     * Este método é chamado:
     * - Pelo bloco finally de start() (sempre executa)
     * - Manualmente para encerrar servidor (Ctrl+C, etc)
     * 
     * Nota: As threads ClientHandler ativas não são interrompidas,
     * elas continuam até terminar o processamento do cliente atual.
     */
    public void stop() {
        // Marca como não executando - para o loop principal
        running = false;
        
        try {
            // Verifica se ServerSocket existe e não está fechado
            if (serverSocket != null && !serverSocket.isClosed()) {
                // Fecha o socket, liberando a porta
                serverSocket.close();
                System.out.println("\n✓ Servidor encerrado com sucesso");
                System.out.println("  Porta 8080 liberada");
            }
        } catch (IOException e) {
            // Erro ao fechar socket (raro, mas possível)
            System.err.println("✗ Erro ao encerrar servidor: " + e.getMessage());
        }
    }
}