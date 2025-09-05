const { initializeApp } = require('firebase/app');
const { getDatabase, ref, set, get, push, update, remove, onValue } = require('firebase/database');

class FirebaseConfig {
  constructor() {
    // Firebase configuration
    const firebaseConfig = {
      databaseURL: 'https://finanza-2cd68-default-rtdb.firebaseio.com/'
    };

    // Initialize Firebase
    this.app = initializeApp(firebaseConfig);
    this.database = getDatabase(this.app);
    
    console.log('✅ Firebase Realtime Database inicializado');
    console.log('🔗 Database URL: https://finanza-2cd68-default-rtdb.firebaseio.com/');
    
    this.initializeDefaultData();
  }

  async initializeDefaultData() {
    try {
      // Check if we have initial data
      const usersRef = ref(this.database, 'usuarios');
      const snapshot = await get(usersRef);
      
      if (!snapshot.exists()) {
        console.log('🔧 Inicializando dados padrão no Firebase...');
        await this.createDefaultData();
      } else {
        console.log('✅ Dados já existem no Firebase');
      }
    } catch (error) {
      console.error('❌ Erro ao verificar dados iniciais:', error);
    }
  }

  async createDefaultData() {
    try {
      // Create default categories
      const categoriesData = {
        // Despesas
        1: { nome: 'Alimentação', cor_hex: '#FF6B6B', tipo: 'despesa' },
        2: { nome: 'Transporte', cor_hex: '#4ECDC4', tipo: 'despesa' },
        3: { nome: 'Saúde', cor_hex: '#45B7D1', tipo: 'despesa' },
        4: { nome: 'Educação', cor_hex: '#96CEB4', tipo: 'despesa' },
        5: { nome: 'Lazer', cor_hex: '#FFEAA7', tipo: 'despesa' },
        6: { nome: 'Casa', cor_hex: '#DDA0DD', tipo: 'despesa' },
        7: { nome: 'Roupas', cor_hex: '#98D8C8', tipo: 'despesa' },
        8: { nome: 'Tecnologia', cor_hex: '#F7DC6F', tipo: 'despesa' },
        9: { nome: 'Viagem', cor_hex: '#BB8FCE', tipo: 'despesa' },
        10: { nome: 'Outros', cor_hex: '#85929E', tipo: 'despesa' },
        // Receitas
        11: { nome: 'Salário', cor_hex: '#2ECC71', tipo: 'receita' },
        12: { nome: 'Freelance', cor_hex: '#3498DB', tipo: 'receita' },
        13: { nome: 'Investimentos', cor_hex: '#9B59B6', tipo: 'receita' },
        14: { nome: 'Vendas', cor_hex: '#E67E22', tipo: 'receita' },
        15: { nome: 'Prêmios', cor_hex: '#F1C40F', tipo: 'receita' },
        16: { nome: 'Restituição', cor_hex: '#1ABC9C', tipo: 'receita' },
        17: { nome: 'Outros', cor_hex: '#34495E', tipo: 'receita' }
      };

      // Create default user (password will be hashed by auth routes)
      const userData = {
        1: {
          nome: 'Administrador',
          email: 'admin@finanza.com',
          senha: 'admin', // This will be hashed in auth routes
          data_criacao: Date.now()
        }
      };

      // Create default accounts
      const accountsData = {
        1: { nome: 'Conta Corrente', saldo_inicial: 1000.00, usuario_id: 1 },
        2: { nome: 'Poupança', saldo_inicial: 5000.00, usuario_id: 1 }
      };

      // Create sample transactions
      const transactionsData = {
        1: {
          valor: 3500.00,
          data: Date.now(),
          descricao: 'Salário Mensal',
          conta_id: 1,
          categoria_id: 11,
          usuario_id: 1,
          tipo: 'receita'
        },
        2: {
          valor: -150.00,
          data: Date.now(),
          descricao: 'Supermercado',
          conta_id: 1,
          categoria_id: 1,
          usuario_id: 1,
          tipo: 'despesa'
        },
        3: {
          valor: -80.00,
          data: Date.now(),
          descricao: 'Combustível',
          conta_id: 1,
          categoria_id: 2,
          usuario_id: 1,
          tipo: 'despesa'
        }
      };

      // Set data in Firebase
      await set(ref(this.database, 'categorias'), categoriesData);
      await set(ref(this.database, 'usuarios'), userData);
      await set(ref(this.database, 'contas'), accountsData);
      await set(ref(this.database, 'lancamentos'), transactionsData);

      console.log('✅ Dados padrão criados no Firebase com sucesso!');
      console.log('📧 Usuário padrão: admin@finanza.com | 🔑 Senha: admin');
    } catch (error) {
      console.error('❌ Erro ao criar dados padrão:', error);
    }
  }

  // Helper methods for database operations
  async create(path, data) {
    try {
      const newRef = push(ref(this.database, path));
      await set(newRef, { ...data, id: newRef.key });
      return { id: newRef.key, ...data };
    } catch (error) {
      throw error;
    }
  }

  async get(path, id = null) {
    try {
      const dbRef = id ? ref(this.database, `${path}/${id}`) : ref(this.database, path);
      const snapshot = await get(dbRef);
      return snapshot.exists() ? snapshot.val() : null;
    } catch (error) {
      throw error;
    }
  }

  async update(path, id, data) {
    try {
      await update(ref(this.database, `${path}/${id}`), data);
      return { id, ...data };
    } catch (error) {
      throw error;
    }
  }

  async delete(path, id) {
    try {
      await remove(ref(this.database, `${path}/${id}`));
      return { success: true };
    } catch (error) {
      throw error;
    }
  }

  async query(path, orderBy = null, equalTo = null) {
    try {
      const snapshot = await get(ref(this.database, path));
      if (!snapshot.exists()) return [];
      
      const data = snapshot.val();
      let results = Object.keys(data).map(key => ({ id: key, ...data[key] }));
      
      // Simple filtering (replace with Firebase queries if needed)
      if (equalTo !== null) {
        results = results.filter(item => item[orderBy] === equalTo);
      }
      
      return results;
    } catch (error) {
      throw error;
    }
  }

  // Real-time listener
  onValue(path, callback) {
    const dbRef = ref(this.database, path);
    return onValue(dbRef, callback);
  }
}

module.exports = new FirebaseConfig();