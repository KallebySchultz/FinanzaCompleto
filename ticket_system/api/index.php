<?php
// API Router for Ticket System
// Simple PHP backend for handling API requests

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight OPTIONS requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database configuration
define('DB_HOST', 'localhost');
define('DB_NAME', 'ticket_system');
define('DB_USER', 'root');
define('DB_PASS', '');

// Simple PDO connection
function getDatabase() {
    static $pdo = null;
    
    if ($pdo === null) {
        try {
            $pdo = new PDO(
                "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=utf8mb4",
                DB_USER,
                DB_PASS,
                [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES => false
                ]
            );
        } catch (PDOException $e) {
            sendResponse(['error' => 'Database connection failed: ' . $e->getMessage()], 500);
        }
    }
    
    return $pdo;
}

// Response helper
function sendResponse($data, $statusCode = 200) {
    http_response_code($statusCode);
    echo json_encode($data);
    exit();
}

// Get current user from token (simplified)
function getCurrentUser() {
    return [
        'id' => 1,
        'username' => 'admin',
        'email' => 'admin@example.com',
        'role' => 'admin'
    ];
}

// Parse request URI
$requestUri = $_SERVER['REQUEST_URI'];
$path = parse_url($requestUri, PHP_URL_PATH);
$path = trim($path, '/');

// Remove 'api' prefix if present
if (strpos($path, 'api/') === 0) {
    $path = substr($path, 4);
}

// Split path into segments
$segments = explode('/', $path);
$method = $_SERVER['REQUEST_METHOD'];

// Get request body for POST/PUT requests
$input = json_decode(file_get_contents('php://input'), true);

try {
    $pdo = getDatabase();
    
    // Route handling
    switch ($segments[0]) {
        case 'health':
            sendResponse(['status' => 'ok']);
            break;
            
        case 'auth':
            handleAuth($segments, $method, $input);
            break;
            
        case 'tickets':
            handleTickets($segments, $method, $input, $pdo);
            break;
            
        case 'customers':
            handleCustomers($segments, $method, $input, $pdo);
            break;
            
        case 'config':
            handleConfig($segments, $method, $input, $pdo);
            break;
            
        case 'reports':
            handleReports($segments, $method, $input, $pdo);
            break;
            
        default:
            sendResponse(['error' => 'Endpoint not found'], 404);
    }
    
} catch (Exception $e) {
    sendResponse(['error' => $e->getMessage()], 500);
}

// Authentication handlers
function handleAuth($segments, $method, $input) {
    switch ($segments[1] ?? '') {
        case 'login':
            if ($method === 'POST') {
                // Simplified login - in production, verify credentials properly
                $user = [
                    'id' => 1,
                    'username' => 'admin',
                    'email' => 'admin@example.com',
                    'firstName' => 'Admin',
                    'lastName' => 'User',
                    'role' => 'admin'
                ];
                
                sendResponse([
                    'success' => true,
                    'token' => 'mock_jwt_token_' . time(),
                    'user' => $user
                ]);
            }
            break;
            
        case 'logout':
            sendResponse(['success' => true]);
            break;
            
        default:
            sendResponse(['error' => 'Auth endpoint not found'], 404);
    }
}

// Ticket handlers
function handleTickets($segments, $method, $input, $pdo) {
    switch ($method) {
        case 'GET':
            if (isset($segments[1]) && is_numeric($segments[1])) {
                // Get specific ticket
                getTicketById($segments[1], $pdo);
            } else {
                // Get all tickets with filters
                getAllTickets($_GET, $pdo);
            }
            break;
            
        case 'POST':
            createTicket($input, $pdo);
            break;
            
        case 'PUT':
            if (isset($segments[1]) && is_numeric($segments[1])) {
                updateTicket($segments[1], $input, $pdo);
            }
            break;
            
        case 'DELETE':
            if (isset($segments[1]) && is_numeric($segments[1])) {
                deleteTicket($segments[1], $pdo);
            }
            break;
            
        default:
            sendResponse(['error' => 'Method not allowed'], 405);
    }
}

