package model;

import java.sql.Timestamp;

/**
 * Classe modelo para representar um usuário no cliente
 */
public class Usuario {
    public static final String TIPO_ADMIN = "admin";
    public static final String TIPO_USUARIO = "usuario";
    
    private int id;
    private String nome;
    private String email;
    private String tipoUsuario;
    private Timestamp dataCriacao;
    
    // Constructors
    public Usuario() {}
    
    public Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }
    
    public Usuario(int id, String nome, String email, String tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }
    
    public Usuario(int id, String nome, String email, Timestamp dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;
    }
    
    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Timestamp getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Timestamp dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public boolean isAdmin() { return TIPO_ADMIN.equals(tipoUsuario); }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}