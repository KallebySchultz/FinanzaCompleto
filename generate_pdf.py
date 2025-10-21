#!/usr/bin/env python3
"""
Gerador de PDF Profissional para Mapeamento do Sistema Finanza
Converte o markdown aprimorado em um PDF com formatação profissional
"""

import markdown2
from weasyprint import HTML, CSS
from weasyprint.text.fonts import FontConfiguration
import sys
import os

def create_custom_css():
    """Cria CSS customizado para o PDF com fonte Arial e formatação profissional"""
    return """
    @page {
        size: A4;
        margin: 2cm 1.5cm;
        
        @top-left {
            content: "Mapeamento Completo - Sistema Finanza";
            font-size: 9pt;
            color: #666;
            font-family: Arial, sans-serif;
        }
        
        @top-right {
            content: "Página " counter(page) " de " counter(pages);
            font-size: 9pt;
            color: #666;
            font-family: Arial, sans-serif;
        }
        
        @bottom-center {
            content: "© 2024 - IFSUL Campus Venâncio Aires - Projeto Acadêmico";
            font-size: 8pt;
            color: #999;
            font-family: Arial, sans-serif;
        }
    }
    
    /* Reset e configuração base */
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }
    
    body {
        font-family: Arial, Helvetica, sans-serif;
        font-size: 10pt;
        line-height: 1.6;
        color: #333;
        background: white;
    }
    
    /* Títulos principais */
    h1 {
        font-size: 24pt;
        font-weight: bold;
        color: #1a5490;
        margin-top: 20pt;
        margin-bottom: 15pt;
        padding-bottom: 8pt;
        border-bottom: 3px solid #1a5490;
        page-break-after: avoid;
    }
    
    h2 {
        font-size: 18pt;
        font-weight: bold;
        color: #2874a6;
        margin-top: 18pt;
        margin-bottom: 12pt;
        padding-left: 5pt;
        border-left: 5px solid #2874a6;
        page-break-after: avoid;
    }
    
    h3 {
        font-size: 14pt;
        font-weight: bold;
        color: #2980b9;
        margin-top: 15pt;
        margin-bottom: 10pt;
        page-break-after: avoid;
    }
    
    h4 {
        font-size: 12pt;
        font-weight: bold;
        color: #5dade2;
        margin-top: 12pt;
        margin-bottom: 8pt;
        page-break-after: avoid;
    }
    
    h5 {
        font-size: 11pt;
        font-weight: bold;
        color: #85c1e9;
        margin-top: 10pt;
        margin-bottom: 6pt;
        font-style: italic;
        page-break-after: avoid;
    }
    
    /* Parágrafos e texto */
    p {
        margin-bottom: 10pt;
        text-align: justify;
    }
    
    /* Listas */
    ul, ol {
        margin-left: 20pt;
        margin-bottom: 12pt;
    }
    
    li {
        margin-bottom: 5pt;
        line-height: 1.5;
    }
    
    ul li {
        list-style-type: disc;
    }
    
    ul ul li {
        list-style-type: circle;
    }
    
    /* Listas numeradas customizadas */
    ol {
        counter-reset: item;
    }
    
    ol li {
        display: block;
        margin-left: 25pt;
    }
    
    ol li:before {
        content: counter(item) ". ";
        counter-increment: item;
        font-weight: bold;
        color: #2874a6;
        margin-right: 5pt;
    }
    
    /* Blocos de código */
    code {
        font-family: 'Courier New', Courier, monospace;
        background-color: #f5f5f5;
        padding: 2pt 4pt;
        border-radius: 3pt;
        font-size: 9pt;
        color: #c7254e;
        border: 1px solid #e1e1e8;
    }
    
    pre {
        background-color: #f8f8f8;
        border: 1px solid #ddd;
        border-left: 4px solid #2874a6;
        padding: 12pt;
        margin: 10pt 0;
        overflow-x: auto;
        font-family: 'Courier New', Courier, monospace;
        font-size: 9pt;
        line-height: 1.4;
        border-radius: 4pt;
        page-break-inside: avoid;
    }
    
    pre code {
        background-color: transparent;
        padding: 0;
        border: none;
        color: #333;
    }
    
    /* Tabelas */
    table {
        width: 100%;
        border-collapse: collapse;
        margin: 15pt 0;
        page-break-inside: avoid;
        font-size: 9pt;
    }
    
    thead {
        background-color: #2874a6;
        color: white;
        font-weight: bold;
    }
    
    th {
        padding: 8pt 10pt;
        text-align: left;
        border: 1px solid #ddd;
    }
    
    td {
        padding: 6pt 10pt;
        border: 1px solid #ddd;
    }
    
    tbody tr:nth-child(even) {
        background-color: #f2f2f2;
    }
    
    tbody tr:hover {
        background-color: #e8f4f8;
    }
    
    /* Links */
    a {
        color: #2874a6;
        text-decoration: none;
        font-weight: 500;
    }
    
    a:hover {
        text-decoration: underline;
    }
    
    /* Linha horizontal */
    hr {
        border: none;
        border-top: 2px solid #ddd;
        margin: 20pt 0;
    }
    
    /* Blockquotes e avisos */
    blockquote {
        border-left: 4px solid #f39c12;
        background-color: #fef9e7;
        padding: 10pt;
        margin: 10pt 0;
        font-style: italic;
        page-break-inside: avoid;
    }
    
    /* Badges e tags */
    .badge {
        display: inline-block;
        padding: 3pt 8pt;
        font-size: 8pt;
        font-weight: bold;
        border-radius: 10pt;
        margin-right: 5pt;
    }
    
    .badge-info {
        background-color: #d6eaf8;
        color: #1a5490;
    }
    
    .badge-success {
        background-color: #d5f4e6;
        color: #196f3d;
    }
    
    .badge-warning {
        background-color: #fcf3cf;
        color: #9c640c;
    }
    
    /* Caixas de destaque */
    .highlight-box {
        background-color: #e8f8f5;
        border: 2px solid #16a085;
        border-radius: 5pt;
        padding: 12pt;
        margin: 12pt 0;
        page-break-inside: avoid;
    }
    
    .warning-box {
        background-color: #fef5e7;
        border: 2px solid #f39c12;
        border-radius: 5pt;
        padding: 12pt;
        margin: 12pt 0;
        page-break-inside: avoid;
    }
    
    .info-box {
        background-color: #ebf5fb;
        border: 2px solid #3498db;
        border-radius: 5pt;
        padding: 12pt;
        margin: 12pt 0;
        page-break-inside: avoid;
    }
    
    /* Strong e emphasis */
    strong, b {
        font-weight: bold;
        color: #1a5490;
    }
    
    em, i {
        font-style: italic;
        color: #555;
    }
    
    /* Elementos de fluxo */
    .flow-diagram {
        background-color: #f8f9fa;
        border: 2px dashed #6c757d;
        padding: 15pt;
        margin: 15pt 0;
        font-family: 'Courier New', Courier, monospace;
        font-size: 8pt;
        line-height: 1.8;
        page-break-inside: avoid;
    }
    
    /* Cabeçalho do documento */
    .document-header {
        text-align: center;
        margin-bottom: 30pt;
        padding: 20pt;
        background: linear-gradient(135deg, #1a5490 0%, #2874a6 100%);
        color: white;
        border-radius: 5pt;
    }
    
    .document-title {
        font-size: 28pt;
        font-weight: bold;
        margin-bottom: 10pt;
    }
    
    .document-subtitle {
        font-size: 14pt;
        font-weight: normal;
        margin-bottom: 5pt;
    }
    
    /* Índice customizado */
    .toc {
        background-color: #f8f9fa;
        border: 1px solid #dee2e6;
        border-radius: 5pt;
        padding: 15pt;
        margin: 20pt 0;
        page-break-inside: avoid;
    }
    
    .toc-title {
        font-size: 16pt;
        font-weight: bold;
        color: #1a5490;
        margin-bottom: 10pt;
        text-align: center;
    }
    
    .toc ul {
        list-style-type: none;
        margin-left: 0;
    }
    
    .toc li {
        margin-bottom: 5pt;
        padding-left: 15pt;
    }
    
    .toc li:before {
        content: "▸ ";
        color: #2874a6;
        font-weight: bold;
    }
    
    /* Quebras de página controladas */
    .page-break {
        page-break-after: always;
    }
    
    .avoid-break {
        page-break-inside: avoid;
    }
    
    /* Emoji e ícones no texto */
    .icon {
        font-size: 12pt;
        margin-right: 5pt;
    }
    """

