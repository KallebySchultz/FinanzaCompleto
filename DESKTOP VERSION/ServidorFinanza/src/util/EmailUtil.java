package util;

/**
 * Utilitário para envio de emails (versão simplificada para desenvolvimento)
 */
public class EmailUtil {
    
    /**
     * Envia email de recuperação de senha
     * Para desenvolvimento, apenas simula o envio do email
     * @param destinatario email do destinatário
     * @param novaSenha nova senha temporária
     * @return true se enviado com sucesso
     */
    public static boolean enviarRecuperacaoSenha(String destinatario, String novaSenha) {
        try {
            // Para desenvolvimento/demo, apenas simular o envio
            System.out.println("=== SIMULAÇÃO DE EMAIL ===");
            System.out.println("Para: " + destinatario);
            System.out.println("Assunto: Finanza - Recuperação de Senha");
            System.out.println("Conteúdo:");
            System.out.println("Olá,");
            System.out.println("Você solicitou a recuperação de senha para sua conta no Finanza Desktop.");
            System.out.println("Sua nova senha temporária é: " + novaSenha);
            System.out.println("Por favor, faça login com esta senha e altere-a imediatamente através do menu Perfil.");
            System.out.println("Caso você não tenha solicitado esta recuperação, ignore este email.");
            System.out.println("Atenciosamente,");
            System.out.println("Equipe Finanza");
            System.out.println("=========================");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de recuperação: " + e.getMessage());
            return false;
        }
    }
    
    // TODO: Para produção, implementar envio real usando JavaMail API
    // Dependências necessárias: javax.mail:mail:1.4+ ou javax.mail:javax.mail-api:1.6+
}