#!/bin/bash

# 🔧 Finanza Development Helper Script
# Facilitates common development tasks

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Print functions
print_header() {
    echo -e "${PURPLE}
╔═══════════════════════════════════════╗
║            🔧 FINANZA DEV             ║
║       Development Helper Script       ║
╚═══════════════════════════════════════╝${NC}"
}

print_section() {
    echo -e "\n${CYAN}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Main menu
show_menu() {
    echo -e "\n${YELLOW}Escolha uma opção:${NC}"
    echo "1. 🚀 Iniciar servidor de desenvolvimento"
    echo "2. 🧪 Testar API"
    echo "3. 🗄️  Resetar banco de dados"
    echo "4. 📊 Status do sistema"
    echo "5. 🔧 Configurar ambiente"
    echo "6. 📱 Descobrir IP para Android"
    echo "7. 🧹 Limpar cache e dependências"
    echo "8. 📦 Instalar/atualizar dependências"
    echo "9. 📖 Mostrar documentação"
    echo "10. 🔍 Verificar logs"
    echo "0. ❌ Sair"
    echo ""
}

# Start development server
start_dev_server() {
    print_section "Iniciando servidor de desenvolvimento"
    
    if [ ! -d "server/node_modules" ]; then
        print_warning "Dependências não encontradas. Instalando..."
        cd server && npm install && cd ..
    fi
    
    print_info "Servidor será iniciado em modo desenvolvimento com auto-reload"
    cd server
    npm run dev
}

# Test API endpoints
test_api() {
    print_section "Testando endpoints da API"
    
    # Check if server is running
    if ! curl -s http://localhost:8080/api/health > /dev/null; then
        print_error "Servidor não está rodando. Inicie-o primeiro."
        return 1
    fi
    
    print_info "Testando health check..."
    HEALTH=$(curl -s http://localhost:8080/api/health)
    echo "$HEALTH" | jq . 2>/dev/null || echo "$HEALTH"
    
    print_info "Testando registro de usuário..."
    REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
        -H "Content-Type: application/json" \
        -d "{\"nome\":\"Teste $(date +%s)\",\"email\":\"teste$(date +%s)@example.com\",\"senha\":\"123456\"}")
    
    echo "$REGISTER_RESPONSE" | jq . 2>/dev/null || echo "$REGISTER_RESPONSE"
    
    # Extract token for further tests
    TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token' 2>/dev/null)
    
    if [ "$TOKEN" != "null" ] && [ "$TOKEN" != "" ]; then
        print_success "Token obtido com sucesso"
        
        print_info "Testando criação de conta..."
        ACCOUNT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/accounts \
            -H "Authorization: Bearer $TOKEN" \
            -H "Content-Type: application/json" \
            -d '{"nome":"Conta Teste","saldo_inicial":1000.00}')
        
        echo "$ACCOUNT_RESPONSE" | jq . 2>/dev/null || echo "$ACCOUNT_RESPONSE"
        
        print_info "Testando resumo financeiro..."
        SUMMARY_RESPONSE=$(curl -s -X GET http://localhost:8080/api/users/financial-summary \
            -H "Authorization: Bearer $TOKEN")
        
        echo "$SUMMARY_RESPONSE" | jq . 2>/dev/null || echo "$SUMMARY_RESPONSE"
    fi
    
    print_success "Testes da API concluídos!"
}

# Reset database
reset_database() {
    print_section "Resetando banco de dados"
    
    print_warning "Esta ação irá remover todos os dados!"
    read -p "Tem certeza? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if [ -f "database/finanza.db" ]; then
            rm database/finanza.db
            print_success "Banco de dados removido"
        fi
        
        if command -v sqlite3 &> /dev/null; then
            sqlite3 database/finanza.db < database/finanza.sql
            print_success "Banco de dados recriado com dados de exemplo"
        else
            print_info "SQLite3 não encontrado. O banco será criado na próxima inicialização."
        fi
    else
        print_info "Operação cancelada"
    fi
}

