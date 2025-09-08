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
        builder.setTitle("🔐 Recuperar Senha");
        builder.setMessage("Digite seu email para gerar uma nova senha temporária:");
        
        // Criar campo de input para email
        android.widget.EditText inputEmailRecuperacao = new android.widget.EditText(this);
        inputEmailRecuperacao.setHint("Digite seu email");
        inputEmailRecuperacao.setInputType(android.text.InputType.TYPE_TEXT_EMAIL_ADDRESS);
        inputEmailRecuperacao.setPadding(50, 30, 50, 30);
        
        // Se há email preenchido no campo de login, usar como sugestão
        String emailAtual = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        if (!emailAtual.isEmpty()) {
            inputEmailRecuperacao.setText(emailAtual);
        }
        
        builder.setView(inputEmailRecuperacao);
        
        builder.setPositiveButton("Recuperar", (dialog, which) -> {
            String email = inputEmailRecuperacao.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite um email válido", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Formato de email inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            recuperarSenha(email);
        });
        
        builder.setNegativeButton("Cancelar", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarSenha(String email) {
        // Mostrar feedback visual durante processamento
        Toast.makeText(this, "Processando recuperação...", Toast.LENGTH_SHORT).show();
        
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
                        // Limpar campo de senha para que usuário digite a nova
                        LoginActivity.this.inputSenha.setText("");
                        // Mostrar dica sobre onde alterar senha
                        Toast.makeText(LoginActivity.this, 
                            "Use a senha temporária para entrar. Altere-a em Perfil > Editar Perfil.", 
                            Toast.LENGTH_LONG).show();
                    });
                    builder.setCancelable(false); // Força usuário a ver a senha
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