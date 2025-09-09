#!/bin/bash

# Script para configurar dependências do projeto Finanza Desktop
# Setup dependencies for Finanza Desktop project

echo "=== Configurando Dependências Finanza Desktop ==="
echo "Setting up Finanza Desktop Dependencies"

# Criar diretório lib se não existir
mkdir -p lib

# Verificar se MySQL Connector já existe
if [ -f "lib/mysql-connector-j-8.0.33.jar" ]; then
    echo "✅ MySQL Connector já existe"
    echo "✅ MySQL Connector already exists"
else
    echo "📥 Baixando MySQL Connector..."
    echo "📥 Downloading MySQL Connector..."
    
    cd lib
    
    # Tentar baixar do Maven Central
    if wget -q "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"; then
        echo "✅ MySQL Connector baixado com sucesso!"
        echo "✅ MySQL Connector downloaded successfully!"
    else
        echo "❌ Erro ao baixar MySQL Connector"
        echo "❌ Failed to download MySQL Connector"
        echo "   Verifique sua conexão com a internet"
        echo "   Please check your internet connection"
        exit 1
    fi
    
    cd ..
fi

# Verificar tamanho do arquivo
if [ -f "lib/mysql-connector-j-8.0.33.jar" ]; then
    file_size=$(stat -c%s "lib/mysql-connector-j-8.0.33.jar")
    if [ $file_size -gt 1000000 ]; then
        echo "✅ MySQL Connector configurado corretamente (${file_size} bytes)"
        echo "✅ MySQL Connector configured correctly (${file_size} bytes)"
    else
        echo "⚠️  Arquivo MySQL Connector parece corrompido"
        echo "⚠️  MySQL Connector file appears corrupted"
        rm -f "lib/mysql-connector-j-8.0.33.jar"
        exit 1
    fi
fi

echo ""
echo "🎉 Dependências configuradas com sucesso!"
echo "🎉 Dependencies configured successfully!"
echo ""
echo "Agora você pode executar:"
echo "Now you can run:"
echo "  ./run_server.sh    # Para o servidor"
echo "  ./run_client.sh    # Para o cliente"
echo ""