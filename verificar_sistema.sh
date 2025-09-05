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
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

echo "========================================"
echo -e "${MAGENTA}🔍 FINANZA - VERIFICAÇÃO DO SISTEMA${NC}"
echo "========================================"
echo

# Initialize counters
total_checks=0
passed_checks=0

# Function to run a check
run_check() {
    local name="$1"
    local command="$2"
    local success_msg="$3"
    local error_msg="$4"
    
    echo -n -e "${CYAN}🔍 $name... ${NC}"
    total_checks=$((total_checks + 1))
    
    if eval "$command" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ $success_msg${NC}"
        passed_checks=$((passed_checks + 1))
        return 0
    else
        echo -e "${RED}❌ $error_msg${NC}"
        return 1
    fi
}

echo -e "${BLUE}📋 VERIFICANDO REQUISITOS DO SISTEMA...${NC}"
echo

# Check Node.js
if command -v node &> /dev/null; then
    node_version=$(node --version)
    echo -e "${GREEN}✅ Node.js instalado ($node_version)${NC}"
    passed_checks=$((passed_checks + 1))
else
    echo -e "${RED}❌ Node.js não encontrado${NC}"
    echo -e "${YELLOW}   📥 Instale em: https://nodejs.org${NC}"
fi
total_checks=$((total_checks + 1))

# Check npm
if command -v npm &> /dev/null; then
    npm_version=$(npm --version)
    echo -e "${GREEN}✅ npm instalado ($npm_version)${NC}"
    passed_checks=$((passed_checks + 1))
else
    echo -e "${RED}❌ npm não encontrado${NC}"
fi
total_checks=$((total_checks + 1))

# Check curl
run_check "Verificando curl" "command -v curl" "curl disponível" "curl não encontrado (necessário para testes)"

echo
echo -e "${BLUE}📁 VERIFICANDO ESTRUTURA DE ARQUIVOS...${NC}"
echo

# Check project structure
run_check "Pasta server" "[ -d 'server' ]" "Pasta server encontrada" "Pasta server não encontrada"
run_check "Pasta DESKTOP VERSION" "[ -d 'DESKTOP VERSION' ]" "Pasta DESKTOP VERSION encontrada" "Pasta DESKTOP VERSION não encontrada"
run_check "Pasta database" "[ -d 'database' ]" "Pasta database encontrada" "Pasta database não encontrada"
run_check "Arquivo server/package.json" "[ -f 'server/package.json' ]" "package.json do servidor encontrado" "package.json do servidor não encontrado"
run_check "Arquivo DESKTOP VERSION/package.json" "[ -f 'DESKTOP VERSION/package.json' ]" "package.json do cliente encontrado" "package.json do cliente não encontrado"
run_check "Schema do banco" "[ -f 'database/finanza.sql' ]" "Schema SQL encontrado" "Schema SQL não encontrado"

echo
echo -e "${BLUE}📦 VERIFICANDO DEPENDÊNCIAS...${NC}"
echo

# Check server dependencies
if [ -d "server/node_modules" ]; then
    echo -e "${GREEN}✅ Dependências do servidor instaladas${NC}"
    passed_checks=$((passed_checks + 1))
else
    echo -e "${YELLOW}⚠️  Dependências do servidor não instaladas${NC}"
    echo -e "${BLUE}   💡 Execute: cd server && npm install${NC}"
fi
total_checks=$((total_checks + 1))

# Check client dependencies
if [ -d "DESKTOP VERSION/node_modules" ]; then
    echo -e "${GREEN}✅ Dependências do cliente instaladas${NC}"
    passed_checks=$((passed_checks + 1))
else
    echo -e "${YELLOW}⚠️  Dependências do cliente não instaladas${NC}"
    echo -e "${BLUE}   💡 Execute: cd 'DESKTOP VERSION' && npm install${NC}"
fi
total_checks=$((total_checks + 1))

echo
echo -e "${BLUE}🔌 VERIFICANDO PORTAS...${NC}"
echo

