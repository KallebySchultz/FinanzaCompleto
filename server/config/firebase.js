const { initializeApp } = require('firebase/app');
const { getDatabase, ref, set, get, push, update, remove } = require('firebase/database');

class FirebaseConfig {
  constructor() {
    // Firebase configuration
    const firebaseConfig = {
      databaseURL: 'https://finanza-2cd68-default-rtdb.firebaseio.com/'
    };

    try {
      // Initialize Firebase
      this.app = initializeApp(firebaseConfig);
      this.database = getDatabase(this.app);
      
      console.log('✅ Firebase Realtime Database inicializado');
      console.log('🔗 Database URL: https://finanza-2cd68-default-rtdb.firebaseio.com/');
      
      // Initialize simple test data
      this.initializeSimpleData().catch(err => {
        console.log('ℹ️  Dados já existem ou não foi possível inicializar:', err.message);
      });
    } catch (error) {
      console.error('❌ Erro ao inicializar Firebase:', error);
      throw error;
    }
  }

  async initializeSimpleData() {
    try {
      // Check if we already have a test user
      const testRef = ref(this.database, 'usuarios/test');
      const snapshot = await get(testRef);
      
      if (!snapshot.exists()) {
        console.log('🔧 Criando dados de teste...');
        
        // Create simple test data
        await set(ref(this.database, 'usuarios/test'), {
          id: 'test',
          nome: 'Administrador',
          email: 'admin@finanza.com',
          senha: 'admin', // This will be hashed in auth routes
          data_criacao: Date.now()
        });

        await set(ref(this.database, 'categorias/cat1'), {
          id: 'cat1',
          nome: 'Alimentação',
          cor_hex: '#FF6B6B',
          tipo: 'despesa'
        });

        await set(ref(this.database, 'categorias/cat2'), {
          id: 'cat2',
          nome: 'Salário',
          cor_hex: '#2ECC71',
          tipo: 'receita'
        });

        console.log('✅ Dados de teste criados!');
      } else {
        console.log('✅ Dados já existem no Firebase');
      }
    } catch (error) {
      console.error('❌ Erro ao inicializar dados:', error);
    }
  }

  // Helper methods for database operations
  async create(path, data) {
    try {
      const newRef = push(ref(this.database, path));
      const newData = { ...data, id: newRef.key };
      await set(newRef, newData);
      return newData;
    } catch (error) {
      console.error(`❌ Erro ao criar em ${path}:`, error);
      throw error;
    }
  }

  async get(path, id = null) {
    try {
      const dbRef = id ? ref(this.database, `${path}/${id}`) : ref(this.database, path);
      const snapshot = await get(dbRef);
      return snapshot.exists() ? snapshot.val() : null;
    } catch (error) {
      console.error(`❌ Erro ao buscar em ${path}:`, error);
      throw error;
    }
  }

  async update(path, id, data) {
    try {
      await update(ref(this.database, `${path}/${id}`), data);
      return { id, ...data };
    } catch (error) {
      console.error(`❌ Erro ao atualizar ${path}/${id}:`, error);
      throw error;
    }
  }

  async delete(path, id) {
    try {
      await remove(ref(this.database, `${path}/${id}`));
      return { success: true };
    } catch (error) {
      console.error(`❌ Erro ao deletar ${path}/${id}:`, error);
      throw error;
    }
  }

  async query(path) {
    try {
      const snapshot = await get(ref(this.database, path));
      if (!snapshot.exists()) return [];
      
      const data = snapshot.val();
      return Object.keys(data).map(key => ({ id: key, ...data[key] }));
    } catch (error) {
      console.error(`❌ Erro ao consultar ${path}:`, error);
      return [];
    }
  }
}

module.exports = new FirebaseConfig();