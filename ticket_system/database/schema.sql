-- Ticket System Database Schema
-- Complete ticket management system with all required features

-- Drop existing tables if they exist
DROP TABLE IF EXISTS ticket_attachments;
DROP TABLE IF EXISTS ticket_messages;
DROP TABLE IF EXISTS ticket_time_logs;
DROP TABLE IF EXISTS ticket_assignments;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS knowledge_base_articles;
DROP TABLE IF EXISTS knowledge_base_categories;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS automation_rules;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS organizations;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS priorities;
DROP TABLE IF EXISTS ticket_status;

-- ================================================
-- Configuration Tables
-- ================================================

-- Departments
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    email VARCHAR(150),
    color VARCHAR(7) DEFAULT '#007bff',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Categories
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    department_id INT,
    color VARCHAR(7) DEFAULT '#6c757d',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Priorities
CREATE TABLE priorities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL UNIQUE,
    color VARCHAR(7) NOT NULL,
    sla_hours INT DEFAULT 24,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Ticket Status
CREATE TABLE ticket_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) NOT NULL,
    is_open BOOLEAN DEFAULT TRUE,
    is_closed BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================================================
-- User Management
-- ================================================

-- Users/Agents
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    role ENUM('admin', 'agent', 'supervisor') DEFAULT 'agent',
    department_id INT,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Organizations
CREATE TABLE organizations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    domain VARCHAR(100),
    logo_url VARCHAR(255),
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(150),
    website VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Customers
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    organization_id INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- ================================================
-- Ticket Management
-- ================================================

-- Tickets
CREATE TABLE tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_number VARCHAR(20) UNIQUE NOT NULL,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    customer_id INT NOT NULL,
    assigned_user_id INT,
    department_id INT NOT NULL,
    category_id INT,
    priority_id INT NOT NULL,
    status_id INT NOT NULL,
    created_by_user_id INT,
    closed_at TIMESTAMP NULL,
    due_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (assigned_user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (priority_id) REFERENCES priorities(id),
    FOREIGN KEY (status_id) REFERENCES ticket_status(id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(id),
    INDEX idx_ticket_number (ticket_number),
    INDEX idx_customer (customer_id),
    INDEX idx_assigned_user (assigned_user_id),
    INDEX idx_status (status_id),
    INDEX idx_priority (priority_id),
    INDEX idx_created_at (created_at)
);

-- Ticket Messages
CREATE TABLE ticket_messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    user_id INT,
    customer_id INT,
    message TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT FALSE,
    is_system_message BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_created_at (created_at)
);

-- Ticket Attachments
CREATE TABLE ticket_attachments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT,
    message_id INT,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size INT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    uploaded_by_user_id INT,
    uploaded_by_customer_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (message_id) REFERENCES ticket_messages(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by_user_id) REFERENCES users(id),
    FOREIGN KEY (uploaded_by_customer_id) REFERENCES customers(id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_message (message_id)
);

-- Ticket Time Logs
CREATE TABLE ticket_time_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    user_id INT NOT NULL,
    description TEXT,
    time_spent_minutes INT NOT NULL,
    billable BOOLEAN DEFAULT FALSE,
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_user (user_id),
    INDEX idx_logged_at (logged_at)
);

-- Ticket Assignments History
CREATE TABLE ticket_assignments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    from_user_id INT,
    to_user_id INT,
    assigned_by_user_id INT NOT NULL,
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    FOREIGN KEY (to_user_id) REFERENCES users(id),
    FOREIGN KEY (assigned_by_user_id) REFERENCES users(id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_created_at (created_at)
);

-- ================================================
-- Knowledge Base
-- ================================================

-- Knowledge Base Categories
CREATE TABLE knowledge_base_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50) DEFAULT 'fas fa-folder',
    color VARCHAR(7) DEFAULT '#007bff',
    parent_id INT,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES knowledge_base_categories(id)
);

-- Knowledge Base Articles
CREATE TABLE knowledge_base_articles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    summary TEXT,
    category_id INT NOT NULL,
    author_user_id INT NOT NULL,
    is_published BOOLEAN DEFAULT FALSE,
    views_count INT DEFAULT 0,
    helpful_count INT DEFAULT 0,
    not_helpful_count INT DEFAULT 0,
    tags TEXT,
    published_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES knowledge_base_categories(id),
    FOREIGN KEY (author_user_id) REFERENCES users(id),
    INDEX idx_category (category_id),
    INDEX idx_published (is_published),
    INDEX idx_created_at (created_at),
    FULLTEXT idx_search (title, content, summary, tags)
);