function getAllTickets($filters, $pdo) {
    $sql = "SELECT t.*, 
                   CONCAT(c.first_name, ' ', c.last_name) as customer_name,
                   c.email as customer_email,
                   d.name as department_name,
                   cat.name as category_name,
                   p.name as priority_name,
                   p.color as priority_color,
                   s.name as status_name,
                   s.color as status_color,
                   CONCAT(u.first_name, ' ', u.last_name) as assigned_user_name
            FROM tickets t
            LEFT JOIN customers c ON t.customer_id = c.id
            LEFT JOIN departments d ON t.department_id = d.id
            LEFT JOIN categories cat ON t.category_id = cat.id
            LEFT JOIN priorities p ON t.priority_id = p.id
            LEFT JOIN ticket_status s ON t.status_id = s.id
            LEFT JOIN users u ON t.assigned_user_id = u.id
            ORDER BY t.created_at DESC";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $tickets = $stmt->fetchAll();
    
    sendResponse(['data' => $tickets]);
}

function getTicketById($id, $pdo) {
    $sql = "SELECT t.*, 
                   CONCAT(c.first_name, ' ', c.last_name) as customer_name,
                   c.email as customer_email,
                   d.name as department_name,
                   cat.name as category_name,
                   p.name as priority_name,
                   s.name as status_name
            FROM tickets t
            LEFT JOIN customers c ON t.customer_id = c.id
            LEFT JOIN departments d ON t.department_id = d.id
            LEFT JOIN categories cat ON t.category_id = cat.id
            LEFT JOIN priorities p ON t.priority_id = p.id
            LEFT JOIN ticket_status s ON t.status_id = s.id
            WHERE t.id = ?";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$id]);
    $ticket = $stmt->fetch();
    
    if (!$ticket) {
        sendResponse(['error' => 'Ticket not found'], 404);
    }
    
    // Get messages
    $msgSql = "SELECT tm.*, 
                      CONCAT(u.first_name, ' ', u.last_name) as user_name,
                      CONCAT(c.first_name, ' ', c.last_name) as customer_name
               FROM ticket_messages tm
               LEFT JOIN users u ON tm.user_id = u.id
               LEFT JOIN customers c ON tm.customer_id = c.id
               WHERE tm.ticket_id = ?
               ORDER BY tm.created_at ASC";
    
    $msgStmt = $pdo->prepare($msgSql);
    $msgStmt->execute([$id]);
    $ticket['messages'] = $msgStmt->fetchAll();
    
    sendResponse(['data' => $ticket]);
}

function createTicket($input, $pdo) {
    $sql = "INSERT INTO tickets (subject, description, customer_id, department_id, category_id, priority_id, status_id, created_by_user_id)
            VALUES (?, ?, ?, ?, ?, ?, 1, 1)";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        $input['subject'],
        $input['description'],
        $input['customerId'],
        $input['departmentId'],
        $input['categoryId'] ?? null,
        $input['priorityId']
    ]);
    
    $ticketId = $pdo->lastInsertId();
    
    // Add initial message
    $msgSql = "INSERT INTO ticket_messages (ticket_id, customer_id, message, is_internal)
               VALUES (?, ?, ?, 0)";
    
    $msgStmt = $pdo->prepare($msgSql);
    $msgStmt->execute([$ticketId, $input['customerId'], $input['description']]);
    
    sendResponse(['success' => true, 'id' => $ticketId]);
}

function updateTicket($id, $input, $pdo) {
    $sql = "UPDATE tickets SET subject = ?, description = ?, status_id = ?, priority_id = ?, assigned_user_id = ?
            WHERE id = ?";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        $input['subject'],
        $input['description'],
        $input['statusId'],
        $input['priorityId'],
        $input['assignedUserId'],
        $id
    ]);
    
    sendResponse(['success' => true]);
}

