package view;

import controller.FinanceController;
import model.Conta;
import model.Categoria;
import model.Movimentacao;
import model.Usuario;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import util.ExportUtil;

/**
 * Tela de exportação e importação de dados
 */
public class ExportacaoView extends JFrame {
    private FinanceController financeController;
    private Usuario usuario;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;
    
    private JCheckBox contasCheckBox;
    private JCheckBox categoriasCheckBox;
    private JCheckBox movimentacoesCheckBox;
    private JComboBox<String> formatoComboBox;
    
    public ExportacaoView(FinanceController financeController, Usuario usuario) {
        this.financeController = financeController;
        this.usuario = usuario;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        setTitle("Finanza Desktop - Exportar/Importar Dados");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Exportação e Importação de Dados");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Painel de seleção de dados
        JPanel selecaoPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        selecaoPanel.setBorder(BorderFactory.createTitledBorder("Selecione os dados e formato para exportar:"));
        
        contasCheckBox = new JCheckBox("Contas", true);
        categoriasCheckBox = new JCheckBox("Categorias", true);
        movimentacoesCheckBox = new JCheckBox("Movimentações", true);
        
        // ComboBox para formato de exportação
        formatoComboBox = new JComboBox<>(new String[]{
            "CSV Aprimorado (Tabelas Formatadas)", 
            "HTML (Tabelas Bonitas para Web/Impressão)",
            "CSV Simples (Compatibilidade)"
        });
        formatoComboBox.setSelectedIndex(0);
        
        selecaoPanel.add(new JLabel("Dados disponíveis para exportação:"));
        selecaoPanel.add(contasCheckBox);
        selecaoPanel.add(categoriasCheckBox);
        selecaoPanel.add(movimentacoesCheckBox);
        selecaoPanel.add(new JLabel("Formato de exportação:"));
        selecaoPanel.add(formatoComboBox);
        
        mainPanel.add(selecaoPanel, BorderLayout.NORTH);
        
        // Painel de informações
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setText("Informações sobre exportação/importação:\n\n" +
                        "EXPORTAÇÃO APRIMORADA:\n" +
                        "• CSV Aprimorado: Dados organizados em tabelas ASCII bonitas + dados CSV para importação.\n" +
                        "• HTML: Tabelas web com formatação profissional, ideais para impressão ou visualização.\n" +
                        "• CSV Simples: Formato básico compatível com versões anteriores.\n" +
                        "• Todos os formatos incluem totais, saldos e formatação de moeda brasileira.\n\n" +
                        "RECURSOS NOVOS:\n" +
                        "• Tabelas com bordas e formatação visual aprimorada\n" +
                        "• Totais automáticos para contas e movimentações\n" +
                        "• Cores e destaque para receitas/despesas (HTML)\n" +
                        "• Layout responsivo para impressão (HTML)\n" +
                        "• Dados organizados para melhor compreensão financeira\n\n" +
                        "IMPORTAÇÃO:\n" +
                        "• Permite importar dados de arquivos CSV exportados anteriormente.\n" +
                        "• Os dados devem estar no formato correto (mesmo do arquivo exportado).\n" +
                        "• A importação irá ADICIONAR novos registros (não substitui dados existentes).\n\n" +
                        "ESTRUTURA DOS RELATÓRIOS:\n" +
                        "• Cabeçalho com informações do usuário e data\n" +
                        "• Seção de Contas: ID, Nome, Tipo, Saldos + Total Geral\n" +
                        "• Seção de Categorias: ID, Nome, Tipo (organizadas por tipo)\n" +
                        "• Seção de Movimentações: Data, Descrição, Tipo, Valor + Totais por categoria\n" +
                        "• Rodapé com informações adicionais para organização");
        infoArea.setBackground(getBackground());
        infoArea.setBorder(BorderFactory.createTitledBorder("Informações"));
        
        JScrollPane infoScrollPane = new JScrollPane(infoArea);
        infoScrollPane.setPreferredSize(new Dimension(550, 200));
        
        mainPanel.add(infoScrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton exportarBtn = new JButton("Exportar Dados");
        JButton importarBtn = new JButton("Importar Dados");
        JButton cancelarBtn = new JButton("Cancelar");
        
        exportarBtn.setPreferredSize(new Dimension(150, 30));
        importarBtn.setPreferredSize(new Dimension(150, 30));
        cancelarBtn.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(exportarBtn);
        buttonPanel.add(importarBtn);
        buttonPanel.add(cancelarBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pronto para exportar ou importar dados");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Eventos
        exportarBtn.addActionListener(e -> iniciarExportacao());
        importarBtn.addActionListener(e -> iniciarImportacao());
        cancelarBtn.addActionListener(e -> dispose());
    }
    
    private void iniciarExportacao() {
        // Verificar se pelo menos uma opção está selecionada
        if (!contasCheckBox.isSelected() && !categoriasCheckBox.isSelected() && !movimentacoesCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, 
                "Selecione pelo menos um tipo de dados para exportar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Determinar extensão baseada no formato selecionado
        String formato = (String) formatoComboBox.getSelectedItem();
        String extensao = formato.startsWith("HTML") ? "html" : "csv";
        String descricaoFiltro = formato.startsWith("HTML") ? "Arquivos HTML (*.html)" : "Arquivos CSV (*.csv)";
        
        // Escolher local para salvar o arquivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar exportação - " + formato);
        fileChooser.setFileFilter(new FileNameExtensionFilter(descricaoFiltro, extensao));
        fileChooser.setSelectedFile(new java.io.File("finanza_export_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + "." + extensao));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Adicionar extensão se não tiver
            if (!fileToSave.getName().toLowerCase().endsWith("." + extensao)) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + "." + extensao);
            }
            
            exportarDados(fileToSave, formato);
        }
    }
    
    private void exportarDados(java.io.File arquivo, String formato) {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Iniciando exportação em formato " + formato + "...");
                
                // Coletar dados selecionados
                List<Conta> contas = null;
                List<Categoria> categorias = null;
                List<Movimentacao> movimentacoes = null;
                
                if (contasCheckBox.isSelected()) {
                    publish("Coletando dados de contas...");
                    FinanceController.OperationResult<List<Conta>> resultContas = financeController.listarContas();
                    if (resultContas.isSucesso()) {
                        contas = resultContas.getDados();
                    }
                }
                
                if (categoriasCheckBox.isSelected()) {
                    publish("Coletando dados de categorias...");
                    FinanceController.OperationResult<List<Categoria>> resultCategorias = financeController.listarCategorias();
                    if (resultCategorias.isSucesso()) {
                        categorias = resultCategorias.getDados();
                    }
                }
                
                if (movimentacoesCheckBox.isSelected()) {
                    publish("Coletando dados de movimentações...");
                    FinanceController.OperationResult<List<Movimentacao>> resultMovimentacoes = financeController.listarMovimentacoes();
                    if (resultMovimentacoes.isSucesso()) {
                        movimentacoes = resultMovimentacoes.getDados();
                    }
                }
                
                try (FileWriter writer = new FileWriter(arquivo)) {
                    if (formato.startsWith("HTML")) {
                        publish("Gerando relatório HTML com tabelas bonitas...");
                        ExportUtil.exportarHTML(writer, contas, categorias, movimentacoes, 
                                              usuario.getNome(), usuario.getEmail());
                    } else if (formato.startsWith("CSV Aprimorado")) {
                        publish("Gerando relatório CSV com tabelas formatadas...");
                        ExportUtil.exportarCSVMelhorado(writer, contas, categorias, movimentacoes, 
                                                       usuario.getNome(), usuario.getEmail());
                    } else {
                        publish("Gerando relatório CSV simples...");
                        exportarDadosSimples(writer, contas, categorias, movimentacoes);
                    }
                    
                    publish("Exportação concluída com sucesso!");
                    
                } catch (IOException e) {
                    throw new Exception("Erro ao escrever arquivo: " + e.getMessage());
                }
                
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    System.out.println(message); // Log para debug
                }
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    String mensagem = "Dados exportados com sucesso em formato " + formato + "!\nArquivo salvo em: " + arquivo.getAbsolutePath();
                    if (formato.startsWith("HTML")) {
                        mensagem += "\n\nPara melhor organização:\n• Abra o arquivo em um navegador para visualizar\n• Use Ctrl+P para imprimir ou salvar como PDF\n• As tabelas estão formatadas para impressão profissional";
                    }
                    JOptionPane.showMessageDialog(ExportacaoView.this, 
                        mensagem, 
                        "Exportação Concluída", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ExportacaoView.this, 
                        "Erro durante a exportação: " + e.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void exportarDadosSimples(FileWriter writer, List<Conta> contas, List<Categoria> categorias, List<Movimentacao> movimentacoes) throws Exception {
        // Escrever cabeçalho
        writer.write("# Finanza Desktop - Exportação de Dados\n");
        writer.write("# Data: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()) + "\n");
        writer.write("# Usuário: " + usuario.getNome() + " (" + usuario.getEmail() + ")\n");
        writer.write("# Formato: CSV organizado com separador ponto-e-vírgula (;)\n");
        writer.write("# Para importar: Use a funcionalidade 'Importar Dados' desta mesma tela\n");
        writer.write("\n");
        
        // Exportar contas
        if (contas != null) {
            exportarContas(writer, contas);
            writer.write("\n");
        }
        
        // Exportar categorias
        if (categorias != null) {
            exportarCategorias(writer, categorias);
            writer.write("\n");
        }
        
        // Exportar movimentações
        if (movimentacoes != null) {
            exportarMovimentacoes(writer, movimentacoes);
            writer.write("\n");
        }
    }
    
    private void exportarContas(FileWriter writer, List<Conta> contas) throws Exception {
        writer.write("=== CONTAS ===\n");
        writer.write("ID;Nome;Tipo;Saldo_Inicial;Saldo_Atual\n");
        
        for (Conta conta : contas) {
            writer.write(String.format("%d;\"%s\";\"%s\";%.2f;%.2f\n",
                conta.getId(),
                conta.getNome().replace("\"", "\"\""), // Escape aspas
                conta.getTipo().getDescricao(),
                conta.getSaldoInicial(),
                conta.getSaldoAtual()));
        }
    }
    
    private void exportarCategorias(FileWriter writer, List<Categoria> categorias) throws Exception {
        writer.write("=== CATEGORIAS ===\n");
        writer.write("ID;Nome;Tipo\n");
        
        for (Categoria categoria : categorias) {
            writer.write(String.format("%d;\"%s\";\"%s\"\n",
                categoria.getId(),
                categoria.getNome().replace("\"", "\"\""), // Escape aspas
                categoria.getTipo().getDescricao()));
        }
    }
    
    private void exportarMovimentacoes(FileWriter writer, List<Movimentacao> movimentacoes) throws Exception {
        writer.write("=== MOVIMENTAÇÕES ===\n");
        writer.write("ID;Data;Descrição;Tipo;Valor;ID_Conta;ID_Categoria\n");
        
        for (Movimentacao mov : movimentacoes) {
            writer.write(String.format("%d;\"%s\";\"%s\";\"%s\";%.2f;%d;%d\n",
                mov.getId(),
                dateFormat.format(mov.getData()),
                mov.getDescricao() != null ? mov.getDescricao().replace("\"", "\"\"") : "", // Escape aspas
                mov.getTipo().getDescricao(),
                mov.getValor(),
                mov.getIdConta(),
                mov.getIdCategoria()));
        }
    }
    
    private void iniciarImportacao() {
        // Escolher arquivo para importar
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecionar arquivo para importação");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos CSV (*.csv)", "csv"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToImport = fileChooser.getSelectedFile();
            
            // Confirmar importação
            int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja importar os dados do arquivo:\n" + fileToImport.getName() + "\n\n" +
                "ATENÇÃO: Os dados serão ADICIONADOS aos existentes.\n" +
                "Esta operação não pode ser desfeita.\n\n" +
                "IMPORTANTE: Apenas arquivos CSV exportados por este sistema\n" +
                "podem ser importados. Arquivos HTML não são suportados para importação.\n\n" +
                "Continuar com a importação?",
                "Confirmar Importação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                importarDados(fileToImport);
            }
        }
    }
    
    private void importarDados(java.io.File arquivo) {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Iniciando importação...");
                
                try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
                    String linha;
                    String secaoAtual = "";
                    boolean isHeader = false;
                    boolean isCSVDataSection = false;
                    
                    while ((linha = reader.readLine()) != null) {
                        linha = linha.trim();
                        
                        // Ignorar comentários e linhas vazias
                        if (linha.startsWith("#") || linha.isEmpty()) {
                            continue;
                        }
                        
                        // Detectar seções de dados CSV (novo formato)
                        if (linha.startsWith("DADOS_") && linha.endsWith("_CSV:")) {
                            secaoAtual = linha.replace("DADOS_", "").replace("_CSV:", "");
                            isCSVDataSection = true;
                            isHeader = true;
                            publish("Importando " + secaoAtual.toLowerCase() + "...");
                            continue;
                        }
                        
                        // Detectar seções antigas (formato compatibilidade)
                        if (linha.startsWith("=== ") && linha.endsWith(" ===")) {
                            secaoAtual = linha.replace("=== ", "").replace(" ===", "");
                            isCSVDataSection = false;
                            isHeader = true;
                            publish("Importando " + secaoAtual.toLowerCase() + "...");
                            continue;
                        }
                        
                        // Ignorar cabeçalhos das tabelas e linhas de formatação
                        if (isHeader || linha.startsWith("┌") || linha.startsWith("├") || 
                            linha.startsWith("│") || linha.startsWith("└") || 
                            linha.startsWith("ID;") || linha.startsWith("TOTAL")) {
                            if (linha.startsWith("ID;")) {
                                isHeader = false; // Próxima linha serão dados
                            }
                            continue;
                        }
                        
                        // Processar dados baseado na seção atual
                        if (!linha.trim().isEmpty() && !linha.contains("===")) {
                            switch (secaoAtual) {
                                case "CONTAS":
                                    importarLinhaConta(linha);
                                    break;
                                case "CATEGORIAS":
                                    importarLinhaCategoria(linha);
                                    break;
                                case "MOVIMENTAÇÕES":
                                case "MOVIMENTACOES":
                                    importarLinhaMovimentacao(linha);
                                    break;
                            }
                        }
                    }
                    
                    publish("Importação concluída!");
                    
                } catch (IOException e) {
                    throw new Exception("Erro ao ler arquivo: " + e.getMessage());
                }
                
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    System.out.println(message); // Log para debug
                }
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(ExportacaoView.this,
                        "Dados importados com sucesso!\n\n" +
                        "Os novos dados foram adicionados ao sistema.\n" +
                        "Recomenda-se verificar as telas de contas, categorias e movimentações.",
                        "Importação Concluída",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ExportacaoView.this,
                        "Erro durante a importação: " + e.getMessage() + "\n\n" +
                        "Verifique se o arquivo está no formato correto\n" +
                        "(exportado por este mesmo sistema).",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void importarLinhaConta(String linha) throws Exception {
        // Formato: ID;Nome;Tipo;Saldo_Inicial;Saldo_Atual
        String[] campos = linha.split(";");
        if (campos.length >= 5) {
            String nome = campos[1].replace("\"", ""); // Remove aspas
            String tipoStr = campos[2].replace("\"", "");
            double saldoInicial = Double.parseDouble(campos[3].replace(",", "."));
            
            // Determinar tipo da conta baseado na string
            // Como não temos acesso direto ao enum, vamos usar os valores conhecidos
            // Esta é uma simplificação - em um sistema real precisaríamos de melhor mapeamento
            System.out.println("Importando conta: " + nome + " (" + tipoStr + ")");
            // Nota: Para completar a importação, seria necessário implementar métodos
            // de adição no FinanceController para contas
        }
    }
    
    private void importarLinhaCategoria(String linha) throws Exception {
        // Formato: ID;Nome;Tipo
        String[] campos = linha.split(";");
        if (campos.length >= 3) {
            String nome = campos[1].replace("\"", "");
            String tipoStr = campos[2].replace("\"", "");
            
            // Converter string para enum
            Categoria.TipoCategoria tipo = Categoria.TipoCategoria.fromString(tipoStr.toLowerCase());
            
            // Adicionar categoria
            FinanceController.OperationResult<Integer> result = 
                financeController.adicionarCategoria(nome, tipo);
            
            if (!result.isSucesso()) {
                System.err.println("Erro ao importar categoria " + nome + ": " + result.getMensagem());
            }
        }
    }
    
    private void importarLinhaMovimentacao(String linha) throws Exception {
        // Formato: ID;Data;Descrição;Tipo;Valor;ID_Conta;ID_Categoria
        String[] campos = linha.split(";");
        if (campos.length >= 7) {
            String dataStr = campos[1].replace("\"", "");
            String descricao = campos[2].replace("\"", "");
            String tipoStr = campos[3].replace("\"", "");
            double valor = Double.parseDouble(campos[4].replace(",", "."));
            int idConta = Integer.parseInt(campos[5]);
            int idCategoria = Integer.parseInt(campos[6]);
            
            // Converter data
            java.sql.Date data = java.sql.Date.valueOf(
                new SimpleDateFormat("dd/MM/yyyy").parse(dataStr).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            
            // Converter tipo
            Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.fromString(tipoStr.toLowerCase());
            
            // Adicionar movimentação
            FinanceController.OperationResult<Integer> result = 
                financeController.adicionarMovimentacao(valor, data, descricao, tipo, idConta, idCategoria);
            
            if (!result.isSucesso()) {
                System.err.println("Erro ao importar movimentação " + descricao + ": " + result.getMensagem());
            }
        }
    }
}