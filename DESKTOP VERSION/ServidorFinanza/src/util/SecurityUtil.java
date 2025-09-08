package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Classe utilitária para operações de segurança e criptografia
 */
public class SecurityUtil {
    
    /**
     * Gera hash SHA-256 de uma senha
     * @param senha senha original
     * @return hash da senha
     */
    public static String hashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
    
    /**
     * Verifica se uma senha corresponde ao hash
     * @param senha senha a verificar
     * @param hash hash armazenado
     * @return true se a senha estiver correta
     */
    public static boolean verificarSenha(String senha, String hash) {
        String senhaHash = hashSenha(senha);
        return senhaHash.equals(hash);
    }
    
    /**
     * Valida formato de email
     * @param email email a validar
     * @return true se o formato estiver correto
     */
    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Valida força da senha
     * @param senha senha a validar
     * @return true se a senha for forte o suficiente
     */
    public static boolean validarSenha(String senha) {
        return senha != null && senha.length() >= 6;
    }
    
    /**
     * Gera uma senha temporária aleatória
     * @return senha temporária de 8 caracteres
     */
    public static String gerarSenhaTemporaria() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(chars.length());
            senha.append(chars.charAt(index));
        }
        
        return senha.toString();
    }
}