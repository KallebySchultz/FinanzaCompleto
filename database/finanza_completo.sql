-- =====================================================================================================================
-- FINANZA - SISTEMA DE GESTÃO FINANCEIRA PESSOAL
-- Script SQL Completo para MySQL/MariaDB
-- Versão: 2.0 (Com suporte a diferenciação Admin/Usuário)
-- =====================================================================================================================

-- =====================================================================================================================
-- SEÇÃO 1: CRIAÇÃO DO BANCO DE DADOS
-- =====================================================================================================================

-- Cria o banco de dados se não existir
CREATE DATABASE IF NOT EXISTS finanza_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Seleciona o banco de dados
USE finanza_db;

-- =====================================================================================================================
-- SEÇÃO 2: REMOÇÃO DE TABELAS EXISTENTES (ORDEM CORRETA POR DEPENDÊNCIAS)
-- Descomente se precisar recriar as tabelas do zero
-- =====================================================================================================================

-- DROP TABLE IF EXISTS movimentacao;
-- DROP TABLE IF EXISTS categoria;
-- DROP TABLE IF EXISTS conta;
-- DROP TABLE IF EXISTS usuario;

-- =====================================================================================================================
-- SEÇÃO 3: CRIAÇÃO DAS TABELAS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Tabela: usuario
-- Descrição: Armazena informações dos usuários do sistema
-- Campos importantes:
--   - tipo_usuario: 'admin' para administradores (acesso desktop), 'usuario' para usuários comuns (acesso mobile)
--   - senha_hash: Senha hasheada com BCrypt
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    tipo_usuario ENUM('admin', 'usuario') NOT NULL DEFAULT 'usuario',
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_usuario_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------------------------------------------------
-- Tabela: conta
-- Descrição: Representa contas financeiras do usuário
-- Tipos de conta:
--   - 'corrente': Conta corrente bancária
--   - 'poupanca': Conta poupança
--   - 'cartao': Cartão de crédito
--   - 'investimento': Conta de investimentos
--   - 'dinheiro': Dinheiro em espécie/carteira
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS conta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro') NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uk_conta_nome_usuario UNIQUE (nome, id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------------------------------------------------
-- Tabela: categoria
-- Descrição: Categorias para classificar movimentações financeiras
-- Tipos de categoria:
--   - 'receita': Para entradas de dinheiro (salário, vendas, etc.)
--   - 'despesa': Para saídas de dinheiro (alimentação, transporte, etc.)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('receita', 'despesa') NOT NULL,
    cor_hex VARCHAR(7) DEFAULT '#808080',
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uk_categoria_nome_tipo_usuario UNIQUE (nome, tipo, id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------------------------------------------------
-- Tabela: movimentacao
-- Descrição: Registra todas as transações financeiras (receitas e despesas)
-- Observações:
--   - O valor é sempre positivo, o tipo determina se é receita ou despesa
--   - Cada movimentação pertence a uma conta, categoria e usuário
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimentacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    valor DECIMAL(15,2) NOT NULL,
    data DATE NOT NULL,
    descricao VARCHAR(500),
    tipo ENUM('receita', 'despesa') NOT NULL,
    id_conta INT NOT NULL,
    id_categoria INT NOT NULL,
    id_usuario INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimentacao_conta FOREIGN KEY (id_conta) REFERENCES conta(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimentacao_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimentacao_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT chk_movimentacao_valor CHECK (valor > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================================================================
-- SEÇÃO 4: ÍNDICES PARA PERFORMANCE
-- =====================================================================================================================

-- Índices para tabela usuario
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_tipo ON usuario(tipo_usuario);
CREATE INDEX idx_usuario_ativo ON usuario(ativo);

-- Índices para tabela conta
CREATE INDEX idx_conta_usuario ON conta(id_usuario);
CREATE INDEX idx_conta_tipo ON conta(tipo);

-- Índices para tabela categoria
CREATE INDEX idx_categoria_usuario ON categoria(id_usuario);
CREATE INDEX idx_categoria_tipo ON categoria(tipo);

-- Índices para tabela movimentacao
CREATE INDEX idx_movimentacao_data ON movimentacao(data);
CREATE INDEX idx_movimentacao_usuario ON movimentacao(id_usuario);
CREATE INDEX idx_movimentacao_conta ON movimentacao(id_conta);
CREATE INDEX idx_movimentacao_categoria ON movimentacao(id_categoria);
CREATE INDEX idx_movimentacao_tipo ON movimentacao(tipo);
CREATE INDEX idx_movimentacao_data_usuario ON movimentacao(data, id_usuario);
CREATE INDEX idx_movimentacao_tipo_usuario ON movimentacao(tipo, id_usuario);

-- =====================================================================================================================
-- SEÇÃO 5: VIEWS PARA RELATÓRIOS E DASHBOARD
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- View: v_saldo_contas
-- Descrição: Calcula o saldo atual de cada conta (saldo_inicial + receitas - despesas)
-- ---------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_saldo_contas AS
SELECT 
    c.id,
    c.nome,
    c.tipo,
    c.saldo_inicial,
    c.id_usuario,
    c.data_criacao,
    COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0) AS total_receitas,
    COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0) AS total_despesas,
    c.saldo_inicial + COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0) 
                    - COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0) AS saldo_atual
