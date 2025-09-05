// Finanza Desktop - API Communication Layer

class FinanzaAPI {
  constructor() {
    this.baseURL = 'http://localhost:8080/api'; // Ajuste conforme seu backend
  }

  // Sempre pega o token mais recente do localStorage
  getToken() {
    return localStorage.getItem('finanza_token');
  }

  // Configurar cabeçalhos de autenticação
  getHeaders() {
    const headers = {
      'Content-Type': 'application/json'
    };
    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
  }

  // Fazer requisição à API
  async request(endpoint, options = {}) {
    try {
      const response = await fetch(`${this.baseURL}${endpoint}`, {
        headers: this.getHeaders(),
        ...options
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Erro na requisição');
      }

      return data;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  }

  // Autenticação
  async login(email, senha) {
    const response = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha })
    });

    // Salva token sempre após login
    if (response.token) {
      localStorage.setItem('finanza_token', response.token);
    }
    return response;
  }

  async register(nome, email, senha) {
    const response = await this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ nome, email, senha })
    });

    if (response.token) {
      localStorage.setItem('finanza_token', response.token);
    }
    return response;
  }

  logout() {
    localStorage.removeItem('finanza_token');
  }

  // Usuário
  async getCurrentUser() {
    try {
      return await this.request('/auth/me');
    } catch (error) {
      if (localStorage.getItem('finanza_token') === 'mock-token') {
        return {
          id: 1,
          nome: 'Usuário Demo',
          email: 'demo@finanza.com',
          created_at: new Date().toISOString()
        };
      }
      throw error;
    }
  }

  async updateProfile(userData) {
    return await this.request('/users/profile', {
      method: 'PUT',
      body: JSON.stringify(userData)
    });
  }

  // Dashboard
  async getFinancialSummary() {
    try {
      return await this.request('/dashboard/summary');
    } catch (error) {
      // Return mock data for demo if backend not available
      return {
        saldo_total: 5420.50,
        receitas_mes: 3200.00,
        despesas_mes: 1850.75,
        total_contas: 3
      };
    }
  }

  // Contas
  async getAccounts() {
    return await this.request('/accounts');
  }

  async createAccount(accountData) {
    return await this.request('/accounts', {
      method: 'POST',
      body: JSON.stringify(accountData)
    });
  }

  async updateAccount(id, accountData) {
    return await this.request(`/accounts/${id}`, {
      method: 'PUT',
      body: JSON.stringify(accountData)
    });
  }

  async deleteAccount(id) {
    return await this.request(`/accounts/${id}`, {
      method: 'DELETE'
    });
  }

  // Transações
  async getTransactions(filters = {}) {
    const query = new URLSearchParams(filters).toString();
    return await this.request(`/transactions${query ? '?' + query : ''}`);
  }

  async createTransaction(transactionData) {
    return await this.request('/transactions', {
      method: 'POST',
      body: JSON.stringify(transactionData)
    });
  }

  async updateTransaction(id, transactionData) {
    return await this.request(`/transactions/${id}`, {
      method: 'PUT',
      body: JSON.stringify(transactionData)
    });
  }

  async deleteTransaction(id) {
    return await this.request(`/transactions/${id}`, {
      method: 'DELETE'
    });
  }

  // Categorias
  async getCategories() {
    return await this.request('/categories');
  }

  // Admin
  async getUsers() {
    return await this.request('/admin/users');
  }

  async getUserStats() {
    return await this.request('/admin/stats');
  }
}

// Export for use in other files
window.FinanzaAPI = FinanzaAPI;