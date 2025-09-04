// Finanza Desktop - Navigation Utilities

class FinanzaNavigation {
  constructor() {
    this.pages = {
      'dashboard': 'dashboard.html',
      'accounts': 'accounts.html',
      'transactions': 'transactions.html',
      'profile': 'profile.html',
      'admin': 'admin.html'
    };
  }

  // Navigate to a specific page
  navigateTo(page) {
    if (this.pages[page]) {
      window.location.href = this.pages[page];
    } else {
      console.error('Page not found:', page);
    }
  }

  // Get current page name from URL
  getCurrentPage() {
    const path = window.location.pathname;
    const filename = path.split('/').pop();
    return filename.replace('.html', '') || 'dashboard';
  }

  // Generate sidebar HTML
  generateSidebar(currentUser = null) {
    const isAdmin = currentUser?.email === 'admin@finanza.com';
    const currentPage = this.getCurrentPage();

    return `
      <div class="sidebar">
        <div class="sidebar-logo">
          <div class="logo logo-small">F</div>
          <h2>Finanza</h2>
        </div>
        
        <ul class="nav-menu">
          <li class="nav-item">
            <a href="dashboard.html" class="nav-link ${currentPage === 'dashboard' ? 'active' : ''}">
              <span class="nav-icon">
                <img src="../assets/images/ícone home.png" alt="Dashboard">
              </span>
              Dashboard
            </a>
          </li>
          <li class="nav-item">
            <a href="accounts.html" class="nav-link ${currentPage === 'accounts' ? 'active' : ''}">
              <span class="nav-icon">
                <img src="../assets/images/ícone banco.png" alt="Contas">
              </span>
              Contas
            </a>
          </li>
          <li class="nav-item">
            <a href="transactions.html" class="nav-link ${currentPage === 'transactions' ? 'active' : ''}">
              <span class="nav-icon">
                <img src="../assets/images/ícone movimentações.png" alt="Transações">
              </span>
              Transações
            </a>
          </li>
          <li class="nav-item">
            <a href="profile.html" class="nav-link ${currentPage === 'profile' ? 'active' : ''}">
              <span class="nav-icon">
                <img src="../assets/images/ícone menu.png" alt="Perfil">
              </span>
              Perfil
            </a>
          </li>
          ${isAdmin ? `
          <li class="nav-item">
            <a href="admin.html" class="nav-link ${currentPage === 'admin' ? 'active' : ''}">
              <span class="nav-icon">
                <img src="../assets/images/ícone menu.png" alt="Admin">
              </span>
              Admin
            </a>
          </li>
          ` : ''}
        </ul>
      </div>
    `;
  }

  // Generate main content header
  generateHeader(title, currentUser = null, showLogout = true) {
    return `
      <div class="card-header">
        <h1 class="card-title">${title}</h1>
        <div>
          ${currentUser ? `<span>Olá, ${currentUser.nome}!</span>` : ''}
          ${showLogout ? `<button class="btn btn-secondary ml-2" onclick="auth.logout()">Sair</button>` : ''}
        </div>
      </div>
    `;
  }

  // Set up navigation event listeners
  setupNavigation() {
    // Handle logout clicks
    document.addEventListener('click', (e) => {
      if (e.target.matches('[data-action="logout"]')) {
        e.preventDefault();
        if (window.auth) {
          window.auth.logout();
        }
      }
    });

    // Handle navigation clicks
    document.addEventListener('click', (e) => {
      if (e.target.matches('a[href$=".html"]')) {
        // Let normal navigation work, but could add tracking here
      }
    });
  }

  // Initialize navigation for a page
  async initPage() {
    this.setupNavigation();
    
    // Return current user if available
    if (window.auth) {
      return await window.auth.getCurrentUser();
    }
    return null;
  }
}

// Export for use in other files
window.FinanzaNavigation = FinanzaNavigation;