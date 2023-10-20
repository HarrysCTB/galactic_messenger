package galactic_messenger.client;

import galactic_messenger.utils.Color;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommandHandler {
    public static String handleCommand(String command) {
        String[] parts = command.split(" ", 2);
        String action = parts[0].toLowerCase();

        switch (action) {
            case "/register":
                return handleRegisterCommand(parts);
            case "/login":
                return handleLoginCommand(parts);
            case "/help":
                handleHelpCommand();
                break;
            case "/exit":
                handleExitCommand();
                break;
            default:
                System.out.println(Color.colorize("Unknown command: " + action, Color.RED));
        }
        return null;
    }

    private static String handleRegisterCommand(String[] parts) {
        if (parts.length != 2) {
            System.out.println(Color.colorize("Usage: /register <username> <password>", Color.RED));
            return null;
        }

        String[] registrationInfo = parts[1].split(" ");
        if (registrationInfo.length != 2) {
            System.out.println(Color.colorize("Usage: /register <username> <password>", Color.RED));
            return null;
        }

        String username = registrationInfo[0];
        String password = registrationInfo[1];

        if (isValidUsername(username) && isValidPassword(password)) {
            String hashedPassword = hashPassword(password);

            return "/register " + username + " " + hashedPassword;
        } else {
            System.out.println(Color.colorize("Invalid username or password. Rules:\n" +
                    "Username must be 3-20 characters long and can only contain letters and numbers.\n" +
                    "Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character.",
                    Color.RED));
            return null;
        }
    }

    private static String handleLoginCommand(String[] parts) {
        if (parts.length != 2) {
            System.out.println(Color.colorize("Usage: /login <username> <password>", Color.RED));
            return null;
        }

        String[] loginInfo = parts[1].split(" ");
        if (loginInfo.length != 2) {
            System.out.println(Color.colorize("Usage: /login <username> <password>", Color.RED));
            return null;
        }

        String username = parts[1].split(" ")[0];
        String password = parts[1].split(" ")[1];

        String hashedPassword = hashPassword(password);

        return "/login " + username + " " + hashedPassword;
    }

    private static void handleHelpCommand() {
        String[][] commandList = {
                { "/register <username> <password>", "Register a new user" },
                { "/login <username> <password>", "Login as an existing user" },
                { "/help", "Display available commands" },
                { "/exit", "Exit the Galactic Messenger" }
                // Ajoutez ici d'autres commandes et leurs descriptions
        };

        System.out.println("Available commands:");
        for (String[] commandInfo : commandList) {
            System.out.println(Color.colorize(commandInfo[0], Color.CYAN) + " - " + commandInfo[1]);
        }
    }

    private static void handleExitCommand() {
        System.out.println(Color.colorize("Live long and prosper 🖖", Color.BLUE));
        System.exit(0);
    }

    private static boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9]{3,20}$");
    }

    private static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!%*?&])[A-Za-z\\d@#$!%*?&]{8,}$");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes());

            byte[] hashedPasswordBytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPasswordBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing the password.");
        }
    }
}
