@echo off
chcp 65001 >nul 2>&1
setlocal EnableDelayedExpansion

:: Finanza - Script de Limpeza Segura
:: Remove arquivos inúteis identificados na análise

echo 🧹 FINANZA - LIMPEZA DE ARQUIVOS INÚTEIS
echo ========================================
echo.

set /a removed_count=0
set /a space_saved=0

:: Função para confirmar ação
:confirm
set /p choice="%~1 (y/N): "
if /i "%choice%"=="y" goto :confirm_yes
if /i "%choice%"=="s" goto :confirm_yes
goto :confirm_no

:confirm_yes
exit /b 0

:confirm_no
exit /b 1

:: Função para remover arquivo com confirmação
:safe_remove
set "file=%~1"
set "reason=%~2"
set "size=%~3"

if exist "%file%" (
    echo 📁 Encontrado: %file%
    echo    Razão: %reason%
    call :confirm "   Excluir este arquivo?"
    if !errorlevel! equ 0 (
        del /f /q "%file%" 2>nul || rmdir /s /q "%file%" 2>nul
        echo    ✅ Removido com sucesso!
        set /a removed_count+=1
        set /a space_saved+=%size%
        exit /b 0
    ) else (
        echo    ⏭️  Pulado
        exit /b 1
    )
) else (
    echo ❌ Arquivo não encontrado: %file%
    exit /b 1
)
echo.
exit /b 0

:: Arquivos com exclusão 100% segura
echo 🔒 ARQUIVOS DE EXCLUSÃO SEGURA
echo ------------------------------

:: 1. DESKTOP VERSION.rar (redundante)
call :safe_remove "DESKTOP VERSION.rar" "Arquivo comprimido redundante (1.5MB)" 1536

:: 2. verify_netbeans.sh (desatualizado)
call :safe_remove "verify_netbeans.sh" "Script desatualizado para NetBeans inexistente (4KB)" 4

echo.
echo ⚠️  ARQUIVOS DE EXCLUSÃO OPCIONAL
echo --------------------------------

:: 3. firebase-test.html (ferramenta de teste)
call :safe_remove "firebase-test.html" "Arquivo de teste Firebase (20KB) - útil para depuração" 20

:: 4. .idea/ (configurações IDE)
call :safe_remove ".idea" "Configurações do IDE (44KB) - regenerável mas útil" 44

echo.
echo 📊 RESUMO DA LIMPEZA
echo ====================
echo 🗑️  Arquivos removidos: !removed_count!
set /a space_mb=!space_saved!/1024
echo 💾 Espaço economizado: !space_saved!KB (~!space_mb!MB)
echo.

if !removed_count! gtr 0 (
    echo ✅ Limpeza concluída com sucesso!
    echo 💡 Os arquivos foram removidos do sistema de arquivos
    echo 📋 Para confirmar no Git, execute: git add . && git commit -m "Limpeza: removidos arquivos inúteis"
) else (
    echo ℹ️  Nenhum arquivo foi removido
)

echo.
echo 📖 Para mais detalhes, consulte: CLEANUP_ANALYSIS.md
echo.
pause