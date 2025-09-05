@echo off
chcp 65001 >nul
color 0A
cls
echo ========================================
echo    🚀 FINANZA - SERVIDOR API
echo ========================================
echo.

echo 🔍 Verificando se Node.js esta instalado...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo.
    echo ❌ ERRO: Node.js nao foi encontrado!
    echo.
    echo 📥 SOLUCAO:
    echo 1. Acesse: https://nodejs.org
    echo 2. Baixe a versao LTS (recomendada)
    echo 3. Instale normalmente
    echo 4. Reinicie o computador
    echo 5. Execute este arquivo novamente
    echo.
    pause
    exit /b 1
)

echo ✅ Node.js encontrado
echo.

echo 📁 Navegando para a pasta do servidor...
cd /d "%~dp0server"

echo 🔍 Verificando dependencias...
if not exist "node_modules" (
    echo.
    echo 📦 Primeira execucao detectada!
    echo 📦 Instalando dependencias... (isso pode demorar alguns minutos)
    echo.
    npm install
    if %errorlevel% neq 0 (
        color 0C
        echo.
        echo ❌ ERRO: Falha ao instalar dependencias!
        echo.
        echo 🔧 SOLUCOES POSSIVEIS:
        echo 1. Verifique sua conexao com a internet
        echo 2. Execute como administrador
        echo 3. Feche programas antivirus temporariamente
        echo.
        pause
        exit /b 1
    )
    echo ✅ Dependencias instaladas com sucesso!
    echo.
)

echo ✅ Dependencias OK
echo.

echo 🗄️ Verificando banco de dados...
if not exist "../database/finanza.db" (
    echo 🔧 Banco sera criado automaticamente na primeira execucao
    if exist "../database/finanza.sql" (
        echo ✅ Schema SQL encontrado
    ) else (
        echo ⚠️  AVISO: Arquivo finanza.sql nao encontrado em database/
    )
) else (
    echo ✅ Banco de dados encontrado
)

echo.
echo ========================================
echo         🚀 INICIANDO SERVIDOR
echo ========================================
echo.
echo 📍 Servidor rodara na porta 8080
echo 🌐 Acesso local: http://localhost:8080/api/health
echo 📱 Para Android: Configure o IP da sua rede
echo.
echo 💡 DICAS:
echo • Para descobrir seu IP, execute: descobrir_ip.bat
echo • Configure no app Android: [SEU_IP]:8080
echo • Mantenha esta janela ABERTA enquanto usar o sistema
echo • Para parar: Pressione Ctrl+C
echo.
echo ========================================
echo.

rem Define variaveis de ambiente
set PORT=8080
set CORS_ORIGIN=*
set NODE_ENV=production

echo 🔄 Iniciando servidor...
npm start

echo.
echo 🛑 Servidor encerrado.
echo.
pause