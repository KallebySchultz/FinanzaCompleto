// Finanza Icon System
// Modern SVG icons that match the blue gradient theme

class FinanzaIcons {
  constructor() {
    this.iconCache = new Map();
  }

  // Dashboard/Analytics Icon
  getDashboardIcon() {
    return `
      <svg class="finanza-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M3 3V21H21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M9 9L12 6L16 10L20 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <circle cx="9" cy="9" r="1" fill="currentColor"/>
        <circle cx="12" cy="6" r="1" fill="currentColor"/>
        <circle cx="16" cy="10" r="1" fill="currentColor"/>
        <circle cx="20" cy="7" r="1" fill="currentColor"/>
      </svg>
    `;
  }

  // Bank/Accounts Icon
  getBankIcon() {
    return `
      <svg class="finanza-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M3 21H21M5 21V7L12 3L19 7V21M9 21V12H15V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M9 7H10M14 7H15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    `;
  }

  // Money/Transactions Icon
  getMoneyIcon() {
    return `
      <svg class="finanza-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
        <path d="M12 6V18M9 12H15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        <path d="M15 8.5C15 7.67 14.33 7 13.5 7H10.5C9.67 7 9 7.67 9 8.5S9.67 10 10.5 10H13.5C14.33 10 15 10.67 15 11.5S14.33 13 13.5 13H10.5C9.67 13 9 13.33 9 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
    `;
  }

  // User/Profile Icon
  getUserIcon() {
    return `
      <svg class="finanza-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2"/>
        <path d="M6 21V19C6 16.79 7.79 15 10 15H14C16.21 15 18 16.79 18 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    `;
  }

  // Settings/Admin Icon
  getSettingsIcon() {
    return `
      <svg class="finanza-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
        <path d="M19.4 15A1.65 1.65 0 0 0 20.25 13.38L20.47 13A1.65 1.65 0 0 0 19.82 10.5L19.4 9A1.65 1.65 0 0 0 17.75 8.25L17.38 8.03A1.65 1.65 0 0 0 14.88 8.68L13.5 9.82A1.65 1.65 0 0 0 13.25 11.75L13.03 12.38A1.65 1.65 0 0 0 13.68 14.88L14.82 16.25A1.65 1.65 0 0 0 16.75 16.5L17.38 16.72A1.65 1.65 0 0 0 19.88 16.07L21.25 14.82A1.65 1.65 0 0 0 21.5 12.75L21.28 12.12A1.65 1.65 0 0 0 20.63 9.62L19.25 8.25A1.65 1.65 0 0 0 17.12 8L16.5 8.22A1.65 1.65 0 0 0 14 8.87L12.75 10.25A1.65 1.65 0 0 0 12.5 12.38L12.72 13A1.65 1.65 0 0 0 13.37 15.5L14.75 16.87A1.65 1.65 0 0 0 16.88 17.12L17.5 16.9A1.65 1.65 0 0 0 20 16.25L21.37 14.87A1.65 1.65 0 0 0 21.62 12.75L21.4 12.12A1.65 1.65 0 0 0 20.75 9.62L19.37 8.25" stroke="currentColor" stroke-width="1.5"/>
        <path d="M12 1V3M12 21V23M4.22 4.22L5.64 5.64M18.36 18.36L19.78 19.78M1 12H3M21 12H23M4.22 19.78L5.64 18.36M18.36 5.64L19.78 4.22" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
    `;
  }

  // Simplified settings icon for better visibility
  getSimpleSettingsIcon() {
    return `
      <svg class="finanza-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
        <path d="M12 1V3M12 21V23M4.22 4.22L5.64 5.64M18.36 18.36L19.78 19.78M1 12H3M21 12H23M4.22 19.78L5.64 18.36M18.36 5.64L19.78 4.22" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    `;
  }

  // Get icon by name
  getIcon(iconName) {
    if (this.iconCache.has(iconName)) {
      return this.iconCache.get(iconName);
    }

    let icon;
    switch (iconName) {
      case 'dashboard':
        icon = this.getDashboardIcon();
        break;
      case 'bank':
      case 'accounts':
        icon = this.getBankIcon();
        break;
      case 'money':
      case 'transactions':
        icon = this.getMoneyIcon();
        break;
      case 'user':
      case 'profile':
        icon = this.getUserIcon();
        break;
      case 'settings':
      case 'admin':
        icon = this.getSimpleSettingsIcon();
        break;
      default:
        icon = `<span class="finanza-icon-fallback">${iconName}</span>`;
    }

    this.iconCache.set(iconName, icon);
    return icon;
  }

  // Create icon element
  createIcon(iconName, className = '') {
    const iconHtml = this.getIcon(iconName);
    const wrapper = document.createElement('span');
    wrapper.className = `nav-icon finanza-icon-wrapper ${className}`;
    wrapper.innerHTML = iconHtml;
    return wrapper;
  }
}

// Export for use in the main app
window.FinanzaIcons = FinanzaIcons;