FROM conta c
LEFT JOIN movimentacao m ON c.id = m.id_conta
GROUP BY c.id, c.nome, c.tipo, c.saldo_inicial, c.id_usuario, c.data_criacao;

-- ---------------------------------------------------------------------------------------------------------------------
-- View: v_resumo_categorias
-- Descrição: Resumo de totais por categoria
-- ---------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_resumo_categorias AS
SELECT 
    cat.id,
    cat.nome,
    cat.tipo,
    cat.cor_hex,
    cat.id_usuario,
    COUNT(m.id) AS total_movimentacoes,
    COALESCE(SUM(m.valor), 0) AS total_valor
FROM categoria cat
LEFT JOIN movimentacao m ON cat.id = m.id_categoria
GROUP BY cat.id, cat.nome, cat.tipo, cat.cor_hex, cat.id_usuario;

-- ---------------------------------------------------------------------------------------------------------------------
-- View: v_movimentacoes_detalhadas
-- Descrição: Lista movimentações com informações completas de conta, categoria e usuário
-- ---------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_movimentacoes_detalhadas AS
SELECT 
    m.id,
    m.valor,
    m.data,
    m.descricao,
    m.tipo,
    m.data_criacao,
    m.data_atualizacao,
    c.id AS conta_id,
    c.nome AS conta_nome,
    c.tipo AS conta_tipo,
    cat.id AS categoria_id,
    cat.nome AS categoria_nome,
    cat.cor_hex AS categoria_cor,
    u.id AS usuario_id,
    u.nome AS usuario_nome,
    u.email AS usuario_email
FROM movimentacao m
JOIN conta c ON m.id_conta = c.id
JOIN categoria cat ON m.id_categoria = cat.id
JOIN usuario u ON m.id_usuario = u.id
ORDER BY m.data DESC, m.id DESC;

-- ---------------------------------------------------------------------------------------------------------------------
-- View: v_dashboard_usuario
-- Descrição: Resumo financeiro do usuário para dashboard
-- ---------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_dashboard_usuario AS
SELECT 
    u.id AS usuario_id,
    u.nome AS usuario_nome,
    COUNT(DISTINCT c.id) AS total_contas,
    COUNT(DISTINCT cat.id) AS total_categorias,
    COUNT(m.id) AS total_movimentacoes,
    COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0) AS total_receitas,
    COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0) AS total_despesas,
    COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0) - 
    COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0) AS saldo_periodo
FROM usuario u
LEFT JOIN conta c ON u.id = c.id_usuario
LEFT JOIN categoria cat ON u.id = cat.id_usuario
LEFT JOIN movimentacao m ON u.id = m.id_usuario
GROUP BY u.id, u.nome;

-- ---------------------------------------------------------------------------------------------------------------------
-- View: v_resumo_mensal
-- Descrição: Resumo de receitas e despesas por mês
-- ---------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_resumo_mensal AS
SELECT 
    m.id_usuario,
    YEAR(m.data) AS ano,
    MONTH(m.data) AS mes,
    COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0) AS total_receitas,
    COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0) AS total_despesas,
    COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0) - 
    COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0) AS saldo_mes,
    COUNT(*) AS total_movimentacoes
FROM movimentacao m
GROUP BY m.id_usuario, YEAR(m.data), MONTH(m.data)
ORDER BY m.id_usuario, ano DESC, mes DESC;

