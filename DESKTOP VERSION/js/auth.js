// Finanza Desktop - Authentication Utilities

class FinanzaAuth {
  constructor() {
    this.api = new FinanzaAPI();
    this.currentUser = null;
  }

  // Check if user is authenticated
  isAuthenticated() {
    return localStorage.getItem('finanza_token') !== null;
  }

  // Get current user from API
  async getCurrentUser() {
    if (!this.isAuthenticated()) {
      return null;
    }

    try {
      if (!this.currentUser) {
        this.currentUser = await this.api.getCurrentUser();
      }
      return this.currentUser;
    } catch (error) {
      console.error('Error getting current user:', error);
      this.logout();
      return null;
    }
  }

  // Handle login
  async login(email, senha) {
    try {
      const response = await this.api.login(email, senha);
      this.currentUser = response.user;
      return response;
    } catch (error) {
      throw error;
    }
  }

  // Handle registration
  async register(nome, email, senha) {
    try {
      const response = await this.api.register(nome, email, senha);
      this.currentUser = response.user;
      return response;
    } catch (error) {
      throw error;
    }
  }

  // Handle logout
  logout() {
    this.api.logout();
    this.currentUser = null;
    window.location.href = 'login.html';
  }

  // Redirect to login if not authenticated
  requireAuth() {
    if (!this.isAuthenticated()) {
      window.location.href = 'login.html';
      return false;
    }
    return true;
  }

  // Redirect to dashboard if already authenticated
  redirectIfAuthenticated() {
    if (this.isAuthenticated()) {
      window.location.href = 'dashboard.html';
      return true;
    }
    return false;
  }

  // Format currency helper
  formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value || 0);
  }

  // Show error message
  showError(message, containerId = 'errorContainer') {
    const container = document.getElementById(containerId);
    if (container) {
      container.textContent = message;
      container.classList.remove('hidden');
    }
  }

  // Hide error message
  hideError(containerId = 'errorContainer') {
    const container = document.getElementById(containerId);
    if (container) {
      container.classList.add('hidden');
    }
  }

  // Show success message
  showSuccess(message, containerId = 'successContainer') {
    const container = document.getElementById(containerId);
    if (container) {
      container.textContent = message;
      container.classList.remove('hidden');
    }
  }

  // Show loading state
  showLoading(buttonElement, loadingText = 'Carregando...') {
    if (buttonElement) {
      buttonElement.disabled = true;
      buttonElement.dataset.originalText = buttonElement.textContent;
      buttonElement.innerHTML = `<span class="loading"></span> ${loadingText}`;
    }
  }

  // Hide loading state
  hideLoading(buttonElement) {
    if (buttonElement && buttonElement.dataset.originalText) {
      buttonElement.disabled = false;
      buttonElement.textContent = buttonElement.dataset.originalText;
      delete buttonElement.dataset.originalText;
    }
  }
}

// Export for use in other files
window.FinanzaAuth = FinanzaAuth;