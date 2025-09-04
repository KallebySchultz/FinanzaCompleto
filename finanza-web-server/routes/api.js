const express = require('express');
const router = express.Router();
const javaServerProxy = require('../utils/javaServerProxy');
const { authenticateToken } = require('../middleware/auth');

// Health check endpoint
router.get('/ping', async (req, res) => {
    try {
        const response = await javaServerProxy.sendCommand(req.javaServer, { action: 'ping' });
        res.json(response);
    } catch (error) {
        res.status(503).json({
            action: 'ping',
            success: false,
            message: 'Servidor Java indisponível: ' + error.message,
            timestamp: Date.now()
        });
    }
});

// Authentication endpoints
router.post('/auth/login', async (req, res) => {
    try {
        const { email, senha } = req.body;
        
        if (!email || !senha) {
            return res.status(400).json({
                action: 'login',
                success: false,
                message: 'Email e senha são obrigatórios',
                timestamp: Date.now()
            });
        }
        
        const response = await javaServerProxy.sendCommand(req.javaServer, {
            action: 'login',
            email: email,
            senha: senha
        });
        
        // If login successful, you could generate JWT token here
        if (response.success) {
            // Generate JWT token (simplified)
            const token = Buffer.from(`${email}:${Date.now()}`).toString('base64');
            response.token = token;
        }
        
        res.json(response);
    } catch (error) {
        res.status(500).json({
            action: 'login',
            success: false,
            message: 'Erro interno: ' + error.message,
            timestamp: Date.now()
        });
    }
});

router.post('/auth/register', async (req, res) => {
    try {
        const { nome, email, senha } = req.body;
        
        if (!nome || !email || !senha) {
            return res.status(400).json({
                action: 'register',
                success: false,
                message: 'Nome, email e senha são obrigatórios',
                timestamp: Date.now()
            });
        }
        
        const response = await javaServerProxy.sendCommand(req.javaServer, {
            action: 'register',
            nome: nome,
            email: email,
            senha: senha
        });
        
        res.status(response.success ? 201 : 400).json(response);
    } catch (error) {
        res.status(500).json({
            action: 'register',
            success: false,
            message: 'Erro interno: ' + error.message,
            timestamp: Date.now()
        });
    }
});

// User synchronization endpoints
router.get('/sync/user/:userId', async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);
        
        if (isNaN(userId)) {
            return res.status(400).json({
                action: 'sync_user',
                success: false,
                message: 'ID do usuário inválido',
                timestamp: Date.now()
            });
        }
        
        const response = await javaServerProxy.sendCommand(req.javaServer, {
            action: 'sync_user',
            userId: userId
        });
        
        res.json(response);
    } catch (error) {
        res.status(500).json({
            action: 'sync_user',
            success: false,
            message: 'Erro interno: ' + error.message,
            timestamp: Date.now()
        });
    }
});

router.get('/sync/accounts/:userId', async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);
        
        if (isNaN(userId)) {
            return res.status(400).json({
                action: 'sync_accounts',
                success: false,
                message: 'ID do usuário inválido',
                timestamp: Date.now()
            });
        }
        
        const response = await javaServerProxy.sendCommand(req.javaServer, {
            action: 'sync_accounts',
            userId: userId
        });
        
        res.json(response);
    } catch (error) {
        res.status(500).json({
            action: 'sync_accounts',
            success: false,
            message: 'Erro interno: ' + error.message,
            timestamp: Date.now()
        });
    }
});

router.get('/sync/transactions/:userId', async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);
        
        if (isNaN(userId)) {
            return res.status(400).json({
                action: 'sync_transactions',
                success: false,
                message: 'ID do usuário inválido',
                timestamp: Date.now()
            });
        }
        
        const response = await javaServerProxy.sendCommand(req.javaServer, {
            action: 'sync_transactions',
            userId: userId
        });
        
        res.json(response);
    } catch (error) {
        res.status(500).json({
            action: 'sync_transactions',
            success: false,
            message: 'Erro interno: ' + error.message,
            timestamp: Date.now()
        });
    }
});

router.get('/sync/categories', async (req, res) => {
    try {
        const response = await javaServerProxy.sendCommand(req.javaServer, {
            action: 'sync_categories'
        });
        
        res.json(response);
    } catch (error) {
        res.status(500).json({
            action: 'sync_categories',
            success: false,
            message: 'Erro interno: ' + error.message,
            timestamp: Date.now()
        });
    }
});

// Server status endpoint
router.get('/status', async (req, res) => {
    try {
        const pingResponse = await javaServerProxy.sendCommand(req.javaServer, { action: 'ping' });
        
        res.json({
            webServer: {
                status: 'running',
                timestamp: Date.now(),
                uptime: process.uptime()
            },
            javaServer: {
                status: pingResponse.success ? 'running' : 'down',
                host: req.javaServer.host,
                port: req.javaServer.port,
                response: pingResponse
            }
        });
    } catch (error) {
        res.json({
            webServer: {
                status: 'running',
                timestamp: Date.now(),
                uptime: process.uptime()
            },
            javaServer: {
                status: 'down',
                host: req.javaServer.host,
                port: req.javaServer.port,
                error: error.message
            }
        });
    }
});

module.exports = router;