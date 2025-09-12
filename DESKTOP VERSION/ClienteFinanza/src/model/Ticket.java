package model;

import java.time.LocalDateTime;

/**
 * Classe modelo para representar um ticket/chamado
 */
public class Ticket {
    public enum StatusTicket {
        ABERTO, EM_ANDAMENTO, RESOLVIDO, FECHADO, CANCELADO
    }
    
    private int id;
    private String titulo;
    private String descricao;
    private StatusTicket status;
    private int prioridade; // 1=Baixa, 2=Media, 3=Alta, 4=Crítica
    private int departamentoId;
    private int usuarioId; // Usuário que criou o ticket
    private int agenteId; // Agente responsável (pode ser 0 se não atribuído)
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private String observacoes;
    
    // Constructors
    public Ticket() {
        this.status = StatusTicket.ABERTO;
        this.dataAbertura = LocalDateTime.now();
        this.prioridade = 2; // Média por padrão
    }
    
    public Ticket(String titulo, String descricao, int prioridade, int departamentoId, int usuarioId) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.departamentoId = departamentoId;
        this.usuarioId = usuarioId;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public StatusTicket getStatus() { return status; }
    public void setStatus(StatusTicket status) { this.status = status; }
    
    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }
    
    public String getPrioridadeString() {
        switch(prioridade) {
            case 1: return "Baixa";
            case 2: return "Média";
            case 3: return "Alta";
            case 4: return "Crítica";
            default: return "Indefinida";
        }
    }
    
    public int getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(int departamentoId) { this.departamentoId = departamentoId; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getAgenteId() { return agenteId; }
    public void setAgenteId(int agenteId) { this.agenteId = agenteId; }
    
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", status=" + status +
                ", prioridade=" + getPrioridadeString() +
                ", usuarioId=" + usuarioId +
                ", agenteId=" + agenteId +
                '}';
    }
}