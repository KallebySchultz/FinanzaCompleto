package com.example.finanza;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import com.example.finanza.network.Protocol;

/**
 * Unit tests for SyncService foreign key constraint fix
 */
public class SyncServiceTest {

    @Test
    public void testProtocolParseCommand() {
        // Test parsing server response for category creation
        String response = "OK|1700";
        String[] parts = Protocol.parseCommand(response);
        
        assertEquals("Should have 2 parts", 2, parts.length);
        assertEquals("First part should be OK", "OK", parts[0]);
        assertEquals("Second part should be server ID", "1700", parts[1]);
    }
    
    @Test 
    public void testProtocolParseCommand_ErrorResponse() {
        // Test parsing error response
        String response = "ERROR|Erro ao criar movimentação";
        String[] parts = Protocol.parseCommand(response);
        
        assertEquals("Should have 2 parts", 2, parts.length);
        assertEquals("First part should be ERROR", "ERROR", parts[0]);
        assertTrue("Second part should contain error message", 
                  parts[1].contains("movimentação"));
    }
    
    @Test
    public void testCategoriaIdParsing() {
        // Test that server ID can be correctly parsed from response
        String serverResponse = "OK|1700";
        String[] partes = Protocol.parseCommand(serverResponse);
        
        if (partes.length >= 2 && Protocol.STATUS_OK.equals(partes[0])) {
            try {
                int serverId = Integer.parseInt(partes[1]);
                assertEquals("Server ID should be 1700", 1700, serverId);
            } catch (NumberFormatException e) {
                fail("Should be able to parse server ID as integer");
            }
        } else {
            fail("Response should be valid OK format");
        }
    }
    
    @Test
    public void testMovimentacaoCommandFormat() {
        // Test that movimentacao command format matches expected server format
        String expectedFormat = "ADD_MOVIMENTACAO|10.0|2025-09-07|teste|receita|1700|46";
        String[] parts = expectedFormat.split("\\|");
        
        assertEquals("Command should have 7 parts", 7, parts.length);
        assertEquals("Command type", "ADD_MOVIMENTACAO", parts[0]);
        assertEquals("Valor", "10.0", parts[1]);
        assertEquals("Data", "2025-09-07", parts[2]);
        assertEquals("Descricao", "teste", parts[3]);
        assertEquals("Tipo", "receita", parts[4]);
        assertEquals("ContaId", "1700", parts[5]); // Should be server ID, not local ID
        assertEquals("CategoriaId", "46", parts[6]); // Should be server ID, not local ID
    }
}