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
echo -e "${CYAN}🔍 FINANZA - DESCOBRIR IP DA REDE${NC}"
echo "========================================"
echo

echo -e "${YELLOW}🌐 Descobrindo endereços IP disponíveis...${NC}"
echo

# Function to get IP addresses
get_network_info() {
    echo -e "${BLUE}📡 INTERFACES DE REDE ATIVAS:${NC}"
    echo "----------------------------------------"
    
    # For Linux
    if command -v ip &> /dev/null; then
        ip route get 1.1.1.1 2>/dev/null | grep -oP 'src \K\S+' | head -1 | while read ip; do
            if [ ! -z "$ip" ] && [[ "$ip" != "127.0.0.1" ]]; then
                echo -e "${GREEN}🌟 IP PRINCIPAL: $ip${NC}"
                echo -e "${YELLOW}📱 Configure no app Android: $ip:8080${NC}"
                echo
            fi
        done
        
        echo -e "${CYAN}📋 TODOS OS IPs DISPONÍVEIS:${NC}"
        ip addr show | grep -oP '(?<=inet\s)\d+(\.\d+){3}' | grep -v '127.0.0.1' | while read ip; do
            echo -e "${BLUE}   • $ip${NC}"
        done
        
    # For macOS
    elif command -v ifconfig &> /dev/null; then
        route get 8.8.8.8 2>/dev/null | grep interface | awk '{print $2}' | while read interface; do
            main_ip=$(ifconfig "$interface" 2>/dev/null | grep -oE 'inet ([0-9]{1,3}\.){3}[0-9]{1,3}' | awk '{print $2}' | head -1)
            if [ ! -z "$main_ip" ] && [[ "$main_ip" != "127.0.0.1" ]]; then
                echo -e "${GREEN}🌟 IP PRINCIPAL: $main_ip${NC}"
                echo -e "${YELLOW}📱 Configure no app Android: $main_ip:8080${NC}"
                echo
                break
            fi
        done
        
        echo -e "${CYAN}📋 TODOS OS IPs DISPONÍVEIS:${NC}"
        ifconfig | grep -oE 'inet ([0-9]{1,3}\.){3}[0-9]{1,3}' | awk '{print $2}' | grep -v '127.0.0.1' | while read ip; do
            echo -e "${BLUE}   • $ip${NC}"
        done
    fi
}

# Get network information
get_network_info

echo
echo "----------------------------------------"
echo -e "${YELLOW}💡 INSTRUÇÕES PARA CONFIGURAR O ANDROID:${NC}"
echo
echo -e "${BLUE}1.${NC} Abra o app Finanza no celular"
echo -e "${BLUE}2.${NC} Vá em Configurações → Servidor"
echo -e "${BLUE}3.${NC} Digite o IP principal mostrado acima"
echo -e "${BLUE}4.${NC} Mantenha a porta como 8080"
echo -e "${BLUE}5.${NC} Teste a conexão"
echo
echo -e "${YELLOW}⚠️  IMPORTANTE:${NC}"
echo "• O celular deve estar na MESMA rede WiFi do computador"
echo "• O servidor deve estar rodando (execute ./iniciar_servidor.sh)"
echo "• Se não funcionar, tente os outros IPs listados"
echo
echo -e "${GREEN}🔗 TESTE DE CONECTIVIDADE:${NC}"
echo "Você pode testar se o servidor está acessível visitando:"

# Get the main IP again for the test URLs
if command -v ip &> /dev/null; then
    main_ip=$(ip route get 1.1.1.1 2>/dev/null | grep -oP 'src \K\S+' | head -1)
elif command -v ifconfig &> /dev/null; then
    interface=$(route get 8.8.8.8 2>/dev/null | grep interface | awk '{print $2}' | head -1)
    main_ip=$(ifconfig "$interface" 2>/dev/null | grep -oE 'inet ([0-9]{1,3}\.){3}[0-9]{1,3}' | awk '{print $2}' | head -1)
fi

if [ ! -z "$main_ip" ] && [[ "$main_ip" != "127.0.0.1" ]]; then
    echo -e "${CYAN}   http://$main_ip:8080/api/health${NC}"
    echo
    echo -e "${BLUE}📱 URL completa para o app: $main_ip:8080${NC}"
fi

echo
read -p "Pressione Enter para continuar..."