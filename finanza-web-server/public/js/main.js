// Finanza Web - Main JavaScript

class FinanzaApp {
    constructor() {
        this.apiBase = '/api';
        this.socket = null;
        this.init();
    }

    init() {
        this.initSocket();
        this.bindEvents();
        this.loadInitialData();
    }

    // WebSocket connection
    initSocket() {
        this.socket = io();
        
        this.socket.on('connect', () => {
            console.log('WebSocket conectado');
            this.updateConnectionStatus(true);
        });
        
        this.socket.on('disconnect', () => {
            console.log('WebSocket desconectado');
            this.updateConnectionStatus(false);
        });
    }

    // Bind event listeners
    bindEvents() {
        // Login form
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Register form
        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', (e) => this.handleRegister(e));
        }

        // Test connection button
        const testConnectionBtn = document.getElementById('testConnection');
        if (testConnectionBtn) {
            testConnectionBtn.addEventListener('click', () => this.testServerConnection());
        }

        // Sync buttons
        document.querySelectorAll('[data-sync]').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleSync(e));
        });
    }

    // Load initial data
    loadInitialData() {
        if (window.location.pathname === '/dashboard') {
            this.loadDashboardData();
        } else if (window.location.pathname === '/server') {
            this.loadServerStatus();
        }
    }

    // API methods
    async apiCall(endpoint, options = {}) {
        try {
            const response = await fetch(this.apiBase + endpoint, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || 'Erro na requisição');
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            this.showMessage(error.message, 'error');
            throw error;
        }
    }

    // Authentication
    async handleLogin(e) {
        e.preventDefault();
        
        const formData = new FormData(e.target);
        const loginData = {
            email: formData.get('email'),
            senha: formData.get('senha')
        };

        this.showLoading('loginBtn');

        try {
            const response = await this.apiCall('/auth/login', {
                method: 'POST',
                body: JSON.stringify(loginData)
            });

            if (response.success) {
                localStorage.setItem('finanza_token', response.token);
                this.showMessage('Login realizado com sucesso!', 'success');
                setTimeout(() => {
                    window.location.href = '/dashboard';
                }, 1000);
            } else {
                this.showMessage(response.message, 'error');
            }
        } catch (error) {
            // Error already handled in apiCall
        } finally {
            this.hideLoading('loginBtn');
        }
    }

    async handleRegister(e) {
        e.preventDefault();
        
        const formData = new FormData(e.target);
        const registerData = {
            nome: formData.get('nome'),
            email: formData.get('email'),
            senha: formData.get('senha')
        };

        // Simple password confirmation check
        const confirmSenha = formData.get('confirmSenha');
        if (registerData.senha !== confirmSenha) {
            this.showMessage('As senhas não conferem', 'error');
            return;
        }

        this.showLoading('registerBtn');

        try {
            const response = await this.apiCall('/auth/register', {
                method: 'POST',
                body: JSON.stringify(registerData)
            });

            if (response.success) {
                this.showMessage('Cadastro realizado com sucesso!', 'success');
                setTimeout(() => {
                    window.location.href = '/login';
                }, 1000);
            } else {
                this.showMessage(response.message, 'error');
            }
        } catch (error) {
            // Error already handled in apiCall
        } finally {
            this.hideLoading('registerBtn');
        }
    }

    // Dashboard
    async loadDashboardData() {
        try {
            // Load user data, accounts, recent transactions
            const userId = 1; // For demo, in real app get from token
            
            const [userResponse, accountsResponse, transactionsResponse] = await Promise.all([
                this.apiCall(`/sync/user/${userId}`),
                this.apiCall(`/sync/accounts/${userId}`),
                this.apiCall(`/sync/transactions/${userId}`)
            ]);

            this.updateDashboard({
                user: userResponse,
                accounts: accountsResponse,
                transactions: transactionsResponse
            });

        } catch (error) {
            console.error('Error loading dashboard:', error);
        }
    }

    updateDashboard(data) {
        // Update dashboard with loaded data
        const userGreeting = document.getElementById('userGreeting');
        if (userGreeting) {
            userGreeting.textContent = `Bem-vindo, Usuário!`;
        }

        // Update stats cards
        this.updateElement('totalAccounts', '0');
        this.updateElement('totalTransactions', '0');
        this.updateElement('totalBalance', 'R$ 0,00');
    }

    // Server management
    async loadServerStatus() {
        try {
            const response = await fetch('/server/api/status');
            const status = await response.json();
            
            this.updateServerStatus(status);
        } catch (error) {
            console.error('Error loading server status:', error);
        }
    }

    updateServerStatus(status) {
        // Update server status display
        const webStatus = document.getElementById('webServerStatus');
        const javaStatus = document.getElementById('javaServerStatus');
        
        if (webStatus) {
            webStatus.innerHTML = `
                <span class="status status-online">Online</span>
                <small>Uptime: ${Math.floor(status.webServer.uptime / 60)}m</small>
            `;
        }
        
        if (javaStatus) {
            const isOnline = status.javaServer.connected;
            javaStatus.innerHTML = `
                <span class="status ${isOnline ? 'status-online' : 'status-offline'}">
                    ${isOnline ? 'Online' : 'Offline'}
                </span>
                <small>${status.javaServer.host}:${status.javaServer.port}</small>
            `;
        }
    }

    async testServerConnection() {
        this.showLoading('testConnection');
        
        try {
            const response = await fetch('/server/api/test-connection', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({})
            });
            
            const result = await response.json();
            
            if (result.connected) {
                this.showMessage('Conexão com servidor Java OK!', 'success');
            } else {
                this.showMessage('Falha na conexão: ' + result.error, 'error');
            }
        } catch (error) {
            this.showMessage('Erro ao testar conexão', 'error');
        } finally {
            this.hideLoading('testConnection');
        }
    }

    // Synchronization
    async handleSync(e) {
        const syncType = e.target.dataset.sync;
        const userId = 1; // For demo
        
        this.showLoading(e.target.id);
        
        try {
            let endpoint;
            switch (syncType) {
                case 'user':
                    endpoint = `/sync/user/${userId}`;
                    break;
                case 'accounts':
                    endpoint = `/sync/accounts/${userId}`;
                    break;
                case 'transactions':
                    endpoint = `/sync/transactions/${userId}`;
                    break;
                case 'categories':
                    endpoint = `/sync/categories`;
                    break;
                default:
                    throw new Error('Tipo de sincronização inválido');
            }
            
            const response = await this.apiCall(endpoint);
            this.showMessage(response.message, 'success');
            
        } catch (error) {
            // Error already handled in apiCall
        } finally {
            this.hideLoading(e.target.id);
        }
    }

    // UI helpers
    updateConnectionStatus(connected) {
        const indicator = document.getElementById('connectionIndicator');
        if (indicator) {
            indicator.className = `status ${connected ? 'status-online' : 'status-offline'}`;
            indicator.textContent = connected ? 'Conectado' : 'Desconectado';
        }
    }

    updateElement(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    }

    showMessage(message, type = 'info') {
        // Create or update message container
        let messageContainer = document.getElementById('messageContainer');
        if (!messageContainer) {
            messageContainer = document.createElement('div');
            messageContainer.id = 'messageContainer';
            messageContainer.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 1000;
                max-width: 400px;
            `;
            document.body.appendChild(messageContainer);
        }

        const messageEl = document.createElement('div');
        messageEl.className = `alert alert-${type}`;
        messageEl.style.cssText = `
            padding: 1rem;
            margin-bottom: 0.5rem;
            border-radius: 6px;
            color: white;
            background-color: ${type === 'success' ? 'var(--success-color)' : 
                              type === 'error' ? 'var(--danger-color)' : 
                              'var(--primary-color)'};
        `;
        messageEl.textContent = message;

        messageContainer.appendChild(messageEl);

        // Auto remove after 5 seconds
        setTimeout(() => {
            messageEl.remove();
        }, 5000);
    }

    showLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.disabled = true;
            element.innerHTML = '<span class="loading"></span> Carregando...';
        }
    }

    hideLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.disabled = false;
            // Restore original text - in a real app, store original text
            element.innerHTML = element.textContent.replace('Carregando...', '').trim();
        }
    }
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.finanzaApp = new FinanzaApp();
});