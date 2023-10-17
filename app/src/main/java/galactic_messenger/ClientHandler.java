package galactic_messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Set<String> connectedClients;
    private Map<String, ClientHandler> clientHandlers;
    private String username;
    private PrintWriter out;
    private ClientHandler chatPartner;

    public ClientHandler(Socket clientSocket, Set<String> connectedClients, Map<String, ClientHandler> clientHandlers) {
        this.clientSocket = clientSocket;
        this.connectedClients = connectedClients;
        this.clientHandlers = clientHandlers;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Veuillez entrer votre nom d'utilisateur :");
            username = in.readLine();

            synchronized (connectedClients) {
                connectedClients.add(username);
            }

            synchronized (clientHandlers) {
                clientHandlers.put(username, this);
            }

            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                if ("/list".equalsIgnoreCase(clientInput)) {
                    synchronized (connectedClients) {
                        out.println(connectedClients.toString());
                    }
                } else if (clientInput.startsWith("/private_chat")) {
                    String targetUser = clientInput.split(" ")[1];
                    startPrivateChat(targetUser);
                } else if (clientInput.startsWith("/accept")) {
                    String requester = clientInput.split(" ")[1];
                    acceptPrivateChat(requester);
                } else if (clientInput.equalsIgnoreCase("/decline")) {
                    declinePrivateChat();
                } else if (chatPartner != null) {
                    chatPartner.out.println(username + ": " + clientInput);
                }
                // TODO: Ajouter d'autres commandes selon les besoins
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (connectedClients) {
                connectedClients.remove(username);
            }
            synchronized (clientHandlers) {
                clientHandlers.remove(username);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startPrivateChat(String targetUser) {
        ClientHandler targetHandler = clientHandlers.get(targetUser);
        if (targetHandler != null && !targetUser.equals(username)) {
            targetHandler.out.println(username + " souhaite démarrer un chat privé avec vous. Tapez /accept " + username
                    + " pour accepter.");
            chatPartner = targetHandler;
            out.println("Demande de chat privé envoyée à " + targetUser);
        } else {
            out.println("Utilisateur non disponible pour le chat privé.");
        }
    }

    private void acceptPrivateChat(String requester) {
        ClientHandler requesterHandler = clientHandlers.get(requester);
        if (requesterHandler != null && requesterHandler.chatPartner == this) {
            chatPartner = requesterHandler;
            chatPartner.out.println("Votre demande de chat privé avec " + username + " a été acceptée.");
            out.println("Vous avez accepté le chat privé avec " + requester);
        } else {
            out.println("Pas de demande de chat privé en attente de " + requester);
        }
    }

    private void declinePrivateChat() {
        if (chatPartner != null) {
            chatPartner.out.println(username + " a refusé votre demande de chat privé.");
            chatPartner.chatPartner = null;
            chatPartner = null;
            out.println("Vous avez refusé le chat privé.");
        } else {
            out.println("Pas de demande de chat privé en attente.");
        }
    }
}
