package com.example.finanza;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Button;
import com.example.finanza.R;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Lancamento;
import com.example.finanza.model.Usuario;
import com.example.finanza.model.Conta;
import com.example.finanza.model.Categoria;
import com.example.finanza.ui.MenuActivity;
import com.example.finanza.ui.MovementsActivity; // ADICIONE ESTA IMPORTAÇÃO

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean saldoVisivel = true;
    private AppDatabase db;
    private int usuarioIdAtual;
    private int contaPadraoId;
    private int categoriaReceitaId;
    private int categoriaDespesaId;

    // Para painel customizado
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
                .allowMainThreadQueries()
                .build();

        // Busca ou cria usuário padrão
        Usuario usuario;
        List<Usuario> usuarios = db.usuarioDao().listarTodos();
        if (usuarios.size() == 0) {
            usuario = new Usuario();
            usuario.nome = "Usuário";
            usuario.email = "exemplo@email.com";
            int id = (int) db.usuarioDao().inserir(usuario);
            usuario.id = id;
        } else {
            usuario = usuarios.get(0);
        }
        usuarioIdAtual = usuario.id;

        // Busca ou cria conta padrão
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

// Busca ou cria categorias padrão de receita
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
// Atualiza a lista após possíveis inserções
        receitasCats = db.categoriaDao().listarPorTipo("receita");

// Seleciona a categoria "Receita" (principal)
        Categoria catReceita = null;
        for (Categoria cat : receitasCats) {
            if ("Receita".equalsIgnoreCase(cat.nome)) {
                catReceita = cat;
                break;
            }
        }
        if (catReceita == null && !receitasCats.isEmpty()) {
            catReceita = receitasCats.get(0); // fallback
        }
        categoriaReceitaId = catReceita != null ? catReceita.id : -1;

// Busca ou cria categorias padrão de despesa
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
// Atualiza a lista após possíveis inserções
        despesaCats = db.categoriaDao().listarPorTipo("despesa");

