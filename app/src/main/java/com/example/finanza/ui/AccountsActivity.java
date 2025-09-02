package com.example.finanza.ui;

import android.content.Intent;
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

import java.util.List;

/**
 * Activity para gerenciamento de contas
 */
public class AccountsActivity extends AppCompatActivity {

    private AppDatabase db;
    private int usuarioIdAtual;

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

        // Ajusta as cores da barra de status e navegação
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primaryDarkBlue));
        getWindow().getDecorView().setSystemUiVisibility(0);

        // Inicializa banco de dados Room
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "finanza-db")
                .allowMainThreadQueries()
                .build();

        // Busca usuário atual
        List<Usuario> usuarios = db.usuarioDao().listarTodos();
        usuarioIdAtual = usuarios.size() > 0 ? usuarios.get(0).id : 0;

        // Inicialização das views
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

        // Botão para mostrar/ocultar painel de cadastro de conta
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

        // Salvar nova conta
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

        // Navegação inferior
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

    /**
     * Atualiza a lista de contas exibida
     */
    private void updateAccounts() {
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);

        accountsList.removeAllViews();

        double saldoTotal = consultarSaldoGeral();
        txtSaldoAtual.setText(formatarMoeda(saldoTotal));

        // Remove o tratamento especial da conta padrão - todas as contas são tratadas igualmente
        for (Conta conta : contas) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(16, 20, 16, 20);

            // Fundo estilizado
            item.setBackground(getResources().getDrawable(R.drawable.bg_transaction_item));

            // Margem inferior para distanciamento
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            itemParams.bottomMargin = 12;
            item.setLayoutParams(itemParams);

            // Informações da conta (sem ícones)
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

            // Clique curto: editar conta
            final Conta finalConta = conta;
            item.setOnClickListener(v -> editarConta(finalConta));

            // Clique longo: excluir conta
            item.setOnLongClickListener(v -> {
                confirmarExclusaoConta(finalConta);
                return true;
            });

            accountsList.addView(item);
        }
        
        // Esconde a seção da conta padrão que estava sendo exibida separadamente
        if (defaultAccountBox != null) {
            defaultAccountBox.setVisibility(View.GONE);
        }
    }

    /**
     * Retorna saldo atual de uma conta
     */
    private double consultarSaldoConta(Conta conta) {
        Double receitas = db.lancamentoDao().somaPorTipoConta("receita", usuarioIdAtual, conta.id);
        Double despesas = db.lancamentoDao().somaPorTipoConta("despesa", usuarioIdAtual, conta.id);
        receitas = receitas != null ? receitas : 0.0;
        despesas = despesas != null ? despesas : 0.0;
        return conta.saldoInicial + receitas - despesas;
    }

    /**
     * Retorna saldo geral somando todas as contas
     */
    private double consultarSaldoGeral() {
        List<Conta> contas = db.contaDao().listarPorUsuario(usuarioIdAtual);
        double saldoTotal = 0.0;
        for (Conta conta : contas) {
            saldoTotal += consultarSaldoConta(conta);
        }
        return saldoTotal;
    }

    /**
     * Modal para edição de conta com botões corrigidos e layout comentado
     */
    private void editarConta(Conta conta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout para fundo arredondado e tamanho customizado
        FrameLayout frameLayout = new FrameLayout(this);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Modal maior: padding 24dp e largura 340dp
        int dpPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f); // Add high elevation to ensure modal appears above everything
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView title = new TextView(this);
        title.setText("Editar Conta");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Campo nome da conta
        final EditText inputNome = new EditText(this);
        inputNome.setHint("Nome da conta");
        inputNome.setText(conta.nome);
        inputNome.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputNome.setHintTextColor(getResources().getColor(R.color.darkGray));
        inputNome.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        LinearLayout.LayoutParams inputParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams1.bottomMargin = dpPadding / 2;
        inputNome.setLayoutParams(inputParams1);
        layout.addView(inputNome);

        // Campo saldo inicial
        final EditText inputSaldo = new EditText(this);
        inputSaldo.setHint("Saldo inicial");
        inputSaldo.setText(String.valueOf(conta.saldoInicial));
        inputSaldo.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        inputSaldo.setHintTextColor(getResources().getColor(R.color.darkGray));
        inputSaldo.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
        inputSaldo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams inputParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams2.bottomMargin = dpPadding / 2;
        inputSaldo.setLayoutParams(inputParams2);
        layout.addView(inputSaldo);

        // Botões "Salvar" e "Cancelar" com layout corrigido
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar");
        btnSalvar.setTextColor(getResources().getColor(R.color.white));
        btnSalvar.setTypeface(null, Typeface.BOLD);
        btnSalvar.setBackground(getResources().getDrawable(R.drawable.button_blue));
        LinearLayout.LayoutParams btnSalvarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // WRAP_CONTENT e peso 1f
        btnSalvarParams.rightMargin = dpPadding / 4;
        btnSalvar.setLayoutParams(btnSalvarParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // WRAP_CONTENT e peso 1f
        btnCancelarParams.leftMargin = dpPadding / 4;
        btnCancelar.setLayoutParams(btnCancelarParams);

        buttonLayout.addView(btnSalvar);
        buttonLayout.addView(btnCancelar);
        layout.addView(buttonLayout);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Listener do botão Salvar
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

        // Listener do botão Cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Exibe confirmação de exclusão de conta com modal consistente
     */
    private void confirmarExclusaoConta(Conta conta) {
        // Busca lançamentos da conta
        List<Lancamento> lancamentos = db.lancamentoDao().listarPorConta(conta.id);

        String message = "Deseja excluir a conta '" + conta.nome + "'?";
        if (!lancamentos.isEmpty()) {
            message += "\n\nATENÇÃO: Esta conta possui " + lancamentos.size() +
                    " transação(ões). Elas também serão excluídas.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // FrameLayout para fundo arredondado e tamanho customizado
        FrameLayout frameLayout = new FrameLayout(this);

        // ScrollView para garantir responsividade
        ScrollView scrollView = new ScrollView(this);

        // LinearLayout principal do modal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int dpPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layout.setPadding(dpPadding, dpPadding, dpPadding, dpPadding);
        layout.setBackground(getResources().getDrawable(R.drawable.bg_modal_white));
        layout.setElevation(16f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(layoutParams);

        // Título do modal
        TextView title = new TextView(this);
        title.setText("Excluir Conta");
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpPadding / 2;
        title.setLayoutParams(titleParams);
        layout.addView(title);

        // Mensagem de confirmação
        TextView messageText = new TextView(this);
        messageText.setText(message);
        messageText.setTextSize(16);
        messageText.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        messageText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        messageParams.bottomMargin = dpPadding;
        messageText.setLayoutParams(messageParams);
        layout.addView(messageText);

        // Botões "Excluir" e "Cancelar" com layout consistente
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button btnExcluir = new Button(this);
        btnExcluir.setText("Sim, excluir");
        btnExcluir.setTextColor(getResources().getColor(R.color.white));
        btnExcluir.setTypeface(null, Typeface.BOLD);
        btnExcluir.setBackground(getResources().getDrawable(R.drawable.button_red)); // Assuming red button for delete
        LinearLayout.LayoutParams btnExcluirParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnExcluirParams.rightMargin = dpPadding / 4;
        btnExcluir.setLayoutParams(btnExcluirParams);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(getResources().getColor(R.color.primaryDarkBlue));
        btnCancelar.setTypeface(null, Typeface.BOLD);
        btnCancelar.setBackground(getResources().getDrawable(R.drawable.button_gray));
        LinearLayout.LayoutParams btnCancelarParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnCancelarParams.leftMargin = dpPadding / 4;
        btnCancelar.setLayoutParams(btnCancelarParams);

        buttonLayout.addView(btnExcluir);
        buttonLayout.addView(btnCancelar);
        layout.addView(buttonLayout);

        // Adiciona o layout ao ScrollView e ao FrameLayout
        scrollView.addView(layout);
        frameLayout.addView(scrollView);
        builder.setView(frameLayout);

        // Fundo transparente para mostrar os cantos arredondados do modal
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Listener do botão Excluir
        btnExcluir.setOnClickListener(v -> {
            // Exclui todos os lançamentos antes da conta
            for (Lancamento lancamento : lancamentos) {
                db.lancamentoDao().deletar(lancamento);
            }
            db.contaDao().deletar(conta);
            updateAccounts();
            dialog.dismiss();
            Toast.makeText(this, "Conta excluída!", Toast.LENGTH_SHORT).show();
        });

        // Listener do botão Cancelar
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Formata valor monetário para exibição
     */
    private String formatarMoeda(double valor) {
        return String.format("R$ %.2f", valor);
    }
}
}