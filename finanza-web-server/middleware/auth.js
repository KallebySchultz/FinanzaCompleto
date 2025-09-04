const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET || 'finanza-secret-key-change-in-production';

/**
 * Middleware to authenticate JWT tokens
 */
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN
    
    if (!token) {
        return res.status(401).json({
            success: false,
            message: 'Token de acesso necessário',
            timestamp: Date.now()
        });
    }
    
    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) {
            return res.status(403).json({
                success: false,
                message: 'Token inválido',
                timestamp: Date.now()
            });
        }
        
        req.user = user;
        next();
    });
}

/**
 * Generate JWT token for user
 */
function generateToken(userData) {
    return jwt.sign(userData, JWT_SECRET, { expiresIn: '24h' });
}

/**
 * Middleware to check if user is logged in (for web routes)
 */
function requireLogin(req, res, next) {
    // Simple session check - in production, use proper session management
    if (!req.session || !req.session.user) {
        return res.redirect('/login');
    }
    next();
}

module.exports = {
    authenticateToken,
    generateToken,
    requireLogin
};