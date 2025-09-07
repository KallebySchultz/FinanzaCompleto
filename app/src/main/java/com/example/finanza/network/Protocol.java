package com.example.finanza.network;

/**
 * Enhanced Protocol for bidirectional synchronization between mobile and desktop
 * Supports UUIDs, timestamps, and metadata for conflict resolution
 */
public class Protocol {
    
    // Command separators
    public static final String COMMAND_SEPARATOR = "|";
    public static final String FIELD_SEPARATOR = ";";
    public static final String DATA_SEPARATOR = ",";
    
    // For compatibility with existing code
    public static final String SEPARATOR = "|";
    
    // Response status codes
    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_INVALID_DATA = "INVALID_DATA";
    public static final String STATUS_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String STATUS_USER_EXISTS = "USER_EXISTS";
    public static final String STATUS_NOT_FOUND = "NOT_FOUND";
    public static final String STATUS_CONFLICT = "CONFLICT";
    public static final String STATUS_DUPLICATE = "DUPLICATE";
    
    // Authentication commands
    public static final String CMD_LOGIN = "LOGIN";
    public static final String CMD_REGISTER = "REGISTER";
    public static final String CMD_LOGOUT = "LOGOUT";
    public static final String CMD_GET_DASHBOARD = "GET_DASHBOARD";
    
    // User profile commands
    public static final String CMD_GET_PERFIL = "GET_PERFIL";
    public static final String CMD_UPDATE_PERFIL = "UPDATE_PERFIL";
    public static final String CMD_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    
    // Account commands
    public static final String CMD_LIST_CONTAS = "LIST_CONTAS";
    public static final String CMD_ADD_CONTA = "ADD_CONTA";
    public static final String CMD_UPDATE_CONTA = "UPDATE_CONTA";
    public static final String CMD_DELETE_CONTA = "DELETE_CONTA";
    
    // Category commands
    public static final String CMD_LIST_CATEGORIAS = "LIST_CATEGORIAS";
    public static final String CMD_LIST_CATEGORIAS_TIPO = "LIST_CATEGORIAS_TIPO";
    public static final String CMD_ADD_CATEGORIA = "ADD_CATEGORIA";
    public static final String CMD_UPDATE_CATEGORIA = "UPDATE_CATEGORIA";
    public static final String CMD_DELETE_CATEGORIA = "DELETE_CATEGORIA";
    
    // Transaction/Movement commands
    public static final String CMD_LIST_MOVIMENTACOES = "LIST_MOVIMENTACOES";
    public static final String CMD_LIST_MOVIMENTACOES_PERIODO = "LIST_MOVIMENTACOES_PERIODO";
    public static final String CMD_LIST_MOVIMENTACOES_CONTA = "LIST_MOVIMENTACOES_CONTA";
    public static final String CMD_ADD_MOVIMENTACAO = "ADD_MOVIMENTACAO";
    public static final String CMD_UPDATE_MOVIMENTACAO = "UPDATE_MOVIMENTACAO";
    public static final String CMD_DELETE_MOVIMENTACAO = "DELETE_MOVIMENTACAO";
    
    // Enhanced sync commands for incremental and conflict resolution
    public static final String CMD_SYNC_STATUS = "SYNC_STATUS";
    public static final String CMD_INCREMENTAL_SYNC = "INCREMENTAL_SYNC";
    public static final String CMD_LIST_CHANGES_SINCE = "LIST_CHANGES_SINCE";
    public static final String CMD_RESOLVE_CONFLICT = "RESOLVE_CONFLICT";
    public static final String CMD_BULK_UPLOAD = "BULK_UPLOAD";
    public static final String CMD_VERIFY_INTEGRITY = "VERIFY_INTEGRITY";
    
    // Enhanced category commands with UUID support
    public static final String CMD_ADD_CATEGORIA_ENHANCED = "ADD_CATEGORIA_ENHANCED";
    public static final String CMD_UPDATE_CATEGORIA_ENHANCED = "UPDATE_CATEGORIA_ENHANCED";
    public static final String CMD_SYNC_CATEGORIA = "SYNC_CATEGORIA";
    
    // Enhanced account commands with UUID support
    public static final String CMD_ADD_CONTA_ENHANCED = "ADD_CONTA_ENHANCED";
    public static final String CMD_UPDATE_CONTA_ENHANCED = "UPDATE_CONTA_ENHANCED";
    public static final String CMD_SYNC_CONTA = "SYNC_CONTA";
    
    // Enhanced transaction commands with UUID support
    public static final String CMD_ADD_MOVIMENTACAO_ENHANCED = "ADD_MOVIMENTACAO_ENHANCED";
    public static final String CMD_UPDATE_MOVIMENTACAO_ENHANCED = "UPDATE_MOVIMENTACAO_ENHANCED";
    public static final String CMD_SYNC_MOVIMENTACAO = "SYNC_MOVIMENTACAO";
    public static final String CMD_DELETE_MOVIMENTACAO_SOFT = "DELETE_MOVIMENTACAO_SOFT";
    
    /**
     * Build command string with multiple parameters
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
     * Parse command string into components
     */
    public static String[] parseCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return new String[0];
        }
        return command.split("\\" + SEPARATOR);
    }
    
    /**
     * Parse field string into components (for data lists)
     */
    public static String[] parseFields(String fields) {
        if (fields == null || fields.trim().isEmpty()) {
            return new String[0];
        }
        return fields.split(FIELD_SEPARATOR);
    }
    
    /**
     * Parse data string into components (for individual records)
     */
    public static String[] parseData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new String[0];
        }
        return data.split(DATA_SEPARATOR);
    }
    
    /**
     * Create success response
     */
    public static String createSuccessResponse(String data) {
        return STATUS_OK + SEPARATOR + (data != null ? data : "");
    }
    
    /**
     * Create error response
     */
    public static String createErrorResponse(String message) {
        return STATUS_ERROR + SEPARATOR + (message != null ? message : "Unknown error");
    }
    
    /**
     * Create response with specific status
     */
    public static String createResponse(String status, String data) {
        return status + SEPARATOR + (data != null ? data : "");
    }
    
    /**
     * Build enhanced categoria command with UUID and metadata
     */
    public static String buildCategoriaEnhanced(String uuid, String nome, String tipo, String cor, long lastModified) {
        return buildCommand(CMD_ADD_CATEGORIA_ENHANCED, uuid, nome, tipo, cor, String.valueOf(lastModified));
    }
    
    /**
     * Build enhanced conta command with UUID and metadata
     */
    public static String buildContaEnhanced(String uuid, String nome, String tipo, double saldoInicial, long lastModified) {
        return buildCommand(CMD_ADD_CONTA_ENHANCED, uuid, nome, tipo, String.valueOf(saldoInicial), String.valueOf(lastModified));
    }
    
    /**
     * Build enhanced movimentacao command with UUID and metadata
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