package com.finanza.desktop.view;

import com.finanza.desktop.config.SettingsManager;
import com.finanza.desktop.network.NetworkManager;
import com.finanza.desktop.ui.ModernUIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de Configurações
 * Configurações do aplicativo e conexão
 */
public class SettingsView extends JPanel {
    
    // Cores do tema
    private static final Color PRIMARY_DARK_BLUE = ModernUIHelper.PRIMARY_DARK_BLUE;
    private static final Color ACCENT_BLUE = ModernUIHelper.ACCENT_BLUE;
    private static final Color POSITIVE_GREEN = ModernUIHelper.POSITIVE_GREEN;
    private static final Color NEGATIVE_RED = ModernUIHelper.NEGATIVE_RED;
    private static final Color WHITE = ModernUIHelper.WHITE;
    private static final Color GRAY = ModernUIHelper.GRAY;
    
    private SettingsManager settingsManager;
    private NetworkManager networkManager;
    private JLabel connectionStatusLabel;
    private JTextField serverUrlField;
    private JTextField serverPortField;
    private JCheckBox autoConnectCheckBox;
    
    public SettingsView(SettingsManager settingsManager, NetworkManager networkManager) {
        this.settingsManager = settingsManager;
        this.networkManager = networkManager;
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel(ModernUIHelper.ICON_SETTINGS + " Configurações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK_BLUE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Conteúdo principal
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Status da conexão
        JPanel connectionPanel = ModernUIHelper.createCardPanel();
        connectionPanel.setLayout(new BorderLayout());
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        connectionPanel.setPreferredSize(new Dimension(600, 80));
        
        JLabel connectionTitle = new JLabel("Status da Conexão");
        connectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        connectionTitle.setForeground(PRIMARY_DARK_BLUE);
        connectionPanel.add(connectionTitle, BorderLayout.NORTH);
        
        updateConnectionStatus();
        connectionPanel.add(connectionStatusLabel, BorderLayout.CENTER);
        
        JPanel connectionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        connectionButtonPanel.setBackground(WHITE);
        
        JButton testConnectionButton = ModernUIHelper.createModernButton("Testar", ModernUIHelper.ICON_CONNECT, ACCENT_BLUE);
        testConnectionButton.addActionListener(e -> testConnection());
        connectionButtonPanel.add(testConnectionButton);
        
        connectionPanel.add(connectionButtonPanel, BorderLayout.EAST);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(connectionPanel, gbc);
        
        // Configurações do servidor
        JPanel serverPanel = ModernUIHelper.createCardPanel();
        serverPanel.setLayout(new GridBagLayout());
        serverPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        serverPanel.setPreferredSize(new Dimension(600, 200));
        
        GridBagConstraints serverGbc = new GridBagConstraints();
        serverGbc.insets = new Insets(8, 10, 8, 10);
        serverGbc.anchor = GridBagConstraints.WEST;
        
        JLabel serverTitle = new JLabel("Configurações do Servidor");
        serverTitle.setFont(new Font("Arial", Font.BOLD, 16));
        serverTitle.setForeground(PRIMARY_DARK_BLUE);
        serverGbc.gridx = 0; serverGbc.gridy = 0;
        serverGbc.gridwidth = 2;
        serverPanel.add(serverTitle, serverGbc);
        
        // URL do servidor
        JLabel urlLabel = new JLabel("URL do Servidor:");
        urlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        serverGbc.gridx = 0; serverGbc.gridy = 1;
        serverGbc.gridwidth = 1;
        serverPanel.add(urlLabel, serverGbc);
        
        serverUrlField = ModernUIHelper.createModernTextField("", 20);
        serverUrlField.setText(settingsManager.getServerHost());
        serverGbc.gridx = 1;
        serverPanel.add(serverUrlField, serverGbc);
        
        // Porta do servidor
        JLabel portLabel = new JLabel("Porta:");
        portLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        serverGbc.gridx = 0; serverGbc.gridy = 2;
        serverPanel.add(portLabel, serverGbc);
        
        serverPortField = ModernUIHelper.createModernTextField("", 10);
        serverPortField.setText(String.valueOf(settingsManager.getServerPort()));
        serverGbc.gridx = 1;
        serverPanel.add(serverPortField, serverGbc);
        
        // Auto conectar
        autoConnectCheckBox = new JCheckBox("Conectar automaticamente ao iniciar");
        autoConnectCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        autoConnectCheckBox.setBackground(WHITE);
        autoConnectCheckBox.setSelected(settingsManager.isAutoConnect());
        serverGbc.gridx = 0; serverGbc.gridy = 3;
        serverGbc.gridwidth = 2;
        serverPanel.add(autoConnectCheckBox, serverGbc);
        
        gbc.gridy = 1;
        contentPanel.add(serverPanel, gbc);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(WHITE);
        
        JButton saveButton = ModernUIHelper.createModernButton("Salvar Configurações", ModernUIHelper.ICON_SAVE, POSITIVE_GREEN);
        saveButton.addActionListener(e -> saveSettings());
        actionPanel.add(saveButton);
        
        JButton resetButton = ModernUIHelper.createModernButton("Restaurar Padrões", "🔄", GRAY);
        resetButton.addActionListener(e -> resetToDefaults());
        actionPanel.add(resetButton);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        contentPanel.add(actionPanel, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void updateConnectionStatus() {
        if (connectionStatusLabel == null) {
            connectionStatusLabel = new JLabel();
            connectionStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        
        if (networkManager.isConnected()) {
            connectionStatusLabel.setText(ModernUIHelper.ICON_CONNECT + " Conectado ao servidor");
            connectionStatusLabel.setForeground(POSITIVE_GREEN);
        } else {
            connectionStatusLabel.setText(ModernUIHelper.ICON_DISCONNECT + " Desconectado");
            connectionStatusLabel.setForeground(NEGATIVE_RED);
        }
    }
    
    private void testConnection() {
        // Salvar configurações temporariamente para teste
        String url = serverUrlField.getText().trim();
        String portText = serverPortField.getText().trim();
        
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite a URL do servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int port = Integer.parseInt(portText);
            
            // Atualizar configurações temporariamente
            settingsManager.setServerHost(url);
            settingsManager.setServerPort(port);
            
            // Testar conexão
            JLabel loadingLabel = new JLabel(ModernUIHelper.ICON_CONNECT + " Testando conexão...");
            loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            loadingLabel.setForeground(ACCENT_BLUE);
            
            // Substituir temporariamente o status
            JPanel parent = (JPanel) connectionStatusLabel.getParent();
            parent.remove(connectionStatusLabel);
            parent.add(loadingLabel, BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
            
            // Simular teste de conexão em thread separada
            SwingUtilities.invokeLater(() -> {
                boolean connected = false;
                try {
                    connected = networkManager.testConnection().get();
                } catch (Exception ex) {
                    connected = false;
                }
                
                // Restaurar label original
                parent.remove(loadingLabel);
                parent.add(connectionStatusLabel, BorderLayout.CENTER);
                updateConnectionStatus();
                parent.revalidate();
                parent.repaint();
                
                if (connected) {
                    JOptionPane.showMessageDialog(this, "Conexão estabelecida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao conectar com o servidor.\nVerifique a URL e porta.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                }
            });
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Porta deve ser um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveSettings() {
        String url = serverUrlField.getText().trim();
        String portText = serverPortField.getText().trim();
        boolean autoConnect = autoConnectCheckBox.isSelected();
        
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite a URL do servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int port = Integer.parseInt(portText);
            
            // Salvar configurações
            settingsManager.setServerHost(url);
            settingsManager.setServerPort(port);
            settingsManager.setAutoConnect(autoConnect);
            settingsManager.saveSettings();
            
            updateConnectionStatus();
            JOptionPane.showMessageDialog(this, "Configurações salvas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Porta deve ser um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetToDefaults() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja restaurar as configurações padrão?",
            "Confirmar Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            settingsManager.resetToDefaults();
            
            // Atualizar campos na interface
            serverUrlField.setText(settingsManager.getServerHost());
            serverPortField.setText(String.valueOf(settingsManager.getServerPort()));
            autoConnectCheckBox.setSelected(settingsManager.isAutoConnect());
            
            updateConnectionStatus();
            
            JOptionPane.showMessageDialog(this, "Configurações restauradas para os valores padrão.", "Reset Concluído", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void updateConnectionStatusDisplay() {
        updateConnectionStatus();
    }
}