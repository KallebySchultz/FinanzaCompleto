package com.example.finanza.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finanza.MainActivity;
import com.example.finanza.R;
import com.example.finanza.model.Usuario;
import com.example.finanza.network.AuthManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextInputEditText inputEmail, inputSenha;
    private Button btnLogin;
    private TextView txtCriarConta;
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
        btnConfiguracoes = findViewById(R.id.btn_configuracoes);

        btnLogin.setOnClickListener(v -> realizarLogin());
        txtCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        btnConfiguracoes.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Adicionar listener de longo clique no campo de senha para recuperação de senha
        inputSenha.setOnLongClickListener(v -> {
            mostrarDialogRecuperacaoSenha();
            return true;
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

    private void mostrarDialogRecuperacaoSenha() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Senha");
        builder.setMessage("Digite seu email para recuperar a senha:");
        
        // Criar campo de input para email
        android.widget.EditText inputEmailRecuperacao = new android.widget.EditText(this);
        inputEmailRecuperacao.setHint("Digite seu email");
        inputEmailRecuperacao.setInputType(android.text.InputType.TYPE_TEXT_EMAIL_ADDRESS);
        inputEmailRecuperacao.setPadding(50, 30, 50, 30);
        builder.setView(inputEmailRecuperacao);
        
        builder.setPositiveButton("Recuperar", (dialog, which) -> {
            String email = inputEmailRecuperacao.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite um email válido", Toast.LENGTH_SHORT).show();
                return;
            }
            recuperarSenha(email);
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void recuperarSenha(String email) {
        authManager.recuperarSenha(email, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Senha Recuperada");
                    builder.setMessage("Sua nova senha temporária é: " + usuario.senha + 
                                     "\n\nVocê pode alterá-la após fazer login.");
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        // Preencher automaticamente o email no campo de login
                        LoginActivity.this.inputEmail.setText(email);
                    });
                    builder.show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}