package com.example.finanza.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configurar barra de status
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.primaryDarkBlue));
            getWindow().setNavigationBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.primaryDarkBlue));
        }
        getWindow().getDecorView().setSystemUiVisibility(0);

        // Inicializar AuthManager
        authManager = AuthManager.getInstance(this);

        // Verificar se já está logado
        if (authManager.isLoggedIn()) {
            // Usuário já está logado, ir para MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuarioId", authManager.getLoggedUserId());
            startActivity(intent);
            finish();
            return;
        }

        // Inicializar views
        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);
        btnLogin = findViewById(R.id.btn_login);
        txtCriarConta = findViewById(R.id.txt_criar_conta);

        // Configurar listeners
        btnLogin.setOnClickListener(v -> realizarLogin());
        txtCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void realizarLogin() {
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String senha = inputSenha.getText() != null ? inputSenha.getText().toString().trim() : "";

        // Limpar erros anteriores
        inputEmail.setError(null);
        inputSenha.setError(null);

        // Validações
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

        // Desabilitar botão durante login
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        // Tentar fazer login usando AuthManager
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
}