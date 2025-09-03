#!/bin/bash

# Script para compilar e executar o servidor Finanza

echo "=== Compilando Servidor Finanza ==="

# Criar diretório de build se não existir
mkdir -p build/classes

# Compilar código Java
javac -d build/classes src/main/java/com/finanza/server/*.java

if [ $? -eq 0 ]; then
    echo "Compilação bem-sucedida!"
    echo ""
    echo "=== Iniciando Servidor ==="
    echo "Para parar o servidor, pressione Ctrl+C"
    echo ""
    
    # Executar servidor
    cd build/classes
    java com.finanza.server.FinanzaServer
else
    echo "Erro na compilação!"
    exit 1
fi