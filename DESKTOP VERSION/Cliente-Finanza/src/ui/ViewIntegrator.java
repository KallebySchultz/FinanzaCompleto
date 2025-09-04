package ui;

import view.*;
import controller.*;
import model.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * ViewIntegrator - Integrates views with controllers and business logic
 */
public class ViewIntegrator {
    
    private final FinanzaDesktop app;
    private final NumberFormat currencyFormat;
    
    public ViewIntegrator(FinanzaDesktop app) {
        this.app = app;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        setupAllViewIntegrations();
    }
    
    private void setupAllViewIntegrations() {
        setupLoginView();
        setupHomeView();
        setupMovementsView();
        setupAccountsView();
        setupMenuView();
        setupCadastroView();
    }
    
    // ======= LOGIN VIEW INTEGRATION =======
    private void setupLoginView() {
        LoginView loginView = app.getLoginView();
        UsuarioController usuarioController = app.getUsuarioController();
        
        // Get the login button using reflection to access private field
        try {
            java.lang.reflect.Field buttonField = LoginView.class.getDeclaredField("jButtonLogin");
            buttonField.setAccessible(true);
            JButton loginButton = (JButton) buttonField.get(loginView);
            
            java.lang.reflect.Field emailField = LoginView.class.getDeclaredField("jTextFieldEmail");
            emailField.setAccessible(true);
            JTextField emailTextField = (JTextField) emailField.get(loginView);
            
            java.lang.reflect.Field senhaField = LoginView.class.getDeclaredField("jTextFieldSenha");
            senhaField.setAccessible(true);
            JTextField senhaTextField = (JTextField) senhaField.get(loginView);
            
            // Remove existing action listeners
            for (ActionListener al : loginButton.getActionListeners()) {
                loginButton.removeActionListener(al);
            }
            
            // Add new action listener for login
            loginButton.addActionListener(e -> {
                String email = emailTextField.getText().trim();
                String senha = senhaTextField.getText().trim();
                
                if (email.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(loginView, 
                        "Por favor, preencha todos os campos!", 
                        "Campos Obrigatórios", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Disable button during authentication
                loginButton.setEnabled(false);
                loginButton.setText("Autenticando...");
                
                // Create a worker thread for authentication
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return usuarioController.autenticar(email, senha);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                // Clear fields
                                emailTextField.setText("");
                                senhaTextField.setText("");
                                
                                // Show success message and navigate to home
                                JOptionPane.showMessageDialog(loginView, 
                                    "Login realizado com sucesso!", 
                                    "Sucesso", 
                                    JOptionPane.INFORMATION_MESSAGE);
                                
                                // Refresh home view data and navigate
                                refreshHomeViewData();
                                app.showHomeView();
                            } else {
                                JOptionPane.showMessageDialog(loginView, 
                                    "Email ou senha incorretos!", 
                                    "Erro de Autenticação", 
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(loginView, 
                                "Erro durante autenticação: " + e.getMessage(), 
                                "Erro", 
                                JOptionPane.ERROR_MESSAGE);
                        } finally {
                            // Re-enable button
                            loginButton.setEnabled(true);
                            loginButton.setText("Login");
                        }
                    }
                };
                worker.execute();
            });
            
            // Add "Cadastre-se" link functionality
            java.lang.reflect.Field cadastroField = LoginView.class.getDeclaredField("jLabel5");
            cadastroField.setAccessible(true);
            JLabel cadastroLabel = (JLabel) cadastroField.get(loginView);
            
            cadastroLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            cadastroLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    app.showCadastroView();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar LoginView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ======= HOME VIEW INTEGRATION =======
    private void setupHomeView() {
        HomeView homeView = app.getHomeView();
        
        try {
            // Setup navigation buttons
            setupNavigationButton(homeView, "jButtonHome", () -> app.showHomeView());
            setupNavigationButton(homeView, "jButtonMovements", () -> app.showMovementsView());
            setupNavigationButton(homeView, "jButtonAccounts", () -> app.showAccountsView());
            setupNavigationButton(homeView, "jButtonMenu", () -> app.showMenuView());
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar HomeView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void refreshHomeViewData() {
        if (!app.getUsuarioController().isLogado()) {
            return;
        }
        
        try {
            int userId = app.getUsuarioController().getUsuarioLogadoId();
            Usuario usuario = app.getUsuarioController().getUsuarioLogado();
            
            // Get financial summary
            LancamentoController lancamentoController = new LancamentoController();
            CategoriaController categoriaController = new CategoriaController();
            var resumo = lancamentoController.obterResumo(userId);
            
            HomeView homeView = app.getHomeView();
            
            // Update financial labels
            updateHomeLabel(homeView, "jLabel1", "SALDO TOTAL: " + currencyFormat.format(resumo.getSaldo()));
            updateHomeLabel(homeView, "jLabel2", "RECEITAS: " + currencyFormat.format(resumo.getTotalReceitas()));
            updateHomeLabel(homeView, "jLabel3", "DESPESAS: " + currencyFormat.format(resumo.getTotalDespesas()));
            
            // Update recent transactions
            List<Lancamento> allTransactions = lancamentoController.listarLancamentos(userId);
            if (allTransactions != null && !allTransactions.isEmpty()) {
                List<Lancamento> recentTransactions = allTransactions.subList(0, Math.min(5, allTransactions.size()));
                updateRecentTransactions(homeView, recentTransactions);
            } else {
                updateRecentTransactions(homeView, java.util.Collections.emptyList());
            }
            
            // Add charts to home view
            addChartsToHomeView(homeView, resumo, allTransactions, categoriaController.listarCategorias());
            
            // Update window title
            homeView.setTitle("Finanza - Bem-vindo, " + usuario.getNome());
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar dados da HomeView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateHomeLabel(HomeView homeView, String fieldName, String text) {
        try {
            java.lang.reflect.Field field = HomeView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            JLabel label = (JLabel) field.get(homeView);
            label.setText(text);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar label " + fieldName + ": " + e.getMessage());
        }
    }
    
    private void updateRecentTransactions(HomeView homeView, List<Lancamento> transactions) {
        try {
            java.lang.reflect.Field field = HomeView.class.getDeclaredField("jLabel4");
            field.setAccessible(true);
            JLabel transactionsLabel = (JLabel) field.get(homeView);
            
            if (transactions.isEmpty()) {
                transactionsLabel.setText("ÚLTIMAS TRANSAÇÕES: Nenhuma transação encontrada");
            } else {
                StringBuilder sb = new StringBuilder("<html>ÚLTIMAS TRANSAÇÕES:<br>");
                for (Lancamento lancamento : transactions) {
                    String tipo = "receita".equals(lancamento.getTipo()) ? "+" : "-";
                    sb.append(String.format("• %s %s %s<br>", 
                        tipo, 
                        currencyFormat.format(lancamento.getValor()),
                        lancamento.getDescricao()));
                }
                sb.append("</html>");
                transactionsLabel.setText(sb.toString());
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar transações recentes: " + e.getMessage());
        }
    }
    
    private void addChartsToHomeView(HomeView homeView, LancamentoController.TransactionSummary resumo, List<Lancamento> allTransactions, List<Categoria> categorias) {
        try {
            // Get the main panel
            java.lang.reflect.Field panelField = HomeView.class.getDeclaredField("jPanel5");
            panelField.setAccessible(true);
            JPanel mainPanel = (JPanel) panelField.get(homeView);
            
            // Clear existing charts if any
            mainPanel.removeAll();
            
            // Create income vs expense bar chart
            JPanel incomeExpenseChart = ChartUtils.createIncomeExpenseBarChart(
                resumo.getTotalReceitas(), 
                resumo.getTotalDespesas(), 
                400, 200
            );
            incomeExpenseChart.setBounds(10, 30, 400, 200);
            mainPanel.add(incomeExpenseChart);
            
            // Create expense pie chart if there are expenses
            List<Lancamento> despesas = allTransactions != null ? 
                allTransactions.stream()
                    .filter(l -> "despesa".equals(l.getTipo()))
                    .collect(java.util.stream.Collectors.toList()) 
                : java.util.Collections.emptyList();
                
            if (!despesas.isEmpty()) {
                JPanel expensePieChart = ChartUtils.createExpensePieChart(despesas, categorias, 400, 200);
                expensePieChart.setBounds(420, 30, 400, 200);
                mainPanel.add(expensePieChart);
            }
            
            // Repaint the panel
            mainPanel.revalidate();
            mainPanel.repaint();
            
        } catch (Exception e) {
            System.err.println("Erro ao adicionar gráficos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ======= MOVEMENTS VIEW INTEGRATION =======
    private void setupMovementsView() {
        MovementsView movementsView = app.getMovementsView();
        
        try {
            // Setup navigation buttons
            setupNavigationButton(movementsView, "jButtonHome", () -> app.showHomeView());
            setupNavigationButton(movementsView, "jButtonMovements", () -> app.showMovementsView());
            setupNavigationButton(movementsView, "jButtonAccounts", () -> app.showAccountsView());
            setupNavigationButton(movementsView, "jButtonMenu", () -> app.showMenuView());
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar MovementsView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ======= ACCOUNTS VIEW INTEGRATION =======
    private void setupAccountsView() {
        AccountsView accountsView = app.getAccountsView();
        
        try {
            // Setup navigation buttons
            setupNavigationButton(accountsView, "jButtonHome", () -> app.showHomeView());
            setupNavigationButton(accountsView, "jButtonMovements", () -> app.showMovementsView());
            setupNavigationButton(accountsView, "jButtonAccounts", () -> app.showAccountsView());
            setupNavigationButton(accountsView, "jButtonMenu", () -> app.showMenuView());
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar AccountsView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ======= MENU VIEW INTEGRATION =======
    private void setupMenuView() {
        MenuView menuView = app.getMenuView();
        
        try {
            // Setup navigation buttons
            setupNavigationButton(menuView, "jButtonHome", () -> app.showHomeView());
            setupNavigationButton(menuView, "jButtonMovements", () -> app.showMovementsView());
            setupNavigationButton(menuView, "jButtonAccounts", () -> app.showAccountsView());
            setupNavigationButton(menuView, "jButtonMenu", () -> app.showMenuView());
            
            // Add menu options click handlers
            setupMenuOption(menuView, "jLabel8", () -> app.showConfigView()); // CONFIGURAÇÕES
            
            // Add logout functionality if there's a logout button/label
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar MenuView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ======= CADASTRO VIEW INTEGRATION =======
    private void setupCadastroView() {
        CadastroView cadastroView = app.getCadastroView();
        
        try {
            // Add back to login functionality if there's a back button
            // This would be implemented based on the actual CadastroView structure
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar CadastroView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ======= UTILITY METHODS =======
    private void setupNavigationButton(JFrame view, String buttonFieldName, Runnable action) {
        try {
            java.lang.reflect.Field field = view.getClass().getDeclaredField(buttonFieldName);
            field.setAccessible(true);
            JButton button = (JButton) field.get(view);
            
            // Remove existing action listeners
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
            
            // Add new action listener
            button.addActionListener(e -> action.run());
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar botão " + buttonFieldName + ": " + e.getMessage());
        }
    }
    
    private void setupMenuOption(JFrame view, String labelFieldName, Runnable action) {
        try {
            java.lang.reflect.Field field = view.getClass().getDeclaredField(labelFieldName);
            field.setAccessible(true);
            JLabel label = (JLabel) field.get(view);
            
            label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    action.run();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Erro ao configurar menu option " + labelFieldName + ": " + e.getMessage());
        }
    }
}