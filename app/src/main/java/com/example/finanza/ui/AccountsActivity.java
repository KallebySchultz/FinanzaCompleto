package com.example.finanza;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Usuario;
import com.example.finanza.ui.MenuActivity;
import com.example.finanza.ui.MovementsActivity;

import java.util.List;

public class AccountsActivity extends AppCompatActivity {

    private AppDatabase db;
    private int usuarioIdAtual;

    // Views
    private TextView txtSaldoAtual;
    private TextView defaultAccountName, defaultAccountSaldo;
    private ImageView defaultAccountIcon;
    private LinearLayout accountsList;

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

        // Botão adicionar conta
        ImageButton navAdd = findViewById(R.id.nav_add);
        navAdd.setOnClickListener(v -> {
            // Diálogo para nome da nova conta
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nova Conta");
            builder.setMessage("Digite o nome da conta:");

            final EditText input = new EditText(this);
            input.setHint("Nome da conta");
            builder.setView(input);

            builder.setPositiveButton("Criar", (dialog, which) -> {
                String nomeConta = input.getText().toString().trim();
                if (!nomeConta.isEmpty()) {
                    Conta novaConta = new Conta();
                    novaConta.nome = nomeConta;
                    novaConta.saldoInicial = 0.0;
                    novaConta.usuarioId = usuarioIdAtual;
                    db.contaDao().inserir(novaConta);
                    updateAccounts(); // Atualiza a lista
                    Toast.makeText(this, "Conta criada!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Digite um nome válido!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // Bottom Navigation
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
            // Já está na tela de contas!
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

        // Inicial: destaca Accounts
        navAccounts.setColorFilter(getResources().getColor(R.color.accentBlue));
        navHome.setColorFilter(getResources().getColor(R.color.white));
        navMenu.setColorFilter(getResources().getColor(R.color.white));
        navMovements.setColorFilter(getResources().getColor(R.color.white));

        updateAccounts();
    }

    private void updateAccounts() {
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);

        accountsList.removeAllViews();

        // Saldo total (todas as contas)
        double saldoTotal = consultarSaldoGeral();

        // Conta padrão
        Conta contaPadrao = null;
        for (Conta c : contas) {
            if ("Conta Padrão".equals(c.nome)) {
                contaPadrao = c;
                break;
            }
        }

        // Atualiza SALDO ATUAL (topo)
        txtSaldoAtual.setText(String.format("R$ %.2f", saldoTotal));

        // Conta padrão mostra saldo calculado
        if (contaPadrao != null) {
            defaultAccountName.setText(contaPadrao.nome);
            defaultAccountSaldo.setText(String.format("R$ %.2f", consultarSaldoConta(contaPadrao)));
            defaultAccountIcon.setImageResource(R.drawable.ic_bank);
        }

        // Lista as contas individualmente (saldo atualizado), exceto "Conta Padrão"
        for (Conta conta : contas) {
            if (contaPadrao != null && conta.id == contaPadrao.id) continue;

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(0, 16, 0, 16);

            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
            icon.setImageResource(R.drawable.ic_bank);
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

            accountsList.addView(item);
        }
    }

    // Saldo real de cada conta (saldoInicial + receitas - despesas)
    private double consultarSaldoConta(Conta conta) {
        Double receitas = db.lancamentoDao().somaPorTipoConta("receita", usuarioIdAtual, conta.id);
        Double despesas = db.lancamentoDao().somaPorTipoConta("despesa", usuarioIdAtual, conta.id);
        receitas = receitas != null ? receitas : 0.0;
        despesas = despesas != null ? despesas : 0.0;
        return conta.saldoInicial + receitas - despesas;
    }

    // Saldo total de todas as contas (usado no topo)
    private double consultarSaldoGeral() {
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        double saldoTotal = 0.0;
        for (Conta conta : contas) {
            saldoTotal += consultarSaldoConta(conta);
        }
        return saldoTotal;
    }
}