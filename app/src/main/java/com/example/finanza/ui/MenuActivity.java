package com.example.finanza.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

                showSuccessDialog("Sucesso", "Categoria cadastrada com sucesso!", () -> {
                    categoriasPanel.setVisibility(View.GONE);
                });

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

    private static final int CREATE_CSV_FILE = 1;
    private static final int CREATE_REPORT_FILE = 2;
    private StringBuilder pendingCsvData;
    private StringBuilder pendingReportData;

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
                
                // Store data for later use when file is selected
                pendingCsvData = csvData;
                
                // Launch file picker
                createCsvFile();
                
            } catch (Exception e) {
                showErrorDialog("Erro na Exportação", "Ocorreu um erro ao exportar os dados:\n" + e.getMessage());
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
                
                // Store data for later use when file is selected
                pendingReportData = reportData;
                
                // Launch file picker
                createReportFile();
                
            } catch (Exception e) {
                showErrorDialog("Erro na Geração do Relatório", "Ocorreu um erro ao gerar o relatório:\n" + e.getMessage());
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

    private void createCsvFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String fileName = "finanza_dados_" + new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date()) + ".csv";
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, CREATE_CSV_FILE);
    }

    private void createReportFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        String fileName = "finanza_relatorio_" + new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date()) + ".txt";
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, CREATE_REPORT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }

            if (uri != null) {
                try {
                    if (requestCode == CREATE_CSV_FILE && pendingCsvData != null) {
                        writeToFile(uri, pendingCsvData.toString());
                        
                        // Count records for success message
                        List<Lancamento> lancamentos = db.lancamentoDao().listarPorUsuario(1);
                        
                        showSuccessDialogWithPreview("Exportação Concluída", 
                            "Arquivo CSV criado com sucesso!\n\n" +
                            "Registros exportados: " + lancamentos.size() + "\n" +
                            "Arquivo salvo no local selecionado.",
                            "Preview dos Dados CSV",
                            pendingCsvData.length() > 1000 ? 
                                pendingCsvData.substring(0, 1000) + "..." : 
                                pendingCsvData.toString());
                        
                        pendingCsvData = null;
                        
                    } else if (requestCode == CREATE_REPORT_FILE && pendingReportData != null) {
                        writeToFile(uri, pendingReportData.toString());
                        
                        showSuccessDialogWithPreview("Relatório Gerado", 
                            "Relatório criado com sucesso!\n\n" +
                            "Arquivo salvo no local selecionado.",
                            "Relatório Financeiro",
                            pendingReportData.toString());
                        
                        pendingReportData = null;
                    }
                    
                } catch (Exception e) {
                    showErrorDialog("Erro ao Salvar Arquivo", "Ocorreu um erro ao salvar o arquivo:\n" + e.getMessage());
                }
            }
        }
    }

    private void writeToFile(Uri uri, String content) throws Exception {
        try (java.io.OutputStream outputStream = getContentResolver().openOutputStream(uri);
             java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(outputStream)) {
            writer.write(content);
        }
    }

    /**
     * Helper method to show standardized success/info dialogs
     */
    private void showSuccessDialog(String title, String message, Runnable onOkCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout centralizado
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER // CENTRALIZA O MODAL NA TELA!
        );
        frameLayout.setLayoutParams(frameParams);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int dpPadding = (int) android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        titleView.setLayoutParams(titleParams);
        layout.addView(titleView);

        // Mensagem
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        messageView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = dpPadding;
        messageView.setLayoutParams(messageParams);
        layout.addView(messageView);

        // Botão OK
        Button btnOk = new Button(this);
        btnOk.setText("OK");
        btnOk.setTextColor(getResources().getColor(R.color.white));
        btnOk.setTypeface(null, android.graphics.Typeface.BOLD);
        btnOk.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnOkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnOk.setLayoutParams(btnOkParams);
        layout.addView(btnOk);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Force center the dialog window
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        // Listener do botão OK
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (onOkCallback != null) {
                onOkCallback.run();
            }
        });

        dialog.show();
    }

    /**
     * Helper method to show standardized error dialogs
     */
    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout centralizado
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        frameLayout.setLayoutParams(frameParams);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int dpPadding = (int) android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setTextColor(getResources().getColor(R.color.negativeRed)); // Red for errors
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        titleView.setLayoutParams(titleParams);
        layout.addView(titleView);

        // Mensagem
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        messageView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = dpPadding;
        messageView.setLayoutParams(messageParams);
        layout.addView(messageView);

        // Botão OK
        Button btnOk = new Button(this);
        btnOk.setText("OK");
        btnOk.setTextColor(getResources().getColor(R.color.white));
        btnOk.setTypeface(null, android.graphics.Typeface.BOLD);
        btnOk.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnOkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnOk.setLayoutParams(btnOkParams);
        layout.addView(btnOk);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        btnOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Helper method to show standardized success dialogs with preview functionality
     */
    private void showSuccessDialogWithPreview(String title, String message, String previewTitle, String previewContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout centralizado
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        frameLayout.setLayoutParams(frameParams);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int dpPadding = (int) android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setTextColor(getResources().getColor(R.color.positiveGreen)); // Green for success
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        titleView.setLayoutParams(titleParams);
        layout.addView(titleView);

        // Mensagem
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        messageView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = dpPadding;
        messageView.setLayoutParams(messageParams);
        layout.addView(messageView);

        // Botões "Visualizar" e "OK"
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button btnPreview = new Button(this);
        btnPreview.setText("Visualizar");
        btnPreview.setTextColor(getResources().getColor(R.color.white));
        btnPreview.setTypeface(null, android.graphics.Typeface.BOLD);
        btnPreview.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnPreviewParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnPreviewParams.rightMargin = dpPadding / 4;
        btnPreview.setLayoutParams(btnPreviewParams);

        Button btnOk = new Button(this);
        btnOk.setText("OK");
        btnOk.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnOk.setTypeface(null, android.graphics.Typeface.BOLD);
        btnOk.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnOkParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnOkParams.leftMargin = dpPadding / 4;
        btnOk.setLayoutParams(btnOkParams);

        buttonLayout.addView(btnPreview);
        buttonLayout.addView(btnOk);
        layout.addView(buttonLayout);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        // Listeners dos botões
        btnPreview.setOnClickListener(v -> {
            dialog.dismiss();
            showSuccessDialog(previewTitle, previewContent, null);
        });

        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}