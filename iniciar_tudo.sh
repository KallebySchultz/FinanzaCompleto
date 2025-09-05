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
echo -e "${MAGENTA}  🚀 FINANZA - INICIALIZAÇÃO COMPLETA${NC}"
echo "========================================"
echo

echo -e "${YELLOW}💡 Este script vai:${NC}"
echo -e "${BLUE}1️⃣ Iniciar o servidor API (porta 8080)${NC}"
echo -e "${BLUE}2️⃣ Iniciar o cliente desktop (porta 3001)${NC}" 
echo -e "${BLUE}3️⃣ Abrir automaticamente no navegador${NC}"
echo

read -p "Deseja continuar? (S/n): " resposta
if [[ "$resposta" =~ ^[Nn]$ ]]; then
    echo
    echo "Cancelando..."
    read -p "Pressione Enter para continuar..."
    exit 0
fi

echo
echo -e "${CYAN}🔄 Iniciando sistema completo...${NC}"
echo

# Check if we're on macOS or Linux to handle browser opening
if [[ "$OSTYPE" == "darwin"* ]]; then
    BROWSER_CMD="open"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    BROWSER_CMD="xdg-open"
else
    BROWSER_CMD="echo 'Abra manualmente:'"
fi

echo -e "${BLUE}📡 Iniciando servidor API...${NC}"
# Start server in background
gnome-terminal --title="Finanza Server" -- bash -c "./iniciar_servidor.sh" 2>/dev/null || \
xterm -title "Finanza Server" -e "./iniciar_servidor.sh" 2>/dev/null || \
./iniciar_servidor.sh &

echo -e "${YELLOW}⏳ Aguardando servidor inicializar...${NC}"
sleep 10

# Wait for server to be ready
echo -e "${CYAN}🔍 Verificando se servidor está pronto...${NC}"
counter=0
max_attempts=30
while [ $counter -lt $max_attempts ]; do
    if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Servidor API está rodando!${NC}"
        break
    fi
    sleep 1
    counter=$((counter + 1))
    if [ $((counter % 5)) -eq 0 ]; then
        echo -e "${YELLOW}⏳ Ainda aguardando servidor... (tentativa $counter/$max_attempts)${NC}"
    fi
done

if [ $counter -eq $max_attempts ]; then
    echo -e "${RED}❌ ERRO: Servidor não respondeu no tempo esperado!${NC}"
    echo -e "${YELLOW}💡 Tente executar './iniciar_servidor.sh' manualmente primeiro${NC}"
    exit 1
fi

echo -e "${BLUE}🖥️ Iniciando cliente desktop...${NC}"
# Start client in background
gnome-terminal --title="Finanza Desktop" -- bash -c "./iniciar_cliente.sh" 2>/dev/null || \
xterm -title "Finanza Desktop" -e "./iniciar_cliente.sh" 2>/dev/null || \
./iniciar_cliente.sh &

echo -e "${YELLOW}⏳ Aguardando cliente inicializar...${NC}"
sleep 5

# Wait for client to be ready
echo -e "${CYAN}🔍 Verificando se cliente está pronto...${NC}"
counter=0
max_attempts=20
while [ $counter -lt $max_attempts ]; do
    if curl -s http://localhost:3001 > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Cliente desktop está rodando!${NC}"
        break
    fi
    sleep 1
    counter=$((counter + 1))
    if [ $((counter % 5)) -eq 0 ]; then
        echo -e "${YELLOW}⏳ Ainda aguardando cliente... (tentativa $counter/$max_attempts)${NC}"
    fi
done

echo -e "${BLUE}🌐 Abrindo navegador...${NC}"
$BROWSER_CMD http://localhost:3001 2>/dev/null || echo -e "${YELLOW}Abra manualmente: http://localhost:3001${NC}"

echo
echo -e "${GREEN}✅ Sistema iniciado com sucesso!${NC}"
echo
echo -e "${CYAN}📋 SERVIÇOS RODANDO:${NC}"
echo -e "${GREEN}• Finanza Server (porta 8080) - NÃO FECHE${NC}"
echo -e "${GREEN}• Finanza Desktop (porta 3001) - NÃO FECHE${NC}"  
echo -e "${GREEN}• Navegador com o sistema${NC}"
echo
echo -e "${YELLOW}💡 PARA USAR NO CELULAR:${NC}"
echo "1. Execute: ./descobrir_ip.sh"
echo "2. Configure o app Android com seu IP"
echo
echo -e "${YELLOW}🛑 PARA PARAR TUDO:${NC}"
echo "1. Feche os terminais do servidor e cliente"
echo "2. Ou execute: ./parar_sistema.sh"
echo
echo -e "${BLUE}🔗 URL do sistema: http://localhost:3001${NC}"
echo -e "${BLUE}📧 Login padrão: admin@finanza.com${NC}"
echo -e "${BLUE}🔑 Senha padrão: admin${NC}"
echo

read -p "Pressione Enter para continuar..."