package com.example.finanza.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
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
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.view.ViewGroup;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Lancamento;
import com.example.finanza.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity para gerenciamento de categorias
 */
public class CategoriaActivity extends AppCompatActivity {

    // Banco de dados Room
    private AppDatabase db;

    // Views principais
    private LinearLayout categoriasList;
    private EditText etNovaCategoria;
    private Button btnAdicionarCategoria, btnMostrarReceitas, btnMostrarDespesas;
    private List<Categoria> todasCategorias;
    private List<Categoria> categoriasExibidas;
    // Filtro de tipo ("", "receita" ou "despesa")
    private String filtroTipo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        // Ajusta as cores da barra de status e navegação
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));

        // Inicializa banco de dados Room
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .allowMainThreadQueries()
                .build();

        // Inicialização das views
        categoriasList = findViewById(R.id.categorias_list);
        etNovaCategoria = findViewById(R.id.etNovaCategoria);
        btnAdicionarCategoria = findViewById(R.id.btnAdicionarCategoria);
        btnMostrarReceitas = findViewById(R.id.btnMostrarReceitas);
        btnMostrarDespesas = findViewById(R.id.btnMostrarDespesas);

        todasCategorias = new ArrayList<>();
        categoriasExibidas = new ArrayList<>();

        // Configura navegação inferior
        setupNavigation();

        // Adiciona nova categoria
        btnAdicionarCategoria.setOnClickListener(v -> {
            String nomeCategoria = etNovaCategoria.getText() != null ? etNovaCategoria.getText().toString().trim() : "";
            if (!nomeCategoria.isEmpty()) {
                mostrarDialogoNovaCategoria(nomeCategoria);
            } else {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show();
            }
        });

        // Filtro para receitas
        btnMostrarReceitas.setOnClickListener(v -> {
            filtroTipo = "receita";
            atualizarCategoriasFiltradas();
            btnMostrarReceitas.setBackground(getResources().getDrawable(R.drawable.button_blue));
            btnMostrarDespesas.setBackground(getResources().getDrawable(R.drawable.button_gray));
        });

        // Filtro para despesas
        btnMostrarDespesas.setOnClickListener(v -> {
            filtroTipo = "despesa";
            atualizarCategoriasFiltradas();
            btnMostrarReceitas.setBackground(getResources().getDrawable(R.drawable.button_gray));
            btnMostrarDespesas.setBackground(getResources().getDrawable(R.drawable.button_blue));
        });

        // Inicialmente mostra todas categorias
        filtroTipo = "";
        carregarCategorias();
    }

    /**
     * Configura os botões de navegação inferior
     */
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
                // Smart back navigation - nav_menu acts as back button
                finish(); // Returns to previous activity
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

    /**
     * Carrega todas as categorias do banco
     */
    private void carregarCategorias() {
        todasCategorias.clear();
        todasCategorias.addAll(db.categoriaDao().listarTodas());
        atualizarCategoriasFiltradas();
    }

    /**
     * Filtra as categorias exibidas de acordo com o tipo selecionado
     */
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

    /**
     * Atualiza a lista visual de categorias
     */
    private void atualizarListaCategorias() {
        categoriasList.removeAllViews();

        for (Categoria categoria : categoriasExibidas) {
            // Cria um card para cada categoria
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(12, 16, 12, 16);
            item.setBackground(getResources().getDrawable(R.drawable.bg_transaction_item));

            // Ícone da categoria
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

            // Informações da categoria
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

            // Clique curto: editar categoria
            final Categoria finalCategoria = categoria;
            item.setOnClickListener(v -> editarCategoria(finalCategoria));

            // Clique longo: excluir categoria
            item.setOnLongClickListener(v -> {
                confirmarExclusaoCategoria(finalCategoria);
                return true;
            });

            categoriasList.addView(item);
        }
    }

    /**
     * Exibe o modal para adicionar nova categoria
     * @param nomeInicial Nome sugerido no campo
     */
    private void mostrarDialogoNovaCategoria(String nomeInicial) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout centralizado
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER // CENTRALIZA O MODAL NA TELA!
        );
        frameLayout.setLayoutParams(frameParams);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Modal maior: padding 24dp e largura 340dp
        int dpPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView title = new TextView(this);
        title.setText("Nova Categoria");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Campo nome da categoria
        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome da categoria");
        inputNome.setText(nomeInicial);
        inputNome.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setHintTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams.bottomMargin = dpPadding / 2;
        inputNome.setLayoutParams(inputParams);
        layout.addView(inputNome);

        // Grupo dos radio buttons
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
        radioParams.leftMargin = dpPadding / 2;
        radioDespesa.setLayoutParams(radioParams);

        tipoGroup.addView(radioReceita);
        tipoGroup.addView(radioDespesa);

        LinearLayout.LayoutParams tipoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipoParams.bottomMargin = dpPadding / 2;
        tipoGroup.setLayoutParams(tipoParams);
        layout.addView(tipoGroup);

        // Botões "Salvar" e "Cancelar"
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar");
        btnSalvar.setTextColor(getResources().getColor(R.color.white));
        btnSalvar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnSalvar.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnSalvarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnSalvarParams.rightMargin = dpPadding / 4;
        btnSalvar.setLayoutParams(btnSalvarParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnCancelarParams.leftMargin = dpPadding / 4;
        btnCancelar.setLayoutParams(btnCancelarParams);

        buttonLayout.addView(btnSalvar);
        buttonLayout.addView(btnCancelar);
        layout.addView(buttonLayout);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Force center the dialog window
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        // Listener do botão Salvar
        btnSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
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

        // Listener do botão Cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Exibe o modal para editar categoria
     * @param categoria Categoria a ser editada
     */
    private void editarCategoria(Categoria categoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout centralizado
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER // CENTRALIZA O MODAL NA TELA!
        );
        frameLayout.setLayoutParams(frameParams);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Modal maior: padding 24dp e largura 340dp
        int dpPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView title = new TextView(this);
        title.setText("Editar Categoria");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Campo nome da categoria
        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome da categoria");
        inputNome.setText(categoria.nome);
        inputNome.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setHintTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams.bottomMargin = dpPadding / 2;
        inputNome.setLayoutParams(inputParams);
        layout.addView(inputNome);

        // Grupo dos radio buttons
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
        radioParams.leftMargin = dpPadding / 2;
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
        tipoParams.bottomMargin = dpPadding / 2;
        tipoGroup.setLayoutParams(tipoParams);
        layout.addView(tipoGroup);

        // Botões "Salvar" e "Cancelar"
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar");
        btnSalvar.setTextColor(getResources().getColor(R.color.white));
        btnSalvar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnSalvar.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnSalvarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnSalvarParams.rightMargin = dpPadding / 4;
        btnSalvar.setLayoutParams(btnSalvarParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnCancelarParams.leftMargin = dpPadding / 4;
        btnCancelar.setLayoutParams(btnCancelarParams);

        buttonLayout.addView(btnSalvar);
        buttonLayout.addView(btnCancelar);
        layout.addView(buttonLayout);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Force center the dialog window
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        // Listener do botão Salvar
        btnSalvar.setOnClickListener(v -> {
            String novoNome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
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

        // Listener do botão Cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Exibe confirmação de exclusão de categoria
     * @param categoria Categoria a excluir
     */
    private void confirmarExclusaoCategoria(Categoria categoria) {
        List<Lancamento> lancamentos = db.lancamentoDao().listarPorCategoria(categoria.id);

        String message = "Deseja excluir a categoria '" + categoria.nome + "'?";
        if (!lancamentos.isEmpty()) {
            message += "\n\nATENÇÃO: Esta categoria possui " + lancamentos.size() +
                    " transação(ões). Elas também serão excluídas.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout centralizado
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER // CENTRALIZA O MODAL NA TELA!
        );
        frameLayout.setLayoutParams(frameParams);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int dpPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        frameLayout.setLayoutParams(layoutParams);

        // Título do modal
        TextView title = new TextView(this);
        title.setText("Excluir Categoria");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Mensagem de confirmação
        TextView messageText = new TextView(this);
        messageText.setText(message);
        messageText.setTextSize(16);
        messageText.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        messageText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = dpPadding;
        messageText.setLayoutParams(messageParams);
        layout.addView(messageText);

        // Botões "Excluir" e "Cancelar" com layout consistente
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button btnExcluir = new Button(this);
        btnExcluir.setText("Sim, excluir");
        btnExcluir.setTextColor(getResources().getColor(R.color.white));
        btnExcluir.setTypeface(null, android.graphics.Typeface.BOLD);
        btnExcluir.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnExcluirParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnExcluirParams.rightMargin = dpPadding / 4;
        btnExcluir.setLayoutParams(btnExcluirParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnCancelarParams.leftMargin = dpPadding / 4;
        btnCancelar.setLayoutParams(btnCancelarParams);

        buttonLayout.addView(btnExcluir);
        buttonLayout.addView(btnCancelar);
        layout.addView(buttonLayout);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Force center the dialog window
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        // Listener do botão Excluir
        btnExcluir.setOnClickListener(v -> {
            for (Lancamento lancamento : lancamentos) {
                db.lancamentoDao().deletar(lancamento);
            }
            db.categoriaDao().deletar(categoria);
            carregarCategorias();
            dialog.dismiss();
            Toast.makeText(this, "Categoria excluída!", Toast.LENGTH_SHORT).show();
        });

        // Listener do botão Cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}