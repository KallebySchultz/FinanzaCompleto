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
echo -e "${RED}🛑 FINANZA - PARAR SISTEMA${NC}"
echo "========================================"
echo

echo -e "${YELLOW}⏳ Parando serviços Finanza...${NC}"

# Kill processes on specific ports
echo -e "${CYAN}🔍 Procurando processos nas portas 8080 e 3001...${NC}"

# Find and kill processes on port 8080 (server)
SERVER_PID=$(lsof -ti:8080 2>/dev/null)
if [ ! -z "$SERVER_PID" ]; then
    echo -e "${YELLOW}🛑 Parando servidor API (PID: $SERVER_PID)...${NC}"
    kill -TERM $SERVER_PID 2>/dev/null
    sleep 2
    if kill -0 $SERVER_PID 2>/dev/null; then
        echo -e "${RED}⚠️  Forçando parada do servidor...${NC}"
        kill -KILL $SERVER_PID 2>/dev/null
    fi
    echo -e "${GREEN}✅ Servidor API parado${NC}"
else
    echo -e "${BLUE}ℹ️  Nenhum processo encontrado na porta 8080${NC}"
fi

# Find and kill processes on port 3001 (client)
CLIENT_PID=$(lsof -ti:3001 2>/dev/null)
if [ ! -z "$CLIENT_PID" ]; then
    echo -e "${YELLOW}🛑 Parando cliente desktop (PID: $CLIENT_PID)...${NC}"
    kill -TERM $CLIENT_PID 2>/dev/null
    sleep 2
    if kill -0 $CLIENT_PID 2>/dev/null; then
        echo -e "${RED}⚠️  Forçando parada do cliente...${NC}"
        kill -KILL $CLIENT_PID 2>/dev/null
    fi
    echo -e "${GREEN}✅ Cliente desktop parado${NC}"
else
    echo -e "${BLUE}ℹ️  Nenhum processo encontrado na porta 3001${NC}"
fi

# Kill any remaining node processes related to finanza
echo -e "${CYAN}🔍 Procurando outros processos Node.js relacionados...${NC}"
FINANZA_PIDS=$(pgrep -f "finanza\|server\.js" 2>/dev/null | grep -v $$)
if [ ! -z "$FINANZA_PIDS" ]; then
    echo -e "${YELLOW}🛑 Parando processos adicionais...${NC}"
    echo "$FINANZA_PIDS" | xargs kill -TERM 2>/dev/null
    sleep 2
    echo "$FINANZA_PIDS" | xargs kill -KILL 2>/dev/null
    echo -e "${GREEN}✅ Processos adicionais parados${NC}"
fi

echo
echo -e "${GREEN}✅ Sistema Finanza parado completamente!${NC}"
echo
echo -e "${BLUE}💡 Para iniciar novamente:${NC}"
echo "• Execute: ./iniciar_tudo.sh (completo)"
echo "• Ou: ./iniciar_servidor.sh + ./iniciar_cliente.sh (separadamente)"
echo

read -p "Pressione Enter para continuar..."