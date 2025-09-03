@echo off

echo ===============================================
echo       🏦 Finanza Desktop Client v2.0
echo ===============================================
echo.

REM Verificar se Maven está disponível
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Maven detectado - usando compilação Maven
    echo.
    echo === Compilando com Maven ===
    call mvn compile
    
    if %errorlevel% equ 0 (
        echo ✅ Compilação bem-sucedida!
        echo.
        echo === Iniciando Cliente Desktop ===
        echo 🚀 Iniciando aplicação com interface moderna...
        echo ⚙️  Use a tela de Configurações para definir IP do servidor
        echo.
        call mvn exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"
    ) else (
        echo ❌ Erro na compilação Maven!
        echo.
        echo Tentando compilação manual...
        goto manual_compile
    )
    goto end
)

:manual_compile
echo 📝 Maven não encontrado - usando javac manual
echo.

REM Criar diretório de build se não existir
if not exist "build\classes" mkdir "build\classes"

echo === Compilando código Java ===
javac -d build\classes -cp "src\main\java" src\main\java\com\finanza\desktop\*.java src\main\java\com\finanza\desktop\config\*.java src\main\java\com\finanza\desktop\network\*.java src\main\java\com\finanza\desktop\ui\*.java

if %errorlevel% equ 0 (
    echo ✅ Compilação bem-sucedida!
    echo.
    echo === Iniciando Cliente Desktop ===
    echo 🚀 Iniciando aplicação com interface moderna...
    echo ⚙️  Use a tela de Configurações para definir IP do servidor
    echo.
    
    REM Executar cliente desktop
    cd build\classes
    java com.finanza.desktop.FinanzaDesktop
) else (
    echo ❌ Erro na compilação!
    echo.
    echo Dicas de solução:
    echo • Verifique se o JDK está instalado (java -version)
    echo • Instale o Maven para melhor suporte
    echo • Verifique se está na pasta correta
    pause
    exit /b 1
)

:end
echo.
echo 👋 Cliente Desktop encerrado
pause