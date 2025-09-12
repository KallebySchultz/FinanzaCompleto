// Main Application JavaScript
// Ticket System - Finanza

// Application state
const App = {
    currentUser: null,
    currentSection: 'dashboard',
    data: {
        tickets: [],
        customers: [],
        departments: [],
        categories: [],
        priorities: [],
        statuses: [],
        users: [],
        knowledgeBase: []
    },
    
    // Initialize the application
    init() {
        this.loadStoredData();
        this.setupEventListeners();
        this.loadDefaultData();
        this.updateUI();
        this.startRealTimeUpdates();
    },
    
    // Load data from localStorage
    loadStoredData() {
        const storedData = localStorage.getItem('ticketSystemData');
        if (storedData) {
            this.data = { ...this.data, ...JSON.parse(storedData) };
        }
        
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
            this.currentUser = JSON.parse(storedUser);
        } else {
            // Default admin user
            this.currentUser = {
                id: 1,
                username: 'admin',
                email: 'admin@example.com',
                firstName: 'Admin',
                lastName: 'User',
                role: 'admin',
                avatar: 'assets/images/default-avatar.png'
            };
        }
    },
    
    // Save data to localStorage
    saveData() {
        localStorage.setItem('ticketSystemData', JSON.stringify(this.data));
        localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
    },
    
    // Load default data if not exists
    loadDefaultData() {
        if (this.data.departments.length === 0) {
            this.data.departments = [
                { id: 1, name: 'Technical Support', color: '#007bff', isActive: true },
                { id: 2, name: 'Sales', color: '#28a745', isActive: true },
                { id: 3, name: 'Billing', color: '#ffc107', isActive: true },
                { id: 4, name: 'General', color: '#6c757d', isActive: true }
            ];
        }
        
        if (this.data.categories.length === 0) {
            this.data.categories = [
                { id: 1, name: 'Software Issue', departmentId: 1, color: '#dc3545' },
                { id: 2, name: 'Hardware Issue', departmentId: 1, color: '#fd7e14' },
                { id: 3, name: 'Product Inquiry', departmentId: 2, color: '#17a2b8' },
                { id: 4, name: 'Payment Issue', departmentId: 3, color: '#e83e8c' },
                { id: 5, name: 'General Question', departmentId: 4, color: '#007bff' }
            ];
        }
        
        if (this.data.priorities.length === 0) {
            this.data.priorities = [
                { id: 1, name: 'Low', level: 1, color: '#28a745', slaHours: 72 },
                { id: 2, name: 'Medium', level: 2, color: '#ffc107', slaHours: 24 },
                { id: 3, name: 'High', level: 3, color: '#fd7e14', slaHours: 8 },
                { id: 4, name: 'Urgent', level: 4, color: '#dc3545', slaHours: 2 }
            ];
        }
        
        if (this.data.statuses.length === 0) {
            this.data.statuses = [
                { id: 1, name: 'Open', color: '#007bff', isOpen: true, isClosed: false },
                { id: 2, name: 'In Progress', color: '#ffc107', isOpen: true, isClosed: false },
                { id: 3, name: 'Waiting for Customer', color: '#6c757d', isOpen: true, isClosed: false },
                { id: 4, name: 'Resolved', color: '#28a745', isOpen: false, isClosed: true },
                { id: 5, name: 'Closed', color: '#dc3545', isOpen: false, isClosed: true }
            ];
        }
        
        if (this.data.customers.length === 0) {
            this.data.customers = [
                {
                    id: 1,
                    firstName: 'John',
                    lastName: 'Doe',
                    email: 'john.doe@acme.com',
                    phone: '+1 (555) 111-2222',
                    organization: 'Acme Corporation',
                    avatar: null,
                    createdAt: new Date().toISOString()
                },
                {
                    id: 2,
                    firstName: 'Jane',
                    lastName: 'Smith',
                    email: 'jane.smith@techsolutions.com',
                    phone: '+1 (555) 333-4444',
                    organization: 'Tech Solutions Inc',
                    avatar: null,
                    createdAt: new Date().toISOString()
                }
            ];
        }
        
        if (this.data.tickets.length === 0) {
            this.generateSampleTickets();
        }
        
        this.saveData();
    },
    
    // Generate sample tickets
    generateSampleTickets() {
        const sampleTickets = [
            {
                id: 1,
                ticketNumber: 'T000001',
                subject: 'Cannot access email',
                description: 'I am unable to access my email account. Getting login errors.',
                customerId: 1,
                departmentId: 1,
                categoryId: 1,
                priorityId: 2,
                statusId: 1,
                assignedUserId: 1,
                createdAt: new Date(Date.now() - 86400000).toISOString(), // 1 day ago
                updatedAt: new Date().toISOString(),
                messages: [
                    {
                        id: 1,
                        userId: null,
                        customerId: 1,
                        message: 'I am unable to access my email account. Getting login errors.',
                        isInternal: false,
                        createdAt: new Date(Date.now() - 86400000).toISOString()
                    }
                ],
                attachments: [],
                timeLogs: []
            },
            {
                id: 2,
                ticketNumber: 'T000002',
                subject: 'Software installation help needed',
                description: 'Need help installing the new software update.',
                customerId: 2,
                departmentId: 1,
                categoryId: 2,
                priorityId: 3,
                statusId: 2,
                assignedUserId: 1,
                createdAt: new Date(Date.now() - 43200000).toISOString(), // 12 hours ago
                updatedAt: new Date().toISOString(),
                messages: [
                    {
                        id: 2,
                        userId: null,
                        customerId: 2,
                        message: 'Need help installing the new software update.',
                        isInternal: false,
                        createdAt: new Date(Date.now() - 43200000).toISOString()
                    },
                    {
                        id: 3,
                        userId: 1,
                        customerId: null,
                        message: 'I will help you with the installation. Please download the installer from our website.',
                        isInternal: false,
                        createdAt: new Date(Date.now() - 21600000).toISOString()
                    }
                ],
                attachments: [],
                timeLogs: [
                    {
                        id: 1,
                        userId: 1,
                        description: 'Initial investigation',
                        timeSpentMinutes: 30,
                        loggedAt: new Date(Date.now() - 21600000).toISOString()
                    }
                ]
            }
        ];
        
        this.data.tickets = sampleTickets;
    },
    
    // Setup event listeners
    setupEventListeners() {
        // Sidebar navigation
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const section = link.dataset.section;
                this.showSection(section);
            });
        });
        
        // Sidebar toggle
        document.getElementById('toggleSidebar').addEventListener('click', () => {
            document.getElementById('sidebar').classList.toggle('collapsed');
        });
        
        // Modal close events
        document.querySelectorAll('.modal-close').forEach(btn => {
            btn.addEventListener('click', () => {
                this.closeModal();
            });
        });
        
        // Modal overlay click
        document.getElementById('modalOverlay').addEventListener('click', (e) => {
            if (e.target === e.currentTarget) {
                this.closeModal();
            }
        });
        
        // Global search
        document.getElementById('globalSearch').addEventListener('input', (e) => {
            this.performGlobalSearch(e.target.value);
        });
        
        // New ticket button
        document.getElementById('newTicketBtn').addEventListener('click', () => {
            this.showNewTicketModal();
        });
        
        // New customer button
        document.getElementById('newCustomerBtn').addEventListener('click', () => {
            this.showNewCustomerModal();
        });
        
        // Filter events
        document.getElementById('statusFilter').addEventListener('change', () => {
            this.filterTickets();
        });
        
        document.getElementById('priorityFilter').addEventListener('change', () => {
            this.filterTickets();
        });
        
        document.getElementById('categoryFilter').addEventListener('change', () => {
            this.filterTickets();
        });
        
        document.getElementById('ticketSearch').addEventListener('input', () => {
            this.filterTickets();
        });
        
        // Responsive sidebar for mobile
        if (window.innerWidth <= 1024) {
            document.getElementById('toggleSidebar').addEventListener('click', () => {
                document.getElementById('sidebar').classList.toggle('open');
            });
        }
    },
    
    // Show specific section
    showSection(sectionName) {
        // Update navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-section="${sectionName}"]`).parentElement.classList.add('active');
        
        // Update content
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });
        document.getElementById(`${sectionName}-section`).classList.add('active');
        
        // Update page title and breadcrumb
        const titles = {
            dashboard: 'Dashboard',
            tickets: 'Gerenciar Chamados',
            customers: 'Gerenciar Clientes',
            knowledge: 'Base de Conhecimento',
            reports: 'Relatórios',
            settings: 'Configurações'
        };
        
        document.getElementById('pageTitle').textContent = titles[sectionName];
        document.getElementById('breadcrumb').innerHTML = `<span>Início</span> > <span>${titles[sectionName]}</span>`;
        
        this.currentSection = sectionName;
        
        // Load section-specific data
        switch (sectionName) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'tickets':
                this.loadTickets();
                break;
            case 'customers':
                this.loadCustomers();
                break;
            case 'knowledge':
                this.loadKnowledgeBase();
                break;
            case 'reports':
                this.loadReports();
                break;
            case 'settings':
                this.loadSettings();
                break;
        }
    },
    
    // Load dashboard data
    loadDashboard() {
        this.updateDashboardStats();
        this.loadRecentTickets();
        this.loadProductivityChart();
    },
    
    // Update dashboard statistics
    updateDashboardStats() {
        const openTickets = this.data.tickets.filter(t => 
            this.data.statuses.find(s => s.id === t.statusId)?.isOpen
        ).length;
        
        const closedTickets = this.data.tickets.filter(t => 
            this.data.statuses.find(s => s.id === t.statusId)?.isClosed
        ).length;
        
        const highPriorityTickets = this.data.tickets.filter(t => 
            this.data.priorities.find(p => p.id === t.priorityId)?.level >= 3
        ).length;
        
        const totalCustomers = this.data.customers.length;
        
        document.getElementById('openTickets').textContent = openTickets;
        document.getElementById('closedTickets').textContent = closedTickets;
        document.getElementById('highPriorityTickets').textContent = highPriorityTickets;
        document.getElementById('totalCustomers').textContent = totalCustomers;
    },
    
    // Load recent tickets
    loadRecentTickets() {
        const recentTickets = this.data.tickets
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            .slice(0, 5);
        
        const container = document.getElementById('recentTicketsList');
        container.innerHTML = '';
        
        if (recentTickets.length === 0) {
            container.innerHTML = '<div class="empty-state"><p>Nenhum chamado encontrado</p></div>';
            return;
        }
        
        recentTickets.forEach(ticket => {
            const customer = this.data.customers.find(c => c.id === ticket.customerId);
            const status = this.data.statuses.find(s => s.id === ticket.statusId);
            const priority = this.data.priorities.find(p => p.id === ticket.priorityId);
            
            const ticketElement = document.createElement('div');
            ticketElement.className = 'ticket-item';
            ticketElement.innerHTML = `
                <div class="ticket-info">
                    <div class="ticket-number">${ticket.ticketNumber}</div>
                    <h4 class="ticket-subject">${ticket.subject}</h4>
                    <div class="ticket-meta">
                        <span class="ticket-customer">${customer?.firstName} ${customer?.lastName}</span>
                        <span>•</span>
                        <span>${this.formatDate(ticket.createdAt)}</span>
                    </div>
                </div>
                <div class="ticket-badges">
                    <span class="status-badge ${status?.name.toLowerCase().replace(/\s+/g, '-')}">${status?.name}</span>
                    <span class="priority-badge ${priority?.name.toLowerCase()}">${priority?.name}</span>
                </div>
            `;
            
            ticketElement.addEventListener('click', () => {
                this.showTicketDetails(ticket.id);
            });
            
            container.appendChild(ticketElement);
        });
    },
    
    // Load productivity chart
    loadProductivityChart() {
        const ctx = document.getElementById('productivityChart');
        if (!ctx) return;
        
        // Sample data for demo
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Chamados Resolvidos',
                    data: [12, 19, 3, 5, 2, 3],
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    },
    
    // Load tickets
    loadTickets() {
        this.populateFilterDropdowns();
        this.renderTicketsTable();
    },
    
    // Populate filter dropdowns
    populateFilterDropdowns() {
        const categoryFilter = document.getElementById('categoryFilter');
        categoryFilter.innerHTML = '<option value="">Todas as Categorias</option>';
        
        this.data.categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category.id;
            option.textContent = category.name;
            categoryFilter.appendChild(option);
        });
    },
    
    // Render tickets table
    renderTicketsTable() {
        const tbody = document.getElementById('ticketsTableBody');
        tbody.innerHTML = '';
        
        const filteredTickets = this.getFilteredTickets();
        
        if (filteredTickets.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center">Nenhum chamado encontrado</td></tr>';
            return;
        }
        
        filteredTickets.forEach(ticket => {
            const customer = this.data.customers.find(c => c.id === ticket.customerId);
            const status = this.data.statuses.find(s => s.id === ticket.statusId);
            const priority = this.data.priorities.find(p => p.id === ticket.priorityId);
            const agent = ticket.assignedUserId ? 'Admin User' : 'Não atribuído';
            
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${ticket.ticketNumber}</td>
                <td>${customer?.firstName} ${customer?.lastName}</td>
                <td>${ticket.subject}</td>
                <td><span class="status-badge ${status?.name.toLowerCase().replace(/\s+/g, '-')}">${status?.name}</span></td>
                <td><span class="priority-badge ${priority?.name.toLowerCase()}">${priority?.name}</span></td>
                <td>${agent}</td>
                <td>${this.formatDate(ticket.createdAt)}</td>
                <td>
                    <div class="ticket-actions">
                        <button class="action-btn view" onclick="App.showTicketDetails(${ticket.id})">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="action-btn edit" onclick="App.editTicket(${ticket.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="action-btn delete" onclick="App.deleteTicket(${ticket.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            `;
            
            tbody.appendChild(row);
        });
    },
    
    // Get filtered tickets
    getFilteredTickets() {
        let filtered = [...this.data.tickets];
        
        const statusFilter = document.getElementById('statusFilter').value;
        const priorityFilter = document.getElementById('priorityFilter').value;
        const categoryFilter = document.getElementById('categoryFilter').value;
        const searchFilter = document.getElementById('ticketSearch').value.toLowerCase();
        
        if (statusFilter) {
            const statusName = document.getElementById('statusFilter').selectedOptions[0].text;
            filtered = filtered.filter(ticket => {
                const status = this.data.statuses.find(s => s.id === ticket.statusId);
                return status?.name === statusName;
            });
        }
        
        if (priorityFilter) {
            const priorityName = document.getElementById('priorityFilter').selectedOptions[0].text;
            filtered = filtered.filter(ticket => {
                const priority = this.data.priorities.find(p => p.id === ticket.priorityId);
                return priority?.name === priorityName;
            });
        }
        
        if (categoryFilter) {
            filtered = filtered.filter(ticket => ticket.categoryId === parseInt(categoryFilter));
        }
        
        if (searchFilter) {
            filtered = filtered.filter(ticket => {
                const customer = this.data.customers.find(c => c.id === ticket.customerId);
                const customerName = `${customer?.firstName} ${customer?.lastName}`.toLowerCase();
                return ticket.ticketNumber.toLowerCase().includes(searchFilter) ||
                       ticket.subject.toLowerCase().includes(searchFilter) ||
                       customerName.includes(searchFilter);
            });
        }
        
        return filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    },
    
    // Filter tickets
    filterTickets() {
        this.renderTicketsTable();
    },
    
    // Load customers
    loadCustomers() {
        this.renderCustomersGrid();
    },
    
    // Render customers grid
    renderCustomersGrid() {
        const container = document.getElementById('customersGrid');
        container.innerHTML = '';
        
        if (this.data.customers.length === 0) {
            container.innerHTML = '<div class="empty-state"><i class="fas fa-users"></i><h3>Nenhum cliente encontrado</h3><p>Adicione o primeiro cliente para começar.</p></div>';
            return;
        }
        
        this.data.customers.forEach(customer => {
            const customerTickets = this.data.tickets.filter(t => t.customerId === customer.id);
            const openTickets = customerTickets.filter(t => 
                this.data.statuses.find(s => s.id === t.statusId)?.isOpen
            ).length;
            
            const customerCard = document.createElement('div');
            customerCard.className = 'customer-card';
            customerCard.innerHTML = `
                <div class="customer-header">
                    <div class="customer-avatar">
                        ${customer.avatar ? `<img src="${customer.avatar}" alt="${customer.firstName}">` : customer.firstName.charAt(0)}
                    </div>
                    <div class="customer-info">
                        <h4>${customer.firstName} ${customer.lastName}</h4>
                        <p>${customer.organization || 'Pessoa Física'}</p>
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
                </div>
            `;
            
            customerCard.addEventListener('click', () => {
                this.showCustomerDetails(customer.id);
            });
            
            container.appendChild(customerCard);
        });
    },
    
    // Load knowledge base
    loadKnowledgeBase() {
        this.renderKnowledgeCategories();
    },
    
    // Render knowledge base categories
    renderKnowledgeCategories() {
        const container = document.getElementById('knowledgeCategories');
        container.innerHTML = '';
        
        const categories = [
            { id: 1, name: 'Getting Started', description: 'Basic information for new users', icon: 'fas fa-play-circle', articles: 5 },
            { id: 2, name: 'Troubleshooting', description: 'Common problems and solutions', icon: 'fas fa-tools', articles: 12 },
            { id: 3, name: 'How-To Guides', description: 'Step-by-step instructions', icon: 'fas fa-list-ol', articles: 8 },
            { id: 4, name: 'FAQ', description: 'Frequently asked questions', icon: 'fas fa-question-circle', articles: 15 }
        ];
        
        categories.forEach(category => {
            const categoryCard = document.createElement('div');
            categoryCard.className = 'knowledge-category';
            categoryCard.innerHTML = `
                <div class="category-icon">
                    <i class="${category.icon}"></i>
                </div>
                <h3 class="category-title">${category.name}</h3>
                <p class="category-description">${category.description}</p>
                <div class="category-stats">
                    <span>${category.articles} artigos</span>
                    <span>Ver todos</span>
                </div>
            `;
            
            container.appendChild(categoryCard);
        });
    },
    
    // Load reports
    loadReports() {
        this.loadStatusChart();
        this.loadResponseTimeChart();
    },
    
    // Load status chart
    loadStatusChart() {
        const ctx = document.getElementById('statusChart');
        if (!ctx) return;
        
        const statusCounts = this.data.statuses.map(status => {
            return this.data.tickets.filter(t => t.statusId === status.id).length;
        });
        
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: this.data.statuses.map(s => s.name),
                datasets: [{
                    data: statusCounts,
                    backgroundColor: this.data.statuses.map(s => s.color)
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    },
    
    // Load response time chart
    loadResponseTimeChart() {
        const ctx = document.getElementById('responseTimeChart');
        if (!ctx) return;
        
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Tempo Médio (horas)',
                    data: [24, 18, 12, 15, 20, 16],
                    backgroundColor: '#007bff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    },
    
    // Load settings
    loadSettings() {
        this.showSettingsTab('departments');
    },
    
    // Show settings tab
    showSettingsTab(tabName) {
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
        
        const content = document.getElementById('settingsContent');
        
        switch (tabName) {
            case 'departments':
                content.innerHTML = this.renderDepartmentsSettings();
                break;
            case 'categories':
                content.innerHTML = this.renderCategoriesSettings();
                break;
            case 'priorities':
                content.innerHTML = this.renderPrioritiesSettings();
                break;
            case 'users':
                content.innerHTML = this.renderUsersSettings();
                break;
        }
    },
    
    // Render departments settings
    renderDepartmentsSettings() {
        return `
            <div class="settings-section">
                <div class="section-header">
                    <h3>Departamentos</h3>
                    <button class="btn-primary" onclick="App.addDepartment()">
                        <i class="fas fa-plus"></i> Adicionar Departamento
                    </button>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Nome</th>
                                <th>Cor</th>
                                <th>Status</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${this.data.departments.map(dept => `
                                <tr>
                                    <td>${dept.name}</td>
                                    <td><span class="color-indicator" style="background: ${dept.color}"></span> ${dept.color}</td>
                                    <td><span class="status-badge ${dept.isActive ? 'open' : 'closed'}">${dept.isActive ? 'Ativo' : 'Inativo'}</span></td>
                                    <td>
                                        <button class="action-btn edit" onclick="App.editDepartment(${dept.id})">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="action-btn delete" onclick="App.deleteDepartment(${dept.id})">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    },
    
    // Render categories settings
    renderCategoriesSettings() {
        return `
            <div class="settings-section">
                <div class="section-header">
                    <h3>Categorias</h3>
                    <button class="btn-primary" onclick="App.addCategory()">
                        <i class="fas fa-plus"></i> Adicionar Categoria
                    </button>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Nome</th>
                                <th>Departamento</th>
                                <th>Cor</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${this.data.categories.map(cat => {
                                const dept = this.data.departments.find(d => d.id === cat.departmentId);
                                return `
                                    <tr>
                                        <td>${cat.name}</td>
                                        <td>${dept?.name || 'N/A'}</td>
                                        <td><span class="color-indicator" style="background: ${cat.color}"></span> ${cat.color}</td>
                                        <td>
                                            <button class="action-btn edit" onclick="App.editCategory(${cat.id})">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button class="action-btn delete" onclick="App.deleteCategory(${cat.id})">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                `;
                            }).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    },
    
    // Render priorities settings
    renderPrioritiesSettings() {
        return `
            <div class="settings-section">
                <div class="section-header">
                    <h3>Prioridades</h3>
                    <button class="btn-primary" onclick="App.addPriority()">
                        <i class="fas fa-plus"></i> Adicionar Prioridade
                    </button>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Nome</th>
                                <th>Nível</th>
                                <th>Cor</th>
                                <th>SLA (horas)</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${this.data.priorities.map(priority => `
                                <tr>
                                    <td>${priority.name}</td>
                                    <td>${priority.level}</td>
                                    <td><span class="color-indicator" style="background: ${priority.color}"></span> ${priority.color}</td>
                                    <td>${priority.slaHours}</td>
                                    <td>
                                        <button class="action-btn edit" onclick="App.editPriority(${priority.id})">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="action-btn delete" onclick="App.deletePriority(${priority.id})">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    },
    
    // Render users settings
    renderUsersSettings() {
        return `
            <div class="settings-section">
                <div class="section-header">
                    <h3>Usuários</h3>
                    <button class="btn-primary" onclick="App.addUser()">
                        <i class="fas fa-plus"></i> Adicionar Usuário
                    </button>
                </div>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Nome</th>
                                <th>Email</th>
                                <th>Perfil</th>
                                <th>Departamento</th>
                                <th>Status</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Admin User</td>
                                <td>admin@example.com</td>
                                <td><span class="priority-badge high">Administrador</span></td>
                                <td>Technical Support</td>
                                <td><span class="status-badge open">Ativo</span></td>
                                <td>
                                    <button class="action-btn edit" onclick="App.editUser(1)">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    },
    
    // Show new ticket modal
    showNewTicketModal() {
        const modal = document.getElementById('newTicketModal');
        const form = document.getElementById('newTicketForm');
        
        form.innerHTML = `
            <div class="form-group">
                <label class="form-label">Cliente</label>
                <select class="form-select" name="customerId" required>
                    <option value="">Selecione um cliente</option>
                    ${this.data.customers.map(customer => 
                        `<option value="${customer.id}">${customer.firstName} ${customer.lastName} (${customer.email})</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Departamento</label>
                <select class="form-select" name="departmentId" required>
                    <option value="">Selecione um departamento</option>
                    ${this.data.departments.map(dept => 
                        `<option value="${dept.id}">${dept.name}</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Categoria</label>
                <select class="form-select" name="categoryId">
                    <option value="">Selecione uma categoria</option>
                    ${this.data.categories.map(cat => 
                        `<option value="${cat.id}">${cat.name}</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Prioridade</label>
                <select class="form-select" name="priorityId" required>
                    ${this.data.priorities.map(priority => 
                        `<option value="${priority.id}">${priority.name}</option>`
                    ).join('')}
                </select>
            </div>
            
            <div class="form-group">
                <label class="form-label">Assunto</label>
                <input type="text" class="form-input" name="subject" required placeholder="Digite o assunto do chamado">
            </div>
            
            <div class="form-group">
                <label class="form-label">Descrição</label>
                <textarea class="form-textarea" name="description" required placeholder="Descreva o problema ou solicitação"></textarea>
            </div>
            
            <div class="form-group">
                <label class="form-label">Anexos</label>
                <div class="file-upload-area" onclick="document.getElementById('fileInput').click()">
                    <i class="fas fa-cloud-upload-alt file-upload-icon"></i>
                    <p class="file-upload-text">Clique para selecionar arquivos</p>
                    <p class="file-upload-hint">Ou arraste e solte aqui</p>
                </div>
                <input type="file" id="fileInput" multiple style="display: none">
                <div class="uploaded-files" id="uploadedFiles"></div>
            </div>
            
            <div class="form-actions" style="display: flex; gap: 1rem; justify-content: flex-end; margin-top: 2rem;">
                <button type="button" class="btn-secondary" onclick="App.closeModal()">Cancelar</button>
                <button type="submit" class="btn-primary">Criar Chamado</button>
            </div>
        `;
        
        // Add form submit handler
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.createTicket(new FormData(form));
        });
        
        this.showModal('newTicketModal');
    },
    
    // Show new customer modal
    showNewCustomerModal() {
        const modal = document.getElementById('newTicketModal');
        const modalHeader = modal.querySelector('.modal-header h3');
        const form = document.getElementById('newTicketForm');
        
        modalHeader.textContent = 'Novo Cliente';
        
        form.innerHTML = `
            <div class="form-group">
                <label class="form-label">Nome</label>
                <input type="text" class="form-input" name="firstName" required placeholder="Digite o nome">
            </div>
            
            <div class="form-group">
                <label class="form-label">Sobrenome</label>
                <input type="text" class="form-input" name="lastName" required placeholder="Digite o sobrenome">
            </div>
            
            <div class="form-group">
                <label class="form-label">Email</label>
                <input type="email" class="form-input" name="email" required placeholder="Digite o email">
            </div>
            
            <div class="form-group">
                <label class="form-label">Telefone</label>
                <input type="tel" class="form-input" name="phone" placeholder="Digite o telefone">
            </div>
            
            <div class="form-group">
                <label class="form-label">Organização</label>
                <input type="text" class="form-input" name="organization" placeholder="Digite a organização">
            </div>
            
            <div class="form-group">
                <label class="form-label">Foto do Perfil</label>
                <div class="file-upload-area" onclick="document.getElementById('avatarInput').click()">
                    <i class="fas fa-user-circle file-upload-icon"></i>
                    <p class="file-upload-text">Clique para selecionar uma foto</p>
                    <p class="file-upload-hint">JPG, PNG até 2MB</p>
                </div>
                <input type="file" id="avatarInput" accept="image/*" style="display: none">
            </div>
            
            <div class="form-actions" style="display: flex; gap: 1rem; justify-content: flex-end; margin-top: 2rem;">
                <button type="button" class="btn-secondary" onclick="App.closeModal()">Cancelar</button>
                <button type="submit" class="btn-primary">Criar Cliente</button>
            </div>
        `;
        
        // Add form submit handler
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.createCustomer(new FormData(form));
        });
        
        this.showModal('newTicketModal');
    },
    
    // Create ticket
    createTicket(formData) {
        const newTicket = {
            id: this.data.tickets.length + 1,
            ticketNumber: `T${String(this.data.tickets.length + 1).padStart(6, '0')}`,
            subject: formData.get('subject'),
            description: formData.get('description'),
            customerId: parseInt(formData.get('customerId')),
            departmentId: parseInt(formData.get('departmentId')),
            categoryId: formData.get('categoryId') ? parseInt(formData.get('categoryId')) : null,
            priorityId: parseInt(formData.get('priorityId')),
            statusId: 1, // Open
            assignedUserId: null,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            messages: [{
                id: Date.now(),
                userId: null,
                customerId: parseInt(formData.get('customerId')),
                message: formData.get('description'),
                isInternal: false,
                createdAt: new Date().toISOString()
            }],
            attachments: [],
            timeLogs: []
        };
        
        this.data.tickets.push(newTicket);
        this.saveData();
        this.closeModal();
        this.showNotification('Chamado criado com sucesso!', 'success');
        
        if (this.currentSection === 'tickets') {
            this.loadTickets();
        }
        if (this.currentSection === 'dashboard') {
            this.loadDashboard();
        }
    },
    
    // Create customer
    createCustomer(formData) {
        const newCustomer = {
            id: this.data.customers.length + 1,
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            email: formData.get('email'),
            phone: formData.get('phone'),
            organization: formData.get('organization'),
            avatar: null,
            createdAt: new Date().toISOString()
        };
        
        this.data.customers.push(newCustomer);
        this.saveData();
        this.closeModal();
        this.showNotification('Cliente criado com sucesso!', 'success');
        
        if (this.currentSection === 'customers') {
            this.loadCustomers();
        }
    },
    
    // Show modal
    showModal(modalId) {
        document.getElementById('modalOverlay').classList.add('active');
        document.getElementById(modalId).style.display = 'block';
    },
    
    // Close modal
    closeModal() {
        document.getElementById('modalOverlay').classList.remove('active');
        document.querySelectorAll('.modal').forEach(modal => {
            modal.style.display = 'none';
        });
    },
    
    // Show notification
    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'info-circle'}"></i>
            <span>${message}</span>
            <button class="notification-close">&times;</button>
        `;
        
        // Add to DOM
        document.body.appendChild(notification);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            notification.remove();
        }, 5000);
        
        // Close button
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.remove();
        });
    },
    
    // Format date
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },
    
    // Start real-time updates
    startRealTimeUpdates() {
        setInterval(() => {
            if (this.currentSection === 'dashboard') {
                this.updateDashboardStats();
            }
        }, 30000); // Update every 30 seconds
    },
    
    // Update UI
    updateUI() {
        // Update user info in sidebar
        if (this.currentUser) {
            document.getElementById('userName').textContent = `${this.currentUser.firstName} ${this.currentUser.lastName}`;
            document.getElementById('userRole').textContent = this.currentUser.role;
            if (this.currentUser.avatar) {
                document.getElementById('userAvatar').src = this.currentUser.avatar;
            }
        }
        
        // Update notification badge
        const notificationBadge = document.getElementById('notificationBadge');
        const pendingTickets = this.data.tickets.filter(t => t.statusId === 3).length; // Waiting for customer
        notificationBadge.textContent = pendingTickets;
        if (pendingTickets === 0) {
            notificationBadge.style.display = 'none';
        }
    },
    
    // Perform global search
    performGlobalSearch(query) {
        if (!query.trim()) return;
        
        // Search in tickets
        const ticketResults = this.data.tickets.filter(ticket => 
            ticket.ticketNumber.toLowerCase().includes(query.toLowerCase()) ||
            ticket.subject.toLowerCase().includes(query.toLowerCase())
        );
        
        // For now, just log results (in a real app, show search results)
        console.log('Search results:', ticketResults);
    }
};

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});

// Add settings tab event listeners
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            App.showSettingsTab(btn.dataset.tab);
        });
    });
});

// Make App globally available
window.App = App;