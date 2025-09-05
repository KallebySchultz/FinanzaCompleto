@echo off
chcp 65001 >nul
color 0B
cls
echo ========================================
echo  📦 FINANZA - INSTALAR DEPENDENCIAS
echo ========================================
echo.

echo 💡 Este script irá instalar todas as dependências necessárias
echo    para o servidor e cliente desktop do Finanza.
echo.

rem Check if Node.js is installed
node --version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo ❌ ERRO: Node.js não encontrado!
    echo.
    echo 📥 SOLUCAO:
    echo 1. Acesse: https://nodejs.org
    echo 2. Baixe a versao LTS (recomendada)
    echo 3. Instale normalmente
    echo 4. Reinicie o computador
    echo 5. Execute este script novamente
    echo.
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('node --version') do set node_version=%%i
for /f "tokens=*" %%j in ('npm --version') do set npm_version=%%j

echo ✅ Node.js encontrado (%node_version%)
echo ✅ npm encontrado (%npm_version%)
echo.

set /p resposta="Deseja continuar com a instalacao? (S/n): "
if /i "%resposta%"=="n" (
    echo Instalacao cancelada.
    exit /b 0
)

echo.
echo 🔄 Iniciando instalacao das dependencias...
echo.

rem Install server dependencies
echo 📡 Instalando dependencias do servidor...
echo ----------------------------------------
cd /d "%~dp0server"
if %errorlevel% neq 0 (
    echo ❌ ERRO: Nao foi possivel acessar a pasta 'server'
    pause
    exit /b 1
)

npm install
set server_exit_code=%errorlevel%

if %server_exit_code% equ 0 (
    echo ✅ Dependencias do servidor instaladas com sucesso!
) else (
    color 0C
    echo ❌ ERRO: Falha ao instalar dependencias do servidor
    echo.
    echo 🔧 SOLUCOES POSSIVEIS:
    echo 1. Verifique sua conexao com a internet
    echo 2. Execute como administrador
    echo 3. Limpe o cache npm: npm cache clean --force
    echo 4. Delete node_modules e package-lock.json, tente novamente
    echo.
    pause
    exit /b 1
)

echo.
echo 🖥️ Instalando dependencias do cliente desktop...
echo ----------------------------------------
cd /d "%~dp0DESKTOP VERSION"
if %errorlevel% neq 0 (
    echo ❌ ERRO: Nao foi possivel acessar a pasta 'DESKTOP VERSION'
    pause
    exit /b 1
)

npm install
set client_exit_code=%errorlevel%

if %client_exit_code% equ 0 (
    echo ✅ Dependencias do cliente instaladas com sucesso!
) else (
    color 0C
    echo ❌ ERRO: Falha ao instalar dependencias do cliente
    echo.
    echo 🔧 SOLUCOES POSSIVEIS:
    echo 1. Verifique sua conexao com a internet
    echo 2. Execute como administrador
    echo 3. Limpe o cache npm: npm cache clean --force
    echo 4. Delete node_modules e package-lock.json, tente novamente
    echo.
    pause
    exit /b 1
)

rem Return to main directory
cd /d "%~dp0"

echo.
echo ========================================
echo 🎉 INSTALACAO CONCLUIDA COM SUCESSO!
echo ========================================
echo.

echo 📋 O QUE FOI INSTALADO:
echo.
echo ✅ Servidor API:
echo    • express, cors, sqlite3, bcrypt
echo    • jsonwebtoken, helmet, morgan
echo    • express-rate-limit, dotenv
echo.
echo ✅ Cliente Desktop:
echo    • express, cors
echo    • nodemon (desenvolvimento)
echo.

echo 💡 PROXIMOS PASSOS:
echo 1. Execute a verificacao: verificar_sistema.bat
echo 2. Inicie o sistema: iniciar_tudo.bat
echo 3. Acesse: http://localhost:3001
echo.

echo 🔗 INFORMACOES DE LOGIN:
echo 📧 Email: admin@finanza.com
echo 🔑 Senha: admin
echo.

pause