package com.example.finanza.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import com.example.finanza.model.*;

/**
 * AppDatabase - Banco de Dados Principal do Finanza
 *
 * Classe abstrata que configura e gerencia o banco de dados Room (SQLite)
 * do aplicativo Finanza. Utiliza o padrão Singleton para garantir uma única
 * instância do banco em toda a aplicação.
 *
 * Funcionalidades:
 * - Configuração centralizada de todas as entidades do sistema
 * - Gerenciamento de versões e migrações do banco de dados
 * - Fornecimento de acesso aos DAOs (Data Access Objects)
 * - Suporte a sincronização com campos de metadados
 * - Indexação para otimização de consultas
 *
 * Entidades gerenciadas:
 * - Usuario: Dados de autenticação e perfil do usuário
 * - Conta: Contas bancárias e financeiras
 * - Categoria: Categorias para classificação de transações
 * - Lancamento: Transações financeiras (receitas e despesas)
 *
 * Migrações disponíveis:
 * - 2→3: Adiciona campos de sincronização e UUIDs
 * - 3→4: Adiciona campo 'tipo' em Conta
 * - 4→5: Adiciona campo 'saldoAtual' em Conta
 * - 5→6: Correção de integridade do schema
 *
 * @author Finanza Team
 * @version 6
 * @since 2024
 */
