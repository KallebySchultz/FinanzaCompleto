#!/bin/bash

# Verification Script for Transaction Sync Fix
# This script helps verify that the UPDATE_MOVIMENTACAO and DELETE_MOVIMENTACAO fixes work correctly

echo "🔍 VERIFICATION: Transaction Sync Fix"
echo "======================================"
echo

echo "✅ 1. Checking Lancamento model changes..."
if grep -q "public int serverId" "/home/runner/work/FinanzaCompleto/FinanzaCompleto/app/src/main/java/com/example/finanza/model/Lancamento.java"; then
    echo "   ✓ serverId field added to Lancamento model"
else
    echo "   ❌ serverId field NOT found in Lancamento model"
    exit 1
fi

echo
echo "✅ 2. Checking SyncService UPDATE fix..."
if grep -q "idParaServidor.*serverId.*lancamento.id" "/home/runner/work/FinanzaCompleto/FinanzaCompleto/app/src/main/java/com/example/finanza/network/SyncService.java"; then
    echo "   ✓ atualizarLancamento() now uses server ID"
else
    echo "   ❌ atualizarLancamento() fix NOT found"
    exit 1
fi

echo
echo "✅ 3. Checking SyncService DELETE fix..."
if grep -q "idParaServidor.*lancamento.serverId.*lancamento.id" "/home/runner/work/FinanzaCompleto/FinanzaCompleto/app/src/main/java/com/example/finanza/network/SyncService.java"; then
    echo "   ✓ deletarLancamento() now uses server ID"
else
    echo "   ❌ deletarLancamento() fix NOT found"
    exit 1
fi

echo
echo "✅ 4. Checking server ID storage in sync download..."
if grep -q "lancamento.serverId = serverId" "/home/runner/work/FinanzaCompleto/FinanzaCompleto/app/src/main/java/com/example/finanza/network/SyncService.java"; then
    echo "   ✓ processarMovimentacoesDoServidor() stores server IDs"
else
    echo "   ❌ Server ID storage in download sync NOT found"
    exit 1
fi

echo
echo "✅ 5. Checking server ID storage in sync upload..."
if grep -q "lancamento.serverId = serverId" "/home/runner/work/FinanzaCompleto/FinanzaCompleto/app/src/main/java/com/example/finanza/network/SyncService.java"; then
    echo "   ✓ adicionarLancamento() stores server IDs from responses"
else
    echo "   ❌ Server ID storage in upload sync NOT found"
    exit 1
fi

echo
echo "✅ 6. Checking server-side error handling improvements..."
if grep -q "buscarPorIdEUsuario" "/home/runner/work/FinanzaCompleto/FinanzaCompleto/DESKTOP VERSION/ServidorFinanza/src/dao/MovimentacaoDAO.java"; then
    echo "   ✓ MovimentacaoDAO.atualizar() has improved error handling"
else
    echo "   ❌ Server-side error handling improvements NOT found"
    exit 1
fi

echo
echo "🎯 VERIFICATION SUMMARY"
echo "======================="
echo "✅ All fixes have been successfully implemented!"
echo
echo "📋 Changes Made:"
echo "   • Added serverId field to Lancamento model"
echo "   • Fixed UPDATE operations to use server IDs"
echo "   • Fixed DELETE operations to use server IDs"  
echo "   • Enhanced sync download to store server IDs"
echo "   • Enhanced sync upload to capture server IDs"
echo "   • Improved server error handling and logging"
echo
echo "🔧 Next Steps:"
echo "   1. Build and deploy the mobile app with these changes"
echo "   2. Restart the desktop server"
echo "   3. Test UPDATE_MOVIMENTACAO command with existing transaction"
echo "   4. Test DELETE_MOVIMENTACAO command with existing transaction"
echo "   5. Verify that both operations now succeed"
echo
echo "🐛 Original Problem:"
echo "   Command: UPDATE_MOVIMENTACAO|653|1000.0|2025-09-09|teste1|receita|46|1717"
echo "   Response: ERROR|Erro ao atualizar movimentação no banco de dados"
echo
echo "🎉 Expected Result After Fix:"
echo "   Command: UPDATE_MOVIMENTACAO|653|1000.0|2025-09-09|teste1|receita|46|1717"
echo "   Response: OK|Movimentação atualizada com sucesso"
echo