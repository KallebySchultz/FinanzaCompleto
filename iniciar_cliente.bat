@echo off
chcp 65001 >nul
color 0B
cls
echo ========================================
echo   🖥️ FINANZA - CLIENTE DESKTOP
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

echo 📁 Navegando para a pasta do cliente desktop...
cd /d "%~dp0DESKTOP VERSION"

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

echo 🔍 Verificando se o servidor API esta rodando...
curl -s http://localhost:8080/api/health >nul 2>&1
if %errorlevel% neq 0 (
    color 0E
    echo.
    echo ⚠️  AVISO: Servidor API nao esta rodando!
    echo.
    echo 🔧 SOLUCAO:
    echo 1. Execute 'iniciar_servidor.bat' primeiro
    echo 2. Aguarde o servidor inicializar completamente
    echo 3. Execute este arquivo novamente
    echo.
    echo 💡 O servidor deve estar rodando na porta 8080
    echo.
    set /p resposta="Deseja continuar mesmo assim? (s/N): "
    if /i not "%resposta%"=="s" (
        echo.
        echo Cancelando...
        pause
        exit /b 1
    )
    color 0B
    echo.
    echo ⚠️  Continuando sem verificacao do servidor...
    echo.
)

echo.
echo ========================================
echo        🖥️ INICIANDO CLIENTE WEB
echo ========================================
echo.
echo 📍 Cliente rodara na porta 3001
echo 🌐 Acesso: http://localhost:3001
echo 🔗 Conecta-se ao servidor API na porta 8080
echo.
echo 💡 DICAS:
echo • Abra seu navegador em: http://localhost:3001
echo • Login padrao: admin@finanza.com / admin
echo • Mantenha esta janela ABERTA enquanto usar o sistema
echo • Para parar: Pressione Ctrl+C
echo.
echo ========================================
echo.

echo 🔄 Iniciando cliente web...
npm start

echo.
echo 🛑 Cliente desktop encerrado.
echo.
pause