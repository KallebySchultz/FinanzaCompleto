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

/**
 * LoginActivity - Tela de autenticação do usuário
 *
 * Esta activity é responsável por autenticar o usuário no sistema Finanza.
 * Oferece funcionalidades de login, cadastro e recuperação de senha.
 *
 * Funcionalidades principais:
 * - Login com email e senha
 * - Validação automática de sessão existente
 * - Navegação para tela de registro
 * - Recuperação de senha via email
 * - Acesso às configurações de servidor
 *
 * Fluxo:
 * 1. Verifica se usuário já está logado (sessão ativa)
 * 2. Se sim, redireciona direto para MainActivity
 * 3. Se não, exibe formulário de login
 * 4. Após login bem-sucedido, salva sessão e vai para MainActivity
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class LoginActivity extends AppCompatActivity {

    // Gerenciador de autenticação (Singleton)
    private AuthManager authManager;
    
    // Campos de entrada do formulário
    private TextInputEditText inputEmail, inputSenha;
    
    // Botões e links da interface
    private Button btnLogin;
    private TextView txtCriarConta, txtRecuperarSenha;
    private ImageView btnConfiguracoes;

    /**
     * Método onCreate - Inicialização da tela de login
     *
     * Responsável por:
     * - Configurar aparência da interface (cores de barra de status)
     * - Verificar se usuário já está autenticado
     * - Inicializar componentes visuais
     * - Configurar listeners de eventos
     *
     * @param savedInstanceState Estado salvo da atividade (se houver)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configurar cores da interface para seguir o tema do app
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);

        // Obter instância do gerenciador de autenticação
        authManager = AuthManager.getInstance(this);

        // Verificar se já existe uma sessão ativa
        if (authManager.isLoggedIn()) {
            // Usuário já autenticado, redirecionar para tela principal
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuarioId", authManager.getLoggedUserId());
            startActivity(intent);
            finish();
            return;
        }

        // Vincular elementos visuais às variáveis
        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);
        btnLogin = findViewById(R.id.btn_login);
        txtCriarConta = findViewById(R.id.txt_criar_conta);
        txtRecuperarSenha = findViewById(R.id.txt_recuperar_senha);
        btnConfiguracoes = findViewById(R.id.btn_configuracoes);

        // Configurar ação do botão de login
        btnLogin.setOnClickListener(v -> realizarLogin());
        
        // Configurar navegação para tela de registro
        txtCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // Configurar recuperação de senha
        txtRecuperarSenha.setOnClickListener(v -> mostrarDialogRecuperarSenha());

        // Configurar acesso às configurações
        btnConfiguracoes.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Realiza o processo de login do usuário
     *
     * Valida os campos de email e senha, e então:
     * 1. Envia credenciais para o AuthManager
     * 2. AuthManager comunica com o servidor via ServerClient
     * 3. Servidor valida credenciais no banco MySQL
     * 4. Em caso de sucesso, salva sessão e redireciona para MainActivity
     * 5. Em caso de erro, exibe mensagem de erro
     *
     * Validações:
     * - Email não pode estar vazio
     * - Senha não pode estar vazia
     */
    private void realizarLogin() {
        // Obter valores dos campos de entrada
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String senha = inputSenha.getText() != null ? inputSenha.getText().toString().trim() : "";

        // Limpar mensagens de erro anteriores
        inputEmail.setError(null);
        inputSenha.setError(null);

        // Validar campos obrigatórios
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

        // Desabilitar botão e mostrar feedback visual
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        // Executar login via AuthManager (comunicação assíncrona com servidor)
        authManager.login(email, senha, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(Usuario usuario) {
                // Login bem-sucedido, redirecionar para tela principal
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
                // Login falhou, exibir erro e reabilitar botão
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Entrar");
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Exibe diálogo para recuperação de senha
     *
     * Permite que o usuário solicite recuperação de senha informando seu email.
     * O servidor irá enviar instruções de recuperação para o email cadastrado.
     *
     * Funcionalidade:
     * 1. Exibe diálogo com campo de email
     * 2. Valida se email está preenchido e em formato válido
     * 3. Envia solicitação ao servidor via ServerClient
     * 4. Servidor processa e envia email de recuperação
     * 5. Exibe mensagem de confirmação ou erro
     */
    private void mostrarDialogRecuperarSenha() {
        // Inflar layout do diálogo customizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recuperar_senha, null);
        
        // Vincular elementos do diálogo
        TextInputEditText inputEmailRecovery = dialogView.findViewById(R.id.input_email_recovery);
        Button btnEnviarRecovery = dialogView.findViewById(R.id.btn_enviar_recovery);
        Button btnCancelarRecovery = dialogView.findViewById(R.id.btn_cancelar_recovery);
        
        // Criar e configurar o diálogo
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        // Configurar botão de cancelar
        btnCancelarRecovery.setOnClickListener(v -> dialog.dismiss());
        
        // Configurar botão de enviar
        btnEnviarRecovery.setOnClickListener(v -> {
            // Obter email informado
            String email = inputEmailRecovery.getText() != null ? 
                inputEmailRecovery.getText().toString().trim() : "";
            
            // Validar campo de email
            if (email.isEmpty()) {
                inputEmailRecovery.setError("Digite o email");
                return;
            }
            
            // Validar formato básico de email
            if (!email.contains("@")) {
                inputEmailRecovery.setError("Digite um email válido");
                return;
            }
            
            // Desabilitar botão e mostrar feedback
            btnEnviarRecovery.setEnabled(false);
            btnEnviarRecovery.setText("Enviando...");
            
            // Enviar solicitação de recuperação ao servidor
            ServerClient serverClient = ServerClient.getInstance(this);
            serverClient.recuperarSenha(email, new ServerClient.ServerCallback<String>() {
                @Override
                public void onSuccess(String mensagem) {
                    // Recuperação solicitada com sucesso
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, mensagem, Toast.LENGTH_LONG).show();
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Erro ao solicitar recuperação
                    runOnUiThread(() -> {
                        btnEnviarRecovery.setEnabled(true);
                        btnEnviarRecovery.setText("Enviar");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
        
        // Exibir o diálogo
        dialog.show();
    }
}