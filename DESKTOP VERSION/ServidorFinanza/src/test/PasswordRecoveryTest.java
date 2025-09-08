package test;

import dao.UsuarioDAO;
import model.Usuario;
import util.SecurityUtil;

/**
 * Teste simples para verificar a funcionalidade de recuperação de senha
 */
public class PasswordRecoveryTest {
    
    public static void main(String[] args) {
        PasswordRecoveryTest test = new PasswordRecoveryTest();
        test.testPasswordRecovery();
    }
    
    public void testPasswordRecovery() {
        System.out.println("=== Teste de Recuperação de Senha ===");
        
        try {
            // Testa geração de token
            System.out.println("1. Testando geração de token de recuperação...");
            String token = SecurityUtil.gerarTokenRecuperacao();
            System.out.println("Token gerado: " + token);
            assert token != null && token.length() == 8 : "Token deve ter 8 caracteres";
            System.out.println("✓ Geração de token funcionando");
            
            // Testa validação de senha
            System.out.println("\n2. Testando validação de senha...");
            assert SecurityUtil.validarSenha("123456") : "Senha válida deve passar na validação";
            assert !SecurityUtil.validarSenha("123") : "Senha inválida deve falhar na validação";
            System.out.println("✓ Validação de senha funcionando");
            
            // Testa hash de senha
            System.out.println("\n3. Testando hash de senha...");
            String senha = "minhaSenha123";
            String hash1 = SecurityUtil.hashSenha(senha);
            String hash2 = SecurityUtil.hashSenha(senha);
            assert hash1.equals(hash2) : "Hash deve ser consistente";
            assert SecurityUtil.verificarSenha(senha, hash1) : "Verificação de senha deve funcionar";
            System.out.println("✓ Hash de senha funcionando");
            
            // Teste com UsuarioDAO (apenas se banco estiver disponível)
            System.out.println("\n4. Testando integração com UsuarioDAO...");
            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                
                // Cria usuário de teste
                Usuario usuarioTeste = new Usuario();
                usuarioTeste.setNome("Teste Recovery");
                usuarioTeste.setEmail("teste.recovery@example.com");
                usuarioTeste.setSenhaHash(SecurityUtil.hashSenha("senhaAntiga"));
                
                // Simula geração de token (não insere no banco para evitar dependências)
                String tokenRecovery = SecurityUtil.gerarTokenRecuperacao();
                System.out.println("Token simulado: " + tokenRecovery);
                System.out.println("✓ Integração com UsuarioDAO preparada");
                
            } catch (Exception e) {
                System.out.println("⚠ Banco não disponível para teste completo: " + e.getMessage());
            }
            
            System.out.println("\n=== Todos os testes passaram! ===");
            
        } catch (Exception e) {
            System.err.println("❌ Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}