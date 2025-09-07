# Fix Summary: Movimentação Foreign Key Constraint Issue

## Problem
The application was failing to create movimentações (transactions) with the error:
```
Cannot add or update a child row: a foreign key constraint fails 
(`finanza_db`.`movimentacao`, CONSTRAINT `movimentacao_ibfk_2` 
FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id`) ON DELETE CASCADE)
```

## Root Cause Analysis

### What was happening:
1. Mobile app creates categories locally with auto-generated IDs (1, 2, 3...)
2. SyncService uploads categories to server: `ADD_CATEGORIA|teste|receita|#666666`
3. Server creates category and responds: `OK|1700` (server ID)
4. **BUG**: Local category record was NOT updated with server ID (1700)
5. Mobile app creates movimentação referencing local category ID: `categoriaId = 1`
6. SyncService uploads movimentação: `ADD_MOVIMENTACAO|10.0|2025-09-07|teste|receita|1|1`
7. **FAILURE**: Server tries to insert movimentação with `categoriaId = 1`, but categoria with ID 1 doesn't exist on server (it's ID 1700)

### Key Issue:
The sync process was not updating local records with server IDs, causing foreign key mismatches.

## Solution Implemented

### 1. Enhanced Category Sync Response Handling
**File**: `app/src/main/java/com/example/finanza/network/SyncService.java`

**Before**:
```java
@Override
public void onSuccess(String result) {
    Log.d(TAG, "Categoria sincronizada: " + categoria.nome);
    // No handling of server ID response!
}
```

**After**:
```java
@Override
public void onSuccess(String result) {
    Log.d(TAG, "Categoria sincronizada: " + categoria.nome + " - " + result);
    
    // CRITICAL FIX: Parse server response and update local categoria with server ID
    try {
        String[] partes = Protocol.parseCommand(result);
        if (partes.length >= 2 && Protocol.STATUS_OK.equals(partes[0])) {
            int serverId = Integer.parseInt(partes[1]);
            
            // Update the local categoria with the server ID
            if (categoria.id != serverId) {
                Log.d(TAG, "Atualizando categoria local ID " + categoria.id + " para server ID " + serverId);
                
                // Update lancamentos that reference this categoria
                database.lancamentoDao().atualizarCategoriaId(categoria.id, serverId);
                
                // Create new categoria with server ID and delete old one
                Categoria serverCategoria = new Categoria();
                serverCategoria.id = serverId;
                serverCategoria.nome = categoria.nome;
                serverCategoria.tipo = categoria.tipo;
                serverCategoria.corHex = categoria.corHex;
                serverCategoria.uuid = categoria.uuid;
                serverCategoria.syncStatus = 1; // synced
                serverCategoria.lastSyncTime = System.currentTimeMillis();
                
                database.categoriaDao().deletar(categoria);
                database.categoriaDao().inserir(serverCategoria);
            }
        }
    } catch (Exception e) {
        Log.w(TAG, "Erro ao processar resposta da categoria: " + e.getMessage());
    }
}
```

### 2. Enhanced Account Sync Response Handling
**File**: `app/src/main/java/com/example/finanza/network/SyncService.java`

Applied the same fix to `sincronizarContas()` method to handle account server ID responses.

### 3. Added Foreign Key Update Methods
**File**: `app/src/main/java/com/example/finanza/db/LancamentoDao.java`

```java
/**
 * Update categoria ID for all lancamentos that reference the old categoria ID
 * Used when categoria sync updates local ID to server ID
 */
@Query("UPDATE Lancamento SET categoriaId = :novoId WHERE categoriaId = :antigoId")
void atualizarCategoriaId(int antigoId, int novoId);

/**
 * Update conta ID for all lancamentos that reference the old conta ID
 * Used when conta sync updates local ID to server ID
 */
@Query("UPDATE Lancamento SET contaId = :novoId WHERE contaId = :antigoId")
void atualizarContaId(int antigoId, int novoId);
```

### 4. Enhanced Server Data Sync Methods
**Files**: 
- `app/src/main/java/com/example/finanza/db/CategoriaDao.java`
- `app/src/main/java/com/example/finanza/db/ContaDao.java`

Added methods to properly handle categories and accounts received from server with explicit server IDs.

## How the Fix Works

### Flow After Fix:
1. Mobile app creates category locally with auto-generated ID (e.g., ID = 1)
2. SyncService uploads: `ADD_CATEGORIA|teste|receita|#666666`
3. Server responds: `OK|1700`
4. **NEW**: SyncService parses response, extracts server ID (1700)
5. **NEW**: Updates all lancamentos: `UPDATE Lancamento SET categoriaId = 1700 WHERE categoriaId = 1`
6. **NEW**: Replaces local category: deletes category with ID=1, creates new with ID=1700
7. Mobile app creates movimentação: `categoriaId = 1700` (now correct!)
8. SyncService uploads: `ADD_MOVIMENTACAO|10.0|2025-09-07|teste|receita|1700|46`
9. **SUCCESS**: Server can create movimentação because categoria with ID=1700 exists

## Testing the Fix

### Manual Verification Steps:

1. **Start with fresh app state** (clear app data)
2. **Create a category** (e.g., "Teste" of type "receita")
3. **Trigger sync** - Check logs for:
   ```
   Categoria sincronizada: Teste - OK|[SERVER_ID]
   Atualizando categoria local ID [LOCAL_ID] para server ID [SERVER_ID]
   ```
4. **Create a movimentação** using that category
5. **Trigger sync** - Check logs for:
   ```
   ADD_MOVIMENTACAO|[VALUE]|[DATE]|[DESC]|[TYPE]|[ACCOUNT_SERVER_ID]|[CATEGORY_SERVER_ID]
   ```
   The category ID should now be the server ID, not a local auto-generated ID.
6. **Verify success** - Should see `OK|[MOVIMENTACAO_ID]` instead of foreign key error

### Expected Log Changes:

**Before Fix**:
```
ADD_MOVIMENTACAO|10.0|2025-09-07|teste|receita|1|1
Erro ao inserir movimentação: Cannot add or update a child row: a foreign key constraint fails
```

**After Fix**:
```
ADD_CATEGORIA|teste|receita|#666666
Resposta enviada: OK|1700
Atualizando categoria local ID 1 para server ID 1700
ADD_MOVIMENTACAO|10.0|2025-09-07|teste|receita|46|1700
Resposta enviada: OK|[MOVIMENTACAO_ID]
```

## Files Modified

1. `app/src/main/java/com/example/finanza/network/SyncService.java` - Enhanced sync response handling
2. `app/src/main/java/com/example/finanza/db/LancamentoDao.java` - Added foreign key update methods  
3. `app/src/main/java/com/example/finanza/db/CategoriaDao.java` - Enhanced server sync methods
4. `app/src/main/java/com/example/finanza/db/ContaDao.java` - Enhanced server sync methods
5. `app/src/test/java/com/example/finanza/SyncServiceTest.java` - Added unit tests

## Impact

- **Fixes the foreign key constraint failure** that prevented movimentações from being saved
- **Ensures data consistency** between mobile app and server
- **Maintains referential integrity** in the database
- **Backward compatible** - doesn't break existing functionality
- **Handles both categories and accounts** to prevent similar issues