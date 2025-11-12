package com.example.finanza.network;

/**
 * Protocol - Protocolo de Comunicação Finanza
 * 
 * Define o protocolo de comunicação entre o aplicativo móvel Android
 * e o servidor desktop Java via sockets TCP. Suporta sincronização
 * bidirecional, resolução de conflitos e operações CRUD completas.
 * 
 * Arquitetura do Protocolo:
 * - Comandos separados por "|" (pipe)
 * - Campos separados por ";" (ponto e vírgula)
 * - Dados separados por "," (vírgula)
 * - Suporte a UUIDs para identificação única
 * - Timestamps para controle de versão
 * - Metadados para resolução de conflitos
 * 
 * Formato de Comando:
 * COMANDO|PARAMETRO1|PARAMETRO2|...
 * 
 * Formato de Resposta:
 * STATUS|DADOS (onde STATUS pode ser OK, ERROR, etc.)
 * 
 * Exemplos:
 * - LOGIN|usuario@email.com|senha123
 * - ADD_CONTA|Conta Corrente|corrente|1000.0
 * - OK|{"id": 1, "nome": "Conta Corrente"}
 * 
 * @author Finanza Team
 * @version 2.0
 * @since 2024
 */
public class Protocol {
    
    // ================== SEPARADORES DE COMANDO ==================
    
    /** Separador principal de comandos e parâmetros */
    public static final String COMMAND_SEPARATOR = "|";
    
    /** Separador de campos em listas de dados */
    public static final String FIELD_SEPARATOR = ";";
    
    /** Separador de dados individuais */
    public static final String DATA_SEPARATOR = ",";
    
    /** Separador para compatibilidade com código existente */
    public static final String SEPARATOR = "|";
    
    // ================== CÓDIGOS DE STATUS ==================
    
    /** Operação executada com sucesso */
    public static final String STATUS_OK = "OK";
    
    /** Erro genérico na operação */
    public static final String STATUS_ERROR = "ERROR";
    
    /** Dados fornecidos são inválidos */
    public static final String STATUS_INVALID_DATA = "INVALID_DATA";
    
    /** Credenciais de autenticação inválidas */
    public static final String STATUS_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    
    /** Usuário já existe no sistema */
    public static final String STATUS_USER_EXISTS = "USER_EXISTS";
    
    /** Recurso solicitado não foi encontrado */
    public static final String STATUS_NOT_FOUND = "NOT_FOUND";
    
    /** Conflito detectado durante sincronização */
    public static final String STATUS_CONFLICT = "CONFLICT";
    
    /** Registro duplicado detectado */
    public static final String STATUS_DUPLICATE = "DUPLICATE";
    
    // ================== COMANDOS DE AUTENTICAÇÃO ==================
    
    /** Comando para realizar login */
    public static final String CMD_LOGIN = "LOGIN";
    
    /** Comando para registrar novo usuário */
    public static final String CMD_REGISTER = "REGISTER";
    
    /** Comando para realizar logout */
    public static final String CMD_LOGOUT = "LOGOUT";
    
    /** Comando para obter dados do dashboard */
    public static final String CMD_GET_DASHBOARD = "GET_DASHBOARD";
    
    // ================== COMANDOS DE PERFIL ==================
    
    /** Comando para obter dados do perfil do usuário */
    public static final String CMD_GET_PERFIL = "GET_PERFIL";
    
    /** Comando para atualizar perfil do usuário */
    public static final String CMD_UPDATE_PERFIL = "UPDATE_PERFIL";
    
    /** Comando para alterar senha do usuário */
    public static final String CMD_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    
    /** Comando para recuperar senha por email */
    public static final String CMD_RESET_PASSWORD = "RESET_PASSWORD";
    
    // ================== COMANDOS DE CONTAS ==================
    
    /** Comando para listar todas as contas do usuário */
    public static final String CMD_LIST_CONTAS = "LIST_CONTAS";
    
    /** Comando para adicionar nova conta */
    public static final String CMD_ADD_CONTA = "ADD_CONTA";
    
    /** Comando para atualizar conta existente */
    public static final String CMD_UPDATE_CONTA = "UPDATE_CONTA";
    
    /** Comando para excluir conta */
    public static final String CMD_DELETE_CONTA = "DELETE_CONTA";
    
    // ================== COMANDOS DE CATEGORIAS ==================
    
    /** Comando para listar todas as categorias */
    public static final String CMD_LIST_CATEGORIAS = "LIST_CATEGORIAS";
    
    /** Comando para listar categorias por tipo (receita/despesa) */
    public static final String CMD_LIST_CATEGORIAS_TIPO = "LIST_CATEGORIAS_TIPO";
    
    /** Comando para adicionar nova categoria */
    public static final String CMD_ADD_CATEGORIA = "ADD_CATEGORIA";
    
