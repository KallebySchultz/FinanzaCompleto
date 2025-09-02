package com.example.finanza.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextInputEditText inputNome, inputEmail, inputSenha, inputConfirmarSenha;
    private Button btnCriarConta;
    private TextView txtFazerLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        // Inicializar views
        inputNome = findViewById(R.id.input_nome);
        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);
        inputConfirmarSenha = findViewById(R.id.input_confirmar_senha);
        btnCriarConta = findViewById(R.id.btn_criar_conta);
        txtFazerLogin = findViewById(R.id.txt_fazer_login);

        // Configurar listeners
        btnCriarConta.setOnClickListener(v -> realizarCadastro());
        txtFazerLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void realizarCadastro() {
        String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String senha = inputSenha.getText() != null ? inputSenha.getText().toString().trim() : "";
        String confirmarSenha = inputConfirmarSenha.getText() != null ? inputConfirmarSenha.getText().toString().trim() : "";

        // Limpar erros anteriores
        inputNome.setError(null);
        inputEmail.setError(null);
        inputSenha.setError(null);
        inputConfirmarSenha.setError(null);

        // Validações
        boolean hasError = false;
        if (nome.isEmpty()) {
            inputNome.setError("Digite o nome");
            hasError = true;
        }
        if (email.isEmpty()) {
            inputEmail.setError("Digite o email");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Email inválido");
            hasError = true;
        }
        if (senha.isEmpty()) {
            inputSenha.setError("Digite a senha");
            hasError = true;
        } else if (senha.length() < 6) {
            inputSenha.setError("Senha deve ter pelo menos 6 caracteres");
            hasError = true;
        }
        if (confirmarSenha.isEmpty()) {
            inputConfirmarSenha.setError("Confirme a senha");
            hasError = true;
        } else if (!senha.equals(confirmarSenha)) {
            inputConfirmarSenha.setError("Senhas não coincidem");
            hasError = true;
        }

        if (hasError) return;

        // Verificar se email já existe
        Usuario usuarioExistente = db.usuarioDao().buscarPorEmail(email);
        if (usuarioExistente != null) {
            inputEmail.setError("Este email já está em uso");
            return;
        }

        // Criar novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.nome = nome;
        novoUsuario.email = email;
        novoUsuario.senha = senha;
        novoUsuario.dataCriacao = System.currentTimeMillis();

        long usuarioId = db.usuarioDao().inserir(novoUsuario);
        novoUsuario.id = (int) usuarioId;

        // Salvar login
        SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
        prefs.edit().putInt("usuarioId", novoUsuario.id).apply();

        Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

        // Ir para MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usuarioId", novoUsuario.id);
        startActivity(intent);
        finish();
    }
}