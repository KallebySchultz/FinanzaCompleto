package com.example.finanza;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Button;

import com.example.finanza.R;
import com.example.finanza.ui.AccountsActivity;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Categoria;
import com.example.finanza.ui.MenuActivity;
import com.example.finanza.ui.MovementsActivity;
import com.example.finanza.network.SyncService;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean saldoVisivel = true;
    private AppDatabase db;
    private SyncService syncService;
    private int usuarioIdAtual;
    private int contaPadraoId;
    private int categoriaReceitaId;
    private int categoriaDespesaId;

    // Painel customizado
    private Conta contaSelecionada;
    private Categoria categoriaSelecionada;
    private long dataSelecionada = System.currentTimeMillis();
    private boolean isReceitaPanel = true; // true: receita, false: despesa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .fallbackToDestructiveMigration() // Para lidar com mudanças no schema
                .allowMainThreadQueries()
                .build();

        // Inicializar serviço de sincronização
        syncService = new SyncService(this);

        // Obter usuário autenticado
        usuarioIdAtual = getIntent().getIntExtra("usuarioId", -1);
        if (usuarioIdAtual == -1) {
            // Usuário não autenticado, voltar para login
            Intent loginIntent = new Intent(this, com.example.finanza.ui.LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Busca/cria conta padrão
        Conta contaPadrao;
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        if (contas.size() == 0) {
            contaPadrao = new Conta();
            contaPadrao.nome = "Conta Padrão";
            contaPadrao.saldoInicial = 0.0;
            contaPadrao.usuarioId = usuarioIdAtual;
            int id = (int) db.contaDao().inserir(contaPadrao);
            contaPadrao.id = id;
        } else {
            contaPadrao = contas.get(0);
        }
        contaPadraoId = contaPadrao.id;
        contaSelecionada = contaPadrao;

        // Categorias padrão receita
        String[][] categoriasReceitaPadrao = {
                {"Receita", "#22BB33"},
                {"Salário", "#22BB33"},
                {"Freelancer", "#22BB33"},
                {"Serviço autônomo", "#22BB33"},
                {"Venda", "#22BB33"},
                {"Recebimento de aluguel", "#22BB33"},
                {"Reajuste de saldo", "#22BB33"}
        };
        List<Categoria> receitasCats = db.categoriaDao().listarPorTipo("receita");
        for (String[] catPadrao : categoriasReceitaPadrao) {
            boolean existe = false;
            for (Categoria cat : receitasCats) {
                if (cat.nome.equalsIgnoreCase(catPadrao[0])) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                Categoria nova = new Categoria();
                nova.nome = catPadrao[0];
                nova.corHex = catPadrao[1];
                nova.tipo = "receita";
                db.categoriaDao().inserir(nova);
            }
        }
        receitasCats = db.categoriaDao().listarPorTipo("receita");
        Categoria catReceita = null;
        for (Categoria cat : receitasCats) {
            if ("Receita".equalsIgnoreCase(cat.nome)) {
                catReceita = cat;
                break;
            }
        }
        if (catReceita == null && !receitasCats.isEmpty()) {
            catReceita = receitasCats.get(0);
        }
        categoriaReceitaId = catReceita != null ? catReceita.id : -1;

        // Categorias padrão despesa
        String[][] categoriasDespesaPadrao = {
                {"Despesa", "#FF2222"},
                {"Alimentação", "#FF2222"},
                {"Transporte", "#FF2222"},
                {"Moradia", "#FF2222"},
                {"Saúde", "#FF2222"},
                {"Educação", "#FF2222"},
                {"Lazer", "#FF2222"},
                {"Compras", "#FF2222"},
                {"Supermercado", "#FF2222"},
                {"Assinaturas", "#FF2222"},
                {"Contas", "#FF2222"},
                {"Cartão de crédito", "#FF2222"},
                {"Viagem", "#FF2222"}
        };
        List<Categoria> despesaCats = db.categoriaDao().listarPorTipo("despesa");
        for (String[] catPadrao : categoriasDespesaPadrao) {
            boolean existe = false;
            for (Categoria cat : despesaCats) {
                if (cat.nome.equalsIgnoreCase(catPadrao[0])) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                Categoria nova = new Categoria();
                nova.nome = catPadrao[0];
                nova.corHex = catPadrao[1];
                nova.tipo = "despesa";
                db.categoriaDao().inserir(nova);
            }
        }
        despesaCats = db.categoriaDao().listarPorTipo("despesa");
        Categoria catDespesa = null;
        for (Categoria cat : despesaCats) {
            if ("Despesa".equalsIgnoreCase(cat.nome)) {
                catDespesa = cat;
                break;
            }
        }
        if (catDespesa == null && !despesaCats.isEmpty()) {
            catDespesa = despesaCats.get(0);
        }
        categoriaDespesaId = catDespesa != null ? catDespesa.id : -1;

        final TextView tvSaldo = findViewById(R.id.tvSaldo);
        final TextView tvReceita = findViewById(R.id.tvReceita);
        final TextView tvDespesa = findViewById(R.id.tvDespesa);
        final ImageView imgEye = findViewById(R.id.imgEye);

        atualizarValores(tvSaldo, tvReceita, tvDespesa);
        updateHomeContent();

        imgEye.setOnClickListener(v -> {
            saldoVisivel = !saldoVisivel;
            imgEye.setImageResource(saldoVisivel ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed);
            atualizarValores(tvSaldo, tvReceita, tvDespesa);
            updateHomeContent();
        });

        final ImageView navHome = findViewById(R.id.nav_home);
        final ImageView navMenu = findViewById(R.id.nav_menu);

        navHome.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.accentBlue));
            navMenu.setColorFilter(getResources().getColor(R.color.white));
        });

        navMenu.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.white));
            navMenu.setColorFilter(getResources().getColor(R.color.accentBlue));
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("usuarioId", usuarioIdAtual);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        final ImageView navAccounts = findViewById(R.id.nav_accounts);
        if (navAccounts != null) {
            navAccounts.setOnClickListener(v -> {
                navHome.setColorFilter(getResources().getColor(R.color.white));
                navMenu.setColorFilter(getResources().getColor(R.color.white));
                navAccounts.setColorFilter(getResources().getColor(R.color.accentBlue));
                Intent intent = new Intent(this, AccountsActivity.class);
                intent.putExtra("usuarioId", usuarioIdAtual);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        final ImageView navMovements = findViewById(R.id.nav_movements);
        if (navMovements != null) {
            navMovements.setOnClickListener(v -> {
                navHome.setColorFilter(getResources().getColor(R.color.white));
                navMenu.setColorFilter(getResources().getColor(R.color.white));
                navMovements.setColorFilter(getResources().getColor(R.color.accentBlue));
                Intent intent = new Intent(this, MovementsActivity.class);
                intent.putExtra("usuarioId", usuarioIdAtual);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        final ImageButton navAdd = findViewById(R.id.nav_add);
        final FrameLayout addPanel = findViewById(R.id.add_panel);
        final LinearLayout btnReceita = findViewById(R.id.btnReceita);
        final LinearLayout btnDespesa = findViewById(R.id.btnDespesa);

        navAdd.setOnClickListener(v -> {
            if (addPanel.getVisibility() == View.VISIBLE) {
                addPanel.setVisibility(View.GONE);
                navAdd.setImageResource(R.drawable.ic_add);
            } else {
                addPanel.setVisibility(View.VISIBLE);
                navAdd.setImageResource(R.drawable.ic_close);
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
    }

    // Mostra o modal de adicionar transação
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

        // Inicializa conta padrão
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        final Conta[] contaSelecionada = {contas.isEmpty() ? null : contas.get(0)};
        if (contaSelecionada[0] != null) inputConta.setText(contaSelecionada[0].nome);

        inputConta.setOnClickListener(v -> {
            List<Conta> contasList = db.contaDao().listarPorUsuario(usuarioIdAtual);
            String[] contasArray = new String[contasList.size()];
            for (int i = 0; i < contasList.size(); i++) {
                contasArray[i] = contasList.get(i).nome;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Selecionar conta")
                    .setItems(contasArray, (d, which) -> {
                        contaSelecionada[0] = contasList.get(which);
                        inputConta.setText(contaSelecionada[0].nome);
                    }).show();
        });

        final long[] dataSelecionada = {System.currentTimeMillis()};
        inputData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dataSelecionada[0]);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth);
                        dataSelecionada[0] = chosen.getTimeInMillis();
                        inputData.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        final Categoria[] categoriaSelecionada = {null};
        inputCategoria.setOnClickListener(v -> {
            String tipo = isReceitaPanel ? "receita" : "despesa";
            List<Categoria> categoriasList = db.categoriaDao().listarPorTipo(tipo);
            String[] categoriasArray = new String[categoriasList.size()];
            for (int i = 0; i < categoriasList.size(); i++) {
                categoriasArray[i] = categoriasList.get(i).nome;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Selecionar categoria")
                    .setItems(categoriasArray, (d, which) -> {
                        categoriaSelecionada[0] = categoriasList.get(which);
                        inputCategoria.setText(categoriaSelecionada[0].nome);
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
            if (contaSelecionada[0] == null) {
                inputConta.setError("Selecione a conta");
                hasError = true;
            }
            if (categoriaSelecionada[0] == null) {
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
                    lancamento.data = dataSelecionada[0];
                    lancamento.descricao = nome.isEmpty() ? (isReceitaPanel ? "Receita" : "Despesa") : nome;
                    lancamento.contaId = contaSelecionada[0].id;
                    lancamento.categoriaId = categoriaSelecionada[0].id;
                    lancamento.usuarioId = usuarioIdAtual;
                    lancamento.tipo = isReceitaPanel ? "receita" : "despesa";
                    db.lancamentoDao().inserir(lancamento);
                    atualizarValores(findViewById(R.id.tvSaldo), findViewById(R.id.tvReceita), findViewById(R.id.tvDespesa));
                    updateHomeContent();
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    inputValor.setError("Valor inválido! Use apenas números e ponto para decimais.");
                }
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Sincronizar dados quando a activity é retomada
        if (syncService != null && usuarioIdAtual != -1) {
            syncService.sincronizarTudo(usuarioIdAtual);
        }

        // Atualizar UI
        final TextView tvSaldo = findViewById(R.id.tvSaldo);
        final TextView tvReceita = findViewById(R.id.tvReceita);
        final TextView tvDespesa = findViewById(R.id.tvDespesa);
        atualizarValores(tvSaldo, tvReceita, tvDespesa);
        updateHomeContent();
    }

    private void atualizarValores(TextView tvSaldo, TextView tvReceita, TextView tvDespesa) {
        double receitas = consultarReceitas();
        double despesas = consultarDespesas();
        double saldo = receitas - despesas;
        if (saldoVisivel) {
            tvSaldo.setText(formatarMoeda(saldo));
            tvReceita.setText(formatarMoeda(receitas));
            tvDespesa.setText(formatarMoeda(despesas));
        } else {
            tvSaldo.setText("****");
            tvReceita.setText("****");
            tvDespesa.setText("****");
        }
    }

    private double consultarReceitas() {
        Double valor = db.lancamentoDao().somaPorTipo("receita", usuarioIdAtual);
        return valor != null ? valor : 0.0;
    }

    private double consultarDespesas() {
        Double valor = db.lancamentoDao().somaPorTipo("despesa", usuarioIdAtual);
        return valor != null ? valor : 0.0;
    }

    private double consultarSaldo() {
        return consultarReceitas() - consultarDespesas();
    }

    private String formatarMoeda(double valor) {
        return String.format("R$ %.2f", valor);
    }

    private void updateHomeContent() {
        updateAccountsSummary();
        updateCategoriesSummary();
        updateRecentTransactions();
    }

    private void updateAccountsSummary() {
        LinearLayout container = findViewById(R.id.accounts_summary_container);
        container.removeAllViews();

        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        for (Conta conta : contas) {
            LinearLayout itemConta = new LinearLayout(this);
            itemConta.setOrientation(LinearLayout.HORIZONTAL);
            itemConta.setPadding(16, 12, 16, 12);
            itemConta.setClickable(true);
            itemConta.setFocusable(true);

            itemConta.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AccountsActivity.class);
                intent.putExtra("usuarioId", usuarioIdAtual);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });

            LinearLayout infoContainer = new LinearLayout(this);
            infoContainer.setOrientation(LinearLayout.VERTICAL);
            infoContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView nomeConta = new TextView(this);
            nomeConta.setText(conta.nome);
            nomeConta.setTextColor(getResources().getColor(R.color.white));
            nomeConta.setTextSize(16);
            nomeConta.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView saldoConta = new TextView(this);
            double saldoAtual = consultarSaldoConta(conta);
            if (saldoVisivel) {
                saldoConta.setText(formatarMoeda(saldoAtual));
                saldoConta.setTextColor(saldoAtual >= 0 ? getResources().getColor(R.color.positiveGreen) : getResources().getColor(R.color.negativeRed));
            } else {
                saldoConta.setText("****");
                saldoConta.setTextColor(getResources().getColor(R.color.white));
            }
            saldoConta.setTextSize(14);

            infoContainer.addView(nomeConta);
            infoContainer.addView(saldoConta);

            itemConta.addView(infoContainer);

            container.addView(itemConta);
        }
    }

    private void updateCategoriesSummary() {
        LinearLayout container = findViewById(R.id.categories_summary_container);
        container.removeAllViews();

        List<Categoria> categoriasDespesa = db.categoriaDao().listarPorTipo("despesa");

        java.util.List<CategoryExpense> categoryExpenses = new java.util.ArrayList<>();
        for (Categoria categoria : categoriasDespesa) {
            Double totalGasto = db.lancamentoDao().somaPorCategoria(categoria.id, usuarioIdAtual);
            if (totalGasto != null && totalGasto > 0) {
                categoryExpenses.add(new CategoryExpense(categoria, totalGasto));
            }
        }
        java.util.Collections.sort(categoryExpenses, (a, b) -> Double.compare(b.totalGasto, a.totalGasto));

        int maxCategorias = Math.min(5, categoryExpenses.size());
        for (int i = 0; i < maxCategorias; i++) {
            CategoryExpense categoryExpense = categoryExpenses.get(i);

            LinearLayout itemCategoria = new LinearLayout(this);
            itemCategoria.setOrientation(LinearLayout.HORIZONTAL);
            itemCategoria.setPadding(16, 12, 16, 12);

            LinearLayout infoContainer = new LinearLayout(this);
            infoContainer.setOrientation(LinearLayout.VERTICAL);
            infoContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView nomeCategoria = new TextView(this);
            nomeCategoria.setText(categoryExpense.categoria.nome);
            nomeCategoria.setTextColor(getResources().getColor(R.color.white));
            nomeCategoria.setTextSize(16);
            nomeCategoria.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView valorCategoria = new TextView(this);
            if (saldoVisivel) {
                valorCategoria.setText(formatarMoeda(categoryExpense.totalGasto));
                valorCategoria.setTextColor(getResources().getColor(R.color.negativeRed));
            } else {
                valorCategoria.setText("****");
                valorCategoria.setTextColor(getResources().getColor(R.color.white));
            }
            valorCategoria.setTextSize(14);

            infoContainer.addView(nomeCategoria);
            infoContainer.addView(valorCategoria);

            itemCategoria.addView(infoContainer);

            container.addView(itemCategoria);
        }

        if (categoryExpenses.isEmpty()) {
            TextView noData = new TextView(this);
            noData.setText("Nenhuma despesa registrada");
            noData.setTextColor(getResources().getColor(R.color.white));
            noData.setTextSize(14);
            noData.setGravity(android.view.Gravity.CENTER);
            container.addView(noData);
        }
    }

    private static class CategoryExpense {
        Categoria categoria;
        double totalGasto;
        CategoryExpense(Categoria categoria, double totalGasto) {
            this.categoria = categoria;
            this.totalGasto = totalGasto;
        }
    }

    private void updateRecentTransactions() {
        LinearLayout container = findViewById(R.id.recent_transactions_container);
        container.removeAllViews();

        List<Lancamento> transacoes = db.lancamentoDao().listarUltimasPorUsuario(usuarioIdAtual, 5);
        for (Lancamento transacao : transacoes) {
            LinearLayout itemTransacao = new LinearLayout(this);
            itemTransacao.setOrientation(LinearLayout.HORIZONTAL);
            itemTransacao.setPadding(16, 12, 16, 12);
            itemTransacao.setClickable(true);
            itemTransacao.setFocusable(true);

            itemTransacao.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MovementsActivity.class);
                intent.putExtra("usuarioId", usuarioIdAtual);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });

            ImageView icon = new ImageView(this);
            icon.setImageResource(transacao.tipo.equals("receita") ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
            icon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            icon.setColorFilter(transacao.tipo.equals("receita") ?
                    getResources().getColor(R.color.positiveGreen) :
                    getResources().getColor(R.color.negativeRed));

            LinearLayout infoContainer = new LinearLayout(this);
            infoContainer.setOrientation(LinearLayout.VERTICAL);
            infoContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            infoContainer.setPadding(16, 0, 0, 0);

            TextView descricaoTransacao = new TextView(this);
            descricaoTransacao.setText(transacao.descricao);
            descricaoTransacao.setTextColor(getResources().getColor(R.color.white));
            descricaoTransacao.setTextSize(16);
            descricaoTransacao.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView valorTransacao = new TextView(this);
            if (saldoVisivel) {
                valorTransacao.setText(formatarMoeda(transacao.valor));
                valorTransacao.setTextColor(transacao.tipo.equals("receita") ?
                        getResources().getColor(R.color.positiveGreen) :
                        getResources().getColor(R.color.negativeRed));
            } else {
                valorTransacao.setText("****");
                valorTransacao.setTextColor(getResources().getColor(R.color.white));
            }
            valorTransacao.setTextSize(14);

            infoContainer.addView(descricaoTransacao);
            infoContainer.addView(valorTransacao);

            itemTransacao.addView(icon);
            itemTransacao.addView(infoContainer);

            container.addView(itemTransacao);
        }
    }

    private double consultarSaldoConta(Conta conta) {
        Double receitas = db.lancamentoDao().somaPorContaETipo(conta.id, "receita");
        Double despesas = db.lancamentoDao().somaPorContaETipo(conta.id, "despesa");

        double totalReceitas = receitas != null ? receitas : 0.0;
        double totalDespesas = despesas != null ? despesas : 0.0;

        return conta.saldoInicial + totalReceitas - totalDespesas;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncService != null) {
            syncService.fechar();
        }
    }
}