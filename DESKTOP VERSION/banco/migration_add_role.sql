-- Migration script to add role field to existing databases
-- Run this on existing Finanza databases to add user role support
-- Date: October 2025

USE finanza_db;

-- Add role column to usuario table if it doesn't exist
ALTER TABLE usuario 
ADD COLUMN IF NOT EXISTS role ENUM('user', 'admin') NOT NULL DEFAULT 'user' 
AFTER senha_hash;

-- Update existing users to have 'user' role by default
UPDATE usuario 
SET role = 'user' 
WHERE role IS NULL OR role = '';

-- Create admin user if it doesn't exist (password: admin)
-- Note: Change the password hash in production!
INSERT IGNORE INTO usuario (nome, email, senha_hash, role) VALUES 
('Administrador', 'admin@finanza.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'admin');

-- Verify the changes
SELECT id, nome, email, role FROM usuario;

-- Migration completed successfully
SELECT 'Migration completed! Role column added to usuario table' AS Status;
