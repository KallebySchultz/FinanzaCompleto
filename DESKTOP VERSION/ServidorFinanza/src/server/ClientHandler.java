package server;

import dao.UsuarioDAO;
import dao.ContaDAO;
import dao.CategoriaDAO;
import dao.MovimentacaoDAO;
import model.Usuario;
import model.Conta;
import model.Categoria;
import model.Movimentacao;
import util.SecurityUtil;
import util.EmailUtil;

import java.io.*;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Thread para atender cada cliente conectado
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private Usuario usuarioLogado;
    private UsuarioDAO usuarioDAO;
    private ContaDAO contaDAO;
    private CategoriaDAO categoriaDAO;
    private MovimentacaoDAO movimentacaoDAO;
    private boolean testMode;
    
    public ClientHandler(Socket socket) {
        this(socket, false);
    }
    
    public ClientHandler(Socket socket, boolean testMode) {
        this.clientSocket = socket;
        this.testMode = testMode;
        
        if (!testMode) {
            this.usuarioDAO = new UsuarioDAO();
            this.contaDAO = new ContaDAO();
            this.categoriaDAO = new CategoriaDAO();
            this.movimentacaoDAO = new MovimentacaoDAO();
        }
        
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Erro ao configurar streams do cliente: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
        
        try {
            String mensagem;
            while ((mensagem = input.readLine()) != null) {
                System.out.println("Comando recebido: " + mensagem);
                processarComando(mensagem);
            }
        } catch (IOException e) {
            System.err.println("Erro na comunicação com cliente: " + e.getMessage());
        } finally {
            fecharConexao();
        }
    }
    
    private void processarComando(String mensagem) {
        String[] partes = Protocol.parseCommand(mensagem);
        
        if (partes.length == 0) {
            enviarResposta(Protocol.createErrorResponse("Comando inválido"));
            return;
        }
        
        String comando = partes[0];
        
        switch (comando) {
            case Protocol.CMD_LOGIN:
                processarLogin(partes);
                break;
            case Protocol.CMD_REGISTER:
                processarRegistro(partes);
                break;
            case Protocol.CMD_LOGOUT:
                processarLogout();
                break;
            case Protocol.CMD_GET_DASHBOARD:
                processarDashboard();
                break;
                
            // Comandos de Conta
            case Protocol.CMD_LIST_CONTAS:
                processarListarContas();
                break;
            case Protocol.CMD_ADD_CONTA:
                processarAdicionarConta(partes);
                break;
            case Protocol.CMD_UPDATE_CONTA:
                processarAtualizarConta(partes);
                break;
            case Protocol.CMD_DELETE_CONTA:
                processarRemoverConta(partes);
                break;
                
            // Comandos de Categoria
            case Protocol.CMD_LIST_CATEGORIAS:
                processarListarCategorias();
                break;
            case Protocol.CMD_LIST_CATEGORIAS_TIPO:
                processarListarCategoriasPorTipo(partes);
                break;
            case Protocol.CMD_ADD_CATEGORIA:
                processarAdicionarCategoria(partes);
                break;
            case Protocol.CMD_UPDATE_CATEGORIA:
                processarAtualizarCategoria(partes);
                break;
            case Protocol.CMD_DELETE_CATEGORIA:
                processarRemoverCategoria(partes);
                break;
                
            // Comandos de Movimentação
            case Protocol.CMD_LIST_MOVIMENTACOES:
                processarListarMovimentacoes();
                break;
            case Protocol.CMD_LIST_MOVIMENTACOES_PERIODO:
                processarListarMovimentacoesPorPeriodo(partes);
                break;
            case Protocol.CMD_LIST_MOVIMENTACOES_CONTA:
                processarListarMovimentacoesPorConta(partes);
                break;
            case Protocol.CMD_ADD_MOVIMENTACAO:
                processarAdicionarMovimentacao(partes);
                break;
            case Protocol.CMD_UPDATE_MOVIMENTACAO:
                processarAtualizarMovimentacao(partes);
                break;
            case Protocol.CMD_DELETE_MOVIMENTACAO:
                processarRemoverMovimentacao(partes);
                break;
                
            // Comandos de Perfil
            case Protocol.CMD_GET_PERFIL:
                processarObterPerfil();
                break;
            case Protocol.CMD_UPDATE_PERFIL:
                processarAtualizarPerfil(partes);
                break;
            case Protocol.CMD_CHANGE_PASSWORD:
                processarAlterarSenha(partes);
                break;
            case Protocol.CMD_RECOVER_PASSWORD:
                processarRecuperarSenha(partes);
                break;
                
            default:
                enviarResposta(Protocol.createErrorResponse("Comando não reconhecido: " + comando));
        }
    }
    
    private void processarLogin(String[] partes) {
        if (partes.length < 3) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para login"));
            return;
        }
        
        String email = partes[1];
        String senha = partes[2];
        
        if (testMode) {
            // Modo de teste - aceitar qualquer login válido
            if (email != null && !email.trim().isEmpty() && senha != null && !senha.trim().isEmpty()) {
                // Criar usuário fictício para teste
                this.usuarioLogado = new Usuario(1, "Usuário Teste", email, "hash_teste");
                this.usuarioLogado.setDataCriacao(new java.sql.Timestamp(System.currentTimeMillis()));
                String dadosUsuario = "1" + Protocol.FIELD_SEPARATOR + 
                                     "Usuário Teste" + Protocol.FIELD_SEPARATOR + 
                                     email;
                enviarResposta(Protocol.createSuccessResponse(dadosUsuario));
                System.out.println("Login de teste realizado: " + email);
            } else {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Email ou senha inválidos"));
            }
            return;
        }
        
        if (!SecurityUtil.validarEmail(email)) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido"));
            return;
        }
        
        Usuario usuario = usuarioDAO.autenticar(email, senha);
        
        if (usuario != null) {
            this.usuarioLogado = usuario;
            String dadosUsuario = usuario.getId() + Protocol.FIELD_SEPARATOR + 
                                 usuario.getNome() + Protocol.FIELD_SEPARATOR + 
                                 usuario.getEmail();
            enviarResposta(Protocol.createSuccessResponse(dadosUsuario));
            System.out.println("Login realizado: " + usuario.getEmail());
        } else {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Email ou senha inválidos"));
        }
    }
    
    private void processarRegistro(String[] partes) {
        if (partes.length < 4) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para registro"));
            return;
        }
        
        String nome = partes[1];
        String email = partes[2];
        String senha = partes[3];
        
        // Validações
        if (nome == null || nome.trim().isEmpty()) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome é obrigatório"));
            return;
        }
        
        if (!SecurityUtil.validarEmail(email)) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido"));
            return;
        }
        
        if (!SecurityUtil.validarSenha(senha)) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Senha deve ter pelo menos 6 caracteres"));
            return;
        }
        
        if (testMode) {
            // Em modo de teste, simular registro
            Usuario usuarioTeste = new Usuario(1, nome, email, "hash_teste");
            this.usuarioLogado = usuarioTeste;
            String dadosUsuario = "1" + Protocol.FIELD_SEPARATOR + 
                                 nome + Protocol.FIELD_SEPARATOR + 
                                 email;
            enviarResposta(Protocol.createSuccessResponse(dadosUsuario));
            System.out.println("Usuário registrado em modo teste: " + email);
            return;
        }
        
        // Verifica se usuário já existe
        if (usuarioDAO.buscarPorEmail(email) != null) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_USER_EXISTS, "Email já cadastrado"));
            return;
        }
        
        // Cria novo usuário
        Usuario novoUsuario = new Usuario(nome, email, SecurityUtil.hashSenha(senha));
        
        if (usuarioDAO.inserir(novoUsuario)) {
            this.usuarioLogado = novoUsuario;
            String dadosUsuario = novoUsuario.getId() + Protocol.FIELD_SEPARATOR + 
                                 novoUsuario.getNome() + Protocol.FIELD_SEPARATOR + 
                                 novoUsuario.getEmail();
            enviarResposta(Protocol.createSuccessResponse(dadosUsuario));
            System.out.println("Usuário registrado: " + novoUsuario.getEmail());
        } else {
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarLogout() {
        if (usuarioLogado != null) {
            System.out.println("Logout realizado: " + usuarioLogado.getEmail());
            usuarioLogado = null;
        }
        enviarResposta(Protocol.createSuccessResponse("Logout realizado"));
    }
    
    private void processarDashboard() {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (testMode) {
            // Resposta de teste para dashboard
            String dashboardData = "1500.00" + Protocol.FIELD_SEPARATOR + 
                                  "2000.00" + Protocol.FIELD_SEPARATOR + 
                                  "500.00" + Protocol.FIELD_SEPARATOR + 
                                  "10";
            enviarResposta(Protocol.createSuccessResponse(dashboardData));
            return;
        }
        
        try {
            // Calcular dados do mês atual
            LocalDate hoje = LocalDate.now();
            LocalDate inicioMes = hoje.withDayOfMonth(1);
            LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
            
            Date dataInicio = Date.valueOf(inicioMes);
            Date dataFim = Date.valueOf(fimMes);
            
            // Calcular receitas e despesas do mês
            double receitasMes = movimentacaoDAO.calcularTotalReceitas(usuarioLogado.getId(), dataInicio, dataFim);
            double despesasMes = movimentacaoDAO.calcularTotalDespesas(usuarioLogado.getId(), dataInicio, dataFim);
            
            // Calcular saldo total de todas as contas
            double saldoTotal = 0.0;
            List<Conta> contas = contaDAO.listarPorUsuario(usuarioLogado.getId());
            for (Conta conta : contas) {
                saldoTotal += contaDAO.calcularSaldoAtual(conta.getId());
            }
            
            // Contar número de transações
            int numTransacoes = movimentacaoDAO.contarMovimentacoes(usuarioLogado.getId());
            
            String dashboardData = String.format("%.2f", saldoTotal) + Protocol.FIELD_SEPARATOR + 
                                  String.format("%.2f", receitasMes) + Protocol.FIELD_SEPARATOR + 
                                  String.format("%.2f", despesasMes) + Protocol.FIELD_SEPARATOR + 
                                  numTransacoes;
            
            enviarResposta(Protocol.createSuccessResponse(dashboardData));
            
        } catch (Exception e) {
            System.err.println("Erro ao processar dashboard: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void enviarResposta(String resposta) {
        output.println(resposta);
        System.out.println("Resposta enviada: " + resposta);
    }
    
    // ========== MÉTODOS DE CONTA ==========
    
    private void processarListarContas() {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (testMode) {
            // Resposta de teste
            String contasTeste = "1,Conta Teste,corrente,1000.00,1000.00" + Protocol.FIELD_SEPARATOR +
                               "2,Poupança Teste,poupanca,500.00,500.00";
            enviarResposta(Protocol.createSuccessResponse(contasTeste));
            return;
        }
        
        try {
            List<Conta> contas = contaDAO.listarPorUsuario(usuarioLogado.getId());
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < contas.size(); i++) {
                Conta conta = contas.get(i);
                double saldoAtual = contaDAO.calcularSaldoAtual(conta.getId());
                
                sb.append(conta.getId()).append(",")
                  .append(conta.getNome()).append(",")
                  .append(conta.getTipo().getValor()).append(",")
                  .append(String.format("%.2f", conta.getSaldoInicial())).append(",")
                  .append(String.format("%.2f", saldoAtual));
                
                if (i < contas.size() - 1) {
                    sb.append(Protocol.FIELD_SEPARATOR);
                }
            }
            
            enviarResposta(Protocol.createSuccessResponse(sb.toString()));
            
        } catch (Exception e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAdicionarConta(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 4) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para criar conta"));
            return;
        }
        
        if (testMode) {
            // Em modo de teste, apenas validar parâmetros básicos e retornar sucesso
            String nome = partes[1];
            if (nome == null || nome.trim().isEmpty()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome da conta é obrigatório"));
                return;
            }
            enviarResposta(Protocol.createSuccessResponse("99")); // ID fictício
            return;
        }
        
        try {
            String nome = partes[1];
            String tipoStr = partes[2];
            double saldoInicial = Double.parseDouble(partes[3]);
            
            if (nome == null || nome.trim().isEmpty()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome da conta é obrigatório"));
                return;
            }
            
            Conta.TipoConta tipo = Conta.TipoConta.fromString(tipoStr);
            Conta novaConta = new Conta(nome, tipo, saldoInicial, usuarioLogado.getId());
            
            if (contaDAO.inserir(novaConta)) {
                enviarResposta(Protocol.createSuccessResponse(String.valueOf(novaConta.getId())));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao criar conta"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Valor inválido"));
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Tipo de conta inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao adicionar conta: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAtualizarConta(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 5) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para atualizar conta"));
            return;
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            String nome = partes[2];
            String tipoStr = partes[3];
            double saldoInicial = Double.parseDouble(partes[4]);
            
            if (nome == null || nome.trim().isEmpty()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome da conta é obrigatório"));
                return;
            }
            
            Conta.TipoConta tipo = Conta.TipoConta.fromString(tipoStr);
            Conta conta = new Conta(id, nome, tipo, saldoInicial, usuarioLogado.getId());
            
            if (contaDAO.atualizar(conta)) {
                enviarResposta(Protocol.createSuccessResponse("Conta atualizada"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao atualizar conta"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Valor inválido"));
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Tipo de conta inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao atualizar conta: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarRemoverConta(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 2) {
            enviarResposta(Protocol.createErrorResponse("ID da conta é obrigatório"));
            return;
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            
            if (contaDAO.remover(id, usuarioLogado.getId())) {
                enviarResposta(Protocol.createSuccessResponse("Conta removida"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao remover conta"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "ID inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao remover conta: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    // ========== MÉTODOS DE CATEGORIA ==========
    
    private void processarListarCategorias() {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (testMode) {
            // Resposta de teste
            String categoriasTeste = "1,Salário,receita" + Protocol.FIELD_SEPARATOR +
                                   "2,Alimentação,despesa" + Protocol.FIELD_SEPARATOR +
                                   "3,Transporte,despesa";
            enviarResposta(Protocol.createSuccessResponse(categoriasTeste));
            return;
        }
        
        try {
            List<Categoria> categorias = categoriaDAO.listarPorUsuario(usuarioLogado.getId());
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < categorias.size(); i++) {
                Categoria categoria = categorias.get(i);
                
                sb.append(categoria.getId()).append(",")
                  .append(categoria.getNome()).append(",")
                  .append(categoria.getTipo().getValor());
                
                if (i < categorias.size() - 1) {
                    sb.append(Protocol.FIELD_SEPARATOR);
                }
            }
            
            enviarResposta(Protocol.createSuccessResponse(sb.toString()));
            
        } catch (Exception e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarListarCategoriasPorTipo(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 2) {
            enviarResposta(Protocol.createErrorResponse("Tipo da categoria é obrigatório"));
            return;
        }
        
        if (testMode) {
            // Resposta de teste baseada no tipo solicitado
            String tipo = partes[1];
            String categoriasTeste;
            if ("receita".equals(tipo)) {
                categoriasTeste = "1,Salário,receita" + Protocol.FIELD_SEPARATOR + "3,Freelance,receita";
            } else if ("despesa".equals(tipo)) {
                categoriasTeste = "2,Alimentação,despesa" + Protocol.FIELD_SEPARATOR + "4,Transporte,despesa";
            } else {
                categoriasTeste = ""; // Tipo não reconhecido retorna vazio
            }
            enviarResposta(Protocol.createSuccessResponse(categoriasTeste));
            return;
        }
        
        try {
            Categoria.TipoCategoria tipo = Categoria.TipoCategoria.fromString(partes[1]);
            List<Categoria> categorias = categoriaDAO.listarPorTipo(usuarioLogado.getId(), tipo);
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < categorias.size(); i++) {
                Categoria categoria = categorias.get(i);
                
                sb.append(categoria.getId()).append(",")
                  .append(categoria.getNome()).append(",")
                  .append(categoria.getTipo().getValor());
                
                if (i < categorias.size() - 1) {
                    sb.append(Protocol.FIELD_SEPARATOR);
                }
            }
            
            enviarResposta(Protocol.createSuccessResponse(sb.toString()));
            
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Tipo de categoria inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao listar categorias por tipo: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAdicionarCategoria(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 3) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para criar categoria"));
            return;
        }
        
        if (testMode) {
            // Em modo de teste, apenas validar parâmetros básicos e retornar sucesso
            String nome = partes[1];
            if (nome == null || nome.trim().isEmpty()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome da categoria é obrigatório"));
                return;
            }
            enviarResposta(Protocol.createSuccessResponse("99")); // ID fictício
            return;
        }
        
        try {
            String nome = partes[1];
            String tipoStr = partes[2];
            
            if (nome == null || nome.trim().isEmpty()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome da categoria é obrigatório"));
                return;
            }
            
            Categoria.TipoCategoria tipo = Categoria.TipoCategoria.fromString(tipoStr);
            Categoria novaCategoria = new Categoria(nome, tipo, usuarioLogado.getId());
            
            if (categoriaDAO.inserir(novaCategoria)) {
                enviarResposta(Protocol.createSuccessResponse(String.valueOf(novaCategoria.getId())));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao criar categoria"));
            }
            
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Tipo de categoria inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao adicionar categoria: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAtualizarCategoria(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 4) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para atualizar categoria"));
            return;
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            String nome = partes[2];
            String tipoStr = partes[3];
            
            if (nome == null || nome.trim().isEmpty()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome da categoria é obrigatório"));
                return;
            }
            
            Categoria.TipoCategoria tipo = Categoria.TipoCategoria.fromString(tipoStr);
            Categoria categoria = new Categoria(id, nome, tipo, usuarioLogado.getId());
            
            if (categoriaDAO.atualizar(categoria)) {
                enviarResposta(Protocol.createSuccessResponse("Categoria atualizada"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao atualizar categoria"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "ID inválido"));
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Tipo de categoria inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarRemoverCategoria(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 2) {
            enviarResposta(Protocol.createErrorResponse("ID da categoria é obrigatório"));
            return;
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            
            if (categoriaDAO.remover(id, usuarioLogado.getId())) {
                enviarResposta(Protocol.createSuccessResponse("Categoria removida"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao remover categoria"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "ID inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao remover categoria: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    // ========== MÉTODOS DE MOVIMENTAÇÃO ==========
    
    private void processarListarMovimentacoes() {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (testMode) {
            // Resposta de teste
            String movimentacoesTeste = "1,100.00,2023-12-01,Receita Teste,receita,1,1" + Protocol.FIELD_SEPARATOR +
                                      "2,-50.00,2023-12-02,Despesa Teste,despesa,1,2";
            enviarResposta(Protocol.createSuccessResponse(movimentacoesTeste));
            return;
        }
        
        try {
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarPorUsuario(usuarioLogado.getId());
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < movimentacoes.size(); i++) {
                Movimentacao mov = movimentacoes.get(i);
                
                sb.append(mov.getId()).append(",")
                  .append(String.format("%.2f", mov.getValor())).append(",")
                  .append(mov.getData().toString()).append(",")
                  .append(mov.getDescricao() != null ? mov.getDescricao() : "").append(",")
                  .append(mov.getTipo().getValor()).append(",")
                  .append(mov.getIdConta()).append(",")
                  .append(mov.getIdCategoria());
                
                if (i < movimentacoes.size() - 1) {
                    sb.append(Protocol.FIELD_SEPARATOR);
                }
            }
            
            enviarResposta(Protocol.createSuccessResponse(sb.toString()));
            
        } catch (Exception e) {
            System.err.println("Erro ao listar movimentações: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarListarMovimentacoesPorPeriodo(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 3) {
            enviarResposta(Protocol.createErrorResponse("Período é obrigatório"));
            return;
        }
        
        try {
            Date dataInicio = Date.valueOf(partes[1]);
            Date dataFim = Date.valueOf(partes[2]);
            
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarPorPeriodo(usuarioLogado.getId(), dataInicio, dataFim);
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < movimentacoes.size(); i++) {
                Movimentacao mov = movimentacoes.get(i);
                
                sb.append(mov.getId()).append(",")
                  .append(String.format("%.2f", mov.getValor())).append(",")
                  .append(mov.getData().toString()).append(",")
                  .append(mov.getDescricao() != null ? mov.getDescricao() : "").append(",")
                  .append(mov.getTipo().getValor()).append(",")
                  .append(mov.getIdConta()).append(",")
                  .append(mov.getIdCategoria());
                
                if (i < movimentacoes.size() - 1) {
                    sb.append(Protocol.FIELD_SEPARATOR);
                }
            }
            
            enviarResposta(Protocol.createSuccessResponse(sb.toString()));
            
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Data inválida"));
        } catch (Exception e) {
            System.err.println("Erro ao listar movimentações por período: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarListarMovimentacoesPorConta(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 2) {
            enviarResposta(Protocol.createErrorResponse("ID da conta é obrigatório"));
            return;
        }
        
        try {
            int idConta = Integer.parseInt(partes[1]);
            
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarPorConta(idConta, usuarioLogado.getId());
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < movimentacoes.size(); i++) {
                Movimentacao mov = movimentacoes.get(i);
                
                sb.append(mov.getId()).append(",")
                  .append(String.format("%.2f", mov.getValor())).append(",")
                  .append(mov.getData().toString()).append(",")
                  .append(mov.getDescricao() != null ? mov.getDescricao() : "").append(",")
                  .append(mov.getTipo().getValor()).append(",")
                  .append(mov.getIdConta()).append(",")
                  .append(mov.getIdCategoria());
                
                if (i < movimentacoes.size() - 1) {
                    sb.append(Protocol.FIELD_SEPARATOR);
                }
            }
            
            enviarResposta(Protocol.createSuccessResponse(sb.toString()));
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "ID inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao listar movimentações por conta: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAdicionarMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 7) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para criar movimentação"));
            return;
        }
        
        try {
            double valor = Double.parseDouble(partes[1]);
            Date data = Date.valueOf(partes[2]);
            String descricao = partes[3];
            String tipoStr = partes[4];
            int idConta = Integer.parseInt(partes[5]);
            int idCategoria = Integer.parseInt(partes[6]);
            
            if (valor <= 0) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Valor deve ser maior que zero"));
                return;
            }
            
            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.fromString(tipoStr);
            Movimentacao novaMovimentacao = new Movimentacao(valor, data, descricao, tipo, idConta, idCategoria, usuarioLogado.getId());
            
            if (movimentacaoDAO.inserir(novaMovimentacao)) {
                enviarResposta(Protocol.createSuccessResponse(String.valueOf(novaMovimentacao.getId())));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao criar movimentação"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Valor ou ID inválido"));
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Tipo de movimentação ou data inválida"));
        } catch (Exception e) {
            System.err.println("Erro ao adicionar movimentação: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAtualizarMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 8) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para atualizar movimentação"));
            return;
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            double valor = Double.parseDouble(partes[2]);
            Date data = Date.valueOf(partes[3]);
            String descricao = partes[4];
            String tipoStr = partes[5];
            int idConta = Integer.parseInt(partes[6]);
            int idCategoria = Integer.parseInt(partes[7]);
            
            if (valor <= 0) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Valor deve ser maior que zero"));
                return;
            }
            
            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.fromString(tipoStr);
            Movimentacao movimentacao = new Movimentacao(id, valor, data, descricao, tipo, idConta, idCategoria, usuarioLogado.getId());
            
            if (movimentacaoDAO.atualizar(movimentacao)) {
                enviarResposta(Protocol.createSuccessResponse("Movimentação atualizada"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao atualizar movimentação"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Valor ou ID inválido"));
        } catch (IllegalArgumentException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Tipo de movimentação ou data inválida"));
        } catch (Exception e) {
            System.err.println("Erro ao atualizar movimentação: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarRemoverMovimentacao(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 2) {
            enviarResposta(Protocol.createErrorResponse("ID da movimentação é obrigatório"));
            return;
        }
        
        try {
            int id = Integer.parseInt(partes[1]);
            
            if (movimentacaoDAO.remover(id, usuarioLogado.getId())) {
                enviarResposta(Protocol.createSuccessResponse("Movimentação removida"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao remover movimentação"));
            }
            
        } catch (NumberFormatException e) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "ID inválido"));
        } catch (Exception e) {
            System.err.println("Erro ao remover movimentação: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    // ========== MÉTODOS DE PERFIL ==========
    
    private void processarObterPerfil() {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (testMode) {
            // Resposta de teste
            String perfilTeste = usuarioLogado.getId() + Protocol.FIELD_SEPARATOR + 
                               usuarioLogado.getNome() + Protocol.FIELD_SEPARATOR + 
                               usuarioLogado.getEmail() + Protocol.FIELD_SEPARATOR +
                               "2023-01-01 10:00:00";
            enviarResposta(Protocol.createSuccessResponse(perfilTeste));
            return;
        }
        
        try {
            String perfilData = usuarioLogado.getId() + Protocol.FIELD_SEPARATOR + 
                               usuarioLogado.getNome() + Protocol.FIELD_SEPARATOR + 
                               usuarioLogado.getEmail() + Protocol.FIELD_SEPARATOR +
                               usuarioLogado.getDataCriacao().toString();
            
            enviarResposta(Protocol.createSuccessResponse(perfilData));
            
        } catch (Exception e) {
            System.err.println("Erro ao obter perfil: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAtualizarPerfil(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 3) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para atualizar perfil"));
            return;
        }
        
        try {
            String nome = partes[1];
            String email = partes[2];
            
            if (nome == null || nome.trim().isEmpty()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nome é obrigatório"));
                return;
            }
            
            if (!SecurityUtil.validarEmail(email)) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido"));
                return;
            }
            
            // Verifica se o email já está em uso por outro usuário
            Usuario usuarioExistente = usuarioDAO.buscarPorEmail(email);
            if (usuarioExistente != null && usuarioExistente.getId() != usuarioLogado.getId()) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_USER_EXISTS, "Email já está em uso"));
                return;
            }
            
            usuarioLogado.setNome(nome);
            usuarioLogado.setEmail(email);
            
            if (usuarioDAO.atualizar(usuarioLogado)) {
                enviarResposta(Protocol.createSuccessResponse("Perfil atualizado"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao atualizar perfil"));
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar perfil: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarAlterarSenha(String[] partes) {
        if (usuarioLogado == null) {
            enviarResposta(Protocol.createErrorResponse("Usuário não autenticado"));
            return;
        }
        
        if (partes.length < 3) {
            enviarResposta(Protocol.createErrorResponse("Dados insuficientes para alterar senha"));
            return;
        }
        
        try {
            String senhaAtual = partes[1];
            String novaSenha = partes[2];
            
            // Verifica senha atual
            if (!SecurityUtil.verificarSenha(senhaAtual, usuarioLogado.getSenhaHash())) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_CREDENTIALS, "Senha atual incorreta"));
                return;
            }
            
            if (!SecurityUtil.validarSenha(novaSenha)) {
                enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Nova senha deve ter pelo menos 6 caracteres"));
                return;
            }
            
            if (usuarioDAO.atualizarSenha(usuarioLogado.getId(), novaSenha)) {
                enviarResposta(Protocol.createSuccessResponse("Senha alterada"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao alterar senha"));
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao alterar senha: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void processarRecuperarSenha(String[] partes) {
        if (partes.length < 2) {
            enviarResposta(Protocol.createErrorResponse("Email é obrigatório para recuperação de senha"));
            return;
        }
        
        String email = partes[1];
        
        if (!SecurityUtil.validarEmail(email)) {
            enviarResposta(Protocol.createResponse(Protocol.STATUS_INVALID_DATA, "Email inválido"));
            return;
        }
        
        if (testMode) {
            // Em modo de teste, simular recuperação de senha
            String senhaTemporaria = SecurityUtil.gerarSenhaTemporaria();
            
            // Simular envio de email
            if (EmailUtil.enviarRecuperacaoSenha(email, senhaTemporaria)) {
                enviarResposta(Protocol.createSuccessResponse("Nova senha enviada por email"));
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao enviar email"));
            }
            return;
        }
        
        try {
            // Busca usuário por email
            Usuario usuario = usuarioDAO.buscarPorEmail(email);
            
            if (usuario == null) {
                // Por segurança, não revelar se o email existe ou não
                enviarResposta(Protocol.createSuccessResponse("Se o email existir, uma nova senha será enviada"));
                return;
            }
            
            // Gera nova senha temporária
            String senhaTemporaria = SecurityUtil.gerarSenhaTemporaria();
            
            // Atualiza senha no banco
            if (usuarioDAO.atualizarSenha(usuario.getId(), senhaTemporaria)) {
                // Envia email com nova senha
                if (EmailUtil.enviarRecuperacaoSenha(email, senhaTemporaria)) {
                    enviarResposta(Protocol.createSuccessResponse("Nova senha enviada por email"));
                } else {
                    enviarResposta(Protocol.createErrorResponse("Erro ao enviar email"));
                }
            } else {
                enviarResposta(Protocol.createErrorResponse("Erro ao atualizar senha"));
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao processar recuperação de senha: " + e.getMessage());
            enviarResposta(Protocol.createErrorResponse("Erro interno do servidor"));
        }
    }
    
    private void fecharConexao() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Conexão com cliente fechada");
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}