import java.io.IOException;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ClientConnexion {

    public boolean checkServerLogin(String MY_IP, int MY_PORT) {
        // Codes d'échappement ANSI pour les couleurs
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";

        try (Socket socket = new Socket(MY_IP, MY_PORT)) {
            System.out.println(ANSI_GREEN + "CONNEXION SUCCESS" + ANSI_RESET);
            return true;
        } catch (UnknownHostException e) {
            System.out.println(ANSI_RED + "Erreur: L'adresse IP est inconnue ou le nom d'hôte ne peut pas être résolu." + ANSI_RESET);
            return false;
        } catch (ConnectException e) {
            System.out.println(ANSI_RED + "Erreur: Impossible de se connecter au serveur. Veuillez vérifier l'IP et le port." + ANSI_RESET);
            return false;
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Erreur: Un problème d'entrée/sortie s'est produit lors de la tentative de connexion." + ANSI_RESET);
            return false;
        }
    }

    public void startClientSession() {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_BLUE = "\u001B[34m";

        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print(ANSI_BLUE + "Entrez une commande (/register, /login, /help): " + ANSI_RESET);
                String command = userInput.readLine();

                switch (command) {
                    case "/register":
                        register();
                        break;
                    case "/login":
                        login();
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
        // Code pour gérer l'inscription
        System.out.println("Fonction d'inscription à implémenter.");
    }

    private void login() {
        // Code pour gérer la connexion
        System.out.println("Fonction de connexion à implémenter.");
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

        ClientConnexion client = new ClientConnexion();
        if (client.checkServerLogin(IP, PORT)) {
            client.startClientSession();
        }
    }
}

