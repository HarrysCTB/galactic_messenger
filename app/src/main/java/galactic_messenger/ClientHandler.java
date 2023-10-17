package galactic_messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Set<String> connectedClients;
    private String username;

    public ClientHandler(Socket clientSocket, Set<String> connectedClients) {
        this.clientSocket = clientSocket;
        this.connectedClients = connectedClients;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Veuillez entrer votre nom d'utilisateur :");
            username = in.readLine();

            synchronized(connectedClients) {
                connectedClients.add(username);
            }

            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                if ("/list".equalsIgnoreCase(clientInput)) {
                    synchronized(connectedClients) {
                        out.println(connectedClients.toString());
                    }
                }
                // Handle other commands as needed
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized(connectedClients) {
                connectedClients.remove(username);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


