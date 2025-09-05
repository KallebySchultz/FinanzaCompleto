# Finanza - Firebase Realtime Database Integration

## ✅ Completed Migration

This project has been successfully migrated from SQLite to Firebase Realtime Database with real-time synchronization capabilities.

### 🔥 Firebase Configuration

- **Database URL**: `https://finanza-2cd68-default-rtdb.firebaseio.com/`
- **Architecture**: Firebase Realtime Database with REST API
- **Real-time sync**: ✅ Enabled between desktop and mobile clients

### 🗂️ Data Structure

The Firebase database uses the following structure:

```json
{
  "usuarios": {
    "user_id": {
      "id": "user_id",
      "nome": "User Name",
      "email": "user@email.com", 
      "senha": "hashed_password",
      "data_criacao": 1234567890
    }
  },
  "contas": {
    "account_id": {
      "id": "account_id",
      "nome": "Account Name",
      "saldo_inicial": 1000.00,
      "usuario_id": "user_id"
    }
  },
  "lancamentos": {
    "transaction_id": {
      "id": "transaction_id",
      "valor": -50.00,
      "data": 1234567890,
      "descricao": "Transaction description",
      "conta_id": "account_id",
      "categoria_id": "category_id",
      "usuario_id": "user_id",
      "tipo": "despesa"
    }
  },
  "categorias": {
    "category_id": {
      "id": "category_id",
      "nome": "Category Name",
      "cor_hex": "#FF6B6B",
      "tipo": "despesa"
    }
  }
}
```

### 🖥️ Server Changes

- **Removed**: SQLite dependencies and database.js
- **Added**: Firebase configuration (`server/config/firebase.js`)
- **Updated**: All routes to use Firebase Realtime Database
- **Port**: Server runs on port 8080 (configured for Android compatibility)

### 📱 Android Client

- **New**: `FirebaseClient.java` - Firebase REST API client
- **Updated**: `SyncService.java` - Uses Firebase instead of socket communication
- **Features**: Real-time data synchronization via Firebase REST API

### 🖥️ Desktop Client

- **New**: `firebase-api.js` - Firebase JavaScript SDK integration
- **Features**: Real-time listeners with `onValue()` for automatic updates
- **Test**: `firebase-test.html` - Comprehensive test interface

### 🚀 Getting Started

#### 1. Start the Server
```bash
cd server
npm install
npm start
```
Server will start on `http://localhost:8080`

#### 2. Test Firebase Connection
Open `firebase-test.html` in a browser or:
```bash
cd "DESKTOP VERSION"
npm install
npm start
```
Desktop client will start on `http://localhost:3001`

#### 3. Default Login
- **Email**: `admin@finanza.com`
- **Password**: `admin`

### 🔄 Real-time Synchronization

The system now supports real-time synchronization:

1. **Desktop → Mobile**: Changes made in desktop are instantly visible on mobile
2. **Mobile → Desktop**: Changes made in mobile are instantly visible on desktop
3. **Multiple clients**: All connected clients see updates in real-time

### 🧪 Testing Real-time Sync

1. Open the desktop client in a browser
2. Login with admin credentials
3. Click "Ativar Tempo Real" (Enable Real-time)
4. Open another browser tab/window
5. Make changes in one tab → see instant updates in the other

### 🛠️ Technical Details

**Server Architecture**:
- Express.js with Firebase Admin SDK
- REST API endpoints maintained for compatibility
- Firebase Realtime Database as backend

**Android Architecture**: 
- HTTP requests to Firebase REST API
- Background sync service with auto-refresh
- Local SQLite cache for offline capabilities

**Desktop Architecture**:
- Firebase JavaScript SDK with real-time listeners
- Direct Firebase connection for optimal performance
- Instant updates via WebSocket connections

### 📊 Features

- ✅ User authentication
- ✅ Account management
- ✅ Transaction tracking
- ✅ Category management
- ✅ Real-time synchronization
- ✅ Financial summaries
- ✅ Cross-platform compatibility

### 🔧 Configuration

All Firebase configuration is centralized:
- Server: `server/config/firebase.js`
- Desktop: `DESKTOP VERSION/js/firebase-api.js`
- Android: `app/src/main/java/com/example/finanza/network/FirebaseClient.java`

The database URL is consistent across all platforms: `https://finanza-2cd68-default-rtdb.firebaseio.com/`