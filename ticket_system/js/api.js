// API Layer for Ticket System
// Handles all backend communication

class TicketAPI {
    constructor() {
        this.baseURL = 'api';
        this.endpoints = {
            // Authentication
            login: '/auth/login',
            logout: '/auth/logout',
            refresh: '/auth/refresh',
            
            // Tickets
            tickets: '/tickets',
            ticketById: (id) => `/tickets/${id}`,
            ticketMessages: (id) => `/tickets/${id}/messages`,
            ticketAttachments: (id) => `/tickets/${id}/attachments`,
            ticketTimeLogs: (id) => `/tickets/${id}/time-logs`,
            ticketAssign: (id) => `/tickets/${id}/assign`,
            
            // Customers
            customers: '/customers',
            customerById: (id) => `/customers/${id}`,
            customerTickets: (id) => `/customers/${id}/tickets`,
            
            // Organizations
            organizations: '/organizations',
            organizationById: (id) => `/organizations/${id}`,
            
            // Users/Agents
            users: '/users',
            userById: (id) => `/users/${id}`,
            userProfile: '/users/profile',
            
            // Configuration
            departments: '/config/departments',
            categories: '/config/categories',
            priorities: '/config/priorities',
            statuses: '/config/statuses',
            
            // Knowledge Base
            knowledgeBase: '/knowledge-base',
            kbCategories: '/knowledge-base/categories',
            kbArticles: '/knowledge-base/articles',
            kbArticleById: (id) => `/knowledge-base/articles/${id}`,
            
            // Reports
            reports: '/reports',
            dashboardStats: '/reports/dashboard',
            ticketStats: '/reports/tickets',
            agentProductivity: '/reports/agents',
            
            // File Upload
            upload: '/upload',
            uploadAvatar: '/upload/avatar',
            
            // Automation
            automationRules: '/automation/rules',
            
            // Audit
            auditLogs: '/audit/logs'
        };
        
        this.token = localStorage.getItem('authToken');
        this.setupRequestInterceptor();
    }
    
