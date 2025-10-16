-- Finanza Database Schema
-- Sistema de Gestão Financeira Pessoal
-- SQLite Database

-- ================================================
-- Tabela de Usuários
-- ================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    senha TEXT NOT NULL,
    data_criacao INTEGER NOT NULL
);

-- ================================================
-- Tabela de Contas
-- ================================================
CREATE TABLE IF NOT EXISTS contas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    saldo_inicial REAL NOT NULL DEFAULT 0,
    usuario_id INTEGER NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ================================================
-- Tabela de Categorias
-- ================================================
CREATE TABLE IF NOT EXISTS categorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cor_hex TEXT,
    tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa'))
);

-- ================================================
-- Tabela de Lançamentos (Transações)
-- ================================================
CREATE TABLE IF NOT EXISTS lancamentos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    valor REAL NOT NULL,
    data INTEGER NOT NULL,
    descricao TEXT,
    conta_id INTEGER NOT NULL,
    categoria_id INTEGER,
    usuario_id INTEGER NOT NULL,
    tipo TEXT NOT NULL CHECK (tipo IN ('receita', 'despesa')),
    FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ================================================
-- Índices para melhor performance
-- ================================================
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_contas_usuario ON contas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_lancamentos_usuario ON lancamentos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_lancamentos_conta ON lancamentos(conta_id);
CREATE INDEX IF NOT EXISTS idx_lancamentos_categoria ON lancamentos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_lancamentos_data ON lancamentos(data);
CREATE INDEX IF NOT EXISTS idx_lancamentos_tipo ON lancamentos(tipo);

-- ================================================
-- Categorias Padrão - Despesas
-- ================================================
INSERT OR IGNORE INTO categorias (nome, cor_hex, tipo) VALUES
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
INSERT OR IGNORE INTO categorias (nome, cor_hex, tipo) VALUES
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
INSERT OR IGNORE INTO usuarios (nome, email, senha, data_criacao) VALUES
('Administrador', 'admin@finanza.com', 'admin', strftime('%s', 'now') * 1000);

-- ================================================
-- Conta padrão para o usuário de teste
-- ================================================
INSERT OR IGNORE INTO contas (nome, saldo_inicial, usuario_id) VALUES
('Conta Corrente', 1000.00, 1),
('Poupança', 5000.00, 1);

-- ================================================
-- Lançamentos de exemplo
-- ================================================
INSERT OR IGNORE INTO lancamentos (valor, data, descricao, conta_id, categoria_id, usuario_id, tipo) VALUES
(3500.00, strftime('%s', 'now') * 1000, 'Salário Mensal', 1, 1, 1, 'receita'),
(-150.00, strftime('%s', 'now') * 1000, 'Supermercado', 1, 1, 1, 'despesa'),
(-80.00, strftime('%s', 'now') * 1000, 'Combustível', 1, 2, 1, 'despesa'),
(-120.00, strftime('%s', 'now') * 1000, 'Academia', 1, 3, 1, 'despesa'),
(500.00, strftime('%s', 'now') * 1000, 'Freelance Web', 1, 2, 1, 'receita');

-- ================================================
-- Views úteis para relatórios
-- ================================================

-- View para saldo atual por conta
CREATE VIEW IF NOT EXISTS v_saldo_contas AS
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
CREATE VIEW IF NOT EXISTS v_resumo_categorias AS
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
CREATE VIEW IF NOT EXISTS v_lancamentos_detalhados AS
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

-- ================================================
-- Triggers para manter integridade
-- ================================================

-- Trigger para validar valores de lançamentos
CREATE TRIGGER IF NOT EXISTS tr_validar_lancamento
BEFORE INSERT ON lancamentos
FOR EACH ROW
BEGIN
    -- Não permitir valores zerados
    SELECT CASE
        WHEN NEW.valor = 0 THEN
            RAISE(ABORT, 'Valor do lançamento não pode ser zero')
    END;
    
    -- Validar que receitas são positivas e despesas são negativas
    SELECT CASE
        WHEN NEW.tipo = 'receita' AND NEW.valor < 0 THEN
            RAISE(ABORT, 'Receitas devem ter valor positivo')
        WHEN NEW.tipo = 'despesa' AND NEW.valor > 0 THEN
            RAISE(ABORT, 'Despesas devem ter valor negativo')
    END;
END;

-- ================================================
-- Comentários sobre o schema
-- ================================================

/*
ESTRUTURA DO BANCO DE DADOS FINANZA:

1. USUARIOS
   - Armazena informações dos usuários do sistema
   - Email é único para evitar duplicações
   - Senha deve ser hasheada em produção

2. CONTAS
   - Representa contas financeiras (corrente, poupança, etc.)
   - Cada conta pertence a um usuário
   - Saldo inicial é o valor inicial da conta

3. CATEGORIAS
   - Categorias pré-definidas para classificação
   - Separadas por tipo (receita/despesa)
   - Cores em hexadecimal para interface

4. LANCAMENTOS
   - Movimentações financeiras (receitas e despesas)
   - Sempre associados a uma conta e usuário
   - Categoria é opcional
   - Data armazenada em timestamp (milissegundos)

PADRÕES SEGUIDOS:
- Foreign keys para integridade referencial
- Indexes para performance
- Views para consultas complexas
- Triggers para validação
- Dados de exemplo para testes
- Estrutura similar ao Android (Room/SQLite)
*/