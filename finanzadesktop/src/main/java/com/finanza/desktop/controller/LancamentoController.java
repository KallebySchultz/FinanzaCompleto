package com.finanza.desktop.controller;

import com.finanza.desktop.model.Lancamento;
import com.finanza.desktop.database.LancamentoDao;
import java.util.List;
import java.util.Date;

/**
 * Controller para operações de lançamentos financeiros
 * Segue padrões similares aos Controllers do Android
 */
public class LancamentoController {
    private final LancamentoDao lancamentoDao;
    
    public LancamentoController() {
        this.lancamentoDao = new LancamentoDao();
    }
    
    /**
     * Cria um novo lançamento
     * @param valor Valor do lançamento (positivo para receita, negativo para despesa)
     * @param descricao Descrição do lançamento
     * @param contaId ID da conta
     * @param categoriaId ID da categoria (pode ser null)
     * @param usuarioId ID do usuário
     * @param tipo Tipo do lançamento ("receita" ou "despesa")
     * @return ID do lançamento criado
     */
    public int criarLancamento(double valor, String descricao, int contaId, 
                              Integer categoriaId, int usuarioId, String tipo) {
        
        // Validações
        if (valor == 0) {
            throw new IllegalArgumentException("Valor não pode ser zero");
        }
        
        if (contaId <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser válido");
        }
        
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        if (tipo == null || (!tipo.equals("receita") && !tipo.equals("despesa"))) {
            throw new IllegalArgumentException("Tipo deve ser 'receita' ou 'despesa'");
        }
        
        // Validar coerência entre tipo e valor
        if (tipo.equals("receita") && valor < 0) {
            throw new IllegalArgumentException("Receitas devem ter valor positivo");
        }
        
        if (tipo.equals("despesa") && valor > 0) {
            throw new IllegalArgumentException("Despesas devem ter valor negativo");
        }
        
        Lancamento lancamento = new Lancamento();
        lancamento.setValor(valor);
        lancamento.setDescricao(descricao != null ? descricao.trim() : "");
        lancamento.setData(System.currentTimeMillis());
        lancamento.setContaId(contaId);
        lancamento.setCategoriaId(categoriaId);
        lancamento.setUsuarioId(usuarioId);
        lancamento.setTipo(tipo);
        
        return lancamentoDao.inserir(lancamento);
    }
    
    /**
     * Lista lançamentos de um usuário
     * @param usuarioId ID do usuário
     * @return Lista de lançamentos
     */
    public List<Lancamento> listarLancamentosPorUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        return lancamentoDao.listarPorUsuario(usuarioId);
    }
    
    /**
     * Lista lançamentos de uma conta
     * @param contaId ID da conta
     * @return Lista de lançamentos
     */
    public List<Lancamento> listarLancamentosPorConta(int contaId) {
        if (contaId <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser válido");
        }
        
        return lancamentoDao.listarPorConta(contaId);
    }
    
    /**
     * Lista últimos lançamentos de um usuário
     * @param usuarioId ID do usuário
     * @param limite Número máximo de lançamentos
     * @return Lista dos últimos lançamentos
     */
    public List<Lancamento> listarUltimosLancamentos(int usuarioId, int limite) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        if (limite <= 0) {
            limite = 10; // padrão
        }
        
        return lancamentoDao.listarUltimosPorUsuario(usuarioId, limite);
    }
    
    /**
     * Busca lançamento por ID
     * @param id ID do lançamento
     * @return Lançamento encontrado ou null
     */
    public Lancamento buscarLancamentoPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do lançamento deve ser válido");
        }
        
        return lancamentoDao.buscarPorId(id);
    }
    
    /**
     * Atualiza um lançamento
     * @param lancamento Lançamento com dados atualizados
     * @return true se atualizado com sucesso
     */
    public boolean atualizarLancamento(Lancamento lancamento) {
        if (lancamento == null) {
            throw new IllegalArgumentException("Lançamento não pode ser nulo");
        }
        
        if (lancamento.getId() <= 0) {
            throw new IllegalArgumentException("ID do lançamento deve ser válido");
        }
        
        if (lancamento.getValor() == 0) {
            throw new IllegalArgumentException("Valor não pode ser zero");
        }
        
        if (lancamento.getTipo() == null || 
            (!lancamento.getTipo().equals("receita") && !lancamento.getTipo().equals("despesa"))) {
            throw new IllegalArgumentException("Tipo deve ser 'receita' ou 'despesa'");
        }
        
        return lancamentoDao.atualizar(lancamento);
    }
    
    /**
     * Remove um lançamento
     * @param id ID do lançamento
     * @return true se removido com sucesso
     */
    public boolean removerLancamento(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do lançamento deve ser válido");
        }
        
        return lancamentoDao.deletar(id);
    }
    
    /**
     * Calcula total de receitas de um usuário
     * @param usuarioId ID do usuário
     * @return Total de receitas
     */
    public double calcularTotalReceitas(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        Double total = lancamentoDao.somaPorTipo("receita", usuarioId);
        return total != null ? total : 0.0;
    }
    
    /**
     * Calcula total de despesas de um usuário
     * @param usuarioId ID do usuário
     * @return Total de despesas (valor absoluto)
     */
    public double calcularTotalDespesas(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        Double total = lancamentoDao.somaPorTipo("despesa", usuarioId);
        return total != null ? Math.abs(total) : 0.0;
    }
    
    /**
     * Calcula saldo líquido de um usuário (receitas - despesas)
     * @param usuarioId ID do usuário
     * @return Saldo líquido
     */
    public double calcularSaldoLiquido(int usuarioId) {
        double receitas = calcularTotalReceitas(usuarioId);
        double despesas = calcularTotalDespesas(usuarioId);
        return receitas - despesas;
    }
    
    /**
     * Busca lançamentos por período
     * @param usuarioId ID do usuário
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @return Lista de lançamentos no período
     */
    public List<Lancamento> buscarPorPeriodo(int usuarioId, Date dataInicio, Date dataFim) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas de início e fim não podem ser nulas");
        }
        
        if (dataInicio.after(dataFim)) {
            throw new IllegalArgumentException("Data de início deve ser anterior à data de fim");
        }
        
        return lancamentoDao.listarPorUsuarioPeriodo(usuarioId, 
                dataInicio.getTime(), dataFim.getTime());
    }
}