package com.example.finanza.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import com.example.finanza.R;
import com.example.finanza.ui.AccountsActivity;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Conta;
import com.example.finanza.ui.MovementsActivity;
import com.example.finanza.ui.CategoriaActivity;
import com.example.finanza.ui.ReportsActivity;
import java.util.List;
public class MenuActivity extends AppCompatActivity {
    private FrameLayout categoriasPanel;
    private EditText inputNomeCategoria;
    private RadioGroup tipoGroup;
    private Button btnSalvarCategoria;
    private Button btnVoltarPainel;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .allowMainThreadQueries()
                .build();

        ImageView navHome = findViewById(R.id.nav_home);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, com.example.finanza.MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        ImageView navAccounts = findViewById(R.id.nav_accounts);
        if (navAccounts != null) {
            navAccounts.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, AccountsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        ImageView navMovements = findViewById(R.id.nav_movements);
        if (navMovements != null) {
            navMovements.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, MovementsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        ImageView navMenu = findViewById(R.id.nav_menu);
        if (navMenu != null) {
            navMenu.setOnClickListener(v -> {
                // Já está na tela de menu
            });
        }

        highlightBottomNav();

        categoriasPanel = findViewById(R.id.categorias_panel);
        inputNomeCategoria = findViewById(R.id.input_nome_categoria);
        tipoGroup = findViewById(R.id.tipo_group);
        btnSalvarCategoria = findViewById(R.id.btn_salvar_categoria);
        btnVoltarPainel = findViewById(R.id.btn_voltar_painel);

        TextView btnCategorias = findViewById(R.id.btnCategorias);
        if (btnCategorias != null) {
            btnCategorias.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, CategoriaActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnVoltarPainel != null) {
            btnVoltarPainel.setOnClickListener(v -> {
                categoriasPanel.setVisibility(View.GONE);
                inputNomeCategoria.setText("");
                tipoGroup.check(R.id.radio_receita);
            });
        }

        if (btnSalvarCategoria != null) {
            btnSalvarCategoria.setOnClickListener(v -> {
                String nome = inputNomeCategoria.getText().toString().trim();
                String tipo = tipoGroup.getCheckedRadioButtonId() == R.id.radio_receita ? "receita" : "despesa";
                String corHex = tipo.equals("receita") ? "#22BB33" : "#FF2222";
                if (nome.isEmpty()) {
                    inputNomeCategoria.setError("Digite o nome");
                    return;
                }
                Categoria categoria = new Categoria();
                categoria.nome = nome;
                categoria.corHex = corHex;
                categoria.tipo = tipo;
                db.categoriaDao().inserir(categoria);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Categoria cadastrada com sucesso!")
                        .setPositiveButton("OK", (d, w) -> categoriasPanel.setVisibility(View.GONE))
                        .show();

                inputNomeCategoria.setText("");
                tipoGroup.check(R.id.radio_receita);
            });
        }

        // Export functionality
        TextView btnExportar = findViewById(R.id.btnExportar);
        if (btnExportar != null) {
            btnExportar.setOnClickListener(v -> exportarDados());
        }

        // Reports functionality
        TextView btnGraficos = findViewById(R.id.btnGraficos);
        if (btnGraficos != null) {
            btnGraficos.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, ReportsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    private void exportarDados() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exportar Dados");
        builder.setMessage("Escolha o formato para exportar seus dados financeiros:");
        
        builder.setPositiveButton("Exportar CSV", (dialog, which) -> {
            try {
                // Create CSV data
                StringBuilder csvData = new StringBuilder();
                csvData.append("Tipo,Descrição,Valor,Data,Categoria,Conta\n");
                
                List<Lancamento> lancamentos = db.lancamentoDao().listarPorUsuario(1); // assuming user ID 1
                java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
                
                for (Lancamento lanc : lancamentos) {
                    Categoria categoria = db.categoriaDao().buscarPorId(lanc.categoriaId);
                    Conta conta = db.contaDao().buscarPorId(lanc.contaId);
                    
                    csvData.append("\"").append(lanc.tipo).append("\",")
                           .append("\"").append(lanc.descricao.replace("\"", "\"\"")).append("\",")
                           .append("\"").append(formatter.format(lanc.valor)).append("\",")
                           .append("\"").append(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(lanc.data))).append("\",")
                           .append("\"").append(categoria != null ? categoria.nome.replace("\"", "\"\"") : "N/A").append("\",")
                           .append("\"").append(conta != null ? conta.nome.replace("\"", "\"\"") : "N/A").append("\"\n");
                }
                
                // Create file in internal storage
                String fileName = "finanza_dados_" + new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date()) + ".csv";
                java.io.File csvFile = new java.io.File(getFilesDir(), fileName);
                
                try (java.io.FileWriter writer = new java.io.FileWriter(csvFile)) {
                    writer.write(csvData.toString());
                }
                
                // Show success dialog with file info
                AlertDialog.Builder successBuilder = new AlertDialog.Builder(this);
                successBuilder.setTitle("Exportação Concluída");
                successBuilder.setMessage("Arquivo CSV criado com sucesso!\n\n" +
                        "Arquivo: " + fileName + "\n" +
                        "Registros exportados: " + lancamentos.size() + "\n" +
                        "Tamanho: " + (csvFile.length() / 1024) + " KB\n\n" +
                        "O arquivo foi salvo no armazenamento interno do aplicativo.");
                
                successBuilder.setPositiveButton("Visualizar Dados", (d, w) -> {
                    // Show preview of CSV data
                    AlertDialog.Builder previewBuilder = new AlertDialog.Builder(this);
                    previewBuilder.setTitle("Preview dos Dados CSV");
                    previewBuilder.setMessage(csvData.length() > 1000 ? 
                        csvData.substring(0, 1000) + "..." : csvData.toString());
                    previewBuilder.setPositiveButton("OK", null);
                    previewBuilder.show();
                });
                
                successBuilder.setNegativeButton("OK", null);
                successBuilder.show();
                
            } catch (Exception e) {
                AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this);
                errorBuilder.setTitle("Erro na Exportação");
                errorBuilder.setMessage("Ocorreu um erro ao exportar os dados:\n" + e.getMessage());
                errorBuilder.setPositiveButton("OK", null);
                errorBuilder.show();
            }
        });
        
