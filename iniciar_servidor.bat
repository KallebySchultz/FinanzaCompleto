@echo off
echo ========================================
echo    FINANZA - INICIANDO SERVIDOR API
echo ========================================
echo.

echo Verificando se Node.js esta instalado...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Node.js nao foi encontrado!
    echo Por favor, instale o Node.js a partir de: https://nodejs.org
    echo.
    pause
    exit /b 1
)

echo ✅ Node.js encontrado
echo.

echo Navegando para a pasta do servidor...
cd /d "%~dp0server"

echo Verificando dependencias...
if not exist "node_modules" (
    echo 📦 Instalando dependencias do npm...
    npm install
    if %errorlevel% neq 0 (
        echo ERRO: Falha ao instalar dependencias!
        pause
        exit /b 1
    )
)

echo ✅ Dependencias verificadas
echo.

echo Verificando banco de dados...
if not exist "../database/finanza.db" (
    echo 🗄️ Criando banco de dados...
    if exist "../database/finanza.sql" (
        echo Banco sera inicializado automaticamente pelo servidor.
    ) else (
        echo AVISO: Arquivo finanza.sql nao encontrado em database/
    )
)

echo.
echo 🚀 INICIANDO SERVIDOR FINANZA API...
echo.
echo 📍 Servidor rodara na porta 8080 (compativel com Android)
echo 🌐 Acesso local: http://localhost:8080
echo 📱 Acesso rede local: http://[SEU_IP]:8080
echo.
echo Para conectar o celular Android:
echo 1. Conecte o celular na mesma rede WiFi do computador
echo 2. Descubra o IP do seu computador (ipconfig)
echo 3. Configure o IP no app Android (ex: 192.168.1.100:8080)
echo.
echo Pressione Ctrl+C para parar o servidor
echo ========================================
echo.

rem Define a porta 8080 para compatibilidade com Android
set PORT=8080
set CORS_ORIGIN=*

npm start

echo.
echo Servidor encerrado.
pause