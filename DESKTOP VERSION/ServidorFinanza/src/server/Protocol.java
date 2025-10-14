package server;

/**
 * Protocolo de comunicação entre cliente e servidor
 */
public class Protocol {
    
    // Comandos do protocolo
    public static final String CMD_LOGIN = "LOGIN";
    public static final String CMD_REGISTER = "REGISTER";
    public static final String CMD_LOGOUT = "LOGOUT";
    public static final String CMD_GET_DASHBOARD = "GET_DASHBOARD";
    
    // Comandos de Conta
    public static final String CMD_LIST_CONTAS = "LIST_CONTAS";
    public static final String CMD_ADD_CONTA = "ADD_CONTA";
    public static final String CMD_UPDATE_CONTA = "UPDATE_CONTA";
    public static final String CMD_DELETE_CONTA = "DELETE_CONTA";
    
    // Comandos de Categoria
    public static final String CMD_LIST_CATEGORIAS = "LIST_CATEGORIAS";
    public static final String CMD_LIST_CATEGORIAS_TIPO = "LIST_CATEGORIAS_TIPO";
    public static final String CMD_ADD_CATEGORIA = "ADD_CATEGORIA";
    public static final String CMD_UPDATE_CATEGORIA = "UPDATE_CATEGORIA";
    public static final String CMD_DELETE_CATEGORIA = "DELETE_CATEGORIA";
    
    // Comandos de Movimentação
    public static final String CMD_LIST_MOVIMENTACOES = "LIST_MOVIMENTACOES";
    public static final String CMD_LIST_MOVIMENTACOES_PERIODO = "LIST_MOVIMENTACOES_PERIODO";
    public static final String CMD_LIST_MOVIMENTACOES_CONTA = "LIST_MOVIMENTACOES_CONTA";
    public static final String CMD_ADD_MOVIMENTACAO = "ADD_MOVIMENTACAO";
    public static final String CMD_UPDATE_MOVIMENTACAO = "UPDATE_MOVIMENTACAO";
    public static final String CMD_DELETE_MOVIMENTACAO = "DELETE_MOVIMENTACAO";
    
    // Comandos de Perfil e Usuário
    public static final String CMD_GET_PERFIL = "GET_PERFIL";
    public static final String CMD_UPDATE_PERFIL = "UPDATE_PERFIL";
    public static final String CMD_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    public static final String CMD_RESET_PASSWORD = "RESET_PASSWORD";
    public static final String CMD_LIST_USERS = "LIST_USERS";
    public static final String CMD_UPDATE_USER = "UPDATE_USER";
    public static final String CMD_UPDATE_USER_PASSWORD = "UPDATE_USER_PASSWORD";
    
    // Status de resposta
    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String STATUS_USER_EXISTS = "USER_EXISTS";
    public static final String STATUS_INVALID_DATA = "INVALID_DATA";
    
    // Separadores
    public static final String SEPARATOR = "|";
    public static final String FIELD_SEPARATOR = ";";
    
    /**
     * Cria mensagem de resposta de sucesso
     */
    public static String createSuccessResponse(String data) {
        return STATUS_OK + SEPARATOR + (data != null ? data : "");
    }
    
    /**
     * Cria mensagem de resposta de erro
     */
    public static String createErrorResponse(String errorMessage) {
        return STATUS_ERROR + SEPARATOR + errorMessage;
    }
    
    /**
     * Cria mensagem de resposta com status específico
     */
    public static String createResponse(String status, String data) {
        return status + SEPARATOR + (data != null ? data : "");
    }
    
    /**
     * Parseia comando recebido do cliente
     */
    public static String[] parseCommand(String message) {
        if (message == null || message.trim().isEmpty()) {
            return new String[0];
        }
        return message.split("\\" + SEPARATOR);
    }
    
    /**
     * Parseia dados de campos separados
     */
    public static String[] parseFields(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new String[0];
        }
        return data.split(FIELD_SEPARATOR);
    }
    
    /**
     * Constrói comando com parâmetros
     */
    public static String buildCommand(String command, String... params) {
        StringBuilder sb = new StringBuilder(command);
        for (String param : params) {
            sb.append(SEPARATOR).append(param != null ? param : "");
        }
        return sb.toString();
    }
}