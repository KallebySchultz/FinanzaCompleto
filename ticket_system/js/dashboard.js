// Dashboard Module
// Handles dashboard functionality and charts

const DashboardModule = {
    charts: {},
    refreshInterval: null,
    
    // Initialize dashboard
    init() {
        this.loadDashboardData();
        this.setupCharts();
        this.startAutoRefresh();
    },
    
    // Load dashboard data
    async loadDashboardData() {
        try {
            // Try to load from API first
            const response = await API.getDashboardStats();
            this.updateStats(response.data);
        } catch (error) {
            console.warn('API not available, using local data');
            this.updateStatsFromLocal();
        }
        
        this.loadRecentTickets();
    },
    
    // Update statistics from API data
    updateStats(data) {
        document.getElementById('openTickets').textContent = data.openTickets || 0;
        document.getElementById('closedTickets').textContent = data.closedTickets || 0;
        document.getElementById('highPriorityTickets').textContent = data.highPriorityTickets || 0;
        document.getElementById('totalCustomers').textContent = data.totalCustomers || 0;
    },
    
    // Update statistics from local data
    updateStatsFromLocal() {
        const openTickets = App.data.tickets.filter(t => 
            App.data.statuses.find(s => s.id === t.statusId)?.isOpen
        ).length;
        
        const closedTickets = App.data.tickets.filter(t => 
            App.data.statuses.find(s => s.id === t.statusId)?.isClosed
        ).length;
        
        const highPriorityTickets = App.data.tickets.filter(t => 
            App.data.priorities.find(p => p.id === t.priorityId)?.level >= 3
        ).length;
        
        const totalCustomers = App.data.customers.length;
        
        document.getElementById('openTickets').textContent = openTickets;
        document.getElementById('closedTickets').textContent = closedTickets;
        document.getElementById('highPriorityTickets').textContent = highPriorityTickets;
        document.getElementById('totalCustomers').textContent = totalCustomers;
    },
    
    // Load recent tickets
    loadRecentTickets() {
        const recentTickets = App.data.tickets
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            .slice(0, 5);
        
        const container = document.getElementById('recentTicketsList');
        
        if (recentTickets.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-ticket-alt"></i>
                    <p>Nenhum chamado encontrado</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = recentTickets.map(ticket => {
            const customer = App.data.customers.find(c => c.id === ticket.customerId);
            const status = App.data.statuses.find(s => s.id === ticket.statusId);
            const priority = App.data.priorities.find(p => p.id === ticket.priorityId);
            
            return `
                <div class="ticket-item" onclick="TicketsModule.showTicketDetails(${ticket.id})">
                    <div class="ticket-info">
                        <div class="ticket-number">${ticket.ticketNumber}</div>
                        <h4 class="ticket-subject">${ticket.subject}</h4>
                        <div class="ticket-meta">
                            <span class="ticket-customer">${customer?.firstName} ${customer?.lastName}</span>
                            <span>•</span>
                            <span>${App.formatDate(ticket.createdAt)}</span>
                        </div>
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
        }).join('');
    },
    
    // Setup charts
    setupCharts() {
        this.createProductivityChart();
        this.createStatusChart();
        this.createResponseTimeChart();
    },
    
    // Create productivity chart
    createProductivityChart() {
        const ctx = document.getElementById('productivityChart');
        if (!ctx) return;
        
        // Generate sample data based on tickets
        const last6Months = [];
        const monthlyData = [];
        
        for (let i = 5; i >= 0; i--) {
            const date = new Date();
            date.setMonth(date.getMonth() - i);
            last6Months.push(date.toLocaleDateString('pt-BR', { month: 'short' }));
            
            // Count tickets created in this month
            const monthTickets = App.data.tickets.filter(ticket => {
                const ticketDate = new Date(ticket.createdAt);
                return ticketDate.getMonth() === date.getMonth() && 
                       ticketDate.getFullYear() === date.getFullYear();
            }).length;
            
            monthlyData.push(monthTickets);
        }
        
        this.charts.productivity = new Chart(ctx, {
            type: 'line',
            data: {
                labels: last6Months,
                datasets: [{
                    label: 'Chamados Criados',
                    data: monthlyData,
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    tension: 0.4,
                    fill: true
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
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                },
                elements: {
                    point: {
                        radius: 4,
                        hoverRadius: 6
                    }
                }
            }
        });
    },
    
    // Create status chart
    createStatusChart() {
        const ctx = document.getElementById('statusChart');
        if (!ctx) return;
        
        const statusCounts = App.data.statuses.map(status => {
            return App.data.tickets.filter(t => t.statusId === status.id).length;
        });
        
        this.charts.status = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: App.data.statuses.map(s => s.name),
                datasets: [{
                    data: statusCounts,
                    backgroundColor: App.data.statuses.map(s => s.color),
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true
                        }
                    }
                },
                cutout: '60%'
            }
        });
    },
    
    // Create response time chart
    createResponseTimeChart() {
        const ctx = document.getElementById('responseTimeChart');
        if (!ctx) return;
        
        // Generate sample response time data
        const last7Days = [];
        const responseTimeData = [];
        
        for (let i = 6; i >= 0; i--) {
            const date = new Date();
            date.setDate(date.getDate() - i);
            last7Days.push(date.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }));
            
            // Generate random response time between 1-8 hours
            responseTimeData.push(Math.floor(Math.random() * 7) + 1);
        }
        
        this.charts.responseTime = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: last7Days,
                datasets: [{
                    label: 'Tempo Médio (horas)',
                    data: responseTimeData,
                    backgroundColor: '#28a745',
                    borderRadius: 4,
                    borderSkipped: false
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
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1,
                            callback: function(value) {
                                return value + 'h';
                            }
                        }
                    }
                }
            }
        });
    },
    
    // Refresh dashboard data
    refresh() {
        this.loadDashboardData();
        this.updateCharts();
    },
    
    // Update charts with new data
    updateCharts() {
        if (this.charts.status) {
            const statusCounts = App.data.statuses.map(status => {
                return App.data.tickets.filter(t => t.statusId === status.id).length;
            });
            
            this.charts.status.data.datasets[0].data = statusCounts;
            this.charts.status.update();
        }
    },
    
    // Start auto-refresh
    startAutoRefresh() {
        // Refresh every 30 seconds
        this.refreshInterval = setInterval(() => {
            this.refresh();
        }, 30000);
    },
    
    // Stop auto-refresh
    stopAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
            this.refreshInterval = null;
        }
    },
    
    // Destroy charts
    destroyCharts() {
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
        this.charts = {};
    },
    
    // Cleanup when leaving dashboard
    cleanup() {
        this.stopAutoRefresh();
        // Don't destroy charts as they might be needed again
    }
};

// Reports Module
const ReportsModule = {
    // Initialize reports
    init() {
        this.loadReports();
    },
    
    // Load reports data
    async loadReports() {
        try {
            // Try to load from API
            const [ticketStats, agentStats] = await Promise.all([
                API.getTicketStats(),
                API.getAgentProductivity()
            ]);
            
            this.renderTicketStatsChart(ticketStats.data);
            this.renderAgentProductivityChart(agentStats.data);
            
        } catch (error) {
            console.warn('Reports API not available, using local data');
            this.loadReportsFromLocal();
        }
    },
    
    // Load reports from local data
    loadReportsFromLocal() {
        // Create ticket stats
        const ticketStats = App.data.statuses.map(status => ({
            status_name: status.name,
            status_color: status.color,
            count: App.data.tickets.filter(t => t.statusId === status.id).length
        }));
        
        this.renderTicketStatsChart(ticketStats);
        
        // Create agent productivity stats
        const agentStats = [{
            agent_name: 'Admin User',
            total_tickets: App.data.tickets.length,
            closed_tickets: App.data.tickets.filter(t => 
                App.data.statuses.find(s => s.id === t.statusId)?.isClosed
            ).length,
            open_tickets: App.data.tickets.filter(t => 
                App.data.statuses.find(s => s.id === t.statusId)?.isOpen
            ).length
        }];
        
        this.renderAgentProductivityChart(agentStats);
    },
    
    // Render ticket stats chart
    renderTicketStatsChart(data) {
        const ctx = document.getElementById('statusChart');
        if (!ctx || !data) return;
        
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: data.map(item => item.status_name),
                datasets: [{
                    data: data.map(item => item.count),
                    backgroundColor: data.map(item => item.status_color),
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    },
    
    // Render agent productivity chart
    renderAgentProductivityChart(data) {
        const ctx = document.getElementById('responseTimeChart');
        if (!ctx || !data) return;
        
        const agents = data.map(item => item.agent_name);
        const closedTickets = data.map(item => item.closed_tickets);
        const openTickets = data.map(item => item.open_tickets);
        
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: agents,
                datasets: [
                    {
                        label: 'Fechados',
                        data: closedTickets,
                        backgroundColor: '#28a745'
                    },
                    {
                        label: 'Abertos',
                        data: openTickets,
                        backgroundColor: '#007bff'
                    }
                ]
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
    
    // Export report to PDF
    async exportToPDF() {
        try {
            const loadingId = ModalsModule.showLoading('Gerando relatório PDF...');
            
            // Simulate PDF generation
            await new Promise(resolve => setTimeout(resolve, 2000));
            
            ModalsModule.hideLoading();
            NotificationSystem.success('Relatório PDF gerado com sucesso!');
            
            // In a real implementation, this would trigger a download
        } catch (error) {
            ModalsModule.hideLoading();
            NotificationSystem.error('Erro ao gerar relatório PDF');
        }
    },
    
    // Export report to Excel
    async exportToExcel() {
        try {
            const loadingId = ModalsModule.showLoading('Gerando relatório Excel...');
            
            // Simulate Excel generation
            await new Promise(resolve => setTimeout(resolve, 1500));
            
            ModalsModule.hideLoading();
            NotificationSystem.success('Relatório Excel gerado com sucesso!');
            
            // In a real implementation, this would trigger a download
        } catch (error) {
            ModalsModule.hideLoading();
            NotificationSystem.error('Erro ao gerar relatório Excel');
        }
    }
};

// Knowledge Base Module
const KnowledgeModule = {
    categories: [],
    articles: [],
    
    // Initialize knowledge base
    init() {
        this.loadKnowledgeBase();
    },
    
    // Load knowledge base data
    async loadKnowledgeBase() {
        try {
            const [categories, articles] = await Promise.all([
                API.getKnowledgeBaseCategories(),
                API.getKnowledgeBaseArticles()
            ]);
            
            this.categories = categories.data || [];
            this.articles = articles.data || [];
            
        } catch (error) {
            console.warn('Knowledge base API not available, using default data');
            this.loadDefaultKnowledgeBase();
        }
        
        this.renderKnowledgeCategories();
    },
    
    // Load default knowledge base
    loadDefaultKnowledgeBase() {
        this.categories = [
            { 
                id: 1, 
                name: 'Primeiros Passos', 
                description: 'Informações básicas para novos usuários',
                icon: 'fas fa-play-circle',
                articles_count: 5
            },
            { 
                id: 2, 
                name: 'Solução de Problemas', 
                description: 'Problemas comuns e suas soluções',
                icon: 'fas fa-tools',
                articles_count: 12
            },
            { 
                id: 3, 
                name: 'Guias Passo a Passo', 
                description: 'Instruções detalhadas para procedimentos',
                icon: 'fas fa-list-ol',
                articles_count: 8
            },
            { 
                id: 4, 
                name: 'Perguntas Frequentes', 
                description: 'Respostas para dúvidas mais comuns',
                icon: 'fas fa-question-circle',
                articles_count: 15
            }
        ];
    },
    
    // Render knowledge base categories
    renderKnowledgeCategories() {
        const container = document.getElementById('knowledgeCategories');
        
        if (this.categories.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-book"></i>
                    <h3>Base de conhecimento vazia</h3>
                    <p>Adicione categorias e artigos para começar.</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = this.categories.map(category => `
            <div class="knowledge-category" onclick="KnowledgeModule.showCategory(${category.id})">
                <div class="category-icon">
                    <i class="${category.icon}"></i>
                </div>
                <h3 class="category-title">${category.name}</h3>
                <p class="category-description">${category.description}</p>
                <div class="category-stats">
                    <span>${category.articles_count || 0} artigos</span>
                    <span>Ver todos</span>
                </div>
            </div>
        `).join('');
    },
    
    // Show category details
    showCategory(categoryId) {
        const category = this.categories.find(c => c.id === categoryId);
        if (!category) return;
        
        NotificationSystem.info(`Visualizando categoria: ${category.name}`);
        // In a real implementation, this would show the category articles
    }
};

// Settings Module
const SettingsModule = {
    currentTab: 'departments',
    
    // Initialize settings
    init() {
        this.showTab('departments');
        this.setupTabListeners();
    },
    
    // Setup tab listeners
    setupTabListeners() {
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                this.showTab(btn.dataset.tab);
            });
        });
    },
    
    // Show specific tab
    showTab(tabName) {
        // Update tab buttons
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        const activeTab = document.querySelector(`[data-tab="${tabName}"]`);
        if (activeTab) {
            activeTab.classList.add('active');
        }
        
        this.currentTab = tabName;
        this.renderTabContent(tabName);
    },
    
    // Render tab content
    renderTabContent(tabName) {
        const content = document.getElementById('settingsContent');
        
        switch (tabName) {
            case 'departments':
                content.innerHTML = this.renderDepartmentsTab();
                break;
            case 'categories':
                content.innerHTML = this.renderCategoriesTab();
                break;
            case 'priorities':
                content.innerHTML = this.renderPrioritiesTab();
                break;
            case 'users':
                content.innerHTML = this.renderUsersTab();
                break;
        }
    },
    
    // Render departments tab
    renderDepartmentsTab() {
        return `
            <div class="settings-section">
                <div class="section-header">
                    <h3>Departamentos</h3>
                    <button class="btn-primary" onclick="SettingsModule.addDepartment()">
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
                                <th>Chamados</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${App.data.departments.map(dept => {
                                const ticketCount = App.data.tickets.filter(t => t.departmentId === dept.id).length;
                                return `
                                    <tr>
                                        <td>${dept.name}</td>
                                        <td>
                                            <div class="color-indicator-wrapper">
                                                <span class="color-indicator" style="background: ${dept.color}"></span>
                                                ${dept.color}
                                            </div>
                                        </td>
                                        <td>
                                            <span class="status-badge ${dept.isActive ? 'open' : 'closed'}">
                                                ${dept.isActive ? 'Ativo' : 'Inativo'}
                                            </span>
                                        </td>
                                        <td>${ticketCount}</td>
                                        <td>
                                            <div class="action-buttons">
                                                <button class="action-btn edit" onclick="SettingsModule.editDepartment(${dept.id})" title="Editar">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="action-btn delete" onclick="SettingsModule.deleteDepartment(${dept.id})" title="Excluir">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
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
    
    // Render other tabs (placeholder implementations)
    renderCategoriesTab() {
        return '<div class="coming-soon">Gerenciamento de categorias em desenvolvimento...</div>';
    },
    
    renderPrioritiesTab() {
        return '<div class="coming-soon">Gerenciamento de prioridades em desenvolvimento...</div>';
    },
    
    renderUsersTab() {
        return '<div class="coming-soon">Gerenciamento de usuários em desenvolvimento...</div>';
    },
    
    // Department management functions (placeholders)
    addDepartment() {
        NotificationSystem.info('Função de adicionar departamento em desenvolvimento');
    },
    
    editDepartment(id) {
        NotificationSystem.info(`Editar departamento ${id} em desenvolvimento`);
    },
    
    deleteDepartment(id) {
        NotificationSystem.info(`Excluir departamento ${id} em desenvolvimento`);
    }
};

// Make modules globally available
window.DashboardModule = DashboardModule;
window.ReportsModule = ReportsModule;
window.KnowledgeModule = KnowledgeModule;
window.SettingsModule = SettingsModule;