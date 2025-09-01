package com.example.finanza.ui;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Button;

import com.example.finanza.R;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Categoria;
import com.example.finanza.AccountsActivity;
import com.example.finanza.MainActivity;
import com.example.finanza.ui.MenuActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

        // Inicializa mês atual
        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);

        // Navegação dos meses
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            updateMovements();
        });
        btnNextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            updateMovements();
        });

        // Navegação por clique no mês (abre DatePicker apenas para mês/ano)
        txtMonth.setOnClickListener(v -> {
            int year = currentMonth.get(Calendar.YEAR);
            int month = currentMonth.get(Calendar.MONTH);

            DatePickerDialog dpd = new DatePickerDialog(this, (view, y, m, d) -> {
                currentMonth.set(Calendar.YEAR, y);
                currentMonth.set(Calendar.MONTH, m);
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
            String nome = inputNome.getText() != null ? inputNome.getText().toString() : "";
            String valorStr = inputValor.getText() != null ? inputValor.getText().toString().replace(",", ".") : "";
            if (contaSelecionada != null && categoriaSelecionada != null && !valorStr.isEmpty()) {
                try {
                    double valor = Double.parseDouble(valorStr);
                    Lancamento lancamento = new Lancamento();
                    lancamento.valor = valor;
                    lancamento.data = dataSelecionada;
                    lancamento.descricao = nome.isEmpty() ? "Reajuste de saldo" : nome;
                    lancamento.contaId = contaSelecionada.id;
                    lancamento.categoriaId = categoriaSelecionada.id;
                    lancamento.usuarioId = usuarioIdAtual;
                    lancamento.tipo = isReceitaPanel ? "receita" : "despesa";
                    db.lancamentoDao().inserir(lancamento);
                    updateMovements();
                    addTransactionPanel.setVisibility(View.GONE);
                    navAdd.setImageResource(R.drawable.ic_add);
                    limparCamposPainel(inputNome, inputConta, inputData, inputCategoria, inputValor);
                } catch (NumberFormatException e) {
                    inputValor.setError("Valor inválido!");
                }
            } else {
                if (contaSelecionada == null) inputConta.setError("Selecione a conta");
                if (categoriaSelecionada == null) inputCategoria.setError("Selecione a categoria");
                if (valorStr.isEmpty()) inputValor.setError("Digite o valor");
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

    private void updateMovements() {
        // Atualiza o texto do mês
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new Locale("pt", "BR"));
        txtMonth.setText(monthFormat.format(currentMonth.getTime()).toUpperCase());

        // Limpa lista de transações
        transactionsList.removeAllViews();

        // Busca lançamentos do mês atual
        Calendar inicio = (Calendar) currentMonth.clone();
        Calendar fim = (Calendar) currentMonth.clone();
        fim.set(Calendar.DAY_OF_MONTH, fim.getActualMaximum(Calendar.DAY_OF_MONTH));
        fim.set(Calendar.HOUR_OF_DAY, 23);
        fim.set(Calendar.MINUTE, 59);
        fim.set(Calendar.SECOND, 59);

        List<Lancamento> lancamentos = db.lancamentoDao().listarPorUsuarioPeriodo(
                usuarioIdAtual,
                inicio.getTimeInMillis(),
                fim.getTimeInMillis());

        double saldoFinal = 0.0;

        if (lancamentos != null && lancamentos.size() > 0) {
            SimpleDateFormat diaFormat = new SimpleDateFormat("EEEE, d", new Locale("pt", "BR"));
            Collections.sort(lancamentos, (l1, l2) -> Long.compare(l2.data, l1.data)); // mais recente primeiro

            long ultimoDia = -1;
            for (Lancamento lanc : lancamentos) {
                Calendar dataLanc = Calendar.getInstance();
                dataLanc.setTimeInMillis(lanc.data);
                long diaMillis = dataLanc.getTimeInMillis() / (1000 * 60 * 60 * 24); // só o dia

                if (diaMillis != ultimoDia) {
                    // Mostra o cabeçalho do dia
                    TextView diaHeader = new TextView(this);
                    diaHeader.setText(diaFormat.format(dataLanc.getTime()).toUpperCase());
                    diaHeader.setTextColor(getResources().getColor(R.color.white));
                    diaHeader.setTextSize(15);
                    diaHeader.setTypeface(null, android.graphics.Typeface.BOLD);
                    diaHeader.setPadding(0, 24, 0, 12);
                    transactionsList.addView(diaHeader);
                    ultimoDia = diaMillis;
                }

                // Mostra a transação
                LinearLayout transItem = new LinearLayout(this);
                transItem.setOrientation(LinearLayout.HORIZONTAL);
                transItem.setPadding(0, 6, 0, 6);

                TextView nome = new TextView(this);
                nome.setText(lanc.descricao + " • " + (lanc.tipo.equals("receita") ? "Receita" : "Despesa"));
                nome.setTextColor(getResources().getColor(R.color.white));
                nome.setTextSize(15);

                TextView valor = new TextView(this);
                valor.setText(String.format("R$ %.2f", lanc.valor));
                valor.setTextColor(getResources().getColor(
                        lanc.tipo.equals("receita") ? R.color.positiveGreen : R.color.negativeRed));
                valor.setTextSize(15);
                valor.setPadding(20, 0, 0, 0);

                transItem.addView(nome);
                transItem.addView(valor);
                transactionsList.addView(transItem);

                // Soma saldo
                saldoFinal += lanc.tipo.equals("receita") ? lanc.valor : -lanc.valor;
            }
        }

        // Mostra saldo final do mês
        saldoMes.setText(String.format("R$ %.2f", saldoFinal));
    }
}