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
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    public abstract UsuarioDao usuarioDao();
    public abstract ContaDao contaDao();
    public abstract CategoriaDao categoriaDao();
    public abstract LancamentoDao lancamentoDao();
    
    /**
     * Migration from version 2 to 3 - Adding sync metadata fields
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add sync metadata to Usuario table
            database.execSQL("ALTER TABLE Usuario ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN lastModified INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN syncStatus INTEGER DEFAULT 2");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN lastSyncTime INTEGER DEFAULT 0");
            
            // Add sync metadata to Conta table
            database.execSQL("ALTER TABLE Conta ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Conta ADD COLUMN lastModified INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Conta ADD COLUMN syncStatus INTEGER DEFAULT 2");
            database.execSQL("ALTER TABLE Conta ADD COLUMN lastSyncTime INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Conta ADD COLUMN serverHash TEXT DEFAULT ''");
            
            // Add sync metadata to Categoria table
            database.execSQL("ALTER TABLE Categoria ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN lastModified INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN syncStatus INTEGER DEFAULT 2");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN lastSyncTime INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN serverHash TEXT DEFAULT ''");
            
            // Add sync metadata to Lancamento table
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN lastModified INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN syncStatus INTEGER DEFAULT 2");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN lastSyncTime INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN serverHash TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN isDeleted INTEGER DEFAULT 0");
            
            // Generate UUIDs for existing records
            database.execSQL("UPDATE Usuario SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Conta SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Categoria SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Lancamento SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            
            // Set lastModified timestamp for existing records
            long currentTime = System.currentTimeMillis();
            database.execSQL("UPDATE Usuario SET lastModified = " + currentTime + " WHERE lastModified = 0");
            database.execSQL("UPDATE Conta SET lastModified = " + currentTime + " WHERE lastModified = 0");
            database.execSQL("UPDATE Categoria SET lastModified = " + currentTime + " WHERE lastModified = 0");
            database.execSQL("UPDATE Lancamento SET lastModified = " + currentTime + " WHERE lastModified = 0");
            
            // Create indexes for better sync performance
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_uuid ON Usuario(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_syncStatus ON Usuario(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_uuid ON Conta(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_syncStatus ON Conta(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_uuid ON Categoria(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_syncStatus ON Categoria(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_uuid ON Lancamento(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_syncStatus ON Lancamento(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_lastModified ON Lancamento(lastModified)");
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
                            .addMigrations(MIGRATION_2_3)
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