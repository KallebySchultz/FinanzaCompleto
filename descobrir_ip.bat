@echo off
chcp 65001 >nul
color 0E
echo ========================================
echo   🔍 FINANZA - DESCOBRIR IP DA REDE
echo ========================================
echo.

echo 🔍 Descobrindo o IP do seu computador na rede WiFi...
echo.

echo ========================================
echo           INFORMACOES DE REDE
echo ========================================
ipconfig | findstr /i "Adaptador\|IPv4\|Gateway"

echo.
echo ========================================
echo.

echo 📋 INSTRUCOES PARA CONFIGURAR O ANDROID:
echo.
echo 1️⃣ Procure acima pelo "Adaptador de Rede sem Fio" ou "WiFi"
echo 2️⃣ Anote o numero do "Endereco IPv4" 
echo     Exemplo: 192.168.1.100
echo.
echo 3️⃣ No app Android do Finanza:
echo     • Abra o app
echo     • Toque no icone de "Configuracoes" (engrenagem)
echo     • Configure:
echo       Host: [SEU_IP_AQUI]
echo       Porta: 8080
echo     • Toque em "Salvar Configuracoes"
echo     • Toque em "Testar Conexao"
echo.
echo 4️⃣ Exemplo de configuracao completa:
echo     Host: 192.168.1.100
echo     Porta: 8080
echo.
echo ⚠️ IMPORTANTE:
echo • O celular deve estar na MESMA rede WiFi do computador!
echo • O servidor deve estar rodando (iniciar_servidor.bat)
echo • Use apenas numeros, sem "http://" ou espacos
echo.
echo 💡 DICA: Se nao conseguir conectar:
echo 1. Verifique se o Windows Firewall nao esta bloqueando
echo 2. Certifique-se que ambos estao na mesma rede WiFi
echo 3. Tente reiniciar o roteador se necessario
echo.

pause