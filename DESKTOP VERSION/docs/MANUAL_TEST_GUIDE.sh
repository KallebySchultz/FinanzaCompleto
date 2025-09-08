#!/bin/bash

# Manual Test Guide for Password Recovery Feature

echo "=== Manual Test Guide for Password Recovery ==="
echo ""
echo "Prerequisites:"
echo "1. MySQL server running with finanza_db database"
echo "2. Run the migration script: DESKTOP VERSION/banco/password_reset_migration.sql"
echo "3. At least one user registered in the system"
echo ""

echo "Test Steps:"
echo ""

echo "1. Start the server:"
echo "   cd 'DESKTOP VERSION/ServidorFinanza'"
echo "   java -cp classes MainServidor"
echo ""

echo "2. Start the client:"
echo "   cd 'DESKTOP VERSION/ClienteFinanza'"
echo "   java -cp build/classes MainCliente"
echo ""

echo "3. On the login screen, click 'Esqueci minha senha'"
echo ""

echo "4. In the recovery dialog:"
echo "   - Enter a valid email address"
echo "   - Click 'Solicitar Código'"
echo "   - A recovery code will be displayed"
echo ""

echo "5. Complete the password reset:"
echo "   - Enter the recovery code"
echo "   - Enter a new password (min 6 characters)"
echo "   - Confirm the new password"
echo "   - Click 'Redefinir Senha'"
echo ""

echo "6. Verify the password was changed:"
echo "   - Close the recovery dialog"
echo "   - Try logging in with the old password (should fail)"
echo "   - Try logging in with the new password (should succeed)"
echo ""

echo "Expected Results:"
echo "✓ Recovery dialog opens successfully"
echo "✓ Recovery code is generated and displayed"
echo "✓ Password is updated in the database"
echo "✓ Login works with new password"
echo "✓ Old password no longer works"
echo ""

echo "Error Cases to Test:"
echo "- Invalid email format"
echo "- Non-existent email"
echo "- Invalid recovery code"
echo "- Expired recovery code (after 1 hour)"
echo "- Password too short (less than 6 characters)"
echo "- Password confirmation mismatch"
echo ""

echo "Database Verification:"
echo "You can check the database directly:"
echo "SELECT email, password_reset_token, reset_token_expiry FROM usuario WHERE email = 'your@email.com';"
echo ""

echo "For automated testing, run:"
echo "cd 'DESKTOP VERSION/ServidorFinanza'"
echo "java -cp classes -ea test.PasswordRecoveryTest"