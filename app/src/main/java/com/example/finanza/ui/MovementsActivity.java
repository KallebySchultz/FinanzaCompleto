package com.example.finanza.ui;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import com.example.finanza.R;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Categoria;
import com.example.finanza.ui.AccountsActivity;
import com.example.finanza.MainActivity;
import com.example.finanza.ui.MenuActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Activity para gerenciamento de movimentações (transações)
 */
public class MovementsActivity extends AppCompatActivity {

    private AppDatabase db;
    private int usuarioIdAtual;

    // Para navegação de mês/ano
    private Calendar currentMonth;

    private TextView txtMonth, saldoMes;
    private ImageView btnPrevMonth, btnNextMonth;
    private LinearLayout transactionsList;

    // Painel customizado
    private FrameLayout addPanel, addTransactionPanel;
    private LinearLayout btnReceita, btnDespesa;
    private ImageButton btnClosePanel, navAdd;
    private Button btnSalvarLancamento;
    private TextInputEditText inputNome, inputConta, inputData, inputCategoria, inputValor;

    // Para lançamento
    private Conta contaSelecionada;
    private Categoria categoriaSelecionada;
    private long dataSelecionada = System.currentTimeMillis();
    private boolean isReceitaPanel = true; // true: receita, false: despesa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movements);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .allowMainThreadQueries()
                .build();

        // Busca usuário atual
        List<Usuario> usuarios = db.usuarioDao().listarTodos();
        usuarioIdAtual = usuarios.size() > 0 ? usuarios.get(0).id : 0;

