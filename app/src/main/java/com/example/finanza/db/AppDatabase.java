package com.example.finanza.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import com.example.finanza.model.*;

@Database(
        entities = {Usuario.class, Conta.class, Categoria.class, Lancamento.class},
        version = 6, // <-- ALTERADO: aumente a versão ao modificar entidades!
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UsuarioDao usuarioDao();
    public abstract ContaDao contaDao();
    public abstract CategoriaDao categoriaDao();
    public abstract LancamentoDao lancamentoDao();

    /**
     * Migration from version 5 to 6 - Schema integrity fix
     */
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // This migration ensures schema integrity after sync fields were added
            // Rather than guessing what columns might be missing, we'll just ensure
            // the schema matches what the entities expect
            
            // For a more robust approach, we'll simply add all necessary columns
            // SQLite will ignore duplicate column additions, but to be safe,
            // we'll use a different approach
            
            // Add missing columns to Usuario table if they don't exist
            addColumnIfNotExists(database, "Usuario", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Usuario", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Usuario", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Usuario", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            
            // Add missing columns to Conta table if they don't exist
            addColumnIfNotExists(database, "Conta", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Conta", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Conta", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Conta", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Conta", "serverHash", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Conta", "saldoAtual", "REAL NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Conta", "tipo", "TEXT DEFAULT 'corrente'");
            
            // Add missing columns to Categoria table if they don't exist
            addColumnIfNotExists(database, "Categoria", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Categoria", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Categoria", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Categoria", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Categoria", "serverHash", "TEXT DEFAULT ''");
            
            // Add missing columns to Lancamento table if they don't exist
            addColumnIfNotExists(database, "Lancamento", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Lancamento", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Lancamento", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Lancamento", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Lancamento", "serverHash", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Lancamento", "isDeleted", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Lancamento", "serverId", "INTEGER NOT NULL DEFAULT 0");
            
            // Create indexes if they don't exist
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_uuid ON Usuario(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_syncStatus ON Usuario(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_uuid ON Conta(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_syncStatus ON Conta(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_uuid ON Categoria(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_syncStatus ON Categoria(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_uuid ON Lancamento(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_syncStatus ON Lancamento(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_lastModified ON Lancamento(lastModified)");
            
            // Update existing records with UUIDs and timestamps if needed
            // Use proper UUID format for compatibility
            database.execSQL("UPDATE Usuario SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Conta SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Categoria SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Lancamento SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            
            // Update timestamps for existing records only if they are 0
            long currentTime = System.currentTimeMillis();
            database.execSQL("UPDATE Usuario SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
            database.execSQL("UPDATE Conta SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
            database.execSQL("UPDATE Categoria SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
            database.execSQL("UPDATE Lancamento SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
        }
        
        private void addColumnIfNotExists(SupportSQLiteDatabase database, String tableName, String columnName, String columnDef) {
            // Check if column exists by querying table info
            boolean columnExists = false;
            try {
                android.database.Cursor cursor = database.query("PRAGMA table_info(" + tableName + ")");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String existingColumnName = cursor.getString(1); // column name is at index 1
                        if (columnName.equals(existingColumnName)) {
                            columnExists = true;
                            break;
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                // If we can't check, assume column doesn't exist and try to add it
            }
            
            if (!columnExists) {
                try {
                    database.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDef);
                } catch (Exception e) {
                    // Column might exist but pragma check failed, ignore error
                }
            }
        }
    };

    /**
     * Migration from version 4 to 5 - Add saldoAtual field to Conta
     */
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Adiciona saldoAtual na tabela Conta
            database.execSQL("ALTER TABLE Conta ADD COLUMN saldoAtual REAL NOT NULL DEFAULT 0");
        }
    };

    /**
     * Migration from version 3 to 4 - Add tipo field to Conta
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add tipo field to Conta table
            database.execSQL("ALTER TABLE Conta ADD COLUMN tipo TEXT DEFAULT 'corrente'");
        }
    };

    /**
     * Migration from version 2 to 3 - Adiciona apenas colunas novas!
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Adicione apenas colunas que NÃO existem!
            database.execSQL("ALTER TABLE Usuario ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");

            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_uuid ON Usuario(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_syncStatus ON Usuario(syncStatus)");

            // Conta
            database.execSQL("ALTER TABLE Conta ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Conta ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Conta ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Conta ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Conta ADD COLUMN serverHash TEXT DEFAULT ''");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_uuid ON Conta(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_syncStatus ON Conta(syncStatus)");

            // Categoria
            database.execSQL("ALTER TABLE Categoria ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN serverHash TEXT DEFAULT ''");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_uuid ON Categoria(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_syncStatus ON Categoria(syncStatus)");

            // Lancamento
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN serverHash TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_uuid ON Lancamento(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_syncStatus ON Lancamento(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_lastModified ON Lancamento(lastModified)");

            // Atualize valores existentes, se necessário
            database.execSQL("UPDATE Usuario SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            long currentTime = System.currentTimeMillis();
            database.execSQL("UPDATE Usuario SET lastModified = " + currentTime + " WHERE lastModified = 0");
        }
    };

    /**
     * Get database instance with proper migrations
     */
    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "finanza-database")
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                            .fallbackToDestructiveMigration() // Allow destructive migration for development
                            .allowMainThreadQueries() // Somente para testes/dev!
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Close database (for testing)
     */
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}