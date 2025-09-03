@echo off
REM 🏦 Finanza Desktop Version - Startup Script (Windows)
REM Inicia servidor e cliente desktop automaticamente

echo 🏦 ===== FINANZA DESKTOP VERSION =====
echo.

REM Verificar se Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java não encontrado. Por favor, instale o Java JDK 8 ou superior.
    pause
    exit /b 1
)

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%g
echo ✅ Java encontrado: %JAVA_VERSION%

REM Verificar se Maven está instalado
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven não encontrado. Por favor, instale o Apache Maven.
    pause
    exit /b 1
)

echo ✅ Maven encontrado

echo.
echo 🔧 Compilando servidor...
cd Server
call mvn clean compile -q

if %errorlevel% neq 0 (
    echo ❌ Erro ao compilar servidor
    pause
    exit /b 1
)

echo ✅ Servidor compilado com sucesso
echo 🚀 Iniciando servidor...

REM Iniciar servidor em background
start "Finanza Server" cmd /c "mvn exec:java -Dexec.mainClass=com.finanza.server.FinanzaServer"

REM Aguardar servidor inicializar
timeout /t 3 /nobreak >nul

echo ✅ Servidor iniciado
echo 📡 Servidor rodando na porta 8080

cd ..

echo.
echo 🔧 Compilando cliente desktop...
cd "Desktop Client"
call mvn clean compile -q

if %errorlevel% neq 0 (
    echo ❌ Erro ao compilar cliente desktop
    pause
    exit /b 1
)

echo ✅ Cliente desktop compilado com sucesso
echo 🖥️ Iniciando cliente desktop...

REM Iniciar cliente desktop
start "Finanza Desktop" cmd /c "mvn exec:java -Dexec.mainClass=com.finanza.desktop.FinanzaDesktop"

echo ✅ Cliente desktop iniciado

cd ..

echo.
echo 🌐 ===== INFORMAÇÕES DE REDE =====
echo.

REM Detectar IP local (Windows)
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do set LOCAL_IP=%%a
set LOCAL_IP=%LOCAL_IP: =%

echo 📍 IP local detectado: %LOCAL_IP%
echo 🔌 Porta do servidor: 8080
echo.
echo 📱 Para conectar o app móvel:
echo    • Configure o IP: %LOCAL_IP%
echo    • Configure a porta: 8080
echo    • Certifique-se que estão na mesma rede WiFi
echo.
echo 🖥️ Para configurar o cliente desktop:
echo    • Abra o cliente desktop
echo    • Vá em 'Configurações'
echo    • Use IP: %LOCAL_IP% (ou localhost para teste local)
echo.

echo ✨ ===== SISTEMA INICIADO =====
echo.
echo 🎯 Como usar:
echo    1. Configure a rede no cliente desktop (Configurações)
echo    2. Configure o IP no app móvel (%LOCAL_IP%:8080)
echo    3. Teste a conexão em ambos
echo    4. Comece a sincronizar dados!
echo.
echo 🛑 Para parar o sistema, feche esta janela ou pressione qualquer tecla
echo.

pause