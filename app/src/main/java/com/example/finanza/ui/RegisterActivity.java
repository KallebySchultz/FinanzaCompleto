package com.example.finanza.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finanza.MainActivity;
import com.example.finanza.R;
import com.example.finanza.model.Usuario;
import com.example.finanza.network.AuthManager;
import com.google.android.material.textfield.TextInputEditText;

/**
 * RegisterActivity - Tela de Cadastro de Novo Usuário
 *
 * Esta atividade permite que novos usuários criem uma conta no
 * aplicativo Finanza.
 *
 * Funcionalidades principais:
 * - Cadastro de novo usuário com validação completa
 * - Campos: Nome completo, Email, Senha e Confirmação de senha
 * - Validação de formato de email
 * - Validação de força de senha
 * - Confirmação de senha igual
 * - Comunicação com servidor para registro
 * - Suporte a modo offline (registro local)
 * - Link para retornar à tela de login
 *
 * Validações implementadas:
 * - Nome não pode estar vazio
 * - Email deve estar em formato válido
 * - Senha deve ter mínimo de caracteres
 * - Confirmação de senha deve ser idêntica
 * - Email não pode estar duplicado no sistema
 *
 * Comportamento:
 * - Feedback visual de erros em cada campo
 * - Mensagens claras de erro/sucesso
 * - Redirecionamento automático após cadastro bem-sucedido
 * - Opção de voltar para login sem cadastrar
 *
 * Segurança:
 * - Senha é criptografada antes de enviar ao servidor
 * - Validação de força de senha
 * - Proteção contra duplicação de email
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class RegisterActivity extends AppCompatActivity {

    private AuthManager authManager;
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

        // Inicializar AuthManager
        authManager = AuthManager.getInstance(this);

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

        // Desabilitar botão durante registro
        btnCriarConta.setEnabled(false);
        btnCriarConta.setText("Criando conta...");

        // Registrar usando AuthManager
        authManager.registrar(nome, email, senha, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                    
                    // Redirecionar para login após registro bem-sucedido
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnCriarConta.setEnabled(true);
                    btnCriarConta.setText("Criar Conta");
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}