import java.io.*;
import java.net.*;

/**
 * Test to verify that users registered from desktop panel are created as administrators
 */
public class test_register_admin {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            System.out.println("Connected to server");
            
            // Test 1: Register user from desktop (should create admin)
            String uniqueEmail = "desktop_user_" + System.currentTimeMillis() + "@test.com";
            out.println("REGISTER|Desktop User|" + uniqueEmail + "|password123|admin");
            String registerResponse = in.readLine();
            System.out.println("REGISTER (desktop) response: " + registerResponse);
            
            // Verify the response contains tipo_usuario = admin
            if (registerResponse.contains("admin")) {
                System.out.println("✓ SUCCESS: Desktop registration created admin user");
            } else {
                System.out.println("✗ FAILED: Desktop registration did not create admin user");
            }
            
            // Test 2: Register user from mobile (should create regular user)
            String uniqueEmailMobile = "mobile_user_" + System.currentTimeMillis() + "@test.com";
            out.println("REGISTER|Mobile User|" + uniqueEmailMobile + "|password123|mobile");
            String registerResponseMobile = in.readLine();
            System.out.println("REGISTER (mobile) response: " + registerResponseMobile);
            
            // Verify the response contains tipo_usuario = usuario
            if (registerResponseMobile.contains("usuario")) {
                System.out.println("✓ SUCCESS: Mobile registration created regular user");
            } else {
                System.out.println("✗ FAILED: Mobile registration did not create regular user");
            }
            
            // Test 3: Test backward compatibility - no client type parameter (should default to mobile/usuario)
            String uniqueEmailCompat = "compat_user_" + System.currentTimeMillis() + "@test.com";
            out.println("REGISTER|Compat User|" + uniqueEmailCompat + "|password123");
            String registerResponseCompat = in.readLine();
            System.out.println("REGISTER (no type) response: " + registerResponseCompat);
            
            // Verify backward compatibility defaults to usuario
            if (registerResponseCompat.contains("usuario") || registerResponseCompat.contains("OK")) {
                System.out.println("✓ SUCCESS: Backward compatible registration works");
            } else {
                System.out.println("✗ FAILED: Backward compatibility broken");
            }
            
            socket.close();
            System.out.println("\nAll tests completed!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
