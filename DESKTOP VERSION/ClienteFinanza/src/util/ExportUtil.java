package util;

import model.Conta;
import model.Categoria;
import model.Movimentacao;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Utilitário para formatação e exportação de dados
 */
public class ExportUtil {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    /**
     * Exporta dados para CSV com formatação melhorada
     */
    public static void exportarCSVMelhorado(FileWriter writer, 
                                          List<Conta> contas, 
                                          List<Categoria> categorias, 
                                          List<Movimentacao> movimentacoes,
                                          String nomeUsuario, 
                                          String emailUsuario) throws IOException {
        
        // Cabeçalho do arquivo
        writer.write("==============================================\n");
        writer.write("       FINANZA DESKTOP - RELATÓRIO DE DADOS  \n");
        writer.write("==============================================\n");
        writer.write("Data de Exportação: " + DATE_FORMAT.format(new java.util.Date()) + "\n");
        writer.write("Usuário: " + nomeUsuario + " (" + emailUsuario + ")\n");
        writer.write("==============================================\n\n");
        
        // Exportar contas se fornecidas
        if (contas != null && !contas.isEmpty()) {
            exportarContasFormatadas(writer, contas);
        }
        
        // Exportar categorias se fornecidas
        if (categorias != null && !categorias.isEmpty()) {
            exportarCategoriasFormatadas(writer, categorias);
        }
        
        // Exportar movimentações se fornecidas
        if (movimentacoes != null && !movimentacoes.isEmpty()) {
            exportarMovimentacoesFormatadas(writer, movimentacoes);
        }
        
        // Rodapé
        writer.write("\n==============================================\n");
        writer.write("         Exportação concluída com sucesso!    \n");
        writer.write("==============================================\n");
    }
    
    /**
     * Exporta contas com formatação de tabela
     */
    private static void exportarContasFormatadas(FileWriter writer, List<Conta> contas) throws IOException {
        writer.write("┌─────────────────────────────────────────────────────────┐\n");
        writer.write("│                    RESUMO DE CONTAS                     │\n");
        writer.write("├─────┬──────────────────┬─────────────┬─────────────────┤\n");
        writer.write("│ ID  │ Nome da Conta    │ Tipo        │ Saldo Atual     │\n");
        writer.write("├─────┼──────────────────┼─────────────┼─────────────────┤\n");
        
        double totalSaldo = 0.0;
        for (Conta conta : contas) {
            String nome = String.format("%-16s", 
                conta.getNome().length() > 16 ? conta.getNome().substring(0, 13) + "..." : conta.getNome());
            String tipo = String.format("%-11s", conta.getTipo().getDescricao());
            String saldo = String.format("%15s", CURRENCY_FORMAT.format(conta.getSaldoAtual()));
            
            writer.write(String.format("│ %-3d │ %s │ %s │ %s │\n", 
                conta.getId(), nome, tipo, saldo));
            totalSaldo += conta.getSaldoAtual();
        }
        
        writer.write("├─────┴──────────────────┴─────────────┼─────────────────┤\n");
        writer.write(String.format("│ TOTAL GERAL:                           │ %15s │\n", 
            CURRENCY_FORMAT.format(totalSaldo)));
        writer.write("└────────────────────────────────────────┴─────────────────┘\n\n");
        
        // Dados detalhados em CSV para importação
        writer.write("DADOS_CONTAS_CSV:\n");
        writer.write("ID;Nome;Tipo;Saldo_Inicial;Saldo_Atual\n");
        for (Conta conta : contas) {
            writer.write(String.format("%d;\"%s\";\"%s\";%.2f;%.2f\n",
                conta.getId(),
                conta.getNome().replace("\"", "\"\""),
                conta.getTipo().getDescricao(),
                conta.getSaldoInicial(),
                conta.getSaldoAtual()));
        }
        writer.write("\n");
    }
    