function deleteTicket($id, $pdo) {
    $sql = "DELETE FROM tickets WHERE id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$id]);
    
    sendResponse(['success' => true]);
}

// Customer handlers
function handleCustomers($segments, $method, $input, $pdo) {
    switch ($method) {
        case 'GET':
            if (isset($segments[1]) && is_numeric($segments[1])) {
                getCustomerById($segments[1], $pdo);
            } else {
                getAllCustomers($_GET, $pdo);
            }
            break;
            
        case 'POST':
            createCustomer($input, $pdo);
            break;
            
        case 'PUT':
            if (isset($segments[1]) && is_numeric($segments[1])) {
                updateCustomer($segments[1], $input, $pdo);
            }
            break;
            
        case 'DELETE':
            if (isset($segments[1]) && is_numeric($segments[1])) {
                deleteCustomer($segments[1], $pdo);
            }
            break;
    }
}

function getAllCustomers($filters, $pdo) {
    $sql = "SELECT c.*, o.name as organization_name,
                   COUNT(t.id) as total_tickets,
                   COUNT(CASE WHEN ts.is_open = 1 THEN 1 END) as open_tickets
            FROM customers c
            LEFT JOIN organizations o ON c.organization_id = o.id
            LEFT JOIN tickets t ON c.id = t.customer_id
            LEFT JOIN ticket_status ts ON t.status_id = ts.id
            GROUP BY c.id
            ORDER BY c.created_at DESC";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $customers = $stmt->fetchAll();
    
    sendResponse(['data' => $customers]);
}

function getCustomerById($id, $pdo) {
    $sql = "SELECT c.*, o.name as organization_name
            FROM customers c
            LEFT JOIN organizations o ON c.organization_id = o.id
            WHERE c.id = ?";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$id]);
    $customer = $stmt->fetch();
    
    if (!$customer) {
        sendResponse(['error' => 'Customer not found'], 404);
    }
    
    sendResponse(['data' => $customer]);
}

function createCustomer($input, $pdo) {
    $sql = "INSERT INTO customers (first_name, last_name, email, phone, organization_id)
            VALUES (?, ?, ?, ?, ?)";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        $input['firstName'],
        $input['lastName'],
        $input['email'],
        $input['phone'] ?? null,
        $input['organizationId'] ?? null
    ]);
    
    $customerId = $pdo->lastInsertId();
    sendResponse(['success' => true, 'id' => $customerId]);
}

// Configuration handlers
function handleConfig($segments, $method, $input, $pdo) {
    $configType = $segments[1] ?? '';
    
    switch ($configType) {
        case 'departments':
            handleDepartments($method, $input, $pdo);
            break;
        case 'categories':
            handleCategories($method, $input, $pdo);
            break;
        case 'priorities':
            handlePriorities($method, $input, $pdo);
            break;
        case 'statuses':
            handleStatuses($method, $input, $pdo);
            break;
        default:
            sendResponse(['error' => 'Config type not found'], 404);
    }
}

function handleDepartments($method, $input, $pdo) {
    if ($method === 'GET') {
        $sql = "SELECT * FROM departments WHERE is_active = 1 ORDER BY name";
        $stmt = $pdo->prepare($sql);
        $stmt->execute();
        $departments = $stmt->fetchAll();
        sendResponse(['data' => $departments]);
    }
}

function handleCategories($method, $input, $pdo) {
    if ($method === 'GET') {
        $sql = "SELECT c.*, d.name as department_name 
                FROM categories c 
                LEFT JOIN departments d ON c.department_id = d.id 
                WHERE c.is_active = 1 
                ORDER BY c.name";
        $stmt = $pdo->prepare($sql);
        $stmt->execute();
        $categories = $stmt->fetchAll();
        sendResponse(['data' => $categories]);
    }
}

function handlePriorities($method, $input, $pdo) {
    if ($method === 'GET') {
        $sql = "SELECT * FROM priorities WHERE is_active = 1 ORDER BY level";
        $stmt = $pdo->prepare($sql);
        $stmt->execute();
        $priorities = $stmt->fetchAll();
        sendResponse(['data' => $priorities]);
    }
}

