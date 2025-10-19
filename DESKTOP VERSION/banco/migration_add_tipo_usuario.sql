-- Migration script to add tipo_usuario field to existing database
-- Este script adiciona o campo tipo_usuario à tabela usuario
-- e define valores padrão para usuários existentes

USE finanza_db;

-- Adiciona o campo tipo_usuario se ele não existir
ALTER TABLE usuario 
ADD COLUMN IF NOT EXISTS tipo_usuario ENUM('admin', 'usuario') NOT NULL DEFAULT 'usuario' AFTER senha_hash;

-- Define o primeiro usuário como admin (assumindo que é o administrador)
UPDATE usuario SET tipo_usuario = 'admin' WHERE id = 1 LIMIT 1;

-- Todos os outros usuários são definidos como usuário comum
UPDATE usuario SET tipo_usuario = 'usuario' WHERE id > 1;

-- Exibe os usuários e seus tipos
SELECT id, nome, email, tipo_usuario FROM usuario;
