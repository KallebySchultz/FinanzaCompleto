package controller;

import model.Conta;
import model.Categoria;
import model.Movimentacao;
import model.Usuario;
import util.NetworkClient;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller para operações financeiras
 */
public class FinanceController {
    private NetworkClient networkClient;
    
    // Constantes do protocolo
    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ERROR";
    private static final String STATUS_INVALID_DATA = "INVALID_DATA";
    private static final String SEPARATOR = "|";
    private static final String FIELD_SEPARATOR = ";";
    
    // Comandos de Dashboard
    private static final String CMD_GET_DASHBOARD = "GET_DASHBOARD";
    
    // Comandos de Conta
    private static final String CMD_LIST_CONTAS = "LIST_CONTAS";
    private static final String CMD_ADD_CONTA = "ADD_CONTA";
    private static final String CMD_UPDATE_CONTA = "UPDATE_CONTA";
    private static final String CMD_DELETE_CONTA = "DELETE_CONTA";
    
    // Comandos de Categoria
    private static final String CMD_LIST_CATEGORIAS = "LIST_CATEGORIAS";
    private static final String CMD_LIST_CATEGORIAS_TIPO = "LIST_CATEGORIAS_TIPO";
    private static final String CMD_ADD_CATEGORIA = "ADD_CATEGORIA";
    private static final String CMD_UPDATE_CATEGORIA = "UPDATE_CATEGORIA";
    private static final String CMD_DELETE_CATEGORIA = "DELETE_CATEGORIA";
    
    // Comandos de Movimentação
    private static final String CMD_LIST_MOVIMENTACOES = "LIST_MOVIMENTACOES";
    private static final String CMD_LIST_MOVIMENTACOES_PERIODO = "LIST_MOVIMENTACOES_PERIODO";
    private static final String CMD_LIST_MOVIMENTACOES_CONTA = "LIST_MOVIMENTACOES_CONTA";
    private static final String CMD_ADD_MOVIMENTACAO = "ADD_MOVIMENTACAO";
    private static final String CMD_UPDATE_MOVIMENTACAO = "UPDATE_MOVIMENTACAO";
    private static final String CMD_DELETE_MOVIMENTACAO = "DELETE_MOVIMENTACAO";
    
    // Comandos de Perfil
    private static final String CMD_GET_PERFIL = "GET_PERFIL";
    private static final String CMD_UPDATE_PERFIL = "UPDATE_PERFIL";
    private static final String CMD_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    
    public FinanceController(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }
    
    /**
     * Utilitário para converter string com vírgula para double
     * Converte formato brasileiro (0,00) para formato americano (0.00) antes do parsing
     */
    private double parsePortugueseDouble(String valor) throws NumberFormatException {
        if (valor == null || valor.trim().isEmpty()) {
            return 0.0;
        }
        // Substitui vírgula por ponto para parsing correto
        return Double.parseDouble(valor.replace(",", "."));
    }
    
    // ========== MÉTODOS DE DASHBOARD ==========
    
    public DashboardData getDashboard() {
        if (!networkClient.isConnected()) {
            return new DashboardData(false, "Não conectado ao servidor", 0.0, 0.0, 0.0, 0);
        }
        
        String resposta = networkClient.sendCommand(CMD_GET_DASHBOARD);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            if (partes.length >= 2) {
                String[] dados = partes[1].split(FIELD_SEPARATOR);
                if (dados.length >= 4) {
                    try {
                        double saldoTotal = parsePortugueseDouble(dados[0]);
                        double receitasMes = parsePortugueseDouble(dados[1]);
                        double despesasMes = parsePortugueseDouble(dados[2]);
                        int numTransacoes = Integer.parseInt(dados[3]);
                        
                        return new DashboardData(true, "Dados carregados", saldoTotal, receitasMes, despesasMes, numTransacoes);
                    } catch (NumberFormatException e) {
                        return new DashboardData(false, "Erro ao processar dados", 0.0, 0.0, 0.0, 0);
                    }
                }
            }
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new DashboardData(false, erro, 0.0, 0.0, 0.0, 0);
    }
    
    // ========== MÉTODOS DE CONTA ==========
    
    public OperationResult<List<Conta>> listarContas() {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String resposta = networkClient.sendCommand(CMD_LIST_CONTAS);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            List<Conta> contas = new ArrayList<>();
            
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] contasData = partes[1].split(FIELD_SEPARATOR);
                
