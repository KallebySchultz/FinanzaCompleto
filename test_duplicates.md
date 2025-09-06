# Test Plan for Duplicate Prevention Fix

## Changes Made

1. **ContaDAO.java**: Added `buscarPorNomeEUsuario()` method and modified `inserir()` to check for existing accounts
2. **MovimentacaoDAO.java**: Added `buscarDuplicata()` method and modified `inserir()` to check for existing transactions

## Expected Behavior

### Before Fix:
- Multiple ADD_CONTA commands would create duplicate accounts (IDs 28, 29, 30, 31 for same account)
- Multiple ADD_MOVIMENTACAO commands would create duplicate transactions (IDs 10, 11, 12 for same transaction)
- Account balances would be incorrectly calculated due to duplicate transactions (-100, -200, -300)

### After Fix:
- Multiple ADD_CONTA commands should reuse existing account ID
- Multiple ADD_MOVIMENTACAO commands should reuse existing transaction ID  
- Account balances should remain stable (no duplicate application of transactions)

## Test Scenarios

1. **Duplicate Accounts**: Send same ADD_CONTA command multiple times - should get same account ID back
2. **Duplicate Transactions**: Send same ADD_MOVIMENTACAO command multiple times - should get same transaction ID back
3. **Balance Calculation**: Account balance should not change after repeated sync operations

## Server Compilation

✅ Server compiles successfully with the changes
✅ No compilation errors introduced