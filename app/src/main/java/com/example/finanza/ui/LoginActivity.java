package com.example.finanza.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finanza.MainActivity;
import com.example.finanza.R;
import com.example.finanza.model.Usuario;
import com.example.finanza.network.AuthManager;
import com.example.finanza.network.ServerClient;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextInputEditText inputEmail, inputSenha;
    private Button btnLogin;
    private TextView txtCriarConta, txtRecuperarSenha;
    private ImageView btnConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);

        authManager = AuthManager.getInstance(this);

        if (authManager.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuarioId", authManager.getLoggedUserId());
            startActivity(intent);
            finish();
            return;
        }

        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);
        btnLogin = findViewById(R.id.btn_login);
        txtCriarConta = findViewById(R.id.txt_criar_conta);
        txtRecuperarSenha = findViewById(R.id.txt_recuperar_senha);
        btnConfiguracoes = findViewById(R.id.btn_configuracoes);

        btnLogin.setOnClickListener(v -> realizarLogin());
        txtCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        txtRecuperarSenha.setOnClickListener(v -> mostrarDialogRecuperarSenha());

        btnConfiguracoes.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void realizarLogin() {
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String senha = inputSenha.getText() != null ? inputSenha.getText().toString().trim() : "";

        inputEmail.setError(null);
        inputSenha.setError(null);

        boolean hasError = false;
        if (email.isEmpty()) {
            inputEmail.setError("Digite o email");
            hasError = true;
        }
        if (senha.isEmpty()) {
            inputSenha.setError("Digite a senha");
            hasError = true;
        }
        if (hasError) return;

        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        authManager.login(email, senha, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("usuarioId", usuario.id);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Entrar");
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void mostrarDialogRecuperarSenha() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recuperar_senha, null);
        
        TextInputEditText inputEmailRecovery = dialogView.findViewById(R.id.input_email_recovery);
        Button btnEnviarRecovery = dialogView.findViewById(R.id.btn_enviar_recovery);
        Button btnCancelarRecovery = dialogView.findViewById(R.id.btn_cancelar_recovery);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        btnCancelarRecovery.setOnClickListener(v -> dialog.dismiss());
        
        btnEnviarRecovery.setOnClickListener(v -> {
            String email = inputEmailRecovery.getText() != null ? 
                inputEmailRecovery.getText().toString().trim() : "";
            
            if (email.isEmpty()) {
                inputEmailRecovery.setError("Digite o email");
                return;
            }
            
            if (!email.contains("@")) {
                inputEmailRecovery.setError("Digite um email válido");
                return;
            }
            
            btnEnviarRecovery.setEnabled(false);
            btnEnviarRecovery.setText("Enviando...");
            
            ServerClient serverClient = ServerClient.getInstance(this);
            serverClient.recuperarSenha(email, new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String mensagem) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, mensagem, Toast.LENGTH_LONG).show();
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        btnEnviarRecovery.setEnabled(true);
                        btnEnviarRecovery.setText("Enviar");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
        
        dialog.show();
    }
}