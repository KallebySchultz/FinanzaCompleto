#!/bin/bash

echo "🚀 Finanza Desktop - Script de Execução"
echo "========================================"

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Por favor, instale o Java 8 ou superior."
    exit 1
fi

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado. Por favor, instale o Maven 3.6+."
    exit 1
fi

echo "✅ Java e Maven encontrados"

# Navegar para o diretório do desktop client
cd "$(dirname "$0")/desktop-client"

echo "📦 Compilando o projeto..."
if mvn clean compile; then
    echo "✅ Compilação bem-sucedida!"
else
    echo "❌ Erro na compilação. Verifique os logs acima."
    exit 1
fi

echo "🗄️  Testando banco de dados..."
java -cp target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout | tail -n 1) com.finanza.desktop.test.DatabaseTest

echo ""
echo "🎯 Iniciando Finanza Desktop..."
echo "Aguarde a interface gráfica carregar..."
echo ""

# Executar a aplicação
mvn exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"