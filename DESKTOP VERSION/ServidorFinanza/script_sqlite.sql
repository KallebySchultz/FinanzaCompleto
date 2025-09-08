-- SQLite version of the database schema
-- Note: SQLite uses TEXT for VARCHAR and REAL for DECIMAL

-- Create tables
CREATE TABLE IF NOT EXISTS usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    senha_hash TEXT NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    tipo TEXT CHECK(tipo IN ('corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro')) NOT NULL,
    saldo_inicial REAL DEFAULT 0.00,
    id_usuario INTEGER NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categoria (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    tipo TEXT CHECK(tipo IN ('receita', 'despesa')) NOT NULL,
    id_usuario INTEGER NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS movimentacao (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    valor REAL NOT NULL,
    data DATE NOT NULL,
    descricao TEXT,
    tipo TEXT CHECK(tipo IN ('receita', 'despesa')) NOT NULL,
    id_conta INTEGER NOT NULL,
    id_categoria INTEGER NOT NULL,
    id_usuario INTEGER NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conta) REFERENCES conta(id) ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Insert test data
-- Test user
INSERT OR IGNORE INTO usuario (id, nome, email, senha_hash) VALUES 
(1, 'João Silva', 'joao@test.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'); -- password: hello

-- Test accounts  
INSERT OR IGNORE INTO conta (id, nome, tipo, saldo_inicial, id_usuario) VALUES 
(1, 'Nubank', 'corrente', 1000.00, 1),
(2, 'Poupança CEF', 'poupanca', 5000.00, 1),
(3, 'Cartão Visa', 'cartao', 0.00, 1);

-- Test categories
INSERT OR IGNORE INTO categoria (id, nome, tipo, id_usuario) VALUES 
(1, 'Alimentação', 'despesa', 1),
(2, 'Transporte', 'despesa', 1),
(3, 'Moradia', 'despesa', 1),
(4, 'Lazer', 'despesa', 1),
(5, 'Salário', 'receita', 1),
(6, 'Freelance', 'receita', 1),
(7, 'Investimentos', 'receita', 1);

-- Test transactions
INSERT OR IGNORE INTO movimentacao (id, valor, data, descricao, tipo, id_conta, id_categoria, id_usuario) VALUES 
(1, 250.50, '2025-09-01', 'Supermercado BH', 'despesa', 1, 1, 1),
(2, 45.00, '2025-09-02', 'Uber', 'despesa', 1, 2, 1),
(3, 3500.00, '2025-09-01', 'Salário empresa', 'receita', 1, 5, 1),
(4, 150.75, '2025-09-03', 'Padaria', 'despesa', 1, 1, 1),
(5, 800.00, '2025-09-05', 'Freelance projeto X', 'receita', 1, 6, 1),
(6, 120.00, '2025-09-04', 'Cinema', 'despesa', 1, 4, 1),
(7, 1200.00, '2025-09-06', 'Aluguel', 'despesa', 1, 3, 1),
(8, 300.00, '2025-09-07', 'Dividendos', 'receita', 2, 7, 1),
(9, 89.90, '2025-09-08', 'Restaurante', 'despesa', 1, 1, 1);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_movimentacao_data ON movimentacao(data);
CREATE INDEX IF NOT EXISTS idx_movimentacao_usuario ON movimentacao(id_usuario);
CREATE INDEX IF NOT EXISTS idx_conta_usuario ON conta(id_usuario);
CREATE INDEX IF NOT EXISTS idx_categoria_usuario ON categoria(id_usuario);