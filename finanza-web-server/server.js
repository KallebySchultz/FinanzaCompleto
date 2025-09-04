const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');
const http = require('http');
const socketIo = require('socket.io');

// Import routes
const apiRoutes = require('./routes/api');
const webRoutes = require('./routes/web');
const serverRoutes = require('./routes/server');

// Import middleware
const authMiddleware = require('./middleware/auth');
const errorHandler = require('./middleware/errorHandler');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

// Configuration
const PORT = process.env.PORT || 3000;
const JAVA_SERVER_HOST = process.env.JAVA_SERVER_HOST || 'localhost';
const JAVA_SERVER_PORT = process.env.JAVA_SERVER_PORT || 8080;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

// Set view engine
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Make io available to routes
app.use((req, res, next) => {
    req.io = io;
    req.javaServer = { host: JAVA_SERVER_HOST, port: JAVA_SERVER_PORT };
    next();
});

// Routes
app.use('/api', apiRoutes);
app.use('/server', serverRoutes);
app.use('/', webRoutes);

// Error handling middleware
app.use(errorHandler);

// WebSocket connection handling
io.on('connection', (socket) => {
    console.log('Cliente conectado via WebSocket:', socket.id);
    
    socket.on('join-room', (room) => {
        socket.join(room);
        console.log(`Cliente ${socket.id} entrou na sala: ${room}`);
    });
    
    socket.on('disconnect', () => {
        console.log('Cliente desconectado:', socket.id);
    });
});

// Start server
server.listen(PORT, () => {
    console.log('=================================================');
    console.log('🏦 Finanza Web Server');
    console.log('=================================================');
    console.log(`🌐 Servidor rodando na porta: ${PORT}`);
    console.log(`📱 Interface web: http://localhost:${PORT}`);
    console.log(`🔌 WebSocket: ws://localhost:${PORT}`);
    console.log(`🖧 Java Server: ${JAVA_SERVER_HOST}:${JAVA_SERVER_PORT}`);
    console.log('=================================================');
});

// Graceful shutdown
process.on('SIGTERM', () => {
    console.log('Parando servidor web...');
    server.close(() => {
        console.log('Servidor web parado com sucesso.');
        process.exit(0);
    });
});

module.exports = app;