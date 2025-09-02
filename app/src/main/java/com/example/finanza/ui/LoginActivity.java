package com.example.finanza.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.MainActivity;
import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextInputEditText inputEmail, inputSenha;
    private Button btnLogin;
    private TextView txtCriarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configurar barra de status
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);

        // Inicializar database
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .fallbackToDestructiveMigration() // Para lidar com mudanças no schema
                .allowMainThreadQueries()
                .build();

        // Verificar se já está logado
        SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
        int usuarioLogado = prefs.getInt("usuarioId", -1);
        if (usuarioLogado != -1) {
            // Usuário já está logado, ir para MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuarioId", usuarioLogado);
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

        // Tentar fazer login
        Usuario usuario = db.usuarioDao().login(email, senha);
        if (usuario != null) {
            // Login bem-sucedido
            SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
            prefs.edit().putInt("usuarioId", usuario.id).apply();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuarioId", usuario.id);
            startActivity(intent);
            finish();
        } else {
            // Login falhou
            Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
        }
    }
}