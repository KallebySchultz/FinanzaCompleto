package model;

/**
 * Model class for Usuario (User)
 * Represents a user in the Finanza system
 */
public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private long dataCriacao;
    
    // Constructors
    public Usuario() {}
    
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCriacao = System.currentTimeMillis();
    }
    
    public Usuario(int id, String nome, String email, String senha, long dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCriacao = dataCriacao;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public long getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}