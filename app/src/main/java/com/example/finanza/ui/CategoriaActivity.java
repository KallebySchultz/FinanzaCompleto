package com.example.finanza.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Lancamento;
import com.example.finanza.MainActivity;
import com.example.finanza.ui.AccountsActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoriaActivity extends AppCompatActivity {

    private AppDatabase db;
    private ListView listCategorias;
    private EditText etNovaCategoria;
    private Button btnAdicionarCategoria;
    private ArrayAdapter<String> adapter;
    private List<Categoria> categorias;
    private List<String> categoriasNomes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .allowMainThreadQueries()
                .build();

        listCategorias = findViewById(R.id.listCategorias);
        etNovaCategoria = findViewById(R.id.etNovaCategoria);
        btnAdicionarCategoria = findViewById(R.id.btnAdicionarCategoria);

        categorias = new ArrayList<>();
        categoriasNomes = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriasNomes);
        listCategorias.setAdapter(adapter);

        // Navigation setup
        setupNavigation();

        btnAdicionarCategoria.setOnClickListener(v -> {
            String nomeCategoria = etNovaCategoria.getText().toString().trim();
            if (!nomeCategoria.isEmpty()) {
                // Por padrão, criar como categoria de despesa
                Categoria categoria = new Categoria();
                categoria.nome = nomeCategoria;
                categoria.corHex = "#FF2222";
                categoria.tipo = "despesa";
                db.categoriaDao().inserir(categoria);
                etNovaCategoria.setText("");
                carregarCategorias();
            }
        });

        // Clique longo para excluir categoria
        listCategorias.setOnItemLongClickListener((parent, view, position, id) -> {
            Categoria categoria = categorias.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Excluir Categoria")
                    .setMessage("Deseja excluir a categoria '" + categoria.nome + "'?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        // Check if category has transactions
                        List<Lancamento> lancamentos = db.lancamentoDao().listarPorCategoria(categoria.id);
                        if (!lancamentos.isEmpty()) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Categoria em uso")
                                    .setMessage("Esta categoria possui " + lancamentos.size() + 
                                              " transação(ões). Não é possível excluí-la.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else {
                            db.categoriaDao().deletar(categoria);
                            carregarCategorias();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });

        // Clique simples para editar categoria
        listCategorias.setOnItemClickListener((parent, view, position, id) -> {
            editarCategoria(categorias.get(position));
        });

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
        categorias.clear();
        categoriasNomes.clear();

        List<Categoria> todasCategorias = db.categoriaDao().listarTodas();
        for (Categoria categoria : todasCategorias) {
            categorias.add(categoria);
            categoriasNomes.add(categoria.nome + " (" + categoria.tipo + ")");
        }

        adapter.notifyDataSetChanged();
    }

    private void editarCategoria(Categoria categoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Categoria");

        // Create custom layout for edit dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome da categoria");
        inputNome.setText(categoria.nome);
        layout.addView(inputNome);

        // Radio buttons for type
        final RadioGroup tipoGroup = new RadioGroup(this);
        RadioButton radioReceita = new RadioButton(this);
        radioReceita.setText("Receita");
        radioReceita.setId(1);
        RadioButton radioDespesa = new RadioButton(this);
        radioDespesa.setText("Despesa");
        radioDespesa.setId(2);
        
        tipoGroup.addView(radioReceita);
        tipoGroup.addView(radioDespesa);
        
        if ("receita".equals(categoria.tipo)) {
            tipoGroup.check(1);
        } else {
            tipoGroup.check(2);
        }
        
        layout.addView(tipoGroup);
        builder.setView(layout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoNome = inputNome.getText().toString().trim();
            String novoTipo = tipoGroup.getCheckedRadioButtonId() == 1 ? "receita" : "despesa";
            
            if (!novoNome.isEmpty()) {
                categoria.nome = novoNome;
                categoria.tipo = novoTipo;
                categoria.corHex = novoTipo.equals("receita") ? "#22BB33" : "#FF2222";
                db.categoriaDao().atualizar(categoria);
                carregarCategorias();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}