@echo off
REM Script otimizado para rodar o Finanza Desktop local

REM Ir para a pasta do projeto (DESKTOP VERSION)
cd /d "%~dp0"

REM Instalar dependências apenas se node_modules não existir
IF NOT EXIST "node_modules" (
    echo Instalando dependências...
    npm install
) ELSE (
    echo Dependências já instaladas.
)

REM Verificar se a porta 3001 já está em uso
echo Verificando se o servidor já está rodando...
netstat -ano | findstr :3001 >nul
IF %ERRORLEVEL% EQU 0 (
    echo Servidor já está rodando na porta 3001.
) ELSE (
    echo Iniciando servidor...
    start "" /B cmd /c "node server.js"
    REM Aguarde alguns segundos para o servidor subir
    timeout /t 3 /nobreak >nul
)

REM Abrir navegador no endereço do servidor
start http://localhost:3001/

echo Finanza Desktop iniciado!
pause