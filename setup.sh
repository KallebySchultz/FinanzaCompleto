#!/bin/bash

# 🚀 Script de Setup do Finanza
# Configura o ambiente de desenvolvimento automaticamente

echo "💰 Finanza - Setup do Ambiente de Desenvolvimento"
echo "================================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se Node.js está instalado
check_nodejs() {
    print_status "Verificando Node.js..."
    if command -v node &> /dev/null; then
        NODE_VERSION=$(node --version)
        print_success "Node.js encontrado: $NODE_VERSION"
        
        # Verificar se a versão é >= 18
        NODE_MAJOR=$(echo $NODE_VERSION | cut -d'.' -f1 | cut -d'v' -f2)
        if [ "$NODE_MAJOR" -ge 18 ]; then
            print_success "Versão do Node.js é adequada (>=18)"
        else
            print_warning "Versão do Node.js é antiga. Recomendado: >=18"
        fi
    else
        print_error "Node.js não encontrado. Instale a versão 18 ou superior."
        exit 1
    fi
}

# Instalar dependências do servidor
install_server_deps() {
    print_status "Instalando dependências do servidor..."
    cd server
    
    if [ -f "package.json" ]; then
        npm install
        if [ $? -eq 0 ]; then
            print_success "Dependências do servidor instaladas com sucesso!"
        else
            print_error "Erro ao instalar dependências do servidor"
            exit 1
        fi
    else
        print_error "package.json não encontrado no diretório server/"
        exit 1
    fi
    
    cd ..
}

# Verificar e criar arquivo .env se necessário
setup_env() {
    print_status "Configurando variáveis de ambiente..."
    
    if [ ! -f "server/.env" ]; then
        print_warning "Arquivo .env não encontrado. Criando um padrão..."
        
        cat > server/.env << EOL
# Environment variables
NODE_ENV=development
PORT=8080
JWT_SECRET=finanza-super-secret-key-$(date +%s)
DB_PATH=../database/finanza.db

# CORS settings - Allow all origins for local network access
CORS_ORIGIN=*

# Rate limiting
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100

# Firebase (configure as needed)
# FIREBASE_PROJECT_ID=your-project-id
# FIREBASE_PRIVATE_KEY=your-private-key
# FIREBASE_CLIENT_EMAIL=your-client-email
EOL
        print_success "Arquivo .env criado com configurações padrão"
        print_warning "IMPORTANTE: Configure as credenciais do Firebase no arquivo .env"
    else
        print_success "Arquivo .env já existe"
    fi
}

# Inicializar banco de dados
init_database() {
    print_status "Inicializando banco de dados..."
    
    if [ -f "database/finanza.sql" ]; then
        # Criar diretório database se não existir
        mkdir -p database
        
        # Verificar se SQLite está disponível
        if command -v sqlite3 &> /dev/null; then
            sqlite3 database/finanza.db < database/finanza.sql
            print_success "Banco de dados inicializado!"
        else
            print_warning "SQLite3 não encontrado. O banco será criado automaticamente na primeira execução."
        fi
    else
        print_error "Schema SQL não encontrado em database/finanza.sql"
    fi
}

# Descobrir IP da rede local
discover_ip() {
    print_status "Descobrindo IP da rede local..."
    
    # Tentar diferentes métodos para descobrir o IP
    LOCAL_IP=""
    
    # Método 1: ip route (Linux)
    if command -v ip &> /dev/null; then
        LOCAL_IP=$(ip route get 1.1.1.1 | grep -oP 'src \K\S+' 2>/dev/null)
    fi
    
    # Método 2: ifconfig (macOS/Linux)
    if [ -z "$LOCAL_IP" ] && command -v ifconfig &> /dev/null; then
        LOCAL_IP=$(ifconfig | grep -E "inet.*broadcast" | awk '{print $2}' | head -1)
    fi
    
    # Método 3: hostname (fallback)
    if [ -z "$LOCAL_IP" ] && command -v hostname &> /dev/null; then
        LOCAL_IP=$(hostname -I | awk '{print $1}' 2>/dev/null)
    fi
    
    if [ -n "$LOCAL_IP" ]; then
        print_success "IP da rede local: $LOCAL_IP"
        print_status "Configure o Android app para usar: http://$LOCAL_IP:8080"
    else
        print_warning "Não foi possível descobrir o IP automaticamente"
        print_status "Use o comando 'ifconfig' ou 'ip addr' para descobrir o IP manualmente"
    fi
}

# Testar servidor
test_server() {
    print_status "Testando servidor..."
    
    # Iniciar servidor em background
    cd server
    npm start &
    SERVER_PID=$!
    cd ..
    
    # Aguardar alguns segundos para o servidor inicializar
    sleep 5
    
    # Testar endpoint de health
    if command -v curl &> /dev/null; then
        RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/health 2>/dev/null)
        
        if [ "$RESPONSE" = "200" ]; then
            print_success "Servidor está funcionando! (HTTP 200)"
            print_success "Health check: http://localhost:8080/api/health"
        else
            print_warning "Servidor pode não estar funcionando corretamente (HTTP $RESPONSE)"
        fi
    else
        print_warning "curl não encontrado. Não foi possível testar o servidor automaticamente"
    fi
    
    # Parar servidor
    kill $SERVER_PID 2>/dev/null
    sleep 2
}

# Gerar relatório final
generate_report() {
    echo ""
    echo "🎉 Setup Concluído!"
    echo "==================="
    echo ""
    echo "📋 Próximos passos:"
    echo "1. Configure as credenciais do Firebase no arquivo server/.env"
    echo "2. Inicie o servidor: cd server && npm start"
    echo "3. Abra o projeto Android no Android Studio"
    echo "4. Configure o IP do servidor no app Android"
    echo "5. Execute o app em um device/emulador"
    echo ""
    echo "🔗 URLs importantes:"
    echo "- API Health: http://localhost:8080/api/health"
    echo "- Documentação: ./DESENVOLVIMENTO.md"
    echo ""
    echo "💡 Dicas:"
    echo "- Use 'npm run dev' para desenvolvimento com auto-reload"
    echo "- Verifique os logs do servidor para debugging"
    echo "- Configure CORS_ORIGIN=* para desenvolvimento local"
    echo ""
}

# Função principal
main() {
    echo ""
    print_status "Iniciando setup do Finanza..."
    echo ""
    
    check_nodejs
    install_server_deps
    setup_env
    init_database
    discover_ip
    test_server
    generate_report
    
    print_success "Setup concluído com sucesso! 🚀"
}

# Executar função principal
main "$@"