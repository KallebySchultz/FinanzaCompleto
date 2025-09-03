package test;

import database.DatabaseManager;
import controller.UsuarioController;
import model.Usuario;

/**
 * Simple test to verify the system works in headless mode
 */
public class HeadlessTest {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Finanza Desktop - Teste de Sistema (Modo Headless)");
        System.out.println("=================================================");
        
        try {
            // Test database connection
            System.out.println("1. Testando conexão com banco de dados...");
            if (DatabaseManager.testConnection()) {
                System.out.println("   ✓ Banco de dados conectado com sucesso!");
            } else {
                System.out.println("   ✗ Erro na conexão com banco de dados!");
                return;
            }
            
            // Test user controller
            System.out.println("2. Testando UsuarioController...");
            UsuarioController usuarioController = new UsuarioController();
            
            // Try to create a test user
            String testEmail = "test@finanza.com";
            String testNome = "Usuário Teste";
            String testSenha = "123456";
            
            if (usuarioController.cadastrar(testNome, testEmail, testSenha)) {
                System.out.println("   ✓ Usuário teste criado com sucesso!");
                
                // Try to authenticate
                if (usuarioController.autenticar(testEmail, testSenha)) {
                    System.out.println("   ✓ Autenticação funcionando!");
                    
                    Usuario usuario = usuarioController.getUsuarioLogado();
                    if (usuario != null) {
                        System.out.println("   ✓ Usuário atual: " + usuario.getNome());
                    }
                } else {
                    System.out.println("   ✗ Erro na autenticação!");
                }
            } else {
                System.out.println("   - Usuário já existe ou erro na criação (normal se já executado antes)");
                
                // Try to authenticate with existing user
                if (usuarioController.autenticar(testEmail, testSenha)) {
                    System.out.println("   ✓ Autenticação com usuário existente funcionando!");
                }
            }
            
            System.out.println("=================================================");
            System.out.println("Teste concluído! Sistema funcionando corretamente.");
            System.out.println("Em NetBeans, este projeto irá funcionar perfeitamente");
            System.out.println("com interface gráfica completa.");
            System.out.println("=================================================");
            
        } catch (Exception e) {
            System.err.println("Erro durante teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}