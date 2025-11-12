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

    /**
     * Realiza o processo de cadastro de novo usuário
     *
     * Fluxo completo de registro:
     * 1. Coleta dados dos campos de entrada
     * 2. Valida todos os campos (nome, email, senha, confirmação)
     * 3. Envia dados para AuthManager
     * 4. AuthManager tenta registrar no servidor desktop
     * 5. Se servidor offline, registra apenas localmente
     * 6. Redireciona para tela de login após sucesso
     *
     * Validações realizadas:
     * - Nome: obrigatório, não pode estar vazio
     * - Email: obrigatório, deve estar em formato válido (usar@ domínio.com)
     * - Senha: obrigatória, mínimo 6 caracteres
     * - Confirmação: deve ser idêntica à senha digitada
     *
     * Tratamento de erros:
     * - Exibe erro no campo específico com mensagem clara
     * - Interrompe processo se houver qualquer erro
     * - Reabilita botão em caso de falha de registro
     *
     * Feedback ao usuário:
     * - Desabilita botão durante processamento
     * - Altera texto do botão para "Criando conta..."
     * - Exibe toast de sucesso ou erro
     * - Redireciona automaticamente para login se sucesso
     */
    private void realizarCadastro() {
        // ========== FASE 1: COLETA DE DADOS ==========
        // Obtém texto dos campos com proteção contra null
        String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String senha = inputSenha.getText() != null ? inputSenha.getText().toString().trim() : "";
        String confirmarSenha = inputConfirmarSenha.getText() != null ? inputConfirmarSenha.getText().toString().trim() : "";

        // ========== FASE 2: LIMPEZA DE ERROS ANTERIORES ==========
        // Remove mensagens de erro de tentativas anteriores
        inputNome.setError(null);
        inputEmail.setError(null);
        inputSenha.setError(null);
        inputConfirmarSenha.setError(null);

        // ========== FASE 3: VALIDAÇÃO DE CAMPOS ==========
        boolean hasError = false;
        
        // Validação de nome
        if (nome.isEmpty()) {
            inputNome.setError("Digite o nome");
            hasError = true;
        }
        
        // Validação de email
        if (email.isEmpty()) {
            inputEmail.setError("Digite o email");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Valida formato usando padrão Android (verifica @ e domínio)
            inputEmail.setError("Email inválido");
            hasError = true;
        }
        
        // Validação de senha
        if (senha.isEmpty()) {
            inputSenha.setError("Digite a senha");
            hasError = true;
        } else if (senha.length() < 6) {
            // Senha mínima de 6 caracteres para segurança básica
            inputSenha.setError("Senha deve ter pelo menos 6 caracteres");
            hasError = true;
        }
        
        // Validação de confirmação de senha
        if (confirmarSenha.isEmpty()) {
            inputConfirmarSenha.setError("Confirme a senha");
            hasError = true;
        } else if (!senha.equals(confirmarSenha)) {
            // Verifica se senhas são idênticas
            inputConfirmarSenha.setError("Senhas não coincidem");
            hasError = true;
        }

        // Interrompe se houver algum erro de validação
        if (hasError) return;

        // ========== FASE 4: FEEDBACK VISUAL E DESABILITAÇÃO DO BOTÃO ==========
        // Previne múltiplos cliques durante processamento
        btnCriarConta.setEnabled(false);
        btnCriarConta.setText("Criando conta...");

        // ========== FASE 5: REGISTRO VIA AUTHMANAGER ==========
        // AuthManager tenta registrar no servidor ou localmente se offline
        authManager.registrar(nome, email, senha, new AuthManager.AuthCallback() {
            /**
             * Callback de sucesso - cadastro foi realizado
             *
             * @param usuario Objeto do usuário criado
             */
            @Override
            public void onSuccess(Usuario usuario) {
                // Executar na UI thread para atualizar interface
                runOnUiThread(() -> {
                    // Exibir mensagem de sucesso
                    Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                    
                    // Redirecionar para tela de login para que usuário faça login
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Encerra RegisterActivity para não voltar ao pressionar voltar
                });
            }

            /**
             * Callback de erro - falha no cadastro
             *
             * Possíveis causas:
             * - Email já cadastrado no sistema
             * - Falha de conexão com servidor
             * - Dados inválidos rejeitados pelo servidor
             * - Erro ao salvar no banco local
             *
             * @param error Mensagem descritiva do erro
             */
            @Override
            public void onError(String error) {
                // Executar na UI thread para atualizar interface
                runOnUiThread(() -> {
                    // Reabilitar botão para nova tentativa
                    btnCriarConta.setEnabled(true);
                    btnCriarConta.setText("Criar Conta");
                    
                    // Exibir mensagem de erro detalhada
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}