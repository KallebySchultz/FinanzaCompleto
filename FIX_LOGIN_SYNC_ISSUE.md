# Fix: Server Authentication Issue for Locally Logged-in Users

## Problem Description

When users logged in locally (offline mode) and then tried to synchronize data with the server, all sync operations failed with the error:

```
ERROR|Usuário não está logado
```

This occurred for commands like:
- `ADD_CATEGORIA`
- `LIST_CATEGORIAS`
- `ADD_CONTA`
- `LIST_CONTAS`
- `LIST_MOVIMENTACOES`

## Root Cause

The issue was caused by a mismatch between local and server authentication states:

1. **Local Login**: When a user logged in offline, the Android app authenticated them locally using the `AuthManager` and stored the session in SharedPreferences
2. **Server State**: However, the server's `ClientHandler` maintained its own `usuarioLogado` state, which was only set when receiving a `LOGIN` command
3. **Sync Failure**: When the app later tried to sync data with the server, it was already locally authenticated but had never sent a `LOGIN` command to the server
4. **Authorization Check**: The server's methods (e.g., `processarAddCategoria`, `processarListCategorias`) checked if `usuarioLogado == null` and rejected all requests

## Solution

The fix adds an authentication step before attempting any sync operations:

### Changes Made

**File**: `app/src/main/java/com/example/finanza/network/SyncService.java`

1. **Modified `sincronizarTudo` method**: Added a call to `ensureServerAuthentication` before performing any sync operations

2. **Added `ensureServerAuthentication` method**: This new private method:
   - Retrieves the user's credentials from the local database
   - Sends a `LOGIN` command to the server with the user's email and password
   - Waits for the server response (with a 10-second timeout)
   - Returns `true` if authentication succeeds, `false` otherwise

### Implementation Details

```java
private boolean ensureServerAuthentication(int usuarioId) {
    // Get user credentials from local database
    Usuario usuario = database.usuarioDao().buscarPorId(usuarioId);
    
    // Send LOGIN command to server
    serverClient.login(usuario.email, usuario.senha, callback);
    
    // Wait for callback with timeout
    // Return success/failure
}
```

The method uses a synchronization object with `wait()/notify()` to convert the asynchronous login callback into a synchronous operation, making it easy to use within the sync flow.

### Flow After Fix

1. User logs in locally (offline)
2. App saves session locally
3. When sync is triggered:
   - App connects to server
   - **NEW**: App sends LOGIN command to authenticate on server
   - Server sets `usuarioLogado` for this connection
   - App proceeds with sync operations
   - All commands now succeed because user is authenticated on server

## Benefits

- ✅ Users can log in offline and sync later without errors
- ✅ Server properly authenticates each connection before processing data
- ✅ Graceful fallback to offline mode if server authentication fails
- ✅ Maintains existing offline-first functionality
- ✅ No changes required to server-side code

## Testing

To test the fix:

1. Log in to the app while server is offline
2. Create some categories, accounts, or transactions locally
3. Start the server
4. Trigger a sync (or wait for automatic sync)
5. Verify that:
   - Login command is sent to server
   - Server accepts the authentication
   - All pending data is synced successfully
   - No "Usuário não está logado" errors appear

## Related Files

- `app/src/main/java/com/example/finanza/network/SyncService.java` - Main fix
- `app/src/main/java/com/example/finanza/network/ServerClient.java` - Login method used
- `app/src/main/java/com/example/finanza/network/AuthManager.java` - Authentication manager
- `DESKTOP VERSION/ServidorFinanza/src/server/ClientHandler.java` - Server-side handler

## Notes

- The fix reuses the existing `LOGIN` command, so no protocol changes are needed
- Authentication happens automatically before each sync, ensuring the server always has the correct user context
- If authentication fails, the app gracefully falls back to offline mode
- The password is stored in plain text locally (as it was before), which is acceptable for this local-first architecture
