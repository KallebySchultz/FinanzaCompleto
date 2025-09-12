package model;

/**
 * Classe modelo para representar um usuário no cliente
 */
public class Usuario {
    public enum TipoUsuario {
        ADMIN, USUARIO, AGENTE
    }
    
    private int id;
    private String nome;
    private String email;
    private TipoUsuario tipo;
    
    // Constructors
    public Usuario() {
        this.tipo = TipoUsuario.USUARIO; // Default
    }
    
    public Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = TipoUsuario.USUARIO; // Default
    }
    
    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.tipo = TipoUsuario.USUARIO; // Default
    }
    
    public Usuario(int id, String nome, String email, TipoUsuario tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    
    public boolean isAdmin() { return tipo == TipoUsuario.ADMIN; }
    public boolean isAgente() { return tipo == TipoUsuario.AGENTE; }
    public boolean isUsuario() { return tipo == TipoUsuario.USUARIO; }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipo=" + tipo +
                '}';
    }
}