# Finanza Mobile - Socket Synchronization Guide

## Overview

The Finanza Android app now supports **offline-first functionality** with automatic synchronization to the desktop server when connected. The app works fully offline and seamlessly syncs data when a connection to the desktop server is available.

## Key Features

### 🔄 **Offline-First Architecture**
- All data operations work locally even without server connection
- Local Room database stores all user data
- Automatic background synchronization when server becomes available

### 🌐 **Socket-Based Communication**
- Direct TCP socket communication with desktop server
- Uses the same protocol as desktop client
- Real-time data synchronization
- Automatic connection management

### 🔐 **Dual Authentication**
- Local authentication (works offline)
- Server authentication (when connected)
- Automatic fallback to local mode when server unavailable

## Setup Instructions

### 1. Desktop Server Setup
```bash
# Navigate to desktop server folder
cd "DESKTOP VERSION/ServidorFinanza"

# Compile and run the server
# (Follow existing desktop setup instructions)
# Server should be running on port 8080
```

### 2. Android App Configuration
1. **Open Settings** in the Android app
2. **Configure Server Connection**:
   - **IP Address**: Enter the desktop server IP (e.g., `192.168.1.100`)
   - **Port**: Enter server port (default: `8080`)
3. **Test Connection**: Tap "Test Connection" to verify
4. **Save Settings**: Connection settings are saved automatically

### 3. Network Setup
- **Same WiFi Network**: Ensure both Android device and desktop are on the same WiFi
- **Firewall**: Make sure port 8080 is not blocked
- **IP Discovery**: Use the included `descobrir_ip.sh` script to find the server IP

## How It Works

### Authentication Flow
```
1. User enters credentials in Android app
2. App tries to authenticate with server (if connected)
3. If server auth succeeds → User logged in
4. If server unavailable → Falls back to local authentication
5. Local session is maintained regardless of connection status
```

### Data Synchronization Flow
```
1. User performs action (add transaction, account, etc.)
2. Data is saved locally immediately
3. If server connected → Data is also sent to server
4. If server unavailable → Data queued for later sync
5. When connection restored → Pending data is synchronized
```

## New Classes and Components

### 📡 **ServerClient.java**
- Manages TCP socket connections
- Handles async communication with desktop server
- Automatic connection retry and error handling

### 🔐 **AuthManager.java**
- Unified authentication system
- Supports both online and offline authentication
- Session management and user state

### 🔄 **SyncService.java**
- Background data synchronization
- Queue management for offline operations
- Conflict resolution and data consistency

### 📋 **Protocol.java**
- Communication protocol definitions
- Message formatting and parsing
- Compatible with desktop server protocol

## Usage Examples

### Setting Up Server Connection
```java
// In SettingsActivity or any configuration
ServerClient client = ServerClient.getInstance(context);
client.configurarServidor("192.168.1.100", 8080);

// Test connection
client.conectar(new ServerClient.ServerCallback<String>() {
    @Override
    public void onSuccess(String result) {
        // Connection successful
    }
    
    @Override
    public void onError(String error) {
        // Handle connection error
    }
});
```

### Authentication
```java
// Login with offline fallback
AuthManager auth = AuthManager.getInstance(context);
auth.login(email, password, new AuthManager.AuthCallback() {
    @Override
    public void onSuccess(Usuario usuario) {
        // User authenticated (online or offline)
    }
    
    @Override
    public void onError(String error) {
        // Authentication failed
    }
});
```

### Data Operations with Sync
```java
// Add transaction with automatic sync
SyncService sync = SyncService.getInstance(context);
sync.adicionarLancamento(lancamento, new SyncService.SyncCallback() {
    @Override
    public void onSyncCompleted(boolean success, String message) {
        // Transaction saved locally and synced (if online)
    }
    
    // ... other callback methods
});
```

## Connection States

### 🟢 **Online Mode**
- Connected to desktop server
- Real-time synchronization
- Data shared between Android and desktop
- Server authentication available

### 🔴 **Offline Mode**
- No server connection
- Local-only operations
- Data stored in local Room database
- Local authentication only

### 🟡 **Sync Mode**
- Transitioning between online/offline
- Synchronizing pending changes
- Resolving data conflicts

## Troubleshooting

### Connection Issues
- ✅ **Check WiFi**: Both devices on same network
- ✅ **Check IP**: Correct server IP address
- ✅ **Check Port**: Server running on port 8080
- ✅ **Check Firewall**: Port 8080 not blocked

### Sync Issues
- ✅ **Check Logs**: Look for sync error messages
- ✅ **Restart App**: Clear any stuck sync states
- ✅ **Manual Sync**: Trigger sync from settings

### Authentication Issues
- ✅ **Local Mode**: App should work offline
- ✅ **Server Mode**: Check server authentication logs
- ✅ **Reset**: Clear app data if needed

## Advanced Configuration

### Custom Server Settings
- Modify `ServerClient.java` for custom protocols
- Update `Protocol.java` for new message types
- Extend `SyncService.java` for custom sync logic

### Database Migration
- Room database handles schema changes automatically
- Server sync preserves data integrity
- Local fallback ensures no data loss

## Security Considerations

### 🔒 **Data Protection**
- Local data encrypted with Room
- Network communication can be encrypted (future enhancement)
- User authentication with password protection

### 🛡️ **Network Security**
- Currently uses unencrypted TCP sockets
- Recommended for local networks only
- Future: Add TLS/SSL encryption for production use

## Future Enhancements

### Planned Features
- [ ] Encrypted communication (TLS/SSL)
- [ ] Real-time push notifications
- [ ] Conflict resolution UI
- [ ] Backup and restore functionality
- [ ] Multi-device synchronization

---

## Quick Start Checklist

1. ✅ **Start Desktop Server** (port 8080)
2. ✅ **Configure Android Settings** (server IP/port)
3. ✅ **Test Connection** (green status = success)
4. ✅ **Create Account** or **Login** (works offline too)
5. ✅ **Use App Normally** (data syncs automatically)

**The app is designed to work seamlessly whether you're online or offline!**