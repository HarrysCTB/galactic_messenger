package galactic_messenger.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import galactic_messenger.utils.ValidationUtils;
import galactic_messenger.utils.Color; // Importez la classe Color ici

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar galactic_messenger_client.jar [adresse ip serveur] [numéro de port]");
            System.exit(1);
        }

        String ipAddress = ValidationUtils.validateAndParseIPAddress(args[0]);
        int serverPort = ValidationUtils.validateAndParsePort(args[1]);

        try {
            Socket socket = new Socket(ipAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            displayWelcomeMessage();

            while (true) {
                System.out.print("Enter a command: ");
                String command = userInput.readLine();
                String response = CommandHandler.handleCommand(command);

                if (response != null) {
                    // Si la réponse n'est pas nulle, envoyez-la au serveur
                    out.println(response);

                    // Attendez la réponse du serveur
                    String serverResponse = in.readLine();
                    System.out.println(Color.colorize("[Server]: " + serverResponse, Color.BLUE));
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + ipAddress);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(Color.colorize("Error connecting to server: " + e.getMessage(), Color.RED));
            System.exit(1);
        }
    }

    private static void displayWelcomeMessage() {
        System.out.println(Color.colorize("Welcome to Galactic Messenger! 🛸", Color.BLUE));
        System.out.println(Color.colorize("Explore the interstellar chat.", Color.GREEN));
        System.out.println("Available commands:");
        System.out.println(Color.colorize("/register <username> <password> - Register a new user", Color.CYAN));
        System.out.println(Color.colorize("/login <username> <password> - Login as an existing user", Color.CYAN));
        System.out.println(Color.colorize("/help - Display available commands", Color.CYAN));
        System.out.println(Color.colorize("/exit - Exit the Galactic Messenger", Color.CYAN));
    }
}