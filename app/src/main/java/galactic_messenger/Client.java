package galactic_messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Client connection class for the galactic messenger.
 * This class handles client-server connection and user interactions.
 */
public class Client {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    /**
     * Check if the server is available and accept connections.
     *
     * @param serverIP   - IP address of the server
     * @param serverPort - Port number to connect
     * @return true if the connection is successful, false otherwise
     */
    public boolean checkServerLogin(String serverIP, int serverPort) {
        try (Socket socket = new Socket(serverIP, serverPort)) {
            System.out.println(ANSI_GREEN + "CONNEXION SUCCESS" + ANSI_RESET);
            return true;
        } catch (UnknownHostException e) {
            System.out.println(ANSI_RED + "Erreur: L'adresse IP est inconnue ou le nom d'hôte ne peut pas être résolu."
                    + ANSI_RESET);
            return false;
        } catch (ConnectException e) {
            System.out.println(ANSI_RED
                    + "Erreur: Impossible de se connecter au serveur. Veuillez vérifier l'IP et le port." + ANSI_RESET);
            return false;
        } catch (IOException e) {
            System.out.println(
                    ANSI_RED + "Erreur: Un problème d'entrée/sortie s'est produit lors de la tentative de connexion."
                            + ANSI_RESET);
            return false;
        }
    }

    /**
     * Start a client session and handle user interactions.
     */
    public void startClientSession(String serverIP, int serverPort) {
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print(ANSI_BLUE + "Entrez une commande (/register, /login, /help): " + ANSI_RESET);
                String command = userInput.readLine();

                switch (command) {
                    case "/register":
                        register();
                        break;
                    case "/login":
                        login(serverIP, serverPort);
                        break;
                    case "/help":
                        displayHelp();
                        break;
                    case "/exit":
                        return;
                    default:
                        System.out.println("Commande inconnue. Essayez /help pour voir les commandes disponibles.");
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture de la commande utilisateur.");
        }
    }

    private void register() {
        // Code to handle registration
        System.out.println("Fonction d'inscription à implémenter.");
    }

    private void login(String serverIP, int serverPort) {
        try (Socket socket = new Socket(serverIP, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println(in.readLine());  // "Veuillez entrer votre nom d'utilisateur :"
            String username = userInput.readLine();
            out.println(username);

            String command;
            while (true) {
                System.out.print("Entrez une commande: ");
                command = userInput.readLine();
                out.println(command);  // Send command to the server

                if ("/list".equalsIgnoreCase(command)) {
                    System.out.println("Clients connectés: " + in.readLine());
                }
                // Handle other commands as needed
            }

        } catch (IOException e) {
            System.out.println("Erreur lors de la communication avec le serveur.");
        }
    }

    private void displayHelp() {
        System.out.println("/register : S'inscrire comme nouvel utilisateur");
        System.out.println("/login    : Se connecter avec un compte existant");
        System.out.println("/help     : Afficher cette aide");
        System.out.println("/exit     : Quitter l'application");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Veuillez fournir une IP et un port comme arguments.");
            return;
        }

        String IP = args[0];
        int PORT;
        try {
            PORT = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Veuillez fournir un port valide.");
            return;
        }

        Client client = new Client();
        if (client.checkServerLogin(IP, PORT)) {
            client.startClientSession(IP, PORT);
        }
    }
}
