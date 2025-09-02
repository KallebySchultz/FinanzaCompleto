@echo off

echo === Compilando Cliente Desktop Finanza ===

REM Criar diretório de build se não existir
if not exist "build\classes" mkdir "build\classes"

REM Compilar código Java
javac -d build\classes src\main\java\com\finanza\desktop\*.java

if %errorlevel% equ 0 (
    echo Compilação bem-sucedida!
    echo.
    echo === Iniciando Cliente Desktop ===
    
    REM Executar cliente desktop
    cd build\classes
    java com.finanza.desktop.FinanzaDesktop
) else (
    echo Erro na compilação!
    pause
    exit /b 1
)

pause