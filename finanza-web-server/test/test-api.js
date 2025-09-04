const http = require('http');

/**
 * Simple test client for Finanza Web API
 * Tests all endpoints to ensure they're working correctly
 */
class APITester {
    constructor(baseUrl = 'http://localhost:3000') {
        this.baseUrl = baseUrl;
        this.results = [];
    }

    async runTests() {
        console.log('🧪 Iniciando testes da API Finanza Web Server');
        console.log('=================================================');

        await this.testEndpoint('GET', '/api/ping', null, 'Teste de conectividade');
        await this.testEndpoint('POST', '/api/auth/login', {
            email: 'admin@finanza.com',
            senha: 'admin'
        }, 'Login de usuário');
        
        await this.testEndpoint('GET', '/api/sync/user/1', null, 'Sincronização de usuário');
        await this.testEndpoint('GET', '/api/sync/accounts/1', null, 'Sincronização de contas');
        await this.testEndpoint('GET', '/api/sync/transactions/1', null, 'Sincronização de lançamentos');
        await this.testEndpoint('GET', '/api/sync/categories', null, 'Sincronização de categorias');
        await this.testEndpoint('GET', '/api/status', null, 'Status do sistema');

        this.printResults();
    }

    async testEndpoint(method, path, data, description) {
        try {
            const response = await this.makeRequest(method, path, data);
            
            const success = response.statusCode < 400;
            this.results.push({
                method,
                path,
                description,
                status: response.statusCode,
                success,
                response: response.data
            });

            console.log(`${success ? '✅' : '❌'} ${description}: ${response.statusCode}`);
            
        } catch (error) {
            this.results.push({
                method,
                path,
                description,
                status: 'ERROR',
                success: false,
                error: error.message
            });

            console.log(`❌ ${description}: ERROR - ${error.message}`);
        }
    }

    makeRequest(method, path, data) {
        return new Promise((resolve, reject) => {
            const url = new URL(this.baseUrl + path);
            
            const options = {
                hostname: url.hostname,
                port: url.port || 80,
                path: url.pathname + url.search,
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                }
            };

            const req = http.request(options, (res) => {
                let responseData = '';
                
                res.on('data', (chunk) => {
                    responseData += chunk;
                });
                
                res.on('end', () => {
                    try {
                        const jsonData = responseData ? JSON.parse(responseData) : {};
                        resolve({
                            statusCode: res.statusCode,
                            data: jsonData
                        });
                    } catch (e) {
                        resolve({
                            statusCode: res.statusCode,
                            data: responseData
                        });
                    }
                });
            });

            req.on('error', (error) => {
                reject(error);
            });

            if (data && method !== 'GET') {
                req.write(JSON.stringify(data));
            }

            req.end();
        });
    }

    printResults() {
        console.log('\n=================================================');
        console.log('📊 Resumo dos Testes');
        console.log('=================================================');

        const successful = this.results.filter(r => r.success).length;
        const total = this.results.length;

        console.log(`Total de testes: ${total}`);
        console.log(`Sucessos: ${successful}`);
        console.log(`Falhas: ${total - successful}`);
        console.log(`Taxa de sucesso: ${Math.round((successful / total) * 100)}%`);

        console.log('\n📋 Detalhes:');
        this.results.forEach(result => {
            console.log(`${result.success ? '✅' : '❌'} ${result.method} ${result.path} - ${result.status}`);
        });

        console.log('\n=================================================');
        
        if (successful === total) {
            console.log('🎉 Todos os testes passaram! API funcionando perfeitamente.');
        } else {
            console.log('⚠️  Alguns testes falharam. Verifique a conectividade.');
        }
    }
}

// Executar testes se chamado diretamente
if (require.main === module) {
    const tester = new APITester();
    tester.runTests().catch(console.error);
}

module.exports = APITester;