-- ================================================
-- Automation and Rules
-- ================================================

-- Automation Rules
CREATE TABLE automation_rules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    trigger_event ENUM('ticket_created', 'ticket_updated', 'message_added', 'status_changed', 'priority_changed') NOT NULL,
    conditions JSON,
    actions JSON,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_by_user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

-- ================================================
-- Audit and Logging
-- ================================================

-- Audit Logs
CREATE TABLE audit_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entity_type VARCHAR(50) NOT NULL,
    entity_id INT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_values JSON,
    new_values JSON,
    user_id INT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_user (user_id),
    INDEX idx_created_at (created_at)
);

-- ================================================
-- Insert Default Data
-- ================================================

-- Default Departments
INSERT INTO departments (name, description, color) VALUES
('Technical Support', 'Technical issues and troubleshooting', '#007bff'),
('Sales', 'Sales inquiries and support', '#28a745'),
('Billing', 'Billing and payment related issues', '#ffc107'),
('General', 'General inquiries and other issues', '#6c757d');

-- Default Categories
INSERT INTO categories (name, description, department_id, color) VALUES
('Software Issue', 'Software bugs and problems', 1, '#dc3545'),
('Hardware Issue', 'Hardware problems and failures', 1, '#fd7e14'),
('Installation Help', 'Help with software installation', 1, '#20c997'),
('Product Inquiry', 'Questions about products', 2, '#17a2b8'),
('Price Quote', 'Request for pricing information', 2, '#6f42c1'),
('Payment Issue', 'Problems with payments', 3, '#e83e8c'),
('Refund Request', 'Request for refunds', 3, '#6c757d'),
('General Question', 'General questions and inquiries', 4, '#007bff');

-- Default Priorities
INSERT INTO priorities (name, level, color, sla_hours) VALUES
('Low', 1, '#28a745', 72),
('Medium', 2, '#ffc107', 24),
('High', 3, '#fd7e14', 8),
('Urgent', 4, '#dc3545', 2);

-- Default Ticket Status
INSERT INTO ticket_status (name, color, is_open, is_closed, sort_order) VALUES
('Open', '#007bff', TRUE, FALSE, 1),
('In Progress', '#ffc107', TRUE, FALSE, 2),
('Waiting for Customer', '#6c757d', TRUE, FALSE, 3),
('Resolved', '#28a745', FALSE, TRUE, 4),
('Closed', '#dc3545', FALSE, TRUE, 5);

-- Default Admin User (password: admin123)
INSERT INTO users (username, email, password_hash, first_name, last_name, role, department_id) VALUES
('admin', 'admin@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'User', 'admin', 1);

-- Default Knowledge Base Categories
INSERT INTO knowledge_base_categories (name, description, icon, color) VALUES
('Getting Started', 'Basic information for new users', 'fas fa-play-circle', '#28a745'),
('Troubleshooting', 'Common problems and solutions', 'fas fa-tools', '#dc3545'),
('How-To Guides', 'Step-by-step instructions', 'fas fa-list-ol', '#007bff'),
('FAQ', 'Frequently asked questions', 'fas fa-question-circle', '#ffc107');

-- Sample Organization
INSERT INTO organizations (name, domain, email, phone, website) VALUES
('Acme Corporation', 'acme.com', 'contact@acme.com', '+1 (555) 123-4567', 'https://www.acme.com'),
('Tech Solutions Inc', 'techsolutions.com', 'info@techsolutions.com', '+1 (555) 987-6543', 'https://www.techsolutions.com');

-- Sample Customers
INSERT INTO customers (first_name, last_name, email, phone, organization_id) VALUES
('John', 'Doe', 'john.doe@acme.com', '+1 (555) 111-2222', 1),
('Jane', 'Smith', 'jane.smith@techsolutions.com', '+1 (555) 333-4444', 2),
('Bob', 'Johnson', 'bob.johnson@gmail.com', '+1 (555) 555-6666', NULL);

-- ================================================
-- Triggers for Audit Logging
-- ================================================

-- Trigger for ticket updates
DELIMITER //
CREATE TRIGGER ticket_audit_trigger 
AFTER UPDATE ON tickets
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (entity_type, entity_id, action, old_values, new_values, user_id)
    VALUES (
        'ticket',
        NEW.id,
        'update',
        JSON_OBJECT(
            'status_id', OLD.status_id,
            'priority_id', OLD.priority_id,
            'assigned_user_id', OLD.assigned_user_id,
            'subject', OLD.subject
        ),
        JSON_OBJECT(
            'status_id', NEW.status_id,
            'priority_id', NEW.priority_id,
            'assigned_user_id', NEW.assigned_user_id,
            'subject', NEW.subject
        ),
        NEW.assigned_user_id
    );
END//
DELIMITER ;

-- Function to generate ticket numbers
DELIMITER //
CREATE FUNCTION generate_ticket_number() RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE next_number INT;
    DECLARE ticket_number VARCHAR(20);
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(ticket_number, 2) AS UNSIGNED)), 0) + 1 
    INTO next_number 
    FROM tickets 
    WHERE ticket_number REGEXP '^T[0-9]+$';
    
    SET ticket_number = CONCAT('T', LPAD(next_number, 6, '0'));
    
    RETURN ticket_number;