def preprocess_markdown(content):
    """Pré-processa o markdown para melhorar a formatação"""
    
    # Substitui emojis por versões mais visíveis
    content = content.replace('🗺️', '📋')
    content = content.replace('📖', '📑')
    content = content.replace('🎯', '🎓')
    content = content.replace('📱', '📱')
    content = content.replace('💻', '💻')
    content = content.replace('🔄', '🔄')
    content = content.replace('🗄️', '💾')
    content = content.replace('📡', '📡')
    content = content.replace('🔐', '🔒')
    content = content.replace('📚', '📚')
    content = content.replace('🚀', '🚀')
    content = content.replace('🔧', '🔧')
    content = content.replace('⚠️', '⚠️')
    content = content.replace('📞', '📞')
    
    # Adiciona classes especiais para caixas de destaque
    content = content.replace('**Resumo da Cadeia de Chamadas:**', 
                            '\n<div class="highlight-box">\n\n**Resumo da Cadeia de Chamadas:**\n')
    
    # Adiciona fechamento para caixas
    lines = content.split('\n')
    new_lines = []
    in_box = False
    for i, line in enumerate(lines):
        if '<div class="highlight-box">' in line:
            in_box = True
        if in_box and line.startswith('```') and i > 0:
            new_lines.append('</div>\n')
            in_box = False
        new_lines.append(line)
    
    content = '\n'.join(new_lines)
    
    return content

