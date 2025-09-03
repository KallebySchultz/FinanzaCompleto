package com.finanza.desktop.ui;

import com.finanza.desktop.model.Usuario;
import javax.swing.*;

/**
 * Dialog para adicionar nova transação (stub)
 */
public class AddTransactionDialog extends JDialog {
    public AddTransactionDialog(JFrame parent, Usuario usuario) {
        super(parent, "Nova Transação", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        // Implementação temporária
        JLabel label = new JLabel("Funcionalidade em desenvolvimento");
        add(label);
    }
}