                for (String contaStr : contasData) {
                    try {
                        // Parse conta data more carefully to handle Brazilian decimal format
                        // Expected format: id,nome,tipo,saldo_inicial_inteiro,saldo_inicial_decimal,saldo_atual_inteiro,saldo_atual_decimal
                        // Example: 1,nubank,corrente,0,00,2721,00
                        
                        String[] campos = contaStr.split(",");
                        if (campos.length >= 5) {
                            int id = Integer.parseInt(campos[0]);
                            String nome = campos[1];
                            String tipoStr = campos[2];
                            
                            // Handle potential decimal splits
                            String saldoInicialStr;
                            String saldoAtualStr;
                            
                            if (campos.length >= 7) {
                                // Both values might be split: id,nome,tipo,inicial_int,inicial_dec,atual_int,atual_dec
                                saldoInicialStr = campos[3] + "," + campos[4];
                                saldoAtualStr = campos[5] + "," + campos[6];
                            } else if (campos.length >= 6) {
                                // One value might be split
                                saldoInicialStr = campos[3] + "," + campos[4];
                                saldoAtualStr = campos[5];
                            } else {
                                // No splits
                                saldoInicialStr = campos[3];
                                saldoAtualStr = campos[4];
                            }
                            
                            Conta.TipoConta tipo = Conta.TipoConta.fromString(tipoStr);
                            double saldoInicial = parsePortugueseDouble(saldoInicialStr);
                            double saldoAtual = parsePortugueseDouble(saldoAtualStr);
                            
                            Conta conta = new Conta(id, nome, tipo, saldoInicial, saldoAtual);
                            contas.add(conta);
                        }
                    } catch (Exception e) {
                        // Log the error for debugging but continue processing other records
                        System.err.println("Erro ao processar conta: " + contaStr + " - " + e.getMessage());
                    }
                }
            }
            
            return new OperationResult<>(true, "Contas carregadas", contas);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Integer> adicionarConta(String nome, Conta.TipoConta tipo, double saldoInicial) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_ADD_CONTA + SEPARATOR + nome + SEPARATOR + tipo.getValor() + SEPARATOR + saldoInicial;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            if (partes.length >= 2) {
                try {
                    int id = Integer.parseInt(partes[1]);
                    return new OperationResult<>(true, "Conta criada com sucesso", id);
                } catch (NumberFormatException e) {
                    return new OperationResult<>(false, "Erro ao processar resposta", null);
                }
            }
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> atualizarConta(int id, String nome, Conta.TipoConta tipo, double saldoInicial) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_UPDATE_CONTA + SEPARATOR + id + SEPARATOR + nome + SEPARATOR + tipo.getValor() + SEPARATOR + saldoInicial;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Conta atualizada com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> removerConta(int id) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_DELETE_CONTA + SEPARATOR + id;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Conta removida com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    // ========== MÉTODOS DE CATEGORIA ==========
    
    public OperationResult<List<Categoria>> listarCategorias() {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String resposta = networkClient.sendCommand(CMD_LIST_CATEGORIAS);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            List<Categoria> categorias = new ArrayList<>();
            
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] categoriasData = partes[1].split(FIELD_SEPARATOR);
                
                for (String categoriaStr : categoriasData) {
                    String[] campos = categoriaStr.split(",");
                    if (campos.length >= 3) {
                        try {
                            int id = Integer.parseInt(campos[0]);
                            String nome = campos[1];
                            Categoria.TipoCategoria tipo = Categoria.TipoCategoria.fromString(campos[2]);
                            
                            Categoria categoria = new Categoria(id, nome, tipo);
                            categorias.add(categoria);
                        } catch (Exception e) {
                            // Ignorar dados inválidos
                        }
                    }
                }
            }
            
            return new OperationResult<>(true, "Categorias carregadas", categorias);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<List<Categoria>> listarCategoriasPorTipo(Categoria.TipoCategoria tipo) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_LIST_CATEGORIAS_TIPO + SEPARATOR + tipo.getValor();
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            List<Categoria> categorias = new ArrayList<>();
            
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] categoriasData = partes[1].split(FIELD_SEPARATOR);
                
                for (String categoriaStr : categoriasData) {
                    String[] campos = categoriaStr.split(",");
                    if (campos.length >= 3) {
                        try {
                            int id = Integer.parseInt(campos[0]);
                            String nome = campos[1];
                            Categoria.TipoCategoria tipoCategoria = Categoria.TipoCategoria.fromString(campos[2]);
                            
                            Categoria categoria = new Categoria(id, nome, tipoCategoria);
                            categorias.add(categoria);
                        } catch (Exception e) {
                            // Ignorar dados inválidos
                        }
                    }
                }
            }
            
            return new OperationResult<>(true, "Categorias carregadas", categorias);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Integer> adicionarCategoria(String nome, Categoria.TipoCategoria tipo) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_ADD_CATEGORIA + SEPARATOR + nome + SEPARATOR + tipo.getValor();
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            if (partes.length >= 2) {
                try {
                    int id = Integer.parseInt(partes[1]);
                    return new OperationResult<>(true, "Categoria criada com sucesso", id);
                } catch (NumberFormatException e) {
                    return new OperationResult<>(false, "Erro ao processar resposta", null);
                }
            }
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> atualizarCategoria(int id, String nome, Categoria.TipoCategoria tipo) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_UPDATE_CATEGORIA + SEPARATOR + id + SEPARATOR + nome + SEPARATOR + tipo.getValor();
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Categoria atualizada com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> removerCategoria(int id) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_DELETE_CATEGORIA + SEPARATOR + id;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Categoria removida com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    // ========== MÉTODOS DE MOVIMENTAÇÃO ==========
    
    public OperationResult<List<Movimentacao>> listarMovimentacoes() {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String resposta = networkClient.sendCommand(CMD_LIST_MOVIMENTACOES);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            List<Movimentacao> movimentacoes = new ArrayList<>();
            
            if (partes.length >= 2 && !partes[1].trim().isEmpty()) {
                String[] movimentacoesData = partes[1].split(FIELD_SEPARATOR);
                
                for (String movStr : movimentacoesData) {
                    try {
                        // Parse movimentacao data to handle Brazilian decimal format
                        // Expected format: id,valor_inteiro,valor_decimal,data,descricao,tipo,idConta,idCategoria
                        // Example: 3,10,00,2025-09-06,aa,receita,1,11
                        
                        String[] campos = movStr.split(",");
                        if (campos.length >= 8) {
                            // Parse fields according to the expected format
                            int id = Integer.parseInt(campos[0]);
                            
                            // Reconstruct valor from Brazilian decimal format: valor_inteiro,valor_decimal
                            String valorStr = campos[1] + "," + campos[2]; // Reconstruct Brazilian decimal
                            double valor = parsePortugueseDouble(valorStr);
                            
                            Date data = Date.valueOf(campos[3]);
                            String descricao = campos[4];
                            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.fromString(campos[5]);
                            
                            // Skip this record if tipo is null (invalid type)
                            if (tipo == null) {
                                System.err.println("Tipo de movimentação inválido, ignorando registro: " + movStr);
                                continue;
                            }
                            
                            int idConta = Integer.parseInt(campos[6]);
                            int idCategoria = Integer.parseInt(campos[7]);
                            
                            Movimentacao movimentacao = new Movimentacao(id, valor, data, descricao, tipo, idConta, idCategoria);
                            movimentacoes.add(movimentacao);
                        }
                    } catch (Exception e) {
                        // Log the error for debugging but continue processing other records
                        System.err.println("Erro ao processar movimentação: " + movStr + " - " + e.getMessage());
                        e.printStackTrace(); // Add stack trace for better debugging
                    }
                }
            }
            
            // Carregar nomes das categorias para exibição
            carregarNomesCategorias(movimentacoes);
            
            return new OperationResult<>(true, "Movimentações carregadas", movimentacoes);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Integer> adicionarMovimentacao(double valor, Date data, String descricao, 
                                                         Movimentacao.TipoMovimentacao tipo, int idConta, int idCategoria) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_ADD_MOVIMENTACAO + SEPARATOR + valor + SEPARATOR + data.toString() + SEPARATOR + 
                        descricao + SEPARATOR + tipo.getValor() + SEPARATOR + idConta + SEPARATOR + idCategoria;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            if (partes.length >= 2) {
                try {
                    int id = Integer.parseInt(partes[1]);
                    return new OperationResult<>(true, "Movimentação criada com sucesso", id);
                } catch (NumberFormatException e) {
                    return new OperationResult<>(false, "Erro ao processar resposta", null);
                }
            }
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> atualizarMovimentacao(int id, double valor, Date data, String descricao, 
                                                       Movimentacao.TipoMovimentacao tipo, int idConta, int idCategoria) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_UPDATE_MOVIMENTACAO + SEPARATOR + id + SEPARATOR + valor + SEPARATOR + data.toString() + SEPARATOR + 
                        descricao + SEPARATOR + tipo.getValor() + SEPARATOR + idConta + SEPARATOR + idCategoria;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Movimentação atualizada com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> removerMovimentacao(int id) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_DELETE_MOVIMENTACAO + SEPARATOR + id;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Movimentação removida com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    // ========== MÉTODOS DE PERFIL ==========
    
    public OperationResult<Usuario> obterPerfil() {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String resposta = networkClient.sendCommand(CMD_GET_PERFIL);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            if (partes.length >= 2) {
                String[] dados = partes[1].split(FIELD_SEPARATOR);
                if (dados.length >= 3) {
                    try {
                        int id = Integer.parseInt(dados[0]);
                        String nome = dados[1];
                        String email = dados[2];
                        
                        Usuario usuario = new Usuario(id, nome, email);
                        return new OperationResult<>(true, "Perfil carregado", usuario);
                    } catch (NumberFormatException e) {
                        return new OperationResult<>(false, "Erro ao processar dados do perfil", null);
                    }
                }
            }
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> atualizarPerfil(String nome, String email) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_UPDATE_PERFIL + SEPARATOR + nome + SEPARATOR + email;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Perfil atualizado com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    public OperationResult<Void> alterarSenha(String senhaAtual, String novaSenha) {
        if (!networkClient.isConnected()) {
            return new OperationResult<>(false, "Não conectado ao servidor", null);
        }
        
        String comando = CMD_CHANGE_PASSWORD + SEPARATOR + senhaAtual + SEPARATOR + novaSenha;
        String resposta = networkClient.sendCommand(comando);
        String[] partes = resposta.split("\\" + SEPARATOR);
        
        if (partes.length >= 1 && STATUS_OK.equals(partes[0])) {
            return new OperationResult<>(true, "Senha alterada com sucesso", null);
        }
        
        String erro = partes.length >= 2 ? partes[1] : "Erro desconhecido";
        return new OperationResult<>(false, erro, null);
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Carrega os nomes das categorias para as movimentações
     */
    private void carregarNomesCategorias(List<Movimentacao> movimentacoes) {
        if (movimentacoes == null || movimentacoes.isEmpty()) {
            return;
        }
        
        // Carregar todas as categorias uma vez
        OperationResult<List<Categoria>> resultado = listarCategorias();
        if (!resultado.isSucesso() || resultado.getDados() == null) {
            return;
        }
        
        List<Categoria> categorias = resultado.getDados();
        
        // Para cada movimentação, encontrar e definir o nome da categoria
        for (Movimentacao movimentacao : movimentacoes) {
            for (Categoria categoria : categorias) {
                if (categoria.getId() == movimentacao.getIdCategoria()) {
                    movimentacao.setNomeCategoria(categoria.getNome());
                    break;
                }
            }
        }
    }
    
    // ========== CLASSES DE RESULTADO ==========
    
    public static class OperationResult<T> {
        private final boolean sucesso;
        private final String mensagem;
        private final T dados;
        
        public OperationResult(boolean sucesso, String mensagem, T dados) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.dados = dados;
        }
        
        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }
        public T getDados() { return dados; }
    }
    
    public static class DashboardData {
        private final boolean sucesso;
        private final String mensagem;
        private final double saldoTotal;
        private final double receitasMes;
        private final double despesasMes;
        private final int numTransacoes;
        
        public DashboardData(boolean sucesso, String mensagem, double saldoTotal, double receitasMes, double despesasMes, int numTransacoes) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.saldoTotal = saldoTotal;
            this.receitasMes = receitasMes;
            this.despesasMes = despesasMes;
            this.numTransacoes = numTransacoes;
        }
        
        public boolean isSucesso() { return sucesso; }
        public String getMensagem() { return mensagem; }
        public double getSaldoTotal() { return saldoTotal; }
        public double getReceitasMes() { return receitasMes; }
        public double getDespesasMes() { return despesasMes; }
        public int getNumTransacoes() { return numTransacoes; }
    }
}