def generate_pdf(markdown_file, output_file):
    """Gera PDF a partir do arquivo markdown"""
    
    print(f"🔄 Lendo arquivo markdown: {markdown_file}")
    
    # Lê o arquivo markdown
    with open(markdown_file, 'r', encoding='utf-8') as f:
        markdown_content = f.read()
    
    print("🔄 Pré-processando conteúdo...")
    markdown_content = preprocess_markdown(markdown_content)
    
    print("🔄 Convertendo Markdown para HTML...")
    # Converte markdown para HTML com extras
    html_content = markdown2.markdown(
        markdown_content,
        extras=[
            'tables',
            'fenced-code-blocks',
            'code-friendly',
            'header-ids',
            'toc',
            'strike',
            'task_list'
        ]
    )
    
    # Cria HTML completo com meta tags
    full_html = f"""
    <!DOCTYPE html>
    <html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Mapeamento Completo - Sistema Finanza</title>
    </head>
    <body>
        <div class="document-header">
            <div class="document-title">📋 MAPEAMENTO COMPLETO</div>
            <div class="document-subtitle">Sistema de Controle Financeiro Finanza</div>
            <div class="document-subtitle">IFSUL - Campus Venâncio Aires</div>
            <div class="document-subtitle" style="font-size: 11pt; margin-top: 10pt;">
                Documentação Técnica Detalhada com Fluxos Completos de Código
            </div>
        </div>
        {html_content}
    </body>
    </html>
    """
    
    print("🔄 Gerando PDF com WeasyPrint...")
    
    # Cria configuração de fontes
    font_config = FontConfiguration()
    
    # Cria CSS customizado
    css = CSS(string=create_custom_css(), font_config=font_config)
    
    # Gera PDF
    HTML(string=full_html).write_pdf(
        output_file,
        stylesheets=[css],
        font_config=font_config
    )
    
    print(f"✅ PDF gerado com sucesso: {output_file}")
    
    # Mostra tamanho do arquivo
    size = os.path.getsize(output_file)
    size_mb = size / (1024 * 1024)
    print(f"📊 Tamanho do arquivo: {size_mb:.2f} MB")

def main():
    """Função principal"""
    
    # Define caminhos
    script_dir = os.path.dirname(os.path.abspath(__file__))
    markdown_file = os.path.join(script_dir, 'MAPEAMENTO_COMPLETO.md')
    output_file = os.path.join(script_dir, 'MAPEAMENTO_COMPLETO.pdf')
    
    # Verifica se arquivo markdown existe
    if not os.path.exists(markdown_file):
        print(f"❌ Erro: Arquivo não encontrado: {markdown_file}")
        sys.exit(1)
    
    print("=" * 60)
    print("  GERADOR DE PDF - MAPEAMENTO SISTEMA FINANZA")
    print("=" * 60)
    print()
    
    try:
        generate_pdf(markdown_file, output_file)
        print()
        print("=" * 60)
        print("  ✅ PROCESSO CONCLUÍDO COM SUCESSO!")
        print("=" * 60)
        return 0
    except Exception as e:
        print()
        print("=" * 60)
        print(f"  ❌ ERRO: {str(e)}")
        print("=" * 60)
        import traceback
        traceback.print_exc()
        return 1

if __name__ == '__main__':
    sys.exit(main())
