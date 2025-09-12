// Customers Module
// Handles all customer-related functionality

const CustomersModule = {
    customers: [],
    organizations: [],
    
    // Initialize customers module
    init() {
        this.loadCustomers();
        this.loadOrganizations();
    },
    
    // Load customers from API or local storage
    async loadCustomers() {
        try {
            // Try to load from API first
            const response = await API.getCustomers();
            this.customers = response.data || [];
            App.data.customers = this.customers;
        } catch (error) {
            console.warn('API not available, using local data');
            // Fallback to local data
            this.customers = App.data.customers || [];
        }
        
        this.renderCustomersGrid();
    },
    
    // Load organizations
    async loadOrganizations() {
        try {
            const response = await API.getOrganizations();
            this.organizations = response.data || [];
        } catch (error) {
            console.warn('Organizations API not available');
            // Default organizations
            this.organizations = [
                { id: 1, name: 'Acme Corporation', domain: 'acme.com' },
                { id: 2, name: 'Tech Solutions Inc', domain: 'techsolutions.com' }
            ];
        }
    },
    
    // Render customers grid
    renderCustomersGrid() {
        const container = document.getElementById('customersGrid');
        
        if (this.customers.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-users"></i>
                    <h3>Nenhum cliente encontrado</h3>
                    <p>Adicione o primeiro cliente para começar a gerenciar chamados.</p>
                    <button class="btn-primary" onclick="CustomersModule.showNewCustomerModal()">
                        <i class="fas fa-plus"></i> Adicionar Cliente
                    </button>
                </div>
            `;
            return;
        }
        
        container.innerHTML = this.customers.map(customer => {
            const customerTickets = App.data.tickets.filter(t => t.customerId === customer.id);
            const openTickets = customerTickets.filter(t => {
                const status = App.data.statuses.find(s => s.id === t.statusId);
                return status?.isOpen;
            }).length;
            
            const closedTickets = customerTickets.filter(t => {
                const status = App.data.statuses.find(s => s.id === t.statusId);
                return status?.isClosed;
            }).length;
            
            return `
                <div class="customer-card" onclick="CustomersModule.showCustomerDetails(${customer.id})">
                    <div class="customer-header">
                        <div class="customer-avatar">
                            ${customer.avatar ? 
                                `<img src="${customer.avatar}" alt="${customer.firstName}">` : 
                                customer.firstName.charAt(0).toUpperCase()
                            }
                        </div>
                        <div class="customer-info">
                            <h4>${customer.firstName} ${customer.lastName}</h4>
                            <p>${customer.organization || 'Pessoa Física'}</p>
                        </div>
                        <div class="customer-actions">
                            <button class="action-btn edit" onclick="event.stopPropagation(); CustomersModule.editCustomer(${customer.id})" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete" onclick="event.stopPropagation(); CustomersModule.deleteCustomer(${customer.id})" title="Excluir">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    
                    <div class="customer-details">
                        <div class="customer-detail">
                            <i class="fas fa-envelope"></i>
                            <span>${customer.email}</span>
                        </div>
                        <div class="customer-detail">
                            <i class="fas fa-phone"></i>
                            <span>${customer.phone || 'Não informado'}</span>
                        </div>
                        ${customer.organization ? `
                        <div class="customer-detail">
                            <i class="fas fa-building"></i>
                            <span>${customer.organization}</span>
                        </div>
                        ` : ''}
                    </div>
                    
                    <div class="customer-stats">
                        <div class="customer-stat">
                            <span class="number">${customerTickets.length}</span>
                            <span class="label">Total</span>
                        </div>
                        <div class="customer-stat">
                            <span class="number">${openTickets}</span>
                            <span class="label">Abertos</span>
                        </div>
                        <div class="customer-stat">
                            <span class="number">${closedTickets}</span>
                            <span class="label">Fechados</span>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    },
    
    // Show new customer modal
    showNewCustomerModal() {
        const modal = document.getElementById('newTicketModal');
        const modalHeader = modal.querySelector('.modal-header h3');
        const form = document.getElementById('newTicketForm');
        
        modalHeader.textContent = 'Novo Cliente';
        
        form.innerHTML = `
            <div class="customer-form">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Nome *</label>
                        <input type="text" class="form-input" name="firstName" required placeholder="Digite o nome">
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">Sobrenome *</label>
                        <input type="text" class="form-input" name="lastName" required placeholder="Digite o sobrenome">
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Email *</label>
                    <input type="email" class="form-input" name="email" required placeholder="Digite o email">
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Telefone</label>
                        <input type="tel" class="form-input" name="phone" placeholder="(00) 00000-0000">
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">Organização</label>
                        <select class="form-select" name="organizationId">
                            <option value="">Pessoa Física</option>
                            ${this.organizations.map(org => 
                                `<option value="${org.id}">${org.name}</option>`
                            ).join('')}
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Nova Organização</label>
                    <input type="text" class="form-input" name="newOrganization" placeholder="Digite o nome da organização">
                    <small class="form-hint">Deixe em branco para usar uma organização existente</small>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Foto do Perfil</label>
                    <div class="avatar-upload-area">
                        <div class="avatar-preview" id="avatarPreview">
                            <i class="fas fa-user-circle"></i>
                        </div>
                        <div class="avatar-upload-content">
                            <button type="button" class="btn-secondary" onclick="document.getElementById('avatarInput').click()">
                                <i class="fas fa-camera"></i> Escolher Foto
                            </button>
                            <p class="upload-hint">JPG, PNG até 2MB</p>
                        </div>
                        <input type="file" id="avatarInput" name="avatar" accept="image/*" style="display: none">
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="button" class="btn-secondary" onclick="App.closeModal()">Cancelar</button>
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-plus"></i> Criar Cliente
                    </button>
                </div>
            </div>
        `;
        
        // Add avatar preview
        document.getElementById('avatarInput').addEventListener('change', this.handleAvatarUpload);
        
        // Add form submit handler
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.createCustomer(new FormData(form));
        });
        
        App.showModal('newTicketModal');
    },
    
    // Handle avatar upload preview
    handleAvatarUpload(event) {
        const file = event.target.files[0];
        const preview = document.getElementById('avatarPreview');
        
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.innerHTML = `<img src="${e.target.result}" alt="Avatar Preview">`;
            };
            reader.readAsDataURL(file);
        }
    },
    
    // Create new customer
    async createCustomer(formData) {
        try {
            const customerData = {
                firstName: formData.get('firstName'),
                lastName: formData.get('lastName'),
                email: formData.get('email'),
                phone: formData.get('phone'),
                organizationId: formData.get('organizationId') ? parseInt(formData.get('organizationId')) : null,
                newOrganization: formData.get('newOrganization')
            };
            
            // Handle new organization
            if (customerData.newOrganization && !customerData.organizationId) {
                customerData.organization = customerData.newOrganization;
            } else if (customerData.organizationId) {
                const org = this.organizations.find(o => o.id === customerData.organizationId);
                customerData.organization = org?.name;
            }
            
            // Try API first
            try {
                const response = await API.createCustomer(customerData);
                App.showNotification('Cliente criado com sucesso!', 'success');
            } catch (apiError) {
                // Fallback to local creation
                this.createCustomerLocally(customerData);
                App.showNotification('Cliente criado localmente!', 'info');
            }
            
            App.closeModal();
            this.loadCustomers();
            
        } catch (error) {
            App.showNotification('Erro ao criar cliente: ' + error.message, 'error');
        }
    },
    
    // Create customer locally
    createCustomerLocally(customerData) {
        const newCustomer = {
            id: this.customers.length + 1,
            firstName: customerData.firstName,
            lastName: customerData.lastName,
            email: customerData.email,
            phone: customerData.phone,
            organization: customerData.organization,
            avatar: null, // Handle file upload separately
            createdAt: new Date().toISOString()
        };
        
        this.customers.push(newCustomer);
        App.data.customers = this.customers;
        App.saveData();
    },
    
    // Show customer details
    showCustomerDetails(customerId) {
        const customer = this.customers.find(c => c.id === customerId);
        if (!customer) return;
        
        const customerTickets = App.data.tickets.filter(t => t.customerId === customerId);
        const openTickets = customerTickets.filter(t => {
            const status = App.data.statuses.find(s => s.id === t.statusId);
            return status?.isOpen;
        });
        
        const modal = document.getElementById('newTicketModal');
        const modalHeader = modal.querySelector('.modal-header h3');
        const form = document.getElementById('newTicketForm');
        
        modalHeader.textContent = `${customer.firstName} ${customer.lastName}`;
        
        form.innerHTML = `
            <div class="customer-details-view">
                <div class="customer-profile">
                    <div class="profile-header">
                        <div class="profile-avatar">
                            ${customer.avatar ? 
                                `<img src="${customer.avatar}" alt="${customer.firstName}">` : 
                                customer.firstName.charAt(0).toUpperCase()
                            }
                        </div>
                        <div class="profile-info">
                            <h3>${customer.firstName} ${customer.lastName}</h3>
                            <p class="profile-organization">${customer.organization || 'Pessoa Física'}</p>
                            <div class="profile-meta">
                                <span><i class="fas fa-calendar"></i> Cliente desde ${App.formatDate(customer.createdAt)}</span>
                            </div>
                        </div>
                        <div class="profile-actions">
                            <button class="btn-secondary" onclick="CustomersModule.editCustomer(${customer.id})">
                                <i class="fas fa-edit"></i> Editar
                            </button>
                            <button class="btn-primary" onclick="CustomersModule.createTicketForCustomer(${customer.id})">
                                <i class="fas fa-plus"></i> Novo Chamado
                            </button>
                        </div>
                    </div>
                </div>
                
                <div class="customer-info-grid">
                    <div class="info-card">
                        <h4><i class="fas fa-envelope"></i> Contato</h4>
                        <div class="info-item">
                            <label>Email:</label>
                            <span>${customer.email}</span>
                        </div>
                        <div class="info-item">
                            <label>Telefone:</label>
                            <span>${customer.phone || 'Não informado'}</span>
                        </div>
                    </div>
                    
                    <div class="info-card">
                        <h4><i class="fas fa-chart-bar"></i> Estatísticas</h4>
                        <div class="stats-grid-small">
                            <div class="stat-item">
                                <span class="stat-number">${customerTickets.length}</span>
                                <span class="stat-label">Total de Chamados</span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-number">${openTickets.length}</span>
                                <span class="stat-label">Chamados Abertos</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="customer-tickets">
                    <div class="section-header">
                        <h4><i class="fas fa-ticket-alt"></i> Chamados Recentes</h4>
                        <button class="btn-outline" onclick="App.showSection('tickets'); App.closeModal();">
                            Ver Todos
                        </button>
                    </div>
                    
                    <div class="tickets-list">
                        ${customerTickets.length > 0 ? 
                            customerTickets.slice(0, 5).map(ticket => {
                                const status = App.data.statuses.find(s => s.id === ticket.statusId);
                                const priority = App.data.priorities.find(p => p.id === ticket.priorityId);
                                
                                return `
                                    <div class="ticket-item small" onclick="TicketsModule.showTicketDetails(${ticket.id})">
                                        <div class="ticket-info">
                                            <div class="ticket-number">${ticket.ticketNumber}</div>
                                            <div class="ticket-subject">${ticket.subject}</div>
                                            <div class="ticket-date">${App.formatDate(ticket.createdAt)}</div>
                                        </div>
                                        <div class="ticket-badges">
                                            <span class="status-badge ${status?.name.toLowerCase().replace(/\s+/g, '-')}" 
                                                  style="background-color: ${status?.color}15; color: ${status?.color};">
                                                ${status?.name}
                                            </span>
                                            <span class="priority-badge ${priority?.name.toLowerCase()}" 
                                                  style="background-color: ${priority?.color}15; color: ${priority?.color};">
                                                ${priority?.name}
                                            </span>
                                        </div>
                                    </div>
                                `;
                            }).join('') :
                            '<div class="empty-state small">Nenhum chamado encontrado</div>'
                        }
                    </div>
                </div>
                
                <div class="modal-actions">
                    <button type="button" class="btn-secondary" onclick="App.closeModal()">Fechar</button>
                </div>
            </div>
        `;
        
        App.showModal('newTicketModal');
    },
    
    // Edit customer
    editCustomer(customerId) {
        const customer = this.customers.find(c => c.id === customerId);
        if (!customer) return;
        
        const modal = document.getElementById('newTicketModal');
        const modalHeader = modal.querySelector('.modal-header h3');
        const form = document.getElementById('newTicketForm');
        
        modalHeader.textContent = 'Editar Cliente';
        
        form.innerHTML = `
            <div class="customer-form">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Nome *</label>
                        <input type="text" class="form-input" name="firstName" required value="${customer.firstName}">
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">Sobrenome *</label>
                        <input type="text" class="form-input" name="lastName" required value="${customer.lastName}">
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Email *</label>
                    <input type="email" class="form-input" name="email" required value="${customer.email}">
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Telefone</label>
                        <input type="tel" class="form-input" name="phone" value="${customer.phone || ''}">
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">Organização</label>
                        <input type="text" class="form-input" name="organization" value="${customer.organization || ''}">
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Foto do Perfil</label>
                    <div class="avatar-upload-area">
                        <div class="avatar-preview" id="avatarPreview">
                            ${customer.avatar ? 
                                `<img src="${customer.avatar}" alt="Avatar">` : 
                                `<i class="fas fa-user-circle"></i>`
                            }
                        </div>
                        <div class="avatar-upload-content">
                            <button type="button" class="btn-secondary" onclick="document.getElementById('avatarInput').click()">
                                <i class="fas fa-camera"></i> Alterar Foto
                            </button>
                            <p class="upload-hint">JPG, PNG até 2MB</p>
                        </div>
                        <input type="file" id="avatarInput" name="avatar" accept="image/*" style="display: none">
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="button" class="btn-secondary" onclick="App.closeModal()">Cancelar</button>
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-save"></i> Salvar Alterações
                    </button>
                </div>
            </div>
        `;
        
        // Add avatar preview
        document.getElementById('avatarInput').addEventListener('change', this.handleAvatarUpload);
        
        // Add form submit handler
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.updateCustomer(customerId, new FormData(form));
        });
        
        App.showModal('newTicketModal');
    },
    
    // Update customer
    async updateCustomer(customerId, formData) {
        try {
            const customerData = {
                firstName: formData.get('firstName'),
                lastName: formData.get('lastName'),
                email: formData.get('email'),
                phone: formData.get('phone'),
                organization: formData.get('organization')
            };
            
            // Try API first
            try {
                await API.updateCustomer(customerId, customerData);
                App.showNotification('Cliente atualizado com sucesso!', 'success');
            } catch (apiError) {
                // Fallback to local update
                this.updateCustomerLocally(customerId, customerData);
                App.showNotification('Cliente atualizado localmente!', 'info');
            }
            
            App.closeModal();
            this.loadCustomers();
            
        } catch (error) {
            App.showNotification('Erro ao atualizar cliente: ' + error.message, 'error');
        }
    },
    
    // Update customer locally
    updateCustomerLocally(customerId, customerData) {
        const customerIndex = this.customers.findIndex(c => c.id === customerId);
        if (customerIndex !== -1) {
            this.customers[customerIndex] = {
                ...this.customers[customerIndex],
                ...customerData,
                updatedAt: new Date().toISOString()
            };
            
            App.data.customers = this.customers;
            App.saveData();
        }
    },
    
    // Delete customer
    async deleteCustomer(customerId) {
        const customer = this.customers.find(c => c.id === customerId);
        if (!customer) return;
        
        // Check if customer has tickets
        const customerTickets = App.data.tickets.filter(t => t.customerId === customerId);
        if (customerTickets.length > 0) {
            if (!confirm(`O cliente ${customer.firstName} ${customer.lastName} possui ${customerTickets.length} chamado(s). Tem certeza que deseja excluí-lo?`)) {
                return;
            }
        } else {
            if (!confirm(`Tem certeza que deseja excluir o cliente ${customer.firstName} ${customer.lastName}?`)) {
                return;
            }
        }
        
        try {
            // Try API first
            try {
                await API.deleteCustomer(customerId);
                App.showNotification('Cliente excluído com sucesso!', 'success');
            } catch (apiError) {
                // Fallback to local deletion
                this.customers = this.customers.filter(c => c.id !== customerId);
                App.data.customers = this.customers;
                App.saveData();
                App.showNotification('Cliente excluído localmente!', 'info');
            }
            
            this.loadCustomers();
            
        } catch (error) {
            App.showNotification('Erro ao excluir cliente: ' + error.message, 'error');
        }
    },
    
    // Create ticket for specific customer
    createTicketForCustomer(customerId) {
        App.closeModal();
        setTimeout(() => {
            App.showSection('tickets');
            TicketsModule.showNewTicketModal();
            
            // Pre-select the customer
            setTimeout(() => {
                const customerSelect = document.querySelector('select[name="customerId"]');
                if (customerSelect) {
                    customerSelect.value = customerId;
                }
            }, 100);
        }, 200);
    },
    
    // Search customers
    searchCustomers(query) {
        if (!query.trim()) {
            this.renderCustomersGrid();
            return;
        }
        
        const filtered = this.customers.filter(customer => {
            const fullName = `${customer.firstName} ${customer.lastName}`.toLowerCase();
            const email = customer.email.toLowerCase();
            const organization = (customer.organization || '').toLowerCase();
            const searchQuery = query.toLowerCase();
            
            return fullName.includes(searchQuery) ||
                   email.includes(searchQuery) ||
                   organization.includes(searchQuery);
        });
        
        // Render filtered results
        const container = document.getElementById('customersGrid');
        
        if (filtered.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-search"></i>
                    <h3>Nenhum cliente encontrado</h3>
                    <p>Tente buscar com outros termos ou <a href="#" onclick="CustomersModule.showNewCustomerModal()">adicione um novo cliente</a>.</p>
                </div>
            `;
            return;
        }
        
        // Use the same rendering logic but with filtered data
        const originalCustomers = this.customers;
        this.customers = filtered;
        this.renderCustomersGrid();
        this.customers = originalCustomers;
    }
};

// Make globally available
window.CustomersModule = CustomersModule;