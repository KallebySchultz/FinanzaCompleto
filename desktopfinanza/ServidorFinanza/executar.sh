#!/bin/bash

# Script para executar o Servidor Finanza
# Execute este script da pasta ServidorFinanza

echo "=== Finanza Desktop - Script de Execução ==="

# Verificar se estamos na pasta correta
if [ ! -f "build.xml" ]; then
    echo "❌ Erro: Execute este script da pasta ServidorFinanza"
    echo "   cd desktopfinanza/ServidorFinanza"
    echo "   ./executar.sh"
    exit 1
fi

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Erro: Java não encontrado"
    echo "   Instale o JDK 8 ou superior"
    exit 1
fi

# Verificar se o Ant está instalado
if ! command -v ant &> /dev/null; then
    echo "❌ Erro: Apache Ant não encontrado"
    echo "   Instale o Apache Ant"
    exit 1
fi

echo "🔧 Compilando e executando servidor..."
ant run