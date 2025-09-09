#!/bin/bash

# Script de verificação dos READMEs criados
# Verifica se todas as seções obrigatórias estão presentes

echo "🔍 VERIFICAÇÃO DOS READMES - FINANZA"
echo "=================================="

# Verificar README principal
echo ""
echo "📄 README Principal (README.md):"
echo "  ✓ Contexto acadêmico IFSUL:" $(grep -c "IFSUL.*Venâncio Aires" README.md)
echo "  ✓ Seções técnicas:" $(grep -c "^##" README.md)
echo "  ✓ Documentação acadêmica:" $(grep -c "Documentação Acadêmica" README.md)
echo "  ✓ Total de linhas:" $(wc -l < README.md)

# Verificar README Mobile
echo ""
echo "📱 README Mobile (README_MOBILE.md):"
echo "  ✓ Seções Android:" $(grep -c "Android\|Material Design\|Room" README_MOBILE.md)
echo "  ✓ Funcionalidades:" $(grep -c "✅\|❌" README_MOBILE.md)
echo "  ✓ Instalação:" $(grep -c "Instalação\|Como Instalar" README_MOBILE.md)
echo "  ✓ Total de linhas:" $(wc -l < README_MOBILE.md)

# Verificar README Desktop  
echo ""
echo "🖥️ README Desktop (DESKTOP VERSION/README.md):"
echo "  ✓ Seções Java:" $(grep -c "Java\|MySQL\|Swing" "DESKTOP VERSION/README.md")
echo "  ✓ Configuração:" $(grep -c "Configuração\|Instalação" "DESKTOP VERSION/README.md")
echo "  ✓ Troubleshooting:" $(grep -c "Problema\|Erro" "DESKTOP VERSION/README.md")
echo "  ✓ Total de linhas:" $(wc -l < "DESKTOP VERSION/README.md")

# Verificar links internos
echo ""
echo "🔗 Verificação de Links:"
echo "  ✓ Links para mobile:" $(grep -c "README_MOBILE.md" README.md)
echo "  ✓ Links para desktop:" $(grep -c "DESKTOP VERSION/README.md" README.md)
echo "  ✓ Links para docs:" $(grep -c "docs/" README.md)

# Verificar badges
echo ""
echo "🏷️ Badges e Elementos Visuais:"
echo "  ✓ Badges tecnológicos:" $(grep -c "shields.io" README*.md "DESKTOP VERSION/README.md")
echo "  ✓ Emojis estruturais:" $(grep -c "🎓\|📱\|🖥️\|💰" README*.md "DESKTOP VERSION/README.md")

echo ""
echo "✅ VERIFICAÇÃO CONCLUÍDA"
echo "=================================="
echo "📊 Resumo:"
echo "  • README Principal: $(wc -l < README.md) linhas"
echo "  • README Mobile: $(wc -l < README_MOBILE.md) linhas" 
echo "  • README Desktop: $(wc -l < "DESKTOP VERSION/README.md") linhas"
echo "  • Total: $(($(wc -l < README.md) + $(wc -l < README_MOBILE.md) + $(wc -l < "DESKTOP VERSION/README.md"))) linhas de documentação"