function handleStatuses($method, $input, $pdo) {
    if ($method === 'GET') {
        $sql = "SELECT * FROM ticket_status WHERE is_active = 1 ORDER BY sort_order";
        $stmt = $pdo->prepare($sql);
        $stmt->execute();
        $statuses = $stmt->fetchAll();
        sendResponse(['data' => $statuses]);
    }
}

// Reports handlers
function handleReports($segments, $method, $input, $pdo) {
    $reportType = $segments[1] ?? '';
    
    switch ($reportType) {
        case 'dashboard':
            getDashboardStats($pdo);
            break;
        case 'tickets':
            getTicketStats($pdo);
            break;
        case 'agents':
            getAgentProductivity($pdo);
            break;
        default:
            sendResponse(['error' => 'Report type not found'], 404);
    }
}

function getDashboardStats($pdo) {
    // Get open tickets count
    $openSql = "SELECT COUNT(*) as count FROM tickets t 
                JOIN ticket_status s ON t.status_id = s.id 
                WHERE s.is_open = 1";
    $openStmt = $pdo->prepare($openSql);
    $openStmt->execute();
    $openTickets = $openStmt->fetch()['count'];
    
    // Get closed tickets count
    $closedSql = "SELECT COUNT(*) as count FROM tickets t 
                  JOIN ticket_status s ON t.status_id = s.id 
                  WHERE s.is_closed = 1";
    $closedStmt = $pdo->prepare($closedSql);
    $closedStmt->execute();
    $closedTickets = $closedStmt->fetch()['count'];
    
    // Get high priority tickets count
    $highPrioritySql = "SELECT COUNT(*) as count FROM tickets t 
                        JOIN priorities p ON t.priority_id = p.id 
                        WHERE p.level >= 3";
    $highPriorityStmt = $pdo->prepare($highPrioritySql);
    $highPriorityStmt->execute();
    $highPriorityTickets = $highPriorityStmt->fetch()['count'];
    
    // Get total customers count
    $customersSql = "SELECT COUNT(*) as count FROM customers WHERE is_active = 1";
    $customersStmt = $pdo->prepare($customersSql);
    $customersStmt->execute();
    $totalCustomers = $customersStmt->fetch()['count'];
    
    sendResponse([
        'data' => [
            'openTickets' => $openTickets,
            'closedTickets' => $closedTickets,
            'highPriorityTickets' => $highPriorityTickets,
            'totalCustomers' => $totalCustomers
        ]
    ]);
}

function getTicketStats($pdo) {
    $sql = "SELECT 
                s.name as status_name,
                s.color as status_color,
                COUNT(*) as count
            FROM tickets t
            JOIN ticket_status s ON t.status_id = s.id
            GROUP BY s.id, s.name, s.color
            ORDER BY s.sort_order";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $stats = $stmt->fetchAll();
    
    sendResponse(['data' => $stats]);
}

function getAgentProductivity($pdo) {
    $sql = "SELECT 
                CONCAT(u.first_name, ' ', u.last_name) as agent_name,
                COUNT(t.id) as total_tickets,
                COUNT(CASE WHEN s.is_closed = 1 THEN 1 END) as closed_tickets,
                COUNT(CASE WHEN s.is_open = 1 THEN 1 END) as open_tickets,
                AVG(CASE WHEN t.closed_at IS NOT NULL 
                    THEN TIMESTAMPDIFF(HOUR, t.created_at, t.closed_at) 
                    ELSE NULL END) as avg_resolution_hours
            FROM users u
            LEFT JOIN tickets t ON u.id = t.assigned_user_id
            LEFT JOIN ticket_status s ON t.status_id = s.id
            WHERE u.role IN ('agent', 'supervisor')
            GROUP BY u.id, u.first_name, u.last_name";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $productivity = $stmt->fetchAll();
    
    sendResponse(['data' => $productivity]);
}
?>