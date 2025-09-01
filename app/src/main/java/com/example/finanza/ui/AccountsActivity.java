package com.example.finanza.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
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
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Usuario;
import com.example.finanza.model.Lancamento;
import com.example.finanza.MainActivity;

import java.util.List;

public class AccountsActivity extends AppCompatActivity {

    private AppDatabase db;
    private int usuarioIdAtual;

    // Views
    private TextView txtSaldoAtual;
    private TextView defaultAccountName, defaultAccountSaldo;
    private ImageView defaultAccountIcon;
    private LinearLayout accountsList;

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

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .allowMainThreadQueries()
                .build();

        // Busca usuário atual
        List<Usuario> usuarios = db.usuarioDao().listarTodos();
        usuarioIdAtual = usuarios.size() > 0 ? usuarios.get(0).id : 0;

        // Referências dos elementos
        txtSaldoAtual = findViewById(R.id.txt_saldo_atual);
        defaultAccountName = findViewById(R.id.default_account_name);
        defaultAccountSaldo = findViewById(R.id.default_account_saldo);
        defaultAccountIcon = findViewById(R.id.default_account_icon);
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

        // Fecha painel ao clicar fora do balão/modal
        contasPanel.setOnClickListener(v -> {
            contasPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
            inputNomeConta.setText("");
            inputSaldoInicial.setText("");
        });
        // Evita fechar ao clicar dentro do painel
        contasPanel.findViewById(R.id.input_nome_conta).setOnClickListener(v -> {});
        contasPanel.findViewById(R.id.input_saldo_inicial).setOnClickListener(v -> {});
        contasPanel.findViewById(R.id.btn_salvar_conta).setOnClickListener(v -> {});

        btnSalvarConta.setOnClickListener(v -> {
            String nomeConta = inputNomeConta.getText().toString().trim();
            String saldoStr = inputSaldoInicial.getText().toString().trim();
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
            novaConta.usuarioId = usuarioIdAtual;
            db.contaDao().inserir(novaConta);
            updateAccounts();
            contasPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
            inputNomeConta.setText("");
            inputSaldoInicial.setText("");
            Toast.makeText(this, "Conta cadastrada!", Toast.LENGTH_SHORT).show();
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

        Conta contaPadrao = null;
        for (Conta c : contas) {
            if ("Conta Padrão".equals(c.nome)) {
                contaPadrao = c;
                break;
            }
        }

        txtSaldoAtual.setText(String.format("R$ %.2f", saldoTotal));

        if (contaPadrao != null) {
            defaultAccountName.setText(contaPadrao.nome);
            defaultAccountSaldo.setText(String.format("R$ %.2f", consultarSaldoConta(contaPadrao)));
            defaultAccountIcon.setImageResource(R.drawable.ic_bank);
        }

        for (Conta conta : contas) {
            if (contaPadrao != null && conta.id == contaPadrao.id) continue;

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(12, 16, 12, 16);
            
            // Create styled background similar to default account
            item.setBackground(getResources().getDrawable(R.drawable.bg_account_icon_circle_green));

            ImageView icon = new ImageView(this);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
            iconParams.setMargins(0, 0, 10, 0);
            icon.setLayoutParams(iconParams);
            icon.setImageResource(R.drawable.ic_bank);
            icon.setBackground(getResources().getDrawable(R.drawable.bg_account_icon_circle_purple));
            icon.setPadding(6, 6, 6, 6);

            LinearLayout infoBox = new LinearLayout(this);
            infoBox.setOrientation(LinearLayout.VERTICAL);
            infoBox.setPadding(10, 0, 0, 0);

            TextView name = new TextView(this);
            name.setText(conta.nome);
            name.setTextColor(getResources().getColor(R.color.white));
            name.setTextSize(16);
            name.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView saldo = new TextView(this);
            double saldoConta = consultarSaldoConta(conta);
            saldo.setText(String.format("R$ %.2f", saldoConta));
            saldo.setTextColor(getResources().getColor(R.color.positiveGreen));
            saldo.setTextSize(18);
            saldo.setTypeface(null, android.graphics.Typeface.BOLD);

            infoBox.addView(name);
            infoBox.addView(saldo);

            item.addView(icon);
            item.addView(infoBox);

            // Add click listener for edit functionality
            final Conta finalConta = conta;
            item.setOnClickListener(v -> editarConta(finalConta));
            
            // Add long click listener for delete functionality
            item.setOnLongClickListener(v -> {
                confirmarExclusaoConta(finalConta);
                return true;
            });

            accountsList.addView(item);
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

    private void editarConta(Conta conta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Conta");

        // Create a styled layout that matches the transaction launch panel
        FrameLayout frameLayout = new FrameLayout(this);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        
        // Title
        TextView title = new TextView(this);
        title.setText("Editar Conta");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = 16;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Input fields with proper styling
        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome da conta");
        inputNome.setText(conta.nome);
        inputNome.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams1 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams1.bottomMargin = 16;
        inputNome.setLayoutParams(inputParams1);
        layout.addView(inputNome);

        final EditText inputSaldo = new EditText(this);
        inputSaldo.setHint("Saldo inicial");
        inputSaldo.setText(String.valueOf(conta.saldoInicial));
        inputSaldo.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputSaldo.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputSaldo.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        inputSaldo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams inputParams2 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams2.bottomMargin = 16;
        inputSaldo.setLayoutParams(inputParams2);
        layout.addView(inputSaldo);

        // Styled buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.topMargin = 16;
        buttonLayout.setLayoutParams(buttonLayoutParams);

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar");
        btnSalvar.setTextColor(getResources().getColor(R.color.white));
        btnSalvar.setTypeface(null, Typeface.BOLD);
        btnSalvar.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnSalvarParams = new LinearLayout.LayoutParams(
            0, 48);
        btnSalvarParams.weight = 1;
        btnSalvarParams.rightMargin = 8;
        btnSalvar.setLayoutParams(btnSalvarParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, Typeface.BOLD);
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
            String novoSaldoStr = inputSaldo.getText().toString().trim();
            
            if (!novoNome.isEmpty()) {
                conta.nome = novoNome;
                if (!novoSaldoStr.isEmpty()) {
                    try {
                        conta.saldoInicial = Double.parseDouble(novoSaldoStr.replace(",", "."));
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Saldo inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                db.contaDao().atualizar(conta);
                updateAccounts();
                dialog.dismiss();
                Toast.makeText(this, "Conta atualizada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Preencha o nome da conta", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void confirmarExclusaoConta(Conta conta) {
        // Check if account has transactions
        List<Lancamento> lancamentos = db.lancamentoDao().listarPorConta(conta.id);
        
        String message = "Deseja excluir a conta '" + conta.nome + "'?";
        if (!lancamentos.isEmpty()) {
            message += "\n\nATENÇÃO: Esta conta possui " + lancamentos.size() + 
                      " transação(ões). Elas também serão excluídas.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Conta");
        builder.setMessage(message);
        
        builder.setPositiveButton("Sim, excluir", (dialog, which) -> {
            // Delete all transactions first (due to foreign key constraints)
            for (Lancamento lancamento : lancamentos) {
                db.lancamentoDao().deletar(lancamento);
            }
            
            // Then delete the account
            db.contaDao().deletar(conta);
            updateAccounts();
            Toast.makeText(this, "Conta excluída!", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}