        builder.setNeutralButton("Exportar Relatório", (dialog, which) -> {
            try {
                // Create summary report
                StringBuilder reportData = new StringBuilder();
                reportData.append("=== RELATÓRIO FINANCEIRO FINANZA ===\n");
                reportData.append("Gerado em: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())).append("\n\n");
                
                // Get summary data
                List<Lancamento> lancamentos = db.lancamentoDao().listarPorUsuario(1);
                double totalReceitas = 0, totalDespesas = 0;
                java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
                
                for (Lancamento lanc : lancamentos) {
                    if ("receita".equals(lanc.tipo)) {
                        totalReceitas += lanc.valor;
                    } else {
                        totalDespesas += lanc.valor;
                    }
                }
                
                reportData.append("RESUMO GERAL:\n");
                reportData.append("Total de Receitas: ").append(formatter.format(totalReceitas)).append("\n");
                reportData.append("Total de Despesas: ").append(formatter.format(totalDespesas)).append("\n");
                reportData.append("Saldo Total: ").append(formatter.format(totalReceitas - totalDespesas)).append("\n");
                reportData.append("Total de Transações: ").append(lancamentos.size()).append("\n\n");
                
                // Add accounts summary
                List<Conta> contas = db.contaDao().listarTodos();
                reportData.append("CONTAS:\n");
                for (Conta conta : contas) {
                    double saldoConta = conta.saldoInicial;
                    List<Lancamento> lancamentosConta = db.lancamentoDao().buscarPorConta(conta.id);
                    for (Lancamento lanc : lancamentosConta) {
                        saldoConta += "receita".equals(lanc.tipo) ? lanc.valor : -lanc.valor;
                    }
                    reportData.append("- ").append(conta.nome).append(": ").append(formatter.format(saldoConta)).append("\n");
                }
                
                // Create file
                String fileName = "finanza_relatorio_" + new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date()) + ".txt";
                java.io.File reportFile = new java.io.File(getFilesDir(), fileName);
                
                try (java.io.FileWriter writer = new java.io.FileWriter(reportFile)) {
                    writer.write(reportData.toString());
                }
                
                // Show success dialog
                AlertDialog.Builder successBuilder = new AlertDialog.Builder(this);
                successBuilder.setTitle("Relatório Gerado");
                successBuilder.setMessage("Relatório criado com sucesso!\n\n" +
                        "Arquivo: " + fileName + "\n" +
                        "Tamanho: " + (reportFile.length() / 1024) + " KB");
                
                successBuilder.setPositiveButton("Visualizar", (d, w) -> {
                    AlertDialog.Builder previewBuilder = new AlertDialog.Builder(this);
                    previewBuilder.setTitle("Relatório Financeiro");
                    previewBuilder.setMessage(reportData.toString());
                    previewBuilder.setPositiveButton("OK", null);
                    previewBuilder.show();
                });
                
                successBuilder.setNegativeButton("OK", null);
                successBuilder.show();
                
            } catch (Exception e) {
                AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this);
                errorBuilder.setTitle("Erro na Geração do Relatório");
                errorBuilder.setMessage("Ocorreu um erro ao gerar o relatório:\n" + e.getMessage());
                errorBuilder.setPositiveButton("OK", null);
                errorBuilder.show();
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void highlightBottomNav() {
        ImageView navMenu = findViewById(R.id.nav_menu);
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMovements = findViewById(R.id.nav_movements);
        ImageView navAccounts = findViewById(R.id.nav_accounts);

        if (navMenu != null)
            navMenu.setColorFilter(ContextCompat.getColor(this, R.color.accentBlue));
        if (navHome != null)
            navHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navMovements != null)
            navMovements.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navAccounts != null)
            navAccounts.setColorFilter(ContextCompat.getColor(this, R.color.white));
    }
}