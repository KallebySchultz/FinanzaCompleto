package com.example.finanza.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Usuario;
import com.example.finanza.network.AuthManager;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private AppDatabase db;
    private AuthManager authManager;
    private int usuarioIdAtual;
    private Usuario usuarioAtual;
    
    private TextView txtNomeUsuario;
    private TextView txtEmailUsuario;
    private TextView txtDataCriacao;
    private Button btnEditarPerfil;
    private Button btnExcluirConta;
    private Button btnSair;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Configurar barra de status
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);

        // Inicializar database e AuthManager
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
                
        authManager = AuthManager.getInstance(this);

        // Obter usuário da intent ou do AuthManager
        usuarioIdAtual = getIntent().getIntExtra("usuarioId", -1);
        if (usuarioIdAtual == -1) {
            usuarioIdAtual = authManager.getLoggedUserId();
        }
        
        if (usuarioIdAtual == -1) {
            // Usuário não autenticado, voltar para login
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Inicializar views
        txtNomeUsuario = findViewById(R.id.txt_nome_usuario);
        txtEmailUsuario = findViewById(R.id.txt_email_usuario);
        txtDataCriacao = findViewById(R.id.txt_data_criacao);
        btnEditarPerfil = findViewById(R.id.btn_editar_perfil);
        btnExcluirConta = findViewById(R.id.btn_excluir_conta);
        btnSair = findViewById(R.id.btn_sair);

        // Carregar dados do usuário
        carregarDadosUsuario();

        // Configurar listeners
        btnEditarPerfil.setOnClickListener(v -> mostrarDialogEditarPerfil());
        btnExcluirConta.setOnClickListener(v -> confirmarExclusaoConta());
        
        // Adicionar listener para logout se o botão existir
        if (btnSair != null) {
            btnSair.setOnClickListener(v -> confirmarLogout());
        }

        // Configurar navegação
        configurarNavegacao();
    }

    private void carregarDadosUsuario() {
        usuarioAtual = db.usuarioDao().buscarPorId(usuarioIdAtual);
        if (usuarioAtual != null) {
            txtNomeUsuario.setText(usuarioAtual.nome);
            txtEmailUsuario.setText(usuarioAtual.email);
            
            // Configurar avatar inicial
            TextView txtAvatarInicial = findViewById(R.id.txt_avatar_inicial);
            if (txtAvatarInicial != null && usuarioAtual.nome != null && !usuarioAtual.nome.isEmpty()) {
                txtAvatarInicial.setText(String.valueOf(usuarioAtual.nome.charAt(0)).toUpperCase());
            }
            
            // Formatar data de criação
            if (usuarioAtual.dataCriacao > 0) {
                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");
                String dataFormatada = formatter.format(new java.util.Date(usuarioAtual.dataCriacao));
                txtDataCriacao.setText("Membro desde: " + dataFormatada);
            } else {
                txtDataCriacao.setText("Membro desde: Data não informada");
            }
        }
    }

    private void mostrarDialogEditarPerfil() {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);

        TextInputEditText inputNome = dialogView.findViewById(R.id.input_nome_usuario);
        TextInputEditText inputEmail = dialogView.findViewById(R.id.input_email_usuario);
        TextInputEditText inputSenhaAtual = dialogView.findViewById(R.id.input_senha_atual);
        TextInputEditText inputNovaSenha = dialogView.findViewById(R.id.input_nova_senha);
        Button btnSalvar = dialogView.findViewById(R.id.btn_salvar_perfil);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar_edicao);

        // Preencher dados atuais
        inputNome.setText(usuarioAtual.nome);
        inputEmail.setText(usuarioAtual.email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnSalvar.setOnClickListener(v -> {
            String novoNome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
            String novoEmail = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
            String senhaAtual = inputSenhaAtual.getText() != null ? inputSenhaAtual.getText().toString().trim() : "";
            String novaSenha = inputNovaSenha.getText() != null ? inputNovaSenha.getText().toString().trim() : "";

            // Validações
            if (novoNome.isEmpty()) {
                inputNome.setError("Digite o nome");
                return;
            }
            if (novoEmail.isEmpty()) {
                inputEmail.setError("Digite o email");
                return;
            }
            if (senhaAtual.isEmpty()) {
                inputSenhaAtual.setError("Digite a senha atual para confirmar");
                return;
            }

            // Verificar senha atual
            if (!senhaAtual.equals(usuarioAtual.senha)) {
                inputSenhaAtual.setError("Senha atual incorreta");
                return;
            }

            // Verificar se email já está em uso por outro usuário
            Usuario usuarioExistente = db.usuarioDao().buscarPorEmail(novoEmail);
            if (usuarioExistente != null && usuarioExistente.id != usuarioAtual.id) {
                inputEmail.setError("Este email já está em uso");
                return;
            }

            // Atualizar dados
            usuarioAtual.nome = novoNome;
            usuarioAtual.email = novoEmail;
            if (!novaSenha.isEmpty()) {
                usuarioAtual.senha = novaSenha;
            }

            db.usuarioDao().atualizar(usuarioAtual);
            carregarDadosUsuario();
            dialog.dismiss();
            Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
    }

    private void confirmarExclusaoConta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Conta");
        builder.setMessage("Tem certeza que deseja excluir sua conta?\n\nEsta ação é irreversível e todos os seus dados serão perdidos.");
        builder.setPositiveButton("Excluir", (dialog, which) -> mostrarDialogConfirmacaoExclusao());
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogConfirmacaoExclusao() {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_delete_account, null);

        TextInputEditText inputSenha = dialogView.findViewById(R.id.input_senha_confirmacao);
        Button btnConfirmar = dialogView.findViewById(R.id.btn_confirmar_exclusao);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar_exclusao);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnConfirmar.setOnClickListener(v -> {
            String senha = inputSenha.getText() != null ? inputSenha.getText().toString().trim() : "";

            if (senha.isEmpty()) {
                inputSenha.setError("Digite sua senha para confirmar");
                return;
            }

            if (!senha.equals(usuarioAtual.senha)) {
                inputSenha.setError("Senha incorreta");
                return;
            }

            // Excluir todos os dados do usuário
            excluirContaUsuario();
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
    }

    private void excluirContaUsuario() {
        try {
            // Excluir lançamentos do usuário
            db.lancamentoDao().excluirPorUsuario(usuarioIdAtual);
            
            // Excluir contas do usuário
            db.contaDao().excluirPorUsuario(usuarioIdAtual);
            
            // Excluir categorias do usuário (se existir esse método)
            // db.categoriaDao().excluirPorUsuario(usuarioIdAtual);
            
            // Excluir o usuário
            db.usuarioDao().deletar(usuarioAtual);

            // Fazer logout usando AuthManager
            authManager.logout();

            Toast.makeText(this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();

            // Voltar para a tela de login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao excluir conta: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Confirma logout do usuário
     */
    private void confirmarLogout() {
        new AlertDialog.Builder(this)
            .setTitle("Sair da conta")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sair", (dialog, which) -> {
                // Fazer logout usando AuthManager
                authManager.logout();
                
                Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
                
                // Voltar para tela de login
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void configurarNavegacao() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMovements = findViewById(R.id.nav_movements);
        ImageView navAccounts = findViewById(R.id.nav_accounts);
        ImageView navMenu = findViewById(R.id.nav_menu);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.finanza.MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navMovements != null) {
            navMovements.setOnClickListener(v -> {
                Intent intent = new Intent(this, MovementsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navAccounts != null) {
            navAccounts.setOnClickListener(v -> {
                Intent intent = new Intent(this, AccountsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navMenu != null) {
            navMenu.setOnClickListener(v -> {
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // Destacar navegação atual (nenhuma, pois é uma nova tela)
        if (navHome != null) navHome.setColorFilter(getResources().getColor(R.color.white));
        if (navMovements != null) navMovements.setColorFilter(getResources().getColor(R.color.white));
        if (navAccounts != null) navAccounts.setColorFilter(getResources().getColor(R.color.white));
        if (navMenu != null) navMenu.setColorFilter(getResources().getColor(R.color.white));
    }
}