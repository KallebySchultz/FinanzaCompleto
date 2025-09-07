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

public class SettingsActivity extends AppCompatActivity {
    private EditText editServerHost;
    private EditText editServerPort;
    private Button btnSave;
    private Button btnTest;
    private ImageView btnBack;
    private TextView statusText;

    private ServerClient serverClient;
    private SyncService syncService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        serverClient = ServerClient.getInstance(this);
        syncService = SyncService.getInstance(this);

        initViews();
        setupListeners();
        loadCurrentSettings();
    }

    private void initViews() {
        editServerHost = findViewById(R.id.editServerHost);
        editServerPort = findViewById(R.id.editServerPort);
        btnSave = findViewById(R.id.btnSave);
        btnTest = findViewById(R.id.btnTest);
        btnBack = findViewById(R.id.btnBack);
        statusText = findViewById(R.id.statusText);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveSettings());
        btnTest.setOnClickListener(v -> testConnection());

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadCurrentSettings() {
        SharedPreferences prefs = getSharedPreferences("FinanzaServerConfig", MODE_PRIVATE);
        String host = prefs.getString("server_host", "192.168.1.100");
        int port = prefs.getInt("server_port", 8080);

        editServerHost.setText(host);
        editServerPort.setText(String.valueOf(port));

        if (syncService.isOnline()) {
            statusText.setText("🟢 Conectado ao servidor");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            statusText.setText("🔴 Modo offline");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
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
            Toast.makeText(this, "Configurações salvas com sucesso! Teste a conexão para confirmar.", Toast.LENGTH_LONG).show();

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
                }

                @Override
                public void onError(String error) {
                    statusText.setText("❌ " + error);
                    statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    Toast.makeText(SettingsActivity.this, "Erro de conexão: " + error, Toast.LENGTH_LONG).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Porta deve ser um número válido", Toast.LENGTH_SHORT).show();
        }
    }
}