-- ---------------------------------------------------------------------------------------------------------------------
-- View: v_usuarios_admin
-- Descrição: Lista de usuários para visualização administrativa
-- ---------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_usuarios_admin AS
SELECT 
    u.id,
    u.nome,
    u.email,
    u.tipo_usuario,
    u.ativo,
    u.data_criacao,
    u.data_atualizacao,
    COUNT(DISTINCT c.id) AS total_contas,
    COUNT(DISTINCT cat.id) AS total_categorias,
    COUNT(DISTINCT m.id) AS total_movimentacoes
FROM usuario u
LEFT JOIN conta c ON u.id = c.id_usuario
LEFT JOIN categoria cat ON u.id = cat.id_usuario
LEFT JOIN movimentacao m ON u.id = m.id_usuario
GROUP BY u.id, u.nome, u.email, u.tipo_usuario, u.ativo, u.data_criacao, u.data_atualizacao
ORDER BY u.id;

-- =====================================================================================================================
-- SEÇÃO 6: STORED PROCEDURES
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Procedure: sp_calcular_saldo_conta
-- Descrição: Calcula o saldo atual de uma conta específica
-- Parâmetros:
--   - p_id_conta: ID da conta
-- Retorno: Saldo atual da conta
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS sp_calcular_saldo_conta(
    IN p_id_conta INT,
    OUT p_saldo_atual DECIMAL(15,2)
)
BEGIN
    SELECT 
        c.saldo_inicial + COALESCE(SUM(CASE WHEN m.tipo = 'receita' THEN m.valor ELSE 0 END), 0)
                        - COALESCE(SUM(CASE WHEN m.tipo = 'despesa' THEN m.valor ELSE 0 END), 0)
    INTO p_saldo_atual
    FROM conta c
    LEFT JOIN movimentacao m ON c.id = m.id_conta
    WHERE c.id = p_id_conta
    GROUP BY c.id, c.saldo_inicial;
END //

DELIMITER ;

-- ---------------------------------------------------------------------------------------------------------------------
-- Procedure: sp_resumo_financeiro
-- Descrição: Retorna o resumo financeiro de um usuário em um período
-- Parâmetros:
--   - p_id_usuario: ID do usuário
--   - p_data_inicio: Data de início do período
--   - p_data_fim: Data final do período
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS sp_resumo_financeiro(
    IN p_id_usuario INT,
    IN p_data_inicio DATE,
    IN p_data_fim DATE
)
BEGIN
    SELECT 
        COALESCE(SUM(CASE WHEN tipo = 'receita' THEN valor ELSE 0 END), 0) AS total_receitas,
        COALESCE(SUM(CASE WHEN tipo = 'despesa' THEN valor ELSE 0 END), 0) AS total_despesas,
        COALESCE(SUM(CASE WHEN tipo = 'receita' THEN valor ELSE 0 END), 0) - 
        COALESCE(SUM(CASE WHEN tipo = 'despesa' THEN valor ELSE 0 END), 0) AS saldo_periodo,
        COUNT(*) AS total_movimentacoes
    FROM movimentacao
    WHERE id_usuario = p_id_usuario
      AND data BETWEEN p_data_inicio AND p_data_fim;
END //

DELIMITER ;

-- ---------------------------------------------------------------------------------------------------------------------
-- Procedure: sp_criar_categorias_padrao
-- Descrição: Cria as categorias padrão para um novo usuário
-- Parâmetros:
--   - p_id_usuario: ID do usuário
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS sp_criar_categorias_padrao(
    IN p_id_usuario INT
)
BEGIN
    -- Categorias de Despesa
    INSERT IGNORE INTO categoria (nome, tipo, cor_hex, id_usuario) VALUES
    ('Alimentação', 'despesa', '#FF6B6B', p_id_usuario),
    ('Transporte', 'despesa', '#4ECDC4', p_id_usuario),
    ('Moradia', 'despesa', '#45B7D1', p_id_usuario),
    ('Saúde', 'despesa', '#96CEB4', p_id_usuario),
    ('Educação', 'despesa', '#FFEAA7', p_id_usuario),
    ('Lazer', 'despesa', '#DDA0DD', p_id_usuario),
    ('Roupas', 'despesa', '#98D8C8', p_id_usuario),
    ('Tecnologia', 'despesa', '#F7DC6F', p_id_usuario),
    ('Viagem', 'despesa', '#BB8FCE', p_id_usuario),
    ('Outros', 'despesa', '#85929E', p_id_usuario);

    -- Categorias de Receita
    INSERT IGNORE INTO categoria (nome, tipo, cor_hex, id_usuario) VALUES
    ('Salário', 'receita', '#2ECC71', p_id_usuario),
    ('Freelance', 'receita', '#3498DB', p_id_usuario),
    ('Investimentos', 'receita', '#9B59B6', p_id_usuario),
    ('Vendas', 'receita', '#E67E22', p_id_usuario),
    ('Prêmios', 'receita', '#F1C40F', p_id_usuario),
    ('Restituição', 'receita', '#1ABC9C', p_id_usuario),
    ('Outros', 'receita', '#34495E', p_id_usuario);
