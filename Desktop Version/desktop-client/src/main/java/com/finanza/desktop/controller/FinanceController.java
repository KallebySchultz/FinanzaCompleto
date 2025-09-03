package com.finanza.desktop.controller;

import com.finanza.desktop.dao.ContaDAO;
import com.finanza.desktop.dao.LancamentoDAO;
import com.finanza.desktop.dao.CategoriaDAO;
import com.finanza.desktop.model.Conta;
import com.finanza.desktop.model.Lancamento;
import com.finanza.desktop.model.Categoria;
import com.finanza.desktop.model.Usuario;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

/**
 * Controller para gerenciar dados financeiros
 */
public class FinanceController {
    private ContaDAO contaDAO;
    private LancamentoDAO lancamentoDAO;
    private CategoriaDAO categoriaDAO;
    private AuthController authController;

    public FinanceController(AuthController authController) {
        this.contaDAO = new ContaDAO();
        this.lancamentoDAO = new LancamentoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.authController = authController;
    }

    // ==================== OPERAÇÕES COM CONTAS ====================

    /**
     * Cria uma nova conta para o usuário logado
     */
    public boolean criarConta(String nome, double saldoInicial) {
        Usuario usuario = authController.getUsuarioLogado();
        if (usuario == null) {
            return false;
        }

        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        Conta conta = new Conta(nome.trim(), saldoInicial, usuario.getId());
        return contaDAO.criar(conta);
    }

    /**
     * Lista todas as contas do usuário logado
     */
    public List<Conta> listarContas() {
        Usuario usuario = authController.getUsuarioLogado();
        if (usuario == null) {
            return null;
        }
        return contaDAO.listarPorUsuario(usuario.getId());
    }

    /**
     * Calcula o saldo atual de uma conta
     */
    public double calcularSaldoConta(int contaId) {
        return contaDAO.calcularSaldoAtual(contaId);
    }

    /**
     * Atualiza uma conta
     */
    public boolean atualizarConta(Conta conta) {
        return contaDAO.atualizar(conta);
    }

    /**
     * Remove uma conta
     */
    public boolean removerConta(int contaId) {
        return contaDAO.remover(contaId);
    }

    // ==================== OPERAÇÕES COM LANÇAMENTOS ====================

    /**
     * Cria um novo lançamento
     */
    public boolean criarLancamento(double valor, String descricao, int contaId, int categoriaId, String tipo) {
        Usuario usuario = authController.getUsuarioLogado();
        if (usuario == null) {
            return false;
        }

        if (valor <= 0 || descricao == null || descricao.trim().isEmpty()) {
            return false;
        }

        if (!tipo.equals("receita") && !tipo.equals("despesa")) {
            return false;
        }

        Lancamento lancamento = new Lancamento(valor, descricao.trim(), contaId, categoriaId, usuario.getId(), tipo);
        return lancamentoDAO.criar(lancamento);
    }

    /**
     * Lista todos os lançamentos do usuário logado
     */
    public List<Lancamento> listarLancamentos() {
        Usuario usuario = authController.getUsuarioLogado();
        if (usuario == null) {
            return null;
        }
        return lancamentoDAO.listarPorUsuario(usuario.getId());
    }

    /**
     * Lista lançamentos por conta
     */
    public List<Lancamento> listarLancamentosPorConta(int contaId) {
        return lancamentoDAO.listarPorConta(contaId);
    }

    /**
     * Atualiza um lançamento
     */
    public boolean atualizarLancamento(Lancamento lancamento) {
        return lancamentoDAO.atualizar(lancamento);
    }

    /**
     * Remove um lançamento
     */
    public boolean removerLancamento(int lancamentoId) {
        return lancamentoDAO.remover(lancamentoId);
    }

    // ==================== OPERAÇÕES COM CATEGORIAS ====================

    /**
     * Lista todas as categorias
     */
    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarTodas();
    }

    /**
     * Lista categorias por tipo
     */
    public List<Categoria> listarCategoriasPorTipo(String tipo) {
        return categoriaDAO.listarPorTipo(tipo);
    }

    /**
     * Cria uma nova categoria
     */
    public boolean criarCategoria(String nome, String corHex, String tipo) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        if (!tipo.equals("receita") && !tipo.equals("despesa")) {
            return false;
        }

        Categoria categoria = new Categoria(nome.trim(), corHex, tipo);
        return categoriaDAO.criar(categoria);
    }

    // ==================== RELATÓRIOS E ESTATÍSTICAS ====================

    /**
     * Calcula o resumo financeiro do usuário
     */
    public Map<String, Double> calcularResumoFinanceiro() {
        Usuario usuario = authController.getUsuarioLogado();
        if (usuario == null) {
            return null;
        }

        Map<String, Double> resumo = new HashMap<>();
        
        double totalReceitas = lancamentoDAO.calcularTotalReceitas(usuario.getId());
        double totalDespesas = lancamentoDAO.calcularTotalDespesas(usuario.getId());
        double saldo = totalReceitas - totalDespesas;

        resumo.put("receitas", totalReceitas);
        resumo.put("despesas", totalDespesas);
        resumo.put("saldo", saldo);

        return resumo;
    }

    /**
     * Calcula o saldo total de todas as contas
     */
    public double calcularSaldoTotal() {
        List<Conta> contas = listarContas();
        if (contas == null) {
            return 0.0;
        }

        double saldoTotal = 0.0;
        for (Conta conta : contas) {
            saldoTotal += calcularSaldoConta(conta.getId());
        }

        return saldoTotal;
    }

    /**
     * Obtém lançamentos do mês atual
     */
    public List<Lancamento> obterLancamentosMesAtual() {
        Usuario usuario = authController.getUsuarioLogado();
        if (usuario == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioMes = cal.getTimeInMillis();

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        long fimMes = cal.getTimeInMillis();

        return lancamentoDAO.listarPorPeriodo(usuario.getId(), inicioMes, fimMes);
    }
}