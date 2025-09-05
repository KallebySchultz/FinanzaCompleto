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
echo -e "${GREEN}    🚀 FINANZA - SERVIDOR API${NC}"
echo "========================================"
echo

# Function to check Node.js version
check_node_version() {
    local required_major=18
    local required_minor=19
    local required_patch=0
    
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
    
    local node_version=$(node --version | sed 's/v//')
    local major=$(echo $node_version | cut -d. -f1)
    local minor=$(echo $node_version | cut -d. -f2)
    local patch=$(echo $node_version | cut -d. -f3)
    
    # Check if version meets minimum requirements
    if [ "$major" -lt "$required_major" ] || \
       ([ "$major" -eq "$required_major" ] && [ "$minor" -lt "$required_minor" ]) || \
       ([ "$major" -eq "$required_major" ] && [ "$minor" -eq "$required_minor" ] && [ "$patch" -lt "$required_patch" ]); then
        echo
        echo -e "${RED}❌ ERRO: Versão do Node.js incompatível!${NC}"
        echo -e "${YELLOW}📋 Versão atual: v$node_version${NC}"
        echo -e "${YELLOW}📋 Versão mínima: v$required_major.$required_minor.$required_patch${NC}"
        echo
        echo -e "${YELLOW}📥 SOLUÇÃO:${NC}"
        echo "1. Acesse: https://nodejs.org"
        echo "2. Baixe a versão LTS mais recente (recomendada)"
        echo "3. Instale normalmente"
        echo "4. Reinicie o terminal"
        echo "5. Execute este arquivo novamente"
        echo
        echo -e "${CYAN}💡 MOTIVO: Node.js v18.18.x possui problemas conhecidos${NC}"
        echo -e "${CYAN}   de compatibilidade. Use v18.19.0 ou superior.${NC}"
        echo
        read -p "Pressione Enter para continuar..."
        exit 1
    fi
    
    echo -e "${GREEN}✅ Node.js encontrado (v$node_version)${NC}"
}

echo -e "${CYAN}🔍 Verificando se Node.js está instalado...${NC}"
check_node_version
echo

echo -e "${CYAN}📁 Navegando para a pasta do servidor...${NC}"
cd "$(dirname "$0")/server" || {
    echo -e "${RED}❌ ERRO: Não foi possível acessar a pasta 'server'${NC}"
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

echo -e "${CYAN}🗄️ Verificando banco de dados...${NC}"
if [ ! -f "../database/finanza.db" ]; then
    echo -e "${YELLOW}🔧 Banco será criado automaticamente na primeira execução${NC}"
    if [ -f "../database/finanza.sql" ]; then
        echo -e "${GREEN}✅ Schema SQL encontrado${NC}"
    else
        echo -e "${YELLOW}⚠️  AVISO: Arquivo finanza.sql não encontrado em database/${NC}"
    fi
else
    echo -e "${GREEN}✅ Banco de dados encontrado${NC}"
fi

echo
echo "========================================"
echo -e "${GREEN}         🚀 INICIANDO SERVIDOR${NC}"
echo "========================================"
echo
echo -e "${BLUE}📍 Servidor rodará na porta 8080${NC}"
echo -e "${BLUE}🌐 Acesso local: http://localhost:8080/api/health${NC}"
echo -e "${BLUE}📱 Para Android: Configure o IP da sua rede${NC}"
echo
echo -e "${YELLOW}💡 DICAS:${NC}"
echo "• Para descobrir seu IP, execute: ./descobrir_ip.sh"
echo "• Configure no app Android: [SEU_IP]:8080"
echo "• Mantenha este terminal ABERTO enquanto usar o sistema"
echo "• Para parar: Pressione Ctrl+C"
echo
echo "========================================"
echo

# Define environment variables
export PORT=8080
export CORS_ORIGIN="*"
export NODE_ENV=production

echo -e "${CYAN}🔄 Iniciando servidor...${NC}"
npm start

echo
echo -e "${YELLOW}🛑 Servidor encerrado.${NC}"
echo
read -p "Pressione Enter para continuar..."