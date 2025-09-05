#!/bin/bash

# Finanza - Script de Limpeza Segura
# Remove arquivos inúteis identificados na análise

echo "🧹 FINANZA - LIMPEZA DE ARQUIVOS INÚTEIS"
echo "========================================"
echo

# Função para confirmar ação
confirm() {
    read -p "$1 (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[YySs]$ ]]; then
        return 0
    else
        return 1
    fi
}

# Função para remover arquivo com confirmação
safe_remove() {
    local file="$1"
    local reason="$2"
    
    if [ -e "$file" ]; then
        echo "📁 Encontrado: $file"
        echo "   Razão: $reason"
        if confirm "   Excluir este arquivo?"; then
            rm -rf "$file"
            echo "   ✅ Removido com sucesso!"
            return 0
        else
            echo "   ⏭️  Pulado"
            return 1
        fi
    else
        echo "❌ Arquivo não encontrado: $file"
        return 1
    fi
    echo
}

# Arquivos com exclusão 100% segura
echo "🔒 ARQUIVOS DE EXCLUSÃO SEGURA"
echo "------------------------------"

removed_count=0
space_saved=0

# 1. DESKTOP VERSION.rar (redundante)
if safe_remove "DESKTOP VERSION.rar" "Arquivo comprimido redundante (1.5MB)"; then
    removed_count=$((removed_count + 1))
    space_saved=$((space_saved + 1536))
fi

# 2. verify_netbeans.sh (desatualizado)
if safe_remove "verify_netbeans.sh" "Script desatualizado para NetBeans inexistente (4KB)"; then
    removed_count=$((removed_count + 1))
    space_saved=$((space_saved + 4))
fi

echo
echo "⚠️  ARQUIVOS DE EXCLUSÃO OPCIONAL"
echo "--------------------------------"

# 3. firebase-test.html (ferramenta de teste)
if safe_remove "firebase-test.html" "Arquivo de teste Firebase (20KB) - útil para depuração"; then
    removed_count=$((removed_count + 1))
    space_saved=$((space_saved + 20))
fi

# 4. .idea/ (configurações IDE)
if safe_remove ".idea/" "Configurações do IDE (44KB) - regenerável mas útil"; then
    removed_count=$((removed_count + 1))
    space_saved=$((space_saved + 44))
fi

echo
echo "📊 RESUMO DA LIMPEZA"
echo "===================="
echo "🗑️  Arquivos removidos: $removed_count"
echo "💾 Espaço economizado: ${space_saved}KB (~$(echo "scale=1; $space_saved/1024" | bc)MB)"
echo

if [ $removed_count -gt 0 ]; then
    echo "✅ Limpeza concluída com sucesso!"
    echo "💡 Os arquivos foram removidos do sistema de arquivos"
    echo "📋 Para confirmar no Git, execute: git add . && git commit -m 'Limpeza: removidos arquivos inúteis'"
else
    echo "ℹ️  Nenhum arquivo foi removido"
fi

echo
echo "📖 Para mais detalhes, consulte: CLEANUP_ANALYSIS.md"