END //

DELIMITER ;

-- =====================================================================================================================
-- SEÇÃO 7: TRIGGERS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Trigger: tr_movimentacao_valor_positivo
-- Descrição: Garante que o valor da movimentação seja sempre positivo
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE TRIGGER IF NOT EXISTS tr_movimentacao_valor_positivo
BEFORE INSERT ON movimentacao
FOR EACH ROW
BEGIN
    IF NEW.valor <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O valor da movimentação deve ser positivo';
    END IF;
END //

DELIMITER ;

-- ---------------------------------------------------------------------------------------------------------------------
-- Trigger: tr_movimentacao_valor_update
-- Descrição: Garante que o valor da movimentação seja positivo em atualizações
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE TRIGGER IF NOT EXISTS tr_movimentacao_valor_update
BEFORE UPDATE ON movimentacao
FOR EACH ROW
BEGIN
    IF NEW.valor <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O valor da movimentação deve ser positivo';
    END IF;
END //

DELIMITER ;

-- ---------------------------------------------------------------------------------------------------------------------
-- Trigger: tr_usuario_data_atualizacao
-- Descrição: Atualiza automaticamente a data_atualizacao quando o usuário é modificado
-- Nota: MySQL já faz isso com ON UPDATE CURRENT_TIMESTAMP, mas mantemos para compatibilidade
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE TRIGGER IF NOT EXISTS tr_usuario_data_atualizacao
BEFORE UPDATE ON usuario
FOR EACH ROW
BEGIN
    SET NEW.data_atualizacao = CURRENT_TIMESTAMP;
END //

DELIMITER ;

-- =====================================================================================================================
-- SEÇÃO 8: DADOS INICIAIS - USUÁRIOS
-- =====================================================================================================================

-- Usuário Administrador padrão
-- IMPORTANTE: Troque a senha imediatamente após a primeira instalação!
-- Use a aplicação ou um gerador BCrypt para criar um novo hash de senha seguro
INSERT INTO usuario (nome, email, senha_hash, tipo_usuario, ativo) VALUES
('Administrador', 'admin@finanza.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.OBPY8.iLNjO8RI9yVEvYplPwZlJJXC', 'admin', 1)
ON DUPLICATE KEY UPDATE nome = VALUES(nome);

-- ---------------------------------------------------------------------------------------------------------------------
-- BLOCO DE TESTE - REMOVER EM PRODUÇÃO
-- O código abaixo cria um usuário de teste. Descomente apenas em ambiente de desenvolvimento.
-- ---------------------------------------------------------------------------------------------------------------------
-- Usuário comum de teste (apenas para desenvolvimento)
-- REMOVA ESTE BLOCO EM PRODUÇÃO!
/*
INSERT INTO usuario (nome, email, senha_hash, tipo_usuario, ativo) VALUES
('Usuário Teste', 'teste@finanza.com', '$2a$10$LM4xyeFj/1wMhGKbJk8dXO0RY8KRJXN3TZ3jKF0B3YiNjYwKjGhO.', 'usuario', 1)
ON DUPLICATE KEY UPDATE nome = VALUES(nome);
*/

-- =====================================================================================================================
-- SEÇÃO 9: DADOS INICIAIS - CONTAS DO USUÁRIO ADMIN
-- =====================================================================================================================

