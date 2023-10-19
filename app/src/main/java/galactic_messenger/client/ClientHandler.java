package galactic_messenger.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Set<String> connectedClients;
    private Map<String, ClientHandler> clientHandlers;
    private String username;
    private PrintWriter out;
    private ClientHandler chatPartner;
    private boolean inPrivateChat = false;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static Map<String, Set<ClientHandler>> chatGroups = new HashMap<>();
    public static Map<String, String> groupPasswords = new HashMap<>();
    private GroupHandler groupHandler = new GroupHandler();

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
                if ("/online_users".equalsIgnoreCase(clientInput)) {
                    synchronized (connectedClients) {
                        out.println(ANSI_GREEN + "[SERVER] : " + connectedClients.toString() + ANSI_RESET);
                    }
                } else if (clientInput.startsWith("/private_chat")) {
                    String[] commandParts = clientInput.split(" ");
                    if (commandParts.length > 1) {
                        String targetUser = commandParts[1];
                        startPrivateChat(in, out, targetUser);
                    }
                } else if (clientInput.startsWith("/accept")) {
                    String[] commandParts = clientInput.split(" ");
                    if (commandParts.length > 1) {
                        String requester = commandParts[1];
                        acceptPrivateChat(requester);
                    }
                } else if (clientInput.startsWith("/decline")) {
                    String[] commandParts = clientInput.split(" ");
                    if (commandParts.length > 1) {
                        String requester = commandParts[1];
                        declinePrivateChat(requester);
                    } else {
                        out.println(
                                "Veuillez spécifier le nom de l'utilisateur dont vous souhaitez refuser la demande de chat.");
                    }
                } else if (inPrivateChat) {
                    if (chatPartner != null) {
                        if (clientInput.equalsIgnoreCase("/exit_private_chat")) {
                            chatPartner.out.println(
                                    getColorForUser(username) + username + ANSI_RESET + " a quitté le chat privé.");
                            out.println("Vous avez quitté le chat privé.");
                            chatPartner.inPrivateChat = false;
                            inPrivateChat = false;
                        } else {
                            sendMessageToChatPartner(chatPartner, clientInput);
                        }
                    }
                } else if (clientInput.startsWith("/create_group") ||
                        clientInput.startsWith("/join_group") ||
                        clientInput.startsWith("/exit_group") ||
                        clientInput.startsWith("/msg_group")) {
                    groupHandler.handleGroupCommands(clientInput, this);
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

    private void sendMessageToChatPartner(ClientHandler partner, String message) {
        partner.out
                .println(getColorForUser(username) + username + ANSI_RESET + ": " + ANSI_BLUE + message + ANSI_RESET);
    }

    private String getColorForUser(String username) {
        // For this demo, I'm simply alternating between two colors.
        // A more advanced system could use a map of usernames to colors, or other
        // criteria.
        return username.hashCode() % 2 == 0 ? ANSI_RED : ANSI_GREEN;
    }

    private void startPrivateChat(BufferedReader in, PrintWriter out, String targetUser) throws IOException {
        ClientHandler targetHandler = clientHandlers.get(targetUser);

        if (targetHandler != null && targetHandler != this) {
            // Send chat request to target user
            targetHandler.out.println(username + " souhaite démarrer un chat privé avec vous. Tapez /accept " + username
                    + " pour accepter.");
            this.out.println("Demande de chat privé envoyée à " + targetUser);
            targetHandler.chatPartner = this; // Set the chatPartner for targetUser when a request is sent
            // System.out.println("DEBUG: " + username + " set " + targetUser + " as
            // chatPartner");
        } else {
            out.println("Utilisateur cible non trouvé ou invalide.");
        }
    }

    private void acceptPrivateChat(String requester) {
        ClientHandler requesterHandler = clientHandlers.get(requester);

        // System.out.println("DEBUG: Current user (" + username + ") trying to accept
        // chat with " + requester);
        // if (requesterHandler != null) {
        // System.out.println("DEBUG: " + requester + "'s chatPartner is "
        // + (requesterHandler.chatPartner == null ? "null" :
        // requesterHandler.chatPartner.username));
        // }

        if (requesterHandler != null && requesterHandler == this.chatPartner) {
            chatPartner = requesterHandler;
            requesterHandler.chatPartner = this;
            inPrivateChat = true;
            chatPartner.inPrivateChat = true;
            chatPartner.out.println("Votre demande de chat privé avec " + username + " a été acceptée.");
            out.println("Vous êtes maintenant en chat privé. Tapez /private_chat_exit pour quitter.");
        } else {
            out.println("Pas de demande de chat privé en attente de " + requester);
        }
    }

    private void declinePrivateChat(String requester) {
        if (chatPartner != null && chatPartner.username.equals(requester)) {
            chatPartner.out.println(username + " a refusé votre demande de chat privé.");
            chatPartner.chatPartner = null;
            chatPartner = null;
            out.println("Vous avez refusé la demande de chat privé de " + requester + ".");
        } else {
            out.println("Pas de demande de chat privé en attente de " + requester + ".");
        }
    }

    public PrintWriter getOut() {
        return out;
    }

    public String getUsername() {
        return username;
    }

    public static synchronized void addToChatGroup(String groupName, ClientHandler clientHandler) {
        chatGroups.computeIfAbsent(groupName, k -> new HashSet<>()).add(clientHandler);
    }

    public static synchronized void removeFromChatGroup(String groupName, ClientHandler clientHandler) {
        if (chatGroups.containsKey(groupName)) {
            chatGroups.get(groupName).remove(clientHandler);
        }
    }

    public static synchronized Set<ClientHandler> getMembersOfChatGroup(String groupName) {
        return chatGroups.get(groupName);
    }

    public static synchronized Set<String> getAllGroupNames() {
        return new HashSet<>(chatGroups.keySet());
    }

    public static synchronized void setGroupPassword(String groupName, String password) {
        groupPasswords.put(groupName, password);
    }

    public static synchronized String getGroupPassword(String groupName) {
        return groupPasswords.get(groupName);
    }
}