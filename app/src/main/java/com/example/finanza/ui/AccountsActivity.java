package com.example.finanza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.ui.MenuActivity;
import com.example.finanza.ui.MovementsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountsActivity extends AppCompatActivity {

    private AppDatabase db;
    private int usuarioIdAtual;

    // Controle de mês/ano/dia
    private int currentMonth;
    private int currentYear;
    private int currentDay;

    // Views
    private TextView txtMonth, txtDia, txtSaldo, defaultAccountName, defaultAccountSaldo;
    private ImageView btnPrevMonth, btnNextMonth, defaultAccountIcon;
    private LinearLayout accountsList, transactionsList, defaultAccountBox;

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

        // Controle do mês/ano/dia atual
        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Referências dos elementos
        txtMonth = findViewById(R.id.txt_month);
        txtDia = findViewById(R.id.txt_dia);
        txtSaldo = findViewById(R.id.txt_saldo_atual);

        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);

        accountsList = findViewById(R.id.accounts_list);
        transactionsList = findViewById(R.id.transactions_list);

        defaultAccountBox = findViewById(R.id.default_account_box);
        defaultAccountIcon = findViewById(R.id.default_account_icon);
        defaultAccountName = findViewById(R.id.default_account_name);
        defaultAccountSaldo = findViewById(R.id.default_account_saldo);

        // Atualiza mês, dia, contas e lançamentos
        updateMonthAndDay();
        updateAccounts();
        updateLancamentos();

        // Botões navegação de mês
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            currentDay = 1; // volta para o primeiro dia do mês
            updateMonthAndDay();
            updateAccounts();
            updateLancamentos();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            currentDay = 1; // vai para o primeiro dia do mês
            updateMonthAndDay();
            updateAccounts();
            updateLancamentos();
        });

        // Navegação dos dias tocando no txtDia (opcional: pode fazer um picker ou botões)
        txtDia.setOnClickListener(v -> {
            // Avança um dia
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, currentYear);
            cal.set(Calendar.MONTH, currentMonth);
            cal.set(Calendar.DAY_OF_MONTH, currentDay);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            currentDay = cal.get(Calendar.DAY_OF_MONTH);
            currentMonth = cal.get(Calendar.MONTH);
            currentYear = cal.get(Calendar.YEAR);
            updateMonthAndDay();
            updateLancamentos();
        });
        txtDia.setOnLongClickListener(v -> {
            // Volta um dia
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, currentYear);
            cal.set(Calendar.MONTH, currentMonth);
            cal.set(Calendar.DAY_OF_MONTH, currentDay);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            currentDay = cal.get(Calendar.DAY_OF_MONTH);
            currentMonth = cal.get(Calendar.MONTH);
            currentYear = cal.get(Calendar.YEAR);
            updateMonthAndDay();
            updateLancamentos();
            return true;
        });

        // Bottom navigation
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_movements).setOnClickListener(v -> {
            Intent intent = new Intent(this, MovementsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_accounts).setOnClickListener(v -> {
            // já está na tela de contas
        });
        findViewById(R.id.nav_menu).setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        // Botão adicionar
        ImageButton navAdd = findViewById(R.id.nav_add);
        navAdd.setOnClickListener(v -> {
            // Implemente ação ao clicar em adicionar (ex: abrir tela de adicionar lançamento)
        });
    }

    private void updateMonthAndDay() {
        String[] meses = {"JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO",
                "JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"};
        String[] diasSemana = {"DOMINGO", "SEGUNDA", "TERÇA", "QUARTA", "QUINTA", "SEXTA", "SÁBADO"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);

        txtMonth.setText(meses[currentMonth]);
        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
        txtDia.setText(diasSemana[diaSemana - 1] + ", " + currentDay);
    }

    private void updateAccounts() {
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);

        accountsList.removeAllViews();
        double saldoTotal = 0.0;

        // Conta padrão: primeira da lista
        if (contas.size() > 0) {
            Conta contaDefault = contas.get(0);
            defaultAccountName.setText(contaDefault.nome);
            defaultAccountSaldo.setText(String.format("R$%.2f", contaDefault.saldoInicial));
            defaultAccountIcon.setImageResource(R.drawable.ic_bank);
        } else {
            defaultAccountName.setText("Nenhuma conta");
            defaultAccountSaldo.setText("R$0.00");
            defaultAccountIcon.setImageResource(R.drawable.ic_bank);
        }

        for (Conta conta : contas) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(android.view.Gravity.CENTER_VERTICAL);
            item.setPadding(0, 16, 0, 16);

            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
            icon.setImageResource(R.drawable.ic_bank); // ou personalize por tipo de conta
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
            saldo.setText(String.format("R$%.2f", conta.saldoInicial));
            saldo.setTextColor(getResources().getColor(R.color.positiveGreen));
            saldo.setTextSize(18);
            saldo.setTypeface(null, android.graphics.Typeface.BOLD);

            infoBox.addView(name);
            infoBox.addView(saldo);

            item.addView(icon);
            item.addView(infoBox);

            accountsList.addView(item);

            saldoTotal += conta.saldoInicial;
        }

        txtSaldo.setText(String.format("R$%.2f", saldoTotal));
    }

    private void updateLancamentos() {
        transactionsList.removeAllViews();

        // Busca todos os lançamentos do usuário no dia/mês/ano atual
        Calendar inicioDia = Calendar.getInstance();
        inicioDia.set(currentYear, currentMonth, currentDay, 0, 0, 0);
        inicioDia.set(Calendar.MILLISECOND, 0);
        long inicioTimestamp = inicioDia.getTimeInMillis();

        Calendar fimDia = Calendar.getInstance();
        fimDia.set(currentYear, currentMonth, currentDay, 23, 59, 59);
        fimDia.set(Calendar.MILLISECOND, 999);
        long fimTimestamp = fimDia.getTimeInMillis();

        // Supondo que exista método: listarPorUsuarioPeriodo(usuarioId, inicio, fim)
        List<Lancamento> lancamentos = db.lancamentoDao().listarPorUsuarioPeriodo(usuarioIdAtual, inicioTimestamp, fimTimestamp);

        if (lancamentos == null || lancamentos.size() == 0) {
            TextView empty = new TextView(this);
            empty.setText("Nenhum lançamento neste dia.");
            empty.setTextColor(getResources().getColor(R.color.white));
            empty.setPadding(12,12,12,12);
            transactionsList.addView(empty);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        for (Lancamento l : lancamentos) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setPadding(0, 12, 0, 12);

            TextView title = new TextView(this);
            title.setText(l.descricao);
            title.setTextColor(getResources().getColor(R.color.white));
            title.setTextSize(16);
            title.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView valor = new TextView(this);
            valor.setText(String.format("R$%.2f", l.valor));
            valor.setTextColor("receita".equalsIgnoreCase(l.tipo) ? getResources().getColor(R.color.positiveGreen) : getResources().getColor(R.color.negativeRed));
            valor.setTextSize(18);

            TextView date = new TextView(this);
            date.setText(sdf.format(new Date(l.data)));
            date.setTextColor(getResources().getColor(R.color.white));
            date.setTextSize(13);

            item.addView(title);
            item.addView(valor);
            item.addView(date);

            transactionsList.addView(item);
        }
    }
}