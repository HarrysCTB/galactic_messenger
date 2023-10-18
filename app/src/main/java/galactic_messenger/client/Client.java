package galactic_messenger.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";

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

    public void startClientSession(String serverIP, int serverPort) {
        try (Socket socket = new Socket(serverIP, serverPort);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            Thread readThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

            System.out.println(in.readLine()); // "Veuillez entrer votre nom d'utilisateur :"
            String username = userInput.readLine();
            out.println(username);

            String command;
            while ((command = userInput.readLine()) != null) {
                out.println(command);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la communication avec le serveur.");
        }
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