        // Referências dos elementos
        txtMonth = findViewById(R.id.txt_month);
        saldoMes = findViewById(R.id.saldo_mes);
        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);
        transactionsList = findViewById(R.id.transactions_list);

        // Painel customizado
        navAdd = findViewById(R.id.nav_add);
        addPanel = findViewById(R.id.add_panel);
        btnReceita = findViewById(R.id.btnReceita);
        btnDespesa = findViewById(R.id.btnDespesa);
        addTransactionPanel = findViewById(R.id.add_transaction_panel);
        btnClosePanel = findViewById(R.id.btn_close_panel);
        btnSalvarLancamento = findViewById(R.id.btn_salvar_lancamento);
        inputNome = findViewById(R.id.input_nome);
        inputConta = findViewById(R.id.input_conta);
        inputData = findViewById(R.id.input_data);
        inputCategoria = findViewById(R.id.input_categoria);
        inputValor = findViewById(R.id.input_valor);

        // Inicializa mês atual (corrige para garantir hora zero)
        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);
        currentMonth.set(Calendar.HOUR_OF_DAY, 0);
        currentMonth.set(Calendar.MINUTE, 0);
        currentMonth.set(Calendar.SECOND, 0);
        currentMonth.set(Calendar.MILLISECOND, 0);

        // Navegação dos meses
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            currentMonth.set(Calendar.HOUR_OF_DAY, 0);
            currentMonth.set(Calendar.MINUTE, 0);
            currentMonth.set(Calendar.SECOND, 0);
            currentMonth.set(Calendar.MILLISECOND, 0);
            updateMovements();
        });
        btnNextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            currentMonth.set(Calendar.DAY_OF_MONTH, 1);
            currentMonth.set(Calendar.HOUR_OF_DAY, 0);
            currentMonth.set(Calendar.MINUTE, 0);
            currentMonth.set(Calendar.SECOND, 0);
            currentMonth.set(Calendar.MILLISECOND, 0);
            updateMovements();
        });

        // Navegação por clique no mês (abre DatePicker apenas para mês/ano)
        txtMonth.setOnClickListener(v -> {
            int year = currentMonth.get(Calendar.YEAR);
            int month = currentMonth.get(Calendar.MONTH);

            DatePickerDialog dpd = new DatePickerDialog(this, (view, y, m, d) -> {
                currentMonth.set(Calendar.YEAR, y);
                currentMonth.set(Calendar.MONTH, m);
                currentMonth.set(Calendar.DAY_OF_MONTH, 1);
                currentMonth.set(Calendar.HOUR_OF_DAY, 0);
                currentMonth.set(Calendar.MINUTE, 0);
                currentMonth.set(Calendar.SECOND, 0);
                currentMonth.set(Calendar.MILLISECOND, 0);
                updateMovements();
            }, year, month, 1);

            // Esconde o campo dia do DatePicker
            try {
                int daySpinnerId = getResources().getIdentifier("android:id/day", null, null);
                if (daySpinnerId != 0) {
                    View daySpinner = dpd.getDatePicker().findViewById(daySpinnerId);
                    if (daySpinner != null) daySpinner.setVisibility(View.GONE);
                } else {
                    java.lang.reflect.Field[] datePickerFields = dpd.getDatePicker().getClass().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName()) || "mDayPicker".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(dpd.getDatePicker());
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception ignored) {}

            dpd.show();
        });

        // Long click on month for search functionality
        txtMonth.setOnLongClickListener(v -> {
            showSearchDialog();
            return true;
        });

        // Navegação pelos botões inferiores
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMovements = findViewById(R.id.nav_movements);
        ImageView navAccounts = findViewById(R.id.nav_accounts);
        ImageView navMenu = findViewById(R.id.nav_menu);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(MovementsActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
        navMovements.setOnClickListener(v -> {/* já está na tela */});
        navAccounts.setOnClickListener(v -> {
            Intent intent = new Intent(MovementsActivity.this, AccountsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(MovementsActivity.this, MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navMovements.setColorFilter(getResources().getColor(R.color.accentBlue));
        navHome.setColorFilter(getResources().getColor(R.color.white));
        navAccounts.setColorFilter(getResources().getColor(R.color.white));
        navMenu.setColorFilter(getResources().getColor(R.color.white));

        // Botão adicionar (painel customizado igual MainActivity)
        navAdd.setOnClickListener(v -> {
            if (addPanel.getVisibility() == View.GONE && addTransactionPanel.getVisibility() == View.GONE) {
                addPanel.setVisibility(View.VISIBLE);
                navAdd.setImageResource(R.drawable.ic_close);
            } else {
                addPanel.setVisibility(View.GONE);
                addTransactionPanel.setVisibility(View.GONE);
                navAdd.setImageResource(R.drawable.ic_add);
            }
        });

        addPanel.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
        });

        btnReceita.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            addTransactionPanel.setVisibility(View.VISIBLE);
            navAdd.setImageResource(R.drawable.ic_close);
            isReceitaPanel = true;
            inicializarCamposPainel(inputNome, inputConta, inputData, inputCategoria, inputValor, true);
        });

        btnDespesa.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            addTransactionPanel.setVisibility(View.VISIBLE);
            navAdd.setImageResource(R.drawable.ic_close);
            isReceitaPanel = false;
            inicializarCamposPainel(inputNome, inputConta, inputData, inputCategoria, inputValor, false);
        });

        btnClosePanel.setOnClickListener(v -> {
            addTransactionPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
        });

        // Campo Conta (dialogo de seleção)
        inputConta.setOnClickListener(v -> {
            List<Conta> contasList = db.contaDao().listarPorUsuario(usuarioIdAtual);
            String[] contasArray = new String[contasList.size()];
            for (int i = 0; i < contasList.size(); i++) {
                contasArray[i] = contasList.get(i).nome;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecionar conta");
            builder.setItems(contasArray, (dialog, which) -> {
                contaSelecionada = contasList.get(which);
                inputConta.setText(contaSelecionada.nome);
            });
            builder.show();
        });

        // Campo Data (DatePicker)
        inputData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dataSelecionada);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth);
                        dataSelecionada = chosen.getTimeInMillis();
                        inputData.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Campo Categoria (dialogo de seleção)
        inputCategoria.setOnClickListener(v -> {
            String tipo = isReceitaPanel ? "receita" : "despesa";
            List<Categoria> categoriasList = db.categoriaDao().listarPorTipo(tipo);
            String[] categoriasArray = new String[categoriasList.size()];
            for (int i = 0; i < categoriasList.size(); i++) {
                categoriasArray[i] = categoriasList.get(i).nome;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecionar categoria");
            builder.setItems(categoriasArray, (dialog, which) -> {
                categoriaSelecionada = categoriasList.get(which);
                inputCategoria.setText(categoriaSelecionada.nome);
            });
            builder.show();
        });

        btnSalvarLancamento.setOnClickListener(v -> {
            String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
            String valorStr = inputValor.getText() != null ? inputValor.getText().toString().replace(",", ".").trim() : "";

            // Clear previous errors
            inputNome.setError(null);
            inputConta.setError(null);
            inputData.setError(null);
            inputCategoria.setError(null);
            inputValor.setError(null);

            boolean hasError = false;

            if (contaSelecionada == null) {
                inputConta.setError("Selecione a conta");
                hasError = true;
            }
            if (categoriaSelecionada == null) {
                inputCategoria.setError("Selecione a categoria");
                hasError = true;
            }
            if (valorStr.isEmpty()) {
                inputValor.setError("Digite o valor");
                hasError = true;
            }

            if (!hasError) {
                try {
                    double valor = Double.parseDouble(valorStr);

                    // Validate positive value
                    if (valor <= 0) {
                        inputValor.setError("O valor deve ser maior que zero");
                        return;
                    }

                    Lancamento lancamento = new Lancamento();
                    lancamento.valor = valor;
                    lancamento.data = dataSelecionada;
                    lancamento.descricao = nome.isEmpty() ? (isReceitaPanel ? "Receita" : "Despesa") : nome;
                    lancamento.contaId = contaSelecionada.id;
                    lancamento.categoriaId = categoriaSelecionada.id;
                    lancamento.usuarioId = usuarioIdAtual;
                    lancamento.tipo = isReceitaPanel ? "receita" : "despesa";
                    db.lancamentoDao().inserir(lancamento);
                    updateMovements();
                    addTransactionPanel.setVisibility(View.GONE);
                    navAdd.setImageResource(R.drawable.ic_add);
                    limparCamposPainel(inputNome, inputConta, inputData, inputCategoria, inputValor);
                    Toast.makeText(this, "Transação salva!", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    inputValor.setError("Valor inválido! Use apenas números e ponto para decimais.");
                }
            }
        });

        updateMovements();
    }

    private void inicializarCamposPainel(TextInputEditText inputNome,
                                         TextInputEditText inputConta,
                                         TextInputEditText inputData,
                                         TextInputEditText inputCategoria,
                                         TextInputEditText inputValor,
                                         boolean isReceita) {
        inputNome.setText("");
        inputConta.setText(contaSelecionada != null ? contaSelecionada.nome : "");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dataSelecionada);
        inputData.setText(String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
        inputCategoria.setText(categoriaSelecionada != null ? categoriaSelecionada.nome : "");
        inputValor.setText("");
    }

    private void limparCamposPainel(TextInputEditText inputNome,
                                    TextInputEditText inputConta,
                                    TextInputEditText inputData,
                                    TextInputEditText inputCategoria,
                                    TextInputEditText inputValor) {
        inputNome.setText("");
        inputConta.setText("");
        inputData.setText("");
        inputCategoria.setText("");
        inputValor.setText("");
        categoriaSelecionada = null;
        dataSelecionada = System.currentTimeMillis();
    }

    /**
     * Atualiza lista de movimentações do mês atual
     */
    private void updateMovements() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new Locale("pt", "BR"));
        txtMonth.setText(monthFormat.format(currentMonth.getTime()).toUpperCase());

        transactionsList.removeAllViews();

        Calendar inicio = (Calendar) currentMonth.clone();
        inicio.set(Calendar.HOUR_OF_DAY, 0);
        inicio.set(Calendar.MINUTE, 0);
        inicio.set(Calendar.SECOND, 0);
        inicio.set(Calendar.MILLISECOND, 0);

        Calendar fim = (Calendar) currentMonth.clone();
        fim.set(Calendar.DAY_OF_MONTH, fim.getActualMaximum(Calendar.DAY_OF_MONTH));
        fim.set(Calendar.HOUR_OF_DAY, 23);
        fim.set(Calendar.MINUTE, 59);
        fim.set(Calendar.SECOND, 59);
        fim.set(Calendar.MILLISECOND, 999);

        List<Lancamento> lancamentos = db.lancamentoDao().listarPorUsuarioPeriodo(
                usuarioIdAtual,
                inicio.getTimeInMillis(),
                fim.getTimeInMillis());

        double saldoFinal = 0.0;

        if (lancamentos != null && lancamentos.size() > 0) {
            SimpleDateFormat diaFormat = new SimpleDateFormat("EEEE, d", new Locale("pt", "BR"));
            Collections.sort(lancamentos, (l1, l2) -> Long.compare(l2.data, l1.data));

            long ultimoDia = -1;
            for (Lancamento lanc : lancamentos) {
                Calendar dataLanc = Calendar.getInstance();
                dataLanc.setTimeInMillis(lanc.data);
                long diaMillis = dataLanc.get(Calendar.YEAR) * 1000 + dataLanc.get(Calendar.DAY_OF_YEAR);

                if (diaMillis != ultimoDia) {
                    TextView diaHeader = new TextView(this);
                    diaHeader.setText(diaFormat.format(dataLanc.getTime()).toUpperCase());
                    diaHeader.setTextColor(getResources().getColor(R.color.white));
                    diaHeader.setTextSize(15);
                    diaHeader.setTypeface(null, android.graphics.Typeface.BOLD);
                    diaHeader.setPadding(0, 24, 0, 12);
                    transactionsList.addView(diaHeader);
                    ultimoDia = diaMillis;
                }

                LinearLayout transItem = new LinearLayout(this);
                transItem.setOrientation(LinearLayout.HORIZONTAL);
                transItem.setPadding(12, 16, 12, 16);
                transItem.setBackground(getResources().getDrawable(R.drawable.bg_transaction_item));
                LinearLayout.LayoutParams transItemParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                transItemParams.bottomMargin = 8;
                transItem.setLayoutParams(transItemParams);

                // Obtém nome da categoria
                Categoria categoria = db.categoriaDao().buscarPorId(lanc.categoriaId);
                String categoriaNome = categoria != null ? categoria.nome : "";

                TextView nome = new TextView(this);
                nome.setText(lanc.descricao + " • " + categoriaNome);
                nome.setTextColor(getResources().getColor(R.color.white));
                nome.setTextSize(17);
                nome.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                TextView valor = new TextView(this);
                valor.setText(formatarMoeda(lanc.valor));
                valor.setTextColor(getResources().getColor(
                        lanc.tipo.equals("receita") ? R.color.positiveGreen : R.color.negativeRed));
                valor.setTextSize(17);
                valor.setTypeface(null, android.graphics.Typeface.BOLD);
                valor.setPadding(20, 0, 0, 0);

                transItem.addView(nome);
                transItem.addView(valor);

                final Lancamento finalLanc = lanc;
                transItem.setOnClickListener(v -> editarLancamento(finalLanc));
                transItem.setOnLongClickListener(v -> {
                    confirmarExclusaoLancamento(finalLanc);
                    return true;
                });

                transactionsList.addView(transItem);

                saldoFinal += lanc.tipo.equals("receita") ? lanc.valor : -lanc.valor;
            }
        }

        saldoMes.setText(formatarMoeda(saldoFinal));
    }

    /**
     * Modal para edição de movimentação (transação) centralizado na tela, com botões corrigidos
     */
    private void editarLancamento(Lancamento lancamento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Transação");

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
        int dpPadding = (int) android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f); // Add high elevation to ensure modal appears above everything
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView title = new TextView(this);
        title.setText("Editar Transação");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Campo descrição
        final TextInputEditText inputDescricao = new TextInputEditText(this);
        inputDescricao.setHint("Descrição");
        inputDescricao.setText(lancamento.descricao);
        inputDescricao.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputDescricao.setHintTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputDescricao.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams1.bottomMargin = dpPadding / 2;
        inputDescricao.setLayoutParams(inputParams1);
        layout.addView(inputDescricao);

        // Campo valor
        final TextInputEditText inputValor = new TextInputEditText(this);
        inputValor.setHint("Valor");
        inputValor.setText(String.valueOf(lancamento.valor));
        inputValor.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputValor.setHintTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputValor.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        inputValor.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams inputParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams2.bottomMargin = dpPadding / 2;
        inputValor.setLayoutParams(inputParams2);
        layout.addView(inputValor);

        // Categoria selection
        final TextInputEditText inputCategoria = new TextInputEditText(this);
        inputCategoria.setHint("Categoria");
        inputCategoria.setFocusable(false);
        inputCategoria.setClickable(true);
        inputCategoria.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputCategoria.setHintTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputCategoria.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams3.bottomMargin = dpPadding / 2;
        inputCategoria.setLayoutParams(inputParams3);

        List<Categoria> categorias = db.categoriaDao().listarPorTipo(lancamento.tipo);
        Categoria categoriaSelecionada = null;
        for (Categoria cat : categorias) {
            if (cat.id == lancamento.categoriaId) {
                categoriaSelecionada = cat;
                inputCategoria.setText(cat.nome);
                break;
            }
        }
        final Categoria[] categoriaFinal = {categoriaSelecionada};

        inputCategoria.setOnClickListener(v -> {
            String[] nomesCategorias = new String[categorias.size()];
            for (int i = 0; i < categorias.size(); i++) {
                nomesCategorias[i] = categorias.get(i).nome;
            }
            AlertDialog.Builder catBuilder = new AlertDialog.Builder(this);
            catBuilder.setTitle("Selecionar categoria");
            catBuilder.setItems(nomesCategorias, (dialog, which) -> {
                categoriaFinal[0] = categorias.get(which);
                inputCategoria.setText(categoriaFinal[0].nome);
            });
            catBuilder.show();
        });
        layout.addView(inputCategoria);

        // Botões "Salvar" e "Cancelar" com layout corrigido
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar");
        btnSalvar.setTextColor(getResources().getColor(R.color.white));
        btnSalvar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnSalvar.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnSalvarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // WRAP_CONTENT e peso 1f
        btnSalvarParams.rightMargin = dpPadding / 4;
        btnSalvar.setLayoutParams(btnSalvarParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // WRAP_CONTENT e peso 1f
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

        // Listener do botão Salvar
        btnSalvar.setOnClickListener(v -> {
            String novaDescricao = inputDescricao.getText().toString().trim();
            String novoValorStr = inputValor.getText().toString().trim();

            if (!novaDescricao.isEmpty() && !novoValorStr.isEmpty() && categoriaFinal[0] != null) {
                try {
                    double novoValor = Double.parseDouble(novoValorStr.replace(",", "."));
                    if (novoValor <= 0) {
                        Toast.makeText(this, "O valor deve ser maior que zero", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lancamento.descricao = novaDescricao;
                    lancamento.valor = novoValor;
                    lancamento.categoriaId = categoriaFinal[0].id;
                    db.lancamentoDao().atualizar(lancamento);
                    updateMovements();
                    dialog.dismiss();
                    Toast.makeText(this, "Transação atualizada!", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener do botão Cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void confirmarExclusaoLancamento(Lancamento lancamento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout para fundo arredondado e tamanho customizado
        FrameLayout frameLayout = new FrameLayout(this);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int dpPadding = (int) android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView title = new TextView(this);
        title.setText("Excluir Transação");
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
        String message = "Deseja excluir a transação '" + lancamento.descricao +
                "' no valor de " + formatarMoeda(lancamento.valor) + "?";
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
        btnExcluir.setBackground(getResources().getDrawable(R.drawable.button_blue)); // Use blue for consistency
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

        // Listener do botão Excluir
        btnExcluir.setOnClickListener(v -> {
            db.lancamentoDao().deletar(lancamento);
            updateMovements();
            dialog.dismiss();
            Toast.makeText(this, "Transação excluída!", Toast.LENGTH_SHORT).show();
        });

        // Listener do botão Cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔍 Buscar Transações");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputBusca = new EditText(this);
        inputBusca.setHint("Digite a descrição ou valor...");
        layout.addView(inputBusca);

        builder.setView(layout);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String termoBusca = inputBusca.getText().toString().trim();
            if (!termoBusca.isEmpty()) {
                buscarTransacoes(termoBusca);
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.setNeutralButton("Ver Todas", (dialog, which) -> {
            updateMovements();
        });

        builder.show();
    }

    private void buscarTransacoes(String termoBusca) {
        txtMonth.setText("BUSCA: " + termoBusca.toUpperCase());

        transactionsList.removeAllViews();

        String searchPattern = "%" + termoBusca + "%";
        List<Lancamento> resultados = db.lancamentoDao().buscarPorDescricaoOuValor(usuarioIdAtual, searchPattern);

        double saldoTotal = 0.0;

        if (resultados != null && resultados.size() > 0) {
            SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

            for (Lancamento lanc : resultados) {
                LinearLayout resultItem = new LinearLayout(this);
                resultItem.setOrientation(LinearLayout.HORIZONTAL);
                resultItem.setPadding(0, 12, 0, 12);

                Categoria categoria = db.categoriaDao().buscarPorId(lanc.categoriaId);

                TextView descricao = new TextView(this);
                descricao.setText(lanc.descricao + " • " + (categoria != null ? categoria.nome : "") + " • " + dataFormat.format(new java.util.Date(lanc.data)));
                descricao.setTextColor(getResources().getColor(R.color.white));
                descricao.setTextSize(15);
                descricao.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                TextView valor = new TextView(this);
                valor.setText(formatarMoeda(lanc.valor));
                valor.setTextColor(getResources().getColor(
                        lanc.tipo.equals("receita") ? R.color.positiveGreen : R.color.negativeRed));
                valor.setTextSize(15);
                valor.setTypeface(null, android.graphics.Typeface.BOLD);

                resultItem.addView(descricao);
                resultItem.addView(valor);

                final Lancamento finalLanc = lanc;
                resultItem.setOnClickListener(v -> editarLancamento(finalLanc));
                resultItem.setOnLongClickListener(v -> {
                    confirmarExclusaoLancamento(finalLanc);
                    return true;
                });

                transactionsList.addView(resultItem);

                saldoTotal += lanc.tipo.equals("receita") ? lanc.valor : -lanc.valor;
            }
        } else {
            TextView noResults = new TextView(this);
            noResults.setText("Nenhuma transação encontrada para: " + termoBusca);
            noResults.setTextColor(getResources().getColor(R.color.white));
            noResults.setTextSize(16);
            noResults.setPadding(0, 20, 0, 20);
            noResults.setGravity(android.view.Gravity.CENTER);
            transactionsList.addView(noResults);
        }

        saldoMes.setText(String.format("%d resultados • %s",
                resultados != null ? resultados.size() : 0, formatarMoeda(saldoTotal)));
    }

    /**
     * Formata valor monetário para exibição
     */
    private String formatarMoeda(double valor) {
        return String.format("R$ %.2f", valor);
    }
}