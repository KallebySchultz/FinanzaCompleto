#!/bin/bash

# Script para compilar e executar o cliente Finanza

echo "=== Compilando Cliente Finanza ==="

# Limpar e recompilar cliente usando Ant
cd ClienteFinanza
echo "Limpando compilação anterior..."
ant clean

echo "Compilando cliente..."
ant compile

if [ $? -eq 0 ]; then
    echo "Compilação do cliente concluída com sucesso!"
    
    echo "Iniciando cliente..."
    java -cp build/classes MainCliente
else
    echo "Erro na compilação do cliente!"
    exit 1
fi