#!/bin/bash

# Script para executar o Finanza Desktop

echo "========================================"
echo "      Finanza Desktop Application"
echo "========================================"
echo ""

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não está instalado."
    echo "   Por favor, instale o Java 11 ou superior."
    exit 1
fi

# Verificar versão do Java
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "❌ Java 11 ou superior é necessário."
    echo "   Versão atual: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

echo "✅ Java OK - $(java -version 2>&1 | head -n 1)"
echo ""

# Verificar se o JAR existe
if [ ! -f "target/finanza-desktop-1.0.0.jar" ]; then
    echo "❌ Aplicação não foi compilada."
    echo "   Execute: mvn clean package"
    exit 1
fi

echo "🚀 Iniciando Finanza Desktop..."
echo ""

# Executar aplicação
java -jar target/finanza-desktop-1.0.0.jar

echo ""
echo "✅ Aplicação finalizada."