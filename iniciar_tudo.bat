@echo off
chcp 65001 >nul
color 0D
cls
echo ========================================
echo  🚀 FINANZA - INICIALIZACAO COMPLETA
echo ========================================
echo.

echo 💡 Este script vai:
echo 1️⃣ Iniciar o servidor API (porta 8080)
echo 2️⃣ Iniciar o cliente desktop (porta 3001) 
echo 3️⃣ Abrir automaticamente no navegador
echo.

set /p resposta="Deseja continuar? (S/n): "
if /i "%resposta%"=="n" (
    echo.
    echo Cancelando...
    pause
    exit /b 0
)

echo.
echo 🔄 Iniciando sistema completo...
echo.

echo 📡 Iniciando servidor API...
start "Finanza Server" /min cmd /c "iniciar_servidor.bat"

echo ⏳ Aguardando servidor inicializar...
timeout /t 10 /nobreak >nul

echo 🖥️ Iniciando cliente desktop...
start "Finanza Desktop" /min cmd /c "iniciar_cliente.bat"

echo ⏳ Aguardando cliente inicializar...
timeout /t 5 /nobreak >nul

echo 🌐 Abrindo navegador...
start http://localhost:3001

echo.
echo ✅ Sistema iniciado com sucesso!
echo.
echo 📋 JANELAS ABERTAS:
echo • Finanza Server (minimizada) - NAO FECHE
echo • Finanza Desktop (minimizada) - NAO FECHE  
echo • Navegador com o sistema
echo.
echo 💡 PARA USAR NO CELULAR:
echo 1. Execute: descobrir_ip.bat
echo 2. Configure o app Android com seu IP
echo.
echo 🛑 PARA PARAR TUDO:
echo 1. Feche as janelas do servidor e cliente
echo 2. Ou execute: parar_sistema.bat
echo.

pause