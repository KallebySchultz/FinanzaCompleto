package com.example.finanza.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.ScrollView;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.view.ViewGroup;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Usuario;
import com.example.finanza.model.Lancamento;
import com.example.finanza.MainActivity;
import com.example.finanza.network.SyncService;

import java.util.List;

/**
 * AccountsActivity - Tela de Gerenciamento de Contas
 *
 * Esta atividade permite ao usuário gerenciar suas contas financeiras
 * no aplicativo Finanza. Oferece funcionalidades completas de CRUD para
 * diferentes tipos de contas bancárias e financeiras.
 *
 * Funcionalidades principais:
 * - Visualização de todas as contas do usuário
 * - Criação de novas contas (corrente, poupança, cartão, investimento, dinheiro)
 * - Edição de contas existentes (nome e saldo inicial)
 * - Exclusão de contas com validação
 * - Cálculo automático de saldo atual baseado em lançamentos
 * - Sincronização automática com servidor desktop
 * - Navegação integrada com outras telas do app
 *
 * Tipos de conta suportados:
 * - Conta Corrente: Conta bancária para movimentações diárias
 * - Poupança: Conta de investimento de baixo risco
 * - Cartão: Controle de gastos com cartão de crédito
 * - Investimento: Aplicações e investimentos diversos
 * - Dinheiro: Controle de dinheiro em espécie
 *
 * Layout:
 * - Lista dinâmica de contas com saldo e tipo
 * - Botão flutuante para adicionar nova conta
 * - Navegação inferior para outras seções do app
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class AccountsActivity extends AppCompatActivity {

    private AppDatabase db;
    private int usuarioIdAtual;
    private SyncService syncService;

    // Views principais
    private TextView txtSaldoAtual;
    private TextView defaultAccountName, defaultAccountSaldo;
    private ImageView defaultAccountIcon;
    private LinearLayout accountsList, defaultAccountBox;

    // Balão de cadastro de contas
    private FrameLayout contasPanel;
    private EditText inputNomeConta, inputSaldoInicial;
    private Button btnSalvarConta;
    private ImageButton navAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);

        db = AppDatabase.getDatabase(getApplicationContext());

        // Inicializar sync service
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

        txtSaldoAtual = findViewById(R.id.txt_saldo_atual);
        defaultAccountName = findViewById(R.id.default_account_name);
        defaultAccountSaldo = findViewById(R.id.default_account_saldo);
        defaultAccountIcon = findViewById(R.id.default_account_icon);
        defaultAccountBox = findViewById(R.id.default_account_box);
        accountsList = findViewById(R.id.accounts_list);

        contasPanel = findViewById(R.id.contas_panel);
        inputNomeConta = findViewById(R.id.input_nome_conta);
        inputSaldoInicial = findViewById(R.id.input_saldo_inicial);
        btnSalvarConta = findViewById(R.id.btn_salvar_conta);

        navAdd = findViewById(R.id.nav_add);

        navAdd.setOnClickListener(v -> {
            if (contasPanel.getVisibility() == View.GONE) {
                contasPanel.setVisibility(View.VISIBLE);
                navAdd.setImageResource(R.drawable.ic_close);
            } else {
                contasPanel.setVisibility(View.GONE);
                navAdd.setImageResource(R.drawable.ic_add);
                inputNomeConta.setText("");
                inputSaldoInicial.setText("");
            }
        });

        contasPanel.setOnClickListener(v -> {
            contasPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
            inputNomeConta.setText("");
            inputSaldoInicial.setText("");
        });
        contasPanel.findViewById(R.id.input_nome_conta).setOnClickListener(v -> {});
        contasPanel.findViewById(R.id.input_saldo_inicial).setOnClickListener(v -> {});
        contasPanel.findViewById(R.id.btn_salvar_conta).setOnClickListener(v -> {});

        btnSalvarConta.setOnClickListener(v -> {
            String nomeConta = inputNomeConta.getText() != null ? inputNomeConta.getText().toString().trim() : "";
            String saldoStr = inputSaldoInicial.getText() != null ? inputSaldoInicial.getText().toString().trim() : "";
            double saldoInicial = 0.0;
            if (!saldoStr.isEmpty()) {
                try {
                    saldoInicial = Double.parseDouble(saldoStr.replace(",", "."));
                } catch (Exception e) {
                    inputSaldoInicial.setError("Saldo inválido");
                    return;
                }
            }
            if (nomeConta.isEmpty()) {
                inputNomeConta.setError("Digite o nome");
                return;
            }
            Conta novaConta = new Conta();
            novaConta.nome = nomeConta;
            novaConta.saldoInicial = saldoInicial;
            novaConta.saldoAtual = saldoInicial; // Initialize current balance
            novaConta.usuarioId = usuarioIdAtual;
            
            // Use SyncService instead of direct DAO call
            syncService.adicionarConta(novaConta, new SyncService.SyncCallback() {
                @Override
                public void onSyncStarted() {
                    // Optional: show loading indicator
                }

                @Override
                public void onSyncCompleted(boolean success, String message) {
                    runOnUiThread(() -> {
                        if (success) {
                            updateAccounts();
                            contasPanel.setVisibility(View.GONE);
                            navAdd.setImageResource(R.drawable.ic_add);
                            inputNomeConta.setText("");
                            inputSaldoInicial.setText("");
                            Toast.makeText(AccountsActivity.this, "Conta cadastrada!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountsActivity.this, "Erro ao cadastrar conta: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSyncProgress(String operation) {
                    // Optional: show progress
                }
            });
        });

        final ImageView navHome = findViewById(R.id.nav_home);
        final ImageView navMenu = findViewById(R.id.nav_menu);
        final ImageView navAccounts = findViewById(R.id.nav_accounts);
        final ImageView navMovements = findViewById(R.id.nav_movements);

        navHome.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.accentBlue));
            navMenu.setColorFilter(getResources().getColor(R.color.white));
            navAccounts.setColorFilter(getResources().getColor(R.color.white));
            navMovements.setColorFilter(getResources().getColor(R.color.white));
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navMenu.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.white));
            navMenu.setColorFilter(getResources().getColor(R.color.accentBlue));
            navAccounts.setColorFilter(getResources().getColor(R.color.white));
            navMovements.setColorFilter(getResources().getColor(R.color.white));
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navAccounts.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.white));
            navMenu.setColorFilter(getResources().getColor(R.color.white));
            navAccounts.setColorFilter(getResources().getColor(R.color.accentBlue));
            navMovements.setColorFilter(getResources().getColor(R.color.white));
        });

        navMovements.setOnClickListener(v -> {
            navHome.setColorFilter(getResources().getColor(R.color.white));
            navMenu.setColorFilter(getResources().getColor(R.color.white));
            navAccounts.setColorFilter(getResources().getColor(R.color.white));
            navMovements.setColorFilter(getResources().getColor(R.color.accentBlue));
            Intent intent = new Intent(this, MovementsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navAccounts.setColorFilter(getResources().getColor(R.color.accentBlue));
        navHome.setColorFilter(getResources().getColor(R.color.white));
        navMenu.setColorFilter(getResources().getColor(R.color.white));
        navMovements.setColorFilter(getResources().getColor(R.color.white));

        updateAccounts();
    }

    private void updateAccounts() {
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);

        accountsList.removeAllViews();

        double saldoTotal = consultarSaldoGeral();
        txtSaldoAtual.setText(formatarMoeda(saldoTotal));

        for (Conta conta : contas) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(16, 20, 16, 20);

            item.setBackground(getResources().getDrawable(R.drawable.bg_transaction_item));
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            itemParams.bottomMargin = 12;
            item.setLayoutParams(itemParams);

            LinearLayout infoBox = new LinearLayout(this);
            infoBox.setOrientation(LinearLayout.VERTICAL);
            infoBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView name = new TextView(this);
            name.setText(conta.nome);
            name.setTextColor(getResources().getColor(R.color.white));
            name.setTextSize(18);
            name.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView saldo = new TextView(this);
            double saldoConta = consultarSaldoConta(conta);
            saldo.setText(formatarMoeda(saldoConta));
            saldo.setTextColor(saldoConta >= 0 ? getResources().getColor(R.color.positiveGreen) : getResources().getColor(R.color.negativeRed));
            saldo.setTextSize(16);
            saldo.setTypeface(null, android.graphics.Typeface.BOLD);

            infoBox.addView(name);
            infoBox.addView(saldo);

            item.addView(infoBox);

            final Conta finalConta = conta;
            item.setOnClickListener(v -> editarConta(finalConta));
            item.setOnLongClickListener(v -> {
                confirmarExclusaoConta(finalConta);
                return true;
            });

            accountsList.addView(item);
        }
        if (defaultAccountBox != null) {
            defaultAccountBox.setVisibility(View.GONE);
        }
    }

    private double consultarSaldoConta(Conta conta) {
        Double receitas = db.lancamentoDao().somaPorTipoConta("receita", usuarioIdAtual, conta.id);
        Double despesas = db.lancamentoDao().somaPorTipoConta("despesa", usuarioIdAtual, conta.id);
        receitas = receitas != null ? receitas : 0.0;
        despesas = despesas != null ? despesas : 0.0;
        return conta.saldoInicial + receitas - despesas;
    }

    private double consultarSaldoGeral() {
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        double saldoTotal = 0.0;
        for (Conta conta : contas) {
            saldoTotal += consultarSaldoConta(conta);
        }
        return saldoTotal;
    }

    // Modal personalizado para editar conta
    private void editarConta(Conta conta) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_account, null);

        EditText inputNome = dialogView.findViewById(R.id.input_nome_conta);
        EditText inputSaldo = dialogView.findViewById(R.id.input_saldo_inicial);
        Button btnSalvar = dialogView.findViewById(R.id.btn_salvar_conta);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        inputNome.setText(conta.nome);
        inputSaldo.setText(String.valueOf(conta.saldoInicial));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        btnSalvar.setOnClickListener(v -> {
            String novoNome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
            String novoSaldoStr = inputSaldo.getText() != null ? inputSaldo.getText().toString().trim() : "";

            if (!novoNome.isEmpty()) {
                conta.nome = novoNome;
                if (!novoSaldoStr.isEmpty()) {
                    try {
                        double novoSaldo = Double.parseDouble(novoSaldoStr.replace(",", "."));
                        conta.saldoInicial = novoSaldo;
                        conta.saldoAtual = novoSaldo; // Update current balance too
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Saldo inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                
                // Use SyncService instead of direct DAO call
                syncService.atualizarConta(conta, new SyncService.SyncCallback() {
                    @Override
                    public void onSyncStarted() {
                        // Optional: show loading indicator
                    }

                    @Override
                    public void onSyncCompleted(boolean success, String message) {
                        runOnUiThread(() -> {
                            if (success) {
                                updateAccounts();
                                dialog.dismiss();
                                Toast.makeText(AccountsActivity.this, "Conta atualizada!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AccountsActivity.this, "Erro ao atualizar conta: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSyncProgress(String operation) {
                        // Optional: show progress
                    }
                });
            } else {
                Toast.makeText(this, "Preencha o nome da conta", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
    }

    // Modal personalizado para excluir conta
    private void confirmarExclusaoConta(Conta conta) {
        List<Lancamento> lancamentos = db.lancamentoDao().listarPorConta(conta.id);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);

        TextView deleteMessage = dialogView.findViewById(R.id.delete_message);
        Button btnExcluir = dialogView.findViewById(R.id.btn_excluir);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar);

        String message = "Deseja excluir a conta '" + conta.nome + "'?";
        if (!lancamentos.isEmpty()) {
            message += "\n\nATENÇÃO: Esta conta possui " + lancamentos.size() +
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
            syncService.deletarConta(conta, new SyncService.SyncCallback() {
                @Override
                public void onSyncStarted() {
                    // Optional: show loading indicator
                }

                @Override
                public void onSyncCompleted(boolean success, String message) {
                    runOnUiThread(() -> {
                        if (success) {
                            updateAccounts();
                            dialog.dismiss();
                            Toast.makeText(AccountsActivity.this, "Conta excluída!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountsActivity.this, "Erro ao excluir conta: " + message, Toast.LENGTH_SHORT).show();
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

    private String formatarMoeda(double valor) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        return formatter.format(valor);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Sincroniza dados e atualiza UI
        if (syncService != null) {
            syncService.sincronizarTudo(usuarioIdAtual, new SyncService.SyncCallback() {
                @Override
                public void onSyncStarted() {
                    // Opcional: mostrar indicador de sync
                }

                @Override
                public void onSyncCompleted(boolean success, String message) {
                    // Atualiza UI na thread principal após sincronização
                    runOnUiThread(() -> updateAccounts());
                }

                @Override
                public void onSyncProgress(String operation) {
                    // Opcional: mostrar progresso
                }
            });
        }
    }
}