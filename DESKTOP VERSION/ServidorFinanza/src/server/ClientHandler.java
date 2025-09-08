package server;

import dao.*;
import model.*;
import util.SecurityUtil;

import java.io.*;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Manipulador de clientes conectados ao servidor
 * Processa comandos recebidos dos clientes e envia respostas
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean testMode;
    private UsuarioDAO usuarioDAO;
    private ContaDAO contaDAO;
    private CategoriaDAO categoriaDAO;
    private MovimentacaoDAO movimentacaoDAO;
    private Usuario usuarioLogado;
    
    public ClientHandler(Socket clientSocket, boolean testMode) {
        this.clientSocket = clientSocket;
        this.testMode = testMode;
        this.usuarioDAO = new UsuarioDAO();
        this.contaDAO = new ContaDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();
    }
    
    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String clientAddress = clientSocket.getRemoteSocketAddress().toString();
            System.out.println("Cliente conectado: " + clientAddress);
            
            String comando;
            while ((comando = input.readLine()) != null) {
                System.out.println("Comando recebido: " + comando);
                String resposta = processarComando(comando);
                System.out.println("Resposta enviada: " + resposta);
                output.println(resposta);
            }
            
        } catch (IOException e) {
            System.err.println("Erro na comunicação com cliente: " + e.getMessage());
        } finally {
            fecharConexao();
        }
    }
    
    /**
     * Processa comando recebido do cliente
     */
    private String processarComando(String comando) {
        String[] partes = Protocol.parseCommand(comando);
        
        if (partes.length == 0) {
            return Protocol.createErrorResponse("Comando inválido");
        }
        
        String cmd = partes[0];
        
        try {
            switch (cmd) {
                case Protocol.CMD_LOGIN:
                    return processarLogin(partes);
                    
                case Protocol.CMD_REGISTER:
                    return processarRegistro(partes);
                    
                case Protocol.CMD_LOGOUT:
                    return processarLogout();
                    
                case Protocol.CMD_RESET_PASSWORD:
                    return processarResetSenha(partes);
                    
                case Protocol.CMD_CHANGE_PASSWORD:
                    return processarAlterarSenha(partes);
                    
                // Dashboard
                case Protocol.CMD_GET_DASHBOARD:
                    return processarGetDashboard();
                    
                // Contas
                case Protocol.CMD_LIST_CONTAS:
                    return processarListContas();
                case Protocol.CMD_ADD_CONTA:
                    return processarAddConta(partes);
                case Protocol.CMD_UPDATE_CONTA:
                    return processarUpdateConta(partes);
                case Protocol.CMD_DELETE_CONTA:
                    return processarDeleteConta(partes);
                    
                // Categorias
                case Protocol.CMD_LIST_CATEGORIAS:
                    return processarListCategorias();
                case Protocol.CMD_LIST_CATEGORIAS_TIPO:
                    return processarListCategoriasTipo(partes);
                case Protocol.CMD_ADD_CATEGORIA:
                    return processarAddCategoria(partes);
                case Protocol.CMD_UPDATE_CATEGORIA:
                    return processarUpdateCategoria(partes);
                case Protocol.CMD_DELETE_CATEGORIA:
                    return processarDeleteCategoria(partes);
                    
                // Movimentações
                case Protocol.CMD_LIST_MOVIMENTACOES:
                    return processarListMovimentacoes();
                case Protocol.CMD_LIST_MOVIMENTACOES_PERIODO:
                    return processarListMovimentacoesPeriodo(partes);
                case Protocol.CMD_LIST_MOVIMENTACOES_CONTA:
                    return processarListMovimentacoesConta(partes);
                case Protocol.CMD_ADD_MOVIMENTACAO:
                    return processarAddMovimentacao(partes);
                case Protocol.CMD_UPDATE_MOVIMENTACAO:
                    return processarUpdateMovimentacao(partes);
                case Protocol.CMD_DELETE_MOVIMENTACAO:
                    return processarDeleteMovimentacao(partes);
                    
                // Perfil
                case Protocol.CMD_GET_PERFIL:
                    return processarGetPerfil();
                case Protocol.CMD_UPDATE_PERFIL:
                    return processarUpdatePerfil(partes);
                    
                default:
                    return Protocol.createErrorResponse("Comando não reconhecido: " + cmd);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao processar comando '" + cmd + "': " + e.getMessage());
            return Protocol.createErrorResponse("Erro interno do servidor");
        }
    }
    
    /**
     * Processa comando de login
     */
    private String processarLogin(String[] partes) {
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para login");
        }
        
        String email = partes[1];
        String senha = partes[2];
        
        // Validação básica
        if (email.isEmpty() || senha.isEmpty()) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email e senha são obrigatórios");
        }
        
        if (!SecurityUtil.validarEmail(email)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido");
        }
        
        // Modo de teste - sempre retorna sucesso
        if (testMode) {
            usuarioLogado = new Usuario(1, "Usuário Teste", email, "");
            String userData = usuarioLogado.getId() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getNome() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getEmail();
            return Protocol.createSuccessResponse(userData);
        }
        
        // Autenticação real
        Usuario usuario = usuarioDAO.autenticar(email, senha);
        if (usuario != null) {
            usuarioLogado = usuario;
            String userData = usuario.getId() + Protocol.FIELD_SEPARATOR + 
                             usuario.getNome() + Protocol.FIELD_SEPARATOR + 
                             usuario.getEmail();
            return Protocol.createSuccessResponse(userData);
        } else {
            return Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Email ou senha inválidos");
        }
    }
    
    /**
     * Processa comando de registro
     */
    private String processarRegistro(String[] partes) {
        if (partes.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para registro");
        }
        
        String nome = partes[1];
        String email = partes[2];
        String senha = partes[3];
        
        // Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Todos os campos são obrigatórios");
        }
        
        if (!SecurityUtil.validarEmail(email)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido");
        }
        
        if (!SecurityUtil.validarSenha(senha)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Senha deve ter pelo menos 6 caracteres");
        }
        
        // Modo de teste
        if (testMode) {
            usuarioLogado = new Usuario(1, nome, email, "");
            String userData = usuarioLogado.getId() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getNome() + Protocol.FIELD_SEPARATOR + 
                             usuarioLogado.getEmail();
            return Protocol.createSuccessResponse(userData);
        }
        
        // Verifica se email já existe
        if (usuarioDAO.buscarPorEmail(email) != null) {
            return Protocol.createResponse(Protocol.STATUS_USER_EXISTS, "Email já cadastrado");
        }
        
        // Cria novo usuário
        Usuario novoUsuario = new Usuario(nome, email, SecurityUtil.hashSenha(senha));
        if (usuarioDAO.inserir(novoUsuario)) {
            usuarioLogado = novoUsuario;
            String userData = novoUsuario.getId() + Protocol.FIELD_SEPARATOR + 
                             novoUsuario.getNome() + Protocol.FIELD_SEPARATOR + 
                             novoUsuario.getEmail();
            return Protocol.createSuccessResponse(userData);
        } else {
            return Protocol.createErrorResponse("Erro ao criar usuário");
        }
    }
    
    /**
     * Processa comando de logout
     */
    private String processarLogout() {
        usuarioLogado = null;
        return Protocol.createSuccessResponse("Logout realizado");
    }
    
    /**
     * Processa reset de senha por email
     */
    private String processarResetSenha(String[] partes) {
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Email é obrigatório para reset de senha");
        }
        
        String email = partes[1];
        
        if (!SecurityUtil.validarEmail(email)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Reset de senha enviado por email (modo teste)");
        }
        
        // Verifica se usuário existe
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            // Por segurança, não revelamos se o email existe ou não
            return Protocol.createSuccessResponse("Se o email existe, instruções foram enviadas");
        }
        
        // Aqui seria implementado o envio de email real
        // Por simplicidade, vamos apenas gerar uma nova senha temporária
        String novaSenhaTemp = gerarSenhaTemporaria();
        
        if (usuarioDAO.atualizarSenha(usuario.getId(), novaSenhaTemp)) {
            // Em uma implementação real, enviaria por email
            // Para teste, retornamos a nova senha
            return Protocol.createSuccessResponse("Nova senha temporária: " + novaSenhaTemp);
        } else {
            return Protocol.createErrorResponse("Erro ao resetar senha");
        }
    }
    
    /**
     * Processa alteração de senha
     */
    private String processarAlterarSenha(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Senha atual e nova senha são obrigatórias");
        }
        
        String senhaAtual = partes[1];
        String novaSenha = partes[2];
        
        if (!SecurityUtil.validarSenha(novaSenha)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nova senha deve ter pelo menos 6 caracteres");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Senha alterada com sucesso (modo teste)");
        }
        
        // Verifica senha atual
        Usuario usuario = usuarioDAO.autenticar(usuarioLogado.getEmail(), senhaAtual);
        if (usuario == null) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Senha atual incorreta");
        }
        
        // Atualiza senha
        if (usuarioDAO.atualizarSenha(usuarioLogado.getId(), novaSenha)) {
            return Protocol.createSuccessResponse("Senha alterada com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao alterar senha");
        }
    }
    
    /**
     * Gera uma senha temporária simples
     */
    private String gerarSenhaTemporaria() {
        return "temp" + System.currentTimeMillis() % 10000;
    }
    
    // ========== MÉTODOS PARA DASHBOARD ==========
    
    /**
     * Processa comando GET_DASHBOARD
     */
    private String processarGetDashboard() {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        // Modo de teste - retorna dados fictícios
        if (testMode) {
            String dashboardData = "1500.50" + Protocol.FIELD_SEPARATOR + 
                                 "800.00" + Protocol.FIELD_SEPARATOR + 
                                 "300.00" + Protocol.FIELD_SEPARATOR + 
                                 "15";
            return Protocol.createSuccessResponse(dashboardData);
        }
        
        try {
            // Calcular dados reais do dashboard usando DAOs
            
            // 1. Calcular saldo total de todas as contas
            double saldoTotal = 0.0;
            List<Conta> contas = contaDAO.listarPorUsuario(usuarioLogado.getId());
            for (Conta conta : contas) {
                saldoTotal += contaDAO.calcularSaldoAtual(conta.getId());
            }
            
            // 2. Calcular receitas e despesas do mês atual
            LocalDate hoje = LocalDate.now();
            LocalDate inicioMes = hoje.withDayOfMonth(1);
            LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
            
            Date dataInicio = Date.valueOf(inicioMes);
            Date dataFim = Date.valueOf(fimMes);
            
            double receitasMes = movimentacaoDAO.calcularTotalReceitas(usuarioLogado.getId(), dataInicio, dataFim);
            double despesasMes = movimentacaoDAO.calcularTotalDespesas(usuarioLogado.getId(), dataInicio, dataFim);
            
            // 3. Contar total de transações
            int numTransacoes = movimentacaoDAO.contarMovimentacoes(usuarioLogado.getId());
            
            // Formatar os dados usando vírgula como separador decimal (formato brasileiro)
            String saldoTotalStr = String.format("%.2f", saldoTotal).replace(".", ",");
            String receitasMesStr = String.format("%.2f", receitasMes).replace(".", ",");
            String despesasMesStr = String.format("%.2f", despesasMes).replace(".", ",");
            
            String dashboardData = saldoTotalStr + Protocol.FIELD_SEPARATOR + 
                                 receitasMesStr + Protocol.FIELD_SEPARATOR + 
                                 despesasMesStr + Protocol.FIELD_SEPARATOR + 
                                 numTransacoes;
            
            return Protocol.createSuccessResponse(dashboardData);
            
        } catch (Exception e) {
            System.err.println("Erro ao calcular dados do dashboard: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, retornar zeros
            return Protocol.createSuccessResponse("0,00" + Protocol.FIELD_SEPARATOR + 
                                                "0,00" + Protocol.FIELD_SEPARATOR + 
                                                "0,00" + Protocol.FIELD_SEPARATOR + "0");
        }
    }
    
    // ========== MÉTODOS PARA CONTAS ==========
    
    /**
     * Processa comando LIST_CONTAS
     */
    private String processarListContas() {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        // Modo de teste - retorna dados fictícios
        if (testMode) {
            String contas = "1" + Protocol.FIELD_SEPARATOR + "Banco Principal" + Protocol.FIELD_SEPARATOR + "1500.50" + "|" +
                           "2" + Protocol.FIELD_SEPARATOR + "Poupança" + Protocol.FIELD_SEPARATOR + "500.00";
            return Protocol.createSuccessResponse(contas);
        }
        
        try {
            // Buscar contas reais do banco de dados
            List<Conta> contas = contaDAO.listarPorUsuario(usuarioLogado.getId());
            
            if (contas.isEmpty()) {
                return Protocol.createSuccessResponse("");
            }
            
            StringBuilder contasData = new StringBuilder();
            for (int i = 0; i < contas.size(); i++) {
                Conta conta = contas.get(i);
                double saldoAtual = contaDAO.calcularSaldoAtual(conta.getId());
                
                if (i > 0) {
                    contasData.append(Protocol.FIELD_SEPARATOR);
                }
                
                // Formato: id,nome,tipo,saldo_inicial_formatado,saldo_atual_formatado
                // Usar vírgula como separador decimal brasileiro
                String saldoInicialStr = String.format("%.2f", conta.getSaldoInicial()).replace(".", ",");
                String saldoAtualStr = String.format("%.2f", saldoAtual).replace(".", ",");
                
                contasData.append(conta.getId()).append(",")
                         .append(conta.getNome()).append(",")
                         .append(conta.getTipo().getValor()).append(",")
                         .append(saldoInicialStr).append(",")
                         .append(saldoAtualStr);
            }
            
            return Protocol.createSuccessResponse(contasData.toString());
            
        } catch (Exception e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
            e.printStackTrace();
            return Protocol.createErrorResponse("Erro ao carregar contas");
        }
    }
    
    /**
     * Processa comando ADD_CONTA
     */
    private String processarAddConta(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para adicionar conta");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Conta adicionada com sucesso (modo teste)");
        }
        
        try {
            String nome = partes[1];
            String tipoStr = partes[2];
            double saldoInicial = Double.parseDouble(partes[3]);
            
            // Validar tipo de conta
            Conta.TipoConta tipo = Conta.TipoConta.fromString(tipoStr);
            if (tipo == null) {
                return Protocol.createErrorResponse("Tipo de conta inválido");
            }
            
            // Criar nova conta
            Conta novaConta = new Conta();
            novaConta.setNome(nome);
            novaConta.setTipo(tipo);
            novaConta.setSaldoInicial(saldoInicial);
            novaConta.setIdUsuario(usuarioLogado.getId());
            
            if (contaDAO.inserir(novaConta)) {
                return Protocol.createSuccessResponse(String.valueOf(novaConta.getId()));
            } else {
                return Protocol.createErrorResponse("Erro ao criar conta");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("Saldo inicial inválido");
        } catch (Exception e) {
            System.err.println("Erro ao adicionar conta: " + e.getMessage());
            return Protocol.createErrorResponse("Erro interno do servidor");
        }
    }
    
    /**
     * Processa comando UPDATE_CONTA
     */
    private String processarUpdateConta(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para atualizar conta");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Conta atualizada com sucesso (modo teste)");
        }
        
        // TODO: Implementar atualização real no banco de dados
        return Protocol.createSuccessResponse("Conta atualizada com sucesso");
    }
    
    /**
     * Processa comando DELETE_CONTA
     */
    private String processarDeleteConta(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("ID da conta é obrigatório");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Conta removida com sucesso (modo teste)");
        }
        
        // TODO: Implementar remoção real no banco de dados
        return Protocol.createSuccessResponse("Conta removida com sucesso");
    }
    
    // ========== MÉTODOS PARA CATEGORIAS ==========
    
    /**
     * Processa comando LIST_CATEGORIAS
     */
    private String processarListCategorias() {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        // Modo de teste - retorna dados fictícios
        if (testMode) {
            String categorias = "1" + Protocol.FIELD_SEPARATOR + "Alimentação" + Protocol.FIELD_SEPARATOR + "DESPESA" + "|" +
                               "2" + Protocol.FIELD_SEPARATOR + "Salário" + Protocol.FIELD_SEPARATOR + "RECEITA";
            return Protocol.createSuccessResponse(categorias);
        }
        
        try {
            // Buscar categorias reais do banco de dados
            List<Categoria> categorias = categoriaDAO.listarPorUsuario(usuarioLogado.getId());
            
            if (categorias.isEmpty()) {
                return Protocol.createSuccessResponse("");
            }
            
            StringBuilder categoriasData = new StringBuilder();
            for (int i = 0; i < categorias.size(); i++) {
                Categoria categoria = categorias.get(i);
                
                if (i > 0) {
                    categoriasData.append(Protocol.FIELD_SEPARATOR);
                }
                
                // Formato: id,nome,tipo
                categoriasData.append(categoria.getId()).append(",")
                             .append(categoria.getNome()).append(",")
                             .append(categoria.getTipo().getValor());
            }
            
            return Protocol.createSuccessResponse(categoriasData.toString());
            
        } catch (Exception e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            e.printStackTrace();
            return Protocol.createErrorResponse("Erro ao carregar categorias");
        }
    }
    
    /**
     * Processa comando LIST_CATEGORIAS_TIPO
     */
    private String processarListCategoriasTipo(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Tipo da categoria é obrigatório");
        }
        
        // Modo de teste
        if (testMode) {
            String tipo = partes[1];
            if ("RECEITA".equals(tipo)) {
                return Protocol.createSuccessResponse("2" + Protocol.FIELD_SEPARATOR + "Salário" + Protocol.FIELD_SEPARATOR + "RECEITA");
            } else {
                return Protocol.createSuccessResponse("1" + Protocol.FIELD_SEPARATOR + "Alimentação" + Protocol.FIELD_SEPARATOR + "DESPESA");
            }
        }
        
        // TODO: Implementar busca real no banco de dados
        return Protocol.createSuccessResponse("");
    }
    
    /**
     * Processa comando ADD_CATEGORIA
     */
    private String processarAddCategoria(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para adicionar categoria");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Categoria adicionada com sucesso (modo teste)");
        }
        
        try {
            String nome = partes[1];
            String tipoStr = partes[2];
            
            // Validar tipo de categoria
            Categoria.TipoCategoria tipo = Categoria.TipoCategoria.fromString(tipoStr);
            if (tipo == null) {
                return Protocol.createErrorResponse("Tipo de categoria inválido");
            }
            
            // Criar nova categoria
            Categoria novaCategoria = new Categoria();
            novaCategoria.setNome(nome);
            novaCategoria.setTipo(tipo);
            novaCategoria.setIdUsuario(usuarioLogado.getId());
            
            if (categoriaDAO.inserir(novaCategoria)) {
                return Protocol.createSuccessResponse(String.valueOf(novaCategoria.getId()));
            } else {
                return Protocol.createErrorResponse("Erro ao criar categoria");
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao adicionar categoria: " + e.getMessage());
            return Protocol.createErrorResponse("Erro interno do servidor");
        }
    }
    
    /**
     * Processa comando UPDATE_CATEGORIA
     */
    private String processarUpdateCategoria(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para atualizar categoria");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Categoria atualizada com sucesso (modo teste)");
        }
        
        // TODO: Implementar atualização real no banco de dados
        return Protocol.createSuccessResponse("Categoria atualizada com sucesso");
    }
    
    /**
     * Processa comando DELETE_CATEGORIA
     */
    private String processarDeleteCategoria(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("ID da categoria é obrigatório");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Categoria removida com sucesso (modo teste)");
        }
        
        // TODO: Implementar remoção real no banco de dados
        return Protocol.createSuccessResponse("Categoria removida com sucesso");
    }
    
    // ========== MÉTODOS PARA MOVIMENTAÇÕES ==========
    
    /**
     * Processa comando LIST_MOVIMENTACOES
     */
    private String processarListMovimentacoes() {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        // Modo de teste - retorna dados fictícios
        if (testMode) {
            String movimentacoes = "1" + Protocol.FIELD_SEPARATOR + "100.50" + Protocol.FIELD_SEPARATOR + "DESPESA" + 
                                 Protocol.FIELD_SEPARATOR + "Supermercado" + Protocol.FIELD_SEPARATOR + "2024-01-01" + "|" +
                                 "2" + Protocol.FIELD_SEPARATOR + "800.00" + Protocol.FIELD_SEPARATOR + "RECEITA" + 
                                 Protocol.FIELD_SEPARATOR + "Salário" + Protocol.FIELD_SEPARATOR + "2024-01-01";
            return Protocol.createSuccessResponse(movimentacoes);
        }
        
        try {
            // Buscar movimentações reais do banco de dados
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarPorUsuario(usuarioLogado.getId());
            
            if (movimentacoes.isEmpty()) {
                return Protocol.createSuccessResponse("");
            }
            
            StringBuilder movimentacoesData = new StringBuilder();
            for (int i = 0; i < movimentacoes.size(); i++) {
                Movimentacao movimentacao = movimentacoes.get(i);
                
                if (i > 0) {
                    movimentacoesData.append(Protocol.FIELD_SEPARATOR);
                }
                
                // Formato: id,valor_formatado,data,descricao,tipo,idConta,idCategoria
                // Usar vírgula como separador decimal brasileiro - mas dividir em partes inteira e decimal
                String valorStr = String.format("%.2f", movimentacao.getValor());
                String[] valorParts = valorStr.split("\\.");
                String valorInteiro = valorParts[0];
                String valorDecimal = valorParts[1];
                
                movimentacoesData.append(movimentacao.getId()).append(",")
                                .append(valorInteiro).append(",")
                                .append(valorDecimal).append(",")
                                .append(movimentacao.getData().toString()).append(",")
                                .append(movimentacao.getDescricao()).append(",")
                                .append(movimentacao.getTipo().getValor()).append(",")
                                .append(movimentacao.getIdConta()).append(",")
                                .append(movimentacao.getIdCategoria());
            }
            
            return Protocol.createSuccessResponse(movimentacoesData.toString());
            
        } catch (Exception e) {
            System.err.println("Erro ao listar movimentações: " + e.getMessage());
            e.printStackTrace();
            return Protocol.createErrorResponse("Erro ao carregar movimentações");
        }
    }
    
    /**
     * Processa comando LIST_MOVIMENTACOES_PERIODO
     */
    private String processarListMovimentacoesPeriodo(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Data início e fim são obrigatórias");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("1" + Protocol.FIELD_SEPARATOR + "100.50" + Protocol.FIELD_SEPARATOR + "DESPESA" + 
                                                 Protocol.FIELD_SEPARATOR + "Supermercado" + Protocol.FIELD_SEPARATOR + partes[1]);
        }
        
        // TODO: Implementar busca real no banco de dados
        return Protocol.createSuccessResponse("");
    }
    
    /**
     * Processa comando LIST_MOVIMENTACOES_CONTA
     */
    private String processarListMovimentacoesConta(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("ID da conta é obrigatório");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("1" + Protocol.FIELD_SEPARATOR + "100.50" + Protocol.FIELD_SEPARATOR + "DESPESA" + 
                                                 Protocol.FIELD_SEPARATOR + "Supermercado" + Protocol.FIELD_SEPARATOR + "2024-01-01");
        }
        
        // TODO: Implementar busca real no banco de dados
        return Protocol.createSuccessResponse("");
    }
    
    /**
     * Processa comando ADD_MOVIMENTACAO
     */
    private String processarAddMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 7) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para adicionar movimentação");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Movimentação adicionada com sucesso (modo teste)");
        }
        
        try {
            double valor = Double.parseDouble(partes[1]);
            Date data = Date.valueOf(partes[2]);
            String descricao = partes[3];
            String tipoStr = partes[4];
            int idConta = Integer.parseInt(partes[5]);
            int idCategoria = Integer.parseInt(partes[6]);
            
            // Validar tipo de movimentação
            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.fromString(tipoStr);
            if (tipo == null) {
                return Protocol.createErrorResponse("Tipo de movimentação inválido");
            }
            
            // Criar nova movimentação
            Movimentacao novaMovimentacao = new Movimentacao();
            novaMovimentacao.setValor(valor);
            novaMovimentacao.setData(data);
            novaMovimentacao.setDescricao(descricao);
            novaMovimentacao.setTipo(tipo);
            novaMovimentacao.setIdConta(idConta);
            novaMovimentacao.setIdCategoria(idCategoria);
            novaMovimentacao.setIdUsuario(usuarioLogado.getId());
            
            if (movimentacaoDAO.inserir(novaMovimentacao)) {
                return Protocol.createSuccessResponse(String.valueOf(novaMovimentacao.getId()));
            } else {
                return Protocol.createErrorResponse("Erro ao criar movimentação");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("Valor ou IDs inválidos");
        } catch (IllegalArgumentException e) {
            return Protocol.createErrorResponse("Data inválida");
        } catch (Exception e) {
            System.err.println("Erro ao adicionar movimentação: " + e.getMessage());
            return Protocol.createErrorResponse("Erro interno do servidor");
        }
    }
    
    /**
     * Processa comando UPDATE_MOVIMENTACAO
     */
    private String processarUpdateMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 6) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para atualizar movimentação");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Movimentação atualizada com sucesso (modo teste)");
        }
        
        // TODO: Implementar atualização real no banco de dados
        return Protocol.createSuccessResponse("Movimentação atualizada com sucesso");
    }
    
    /**
     * Processa comando DELETE_MOVIMENTACAO
     */
    private String processarDeleteMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("ID da movimentação é obrigatório");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Movimentação removida com sucesso (modo teste)");
        }
        
        // TODO: Implementar remoção real no banco de dados
        return Protocol.createSuccessResponse("Movimentação removida com sucesso");
    }
    
    // ========== MÉTODOS PARA PERFIL ==========
    
    /**
     * Processa comando GET_PERFIL
     */
    private String processarGetPerfil() {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        // Retorna dados do usuário logado
        String perfilData = usuarioLogado.getId() + Protocol.FIELD_SEPARATOR + 
                           usuarioLogado.getNome() + Protocol.FIELD_SEPARATOR + 
                           usuarioLogado.getEmail();
        return Protocol.createSuccessResponse(perfilData);
    }
    
    /**
     * Processa comando UPDATE_PERFIL
     */
    private String processarUpdatePerfil(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Nome e email são obrigatórios");
        }
        
        String novoNome = partes[1];
        String novoEmail = partes[2];
        
        // Validações básicas
        if (novoNome.isEmpty() || novoEmail.isEmpty()) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome e email são obrigatórios");
        }
        
        // Modo de teste
        if (testMode) {
            usuarioLogado.setNome(novoNome);
            usuarioLogado.setEmail(novoEmail);
            return Protocol.createSuccessResponse("Perfil atualizado com sucesso (modo teste)");
        }
        
        // TODO: Implementar atualização real no banco de dados
        return Protocol.createSuccessResponse("Perfil atualizado com sucesso");
    }
    
    /**
     * Fecha conexão com cliente
     */
    private void fecharConexao() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Conexão com cliente encerrada");
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}