    /** Comando para atualizar categoria existente */
    public static final String CMD_UPDATE_CATEGORIA = "UPDATE_CATEGORIA";
    
    /** Comando para excluir categoria */
    public static final String CMD_DELETE_CATEGORIA = "DELETE_CATEGORIA";
    
    // ================== COMANDOS DE MOVIMENTAÇÕES/TRANSAÇÕES ==================
    
    /** Comando para listar todas as movimentações (lançamentos) do usuário */
    public static final String CMD_LIST_MOVIMENTACOES = "LIST_MOVIMENTACOES";
    
    /** Comando para listar movimentações filtradas por período de data */
    public static final String CMD_LIST_MOVIMENTACOES_PERIODO = "LIST_MOVIMENTACOES_PERIODO";
    
    /** Comando para listar movimentações filtradas por conta específica */
    public static final String CMD_LIST_MOVIMENTACOES_CONTA = "LIST_MOVIMENTACOES_CONTA";
    
    /** Comando para adicionar nova movimentação financeira */
    public static final String CMD_ADD_MOVIMENTACAO = "ADD_MOVIMENTACAO";
    
    /** Comando para atualizar movimentação existente */
    public static final String CMD_UPDATE_MOVIMENTACAO = "UPDATE_MOVIMENTACAO";
    
    /** Comando para excluir movimentação */
    public static final String CMD_DELETE_MOVIMENTACAO = "DELETE_MOVIMENTACAO";
    
    // ================== COMANDOS DE SINCRONIZAÇÃO AVANÇADA ==================
    
    /** Comando para verificar status de sincronização do servidor */
    public static final String CMD_SYNC_STATUS = "SYNC_STATUS";
    
    /** Comando para executar sincronização incremental (apenas alterações) */
    public static final String CMD_INCREMENTAL_SYNC = "INCREMENTAL_SYNC";
    
    /** Comando para listar mudanças no servidor desde um timestamp específico */
    public static final String CMD_LIST_CHANGES_SINCE = "LIST_CHANGES_SINCE";
    
    /** Comando para resolver conflito de sincronização */
    public static final String CMD_RESOLVE_CONFLICT = "RESOLVE_CONFLICT";
    
    /** Comando para enviar múltiplos registros de uma vez (otimização) */
    public static final String CMD_BULK_UPLOAD = "BULK_UPLOAD";
    
    /** Comando para verificar integridade dos dados sincronizados */
    public static final String CMD_VERIFY_INTEGRITY = "VERIFY_INTEGRITY";
    
    // ================== COMANDOS AVANÇADOS - CATEGORIAS COM UUID ==================
    
    /** Comando para adicionar categoria com suporte a UUID e metadados de sinc */
    public static final String CMD_ADD_CATEGORIA_ENHANCED = "ADD_CATEGORIA_ENHANCED";
    
    /** Comando para atualizar categoria com controle de versão e conflitos */
    public static final String CMD_UPDATE_CATEGORIA_ENHANCED = "UPDATE_CATEGORIA_ENHANCED";
    
    /** Comando para sincronizar categoria específica usando UUID */
    public static final String CMD_SYNC_CATEGORIA = "SYNC_CATEGORIA";
    
    // ================== COMANDOS AVANÇADOS - CONTAS COM UUID ==================
    
    /** Comando para adicionar conta com suporte a UUID e metadados de sinc */
    public static final String CMD_ADD_CONTA_ENHANCED = "ADD_CONTA_ENHANCED";
    
    /** Comando para atualizar conta com controle de versão e conflitos */
    public static final String CMD_UPDATE_CONTA_ENHANCED = "UPDATE_CONTA_ENHANCED";
    
    /** Comando para sincronizar conta específica usando UUID */
    public static final String CMD_SYNC_CONTA = "SYNC_CONTA";
    
    // ================== COMANDOS AVANÇADOS - MOVIMENTAÇÕES COM UUID ==================
    
    /** Comando para adicionar movimentação com suporte a UUID e metadados de sinc */
    public static final String CMD_ADD_MOVIMENTACAO_ENHANCED = "ADD_MOVIMENTACAO_ENHANCED";
    
    /** Comando para atualizar movimentação com controle de versão e conflitos */
    public static final String CMD_UPDATE_MOVIMENTACAO_ENHANCED = "UPDATE_MOVIMENTACAO_ENHANCED";
    
    /** Comando para sincronizar movimentação específica usando UUID */
    public static final String CMD_SYNC_MOVIMENTACAO = "SYNC_MOVIMENTACAO";
    
