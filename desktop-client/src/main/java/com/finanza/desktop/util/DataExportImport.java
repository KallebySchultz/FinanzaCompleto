package com.finanza.desktop.util;

import com.finanza.desktop.controller.FinanceController;
import com.finanza.desktop.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilitário para importação e exportação de dados
 * Suporte a CSV e JSON para integração com aplicativo móvel
 */
public class DataExportImport {
    private FinanceController financeController;
    private Gson gson;
    
    public DataExportImport(FinanceController financeController) {
        this.financeController = financeController;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    /**
     * Exporta todos os dados do usuário para JSON
     */
    public boolean exportarParaJSON(String caminhoArquivo) {
        try {
            Map<String, Object> dadosExportacao = new HashMap<>();
            
            // Dados básicos
            dadosExportacao.put("dataExportacao", new Date().getTime());
            dadosExportacao.put("versao", "1.0");
            
            // Dados financeiros
            dadosExportacao.put("contas", financeController.listarContas());
            dadosExportacao.put("lancamentos", financeController.listarLancamentos());
            dadosExportacao.put("categorias", financeController.listarCategorias());
            dadosExportacao.put("resumoFinanceiro", financeController.calcularResumoFinanceiro());
            
            // Escrever arquivo
            try (FileWriter writer = new FileWriter(caminhoArquivo)) {
                gson.toJson(dadosExportacao, writer);
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao exportar dados: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporta lançamentos para CSV
     */
    public boolean exportarLancamentosCSV(String caminhoArquivo) {
        try {
            List<Lancamento> lancamentos = financeController.listarLancamentos();
            List<Conta> contas = financeController.listarContas();
            List<Categoria> categorias = financeController.listarCategorias();
            
            if (lancamentos == null) {
                return false;
            }
            
            // Criar mapas para lookup
            Map<Integer, String> nomeContas = new HashMap<>();
            Map<Integer, String> nomeCategorias = new HashMap<>();
            
            if (contas != null) {
                for (Conta conta : contas) {
                    nomeContas.put(conta.getId(), conta.getNome());
                }
            }
            
            if (categorias != null) {
                for (Categoria categoria : categorias) {
                    nomeCategorias.put(categoria.getId(), categoria.getNome());
                }
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
                // Cabeçalho CSV
                writer.println("Data,Descrição,Valor,Tipo,Conta,Categoria");
                
                // Dados
                for (Lancamento lancamento : lancamentos) {
                    String data = sdf.format(new Date(lancamento.getData()));
                    String conta = nomeContas.getOrDefault(lancamento.getContaId(), "N/A");
                    String categoria = nomeCategorias.getOrDefault(lancamento.getCategoriaId(), "N/A");
                    
                    writer.printf("%s,\"%s\",%.2f,%s,\"%s\",\"%s\"%n",
                        data,
                        lancamento.getDescricao().replace("\"", "\"\""),
                        lancamento.getValor(),
                        lancamento.getTipo(),
                        conta.replace("\"", "\"\""),
                        categoria.replace("\"", "\"\"")
                    );
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao exportar CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporta resumo financeiro para texto
     */
    public boolean exportarRelatorioTXT(String caminhoArquivo) {
        try {
            Map<String, Double> resumo = financeController.calcularResumoFinanceiro();
            List<Conta> contas = financeController.listarContas();
            List<Lancamento> lancamentos = financeController.listarLancamentos();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                
                writer.println("===============================================");
                writer.println("           RELATÓRIO FINANCEIRO FINANZA       ");
                writer.println("===============================================");
                writer.println("Data do relatório: " + sdf.format(new Date()));
                writer.println();
                
                // Resumo geral
                writer.println("RESUMO FINANCEIRO:");
                if (resumo != null) {
                    writer.printf("Total de Receitas: R$ %.2f%n", resumo.get("receitas"));
                    writer.printf("Total de Despesas: R$ %.2f%n", resumo.get("despesas"));
                    writer.printf("Saldo Atual: R$ %.2f%n", resumo.get("saldo"));
                }
                writer.println();
                
                // Contas
                writer.println("CONTAS:");
                if (contas != null) {
                    for (Conta conta : contas) {
                        double saldo = financeController.calcularSaldoConta(conta.getId());
                        writer.printf("- %s: R$ %.2f%n", conta.getNome(), saldo);
                    }
                } else {
                    writer.println("Nenhuma conta cadastrada.");
                }
                writer.println();
                
                // Últimas transações
                writer.println("ÚLTIMAS TRANSAÇÕES:");
                if (lancamentos != null && !lancamentos.isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    int count = 0;
                    for (Lancamento lancamento : lancamentos) {
                        if (count >= 10) break;
                        String data = dateFormat.format(new Date(lancamento.getData()));
                        writer.printf("%s - %s: R$ %.2f (%s)%n",
                            data,
                            lancamento.getDescricao(),
                            lancamento.getValor(),
                            lancamento.getTipo().toUpperCase()
                        );
                        count++;
                    }
                } else {
                    writer.println("Nenhuma transação encontrada.");
                }
                
                writer.println();
                writer.println("===============================================");
                writer.println("    Relatório gerado pelo Finanza Desktop     ");
                writer.println("===============================================");
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao exportar relatório: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cria um backup completo do banco de dados em JSON
     */
    public boolean criarBackup(String caminhoArquivo) {
        String nomeArquivo = caminhoArquivo;
        if (!nomeArquivo.endsWith(".json")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            nomeArquivo = "backup_finanza_" + sdf.format(new Date()) + ".json";
        }
        
        return exportarParaJSON(nomeArquivo);
    }
    
    /**
     * Valida se um arquivo JSON é um backup válido do Finanza
     */
    public boolean validarBackup(String caminhoArquivo) {
        try (FileReader reader = new FileReader(caminhoArquivo)) {
            Map<?, ?> dados = gson.fromJson(reader, Map.class);
            
            // Verificar estrutura básica
            return dados.containsKey("dataExportacao") && 
                   dados.containsKey("versao") && 
                   dados.containsKey("contas") && 
                   dados.containsKey("lancamentos");
                   
        } catch (Exception e) {
            System.err.println("Erro ao validar backup: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gera estatísticas detalhadas em texto
     */
    public String gerarEstatisticas() {
        StringBuilder stats = new StringBuilder();
        
        List<Conta> contas = financeController.listarContas();
        List<Lancamento> lancamentos = financeController.listarLancamentos();
        List<Categoria> categorias = financeController.listarCategorias();
        Map<String, Double> resumo = financeController.calcularResumoFinanceiro();
        
        stats.append("=== ESTATÍSTICAS FINANZA ===\n\n");
        
        // Contadores básicos
        stats.append(String.format("Total de Contas: %d\n", contas != null ? contas.size() : 0));
        stats.append(String.format("Total de Categorias: %d\n", categorias != null ? categorias.size() : 0));
        stats.append(String.format("Total de Lançamentos: %d\n", lancamentos != null ? lancamentos.size() : 0));
        stats.append("\n");
        
        // Resumo financeiro
        if (resumo != null) {
            stats.append("=== RESUMO FINANCEIRO ===\n");
            stats.append(String.format("Receitas: R$ %.2f\n", resumo.get("receitas")));
            stats.append(String.format("Despesas: R$ %.2f\n", resumo.get("despesas")));
            stats.append(String.format("Saldo: R$ %.2f\n", resumo.get("saldo")));
            stats.append("\n");
        }
        
        // Conta com maior saldo
        if (contas != null && !contas.isEmpty()) {
            Conta contaMaiorSaldo = null;
            double maiorSaldo = Double.NEGATIVE_INFINITY;
            
            for (Conta conta : contas) {
                double saldo = financeController.calcularSaldoConta(conta.getId());
                if (saldo > maiorSaldo) {
                    maiorSaldo = saldo;
                    contaMaiorSaldo = conta;
                }
            }
            
            if (contaMaiorSaldo != null) {
                stats.append("=== CONTA COM MAIOR SALDO ===\n");
                stats.append(String.format("%s: R$ %.2f\n", contaMaiorSaldo.getNome(), maiorSaldo));
                stats.append("\n");
            }
        }
        
        return stats.toString();
    }
}