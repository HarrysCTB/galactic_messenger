package galactic_messenger.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;


public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Database database;
    private static final Set<String> onlineUsers = Collections.synchronizedSet(new HashSet<>());
    private String username;
    private Queue<String> messageQueue = new LinkedList<>();
    private final ReentrantLock queueLock = new ReentrantLock();
    private volatile String privateChatPartner = null;


    public ClientHandler(Socket clientSocket, Database database) {
        this.clientSocket = clientSocket;
        this.database = database;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            // Lancement de la lecture des entrées dans un thread séparé
            System.out.println("1" + getPrivateChatPartner());
            new Thread(() -> readInput(out)).start();

            while (!clientSocket.isClosed()) {
                sendPendingMessages(out);
                Thread.sleep(100); // Pause de 100 ms
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void readInput(PrintWriter out) {
        System.out.println("2" + getPrivateChatPartner());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                processInput(inputLine, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processInput(String inputLine, PrintWriter out) {
        String partner = getPrivateChatPartner();
        System.out.println("3" + partner);
        if (inputLine.startsWith("/")) {
            String response = handleCommand(inputLine);
            if (response != null) {
                out.println(response);
            }
        } else if (partner != null) {
            // Logique pour les sessions de chat privé
            ClientHandler partnerHandler = Server.getClientHandler(partner);
            if (partnerHandler != null) {
                partnerHandler.sendMessage(this.username + ": " + inputLine);
            }
        } else {
            // Gérer les cas où l'utilisateur n'est pas en chat privé mais envoie un message
            out.println("Unknown command: " + inputLine);
        }
    }

    public synchronized void setPrivateChatPartner(String partner) {
        this.privateChatPartner = partner;
    }
    public synchronized String getPrivateChatPartner() {
        return privateChatPartner;
    }

    private void sendPendingMessages(PrintWriter out) {
        queueLock.lock();
        try {
            while (!messageQueue.isEmpty()) {
                String message = messageQueue.poll();
                if (message != null) {
                    out.println(message);
                }
            }
        } finally {
            queueLock.unlock();
        }
    }

    private void cleanup() {
        // Nettoyage final
        if (this.username != null && !this.username.isEmpty()) {
            onlineUsers.remove(this.username);
            Server.removeClientHandler(this.username);
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOnlineUsers() {
        StringBuilder userList = new StringBuilder("Online Users: ");
        synchronized (onlineUsers) {
            boolean first = true;
            for (String user : onlineUsers) {
                if (first) {
                    first = false;
                } else {
                    userList.append(", ");
                }
                userList.append(user);
            }
        }
        return userList.toString();
    }

    private String sendPrivateChatRequest(String targetUser) {
        ClientHandler targetClientHandler = Server.getClientHandler(targetUser);
        if (targetClientHandler != null) {
            System.out.println("Found target user handler for: " + targetUser);
            targetClientHandler.receivePrivateChatRequest(this.username);
            return "Private Chat Request sent to " + targetUser;
        } else {
            System.out.println("Target user handler not found for: " + targetUser);
            return "User " + targetUser + " is not available for private chat.";
        }
    }

    public void receivePrivateChatRequest(String requestingUser) {
        String requestMessage = "Private Chat Request: " + requestingUser + " wants to start a private chat with you. Respond with /accept " + requestingUser + " or /decline " + requestingUser;
        messageQueue.offer(requestMessage);
        System.out.println("Added to messageQueue: " + requestMessage); // Log pour le débogage
    }

    // Implémentez handleAcceptance et handleDecline de manière similaire
    private String handleAcceptance(String requestingUser) {
        ClientHandler requestingUserHandler = Server.getClientHandler(requestingUser);
        if (requestingUserHandler != null) {
            // Notifier le demandeur que sa demande a été acceptée
            requestingUserHandler.sendMessage("Private Chat Accepted: " + this.username + " has accepted your chat request.");
            this.sendMessage("You have accepted " + requestingUser + "'s private chat request.");
            // Établir la session de chat privé
            setPrivateChatPartner(requestingUser);
            requestingUserHandler.setPrivateChatPartner(this.username);

            System.out.println(this.username + " privateChatPartner set to: " + getPrivateChatPartner());
            System.out.println(requestingUser + " privateChatPartner set to: " + requestingUserHandler.getPrivateChatPartner());
            // Logs pour le débogage
            System.out.println("Chat session established between " + this.username + " and " + requestingUser);
            return "Private Chat Accepted by " + this.username;
        } else {
            return "User " + requestingUser + " is no longer available.";
        }
    }

    public void sendMessage(String message) {
        queueLock.lock();
        try {
            messageQueue.add(message);
        } finally {
            queueLock.unlock();
        }
    }

    private String handleDecline(String requestingUser) {
        ClientHandler requestingUserHandler = Server.getClientHandler(requestingUser);
        if (requestingUserHandler != null) {
            requestingUserHandler.sendMessage("Private Chat Declined: " + this.username + " has declined your chat request.");
            return "Private Chat Declined by " + this.username;
        } else {
            return "Requesting user " + requestingUser + " is no longer available.";
        }
    }

    private String handleCommand(String command) {
        String[] parts = command.split(" ", 3);
        String action = parts[0].toLowerCase();

        switch (action) {
            case "/register":
                if (parts.length == 3) {
                    String username = parts[1];
                    String hashedPassword = parts[2];
                    return database.registerUser(username, hashedPassword);
                } else {
                    return "Invalid command format.";
                }
            case "/login":
                if (parts.length == 3) {
                    this.username = parts[1];
                    String hashedPassword = parts[2];
                    String loginResult = database.loginUser(this.username, hashedPassword);
                    onlineUsers.add(this.username);
                    Server.addClientHandler(username, this);
                    return loginResult;
                } else {
                    return "Invalid command format.";
                }
            case "/online_users":
                return getOnlineUsers();
            case "/logout":
                if (this.username != null) {
                    onlineUsers.remove(this.username);
                    this.username = null;
                }
                return "Logged out";
            case "/private_chat":
                if (parts.length == 2) {
                    // Envoyer une demande de chat privé à l'utilisateur cible
                    String targetUser = parts[1];
                    return sendPrivateChatRequest(targetUser);
                }
                return "Format de commande invalide pour /private_chat";
            case "/accept":
                if (parts.length == 2) {
                    String requestingUser = parts[1];
                    return handleAcceptance(requestingUser); // Assurez-vous que cette méthode retourne une String
                }
                return "Invalid command format for /accept."; // Retourne une String si la condition if n'est pas remplie
            case "/decline":
                if (parts.length == 2) {
                    String requestingUser = parts[1];
                    return handleDecline(requestingUser); // Assurez-vous que cette méthode retourne une String
                }
                return "Invalid command format for /decline."; // Retourne une String si la condition if n'est pas remplie
            case "/exit_private_chat":
                privateChatPartner = null;
                return "Exited Private Chat";
            default:
                return "Unknown command: " + action;
        }
    }
}