package com.example.finanza.util;

import android.util.Log;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.db.AppDatabase;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Comprehensive data validation and integrity utility for Finanza Mobile
 * Ensures data quality, prevents corruption, and validates business rules
 */
public class DataIntegrityValidator {
    
    private static final String TAG = "DataIntegrityValidator";
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    // UUID validation pattern
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    // Color hex validation pattern
    private static final Pattern COLOR_HEX_PATTERN = Pattern.compile(
        "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
    );
    
    // Validation result class
    public static class ValidationResult {
        public boolean isValid = true;
        public String errorMessage = "";
        public String field = "";
        public String severity = "INFO"; // INFO, WARNING, ERROR, CRITICAL
        
        public ValidationResult() {}
        
        public ValidationResult(boolean isValid, String field, String errorMessage, String severity) {
            this.isValid = isValid;
            this.field = field;
            this.errorMessage = errorMessage;
            this.severity = severity;
        }
        
        public static ValidationResult success() {
            return new ValidationResult();
        }
        
        public static ValidationResult error(String field, String message) {
            return new ValidationResult(false, field, message, "ERROR");
        }
        
        public static ValidationResult warning(String field, String message) {
            return new ValidationResult(true, field, message, "WARNING");
        }
        
        public static ValidationResult critical(String field, String message) {
            return new ValidationResult(false, field, message, "CRITICAL");
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", severity, field, errorMessage);
        }
    }
    
    // ========== USER VALIDATION ==========
    
