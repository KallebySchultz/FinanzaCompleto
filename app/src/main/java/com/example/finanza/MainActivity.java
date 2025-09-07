package com.example.finanza;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.network.AuthManager;
import com.example.finanza.network.SyncService;
import com.example.finanza.ui.AccountsActivity;
import com.example.finanza.ui.MenuActivity;
import com.example.finanza.ui.MovementsActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

/**
 * MainActivity - Tela principal da aplicação Finanza
 *
 * Esta atividade representa o dashboard principal do aplicativo financeiro,
 * onde o usuário pode visualizar o resumo de suas finanças e realizar
 * operações básicas como adicionar receitas e despesas.
 *
 * Funcionalidades principais:
 * - Exibição do saldo total e por conta
 * - Adição rápida de receitas e despesas
 * - Navegação para outras seções do app
 * - Sincronização automática com servidor desktop
 * - Visualização de resumo financeiro
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Estado de visibilidade do saldo
    private boolean saldoVisivel = true;

    // Componentes de banco de dados e serviços
    private AppDatabase db;
    private AuthManager authManager;
    private SyncService syncService;

    // IDs de referência do usuário e dados padrão
    private int usuarioIdAtual;
    private int contaPadraoId;
    private int categoriaReceitaId;
    private int categoriaDespesaId;

    // Painel customizado para adição de transações
    private Conta contaSelecionada;
    private Categoria categoriaSelecionada;
    private long dataSelecionada = System.currentTimeMillis();
    private boolean isReceitaPanel = true; // true: receita, false: despesa

    /**
     * Método onCreate - Inicialização da atividade principal
     *
     * Responsável por:
     * - Configurar a interface visual (status bar, navigation bar)
     * - Inicializar banco de dados e serviços de autenticação/sincronização
     * - Validar autenticação do usuário
     * - Carregar dados padrão (contas e categorias)
     * - Configurar listeners de eventos da interface
     *
     * @param savedInstanceState Estado salvo da atividade (se houver)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuração visual da interface
        configurarInterfaceVisual();

        // Inicialização de componentes essenciais
        inicializarComponentes();

        // Validação e configuração do usuário
        if (!validarUsuarioAutenticado()) {
            return; // Sai do método se usuário não válido
        }

        // Carregamento de dados padrão
        carregarDadosPadrao();

        // Configuração da interface principal
        configurarInterfacePrincipal();

        // Configuração de listeners e eventos
        configurarEventListeners();

        // Configuração da navegação
        configurarNavegacao();
    }

    /**
     * Configura cores da status bar e navigation bar
     */
    private void configurarInterfaceVisual() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);
    }

    /**
     * Inicializa banco de dados e serviços principais
     */
    private void inicializarComponentes() {
        db = AppDatabase.getDatabase(getApplicationContext());
        authManager = AuthManager.getInstance(this);
        syncService = SyncService.getInstance(this);
        Log.d(TAG, "Componentes inicializados");
    }

    /**
     * Valida se há um usuário autenticado válido
     *
     * @return true se usuário válido, false caso contrário
     */
    private boolean validarUsuarioAutenticado() {
        // Obter usuário autenticado da intent ou AuthManager
        usuarioIdAtual = getIntent().getIntExtra("usuarioId", -1);
        if (usuarioIdAtual == -1) {
            usuarioIdAtual = authManager.getLoggedUserId();
        }

        if (usuarioIdAtual == -1) {
            Log.w(TAG, "Usuário não autenticado, redirecionando para login");
            redirecionarParaLogin();
            return false;
        }

        // Validar se o usuário existe no banco de dados
        Usuario usuarioAtual = db.usuarioDao().buscarPorId(usuarioIdAtual);
        if (usuarioAtual == null) {
            Log.w(TAG, "Usuário não encontrado no banco, limpando sessão");
            authManager.logout();
            redirecionarParaLogin();
            return false;
        }

        Log.d(TAG, "Usuário validado: " + usuarioAtual.email);
        return true;
    }

    /**
     * Redireciona para a tela de login
     */
    private void redirecionarParaLogin() {
        Intent loginIntent = new Intent(this, com.example.finanza.ui.LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Carrega dados padrão do usuário (contas e categorias)
     */
    private void carregarDadosPadrao() {
        // Buscar contas existentes
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        if (!contas.isEmpty()) {
            Conta contaPadrao = contas.get(0);
            contaPadraoId = contaPadrao.id;
            contaSelecionada = contaPadrao;
            Log.d(TAG, "Conta padrão carregada: " + contaPadrao.nome);
        } else {
            contaPadraoId = -1;
            contaSelecionada = null;
            Log.w(TAG, "Nenhuma conta encontrada para o usuário");
        }

        // Buscar categorias de receita
        List<Categoria> receitasCats = db.categoriaDao().listarPorTipo("receita");
        Categoria catReceita = !receitasCats.isEmpty() ? receitasCats.get(0) : null;
        categoriaReceitaId = catReceita != null ? catReceita.id : -1;

        // Buscar categorias de despesa
        List<Categoria> despesaCats = db.categoriaDao().listarPorTipo("despesa");
        Categoria catDespesa = !despesaCats.isEmpty() ? despesaCats.get(0) : null;
        categoriaDespesaId = catDespesa != null ? catDespesa.id : -1;

        Log.d(TAG, "Dados padrão carregados - Receita ID: " + categoriaReceitaId + ", Despesa ID: " + categoriaDespesaId);
    }

    /**
     * Configura elementos principais da interface
     */
    private void configurarInterfacePrincipal() {
        final TextView tvSaldo = findViewById(R.id.tvSaldo);
        final TextView tvReceita = findViewById(R.id.tvReceita);
        final TextView tvDespesa = findViewById(R.id.tvDespesa);

        // Atualizar valores iniciais
        atualizarValores(tvSaldo, tvReceita, tvDespesa);
        updateHomeContent();
    }

    /**
     * Configura listeners de eventos da interface
     */
    private void configurarEventListeners() {
        final TextView tvSaldo = findViewById(R.id.tvSaldo);
        final TextView tvReceita = findViewById(R.id.tvReceita);
        final TextView tvDespesa = findViewById(R.id.tvDespesa);
        final ImageView imgEye = findViewById(R.id.imgEye);

        // Listener para toggle de visibilidade do saldo
        imgEye.setOnClickListener(v -> {
            saldoVisivel = !saldoVisivel;
            imgEye.setImageResource(saldoVisivel ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed);
            atualizarValores(tvSaldo, tvReceita, tvDespesa);
            updateHomeContent();
        });

        // Configurar botão de adicionar transação
        configurarBotaoAdicionar();

        // Inicializar sincronização em background
        inicializarSincronizacao();
    }

    /**
     * Configura a navegação principal do aplicativo
     */
    private void configurarNavegacao() {
        final ImageView navHome = findViewById(R.id.nav_home);
        final ImageView navMenu = findViewById(R.id.nav_menu);
        final ImageView navAccounts = findViewById(R.id.nav_accounts);
        final ImageView navMovements = findViewById(R.id.nav_movements);

        // Configurar navegação Home
        navHome.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.accentBlue));
            navMenu.setColorFilter(getResources().getColor(R.color.white));
        });

        // Configurar navegação Menu
        navMenu.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.white));
            navMenu.setColorFilter(getResources().getColor(R.color.accentBlue));
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("usuarioId", usuarioIdAtual);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        // Configurar navegação Contas
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

        // Configurar navegação Movimentações
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
    }

    /**
     * Configura o botão de adicionar transações e o painel expansível
     */
    private void configurarBotaoAdicionar() {
        final ImageButton navAdd = findViewById(R.id.nav_add);
        final FrameLayout addPanel = findViewById(R.id.add_panel);
        final LinearLayout btnReceita = findViewById(R.id.btnReceita);
        final LinearLayout btnDespesa = findViewById(R.id.btnDespesa);

        // Configurar botão principal de adicionar
        navAdd.setOnClickListener(v -> {
            if (addPanel.getVisibility() == View.VISIBLE) {
                addPanel.setVisibility(View.GONE);
                navAdd.setImageResource(R.drawable.ic_add);
            } else {
                addPanel.setVisibility(View.VISIBLE);
                navAdd.setImageResource(R.drawable.ic_close);
            }
        });

        // Fechar painel ao clicar fora
        addPanel.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
        });

        // Configurar botão de receita
        btnReceita.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
            showAddTransactionDialog(true);
        });

        // Configurar botão de despesa
        btnDespesa.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
            showAddTransactionDialog(false);
        });
    }

    /**
     * Exibe o diálogo para adicionar nova transação
     *
     * @param isReceitaPanel true para receita, false para despesa
     */
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
            if (contasList.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Nenhuma conta disponível")
                        .setMessage("É necessário criar uma conta antes de registrar movimentações. Acesse o menu de Contas para criar sua primeira conta.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
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
            if (categoriasList.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Nenhuma categoria disponível")
                        .setMessage("É necessário criar uma categoria de " + tipo + " antes de registrar movimentações. Acesse o menu de Categorias para criar sua primeira categoria.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
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

                    // Usar sync service para salvar e sincronizar
                    syncService.adicionarLancamento(lancamento, new SyncService.SyncCallback() {
                        @Override
                        public void onSyncStarted() {
                            // Opcional: mostrar loading
                        }

                        @Override
                        public void onSyncCompleted(boolean success, String message) {
                            runOnUiThread(() -> {
                                if (success) {
                                    atualizarValores(findViewById(R.id.tvSaldo), findViewById(R.id.tvReceita), findViewById(R.id.tvDespesa));
                                    updateHomeContent();
                                    dialog.dismiss();
                                } else {
                                    // Mesmo com erro de sync, a operação local foi feita
                                    atualizarValores(findViewById(R.id.tvSaldo), findViewById(R.id.tvReceita), findViewById(R.id.tvDespesa));
                                    updateHomeContent();
                                    dialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onSyncProgress(String operation) {
                            // Opcional: mostrar progresso
                        }
                    });
                } catch (NumberFormatException e) {
                    inputValor.setError("Valor inválido! Use apenas números e ponto para decimais.");
                }
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Atualiza os valores exibidos no dashboard principal
     *
     * Calcula e exibe o saldo total, receitas e despesas do usuário,
     * considerando o estado de visibilidade (oculto/visível).
     *
     * @param tvSaldo TextView que exibe o saldo total
     * @param tvReceita TextView que exibe o total de receitas
     * @param tvDespesa TextView que exibe o total de despesas
     */
    private void atualizarValores(TextView tvSaldo, TextView tvReceita, TextView tvDespesa) {
        double saldoTotal = 0.0;
        double receitasTotal = 0.0;
        double despesasTotal = 0.0;

        // Calcular totais considerando todas as contas do usuário
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);

        for (Conta conta : contas) {
            Double receitas = db.lancamentoDao().somaPorContaETipo(conta.id, "receita");
            Double despesas = db.lancamentoDao().somaPorContaETipo(conta.id, "despesa");

            double totalReceitas = receitas != null ? receitas : 0.0;
            double totalDespesas = despesas != null ? despesas : 0.0;

            // Saldo = saldo inicial + receitas - despesas
            saldoTotal += conta.saldoInicial + totalReceitas - totalDespesas;
            receitasTotal += totalReceitas;
            despesasTotal += totalDespesas;
        }

        // Exibir valores ou ocultar conforme configuração de visibilidade
        if (saldoVisivel) {
            tvSaldo.setText(formatarMoeda(saldoTotal));
            tvReceita.setText(formatarMoeda(receitasTotal));
            tvDespesa.setText(formatarMoeda(despesasTotal));
        } else {
            tvSaldo.setText("****");
            tvReceita.setText("****");
            tvDespesa.setText("****");
        }
    }

    /**
     * Consulta o total de receitas do usuário
     *
     * @return Valor total das receitas
     */
    private double consultarReceitas() {
        Double valor = db.lancamentoDao().somaPorTipo("receita", usuarioIdAtual);
        return valor != null ? valor : 0.0;
    }

    /**
     * Consulta o total de despesas do usuário
     *
     * @return Valor total das despesas
     */
    private double consultarDespesas() {
        Double valor = db.lancamentoDao().somaPorTipo("despesa", usuarioIdAtual);
        return valor != null ? valor : 0.0;
    }

    /**
     * Calcula o saldo atual do usuário
     *
     * @return Saldo total (receitas - despesas)
     */
    private double consultarSaldo() {
        return consultarReceitas() - consultarDespesas();
    }

    /**
     * Formata valor monetário para exibição
     *
     * @param valor Valor a ser formatado
     * @return String formatada no padrão brasileiro (R$ 0,00)
     */
    private String formatarMoeda(double valor) {
        return String.format("R$ %.2f", valor);
    }

    /**
     * Atualiza todo o conteúdo da tela principal
     *
     * Coordena a atualização de todos os resumos:
     * - Resumo de contas
     * - Resumo de categorias
     * - Transações recentes
     */
    private void updateHomeContent() {
        updateAccountsSummary();
        updateCategoriesSummary();
        updateRecentTransactions();
    }

    /**
     * Atualiza o resumo das contas do usuário no dashboard
     *
     * Cria dinamicamente os cartões de contas com seus respectivos saldos
     * e configura os listeners para navegação.
     */
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

    /**
     * Classe auxiliar para gerenciar gastos por categoria
     *
     * Utilizada internamente para organizar e ordenar categorias
     * por valor de gasto para exibição no dashboard.
     */
    private static class CategoryExpense {
        Categoria categoria;
        double totalGasto;

        CategoryExpense(Categoria categoria, double totalGasto) {
            this.categoria = categoria;
            this.totalGasto = totalGasto;
        }
    }

    /**
     * Atualiza o resumo de categorias de despesa no dashboard
     *
     * Exibe as 5 categorias com maiores gastos, ordenadas por valor.
     * Mostra apenas categorias que possuem movimentações registradas.
     */
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

    /**
     * Atualiza a lista de transações recentes no dashboard
     *
     * Exibe as 5 transações mais recentes do usuário com ícones
     * indicativos e cores apropriadas para receitas/despesas.
     */
    private void updateRecentTransactions() {
        LinearLayout container = findViewById(R.id.recent_transactions_container);
        container.removeAllViews();

        // Buscar as 5 transações mais recentes
        List<Lancamento> transacoes = db.lancamentoDao().listarUltimasPorUsuario(usuarioIdAtual, 5);
        for (Lancamento transacao : transacoes) {
            // Criar item visual para a transação
            LinearLayout itemTransacao = new LinearLayout(this);
            itemTransacao.setOrientation(LinearLayout.HORIZONTAL);
            itemTransacao.setPadding(16, 12, 16, 12);
            itemTransacao.setClickable(true);
            itemTransacao.setFocusable(true);

            // Configurar navegação ao clicar
            itemTransacao.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MovementsActivity.class);
                intent.putExtra("usuarioId", usuarioIdAtual);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });

            // Ícone da transação (seta para cima/baixo)
            ImageView icon = new ImageView(this);
            icon.setImageResource(transacao.tipo.equals("receita") ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
            icon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            icon.setColorFilter(transacao.tipo.equals("receita") ?
                    getResources().getColor(R.color.positiveGreen) :
                    getResources().getColor(R.color.negativeRed));

            // Container com informações da transação
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

    /**
     * Calcula o saldo atual de uma conta específica
     *
     * @param conta A conta para calcular o saldo
     * @return Saldo atual (saldo inicial + receitas - despesas)
     */
    private double consultarSaldoConta(Conta conta) {
        Double receitas = db.lancamentoDao().somaPorContaETipo(conta.id, "receita");
        Double despesas = db.lancamentoDao().somaPorContaETipo(conta.id, "despesa");

        double totalReceitas = receitas != null ? receitas : 0.0;
        double totalDespesas = despesas != null ? despesas : 0.0;

        return conta.saldoInicial + totalReceitas - totalDespesas;
    }

    /**
     * Inicializa sincronização em background com o servidor desktop
     *
     * Executa sincronização silenciosa dos dados do usuário.
     * Em caso de falha, os dados locais permanecem disponíveis.
     */
    private void inicializarSincronizacao() {
        // Tenta sincronizar dados se conectado ao servidor
        syncService.sincronizarTudo(usuarioIdAtual, new SyncService.SyncCallback() {
            @Override
            public void onSyncStarted() {
                Log.d(TAG, "Sincronização iniciada...");
            }

            @Override
            public void onSyncCompleted(boolean success, String message) {
                Log.d(TAG, "Sincronização concluída: " + message);

                // Atualiza a UI na thread principal após sincronização
                runOnUiThread(() -> {
                    atualizarValores(findViewById(R.id.tvSaldo), findViewById(R.id.tvReceita), findViewById(R.id.tvDespesa));
                    updateHomeContent();

                    // Log de falha sem incomodar o usuário
                    if (!success && message.contains("Erro:")) {
                        Log.w(TAG, "Sincronização falhou: " + message);
                        // Dados locais permanecem disponíveis
                    }
                });
            }

            @Override
            public void onSyncProgress(String operation) {
                Log.d(TAG, "Sincronização: " + operation);
            }
        });
    }

    /**
     * Chamado quando a atividade é retomada
     *
     * Executa nova sincronização para manter dados atualizados.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Sincroniza sempre que volta para a tela principal
        if (syncService != null) {
            inicializarSincronizacao();
        }
    }

    /**
     * Chamado quando a atividade é destruída
     *
     * O SyncService é mantido como singleton para persistir entre atividades.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // SyncService é singleton e deve persistir entre Activities
        // Não fazemos shutdown aqui para evitar RejectedExecutionException
    }
}