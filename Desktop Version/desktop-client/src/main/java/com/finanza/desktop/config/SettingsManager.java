package com.finanza.desktop.config;

import java.io.*;
import java.util.Properties;

/**
 * Gerenciador de configurações do aplicativo desktop
 * Armazena e recupera configurações de forma persistente
 */
public class SettingsManager {
    private static final String CONFIG_FILE = "finanza-config.properties";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_TIMEOUT = "5000";
    
    private Properties properties;
    private static SettingsManager instance;
    
    private SettingsManager() {
        properties = new Properties();
        loadSettings();
    }
    
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }
    
    private void loadSettings() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Erro ao carregar configurações: " + e.getMessage());
                setDefaultSettings();
            }
        } else {
            setDefaultSettings();
        }
    }
    
    private void setDefaultSettings() {
        properties.setProperty("server.host", DEFAULT_HOST);
        properties.setProperty("server.port", DEFAULT_PORT);
        properties.setProperty("connection.timeout", DEFAULT_TIMEOUT);
        properties.setProperty("auto.connect", "true");
        properties.setProperty("theme", "light");
        saveSettings();
    }
    
    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Configurações do Finanza Desktop");
        } catch (IOException e) {
            System.err.println("Erro ao salvar configurações: " + e.getMessage());
        }
    }
    
    public String getServerHost() {
        return properties.getProperty("server.host", DEFAULT_HOST);
    }
    
    public void setServerHost(String host) {
        properties.setProperty("server.host", host);
    }
    
    public int getServerPort() {
        try {
            return Integer.parseInt(properties.getProperty("server.port", DEFAULT_PORT));
        } catch (NumberFormatException e) {
            return Integer.parseInt(DEFAULT_PORT);
        }
    }
    
    public void setServerPort(int port) {
        properties.setProperty("server.port", String.valueOf(port));
    }
    
    public int getConnectionTimeout() {
        try {
            return Integer.parseInt(properties.getProperty("connection.timeout", DEFAULT_TIMEOUT));
        } catch (NumberFormatException e) {
            return Integer.parseInt(DEFAULT_TIMEOUT);
        }
    }
    
    public void setConnectionTimeout(int timeout) {
        properties.setProperty("connection.timeout", String.valueOf(timeout));
    }
    
    public boolean isAutoConnect() {
        return Boolean.parseBoolean(properties.getProperty("auto.connect", "true"));
    }
    
    public void setAutoConnect(boolean autoConnect) {
        properties.setProperty("auto.connect", String.valueOf(autoConnect));
    }
    
    public String getTheme() {
        return properties.getProperty("theme", "light");
    }
    
    public void setTheme(String theme) {
        properties.setProperty("theme", theme);
    }
    
    public String getServerAddress() {
        return getServerHost() + ":" + getServerPort();
    }
    
    public void resetToDefaults() {
        setDefaultSettings();
        saveSettings();
    }
}