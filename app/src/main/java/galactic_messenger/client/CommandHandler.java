package galactic_messenger.client;

import galactic_messenger.utils.StyledMessage;
import java.awt.*;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommandHandler {

    private ServerResponseListener responseListener;

    public CommandHandler(ServerResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public String handleCommand(String command) {
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
                return null;
            default:
            responseListener.updateChat(new StyledMessage("CoH Unknown command: " + action, Color.RED));
        }
        return null;
    }

    private String handleRegisterCommand(String[] parts) {
        if (parts.length != 2) {
            responseListener.updateChat(new StyledMessage("Usage: /register <username> <password>", Color.CYAN));
            return null;
        }

        String[] registrationInfo = parts[1].split(" ");
        if (registrationInfo.length != 2) {
            responseListener.updateChat(new StyledMessage("Usage: /register <username> <password>", Color.CYAN));
            return null;
        }

        String username = registrationInfo[0];
        String password = registrationInfo[1];

        if (isValidUsername(username) && isValidPassword(password)) {
            String hashedPassword = hashPassword(password);

            return "/register " + username + " " + hashedPassword;
        } else {
            responseListener.updateChat(new StyledMessage("Invalid username or password. Rules:\n" +
                    "Username must be 3-20 characters long and can only contain letters and numbers.\n" +
                    "Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character.", Color.CYAN));
            return null;
        }
    }

    private String handleLoginCommand(String[] parts) {
        if (parts.length != 2) {
            responseListener.updateChat(new StyledMessage("Usage: /login <username> <password>", Color.CYAN));
            return null;
        }

        String[] loginInfo = parts[1].split(" ");
        if (loginInfo.length != 2) {
            responseListener.updateChat(new StyledMessage("Usage: /login <username> <password>", Color.CYAN));
            return null;
        }

        String username = parts[1].split(" ")[0];
        String password = parts[1].split(" ")[1];

        String hashedPassword = hashPassword(password);

        return "/login " + username + " " + hashedPassword;
    }

    private void handleHelpCommand() {
        StringBuilder helpMessage = new StringBuilder("Available commands:\n");
        String[][] commandList = {
                { "/register <username> <password>", "Register a new user" },
                { "/login <username> <password>", "Login as an existing user" },
                { "/help", "Display available commands" },
                { "/exit", "Exit the Galactic Messenger" }
                // Ajoutez ici d'autres commandes et leurs descriptions
        };

        for (String[] commandInfo : commandList) {
            helpMessage.append(commandInfo[0]).append(" - ").append(commandInfo[1]).append("\n");
        }

        responseListener.updateChat(new StyledMessage(helpMessage.toString(), Color.CYAN));
    }

    private void handleExitCommand() {
        responseListener.updateChat(new StyledMessage("Live long and prosper 🖖", Color.CYAN));
        if (responseListener instanceof Client) {
            ((Client) responseListener).exitApplication();
        }
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
