package util;

import model.Conta;
import model.Categoria;
import model.Movimentacao;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Utilitário melhorado para exportação com CSS separado e templates modularizados
 * Versão compatível com Java 8
 */
public class ExportUtilImproved {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    /**
     * Exporta dados para HTML com CSS separado e design responsivo
     */
    public static void exportarHTMLMelhorado(String filePath,
                                          List<Conta> contas, 
                                          List<Categoria> categorias, 
                                          List<Movimentacao> movimentacoes,
                                          String nomeUsuario, 
                                          String emailUsuario) throws IOException {
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(gerarHTMLCompleto(contas, categorias, movimentacoes, nomeUsuario, emailUsuario));
        }
    }
    
    private static String gerarHTMLCompleto(List<Conta> contas, 
                                         List<Categoria> categorias, 
                                         List<Movimentacao> movimentacoes,
                                         String nomeUsuario, 
                                         String emailUsuario) {
        
        StringBuilder html = new StringBuilder();
        
        // DOCTYPE e head
        html.append(gerarHTMLHead());
        
        // Body start
        html.append("<body>\n");
        
        // Header
        html.append(gerarHeader(nomeUsuario, emailUsuario));
        
        // Summary cards
        html.append(gerarSummaryCards(contas, movimentacoes));
        
        // Main content
        html.append("<div class=\"container\">\n");
        
        // Contas section
        if (contas != null && !contas.isEmpty()) {
            html.append(gerarSecaoContas(contas));
        }
        
        // Categorias section
        if (categorias != null && !categorias.isEmpty()) {
            html.append(gerarSecaoCategorias(categorias));
        }
        
        // Movimentações section
        if (movimentacoes != null && !movimentacoes.isEmpty()) {
            html.append(gerarSecaoMovimentacoes(movimentacoes));
        }
        
        html.append("</div>\n");
        
        // Footer
        html.append(gerarFooter());
        
        html.append("</body>\n</html>");
        
        return html.toString();
    }
    
    private static String gerarHTMLHead() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='pt-BR'>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("    <title>Finanza Desktop - Relatório Completo</title>\n");
        html.append("    <style>\n");
        html.append(lerArquivoCSS("base.css"));
        html.append("\n");
        html.append(lerArquivoCSS("components.css"));
        html.append("\n");
        html.append(lerArquivoCSS("reports.css"));
        html.append("\n    </style>\n");
        html.append("</head>\n");
        return html.toString();
    }
    
    private static String lerArquivoCSS(String nomeArquivo) {
        try {
            // Caminho relativo para os arquivos CSS
            String basePath = System.getProperty("user.dir");
            String cssPath = basePath + "/src/view/styles/" + nomeArquivo;
            return Files.readString(Paths.get(cssPath));
        } catch (IOException e) {
            // Fallback para CSS inline básico se não conseguir ler os arquivos
            return getFallbackCSS();
        } catch (NoSuchMethodError e) {
            // Fallback para Java 8 compatibilidade
            return getFallbackCSS();
        }
    }
    
    private static String getFallbackCSS() {
        StringBuilder css = new StringBuilder();
        css.append(":root {\n");
        css.append("    --primary-color: #667eea;\n");
        css.append("    --secondary-color: #764ba2;\n");
        css.append("    --success-color: #28a745;\n");
        css.append("    --danger-color: #dc3545;\n");
        css.append("    --background-color: #f5f5f5;\n");
        css.append("    --card-background: #ffffff;\n");
        css.append("    --text-color: #333333;\n");
        css.append("    --border-color: #dee2e6;\n");
        css.append("    --spacing-md: 15px;\n");
        css.append("    --spacing-lg: 20px;\n");
        css.append("    --spacing-xl: 30px;\n");
        css.append("    --border-radius: 8px;\n");
        css.append("    --shadow: 0 2px 10px rgba(0,0,0,0.1);\n");
        css.append("}\n\n");
        
        css.append("body {\n");
        css.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
        css.append("    margin: 0;\n");
        css.append("    padding: var(--spacing-lg);\n");
        css.append("    background-color: var(--background-color);\n");
        css.append("    line-height: 1.6;\n");
        css.append("    color: var(--text-color);\n");
        css.append("}\n\n");
        
        css.append(".container { max-width: 1200px; margin: 0 auto; }\n");
        css.append(".report-header {\n");
        css.append("    background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);\n");
        css.append("    color: white; padding: var(--spacing-xl); border-radius: var(--border-radius);\n");
        css.append("    text-align: center; margin-bottom: var(--spacing-xl);\n");
        css.append("}\n");
        css.append(".report-section {\n");
        css.append("    background: var(--card-background); margin: var(--spacing-lg) 0;\n");
        css.append("    padding: var(--spacing-xl); border-radius: var(--border-radius);\n");
        css.append("    box-shadow: var(--shadow);\n");
        css.append("}\n");
        css.append(".report-table { width: 100%; border-collapse: collapse; }\n");
        css.append(".report-table th {\n");
        css.append("    background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);\n");
        css.append("    color: white; padding: var(--spacing-md); text-align: left;\n");
        css.append("}\n");
        css.append(".report-table td { padding: var(--spacing-md); border-bottom: 1px solid var(--border-color); }\n");
        css.append(".valor-positivo { color: var(--success-color); font-weight: bold; }\n");
        css.append(".valor-negativo { color: var(--danger-color); font-weight: bold; }\n");
        css.append(".total-row { background: var(--primary-color) !important; color: white !important; font-weight: bold; }\n");
        css.append(".summary-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: var(--spacing-lg); margin-bottom: var(--spacing-xl); }\n");
        css.append(".summary-card { background: var(--card-background); padding: var(--spacing-lg); border-radius: var(--border-radius); box-shadow: var(--shadow); text-align: center; border-top: 4px solid var(--primary-color); }\n");
        css.append(".badge { display: inline-block; padding: 0.25em 0.5em; font-size: 0.75em; font-weight: 600; border-radius: 4px; }\n");
        css.append(".badge-success { background-color: var(--success-color); color: white; }\n");
        css.append(".badge-danger { background-color: var(--danger-color); color: white; }\n");
        css.append(".receita-row { background-color: rgba(40, 167, 69, 0.05); border-left: 4px solid var(--success-color); }\n");
        css.append(".despesa-row { background-color: rgba(220, 53, 69, 0.05); border-left: 4px solid var(--danger-color); }\n");
        
        return css.toString();
    }
    
    private static String gerarHeader(String nomeUsuario, String emailUsuario) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='report-header'>\n");
        html.append("    <h1>📊 Finanza Desktop</h1>\n");
        html.append("    <p>Relatório Completo de Dados Financeiros</p>\n");
        html.append("    <p>Data: ").append(DATE_FORMAT.format(new java.util.Date()));
        html.append(" | Usuário: ").append(nomeUsuario).append(" (").append(emailUsuario).append(")</p>\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private static String gerarSummaryCards(List<Conta> contas, List<Movimentacao> movimentacoes) {
        double totalContas = 0;
        double totalReceitas = 0;
        double totalDespesas = 0;
        int numMovimentacoes = 0;
        
        if (contas != null) {
            for (Conta conta : contas) {
                totalContas += conta.getSaldoAtual();
            }
        }
        
        if (movimentacoes != null) {
            numMovimentacoes = movimentacoes.size();
            for (Movimentacao mov : movimentacoes) {
                if (mov.getTipo() == Movimentacao.TipoMovimentacao.RECEITA) {
                    totalReceitas += mov.getValor();
                } else {
                    totalDespesas += mov.getValor();
                }
            }
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<div class='summary-cards'>\n");
        html.append("    <div class='summary-card'>\n");
        html.append("        <h3>Saldo Total</h3>\n");
        html.append("        <div class='value ").append(totalContas >= 0 ? "valor-positivo" : "valor-negativo");
        html.append("'>").append(CURRENCY_FORMAT.format(totalContas)).append("</div>\n");
        html.append("        <div class='change'>").append(contas != null ? contas.size() : 0).append(" contas</div>\n");
        html.append("    </div>\n");
        html.append("    <div class='summary-card receita'>\n");
        html.append("        <h3>Total Receitas</h3>\n");
        html.append("        <div class='value valor-positivo'>").append(CURRENCY_FORMAT.format(totalReceitas)).append("</div>\n");
        html.append("        <div class='change'>Este período</div>\n");
        html.append("    </div>\n");
        html.append("    <div class='summary-card despesa'>\n");
        html.append("        <h3>Total Despesas</h3>\n");
        html.append("        <div class='value valor-negativo'>").append(CURRENCY_FORMAT.format(totalDespesas)).append("</div>\n");
        html.append("        <div class='change'>Este período</div>\n");
        html.append("    </div>\n");
        html.append("    <div class='summary-card'>\n");
        html.append("        <h3>Movimentações</h3>\n");
        html.append("        <div class='value'>").append(numMovimentacoes).append("</div>\n");
        html.append("        <div class='change'>Total de registros</div>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private static String gerarSecaoContas(List<Conta> contas) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='report-section'>\n");
        html.append("    <h2>💳 Resumo de Contas</h2>\n");
        html.append("    <div class='table-responsive'>\n");
        html.append("        <table class='report-table'>\n");
        html.append("            <thead>\n");
        html.append("                <tr>\n");
        html.append("                    <th>ID</th>\n");
        html.append("                    <th>Nome da Conta</th>\n");
        html.append("                    <th>Tipo</th>\n");
        html.append("                    <th>Saldo Inicial</th>\n");
        html.append("                    <th>Saldo Atual</th>\n");
        html.append("                    <th>Variação</th>\n");
        html.append("                </tr>\n");
        html.append("            </thead>\n");
        html.append("            <tbody>\n");
        
        double totalInicial = 0;
        double totalAtual = 0;
        
        for (Conta conta : contas) {
            double variacao = conta.getSaldoAtual() - conta.getSaldoInicial();
            String classeValor = conta.getSaldoAtual() >= 0 ? "valor-positivo" : "valor-negativo";
            String classeVariacao = variacao >= 0 ? "valor-positivo" : "valor-negativo";
            
            totalInicial += conta.getSaldoInicial();
            totalAtual += conta.getSaldoAtual();
            
            html.append("                <tr>\n");
            html.append("                    <td>").append(conta.getId()).append("</td>\n");
            html.append("                    <td>").append(conta.getNome()).append("</td>\n");
            html.append("                    <td>").append(conta.getTipo().getDescricao()).append("</td>\n");
            html.append("                    <td>").append(CURRENCY_FORMAT.format(conta.getSaldoInicial())).append("</td>\n");
            html.append("                    <td class='").append(classeValor).append("'>").append(CURRENCY_FORMAT.format(conta.getSaldoAtual())).append("</td>\n");
            html.append("                    <td class='").append(classeVariacao).append("'>").append(CURRENCY_FORMAT.format(variacao)).append("</td>\n");
            html.append("                </tr>\n");
        }
        
        // Linha de total
        double variacaoTotal = totalAtual - totalInicial;
        String classeVarTotal = variacaoTotal >= 0 ? "valor-positivo" : "valor-negativo";
        
        html.append("                <tr class='total-row'>\n");
        html.append("                    <td colspan='3'>TOTAL GERAL</td>\n");
        html.append("                    <td>").append(CURRENCY_FORMAT.format(totalInicial)).append("</td>\n");
        html.append("                    <td>").append(CURRENCY_FORMAT.format(totalAtual)).append("</td>\n");
        html.append("                    <td class='").append(classeVarTotal).append("'>").append(CURRENCY_FORMAT.format(variacaoTotal)).append("</td>\n");
        html.append("                </tr>\n");
        
        html.append("            </tbody>\n");
        html.append("        </table>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        
        return html.toString();
    }
    
    private static String gerarSecaoCategorias(List<Categoria> categorias) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='report-section'>\n");
        html.append("    <h2>📂 Resumo de Categorias</h2>\n");
        html.append("    <div class='table-responsive'>\n");
        html.append("        <table class='report-table'>\n");
        html.append("            <thead>\n");
        html.append("                <tr>\n");
        html.append("                    <th>ID</th>\n");
        html.append("                    <th>Nome da Categoria</th>\n");
        html.append("                    <th>Tipo</th>\n");
        html.append("                    <th>Status</th>\n");
        html.append("                </tr>\n");
        html.append("            </thead>\n");
        html.append("            <tbody>\n");
        
        for (Categoria categoria : categorias) {
            String classeRow = categoria.getTipo() == Categoria.TipoCategoria.RECEITA ? "receita-row" : "despesa-row";
            String badgeClass = categoria.getTipo() == Categoria.TipoCategoria.RECEITA ? "badge-success" : "badge-danger";
            
            html.append("                <tr class='").append(classeRow).append("'>\n");
            html.append("                    <td>").append(categoria.getId()).append("</td>\n");
            html.append("                    <td>").append(categoria.getNome()).append("</td>\n");
            html.append("                    <td><span class='badge ").append(badgeClass).append("'>").append(categoria.getTipo().getDescricao()).append("</span></td>\n");
            html.append("                    <td><span class='badge badge-success'>Ativo</span></td>\n");
            html.append("                </tr>\n");
        }
        
        html.append("            </tbody>\n");
        html.append("        </table>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        
        return html.toString();
    }
    
    private static String gerarSecaoMovimentacoes(List<Movimentacao> movimentacoes) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='report-section'>\n");
        html.append("    <h2>💰 Resumo de Movimentações</h2>\n");
        html.append("    <div class='table-responsive'>\n");
        html.append("        <table class='report-table'>\n");
        html.append("            <thead>\n");
        html.append("                <tr>\n");
        html.append("                    <th>Data</th>\n");
        html.append("                    <th>Descrição</th>\n");
        html.append("                    <th>Tipo</th>\n");
        html.append("                    <th>Valor</th>\n");
        html.append("                    <th>Conta</th>\n");
        html.append("                    <th>Categoria</th>\n");
        html.append("                </tr>\n");
        html.append("            </thead>\n");
        html.append("            <tbody>\n");
        
        double totalReceitas = 0;
        double totalDespesas = 0;
        
        for (Movimentacao mov : movimentacoes) {
            String classeRow = mov.getTipo() == Movimentacao.TipoMovimentacao.RECEITA ? "receita-row" : "despesa-row";
            String classeValor = mov.getTipo() == Movimentacao.TipoMovimentacao.RECEITA ? "valor-positivo" : "valor-negativo";
            String badgeClass = mov.getTipo() == Movimentacao.TipoMovimentacao.RECEITA ? "badge-success" : "badge-danger";
            
            if (mov.getTipo() == Movimentacao.TipoMovimentacao.RECEITA) {
                totalReceitas += mov.getValor();
            } else {
                totalDespesas += mov.getValor();
            }
            
            html.append("                <tr class='").append(classeRow).append("'>\n");
            html.append("                    <td>").append(DATE_FORMAT.format(mov.getData())).append("</td>\n");
            html.append("                    <td>").append(mov.getDescricao() != null ? mov.getDescricao() : "Sem descrição").append("</td>\n");
            html.append("                    <td><span class='badge ").append(badgeClass).append("'>").append(mov.getTipo().getDescricao()).append("</span></td>\n");
            html.append("                    <td class='").append(classeValor).append("'>").append(CURRENCY_FORMAT.format(mov.getValor())).append("</td>\n");
            html.append("                    <td>").append(mov.getNomeConta() != null ? mov.getNomeConta() : "N/A").append("</td>\n");
            html.append("                    <td>").append(mov.getNomeCategoria() != null ? mov.getNomeCategoria() : "N/A").append("</td>\n");
            html.append("                </tr>\n");
        }
        
        // Linhas de totais
        html.append("                <tr class='total-row'>\n");
        html.append("                    <td colspan='3'>TOTAL RECEITAS</td>\n");
        html.append("                    <td class='valor-positivo'>").append(CURRENCY_FORMAT.format(totalReceitas)).append("</td>\n");
        html.append("                    <td colspan='2'></td>\n");
        html.append("                </tr>\n");
        html.append("                <tr class='total-row'>\n");
        html.append("                    <td colspan='3'>TOTAL DESPESAS</td>\n");
        html.append("                    <td class='valor-negativo'>").append(CURRENCY_FORMAT.format(totalDespesas)).append("</td>\n");
        html.append("                    <td colspan='2'></td>\n");
        html.append("                </tr>\n");
        html.append("                <tr class='total-row'>\n");
        html.append("                    <td colspan='3'>SALDO LÍQUIDO</td>\n");
        html.append("                    <td class='").append((totalReceitas - totalDespesas) >= 0 ? "valor-positivo" : "valor-negativo");
        html.append("'>").append(CURRENCY_FORMAT.format(totalReceitas - totalDespesas)).append("</td>\n");
        html.append("                    <td colspan='2'></td>\n");
        html.append("                </tr>\n");
        
        html.append("            </tbody>\n");
        html.append("        </table>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        
        return html.toString();
    }
    
    private static String gerarFooter() {
        StringBuilder html = new StringBuilder();
        html.append("<div class='report-footer'>\n");
        html.append("    <p>Relatório gerado automaticamente pelo Finanza Desktop</p>\n");
        html.append("    <p>Para melhor organização, imprima este relatório ou salve como PDF</p>\n");
        html.append("    <p>Sistema desenvolvido seguindo as melhores práticas de engenharia de software</p>\n");
        html.append("</div>\n");
        return html.toString();
    }
}