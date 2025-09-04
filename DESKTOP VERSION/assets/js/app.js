// Finanza Desktop Client - Main JavaScript

class FinanzaAPI {
  constructor() {
    this.baseURL = 'http://localhost:8080/api'; // Updated to match server port
    this.token = localStorage.getItem('finanza_token');
  }

  // Configurar cabeçalhos de autenticação
  getHeaders() {
    const headers = {
      'Content-Type': 'application/json'
    };
    
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
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
    
    this.token = response.token;
    localStorage.setItem('finanza_token', this.token);
    localStorage.setItem('finanza_user', JSON.stringify(response.user));
    
    return response;
  }

  async register(nome, email, senha) {
    const response = await this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ nome, email, senha })
    });
    
    this.token = response.token;
    localStorage.setItem('finanza_token', this.token);
    localStorage.setItem('finanza_user', JSON.stringify(response.user));
    
    return response;
  }

  logout() {
    this.token = null;
    localStorage.removeItem('finanza_token');
    localStorage.removeItem('finanza_user');
  }

  // Usuários
  async getProfile() {
    return await this.request('/users/profile');
  }

  async updateProfile(data) {
    return await this.request('/users/profile', {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  async getFinancialSummary() {
    return await this.request('/users/financial-summary');
  }

  // Contas
  async getAccounts() {
    return await this.request('/accounts');
  }

  async createAccount(data) {
    return await this.request('/accounts', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }

  async updateAccount(id, data) {
    return await this.request(`/accounts/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  async deleteAccount(id) {
    return await this.request(`/accounts/${id}`, {
      method: 'DELETE'
    });
  }

  // Transações
  async getTransactions(params = {}) {
    const queryString = new URLSearchParams(params).toString();
    return await this.request(`/transactions?${queryString}`);
  }

  async createTransaction(data) {
    return await this.request('/transactions', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }

  async updateTransaction(id, data) {
    return await this.request(`/transactions/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  async deleteTransaction(id) {
    return await this.request(`/transactions/${id}`, {
      method: 'DELETE'
    });
  }

  // Categorias
  async getCategories(tipo = null) {
    const params = tipo ? `?tipo=${tipo}` : '';
    return await this.request(`/categories${params}`);
  }

  // Admin
  async getAdminStats() {
    return await this.request('/admin/stats');
  }

  async getAdminUsers(params = {}) {
    const queryString = new URLSearchParams(params).toString();
    return await this.request(`/admin/users?${queryString}`);
  }

  async createUser(data) {
    return await this.request('/admin/users', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }

  async updateUser(id, data) {
    return await this.request(`/admin/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  async deleteUser(id) {
    return await this.request(`/admin/users/${id}`, {
      method: 'DELETE'
    });
  }
}

// Gerenciador da aplicação
class FinanzaApp {
  constructor() {
    this.api = new FinanzaAPI();
    this.currentUser = JSON.parse(localStorage.getItem('finanza_user') || 'null');
    this.currentPage = 'dashboard';
    this.icons = null; // Will be initialized when needed
    
    this.init();
  }

  init() {
    // Verificar se usuário está logado
    if (!this.api.token || !this.currentUser) {
      this.showLogin();
    } else {
      this.showDashboard();
    }

    // Event listeners
    this.setupEventListeners();
  }

  setupEventListeners() {
    // Navegação
    document.addEventListener('click', (e) => {
      if (e.target.matches('[data-page]')) {
        e.preventDefault();
        this.navigateTo(e.target.dataset.page);
      }
    });

    // Logout
    document.addEventListener('click', (e) => {
      if (e.target.matches('[data-action="logout"]')) {
        e.preventDefault();
        this.logout();
      }
    });

    // Modais
    document.addEventListener('click', (e) => {
      if (e.target.matches('.modal')) {
        this.closeModal();
      }
      if (e.target.matches('.modal-close')) {
        this.closeModal();
      }
    });
  }

  // Navegação
  navigateTo(page) {
    this.currentPage = page;
    
    // Atualizar navegação ativa
    document.querySelectorAll('.nav-link').forEach(link => {
      link.classList.remove('active');
    });
    document.querySelector(`[data-page="${page}"]`)?.classList.add('active');

    // Mostrar página
    switch (page) {
      case 'dashboard':
        this.showDashboard();
        break;
      case 'accounts':
        this.showAccounts();
        break;
      case 'transactions':
        this.showTransactions();
        break;
      case 'profile':
        this.showProfile();
        break;
      case 'admin':
        this.showAdmin();
        break;
      default:
        this.showDashboard();
    }
  }

  // Autenticação
  async showLogin() {
    document.body.innerHTML = `
      <div class="auth-container">
        <div class="auth-card fade-in">
          <div class="logo">F</div>
          <h1>Finanza Desktop</h1>
          <form id="loginForm">
            <div class="form-group">
              <label for="email">Email</label>
              <input type="email" id="email" class="form-input" required>
            </div>
            <div class="form-group">
              <label for="password">Senha</label>
              <input type="password" id="password" class="form-input" required>
            </div>
            <button type="submit" class="btn btn-primary">
              <span class="login-text">Entrar</span>
              <span class="loading hidden"></span>
            </button>
          </form>
          <div id="loginError" class="alert alert-error hidden mt-3"></div>
          <p class="mt-3">
            <a href="#" id="showRegister">Não tem conta? Cadastre-se</a>
          </p>
        </div>
      </div>
    `;

    // Event listeners
    document.getElementById('loginForm').addEventListener('submit', (e) => {
      e.preventDefault();
      this.handleLogin();
    });

    document.getElementById('showRegister').addEventListener('click', (e) => {
      e.preventDefault();
      this.showRegister();
    });
  }

  async handleLogin() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const loginBtn = document.querySelector('.btn-primary');
    const loginText = document.querySelector('.login-text');
    const loading = document.querySelector('.loading');
    const errorDiv = document.getElementById('loginError');

    try {
      // Mostrar loading
      loginText.classList.add('hidden');
      loading.classList.remove('hidden');
      loginBtn.disabled = true;
      errorDiv.classList.add('hidden');

      const response = await this.api.login(email, password);
      this.currentUser = response.user;
      
      this.showDashboard();
    } catch (error) {
      errorDiv.textContent = error.message;
      errorDiv.classList.remove('hidden');
    } finally {
      // Esconder loading
      loginText.classList.remove('hidden');
      loading.classList.add('hidden');
      loginBtn.disabled = false;
    }
  }

  showRegister() {
    document.body.innerHTML = `
      <div class="auth-container">
        <div class="auth-card fade-in">
          <div class="logo">F</div>
          <h1>Criar Conta</h1>
          <form id="registerForm">
            <div class="form-group">
              <label for="name">Nome completo</label>
              <input type="text" id="name" class="form-input" required>
            </div>
            <div class="form-group">
              <label for="email">Email</label>
              <input type="email" id="email" class="form-input" required>
            </div>
            <div class="form-group">
              <label for="password">Senha</label>
              <input type="password" id="password" class="form-input" required minlength="6">
            </div>
            <div class="form-group">
              <label for="confirmPassword">Confirmar senha</label>
              <input type="password" id="confirmPassword" class="form-input" required>
            </div>
            <button type="submit" class="btn btn-primary">
              <span class="register-text">Criar Conta</span>
              <span class="loading hidden"></span>
            </button>
          </form>
          <div id="registerError" class="alert alert-error hidden mt-3"></div>
          <p class="mt-3">
            <a href="#" id="showLogin">Já tem conta? Faça login</a>
          </p>
        </div>
      </div>
    `;

    // Event listeners
    document.getElementById('registerForm').addEventListener('submit', (e) => {
      e.preventDefault();
      this.handleRegister();
    });

    document.getElementById('showLogin').addEventListener('click', (e) => {
      e.preventDefault();
      this.showLogin();
    });
  }

  async handleRegister() {
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const registerBtn = document.querySelector('.btn-primary');
    const registerText = document.querySelector('.register-text');
    const loading = document.querySelector('.loading');
    const errorDiv = document.getElementById('registerError');

    try {
      if (password !== confirmPassword) {
        throw new Error('Senhas não coincidem');
      }

      // Mostrar loading
      registerText.classList.add('hidden');
      loading.classList.remove('hidden');
      registerBtn.disabled = true;
      errorDiv.classList.add('hidden');

      const response = await this.api.register(name, email, password);
      this.currentUser = response.user;
      
      this.showDashboard();
    } catch (error) {
      errorDiv.textContent = error.message;
      errorDiv.classList.remove('hidden');
    } finally {
      // Esconder loading
      registerText.classList.remove('hidden');
      loading.classList.add('hidden');
      registerBtn.disabled = false;
    }
  }

  logout() {
    this.api.logout();
    this.currentUser = null;
    this.showLogin();
  }

  // Dashboard
  async showDashboard() {
    const isAdmin = this.currentUser?.email === 'admin@finanza.com';
    
    document.body.innerHTML = `
      <div class="app-container">
        ${this.getSidebarHTML(isAdmin)}
        <div class="main-content">
          <div class="card-header">
            <h1 class="card-title">Dashboard</h1>
            <div>
              <span>Olá, ${this.currentUser.nome}!</span>
              <button class="btn btn-secondary ml-2" data-action="logout">Sair</button>
            </div>
          </div>
          
          <div id="statsGrid" class="stats-grid">
            <div class="stat-card">
              <div class="loading"></div>
            </div>
          </div>
          
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Resumo Financeiro</h2>
            </div>
            <div id="financialSummary">
              <div class="loading"></div>
            </div>
          </div>
        </div>
      </div>
    `;

    // Carregar dados do dashboard
    await this.loadDashboardData();
  }

  getSidebarHTML(isAdmin = false) {
    // Initialize icons system if not already done
    if (!this.icons) {
      this.icons = new window.FinanzaIcons();
    }

    return `
      <div class="sidebar">
        <div class="sidebar-logo">
          <div class="logo">F</div>
          <h2>Finanza</h2>
        </div>
        
        <ul class="nav-menu">
          <li class="nav-item">
            <a href="#" class="nav-link active" data-page="dashboard">
              <span class="nav-icon finanza-icon-wrapper">${this.icons.getIcon('dashboard')}</span>
              Dashboard
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link" data-page="accounts">
              <span class="nav-icon finanza-icon-wrapper">${this.icons.getIcon('accounts')}</span>
              Contas
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link" data-page="transactions">
              <span class="nav-icon finanza-icon-wrapper">${this.icons.getIcon('transactions')}</span>
              Transações
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link" data-page="profile">
              <span class="nav-icon finanza-icon-wrapper">${this.icons.getIcon('profile')}</span>
              Perfil
            </a>
          </li>
          ${isAdmin ? `
          <li class="nav-item">
            <a href="#" class="nav-link" data-page="admin">
              <span class="nav-icon finanza-icon-wrapper">${this.icons.getIcon('admin')}</span>
              Admin
            </a>
          </li>
          ` : ''}
        </ul>
      </div>
    `;
  }

  async loadDashboardData() {
    try {
      // Carregar resumo financeiro
      const summary = await this.api.getFinancialSummary();
      
      document.getElementById('statsGrid').innerHTML = `
        <div class="stat-card">
          <div class="stat-value stat-balance">R$ ${this.formatCurrency(summary.saldo_total)}</div>
          <div class="stat-label">Saldo Total</div>
        </div>
        <div class="stat-card">
          <div class="stat-value stat-income">R$ ${this.formatCurrency(summary.receitas_mes)}</div>
          <div class="stat-label">Receitas do Mês</div>
        </div>
        <div class="stat-card">
          <div class="stat-value stat-expense">R$ ${this.formatCurrency(summary.despesas_mes)}</div>
          <div class="stat-label">Despesas do Mês</div>
        </div>
        <div class="stat-card">
          <div class="stat-value stat-accounts">${summary.total_contas}</div>
          <div class="stat-label">Contas</div>
        </div>
      `;

      document.getElementById('financialSummary').innerHTML = `
        <p>Você possui <strong>${summary.total_contas}</strong> contas cadastradas.</p>
        <p>Neste mês você teve <strong>R$ ${this.formatCurrency(summary.receitas_mes)}</strong> em receitas e <strong>R$ ${this.formatCurrency(summary.despesas_mes)}</strong> em despesas.</p>
        <p>Seu saldo atual é de <strong>R$ ${this.formatCurrency(summary.saldo_total)}</strong>.</p>
      `;
    } catch (error) {
      this.showError('Erro ao carregar dados do dashboard');
    }
  }

  // Contas
  async showAccounts() {
    const mainContent = document.querySelector('.main-content');
    mainContent.innerHTML = `
      <div class="card-header">
        <h1 class="card-title">Gerenciar Contas</h1>
        <button class="btn btn-primary" onclick="app.showAccountModal()">Nova Conta</button>
      </div>
      
      <div class="card">
        <div id="accountsList">
          <div class="loading"></div>
        </div>
      </div>
    `;

    await this.loadAccounts();
  }

  async loadAccounts() {
    try {
      const accounts = await this.api.getAccounts();
      
      const accountsHTML = accounts.map(account => `
        <div class="card mb-2">
          <div class="card-header">
            <div>
              <h3>${account.nome}</h3>
              <p>Saldo: R$ ${this.formatCurrency(account.saldo_atual)}</p>
            </div>
            <div>
              <button class="btn btn-secondary" onclick="app.editAccount(${account.id}, '${account.nome}', ${account.saldo_inicial})">Editar</button>
              <button class="btn btn-danger ml-1" onclick="app.deleteAccount(${account.id}, '${account.nome}')">Excluir</button>
            </div>
          </div>
        </div>
      `).join('');

      document.getElementById('accountsList').innerHTML = accountsHTML || '<p>Nenhuma conta cadastrada.</p>';
    } catch (error) {
      this.showError('Erro ao carregar contas');
    }
  }

  // Utilitários
  formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }

  showError(message) {
    // Implementar sistema de notificações
    alert(message);
  }

  showSuccess(message) {
    // Implementar sistema de notificações
    alert(message);
  }

  showModal(content) {
    document.body.insertAdjacentHTML('beforeend', `
      <div class="modal active">
        <div class="modal-content">
          ${content}
        </div>
      </div>
    `);
  }

  closeModal() {
    const modal = document.querySelector('.modal');
    if (modal) {
      modal.remove();
    }
  }
}

// Inicializar aplicação
const app = new FinanzaApp();