import java.io.*;
import java.net.*;

public class test_category_command {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            System.out.println("Connected to server");
            
            // Test LOGIN first (required for authentication)
            out.println("LOGIN|test@example.com|password");
            String loginResponse = in.readLine();
            System.out.println("LOGIN response: " + loginResponse);
            
            // Test LIST_CATEGORIAS_TIPO command
            out.println("LIST_CATEGORIAS_TIPO|receita");
            String response = in.readLine();
            System.out.println("LIST_CATEGORIAS_TIPO|receita response: " + response);
            
            // Test with despesa type
            out.println("LIST_CATEGORIAS_TIPO|despesa");
            String response2 = in.readLine();
            System.out.println("LIST_CATEGORIAS_TIPO|despesa response: " + response2);
            
            socket.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
