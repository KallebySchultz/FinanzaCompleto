package com.example.finanza.ui;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finanza.R;
import com.example.finanza.network.ServerClient;
import com.example.finanza.network.FirebaseClient;
import com.example.finanza.network.FirebaseAuthClient;

/**
 * Atividade de configurações do aplicativo
 * Permite configurar o servidor para sincronização e Firebase
 */
public class SettingsActivity extends AppCompatActivity {
    private EditText editServerHost;
    private EditText editServerPort;
    private Button btnSave;
    private Button btnTest;
    private Button btnTestFirebase;
    private Button btnLogout;
    private Button btnBack;
    private TextView statusText;
    private ServerClient serverClient;
    private FirebaseClient firebaseClient;
    private FirebaseAuthClient firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setupListeners();
        loadCurrentSettings();
        
        serverClient = new ServerClient(this);
        firebaseClient = new FirebaseClient(this);
        firebaseAuth = new FirebaseAuthClient(this);
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
        btnBack.setOnClickListener(v -> finish());
        
        // Adicionar teste Firebase via long press no botão Test
        btnTest.setOnLongClickListener(v -> {
            testFirebaseConnection();
            return true;
        });
        
        // Adicionar logout via long press no botão Save
        btnSave.setOnLongClickListener(v -> {
            showLogoutDialog();
            return true;
        });
    }

    private void loadCurrentSettings() {
        SharedPreferences prefs = getSharedPreferences("FinanzaServerConfig", MODE_PRIVATE);
        String host = prefs.getString("server_host", "192.168.1.100");
        int port = prefs.getInt("server_port", 8080);
        
        editServerHost.setText(host);
        editServerPort.setText(String.valueOf(port));
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

    private void testFirebaseConnection() {
        statusText.setText("🔄 Testando conexão Firebase...");
        statusText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        
        firebaseClient.testarConexao(new FirebaseClient.FirebaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                statusText.setText("✅ Firebase: " + result);
                statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                Toast.makeText(SettingsActivity.this, "Firebase conectado com sucesso!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                statusText.setText("❌ Firebase: " + error);
                statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                Toast.makeText(SettingsActivity.this, "Erro Firebase: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Deseja fazer logout? Isso irá desconectar você do Firebase e limpar os dados locais de autenticação.");
        
        builder.setPositiveButton("Sim, fazer logout", (dialog, which) -> {
            performLogout();
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });
        
        builder.show();
    }

    private void performLogout() {
        // Fazer logout do Firebase
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
        }
        
        // Limpar dados locais de autenticação
        SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
        
        // Voltar para tela de login
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverClient != null) {
            serverClient.fechar();
        }
        if (firebaseClient != null) {
            firebaseClient.fechar();
        }
        if (firebaseAuth != null) {
            firebaseAuth.fechar();
        }
    }
}