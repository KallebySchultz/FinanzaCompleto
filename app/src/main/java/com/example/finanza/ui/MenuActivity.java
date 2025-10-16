package com.example.finanza.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.RadioGroup;
import com.example.finanza.network.SyncService;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;

public class MenuActivity extends AppCompatActivity {
    private FrameLayout categoriasPanel;
    private EditText inputNomeCategoria;
    private RadioGroup tipoGroup;
    private Button btnSalvarCategoria;
    private Button btnVoltarPainel;
    private AppDatabase db;
    private SyncService syncService;
    private int usuarioIdAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = AppDatabase.getDatabase(getApplicationContext());

        // Inicializar serviço de sincronização
        syncService = SyncService.getInstance(this);

        // Obter usuário da intent ou de SharedPreferences
        usuarioIdAtual = getIntent().getIntExtra("usuarioId", -1);
        if (usuarioIdAtual == -1) {
            SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
            usuarioIdAtual = prefs.getInt("usuarioId", -1);
            if (usuarioIdAtual == -1) {
                // Usuário não autenticado, voltar para login
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return;
            }
        }

        ImageView navHome = findViewById(R.id.nav_home);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, com.example.finanza.MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        ImageView navAccounts = findViewById(R.id.nav_accounts);
        if (navAccounts != null) {
            navAccounts.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, AccountsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        ImageView navMovements = findViewById(R.id.nav_movements);
        if (navMovements != null) {
            navMovements.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, MovementsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        ImageView navMenu = findViewById(R.id.nav_menu);
        if (navMenu != null) {
            navMenu.setOnClickListener(v -> {
                // Já está na tela de menu
            });
        }

        highlightBottomNav();

        categoriasPanel = findViewById(R.id.categorias_panel);
        inputNomeCategoria = findViewById(R.id.input_nome_categoria);
        tipoGroup = findViewById(R.id.tipo_group);
        btnSalvarCategoria = findViewById(R.id.btn_salvar_categoria);
        btnVoltarPainel = findViewById(R.id.btn_voltar_painel);

        TextView btnCategorias = findViewById(R.id.btnCategorias);
        if (btnCategorias != null) {
            btnCategorias.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, CategoriaActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // Perfil functionality
        TextView btnPerfil = findViewById(R.id.btnPerfil);
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnVoltarPainel != null) {
            btnVoltarPainel.setOnClickListener(v -> {
                categoriasPanel.setVisibility(View.GONE);
                inputNomeCategoria.setText("");
                tipoGroup.check(R.id.radio_receita);
            });
        }

        if (btnSalvarCategoria != null) {
            btnSalvarCategoria.setOnClickListener(v -> {
                String nome = inputNomeCategoria.getText().toString().trim();
                String tipo = tipoGroup.getCheckedRadioButtonId() == R.id.radio_receita ? "receita" : "despesa";
                String corHex = tipo.equals("receita") ? "#22BB33" : "#FF2222";
                if (nome.isEmpty()) {
                    inputNomeCategoria.setError("Digite o nome");
                    return;
                }
                Categoria categoria = new Categoria();
                categoria.nome = nome;
                categoria.corHex = corHex;
                categoria.tipo = tipo;
                db.categoriaDao().inserir(categoria);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Categoria cadastrada com sucesso!")
                        .setPositiveButton("OK", (d, w) -> categoriasPanel.setVisibility(View.GONE))
                        .show();

                inputNomeCategoria.setText("");
                tipoGroup.check(R.id.radio_receita);
            });
        }

        // Sincronização manual (sempre visível e habilitado)
        TextView btnSyncServer = findViewById(R.id.btnSyncServer);
        if (btnSyncServer != null) {
            btnSyncServer.setVisibility(View.VISIBLE);
            btnSyncServer.setText("Sincronizar com Servidor");
            btnSyncServer.setOnClickListener(v -> realizarSincronizacao());
        }

        // Settings functionality
        TextView btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        // Logout functionality
        TextView btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> realizarLogout());
        }
    }

    private void highlightBottomNav() {
        ImageView navMenu = findViewById(R.id.nav_menu);
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMovements = findViewById(R.id.nav_movements);
        ImageView navAccounts = findViewById(R.id.nav_accounts);

        if (navMenu != null)
            navMenu.setColorFilter(ContextCompat.getColor(this, R.color.accentBlue));
        if (navHome != null)
            navHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navMovements != null)
            navMovements.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navAccounts != null)
            navAccounts.setColorFilter(ContextCompat.getColor(this, R.color.white));
    }

    private void realizarSincronizacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sincronização com Servidor");
        builder.setMessage("Deseja sincronizar seus dados com o servidor?");
        builder.setPositiveButton("Sincronizar", (dialog, which) -> {
            syncService.sincronizarTudo(usuarioIdAtual, new SyncService.SyncCallback() {
                @Override
                public void onSyncStarted() {
                    // Pode exibir um loading se quiser
                }

                @Override
                public void onSyncCompleted(boolean success, String message) {
                    // CORRIGIDO: Sempre mostrar UI pela thread principal
                    runOnUiThread(() -> {
                        AlertDialog.Builder resultDialog = new AlertDialog.Builder(MenuActivity.this);
                        resultDialog.setTitle(success ? "Sincronização Concluída" : "Erro na Sincronização");
                        resultDialog.setMessage(message);
                        resultDialog.setPositiveButton("OK", null);
                        resultDialog.show();
                    });
                }

                @Override
                public void onSyncProgress(String operation) {
                    // Pode exibir progresso se quiser
                }
            });
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void realizarLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sair");
        builder.setMessage("Tem certeza que deseja sair?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}