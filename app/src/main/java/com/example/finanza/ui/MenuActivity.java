package com.example.finanza.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.RadioGroup;
import com.example.finanza.network.SyncService;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import com.example.finanza.R;
import com.example.finanza.db.AppDatabase;
import com.example.finanza.model.Categoria;

/**
 * MenuActivity - Tela de Menu e Configurações Rápidas
 *
 * Esta atividade serve como hub central de navegação e configurações
 * rápidas do aplicativo Finanza. Oferece acesso rápido a funcionalidades
 * importantes e atalhos para outras telas.
 *
 * Funcionalidades principais:
 * - Dashboard com visão geral das finanças
 * - Acesso rápido a todas as seções do app
 * - Painel para adicionar categoria rapidamente
 * - Navegação para telas de contas, categorias, movimentos
 * - Acesso ao perfil do usuário
 * - Configurações do aplicativo
 * - Sincronização manual de dados
 *
 * Navegação disponível:
 * - Home/Dashboard: Visão geral financeira
 * - Contas: Gerenciamento de contas bancárias
 * - Categorias: Gerenciamento de categorias
 * - Movimentos: Lançamentos financeiros
 * - Perfil: Dados do usuário
 * - Configurações: Preferências do app
 *
 * Painel de categoria rápida:
 * - Permite criar categoria sem sair do menu
 * - Seleção de tipo (receita/despesa)
 * - Validação de nome único
 *
 * Layout:
 * - Navegação inferior persistente
 * - Botão flutuante para ações rápidas
 * - Cards organizados por seção
 * - Interface intuitiva e responsiva
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
public class MenuActivity extends AppCompatActivity {
    private FrameLayout categoriasPanel;
    private EditText inputNomeCategoria;
    private RadioGroup tipoGroup;
    private Button btnSalvarCategoria;
    private Button btnVoltarPainel;
    private AppDatabase db;
    private SyncService syncService;
    private int usuarioIdAtual;

    /**
     * Método onCreate - Inicialização da tela de menu
     *
     * Responsável por:
     * 1. Configurar layout e componentes visuais
     * 2. Inicializar banco de dados e serviços
     * 3. Validar autenticação do usuário
     * 4. Configurar navegação inferior
     * 5. Configurar painel de cadastro rápido de categoria
     * 6. Configurar listeners para todos os botões
     *
     * Fluxo de autenticação:
     * - Tenta obter usuarioId da Intent (vindo de outra tela)
     * - Se não encontrar, busca em SharedPreferences (sessão salva)
     * - Se ainda não encontrar, redireciona para login
     *
     * @param savedInstanceState Estado salvo da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inicializar banco de dados Room
        db = AppDatabase.getDatabase(getApplicationContext());

        // Inicializar serviço de sincronização com servidor desktop
        syncService = SyncService.getInstance(this);

        // ========== VALIDAÇÃO DE AUTENTICAÇÃO ==========
        // Obter ID do usuário da Intent (passado de outras telas)
        usuarioIdAtual = getIntent().getIntExtra("usuarioId", -1);
        
        // Se não veio pela Intent, busca em SharedPreferences (sessão salva)
        if (usuarioIdAtual == -1) {
            SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
            usuarioIdAtual = prefs.getInt("usuarioId", -1);
            
            // Se ainda não encontrou, usuário não está autenticado
            if (usuarioIdAtual == -1) {
                // Redireciona para tela de login
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish(); // Fecha MenuActivity para não voltar aqui com botão Voltar
                return;
            }
        }

        // ========== CONFIGURAÇÃO DA NAVEGAÇÃO INFERIOR ==========
        // Botão Home - Redireciona para dashboard principal
        ImageView navHome = findViewById(R.id.nav_home);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, com.example.finanza.MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Transição suave sem animação
                finish();
            });
        }

        // Botão Contas - Redireciona para gerenciamento de contas
        ImageView navAccounts = findViewById(R.id.nav_accounts);
        if (navAccounts != null) {
            navAccounts.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, AccountsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // Botão Movimentos - Redireciona para lançamentos financeiros
        ImageView navMovements = findViewById(R.id.nav_movements);
        if (navMovements != null) {
            navMovements.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, MovementsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // Botão Menu - Já está na tela de menu, não faz nada
        ImageView navMenu = findViewById(R.id.nav_menu);
        if (navMenu != null) {
            navMenu.setOnClickListener(v -> {
                // Já está na tela de menu - sem ação necessária
            });
        }

        // Destaca o ícone do menu na navegação inferior
        highlightBottomNav();

        // ========== CONFIGURAÇÃO DO PAINEL DE CATEGORIA RÁPIDA ==========
        // Vincular elementos do painel expansível de categoria
        categoriasPanel = findViewById(R.id.categorias_panel);
        inputNomeCategoria = findViewById(R.id.input_nome_categoria);
        tipoGroup = findViewById(R.id.tipo_group);
        btnSalvarCategoria = findViewById(R.id.btn_salvar_categoria);
        btnVoltarPainel = findViewById(R.id.btn_voltar_painel);

        // ========== CONFIGURAÇÃO DOS BOTÕES DO MENU ==========
        // Botão Categorias - Abre tela completa de gerenciamento
        TextView btnCategorias = findViewById(R.id.btnCategorias);
        if (btnCategorias != null) {
            btnCategorias.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, CategoriaActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // Botão Perfil - Abre tela de perfil do usuário
        TextView btnPerfil = findViewById(R.id.btnPerfil);
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // ========== CONFIGURAÇÃO DO PAINEL DE CADASTRO RÁPIDO ==========
        // Botão Voltar do painel - Fecha o painel e limpa campos
        if (btnVoltarPainel != null) {
            btnVoltarPainel.setOnClickListener(v -> {
                categoriasPanel.setVisibility(View.GONE);
                inputNomeCategoria.setText(""); // Limpa campo nome
                tipoGroup.check(R.id.radio_receita); // Reseta para receita
            });
        }

        // Botão Salvar categoria - Valida e salva nova categoria
        if (btnSalvarCategoria != null) {
            btnSalvarCategoria.setOnClickListener(v -> {
                // Obtém nome digitado
                String nome = inputNomeCategoria.getText().toString().trim();
                
                // Obtém tipo selecionado (receita ou despesa)
                String tipo = tipoGroup.getCheckedRadioButtonId() == R.id.radio_receita ? "receita" : "despesa";
                
                // Define cor padrão baseado no tipo
                String corHex = tipo.equals("receita") ? "#22BB33" : "#FF2222"; // Verde ou Vermelho
                
                // Validação: nome não pode estar vazio
                if (nome.isEmpty()) {
                    inputNomeCategoria.setError("Digite o nome");
                    return;
                }
                
                // Cria nova categoria
                Categoria categoria = new Categoria();
                categoria.nome = nome;
                categoria.corHex = corHex;
                categoria.tipo = tipo;
                
                // Salva no banco de dados local
                db.categoriaDao().inserir(categoria);

                // Exibe mensagem de sucesso e fecha painel
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Categoria cadastrada com sucesso!")
                        .setPositiveButton("OK", (d, w) -> categoriasPanel.setVisibility(View.GONE))
                        .show();

                // Limpa campos para próximo cadastro
                inputNomeCategoria.setText("");
                tipoGroup.check(R.id.radio_receita);
            });
        }

        // ========== CONFIGURAÇÃO DA SINCRONIZAÇÃO MANUAL ==========
        // Botão para forçar sincronização com servidor desktop
        TextView btnSyncServer = findViewById(R.id.btnSyncServer);
        if (btnSyncServer != null) {
            btnSyncServer.setVisibility(View.VISIBLE);
            btnSyncServer.setText("Sincronizar com Servidor");
            btnSyncServer.setOnClickListener(v -> realizarSincronizacao());
        }

        // ========== CONFIGURAÇÃO DE CONFIGURAÇÕES ==========
        // Botão Settings - Abre tela de configurações do aplicativo
        TextView btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        // ========== CONFIGURAÇÃO DE LOGOUT ==========
        // Botão Logout - Encerra sessão do usuário
        TextView btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> realizarLogout());
        }
    }

    /**
     * Destaca o ícone do menu na navegação inferior
     *
     * Altera a cor do ícone do menu para azul (ativo) e os demais
     * para branco (inativos), indicando visualmente que o usuário
     * está na tela de menu.
     *
     * Cores utilizadas:
     * - accentBlue: Ícone ativo (tela atual)
     * - white: Ícones inativos (outras telas)
     */
    private void highlightBottomNav() {
        // Busca todos os ícones da navegação inferior
        ImageView navMenu = findViewById(R.id.nav_menu);
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navMovements = findViewById(R.id.nav_movements);
        ImageView navAccounts = findViewById(R.id.nav_accounts);

        // Define cor azul para ícone do menu (tela ativa)
        if (navMenu != null)
            navMenu.setColorFilter(ContextCompat.getColor(this, R.color.accentBlue));
            
        // Define cor branca para outros ícones (telas inativas)
        if (navHome != null)
            navHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navMovements != null)
            navMovements.setColorFilter(ContextCompat.getColor(this, R.color.white));
        if (navAccounts != null)
            navAccounts.setColorFilter(ContextCompat.getColor(this, R.color.white));
    }

    /**
     * Realiza sincronização manual com o servidor desktop
     *
     * Exibe um diálogo de confirmação antes de iniciar a sincronização.
     * Após confirmação, aciona o SyncService para sincronizar todos os
     * dados do usuário com o servidor via TCP/IP.
     *
     * Processo de sincronização:
     * 1. Exibe diálogo de confirmação
     * 2. Se confirmado, inicia SyncService.sincronizarTudo()
     * 3. SyncService envia dados locais pendentes ao servidor
     * 4. SyncService busca dados atualizados do servidor
     * 5. Resolução automática de conflitos por timestamp
     * 6. Exibe diálogo com resultado (sucesso ou erro)
     *
     * Callbacks do SyncService:
     * - onSyncStarted: Início da sincronização (opcional: exibir loading)
     * - onSyncCompleted: Fim da sincronização com status e mensagem
     * - onSyncProgress: Progresso durante sync (opcional: exibir etapas)
     *
     * Tratamento de UI:
     * - Sempre usa runOnUiThread para atualizar interface
     * - Exibe resultado em diálogo modal
     * - Mensagem clara de sucesso ou erro
     *
     * Nota: Funciona mesmo se servidor estiver offline (dados salvos localmente)
     */
    private void realizarSincronizacao() {
        // Cria diálogo de confirmação
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sincronização com Servidor");
        builder.setMessage("Deseja sincronizar seus dados com o servidor?");
        
        // Botão Sincronizar - Confirma e inicia processo
        builder.setPositiveButton("Sincronizar", (dialog, which) -> {
            // Aciona SyncService para sincronizar tudo
            syncService.sincronizarTudo(usuarioIdAtual, new SyncService.SyncCallback() {
                /**
                 * Callback chamado quando sincronização inicia
                 * Opcional: Pode exibir loading ou feedback visual
                 */
                @Override
                public void onSyncStarted() {
                    // Pode exibir um ProgressDialog ou loading se necessário
                }

                /**
                 * Callback chamado quando sincronização termina
                 *
                 * @param success true se sincronização foi bem-sucedida
                 * @param message Mensagem detalhada do resultado
                 */
                @Override
                public void onSyncCompleted(boolean success, String message) {
                    // IMPORTANTE: Sempre usar runOnUiThread para atualizar UI
                    // pois callback pode vir de thread de background
                    runOnUiThread(() -> {
                        // Cria diálogo com resultado
                        AlertDialog.Builder resultDialog = new AlertDialog.Builder(MenuActivity.this);
                        resultDialog.setTitle(success ? "Sincronização Concluída" : "Erro na Sincronização");
                        resultDialog.setMessage(message);
                        resultDialog.setPositiveButton("OK", null);
                        resultDialog.show();
                    });
                }

                /**
                 * Callback chamado durante progresso da sincronização
                 *
                 * @param operation Operação sendo executada (ex: "Enviando contas...")
                 */
                @Override
                public void onSyncProgress(String operation) {
                    // Pode exibir progresso detalhado se necessário
                }
            });
        });
        
        // Botão Cancelar - Fecha diálogo sem fazer nada
        builder.setNegativeButton("Cancelar", null);
        
        // Exibe o diálogo
        builder.show();
    }

    /**
     * Realiza logout do usuário
     *
     * Exibe diálogo de confirmação antes de encerrar a sessão.
     * Após confirmação, limpa dados de autenticação e redireciona
     * para a tela de login.
     *
     * Processo de logout:
     * 1. Exibe diálogo de confirmação
     * 2. Se confirmado, limpa SharedPreferences (sessão salva)
     * 3. Cria Intent para LoginActivity com flags especiais
     * 4. Flags limpam pilha de Activities (não pode voltar)
     * 5. Inicia LoginActivity e encerra MenuActivity
     *
     * Flags usadas:
     * - FLAG_ACTIVITY_NEW_TASK: Cria nova task (nova pilha de Activities)
     * - FLAG_ACTIVITY_CLEAR_TASK: Limpa pilha anterior (impede voltar)
     *
     * Resultado:
     * - Usuário é levado para tela de login
     * - Não pode voltar para telas autenticadas usando botão Voltar
     * - Dados de sessão são apagados (precisa fazer login novamente)
     *
     * Nota: Dados do usuário permanecem salvos localmente (Room Database)
     */
    private void realizarLogout() {
        // Cria diálogo de confirmação
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sair");
        builder.setMessage("Tem certeza que deseja sair?");
        
        // Botão Sim - Confirma logout
        builder.setPositiveButton("Sim", (dialog, which) -> {
            // Limpa dados de autenticação salvos
            SharedPreferences prefs = getSharedPreferences("FinanzaAuth", MODE_PRIVATE);
            prefs.edit().clear().apply(); // Remove usuarioId, email, etc
            
            // Cria Intent para LoginActivity com flags especiais
            Intent intent = new Intent(this, LoginActivity.class);
            // NEW_TASK: Inicia nova pilha de Activities
            // CLEAR_TASK: Remove Activities antigas da pilha (não pode voltar)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            
            // Inicia LoginActivity
            startActivity(intent);
            
            // Encerra MenuActivity (não ficará na pilha)
            finish();
        });
        
        // Botão Cancelar - Fecha diálogo sem fazer nada
        builder.setNegativeButton("Cancelar", null);
        
        // Exibe o diálogo
        builder.show();
    }
}