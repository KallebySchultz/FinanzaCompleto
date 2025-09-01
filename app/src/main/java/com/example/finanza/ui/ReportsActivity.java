package com.example.finanza.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private AppDatabase db;
    private int usuarioIdAtual;
    private LinearLayout reportsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .allowMainThreadQueries()
                .build();

        // Busca usuário atual
        List<Usuario> usuarios = db.usuarioDao().listarTodos();
        usuarioIdAtual = usuarios.size() > 0 ? usuarios.get(0).id : 0;

        reportsContainer = findViewById(R.id.reports_container);

        setupNavigation();
        generateReports();
    }

    private void setupNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMenu = findViewById(R.id.nav_menu);
        ImageView navAccounts = findViewById(R.id.nav_accounts);
        ImageView navMovements = findViewById(R.id.nav_movements);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navAccounts.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        navMovements.setOnClickListener(v -> {
            Intent intent = new Intent(this, MovementsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        // Highlight current navigation
        navMenu.setColorFilter(getResources().getColor(R.color.accentBlue));
    }

    private void generateReports() {
        // Get current month data
        Calendar currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);
        currentMonth.set(Calendar.HOUR_OF_DAY, 0);
        currentMonth.set(Calendar.MINUTE, 0);
        currentMonth.set(Calendar.SECOND, 0);
        currentMonth.set(Calendar.MILLISECOND, 0);

        Calendar endOfMonth = (Calendar) currentMonth.clone();
        endOfMonth.add(Calendar.MONTH, 1);
        endOfMonth.add(Calendar.MILLISECOND, -1);

        List<Lancamento> lancamentosDoMes = db.lancamentoDao().listarPorUsuarioPeriodo(
                usuarioIdAtual,
                currentMonth.getTimeInMillis(),
                endOfMonth.getTimeInMillis());

        // Summary Report
        addReportSection("📊 RESUMO FINANCEIRO", "");
        
        double totalReceitas = lancamentosDoMes.stream()
                .filter(l -> "receita".equals(l.tipo))
                .mapToDouble(l -> l.valor)
                .sum();
        
        double totalDespesas = lancamentosDoMes.stream()
                .filter(l -> "despesa".equals(l.tipo))
                .mapToDouble(l -> l.valor)
                .sum();
        
        double saldoDoMes = totalReceitas - totalDespesas;
        
        addReportItem("Receitas do mês", String.format("R$ %.2f", totalReceitas), R.color.positiveGreen);
        addReportItem("Despesas do mês", String.format("R$ %.2f", totalDespesas), R.color.negativeRed);
        addReportItem("Saldo do mês", String.format("R$ %.2f", saldoDoMes), 
                saldoDoMes >= 0 ? R.color.positiveGreen : R.color.negativeRed);

        // Category Analysis
        addReportSection("📈 ANÁLISE POR CATEGORIA", "");
        
        Map<String, Double> despesasPorCategoria = new HashMap<>();
        Map<String, Double> receitasPorCategoria = new HashMap<>();
        
        for (Lancamento lanc : lancamentosDoMes) {
            Categoria categoria = db.categoriaDao().buscarPorId(lanc.categoriaId);
            String nomeCategoria = categoria != null ? categoria.nome : "Categoria desconhecida";
            
            if ("despesa".equals(lanc.tipo)) {
                despesasPorCategoria.put(nomeCategoria, 
                    despesasPorCategoria.getOrDefault(nomeCategoria, 0.0) + lanc.valor);
            } else {
                receitasPorCategoria.put(nomeCategoria, 
                    receitasPorCategoria.getOrDefault(nomeCategoria, 0.0) + lanc.valor);
            }
        }

        // Top spending categories
        despesasPorCategoria.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> addReportItem(
                    "💸 " + entry.getKey(), 
                    String.format("R$ %.2f", entry.getValue()), 
                    R.color.negativeRed));

        // Top income categories
        receitasPorCategoria.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> addReportItem(
                    "💰 " + entry.getKey(), 
                    String.format("R$ %.2f", entry.getValue()), 
                    R.color.positiveGreen));

        // Account Analysis
        addReportSection("🏦 ANÁLISE POR CONTA", "");
        
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        for (Conta conta : contas) {
            double saldoConta = consultarSaldoConta(conta);
            addReportItem(conta.nome, String.format("R$ %.2f", saldoConta), 
                    saldoConta >= 0 ? R.color.positiveGreen : R.color.negativeRed);
        }

        // Transaction Count
        addReportSection("📝 ESTATÍSTICAS", "");
        addReportItem("Total de transações", String.valueOf(lancamentosDoMes.size()), R.color.white);
        addReportItem("Transações de receita", 
                String.valueOf(lancamentosDoMes.stream().mapToInt(l -> "receita".equals(l.tipo) ? 1 : 0).sum()), 
                R.color.positiveGreen);
        addReportItem("Transações de despesa", 
                String.valueOf(lancamentosDoMes.stream().mapToInt(l -> "despesa".equals(l.tipo) ? 1 : 0).sum()), 
                R.color.negativeRed);
    }

    private void addReportSection(String title, String subtitle) {
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText(title);
        sectionTitle.setTextColor(getResources().getColor(R.color.white));
        sectionTitle.setTextSize(18);
        sectionTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        sectionTitle.setPadding(0, 30, 0, 15);
        reportsContainer.addView(sectionTitle);

        if (!subtitle.isEmpty()) {
            TextView sectionSubtitle = new TextView(this);
            sectionSubtitle.setText(subtitle);
            sectionSubtitle.setTextColor(getResources().getColor(R.color.white));
            sectionSubtitle.setTextSize(14);
            sectionSubtitle.setPadding(0, 0, 0, 10);
            reportsContainer.addView(sectionSubtitle);
        }
    }

    private void addReportItem(String label, String value, int colorResId) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, 8, 0, 8);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextColor(getResources().getColor(R.color.white));
        labelView.setTextSize(16);
        labelView.setLayoutParams(new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(getResources().getColor(colorResId));
        valueView.setTextSize(16);
        valueView.setTypeface(null, android.graphics.Typeface.BOLD);

        item.addView(labelView);
        item.addView(valueView);
        reportsContainer.addView(item);
    }

    private double consultarSaldoConta(Conta conta) {
        Double receitas = db.lancamentoDao().somaPorTipoConta("receita", usuarioIdAtual, conta.id);
        Double despesas = db.lancamentoDao().somaPorTipoConta("despesa", usuarioIdAtual, conta.id);
        receitas = receitas != null ? receitas : 0.0;
        despesas = despesas != null ? despesas : 0.0;
        return conta.saldoInicial + receitas - despesas;
    }
}