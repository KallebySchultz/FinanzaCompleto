import server.FinanzaServer;

/**
 * MainServidor - Classe Principal do Servidor Finanza
 * 
 * Esta é a classe de entrada (entry point) do servidor Finanza.
 * Responsável por inicializar e executar o servidor de forma standalone.
 * 
 * Funcionalidades:
 * - Inicia o servidor Finanza na porta 8080
 * - Configura shutdown hook para encerramento gracioso
 * - Suporta modo de teste via argumento de linha de comando
 * - Trata exceções fatais durante execução
 * 
 * Uso:
 * - Modo produção: java MainServidor
 * - Modo teste:    java MainServidor --test
 * 
 * Shutdown Hook:
 * - Captura sinais de encerramento (Ctrl+C, kill, etc)
 * - Garante que servidor seja encerrado corretamente
 * - Fecha conexões e libera recursos
 * 
 * Fluxo de execução:
 * 1. Exibe banner de inicialização
 * 2. Verifica argumentos de linha de comando
 * 3. Cria instância do FinanzaServer
 * 4. Registra shutdown hook
 * 5. Inicia servidor (método bloqueante)
 * 6. Se houver erro, exibe e encerra
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class MainServidor {
    
    /**
     * Método principal - ponto de entrada da aplicação servidor
     * 
     * @param args Argumentos de linha de comando
     *             --test: Inicia em modo de teste (sem banco de dados)
     *             
     * Modo de teste é útil para:
     * - Desenvolvimento sem MySQL configurado
     * - Testes automatizados
     * - Validação do protocolo de comunicação
     * 
     * Exemplo de uso:
     *   java MainServidor           → Modo produção
     *   java MainServidor --test    → Modo teste
     */
    public static void main(String[] args) {
        // ========== BANNER DE INICIALIZAÇÃO ==========
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  FINANZA - Sistema de Controle         ║");
        System.out.println("║  Financeiro Pessoal                    ║");
        System.out.println("║  Servidor Backend (Java + MySQL)       ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Desenvolvido por: Kalleby Schultz");
        System.out.println("IFSUL - Campus Venâncio Aires");
        System.out.println("Trabalho Interdisciplinar - 2024\n");
        
        // ========== VERIFICAÇÃO DE ARGUMENTOS ==========
        // Verifica se foi passado argumento --test na linha de comando
        // args é um array de strings com os argumentos
        // args[0] é o primeiro argumento (se houver)
        boolean testMode = args.length > 0 && "--test".equals(args[0]);
        
        if (testMode) {
            System.out.println("⚠ MODO DE TESTE ATIVADO");
            System.out.println("  - Banco de dados desabilitado");
            System.out.println("  - Apenas validação de protocolo\n");
        }
        
        // ========== CRIAÇÃO DO SERVIDOR ==========
        // Instancia o servidor Finanza
        // testMode=true: Sem banco de dados
        // testMode=false: Modo produção completo
        FinanzaServer server = new FinanzaServer(testMode);
        
        // ========== CONFIGURAÇÃO DE SHUTDOWN HOOK ==========
        // Shutdown Hook é executado quando a JVM está sendo encerrada
        // Captura sinais de encerramento:
        // - Ctrl+C no terminal
        // - kill <pid> no Linux/Mac
        // - Encerramento normal da JVM
        // 
        // Garante que:
        // - Servidor seja encerrado graciosamente
        // - Porta 8080 seja liberada
        // - Recursos sejam fechados corretamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║  Sinal de encerramento recebido        ║");
            System.out.println("║  Encerrando servidor...                ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            // Chama método stop() do servidor
            server.stop();
            
            System.out.println("Servidor encerrado com sucesso!");
            System.out.println("Até logo! 👋");
        }));
        
        // ========== INICIALIZAÇÃO DO SERVIDOR ==========
        try {
            // Inicia o servidor (método bloqueante)
            // Só retorna se houver erro ou servidor for encerrado
            // Loop infinito dentro de server.start()
            server.start();
            
        } catch (Exception e) {
            // Captura qualquer exceção não tratada
            // Exibe mensagem de erro detalhada
            System.err.println("\n╔════════════════════════════════════════╗");
            System.err.println("║  ERRO FATAL NO SERVIDOR                ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("Mensagem: " + e.getMessage());
            System.err.println("\nStack trace completo:");
            e.printStackTrace();
            
            System.err.println("\nPossíveis soluções:");
            System.err.println("1. Verifique se a porta 8080 está livre");
            System.err.println("2. Verifique se o MySQL está rodando");
            System.err.println("3. Verifique as credenciais do banco");
            System.err.println("4. Verifique os logs acima para mais detalhes");
            
            // Encerra com código de erro
            System.exit(1);
        }
    }
}