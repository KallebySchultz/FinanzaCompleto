@echo off
echo ========================================
echo   FINANZA - INICIANDO CLIENTE DESKTOP
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

echo Navegando para a pasta do cliente desktop...
cd /d "%~dp0DESKTOP VERSION"

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

echo Verificando configuracao do servidor API...
echo IMPORTANTE: Certifique-se de que o servidor API esteja rodando!
echo Execute 'iniciar_servidor.bat' em outra janela se ainda nao fez.
echo.

echo 🖥️ INICIANDO CLIENTE DESKTOP FINANZA...
echo.
echo 📍 Cliente rodara na porta 3001
echo 🌐 Acesso: http://localhost:3001
echo 🔗 Conecta-se ao servidor API na porta 8080
echo.
echo Pressione Ctrl+C para parar o cliente
echo ========================================
echo.

npm start

echo.
echo Cliente desktop encerrado.
pause