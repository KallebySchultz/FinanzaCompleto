// Modal Management Module
// Handles all modal operations and UI interactions

const ModalsModule = {
    currentModal: null,
    modalStack: [],
    
    // Initialize modals
    init() {
        this.setupEventListeners();
        this.setupKeyboardShortcuts();
    },
    
    // Setup event listeners
    setupEventListeners() {
        // Modal overlay click to close
        document.getElementById('modalOverlay').addEventListener('click', (e) => {
            if (e.target === e.currentTarget) {
                this.closeModal();
            }
        });
        
        // Modal close buttons
        document.querySelectorAll('.modal-close').forEach(button => {
            button.addEventListener('click', () => {
                this.closeModal();
            });
        });
        
        // Prevent modal content clicks from closing modal
        document.querySelectorAll('.modal-content').forEach(content => {
            content.addEventListener('click', (e) => {
                e.stopPropagation();
            });
        });
    },
    
    // Setup keyboard shortcuts
    setupKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            // Escape key to close modal
            if (e.key === 'Escape' && this.currentModal) {
                this.closeModal();
            }
            
            // Ctrl/Cmd + N for new ticket
            if ((e.ctrlKey || e.metaKey) && e.key === 'n' && !this.currentModal) {
                e.preventDefault();
                if (App.currentSection === 'tickets') {
                    TicketsModule.showNewTicketModal();
                } else if (App.currentSection === 'customers') {
                    CustomersModule.showNewCustomerModal();
                }
            }
        });
    },
    
    // Show modal
    showModal(modalId, options = {}) {
        const modal = document.getElementById(modalId);
        const overlay = document.getElementById('modalOverlay');
        
        if (!modal) {
            console.error('Modal not found:', modalId);
            return;
        }
        
        // Add to modal stack
        this.modalStack.push(this.currentModal);
        this.currentModal = modalId;
        
        // Show overlay and modal
        overlay.classList.add('active');
        modal.style.display = 'block';
        
        // Focus first input if available
        setTimeout(() => {
            const firstInput = modal.querySelector('input, select, textarea');
            if (firstInput && !options.skipFocus) {
                firstInput.focus();
            }
        }, 100);
        
        // Prevent body scroll
        document.body.style.overflow = 'hidden';
        
        // Add modal class to body for CSS targeting
        document.body.classList.add('modal-open');
        
        // Trigger custom event
        window.dispatchEvent(new CustomEvent('modalOpened', { 
            detail: { modalId, options } 
        }));
    },
    
    // Close modal
    closeModal(modalId = null) {
        const targetModal = modalId || this.currentModal;
        
        if (!targetModal) return;
        
        const modal = document.getElementById(targetModal);
        const overlay = document.getElementById('modalOverlay');
        
        if (modal) {
            modal.style.display = 'none';
        }
        
        // Check if this is the last modal
        if (this.modalStack.length === 0 || targetModal === this.currentModal) {
            overlay.classList.remove('active');
            document.body.style.overflow = '';
            document.body.classList.remove('modal-open');
        }
        
        // Update current modal
        if (targetModal === this.currentModal) {
            this.currentModal = this.modalStack.pop() || null;
        }
        
        // Clear form data if it's a form modal
        const form = modal?.querySelector('form');
        if (form) {
            form.reset();
        }
        
        // Trigger custom event
        window.dispatchEvent(new CustomEvent('modalClosed', { 
            detail: { modalId: targetModal } 
        }));
    },
    
    // Close all modals
    closeAllModals() {
        this.modalStack = [];
        this.currentModal = null;
        
        document.querySelectorAll('.modal').forEach(modal => {
            modal.style.display = 'none';
        });
        
        document.getElementById('modalOverlay').classList.remove('active');
        document.body.style.overflow = '';
        document.body.classList.remove('modal-open');
    },
    
    // Create dynamic modal
    createModal(id, title, content, options = {}) {
        const existingModal = document.getElementById(id);
        if (existingModal) {
            existingModal.remove();
        }
        
        const modal = document.createElement('div');
        modal.id = id;
        modal.className = 'modal';
        modal.innerHTML = `
            <div class="modal-content ${options.size || 'medium'}">
                <div class="modal-header">
                    <h3>${title}</h3>
                    <button class="modal-close" data-modal="${id}">&times;</button>
                </div>
                <div class="modal-body">
                    ${content}
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // Add close event listener
        modal.querySelector('.modal-close').addEventListener('click', () => {
            this.closeModal(id);
        });
        
        return modal;
    },
    
    // Show confirmation dialog
    showConfirmDialog(title, message, onConfirm, onCancel = null) {
        const modalId = 'confirmDialog';
        
        const content = `
            <div class="confirm-dialog">
                <div class="confirm-message">
                    <i class="fas fa-question-circle"></i>
                    <p>${message}</p>
                </div>
                <div class="confirm-actions">
                    <button type="button" class="btn-secondary" onclick="ModalsModule.closeModal('${modalId}')">
                        Cancelar
                    </button>
                    <button type="button" class="btn-primary" id="confirmButton">
                        Confirmar
                    </button>
                </div>
            </div>
        `;
        
        this.createModal(modalId, title, content, { size: 'small' });
        
        // Add confirm handler
        document.getElementById('confirmButton').addEventListener('click', () => {
            if (onConfirm) onConfirm();
            this.closeModal(modalId);
        });
        
        this.showModal(modalId);
    },
    
    // Show alert dialog
    showAlert(title, message, type = 'info') {
        const modalId = 'alertDialog';
        
        const icons = {
            info: 'fas fa-info-circle',
            success: 'fas fa-check-circle',
            warning: 'fas fa-exclamation-triangle',
            error: 'fas fa-times-circle'
        };
        
        const content = `
            <div class="alert-dialog ${type}">
                <div class="alert-message">
                    <i class="${icons[type]}"></i>
                    <p>${message}</p>
                </div>
                <div class="alert-actions">
                    <button type="button" class="btn-primary" onclick="ModalsModule.closeModal('${modalId}')">
                        OK
                    </button>
                </div>
            </div>
        `;
        
        this.createModal(modalId, title, content, { size: 'small' });
        this.showModal(modalId);
    },
    
    // Show loading modal
    showLoading(message = 'Carregando...') {
        const modalId = 'loadingModal';
        
        const content = `
            <div class="loading-dialog">
                <div class="loading-spinner">
                    <i class="fas fa-spinner fa-spin"></i>
                </div>
                <p>${message}</p>
            </div>
        `;
        
        this.createModal(modalId, '', content, { size: 'small' });
        this.showModal(modalId, { skipFocus: true });
        
        return modalId;
    },
    
    // Hide loading modal
    hideLoading() {
        this.closeModal('loadingModal');
    },
    
    // Show image preview modal
    showImagePreview(src, title = '') {
        const modalId = 'imagePreviewModal';
        
        const content = `
            <div class="image-preview">
                <img src="${src}" alt="${title}" style="max-width: 100%; max-height: 80vh;">
                ${title ? `<p class="image-title">${title}</p>` : ''}
            </div>
        `;
        
        this.createModal(modalId, title || 'Visualizar Imagem', content, { size: 'large' });
        this.showModal(modalId);
    },
    
    // Check if modal is open
    isModalOpen(modalId = null) {
        if (modalId) {
            return this.currentModal === modalId || this.modalStack.includes(modalId);
        }
        return this.currentModal !== null;
    },
    
    // Get current modal
    getCurrentModal() {
        return this.currentModal;
    }
};

// Notification System
const NotificationSystem = {
    notifications: [],
    container: null,
    
    // Initialize notification system
    init() {
        this.createContainer();
    },
    
    // Create notification container
    createContainer() {
        this.container = document.createElement('div');
        this.container.id = 'notificationContainer';
        this.container.className = 'notification-container';
        document.body.appendChild(this.container);
    },
    
    // Show notification
    show(message, type = 'info', duration = 5000) {
        const notification = this.createNotification(message, type, duration);
        this.notifications.push(notification);
        this.container.appendChild(notification.element);
        
        // Animate in
        setTimeout(() => {
            notification.element.classList.add('show');
        }, 10);
        
        // Auto remove
        if (duration > 0) {
            setTimeout(() => {
                this.remove(notification.id);
            }, duration);
        }
        
        return notification.id;
    },
    
    // Create notification element
    createNotification(message, type, duration) {
        const id = 'notification_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
        
        const icons = {
            success: 'fas fa-check-circle',
            error: 'fas fa-times-circle',
            warning: 'fas fa-exclamation-triangle',
            info: 'fas fa-info-circle'
        };
        
        const element = document.createElement('div');
        element.id = id;
        element.className = `notification notification-${type}`;
        element.innerHTML = `
            <div class="notification-content">
                <i class="${icons[type]}"></i>
                <span class="notification-message">${message}</span>
            </div>
            <button class="notification-close" onclick="NotificationSystem.remove('${id}')">
                <i class="fas fa-times"></i>
            </button>
            ${duration > 0 ? `<div class="notification-progress" style="animation-duration: ${duration}ms;"></div>` : ''}
        `;
        
        return { id, element, type, message, duration };
    },
    
    // Remove notification
    remove(id) {
        const notification = document.getElementById(id);
        if (notification) {
            notification.classList.add('hide');
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }
        
        // Remove from array
        this.notifications = this.notifications.filter(n => n.id !== id);
    },
    
    // Clear all notifications
    clearAll() {
        this.notifications.forEach(notification => {
            this.remove(notification.id);
        });
    },
    
    // Show success notification
    success(message, duration = 5000) {
        return this.show(message, 'success', duration);
    },
    
    // Show error notification
    error(message, duration = 8000) {
        return this.show(message, 'error', duration);
    },
    
    // Show warning notification
    warning(message, duration = 6000) {
        return this.show(message, 'warning', duration);
    },
    
    // Show info notification
    info(message, duration = 5000) {
        return this.show(message, 'info', duration);
    }
};

// Tooltip System
const TooltipSystem = {
    tooltip: null,
    
    // Initialize tooltip system
    init() {
        this.createTooltip();
        this.setupEventListeners();
    },
    
    // Create tooltip element
    createTooltip() {
        this.tooltip = document.createElement('div');
        this.tooltip.className = 'tooltip';
        this.tooltip.style.display = 'none';
        document.body.appendChild(this.tooltip);
    },
    
    // Setup event listeners
    setupEventListeners() {
        document.addEventListener('mouseover', (e) => {
            const element = e.target.closest('[data-tooltip]');
            if (element) {
                this.show(element.dataset.tooltip, e);
            }
        });
        
        document.addEventListener('mouseout', (e) => {
            const element = e.target.closest('[data-tooltip]');
            if (element) {
                this.hide();
            }
        });
        
        document.addEventListener('mousemove', (e) => {
            if (this.tooltip.style.display === 'block') {
                this.updatePosition(e);
            }
        });
    },
    
    // Show tooltip
    show(text, event) {
        this.tooltip.textContent = text;
        this.tooltip.style.display = 'block';
        this.updatePosition(event);
    },
    
    // Hide tooltip
    hide() {
        this.tooltip.style.display = 'none';
    },
    
    // Update tooltip position
    updatePosition(event) {
        const x = event.clientX + 10;
        const y = event.clientY - 30;
        
        this.tooltip.style.left = x + 'px';
        this.tooltip.style.top = y + 'px';
    }
};

// Form Validation
const FormValidator = {
    rules: {
        required: (value) => value.trim() !== '',
        email: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
        phone: (value) => /^[\+]?[1-9][\d]{0,15}$/.test(value.replace(/\s/g, '')),
        minLength: (value, min) => value.length >= min,
        maxLength: (value, max) => value.length <= max
    },
    
    messages: {
        required: 'Este campo é obrigatório',
        email: 'Digite um email válido',
        phone: 'Digite um telefone válido',
        minLength: 'Mínimo de {min} caracteres',
        maxLength: 'Máximo de {max} caracteres'
    },
    
    // Validate form
    validateForm(form) {
        const errors = [];
        const inputs = form.querySelectorAll('[data-validate]');
        
        inputs.forEach(input => {
            const rules = input.dataset.validate.split('|');
            const fieldErrors = this.validateField(input, rules);
            
            if (fieldErrors.length > 0) {
                errors.push({
                    field: input.name || input.id,
                    errors: fieldErrors
                });
                
                this.showFieldError(input, fieldErrors[0]);
            } else {
                this.clearFieldError(input);
            }
        });
        
        return errors;
    },
    
    // Validate single field
    validateField(input, rules) {
        const errors = [];
        const value = input.value;
        
        rules.forEach(rule => {
            const [ruleName, ...params] = rule.split(':');
            
            if (this.rules[ruleName]) {
                const isValid = this.rules[ruleName](value, ...params);
                
                if (!isValid) {
                    let message = this.messages[ruleName] || 'Campo inválido';
                    
                    // Replace parameters in message
                    params.forEach((param, index) => {
                        message = message.replace(`{${Object.keys(params)[index]}}`, param);
                    });
                    
                    errors.push(message);
                }
            }
        });
        
        return errors;
    },
    
    // Show field error
    showFieldError(input, message) {
        this.clearFieldError(input);
        
        input.classList.add('error');
        
        const errorElement = document.createElement('div');
        errorElement.className = 'field-error';
        errorElement.textContent = message;
        
        input.parentNode.appendChild(errorElement);
    },
    
    // Clear field error
    clearFieldError(input) {
        input.classList.remove('error');
        
        const errorElement = input.parentNode.querySelector('.field-error');
        if (errorElement) {
            errorElement.remove();
        }
    }
};

// Initialize all modules when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    ModalsModule.init();
    NotificationSystem.init();
    TooltipSystem.init();
});

// Make modules globally available
window.ModalsModule = ModalsModule;
window.NotificationSystem = NotificationSystem;
window.TooltipSystem = TooltipSystem;
window.FormValidator = FormValidator;