# Check if ports are available
if command -v lsof &> /dev/null; then
    if ! lsof -ti:8080 >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Porta 8080 disponível (servidor)${NC}"
        passed_checks=$((passed_checks + 1))
    else
        echo -e "${YELLOW}⚠️  Porta 8080 em uso${NC}"
        echo -e "${BLUE}   💡 Um processo está usando a porta 8080${NC}"
    fi
    total_checks=$((total_checks + 1))
    
    if ! lsof -ti:3001 >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Porta 3001 disponível (cliente)${NC}"
        passed_checks=$((passed_checks + 1))
    else
        echo -e "${YELLOW}⚠️  Porta 3001 em uso${NC}"
        echo -e "${BLUE}   💡 Um processo está usando a porta 3001${NC}"
    fi
    total_checks=$((total_checks + 1))
else
    echo -e "${YELLOW}⚠️  Comando lsof não disponível (não é possível verificar portas)${NC}"
fi

echo
echo -e "${BLUE}🗄️ VERIFICANDO BANCO DE DADOS...${NC}"
echo

# Check database
if [ -f "database/finanza.db" ]; then
    echo -e "${GREEN}✅ Banco de dados existe${NC}"
    passed_checks=$((passed_checks + 1))
    
    # Check if database has tables (basic check)
    if command -v sqlite3 &> /dev/null; then
        table_count=$(sqlite3 database/finanza.db "SELECT COUNT(*) FROM sqlite_master WHERE type='table';" 2>/dev/null)
        if [ "$table_count" -gt 0 ]; then
            echo -e "${GREEN}✅ Banco tem $table_count tabelas${NC}"
            passed_checks=$((passed_checks + 1))
        else
            echo -e "${YELLOW}⚠️  Banco parece vazio${NC}"
        fi
        total_checks=$((total_checks + 1))
    fi
else
    echo -e "${BLUE}ℹ️  Banco será criado na primeira execução${NC}"
fi
total_checks=$((total_checks + 1))

echo
echo -e "${BLUE}🌐 TESTE DE CONECTIVIDADE...${NC}"
echo

# Test if servers are running
if curl -s http://localhost:8080/api/health >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Servidor API está rodando (porta 8080)${NC}"
    passed_checks=$((passed_checks + 1))
else
    echo -e "${BLUE}ℹ️  Servidor API não está rodando${NC}"
    echo -e "${YELLOW}   💡 Execute: ./iniciar_servidor.sh${NC}"
fi
total_checks=$((total_checks + 1))

if curl -s http://localhost:3001 >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Cliente desktop está rodando (porta 3001)${NC}"
    passed_checks=$((passed_checks + 1))
else
    echo -e "${BLUE}ℹ️  Cliente desktop não está rodando${NC}"
    echo -e "${YELLOW}   💡 Execute: ./iniciar_cliente.sh${NC}"
fi
total_checks=$((total_checks + 1))

echo
echo "========================================"
echo -e "${MAGENTA}📊 RESUMO DA VERIFICAÇÃO${NC}"
echo "========================================"

# Calculate percentage
percentage=$((passed_checks * 100 / total_checks))

echo -e "${CYAN}📋 Verificações realizadas: $total_checks${NC}"
echo -e "${GREEN}✅ Verificações aprovadas: $passed_checks${NC}"
echo -e "${RED}❌ Verificações falharam: $((total_checks - passed_checks))${NC}"

if [ $percentage -ge 90 ]; then
    echo -e "${GREEN}🎉 Sistema está $percentage% pronto! Excelente!${NC}"
elif [ $percentage -ge 70 ]; then
    echo -e "${YELLOW}⚠️  Sistema está $percentage% pronto. Alguns ajustes podem ser necessários.${NC}"
else
    echo -e "${RED}⚠️  Sistema está apenas $percentage% pronto. Várias correções são necessárias.${NC}"
fi

echo
echo -e "${BLUE}💡 PRÓXIMOS PASSOS:${NC}"

if [ $passed_checks -lt $total_checks ]; then
    echo "1. Corrija os problemas listados acima"
    echo "2. Execute novamente: ./verificar_sistema.sh"
fi

if [ ! -d "server/node_modules" ] || [ ! -d "DESKTOP VERSION/node_modules" ]; then
    echo "3. Instale as dependências: ./instalar_dependencias.sh"
fi

echo "4. Inicie o sistema: ./iniciar_tudo.sh"
echo "5. Acesse: http://localhost:3001"

echo
read -p "Pressione Enter para continuar..."