// Seleciona a categoria "Despesa" (principal)
        Categoria catDespesa = null;
        for (Categoria cat : despesaCats) {
            if ("Despesa".equalsIgnoreCase(cat.nome)) {
                catDespesa = cat;
                break;
            }
        }
        if (catDespesa == null && !despesaCats.isEmpty()) {
            catDespesa = despesaCats.get(0); // fallback
        }
        categoriaDespesaId = catDespesa != null ? catDespesa.id : -1;

        final TextView tvSaldo = findViewById(R.id.tvSaldo);
        final TextView tvReceita = findViewById(R.id.tvReceita);
        final TextView tvDespesa = findViewById(R.id.tvDespesa);
        final ImageView imgEye = findViewById(R.id.imgEye);

        atualizarValores(tvSaldo, tvReceita, tvDespesa);

        imgEye.setOnClickListener(v -> {
            saldoVisivel = !saldoVisivel;
            if (saldoVisivel) {
                atualizarValores(tvSaldo, tvReceita, tvDespesa);
                imgEye.setImageResource(R.drawable.ic_eye_open);
            } else {
                tvSaldo.setText("****");
                tvReceita.setText("****");
                tvDespesa.setText("****");
                imgEye.setImageResource(R.drawable.ic_eye_closed);
            }
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
                Intent intent = new Intent(this, com.example.finanza.ui.AccountsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // remova se quiser voltar para a MainActivity ao pressionar "back"
            });
        }

        final ImageView navMovements = findViewById(R.id.nav_movements);
        if (navMovements != null) {
            navMovements.setOnClickListener(v -> {
                navHome.setColorFilter(getResources().getColor(R.color.white));
                navMenu.setColorFilter(getResources().getColor(R.color.white));
                navMovements.setColorFilter(getResources().getColor(R.color.accentBlue));
                Intent intent = new Intent(this, MovementsActivity.class); // ADICIONADO
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        final ImageButton navAdd = findViewById(R.id.nav_add);
        final FrameLayout addPanel = findViewById(R.id.add_panel);
        final LinearLayout btnReceita = findViewById(R.id.btnReceita);
        final LinearLayout btnDespesa = findViewById(R.id.btnDespesa);

        // Painel customizado
        final FrameLayout addTransactionPanel = findViewById(R.id.add_transaction_panel);
        final ImageButton btnClosePanel = findViewById(R.id.btn_close_panel);
        final Button btnSalvarLancamento = findViewById(R.id.btn_salvar_lancamento);

        final TextInputEditText inputNome = findViewById(R.id.input_nome);
        final TextInputEditText inputConta = findViewById(R.id.input_conta);
        final TextInputEditText inputData = findViewById(R.id.input_data);
        final TextInputEditText inputCategoria = findViewById(R.id.input_categoria);
        final TextInputEditText inputValor = findViewById(R.id.input_valor);

        navAdd.setOnClickListener(v -> {
            if (addPanel.getVisibility() == View.GONE && addTransactionPanel.getVisibility() == View.GONE) {
                addPanel.setVisibility(View.VISIBLE);
                navAdd.setImageResource(R.drawable.ic_close);
            } else {
                addPanel.setVisibility(View.GONE);
                addTransactionPanel.setVisibility(View.GONE);
                navAdd.setImageResource(R.drawable.ic_add);
            }
        });

        addPanel.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
        });

        btnReceita.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            addTransactionPanel.setVisibility(View.VISIBLE);
            navAdd.setImageResource(R.drawable.ic_close);
            isReceitaPanel = true;
            inicializarCamposPainel(inputNome, inputConta, inputData, inputCategoria, inputValor, true);
        });

        btnDespesa.setOnClickListener(v -> {
            addPanel.setVisibility(View.GONE);
            addTransactionPanel.setVisibility(View.VISIBLE);
            navAdd.setImageResource(R.drawable.ic_close);
            isReceitaPanel = false;
            inicializarCamposPainel(inputNome, inputConta, inputData, inputCategoria, inputValor, false);
        });

        btnClosePanel.setOnClickListener(v -> {
            addTransactionPanel.setVisibility(View.GONE);
            navAdd.setImageResource(R.drawable.ic_add);
        });

        // Campo Conta (dialogo de seleção)
        inputConta.setOnClickListener(v -> {
            List<Conta> contasList = db.contaDao().listarPorUsuario(usuarioIdAtual);
            String[] contasArray = new String[contasList.size()];
            for (int i = 0; i < contasList.size(); i++) {
                contasArray[i] = contasList.get(i).nome;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecionar conta");
            builder.setItems(contasArray, (dialog, which) -> {
                contaSelecionada = contasList.get(which);
                inputConta.setText(contaSelecionada.nome);
            });
            builder.show();
        });

        // Campo Data (DatePicker)
        inputData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dataSelecionada);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth);
                        dataSelecionada = chosen.getTimeInMillis();
                        inputData.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Campo Categoria (dialogo de seleção)
        inputCategoria.setOnClickListener(v -> {
            String tipo = isReceitaPanel ? "receita" : "despesa";
            List<Categoria> categoriasList = db.categoriaDao().listarPorTipo(tipo);
            String[] categoriasArray = new String[categoriasList.size()];
            for (int i = 0; i < categoriasList.size(); i++) {
                categoriasArray[i] = categoriasList.get(i).nome;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecionar categoria");
            builder.setItems(categoriasArray, (dialog, which) -> {
                categoriaSelecionada = categoriasList.get(which);
                inputCategoria.setText(categoriaSelecionada.nome);
            });
            builder.show();
        });

        btnSalvarLancamento.setOnClickListener(v -> {
            String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
            String valorStr = inputValor.getText() != null ? inputValor.getText().toString().replace(",", ".").trim() : "";
            
            // Clear previous errors
            inputNome.setError(null);
            inputConta.setError(null);
            inputData.setError(null);
            inputCategoria.setError(null);
            inputValor.setError(null);
            
            boolean hasError = false;
            
            if (contaSelecionada == null) {
                inputConta.setError("Selecione a conta");
                hasError = true;
            }
            if (categoriaSelecionada == null) {
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
                    
                    // Validate positive value
                    if (valor <= 0) {
                        inputValor.setError("O valor deve ser maior que zero");
                        return;
                    }
                    
                    // Create transaction
                    Lancamento lancamento = new Lancamento();
                    lancamento.valor = valor;
                    lancamento.data = dataSelecionada;
                    lancamento.descricao = nome.isEmpty() ? (isReceitaPanel ? "Receita" : "Despesa") : nome;
                    lancamento.contaId = contaSelecionada.id;
                    lancamento.categoriaId = categoriaSelecionada.id;
                    lancamento.usuarioId = usuarioIdAtual;
                    lancamento.tipo = isReceitaPanel ? "receita" : "despesa";
                    
                    db.lancamentoDao().inserir(lancamento);
                    atualizarValores(tvSaldo, tvReceita, tvDespesa);
                    addTransactionPanel.setVisibility(View.GONE);
                    navAdd.setImageResource(R.drawable.ic_add);
                    limparCamposPainel(inputNome, inputConta, inputData, inputCategoria, inputValor);
                    
                } catch (NumberFormatException e) {
                    inputValor.setError("Valor inválido! Use apenas números e ponto para decimais.");
                }
            }
        });
    }

    private void inicializarCamposPainel(TextInputEditText inputNome,
                                         TextInputEditText inputConta,
                                         TextInputEditText inputData,
                                         TextInputEditText inputCategoria,
                                         TextInputEditText inputValor,
                                         boolean isReceita) {
        inputNome.setText("");
        inputConta.setText(contaSelecionada != null ? contaSelecionada.nome : "");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dataSelecionada);
        inputData.setText(String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
        inputCategoria.setText(categoriaSelecionada != null ? categoriaSelecionada.nome : "");
        inputValor.setText("");
    }

    private void limparCamposPainel(TextInputEditText inputNome,
                                    TextInputEditText inputConta,
                                    TextInputEditText inputData,
                                    TextInputEditText inputCategoria,
                                    TextInputEditText inputValor) {
        inputNome.setText("");
        inputConta.setText("");
        inputData.setText("");
        inputCategoria.setText("");
        inputValor.setText("");
        categoriaSelecionada = null;
        dataSelecionada = System.currentTimeMillis();
    }

    private void atualizarValores(TextView tvSaldo, TextView tvReceita, TextView tvDespesa) {
        double receitas = consultarReceitas();
        double despesas = consultarDespesas();
        double saldo = receitas - despesas;
        if (saldoVisivel) {
            tvSaldo.setText(String.format("R$ %.2f", saldo));
            tvReceita.setText(String.format("R$ %.2f", receitas));
            tvDespesa.setText(String.format("R$ %.2f", despesas));
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
}