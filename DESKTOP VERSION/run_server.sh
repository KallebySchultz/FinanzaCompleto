#!/bin/bash

# Script para compilar e executar o servidor Finanza

echo "=== Compilando Servidor Finanza ==="

# Limpar e recompilar servidor usando Ant
cd ServidorFinanza
echo "Limpando compilação anterior..."
ant clean

echo "Compilando servidor..."
ant compile

if [ $? -eq 0 ]; then
    echo "Compilação do servidor concluída com sucesso!"
    
    echo "Verificando se existe instância anterior do servidor..."
    SERVER_PID=$(pgrep -f "MainServidor")
    if [ ! -z "$SERVER_PID" ]; then
        echo "Parando servidor anterior (PID: $SERVER_PID)..."
        kill $SERVER_PID
        sleep 2
    fi
    
    echo "Iniciando servidor com código atualizado..."
    java -cp "build/classes:../lib/mysql-connector-j-8.0.33.jar" MainServidor
else
    echo "Erro na compilação do servidor!"
    exit 1
fi