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
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Lancamento;
import com.example.finanza.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoriaActivity extends AppCompatActivity {

    private AppDatabase db;
    private LinearLayout categoriasList;
    private EditText etNovaCategoria, etFiltroCategoria;
    private Button btnAdicionarCategoria, btnLimparFiltro;
    private List<Categoria> todasCategorias;
    private List<Categoria> categoriasFiltradas;

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

        categoriasList = findViewById(R.id.categorias_list);
        etNovaCategoria = findViewById(R.id.etNovaCategoria);
        etFiltroCategoria = findViewById(R.id.etFiltroCategoria);
        btnAdicionarCategoria = findViewById(R.id.btnAdicionarCategoria);
        btnLimparFiltro = findViewById(R.id.btnLimparFiltro);

        todasCategorias = new ArrayList<>();
        categoriasFiltradas = new ArrayList<>();

        // Navigation setup
        setupNavigation();

        // Filter functionality
        etFiltroCategoria.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarCategorias(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnLimparFiltro.setOnClickListener(v -> {
            etFiltroCategoria.setText("");
            carregarCategorias();
        });

        btnAdicionarCategoria.setOnClickListener(v -> {
            String nomeCategoria = etNovaCategoria.getText().toString().trim();
            if (!nomeCategoria.isEmpty()) {
                mostrarDialogoNovaCategoria(nomeCategoria);
            } else {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show();
            }
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

    private void filtrarCategorias(String filtro) {
        categoriasFiltradas.clear();
        if (filtro.isEmpty()) {
            categoriasFiltradas.addAll(todasCategorias);
        } else {
            for (Categoria categoria : todasCategorias) {
                if (categoria.nome.toLowerCase().contains(filtro.toLowerCase()) ||
                    categoria.tipo.toLowerCase().contains(filtro.toLowerCase())) {
                    categoriasFiltradas.add(categoria);
                }
            }
        }
        atualizarListaCategorias();
    }

    private void carregarCategorias() {
        todasCategorias.clear();
        todasCategorias.addAll(db.categoriaDao().listarTodas());
        categoriasFiltradas.clear();
        categoriasFiltradas.addAll(todasCategorias);
        atualizarListaCategorias();
    }

    private void atualizarListaCategorias() {
        categoriasList.removeAllViews();

        for (Categoria categoria : categoriasFiltradas) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(12, 16, 12, 16);
            item.setBackground(getResources().getDrawable(R.drawable.bg_account_icon_circle_green));

            // Icon with color based on type
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

            // Add click listener for edit functionality
            final Categoria finalCategoria = categoria;
            item.setOnClickListener(v -> editarCategoria(finalCategoria));
            
            // Add long click listener for delete functionality
            item.setOnLongClickListener(v -> {
                confirmarExclusaoCategoria(finalCategoria);
                return true;
            });

            categoriasList.addView(item);
        }
    }

    private void mostrarDialogoNovaCategoria(String nomeInicial) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nova Categoria");

        // Create a styled layout that matches the transaction launch panel
        FrameLayout frameLayout = new FrameLayout(this);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        
        // Title
        TextView title = new TextView(this);
        title.setText("Nova Categoria");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = 16;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Input field
        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome da categoria");
        inputNome.setText(nomeInicial);
        inputNome.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setTextColorHint(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams.bottomMargin = 16;
        inputNome.setLayoutParams(inputParams);
        layout.addView(inputNome);

        // Radio buttons for type
        final RadioGroup tipoGroup = new RadioGroup(this);
        tipoGroup.setOrientation(RadioGroup.HORIZONTAL);
        tipoGroup.setGravity(Gravity.CENTER);
        
        RadioButton radioReceita = new RadioButton(this);
        radioReceita.setText("Receita");
        radioReceita.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        radioReceita.setId(1);
        radioReceita.setChecked(true);
        
        RadioButton radioDespesa = new RadioButton(this);
        radioDespesa.setText("Despesa");
        radioDespesa.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        radioDespesa.setId(2);
        LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        radioParams.leftMargin = 24;
        radioDespesa.setLayoutParams(radioParams);
        
        tipoGroup.addView(radioReceita);
        tipoGroup.addView(radioDespesa);
        
        LinearLayout.LayoutParams tipoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipoParams.bottomMargin = 16;
        tipoGroup.setLayoutParams(tipoParams);
        layout.addView(tipoGroup);

        // Styled buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(android.view.Gravity.CENTER);

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar");
        btnSalvar.setTextColor(getResources().getColor(R.color.white));
        btnSalvar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnSalvar.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnSalvarParams = new LinearLayout.LayoutParams(
            0, 48);
        btnSalvarParams.weight = 1;
        btnSalvarParams.rightMargin = 8;
        btnSalvar.setLayoutParams(btnSalvarParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
            0, 48);
        btnCancelarParams.weight = 1;
        btnCancelarParams.leftMargin = 8;
        btnCancelar.setLayoutParams(btnCancelarParams);

        buttonLayout.addView(btnSalvar);
        buttonLayout.addView(btnCancelar);
        layout.addView(buttonLayout);

        frameLayout.addView(layout);
        builder.setView(frameLayout);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        btnSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText().toString().trim();
            String tipo = tipoGroup.getCheckedRadioButtonId() == 1 ? "receita" : "despesa";
            
            if (!nome.isEmpty()) {
                Categoria categoria = new Categoria();
                categoria.nome = nome;
                categoria.tipo = tipo;
                categoria.corHex = tipo.equals("receita") ? "#22BB33" : "#FF2222";
                db.categoriaDao().inserir(categoria);
                carregarCategorias();
                etNovaCategoria.setText("");
                dialog.dismiss();
                Toast.makeText(this, "Categoria criada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void editarCategoria(Categoria categoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Categoria");

        // Create a styled layout that matches the transaction launch panel
        FrameLayout frameLayout = new FrameLayout(this);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        
        // Title
        TextView title = new TextView(this);
        title.setText("Editar Categoria");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = 16;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Input field
        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome da categoria");
        inputNome.setText(categoria.nome);
        inputNome.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setTextColorHint(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams.bottomMargin = 16;
        inputNome.setLayoutParams(inputParams);
        layout.addView(inputNome);

        // Radio buttons for type
        final RadioGroup tipoGroup = new RadioGroup(this);
        tipoGroup.setOrientation(RadioGroup.HORIZONTAL);
        tipoGroup.setGravity(Gravity.CENTER);
        
        RadioButton radioReceita = new RadioButton(this);
        radioReceita.setText("Receita");
        radioReceita.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        radioReceita.setId(1);
        
        RadioButton radioDespesa = new RadioButton(this);
        radioDespesa.setText("Despesa");
        radioDespesa.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        radioDespesa.setId(2);
        LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        radioParams.leftMargin = 24;
        radioDespesa.setLayoutParams(radioParams);
        
        tipoGroup.addView(radioReceita);
        tipoGroup.addView(radioDespesa);
        
        if ("receita".equals(categoria.tipo)) {
            tipoGroup.check(1);
        } else {
            tipoGroup.check(2);
        }
        
        LinearLayout.LayoutParams tipoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipoParams.bottomMargin = 16;
        tipoGroup.setLayoutParams(tipoParams);
        layout.addView(tipoGroup);

        // Styled buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(android.view.Gravity.CENTER);

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar");
        btnSalvar.setTextColor(getResources().getColor(R.color.white));
        btnSalvar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnSalvar.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnSalvarParams = new LinearLayout.LayoutParams(
            0, 48);
        btnSalvarParams.weight = 1;
        btnSalvarParams.rightMargin = 8;
        btnSalvar.setLayoutParams(btnSalvarParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
            0, 48);
        btnCancelarParams.weight = 1;
        btnCancelarParams.leftMargin = 8;
        btnCancelar.setLayoutParams(btnCancelarParams);

        buttonLayout.addView(btnSalvar);
        buttonLayout.addView(btnCancelar);
        layout.addView(buttonLayout);

        frameLayout.addView(layout);
        builder.setView(frameLayout);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        btnSalvar.setOnClickListener(v -> {
            String novoNome = inputNome.getText().toString().trim();
            String novoTipo = tipoGroup.getCheckedRadioButtonId() == 1 ? "receita" : "despesa";
            
            if (!novoNome.isEmpty()) {
                categoria.nome = novoNome;
                categoria.tipo = novoTipo;
                categoria.corHex = novoTipo.equals("receita") ? "#22BB33" : "#FF2222";
                db.categoriaDao().atualizar(categoria);
                carregarCategorias();
                dialog.dismiss();
                Toast.makeText(this, "Categoria atualizada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void confirmarExclusaoCategoria(Categoria categoria) {
        // Check if category has transactions
        List<Lancamento> lancamentos = db.lancamentoDao().listarPorCategoria(categoria.id);
        
        String message = "Deseja excluir a categoria '" + categoria.nome + "'?";
        if (!lancamentos.isEmpty()) {
            message += "\n\nATENÇÃO: Esta categoria possui " + lancamentos.size() + 
                      " transação(ões). Elas também serão excluídas.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Categoria");
        builder.setMessage(message);
        
        builder.setPositiveButton("Sim, excluir", (dialog, which) -> {
            // Delete all transactions first (due to foreign key constraints)
            for (Lancamento lancamento : lancamentos) {
                db.lancamentoDao().deletar(lancamento);
            }
            
            // Then delete the category
            db.categoriaDao().deletar(categoria);
            carregarCategorias();
            Toast.makeText(this, "Categoria excluída!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}