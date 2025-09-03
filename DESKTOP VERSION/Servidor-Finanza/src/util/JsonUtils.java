package util;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonUtils - Simple JSON utility for server communication
 * Basic JSON parsing and creation without external dependencies
 */
public class JsonUtils {
    
    /**
     * Parse a simple JSON string into a Map
     * This is a basic parser for simple JSON objects
     */
    public static Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();
        
        if (json == null || json.trim().isEmpty()) {
            return result;
        }
        
        // Remove surrounding braces
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        }
        
        // Split by commas (simple approach)
        String[] pairs = json.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim();
                
                // Remove quotes and parse value
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    // String value
                    value = value.substring(1, value.length() - 1);
                    result.put(key, value);
                } else if (value.equals("true") || value.equals("false")) {
                    // Boolean value
                    result.put(key, Boolean.parseBoolean(value));
                } else if (value.matches("-?\\d+")) {
                    // Integer value
                    result.put(key, Integer.parseInt(value));
                } else if (value.matches("-?\\d+\\.\\d+")) {
                    // Double value
                    result.put(key, Double.parseDouble(value));
                } else {
                    // Default to string
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Create a JSON response string
     */
    public static String createResponse(String action, String message, boolean success) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"action\":\"").append(escapeJson(action)).append("\",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\",");
        json.append("\"success\":").append(success).append(",");
        json.append("\"timestamp\":").append(System.currentTimeMillis());
        json.append("}");
        return json.toString();
    }
    
    /**
     * Create a JSON response with data
     */
    public static String createResponseWithData(String action, String message, boolean success, Map<String, Object> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"action\":\"").append(escapeJson(action)).append("\",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\",");
        json.append("\"success\":").append(success).append(",");
        json.append("\"timestamp\":").append(System.currentTimeMillis()).append(",");
        json.append("\"data\":{");
        
        if (data != null && !data.isEmpty()) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(escapeJson(entry.getKey())).append("\":");
                json.append(valueToJson(entry.getValue()));
                first = false;
            }
        }
        
        json.append("}");
        json.append("}");
        return json.toString();
    }
    
    /**
     * Convert value to JSON format
     */
    private static String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Boolean || value instanceof Number) {
            return value.toString();
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }
    
    /**
     * Escape special JSON characters
     */
    private static String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\b", "\\b")
                 .replace("\f", "\\f")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    /**
     * Create a simple JSON object for user data
     */
    public static String createUserJson(int id, String nome, String email, long dataCriacao) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", id);
        userData.put("nome", nome);
        userData.put("email", email);
        userData.put("dataCriacao", dataCriacao);
        
        return createResponseWithData("user_data", "Dados do usuário", true, userData);
    }
    
    /**
     * Create a simple JSON object for account data
     */
    public static String createAccountJson(int id, String nome, double saldoInicial, int usuarioId) {
        Map<String, Object> accountData = new HashMap<>();
        accountData.put("id", id);
        accountData.put("nome", nome);
        accountData.put("saldoInicial", saldoInicial);
        accountData.put("usuarioId", usuarioId);
        
        return createResponseWithData("account_data", "Dados da conta", true, accountData);
    }
    
    /**
     * Create a simple JSON object for transaction data
     */
    public static String createTransactionJson(int id, double valor, long data, String descricao, 
                                             int contaId, int categoriaId, int usuarioId, String tipo) {
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("id", id);
        transactionData.put("valor", valor);
        transactionData.put("data", data);
        transactionData.put("descricao", descricao);
        transactionData.put("contaId", contaId);
        transactionData.put("categoriaId", categoriaId);
        transactionData.put("usuarioId", usuarioId);
        transactionData.put("tipo", tipo);
        
        return createResponseWithData("transaction_data", "Dados do lançamento", true, transactionData);
    }
}