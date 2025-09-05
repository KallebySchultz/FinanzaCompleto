#!/bin/bash

# Set UTF-8 encoding
export LANG=pt_BR.UTF-8
export LC_ALL=pt_BR.UTF-8

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo "========================================"
echo -e "${BLUE}   🖥️ FINANZA - CLIENTE DESKTOP${NC}"
echo "========================================"
echo

echo -e "${CYAN}🔍 Verificando se Node.js está instalado...${NC}"
if ! command -v node &> /dev/null; then
    echo
    echo -e "${RED}❌ ERRO: Node.js não foi encontrado!${NC}"
    echo
    echo -e "${YELLOW}📥 SOLUÇÃO:${NC}"
    echo "1. Acesse: https://nodejs.org"
    echo "2. Baixe a versão LTS (recomendada)"
    echo "3. Instale normalmente"
    echo "4. Reinicie o terminal"
    echo "5. Execute este arquivo novamente"
    echo
    read -p "Pressione Enter para continuar..."
    exit 1
fi

echo -e "${GREEN}✅ Node.js encontrado ($(node --version))${NC}"
echo

echo -e "${CYAN}📁 Navegando para a pasta do cliente desktop...${NC}"
cd "$(dirname "$0")/DESKTOP VERSION" || {
    echo -e "${RED}❌ ERRO: Não foi possível acessar a pasta 'DESKTOP VERSION'${NC}"
    exit 1
}

echo -e "${CYAN}🔍 Verificando dependências...${NC}"
if [ ! -d "node_modules" ]; then
    echo
    echo -e "${YELLOW}📦 Primeira execução detectada!${NC}"
    echo -e "${YELLOW}📦 Instalando dependências... (isso pode demorar alguns minutos)${NC}"
    echo
    npm install
    if [ $? -ne 0 ]; then
        echo
        echo -e "${RED}❌ ERRO: Falha ao instalar dependências!${NC}"
        echo
        echo -e "${YELLOW}🔧 SOLUÇÕES POSSÍVEIS:${NC}"
        echo "1. Verifique sua conexão com a internet"
        echo "2. Execute com sudo (se necessário)"
        echo "3. Limpe o cache npm: npm cache clean --force"
        echo
        read -p "Pressione Enter para continuar..."
        exit 1
    fi
    echo -e "${GREEN}✅ Dependências instaladas com sucesso!${NC}"
    echo
fi

echo -e "${GREEN}✅ Dependências OK${NC}"
echo

echo -e "${CYAN}🔍 Verificando se o servidor API está rodando...${NC}"
if ! curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
    echo
    echo -e "${YELLOW}⚠️  AVISO: Servidor API não está rodando!${NC}"
    echo
    echo -e "${YELLOW}🔧 SOLUÇÃO:${NC}"
    echo "1. Execute './iniciar_servidor.sh' primeiro"
    echo "2. Aguarde o servidor inicializar completamente"
    echo "3. Execute este arquivo novamente"
    echo
    echo -e "${BLUE}💡 O servidor deve estar rodando na porta 8080${NC}"
    echo
    read -p "Deseja continuar mesmo assim? (s/N): " resposta
    if [[ ! "$resposta" =~ ^[Ss]$ ]]; then
        echo
        echo "Cancelando..."
        read -p "Pressione Enter para continuar..."
        exit 1
    fi
    echo
    echo -e "${YELLOW}⚠️  Continuando sem verificação do servidor...${NC}"
    echo
fi

echo
echo "========================================"
echo -e "${BLUE}        🖥️ INICIANDO CLIENTE WEB${NC}"
echo "========================================"
echo
echo -e "${BLUE}📍 Cliente rodará na porta 3001${NC}"
echo -e "${BLUE}🌐 Acesso: http://localhost:3001${NC}"
echo -e "${BLUE}🔗 Conecta-se ao servidor API na porta 8080${NC}"
echo
echo -e "${YELLOW}💡 DICAS:${NC}"
echo "• Abra seu navegador em: http://localhost:3001"
echo "• Login padrão: admin@finanza.com / admin"
echo "• Mantenha este terminal ABERTO enquanto usar o sistema"
echo "• Para parar: Pressione Ctrl+C"
echo
echo "========================================"
echo

echo -e "${CYAN}🔄 Iniciando cliente web...${NC}"
npm start

echo
echo -e "${YELLOW}🛑 Cliente desktop encerrado.${NC}"
echo
read -p "Pressione Enter para continuar..."