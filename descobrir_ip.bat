@echo off
echo ========================================
echo      FINANZA - DESCOBRIR IP DA REDE
echo ========================================
echo.

echo 🔍 Descobrindo o IP do seu computador na rede WiFi...
echo.

echo Informacoes de rede:
echo ----------------------------------------
ipconfig | findstr /i "Adaptador\|IPv4\|Gateway"

echo.
echo ----------------------------------------
echo.

echo 📋 INSTRUCOES PARA CONFIGURAR O ANDROID:
echo.
echo 1. Procure pelo adaptador de rede sem fio (WiFi)
echo 2. Anote o "Endereco IPv4" (ex: 192.168.1.100)
echo 3. No app Android, configure:
echo    - Host: [SEU_IP_AQUI]
echo    - Porta: 8080
echo.
echo 4. Exemplo de configuracao:
echo    Host: 192.168.1.100
echo    Porta: 8080
echo.
echo 📱 O celular deve estar na MESMA rede WiFi!
echo.

pause