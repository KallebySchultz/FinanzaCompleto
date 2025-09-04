/**
 * Error handling middleware
 */
function errorHandler(err, req, res, next) {
    console.error('Error:', err);
    
    // Set default error status
    let status = err.status || 500;
    let message = err.message || 'Erro interno do servidor';
    
    // Handle different error types
    if (err.name === 'ValidationError') {
        status = 400;
        message = 'Dados inválidos: ' + err.message;
    } else if (err.name === 'UnauthorizedError') {
        status = 401;
        message = 'Não autorizado';
    } else if (err.code === 'ECONNREFUSED') {
        status = 503;
        message = 'Servidor Java indisponível';
    }
    
    // Send JSON response for API routes
    if (req.path.startsWith('/api/')) {
        return res.status(status).json({
            success: false,
            error: message,
            timestamp: Date.now()
        });
    }
    
    // Send error page for web routes
    res.status(status).render('error', {
        title: 'Erro - Finanza',
        page: 'error',
        error: {
            status: status,
            message: message
        }
    });
}

/**
 * 404 handler
 */
function notFoundHandler(req, res) {
    if (req.path.startsWith('/api/')) {
        return res.status(404).json({
            success: false,
            error: 'Endpoint não encontrado',
            timestamp: Date.now()
        });
    }
    
    res.status(404).render('error', {
        title: 'Página não encontrada - Finanza',
        page: 'error',
        error: {
            status: 404,
            message: 'Página não encontrada'
        }
    });
}

module.exports = errorHandler;