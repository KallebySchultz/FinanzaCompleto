package com.finanza.server.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Armazenamento simples de dados em JSON
 * Simula um banco de dados para sincronização
 */
public class DataStore {
    private static final String DATA_DIR = "finanza-data";
    private static final String USERS_FILE = "users.json";
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final String TRANSACTIONS_FILE = "transactions.json";
    
    private static DataStore instance;
    private Map<String, Object> users;
    private Map<String, Object> accounts;
    private Map<String, Object> transactions;
    
    private DataStore() {
        initializeStorage();
        loadData();
    }
    
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }
    
    private void initializeStorage() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            
            users = new ConcurrentHashMap<>();
            accounts = new ConcurrentHashMap<>();
            transactions = new ConcurrentHashMap<>();
            
        } catch (IOException e) {
            System.err.println("Erro ao inicializar armazenamento: " + e.getMessage());
        }
    }
    
    private void loadData() {
        loadJsonFile(USERS_FILE, users);
        loadJsonFile(ACCOUNTS_FILE, accounts);
        loadJsonFile(TRANSACTIONS_FILE, transactions);
    }
    
    private void loadJsonFile(String filename, Map<String, Object> dataMap) {
        Path filePath = Paths.get(DATA_DIR, filename);
        if (Files.exists(filePath)) {
            try {
                String content = Files.readString(filePath);
                if (!content.trim().isEmpty()) {
                    // Simulação simples de parsing JSON
                    // Em produção, use uma biblioteca como Jackson ou Gson
                    System.out.println("Carregado: " + filename);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar " + filename + ": " + e.getMessage());
            }
        }
    }
    
    private void saveJsonFile(String filename, Map<String, Object> dataMap) {
        Path filePath = Paths.get(DATA_DIR, filename);
        try {
            // Simulação simples de serialização JSON
            StringBuilder json = new StringBuilder("{\n");
            boolean first = true;
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (!first) json.append(",\n");
                json.append("  \"").append(entry.getKey()).append("\": ");
                json.append("\"").append(entry.getValue().toString()).append("\"");
                first = false;
            }
            json.append("\n}");
            
            Files.writeString(filePath, json.toString());
            System.out.println("Salvo: " + filename);
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + filename + ": " + e.getMessage());
        }
    }
    
    // Métodos para usuários
    public void saveUser(String userId, String userData) {
        users.put(userId, userData);
        saveJsonFile(USERS_FILE, users);
    }
    
    public String getUser(String userId) {
        Object userData = users.get(userId);
        return userData != null ? userData.toString() : null;
    }
    
    public Map<String, Object> getAllUsers() {
        return new HashMap<>(users);
    }
    
    // Métodos para contas
    public void saveAccount(String accountId, String accountData) {
        accounts.put(accountId, accountData);
        saveJsonFile(ACCOUNTS_FILE, accounts);
    }
    
    public String getAccount(String accountId) {
        Object accountData = accounts.get(accountId);
        return accountData != null ? accountData.toString() : null;
    }
    
    public Map<String, Object> getAllAccounts() {
        return new HashMap<>(accounts);
    }
    
    // Métodos para transações
    public void saveTransaction(String transactionId, String transactionData) {
        transactions.put(transactionId, transactionData);
        saveJsonFile(TRANSACTIONS_FILE, transactions);
    }
    
    public String getTransaction(String transactionId) {
        Object transactionData = transactions.get(transactionId);
        return transactionData != null ? transactionData.toString() : null;
    }
    
    public Map<String, Object> getAllTransactions() {
        return new HashMap<>(transactions);
    }
    
    // Métodos de busca por usuário
    public Map<String, Object> getAccountsByUser(String userId) {
        Map<String, Object> userAccounts = new HashMap<>();
        for (Map.Entry<String, Object> entry : accounts.entrySet()) {
            String data = entry.getValue().toString();
            if (data.contains("\"userId\":\"" + userId + "\"")) {
                userAccounts.put(entry.getKey(), entry.getValue());
            }
        }
        return userAccounts;
    }
    
    public Map<String, Object> getTransactionsByUser(String userId) {
        Map<String, Object> userTransactions = new HashMap<>();
        for (Map.Entry<String, Object> entry : transactions.entrySet()) {
            String data = entry.getValue().toString();
            if (data.contains("\"userId\":\"" + userId + "\"")) {
                userTransactions.put(entry.getKey(), entry.getValue());
            }
        }
        return userTransactions;
    }
    
    // Estatísticas
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("users", users.size());
        stats.put("accounts", accounts.size());
        stats.put("transactions", transactions.size());
        return stats;
    }
}