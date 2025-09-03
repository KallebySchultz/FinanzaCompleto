#!/bin/bash

# 🏦 Finanza Desktop Version - Startup Script
# Inicia servidor e cliente desktop automaticamente

echo "🏦 ===== FINANZA DESKTOP VERSION ====="
echo ""

# Função para verificar se Java está instalado
check_java() {
    if ! command -v java &> /dev/null; then
        echo "❌ Java não encontrado. Por favor, instale o Java JDK 8 ou superior."
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "✅ Java encontrado: $java_version"
}

# Função para verificar se Maven está instalado
check_maven() {
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven não encontrado. Por favor, instale o Apache Maven."
        exit 1
    fi
    
    mvn_version=$(mvn -version | head -n 1)
    echo "✅ Maven encontrado: $mvn_version"
}

# Função para compilar e iniciar o servidor
start_server() {
    echo ""
    echo "🔧 Compilando servidor..."
    cd Server
    mvn clean compile -q
    
    if [ $? -eq 0 ]; then
        echo "✅ Servidor compilado com sucesso"
        echo "🚀 Iniciando servidor..."
        echo ""
        
        # Iniciar servidor em background
        mvn exec:java -Dexec.mainClass="com.finanza.server.FinanzaServer" &
        SERVER_PID=$!
        
        # Aguardar servidor inicializar
        sleep 3
        echo "✅ Servidor iniciado (PID: $SERVER_PID)"
        echo "📡 Servidor rodando na porta 8080"
    else
        echo "❌ Erro ao compilar servidor"
        exit 1
    fi
    
    cd ..
}

# Função para compilar e iniciar o cliente desktop
start_desktop() {
    echo ""
    echo "🔧 Compilando cliente desktop..."
    cd "Desktop Client"
    mvn clean compile -q
    
    if [ $? -eq 0 ]; then
        echo "✅ Cliente desktop compilado com sucesso"
        echo "🖥️ Iniciando cliente desktop..."
        echo ""
        
        # Iniciar cliente desktop
        mvn exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop" &
        DESKTOP_PID=$!
        
        echo "✅ Cliente desktop iniciado (PID: $DESKTOP_PID)"
    else
        echo "❌ Erro ao compilar cliente desktop"
        exit 1
    fi
    
    cd ..
}

# Função para mostrar informações de rede
show_network_info() {
    echo ""
    echo "🌐 ===== INFORMAÇÕES DE REDE ====="
    echo ""
    
    # Detectar IP local
    if command -v ip &> /dev/null; then
        local_ip=$(ip route get 1.1.1.1 | grep -oP 'src \K\S+' 2>/dev/null)
    elif command -v ifconfig &> /dev/null; then
        local_ip=$(ifconfig | grep -oP 'inet \K192\.168\.\d+\.\d+' | head -n 1)
    else
        local_ip="[Não detectado]"
    fi
    
    echo "📍 IP local detectado: $local_ip"
    echo "🔌 Porta do servidor: 8080"
    echo ""
    echo "📱 Para conectar o app móvel:"
    echo "   • Configure o IP: $local_ip"
    echo "   • Configure a porta: 8080"
    echo "   • Certifique-se que estão na mesma rede WiFi"
    echo ""
    echo "🖥️ Para configurar o cliente desktop:"
    echo "   • Abra o cliente desktop"
    echo "   • Vá em 'Configurações'"
    echo "   • Use IP: $local_ip (ou localhost para teste local)"
    echo ""
}

# Função para aguardar e finalizar
wait_and_cleanup() {
    echo "✨ ===== SISTEMA INICIADO ====="
    echo ""
    echo "🎯 Como usar:"
    echo "   1. Configure a rede no cliente desktop (Configurações)"
    echo "   2. Configure o IP no app móvel ($local_ip:8080)"
    echo "   3. Teste a conexão em ambos"
    echo "   4. Comece a sincronizar dados!"
    echo ""
    echo "🛑 Para parar o sistema, pressione Ctrl+C"
    echo ""
    
    # Aguardar Ctrl+C
    trap 'echo ""; echo "🛑 Parando sistema..."; kill $SERVER_PID $DESKTOP_PID 2>/dev/null; echo "✅ Sistema parado"; exit 0' INT
    
    # Loop infinito aguardando interrupção
    while true; do
        sleep 1
    done
}

# Script principal
main() {
    check_java
    check_maven
    start_server
    start_desktop
    show_network_info
    wait_and_cleanup
}

# Executar script principal
main