#!/usr/bin/env python3
"""
Gerador de PDF para Fluxograma Simples do Sistema Finanza
"""

import markdown2
from weasyprint import HTML, CSS
from weasyprint.text.fonts import FontConfiguration
import sys
import os

def create_custom_css():
    """Cria CSS customizado para o PDF"""
    return """
    @page {
        size: A4;
        margin: 2cm 1.5cm;
        
        @top-left {
            content: "Fluxograma Simplificado - Sistema Finanza";
            font-size: 9pt;
            color: #666;
            font-family: Arial, sans-serif;
        }
        
        @top-right {
            content: "Página " counter(page);
            font-size: 9pt;
            color: #666;
            font-family: Arial, sans-serif;
        }
        
        @bottom-center {
            content: "© 2024 - IFSUL Campus Venâncio Aires";
            font-size: 8pt;
            color: #999;
            font-family: Arial, sans-serif;
        }
    }
    
    body {
        font-family: Arial, Helvetica, sans-serif;
        font-size: 10pt;
        line-height: 1.6;
        color: #333;
        background: white;
    }
    
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
    
    p {
        margin-bottom: 10pt;
        text-align: justify;
    }
    
    ul, ol {
        margin-left: 20pt;
        margin-bottom: 12pt;
    }
    
    li {
        margin-bottom: 5pt;
        line-height: 1.5;
    }
    
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
        font-size: 8pt;
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
    
    strong, b {
        font-weight: bold;
        color: #1a5490;
    }
    
    hr {
        border: none;
        border-top: 2px solid #ddd;
        margin: 20pt 0;
    }
    
    blockquote {
        border-left: 4px solid #f39c12;
        background-color: #fef9e7;
        padding: 10pt;
        margin: 10pt 0;
        page-break-inside: avoid;
    }
    """

def generate_pdf(markdown_file, output_file):
    """Gera PDF a partir do arquivo markdown"""
    
    print(f"📄 Lendo arquivo: {markdown_file}")
    
    with open(markdown_file, 'r', encoding='utf-8') as f:
        markdown_content = f.read()
    
    print("🔄 Convertendo Markdown para HTML...")
    html_content = markdown2.markdown(
        markdown_content,
        extras=[
            'tables',
            'fenced-code-blocks',
            'code-friendly',
            'header-ids'
        ]
    )
    
    full_html = f"""
    <!DOCTYPE html>
    <html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Fluxograma Simplificado - Sistema Finanza</title>
    </head>
    <body>
        <div style="text-align: center; margin-bottom: 30pt; padding: 20pt; background: linear-gradient(135deg, #1a5490 0%, #2874a6 100%); color: white; border-radius: 5pt;">
            <div style="font-size: 28pt; font-weight: bold; margin-bottom: 10pt;">📊 FLUXOGRAMA SIMPLIFICADO</div>
            <div style="font-size: 14pt;">Sistema de Controle Financeiro Finanza</div>
            <div style="font-size: 12pt; margin-top: 5pt;">IFSUL - Campus Venâncio Aires</div>
        </div>
        {html_content}
    </body>
    </html>
    """
    
    print("📝 Gerando PDF...")
    
    font_config = FontConfiguration()
    css = CSS(string=create_custom_css(), font_config=font_config)
    
    HTML(string=full_html).write_pdf(
        output_file,
        stylesheets=[css],
        font_config=font_config
    )
    
    print(f"✅ PDF gerado: {output_file}")
    
    size = os.path.getsize(output_file)
    size_kb = size / 1024
    print(f"📊 Tamanho: {size_kb:.2f} KB")

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    markdown_file = os.path.join(script_dir, 'FLUXOGRAMA_SIMPLES.md')
    output_file = os.path.join(script_dir, 'FLUXOGRAMA_SIMPLES.pdf')
    
    if not os.path.exists(markdown_file):
        print(f"❌ Erro: Arquivo não encontrado: {markdown_file}")
        sys.exit(1)
    
    print("=" * 60)
    print("  GERADOR DE PDF - FLUXOGRAMA SIMPLIFICADO")
    print("=" * 60)
    print()
    
    try:
        generate_pdf(markdown_file, output_file)
        print()
        print("=" * 60)
        print("  ✅ PROCESSO CONCLUÍDO!")
        print("=" * 60)
        return 0
    except Exception as e:
        print(f"❌ ERRO: {str(e)}")
        import traceback
        traceback.print_exc()
        return 1

if __name__ == '__main__':
    sys.exit(main())
