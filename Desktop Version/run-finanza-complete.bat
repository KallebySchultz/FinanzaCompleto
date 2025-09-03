@echo off
echo 🚀 Finanza Desktop - Script de Execução
echo ========================================

:: Verificar se o Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java não encontrado. Por favor, instale o Java 8 ou superior.
    pause
    exit /b 1
)

:: Verificar se o Maven está instalado
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven não encontrado. Por favor, instale o Maven 3.6+.
    pause
    exit /b 1
)

echo ✅ Java e Maven encontrados

:: Navegar para o diretório do desktop client
cd "%~dp0desktop-client"

echo 📦 Compilando o projeto...
call mvn clean compile
if %errorlevel% neq 0 (
    echo ❌ Erro na compilação. Verifique os logs acima.
    pause
    exit /b 1
)

echo ✅ Compilação bem-sucedida!

echo 🗄️  Testando banco de dados...
for /f %%i in ('mvn -q dependency:build-classpath -Dmdep.outputFile=classpath.tmp') do set CLASSPATH=%%i
java -cp "target/classes;%CLASSPATH%" com.finanza.desktop.test.DatabaseTest

echo.
echo 🎯 Iniciando Finanza Desktop...
echo Aguarde a interface gráfica carregar...
echo.

:: Executar a aplicação
call mvn exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"

pause