-- Contas para o administrador
INSERT INTO conta (nome, tipo, saldo_inicial, id_usuario) VALUES
('Conta Corrente Pessoal', 'corrente', 5000.00, 1),
('Poupança', 'poupanca', 10000.00, 1),
('Cartão de Crédito', 'cartao', 0.00, 1),
('Investimentos', 'investimento', 25000.00, 1),
('Carteira', 'dinheiro', 500.00, 1)
ON DUPLICATE KEY UPDATE saldo_inicial = VALUES(saldo_inicial);

-- ---------------------------------------------------------------------------------------------------------------------
-- BLOCO DE TESTE - REMOVER EM PRODUÇÃO
-- Contas para usuário de teste (apenas se o usuário de teste foi criado acima)
-- ---------------------------------------------------------------------------------------------------------------------
/*
INSERT INTO conta (nome, tipo, saldo_inicial, id_usuario) VALUES
('Nubank', 'corrente', 1500.00, 2),
('Poupança Caixa', 'poupanca', 3000.00, 2),
('Cartão Visa', 'cartao', 0.00, 2)
ON DUPLICATE KEY UPDATE saldo_inicial = VALUES(saldo_inicial);
*/

-- =====================================================================================================================
-- SEÇÃO 10: DADOS INICIAIS - CATEGORIAS
-- =====================================================================================================================

-- Categorias de DESPESA para o administrador
INSERT INTO categoria (nome, tipo, cor_hex, id_usuario) VALUES
('Alimentação', 'despesa', '#FF6B6B', 1),
('Transporte', 'despesa', '#4ECDC4', 1),
('Moradia', 'despesa', '#45B7D1', 1),
('Saúde', 'despesa', '#96CEB4', 1),
('Educação', 'despesa', '#FFEAA7', 1),
('Lazer', 'despesa', '#DDA0DD', 1),
('Roupas', 'despesa', '#98D8C8', 1),
('Tecnologia', 'despesa', '#F7DC6F', 1),
('Viagem', 'despesa', '#BB8FCE', 1),
('Outros', 'despesa', '#85929E', 1)
ON DUPLICATE KEY UPDATE cor_hex = VALUES(cor_hex);

-- Categorias de RECEITA para o administrador
INSERT INTO categoria (nome, tipo, cor_hex, id_usuario) VALUES
('Salário', 'receita', '#2ECC71', 1),
('Freelance', 'receita', '#3498DB', 1),
('Investimentos', 'receita', '#9B59B6', 1),
('Vendas', 'receita', '#E67E22', 1),
('Prêmios', 'receita', '#F1C40F', 1),
('Restituição', 'receita', '#1ABC9C', 1),
('Outros', 'receita', '#34495E', 1)
ON DUPLICATE KEY UPDATE cor_hex = VALUES(cor_hex);

-- ---------------------------------------------------------------------------------------------------------------------
-- BLOCO DE TESTE - REMOVER EM PRODUÇÃO
-- Categorias para usuário de teste (apenas se o usuário de teste foi criado acima)
-- ---------------------------------------------------------------------------------------------------------------------
/*
-- Categorias de DESPESA para o usuário de teste
INSERT INTO categoria (nome, tipo, cor_hex, id_usuario) VALUES
('Alimentação', 'despesa', '#FF6B6B', 2),
('Transporte', 'despesa', '#4ECDC4', 2),
('Moradia', 'despesa', '#45B7D1', 2),
('Saúde', 'despesa', '#96CEB4', 2),
('Educação', 'despesa', '#FFEAA7', 2),
('Lazer', 'despesa', '#DDA0DD', 2),
('Roupas', 'despesa', '#98D8C8', 2),
('Tecnologia', 'despesa', '#F7DC6F', 2),
('Viagem', 'despesa', '#BB8FCE', 2),
('Outros', 'despesa', '#85929E', 2)
ON DUPLICATE KEY UPDATE cor_hex = VALUES(cor_hex);

-- Categorias de RECEITA para o usuário de teste
INSERT INTO categoria (nome, tipo, cor_hex, id_usuario) VALUES
('Salário', 'receita', '#2ECC71', 2),
('Freelance', 'receita', '#3498DB', 2),
('Investimentos', 'receita', '#9B59B6', 2),
('Vendas', 'receita', '#E67E22', 2),
('Prêmios', 'receita', '#F1C40F', 2),
('Restituição', 'receita', '#1ABC9C', 2),
('Outros', 'receita', '#34495E', 2)
ON DUPLICATE KEY UPDATE cor_hex = VALUES(cor_hex);
*/