    /**
     * Exporta categorias com formatação de tabela
     */
    private static void exportarCategoriasFormatadas(FileWriter writer, List<Categoria> categorias) throws IOException {
        writer.write("┌─────────────────────────────────────────────────────────┐\n");
        writer.write("│                  RESUMO DE CATEGORIAS                   │\n");
        writer.write("├─────┬──────────────────────────────┬─────────────────────┤\n");
        writer.write("│ ID  │ Nome da Categoria            │ Tipo                │\n");
        writer.write("├─────┼──────────────────────────────┼─────────────────────┤\n");
        
        for (Categoria categoria : categorias) {
            String nome = String.format("%-28s", 
                categoria.getNome().length() > 28 ? categoria.getNome().substring(0, 25) + "..." : categoria.getNome());
            String tipo = String.format("%-19s", categoria.getTipo().getDescricao());
            
            writer.write(String.format("│ %-3d │ %s │ %s │\n", 
                categoria.getId(), nome, tipo));
        }
        
        writer.write("└─────┴──────────────────────────────┴─────────────────────┘\n\n");
        
        // Dados detalhados em CSV para importação
        writer.write("DADOS_CATEGORIAS_CSV:\n");
        writer.write("ID;Nome;Tipo\n");
        for (Categoria categoria : categorias) {
            writer.write(String.format("%d;\"%s\";\"%s\"\n",
                categoria.getId(),
                categoria.getNome().replace("\"", "\"\""),
                categoria.getTipo().getDescricao()));
        }
        writer.write("\n");
    }
    
    /**
     * Exporta movimentações com formatação de tabela
     */
    private static void exportarMovimentacoesFormatadas(FileWriter writer, List<Movimentacao> movimentacoes) throws IOException {
        writer.write("┌───────────────────────────────────────────────────────────────────────────────────┐\n");
        writer.write("│                           RESUMO DE MOVIMENTAÇÕES                                │\n");
        writer.write("├────────────┬────────────────────┬─────────────┬────────────────┬─────────────────┤\n");
        writer.write("│ Data       │ Descrição          │ Tipo        │ Valor          │ Conta/Categoria │\n");
        writer.write("├────────────┼────────────────────┼─────────────┼────────────────┼─────────────────┤\n");
        
        double totalReceitas = 0.0;
        double totalDespesas = 0.0;
        
        for (Movimentacao mov : movimentacoes) {
            String data = DATE_FORMAT.format(mov.getData());
            String descricao = String.format("%-18s", 
                mov.getDescricao() != null && mov.getDescricao().length() > 18 ? 
                mov.getDescricao().substring(0, 15) + "..." : 
                (mov.getDescricao() != null ? mov.getDescricao() : ""));
            String tipo = String.format("%-11s", mov.getTipo().getDescricao());
            String valor = String.format("%14s", CURRENCY_FORMAT.format(mov.getValor()));
            String contaCategoria = String.format("%-15s", "C:" + mov.getIdConta() + "/Cat:" + 
                (mov.getNomeCategoria() != null ? mov.getNomeCategoria() : "ID:" + mov.getIdCategoria()));
            
            writer.write(String.format("│ %s │ %s │ %s │ %s │ %s │\n", 
                data, descricao, tipo, valor, contaCategoria));
                
            if (mov.getTipo().getDescricao().toLowerCase().contains("receita")) {
                totalReceitas += mov.getValor();
            } else {
                totalDespesas += mov.getValor();
            }
        }
        
        writer.write("├────────────┴────────────────────┴─────────────┼────────────────┼─────────────────┤\n");
        writer.write(String.format("│ TOTAL RECEITAS:                               │ %14s │                 │\n", 
            CURRENCY_FORMAT.format(totalReceitas)));
        writer.write(String.format("│ TOTAL DESPESAS:                               │ %14s │                 │\n", 
            CURRENCY_FORMAT.format(totalDespesas)));
        writer.write("├───────────────────────────────────────────────┼────────────────┼─────────────────┤\n");
        writer.write(String.format("│ SALDO LÍQUIDO:                                │ %14s │                 │\n", 
            CURRENCY_FORMAT.format(totalReceitas - totalDespesas)));
        writer.write("└───────────────────────────────────────────────┴────────────────┴─────────────────┘\n\n");
        
        // Dados detalhados em CSV para importação
        writer.write("DADOS_MOVIMENTACOES_CSV:\n");
        writer.write("ID;Data;Descrição;Tipo;Valor;ID_Conta;ID_Categoria\n");
        for (Movimentacao mov : movimentacoes) {
            writer.write(String.format("%d;\"%s\";\"%s\";\"%s\";%.2f;%d;%d\n",
                mov.getId(),
                DATE_FORMAT.format(mov.getData()),
                mov.getDescricao() != null ? mov.getDescricao().replace("\"", "\"\"") : "",
                mov.getTipo().getDescricao(),
                mov.getValor(),
                mov.getIdConta(),
                mov.getIdCategoria()));
        }
        writer.write("\n");
    }
    
