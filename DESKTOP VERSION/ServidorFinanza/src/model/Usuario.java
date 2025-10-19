package model;

import java.sql.Timestamp;

/**
 * Classe modelo para representar um usuário do sistema
 */
public class Usuario {
    public static final String TIPO_ADMIN = "admin";
    public static final String TIPO_USUARIO = "usuario";
    
    private int id;
    private String nome;
    private String email;
    private String senhaHash;
    private String tipoUsuario; // 'admin' ou 'usuario'
    private Timestamp dataCriacao;
    private Timestamp dataAtualizacao;
    
    // Constructors
    public Usuario() {
        this.tipoUsuario = TIPO_USUARIO; // padrão é usuário comum
    }
    
    public Usuario(String nome, String email, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipoUsuario = TIPO_USUARIO; // padrão é usuário comum
    }
    
    public Usuario(int id, String nome, String email, String senhaHash) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipoUsuario = TIPO_USUARIO; // padrão é usuário comum
    }
    
    public Usuario(int id, String nome, String email, String senhaHash, String tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipoUsuario = tipoUsuario;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    
    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public Timestamp getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Timestamp dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public boolean isAdmin() { return TIPO_ADMIN.equals(tipoUsuario); }
    public boolean isUsuario() { return TIPO_USUARIO.equals(tipoUsuario); }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                '}';
    }
}