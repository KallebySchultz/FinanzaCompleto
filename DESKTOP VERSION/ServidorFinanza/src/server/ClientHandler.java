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
                
                // Admin - Gerenciamento de usuários
                case Protocol.CMD_LIST_USERS:
                    return processarListUsers();
                case Protocol.CMD_UPDATE_USER:
                    return processarUpdateUser(partes);
                case Protocol.CMD_UPDATE_USER_PASSWORD:
                    return processarUpdateUserPassword(partes);
                case Protocol.CMD_DELETE_USER:
                    return processarDeleteUser(partes);
                
                // Admin - Gerenciamento de dados de usuários
                case Protocol.CMD_ADMIN_LIST_CONTAS_USER:
                    return processarAdminListContasUser(partes);
                case Protocol.CMD_ADMIN_LIST_CATEGORIAS_USER:
                    return processarAdminListCategoriasUser(partes);
                case Protocol.CMD_ADMIN_LIST_MOVIMENTACOES_USER:
                    return processarAdminListMovimentacoesUser(partes);
                case Protocol.CMD_ADMIN_DELETE_CONTA:
                    return processarAdminDeleteConta(partes);
                case Protocol.CMD_ADMIN_DELETE_CATEGORIA:
                    return processarAdminDeleteCategoria(partes);
                case Protocol.CMD_ADMIN_DELETE_MOVIMENTACAO:
                    return processarAdminDeleteMovimentacao(partes);
                case Protocol.CMD_ADMIN_UPDATE_CONTA:
                    return processarAdminUpdateConta(partes);
                case Protocol.CMD_ADMIN_UPDATE_CATEGORIA:
                    return processarAdminUpdateCategoria(partes);
                case Protocol.CMD_ADMIN_UPDATE_MOVIMENTACAO:
                    return processarAdminUpdateMovimentacao(partes);
                    
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
        
        if (partes.length < 5) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para atualizar conta");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Conta atualizada com sucesso (modo teste)");
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            String nome = partes[2];
            String tipoStr = partes[3];
            double saldoInicial = Double.parseDouble(partes[4]);
            
            // Criar objeto conta para atualização
            Conta conta = new Conta();
            conta.setId(id);
            conta.setNome(nome);
            conta.setTipo(Conta.TipoConta.fromString(tipoStr));
            conta.setSaldoInicial(saldoInicial);
            conta.setIdUsuario(usuarioLogado.getId());
            
            // Chamar DAO para atualizar
            boolean sucesso = contaDAO.atualizar(conta);
            
            if (sucesso) {
                return Protocol.createSuccessResponse("Conta atualizada com sucesso");
            } else {
                return Protocol.createErrorResponse("Erro ao atualizar conta no banco de dados");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("Parâmetros numéricos inválidos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return Protocol.createErrorResponse("Tipo de conta inválido: " + e.getMessage());
        } catch (Exception e) {
            return Protocol.createErrorResponse("Erro interno: " + e.getMessage());
        }
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
        
        try {
            int id = Integer.parseInt(partes[1]);
            
            // Chamar DAO para remover
            boolean sucesso = contaDAO.remover(id, usuarioLogado.getId());
            
            if (sucesso) {
                return Protocol.createSuccessResponse("Conta removida com sucesso");
            } else {
                return Protocol.createErrorResponse("Erro ao remover conta - conta não encontrada ou não pertence ao usuário");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("ID da conta deve ser um número válido");
        } catch (Exception e) {
            return Protocol.createErrorResponse("Erro interno: " + e.getMessage());
        }
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
            if ("receita".equalsIgnoreCase(tipo)) {
                return Protocol.createSuccessResponse("2,Salário,receita");
            } else if ("despesa".equalsIgnoreCase(tipo)) {
                return Protocol.createSuccessResponse("1,Alimentação,despesa");
            } else {
                return Protocol.createErrorResponse("Tipo de categoria inválido");
            }
        }
        
        try {
            // Parse do tipo da categoria
            String tipoStr = partes[1];
            Categoria.TipoCategoria tipo = Categoria.TipoCategoria.fromString(tipoStr);
            
            // Buscar categorias reais do banco de dados por tipo
            List<Categoria> categorias = categoriaDAO.listarPorTipo(usuarioLogado.getId(), tipo);
            
            if (categorias.isEmpty()) {
                return Protocol.createSuccessResponse("");
            }
            
            StringBuilder categoriasData = new StringBuilder();
            for (int i = 0; i < categorias.size(); i++) {
                Categoria categoria = categorias.get(i);
                
                if (i > 0) {
                    categoriasData.append(Protocol.FIELD_SEPARATOR);
                }
                
                // Formato: id,nome,tipo (mesmo formato que LIST_CATEGORIAS)
                categoriasData.append(categoria.getId()).append(",")
                             .append(categoria.getNome()).append(",")
                             .append(categoria.getTipo().getValor());
            }
            
            return Protocol.createSuccessResponse(categoriasData.toString());
            
        } catch (IllegalArgumentException e) {
            return Protocol.createErrorResponse("Tipo de categoria inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao listar categorias por tipo: " + e.getMessage());
            e.printStackTrace();
            return Protocol.createErrorResponse("Erro ao carregar categorias");
        }
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
        
        try {
            int id = Integer.parseInt(partes[1]);
            String nome = partes[2];
            String tipoStr = partes[3];
            
            // Criar objeto categoria para atualização
            Categoria categoria = new Categoria();
            categoria.setId(id);
            categoria.setNome(nome);
            categoria.setTipo(Categoria.TipoCategoria.fromString(tipoStr));
            categoria.setIdUsuario(usuarioLogado.getId());
            
            // Chamar DAO para atualizar
            boolean sucesso = categoriaDAO.atualizar(categoria);
            
            if (sucesso) {
                return Protocol.createSuccessResponse("Categoria atualizada com sucesso");
            } else {
                return Protocol.createErrorResponse("Erro ao atualizar categoria no banco de dados");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("ID da categoria deve ser um número válido");
        } catch (IllegalArgumentException e) {
            return Protocol.createErrorResponse("Tipo de categoria inválido: " + e.getMessage());
        } catch (Exception e) {
            return Protocol.createErrorResponse("Erro interno: " + e.getMessage());
        }
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
        
        try {
            int id = Integer.parseInt(partes[1]);
            
            // Chamar DAO para remover
            boolean sucesso = categoriaDAO.remover(id, usuarioLogado.getId());
            
            if (sucesso) {
                return Protocol.createSuccessResponse("Categoria removida com sucesso");
            } else {
                return Protocol.createErrorResponse("Erro ao remover categoria - categoria não encontrada, não pertence ao usuário ou está em uso");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("ID da categoria deve ser um número válido");
        } catch (Exception e) {
            return Protocol.createErrorResponse("Erro interno: " + e.getMessage());
        }
    }
    
    // ========== MÉTODOS PARA MOVIMENTAÇÕES ==========
    
    /**
     * Processa comando LIST_MOVIMENTACOES
     * CORRIGIDO: Adicionada validação antes de acessar índices do split e campos nulos.
     */
    private String processarListMovimentacoes() {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }

        // Modo de teste - retorna dados fictícios
        if (testMode) {
            String movimentacoes = "1,100,50,2024-01-01,Supermercado,despesa,1,1" + Protocol.FIELD_SEPARATOR +
                                   "2,800,00,2024-01-01,Salário,receita,1,2" + Protocol.FIELD_SEPARATOR +
                                   "3,50,25,2024-01-02,Teste,invalid_type,1,3";
            return Protocol.createSuccessResponse(movimentacoes);
        }

        try {
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarPorUsuario(usuarioLogado.getId());

            if (movimentacoes.isEmpty()) {
                return Protocol.createSuccessResponse("");
            }

            StringBuilder movimentacoesData = new StringBuilder();
            boolean firstItem = true;
            for (int i = 0; i < movimentacoes.size(); i++) {
                Movimentacao mov = movimentacoes.get(i);

                // Validação: movimentação nula
                if (mov == null) {
                    System.err.println("Movimentação nula encontrada na posição " + i + " da lista.");
                    continue;
                }
                // Validação: tipo nulo
                if (mov.getTipo() == null) {
                    System.err.println("Movimentação com tipo nulo (ID: " + mov.getId() + ")");
                    continue;
                }
                // Validação: valor nulo ou mal formatado
                // Use US locale to ensure dot as decimal separator
                String valorStr = String.format(java.util.Locale.US, "%.2f", mov.getValor());
                String[] valorParts = valorStr.split("\\.");
                if (valorParts.length < 2) {
                    System.err.println("Valor mal formatado para movimentação ID: " + mov.getId() + " - valor: " + valorStr);
                    continue;
                }
                String valorInteiro = valorParts[0];
                String valorDecimal = valorParts[1];

                if (!firstItem) {
                    movimentacoesData.append(Protocol.FIELD_SEPARATOR);
                }
                firstItem = false;
                
                String descricao = mov.getDescricao() != null ? mov.getDescricao() : "";
                int idConta = mov.getIdConta();
                int idCategoria = mov.getIdCategoria();

                movimentacoesData.append(mov.getId()).append(",")
                                 .append(valorInteiro).append(",")
                                 .append(valorDecimal).append(",")
                                 .append(mov.getData().toString()).append(",")
                                 .append(descricao).append(",")
                                 .append(mov.getTipo().getValor()).append(",")
                                 .append(idConta).append(",")
                                 .append(idCategoria);
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
        
        if (partes.length < 8) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para atualizar movimentação");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Movimentação atualizada com sucesso (modo teste)");
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            double valor = Double.parseDouble(partes[2]);
            Date data = Date.valueOf(partes[3]);
            String descricao = partes[4];
            String tipoStr = partes[5];
            int idConta = Integer.parseInt(partes[6]);
            int idCategoria = Integer.parseInt(partes[7]);
            
            // Criar objeto movimentação para atualização
            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setId(id);
            movimentacao.setValor(valor);
            movimentacao.setData(data);
            movimentacao.setDescricao(descricao);
            movimentacao.setTipo(Movimentacao.TipoMovimentacao.fromString(tipoStr));
            movimentacao.setIdConta(idConta);
            movimentacao.setIdCategoria(idCategoria);
            movimentacao.setIdUsuario(usuarioLogado.getId());
            
            // Validar tipo de movimentação
            if (movimentacao.getTipo() == null) {
                return Protocol.createErrorResponse("Tipo de movimentação inválido");
            }
            
            // Chamar DAO para atualizar
            boolean sucesso = movimentacaoDAO.atualizar(movimentacao);
            
            if (sucesso) {
                return Protocol.createSuccessResponse("Movimentação atualizada com sucesso");
            } else {
                return Protocol.createErrorResponse("Erro ao atualizar movimentação no banco de dados");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("Parâmetros numéricos inválidos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return Protocol.createErrorResponse("Data inválida: " + e.getMessage());
        } catch (Exception e) {
            return Protocol.createErrorResponse("Erro interno: " + e.getMessage());
        }
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
        
        try {
            int id = Integer.parseInt(partes[1]);
            
            // Chamar DAO para remover
            boolean sucesso = movimentacaoDAO.remover(id, usuarioLogado.getId());
            
            if (sucesso) {
                return Protocol.createSuccessResponse("Movimentação removida com sucesso");
            } else {
                return Protocol.createErrorResponse("Erro ao remover movimentação - movimentação não encontrada ou não pertence ao usuário");
            }
            
        } catch (NumberFormatException e) {
            return Protocol.createErrorResponse("ID da movimentação deve ser um número válido");
        } catch (Exception e) {
            return Protocol.createErrorResponse("Erro interno: " + e.getMessage());
        }
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
     * Lista todos os usuários (comando admin)
     */
    private String processarListUsers() {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        // Modo de teste
        if (testMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("1").append(Protocol.FIELD_SEPARATOR)
              .append("Admin Teste").append(Protocol.FIELD_SEPARATOR)
              .append("admin@test.com").append("\n");
            sb.append("2").append(Protocol.FIELD_SEPARATOR)
              .append("Usuario Teste").append(Protocol.FIELD_SEPARATOR)
              .append("user@test.com");
            return Protocol.createSuccessResponse(sb.toString());
        }
        
        // Listar todos os usuários
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        
        if (usuarios.isEmpty()) {
            return Protocol.createSuccessResponse("");
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            sb.append(u.getId()).append(Protocol.FIELD_SEPARATOR)
              .append(u.getNome()).append(Protocol.FIELD_SEPARATOR)
              .append(u.getEmail());
            if (i < usuarios.size() - 1) {
                sb.append("\n");
            }
        }
        
        return Protocol.createSuccessResponse(sb.toString());
    }
    
    /**
     * Atualiza informações de um usuário (comando admin)
     */
    private String processarUpdateUser(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int userId = Integer.parseInt(partes[1]);
        String novoNome = partes[2];
        String novoEmail = partes[3];
        
        // Validações
        if (novoNome.isEmpty() || novoEmail.isEmpty()) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome e email são obrigatórios");
        }
        
        if (!SecurityUtil.validarEmail(novoEmail)) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Usuário atualizado com sucesso (modo teste)");
        }
        
        // Buscar usuário
        Usuario usuario = usuarioDAO.buscarPorId(userId);
        if (usuario == null) {
            return Protocol.createErrorResponse("Usuário não encontrado");
        }
        
        // Atualizar
        usuario.setNome(novoNome);
        usuario.setEmail(novoEmail);
        
        if (usuarioDAO.atualizar(usuario)) {
            return Protocol.createSuccessResponse("Usuário atualizado com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao atualizar usuário");
        }
    }
    
    /**
     * Atualiza senha de um usuário (comando admin)
     */
    private String processarUpdateUserPassword(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int userId = Integer.parseInt(partes[1]);
        String novaSenha = partes[2];
        
        // Validações
        if (novaSenha.isEmpty() || novaSenha.length() < 6) {
            return Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Senha deve ter no mínimo 6 caracteres");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Senha atualizada com sucesso (modo teste)");
        }
        
        // Atualizar senha
        if (usuarioDAO.atualizarSenha(userId, novaSenha)) {
            return Protocol.createSuccessResponse("Senha atualizada com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao atualizar senha");
        }
    }
    
    /**
     * Processa comando de excluir usuário (apenas admin)
     */
    private String processarDeleteUser(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int userId = Integer.parseInt(partes[1]);
        
        // Não permitir excluir o próprio usuário logado
        if (userId == usuarioLogado.getId()) {
            return Protocol.createErrorResponse("Não é possível excluir o próprio usuário logado");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Usuário excluído com sucesso (modo teste)");
        }
        
        // Excluir usuário
        if (usuarioDAO.excluir(userId)) {
            return Protocol.createSuccessResponse("Usuário excluído com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao excluir usuário");
        }
    }
    
    // ========== MÉTODOS ADMIN PARA GERENCIAR DADOS DE USUÁRIOS ==========
    
    /**
     * Lista contas de um usuário específico (comando admin)
     */
    private String processarAdminListContasUser(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int userId = Integer.parseInt(partes[1]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("1;Conta Teste;corrente;1000.00");
        }
        
        // Buscar contas do usuário
        List<Conta> contas = contaDAO.listarPorUsuario(userId);
        
        if (contas.isEmpty()) {
            return Protocol.createSuccessResponse("");
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contas.size(); i++) {
            Conta c = contas.get(i);
            sb.append(c.getId()).append(Protocol.FIELD_SEPARATOR)
              .append(c.getNome()).append(Protocol.FIELD_SEPARATOR)
              .append(c.getTipo()).append(Protocol.FIELD_SEPARATOR)
              .append(c.getSaldoInicial());
            if (i < contas.size() - 1) {
                sb.append("\n");
            }
        }
        
        return Protocol.createSuccessResponse(sb.toString());
    }
    
    /**
     * Lista categorias de um usuário específico (comando admin)
     */
    private String processarAdminListCategoriasUser(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int userId = Integer.parseInt(partes[1]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("1;Alimentação;despesa");
        }
        
        // Buscar categorias do usuário
        List<Categoria> categorias = categoriaDAO.listarPorUsuario(userId);
        
        if (categorias.isEmpty()) {
            return Protocol.createSuccessResponse("");
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categorias.size(); i++) {
            Categoria cat = categorias.get(i);
            sb.append(cat.getId()).append(Protocol.FIELD_SEPARATOR)
              .append(cat.getNome()).append(Protocol.FIELD_SEPARATOR)
              .append(cat.getTipo());
            if (i < categorias.size() - 1) {
                sb.append("\n");
            }
        }
        
        return Protocol.createSuccessResponse(sb.toString());
    }
    
    /**
     * Lista movimentações de um usuário específico (comando admin)
     */
    private String processarAdminListMovimentacoesUser(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int userId = Integer.parseInt(partes[1]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("1;100.00;2024-01-01;Teste;despesa;1;1");
        }
        
        // Buscar movimentações do usuário
        List<Movimentacao> movimentacoes = movimentacaoDAO.listarPorUsuario(userId);
        
        if (movimentacoes.isEmpty()) {
            return Protocol.createSuccessResponse("");
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < movimentacoes.size(); i++) {
            Movimentacao m = movimentacoes.get(i);
            sb.append(m.getId()).append(Protocol.FIELD_SEPARATOR)
              .append(m.getValor()).append(Protocol.FIELD_SEPARATOR)
              .append(m.getData()).append(Protocol.FIELD_SEPARATOR)
              .append(m.getDescricao() != null ? m.getDescricao() : "").append(Protocol.FIELD_SEPARATOR)
              .append(m.getTipo()).append(Protocol.FIELD_SEPARATOR)
              .append(m.getIdConta()).append(Protocol.FIELD_SEPARATOR)
              .append(m.getIdCategoria());
            if (i < movimentacoes.size() - 1) {
                sb.append("\n");
            }
        }
        
        return Protocol.createSuccessResponse(sb.toString());
    }
    
    /**
     * Atualiza uma conta (comando admin)
     */
    private String processarAdminUpdateConta(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 5) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int contaId = Integer.parseInt(partes[1]);
        String novoNome = partes[2];
        String novoTipo = partes[3];
        double novoSaldo = Double.parseDouble(partes[4]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Conta atualizada com sucesso (modo teste)");
        }
        
        // Buscar e atualizar conta
        Conta conta = contaDAO.buscarPorId(contaId);
        if (conta == null) {
            return Protocol.createErrorResponse("Conta não encontrada");
        }
        
        conta.setNome(novoNome);
        conta.setTipo(novoTipo);
        conta.setSaldoInicial(novoSaldo);
        
        if (contaDAO.atualizar(conta)) {
            return Protocol.createSuccessResponse("Conta atualizada com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao atualizar conta");
        }
    }
    
    /**
     * Atualiza uma categoria (comando admin)
     */
    private String processarAdminUpdateCategoria(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 4) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int categoriaId = Integer.parseInt(partes[1]);
        String novoNome = partes[2];
        String novoTipo = partes[3];
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Categoria atualizada com sucesso (modo teste)");
        }
        
        // Buscar e atualizar categoria
        Categoria categoria = categoriaDAO.buscarPorId(categoriaId);
        if (categoria == null) {
            return Protocol.createErrorResponse("Categoria não encontrada");
        }
        
        categoria.setNome(novoNome);
        categoria.setTipo(novoTipo);
        
        if (categoriaDAO.atualizar(categoria)) {
            return Protocol.createSuccessResponse("Categoria atualizada com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao atualizar categoria");
        }
    }
    
    /**
     * Atualiza uma movimentação (comando admin)
     */
    private String processarAdminUpdateMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 8) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int movimentacaoId = Integer.parseInt(partes[1]);
        double novoValor = Double.parseDouble(partes[2]);
        Date novaData = Date.valueOf(partes[3]);
        String novaDescricao = partes[4];
        String novoTipo = partes[5];
        int novoIdConta = Integer.parseInt(partes[6]);
        int novoIdCategoria = Integer.parseInt(partes[7]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Movimentação atualizada com sucesso (modo teste)");
        }
        
        // Buscar e atualizar movimentação
        Movimentacao movimentacao = movimentacaoDAO.buscarPorId(movimentacaoId);
        if (movimentacao == null) {
            return Protocol.createErrorResponse("Movimentação não encontrada");
        }
        
        movimentacao.setValor(novoValor);
        movimentacao.setData(novaData);
        movimentacao.setDescricao(novaDescricao);
        movimentacao.setTipo(novoTipo);
        movimentacao.setIdConta(novoIdConta);
        movimentacao.setIdCategoria(novoIdCategoria);
        
        if (movimentacaoDAO.atualizar(movimentacao)) {
            return Protocol.createSuccessResponse("Movimentação atualizada com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao atualizar movimentação");
        }
    }
    
    /**
     * Exclui uma conta (comando admin)
     */
    private String processarAdminDeleteConta(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int contaId = Integer.parseInt(partes[1]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Conta excluída com sucesso (modo teste)");
        }
        
        if (contaDAO.excluir(contaId)) {
            return Protocol.createSuccessResponse("Conta excluída com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao excluir conta");
        }
    }
    
    /**
     * Exclui uma categoria (comando admin)
     */
    private String processarAdminDeleteCategoria(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int categoriaId = Integer.parseInt(partes[1]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Categoria excluída com sucesso (modo teste)");
        }
        
        if (categoriaDAO.excluir(categoriaId)) {
            return Protocol.createSuccessResponse("Categoria excluída com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao excluir categoria");
        }
    }
    
    /**
     * Exclui uma movimentação (comando admin)
     */
    private String processarAdminDeleteMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não autenticado");
        }
        
        if (partes.length < 2) {
            return Protocol.createErrorResponse("Parâmetros insuficientes");
        }
        
        int movimentacaoId = Integer.parseInt(partes[1]);
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Movimentação excluída com sucesso (modo teste)");
        }
        
        if (movimentacaoDAO.excluir(movimentacaoId)) {
            return Protocol.createSuccessResponse("Movimentação excluída com sucesso");
        } else {
            return Protocol.createErrorResponse("Erro ao excluir movimentação");
        }
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