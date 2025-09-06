-- Finanza Database Schema for MySQL
-- Sistema de Gestão Financeira Pessoal
-- MySQL Database

-- ================================================
-- Criar banco de dados
-- ================================================
CREATE DATABASE IF NOT EXISTS finanza_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE finanza_db;

-- ================================================
-- Tabela de Usuários
-- ================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_criacao BIGINT NOT NULL
);

-- ================================================
-- Tabela de Contas
-- ================================================
CREATE TABLE IF NOT EXISTS contas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0,
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ================================================
-- Tabela de Categorias
-- ================================================
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cor_hex VARCHAR(7),
    tipo ENUM('receita', 'despesa') NOT NULL
);

-- ================================================
-- Tabela de Lançamentos (Transações)
-- ================================================
CREATE TABLE IF NOT EXISTS lancamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(15,2) NOT NULL,
    data BIGINT NOT NULL,
    descricao TEXT,
    conta_id INT NOT NULL,
    categoria_id INT,
    usuario_id INT NOT NULL,
    tipo ENUM('receita', 'despesa') NOT NULL,
    FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ================================================
-- Índices para melhor performance
-- ================================================
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_contas_usuario ON contas(usuario_id);
CREATE INDEX idx_lancamentos_usuario ON lancamentos(usuario_id);
CREATE INDEX idx_lancamentos_conta ON lancamentos(conta_id);
CREATE INDEX idx_lancamentos_categoria ON lancamentos(categoria_id);
CREATE INDEX idx_lancamentos_data ON lancamentos(data);
CREATE INDEX idx_lancamentos_tipo ON lancamentos(tipo);

-- ================================================
-- Categorias Padrão - Despesas
-- ================================================
INSERT IGNORE INTO categorias (nome, cor_hex, tipo) VALUES
('Alimentação', '#FF6B6B', 'despesa'),
('Transporte', '#4ECDC4', 'despesa'),
('Saúde', '#45B7D1', 'despesa'),
('Educação', '#96CEB4', 'despesa'),
('Lazer', '#FFEAA7', 'despesa'),
('Casa', '#DDA0DD', 'despesa'),
('Roupas', '#98D8C8', 'despesa'),
('Tecnologia', '#F7DC6F', 'despesa'),
('Viagem', '#BB8FCE', 'despesa'),
('Outros', '#85929E', 'despesa');

-- ================================================
-- Categorias Padrão - Receitas
-- ================================================
INSERT IGNORE INTO categorias (nome, cor_hex, tipo) VALUES
('Salário', '#2ECC71', 'receita'),
('Freelance', '#3498DB', 'receita'),
('Investimentos', '#9B59B6', 'receita'),
('Vendas', '#E67E22', 'receita'),
('Prêmios', '#F1C40F', 'receita'),
('Restituição', '#1ABC9C', 'receita'),
('Outros', '#34495E', 'receita');

-- ================================================
-- Usuário padrão para testes (senha: "admin")
-- NOTA: A senha será atualizada automaticamente para hash bcrypt
-- ================================================
INSERT IGNORE INTO usuarios (nome, email, senha, data_criacao) VALUES
('Administrador', 'admin@finanza.com', 'admin', UNIX_TIMESTAMP() * 1000);

-- ================================================
-- Conta padrão para o usuário de teste
-- ================================================
INSERT IGNORE INTO contas (nome, saldo_inicial, usuario_id) VALUES
('Conta Corrente', 1000.00, 1),
('Poupança', 5000.00, 1);

-- ================================================
-- Lançamentos de exemplo
-- ================================================
INSERT IGNORE INTO lancamentos (valor, data, descricao, conta_id, categoria_id, usuario_id, tipo) VALUES
(3500.00, UNIX_TIMESTAMP() * 1000, 'Salário Mensal', 1, 11, 1, 'receita'),
(-150.00, UNIX_TIMESTAMP() * 1000, 'Supermercado', 1, 1, 1, 'despesa'),
(-80.00, UNIX_TIMESTAMP() * 1000, 'Combustível', 1, 2, 1, 'despesa'),
(-120.00, UNIX_TIMESTAMP() * 1000, 'Academia', 1, 3, 1, 'despesa'),
(500.00, UNIX_TIMESTAMP() * 1000, 'Freelance Web', 1, 12, 1, 'receita');

-- ================================================
-- Views úteis para relatórios
-- ================================================

-- View para saldo atual por conta
CREATE OR REPLACE VIEW v_saldo_contas AS
SELECT 
    c.id,
    c.nome,
    c.saldo_inicial,
    c.usuario_id,
    COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as movimentacao,
    c.saldo_inicial + COALESCE(SUM(CASE WHEN l.tipo = 'receita' THEN l.valor ELSE -l.valor END), 0) as saldo_atual
FROM contas c
LEFT JOIN lancamentos l ON c.id = l.conta_id
GROUP BY c.id, c.nome, c.saldo_inicial, c.usuario_id;

-- View para resumo por categoria
CREATE OR REPLACE VIEW v_resumo_categorias AS
SELECT 
    cat.id,
    cat.nome,
    cat.cor_hex,
    cat.tipo,
    COUNT(l.id) as total_lancamentos,
    COALESCE(SUM(l.valor), 0) as total_valor
FROM categorias cat
LEFT JOIN lancamentos l ON cat.id = l.categoria_id
GROUP BY cat.id, cat.nome, cat.cor_hex, cat.tipo;

-- View para lançamentos com detalhes
CREATE OR REPLACE VIEW v_lancamentos_detalhados AS
SELECT 
    l.id,
    l.valor,
    l.data,
    l.descricao,
    l.tipo,
    c.nome as conta_nome,
    cat.nome as categoria_nome,
    cat.cor_hex as categoria_cor,
    u.nome as usuario_nome
FROM lancamentos l
JOIN contas c ON l.conta_id = c.id
JOIN usuarios u ON l.usuario_id = u.id
LEFT JOIN categorias cat ON l.categoria_id = cat.id
ORDER BY l.data DESC;