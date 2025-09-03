#!/bin/bash

# Script para compilar e executar o cliente desktop Finanza v2.0

echo "==============================================="
echo "       🏦 Finanza Desktop Client v2.0"
echo "==============================================="
echo ""

# Verificar se Maven está disponível
if command -v mvn &> /dev/null; then
    echo "✅ Maven detectado - usando compilação Maven"
    echo ""
    echo "=== Compilando com Maven ==="
    mvn compile
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilação bem-sucedida!"
        echo ""
        echo "=== Iniciando Cliente Desktop ==="
        echo "🚀 Iniciando aplicação com interface moderna..."
        echo "⚙️  Use a tela de Configurações para definir IP do servidor"
        echo ""
        mvn exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"
    else
        echo "❌ Erro na compilação Maven!"
        echo ""
        echo "Tentando compilação manual..."
        manual_compile
    fi
else
    manual_compile
fi

function manual_compile() {
    echo "📝 Maven não encontrado - usando javac manual"
    echo ""
    
    # Criar diretório de build se não existir
    mkdir -p build/classes
    
    echo "=== Compilando código Java ==="
    javac -d build/classes -cp "src/main/java" \
        src/main/java/com/finanza/desktop/*.java \
        src/main/java/com/finanza/desktop/config/*.java \
        src/main/java/com/finanza/desktop/network/*.java \
        src/main/java/com/finanza/desktop/ui/*.java
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilação bem-sucedida!"
        echo ""
        echo "=== Iniciando Cliente Desktop ==="
        echo "🚀 Iniciando aplicação com interface moderna..."
        echo "⚙️  Use a tela de Configurações para definir IP do servidor"
        echo ""
        
        # Executar cliente desktop
        cd build/classes
        java com.finanza.desktop.FinanzaDesktop
    else
        echo "❌ Erro na compilação!"
        echo ""
        echo "Dicas de solução:"
        echo "• Verifique se o JDK está instalado (java -version)"
        echo "• Instale o Maven para melhor suporte"
        echo "• Verifique se está na pasta correta"
        exit 1
    fi
}

echo ""
echo "👋 Cliente Desktop encerrado"