package com.example.finanza.ui;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
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
import com.example.finanza.network.SyncService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MovementsActivity extends AppCompatActivity {

    private AppDatabase db;
    private SyncService syncService;
    private int usuarioIdAtual;

    // Para navegação de mês/ano
    private Calendar currentMonth;

    private TextView txtMonth, saldoMes;
    private ImageView btnPrevMonth, btnNextMonth;
    private LinearLayout transactionsList;

    private FrameLayout addPanel;
    private LinearLayout btnReceita, btnDespesa;
    private ImageButton navAdd;

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
                .fallbackToDestructiveMigration() // Para lidar com mudanças no schema
                .allowMainThreadQueries()
                .build();

        // Inicializar serviço de sincronização
        syncService = new SyncService(this);

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

        txtMonth = findViewById(R.id.txt_month);
        saldoMes = findViewById(R.id.saldo_mes);
        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);
        transactionsList = findViewById(R.id.transactions_list);

        navAdd = findViewById(R.id.nav_add);
        addPanel = findViewById(R.id.add_panel);
        btnReceita = findViewById(R.id.btnReceita);
        btnDespesa = findViewById(R.id.btnDespesa);

        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);
        currentMonth.set(Calendar.HOUR_OF_DAY, 0);
        currentMonth.set(Calendar.MINUTE, 0);
        currentMonth.set(Calendar.SECOND, 0);
        currentMonth.set(Calendar.MILLISECOND, 0);

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

        txtMonth.setOnLongClickListener(v -> {
            showSearchDialog();
            return true;
        });

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
        navMovements.setOnClickListener(v -> {});
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

        navAdd.setOnClickListener(v -> {
            if (addPanel.getVisibility() == View.GONE) {
                addPanel.setVisibility(View.VISIBLE);
                navAdd.setImageResource(R.drawable.ic_close);
            } else {
                addPanel.setVisibility(View.GONE);
                navAdd.setImageResource(R.drawable.ic_add);
            }
        });

        addPanel.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
        });

        btnReceita.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
            showAddTransactionDialog(true);
        });

        btnDespesa.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
            showAddTransactionDialog(false);
        });

        updateMovements();
    }

    private void showAddTransactionDialog(boolean isReceitaPanel) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextInputEditText inputNome = dialogView.findViewById(R.id.input_nome);
        TextInputEditText inputConta = dialogView.findViewById(R.id.input_conta);
        TextInputEditText inputData = dialogView.findViewById(R.id.input_data);
        TextInputEditText inputCategoria = dialogView.findViewById(R.id.input_categoria);
        TextInputEditText inputValor = dialogView.findViewById(R.id.input_valor);
        Button btnSalvar = dialogView.findViewById(R.id.btn_salvar);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        inputNome.setText("");
        inputConta.setText("");
        inputData.setText(new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(new java.util.Date(System.currentTimeMillis())));
        inputCategoria.setText("");
        inputValor.setText("");

        final Conta[] contaSelecionadaDialog = {null};
        inputConta.setOnClickListener(v -> {
            List<Conta> contasList = db.contaDao().listarPorUsuario(usuarioIdAtual);
            String[] contasArray = new String[contasList.size()];
            for (int i = 0; i < contasList.size(); i++) contasArray[i] = contasList.get(i).nome;
            new AlertDialog.Builder(this)
                    .setTitle("Selecionar conta")
                    .setItems(contasArray, (dialog2, which) -> {
                        contaSelecionadaDialog[0] = contasList.get(which);
                        inputConta.setText(contaSelecionadaDialog[0].nome);
                    }).show();
        });

        final long[] dataSelecionadaDialog = {System.currentTimeMillis()};
        inputData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dataSelecionadaDialog[0]);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth);
                        dataSelecionadaDialog[0] = chosen.getTimeInMillis();
                        inputData.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        final Categoria[] categoriaSelecionadaDialog = {null};
        inputCategoria.setOnClickListener(v -> {
            String tipo = isReceitaPanel ? "receita" : "despesa";
            List<Categoria> categoriasList = db.categoriaDao().listarPorTipo(tipo);
            String[] categoriasArray = new String[categoriasList.size()];
            for (int i = 0; i < categoriasList.size(); i++) categoriasArray[i] = categoriasList.get(i).nome;
            new AlertDialog.Builder(this)
                    .setTitle("Selecionar categoria")
                    .setItems(categoriasArray, (dialog2, which) -> {
                        categoriaSelecionadaDialog[0] = categoriasList.get(which);
                        inputCategoria.setText(categoriaSelecionadaDialog[0].nome);
                    }).show();
        });

        btnSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
            String valorStr = inputValor.getText() != null ? inputValor.getText().toString().replace(",", ".").trim() : "";

            inputNome.setError(null);
            inputConta.setError(null);
            inputData.setError(null);
            inputCategoria.setError(null);
            inputValor.setError(null);

            boolean hasError = false;
            if (contaSelecionadaDialog[0] == null) {
                inputConta.setError("Selecione a conta");
                hasError = true;
            }
            if (categoriaSelecionadaDialog[0] == null) {
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
                    if (valor <= 0) {
                        inputValor.setError("O valor deve ser maior que zero");
                        return;
                    }
                    Lancamento lancamento = new Lancamento();
                    lancamento.valor = valor;
                    lancamento.data = dataSelecionadaDialog[0];
                    lancamento.descricao = nome.isEmpty() ? (isReceitaPanel ? "Receita" : "Despesa") : nome;
                    lancamento.contaId = contaSelecionadaDialog[0].id;
                    lancamento.categoriaId = categoriaSelecionadaDialog[0].id;
                    lancamento.usuarioId = usuarioIdAtual;
                    lancamento.tipo = isReceitaPanel ? "receita" : "despesa";
                    db.lancamentoDao().inserir(lancamento);
                    
                    // Sincronizar com Firebase após criar nova transação
                    if (syncService != null) {
                        syncService.sincronizarTudo(usuarioIdAtual);
                    }
                    
                    updateMovements();
                    dialog.dismiss();
                    Toast.makeText(this, "Transação salva!", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    inputValor.setError("Valor inválido! Use apenas números e ponto para decimais.");
                }
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

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

    private void editarLancamento(Lancamento lancamento) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_transaction, null);

        TextInputEditText inputDescricao = dialogView.findViewById(R.id.input_descricao);
        TextInputEditText inputValor = dialogView.findViewById(R.id.input_valor);
        TextInputEditText inputCategoria = dialogView.findViewById(R.id.input_categoria);
        Button btnSalvar = dialogView.findViewById(R.id.btn_salvar);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        inputDescricao.setText(lancamento.descricao);
        inputValor.setText(String.valueOf(lancamento.valor));

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnSalvar.setOnClickListener(v -> {
            String novaDescricao = inputDescricao.getText() != null ? inputDescricao.getText().toString().trim() : "";
            String novoValorStr = inputValor.getText() != null ? inputValor.getText().toString().trim() : "";

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
                    
                    // Sincronizar com Firebase após atualizar transação
                    if (syncService != null) {
                        syncService.sincronizarTudo(usuarioIdAtual);
                    }
                    
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

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
    }

    private void confirmarExclusaoLancamento(Lancamento lancamento) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_transaction, null);

        TextView deleteMessage = dialogView.findViewById(R.id.delete_message);
        Button btnExcluir = dialogView.findViewById(R.id.btn_excluir);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        String message = "Deseja excluir a transação '" + lancamento.descricao +
                "' no valor de " + formatarMoeda(lancamento.valor) + "?";
        deleteMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnExcluir.setOnClickListener(v -> {
            db.lancamentoDao().deletar(lancamento);
            
            // Sincronizar com Firebase após excluir transação
            if (syncService != null) {
                syncService.sincronizarTudo(usuarioIdAtual);
            }
            
            updateMovements();
            dialog.dismiss();
            Toast.makeText(this, "Transação excluída!", Toast.LENGTH_SHORT).show();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
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
            String termoBusca = inputBusca.getText() != null ? inputBusca.getText().toString().trim() : "";
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

    private String formatarMoeda(double valor) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        return formatter.format(valor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncService != null) {
            syncService.fechar();
        }
    }
}