    // Setup request interceptor for authentication
    setupRequestInterceptor() {
        const originalFetch = window.fetch;
        
        window.fetch = async (url, options = {}) => {
            // Add authentication header
            if (this.token && !options.headers?.Authorization) {
                options.headers = {
                    ...options.headers,
                    'Authorization': `Bearer ${this.token}`
                };
            }
            
            // Add content type for JSON requests
            if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
                options.headers = {
                    ...options.headers,
                    'Content-Type': 'application/json'
                };
                options.body = JSON.stringify(options.body);
            }
            
            try {
                const response = await originalFetch(url, options);
                
                // Handle authentication errors
                if (response.status === 401) {
                    this.handleAuthError();
                    throw new Error('Authentication required');
                }
                
                return response;
            } catch (error) {
                console.error('API Request failed:', error);
                throw error;
            }
        };
    }
    
    // Handle authentication errors
    handleAuthError() {
        this.token = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
        
        // Redirect to login or show login modal
        // For now, just log the error
        console.warn('Authentication expired. Please login again.');
    }
    
    // Generic request method
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        
        try {
            const response = await fetch(url, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });
            
            if (!response.ok) {
                const error = await response.json().catch(() => ({ message: 'Request failed' }));
                throw new Error(error.message || `HTTP ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error(`API Error (${endpoint}):`, error);
            throw error;
        }
    }
    
    // GET request
    async get(endpoint, params = {}) {
        const url = new URL(`${this.baseURL}${endpoint}`, window.location.origin);
        Object.keys(params).forEach(key => {
            if (params[key] !== null && params[key] !== undefined) {
                url.searchParams.append(key, params[key]);
            }
        });
        
        return this.request(url.pathname + url.search, {
            method: 'GET'
        });
    }
    
    // POST request
    async post(endpoint, data = {}) {
        return this.request(endpoint, {
            method: 'POST',
            body: data
        });
    }
    
    // PUT request
    async put(endpoint, data = {}) {
        return this.request(endpoint, {
            method: 'PUT',
            body: data
        });
    }
    
    // PATCH request
    async patch(endpoint, data = {}) {
        return this.request(endpoint, {
            method: 'PATCH',
            body: data
        });
    }
    
    // DELETE request
    async delete(endpoint) {
        return this.request(endpoint, {
            method: 'DELETE'
        });
    }
    
    // File upload
    async upload(endpoint, formData) {
        const url = `${this.baseURL}${endpoint}`;
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Authorization': this.token ? `Bearer ${this.token}` : undefined
            },
            body: formData
        });
        
        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Upload failed' }));
            throw new Error(error.message || `HTTP ${response.status}`);
        }
        
        return await response.json();
    }
    
    // Authentication methods
    async login(credentials) {
        const response = await this.post(this.endpoints.login, credentials);
        
        if (response.token) {
            this.token = response.token;
            localStorage.setItem('authToken', this.token);
            localStorage.setItem('currentUser', JSON.stringify(response.user));
        }
        
        return response;
    }
    
    async logout() {
        try {
            await this.post(this.endpoints.logout);
        } catch (error) {
            console.warn('Logout request failed:', error);
        } finally {
            this.token = null;
            localStorage.removeItem('authToken');
            localStorage.removeItem('currentUser');
        }
    }
    
    async refreshToken() {
        const response = await this.post(this.endpoints.refresh);
        
        if (response.token) {
            this.token = response.token;
            localStorage.setItem('authToken', this.token);
        }
        
        return response;
    }
    
    // Ticket methods
    async getTickets(filters = {}) {
        return this.get(this.endpoints.tickets, filters);
    }
    
    async getTicketById(id) {
        return this.get(this.endpoints.ticketById(id));
    }
    
    async createTicket(ticketData) {
        return this.post(this.endpoints.tickets, ticketData);
    }
    
    async updateTicket(id, ticketData) {
        return this.put(this.endpoints.ticketById(id), ticketData);
    }
    
    async deleteTicket(id) {
        return this.delete(this.endpoints.ticketById(id));
    }
    
    async assignTicket(id, userId) {
        return this.post(this.endpoints.ticketAssign(id), { userId });
    }
    
    async getTicketMessages(id) {
        return this.get(this.endpoints.ticketMessages(id));
    }
    
    async addTicketMessage(id, messageData) {
        return this.post(this.endpoints.ticketMessages(id), messageData);
    }
    
    async getTicketAttachments(id) {
        return this.get(this.endpoints.ticketAttachments(id));
    }
    
    async addTicketAttachment(id, formData) {
        return this.upload(this.endpoints.ticketAttachments(id), formData);
    }
    
    async getTicketTimeLogs(id) {
        return this.get(this.endpoints.ticketTimeLogs(id));
    }
    
    async addTicketTimeLog(id, timeLogData) {
        return this.post(this.endpoints.ticketTimeLogs(id), timeLogData);
    }
    
    // Customer methods
    async getCustomers(filters = {}) {
        return this.get(this.endpoints.customers, filters);
    }
    
    async getCustomerById(id) {
        return this.get(this.endpoints.customerById(id));
    }
    
    async createCustomer(customerData) {
        return this.post(this.endpoints.customers, customerData);
    }
    
    async updateCustomer(id, customerData) {
        return this.put(this.endpoints.customerById(id), customerData);
    }
    
    async deleteCustomer(id) {
        return this.delete(this.endpoints.customerById(id));
    }
    
    async getCustomerTickets(id) {
        return this.get(this.endpoints.customerTickets(id));
    }
    
    // Organization methods
    async getOrganizations() {
        return this.get(this.endpoints.organizations);
    }
    
    async getOrganizationById(id) {
        return this.get(this.endpoints.organizationById(id));
    }
    
    async createOrganization(orgData) {
        return this.post(this.endpoints.organizations, orgData);
    }
    
    async updateOrganization(id, orgData) {
        return this.put(this.endpoints.organizationById(id), orgData);
    }
    
    // User methods
    async getUsers() {
        return this.get(this.endpoints.users);
    }
    
    async getUserById(id) {
        return this.get(this.endpoints.userById(id));
    }
    
    async createUser(userData) {
        return this.post(this.endpoints.users, userData);
    }
    
    async updateUser(id, userData) {
        return this.put(this.endpoints.userById(id), userData);
    }
    
    async deleteUser(id) {
        return this.delete(this.endpoints.userById(id));
    }
    
    async getUserProfile() {
        return this.get(this.endpoints.userProfile);
    }
    
    async updateUserProfile(profileData) {
        return this.put(this.endpoints.userProfile, profileData);
    }
    
    // Configuration methods
    async getDepartments() {
        return this.get(this.endpoints.departments);
    }
    
    async createDepartment(deptData) {
        return this.post(this.endpoints.departments, deptData);
    }
    
    async updateDepartment(id, deptData) {
        return this.put(`${this.endpoints.departments}/${id}`, deptData);
    }
    
    async deleteDepartment(id) {
        return this.delete(`${this.endpoints.departments}/${id}`);
    }
    
    async getCategories() {
        return this.get(this.endpoints.categories);
    }
    
    async createCategory(catData) {
        return this.post(this.endpoints.categories, catData);
    }
    
    async updateCategory(id, catData) {
        return this.put(`${this.endpoints.categories}/${id}`, catData);
    }
    
    async deleteCategory(id) {
        return this.delete(`${this.endpoints.categories}/${id}`);
    }
    
    async getPriorities() {
        return this.get(this.endpoints.priorities);
    }
    
    async createPriority(priorityData) {
        return this.post(this.endpoints.priorities, priorityData);
    }
    
    async updatePriority(id, priorityData) {
        return this.put(`${this.endpoints.priorities}/${id}`, priorityData);
    }
    
    async deletePriority(id) {
        return this.delete(`${this.endpoints.priorities}/${id}`);
    }
    
    async getStatuses() {
        return this.get(this.endpoints.statuses);
    }
    
    async createStatus(statusData) {
        return this.post(this.endpoints.statuses, statusData);
    }
    
    async updateStatus(id, statusData) {
        return this.put(`${this.endpoints.statuses}/${id}`, statusData);
    }
    
    async deleteStatus(id) {
        return this.delete(`${this.endpoints.statuses}/${id}`);
    }
    
    // Knowledge Base methods
    async getKnowledgeBaseCategories() {
        return this.get(this.endpoints.kbCategories);
    }
    
    async getKnowledgeBaseArticles(categoryId = null) {
        const params = categoryId ? { categoryId } : {};
        return this.get(this.endpoints.kbArticles, params);
    }
    
    async getKnowledgeBaseArticleById(id) {
        return this.get(this.endpoints.kbArticleById(id));
    }
    
    async createKnowledgeBaseArticle(articleData) {
        return this.post(this.endpoints.kbArticles, articleData);
    }
    
    async updateKnowledgeBaseArticle(id, articleData) {
        return this.put(this.endpoints.kbArticleById(id), articleData);
    }
    
    async deleteKnowledgeBaseArticle(id) {
        return this.delete(this.endpoints.kbArticleById(id));
    }
    
    async searchKnowledgeBase(query) {
        return this.get(this.endpoints.kbArticles, { search: query });
    }
    
    // Report methods
    async getDashboardStats() {
        return this.get(this.endpoints.dashboardStats);
    }
    
    async getTicketStats(filters = {}) {
        return this.get(this.endpoints.ticketStats, filters);
    }
    
    async getAgentProductivity(filters = {}) {
        return this.get(this.endpoints.agentProductivity, filters);
    }
    
    async exportReport(type, format, filters = {}) {
        const params = { format, ...filters };
        return this.get(`${this.endpoints.reports}/${type}/export`, params);
    }
    
    // File upload methods
    async uploadFile(file, type = 'general') {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('type', type);
        
        return this.upload(this.endpoints.upload, formData);
    }
    
    async uploadAvatar(file) {
        const formData = new FormData();
        formData.append('avatar', file);
        
        return this.upload(this.endpoints.uploadAvatar, formData);
    }
    
    // Automation methods
    async getAutomationRules() {
        return this.get(this.endpoints.automationRules);
    }
    
    async createAutomationRule(ruleData) {
        return this.post(this.endpoints.automationRules, ruleData);
    }
    
    async updateAutomationRule(id, ruleData) {
        return this.put(`${this.endpoints.automationRules}/${id}`, ruleData);
    }
    
    async deleteAutomationRule(id) {
        return this.delete(`${this.endpoints.automationRules}/${id}`);
    }
    
    // Audit methods
    async getAuditLogs(filters = {}) {
        return this.get(this.endpoints.auditLogs, filters);
    }
    
    // Utility methods
    async healthCheck() {
        try {
            const response = await fetch(`${this.baseURL}/health`);
            return response.ok;
        } catch (error) {
            return false;
        }
    }
    
    // Mock API methods for development
    getMockData(type) {
        const mockData = {
            tickets: [
                {
                    id: 1,
                    ticketNumber: 'T000001',
                    subject: 'Cannot access email',
                    status: 'open',
                    priority: 'medium',
                    customer: 'John Doe',
                    assignedTo: 'Admin User',
                    createdAt: '2024-01-15T10:00:00Z'
                }
            ],
            customers: [
                {
                    id: 1,
                    firstName: 'John',
                    lastName: 'Doe',
                    email: 'john.doe@example.com',
                    organization: 'Acme Corp',
                    ticketsCount: 5
                }
            ],
            dashboardStats: {
                openTickets: 15,
                closedTickets: 45,
                highPriorityTickets: 3,
                totalCustomers: 25,
                avgResponseTime: 2.5
            }
        };
        
        return Promise.resolve(mockData[type] || []);
    }
}

// Singleton instance
const API = new TicketAPI();

// Make it globally available
window.API = API;

// Export for modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = TicketAPI;
}