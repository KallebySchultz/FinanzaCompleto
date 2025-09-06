# Test Plan for Duplicate Prevention Fix

## Changes Made

### Server-Side (DESKTOP VERSION/ServidorFinanza)

1. **ContaDAO.java**: 
   - Added `buscarPorNomeEUsuario()` method to check for existing accounts by name and user
   - Modified `inserir()` to check for duplicates and return existing account ID if found
   
2. **MovimentacaoDAO.java**: 
   - Added `buscarDuplicata()` method to check for existing transactions by value, date, description, account, and user
   - Modified `inserir()` to check for duplicates and return existing transaction ID if found

### Mobile-Side (app/src/main/java/com/example/finanza/network/SyncService.java)

3. **SyncService.java**:
   - Improved error handling in `sincronizarContas()` to gracefully handle duplicate account responses
   - Improved error handling in `sincronizarLancamentos()` to gracefully handle duplicate transaction responses  
   - Improved error handling in `adicionarConta()` and `adicionarLancamento()` methods
   - Now logs duplicate attempts as debug messages instead of errors (similar to existing category handling)

## Expected Behavior

### Before Fix:
- Multiple ADD_CONTA commands would create duplicate accounts (IDs 28, 29, 30, 31 for same account)
- Multiple ADD_MOVIMENTACAO commands would create duplicate transactions (IDs 10, 11, 12 for same transaction)
- Account balances would be incorrectly calculated due to duplicate transactions (-100, -200, -300)

### After Fix:
- Multiple ADD_CONTA commands should reuse existing account ID
- Multiple ADD_MOVIMENTACAO commands should reuse existing transaction ID  
- Account balances should remain stable (no duplicate application of transactions)
- Mobile app logs duplicates as debug messages instead of errors

## Test Scenarios

1. **Duplicate Accounts**: Send same ADD_CONTA command multiple times - should get same account ID back
2. **Duplicate Transactions**: Send same ADD_MOVIMENTACAO command multiple times - should get same transaction ID back
3. **Balance Calculation**: Account balance should not change after repeated sync operations
4. **User Experience**: Mobile app should not show errors for normal duplicate sync operations

## Technical Details

### Server-Side Duplicate Detection Logic

**ContaDAO**: Accounts are considered duplicates if they have the same `nome` (name) and `id_usuario` (user ID)

**MovimentacaoDAO**: Transactions are considered duplicates if they have the same:
- `valor` (amount)
- `data` (date) 
- `descricao` (description)
- `id_conta` (account ID)
- `id_usuario` (user ID)

### Mobile App Default Data Creation

The mobile app creates default data on first login:
- Default account: "Conta Padrão" 
- Default categories: Multiple receita (income) and despesa (expense) categories

Then `SyncService.sincronizarTudo()` sends ALL local data to server, which was causing duplicates on repeated syncs.

## Validation

✅ Server compiles successfully with the changes
✅ No compilation errors introduced  
✅ Logic follows existing pattern from CategoriaDAO (which already had duplicate prevention)
✅ Mobile app error handling improved to match existing category duplicate handling

## Expected Server Logs After Fix

Instead of:
```
ADD_CONTA|Conta Padrão|corrente|0.0 -> OK|28
ADD_CONTA|Conta Padrão|corrente|0.0 -> OK|29  (duplicate!)
ADD_MOVIMENTACAO|100.0|2025-09-14|teste|despesa|6|17 -> OK|10
ADD_MOVIMENTACAO|100.0|2025-09-14|teste|despesa|6|17 -> OK|11  (duplicate!)
```

Should now be:
```
ADD_CONTA|Conta Padrão|corrente|0.0 -> OK|28
ADD_CONTA|Conta Padrão|corrente|0.0 -> OK|28  (reuses existing ID)
ADD_MOVIMENTACAO|100.0|2025-09-14|teste|despesa|6|17 -> OK|10
ADD_MOVIMENTACAO|100.0|2025-09-14|teste|despesa|6|17 -> OK|10  (reuses existing ID)
```

Account balance should remain stable instead of increasing with each duplicate transaction.