-- Migration script to add password reset functionality
-- Add password reset token and expiry columns to usuario table

USE finanza_db;

-- Add columns for password reset functionality
ALTER TABLE usuario 
ADD COLUMN password_reset_token VARCHAR(255) NULL,
ADD COLUMN reset_token_expiry TIMESTAMP NULL;

-- Create index for token lookup
CREATE INDEX idx_usuario_reset_token ON usuario(password_reset_token);