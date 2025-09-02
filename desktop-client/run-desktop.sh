#!/bin/bash

# Script para compilar e executar o cliente desktop Finanza

echo "=== Compilando Cliente Desktop Finanza ==="

# Criar diretório de build se não existir
mkdir -p build/classes

# Compilar código Java
javac -d build/classes src/main/java/com/finanza/desktop/*.java

if [ $? -eq 0 ]; then
    echo "Compilação bem-sucedida!"
    echo ""
    echo "=== Iniciando Cliente Desktop ==="
    
    # Executar cliente desktop
    cd build/classes
    java com.finanza.desktop.FinanzaDesktop
else
    echo "Erro na compilação!"
    exit 1
fi