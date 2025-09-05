// Firebase API Layer for Desktop
import { initializeApp } from 'firebase/app';
import { getDatabase, ref, set, get, push, update, remove, onValue, query, orderByChild, equalTo } from 'firebase/database';

class FirebaseAPI {
  constructor() {
    // Firebase configuration
    const firebaseConfig = {
      databaseURL: 'https://finanza-2cd68-default-rtdb.firebaseio.com/'
    };

    // Initialize Firebase
    this.app = initializeApp(firebaseConfig);
    this.database = getDatabase(this.app);
    
    // Keep track of current user
    this.currentUser = JSON.parse(localStorage.getItem('finanza_user') || 'null');
    
    console.log('🔥 Firebase initialized for Desktop');
  }

  // Set current user after login
  setCurrentUser(user) {
    this.currentUser = user;
    localStorage.setItem('finanza_user', JSON.stringify(user));
  }

  // Clear user data on logout  
  clearUser() {
    this.currentUser = null;
    localStorage.removeItem('finanza_user');
    localStorage.removeItem('finanza_token');
  }

  // Authentication methods (mock for now - could integrate with Firebase Auth later)
  async login(email, password) {
    try {
      // For now, use a simple check against stored data
      const usersRef = ref(this.database, 'usuarios');
      const snapshot = await get(usersRef);
      
      if (snapshot.exists()) {
        const users = snapshot.val();
        const user = Object.values(users).find(u => u.email === email);
        
        if (user && user.senha === password) {
          // Mock JWT token
          const token = btoa(JSON.stringify({ userId: user.id, email: user.email }));
          localStorage.setItem('finanza_token', token);
          this.setCurrentUser(user);
          
          return {
            success: true,
            token,
            user: {
              id: user.id,
              nome: user.nome,
              email: user.email
            }
          };
        }
      }
      
      throw new Error('Email ou senha incorretos');
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  async logout() {
    this.clearUser();
    return { success: true };
  }

  // Database operations
  async createData(collection, data) {
    try {
      const collectionRef = ref(this.database, collection);
      const newRef = push(collectionRef);
      const newData = { ...data, id: newRef.key };
      await set(newRef, newData);
      return newData;
    } catch (error) {
      console.error(`Error creating ${collection}:`, error);
      throw error;
    }
  }

  async getData(collection, id = null) {
    try {
      const dataRef = id ? ref(this.database, `${collection}/${id}`) : ref(this.database, collection);
      const snapshot = await get(dataRef);
      return snapshot.exists() ? snapshot.val() : null;
    } catch (error) {
      console.error(`Error getting ${collection}:`, error);
      throw error;
    }
  }

  async updateData(collection, id, data) {
    try {
      const dataRef = ref(this.database, `${collection}/${id}`);
      await update(dataRef, data);
      return { id, ...data };
    } catch (error) {
      console.error(`Error updating ${collection}/${id}:`, error);
      throw error;
    }
  }

  async deleteData(collection, id) {
    try {
      const dataRef = ref(this.database, `${collection}/${id}`);
      await remove(dataRef);
      return { success: true };
    } catch (error) {
      console.error(`Error deleting ${collection}/${id}:`, error);
      throw error;
    }
  }

  // Helper to convert Firebase object to array
  objectToArray(obj) {
    if (!obj) return [];
    return Object.keys(obj).map(key => ({ id: key, ...obj[key] }));
  }

  // Financial data methods
  async getAccounts() {
    try {
      if (!this.currentUser) throw new Error('User not logged in');
      
      const accountsData = await this.getData('contas');
      const accounts = this.objectToArray(accountsData);
      
      // Filter by current user
      const userAccounts = accounts.filter(account => account.usuario_id === this.currentUser.id);
      
      // Calculate current balance for each account
      const transactionsData = await this.getData('lancamentos');
      const transactions = this.objectToArray(transactionsData);
      
      return userAccounts.map(account => {
        const accountTransactions = transactions.filter(t => t.conta_id === account.id);
        const movimentacao = accountTransactions.reduce((sum, t) => {
          return sum + (t.tipo === 'receita' ? t.valor : t.valor);
        }, 0);
        
        return {
          ...account,
          saldo_atual: account.saldo_inicial + movimentacao
        };
      });
    } catch (error) {
      console.error('Error getting accounts:', error);
      throw error;
    }
  }

  async createAccount(accountData) {
    if (!this.currentUser) throw new Error('User not logged in');
    
    const data = {
      ...accountData,
      usuario_id: this.currentUser.id
    };
    
    return this.createData('contas', data);
  }

  async getTransactions(filters = {}) {
    try {
      if (!this.currentUser) throw new Error('User not logged in');
      
      const transactionsData = await this.getData('lancamentos');
      let transactions = this.objectToArray(transactionsData);
      
      // Filter by current user
      transactions = transactions.filter(t => t.usuario_id === this.currentUser.id);
      
      // Apply additional filters
      if (filters.conta_id) {
        transactions = transactions.filter(t => t.conta_id === filters.conta_id);
      }
      
      if (filters.tipo) {
        transactions = transactions.filter(t => t.tipo === filters.tipo);
      }
      
      if (filters.start_date) {
        transactions = transactions.filter(t => t.data >= filters.start_date);
      }
      
      if (filters.end_date) {
        transactions = transactions.filter(t => t.data <= filters.end_date);
      }
      
      // Sort by date (newest first)
      transactions.sort((a, b) => b.data - a.data);
      
      // Add account and category names
      const accounts = await this.getAccounts();
      const categoriesData = await this.getData('categorias');
      const categories = this.objectToArray(categoriesData);
      
      return transactions.map(t => {
        const account = accounts.find(a => a.id === t.conta_id);
        const category = categories.find(c => c.id === t.categoria_id);
        
        return {
          ...t,
          conta_nome: account ? account.nome : null,
          categoria_nome: category ? category.nome : null,
          categoria_cor: category ? category.cor_hex : null
        };
      });
    } catch (error) {
      console.error('Error getting transactions:', error);
      throw error;
    }
  }

  async createTransaction(transactionData) {
    if (!this.currentUser) throw new Error('User not logged in');
    
    const data = {
      ...transactionData,
      usuario_id: this.currentUser.id,
      data: Date.now()
    };
    
    return this.createData('lancamentos', data);
  }

  async getCategories() {
    try {
      const categoriesData = await this.getData('categorias');
      return this.objectToArray(categoriesData);
    } catch (error) {
      console.error('Error getting categories:', error);
      throw error;
    }
  }

  // Real-time listeners
  onAccountsChange(callback) {
    const accountsRef = ref(this.database, 'contas');
    return onValue(accountsRef, (snapshot) => {
      if (snapshot.exists()) {
        const accounts = this.objectToArray(snapshot.val());
        const userAccounts = accounts.filter(a => a.usuario_id === this.currentUser?.id);
        callback(userAccounts);
      } else {
        callback([]);
      }
    });
  }

  onTransactionsChange(callback) {
    const transactionsRef = ref(this.database, 'lancamentos');
    return onValue(transactionsRef, (snapshot) => {
      if (snapshot.exists()) {
        const transactions = this.objectToArray(snapshot.val());
        const userTransactions = transactions.filter(t => t.usuario_id === this.currentUser?.id);
        callback(userTransactions);
      } else {
        callback([]);
      }
    });
  }

  // Financial summary
  async getFinancialSummary() {
    try {
      const accounts = await this.getAccounts();
      const transactions = await this.getTransactions();
      
      const saldo_total = accounts.reduce((sum, account) => sum + account.saldo_atual, 0);
      
      // Current month calculations
      const now = new Date();
      const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1).getTime();
      const monthTransactions = transactions.filter(t => t.data >= startOfMonth);
      
      const receitas_mes = monthTransactions
        .filter(t => t.tipo === 'receita')
        .reduce((sum, t) => sum + t.valor, 0);
        
      const despesas_mes = monthTransactions
        .filter(t => t.tipo === 'despesa')
        .reduce((sum, t) => sum + Math.abs(t.valor), 0);
      
      return {
        saldo_total,
        receitas_mes,
        despesas_mes,
        total_contas: accounts.length
      };
    } catch (error) {
      console.error('Error getting financial summary:', error);
      throw error;
    }
  }
}

// Export the API instance
const firebaseAPI = new FirebaseAPI();
export default firebaseAPI;