package server;

import dao.UsuarioDAO;
import model.Usuario;
import util.SecurityUtil;

import java.io.*;
import java.net.Socket;

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
    private Usuario usuarioLogado;
    
    public ClientHandler(Socket clientSocket, boolean testMode) {
        this.clientSocket = clientSocket;
        this.testMode = testMode;
        this.usuarioDAO = new UsuarioDAO();
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
        
        // TODO: Implementar busca real no banco de dados
        return Protocol.createSuccessResponse("0.00;0.00;0.00;0");
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
        
        // TODO: Implementar busca real no banco de dados
        return Protocol.createSuccessResponse("");
    }
    
    /**
     * Processa comando ADD_CONTA
     */
    private String processarAddConta(String[] partes) {
        if (usuarioLogado == null) {
            return Protocol.createErrorResponse("Usuário não está logado");
        }
        
        if (partes.length < 3) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para adicionar conta");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Conta adicionada com sucesso (modo teste)");
        }
        
        // TODO: Implementar inserção real no banco de dados
        return Protocol.createSuccessResponse("Conta adicionada com sucesso");
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
        
        // TODO: Implementar busca real no banco de dados
        return Protocol.createSuccessResponse("");
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
        
        // TODO: Implementar inserção real no banco de dados
        return Protocol.createSuccessResponse("Categoria adicionada com sucesso");
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
        
        // TODO: Implementar busca real no banco de dados
        return Protocol.createSuccessResponse("");
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
        
        if (partes.length < 5) {
            return Protocol.createErrorResponse("Parâmetros insuficientes para adicionar movimentação");
        }
        
        // Modo de teste
        if (testMode) {
            return Protocol.createSuccessResponse("Movimentação adicionada com sucesso (modo teste)");
        }
        
        // TODO: Implementar inserção real no banco de dados
        return Protocol.createSuccessResponse("Movimentação adicionada com sucesso");
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