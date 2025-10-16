package com.example.finanza.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finanza.R;
import com.example.finanza.network.ServerClient;
import com.example.finanza.network.SyncService;
import com.example.finanza.network.AuthManager;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {
    private EditText editServerHost;
    private EditText editServerPort;
    private Button btnSave;
    private Button btnTest;
    private Button btnSync;
    private ImageView btnBack;
    private TextView statusText;

    private ServerClient serverClient;
    private SyncService syncService;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        serverClient = ServerClient.getInstance(this);
        syncService = SyncService.getInstance(this);
        authManager = AuthManager.getInstance(this);

        initViews();
        setupListeners();
        loadCurrentSettings();
    }

    private void initViews() {
        editServerHost = findViewById(R.id.editServerHost);
        editServerPort = findViewById(R.id.editServerPort);
        btnSave = findViewById(R.id.btnSave);
        btnTest = findViewById(R.id.btnTest);
        btnSync = findViewById(R.id.btnSync);
        btnBack = findViewById(R.id.btnBack);
        statusText = findViewById(R.id.statusText);
        
        // Ensure back button is properly initialized and clickable
        if (btnBack != null) {
            btnBack.setClickable(true);
            btnBack.setFocusable(true);
            Log.d("SettingsActivity", "Back button initialized successfully");
        } else {
            Log.e("SettingsActivity", "Back button not found in layout!");
        }
        
        // Initialize sync button if it exists
        if (btnSync != null) {
            btnSync.setEnabled(syncService.isOnline());
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveSettings());
        btnTest.setOnClickListener(v -> testConnection());

        // Add manual sync button listener
        if (btnSync != null) {
            btnSync.setOnClickListener(v -> triggerManualSync());
        }

        btnBack.setOnClickListener(v -> {
            Log.d("SettingsActivity", "Back button clicked");
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void loadCurrentSettings() {
        SharedPreferences prefs = getSharedPreferences("FinanzaServerConfig", MODE_PRIVATE);
        String host = prefs.getString("server_host", "192.168.1.100");
        int port = prefs.getInt("server_port", 8080);

        editServerHost.setText(host);
        editServerPort.setText(String.valueOf(port));

        updateConnectionStatus();
    }

    private void updateConnectionStatus() {
        if (syncService.isOnline()) {
            statusText.setText("🟢 Conectado ao servidor");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            if (btnSync != null) {
                btnSync.setEnabled(true);
            }
        } else {
            statusText.setText("🔴 Modo offline");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            if (btnSync != null) {
                btnSync.setEnabled(false);
            }
        }
    }

    /**
     * Triggers manual synchronization of pending offline data
     * This is critical for syncing data created while offline
     */
    private void triggerManualSync() {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Você precisa estar logado para sincronizar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!syncService.isOnline()) {
            Toast.makeText(this, "Conecte-se ao servidor primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        statusText.setText("🔄 Sincronizando dados...");
        statusText.setTextColor(getResources().getColor(android.R.color.black));
        
        if (btnSync != null) {
            btnSync.setEnabled(false);
        }

        int userId = authManager.getLoggedUserId();
        syncService.sincronizarTudo(userId, new SyncService.SyncCallback() {
            @Override
            public void onSyncStarted() {
                runOnUiThread(() -> {
                    statusText.setText("🔄 Sincronização iniciada...");
                });
            }

            @Override
            public void onSyncCompleted(boolean success, String message) {
                runOnUiThread(() -> {
                    if (success) {
                        statusText.setText("✅ " + message);
                        statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        Toast.makeText(SettingsActivity.this, "Sincronização concluída!", Toast.LENGTH_SHORT).show();
                    } else {
                        statusText.setText("❌ " + message);
                        statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        Toast.makeText(SettingsActivity.this, "Erro na sincronização: " + message, Toast.LENGTH_LONG).show();
                    }
                    if (btnSync != null) {
                        btnSync.setEnabled(true);
                    }
                });
            }

            @Override
            public void onSyncProgress(String operation) {
                runOnUiThread(() -> {
                    statusText.setText("🔄 " + operation);
                });
            }
        });
    }

    private void saveSettings() {
        String host = editServerHost.getText().toString().trim();
        String portStr = editServerPort.getText().toString().trim();

        if (host.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o IP do servidor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                Toast.makeText(this, "Porta deve estar entre 1 e 65535", Toast.LENGTH_SHORT).show();
                return;
            }

            serverClient.configurarServidor(host, port);
            statusText.setText("⚙️ Configurações salvas: " + host + ":" + port);
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            Toast.makeText(this, "Configurações salvas com sucesso!", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Porta deve ser um número válido", Toast.LENGTH_SHORT).show();
        }
    }

    private void testConnection() {
        statusText.setText("🔄 Testando conexão...");
        statusText.setTextColor(getResources().getColor(android.R.color.black));

        String host = editServerHost.getText().toString().trim();
        String portStr = editServerPort.getText().toString().trim();

        if (host.isEmpty() || portStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha o IP e porta do servidor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int port = Integer.parseInt(portStr);

            serverClient.conectar(host, port, new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    statusText.setText("✅ " + result);
                    statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    Toast.makeText(SettingsActivity.this, "Conexão bem-sucedida!", Toast.LENGTH_SHORT).show();
                    updateConnectionStatus();
                }

                @Override
                public void onError(String error) {
                    statusText.setText("❌ " + error);
                    statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    Toast.makeText(SettingsActivity.this, "Erro de conexão: " + error, Toast.LENGTH_LONG).show();
                    updateConnectionStatus();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Porta deve ser um número válido", Toast.LENGTH_SHORT).show();
        }
    }
}