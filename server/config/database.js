const sqlite3 = require('sqlite3').verbose();
const path = require('path');

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
    // Database is already initialized with the SQL file
    console.log('✅ Schema do banco de dados pronto');
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