    /**
     * Gera relatório HTML com tabelas bonitas
     */
    public static void exportarHTML(FileWriter writer, 
                                   List<Conta> contas, 
                                   List<Categoria> categorias, 
                                   List<Movimentacao> movimentacoes,
                                   String nomeUsuario, 
                                   String emailUsuario) throws IOException {
        
        writer.write("<!DOCTYPE html>\n");
        writer.write("<html lang='pt-BR'>\n");
        writer.write("<head>\n");
        writer.write("    <meta charset='UTF-8'>\n");
        writer.write("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        writer.write("    <title>Finanza Desktop - Relatório de Dados</title>\n");
        writer.write("    <style>\n");
        writer.write("        body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        writer.write("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; text-align: center; margin-bottom: 30px; }\n");
        writer.write("        .header h1 { margin: 0; font-size: 2.5em; }\n");
        writer.write("        .header p { margin: 5px 0; opacity: 0.9; }\n");
        writer.write("        .section { background: white; margin: 20px 0; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        writer.write("        .section h2 { color: #333; border-bottom: 3px solid #667eea; padding-bottom: 10px; margin-top: 0; }\n");
        writer.write("        table { width: 100%; border-collapse: collapse; margin-top: 15px; }\n");
        writer.write("        th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 12px; text-align: left; font-weight: bold; }\n");
        writer.write("        td { padding: 12px; border-bottom: 1px solid #eee; }\n");
        writer.write("        tr:nth-child(even) { background-color: #f9f9f9; }\n");
        writer.write("        tr:hover { background-color: #f0f0f0; }\n");
        writer.write("        .valor-positivo { color: #28a745; font-weight: bold; }\n");
        writer.write("        .valor-negativo { color: #dc3545; font-weight: bold; }\n");
        writer.write("        .total-row { background-color: #667eea !important; color: white; font-weight: bold; }\n");
        writer.write("        .total-row td { border-bottom: none; }\n");
        writer.write("        .receita { background-color: #d4edda; }\n");
        writer.write("        .despesa { background-color: #f8d7da; }\n");
        writer.write("        .footer { text-align: center; margin-top: 30px; color: #666; font-size: 0.9em; }\n");
        writer.write("    </style>\n");
        writer.write("</head>\n");
        writer.write("<body>\n");
        
        // Cabeçalho
        writer.write("    <div class='header'>\n");
        writer.write("        <h1>📊 Finanza Desktop</h1>\n");
        writer.write("        <p>Relatório Completo de Dados Financeiros</p>\n");
        writer.write("        <p>Data: " + DATE_FORMAT.format(new java.util.Date()) + " | Usuário: " + nomeUsuario + " (" + emailUsuario + ")</p>\n");
        writer.write("    </div>\n");
        
        // Contas
        if (contas != null && !contas.isEmpty()) {
            exportarContasHTML(writer, contas);
        }
        
        // Categorias
        if (categorias != null && !categorias.isEmpty()) {
            exportarCategoriasHTML(writer, categorias);
        }
        
        // Movimentações
        if (movimentacoes != null && !movimentacoes.isEmpty()) {
            exportarMovimentacoesHTML(writer, movimentacoes);
        }
        
        // Rodapé
        writer.write("    <div class='footer'>\n");
        writer.write("        <p>Relatório gerado automaticamente pelo Finanza Desktop</p>\n");
        writer.write("        <p>Para melhor organização, imprima este relatório ou salve como PDF</p>\n");
        writer.write("    </div>\n");
        writer.write("</body>\n");
        writer.write("</html>\n");
    }
    
    private static void exportarContasHTML(FileWriter writer, List<Conta> contas) throws IOException {
        writer.write("    <div class='section'>\n");
        writer.write("        <h2>💳 Resumo de Contas</h2>\n");
        writer.write("        <table>\n");
        writer.write("            <thead>\n");
        writer.write("                <tr><th>ID</th><th>Nome da Conta</th><th>Tipo</th><th>Saldo Inicial</th><th>Saldo Atual</th></tr>\n");
        writer.write("            </thead>\n");
        writer.write("            <tbody>\n");
        
        double totalSaldo = 0.0;
        for (Conta conta : contas) {
            String classeValor = conta.getSaldoAtual() >= 0 ? "valor-positivo" : "valor-negativo";
            writer.write(String.format("                <tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td class='%s'>%s</td></tr>\n",
                conta.getId(),
                conta.getNome(),
                conta.getTipo().getDescricao(),
                CURRENCY_FORMAT.format(conta.getSaldoInicial()),
                classeValor,
                CURRENCY_FORMAT.format(conta.getSaldoAtual())));
            totalSaldo += conta.getSaldoAtual();
        }
        
        String classeTotalValor = totalSaldo >= 0 ? "valor-positivo" : "valor-negativo";
        writer.write(String.format("                <tr class='total-row'><td colspan='4'>TOTAL GERAL</td><td class='%s'>%s</td></tr>\n",
            classeTotalValor, CURRENCY_FORMAT.format(totalSaldo)));
        
        writer.write("            </tbody>\n");
        writer.write("        </table>\n");
        writer.write("    </div>\n");
    }
    
    private static void exportarCategoriasHTML(FileWriter writer, List<Categoria> categorias) throws IOException {
        writer.write("    <div class='section'>\n");
        writer.write("        <h2>📂 Resumo de Categorias</h2>\n");
        writer.write("        <table>\n");
        writer.write("            <thead>\n");
        writer.write("                <tr><th>ID</th><th>Nome da Categoria</th><th>Tipo</th></tr>\n");
        writer.write("            </thead>\n");
        writer.write("            <tbody>\n");
        
        for (Categoria categoria : categorias) {
            String classeRow = categoria.getTipo().getDescricao().toLowerCase().contains("receita") ? "receita" : "despesa";
            writer.write(String.format("                <tr class='%s'><td>%d</td><td>%s</td><td>%s</td></tr>\n",
                classeRow,
                categoria.getId(),
                categoria.getNome(),
                categoria.getTipo().getDescricao()));
        }
        
        writer.write("            </tbody>\n");
        writer.write("        </table>\n");
        writer.write("    </div>\n");
    }
    
    private static void exportarMovimentacoesHTML(FileWriter writer, List<Movimentacao> movimentacoes) throws IOException {
        writer.write("    <div class='section'>\n");
        writer.write("        <h2>💰 Resumo de Movimentações</h2>\n");
        writer.write("        <table>\n");
        writer.write("            <thead>\n");
        writer.write("                <tr><th>Data</th><th>Descrição</th><th>Tipo</th><th>Valor</th><th>Conta</th><th>Categoria</th></tr>\n");
        writer.write("            </thead>\n");
        writer.write("            <tbody>\n");
        
        double totalReceitas = 0.0;
        double totalDespesas = 0.0;
        
        for (Movimentacao mov : movimentacoes) {
            boolean isReceita = mov.getTipo().getDescricao().toLowerCase().contains("receita");
            String classeRow = isReceita ? "receita" : "despesa";
            String classeValor = isReceita ? "valor-positivo" : "valor-negativo";
            
            writer.write(String.format("                <tr class='%s'><td>%s</td><td>%s</td><td>%s</td><td class='%s'>%s</td><td>%d</td><td>%s</td></tr>\n",
                classeRow,
                DATE_FORMAT.format(mov.getData()),
                mov.getDescricao() != null ? mov.getDescricao() : "",
                mov.getTipo().getDescricao(),
                classeValor,
                CURRENCY_FORMAT.format(mov.getValor()),
                mov.getIdConta(),
                mov.getNomeCategoria() != null ? mov.getNomeCategoria() : "ID:" + mov.getIdCategoria()));
                
            if (isReceita) {
                totalReceitas += mov.getValor();
            } else {
                totalDespesas += mov.getValor();
            }
        }
        
        // Totais
        writer.write("                <tr class='total-row'><td colspan='3'>TOTAL RECEITAS</td><td class='valor-positivo'>" + CURRENCY_FORMAT.format(totalReceitas) + "</td><td colspan='2'></td></tr>\n");
        writer.write("                <tr class='total-row'><td colspan='3'>TOTAL DESPESAS</td><td class='valor-negativo'>" + CURRENCY_FORMAT.format(totalDespesas) + "</td><td colspan='2'></td></tr>\n");
        
        double saldoLiquido = totalReceitas - totalDespesas;
        String classeSaldo = saldoLiquido >= 0 ? "valor-positivo" : "valor-negativo";
        writer.write("                <tr class='total-row'><td colspan='3'>SALDO LÍQUIDO</td><td class='" + classeSaldo + "'>" + CURRENCY_FORMAT.format(saldoLiquido) + "</td><td colspan='2'></td></tr>\n");
        
        writer.write("            </tbody>\n");
        writer.write("        </table>\n");
        writer.write("    </div>\n");
    }
}