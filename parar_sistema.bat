@echo off
chcp 65001 >nul
color 0C
echo ========================================
echo    🛑 FINANZA - PARAR SISTEMA
echo ========================================
echo.

echo 🔍 Procurando processos do Finanza...
echo.

echo 🛑 Parando servidor Node.js na porta 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080"') do (
    echo Matando processo %%a
    taskkill /f /pid %%a >nul 2>&1
)

echo 🛑 Parando cliente Node.js na porta 3001...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3001"') do (
    echo Matando processo %%a  
    taskkill /f /pid %%a >nul 2>&1
)

echo 🛑 Fechando janelas do Finanza...
taskkill /f /fi "WindowTitle eq Finanza Server*" >nul 2>&1
taskkill /f /fi "WindowTitle eq Finanza Desktop*" >nul 2>&1

echo.
echo ✅ Sistema Finanza parado!
echo.
echo 💡 Para reiniciar, execute:
echo • iniciar_tudo.bat (automatico)
echo • ou iniciar_servidor.bat + iniciar_cliente.bat (manual)
echo.

pause