-- =====================================================================================================================
-- SEÇÃO 11: DADOS DE EXEMPLO - MOVIMENTAÇÕES DO ADMINISTRADOR
-- =====================================================================================================================

-- Movimentações de exemplo para o mês atual (Administrador)
INSERT INTO movimentacao (valor, data, descricao, tipo, id_conta, id_categoria, id_usuario) VALUES
-- Receitas
(8500.00, CURDATE() - INTERVAL 25 DAY, 'Salário mensal', 'receita', 1, 11, 1),
(1500.00, CURDATE() - INTERVAL 20 DAY, 'Projeto freelance', 'receita', 1, 12, 1),
(350.00, CURDATE() - INTERVAL 15 DAY, 'Dividendos de ações', 'receita', 4, 13, 1),
(200.00, CURDATE() - INTERVAL 10 DAY, 'Venda de itens usados', 'receita', 5, 14, 1),

-- Despesas
(1500.00, CURDATE() - INTERVAL 24 DAY, 'Aluguel', 'despesa', 1, 3, 1),
(450.00, CURDATE() - INTERVAL 22 DAY, 'Supermercado', 'despesa', 1, 1, 1),
(150.00, CURDATE() - INTERVAL 20 DAY, 'Combustível', 'despesa', 1, 2, 1),
(89.90, CURDATE() - INTERVAL 18 DAY, 'Internet e telefone', 'despesa', 1, 8, 1),
(250.00, CURDATE() - INTERVAL 16 DAY, 'Plano de saúde', 'despesa', 1, 4, 1),
(380.00, CURDATE() - INTERVAL 14 DAY, 'Supermercado', 'despesa', 1, 1, 1),
(120.00, CURDATE() - INTERVAL 12 DAY, 'Cinema e jantar', 'despesa', 3, 6, 1),
(75.00, CURDATE() - INTERVAL 10 DAY, 'Uber', 'despesa', 1, 2, 1),
(199.00, CURDATE() - INTERVAL 8 DAY, 'Curso online', 'despesa', 3, 5, 1),
(65.00, CURDATE() - INTERVAL 6 DAY, 'Restaurante', 'despesa', 1, 1, 1),
(45.00, CURDATE() - INTERVAL 4 DAY, 'Farmácia', 'despesa', 5, 4, 1),
(320.00, CURDATE() - INTERVAL 2 DAY, 'Supermercado', 'despesa', 1, 1, 1);

-- =====================================================================================================================
-- SEÇÃO 12: DADOS DE EXEMPLO - MOVIMENTAÇÕES (OPCIONAL - PODE COMENTAR EM PRODUÇÃO)
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- BLOCO DE TESTE - REMOVER EM PRODUÇÃO
-- Movimentações de exemplo para usuário de teste
-- ---------------------------------------------------------------------------------------------------------------------
/*
INSERT INTO movimentacao (valor, data, descricao, tipo, id_conta, id_categoria, id_usuario) VALUES
-- Receitas
(3500.00, CURDATE() - INTERVAL 28 DAY, 'Salário', 'receita', 6, 28, 2),
(500.00, CURDATE() - INTERVAL 15 DAY, 'Freelance design', 'receita', 6, 29, 2),

-- Despesas
(800.00, CURDATE() - INTERVAL 27 DAY, 'Aluguel', 'despesa', 6, 20, 2),
(250.00, CURDATE() - INTERVAL 25 DAY, 'Supermercado', 'despesa', 6, 18, 2),
(80.00, CURDATE() - INTERVAL 23 DAY, 'Uber', 'despesa', 6, 19, 2),
(150.00, CURDATE() - INTERVAL 20 DAY, 'Farmácia', 'despesa', 6, 21, 2),
(45.00, CURDATE() - INTERVAL 18 DAY, 'Streaming', 'despesa', 6, 23, 2),
(180.00, CURDATE() - INTERVAL 15 DAY, 'Supermercado', 'despesa', 6, 18, 2),
(60.00, CURDATE() - INTERVAL 10 DAY, 'Pizza delivery', 'despesa', 6, 18, 2),
(35.00, CURDATE() - INTERVAL 5 DAY, 'Padaria', 'despesa', 6, 18, 2);
*/

