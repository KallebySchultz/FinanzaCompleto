package com.example.finanza.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Lancamento;
import com.example.finanza.MainActivity;
import com.example.finanza.network.SyncService;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity para gerenciamento de categorias
 */
public class CategoriaActivity extends AppCompatActivity {

    private AppDatabase db;
    private SyncService syncService;
    private LinearLayout categoriasList;
    private Button btnAdicionarCategoria, btnMostrarReceitas, btnMostrarDespesas;
    private List<Categoria> todasCategorias;
    private List<Categoria> categoriasExibidas;
    private String filtroTipo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));

        db = AppDatabase.getDatabase(getApplicationContext());

        // Inicializar sync service  
        syncService = SyncService.getInstance(this);

        categoriasList = findViewById(R.id.categorias_list);
        btnAdicionarCategoria = findViewById(R.id.btnAdicionarCategoria);
        btnMostrarReceitas = findViewById(R.id.btnMostrarReceitas);
        btnMostrarDespesas = findViewById(R.id.btnMostrarDespesas);

        todasCategorias = new ArrayList<>();
        categoriasExibidas = new ArrayList<>();

        setupNavigation();

        btnAdicionarCategoria.setOnClickListener(v -> {
            mostrarDialogoNovaCategoria("");
        });

        btnMostrarReceitas.setOnClickListener(v -> {
            filtroTipo = "receita";
            atualizarCategoriasFiltradas();
            btnMostrarReceitas.setBackground(getResources().getDrawable(R.drawable.button_blue));
            btnMostrarDespesas.setBackground(getResources().getDrawable(R.drawable.button_gray));
        });

        btnMostrarDespesas.setOnClickListener(v -> {
            filtroTipo = "despesa";
            atualizarCategoriasFiltradas();
            btnMostrarReceitas.setBackground(getResources().getDrawable(R.drawable.button_gray));
            btnMostrarDespesas.setBackground(getResources().getDrawable(R.drawable.button_blue));
        });

        filtroTipo = "";
        carregarCategorias();
    }

    private void setupNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMenu = findViewById(R.id.nav_menu);
        ImageView navAccounts = findViewById(R.id.nav_accounts);
        ImageView navMovements = findViewById(R.id.nav_movements);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
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

        if (navAccounts != null) {
            navAccounts.setOnClickListener(v -> {
                Intent intent = new Intent(this, AccountsActivity.class);
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
    }

    private void carregarCategorias() {
        todasCategorias.clear();
        todasCategorias.addAll(db.categoriaDao().listarTodas());
        atualizarCategoriasFiltradas();
    }

    private void atualizarCategoriasFiltradas() {
        categoriasExibidas.clear();
        if (filtroTipo.isEmpty()) {
            categoriasExibidas.addAll(todasCategorias);
        } else {
            for (Categoria categoria : todasCategorias) {
                if (categoria.tipo.equals(filtroTipo)) {
                    categoriasExibidas.add(categoria);
                }
            }
        }
        atualizarListaCategorias();
    }

    private void atualizarListaCategorias() {
        categoriasList.removeAllViews();

        for (Categoria categoria : categoriasExibidas) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(12, 16, 12, 16);
            item.setBackground(getResources().getDrawable(R.drawable.bg_transaction_item));

            ImageView icon = new ImageView(this);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(32, 32);
            iconParams.setMargins(0, 0, 10, 0);
            icon.setLayoutParams(iconParams);
            icon.setImageResource(R.drawable.ic_arrows);

            if ("receita".equals(categoria.tipo)) {
                icon.setBackground(getResources().getDrawable(R.drawable.bg_account_icon_circle_green));
                icon.setColorFilter(getResources().getColor(R.color.positiveGreen));
            } else {
                icon.setBackground(getResources().getDrawable(R.drawable.bg_account_icon_circle_purple));
                icon.setColorFilter(getResources().getColor(R.color.negativeRed));
            }
            icon.setPadding(4, 4, 4, 4);

            LinearLayout infoBox = new LinearLayout(this);
            infoBox.setOrientation(LinearLayout.VERTICAL);
            infoBox.setPadding(10, 0, 0, 0);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            infoParams.weight = 1;
            infoBox.setLayoutParams(infoParams);

            TextView name = new TextView(this);
            name.setText(categoria.nome);
            name.setTextColor(getResources().getColor(R.color.white));
            name.setTextSize(16);
            name.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tipo = new TextView(this);
            tipo.setText(categoria.tipo.toUpperCase());
            tipo.setTextColor(getResources().getColor(
                    "receita".equals(categoria.tipo) ? R.color.positiveGreen : R.color.negativeRed));
            tipo.setTextSize(12);
            tipo.setTypeface(null, android.graphics.Typeface.BOLD);

            infoBox.addView(name);
            infoBox.addView(tipo);

            item.addView(icon);
            item.addView(infoBox);

            final Categoria finalCategoria = categoria;
            item.setOnClickListener(v -> editarCategoria(finalCategoria));
            item.setOnLongClickListener(v -> {
                confirmarExclusaoCategoria(finalCategoria);
                return true;
            });

            categoriasList.addView(item);
        }
    }

    private void mostrarDialogoNovaCategoria(String nomeInicial) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_categoria, null);

        EditText inputNome = dialogView.findViewById(R.id.input_nome);
        RadioGroup tipoGroup = dialogView.findViewById(R.id.tipo_group);
        Button btnSalvar = dialogView.findViewById(R.id.btn_salvar);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        inputNome.setText(nomeInicial);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
            String tipo = tipoGroup.getCheckedRadioButtonId() == R.id.radio_receita ? "receita" : "despesa";
            if (!nome.isEmpty()) {
                Categoria categoria = new Categoria();
                categoria.nome = nome;
                categoria.tipo = tipo;
                categoria.corHex = tipo.equals("receita") ? "#22BB33" : "#FF2222";
                
                // Use SyncService instead of direct DAO call
                syncService.adicionarCategoria(categoria, new SyncService.SyncCallback() {
                    @Override
                    public void onSyncStarted() {
                        // Optional: show loading indicator
                    }

                    @Override
                    public void onSyncCompleted(boolean success, String message) {
                        runOnUiThread(() -> {
                            if (success) {
                                carregarCategorias(); // ATUALIZA IMEDIATAMENTE APÓS ADICIONAR
                                dialog.dismiss();
                                Toast.makeText(CategoriaActivity.this, "Categoria criada!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CategoriaActivity.this, "Erro ao criar categoria: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSyncProgress(String operation) {
                        // Optional: show progress
                    }
                });
            } else {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
    }

    private void editarCategoria(Categoria categoria) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_categoria, null);

        EditText inputNome = dialogView.findViewById(R.id.input_nome);
        RadioGroup tipoGroup = dialogView.findViewById(R.id.tipo_group);
        Button btnSalvar = dialogView.findViewById(R.id.btn_salvar);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        inputNome.setText(categoria.nome);
        if ("receita".equals(categoria.tipo)) {
            tipoGroup.check(R.id.radio_receita);
        } else {
            tipoGroup.check(R.id.radio_despesa);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnSalvar.setOnClickListener(v -> {
            String novoNome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
            String novoTipo = tipoGroup.getCheckedRadioButtonId() == R.id.radio_receita ? "receita" : "despesa";
            if (!novoNome.isEmpty()) {
                categoria.nome = novoNome;
                categoria.tipo = novoTipo;
                categoria.corHex = novoTipo.equals("receita") ? "#22BB33" : "#FF2222";
                
                // Use SyncService instead of direct DAO call
                syncService.atualizarCategoria(categoria, new SyncService.SyncCallback() {
                    @Override
                    public void onSyncStarted() {
                        // Optional: show loading indicator
                    }

                    @Override
                    public void onSyncCompleted(boolean success, String message) {
                        runOnUiThread(() -> {
                            if (success) {
                                carregarCategorias();
                                dialog.dismiss();
                                Toast.makeText(CategoriaActivity.this, "Categoria atualizada!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CategoriaActivity.this, "Erro ao atualizar categoria: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSyncProgress(String operation) {
                        // Optional: show progress
                    }
                });
            } else {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
    }

    private void confirmarExclusaoCategoria(Categoria categoria) {
        List<Lancamento> lancamentos = db.lancamentoDao().listarPorCategoria(categoria.id);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_categoria, null);

        TextView deleteMessage = dialogView.findViewById(R.id.delete_message);
        Button btnExcluir = dialogView.findViewById(R.id.btn_excluir);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        String message = "Deseja excluir a categoria '" + categoria.nome + "'?";
        if (!lancamentos.isEmpty()) {
            message += "\n\nATENÇÃO: Esta categoria possui " + lancamentos.size() +
                    " transação(ões). Elas também serão excluídas.";
        }
        deleteMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnExcluir.setOnClickListener(v -> {
            // Use SyncService instead of direct DAO calls
            syncService.deletarCategoria(categoria, new SyncService.SyncCallback() {
                @Override
                public void onSyncStarted() {
                    // Optional: show loading indicator
                }

                @Override
                public void onSyncCompleted(boolean success, String message) {
                    runOnUiThread(() -> {
                        if (success) {
                            carregarCategorias();
                            dialog.dismiss();
                            Toast.makeText(CategoriaActivity.this, "Categoria excluída!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CategoriaActivity.this, "Erro ao excluir categoria: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSyncProgress(String operation) {
                    // Optional: show progress
                }
            });
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Sincroniza dados e atualiza UI
        if (syncService != null) {
            // Obter usuário logado das SharedPreferences
            android.content.SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
            int usuarioId = prefs.getInt("usuarioId", 1);
            
            syncService.sincronizarTudo(usuarioId, new SyncService.SyncCallback() {
                @Override
                public void onSyncStarted() {
                    // Opcional: mostrar indicador de sync
                }

                @Override
                public void onSyncCompleted(boolean success, String message) {
                    // Atualiza UI na thread principal após sincronização
                    runOnUiThread(() -> carregarCategorias());
                }

                @Override
                public void onSyncProgress(String operation) {
                    // Opcional: mostrar progresso
                }
            });
        }
    }
}