    /**
     * Constrói string de comando com múltiplos parâmetros
     * 
     * Monta um comando no formato: COMANDO|PARAM1|PARAM2|...
     * Trata valores null como strings vazias para evitar erros.
     * 
     * @param command Nome do comando (ex: "LOGIN", "ADD_CONTA")
     * @param params Parâmetros variáveis do comando
     * @return String formatada do comando
     * 
     * @example
     * buildCommand("LOGIN", "user@email.com", "senha123")
     * // Retorna: "LOGIN|user@email.com|senha123"
     */
    public static String buildCommand(String command, String... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(command);
        
        for (String param : params) {
            sb.append(SEPARATOR);
            sb.append(param != null ? param : "");
        }
        
        return sb.toString();
    }
    
    /**
     * Analisa string de comando em componentes separados
     * 
     * Separa um comando completo em suas partes constituintes
     * usando o separador principal (|).
     * 
     * @param command String do comando completo
     * @return Array de strings com os componentes
     * 
     * @example
     * parseCommand("LOGIN|user@email.com|senha123")
     * // Retorna: ["LOGIN", "user@email.com", "senha123"]
     */
    public static String[] parseCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return new String[0];
        }
        return command.split("\\" + SEPARATOR);
    }
    
    /**
     * Analisa string de campos em componentes (para listas de dados)
     * 
     * Utilizado para separar campos em listas de dados usando
     * o separador de campos (;).
     * 
     * @param fields String com campos separados por ";"
     * @return Array de strings com os campos individuais
     */
    public static String[] parseFields(String fields) {
        if (fields == null || fields.trim().isEmpty()) {
            return new String[0];
        }
        return fields.split(FIELD_SEPARATOR);
    }
    
    /**
     * Analisa string de dados em componentes (para registros individuais)
     * 
     * Utilizado para separar dados dentro de um registro usando
     * o separador de dados (,).
     * 
     * @param data String com dados separados por ","
     * @return Array de strings com os dados individuais
     */
    public static String[] parseData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new String[0];
        }
        return data.split(DATA_SEPARATOR);
    }
    
    /**
     * Cria resposta de sucesso padronizada
     * 
     * @param data Dados a serem incluídos na resposta (opcional)
     * @return String formatada: "OK|dados"
     */
    public static String createSuccessResponse(String data) {
        return STATUS_OK + SEPARATOR + (data != null ? data : "");
    }
    
    /**
     * Cria resposta de erro padronizada
     * 
     * @param message Mensagem de erro
     * @return String formatada: "ERROR|mensagem"
     */
    public static String createErrorResponse(String message) {
        return STATUS_ERROR + SEPARATOR + (message != null ? message : "Unknown error");
    }
    
    /**
     * Cria resposta com status específico
     * 
     * @param status Código de status (ex: OK, ERROR, CONFLICT)
     * @param data Dados da resposta
     * @return String formatada: "STATUS|dados"
     */
    public static String createResponse(String status, String data) {
        return status + SEPARATOR + (data != null ? data : "");
    }
    
    /**
     * Constrói comando de categoria com UUID e metadados (versão aprimorada)
     * 
     * Usado para sincronização avançada com controle de versão e UUIDs.
     * 
     * @param uuid Identificador único da categoria
     * @param nome Nome da categoria
     * @param tipo Tipo da categoria (receita/despesa)
     * @param cor Cor da categoria (código hex)
     * @param lastModified Timestamp da última modificação
     * @return Comando formatado para envio ao servidor
     */
    public static String buildCategoriaEnhanced(String uuid, String nome, String tipo, String cor, long lastModified) {
        return buildCommand(CMD_ADD_CATEGORIA_ENHANCED, uuid, nome, tipo, cor, String.valueOf(lastModified));
    }
    
    /**
     * Constrói comando de conta com UUID e metadados (versão aprimorada)
     * 
     * @param uuid Identificador único da conta
     * @param nome Nome da conta
     * @param tipo Tipo da conta (corrente, poupanca, etc.)
     * @param saldoInicial Saldo inicial da conta
     * @param lastModified Timestamp da última modificação
     * @return Comando formatado para envio ao servidor
     */
    public static String buildContaEnhanced(String uuid, String nome, String tipo, double saldoInicial, long lastModified) {
        return buildCommand(CMD_ADD_CONTA_ENHANCED, uuid, nome, tipo, String.valueOf(saldoInicial), String.valueOf(lastModified));
    }
    
    /**
     * Constrói comando de movimentação com UUID e metadados (versão aprimorada)
     * 
     * @param uuid Identificador único da movimentação
     * @param valor Valor da movimentação
     * @param data Data da movimentação (timestamp)
     * @param descricao Descrição da movimentação
     * @param tipo Tipo da movimentação (receita/despesa)
     * @param contaId ID da conta associada
     * @param categoriaId ID da categoria associada
     * @param lastModified Timestamp da última modificação
     * @param isDeleted Flag indicando se foi excluída (soft delete)
     * @return Comando formatado para envio ao servidor
     */
    public static String buildMovimentacaoEnhanced(String uuid, double valor, long data, String descricao, 
                                                  String tipo, int contaId, int categoriaId, long lastModified, boolean isDeleted) {
        return buildCommand(CMD_ADD_MOVIMENTACAO_ENHANCED, uuid, String.valueOf(valor), String.valueOf(data), 
                          descricao, tipo, String.valueOf(contaId), String.valueOf(categoriaId), 
                          String.valueOf(lastModified), String.valueOf(isDeleted));
    }
    
    /**
     * Build incremental sync command
     */
    public static String buildIncrementalSyncCommand(String entityType, long sinceTimestamp) {
        return buildCommand(CMD_LIST_CHANGES_SINCE, entityType, String.valueOf(sinceTimestamp));
    }
    
    /**
     * Build conflict resolution command
     */
    public static String buildConflictResolutionCommand(String entityType, String uuid, String resolution) {
        return buildCommand(CMD_RESOLVE_CONFLICT, entityType, uuid, resolution);
    }
    
    /**
     * Build bulk upload command for efficient batch operations
     */
    public static String buildBulkUploadCommand(String entityType, String dataJson) {
        return buildCommand(CMD_BULK_UPLOAD, entityType, dataJson);
    }
    
    /**
     * Format categoria data for transmission (with metadata)
     */
    public static String formatCategoriaData(String uuid, String nome, String tipo, String cor, 
                                           long lastModified, int syncStatus, String serverHash) {
        return String.join(DATA_SEPARATOR, 
            uuid != null ? uuid : "",
            nome != null ? nome : "",
            tipo != null ? tipo : "",
            cor != null ? cor : "#666666",
            String.valueOf(lastModified),
            String.valueOf(syncStatus),
            serverHash != null ? serverHash : ""
        );
    }
    
    /**
     * Format conta data for transmission (with metadata)
     */
    public static String formatContaData(String uuid, String nome, String tipo, double saldoInicial,
                                       long lastModified, int syncStatus, String serverHash) {
        return String.join(DATA_SEPARATOR,
            uuid != null ? uuid : "",
            nome != null ? nome : "",
            tipo != null ? tipo : "",
            String.valueOf(saldoInicial),
            String.valueOf(lastModified),
            String.valueOf(syncStatus),
            serverHash != null ? serverHash : ""
        );
    }
    
    /**
     * Format movimentacao data for transmission (with metadata)
     */
    public static String formatMovimentacaoData(String uuid, double valor, long data, String descricao,
                                              String tipo, int contaId, int categoriaId, int usuarioId,
                                              long lastModified, int syncStatus, String serverHash, boolean isDeleted) {
        return String.join(DATA_SEPARATOR,
            uuid != null ? uuid : "",
            String.valueOf(valor),
            String.valueOf(data),
            descricao != null ? descricao : "",
            tipo != null ? tipo : "",
            String.valueOf(contaId),
            String.valueOf(categoriaId),
            String.valueOf(usuarioId),
            String.valueOf(lastModified),
            String.valueOf(syncStatus),
            serverHash != null ? serverHash : "",
            String.valueOf(isDeleted)
        );
    }
    
    /**
     * Validate command format
     */
    public static boolean isValidCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = parseCommand(command);
        return parts.length > 0 && !parts[0].trim().isEmpty();
    }
    
    /**
     * Extract command type from command string
     */
    public static String extractCommandType(String command) {
        if (!isValidCommand(command)) {
            return "";
        }
        
        String[] parts = parseCommand(command);
        return parts[0].trim();
    }
    
    /**
     * Check if response indicates success
     */
    public static boolean isSuccessResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = parseCommand(response);
        return parts.length > 0 && STATUS_OK.equals(parts[0]);
    }
    
    /**
     * Extract data from successful response
     */
    public static String extractResponseData(String response) {
        if (!isSuccessResponse(response)) {
            return "";
        }
        
        String[] parts = parseCommand(response);
        return parts.length > 1 ? parts[1] : "";
    }
    
    /**
     * Extract error message from error response
     */
    public static String extractErrorMessage(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "Unknown error";
        }
        
        String[] parts = parseCommand(response);
        if (parts.length > 0 && STATUS_ERROR.equals(parts[0])) {
            return parts.length > 1 ? parts[1] : "Unknown error";
        }
        
        return "Invalid response format";
    }
    
    /**
     * Generate checksum for data integrity verification
     */
    public static String generateChecksum(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        
        // Simple hash-based checksum (in production, use more robust hashing)
        int hash = data.hashCode();
        return String.valueOf(Math.abs(hash));
    }
    
    /**
     * Verify data integrity using checksum
     */
    public static boolean verifyChecksum(String data, String expectedChecksum) {
        if (data == null || expectedChecksum == null) {
            return false;
        }
        
        String actualChecksum = generateChecksum(data);
        return actualChecksum.equals(expectedChecksum);
    }
}