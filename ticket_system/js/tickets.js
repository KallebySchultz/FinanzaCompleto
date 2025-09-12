// Tickets Module
// Handles all ticket-related functionality

const TicketsModule = {
    currentTicket: null,
    filters: {
        status: '',
        priority: '',
        category: '',
        search: ''
    },
    
    // Initialize tickets module
    init() {
        this.setupEventListeners();
        this.loadTickets();
    },
    
    // Setup event listeners
    setupEventListeners() {
        // Filter changes
        document.getElementById('statusFilter').addEventListener('change', (e) => {
            this.filters.status = e.target.value;
            this.applyFilters();
        });
        
        document.getElementById('priorityFilter').addEventListener('change', (e) => {
            this.filters.priority = e.target.value;
            this.applyFilters();
        });
        
        document.getElementById('categoryFilter').addEventListener('change', (e) => {
            this.filters.category = e.target.value;
            this.applyFilters();
        });
        
        document.getElementById('ticketSearch').addEventListener('input', (e) => {
            this.filters.search = e.target.value;
            this.applyFilters();
        });
        
        // New ticket button
        document.getElementById('newTicketBtn').addEventListener('click', () => {
            this.showNewTicketModal();
        });
    },
    
    // Load tickets from API or local storage
    async loadTickets() {
        try {
            // Try to load from API first
            const response = await API.getTickets();
            App.data.tickets = response.data || [];
        } catch (error) {
            console.warn('API not available, using local data');
            // Fallback to local data
        }
        
        this.renderTicketsTable();
        this.updateStats();
    },
    
    // Apply filters to tickets
    applyFilters() {
        this.renderTicketsTable();
    },
    
    // Get filtered tickets
    getFilteredTickets() {
        let filtered = [...App.data.tickets];
        
        // Status filter
        if (this.filters.status) {
            filtered = filtered.filter(ticket => {
                const status = App.data.statuses.find(s => s.id === ticket.statusId);
                return status?.name.toLowerCase() === this.filters.status;
            });
        }
        
        // Priority filter
        if (this.filters.priority) {
            filtered = filtered.filter(ticket => {
                const priority = App.data.priorities.find(p => p.id === ticket.priorityId);
                return priority?.name.toLowerCase() === this.filters.priority;
            });
        }
        
        // Category filter
        if (this.filters.category) {
            filtered = filtered.filter(ticket => 
                ticket.categoryId === parseInt(this.filters.category)
            );
        }
        
        // Search filter
        if (this.filters.search) {
            const search = this.filters.search.toLowerCase();
            filtered = filtered.filter(ticket => {
                const customer = App.data.customers.find(c => c.id === ticket.customerId);
                const customerName = `${customer?.firstName} ${customer?.lastName}`.toLowerCase();
                
                return ticket.ticketNumber.toLowerCase().includes(search) ||
                       ticket.subject.toLowerCase().includes(search) ||
                       customerName.includes(search);
            });
        }
        
        return filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    },
    
    // Render tickets table
    renderTicketsTable() {
        const tbody = document.getElementById('ticketsTableBody');
        const filteredTickets = this.getFilteredTickets();
        
        if (filteredTickets.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center">Nenhum chamado encontrado</td></tr>';
            return;
        }
        
        tbody.innerHTML = filteredTickets.map(ticket => {
            const customer = App.data.customers.find(c => c.id === ticket.customerId);
            const status = App.data.statuses.find(s => s.id === ticket.statusId);
            const priority = App.data.priorities.find(p => p.id === ticket.priorityId);
            const agent = ticket.assignedUserId ? 'Admin User' : 'Não atribuído';
            
            return `
                <tr data-ticket-id="${ticket.id}">
                    <td>
                        <a href="#" onclick="TicketsModule.showTicketDetails(${ticket.id})" class="ticket-link">
                            ${ticket.ticketNumber}
                        </a>
                    </td>
                    <td>
                        <div class="customer-info">
                            <div class="customer-name">${customer?.firstName} ${customer?.lastName}</div>
                            <div class="customer-email">${customer?.email}</div>
                        </div>
                    </td>
                    <td>
                        <div class="ticket-subject" title="${ticket.description}">
                            ${ticket.subject}
                        </div>
                    </td>
                    <td>
                        <span class="status-badge ${status?.name.toLowerCase().replace(/\s+/g, '-')}" 
                              style="background-color: ${status?.color}15; color: ${status?.color};">
                            ${status?.name}
                        </span>
                    </td>
                    <td>
                        <span class="priority-badge ${priority?.name.toLowerCase()}" 
                              style="background-color: ${priority?.color}15; color: ${priority?.color};">
                            ${priority?.name}
                        </span>
                    </td>
                    <td>${agent}</td>
                    <td>${App.formatDate(ticket.createdAt)}</td>
                    <td>
                        <div class="ticket-actions">
                            <button class="action-btn view" onclick="TicketsModule.showTicketDetails(${ticket.id})" title="Ver detalhes">
                                <i class="fas fa-eye"></i>
                            </button>
                            <button class="action-btn edit" onclick="TicketsModule.editTicket(${ticket.id})" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete" onclick="TicketsModule.deleteTicket(${ticket.id})" title="Excluir">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    },
    
    // Update statistics
    updateStats() {
        const openTickets = App.data.tickets.filter(t => 
            App.data.statuses.find(s => s.id === t.statusId)?.isOpen
        ).length;
        
        const closedTickets = App.data.tickets.filter(t => 
            App.data.statuses.find(s => s.id === t.statusId)?.isClosed
        ).length;
        
        const highPriorityTickets = App.data.tickets.filter(t => 
            App.data.priorities.find(p => p.id === t.priorityId)?.level >= 3
        ).length;
        
        // Update dashboard if visible
        if (App.currentSection === 'dashboard') {
            document.getElementById('openTickets').textContent = openTickets;
            document.getElementById('closedTickets').textContent = closedTickets;
            document.getElementById('highPriorityTickets').textContent = highPriorityTickets;
        }
    },
    
    // Show ticket details modal
    showTicketDetails(ticketId) {
        const ticket = App.data.tickets.find(t => t.id === ticketId);
        if (!ticket) return;
        
        const customer = App.data.customers.find(c => c.id === ticket.customerId);
        const status = App.data.statuses.find(s => s.id === ticket.statusId);
        const priority = App.data.priorities.find(p => p.id === ticket.priorityId);
        const category = App.data.categories.find(c => c.id === ticket.categoryId);
        const department = App.data.departments.find(d => d.id === ticket.departmentId);
        
        const modal = document.getElementById('newTicketModal');
        const modalHeader = modal.querySelector('.modal-header h3');
        const form = document.getElementById('newTicketForm');
        
        modalHeader.textContent = `Chamado ${ticket.ticketNumber}`;
        
        form.innerHTML = `
            <div class="ticket-details">
                <div class="ticket-header">
                    <div class="ticket-info">
                        <h3>${ticket.subject}</h3>
                        <div class="ticket-meta">
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
                    <div class="ticket-actions">
                        <button class="btn-secondary" onclick="TicketsModule.editTicket(${ticket.id})">
                            <i class="fas fa-edit"></i> Editar
                        </button>
                        <button class="btn-secondary" onclick="TicketsModule.assignTicket(${ticket.id})">
                            <i class="fas fa-user"></i> Atribuir
                        </button>
                    </div>
                </div>
                
                <div class="ticket-details-grid">
                    <div class="detail-item">
                        <label>Cliente:</label>
                        <span>${customer?.firstName} ${customer?.lastName}</span>
                    </div>
                    <div class="detail-item">
                        <label>Email:</label>
                        <span>${customer?.email}</span>
                    </div>
                    <div class="detail-item">
                        <label>Departamento:</label>
                        <span>${department?.name}</span>
                    </div>
                    <div class="detail-item">
                        <label>Categoria:</label>
                        <span>${category?.name || 'Não definida'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Criado em:</label>
                        <span>${App.formatDate(ticket.createdAt)}</span>
                    </div>
                    <div class="detail-item">
                        <label>Atendente:</label>
                        <span>${ticket.assignedUserId ? 'Admin User' : 'Não atribuído'}</span>
                    </div>
                </div>
                
                <div class="ticket-description">
                    <h4>Descrição</h4>
                    <p>${ticket.description}</p>
                </div>
                
                <div class="ticket-messages">
                    <h4>Mensagens</h4>
                    <div class="messages-list">
                        ${this.renderMessages(ticket.messages || [])}
                    </div>
                    
                    <div class="add-message">
                        <textarea placeholder="Adicionar uma resposta..." rows="3"></textarea>
                        <div class="message-actions">
                            <label>
                                <input type="checkbox"> Nota interna
                            </label>
                            <button class="btn-primary">
                                <i class="fas fa-paper-plane"></i> Enviar
                            </button>
                        </div>
                    </div>
                </div>
                
                ${ticket.attachments && ticket.attachments.length > 0 ? `
                <div class="ticket-attachments">
                    <h4>Anexos</h4>
                    <div class="attachments-list">
                        ${ticket.attachments.map(att => `
                            <div class="attachment-item">
                                <i class="fas fa-file"></i>
                                <span>${att.filename}</span>
                                <a href="${att.url}" download>
                                    <i class="fas fa-download"></i>
                                </a>
                            </div>
                        `).join('')}
                    </div>
                </div>
                ` : ''}
                
                <div class="modal-actions">
                    <button type="button" class="btn-secondary" onclick="App.closeModal()">Fechar</button>
                </div>
            </div>
        `;
        
        App.showModal('newTicketModal');
    },
    
    // Render messages
    renderMessages(messages) {
        if (!messages || messages.length === 0) {
            return '<div class="empty-messages">Nenhuma mensagem ainda</div>';
        }
        
        return messages.map(message => {
            const isFromCustomer = message.customerId && !message.userId;
            const author = isFromCustomer ? 
                `${App.data.customers.find(c => c.id === message.customerId)?.firstName || 'Cliente'}` :
                'Admin User';
            
            return `
                <div class="message-item ${isFromCustomer ? 'customer-message' : 'agent-message'} ${message.isInternal ? 'internal-message' : ''}">
                    <div class="message-header">
                        <div class="message-author">
                            <div class="author-avatar">
                                ${author.charAt(0)}
                            </div>
                            <div class="author-info">
                                <span class="author-name">${author}</span>
                                <span class="message-time">${App.formatDate(message.createdAt)}</span>
                            </div>
                        </div>
                        ${message.isInternal ? '<span class="internal-badge">Interno</span>' : ''}
                    </div>
                    <div class="message-content">
                        ${message.message}
                    </div>
                </div>
            `;
        }).join('');
    },
    
    // Show new ticket modal
    showNewTicketModal() {
        const modal = document.getElementById('newTicketModal');
        const modalHeader = modal.querySelector('.modal-header h3');
        const form = document.getElementById('newTicketForm');
        
        modalHeader.textContent = 'Novo Chamado';
        
        form.innerHTML = `
            <div class="form-group">
                <label class="form-label">Cliente *</label>
                <select class="form-select" name="customerId" required>
                    <option value="">Selecione um cliente</option>
                    ${App.data.customers.map(customer => 
                        `<option value="${customer.id}">${customer.firstName} ${customer.lastName} (${customer.email})</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Departamento *</label>
                <select class="form-select" name="departmentId" required>
                    <option value="">Selecione um departamento</option>
                    ${App.data.departments.map(dept => 
                        `<option value="${dept.id}">${dept.name}</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Categoria</label>
                <select class="form-select" name="categoryId">
                    <option value="">Selecione uma categoria</option>
                    ${App.data.categories.map(cat => 
                        `<option value="${cat.id}">${cat.name}</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Prioridade *</label>
                <select class="form-select" name="priorityId" required>
                    ${App.data.priorities.map(priority => 
                        `<option value="${priority.id}" ${priority.level === 2 ? 'selected' : ''}>${priority.name}</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Assunto *</label>
                <input type="text" class="form-input" name="subject" required placeholder="Digite o assunto do chamado">
            </div>
            
            <div class="form-group">
                <label class="form-label">Descrição *</label>
                <textarea class="form-textarea" name="description" required placeholder="Descreva o problema ou solicitação" rows="5"></textarea>
            </div>
            
            <div class="form-group">
                <label class="form-label">Anexos</label>
                <div class="file-upload-area" onclick="document.getElementById('fileInput').click()">
                    <i class="fas fa-cloud-upload-alt file-upload-icon"></i>
                    <p class="file-upload-text">Clique para selecionar arquivos</p>
                    <p class="file-upload-hint">Ou arraste e solte aqui (máx. 10MB)</p>
                </div>
                <input type="file" id="fileInput" multiple accept=".pdf,.doc,.docx,.jpg,.jpeg,.png,.gif" style="display: none">
                <div class="uploaded-files" id="uploadedFiles"></div>
            </div>
            
            <div class="form-actions">
                <button type="button" class="btn-secondary" onclick="App.closeModal()">Cancelar</button>
                <button type="submit" class="btn-primary">
                    <i class="fas fa-plus"></i> Criar Chamado
                </button>
            </div>
        `;
        
        // Add file upload handling
        document.getElementById('fileInput').addEventListener('change', this.handleFileUpload);
        
        // Add form submit handler
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.createTicket(new FormData(form));
        });
        
        App.showModal('newTicketModal');
    },
    
    // Handle file upload
    handleFileUpload(event) {
        const files = Array.from(event.target.files);
        const container = document.getElementById('uploadedFiles');
        
        container.innerHTML = files.map(file => `
            <div class="uploaded-file">
                <div class="file-info">
                    <i class="fas fa-file file-icon"></i>
                    <div>
                        <div class="file-name">${file.name}</div>
                        <div class="file-size">${(file.size / 1024 / 1024).toFixed(2)} MB</div>
                    </div>
                </div>
                <button type="button" class="file-remove" onclick="this.parentElement.remove()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `).join('');
    },
    
    // Create new ticket
    async createTicket(formData) {
        try {
            const ticketData = {
                subject: formData.get('subject'),
                description: formData.get('description'),
                customerId: parseInt(formData.get('customerId')),
                departmentId: parseInt(formData.get('departmentId')),
                categoryId: formData.get('categoryId') ? parseInt(formData.get('categoryId')) : null,
                priorityId: parseInt(formData.get('priorityId'))
            };
            
            // Try API first
            try {
                const response = await API.createTicket(ticketData);
                App.showNotification('Chamado criado com sucesso!', 'success');
            } catch (apiError) {
                // Fallback to local creation
                this.createTicketLocally(ticketData);
                App.showNotification('Chamado criado localmente!', 'info');
            }
            
            App.closeModal();
            this.loadTickets();
            
        } catch (error) {
            App.showNotification('Erro ao criar chamado: ' + error.message, 'error');
        }
    },
    
    // Create ticket locally
    createTicketLocally(ticketData) {
        const newTicket = {
            id: App.data.tickets.length + 1,
            ticketNumber: `T${String(App.data.tickets.length + 1).padStart(6, '0')}`,
            ...ticketData,
            statusId: 1, // Open
            assignedUserId: null,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            messages: [{
                id: Date.now(),
                userId: null,
                customerId: ticketData.customerId,
                message: ticketData.description,
                isInternal: false,
                createdAt: new Date().toISOString()
            }],
            attachments: [],
            timeLogs: []
        };
        
        App.data.tickets.push(newTicket);
        App.saveData();
    },
    
    // Edit ticket
    editTicket(ticketId) {
        const ticket = App.data.tickets.find(t => t.id === ticketId);
        if (!ticket) return;
        
        // Implementation for edit modal
        console.log('Edit ticket:', ticket);
    },
    
    // Delete ticket
    async deleteTicket(ticketId) {
        if (!confirm('Tem certeza que deseja excluir este chamado?')) return;
        
        try {
            // Try API first
            try {
                await API.deleteTicket(ticketId);
            } catch (apiError) {
                // Fallback to local deletion
                App.data.tickets = App.data.tickets.filter(t => t.id !== ticketId);
                App.saveData();
            }
            
            App.showNotification('Chamado excluído com sucesso!', 'success');
            this.loadTickets();
            
        } catch (error) {
            App.showNotification('Erro ao excluir chamado: ' + error.message, 'error');
        }
    },
    
    // Assign ticket
    assignTicket(ticketId) {
        // Implementation for assignment modal
        console.log('Assign ticket:', ticketId);
    }
};

// Make globally available
window.TicketsModule = TicketsModule;