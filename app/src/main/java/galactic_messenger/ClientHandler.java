package galactic_messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Set<String> connectedClients;
    private String username;

    // Map pour suivre les demandes de chat privé. 
    // Clé = l'utilisateur qui a été invité, valeur = l'utilisateur qui a initié le chat privé.
    private static Map<String, String> privateChatRequests = new HashMap<>();

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
                } else if (clientInput.startsWith("/private_chat")) {
                    String targetUsername = clientInput.split(" ")[1];
                    if (connectedClients.contains(targetUsername) && !username.equals(targetUsername)) {
                        privateChatRequests.put(targetUsername, username);
                        out.println("Demande de chat privé envoyée à " + targetUsername);
                    } else {
                        out.println("L'utilisateur n'est pas valide ou est le même que vous.");
                    }
                } else if (clientInput.startsWith("/accept")) {
                    String requester = clientInput.split(" ")[1];
                    if (privateChatRequests.containsKey(username) && privateChatRequests.get(username).equals(requester)) {
                        // Démarre le chat privé
                        out.println("Chat privé démarré avec " + requester);
                        // TODO: implémenter la logique pour gérer le chat privé ici
                    } else {
                        out.println("Aucune demande de chat privé de " + requester);
                    }
                }
                // ... [gérer d'autres commandes comme nécessaire]
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



