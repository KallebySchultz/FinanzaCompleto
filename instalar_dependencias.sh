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
echo -e "${BLUE}📦 FINANZA - INSTALAR DEPENDÊNCIAS${NC}"
echo "========================================"
echo

echo -e "${YELLOW}💡 Este script irá instalar todas as dependências necessárias${NC}"
echo -e "${YELLOW}   para o servidor e cliente desktop do Finanza.${NC}"
echo

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ ERRO: Node.js não encontrado!${NC}"
    echo
    echo -e "${YELLOW}📥 SOLUÇÃO:${NC}"
    echo "1. Acesse: https://nodejs.org"
    echo "2. Baixe a versão LTS (recomendada)"
    echo "3. Instale normalmente"
    echo "4. Reinicie o terminal"
    echo "5. Execute este script novamente"
    echo
    read -p "Pressione Enter para continuar..."
    exit 1
fi

echo -e "${GREEN}✅ Node.js encontrado ($(node --version))${NC}"
echo -e "${GREEN}✅ npm encontrado ($(npm --version))${NC}"
echo

read -p "Deseja continuar com a instalação? (S/n): " resposta
if [[ "$resposta" =~ ^[Nn]$ ]]; then
    echo "Instalação cancelada."
    exit 0
fi

echo
echo -e "${CYAN}🔄 Iniciando instalação das dependências...${NC}"
echo

# Install server dependencies
echo -e "${BLUE}📡 Instalando dependências do servidor...${NC}"
echo "----------------------------------------"
cd "$(dirname "$0")/server" || {
    echo -e "${RED}❌ ERRO: Não foi possível acessar a pasta 'server'${NC}"
    exit 1
}

npm install
server_exit_code=$?

if [ $server_exit_code -eq 0 ]; then
    echo -e "${GREEN}✅ Dependências do servidor instaladas com sucesso!${NC}"
else
    echo -e "${RED}❌ ERRO: Falha ao instalar dependências do servidor${NC}"
    echo
    echo -e "${YELLOW}🔧 SOLUÇÕES POSSÍVEIS:${NC}"
    echo "1. Verifique sua conexão com a internet"
    echo "2. Execute com sudo (se necessário)"
    echo "3. Limpe o cache npm: npm cache clean --force"
    echo "4. Delete node_modules e package-lock.json, tente novamente"
    echo
    read -p "Pressione Enter para continuar..."
    exit 1
fi

echo
echo -e "${BLUE}🖥️ Instalando dependências do cliente desktop...${NC}"
echo "----------------------------------------"
cd "../DESKTOP VERSION" || {
    echo -e "${RED}❌ ERRO: Não foi possível acessar a pasta 'DESKTOP VERSION'${NC}"
    exit 1
}

npm install
client_exit_code=$?

if [ $client_exit_code -eq 0 ]; then
    echo -e "${GREEN}✅ Dependências do cliente instaladas com sucesso!${NC}"
else
    echo -e "${RED}❌ ERRO: Falha ao instalar dependências do cliente${NC}"
    echo
    echo -e "${YELLOW}🔧 SOLUÇÕES POSSÍVEIS:${NC}"
    echo "1. Verifique sua conexão com a internet"
    echo "2. Execute com sudo (se necessário)"
    echo "3. Limpe o cache npm: npm cache clean --force"
    echo "4. Delete node_modules e package-lock.json, tente novamente"
    echo
    read -p "Pressione Enter para continuar..."
    exit 1
fi

# Return to main directory
cd ..

echo
echo "========================================"
echo -e "${GREEN}🎉 INSTALAÇÃO CONCLUÍDA COM SUCESSO!${NC}"
echo "========================================"
echo

echo -e "${BLUE}📋 O QUE FOI INSTALADO:${NC}"
echo
echo -e "${GREEN}✅ Servidor API:${NC}"
echo "   • express, cors, sqlite3, bcrypt"
echo "   • jsonwebtoken, helmet, morgan"
echo "   • express-rate-limit, dotenv"
echo
echo -e "${GREEN}✅ Cliente Desktop:${NC}"
echo "   • express, cors"
echo "   • nodemon (desenvolvimento)"
echo

echo -e "${YELLOW}💡 PRÓXIMOS PASSOS:${NC}"
echo "1. Execute a verificação: ./verificar_sistema.sh"
echo "2. Inicie o sistema: ./iniciar_tudo.sh"
echo "3. Acesse: http://localhost:3001"
echo

echo -e "${BLUE}🔗 INFORMAÇÕES DE LOGIN:${NC}"
echo -e "${CYAN}📧 Email: admin@finanza.com${NC}"
echo -e "${CYAN}🔑 Senha: admin${NC}"
echo

read -p "Pressione Enter para continuar..."