    /**
     * Comprehensive user data validation
     */
    public static ValidationResult validateUsuario(Usuario usuario) {
        if (usuario == null) {
            return ValidationResult.critical("usuario", "User object is null");
        }
        
        // Validate name
        ValidationResult nameResult = validateUserName(usuario.nome);
        if (!nameResult.isValid) {
            return nameResult;
        }
        
        // Validate email
        ValidationResult emailResult = validateEmail(usuario.email);
        if (!emailResult.isValid) {
            return emailResult;
        }
        
        // Validate password (if present)
        if (usuario.senha != null && !usuario.senha.isEmpty()) {
            ValidationResult passwordResult = validatePassword(usuario.senha);
            if (!passwordResult.isValid) {
                return passwordResult;
            }
        }
        
        // Validate UUID if present
        if (usuario.uuid != null && !usuario.uuid.isEmpty()) {
            ValidationResult uuidResult = validateUUID(usuario.uuid);
            if (!uuidResult.isValid) {
                return uuidResult;
            }
        }
        
        // Validate timestamps
        ValidationResult timestampResult = validateTimestamps(usuario.dataCriacao, usuario.lastModified);
        if (!timestampResult.isValid) {
            return timestampResult;
        }
        
        // Validate sync status
        ValidationResult syncResult = validateSyncStatus(usuario.syncStatus);
        if (!syncResult.isValid) {
            return syncResult;
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate user name
     */
    public static ValidationResult validateUserName(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return ValidationResult.error("nome", "User name cannot be empty");
        }
        
        String trimmedName = nome.trim();
        if (trimmedName.length() < 2) {
            return ValidationResult.error("nome", "User name must be at least 2 characters long");
        }
        
        if (trimmedName.length() > 100) {
            return ValidationResult.error("nome", "User name cannot exceed 100 characters");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate email format
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.error("email", "Email cannot be empty");
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return ValidationResult.error("email", "Invalid email format");
        }
        
        if (trimmedEmail.length() > 255) {
            return ValidationResult.error("email", "Email cannot exceed 255 characters");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate password strength
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("password", "Password cannot be empty");
        }
        
        if (password.length() < 6) {
            return ValidationResult.error("password", "Password must be at least 6 characters long");
        }
        
        if (password.length() > 128) {
            return ValidationResult.error("password", "Password cannot exceed 128 characters");
        }
        
        return ValidationResult.success();
    }
    
    // ========== ACCOUNT VALIDATION ==========
    
    /**
     * Comprehensive account data validation
     */
    public static ValidationResult validateConta(Conta conta) {
        if (conta == null) {
            return ValidationResult.critical("conta", "Account object is null");
        }
        
        // Validate account name
        ValidationResult nameResult = validateAccountName(conta.nome);
        if (!nameResult.isValid) {
            return nameResult;
        }
        
        // Validate initial balance
        ValidationResult balanceResult = validateAccountBalance(conta.saldoInicial);
        if (!balanceResult.isValid) {
            return balanceResult;
        }
        
        // Validate user ID
        ValidationResult userIdResult = validateUserId(conta.usuarioId);
        if (!userIdResult.isValid) {
            return userIdResult;
        }
        
        // Validate UUID if present
        if (conta.uuid != null && !conta.uuid.isEmpty()) {
            ValidationResult uuidResult = validateUUID(conta.uuid);
            if (!uuidResult.isValid) {
                return uuidResult;
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate account name
     */
    public static ValidationResult validateAccountName(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return ValidationResult.error("nome", "Account name cannot be empty");
        }
        
        String trimmedName = nome.trim();
        if (trimmedName.length() > 100) {
            return ValidationResult.error("nome", "Account name cannot exceed 100 characters");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate account balance
     */
    public static ValidationResult validateAccountBalance(double balance) {
        if (Double.isNaN(balance) || Double.isInfinite(balance)) {
            return ValidationResult.error("saldoInicial", "Invalid balance value");
        }
        
        return ValidationResult.success();
    }
    
    // ========== CATEGORY VALIDATION ==========
    
    /**
     * Comprehensive category data validation
     */
    public static ValidationResult validateCategoria(Categoria categoria) {
        if (categoria == null) {
            return ValidationResult.critical("categoria", "Category object is null");
        }
        
        // Validate category name
        ValidationResult nameResult = validateCategoryName(categoria.nome);
        if (!nameResult.isValid) {
            return nameResult;
        }
        
        // Validate category type
        ValidationResult typeResult = validateCategoryType(categoria.tipo);
        if (!typeResult.isValid) {
            return typeResult;
        }
        
        // Validate UUID if present
        if (categoria.uuid != null && !categoria.uuid.isEmpty()) {
            ValidationResult uuidResult = validateUUID(categoria.uuid);
            if (!uuidResult.isValid) {
                return uuidResult;
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate category name
     */
    public static ValidationResult validateCategoryName(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return ValidationResult.error("nome", "Category name cannot be empty");
        }
        
        String trimmedName = nome.trim();
        if (trimmedName.length() > 100) {
            return ValidationResult.error("nome", "Category name cannot exceed 100 characters");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate category type
     */
    public static ValidationResult validateCategoryType(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return ValidationResult.error("tipo", "Category type cannot be empty");
        }
        
        String trimmedType = tipo.trim().toLowerCase();
        if (!trimmedType.equals("receita") && !trimmedType.equals("despesa")) {
            return ValidationResult.error("tipo", "Category type must be 'receita' or 'despesa'");
        }
        
        return ValidationResult.success();
    }
    
    // ========== TRANSACTION VALIDATION ==========
    
    /**
     * Comprehensive transaction data validation
     */
    public static ValidationResult validateLancamento(Lancamento lancamento) {
        if (lancamento == null) {
            return ValidationResult.critical("lancamento", "Transaction object is null");
        }
        
        // Validate transaction value
        ValidationResult valueResult = validateTransactionValue(lancamento.valor);
        if (!valueResult.isValid) {
            return valueResult;
        }
        
        // Validate transaction date
        ValidationResult dateResult = validateTransactionDate(lancamento.data);
        if (!dateResult.isValid) {
            return dateResult;
        }
        
        // Validate transaction type
        ValidationResult typeResult = validateTransactionType(lancamento.tipo);
        if (!typeResult.isValid) {
            return typeResult;
        }
        
        // Validate foreign key references
        ValidationResult fkResult = validateTransactionForeignKeys(lancamento.contaId, lancamento.categoriaId, lancamento.usuarioId);
        if (!fkResult.isValid) {
            return fkResult;
        }
        
        // Validate UUID if present
        if (lancamento.uuid != null && !lancamento.uuid.isEmpty()) {
            ValidationResult uuidResult = validateUUID(lancamento.uuid);
            if (!uuidResult.isValid) {
                return uuidResult;
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate transaction value
     */
    public static ValidationResult validateTransactionValue(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            return ValidationResult.error("valor", "Invalid transaction value");
        }
        
        if (valor == 0.0) {
            return ValidationResult.warning("valor", "Transaction value is zero");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate transaction date
     */
    public static ValidationResult validateTransactionDate(long data) {
        if (data <= 0) {
            return ValidationResult.error("data", "Invalid transaction date");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate transaction type
     */
    public static ValidationResult validateTransactionType(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return ValidationResult.error("tipo", "Transaction type cannot be empty");
        }
        
        String trimmedType = tipo.trim().toLowerCase();
        if (!trimmedType.equals("receita") && !trimmedType.equals("despesa")) {
            return ValidationResult.error("tipo", "Transaction type must be 'receita' or 'despesa'");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate transaction foreign key references
     */
    public static ValidationResult validateTransactionForeignKeys(int contaId, int categoriaId, int usuarioId) {
        if (contaId <= 0) {
            return ValidationResult.error("contaId", "Invalid account ID");
        }
        
        if (usuarioId <= 0) {
            return ValidationResult.error("usuarioId", "Invalid user ID");
        }
        
        return ValidationResult.success();
    }
    
    // ========== COMMON VALIDATION METHODS ==========
    
    /**
     * Validate UUID format
     */
    public static ValidationResult validateUUID(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return ValidationResult.error("uuid", "UUID cannot be empty");
        }
        
        String trimmedUuid = uuid.trim();
        if (!UUID_PATTERN.matcher(trimmedUuid).matches()) {
            return ValidationResult.error("uuid", "Invalid UUID format");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate user ID
     */
    public static ValidationResult validateUserId(int usuarioId) {
        if (usuarioId <= 0) {
            return ValidationResult.error("usuarioId", "Invalid user ID");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate sync status
     */
    public static ValidationResult validateSyncStatus(int syncStatus) {
        if (syncStatus < 0 || syncStatus > 3) {
            return ValidationResult.error("syncStatus", "Invalid sync status value");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate timestamps
     */
    public static ValidationResult validateTimestamps(long... timestamps) {
        for (long timestamp : timestamps) {
            if (timestamp < 0) {
                return ValidationResult.error("timestamp", "Invalid timestamp value");
            }
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Log validation result
     */
    public static void logValidationResult(ValidationResult result, String context) {
        if (result == null) {
            Log.w(TAG, "Null validation result for context: " + context);
            return;
        }
        
        String message = String.format("[%s] %s - %s", context, result.field, result.errorMessage);
        
        switch (result.severity) {
            case "CRITICAL":
                Log.e(TAG, message);
                break;
            case "ERROR":
                Log.e(TAG, message);
                break;
            case "WARNING":
                Log.w(TAG, message);
                break;
            default:
                Log.d(TAG, message);
                break;
        }
    }
}