@Database(
        entities = {Usuario.class, Conta.class, Categoria.class, Lancamento.class},
        version = 6,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /** Instância única do banco de dados (Singleton) */
    private static volatile AppDatabase INSTANCE;

    /**
     * Fornece acesso ao DAO de Usuários
     * @return UsuarioDao para operações com usuários
     */
    public abstract UsuarioDao usuarioDao();

    /**
     * Fornece acesso ao DAO de Contas
     * @return ContaDao para operações com contas
     */
    public abstract ContaDao contaDao();

    /**
     * Fornece acesso ao DAO de Categorias
     * @return CategoriaDao para operações com categorias
     */
    public abstract CategoriaDao categoriaDao();

    /**
     * Fornece acesso ao DAO de Lançamentos
     * @return LancamentoDao para operações com lançamentos
     */
    public abstract LancamentoDao lancamentoDao();

    /**
     * Migração da versão 5 para 6 - Correção de integridade do schema
     *
     * Esta migração garante que todas as colunas de sincronização existam
     * em todas as tabelas, corrigindo problemas de schema inconsistente.
     * Adiciona colunas ausentes de forma segura verificando sua existência.
     */
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Garante integridade do schema após adição de campos de sincronização
            
            // Adiciona colunas ausentes na tabela Usuario
            addColumnIfNotExists(database, "Usuario", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Usuario", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Usuario", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Usuario", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            
            // Adiciona colunas ausentes na tabela Conta
            addColumnIfNotExists(database, "Conta", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Conta", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Conta", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Conta", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Conta", "serverHash", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Conta", "saldoAtual", "REAL NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Conta", "tipo", "TEXT DEFAULT 'corrente'");
            
            // Adiciona colunas ausentes na tabela Categoria
            addColumnIfNotExists(database, "Categoria", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Categoria", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Categoria", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Categoria", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Categoria", "serverHash", "TEXT DEFAULT ''");
            
            // Adiciona colunas ausentes na tabela Lancamento
            addColumnIfNotExists(database, "Lancamento", "uuid", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Lancamento", "lastModified", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Lancamento", "syncStatus", "INTEGER NOT NULL DEFAULT 2");
            addColumnIfNotExists(database, "Lancamento", "lastSyncTime", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Lancamento", "serverHash", "TEXT DEFAULT ''");
            addColumnIfNotExists(database, "Lancamento", "isDeleted", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists(database, "Lancamento", "serverId", "INTEGER NOT NULL DEFAULT 0");
            
            // Cria índices para otimização de consultas
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_uuid ON Usuario(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_syncStatus ON Usuario(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_uuid ON Conta(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_syncStatus ON Conta(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_uuid ON Categoria(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_syncStatus ON Categoria(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_uuid ON Lancamento(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_syncStatus ON Lancamento(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_lastModified ON Lancamento(lastModified)");
            
            // Atualiza registros existentes com UUIDs e timestamps
            database.execSQL("UPDATE Usuario SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Conta SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Categoria SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            database.execSQL("UPDATE Lancamento SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            
            long currentTime = System.currentTimeMillis();
            database.execSQL("UPDATE Usuario SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
            database.execSQL("UPDATE Conta SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
            database.execSQL("UPDATE Categoria SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
            database.execSQL("UPDATE Lancamento SET lastModified = " + currentTime + " WHERE lastModified = 0 OR lastModified IS NULL");
        }
        
        /**
         * Adiciona uma coluna na tabela apenas se ela não existir
         *
         * @param database Banco de dados SQLite
         * @param tableName Nome da tabela
         * @param columnName Nome da coluna
         * @param columnDef Definição completa da coluna (tipo e constraints)
         */
        private void addColumnIfNotExists(SupportSQLiteDatabase database, String tableName, String columnName, String columnDef) {
            // Verifica se a coluna existe consultando informações da tabela
            boolean columnExists = false;
            try {
                android.database.Cursor cursor = database.query("PRAGMA table_info(" + tableName + ")");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String existingColumnName = cursor.getString(1);
                        if (columnName.equals(existingColumnName)) {
                            columnExists = true;
                            break;
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                // Se não puder verificar, assume que não existe
            }
            
            if (!columnExists) {
                try {
                    database.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDef);
                } catch (Exception e) {
                    // Coluna pode existir mas verificação falhou, ignora erro
                }
            }
        }
    };

    /**
     * Migração da versão 4 para 5 - Adiciona campo saldoAtual
     *
     * Adiciona o campo saldoAtual na tabela Conta para otimizar
     * consultas de saldo sem precisar calcular sempre.
     */
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Conta ADD COLUMN saldoAtual REAL NOT NULL DEFAULT 0");
        }
    };

    /**
     * Migração da versão 3 para 4 - Adiciona campo tipo em Conta
     *
     * Adiciona o campo tipo na tabela Conta para suportar diferentes
     * tipos de conta (corrente, poupança, cartão, etc).
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Conta ADD COLUMN tipo TEXT DEFAULT 'corrente'");
        }
    };

    /**
     * Migração da versão 2 para 3 - Adiciona campos de sincronização
     *
     * Adiciona todos os campos necessários para sincronização bidirecional
     * com o servidor desktop, incluindo UUIDs, timestamps e status.
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Usuario - Adiciona campos de sincronização
            database.execSQL("ALTER TABLE Usuario ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Usuario ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_uuid ON Usuario(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Usuario_syncStatus ON Usuario(syncStatus)");

            // Conta - Adiciona campos de sincronização
            database.execSQL("ALTER TABLE Conta ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Conta ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Conta ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Conta ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Conta ADD COLUMN serverHash TEXT DEFAULT ''");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_uuid ON Conta(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Conta_syncStatus ON Conta(syncStatus)");

            // Categoria - Adiciona campos de sincronização
            database.execSQL("ALTER TABLE Categoria ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Categoria ADD COLUMN serverHash TEXT DEFAULT ''");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_uuid ON Categoria(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Categoria_syncStatus ON Categoria(syncStatus)");

            // Lancamento - Adiciona campos de sincronização
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN uuid TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN lastSyncTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN serverHash TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Lancamento ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_uuid ON Lancamento(uuid)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_syncStatus ON Lancamento(syncStatus)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_Lancamento_lastModified ON Lancamento(lastModified)");

            // Atualiza registros existentes
            database.execSQL("UPDATE Usuario SET uuid = lower(hex(randomblob(16))) WHERE uuid = '' OR uuid IS NULL");
            long currentTime = System.currentTimeMillis();
            database.execSQL("UPDATE Usuario SET lastModified = " + currentTime + " WHERE lastModified = 0");
        }
    };

    /**
     * Obtém instância única do banco de dados
     *
     * Implementa o padrão Singleton com Double-Checked Locking para
     * thread-safety. Configura o banco com todas as migrações necessárias.
     *
     * @param context Contexto da aplicação
     * @return Instância do banco de dados
     */
    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "finanza-database")
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() // Apenas para desenvolvimento
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Fecha o banco de dados e limpa a instância
     *
     * Utilizado principalmente em testes para garantir limpeza
     * entre execuções.
     */
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}