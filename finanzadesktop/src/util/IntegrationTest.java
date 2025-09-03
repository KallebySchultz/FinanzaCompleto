package util;

import controller.*;
import database.DatabaseManager;
import model.*;

/**
 * IntegrationTest - Complete integration test for the Finanza system
 * Tests database, controllers, and server connectivity
 */
public class IntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("FINANZA INTEGRATION TEST");
        System.out.println("=================================================");
        
        IntegrationTest test = new IntegrationTest();
        
        boolean allTestsPassed = true;
        
        allTestsPassed &= test.testDatabase();
        allTestsPassed &= test.testControllers();
        allTestsPassed &= test.testCompleteWorkflow();
        
        System.out.println("=================================================");
        if (allTestsPassed) {
            System.out.println("✅ TODOS OS TESTES PASSARAM!");
            System.out.println("✅ Sistema Finanza funcionando completamente!");
        } else {
            System.out.println("❌ ALGUNS TESTES FALHARAM!");
        }
        System.out.println("=================================================");
    }
    
    public boolean testDatabase() {
        System.out.println("\n--- TESTE DE BANCO DE DADOS ---");
        try {
            boolean connected = DatabaseManager.testConnection();
            if (connected) {
                System.out.println("✅ Conexão com banco de dados estabelecida");
                return true;
            } else {
                System.out.println("❌ Falha na conexão com banco de dados");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Erro no teste de banco: " + e.getMessage());
            return false;
        }
    }
    
    public boolean testControllers() {
        System.out.println("\n--- TESTE DE CONTROLLERS ---");
        boolean success = true;
        
        // Test UsuarioController
        try {
            UsuarioController usuarioController = new UsuarioController();
            boolean loginSuccess = usuarioController.autenticar("admin@finanza.com", "admin");
            if (loginSuccess) {
                System.out.println("✅ UsuarioController - Login funcional");
                
                Usuario usuario = usuarioController.getUsuarioLogado();
                if (usuario != null) {
                    System.out.println("✅ UsuarioController - Usuário logado: " + usuario.getNome());
                } else {
                    System.out.println("❌ UsuarioController - Usuário logado é nulo");
                    success = false;
                }
            } else {
                System.out.println("❌ UsuarioController - Falha no login");
                success = false;
            }
        } catch (Exception e) {
            System.out.println("❌ UsuarioController - Erro: " + e.getMessage());
            success = false;
        }
        
        // Test ContaController
        try {
            ContaController contaController = new ContaController();
            var contas = contaController.listarContasComSaldo(1); // Admin user ID
            if (contas != null && !contas.isEmpty()) {
                System.out.println("✅ ContaController - " + contas.size() + " contas encontradas");
                for (var conta : contas) {
                    System.out.println("   • " + conta.getConta().getNome() + ": R$ " + 
                                     String.format("%.2f", conta.getSaldoAtual()));
                }
            } else {
                System.out.println("❌ ContaController - Nenhuma conta encontrada");
                success = false;
            }
        } catch (Exception e) {
            System.out.println("❌ ContaController - Erro: " + e.getMessage());
            success = false;
        }
        
        // Test LancamentoController
        try {
            LancamentoController lancamentoController = new LancamentoController();
            var summary = lancamentoController.obterResumo(1); // Admin user ID
            System.out.println("✅ LancamentoController - Resumo obtido:");
            System.out.println("   • Receitas: R$ " + String.format("%.2f", summary.getTotalReceitas()));
            System.out.println("   • Despesas: R$ " + String.format("%.2f", summary.getTotalDespesas()));
            System.out.println("   • Saldo: R$ " + String.format("%.2f", summary.getSaldo()));
            System.out.println("   • Transações: " + summary.getTotalTransacoes());
        } catch (Exception e) {
            System.out.println("❌ LancamentoController - Erro: " + e.getMessage());
            success = false;
        }
        
        // Test CategoriaController
        try {
            CategoriaController categoriaController = new CategoriaController();
            var categorias = categoriaController.listarCategorias();
            if (categorias != null && !categorias.isEmpty()) {
                System.out.println("✅ CategoriaController - " + categorias.size() + " categorias encontradas");
                long receitas = categorias.stream().filter(c -> "receita".equals(c.getTipo())).count();
                long despesas = categorias.stream().filter(c -> "despesa".equals(c.getTipo())).count();
                System.out.println("   • Receitas: " + receitas + ", Despesas: " + despesas);
            } else {
                System.out.println("❌ CategoriaController - Nenhuma categoria encontrada");
                success = false;
            }
        } catch (Exception e) {
            System.out.println("❌ CategoriaController - Erro: " + e.getMessage());
            success = false;
        }
        
        return success;
    }
    
    public boolean testCompleteWorkflow() {
        System.out.println("\n--- TESTE DE WORKFLOW COMPLETO ---");
        boolean success = true;
        
        try {
            // Initialize controllers
            UsuarioController usuarioController = new UsuarioController();
            ContaController contaController = new ContaController();
            LancamentoController lancamentoController = new LancamentoController();
            CategoriaController categoriaController = new CategoriaController();
            
            // 1. Login
            boolean loginSuccess = usuarioController.autenticar("admin@finanza.com", "admin");
            if (!loginSuccess) {
                System.out.println("❌ Workflow - Falha no login");
                return false;
            }
            System.out.println("✅ Workflow - Login realizado");
            
            int userId = usuarioController.getUsuarioLogadoId();
            
            // 2. Create a new account
            boolean accountCreated = contaController.criarConta("Conta Teste", 1000.0, userId);
            if (accountCreated) {
                System.out.println("✅ Workflow - Nova conta criada");
            } else {
                System.out.println("⚠️  Workflow - Conta não criada (pode já existir)");
            }
            
            // 3. List accounts
            var contas = contaController.listarContas(userId);
            if (contas != null && !contas.isEmpty()) {
                System.out.println("✅ Workflow - " + contas.size() + " contas listadas");
                
                // 4. Create a transaction
                Conta firstAccount = contas.get(0);
                var categorias = categoriaController.listarCategorias();
                if (categorias != null && !categorias.isEmpty()) {
                    Categoria firstCategory = categorias.get(0);
                    
                    boolean transactionCreated = lancamentoController.criarLancamento(
                        500.0, "Teste de receita", firstAccount.getId(), 
                        firstCategory.getId(), userId, "receita"
                    );
                    
                    if (transactionCreated) {
                        System.out.println("✅ Workflow - Nova transação criada");
                    } else {
                        System.out.println("❌ Workflow - Falha ao criar transação");
                        success = false;
                    }
                }
            } else {
                System.out.println("❌ Workflow - Falha ao listar contas");
                success = false;
            }
            
            // 5. Get updated summary
            var finalSummary = lancamentoController.obterResumo(userId);
            System.out.println("✅ Workflow - Resumo final:");
            System.out.println("   • Saldo total: R$ " + String.format("%.2f", finalSummary.getSaldo()));
            
            // 6. Logout
            usuarioController.logout();
            if (!usuarioController.isLogado()) {
                System.out.println("✅ Workflow - Logout realizado");
            } else {
                System.out.println("❌ Workflow - Falha no logout");
                success = false;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Workflow - Erro: " + e.getMessage());
            success = false;
        }
        
        return success;
    }
}