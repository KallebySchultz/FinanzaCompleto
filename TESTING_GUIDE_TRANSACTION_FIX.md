# MOBILE APP TRANSACTION SYNC FIX - TESTING GUIDE

## Quick Test Instructions

After deploying the fixed mobile app and server:

### 1. Test Transaction Update
```bash
# Original failing command from the problem statement:
# UPDATE_MOVIMENTACAO|653|1000.0|2025-09-09|teste1|receita|46|1717

# Expected result:
# Before fix: ERROR|Erro ao atualizar movimentação no banco de dados
# After fix:  OK|Movimentação atualizada com sucesso
```

### 2. Test Transaction Delete
```bash
# Original failing command from the problem statement:
# DELETE_MOVIMENTACAO|653

# Expected result:
# Before fix: ERROR|Erro ao remover movimentação - movimentação não encontrada ou não pertence ao usuário
# After fix:  OK|Movimentação removida com sucesso
```

## Manual Testing Steps

### Setup
1. Ensure MySQL database is running with the finanza_db schema
2. Start the desktop server (ServidorFinanza)
3. Install and run the mobile app with the fixes
4. Login with a test user account

### Test Scenario 1: New Transaction Sync
1. **Create** new transaction in mobile app
2. **Verify** transaction gets local ID (e.g., ID=1)
3. **Sync** to server → server assigns server ID (e.g., serverID=1001)
4. **Verify** mobile app stores serverID=1001 in local record
5. **Update** the transaction in mobile app
6. **Verify** UPDATE command uses serverID=1001, not localID=1
7. **Confirm** server successfully updates the transaction

### Test Scenario 2: Download Sync
1. **Add** transaction directly in desktop app/database (e.g., ID=653)
2. **Sync** mobile app from server
3. **Verify** mobile app imports transaction with serverID=653
4. **Update** the transaction in mobile app
5. **Verify** UPDATE command uses serverID=653
6. **Confirm** server successfully updates the transaction

### Test Scenario 3: Delete Operation
1. **Select** transaction that came from server (has serverID)
2. **Delete** in mobile app
3. **Verify** DELETE command uses serverID, not local ID
4. **Confirm** server successfully deletes the transaction

## Debugging Tips

### Mobile App Logs
Look for these log messages:
```
SyncService: Enviando comando UPDATE com ID 653 (servidor: 653, local: 1)
SyncService: Enviando comando DELETE com ID 653 (servidor: 653, local: 1)
SyncService: Server ID 653 salvo para lançamento local 1
```

### Server Logs  
Look for these log messages:
```
MovimentacaoDAO: Movimentação atualizada: ID 653, rows affected: 1
MovimentacaoDAO: Movimentação removida: ID 653, rows affected: 1
```

### Error Scenarios
If still getting errors, check:
1. **User authentication**: Is user logged in on both mobile and server?
2. **Database connectivity**: Can server connect to MySQL database?
3. **Transaction ownership**: Does the transaction belong to the logged user?
4. **Foreign key constraints**: Do referenced account/category IDs exist?

## Database Verification

Check the movimentacao table directly:
```sql
-- Check if transaction exists
SELECT id, descricao, id_usuario FROM movimentacao WHERE id = 653;

-- Check user's transactions
SELECT id, descricao, valor FROM movimentacao WHERE id_usuario = 1;

-- Check if specific update worked
SELECT * FROM movimentacao WHERE id = 653 AND descricao = 'teste1';
```

## Success Criteria
✅ UPDATE_MOVIMENTACAO commands return "OK|Movimentação atualizada com sucesso"  
✅ DELETE_MOVIMENTACAO commands return "OK|Movimentação removida com sucesso"  
✅ Mobile app maintains proper local ↔ server ID mapping  
✅ No regression in existing create/sync functionality  
✅ Server provides clear error messages for debugging  