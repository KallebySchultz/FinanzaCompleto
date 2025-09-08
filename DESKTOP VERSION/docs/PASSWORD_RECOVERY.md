# Password Recovery Feature - Desktop Client

## Overview
The desktop version of Finanza now includes a password recovery feature that allows users to reset their passwords without requiring access to their current password.

## Features Implemented

### 1. Database Schema Changes
- Added `password_reset_token` column to store recovery tokens
- Added `reset_token_expiry` column to handle token expiration (1 hour)
- Added index for efficient token lookup

### 2. Server-Side Components

#### Protocol Commands
- `REQUEST_PASSWORD_RESET`: Requests a password reset token for an email
- `RESET_PASSWORD`: Resets password using a valid token

#### Security Features
- Tokens are 8-character alphanumeric codes (A-Z, 0-9)
- Tokens expire after 1 hour
- Secure token generation using SecureRandom
- Password validation (minimum 6 characters)

#### Database Operations
- `gerarTokenRecuperacao(email)`: Generates and stores recovery token
- `validarTokenRecuperacao(token)`: Validates token and checks expiration
- `redefinirSenhaComToken(token, novaSenha)`: Resets password and clears token

### 3. Client-Side Components

#### LoginView Enhancements
- Added "Esqueci minha senha" (Forgot Password) link
- Link opens the password recovery dialog

#### PasswordRecoveryDialog
- Two-step process:
  1. Enter email to request recovery code
  2. Enter code and new password to reset
- Real-time validation
- User-friendly error handling
- Responsive UI that adapts to each step

#### AuthController Extensions
- `solicitarRecuperacaoSenha(email)`: Requests password reset
- `redefinirSenha(token, novaSenha)`: Resets password with token

## User Flow

1. **User clicks "Esqueci minha senha" on login screen**
2. **Password Recovery Dialog opens**
3. **User enters email and clicks "Solicitar Código"**
4. **System generates recovery code and displays it**
   - In a production environment, this would be sent via email
   - For the desktop demo, it's shown directly to the user
5. **User enters the recovery code and new password**
6. **User clicks "Redefinir Senha"**
7. **Password is updated and user can login with new password**

## Security Considerations

- Tokens are random and cryptographically secure
- Tokens expire automatically after 1 hour
- Only one active token per user (new requests overwrite old tokens)
- Password strength validation maintained
- No sensitive information logged

## Database Migration

To add the password recovery functionality to an existing database:

```sql
-- Run this migration script
USE finanza_db;

ALTER TABLE usuario 
ADD COLUMN password_reset_token VARCHAR(255) NULL,
ADD COLUMN reset_token_expiry TIMESTAMP NULL;

CREATE INDEX idx_usuario_reset_token ON usuario(password_reset_token);
```

## Testing

A test suite has been created (`PasswordRecoveryTest.java`) that verifies:
- Token generation functionality
- Password validation
- Hash consistency
- Basic integration with UsuarioDAO

Run tests with:
```bash
cd ServidorFinanza
javac -cp classes -d classes src/test/PasswordRecoveryTest.java
java -cp classes -ea test.PasswordRecoveryTest
```

## Architecture Notes

- **Desktop-only feature**: This implementation is specifically for the desktop client
- **No email dependency**: Tokens are displayed directly, avoiding SMTP setup complexity
- **Minimal changes**: All modifications are surgical and don't affect existing functionality
- **Backward compatible**: Existing users and functionality remain unaffected

The implementation follows the existing codebase patterns and maintains consistency with the current architecture.