END//
DELIMITER ;

-- Trigger to auto-generate ticket numbers
DELIMITER //
CREATE TRIGGER generate_ticket_number_trigger
BEFORE INSERT ON tickets
FOR EACH ROW
BEGIN
    IF NEW.ticket_number IS NULL OR NEW.ticket_number = '' THEN
        SET NEW.ticket_number = generate_ticket_number();
    END IF;
END//
DELIMITER ;

-- ================================================
-- Indexes for Performance
-- ================================================

-- Additional indexes for better performance
CREATE INDEX idx_tickets_customer_status ON tickets(customer_id, status_id);
CREATE INDEX idx_tickets_assigned_status ON tickets(assigned_user_id, status_id);
CREATE INDEX idx_tickets_department_priority ON tickets(department_id, priority_id);
CREATE INDEX idx_messages_ticket_created ON ticket_messages(ticket_id, created_at);
CREATE INDEX idx_audit_logs_entity_action ON audit_logs(entity_type, entity_id, action);

-- ================================================
-- Views for Reporting
-- ================================================

-- View for ticket statistics
CREATE VIEW ticket_stats AS
SELECT 
    d.name as department_name,
    s.name as status_name,
    p.name as priority_name,
    COUNT(*) as ticket_count,
    AVG(CASE WHEN t.closed_at IS NOT NULL 
        THEN TIMESTAMPDIFF(HOUR, t.created_at, t.closed_at) 
        ELSE NULL END) as avg_resolution_hours
FROM tickets t
JOIN departments d ON t.department_id = d.id
JOIN ticket_status s ON t.status_id = s.id
JOIN priorities p ON t.priority_id = p.id
GROUP BY d.name, s.name, p.name;

-- View for agent productivity
CREATE VIEW agent_productivity AS
SELECT 
    u.id as user_id,
    CONCAT(u.first_name, ' ', u.last_name) as agent_name,
    COUNT(t.id) as total_tickets,
    COUNT(CASE WHEN s.is_closed = TRUE THEN 1 END) as closed_tickets,
    COUNT(CASE WHEN s.is_open = TRUE THEN 1 END) as open_tickets,
    AVG(CASE WHEN t.closed_at IS NOT NULL 
        THEN TIMESTAMPDIFF(HOUR, t.created_at, t.closed_at) 
        ELSE NULL END) as avg_resolution_hours,
    SUM(COALESCE(tl.time_spent_minutes, 0)) as total_time_minutes
FROM users u
LEFT JOIN tickets t ON u.id = t.assigned_user_id
LEFT JOIN ticket_status s ON t.status_id = s.id
LEFT JOIN (
    SELECT ticket_id, SUM(time_spent_minutes) as time_spent_minutes
    FROM ticket_time_logs
    GROUP BY ticket_id
) tl ON t.id = tl.ticket_id
WHERE u.role IN ('agent', 'supervisor')
GROUP BY u.id, u.first_name, u.last_name;