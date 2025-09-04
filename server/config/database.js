const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const fs = require('fs');

class Database {
  constructor() {
    const dbPath = path.join(__dirname, '../../database/finanza.db');
    console.log('📄 Tentando conectar ao banco:', dbPath);
    this.db = new sqlite3.Database(dbPath, (err) => {
      if (err) {
        console.error('❌ Erro ao conectar com o banco de dados:', err.message);
      } else {
        console.log('✅ Conectado ao banco de dados SQLite');
        this.initializeDatabase();
      }
    });
  }

  initializeDatabase() {
    const sqlPath = path.join(__dirname, '../../database/finanza.sql');
    
    // Check if database is empty (needs initialization)
    this.db.get("SELECT COUNT(*) as count FROM sqlite_master WHERE type='table'", (err, row) => {
      if (err) {
        console.log('🔧 Inicializando banco de dados pela primeira vez...');
        this.executeSQLFile(sqlPath);
      } else if (row.count === 0) {
        console.log('🔧 Banco vazio detectado. Inicializando com schema...');
        this.executeSQLFile(sqlPath);
      } else {
        console.log('✅ Schema do banco de dados já existe');
      }
    });
  }

  executeSQLFile(sqlPath) {
    try {
      if (fs.existsSync(sqlPath)) {
        const sql = fs.readFileSync(sqlPath, 'utf8');
        
        console.log('📄 Executando script SQL completo...');
        
        // Execute the entire SQL as one transaction
        this.db.exec(sql, (err) => {
          if (err) {
            console.error('❌ Erro ao executar SQL:', err.message);
          } else {
            console.log('✅ Banco de dados inicializado com sucesso!');
            this.verifyInitialization();
          }
        });
      } else {
        console.error('❌ Arquivo SQL não encontrado:', sqlPath);
      }
    } catch (error) {
      console.error('❌ Erro ao ler arquivo SQL:', error.message);
    }
  }

  verifyInitialization() {
    this.db.all("SELECT name FROM sqlite_master WHERE type='table'", (err, tables) => {
      if (err) {
        console.error('❌ Erro ao verificar tabelas:', err.message);
      } else {
        console.log('📊 Tabelas criadas:', tables.map(t => t.name).join(', '));
        
        // Check if we have sample data
        this.db.get("SELECT COUNT(*) as count FROM usuarios", (err, row) => {
          if (err) {
            console.error('❌ Erro ao verificar usuários:', err.message);
          } else {
            console.log(`👥 Usuários no sistema: ${row.count}`);
            
            // Fix admin password if it's still plain text
            if (row.count > 0) {
              this.fixAdminPassword();
            }
          }
        });
      }
    });
  }

  async fixAdminPassword() {
    try {
      const bcrypt = require('bcrypt');
      
      // Check if admin password is plain text
      this.db.get("SELECT senha FROM usuarios WHERE email = ?", ['admin@finanza.com'], async (err, user) => {
        if (err) {
          console.error('❌ Erro ao verificar senha admin:', err.message);
        } else if (user && user.senha === 'admin') {
          console.log('🔐 Atualizando senha do administrador...');
          const hashedPassword = await bcrypt.hash('admin', 10);
          
          this.db.run(
            'UPDATE usuarios SET senha = ? WHERE email = ?',
            [hashedPassword, 'admin@finanza.com'],
            (err) => {
              if (err) {
                console.error('❌ Erro ao atualizar senha:', err.message);
              } else {
                console.log('✅ Senha do administrador atualizada com sucesso!');
                console.log('📧 Email: admin@finanza.com | 🔑 Senha: admin');
              }
            }
          );
        }
      });
    } catch (error) {
      console.error('❌ Erro ao processar senha admin:', error.message);
    }
  }

  // Helper methods for common database operations
  run(sql, params = []) {
    return new Promise((resolve, reject) => {
      this.db.run(sql, params, function(err) {
        if (err) {
          reject(err);
        } else {
          resolve({ id: this.lastID, changes: this.changes });
        }
      });
    });
  }

  get(sql, params = []) {
    return new Promise((resolve, reject) => {
      this.db.get(sql, params, (err, row) => {
        if (err) {
          reject(err);
        } else {
          resolve(row);
        }
      });
    });
  }

  all(sql, params = []) {
    return new Promise((resolve, reject) => {
      this.db.all(sql, params, (err, rows) => {
        if (err) {
          reject(err);
        } else {
          resolve(rows);
        }
      });
    });
  }

  close() {
    return new Promise((resolve, reject) => {
      this.db.close((err) => {
        if (err) {
          reject(err);
        } else {
          console.log('✅ Conexão com banco de dados fechada');
          resolve();
        }
      });
    });
  }
}

module.exports = new Database();