-- Script inicial para o banco de dados Finanza Desktop
-- Criação das tabelas conforme modelo ER da documentação

CREATE DATABASE IF NOT EXISTS finanza_db;
USE finanza_db;

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de contas
CREATE TABLE IF NOT EXISTS conta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro') NOT NULL,
    saldo_inicial DECIMAL(10,2) DEFAULT 0.00,
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela de categorias
CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('receita', 'despesa') NOT NULL,
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela de movimentações
CREATE TABLE IF NOT EXISTS movimentacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(10,2) NOT NULL,
    data DATE NOT NULL,
    descricao TEXT,
    tipo ENUM('receita', 'despesa') NOT NULL,
    id_conta INT NOT NULL,
    id_categoria INT NOT NULL,
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conta) REFERENCES conta(id) ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Inserção de dados iniciais para teste

-- Categorias padrão de despesa
INSERT IGNORE INTO categoria (nome, tipo, id_usuario) VALUES 
('Alimentação', 'despesa', 1),
('Transporte', 'despesa', 1),
('Moradia', 'despesa', 1),
('Saúde', 'despesa', 1),
('Educação', 'despesa', 1),
('Lazer', 'despesa', 1);

-- Categorias padrão de receita
INSERT IGNORE INTO categoria (nome, tipo, id_usuario) VALUES 
('Salário', 'receita', 1),
('Freelance', 'receita', 1),
('Investimentos', 'receita', 1),
('Outros', 'receita', 1);

-- Índices para melhor performance
CREATE INDEX idx_movimentacao_data ON movimentacao(data);
CREATE INDEX idx_movimentacao_usuario ON movimentacao(id_usuario);
CREATE INDEX idx_conta_usuario ON conta(id_usuario);
CREATE INDEX idx_categoria_usuario ON categoria(id_usuario);