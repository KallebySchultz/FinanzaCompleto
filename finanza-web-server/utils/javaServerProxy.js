const net = require('net');

/**
 * Java Server Proxy - Communicates with the existing Java TCP server
 * Provides a bridge between HTTP requests and TCP socket communication
 */
class JavaServerProxy {
    
    /**
     * Send command to Java server and get response
     * @param {Object} serverConfig - Server configuration {host, port}
     * @param {Object} command - Command object to send
     * @returns {Promise} Promise that resolves with server response
     */
    static sendCommand(serverConfig, command) {
        return new Promise((resolve, reject) => {
            const socket = new net.Socket();
            let responseData = '';
            
            // Set timeout
            socket.setTimeout(5000);
            
            socket.connect(serverConfig.port, serverConfig.host, () => {
                console.log(`Conectado ao servidor Java ${serverConfig.host}:${serverConfig.port}`);
                
                // Send command as JSON
                const commandJson = JSON.stringify(command);
                socket.write(commandJson + '\n');
            });
            
            socket.on('data', (data) => {
                responseData += data.toString();
                
                // Check if we have a complete response (assuming one line response)
                if (responseData.includes('\n')) {
                    const lines = responseData.split('\n');
                    for (let line of lines) {
                        if (line.trim()) {
                            try {
                                // Skip welcome message
                                if (line.includes('Conectado ao Finanza Server')) {
                                    continue;
                                }
                                
                                const response = JSON.parse(line.trim());
                                socket.destroy();
                                resolve(response);
                                return;
                            } catch (e) {
                                // If not JSON, might be welcome message, continue reading
                                continue;
                            }
                        }
                    }
                }
            });
            
            socket.on('timeout', () => {
                socket.destroy();
                reject(new Error('Timeout na conexão com servidor Java'));
            });
            
            socket.on('error', (error) => {
                reject(new Error(`Erro de conexão: ${error.message}`));
            });
            
            socket.on('close', () => {
                if (!responseData.trim()) {
                    reject(new Error('Conexão fechada sem resposta'));
                }
            });
        });
    }
    
    /**
     * Test connection to Java server
     * @param {Object} serverConfig - Server configuration {host, port}
     * @returns {Promise} Promise that resolves with connection status
     */
    static testConnection(serverConfig) {
        return this.sendCommand(serverConfig, { action: 'ping' })
            .then(response => ({
                connected: true,
                response: response
            }))
            .catch(error => ({
                connected: false,
                error: error.message
            }));
    }
    
    /**
     * Get server status
     * @param {Object} serverConfig - Server configuration {host, port}
     * @returns {Promise} Promise that resolves with server status
     */
    static getServerStatus(serverConfig) {
        const startTime = Date.now();
        
        return this.testConnection(serverConfig)
            .then(result => ({
                ...result,
                responseTime: Date.now() - startTime,
                timestamp: Date.now()
            }));
    }
}

module.exports = JavaServerProxy;