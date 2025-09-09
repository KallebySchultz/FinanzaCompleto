# Testing Guide: Android Edit/Delete Sync Fix

This document provides step-by-step instructions for testing the Android edit/delete sync functionality after the implementation.

## Prerequisites
1. Android app installed and running
2. Desktop server running and accessible
3. User logged in to both Android app and desktop server
4. At least one account and category created
5. Network connection between Android and server

## Test Scenario 1: Edit Transaction Sync

### Setup
1. Create a test transaction in the Android app:
   - Value: R$ 50,00
   - Description: "Test Transaction Original"
   - Type: Income (receita)
   - Category: Any available category

### Test Steps
1. **Edit the transaction:**
   - Long press or tap on the transaction in the movements list
   - Change description to: "Test Transaction EDITED"
   - Change value to: R$ 75,00
   - Keep the same category
   - Tap "Save"

2. **Verify Android behavior:**
   - Transaction should update immediately in the Android UI
   - Toast message should show "Transação atualizada!"
   - Updated values should persist after app restart

3. **Verify server sync:**
   - Check server logs for the following message:
     ```
     Comando enviado: UPDATE_MOVIMENTACAO|[ID]|75.0|[DATE]|Test Transaction EDITED|receita|[ACCOUNT_ID]|[CATEGORY_ID]
     Resposta recebida: OK|Movimentação atualizada com sucesso
     ```
   - Check desktop application - transaction should show updated values
   - Check server database - record should be updated

### Expected Results
✅ Transaction updates in Android UI immediately  
✅ No error messages shown to user  
✅ UPDATE_MOVIMENTACAO command sent to server  
✅ Server responds with success  
✅ Desktop app shows updated transaction  
✅ Database contains updated values  

## Test Scenario 2: Delete Transaction Sync

### Setup
1. Create another test transaction:
   - Value: R$ 25,00
   - Description: "Transaction to Delete"
   - Type: Expense (despesa)

### Test Steps
1. **Delete the transaction:**
   - Long press on the transaction in the movements list
   - Confirm deletion in the dialog

2. **Verify Android behavior:**
   - Transaction should disappear from Android UI immediately
   - Toast message should show "Transação excluída!"
   - Transaction should not reappear after app restart

3. **Verify server sync:**
   - Check server logs for:
     ```
     Comando enviado: DELETE_MOVIMENTACAO|[ID]
     Resposta recebida: OK|Movimentação removida com sucesso
     ```
   - Check desktop application - transaction should be removed
   - Check server database - record should be deleted

### Expected Results
✅ Transaction removed from Android UI immediately  
✅ No error messages shown to user  
✅ DELETE_MOVIMENTACAO command sent to server  
✅ Server responds with success  
✅ Desktop app no longer shows the transaction  
✅ Database record is deleted  

## Test Scenario 3: Offline Operation

### Test Steps
1. **Disconnect from server** (disable WiFi or disconnect server)
2. **Edit a transaction** in the Android app
3. **Verify offline behavior:**
   - Transaction should update in Android UI
   - Toast should show "Movimentação atualizada localmente (servidor offline)"
4. **Reconnect to server**
5. **Verify sync on reconnect:**
   - Changes should sync to server when connection is restored

## Test Scenario 4: Error Handling

### Test Steps
1. **Stop the server** while Android app is connected
2. **Try to edit a transaction**
3. **Verify error handling:**
   - Transaction should update locally
   - Toast should show error message about server connection
   - User should still be able to use the app

## Debug Information

### Android Logs to Monitor
- Filter by tag: `SyncService`
- Look for these log messages:
  ```
  Lançamento atualizado localmente: [description]
  Sincronizando atualização de lançamento com servidor...
  Atualização de lançamento sincronizada com servidor: [description]
  
  Lançamento deletado localmente: [description]
  Sincronizando exclusão de lançamento com servidor...
  Exclusão de lançamento sincronizada com servidor: [description]
  ```

### Server Logs to Monitor
- Look for incoming commands:
  ```
  UPDATE_MOVIMENTACAO|[id]|[value]|[date]|[description]|[type]|[account_id]|[category_id]
  DELETE_MOVIMENTACAO|[id]
  ```
- Look for successful responses:
  ```
  OK|Movimentação atualizada com sucesso
  OK|Movimentação removida com sucesso
  ```

## Troubleshooting

### Common Issues
1. **Commands not sent to server:**
   - Check network connection
   - Verify ServerClient.isConnected() returns true
   - Check if user is logged in

2. **Server rejects commands:**
   - Verify user authentication on server
   - Check if transaction ID exists on server
   - Verify all required parameters are sent

3. **UI not updating:**
   - Check if callback methods are being called
   - Verify runOnUiThread() is used for UI updates
   - Check for exceptions in SyncService

### Performance Notes
- Edit/delete operations happen locally first, then sync to server
- User experience should be immediate regardless of server connection
- Failed server sync attempts are logged but don't block the UI
- Offline changes are saved locally and will sync when connection is restored

## Success Criteria
The fix is considered successful when:
1. ✅ Edit operations sync from Android to server
2. ✅ Delete operations sync from Android to server  
3. ✅ Local UI updates immediately
4. ✅ Server database is updated correctly
5. ✅ Desktop app reflects changes
6. ✅ Offline operations work gracefully
7. ✅ Error handling provides appropriate user feedback
8. ✅ No regression in existing functionality