# System status
show_status() {
    print_section "Status do Sistema"
    
    # Check Node.js
    if command -v node &> /dev/null; then
        NODE_VERSION=$(node --version)
        print_success "Node.js: $NODE_VERSION"
    else
        print_error "Node.js não encontrado"
    fi
    
    # Check npm dependencies
    if [ -d "server/node_modules" ]; then
        DEPS_COUNT=$(find server/node_modules -mindepth 1 -maxdepth 1 -type d | wc -l)
        print_success "Dependências NPM: $DEPS_COUNT packages instalados"
    else
        print_warning "Dependências NPM não instaladas"
    fi
    
    # Check server status
    if curl -s http://localhost:8080/api/health > /dev/null; then
        UPTIME=$(curl -s http://localhost:8080/api/health | jq -r '.uptime' 2>/dev/null || echo "N/A")
        print_success "Servidor: Online (uptime: ${UPTIME}s)"
    else
        print_warning "Servidor: Offline"
    fi
    
    # Check database
    if [ -f "database/finanza.db" ]; then
        DB_SIZE=$(du -h database/finanza.db | cut -f1)
        print_success "Banco de dados: Existe ($DB_SIZE)"
    else
        print_warning "Banco de dados: Não encontrado"
    fi
    
    # Check Android Gradle
    if [ -f "gradlew" ]; then
        print_success "Android: Gradle wrapper disponível"
    else
        print_warning "Android: Gradle wrapper não encontrado"
    fi
}

# Setup environment
setup_environment() {
    print_section "Configurando ambiente de desenvolvimento"
    
    # Install server dependencies
    if [ ! -d "server/node_modules" ]; then
        print_info "Instalando dependências do servidor..."
        cd server && npm install && cd ..
    fi
    
    # Create .env if not exists
    if [ ! -f "server/.env" ]; then
        print_info "Criando arquivo .env..."
        cat > server/.env << EOL
NODE_ENV=development
PORT=8080
JWT_SECRET=finanza-dev-secret-$(date +%s)
DB_PATH=../database/finanza.db
CORS_ORIGIN=*
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100
EOL
        print_success "Arquivo .env criado"
    fi
    
    # Initialize database
    if [ ! -f "database/finanza.db" ] && command -v sqlite3 &> /dev/null; then
        print_info "Inicializando banco de dados..."
        sqlite3 database/finanza.db < database/finanza.sql
        print_success "Banco de dados inicializado"
    fi
    
    print_success "Ambiente configurado com sucesso!"
}

# Discover IP for Android
discover_ip() {
    print_section "Descobrindo IP para configuração do Android"
    
    print_info "IPs encontrados na rede:"
    
    # Try different methods to get IP
    if command -v ip &> /dev/null; then
        ip addr show | grep -E "inet.*brd" | awk '{print "  🌐 " $2}' | sed 's/\/.*//g'
    elif command -v ifconfig &> /dev/null; then
        ifconfig | grep -E "inet.*broadcast" | awk '{print "  🌐 " $2}'
    else
        print_warning "Comandos ip/ifconfig não encontrados"
    fi
    
    print_info "Configure o Android app para usar um destes IPs na porta 8080"
    print_info "Exemplo: http://192.168.1.100:8080"
}

# Clean cache and dependencies
clean_cache() {
    print_section "Limpando cache e dependências"
    
    print_warning "Esta ação irá remover node_modules e caches"
    read -p "Continuar? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        # Clean npm cache
        if [ -d "server/node_modules" ]; then
            rm -rf server/node_modules
            print_success "node_modules removido"
        fi
        
        # Clean npm cache
        npm cache clean --force 2>/dev/null || true
        print_success "Cache NPM limpo"
        
        # Clean Gradle cache (if exists)
        if [ -d ".gradle" ]; then
            rm -rf .gradle
            print_success "Cache Gradle limpo"
        fi
        
        print_success "Limpeza concluída!"
        print_info "Execute 'npm install' para reinstalar dependências"
    else
        print_info "Operação cancelada"
    fi
}

# Install/update dependencies
install_dependencies() {
    print_section "Instalando/atualizando dependências"
    
    print_info "Instalando dependências do servidor..."
    cd server
    npm install
    cd ..
    
    print_info "Verificando atualizações disponíveis..."
    cd server
    npm outdated || true
    cd ..
    
    print_success "Dependências atualizadas!"
}

# Show documentation
show_documentation() {
    print_section "Documentação Disponível"
    
    echo "📖 Documentos principais:"
    echo "  • README.md - Visão geral do projeto"
    echo "  • DESENVOLVIMENTO.md - Guia de desenvolvimento"
    echo "  • API_DOCS.md - Documentação da API"
    echo "  • FIREBASE_SETUP.md - Configuração do Firebase"
    echo ""
    echo "🌐 URLs importantes:"
    echo "  • API Health: http://localhost:8080/api/health"
    echo "  • Documentação online: https://github.com/KallebySchultz/Finanza"
    echo ""
    
    read -p "Abrir algum documento? (README/DEV/API/FIREBASE/N): " -r
    case $REPLY in
        README|readme)
            cat README.md | head -50
            ;;
        DEV|dev)
            cat DESENVOLVIMENTO.md | head -50
            ;;
        API|api)
            cat API_DOCS.md | head -50
            ;;
        FIREBASE|firebase)
            cat FIREBASE_SETUP.md | head -50
            ;;
        *)
            print_info "Documentação disponível nos arquivos .md"
            ;;
    esac
}

# Check logs
check_logs() {
    print_section "Verificando logs do sistema"
    
    print_info "Logs recentes do servidor:"
    if [ -f "server/logs/error.log" ]; then
        tail -20 server/logs/error.log
    elif [ -f "server/npm-debug.log" ]; then
        tail -20 server/npm-debug.log
    else
        print_info "Nenhum arquivo de log encontrado"
        print_info "Para ver logs em tempo real, use: npm run dev"
    fi
}

# Main function
main() {
    print_header
    
    while true; do
        show_menu
        read -p "Digite sua opção (0-10): " choice
        
        case $choice in
            1) start_dev_server ;;
            2) test_api ;;
            3) reset_database ;;
            4) show_status ;;
            5) setup_environment ;;
            6) discover_ip ;;
            7) clean_cache ;;
            8) install_dependencies ;;
            9) show_documentation ;;
            10) check_logs ;;
            0) 
                print_success "Obrigado por usar o Finanza! 👋"
                exit 0
                ;;
            *)
                print_error "Opção inválida. Tente novamente."
                ;;
        esac
        
        echo ""
        read -p "Pressione Enter para continuar..." -r
    done
}

# Run main function
main "$@"