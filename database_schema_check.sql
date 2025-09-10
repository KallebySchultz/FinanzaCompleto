-- Database Schema Verification Script
-- This script shows what the Room database schema should look like after migration

-- Expected Usuario table structure after migration
CREATE TABLE IF NOT EXISTS Usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT,
    email TEXT,
    senha TEXT,
    dataCriacao INTEGER,
    uuid TEXT DEFAULT '',
    lastModified INTEGER NOT NULL DEFAULT 0,
    syncStatus INTEGER NOT NULL DEFAULT 2,
    lastSyncTime INTEGER NOT NULL DEFAULT 0
);

-- Expected Conta table structure after migration  
CREATE TABLE IF NOT EXISTS Conta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT,
    tipo TEXT DEFAULT 'corrente',
    saldoInicial REAL,
    saldoAtual REAL NOT NULL DEFAULT 0,
    usuarioId INTEGER,
    uuid TEXT DEFAULT '',
    lastModified INTEGER NOT NULL DEFAULT 0,
    syncStatus INTEGER NOT NULL DEFAULT 2,
    lastSyncTime INTEGER NOT NULL DEFAULT 0,
    serverHash TEXT DEFAULT '',
    FOREIGN KEY (usuarioId) REFERENCES Usuario(id) ON DELETE CASCADE
);

-- Expected Categoria table structure after migration
CREATE TABLE IF NOT EXISTS Categoria (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT,
    corHex TEXT,
    tipo TEXT,
    uuid TEXT DEFAULT '',
    lastModified INTEGER NOT NULL DEFAULT 0,
    syncStatus INTEGER NOT NULL DEFAULT 2,
    lastSyncTime INTEGER NOT NULL DEFAULT 0,
    serverHash TEXT DEFAULT ''
);

-- Expected Lancamento table structure after migration
CREATE TABLE IF NOT EXISTS Lancamento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    valor REAL,
    data INTEGER,
    descricao TEXT,
    contaId INTEGER,
    categoriaId INTEGER,
    usuarioId INTEGER,
    tipo TEXT,
    uuid TEXT DEFAULT '',
    lastModified INTEGER NOT NULL DEFAULT 0,
    syncStatus INTEGER NOT NULL DEFAULT 2,
    lastSyncTime INTEGER NOT NULL DEFAULT 0,
    serverHash TEXT DEFAULT '',
    isDeleted INTEGER NOT NULL DEFAULT 0,
    serverId INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (usuarioId) REFERENCES Usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (contaId) REFERENCES Conta(id) ON DELETE CASCADE,
    FOREIGN KEY (categoriaId) REFERENCES Categoria(id) ON DELETE SET NULL
);

-- Expected indexes after migration
CREATE INDEX IF NOT EXISTS index_Usuario_uuid ON Usuario(uuid);
CREATE INDEX IF NOT EXISTS index_Usuario_syncStatus ON Usuario(syncStatus);
CREATE INDEX IF NOT EXISTS index_Conta_uuid ON Conta(uuid);
CREATE INDEX IF NOT EXISTS index_Conta_syncStatus ON Conta(syncStatus);
CREATE INDEX IF NOT EXISTS index_Categoria_uuid ON Categoria(uuid);
CREATE INDEX IF NOT EXISTS index_Categoria_syncStatus ON Categoria(syncStatus);
CREATE INDEX IF NOT EXISTS index_Lancamento_uuid ON Lancamento(uuid);
CREATE INDEX IF NOT EXISTS index_Lancamento_syncStatus ON Lancamento(syncStatus);
CREATE INDEX IF NOT EXISTS index_Lancamento_lastModified ON Lancamento(lastModified);

-- Verification queries to check schema integrity
.schema Usuario
.schema Conta
.schema Categoria
.schema Lancamento

-- Check if all expected columns exist
PRAGMA table_info(Usuario);
PRAGMA table_info(Conta);
PRAGMA table_info(Categoria);
PRAGMA table_info(Lancamento);

-- Check indexes
.indexes Usuario
.indexes Conta
.indexes Categoria  
.indexes Lancamento