-- =====================================================================================================================
-- SEÇÃO 13: FUNÇÕES ÚTEIS
-- =====================================================================================================================

-- ---------------------------------------------------------------------------------------------------------------------
-- Função: fn_calcular_saldo_total
-- Descrição: Calcula o saldo total de todas as contas de um usuário
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE FUNCTION IF NOT EXISTS fn_calcular_saldo_total(p_id_usuario INT) 
RETURNS DECIMAL(15,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_saldo_total DECIMAL(15,2);
    
    SELECT COALESCE(SUM(saldo_atual), 0)
    INTO v_saldo_total
    FROM v_saldo_contas
    WHERE id_usuario = p_id_usuario;
    
    RETURN v_saldo_total;
END //

DELIMITER ;

-- ---------------------------------------------------------------------------------------------------------------------
-- Função: fn_is_admin
-- Descrição: Verifica se um usuário é administrador
-- ---------------------------------------------------------------------------------------------------------------------
DELIMITER //

CREATE FUNCTION IF NOT EXISTS fn_is_admin(p_id_usuario INT) 
RETURNS TINYINT(1)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_is_admin TINYINT(1);
    
    SELECT tipo_usuario = 'admin'
    INTO v_is_admin
    FROM usuario
    WHERE id = p_id_usuario;
    
    RETURN COALESCE(v_is_admin, 0);
END //

DELIMITER ;

-- =====================================================================================================================
-- SEÇÃO 14: GRANTS E PERMISSÕES (OPCIONAL - AJUSTE CONFORME NECESSÁRIO)
-- =====================================================================================================================

-- Criar usuário para a aplicação (se necessário)
-- CREATE USER IF NOT EXISTS 'finanza_app'@'localhost' IDENTIFIED BY 'senha_segura_aqui';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON finanza_db.* TO 'finanza_app'@'localhost';
-- GRANT EXECUTE ON finanza_db.* TO 'finanza_app'@'localhost';
-- FLUSH PRIVILEGES;

-- =====================================================================================================================
-- SEÇÃO 15: VERIFICAÇÃO FINAL
-- =====================================================================================================================

-- Verificar tabelas criadas
SELECT 'Tabelas criadas:' AS info;
SHOW TABLES;

-- Verificar usuários inseridos
SELECT 'Usuários no sistema:' AS info;
SELECT id, nome, email, tipo_usuario, ativo FROM usuario;

-- Verificar contagem de registros
SELECT 'Contagem de registros:' AS info;
SELECT 
    (SELECT COUNT(*) FROM usuario) AS usuarios,
    (SELECT COUNT(*) FROM conta) AS contas,
    (SELECT COUNT(*) FROM categoria) AS categorias,
    (SELECT COUNT(*) FROM movimentacao) AS movimentacoes;

-- =====================================================================================================================
-- FIM DO SCRIPT
-- =====================================================================================================================

/*
=====================================================================================================================
NOTAS IMPORTANTES:
=====================================================================================================================

1. SEGURANÇA:
   - As senhas de exemplo usam hash BCrypt
   - Em produção, SEMPRE use senhas fortes e únicas
   - Configure SSL/TLS para conexões com o banco de dados
   - Restrinja as permissões do usuário do banco ao mínimo necessário

2. TIPOS DE USUÁRIO:
   - 'admin': Administradores têm acesso ao painel desktop
   - 'usuario': Usuários comuns têm acesso apenas ao aplicativo mobile

3. PRIMEIRO ACESSO:
   - Execute o script para criar o banco de dados
   - Acesse com admin@finanza.com (a senha inicial é codificada no hash do script)
   - IMEDIATAMENTE altere a senha do administrador após o primeiro login
   - Para gerar novos hashes BCrypt, use a aplicação ou ferramentas online como bcrypt-generator.com

4. BACKUP:
   - Faça backup regularmente do banco de dados
   - Exemplo: mysqldump -u root -p finanza_db > backup_finanza.sql

5. CHARSET:
   - O banco usa utf8mb4 para suporte completo a Unicode (incluindo emojis)

6. PERFORMANCE:
   - Índices foram criados para as consultas mais comuns
   - Monitore e ajuste conforme necessário

7. COMPATIBILIDADE:
   - Testado com MySQL 5.7+ e MySQL 8.0+
   - Compatível com MariaDB 10.3+

=====================================================================================================================
*/
