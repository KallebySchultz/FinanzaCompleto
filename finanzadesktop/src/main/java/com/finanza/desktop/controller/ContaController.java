package com.finanza.desktop.controller;

import com.finanza.desktop.model.Conta;
import com.finanza.desktop.database.ContaDao;
import java.util.List;

/**
 * Controller para operações de conta
 * Segue padrões similares aos Controllers do Android
 */
public class ContaController {
    private final ContaDao contaDao;
    
    public ContaController() {
        this.contaDao = new ContaDao();
    }
    
    /**
     * Cria uma nova conta
     * @param nome Nome da conta
     * @param saldoInicial Saldo inicial da conta
     * @param usuarioId ID do usuário proprietário
     * @return ID da conta criada
     */
    public int criarConta(String nome, double saldoInicial, int usuarioId) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da conta não pode ser vazio");
        }
        
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        Conta novaConta = new Conta();
        novaConta.setNome(nome.trim());
        novaConta.setSaldoInicial(saldoInicial);
        novaConta.setUsuarioId(usuarioId);
        
        return contaDao.inserir(novaConta);
    }
    
    /**
     * Lista todas as contas de um usuário
     * @param usuarioId ID do usuário
     * @return Lista de contas
     */
    public List<Conta> listarContasPorUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        return contaDao.listarPorUsuario(usuarioId);
    }
    
    /**
     * Busca uma conta por ID
     * @param id ID da conta
     * @return Conta encontrada ou null
     */
    public Conta buscarContaPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser válido");
        }
        
        return contaDao.buscarPorId(id);
    }
    
    /**
     * Atualiza uma conta
     * @param conta Conta com dados atualizados
     * @return true se atualizado com sucesso
     */
    public boolean atualizarConta(Conta conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta não pode ser nula");
        }
        
        if (conta.getId() <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser válido");
        }
        
        if (conta.getNome() == null || conta.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da conta não pode ser vazio");
        }
        
        return contaDao.atualizar(conta);
    }
    
    /**
     * Remove uma conta
     * @param id ID da conta a ser removida
     * @return true se removido com sucesso
     */
    public boolean removerConta(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser válido");
        }
        
        return contaDao.deletar(id);
    }
    
    /**
     * Calcula o saldo atual de uma conta
     * @param contaId ID da conta
     * @return Saldo atual
     */
    public double calcularSaldoAtual(int contaId) {
        if (contaId <= 0) {
            throw new IllegalArgumentException("ID da conta deve ser válido");
        }
        
        return contaDao.calcularSaldoAtual(contaId);
    }
    
    /**
     * Valida se uma conta pode ser removida
     * Aqui podemos adicionar lógica de negócio como:
     * - Verificar se há lançamentos
     * - Verificar se é a última conta do usuário
     * @param contaId ID da conta
     * @return true se pode ser removida
     */
    public boolean podeRemoverConta(int contaId) {
        if (contaId <= 0) {
            return false;
        }
        
        Conta conta = contaDao.buscarPorId(contaId);
        if (conta == null) {
            return false;
        }
        
        // Verificar se não é a única conta do usuário
        List<Conta> contas = contaDao.listarPorUsuario(conta.getUsuarioId());
        return contas.size() > 1;
    }
    
    /**
     * Calcula o patrimônio total de um usuário
     * @param usuarioId ID do usuário
     * @return Patrimônio total
     */
    public double calcularPatrimonioTotal(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser válido");
        }
        
        List<Conta> contas = contaDao.listarPorUsuario(usuarioId);
        double total = 0.0;
        
        for (Conta conta : contas) {
            total += calcularSaldoAtual(conta.getId());
        }
        
        return total;
    }
}