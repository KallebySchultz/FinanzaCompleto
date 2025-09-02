@echo off

echo === Compilando Servidor Finanza ===

REM Criar diretório de build se não existir
if not exist "build\classes" mkdir "build\classes"

REM Compilar código Java
javac -d build\classes src\main\java\com\finanza\server\*.java

if %errorlevel% equ 0 (
    echo Compilação bem-sucedida!
    echo.
    echo === Iniciando Servidor ===
    echo Para parar o servidor, pressione Ctrl+C
    echo.
    
    REM Executar servidor
    cd build\classes
    java com.finanza.server.FinanzaServer
) else (
    echo Erro na compilação!
    pause
    exit /b 1
)

pause