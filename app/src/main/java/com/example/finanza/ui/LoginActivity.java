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
import com.example.finanza.network.FirebaseAuthClient;
import com.example.finanza.network.SyncService;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AppDatabase db;
    private FirebaseAuthClient firebaseAuth;
    private SyncService syncService;
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

        // Inicializar Firebase Auth
        firebaseAuth = new FirebaseAuthClient(this);
        
        // Inicializar SyncService
        syncService = new SyncService(this);

        // Verificar se já está logado (prioritizando Firebase Auth)
        if (firebaseAuth.isUserLoggedIn()) {
            // Usuário já está logado no Firebase, ir para MainActivity
            String firebaseUserId = firebaseAuth.getSavedUserId();
            String firebaseEmail = firebaseAuth.getSavedUserEmail();
            
            // Criar ou buscar usuário local correspondente
            Usuario usuarioLocal = criarOuBuscarUsuarioLocal(firebaseEmail, firebaseUserId);
            
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuarioId", usuarioLocal.id);
            startActivity(intent);
            finish();
            return;
        } else {
            // Verificar login local (para funcionamento offline)
            SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
            int usuarioLogado = prefs.getInt("usuarioId", -1);
            if (usuarioLogado != -1) {
                // Usuário está logado localmente, ir para MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("usuarioId", usuarioLogado);
                startActivity(intent);
                finish();
                return;
            }
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

        // Desabilitar botão para evitar múltiplos cliques
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        // Tentar login com Firebase primeiro (para sincronização online)
        firebaseAuth.signInWithEmailAndPassword(email, senha, new FirebaseAuthClient.AuthCallback() {
            @Override
            public void onSuccess(String token, String userId, String email) {
                // Login Firebase bem-sucedido
                Usuario usuarioLocal = criarOuBuscarUsuarioLocal(email, userId);
                
                // Salvar sessão local também
                SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
                prefs.edit().putInt("usuarioId", usuarioLocal.id).apply();

                // Iniciar sincronização
                syncService.sincronizarTudo(usuarioLocal.id);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("usuarioId", usuarioLocal.id);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                // Firebase login falhou, tentar login local (para modo offline)
                Usuario usuario = db.usuarioDao().login(email, senha);
                if (usuario != null) {
                    // Login local bem-sucedido
                    SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
                    prefs.edit().putInt("usuarioId", usuario.id).apply();

                    Toast.makeText(LoginActivity.this, "Login offline realizado", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("usuarioId", usuario.id);
                    startActivity(intent);
                    finish();
                } else {
                    // Ambos os logins falharam
                    Toast.makeText(LoginActivity.this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Entrar");
                }
            }
        });
    }

    /**
     * Cria ou busca usuário local correspondente ao usuário Firebase
     */
    private Usuario criarOuBuscarUsuarioLocal(String email, String firebaseUserId) {
        Usuario usuarioLocal = db.usuarioDao().buscarPorEmail(email);
        
        if (usuarioLocal == null) {
            // Criar novo usuário local
            usuarioLocal = new Usuario();
            usuarioLocal.email = email;
            usuarioLocal.nome = email.split("@")[0]; // Nome padrão baseado no email
            usuarioLocal.senha = ""; // Não precisamos da senha para usuários Firebase
            usuarioLocal.dataCriacao = System.currentTimeMillis();
            
            long id = db.usuarioDao().inserir(usuarioLocal);
            usuarioLocal.id = (int) id;
        }
        
        return usuarioLocal;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseAuth != null) {
            firebaseAuth.fechar();
        }
        if (syncService != null) {
            syncService.fechar();
        }
    }
}