@echo off
chcp 65001 >nul
color 0D
cls
echo ========================================
echo   🔍 FINANZA - VERIFICACAO DO SISTEMA
echo ========================================
echo.

rem Initialize counters
set /a total_checks=0
set /a passed_checks=0

echo 📋 VERIFICANDO REQUISITOS DO SISTEMA...
echo.

rem Check Node.js
set /a total_checks+=1
node --version >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=*" %%i in ('node --version') do set node_version=%%i
    echo ✅ Node.js instalado (!node_version!)
    set /a passed_checks+=1
) else (
    echo ❌ Node.js não encontrado
    echo    📥 Instale em: https://nodejs.org
)

rem Check npm
set /a total_checks+=1
npm --version >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=*" %%i in ('npm --version') do set npm_version=%%i
    echo ✅ npm instalado (!npm_version!)
    set /a passed_checks+=1
) else (
    echo ❌ npm não encontrado
)

rem Check curl
set /a total_checks+=1
curl --version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ curl disponível
    set /a passed_checks+=1
) else (
    echo ❌ curl não encontrado (necessário para testes)
)

echo.
echo 📁 VERIFICANDO ESTRUTURA DE ARQUIVOS...
echo.

rem Check project structure
set /a total_checks+=1
if exist "server\" (
    echo ✅ Pasta server encontrada
    set /a passed_checks+=1
) else (
    echo ❌ Pasta server não encontrada
)

set /a total_checks+=1
if exist "DESKTOP VERSION\" (
    echo ✅ Pasta DESKTOP VERSION encontrada
    set /a passed_checks+=1
) else (
    echo ❌ Pasta DESKTOP VERSION não encontrada
)

set /a total_checks+=1
if exist "database\" (
    echo ✅ Pasta database encontrada
    set /a passed_checks+=1
) else (
    echo ❌ Pasta database não encontrada
)

set /a total_checks+=1
if exist "server\package.json" (
    echo ✅ package.json do servidor encontrado
    set /a passed_checks+=1
) else (
    echo ❌ package.json do servidor não encontrado
)

set /a total_checks+=1
if exist "DESKTOP VERSION\package.json" (
    echo ✅ package.json do cliente encontrado
    set /a passed_checks+=1
) else (
    echo ❌ package.json do cliente não encontrado
)

set /a total_checks+=1
if exist "database\finanza.sql" (
    echo ✅ Schema SQL encontrado
    set /a passed_checks+=1
) else (
    echo ❌ Schema SQL não encontrado
)

echo.
echo 📦 VERIFICANDO DEPENDENCIAS...
echo.

rem Check server dependencies
set /a total_checks+=1
if exist "server\node_modules\" (
    echo ✅ Dependências do servidor instaladas
    set /a passed_checks+=1
) else (
    echo ⚠️  Dependências do servidor não instaladas
    echo    💡 Execute: cd server ^&^& npm install
)

rem Check client dependencies
set /a total_checks+=1
if exist "DESKTOP VERSION\node_modules\" (
    echo ✅ Dependências do cliente instaladas
    set /a passed_checks+=1
) else (
    echo ⚠️  Dependências do cliente não instaladas
    echo    💡 Execute: cd "DESKTOP VERSION" ^&^& npm install
)

echo.
echo 🔌 VERIFICANDO PORTAS...
echo.

rem Check ports (basic check using netstat)
set /a total_checks+=1
netstat -an | find ":8080" >nul 2>&1
if %errorlevel% neq 0 (
    echo ✅ Porta 8080 disponível (servidor)
    set /a passed_checks+=1
) else (
    echo ⚠️  Porta 8080 em uso
    echo    💡 Um processo está usando a porta 8080
)

set /a total_checks+=1
netstat -an | find ":3001" >nul 2>&1
if %errorlevel% neq 0 (
    echo ✅ Porta 3001 disponível (cliente)
    set /a passed_checks+=1
) else (
    echo ⚠️  Porta 3001 em uso
    echo    💡 Um processo está usando a porta 3001
)

echo.
echo 🗄️ VERIFICANDO BANCO DE DADOS...
echo.

rem Check database
set /a total_checks+=1
if exist "database\finanza.db" (
    echo ✅ Banco de dados existe
    set /a passed_checks+=1
) else (
    echo ℹ️  Banco será criado na primeira execução
)

echo.
echo 🌐 TESTE DE CONECTIVIDADE...
echo.

rem Test if servers are running
set /a total_checks+=1
curl -s http://localhost:8080/api/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Servidor API está rodando (porta 8080)
    set /a passed_checks+=1
) else (
    echo ℹ️  Servidor API não está rodando
    echo    💡 Execute: iniciar_servidor.bat
)

set /a total_checks+=1
curl -s http://localhost:3001 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Cliente desktop está rodando (porta 3001)
    set /a passed_checks+=1
) else (
    echo ℹ️  Cliente desktop não está rodando
    echo    💡 Execute: iniciar_cliente.bat
)

echo.
echo ========================================
echo 📊 RESUMO DA VERIFICACAO
echo ========================================

rem Calculate percentage
set /a percentage=(%passed_checks% * 100) / %total_checks%

echo 📋 Verificações realizadas: %total_checks%
echo ✅ Verificações aprovadas: %passed_checks%
set /a failed_checks=%total_checks% - %passed_checks%
echo ❌ Verificações falharam: %failed_checks%

if %percentage% geq 90 (
    echo 🎉 Sistema está %percentage%%% pronto! Excelente!
) else if %percentage% geq 70 (
    echo ⚠️  Sistema está %percentage%%% pronto. Alguns ajustes podem ser necessários.
) else (
    echo ⚠️  Sistema está apenas %percentage%%% pronto. Várias correções são necessárias.
)

echo.
echo 💡 PROXIMOS PASSOS:

if %passed_checks% lss %total_checks% (
    echo 1. Corrija os problemas listados acima
    echo 2. Execute novamente: verificar_sistema.bat
)

if not exist "server\node_modules\" (
    echo 3. Instale as dependências: instalar_dependencias.bat
) else if not exist "DESKTOP VERSION\node_modules\" (
    echo 3. Instale as dependências: instalar_dependencias.bat
)

echo 4. Inicie o sistema: iniciar_tudo.bat
echo 5. Acesse: http://localhost:3001

echo.
pause