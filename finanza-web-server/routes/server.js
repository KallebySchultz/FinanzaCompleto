const express = require('express');
const router = express.Router();
const javaServerProxy = require('../utils/javaServerProxy');

// Server management dashboard
router.get('/', (req, res) => {
    res.render('server/dashboard', {
        title: 'Servidor - Finanza',
        page: 'server'
    });
});

// Server status page
router.get('/status', (req, res) => {
    res.render('server/status', {
        title: 'Status do Servidor - Finanza',
        page: 'server-status'
    });
});

// Server logs page
router.get('/logs', (req, res) => {
    res.render('server/logs', {
        title: 'Logs do Servidor - Finanza',
        page: 'server-logs'
    });
});

// API endpoint for real-time server status
router.get('/api/status', async (req, res) => {
    try {
        const javaStatus = await javaServerProxy.getServerStatus(req.javaServer);
        
        res.json({
            timestamp: Date.now(),
            webServer: {
                status: 'running',
                uptime: process.uptime(),
                memory: process.memoryUsage(),
                pid: process.pid
            },
            javaServer: {
                host: req.javaServer.host,
                port: req.javaServer.port,
                ...javaStatus
            }
        });
    } catch (error) {
        res.status(500).json({
            error: 'Erro ao obter status do servidor',
            message: error.message
        });
    }
});

// API endpoint for testing Java server connection
router.post('/api/test-connection', async (req, res) => {
    try {
        const { host, port } = req.body;
        const serverConfig = {
            host: host || req.javaServer.host,
            port: parseInt(port) || req.javaServer.port
        };
        
        const result = await javaServerProxy.testConnection(serverConfig);
        res.json(result);
    } catch (error) {
        res.status(500).json({
            connected: false,
            error: error.message
        });
    }
});

module.exports = router;