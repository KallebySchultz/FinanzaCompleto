-- ================================================
-- Criação do banco de dados e seleção
-- ================================================
CREATE DATABASE IF NOT EXISTS finanza_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE finanza_db;

-- ================================================
-- Tabela de Usuários
-- ================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_criacao BIGINT NOT NULL,
    INDEX idx_email (email)
);

-- ================================================
-- Tabela de Contas
-- ================================================
CREATE TABLE IF NOT EXISTS contas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    saldo_inicial DECIMAL(12,2) NOT NULL DEFAULT 0,
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario (usuario_id)
);

-- ================================================
-- Tabela de Categorias
-- ================================================
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cor_hex VARCHAR(10),
    tipo ENUM('receita', 'despesa') NOT NULL,
    usuario_id INT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario (usuario_id),
    INDEX idx_tipo (tipo)
);

-- ================================================
-- Tabela de Lançamentos (Transações)
-- ================================================
CREATE TABLE IF NOT EXISTS lancamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(12,2) NOT NULL,
    data BIGINT NOT NULL,
    descricao TEXT,
    conta_id INT NOT NULL,
    categoria_id INT,
    usuario_id INT NOT NULL,
    tipo ENUM('receita', 'despesa') NOT NULL,
    FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario (usuario_id),
    INDEX idx_conta (conta_id),
    INDEX idx_categoria (categoria_id),
    INDEX idx_data (data),
    INDEX idx_tipo (tipo)
);

-- ================================================
-- Usuário padrão para testes (senha: "admin")
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
-- Categorias padrão - Despesas
-- ================================================
INSERT IGNORE INTO categorias (nome, cor_hex, tipo, usuario_id) VALUES
('Alimentação', '#FF6B6B', 'despesa', NULL),
('Transporte', '#4ECDC4', 'despesa', NULL),
('Saúde', '#45B7D1', 'despesa', NULL),
('Educação', '#96CEB4', 'despesa', NULL),
('Lazer', '#FFEAA7', 'despesa', NULL),
('Casa', '#DDA0DD', 'despesa', NULL),
('Roupas', '#98D8C8', 'despesa', NULL),
('Tecnologia', '#F7DC6F', 'despesa', NULL),
('Viagem', '#BB8FCE', 'despesa', NULL),
('Outros', '#85929E', 'despesa', NULL);

-- ================================================
-- Categorias padrão - Receitas
-- ================================================
INSERT IGNORE INTO categorias (nome, cor_hex, tipo, usuario_id) VALUES
('Salário', '#2ECC71', 'receita', NULL),
('Freelance', '#3498DB', 'receita', NULL),
('Investimentos', '#9B59B6', 'receita', NULL),
('Vendas', '#E67E22', 'receita', NULL),
('Prêmios', '#F1C40F', 'receita', NULL),
('Restituição', '#1ABC9C', 'receita', NULL),
('Outros', '#34495E', 'receita', NULL);

-- ================================================
-- Lançamentos de exemplo
-- ================================================
INSERT IGNORE INTO lancamentos (valor, data, descricao, conta_id, categoria_id, usuario_id, tipo) VALUES
(3500.00, UNIX_TIMESTAMP() * 1000, 'Salário Mensal', 1, 11, 1, 'receita'),
(-150.00, UNIX_TIMESTAMP() * 1000, 'Supermercado', 1, 1, 1, 'despesa'),
(-80.00, UNIX_TIMESTAMP() * 1000, 'Combustível', 1, 2, 1, 'despesa'),
(-120.00, UNIX_TIMESTAMP() * 1000, 'Academia', 1, 4, 1, 'despesa'),
(500.00, UNIX_TIMESTAMP() * 1000, 'Freelance Web', 1, 12, 1, 'receita');

-- ================================================
-- Trigger para validação de lançamentos
-- ================================================
DELIMITER //

DROP TRIGGER IF EXISTS tr_validar_lancamento//

CREATE TRIGGER tr_validar_lancamento
BEFORE INSERT ON lancamentos
FOR EACH ROW
BEGIN
    -- Não permitir valores zerados
    IF NEW.valor = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Valor do lançamento não pode ser zero';
    END IF;

    -- Validar que receitas são positivas e despesas são negativas
    IF NEW.tipo = 'receita' AND NEW.valor < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Receitas devem ter valor positivo';
    ELSEIF NEW.tipo = 'despesa' AND NEW.valor > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Despesas devem ter valor negativo';
    END IF;
END//

DELIMITER ;

-- ================================================
-- Fim do Script
-- ================================================
